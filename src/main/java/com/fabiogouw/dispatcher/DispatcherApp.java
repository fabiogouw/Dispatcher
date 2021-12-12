package com.fabiogouw.dispatcher;

import com.fabiogouw.dispatcher.adapters.KafkaBatchRepository;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.ExecutionException;

@SpringBootApplication
@EnableScheduling
public class DispatcherApp {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(DispatcherApp.class, args);
    }

    @Bean
    public BatchRepository getRequestHolder() {
        return new KafkaBatchRepository();
    }
}
