package com.floney.floney.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.floney.floney.common.exception.common.ErrorType;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtExceptionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException exception) {
            setErrorResponse(response, ErrorType.EXPIRED_JWT_TOKEN);
        } catch (JwtException exception) {
            setErrorResponse(response, ErrorType.INVALID_AUTHENTICATION);
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorType errorType) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getOutputStream().write(new ObjectMapper().writeValueAsBytes(errorType));
    }
}
