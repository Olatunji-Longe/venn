package com.olatunji.venn.filters;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class RequestTimingFilter extends OncePerRequestFilter {

    public static final String START_TIME_KEY = "START_TIME";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // Set the start time as an attribute on the request object
        request.setAttribute(START_TIME_KEY, System.currentTimeMillis());
        filterChain.doFilter(request, response);
    }
}
