package br.com.api.pitang.configs.security.jwt;

import br.com.api.pitang.configs.security.UserDetailService;
import static br.com.api.pitang.constants.MessagesConstants.INVALID_TOKEN;
import static br.com.api.pitang.constants.MessagesConstants.UNAUTHORIZED;
import br.com.api.pitang.data.models.User;
import br.com.api.pitang.exceptions.AuthenticationJwtException;
import br.com.api.pitang.exceptions.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.claims;
import static io.jsonwebtoken.Jwts.parser;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import java.time.LocalDateTime;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Base64.getEncoder;
import static java.util.Collections.singletonList;
import java.util.Date;
import static java.util.Date.from;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenProvider {


    @Value("${security.jwt.token.secret-key:secret}")
    private String key = "secret";

    @Value("${security.jwt.token.expire-lenght:1440}")
    private long tokenExpire = 1440;

    @Autowired
    private UserDetailService service;

    @PostConstruct
    public void init() {
        key = getEncoder().encodeToString(key.getBytes());
    }

    public String createToken(User user) {
        Claims claims = claims().setSubject(user.getLogin());
        claims.put("roles", singletonList(user.getAuthority()));

        LocalDateTime timeNow = now();
        LocalDateTime timeExpire = timeNow.plusMinutes(tokenExpire);

        return builder().setClaims(claims)
                .setIssuedAt(from(timeNow.atZone(systemDefault()).toInstant()))
                .setExpiration(from(timeExpire.atZone(systemDefault()).toInstant()))
                .signWith(HS256, key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails user = service.loadUserByUsername(getLoginUser(token));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (isBlank(token))
            throw new UnauthorizedException(UNAUTHORIZED); //TODO VERIFICAR EXCEPTION HANDLER PARA ESSE CASO

        if(token.startsWith("Bearer "))
            return token.substring(7);

        return token;
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = parser().setSigningKey(key).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        }catch(Exception ex) {
            throw new AuthenticationJwtException(INVALID_TOKEN);
        }
    }

    private String getLoginUser(String token) {
        return parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }
}
