package com.example.tiffin_center_management.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.tiffin_center_management.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	private final JwtUtil jwtUtil;
    private final CustomerUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

    	// 1. Agar request Auth endpoints ki hai, toh filter ko bypass karo
        String path = request.getServletPath();
        if (path.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }
    	
        String token = null;
        String email = null;

        // Extract token from cookie
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("jwt".equals(cookie.getName())) {
                    token = cookie.getValue();
                }
            }
        }

        // Validate token
        if (token != null) {
//            try {
//                email = jwtUtil.extractEmail(token);
//            } catch (Exception e) {
//                // Invalid token ignore
//            }
        	try {
                email = jwtUtil.extractEmail(token);
                System.out.println("Email from token: " + email); // debug
            } catch (Exception e) {
                System.out.println("JWT ERROR: " + e.getMessage());
            }
        }

        // Set authentication
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

        	
        	if (jwtUtil.validateToken(token, email)) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

//            if (jwtUtil.validateToken(token, email)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
	
}
