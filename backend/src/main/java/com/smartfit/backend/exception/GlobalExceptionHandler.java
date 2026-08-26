package com.smartfit.backend.exception;

import com.smartfit.backend.common.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(
            BusinessException exception
    ) {

        Result<Void> result = Result.error(
                exception.getStatus().value(),
                exception.getMessage()
        );

        return ResponseEntity
                .status(exception.getStatus())
                .body(result);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleValidationException(
            MethodArgumentNotValidException exception
    ) {

        String message = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("参数校验失败");


        Result<Void> result =
                Result.error(400, message);


        return ResponseEntity
                .badRequest()
                .body(result);
    }
}