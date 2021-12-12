package com.fabiogouw.dispatcher.domain;

import java.util.List;

public class ConsentDataRequest {
    private final String consentId;
    private final List<UrlDataRequest> items;
    private String accessToken;

    public ConsentDataRequest(String consentId, List<UrlDataRequest> items) {
        this.consentId = consentId;
        this.items = items;
    }

    public String getConsent() {
        return this.consentId;
    }

    public List<UrlDataRequest> getItems() {
        return this.items;
    }

    public ConsentDataRequest addAccessToken(String accessToken) {
        this.accessToken = accessToken;
        this.items.stream().map(item -> {
            item.setAccessToken(accessToken);
           return item;
        });
        return this;
    }
}
