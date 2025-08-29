package com.example.demo.rest.common;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.SPACE;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice("com.example.demo.rest")
public class ControllerAdvice {
    @Value("${logging.local:true}")
    Boolean logLocal;

    public ResponseEntity<?> build(Errors errors, Throwable thr, String msg) {
        return build(errors, thr, msg, logLocal);
    }

    public static ResponseEntity<?> build(Errors errors, Throwable thr, String msg, boolean loggable) {
        if (loggable) thr.printStackTrace();
        Throwable cause = Optional.ofNullable(thr.getCause()).orElse(thr);
        return new ResponseEntity<>(Api.negativeResponse(
                errors.name().substring(1), msg,
                ExceptionUtils.getStackTrace(cause)), null, errors.getCode());
    }

    public ResponseEntity<?> build(Errors errors, Throwable thr) {
        return build(errors, thr, errors.getDescription());
    }

    @ExceptionHandler(Throwable.class)
    public Object handleException(Throwable t) {
        String[] root = ExceptionUtils.getRootCauseStackTrace(t);
        String collect = Arrays.stream(root).limit(10).collect(Collectors.joining(". "));
        log.error("Handle exception: trace " + StringUtils.remove(collect, '\n'));
        return build(Errors.E500, t, Errors.E500.getDescription() + t.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Object handleException(MethodArgumentTypeMismatchException t) {
        return build(Errors.E700, t, Errors.E700.getDescription() + t.getMessage());
    }

    @ExceptionHandler(Errors.CodifiedException.class)
    public Object handleException(Errors.CodifiedException t) {
        return build(t.getError(), t, t.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleException(MethodArgumentNotValidException t) {
        FieldError error = t.getBindingResult().getFieldError();
        String msg = error.getDefaultMessage();
        return build(Errors.E101, t, "Поле " +
                Objects.requireNonNull(error).getField() + SPACE + msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Object handleException(ConstraintViolationException t) {
        String params = SetUtils.emptyIfNull(t.getConstraintViolations()).stream()
                .map(ConstraintViolation::getPropertyPath)
                .map(Path::toString)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(", "));
        return build(Errors.E101, t, String.format(Errors.E101.getDescription(), params));
    }
/*
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public Object handleException(org.hibernate.exception.ConstraintViolationException t) {
        return build(Errors.E101, t, String.format(Errors.E101.getDescription(), t.getConstraintName()));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Object handleException(MissingServletRequestParameterException t) {
        return build(Errors.E101, t, String.format(Errors.E101.getDescription(), t.getParameterName()));
    }

    @ExceptionHandler(PersistenceException.class)
    public Object handleException(PersistenceException t) {
        if (t.getCause() instanceof org.hibernate.exception.ConstraintViolationException e) {
            return handleException(e);
        }
        return build(Errors.E500, t, Errors.E500.getDescription() + t.getMessage());
    }*/
}
