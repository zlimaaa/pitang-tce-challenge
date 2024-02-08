package br.com.api.pitang.configs.security.jwt;


import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.GenericFilterBean;

import static org.springframework.security.core.context.SecurityContextHolder.getContext;

public class JwtTokenFilter extends GenericFilterBean {

    @Autowired
    private JwtTokenProvider tokenProvider;

    public JwtTokenFilter(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String token = tokenProvider.getToken((HttpServletRequest) request);
        if (token != null && tokenProvider.isValidToken(token)) {
            Authentication auth = tokenProvider.getAuthentication(token);
            if (auth != null)
                getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }

}
