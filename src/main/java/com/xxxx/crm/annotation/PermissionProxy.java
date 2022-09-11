package com.xxxx.crm.annotation;

import com.xxxx.crm.exceptions.NoLoginException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {
    @Autowired
    private HttpSession session;

    @Around(value = "@annotation(com.xxxx.crm.annotation.RequirePermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        List<String> permissions = (List<String>) session.getAttribute("permissions");
        if (null == permissions || permissions.size() == 0){
            throw new NoLoginException();
        }
        Object result = null;
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        RequirePermission requirePermission = methodSignature.getMethod().getDeclaredAnnotation(RequirePermission.class);
        if (!(permissions.contains(requirePermission.code()))){
            throw new NoLoginException();
        }
        result = pjp.proceed();
        return result;
    }
}
