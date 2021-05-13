package com.ayou.pulsardemo.application;

import org.apache.pulsar.client.api.Producer;
import org.apache.pulsar.client.api.PulsarClientException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * PulsarSchedule
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-04
 */
@EnableScheduling
@Component
public class PulsarSchedule {

    @Resource
    Producer producer;

//    @Resource
//    Consumer consumer;

    @Scheduled(fixedRate = 10)
    public void send() {
        //System.out.println(producer);
        try {
            producer.send(("hello-" + UUID.randomUUID().toString()).getBytes());
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

//    @Scheduled(fixedRate = 100)
//    public void re() {
//        try {
//            Message msg = consumer.receive();
//            try {
//                System.out.println("定时任务收到消息: " + new String(msg.getData()));
//                consumer.acknowledge(msg);
//            } catch (Exception e) {
//                consumer.negativeAcknowledge(msg);
//            }
//        } catch (PulsarClientException e) {
//            e.printStackTrace();
//        }
//    }
}
