package io.wangk.peekaboo.common.online.aop;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.util.AopTargetUtil;
import io.wangk.peekaboo.common.online.object.TransactionalBusinessData;
import io.wangk.peekaboo.common.online.service.OnlineOperationService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 拦截多数据库数据写入的AOP对象。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Aspect
@Component
@Slf4j
public class MultiDatabaseWriteAspect {

    @Autowired
    private OnlineOperationService onlineOperationService;

    @Pointcut("execution(public * io.wangk.peekaboo.common.online.service.impl..*(..)) " +
            "&& @annotation(io.wangk.peekaboo.common.core.annotation.MultiDatabaseWriteMethod)")
    public void multiDatabaseAccessMethodPointCut() {
        // 空注释，避免sonar警告
    }

    @Around("multiDatabaseAccessMethodPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String initMethod = AopTargetUtil.getFullMethodName(joinPoint);
        TransactionalBusinessData data = TransactionalBusinessData.getOrCreateFromRequestAttribute();
        if (data.getInitMethod() == null) {
            data.setInitMethod(initMethod);
        }
        try {
            // 调用原来的方法
            Object result = joinPoint.proceed();
            if (StrUtil.equals(initMethod, data.getInitMethod()) && CollUtil.isNotEmpty(data.getSqlDataList())) {
                onlineOperationService.bulkHandleBusinessData(data);
            }
            return result;
        } catch (Exception e) {
            TransactionalBusinessData.removeFromRequestAttribute();
            throw e;
        } finally {
            if (StrUtil.equals(initMethod, data.getInitMethod())) {
                TransactionalBusinessData.removeFromRequestAttribute();
            }
        }
    }
}
