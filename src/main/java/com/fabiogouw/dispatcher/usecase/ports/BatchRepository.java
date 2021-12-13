package com.fabiogouw.dispatcher.usecase.ports;

import com.fabiogouw.dispatcher.domain.UrlDataRequest;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface BatchRepository {
    List<UrlDataRequest> getBatch();
    void commitBatch();
    void addRetry(UrlDataRequest request) throws ExecutionException, InterruptedException, TimeoutException;
}
