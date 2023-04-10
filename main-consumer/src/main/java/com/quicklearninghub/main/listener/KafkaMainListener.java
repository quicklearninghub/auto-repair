package com.quicklearninghub.main.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicklearninghub.database.dto.Account;
import com.quicklearninghub.main.validator.AccountValidator;
import com.quicklearninghub.database.entity.FailedMessageEntity;
import com.quicklearninghub.database.repository.FailedMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


@Service
@Slf4j
@AllArgsConstructor
public class KafkaMainListener {

    private final FailedMessageRepository failedMessageRepository;
    private final AccountValidator accountValidator;
    private final ObjectMapper objectMapper;

    @ConditionalOnProperty("${kafka.main.topic}")
    @Transactional
    @KafkaListener(topics = "${kafka.main.topic}", groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "kafkaListenerContainerFactory")
    public void listen(ConsumerRecord<String, String> consumerRecord) throws JsonProcessingException {
        log.info("Started consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());

        if(nonNull(consumerRecord.value())) {
            Account account =  objectMapper.readValue(consumerRecord.value(), Account.class);
            var errorCode = accountValidator.validate(account);
            Long failedMessageId = getFailedMessageId(consumerRecord);
            Optional<FailedMessageEntity> failedMessageEntityOptional = Optional.empty();
            if(nonNull(failedMessageId)) {
                failedMessageEntityOptional = failedMessageRepository.findById(failedMessageId);
            }
            if(failedMessageEntityOptional.isPresent()) {
                if(isNull(errorCode)) {
                    FailedMessageEntity failedMessageEntity = failedMessageEntityOptional.get();
                    failedMessageEntity.setStatus("CLOSED");
                    failedMessageRepository.save(failedMessageEntity);
                }  else {
                    log.error("Validation failed again: {}", errorCode);
                }
            } else {
                saveFailedMessageEntity(consumerRecord, errorCode);
            }
        }
        log.info("Finished consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());
    }

    private void saveFailedMessageEntity(ConsumerRecord<String, String> consumerRecord, String errorCode) {
        if(nonNull(errorCode)) {
            log.error("Validation failed {}", errorCode);
            FailedMessageEntity failedMessageEntity = new FailedMessageEntity();
            failedMessageEntity.setMessage(consumerRecord.value());
            failedMessageEntity.setStatus("OPEN");
            failedMessageEntity.setErrorCode(errorCode);
            failedMessageRepository.save(failedMessageEntity);
            log.info("Successfully saved FailedMessageEntity to database: {}", failedMessageEntity);
        }
    }

    private static Long getFailedMessageId(ConsumerRecord<String, String> consumerRecord) {
        String failedMessageId = null;
        if(nonNull(consumerRecord.headers())) {
            Iterator<Header> headerIterator = consumerRecord.headers().iterator();
            while (headerIterator.hasNext()) {
                Header header = headerIterator.next();
                if("failedMessageId".equals(header.key())) {
                    failedMessageId = new String(header.value(), StandardCharsets.UTF_16);
                }
            }
        }
        if(nonNull(failedMessageId))
            return Long.parseLong(failedMessageId);

        return null;
    }
}

