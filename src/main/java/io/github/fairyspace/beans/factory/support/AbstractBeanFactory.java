package io.github.fairyspace.beans.factory.support;

import io.github.fairyspace.beans.factory.FactoryBean;
import io.github.fairyspace.beans.factory.config.BeanDefinition;
import io.github.fairyspace.beans.factory.config.BeanPostProcessor;
import io.github.fairyspace.beans.factory.config.ConfigurableBeanFactory;
import io.github.fairyspace.beans.utils.ClassUtils;
import io.github.fairyspace.beans.utils.StringValueResolver;
import io.github.fairyspace.core.convert.ConversionService;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {
    /**
     * ClassLoader to resolve bean class names with, if necessary
     */
    private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

    /** BeanPostProcessors to apply in createBean */
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    private final List<StringValueResolver> embeddedValueResolvers = new ArrayList<>();

    private ConversionService conversionService;

    @Override
    public Object getBean(String name)  {
        return doGetBean(name, null);
    }

    @Override
    public boolean containsBean(String name) {
        return containsBeanDefinition(name);
    }

    @Override
    public void setConversionService(ConversionService conversionService) {
        this.conversionService=conversionService;
    }

    @Override
    public ConversionService getConversionService() {
        return this.conversionService;
    }

    protected abstract boolean containsBeanDefinition(String name);

    @Override
    public Object getBean(String name,Object... args) {
       return doGetBean(name,args);
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) {
        return (T) getBean(name);
    }

    protected <T> T doGetBean(final String name, final Object[] args) {
        Object sharedInstance = getSingleton(name);
        if (sharedInstance != null) {
            // 如果是 FactoryBean，则需要调用 FactoryBean#getObject
            return (T) getObjectForBeanInstance(sharedInstance, name);
        }
        BeanDefinition beanDefinition = getBeanDefinition(name);
        T bean = (T) createBean(name, beanDefinition, args);
        return (T) getObjectForBeanInstance(bean, name);

    }

    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }

        //有FactoryBean接口标记的特殊处理
        Object object = getCachedObjectForFactoryBean(beanName);
        if (object == null) {
            FactoryBean<?> factoryBean = (FactoryBean<?>) beanInstance;
            object = getObjectFromFactoryBean(factoryBean, beanName);
        }
        return object;
    }

    protected abstract Object createBean(String name, BeanDefinition beanDefinition,Object[] args);

    protected abstract BeanDefinition getBeanDefinition(String name);


    /**
     * Return the list of BeanPostProcessors that will get applied
     * to beans created with this factory.
     */
    public List<BeanPostProcessor> getBeanPostProcessors() {
        return this.beanPostProcessors;
    }


    @Override
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor){
        this.beanPostProcessors.remove(beanPostProcessor);
        this.beanPostProcessors.add(beanPostProcessor);
    }

    public ClassLoader getBeanClassLoader() {
        return this.beanClassLoader;
    }


    @Override
    public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
        this.embeddedValueResolvers.add(valueResolver);
    }

    @Override
    public String resolveEmbeddedValue(String value) {
        String result = value;
        for (StringValueResolver resolver : this.embeddedValueResolvers) {
            //取最后一次为准作为值
            result = resolver.resolveStringValue(result);
        }
        return result;
    }



}
