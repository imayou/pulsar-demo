package com.ayou.pulsardemo.infrastructure.annotation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PulsarListenerAnnotationBeanPostProcessor
 *
 * @author ysy
 * @blame ysy
 * @date 2020-01-13
 */
@Component
public class PulsarListenerAnnotationBeanPostProcessor<K, V>
        implements BeanPostProcessor, Ordered, BeanFactoryAware, ApplicationContextAware {

    private final Set<Class<?>> nonAnnotatedClasses = Collections.newSetFromMap(new ConcurrentHashMap<>(64));
    private final Log logger = LogFactory.getLog(getClass());
    private BeanFactory beanFactory;
    private BeanExpressionResolver resolver = new StandardBeanExpressionResolver();
    private BeanExpressionContext expressionContext;
    private final ListenerScope listenerScope = new ListenerScope();
    private ConfigurableApplicationContext applicationContext;

    private PulsarListenerEndpointRegistry pulsarListenerEndpointRegistry = new PulsarListenerEndpointRegistry();

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            this.resolver = ((ConfigurableListableBeanFactory) beanFactory).getBeanExpressionResolver();
            this.expressionContext = new BeanExpressionContext((ConfigurableListableBeanFactory) beanFactory,
                    this.listenerScope);
        }
    }

    @Override
    public int getOrder() {
        return LOWEST_PRECEDENCE;
    }

    @Override
    public Object postProcessAfterInitialization(final Object bean, final String beanName) throws BeansException {
        if (!this.nonAnnotatedClasses.contains(bean.getClass())) {
            Class<?> targetClass = AopUtils.getTargetClass(bean);
            Collection<PulsarListener> classLevelListeners = findListenerAnnotations(targetClass);
            final boolean hasClassLevelListeners = classLevelListeners.size() > 0;
            final List<Method> multiMethods = new ArrayList<>();
            Map<Method, Set<PulsarListener>> annotatedMethods = MethodIntrospector.selectMethods(targetClass,
                    new MethodIntrospector.MetadataLookup<Set<PulsarListener>>() {
                        @Override
                        public Set<PulsarListener> inspect(Method method) {
                            Set<PulsarListener> listenerMethods = findListenerAnnotations(method);
                            return (!listenerMethods.isEmpty() ? listenerMethods : null);
                        }

                    });
            if (hasClassLevelListeners) {
                Set<Method> methodsWithHandler = MethodIntrospector.selectMethods(targetClass,
                        (ReflectionUtils.MethodFilter) method ->
                                AnnotationUtils.findAnnotation(method, PulsarHandler.class) != null);
                multiMethods.addAll(methodsWithHandler);
            }
            if (annotatedMethods.isEmpty()) {
                this.nonAnnotatedClasses.add(bean.getClass());
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("No @PulsarListener annotations found on bean type: " + bean.getClass());
                }
            } else {
                // Non-empty set of methods
                for (Map.Entry<Method, Set<PulsarListener>> entry : annotatedMethods.entrySet()) {
                    Method method = entry.getKey();
                    for (PulsarListener listener : entry.getValue()) {
                        //processPulsarListener(listener, method, bean, beanName);
                        pulsarListenerEndpointRegistry.registry(listener, method, bean, beanName, applicationContext);
                       // logger.info(listener + "" + method + "" + bean + "" + beanName);
                    }
                }
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug(annotatedMethods.size() + " @PulsarListener methods processed on bean '"
                            + beanName + "': " + annotatedMethods);
                }
            }
            if (hasClassLevelListeners) {
                //processMultiMethodListeners(classLevelListeners, multiMethods, bean, beanName);
                //logger.info(classLevelListeners + "" + multiMethods + "" + bean + "" + beanName);
            }
        }
        return bean;
    }


    /*
     * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
     */
    private Collection<PulsarListener> findListenerAnnotations(Class<?> clazz) {
        Set<PulsarListener> listeners = new HashSet<>();
        PulsarListener ann = AnnotationUtils.findAnnotation(clazz, PulsarListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        PulsarListeners anns = AnnotationUtils.findAnnotation(clazz, PulsarListeners.class);
        if (anns != null) {
            listeners.addAll(Arrays.asList(anns.value()));
        }
        return listeners;
    }

    /*
     * AnnotationUtils.getRepeatableAnnotations does not look at interfaces
     */
    private Set<PulsarListener> findListenerAnnotations(Method method) {
        Set<PulsarListener> listeners = new HashSet<>();
        PulsarListener ann = AnnotatedElementUtils.findMergedAnnotation(method, PulsarListener.class);
        if (ann != null) {
            listeners.add(ann);
        }
        PulsarListeners anns = AnnotationUtils.findAnnotation(method, PulsarListeners.class);
        if (anns != null) {
            listeners.addAll(Arrays.asList(anns.value()));
        }
        return listeners;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof ConfigurableApplicationContext) {
            this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        }
    }

    private static class ListenerScope implements Scope {

        private final Map<String, Object> listeners = new HashMap<>();

        ListenerScope() {
            super();
        }

        public void addListener(String key, Object bean) {
            this.listeners.put(key, bean);
        }

        public void removeListener(String key) {
            this.listeners.remove(key);
        }

        @Override
        public Object get(String name, ObjectFactory<?> objectFactory) {
            return this.listeners.get(name);
        }

        @Override
        public Object remove(String name) {
            return null;
        }

        @Override
        public void registerDestructionCallback(String name, Runnable callback) {
        }

        @Override
        public Object resolveContextualObject(String key) {
            return this.listeners.get(key);
        }

        @Override
        public String getConversationId() {
            return null;
        }

    }
}
