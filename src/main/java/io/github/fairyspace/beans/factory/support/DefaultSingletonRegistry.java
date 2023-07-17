package io.github.fairyspace.beans.factory.support;

import io.github.fairyspace.beans.factory.DisposableBean;
import io.github.fairyspace.beans.factory.ObjectFactory;
import io.github.fairyspace.beans.factory.config.SingletonRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultSingletonRegistry implements SingletonRegistry {
    private Map<String,Object> singletonObjects=new HashMap<>();

    //二级缓存
    protected Map<String, Object> earlySingletonObjects = new HashMap<>();

    // 三级缓存，存放代理对象
    /**
     * Cache of singleton factories: bean name --> ObjectFactory
     */
    private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<String, ObjectFactory<?>>();


    private final Map<String, DisposableBean> disposableBeans = new HashMap<>();
    @Override
    public Object getSingleton(String beanName) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject==null){
            singletonObject = earlySingletonObjects.get(beanName);
            // 判断二级缓存中是否有对象，这个对象就是代理对象，因为只有代理对象才会放到三级缓存中
            if (singletonObject==null){
                ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject();
                    // 把三级缓存中的代理对象，放入二级缓存中
                    earlySingletonObjects.put(beanName, singletonObject);
                    singletonFactories.remove(beanName);
                }
            }
        }
        return singletonObject;
    }

    @Override
    public void addSingleton(String beanName, Object singletonObject){
        singletonObjects.put(beanName, singletonObject);
        //简单成品Bean时候删除辅助成品
        earlySingletonObjects.remove(beanName);
        singletonFactories.remove(beanName);
    }

    protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory){
        singletonFactories.put(beanName, singletonFactory);
    }


    public void registerDisposableBean(String beanName, DisposableBean bean) {
        disposableBeans.put(beanName, bean);
    }

    public void destroySingletons() {
        ArrayList<String> beanNames = new ArrayList<>(disposableBeans.keySet());
        for (String beanName : beanNames) {
            DisposableBean disposableBean = disposableBeans.remove(beanName);
            try {
                disposableBean.destroy();
            } catch (Exception e) {
                throw new RuntimeException("Destroy method on bean with name '" + beanName + "' threw an exception", e);
            }
        }
    }
}
