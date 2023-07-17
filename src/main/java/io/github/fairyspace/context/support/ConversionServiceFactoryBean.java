package io.github.fairyspace.context.support;

import io.github.fairyspace.beans.factory.FactoryBean;
import io.github.fairyspace.beans.factory.InitializingBean;
import io.github.fairyspace.core.convert.ConversionService;
import io.github.fairyspace.core.convert.converter.Converter;
import io.github.fairyspace.core.convert.converter.ConverterFactory;
import io.github.fairyspace.core.convert.converter.ConverterRegistry;
import io.github.fairyspace.core.convert.converter.GenericConverter;
import io.github.fairyspace.core.convert.support.DefaultConversionService;
import io.github.fairyspace.core.convert.support.GenericConversionService;

import java.util.Set;

public class ConversionServiceFactoryBean implements FactoryBean<ConversionService>, InitializingBean {


    private Set<?> converters;

    private GenericConversionService conversionService;

    @Override
    public ConversionService getObject()  {
        return conversionService;
    }

    @Override
    public Class<?> getObjectType() {
        return conversionService.getClass();
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    @Override
    public void afterPropertiesSet() {
        this.conversionService = new DefaultConversionService();
        registerConverters(converters, conversionService);
    }

    private void registerConverters(Set<?> converters, ConverterRegistry registry) {
        if (converters != null) {
            for (Object converter : converters) {
                if (converter instanceof GenericConverter) {
                    registry.addConverter((GenericConverter) converter);
                } else if (converter instanceof Converter<?, ?>) {
                    registry.addConverter((Converter<?, ?>) converter);
                } else if (converter instanceof ConverterFactory<?, ?>) {
                    registry.addConverterFactory((ConverterFactory<?, ?>) converter);
                } else {
                    throw new IllegalArgumentException("Each converter object must implement one of the " +
                            "Converter, ConverterFactory, or GenericConverter interfaces");
                }
            }
        }
    }

    public void setConverters(Set<?> converters) {
        this.converters = converters;
    }

}
