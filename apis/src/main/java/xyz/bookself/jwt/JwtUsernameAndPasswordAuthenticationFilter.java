package xyz.bookself.jwt;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;

import javax.management.RuntimeErrorException;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

public class JwtUsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;

    public JwtUsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager){
        this.authenticationManager = authenticationManager;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        try {
            var authenticationRequest = new ObjectMapper().readValue(request.getInputStream(), UsernameAndPasswordAuthenticationRequest.class);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                authenticationRequest.getUsername(),
                authenticationRequest.getPassword()    
            );

            var authenticate = authenticationManager.authenticate(authentication);

            return authenticate;

        } catch (IOException e) {
            throw new RuntimeException(e);
        } 
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult){

        String key = "10fnFyS4HRYQYWi4wapk2cHJLx5gaGil0gPbeOLOCEJbhtDSj6E5PpZF9kUoW5nBtrRv3x4POvrO0zxC6TaxLGqZCzVE1ty4d7R5xHGZMzFoeO1m5ERUFiqesWhwDb4xDkpbOjJxadOebOrW22UWvRSEJdbn0qgmBSWmEQAw19qjrqMueWgY5I9M1Kf08yUAOCrhVXhM9yR0eBZjbfzBpQNfiNlCAYQucoHj0X4PM1Ca09KnfK3PikZXs1W1bKqX";

        String token = Jwts.builder()
        .setSubject(authResult.getName())
        .claim("authorities", authResult.getAuthorities())
        .setIssuedAt(new Date())
        .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(1)))
        .signWith(Keys.hmacShaKeyFor(key.getBytes()))
        .compact();

        response.addHeader("Authorization", "Bearer " + token);
    }
}
