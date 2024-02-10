package br.com.api.pitang.services;


import br.com.api.pitang.configs.security.jwt.JwtTokenProvider;
import static br.com.api.pitang.constants.MessagesConstants.USER_NOT_FOUND;
import br.com.api.pitang.data.dtos.AuthDTO;
import static br.com.api.pitang.data.dtos.AuthDTO.builder;
import br.com.api.pitang.data.models.User;
import static br.com.api.pitang.factory.UserFactory.buildUsers;
import java.util.Map;
import static java.util.Optional.of;
import javax.persistence.EntityNotFoundException;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import org.junit.jupiter.api.TestMethodOrder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@TestInstance(PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("Testes unitarios da autenticacao")
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class AuthServiceTest {

    @Autowired
    private AuthService service;

    @MockBean
    private AuthenticationManager authManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @MockBean
    private UserService userService;

    @Test
    @Order(1)
    @DisplayName("Autenticacao com sucesso")
    public void successSignIn() {

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(userService.findByLogin("ricardo")).thenReturn(of(buildUsers().get(0)).get());
        when(tokenProvider.createToken(any(User.class))).thenReturn("FAKETOKENeyJhbGciOiJIUzI1NiJ9");
        doNothing().when(userService).updateLastLogin(1L);

        AuthDTO authDTO = builder()
                .login("ricardo")
                .password("00669988")
                .build();

        Map<Object, Object> response = service.signIn(authDTO);

        assertNotNull(response);
        assertEquals("Ricardo", response.get("name"));
        assertEquals("FAKETOKENeyJhbGciOiJIUzI1NiJ9", response.get("token"));
    }

    @Test
    @Order(2)
    @DisplayName("Autenticacao com falha")
    public void failedSignIn() {

        try{
            when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
            when(userService.findByLogin("ricardo")).thenThrow(new EntityNotFoundException(USER_NOT_FOUND));

            AuthDTO authDTO = builder()
                    .login("ricardo")
                    .password("00669988")
                    .build();

            service.signIn(authDTO);

        }catch (Exception ex) {
            assertEquals(EntityNotFoundException.class, ex.getClass());
            assertEquals(USER_NOT_FOUND, ex.getMessage());
        }
    }

}
