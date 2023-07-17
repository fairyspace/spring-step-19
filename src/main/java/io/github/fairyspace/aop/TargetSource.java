package io.github.fairyspace.aop;

import io.github.fairyspace.beans.utils.ClassUtils;

public class TargetSource {
    private final Object target;

    public TargetSource(Object target) {
        this.target = target;
    }

    /**
     * Return the type of targets returned by this {@link TargetSource}.
     * <p>Can return <code>null</code>, although certain usages of a
     * <code>TargetSource</code> might just work with a predetermined
     * target class.
     * @return the type of targets returned by this {@link TargetSource}
     */
    public Class<?>[] getTargetClass(){
        Class<?> clazz = this.target.getClass();
        clazz= ClassUtils.isCglibProxyClass(clazz)?clazz.getSuperclass():clazz;
        return clazz.getInterfaces();
    }

    public Object getTarget(){
        return this.target;
    }

}
