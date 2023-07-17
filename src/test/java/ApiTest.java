import io.github.fairyspace.aop.TargetSource;
import io.github.fairyspace.aop.aspectj.AspectJExpressionPointcutAdvisor;
import io.github.fairyspace.aop.framework.ProxyFactory;
import io.github.fairyspace.aop.framework.adapter.AfterReturningAdviceInterceptor;
import io.github.fairyspace.aop.framework.adapter.MethodBeforeAdviceInterceptor;
import io.github.fairyspace.context.support.ClassPathXmlApplicationContext;
import io.github.fairyspace.test.bean.WorldService;
import io.github.fairyspace.test.bean.WorldServiceAfterReturnAdvice;
import io.github.fairyspace.test.bean.WorldServiceBeforeAdvice;
import io.github.fairyspace.test.bean.WorldServiceImpl;
import org.junit.Test;

public class ApiTest {
    @Test
    public void testAutoProxy()  {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("classpath:spring.xml");
        //获取代理对象
        WorldService worldService = applicationContext.getBean("worldService", WorldService.class);
        worldService.explode();
    }

    @Test
    public void testAdvisor() throws Exception {
        WorldService worldService = new WorldServiceImpl();

        //Advisor是Pointcut和Advice的组合
        String expression = "execution(* io.github.fairyspace.test.bean.WorldService.explode(..))";
        //第一个切面[在那里放切点+切出来加什么逻辑]
        AspectJExpressionPointcutAdvisor advisor = new AspectJExpressionPointcutAdvisor();
        advisor.setExpression(expression);
        MethodBeforeAdviceInterceptor methodInterceptor = new MethodBeforeAdviceInterceptor(new WorldServiceBeforeAdvice());
        advisor.setAdvice(methodInterceptor);
        //第二个切面
        AspectJExpressionPointcutAdvisor advisor1=new AspectJExpressionPointcutAdvisor();
        advisor1.setExpression(expression);
        AfterReturningAdviceInterceptor afterReturningAdviceInterceptor=new AfterReturningAdviceInterceptor(new WorldServiceAfterReturnAdvice());
        advisor1.setAdvice(afterReturningAdviceInterceptor);
        //通过ProxyFactory来获得代理
        ProxyFactory factory = new ProxyFactory();
        TargetSource targetSource = new TargetSource(worldService);

        factory.setTargetSource(targetSource);
        factory.setProxyTargetClass(true);

        factory.addAdvisor(advisor);
        factory.addAdvisor(advisor1);


        WorldService proxy = (WorldService) factory.getProxy();
        proxy.explode();
    }

}



