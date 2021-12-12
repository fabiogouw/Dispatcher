package com.fabiogouw.dispatcher.usecase.ports;

import com.fabiogouw.dispatcher.domain.UrlDataRequest;

import java.util.List;

public interface BatchRepository {
    List<UrlDataRequest> getBatch();
    void commitBatch();
    void addRetry(UrlDataRequest request);
}
