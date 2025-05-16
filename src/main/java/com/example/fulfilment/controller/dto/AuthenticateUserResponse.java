package com.example.fulfilment.controller.dto;

import lombok.Value;

@Value
public class AuthenticateUserResponse {
  String access_token;
  String token_type; // almost always "Bearer"
  int expires_in; // in seconds
}
