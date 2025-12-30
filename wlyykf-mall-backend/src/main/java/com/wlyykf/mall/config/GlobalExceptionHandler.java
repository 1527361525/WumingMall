package com.wlyykf.mall.config;

import com.wlyykf.mall.exception.BusinessException;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value=Exception.class)
    Object handleException(Exception e){
        ResponseVO response = new ResponseVO();
        if (e instanceof BusinessException) {
            BusinessException biz=(BusinessException)e;
            log.error("业务异常",e);
            response.setCode(biz.getCode());
            response.setInfo(biz.getMessage());
        } else {
            log.error("服务器异常",e);
            response.setCode(500);
            response.setInfo("服务器返回错误，请联系管理员");
        }
        return response;
    }
}
