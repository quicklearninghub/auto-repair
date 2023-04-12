package com.quicklearninghub.main.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicklearninghub.database.dto.Account;
import com.quicklearninghub.database.entity.FailedMessageEntity;
import com.quicklearninghub.database.repository.FailedMessageRepository;
import com.quicklearninghub.main.validator.AccountValidator;
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

            saveFailedMessageEntity(consumerRecord, errorCode);
        }
        log.info("Finished consuming message on topic: {}, offset {}, message {}", consumerRecord.topic(),
                consumerRecord.offset(), consumerRecord.value());
    }

    public void saveFailedMessageEntity(ConsumerRecord<String, String> consumerRecord,
                                         String errorCode) {
        FailedMessageEntity failedMessageEntity;
        var failedMessageEntityOptional = getFailedMessageEntity(consumerRecord);
        if(failedMessageEntityOptional.isPresent()) {
            failedMessageEntity = failedMessageEntityOptional.get();
            if(isNull(errorCode)) {
                log.info("Updating the failed message {}", failedMessageEntity.getId());
                failedMessageEntity.setStatus("CLOSE");
            } else {
                log.warn("Validation failed again for failedMessage {}", failedMessageEntity.getId());
            }
        } else {
            if(nonNull(errorCode)) {
                failedMessageEntity = new FailedMessageEntity();
                log.info("Validation failed {}", errorCode);
                failedMessageEntity.setMessage(consumerRecord.value());
                failedMessageEntity.setStatus("OPEN");
                failedMessageEntity.setErrorCode(errorCode);
                failedMessageRepository.save(failedMessageEntity);
                log.info("Successfully saved FailedMessageEntity to database: {}", failedMessageEntity);
            }
        }
    }

    private Optional<FailedMessageEntity> getFailedMessageEntity(ConsumerRecord<String, String> consumerRecord) {
        Optional<Long> failedMessageIdOptional = getFailedMessageId(consumerRecord);
        if(failedMessageIdOptional.isPresent()) {
            log.info("Getting failedMessageEntity for id {}", failedMessageIdOptional.get());
            return failedMessageRepository.findById(failedMessageIdOptional.get());
        }
        return Optional.empty();
    }
    private static Optional<Long> getFailedMessageId(ConsumerRecord<String, String> consumerRecord) {
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
            return Optional.of(Long.parseLong(failedMessageId));

        return Optional.empty();
    }
}

