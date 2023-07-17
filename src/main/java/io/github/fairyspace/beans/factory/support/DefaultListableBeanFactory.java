package io.github.fairyspace.beans.factory.support;

import io.github.fairyspace.beans.factory.ConfigurableListableBeanFactory;
import io.github.fairyspace.beans.factory.config.BeanDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements BeanDefinitionRegistry, ConfigurableListableBeanFactory {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();



    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        beanDefinitionMap.forEach((beanName, beanDefinition) -> {
            Class beanClass = beanDefinition.getBeanClass();
            if (type.isAssignableFrom(beanClass)) {
                result.put(beanName, (T) getBean(beanName));
            }
        });
        return result;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
      beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName)  {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) throw new RuntimeException("No bean named '" + beanName + "' is defined");
        return beanDefinition;
    }
    @Override
    public void preInstantiateSingletons()  {
        beanDefinitionMap.keySet().forEach(beanName -> {
            getBean(beanName);
        });
    }

    @Override
    public <T> T getBean(Class<T> requiredType) {
        List<String> beanNames = new ArrayList<>();
        for (Map.Entry<String, BeanDefinition> entry : beanDefinitionMap.entrySet()) {
            Class beanClass = entry.getValue().getBeanClass();
            if (requiredType.isAssignableFrom(beanClass)) {
                beanNames.add(entry.getKey());
            }
        }
        if (1 == beanNames.size()) {
            return getBean(beanNames.get(0), requiredType);
        }

        throw new RuntimeException(requiredType + "expected single bean but found " + beanNames.size() + ": " + beanNames);
    }


}
