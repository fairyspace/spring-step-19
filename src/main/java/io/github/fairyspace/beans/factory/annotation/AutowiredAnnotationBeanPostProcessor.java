package io.github.fairyspace.beans.factory.annotation;

import cn.hutool.core.bean.BeanUtil;
import io.github.fairyspace.beans.BeanFactory;
import io.github.fairyspace.beans.PropertyValues;
import io.github.fairyspace.beans.factory.BeanFactoryAware;
import io.github.fairyspace.beans.factory.ConfigurableListableBeanFactory;
import io.github.fairyspace.beans.factory.config.InstantiationAwareBeanPostProcessor;
import io.github.fairyspace.beans.utils.ClassUtils;

import java.lang.reflect.Field;

//处理@Autowired和@Value注解的BeanPostProcessor
public class AutowiredAnnotationBeanPostProcessor implements InstantiationAwareBeanPostProcessor, BeanFactoryAware {

    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return null;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        return null;
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) {
        return null;
    }

    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) {
        return true;
    }

    @Override
    public PropertyValues postProcessPropertyValues(PropertyValues pvs, Object bean, String beanName) {
        Class<?> clazz = bean.getClass();
        clazz = ClassUtils.isCglibProxyClass(clazz) ? clazz.getSuperclass() : clazz;

        Field[] fields = clazz.getDeclaredFields();

        //处理@Value注解
        for (Field field : fields) {
            Value valueAnnotation = field.getAnnotation(Value.class);
            if (valueAnnotation != null) {
                String value = valueAnnotation.value();
                value = beanFactory.resolveEmbeddedValue(value);
                BeanUtil.setFieldValue(bean, field.getName(), value);
            }
        }

        //处理@Autowired注解
        for (Field field : fields) {
            Autowired autowiredAnnotation = field.getAnnotation(Autowired.class);
            if (autowiredAnnotation != null) {
                Class<?> fieldType = field.getType();
                String dependentBeanName = null;
                Qualifier qualifierAnnotation = field.getAnnotation(Qualifier.class);
                Object dependentBean = null;

                if (qualifierAnnotation != null) {
                    dependentBeanName = qualifierAnnotation.value();
                    dependentBean = beanFactory.getBean(dependentBeanName, fieldType);
                } else {
                    dependentBean = beanFactory.getBean(fieldType);
                }
                BeanUtil.setFieldValue(bean, field.getName(), dependentBean);
            }

        }

        return pvs;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
