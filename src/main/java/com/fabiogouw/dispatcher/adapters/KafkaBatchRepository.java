package com.fabiogouw.dispatcher.adapters;

import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;

import java.util.ArrayList;
import java.util.List;

public class KafkaBatchRepository implements BatchRepository {
    @Override
    public List<UrlDataRequest> getBatch() {
        List<UrlDataRequest> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            items.add(new UrlDataRequest(String.valueOf(i), "https://www.google.com"));
            items.add(new UrlDataRequest(String.valueOf(i), "http://www.uol.com.br"));
            items.add(new UrlDataRequest(String.valueOf(i), "https://www.facebook.com"));
            items.add(new UrlDataRequest(String.valueOf(i), "https://www.bing.com"));
        }
        return items;
    }

    @Override
    public void commitBatch() {
        System.out.println("COMMIT!");
    }

    @Override
    public void addRetry(UrlDataRequest request){

    }
}
