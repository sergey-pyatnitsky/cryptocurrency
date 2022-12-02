package com.cryptocurrency.exception.handler;

import com.cryptocurrency.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.sql.Date;

@ControllerAdvice
public class MainExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger("MainExceptionHandler logger");

    @ResponseBody
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler({NoSuchDataException.class, ExistingDataException.class,
            ActiveStatusDataException.class, OperationExecutionException.class,
            IncorrectDataException.class, FileStorageException.class})
    public ApiError handleCustomException(Exception e) {
        ApiError apiError = ApiError.ApiErrorBuilder.anApiError()
                .withTimestamp(new Date(System.currentTimeMillis()))
                .withStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .withError(e.getMessage()).build();
        logger.error(e.getMessage());
        return apiError;
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler({DisabledException.class, BadCredentialsException.class})
    public ApiError handleAuthException(Exception e) {
        ApiError apiError = ApiError.ApiErrorBuilder.anApiError()
                .withTimestamp(new Date(System.currentTimeMillis()))
                .withStatus(HttpStatus.UNAUTHORIZED.value())
                .withError(e.getMessage()).build();
        logger.error(e.getMessage());
        return apiError;
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e,
                                                                 HttpHeaders headers, HttpStatus status, WebRequest request) {
        ApiError apiError = ApiError.ApiErrorBuilder.anApiError()
                .withTimestamp(new Date(System.currentTimeMillis()))
                .withStatus(HttpStatus.OK.value())
                .withError(e.getBindingResult().getFieldError().getDefaultMessage()).build();
        logger.error(e.getBindingResult().getFieldError().getDefaultMessage());
        return new ResponseEntity<>(apiError, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
