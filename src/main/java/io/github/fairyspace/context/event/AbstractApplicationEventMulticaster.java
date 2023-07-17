package io.github.fairyspace.context.event;

import io.github.fairyspace.beans.BeanFactory;
import io.github.fairyspace.beans.factory.BeanFactoryAware;
import io.github.fairyspace.context.ApplicationEvent;
import io.github.fairyspace.context.ApplicationListener;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class AbstractApplicationEventMulticaster implements ApplicationEventMulticaster, BeanFactoryAware {
    public final Set<ApplicationListener<ApplicationEvent>> applicationListeners = new LinkedHashSet<>();

    private BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void addApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.add((ApplicationListener<ApplicationEvent>) listener);
    }

    @Override
    public void removeApplicationListener(ApplicationListener<?> listener) {
        applicationListeners.remove(listener);
    }



}
