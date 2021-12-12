package com.fabiogouw.dispatcher.usecase;

import com.fabiogouw.dispatcher.domain.ConsentDataRequest;
import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class ConsentBatchRequestor {
    private final String consentId;
    private final List<UrlDataRequest> items;
    private static final WebClient client = WebClient.create();
    private final String accessTokenUrl = "https://duckduckgo.com/";
    private static final Logger log = LoggerFactory.getLogger(ConsentBatchRequestor.class);

    public ConsentBatchRequestor(String consentId, List<UrlDataRequest> items) {
        this.consentId = consentId;
        this.items = items;
    }

    public Mono<ConsentDataRequest> execute() {
        if(items.size() > 0){
            ConsentDataRequest consentDataRequest = new ConsentDataRequest(this.consentId, this.items);
            return Mono.just(consentDataRequest)
                    .flatMap(this::getAccessToken)
                    .zipWhen(full -> {
                        return Flux.fromIterable(full.getItems())
                                .flatMap(this::getData)
                                .collectList();
                    })
                    .map(x -> {
                        return x.getT1();
                    })
                    ;
        }
        return Mono.empty();
    }

    private Mono<ConsentDataRequest> getAccessToken(ConsentDataRequest consentDataRequest) {
        return client
                .get()
                .uri(accessTokenUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("Access token for " + consentDataRequest.getConsent());
                    return consentDataRequest.addAccessToken(response);
                })
                .onErrorReturn(consentDataRequest)
                ;
    }

    private Mono<UrlDataRequest> getData(UrlDataRequest item) {
        return client
                .get()
                .uri(item.getUrl())
                .header("Authorization", "Bearer: " + item.getAccessToken())
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    Metrics.counter("calls.completed").increment();
                    Metrics.counter("calls.made").increment();
                    item.setResult(false, response);
                    log.info(item.getConsent() + " " + item.getUrl() + " Thread " + Thread.currentThread().getId());
                    return item;
                })
                .doOnError(ex -> {
                    Metrics.counter("calls.errors").increment();
                    Metrics.counter("calls.made").increment();
                    item.setResult(true, "ERROR");
                    log.error(item.getConsent() + " " + item.getUrl()+ " ERROR");
                })
                .onErrorReturn(item)
                ;
    }
}
