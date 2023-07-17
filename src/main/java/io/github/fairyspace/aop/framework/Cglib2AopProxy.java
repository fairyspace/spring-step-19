package io.github.fairyspace.aop.framework;

import io.github.fairyspace.aop.AdvisedSupport;
import io.github.fairyspace.beans.utils.ClassUtils;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;
import java.util.List;


public class Cglib2AopProxy  implements AopProxy{
    private final AdvisedSupport advised;

    public Cglib2AopProxy(AdvisedSupport advised) {
        this.advised = advised;
    }
    @Override
    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        Class<?> originClass = advised.getTargetSource().getTarget().getClass();
        originClass=ClassUtils.isCglibProxyClass(originClass)?originClass.getSuperclass():originClass;
        enhancer.setSuperclass(originClass);
        enhancer.setInterfaces(advised.getTargetSource().getTargetClass());
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        return enhancer.create();
    }

    private static class DynamicAdvisedInterceptor implements MethodInterceptor{
        private final AdvisedSupport advised;

        public DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
        }

        @Override
        public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = advised.getTargetSource().getTarget();
            List<Object> chain = this.advised.getInterceptorsAndDynamicInterceptionAdvice(method, target.getClass());
            //aop实现
            CglibMethodInvocation methodInvocation = new CglibMethodInvocation(target, method, args, methodProxy,chain);

            Object retVal = null;
            if(chain==null||chain.isEmpty()){
                return method.invoke(methodInvocation);
            }else{
                // 将拦截器统一封装成ReflectiveMethodInvocation
                MethodInvocation invocation =
                        new ReflectiveMethodInvocation( target, method, args, chain);
                // Proceed to the joinpoint through the interceptor chain.
                // 执行拦截器链
                retVal = invocation.proceed();
            }
            return retVal;
        }
    }

    private static class CglibMethodInvocation extends  ReflectiveMethodInvocation{
        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method, Object[] arguments, MethodProxy methodProxy,List<Object> chain) {
            super(target, method, arguments,chain);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke(this.target, this.arguments);
        }
    }
}
