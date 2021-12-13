package com.fabiogouw.dispatcher.adapters;

import com.fabiogouw.dispatcher.domain.UrlDataRequest;
import com.fabiogouw.dispatcher.usecase.ports.BatchRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class KafkaBatchRepository implements BatchRepository {

    private static final Logger log = LoggerFactory.getLogger(BatchRepository.class);
    private final KafkaProducer<String, String> producer;
    private final KafkaConsumer<String, String> consumer;
    private final String topicName = "my-topic";

    public KafkaBatchRepository(KafkaProducer<String, String> producer,
                                KafkaConsumer<String, String> consumer) {
        this.producer = producer;
        this.consumer = consumer;
    }

    @Override
    public List<UrlDataRequest> getBatch() {
        List<UrlDataRequest> items = new ArrayList<>();
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));
        for(ConsumerRecord<String, String> record : records) {
            items.add(new UrlDataRequest("1", "https://www.google.com", 1));
            // TODO: efetuar o parse a partir de record.value()
        }
        return items;
    }

    @Override
    public void commitBatch() {
        consumer.commitSync(Duration.ofSeconds(5));
        log.info("Commit fake do lote no Kafka");
    }

    @Override
    public void addRetry(UrlDataRequest request) throws ExecutionException, InterruptedException, TimeoutException {
        request.setNewRetry();
        String serializedRecord = request.toString();   // TODO: correct serialization
        ProducerRecord<String, String> record = new ProducerRecord<>(topicName, request.getConsent(), serializedRecord);
        producer.send(record).get(100, TimeUnit.MILLISECONDS);
    }
}
