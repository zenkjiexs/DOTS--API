package com.example.DOTSAPI.services;


import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.DOTSAPI.exception.CustomAuthenticationException;
import com.example.DOTSAPI.model.User;
import com.example.DOTSAPI.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

import static com.example.DOTSAPI.configuration.SecurityConstants.SECRET;
import static com.example.DOTSAPI.configuration.SecurityConstants.TOKEN_PREFIX;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class AuthServices {
    private final UserRepo userRepo;
    public User loadUserFromHttpRequest(HttpServletRequest request) throws CustomAuthenticationException {

           try {
               String tokenHeader = request.getHeader(AUTHORIZATION);
               if(tokenHeader == null || !tokenHeader.startsWith(TOKEN_PREFIX)) {
                   throw new CustomAuthenticationException("Token is missing");
               }
               String token = tokenHeader.replace(TOKEN_PREFIX, "");
               Algorithm algorithm = Algorithm.HMAC256(SECRET.getBytes());
               JWTVerifier verifier = JWT.require(algorithm).build();
               DecodedJWT decodedJWT = verifier.verify(token);
               String userName =  decodedJWT.getSubject();
               return userRepo.getByUserName(userName);
           } catch (CustomAuthenticationException ex) {
               throw new CustomAuthenticationException(ex.getMessage());
           } catch (Exception ex) {
               throw new CustomAuthenticationException("Token is invalid or expired ");
           }
    }

}
