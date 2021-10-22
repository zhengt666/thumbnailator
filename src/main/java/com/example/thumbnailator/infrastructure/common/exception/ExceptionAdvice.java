package com.example.thumbnailator.infrastructure.common.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.thumbnailator.infrastructure.common.result.ThumbnailsResult;

import lombok.extern.slf4j.Slf4j;

/**
 * 统一异常处理
 *
 * <p>
 * 拦截404需添加以下配置：
 * spring.mvc.throw-exception-if-no-handler-found=true
 * spring.resources.add-mappings=false
 *
 * @author Bennu
 * @date 2020/5/7
 */
@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {
    

    @ExceptionHandler(value = Throwable.class)
    public ThumbnailsResult<Object> errorHandler(Exception ex) {
        ThumbnailsResult<Object> result = dealWithException(ex);
        return result;
    }

    private ThumbnailsResult<Object> dealWithException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return  ThumbnailsResult.getInstance(ex.getMessage());
    }
}
