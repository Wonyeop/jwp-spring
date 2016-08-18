package proxy;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;

import org.junit.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

public class HelloTest {

	@Test
	public void hello() {
		HelloTarget ht = new HelloTarget();
		String hello = ht.sayHello("Michael1");
		System.out.println(hello);
	}
	
	@Test
	public void upper(){
		HelloUppercase hu = new HelloUppercase(new HelloTarget());
		String hello = hu.sayHello("Michael2");
		System.out.println(hello);
	}
	
	@Test
	public void test() {
		ProxyFactoryBean pfBean = new ProxyFactoryBean();
		pfBean.setTarget(new HelloTarget());
		
		NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
		pointcut.setMappedName("sayH*");
		pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
//		pfBean.addAdvice(new UppercaseAdvice());
		
		
		Hello proxiedHello = (Hello) pfBean.getObject();
		System.out.println(proxiedHello.sayHello("Toby"));
		System.out.println(proxiedHello.sayHi("Toby"));
		System.out.println(proxiedHello.sayThankYou("Toby"));
	}

}
