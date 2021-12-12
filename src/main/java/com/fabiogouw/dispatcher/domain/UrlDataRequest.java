package com.fabiogouw.dispatcher.domain;

public class UrlDataRequest {
    private final String consent;
    private final String url;
    private String accessToken;
    private boolean executedWithError = false;
    private String result;

    public UrlDataRequest(String consent,
                          String url) {
        this.consent = consent;
        this.url = url;
    }

    public String getConsent() {
        return this.consent;
    }

    public String getUrl(){
        return this.url;
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public boolean getExecutedWithError() {
        return executedWithError;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setResult(boolean error, String result) {
        this.executedWithError = error;
        this.result = result;
    }

}
