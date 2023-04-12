package com.quicklearninghub.main.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quicklearninghub.autorepair.publisher.PublisherService;
import com.quicklearninghub.database.dto.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static java.util.Objects.nonNull;

@RestController
@Slf4j
public class PublisherController {

    @Value(value = "${kafka.main.topic}")
    private String mainTopic;

    @Autowired
    PublisherService publisherService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/main/publish")
    public ResponseEntity<String> publish(@RequestBody Account account) throws JsonProcessingException {
        log.info("Publishing the event {}", account);
        String publish = publisherService.publish(mainTopic, objectMapper.writeValueAsString(account), null);
        if(nonNull(publish)) {
            return ResponseEntity.ok().body(publish);
        }
        return ResponseEntity.internalServerError().body("Message was not published successfully");
    }
}
