package br.com.api.pitang.exceptions;

import java.io.Serializable;

import lombok.Getter;

@Getter
public class ExceptionResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String message;
    private final Integer errorCode;

    public ExceptionResponse(String message, Integer errorCode) {
        super();
        this.message = message;
        this.errorCode = errorCode;
    }

}
