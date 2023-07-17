package io.github.fairyspace.aop.framework;

import io.github.fairyspace.aop.AdvisedSupport;

public class ProxyFactory extends AdvisedSupport{


    public ProxyFactory() {

    }

    public Object getProxy() {
        return createAopProxy().getProxy();
    }

    private AopProxy createAopProxy() {
        if (this.isProxyTargetClass()) {
            return new Cglib2AopProxy(this);
        }

        return new JdkDynamicAopProxy(this);
    }
}
