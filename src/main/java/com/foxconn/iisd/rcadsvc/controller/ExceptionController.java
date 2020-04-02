package com.foxconn.iisd.rcadsvc.controller;

import com.foxconn.iisd.rcadsvc.Exception.UnauthorizedException;
import com.foxconn.iisd.rcadsvc.msg.RestReturnMsg;
import io.swagger.annotations.Api;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.ShiroException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;

@Api(description = "例外處理服務")
@RestControllerAdvice
public class ExceptionController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    // 捕捉Shiro的異常
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public RestReturnMsg handle401(ShiroException e) {
        logger.error("401 UnauthorizedException",e);
        Subject subject = SecurityUtils.getSubject();
        // 檢查用戶是否已驗證 , 是將回傳true
        logger.info("===> 檢查用戶是否已驗證 : " + subject.isAuthenticated());
        if(subject.isAuthenticated())
            return new RestReturnMsg(401, "No Permission", "");
        else
            return new RestReturnMsg(401, "UnauthorizedException", "");
    }

    // 捕捉UnauthorizedException
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnauthorizedException.class)
    public RestReturnMsg handle401() {
        logger.error("401 UnauthorizedException");
        return new RestReturnMsg(401, "UnauthorizedException", "");
    }

    // 捕捉所有其他異常 - BAD REQUEST
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public RestReturnMsg globalException(HttpServletRequest request, Throwable ex) {
        logger.error("400 The content of the request body is missing or incomplete, or contains malformed JSON.",ex);
        return new RestReturnMsg(400,
                "The content of the request body is missing or incomplete, or contains malformed JSON.", "");
    }

    private HttpStatus getStatus(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        if (statusCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
        return HttpStatus.valueOf(statusCode);
    }
}
