package com.matoni.pricehub.security.jwt;

public class InvalidJwtException extends Exception {
  public InvalidJwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
