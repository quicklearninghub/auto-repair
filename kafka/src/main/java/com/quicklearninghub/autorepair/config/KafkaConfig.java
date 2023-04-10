package com.quicklearninghub.autorepair.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@Slf4j
public class KafkaConfig {

    @Value(value = "${spring.kafka.dead_letter_topic:}")
    private String deadLetterTopic;
    @Value(value = "${spring.kafka.enable_dlt: false}")
    private boolean enableDLT;

    private KafkaConsumerProps kafkaConsumerProps;

    private KafkaTemplate kafkaTemplate;

    KafkaConfig(KafkaTemplate kafkaTemplate, KafkaConsumerProps kafkaConsumerProps) {
        this.kafkaTemplate = kafkaTemplate;
        this.kafkaConsumerProps = kafkaConsumerProps;
    }

    @Bean("kafkaListenerContainerFactoryDLT")
    @ConditionalOnProperty(value = "spring.kafka.enable_dlt", havingValue = "true")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactoryDLT() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> concurrentKafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory(kafkaConsumerProps.consumerProps()));
        DeadLetterPublishingRecoverer deadLetterPublishingRecoverer =
            new DeadLetterPublishingRecoverer(kafkaTemplate, this::deadLetterTopicPartion);
        CommonErrorHandler errorHandler = new DefaultErrorHandler(deadLetterPublishingRecoverer, new FixedBackOff(0L, 1L));
        concurrentKafkaListenerContainerFactory.setCommonErrorHandler(errorHandler);
        return concurrentKafkaListenerContainerFactory;
    }

    @Bean("kafkaListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<Object, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<Object, Object> concurrentKafkaListenerContainerFactory
                = new ConcurrentKafkaListenerContainerFactory<>();
        concurrentKafkaListenerContainerFactory.setConsumerFactory(new DefaultKafkaConsumerFactory(kafkaConsumerProps.consumerProps()));
        return concurrentKafkaListenerContainerFactory;
    }

    private TopicPartition deadLetterTopicPartion(ConsumerRecord<?, ?> record, Exception ex) {
        log.info("Exception {} occurred sending the record to the error topic {}", ex.getMessage(), deadLetterTopic);
        return new TopicPartition(deadLetterTopic, -1);
    }
}
