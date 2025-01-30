package com.example.order_service.services;

import com.example.order_service.config.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TokenDataServiceImpl {

    @Autowired
    private JwtUtils jwtUtils;

    private String extraerToken(HttpServletRequest request){
        return request.getHeader("Authorization").substring(7);
    }

    // Retrieves and returns email from the token
    public String getEmail(HttpServletRequest request){
        String token = extraerToken(request);
        String email = jwtUtils.extractUsername(token);
        return email;
    }

    // Retrieves and returns id from the token
    public Long getId(HttpServletRequest request){
        String token = extraerToken(request);
        Long id = jwtUtils.extractUserId(token);
        return id;
    }

    // Retrieves and returns role from the token
    public String getRole(HttpServletRequest request){
        String token = extraerToken(request);
        String role = jwtUtils.extractUserRole(token);
        return role;
    }
}
