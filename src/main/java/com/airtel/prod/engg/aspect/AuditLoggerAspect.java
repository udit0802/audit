package com.airtel.prod.engg.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.airtel.prod.engg.annotation.Audit;

@Component
@Aspect
public class AuditLoggerAspect {
	
	private static final Logger log = LogManager.getLogger(AuditLoggerAspect.class);

	@Around("@annotation(audit)")
	public Object auditAdvice(ProceedingJoinPoint pdp, Audit audit){
		MethodSignature methodSignature = (MethodSignature) pdp.getStaticPart().getSignature();
		Method method = methodSignature.getMethod();
		Class<?> clazz = pdp.getTarget().getClass();
		if(clazz.getAnnotation(RestController.class) == null){
			try {
				return pdp.proceed();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		String apiUrl = "";
		RequestMapping rm = clazz.getAnnotation(RequestMapping.class);
		if(rm != null){
			apiUrl += rm.value()[0];
		}
		RequestMapping _rm = method.getAnnotation(RequestMapping.class);
		apiUrl += _rm.value()[0];
		log.info("requested url : " + apiUrl);
		log.info("API_NAME : " + audit.API_NAME());
		Annotation[][] parameterAnnotations = method.getParameterAnnotations();
		log.info("Request :[");
		for (int argIndex = 0; argIndex < pdp.getArgs().length; argIndex++) {
			for (Annotation annotation : parameterAnnotations[argIndex]) {
				if (annotation instanceof RequestParam) {
					log.info("Request Params received : " + pdp.getArgs()[argIndex].toString());
					break;
				}else if(annotation instanceof PathVariable){
					log.info("Path Varaible received : " + pdp.getArgs()[argIndex].toString());
					break;
				}else if(annotation instanceof RequestBody){
					log.info("RequestBody received : " + pdp.getArgs()[argIndex].toString());
					break;
				}
			}
		}
		log.info("]");
		Object result = null;
		try {
			result = pdp.proceed();
			log.info("response received : " + result.toString());
			log.info("\n");
		} catch (Throwable e) {
			e.printStackTrace();
		}
		return result;
	}
}
