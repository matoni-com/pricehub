package com.matoni.pricehub.service;

import com.matoni.pricehub.service.dto.AuthenticateUserCommand;
import com.matoni.pricehub.service.dto.AuthenticateUserResult;

public interface UserAuthenticationService {
  AuthenticateUserResult authenticate(AuthenticateUserCommand authenticateUserCommand);
}
