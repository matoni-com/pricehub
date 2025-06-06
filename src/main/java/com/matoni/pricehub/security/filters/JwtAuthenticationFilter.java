package com.matoni.pricehub.security.filters;

import com.matoni.pricehub.security.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  @Qualifier("maggieJwt")
  private AuthenticationManager authManager;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String header = request.getHeader("Authorization");

    if (header == null || !header.startsWith("Bearer ")) {
      /* This means the http request is just passed on to the built-in AuthorizationFilter.
      We need to have a call to filterChain.doFilter and not just throw an exception, otherwise
      an http request without a token to endpoints marked as .permitAll() would be denied because
      an exception would be thrown and the request would never reach the built-in AuthorizationFilter.*/
      filterChain.doFilter(request, response);
      return;
    }

    String token = header.replace("Bearer ", "");

    JwtAuthenticationToken auth = new JwtAuthenticationToken(token);

    Authentication authenticated = authManager.authenticate(auth);

    SecurityContextHolder.getContext().setAuthentication(authenticated);

    filterChain.doFilter(request, response);
  }
}
