package com.fabiogouw.dispatcher.adapters;

import com.fabiogouw.dispatcher.usecase.BatchManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@EnableScheduling
/**
 * É o entry point da aplicação, responsável por chamar a funcionalidade
 * de busca de lote de pedidos de dados de APIs externas.
* */
public class BatchManagerRunner {

    @Autowired
    private BatchManager batchManager;
    private boolean running = false;

    @Scheduled(fixedRate = 100)
    public void wakeUp() throws ExecutionException, InterruptedException {
        if(!running) {
            running = true;
            try {
                batchManager.execute();
            }
            finally {
                running = false;
            }
        }
    }

}
