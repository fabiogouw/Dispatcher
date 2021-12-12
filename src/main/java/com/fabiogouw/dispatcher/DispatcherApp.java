package com.fabiogouw.dispatcher;

import com.fabiogouw.dispatcher.adapters.KafkaBatchRepository;
import com.fabiogouw.dispatcher.adapters.WebClientApiRequester;
import com.fabiogouw.dispatcher.usecase.BatchManager;
import com.fabiogouw.dispatcher.usecase.ports.ApiRequester;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DispatcherApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(DispatcherApp.class, args);
    }

    @Bean
    public BatchRepository getBatchRepository() {
        return new KafkaBatchRepository();
    }

    @Bean
    public ApiRequester getApiRequester() {
        return new WebClientApiRequester();
    }

    @Bean
    public BatchManager getBatchManager(BatchRepository repository, ApiRequester requester) {
        return new BatchManager(repository, requester);
    }
}
