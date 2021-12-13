package com.fabiogouw.dispatcher;

import com.fabiogouw.dispatcher.adapters.FakeKafkaBatchRepository;
import com.fabiogouw.dispatcher.adapters.WebClientApiRequester;
import com.fabiogouw.dispatcher.usecase.BatchManager;
import com.fabiogouw.dispatcher.usecase.ports.ApiRequester;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class DispatcherApp {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        SpringApplication.run(DispatcherApp.class, args);
    }

    @Bean
    public BatchRepository getBatchRepository() {
        return new FakeKafkaBatchRepository();
    }

    @Bean
    public ApiRequester getApiRequester() {
        return new WebClientApiRequester();
    }

    @Bean
    public BatchManager getBatchManager(BatchRepository repository, ApiRequester requester) {
        return new BatchManager(repository, requester);
    }

    /*
    @Bean
    public KafkaProducer<String, String> getKafkaProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        return producer;
    }

    @Bean
    public KafkaConsumer<String, String> getKafkaConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }
     */

}
