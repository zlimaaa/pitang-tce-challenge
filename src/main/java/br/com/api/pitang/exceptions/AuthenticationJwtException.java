package br.com.api.pitang.exceptions;

import io.jsonwebtoken.JwtException;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(UNAUTHORIZED)
public class AuthenticationJwtException extends JwtException {

    private static final long serialVersionUID = 1L;

    public AuthenticationJwtException(String message) {
        super(message);
    }
}
