package com.example.fulfilment.service;

import com.example.fulfilment.service.dto.AuthenticateUserCommand;
import com.example.fulfilment.service.dto.AuthenticateUserResult;

public interface UserAuthenticationService {
    AuthenticateUserResult authenticate(AuthenticateUserCommand authenticateUserCommand);
}
