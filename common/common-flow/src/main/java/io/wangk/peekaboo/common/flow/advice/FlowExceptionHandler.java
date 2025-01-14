package io.wangk.peekaboo.common.flow.advice;

import com.alibaba.fastjson.JSON;
import io.wangk.peekaboo.common.core.constant.ErrorCodeEnum;
import io.wangk.peekaboo.common.core.object.ResponseResult;
import io.wangk.peekaboo.common.core.util.ContextUtil;
import io.wangk.peekaboo.common.flow.exception.FlowEmptyUserException;
import io.wangk.peekaboo.common.flow.model.FlowTaskComment;
import io.wangk.peekaboo.common.flow.service.FlowTaskCommentService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.FlowableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 流程业务层的异常处理类。
 *
 * @author wangk
 * @date 2025-01-14
 */
@Slf4j
@Order(1)
@RestControllerAdvice("io.wangk.peekaboo")
public class FlowExceptionHandler {

	@Autowired
	private FlowTaskCommentService flowTaskCommentService;

    @ExceptionHandler(value = FlowableException.class)
	public ResponseResult<Void> exceptionHandle(FlowableException ex, HttpServletRequest request) {
		if (ex instanceof FlowEmptyUserException) {
			FlowEmptyUserException flowEmptyUserException = (FlowEmptyUserException) ex;
			FlowTaskComment comment = JSON.parseObject(flowEmptyUserException.getMessage(), FlowTaskComment.class);
			flowTaskCommentService.saveNew(comment);
			return ResponseResult.error(ErrorCodeEnum.DATA_VALIDATED_FAILED, "下一个任务节点的审批人为空，提交被自动驳回！");
		}
		log.error("Unhandled FlowException from URL [" + request.getRequestURI() + "]", ex);
		ContextUtil.getHttpResponse().setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		return ResponseResult.error(ErrorCodeEnum.UNHANDLED_EXCEPTION, ex.getMessage());
	}

	@SuppressWarnings("unchecked")
	private <T extends Exception> T findCause(Throwable ex, Class<T> clazz) {
		if (ex.getCause() == null) {
			return null;
		}
		if (ex.getCause().getClass().equals(clazz)) {
			return (T) ex.getCause();
		} else {
			return this.findCause(ex.getCause(), clazz);
		}
	}
}
