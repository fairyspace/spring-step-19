package io.github.fairyspace.beans;

public interface BeanFactory {
    Object getBean(String name);
    Object getBean(String name, Object... args);

    <T>T getBean(String name,Class<T> requireType);

    <T> T getBean(Class<T> requiredType);

    boolean containsBean(String name);
}
