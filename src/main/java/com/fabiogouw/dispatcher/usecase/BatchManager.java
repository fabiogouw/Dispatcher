package com.fabiogouw.dispatcher.usecase;

import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import com.fabiogouw.dispatcher.domain.ConsentDataRequest;
import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Responsável por solicitar a obtenção de dados para todo o lote de URLs.
 */
@Component
public class BatchManager {
    private boolean running = false;
    private final BatchRepository repository;
    private static final Logger log = LoggerFactory.getLogger(BatchManager.class);

    public BatchManager(BatchRepository repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 100)
    public void wakeUp() throws ExecutionException, InterruptedException {
        if(!running) {
            running = true;
            try {
                execute();
            }
            finally {
                running = false;
            }
        }
    }

    private int execute() throws ExecutionException, InterruptedException {
        final List<UrlDataRequest> items = repository.getBatch();

        final Map<String, List<UrlDataRequest>> groupedConsentDataRequests = groupItemsByConsent(items);

        List<ConsentDataRequest> processedConsentDataRequests =
                Flux.fromIterable(new ArrayList<>(groupedConsentDataRequests.entrySet()))
                        .parallel(5)
                        .map(entry -> new ConsentBatchRequestor(entry.getKey(), entry.getValue()))
                        .flatMap(executor -> executor.execute())
                        .sequential()
                        .collectList()
                        .block(Duration.ofSeconds(120))
        ;

        for (ConsentDataRequest consentDataRequest: processedConsentDataRequests) {
            consentDataRequest.getItems().stream().map(item -> {
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
}
