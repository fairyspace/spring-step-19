package io.github.fairyspace.aop.framework.autoproxy;

import io.github.fairyspace.aop.*;
import io.github.fairyspace.aop.aspectj.AspectJExpressionPointcutAdvisor;
import io.github.fairyspace.aop.framework.ProxyFactory;
import io.github.fairyspace.beans.BeanFactory;
import io.github.fairyspace.beans.PropertyValues;
import io.github.fairyspace.beans.factory.BeanFactoryAware;
import io.github.fairyspace.beans.factory.config.InstantiationAwareBeanPostProcessor;
import io.github.fairyspace.beans.factory.support.DefaultListableBeanFactory;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class DefaultAdvisorAutoProxyCreator implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {
    private DefaultListableBeanFactory beanFactory;

    private Set<Object> earlyProxyReferences = new HashSet<>();

    @Override
    public Object getEarlyBeanReference(Object bean, String beanName) {
        earlyProxyReferences.add(beanName);
        return wrapIfNecessary(bean, beanName);
    }

    protected Object wrapIfNecessary(Object bean, String beanName) {
        //避免死循环,Bean没有创建会自动创建一个，而创建时候又会对创建Bean初始化后处理，无限循环
        if (isInfrastructureClass(bean.getClass())) return bean;
        Collection<AspectJExpressionPointcutAdvisor> advisors = beanFactory.getBeansOfType(AspectJExpressionPointcutAdvisor.class).values();

        ProxyFactory proxyFactory = new ProxyFactory();
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            ClassFilter classFilter = advisor.getPointcut().getClassFilter();
            if (!classFilter.matches(bean.getClass())) continue;
            TargetSource targetSource = new TargetSource(bean);
            proxyFactory.setTargetSource(targetSource);
            proxyFactory.addAdvisor(advisor);

        }
        if (!proxyFactory.getAdvisors().isEmpty()) {
            return proxyFactory.getProxy();
        }

        return bean;

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (DefaultListableBeanFactory) beanFactory;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (!earlyProxyReferences.contains(beanName)) {
            return wrapIfNecessary(bean, beanName);
        }

        return bean;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {

        return null;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) {
        return pvs;
    }



    private boolean isInfrastructureClass(Class<?> beanClass) {
        boolean adviceFlag = Advice.class.isAssignableFrom(beanClass);
        boolean pointcutFlag = Pointcut.class.isAssignableFrom(beanClass);
        boolean advisor = Advisor.class.isAssignableFrom(beanClass);
        boolean result = adviceFlag || pointcutFlag || advisor;
     //   if (result) System.err.println(beanClass.getSimpleName());
        return result;

    }

}
