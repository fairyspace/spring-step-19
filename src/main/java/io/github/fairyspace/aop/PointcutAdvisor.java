package io.github.fairyspace.aop;

public interface PointcutAdvisor extends Advisor{
    Pointcut getPointcut();
}
