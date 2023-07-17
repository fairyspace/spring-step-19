package io.github.fairyspace.context.support;

import io.github.fairyspace.beans.core.io.DefaultResourceLoader;
import io.github.fairyspace.beans.factory.ConfigurableListableBeanFactory;
import io.github.fairyspace.beans.factory.config.BeanFactoryPostProcessor;
import io.github.fairyspace.beans.factory.config.BeanPostProcessor;
import io.github.fairyspace.context.ApplicationEvent;
import io.github.fairyspace.context.ApplicationListener;
import io.github.fairyspace.context.ConfigurableApplicationContext;
import io.github.fairyspace.context.event.ApplicationEventMulticaster;
import io.github.fairyspace.context.event.ContextRefreshedEvent;
import io.github.fairyspace.context.event.SimpleApplicationEventMulticaster;
import io.github.fairyspace.core.convert.ConversionService;

import java.util.Collection;
import java.util.Map;

public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

    public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

    private ApplicationEventMulticaster applicationEventMulticaster;
    @Override
    public void refresh() {
        // 1. 创建 BeanFactory，并加载[Path>ResourceLoad+Resource>]BeanDefinition
        refreshBeanFactory();

        // 2. 获取 BeanFactory
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();

        // 3 添加 ApplicationContextAwareProcessor，让继承自 ApplicationContextAware 的 Bean 对象都能感知所属的 ApplicationContext
        beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));


        // 4 在Bean实例化之前，执行 BeanFactoryPostProcessor (Invoke factory processors registered as beans in the context.)
        invokeBeanFactoryPostProcessors(beanFactory);


        //5. BeanPostProcessor需要提前于其他 Bean 对象实例化之前执行注册操作【如增加default切面处理】
        registerBeanPostProcessors(beanFactory);


        // 6. 初始化事件发布者
        initApplicationEventMulticaster();

        // 7. 注册事件监听器
        registerListeners();

        // 8. 设置类型转换器，提前实例化单例Bean对象
        finishBeanFactoryInitialization(beanFactory);


        // 9. 发布容器刷新完成事件
        finishRefresh();

    }

    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory){
        //设置类型转换器
        if (beanFactory.containsBean("conversionService")){
            Object conversionService = beanFactory.getBean("conversionService");
            if (conversionService instanceof ConversionService){
                beanFactory.setConversionService( ((ConversionService) conversionService));
            }
        }
        //提前实例化Bean
        beanFactory.preInstantiateSingletons();
    }


    protected abstract void refreshBeanFactory();

    protected abstract ConfigurableListableBeanFactory getBeanFactory();

    private void initApplicationEventMulticaster() {
        ConfigurableListableBeanFactory beanFactory = getBeanFactory();
        applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
        beanFactory.addSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
    }

    private void registerListeners() {
        Collection<ApplicationListener> applicationListeners = getBeansOfType(ApplicationListener.class).values();
        for (ApplicationListener listener : applicationListeners) {
            applicationEventMulticaster.addApplicationListener(listener);
        }
    }

    protected void finishRefresh() {
        publishEvent(new ContextRefreshedEvent(this));
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventMulticaster.multicastEvent(event);
    }



    private void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        for (BeanFactoryPostProcessor beanFactoryPostProcessor : beanFactoryPostProcessorMap.values()) {
            beanFactoryPostProcessor.postProcessBeanFactory(beanFactory);
        }
    }

    private void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorMap.values()) {
            //有声明一个切面处理
            beanFactory.addBeanPostProcessor(beanPostProcessor);
        }
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        return getBeanFactory().getBeansOfType(type);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return getBeanFactory().getBeanDefinitionNames();
    }

    @Override
    public Object getBean(String name)  {
        return getBeanFactory().getBean(name);
    }

    @Override
    public Object getBean(String name, Object... args)  {
        return getBeanFactory().getBean(name, args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType)  {
        return getBeanFactory().getBean(name, requiredType);
    }

    @Override
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }

    @Override
    public void close() {
        getBeanFactory().destroySingletons();
    }

    @Override
    public <T> T getBean(Class<T> requiredType)  {
        return getBeanFactory().getBean(requiredType);
    }

    @Override
    public boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }
}
