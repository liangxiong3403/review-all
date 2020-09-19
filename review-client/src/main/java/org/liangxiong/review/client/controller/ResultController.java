package org.liangxiong.review.client.controller;

import lombok.extern.slf4j.Slf4j;
import org.liangxiong.review.client.annotation.ResultResponseBody;
import org.liangxiong.review.client.exception.ResultException;
import org.liangxiong.review.client.vo.Result;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.WebUtils;

import java.lang.annotation.Annotation;

/**
 * @author liangxiong
 * @version 1.0.0
 * @date 2020-09-08 20:05
 * @description
 **/
@Slf4j
@RestControllerAdvice
public class ResultController implements ResponseBodyAdvice<Object> {

    private static final Class<? extends Annotation> ANNOTATION_TYPE = ResultResponseBody.class;

    /**
     * 类或方法被注解ResultResponseBody标注
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return AnnotatedElementUtils.hasAnnotation(returnType.getContainingClass(), ANNOTATION_TYPE) || returnType.hasMethodAnnotation(ANNOTATION_TYPE);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> aClass, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (body instanceof Result) {
            return body;
        }
        return Result.success(body);
    }

    /**
     * 切面拦截处理全局异常
     *
     * @param exception  异常类型
     * @param webRequest web请求
     * @return 响应体
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Result<?>> exceptionHandler(Exception exception, WebRequest webRequest) {
        log.error("ExceptionHandler: {}", exception.getMessage());
        HttpHeaders headers = new HttpHeaders();
        if (exception instanceof ResultException) {
            return this.handleResultException((ResultException) exception, headers, webRequest);
        } else {
            return this.handlerException(exception, headers, webRequest);
        }
    }

    /**
     * 处理特定业务异常
     *
     * @param exception
     * @param headers
     * @param webRequest
     * @return
     */
    private ResponseEntity<Result<?>> handleResultException(ResultException exception, HttpHeaders headers, WebRequest webRequest) {
        Result<?> body = Result.failure(exception.getResultStatus());
        HttpStatus status = exception.getResultStatus().getHttpStatus();
        return this.handlerExceptionInternal(exception, body, headers, status, webRequest);
    }

    /**
     * 处理一般异常
     *
     * @param exception
     * @param headers
     * @param webRequest
     * @return
     */
    private ResponseEntity<Result<?>> handlerException(Exception exception, HttpHeaders headers, WebRequest webRequest) {
        Result<?> body = Result.failure();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return this.handlerExceptionInternal(exception, body, headers, status, webRequest);
    }

    /**
     * 处理系统服务内部错误
     *
     * @param exception
     * @param body
     * @param headers
     * @param status
     * @param webRequest
     * @return
     */
    private ResponseEntity<Result<?>> handlerExceptionInternal(Exception exception, Result<?> body, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        if (HttpStatus.INTERNAL_SERVER_ERROR.equals(status)) {
            webRequest.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST);
        }
        return new ResponseEntity<>(body, headers, status);
    }

}
