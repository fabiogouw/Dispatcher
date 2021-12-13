package com.fabiogouw.dispatcher.adapters;

import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

// TODO: implementar o consumo de tópicos do Kafka, aqui os dados estão somente simulados
public class FakeKafkaBatchRepository implements BatchRepository {

    private static final Logger log = LoggerFactory.getLogger(BatchRepository.class);

    @Override
    public List<UrlDataRequest> getBatch() {
        List<UrlDataRequest> items = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            items.add(new UrlDataRequest(String.valueOf(i), "https://www.google.com", 1));
            items.add(new UrlDataRequest(String.valueOf(i), "http://www.uol.com.br", 1));
            items.add(new UrlDataRequest(String.valueOf(i), "https://www.facebook.com", 1));
            items.add(new UrlDataRequest(String.valueOf(i), "https://www.bing.com", 1));
        }
        return items;
    }

    @Override
    public void commitBatch() {
        log.warn("Commit fake do lote no Kafka");
    }

    @Override
    public void addRetry(UrlDataRequest request){
        request.setNewRetry();
        // TODO: colocar essa mensagem novamente na fila
    }
}
