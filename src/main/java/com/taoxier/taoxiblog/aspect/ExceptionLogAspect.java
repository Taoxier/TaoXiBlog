package com.taoxier.taoxiblog.aspect;

import com.taoxier.taoxiblog.annotation.OperationLogger;
import com.taoxier.taoxiblog.annotation.VisitLogger;
import com.taoxier.taoxiblog.model.entity.ExceptionLog;
import com.taoxier.taoxiblog.service.ExceptionLogService;
import com.taoxier.taoxiblog.util.AopUtils;
import com.taoxier.taoxiblog.util.IpAddressUtils;
import com.taoxier.taoxiblog.util.JacksonUtils;
import com.taoxier.taoxiblog.util.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @Author taoxier
 * @Date
 * @描述 AOP记录异常日志
 */
    @Component
    @Aspect
    public class ExceptionLogAspect {
        @Autowired
        ExceptionLogService exceptionLogService;

        /**
         * 配置切入点
         */
        @Pointcut("execution(* com.taoxier.taoxiblog.controller..*.*(..))")
        public void logPointcut() {
        }

        @AfterThrowing(value = "logPointcut()", throwing = "e")
        public void logAfterThrowing(JoinPoint joinPoint, Exception e) {
            ExceptionLog log = handleLog(joinPoint, e);
            exceptionLogService.saveExceptionLog(log);
        }


    /**
     * 设置ExceptionLog对象属性
     *
     * @return
     */
    private ExceptionLog handleLog(JoinPoint joinPoint, Exception e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        String method = request.getMethod();
        String ip = IpAddressUtils.getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String description = getDescriptionFromAnnotations(joinPoint);
        String error = StringUtils.getStackTrace(e);
        ExceptionLog log = new ExceptionLog(uri, method, description, error, ip, userAgent);
        Map<String, Object> requestParams = AopUtils.getRequestParams(joinPoint);
        try {
            String paramsJson = JacksonUtils.writeValueAsString(requestParams);
            // 检查字符串长度，如果长度小于2000则截取到字符串末尾
            int endIndex = Math.min(paramsJson.length(), 2000);
            log.setParam(paramsJson.substring(0, endIndex));
        } catch (Exception ex) {
            // 处理可能的JSON转换异常
            log.setParam("Failed to convert request params to JSON");
        }
        return log;
    }

        private String getDescriptionFromAnnotations(JoinPoint joinPoint) {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            OperationLogger operationLogger = method.getAnnotation(OperationLogger.class);
            if (operationLogger != null) {
                return operationLogger.value();
            }
            VisitLogger visitLogger = method.getAnnotation(VisitLogger.class);
            if (visitLogger != null) {
                return visitLogger.value().getBehavior();
            }
            return "";
        }
    }

