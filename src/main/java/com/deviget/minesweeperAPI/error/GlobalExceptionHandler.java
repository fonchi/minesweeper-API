package com.deviget.minesweeperAPI.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Global Handler Exception Class to manage error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handle ValidationException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException e) {
        List<FieldErrorDto> fieldErrorDtos = new ArrayList<FieldErrorDto>();
        if (e.getErrors() != null) {
            List<FieldError> fieldErrors = e.getErrors().getFieldErrors();
            for (FieldError fieldError : fieldErrors) {
                FieldErrorDto fieldErrorDto = new FieldErrorDto(fieldError.getField(), fieldError.getCode(), fieldError.getDefaultMessage());
                fieldErrorDtos.add(fieldErrorDto);
            }
        }
        ErrorDto result = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Validation Error", e.getMessage(), fieldErrorDtos);
        logger.error("Validation Exception: " + e.getMessage(), e);
        return new ResponseEntity<ErrorDto>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle ServerException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> handleServerException(ServerException e) {
        ErrorDto result = new ErrorDto(e.getStatus().value(), "Service Error", e.getClass().getName(), e.getMessage());
        logger.error("Service Error: " + e.getMessage(), e);
        return new ResponseEntity<ErrorDto>(result, e.getStatus());
    }

    /**
     * Handle BadRequestException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        ErrorDto result = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Bad Request Error", e.getClass().getName(), e.getMessage());
        logger.error("Bad Request Error: " + e.getMessage(), e);
        return new ResponseEntity<ErrorDto>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle NotFoundException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        ErrorDto result = new ErrorDto(HttpStatus.BAD_REQUEST.value(), "Not Found Error", e.getClass().getName(), e.getMessage());
        logger.error("Not Found Error: " + e.getMessage(), e);
        return new ResponseEntity<ErrorDto>(result, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handle default exceptions
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception e) {
        ErrorDto result = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unknowkn Error", e.getClass().getName(), e.getMessage());
        logger.error("Unknown Error: " + e.getMessage(), e);
        return new ResponseEntity<ErrorDto>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
