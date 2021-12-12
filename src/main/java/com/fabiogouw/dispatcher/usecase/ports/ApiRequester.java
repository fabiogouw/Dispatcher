package com.fabiogouw.dispatcher.usecase.ports;

import com.fabiogouw.dispatcher.domain.ConsentDataRequest;
import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import reactor.core.publisher.Mono;

public interface ApiRequester {
    Mono<String> getAccessToken(ConsentDataRequest consentDataRequest);
    Mono<UrlDataRequest> getData(UrlDataRequest item);
}
