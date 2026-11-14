package com.startup.validator.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthenticationFilter is executed once per request.
 * It intercepts HTTP requests to check for the presence of a valid JWT token in the 'Authorization' header.
 * 
 * Explanation of Annotations and Lifecycle:
 * `@Component`: Declares this filter as a Spring bean.
 * `OncePerRequestFilter`: Ensures that the filter is executed exactly once per request dispatch.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    // Spring Autowires this constructor automatically because it is the only constructor.
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract the JWT token from the Request header
        String token = getTokenFromRequest(request);

        // 2. Validate token validity
        if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
            
            // 3. Extract the username from the token
            String username = jwtTokenProvider.getUsername(token);

            // 4. Load UserDetails from database
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 5. Build an Authentication token containing the user details and roles (authorities)
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
            );

            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // 6. Set the authentication object in the Spring Security context holder
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // 7. Proceed to the next filter in the security filter chain
        filterChain.doFilter(request, response);
    }

    /**
     * Extracts token from the HTTP Authorization header (expecting format: "Bearer <token>").
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
