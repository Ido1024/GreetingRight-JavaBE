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

    final private JwtUtil jwtUtil;
    final private CustomUserDetailsService customUserDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/login") || path.equals("/refresh-token");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if (shouldNotFilter(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // retrieve the Authorization header from the request
        String header = request.getHeader(JwtProperties.HEADER_STRING);
        String token;


        // extract the token from the hea`der if it's present
        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX)) {
            token = header.substring(JwtProperties.TOKEN_PREFIX.length());
        } else {
            // alternatively, try to get the token from a query parameter
            token = request.getParameter("token");
        }

        try {
            if (token != null) {
                // extract the username from the token
                String username = jwtUtil.extractUsername(token);
                // load user details using the extracted username
                UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

                // validate the token with the loaded user details
                if (jwtUtil.validateToken(token, userDetails)) {
                    // create an authentication object and set it in the Security Context
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (UsernameNotFoundException | AuthenticationCredentialsNotFoundException ex) {
            // Return 401 Unauthorized for invalid credentials
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(ex.getMessage());
            return;
        } catch (Exception ex) {
            // For other exceptions, return 500 Internal Server Error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred while processing the token");
            return;
        }

        // pass the request along the filter chain
        filterChain.doFilter(request, response);
    }
}