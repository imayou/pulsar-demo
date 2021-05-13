package com.ayou.pulsardemo.application;

import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.Message;
import org.apache.pulsar.client.api.MessageListener;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.stereotype.Service;

/**
 * PulsarService
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-04
 */
@Service
public class PulsarService implements MessageListener {
    @Override
    public void received(Consumer consumer, Message message) {
        try {
            Message msg = consumer.receive();
            System.out.println("Message received:" + new String(msg.getData()));
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
        System.out.println("Message received:" + new String(message.getData()));
    }
}
