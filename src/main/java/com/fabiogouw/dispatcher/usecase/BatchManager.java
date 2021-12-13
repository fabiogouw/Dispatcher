package com.fabiogouw.dispatcher.usecase;

import com.fabiogouw.dispatcher.usecase.ports.ApiRequester;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import com.fabiogouw.dispatcher.domain.ConsentDataRequest;
import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Responsável por solicitar a obtenção de dados para todo o lote de URLs.
 */
public class BatchManager {
    private final BatchRepository repository;
    private final ApiRequester requester;
    private static final Logger log = LoggerFactory.getLogger(BatchManager.class);

    public BatchManager(BatchRepository repository, ApiRequester requester) {
        this.repository = repository;
        this.requester = requester;
    }

    public int execute() throws ExecutionException, InterruptedException {
        final List<UrlDataRequest> items = repository.getBatch();

        final Map<String, List<UrlDataRequest>> groupedConsentDataRequests = groupItemsByConsent(items);

        List<ConsentDataRequest> processedConsentDataRequests =
                Flux.fromIterable(new ArrayList<>(groupedConsentDataRequests.entrySet()))
                        .parallel()
                        .map(entry -> new ConsentDataRequest(entry.getKey(), entry.getValue()))
                        .flatMap(consentDataRequest -> execute(consentDataRequest))
                        .sequential()
                        .collectList()
                        .block(Duration.ofSeconds(120))
        ;

        for (ConsentDataRequest consentDataRequest: processedConsentDataRequests) {
            consentDataRequest.getItems().stream().map(item -> {
                // se algum pedido de dado deu erro, por qualquer motivo, colocamos de volta na fila
                // para uma nova tentativa posterior
                if(item.getExecutedWithError()) {
                   repository.addRetry(item);
                }
               return item;
            });
        }
        repository.commitBatch();
        return processedConsentDataRequests.size();
    }

    private Map<String, List<UrlDataRequest>> groupItemsByConsent(List<UrlDataRequest> items) {
        Map<String, List<UrlDataRequest>> grouped = items.stream().collect(
                Collectors.groupingBy(UrlDataRequest::getConsent)
        );
        return grouped;
    }

    public Mono<ConsentDataRequest> execute(ConsentDataRequest consentDataRequest) {
        return Mono.just(consentDataRequest)
                .flatMap(requester::getAccessToken)
                .map(accessToken -> {
                    return consentDataRequest.addAccessToken(accessToken);
                })
                .zipWhen(full -> {
                    return Flux.fromIterable(full.getItems())
                            .flatMap(requester::getData)
                            .map(item -> {
                                // TODO: colocar aqui o código pra guardar o resultado
                                log.info(item.getConsent() + " " + item.getUrl());
                                return item;
                            })
                            .collectList();
                })
                .map(x -> {
                    return x.getT1();
                })
                ;
    }
}
