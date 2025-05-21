package org.example.greetingright.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/login") || path.equals("/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        // If the request is for login or refresh-token, skip JWT check
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Try to get the token from the Authorization header
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        String token = null;

        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            // Remove "Bearer " prefix from the header value
            token = header.substring(JwtProperties.TOKEN_PREFIX.length()).trim();
        } else if (request.getParameter("token") != null) {
            // Support getting token from query parameter (optional)
            token = request.getParameter("token").trim();
        }

        try {
            // Validate and authenticate the user
            if (token != null && !token.isEmpty()) {
                String username = jwtUtil.extractUsername(token);
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // Validate the token and set authentication context
                if (jwtUtil.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (UsernameNotFoundException | AuthenticationCredentialsNotFoundException ex) {
            // If user not found or token invalid - return 401 Unauthorized
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            return;
        } catch (Exception ex) {
            // If other error occurs - return 500 Internal Server Error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
