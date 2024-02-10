package br.com.api.pitang.exceptions;


import br.com.api.pitang.controllers.UserController;
import br.com.api.pitang.data.dtos.UserDTO;
import br.com.api.pitang.services.UserService;
import java.lang.reflect.Method;
import static java.util.Objects.requireNonNull;
import javax.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.MockitoAnnotations.openMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;

@ActiveProfiles("test")
@DisplayName("Testes unitarios dos exceptions handlers")
@ExtendWith(MockitoExtension.class)
public class ExceptionMessageCustomTest {

    @InjectMocks
    private ExceptionMessageCustom exceptionMessage;

    @Mock
    private UserService userService;

    @BeforeEach
    public void setUp() {
        openMocks(this);
    }

    @Test
    public void rendersGenericsExceptions() {
        Exception ex = new Exception("Test exception message");

        ResponseEntity<ExceptionResponse> responseEntity = exceptionMessage.rendersGenericsExceptions(ex);

        assertEquals(INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Test exception message", requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(500, requireNonNull(responseEntity.getBody()).getErrorCode());
    }

    @Test
    public void entityNotFoundException() {
        EntityNotFoundException ex = new EntityNotFoundException("Test exception message");

        ResponseEntity<ExceptionResponse> responseEntity = exceptionMessage.entityNotFoundException(ex);

        assertEquals(NOT_FOUND, responseEntity.getStatusCode());
        assertEquals("Test exception message", requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(404, requireNonNull(responseEntity.getBody()).getErrorCode());
    }

    @Test
    public void badCredentialsException() {
        BadCredentialsException ex = new BadCredentialsException("Test exception message");

        ResponseEntity<ExceptionResponse> responseEntity = exceptionMessage.badCredentialsException(ex);

        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Test exception message", requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(400, requireNonNull(responseEntity.getBody()).getErrorCode());
    }

    @Test
    public void handleMethodArgumentNotValid() throws NoSuchMethodException {
        UserController userController = new UserController();
        Method method = UserController.class.getMethod("create", UserDTO.class);
        HandlerMethod handlerMethod = new HandlerMethod(userController, method);

        MethodParameter methodParameter = new MethodParameter(handlerMethod.getMethod(), 0);

        BindingResult bindingResult = mock(BindingResult.class);

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(methodParameter, bindingResult);

        MockHttpServletRequest servletRequest = new MockHttpServletRequest();
        ServletWebRequest servletWebRequest = new ServletWebRequest(servletRequest);

        ResponseEntity<Object> responseEntity = exceptionMessage.handleMethodArgumentNotValid(ex, null, null, servletWebRequest);

        assertNotNull(responseEntity.getBody());

        ExceptionResponse response = (ExceptionResponse) responseEntity.getBody();

        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals(BAD_REQUEST.getReasonPhrase(), response.getMessage());
        assertEquals(400, response.getErrorCode());

    }

    @Test
    public void authenticationJwtException() {
        AuthenticationJwtException ex = new AuthenticationJwtException("Test exception message");

        ResponseEntity<ExceptionResponse> responseEntity = exceptionMessage.authenticationJwtException(ex);

        assertEquals(UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Test exception message", requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(401, requireNonNull(responseEntity.getBody()).getErrorCode());
    }

    @Test
    public void unauthorizedException() {
        UnauthorizedException ex = new UnauthorizedException("Test exception message");

        ResponseEntity<ExceptionResponse> responseEntity = exceptionMessage.unauthorizedException(ex);

        assertEquals(UNAUTHORIZED, responseEntity.getStatusCode());
        assertEquals("Test exception message", requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(401, requireNonNull(responseEntity.getBody()).getErrorCode());
    }

    @Test
    public void validationException() {
        ValidationException ex = new ValidationException("Test exception message");

        ResponseEntity<ExceptionResponse> responseEntity = exceptionMessage.validationException(ex);

        assertEquals(BAD_REQUEST, responseEntity.getStatusCode());
        assertEquals("Test exception message", requireNonNull(responseEntity.getBody()).getMessage());
        assertEquals(400, requireNonNull(responseEntity.getBody()).getErrorCode());
    }

}
