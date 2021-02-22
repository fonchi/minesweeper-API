package com.deviget.minesweeperAPI.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global Handler Exception Class to manage error responses
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Handle ServerException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(InternalServerException.class)
    public ResponseEntity<?> handleServerException(InternalServerException e) {
        ErrorDto result = new ErrorDto(e.STATUS.value(), e.ERROR, e.getClass().getName(), e.getMessage());
        logger.error(e.getError(), e);
        return new ResponseEntity<>(result, e.STATUS);
    }

    /**
     * Handle BadRequestException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException e) {
        ErrorDto result = new ErrorDto(e.STATUS.value(), e.ERROR, e.getClass().getName(), e.getMessage());
        logger.error(e.getError(), e);
        return new ResponseEntity<>(result, e.STATUS);
    }

    /**
     * Handle NotFoundException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleNotFoundException(NotFoundException e) {
        ErrorDto result = new ErrorDto(e.STATUS.value(), e.ERROR, e.getClass().getName(), e.getMessage());
        logger.error(e.getError(), e);
        return new ResponseEntity<>(result, e.STATUS);
    }

    /**
     * Handle ResourceLockedException
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ResourceLockedException.class)
    public ResponseEntity<?> handleResourceLockedException(ResourceLockedException e) {
        ErrorDto result = new ErrorDto(e.STATUS.value(), e.ERROR, e.getClass().getName(), e.getMessage());
        logger.error(e.getError(), e);
        return new ResponseEntity<>(result, e.STATUS);
    }

    /**
     * Handle default exceptions
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAllException(Exception e) {
        ErrorDto result = new ErrorDto(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unknowkn error", e.getClass().getName(), e.getMessage());
        logger.error("Unknown error: " + e.getMessage(), e);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
