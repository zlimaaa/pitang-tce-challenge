package br.com.api.pitang.services;


import br.com.api.pitang.configs.security.jwt.JwtTokenProvider;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_LOGIN;
import br.com.api.pitang.data.dtos.AuthDTO;
import br.com.api.pitang.data.models.User;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private UserService userService;


    public Map<Object, Object> signIn(AuthDTO authDTO) {

        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authDTO.getLogin(), authDTO.getPassword())
            );
            User user = userService.findByLogin(authDTO.getLogin());
            String token = tokenProvider.createToken(user);

            Map<Object, Object> response = new HashMap<>();
            response.put("name", user.getFirstName());
            response.put("token", token);

            userService.updateLastLogin(user.getId());

            return response;
        } catch (AuthenticationException ex) {
            throw new BadCredentialsException(INVALID_LOGIN);
        }

    }
}
