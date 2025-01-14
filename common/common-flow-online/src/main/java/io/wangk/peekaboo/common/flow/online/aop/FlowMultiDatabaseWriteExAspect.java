package io.wangk.peekaboo.common.flow.online.aop;

import cn.hutool.core.util.StrUtil;
import io.wangk.peekaboo.common.core.util.AopTargetUtil;
import io.wangk.peekaboo.common.flow.online.object.TransactionalFlowBusinessData;
import io.wangk.peekaboo.common.online.exception.OnlineRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 主要用于拦截FlowTransactionBusinessDataListener监听器类抛出的异常。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Aspect
@Component
@Order(1)
@Slf4j
public class FlowMultiDatabaseWriteExAspect {

    @Pointcut("execution(public * io.wangk.peekaboo.common.flow.online.service.impl..*(..)) " +
            "&& @annotation(io.wangk.peekaboo.common.core.annotation.MultiDatabaseWriteMethod)")
    public void multiDatabaseWriteMethodPointCut() {
        // 空注释，避免sonar警告
    }

    @Around("multiDatabaseWriteMethodPointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String initMethod = AopTargetUtil.getFullMethodName(joinPoint);
        try {
            // 调用原来的方法
            Object result = joinPoint.proceed();
            TransactionalFlowBusinessData data = TransactionalFlowBusinessData.getFromRequestAttribute();
            if (StrUtil.equals(initMethod, data.getInitMethod()) && data.getErrorReason() != null) {
                throw new OnlineRuntimeException(data.getErrorReason());
            }
            return result;
        } finally {
            TransactionalFlowBusinessData data = TransactionalFlowBusinessData.getFromRequestAttribute();
            if (data != null && StrUtil.equals(initMethod, data.getInitMethod())) {
                TransactionalFlowBusinessData.removeFromRequestAttribute();
            }
        }
    }
}
