package com.ayou.pulsardemo.application;

import com.ayou.pulsardemo.infrastructure.annotation.PulsarListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.pulsar.client.api.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ysy
 * @blame ysy
 * @date 2020-01-11
 */
@Slf4j
@Service
public class MessageApplicationService {
    @Resource
    Producer producer;

    @Resource(name = "producer2")
    Producer producer2;

    @Resource(name = "producer3")
    Producer producer3;

    @Resource
    PulsarClient pulsarClient;

    AtomicLong t1 = new AtomicLong();
    AtomicLong t2 = new AtomicLong();
    AtomicLong t3 = new AtomicLong();


    @PulsarListener(topic = "my-topic", subscriptionName = "my-subscription")
    public void hello1x1(Consumer consumer) {
        try {
            Message msg = consumer.receive();
            try {
                String s = new String(msg.getData());
                log.info("监听1-1: " + s);
                producer2.send(("监听1-1收到-->发给监听2 " + s).getBytes());
                t1.getAndIncrement();
                consumer.acknowledge(msg);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(msg);
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }


    @PulsarListener(topic = "my-topic", subscriptionName = "my-subscription")
    public void hello1x2(Consumer consumer) {
        try {
            Message msg = consumer.receive();
            try {
                String s = new String(msg.getData());
                log.info("监听1-2: " + s);
                producer2.send(("监听1-2收到-->发给监听2 " + s).getBytes());
                t1.getAndIncrement();
                consumer.acknowledge(msg);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(msg);
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    @PulsarListener(topic = "my-topic2", subscriptionName = "my-subscription2")
    public void hello2(Consumer consumer) {
        try {
            Message msg = consumer.receive();
            try {
                String s = new String(msg.getData());
                log.info("监听2: " + s);
                producer3.send(("监听2收到-->发给监听3 " + s).getBytes());
                t2.getAndIncrement();
                consumer.acknowledge(msg);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(msg);
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

    @PulsarListener(topic = "my-topic3", subscriptionName = "my-subscription3")
    public void hello3(Consumer consumer) {
        try {
            Message msg = consumer.receive();
            try {
                log.info("监听3: " + new String(msg.getData()));
                t3.getAndIncrement();
                consumer.acknowledge(msg);
            } catch (Exception e) {
                e.printStackTrace();
                consumer.negativeAcknowledge(msg);
            }
        } catch (PulsarClientException e) {
            e.printStackTrace();
        }
    }

//    @Bean
//    public void recover() {
//        try {
//            Thread thread = new Thread(() -> {
//                while (true) {
//                    try {
//                        Message msg = consumer.receive();
//                        try {
//                            System.out.println("线程: " + new String(msg.getData()));
//                            consumer.acknowledge(msg);
//                        } catch (Exception e) {
//                            consumer.negativeAcknowledge(msg);
//                        }
//                    } catch (PulsarClientException e) {
//                        e.printStackTrace();
//                    }
//                }
//            });
//
//            thread.setName("pulsar-consumer-" + consumer.getTopic());
//            thread.setDaemon(true);
//            thread.start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
