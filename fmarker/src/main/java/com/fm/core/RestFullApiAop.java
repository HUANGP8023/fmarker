package com.fm.core;


import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
@Aspect
public class RestFullApiAop {

	private final static Log log = LogFactory.getLog(RestFullApiAop.class);

	// 配置切入点,该方法无方法体,主要为方便同类中其他方法使用此处配置的切入点
	@Pointcut("execution(* com.fm.restController..*.*(..))")
	public void aspect() {}

	/*
	 * 配置前置通知,使用在方法aspect()上注册的切入点 同时接受JoinPoint切入点对象,可以没有该参数
	 */
	@Before("aspect()")
	public void before(JoinPoint joinPoint) {
		if (log.isInfoEnabled()) {
			log.info("before " + joinPoint);
		}
	}

	// 配置后置通知,使用在方法aspect()上注册的切入点
	@After("aspect()")
	public void after(JoinPoint  joinPoint) {
		if (log.isInfoEnabled()) {
			log.info("after " + joinPoint);
		}
	}

	// 配置环绕通知,使用在方法aspect()上注册的切入点
	@Around("aspect()")
	public Object  around(ProceedingJoinPoint   pjp) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();  
		long start = System.currentTimeMillis();
		System.out.println("请求参数:"+request.getParameter("aaaaaaa"));
		outParamsValue(request);
		try {
			//访问目标方法的参数：
			long end = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info("around " + pjp + "\tUse time : "+ (end - start) + " ms!");
			}
			Object result = pjp.proceed(); //获取被切函数的 返回值
			System.out.println("返回参数:"+ String.valueOf(result));
			return result;
		} catch (Throwable e) {
			long end = System.currentTimeMillis();
			if (log.isInfoEnabled()) {
				log.info("around " + pjp + "\tUse time : "+ (end - start) + " ms with exception : "+ e.getMessage());
			}
			return null;
		}
		
	}
	
	
	/** 
	 * @des 请求地址和请求参数输出
	 * @author HUANGP
	 * @date 2017-6-14
	 * @param request
	 */
	@SuppressWarnings("rawtypes")
	private void outParamsValue(HttpServletRequest request){
		String requestUrl = String.valueOf(request.getRequestURL());
		Enumeration KeyVal=request.getParameterNames();
		String values="";
		while(KeyVal.hasMoreElements()){
			String key=String.valueOf(KeyVal.nextElement());
			String value=request.getParameter(key);
			values+="&"+key+"="+value;
		}
		log.info("请求参数:"+values.replaceFirst("&",""));
	}

	// 配置后置返回通知,使用在方法aspect()上注册的切入点
	@AfterReturning("aspect()")
	public void afterReturn(JoinPoint joinPoint) {
		if (log.isInfoEnabled()) {
			log.info("afterReturn " + joinPoint);
		}
	}

	// 配置抛出异常后通知,使用在方法aspect()上注册的切入点
	@AfterThrowing(pointcut = "aspect()", throwing = "ex")
	public void afterThrow(JoinPoint joinPoint, Exception ex) {
		if (log.isInfoEnabled()) {
			log.info("<<<<<<<<"+ex.getMessage()+">>>>>>>>");
			log.info("afterThrow " + joinPoint + "\t" + ex.getMessage());
		}
	}

}
