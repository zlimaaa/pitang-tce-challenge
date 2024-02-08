package br.com.api.pitang.configs.security.jwt;

import static br.com.api.pitang.constants.MessagesConstants.INVALID_TOKEN;
import java.time.LocalDateTime;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import br.com.api.pitang.data.models.User;
import br.com.api.pitang.exceptions.AuthenticationJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import br.com.api.pitang.configs.security.UserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;

import static io.jsonwebtoken.Jwts.builder;
import static io.jsonwebtoken.Jwts.claims;
import static io.jsonwebtoken.Jwts.parser;
import static io.jsonwebtoken.SignatureAlgorithm.HS256;
import static java.time.LocalDateTime.now;
import static java.time.ZoneId.systemDefault;
import static java.util.Arrays.asList;
import static java.util.Base64.getEncoder;
import static java.util.Date.from;

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
        claims.put("roles", asList(user.getAuthority()));

        LocalDateTime timeNow = now();
        LocalDateTime timeExpire = timeNow.plusMinutes(this.tokenExpire);

        return builder().setClaims(claims)
                .setIssuedAt(from(timeNow.atZone(systemDefault()).toInstant()))
                .setExpiration(from(timeExpire.atZone(systemDefault()).toInstant()))
                .signWith(HS256, key)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        UserDetails user = this.service.loadUserByUsername(getLoginUser(token));
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    public String getToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if(token != null && token.startsWith("Bearer "))
            return token.substring(7, token.length());

        return null;
    }

    public boolean isValidToken(String token) {
        try {
            Jws<Claims> claims = parser().setSigningKey(key).parseClaimsJws(token);
            if(claims.getBody().getExpiration().before(new Date()))
                return false;

            return true;
        }catch(Exception ex) {
            throw new AuthenticationJwtException(INVALID_TOKEN);
        }
    }

    private String getLoginUser(String token) {
        return parser().setSigningKey(key).parseClaimsJws(token).getBody().getSubject();
    }
}
