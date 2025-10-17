package com.chuwa.redbook.exception;

import java.util.Date;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.chuwa.redbook.payload.ErrorDetails;

@ControllerAdvice
public class GlobalExceptionHandler {

        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorDetails> handleResourceNotFound(ResourceNotFoundException ex, WebRequest request) {
                HttpStatus status = HttpStatus.NOT_FOUND;
                ErrorDetails errorDetails = new ErrorDetails(
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false),
                                status.value()
                );
                logger.warn("Resource not found for request {}: {}", request.getDescription(false), ex.getMessage());
                return new ResponseEntity<>(errorDetails, status);
        }

        @ExceptionHandler(BlogAPIException.class)
        public ResponseEntity<ErrorDetails> handleBlogAPI(BlogAPIException ex, WebRequest request) {
                HttpStatus status = ex.getHttpStatus();
                ErrorDetails errorDetails = new ErrorDetails(
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false),
                                status.value()
                );

                if (status.is4xxClientError()) {
                        logger.warn("BlogAPI client error for request {}: {} (status={})",
                                        request.getDescription(false), ex.getMessage(), status.value());
                } else {
                        logger.error("BlogAPI server error for request {}: {} (status={})",
                                        request.getDescription(false), ex.getMessage(), status.value(), ex);
                }

                return new ResponseEntity<>(errorDetails, status);
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ErrorDetails> handleGeneric(Exception ex, WebRequest request) {
                HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
                ErrorDetails errorDetails = new ErrorDetails(
                                new Date(),
                                ex.getMessage(),
                                request.getDescription(false),
                                status.value()
                );

                logger.error("Unhandled exception for request {}: {}", request.getDescription(false), ex.getMessage(), ex);

                return new ResponseEntity<>(errorDetails, status);
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ErrorDetails> handleValidationExceptions(
                        MethodArgumentNotValidException ex, WebRequest request) {

                // collect all field error messages
                String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                                .collect(Collectors.joining("; "));

                ErrorDetails errorDetails = new ErrorDetails(
                                new Date(),
                                errorMessage,
                                request.getDescription(false),
                                HttpStatus.BAD_REQUEST.value()
                );

                logger.warn("Validation failed for request {}: {}", request.getDescription(false), errorMessage);

                return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
        }

}
