package com.dmitrykornilov.pets.rest.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.dmitrykornilov.pets.rest.controller.BindingErrorsResponse;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exception(Exception e) {
        ObjectMapper mapper = new ObjectMapper();
        ErrorInfo errorInfo = new ErrorInfo(e);
        String respJSONstring = "{}";
        try {
            respJSONstring = mapper.writeValueAsString(errorInfo);
        } catch (JsonProcessingException e1) {
            e1.printStackTrace();
        }
        return ResponseEntity.badRequest().body(respJSONstring);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(code = BAD_REQUEST)
    @ResponseBody
    public ResponseEntity<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request) {
        BindingErrorsResponse errors = new BindingErrorsResponse();
        BindingResult bindingResult = ex.getBindingResult();
        HttpHeaders headers = new HttpHeaders();
        if (bindingResult.hasErrors()) {
            errors.addAllErrors(bindingResult);
            headers.add("errors", errors.toJSON());
        }
        return new ResponseEntity<>(headers, HttpStatus.BAD_REQUEST);
    }

    private class ErrorInfo {
        public final String className;
        public final String exMessage;

        public ErrorInfo(Exception ex) {
            this.className = ex.getClass().getName();
            this.exMessage = ex.getLocalizedMessage();
        }
    }
}
