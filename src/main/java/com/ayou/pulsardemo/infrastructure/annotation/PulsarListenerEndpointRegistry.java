package com.ayou.pulsardemo.infrastructure.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pulsar.client.api.Consumer;
import org.apache.pulsar.client.api.PulsarClient;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Method;

/**
 * PulsarListenerEndpointRegistry
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-13
 */
public class PulsarListenerEndpointRegistry {
    private final Log logger = LogFactory.getLog(getClass());
    private boolean contextRefreshed;
    private ConfigurableApplicationContext applicationContext;

    public void start() {
        //PulsarListenerExecutor.p
    }

    public void stop() {
        PulsarListenerExecutor.shutdown();
    }

    public boolean isRunning() {
        return PulsarListenerExecutor.isRunning();
    }

    public void registry(final PulsarListener listener, final Method method, final Object bean, final String beanName, ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        addConsumer(listener, method);
        Consumer consumer = this.applicationContext.getBean(listener.topic() + listener.subscriptionName(), Consumer.class);
        addListener(consumer, bean, method);
    }

    public synchronized void addConsumer(final PulsarListener listener, final Method method) {
        try {
            if (this.applicationContext.containsBean(listener.topic() + listener.subscriptionName())) {
                return;
            }
            PulsarClient pulsarClient = this.applicationContext.getBean(PulsarClient.class);
            Consumer consumer = pulsarClient.newConsumer().topic(listener.topic()).subscriptionName(listener.subscriptionName()).subscribe();
            this.applicationContext.getBeanFactory().registerSingleton(listener.topic() + listener.subscriptionName(), consumer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void addListener(final Consumer consumer, final Object bean, final Method method) {
        Thread t = new Thread(() -> {
            try {
                while (true) {
                    try {
                        consumer.receive();
                        method.invoke(bean, consumer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        t.setName("consumer-listener-" + consumer.getTopic() + "-" + consumer.getSubscription());
        PulsarListenerExecutor.execute(t);
    }

}
