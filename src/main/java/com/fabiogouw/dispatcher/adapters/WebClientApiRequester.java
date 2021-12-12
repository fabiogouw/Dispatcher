package com.fabiogouw.dispatcher.adapters;

import com.fabiogouw.dispatcher.domain.ConsentDataRequest;
import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import com.fabiogouw.dispatcher.usecase.ports.ApiRequester;
import io.micrometer.core.instrument.Metrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

public class WebClientApiRequester implements ApiRequester {

    private static final WebClient client = WebClient.create();
    private final String accessTokenUrl = "https://duckduckgo.com/";
    private static final Logger log = LoggerFactory.getLogger(WebClientApiRequester.class);

    @Override
    public Mono<String> getAccessToken(ConsentDataRequest consentDataRequest) {
        return client
                .get()
                .uri(accessTokenUrl)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    log.info("Access token for " + consentDataRequest.getConsent());
                    return response;
                })
                ;
    }

    @Override
    public Mono<UrlDataRequest> getData(UrlDataRequest item) {
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
