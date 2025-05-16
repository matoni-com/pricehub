package com.matoni.pricehub.controller;

import com.matoni.pricehub.controller.dto.AuthenticateUserRequest;
import com.matoni.pricehub.controller.dto.AuthenticateUserResponse;
import com.matoni.pricehub.service.UserAuthenticationService;
import com.matoni.pricehub.service.dto.AuthenticateUserCommand;
import com.matoni.pricehub.service.dto.AuthenticateUserResult;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class UserAuthenticationController {

  private AuthenticateUserMapper authenticateUserMapper;
  private UserAuthenticationService service;

  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticateUserResponse> authenticate(
      @RequestBody AuthenticateUserRequest request) {

    AuthenticateUserCommand command = authenticateUserMapper.toCommand(request);
    AuthenticateUserResult result = service.authenticate(command);
    AuthenticateUserResponse response = authenticateUserMapper.toResponse(result);

    return new ResponseEntity<>(response, HttpStatus.OK);
  }

  // TODO: improve error handling
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Object> handleAuthenticationException(AuthenticationException e) {
    return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
  }
}
