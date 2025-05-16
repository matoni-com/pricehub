package com.example.fulfilment.service.dto;

import lombok.Value;

@Value
public class AuthenticateUserResult {
  String access_token;
  String token_type; // almost always "Bearer"
  int expires_in; // in seconds
}
