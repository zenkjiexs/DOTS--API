package com.example.DOTSAPI.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import com.auth0.jwt.interfaces.DecodedJWT;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import static com.example.DOTSAPI.exception.ExceptionUtils.raiseException;
import static com.example.DOTSAPI.configuration.SecurityConstants.SECRET;
import static com.example.DOTSAPI.configuration.SecurityConstants.TOKEN_PREFIX;
import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        try {
            String tokenHeader = request.getHeader(AUTHORIZATION);
            if(tokenHeader == null || !tokenHeader.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch(Exception e) {
            List<String> details = List.of("Invalid token provided");
                raiseException(request, response, "Unauthorized", details, HttpStatus.UNAUTHORIZED);
        }
    }
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)   {
        String token = request.getHeader(AUTHORIZATION).replace(TOKEN_PREFIX,"");
        Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
        DecodedJWT decodedJWT = decodeJWT(token, algorithm);
        String username = decodedJWT.getSubject();
        String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        stream(roles).forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));
        return new UsernamePasswordAuthenticationToken(username, null, authorities);
    }
    public static DecodedJWT decodeJWT(String token,Algorithm algorithm) {
            JWTVerifier verifier = JWT.require(algorithm).build();
            return verifier.verify(token);
    }
}
