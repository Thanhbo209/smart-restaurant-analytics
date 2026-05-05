package com.thanhpham.smart_restaurant_analytics.auth.filter;

import com.thanhpham.smart_restaurant_analytics.auth.service.JwtService;
import com.thanhpham.smart_restaurant_analytics.auth.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);

        io.jsonwebtoken.Claims claims;
        try {
            claims = jwtService.extractClaims(token);
        } catch (Exception ex) {
            log.debug("Invalid JWT: {}", ex.getMessage());
            chain.doFilter(request, response);
            return;
        }

        if (!"ACCESS".equals(claims.get("type"))) {
            log.debug("Rejecting non-access JWT");
            chain.doFilter(request, response);
            return;
        }

        String username = claims.getSubject();

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            if (claims.getExpiration().before(new java.util.Date())) {
                log.debug("JWT expired");
                chain.doFilter(request, response);
                return;
            }

            UserDetails userDetails;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException ex) {
                log.debug("JWT subject no longer exists: {}", username);
                chain.doFilter(request, response);
                return;
            }

            if (!userDetails.isEnabled() || !userDetails.isAccountNonLocked()) {
                log.debug("User disabled or locked");
                chain.doFilter(request, response);
                return;
            }

            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }

        chain.doFilter(request, response);
    }
}