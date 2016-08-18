package next.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import next.controller.user.UserController;

@Aspect
@Component
public class PerformanceAspect {
	private static final Logger log = LoggerFactory.getLogger(UserController.class);

	@Pointcut("within(next.service..*) || within(next.dao..*)")
	public void myPointcut(){
		
	}
	
	@Around("myPointcut()")
	public Object doBasicProfiling(ProceedingJoinPoint pjp) throws Throwable{
		StopWatch watch = new StopWatch();
		watch.start();
		Object retVal = pjp.proceed();
		watch.stop();
		log.debug("execution method : {}, execution time: {}", 
				pjp.toShortString(), watch.getTotalTimeMillis());
		log.debug("parameters : {}", pjp.getArgs());
		return retVal;
	}
}
