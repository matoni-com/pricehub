package com.example.fulfilment.service.dto;

import lombok.Value;

@Value
public class AuthenticateUserCommand {
  String username;
  String password;
}
