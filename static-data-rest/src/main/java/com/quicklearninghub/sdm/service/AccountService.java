package com.quicklearninghub.sdm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicklearninghub.autorepair.publisher.PublisherService;
import com.quicklearninghub.database.dto.Account;
import com.quicklearninghub.database.entity.AccountEntity;
import com.quicklearninghub.database.entity.RiskEntity;
import com.quicklearninghub.database.repository.AccountRepository;
import com.quicklearninghub.database.repository.RiskRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

@Service
@Slf4j
public class AccountService {

    @Value(value = "${kafka.update.topic}")
    private String updateTopic;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RiskRepository riskRepository;


    @Transactional
    public ResponseEntity<AccountEntity> updateAccountRiskId(Long accountId, Long riskId) {
        AccountEntity updatedAccount = getUpdatedAccountEntity(accountId, riskId);
        log.info("Publishing the updated event {}", updatedAccount);
        return publishAccountEntityUpdate(updatedAccount);
    }

    private ResponseEntity<AccountEntity> publishAccountEntityUpdate(AccountEntity updatedAccount) {
        String publish;
        try {
            Account account = new Account();
            account.setAccountId(updatedAccount.getId().toString());
            publish = publisherService.publish(updateTopic, objectMapper.writeValueAsString(account),
                    getHeaders("AccountEntity"));
        } catch (JsonProcessingException e) {
            log.error("JsonProcessingException ", e);
            return ResponseEntity.badRequest().build();
        }
        if(nonNull(publish)) {
            return ResponseEntity.ok(updatedAccount);
        }
        log.error("Unable to publish the account entity {}", updatedAccount);
        return ResponseEntity.internalServerError().build();
    }

    private AccountEntity getUpdatedAccountEntity(Long accountId, Long riskId) {
        AccountEntity account = accountRepository.findById(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Account not found with id " + accountId));

        RiskEntity risk = riskRepository.findById(riskId)
                .orElseThrow(() -> new ResourceNotFoundException("Risk not found with id " + riskId));

        account.setRiskEntity(risk);
        AccountEntity updatedAccount = accountRepository.save(account);
        log.info("Updated the account entity {}", updatedAccount);
        return updatedAccount;
    }

    private Map<String, String> getHeaders(String entityName) {
        Map<String, String> headers = new HashMap<>();
        headers.put("entityName", entityName);
        return headers;
    }
}
