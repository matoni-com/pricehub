package com.matoni.pricehub.controller.dto;

import lombok.Data;

@Data
public class AuthenticateUserRequest {
  private String username;
  private String password;
}
