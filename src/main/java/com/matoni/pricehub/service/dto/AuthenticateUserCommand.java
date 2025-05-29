package com.matoni.pricehub.service.dto;

import lombok.Value;

@Value
public class AuthenticateUserCommand {
  String username;
  String password;
}
