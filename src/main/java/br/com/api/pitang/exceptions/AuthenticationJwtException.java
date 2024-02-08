package br.com.api.pitang.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

import io.jsonwebtoken.JwtException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@ResponseStatus(BAD_REQUEST)
public class AuthenticationJwtException extends JwtException {

    private static final long serialVersionUID = 1L;

    public AuthenticationJwtException(String message) {
        super(message);
    }
}
