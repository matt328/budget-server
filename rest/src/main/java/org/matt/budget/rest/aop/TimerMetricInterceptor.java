package org.matt.budget.rest.aop;

import java.io.Serializable;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Timed
@Interceptor
public class TimerMetricInterceptor implements Serializable {

  private static final long serialVersionUID = 1L;

  private static final Logger log = LoggerFactory.getLogger("org.matt.metrics");

  @AroundInvoke
  public Object computeLatency(InvocationContext invocationCtx) throws Exception {
    long startTime = System.currentTimeMillis();
    // execute the intercepted method and store the return value
    Object returnValue = invocationCtx.proceed();
    long endTime = System.currentTimeMillis();
    log.info("{}.{}: {}ms", invocationCtx.getTarget().getClass().getSuperclass().getName(), invocationCtx.getMethod().getName(), (endTime - startTime));
    return returnValue;
  }
}
