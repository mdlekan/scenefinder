package com.mikelekan.scenefinder.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    // ── Consumer ──────────────────────────────────────────────────

    /*
    Creates a factory that knows how to create Kafka consumers. Think of it like a blueprint — it holds all the configuration needed to
    connect to Kafka as a consumer. It's not a consumer itself, it's the thing that makes consumers.

    The Java generics <String, String> mean both the key and value of each message are Strings.
     */
    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "fraud-monitor-group");
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    /*
    This is the most important bean — it's what @KafkaListener looks for by name.
    It wraps the consumerFactory (above) and adds the threading model on top.
    The Concurrent part means it can run multiple consumer threads in parallel.
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // ── Producer ──────────────────────────────────────────────────

    /*
    Same concept as consumerFactory but for sending messages. A factory that knows how to create Kafka producers.
    You don't currently have a Java producer in your app — Python is doing the producing —
    but it's good practice to include it so the Java side could produce messages if needed later.
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    /*
    The high level API for sending messages from Java. It wraps producerFactory and gives you a simple method to send:

    Without KafkaTemplate you'd have to manage the producer lifecycle manually. It's the Spring equivalent of the Python producer.produce() call.
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}

    /*
    Producing (sending)              Consuming (receiving)
    ───────────────────              ─────────────────────
    producerFactory                  consumerFactory
          ↓                                ↓
    kafkaTemplate                    kafkaListenerContainerFactory
          ↓                                ↓
    kafkaTemplate.send(...)          @KafkaListener method
     */