package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.AuthenticateUserRequest;
import com.example.fulfilment.controller.dto.AuthenticateUserResponse;
import com.example.fulfilment.service.UserAuthenticationService;
import com.example.fulfilment.service.dto.AuthenticateUserCommand;
import com.example.fulfilment.service.dto.AuthenticateUserResult;
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
    public ResponseEntity<AuthenticateUserResponse> authenticate(@RequestBody AuthenticateUserRequest request) {

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

