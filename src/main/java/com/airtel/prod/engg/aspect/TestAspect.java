package com.airtel.prod.engg.aspect;

import java.lang.reflect.Method;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import com.airtel.prod.engg.annotation.Test;

@Component
@Aspect
public class TestAspect {

	@Before("@annotation(test)")
	public void testAdvice(JoinPoint pdp, Test test){
		MethodSignature signature = (MethodSignature) pdp.getSignature();
	    Method method = signature.getMethod();
	    Class<?> declaringClass = method.getDeclaringClass();
		System.out.println("inside the class " + declaringClass);
		System.out.println("the method of the class is " + method);
	}
}
