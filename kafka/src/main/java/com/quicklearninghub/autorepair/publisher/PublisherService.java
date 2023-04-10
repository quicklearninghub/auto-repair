package com.quicklearninghub.autorepair.publisher;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.Header;
import org.apache.kafka.common.header.Headers;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class PublisherService {


    private final KafkaTemplate<String, String> kafkaTemplate;

    public PublisherService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public String publish(String topic, String message, Map<String, String> headers) {
        try {
            String key = UUID.randomUUID().toString();
            ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, message);
            if(nonNull(headers)) {
                headers.forEach((k, v) -> {
                    log.info("Adding headers key {} value {}", k, v);
                    producerRecord.headers().add(k, v.getBytes(StandardCharsets.UTF_16));
                });
            }
            SendResult<String, String> sendResult = kafkaTemplate.send(producerRecord).get();
            if(nonNull(sendResult)) {
                log.info("Message sent {} to the topic {}", message, topic);
                return sendResult.toString();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
