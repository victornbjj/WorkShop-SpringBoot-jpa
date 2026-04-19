package com.educandoweb.course.resources.exceptions;


import com.educandoweb.course.services.exceptions.DataBaseException;
import com.educandoweb.course.services.exceptions.ResourceNotFoundException;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardErro> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request){
      String error = "Resource not found";
      HttpStatus status = HttpStatus.NOT_FOUND;
      StandardErro err = new StandardErro(Instant.now(), status.value(), error, e.getMessage(),request.getRequestURI());

      return ResponseEntity.status(status).body(err);
    }
    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<StandardErro> dataBas(DataBaseException e, HttpServletRequest request){
        String error = "DataBase Error";
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardErro err = new StandardErro(Instant.now(), status.value(), error, e.getMessage(),request.getRequestURI());

        return ResponseEntity.status(status).body(err);

    }
}
