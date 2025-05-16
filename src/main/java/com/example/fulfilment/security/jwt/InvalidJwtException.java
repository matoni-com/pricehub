package com.example.fulfilment.security.jwt;

public class InvalidJwtException extends Exception {
  public InvalidJwtException(String message, Throwable cause) {
    super(message, cause);
  }
}
