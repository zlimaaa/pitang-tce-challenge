package br.com.api.pitang.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CustomException(String message) {
        super(message);
    }
}
