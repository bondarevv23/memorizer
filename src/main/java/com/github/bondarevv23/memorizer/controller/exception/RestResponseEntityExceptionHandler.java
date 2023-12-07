package com.github.bondarevv23.memorizer.controller.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;


@ControllerAdvice(annotations = RestController.class)
public class RestResponseEntityExceptionHandler{

    @ExceptionHandler(value = { MethodArgumentNotValidException.class, DataAccessException.class })
    public ResponseEntity<?> handleVadRequest() {
        return ResponseEntity.badRequest().build();
    }
}
