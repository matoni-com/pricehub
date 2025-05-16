package com.example.fulfilment.service;

import com.example.fulfilment.security.jwt.JwtProvider;
import com.example.fulfilment.service.dto.AuthenticateUserCommand;
import com.example.fulfilment.service.dto.AuthenticateUserResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UserAuthenticationServiceImpl implements UserAuthenticationService {

  private static final int EXPIRATION_PERIOD_IN_SECONDS = 60 * 10; // 10 minutes
  private static final String TOKEN_TYPE = "Bearer";

  @Autowired
  @Qualifier("maggieUsernamePassword")
  private AuthenticationManager maggieAuthManager;

  @Autowired private JwtProvider jwtProvider;

  public AuthenticateUserResult authenticate(AuthenticateUserCommand authenticateUserCommand) {
    Authentication auth =
        new UsernamePasswordAuthenticationToken(
            authenticateUserCommand.getUsername(), authenticateUserCommand.getPassword());

    Authentication authenticated = maggieAuthManager.authenticate(auth);

    String jwt = jwtProvider.createToken(authenticated, (long) EXPIRATION_PERIOD_IN_SECONDS * 1000);

    return new AuthenticateUserResult(jwt, TOKEN_TYPE, EXPIRATION_PERIOD_IN_SECONDS);
  }
}
