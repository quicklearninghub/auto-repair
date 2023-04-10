package com.quicklearninghub.autorepair.service;

import com.quicklearninghub.database.entity.ErrorCodeEntity;
import com.quicklearninghub.database.entity.FailedMessageEntity;
import com.quicklearninghub.database.repository.ErrorCodeRepository;
import com.quicklearninghub.database.repository.FailedMessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Slf4j
@AllArgsConstructor
public class FailedMessageService {

    private final FailedMessageRepository failedMessageRepository;
    private final ErrorCodeRepository errorCodeRepository;
    public List<FailedMessageEntity> getAssociatedFailedMessages(ConsumerRecord<String, String> consumerRecord) {
        String entityName = getEntityName(consumerRecord);
        log.info("Entity name : {}", entityName);
        if(nonNull(entityName)) {
            return getFailedMessageEntities(entityName);
        }
        return Collections.emptyList();
    }

    private static String getEntityName(ConsumerRecord<String, String> consumerRecord) {

        for (Header header : consumerRecord.headers()) {
            if ("entityName".equalsIgnoreCase(header.key())) {
                return new String(header.value(), StandardCharsets.UTF_16);
            }
        }
        return null;
    }

    private List<FailedMessageEntity> getFailedMessageEntities(String entityName) {
        List<ErrorCodeEntity> errorCodeEntities = errorCodeRepository.findByAssociatedEntity(entityName);
        if(!CollectionUtils.isEmpty(errorCodeEntities)) {
            List<String> errorCodes =
                    errorCodeEntities.stream().map(ErrorCodeEntity::getCode).collect(Collectors.toList());
            return failedMessageRepository.findByErrorCodeInAndStatus(errorCodes, "OPEN");
        }
        return Collections.emptyList();
    }
}
