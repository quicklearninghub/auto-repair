package com.quicklearninghub.autorepair.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class KafkaConsumerProps {

    @Value(value = "${spring.kafka.bootstrap-servers:}")
    private String bootstrapAddress;
    @Value(value = "${spring.kafka.consumer.key-deserializer:}")
    private String consumerKeyDeserializer;
    @Value(value = "${spring.kafka.consumer.value-deserializer:}")
    private String consumerValueDeserializer;
    @Value(value = "${spring.kafka.consumer.group-id:}")
    private String consumerGroupId;
    @Value(value = "${spring.json.trusted.packages:}")
    private String trustedPackages;


    protected Map<String, String> consumerProps() {
        Map<String, String> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, consumerKeyDeserializer);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, consumerValueDeserializer);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, trustedPackages);
        return props;
    }
}
