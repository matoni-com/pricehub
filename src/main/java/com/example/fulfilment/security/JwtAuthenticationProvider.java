package com.example.fulfilment.security;

import com.example.fulfilment.security.jwt.InvalidJwtException;
import com.example.fulfilment.security.jwt.JwtValidator;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

  private JwtValidator jwtValidator;

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String jwt = (String) authentication.getCredentials();

    try {
      return jwtValidator.validateToken(jwt);
    } catch (InvalidJwtException e) {
      throw new BadCredentialsException("Invalid JWT", e);
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return JwtAuthenticationToken.class.isAssignableFrom(authentication);
  }
}
