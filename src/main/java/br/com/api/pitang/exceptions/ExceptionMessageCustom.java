package br.com.api.pitang.exceptions;

import static br.com.api.pitang.constants.MessagesConstants.INVALID_FIELDS;
import static br.com.api.pitang.constants.MessagesConstants.MISSING_FIELDS;
import javax.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class ExceptionMessageCustom extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ExceptionResponse> rendersGenericsExceptions(Exception ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), INTERNAL_SERVER_ERROR.value());
        return new ResponseEntity<>(exceptionResponse, INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ExceptionResponse> entityNotFoundException(EntityNotFoundException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(
                ex.getMessage(), NOT_FOUND.value());
        return new ResponseEntity<>(exceptionResponse, NOT_FOUND);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<ExceptionResponse> badCredentialsException(BadCredentialsException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), BAD_REQUEST.value());
        return new ResponseEntity<>(exceptionResponse, BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        String messageError = argumentNotValidSimplifiedMessage(ex.getMessage());

        ExceptionResponse exceptionResponse = new ExceptionResponse(messageError, BAD_REQUEST.value());
        return new ResponseEntity<>(exceptionResponse, BAD_REQUEST);
    }

    @ExceptionHandler(AuthenticationJwtException.class)
    public final ResponseEntity<ExceptionResponse> authenticationJwtException(AuthenticationJwtException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), UNAUTHORIZED.value());
        return new ResponseEntity<>(exceptionResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public final ResponseEntity<ExceptionResponse> unauthorizedException(Exception ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), UNAUTHORIZED.value());
        return new ResponseEntity<>(exceptionResponse, UNAUTHORIZED);
    }

    @ExceptionHandler(ValidationException.class)
    public final ResponseEntity<ExceptionResponse> validationException(ValidationException ex) {
        ExceptionResponse exceptionResponse = new ExceptionResponse(ex.getMessage(), BAD_REQUEST.value());
        return new ResponseEntity<>(exceptionResponse, BAD_REQUEST);
    }

    private String argumentNotValidSimplifiedMessage(String exceptionMessage) {
       if (exceptionMessage.contains(INVALID_FIELDS))
           return INVALID_FIELDS;
        if (exceptionMessage.contains(MISSING_FIELDS))
            return MISSING_FIELDS;

        return BAD_REQUEST.getReasonPhrase();
    }
}
