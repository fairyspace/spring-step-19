package io.github.fairyspace.beans.factory.config;

import io.github.fairyspace.beans.factory.HierarchicalBeanFactory;
import io.github.fairyspace.beans.utils.StringValueResolver;
import io.github.fairyspace.core.convert.ConversionService;

public interface ConfigurableBeanFactory extends HierarchicalBeanFactory,SingletonRegistry{
    String SCOPE_SINGLETON = "singleton";

    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    void addEmbeddedValueResolver(StringValueResolver valueResolver);

    /**
     * Resolve the given embedded value, e.g. an annotation attribute.
     * @param value the value to resolve
     * @return the resolved value (may be the original value as-is)
     * @since 3.0
     */
    String resolveEmbeddedValue(String value);


    void setConversionService(ConversionService conversionService);

    ConversionService getConversionService();
}
