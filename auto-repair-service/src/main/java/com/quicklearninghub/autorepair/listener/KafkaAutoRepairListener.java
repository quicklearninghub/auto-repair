package com.quicklearninghub.autorepair.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicklearninghub.autorepair.publisher.PublisherService;
import com.quicklearninghub.autorepair.service.FailedMessageService;
import com.quicklearninghub.database.dto.Account;
import com.quicklearninghub.database.entity.FailedMessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;


@Service
@Slf4j
public class KafkaAutoRepairListener {

    @Value(value = "${kafka.topic}")
    String mainTopic;

    private final ObjectMapper objectMapper;
    private final FailedMessageService failedMessageService;
    private final PublisherService publisherService;

    public KafkaAutoRepairListener(ObjectMapper objectMapper,
                                   FailedMessageService failedMessageService,
                                   PublisherService publisherService) {
        this.objectMapper = objectMapper;
        this.failedMessageService = failedMessageService;
        this.publisherService = publisherService;
    }

    @KafkaListener(topics = "${kafka.update.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> consumerRecord) {
        log.info("Started consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());


        if(nonNull(consumerRecord.value())) {
            List<FailedMessageEntity> associatedFailedMessages = failedMessageService.getAssociatedFailedMessages(consumerRecord);
            log.info("AssociatedFailedMessages : {}", associatedFailedMessages);
            if(!CollectionUtils.isEmpty(associatedFailedMessages)) {
             // Logic to publish message back to main topic
                associatedFailedMessages.forEach(failedMessageEntity -> {
                    try {
                        publisherService.publish(mainTopic, objectMapper.writeValueAsString(objectMapper.readValue(failedMessageEntity.getMessage(), Account.class)), getHeaders(failedMessageEntity.getId().toString()));
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        log.info("Finished consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());
    }

    private Map<String, String> getHeaders(String failedMessageId) {
        Map<String, String> headers = new HashMap<>();
        headers.put("failedMessageId", failedMessageId);
        return headers;
    }
}

