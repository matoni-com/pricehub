package com.matoni.pricehub.security.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import com.matoni.pricehub.security.JwtAuthenticationToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationFilterTests {

  // used to mock when object needs to be autowired (injected)
  @Mock private AuthenticationManager authenticationManager;
  @InjectMocks private JwtAuthenticationFilter filter;

  @Test
  @DisplayName(
      "Filter pull token from header, delegates it to auth manager, populates security context and calls chain.doFilter")
  public void filterAuthenticatesRequestWithBearerToken() throws ServletException, IOException {
    /*used to mock when object does not need to be autowired, usually for complex function arguments*/
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getHeader("Authorization")).thenReturn("Bearer some_token");

    HttpServletResponse response = mock(HttpServletResponse.class);
    FilterChain chain = mock(FilterChain.class);

    when(authenticationManager.authenticate(new JwtAuthenticationToken("some_token")))
        .thenReturn(new JwtAuthenticationToken("some_user", "some_token", null));

    filter.doFilterInternal(request, response, chain);

    // delegates token verification to authenticationManager
    verify(authenticationManager, times(1)).authenticate(new JwtAuthenticationToken("some_token"));

    // Authentication set in SecurityContext
    assertEquals(
        new JwtAuthenticationToken("some_user", "some_token", null),
        SecurityContextHolder.getContext().getAuthentication());

    // calls chain.doFilter at the end
    verify(chain, times(1)).doFilter(request, response);
  }
}
