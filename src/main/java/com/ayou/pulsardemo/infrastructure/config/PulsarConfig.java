package com.ayou.pulsardemo.infrastructure.config;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClient;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PulsarConfig
 * https://pulsar.apache.org/docs/zh-CN/client-libraries-java/
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-04
 */
@Configuration
public class PulsarConfig {
    @Bean
    public PulsarClient pulsarClient() {
        try {
            PulsarClient client = PulsarClient.builder().serviceUrl("pulsar://172.16.1.8:6650").build();
            return client;
        } catch (PulsarClientException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Bean
    public Producer producer(@Qualifier("pulsarClient") PulsarClient pulsarClient) {
        Producer<byte[]> producer = null;
        try {
            producer = pulsarClient.newProducer().topic("my-topic").create();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

    @Bean(name = "producer2")
    public Producer producer2(@Qualifier("pulsarClient") PulsarClient pulsarClient) {
        Producer<byte[]> producer = null;
        try {
            producer = pulsarClient.newProducer().topic("my-topic2").create();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

    @Bean(name = "producer3")
    public Producer producer3(@Qualifier("pulsarClient") PulsarClient pulsarClient) {
        Producer<byte[]> producer = null;
        try {
            producer = pulsarClient.newProducer().topic("my-topic3").create();
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        return producer;
    }

//    @Bean
//    public Consumer consumer(@Qualifier("pulsarClient") PulsarClient pulsarClient) {
//        Consumer consumer = null;
//        try {
//            consumer = pulsarClient.newConsumer().topic("my-topic").subscriptionName("my-subscription").subscribe();
//        } catch (PulsarClientException e) {
//            e.printStackTrace();
//        }
//        return consumer;
//    }


}
