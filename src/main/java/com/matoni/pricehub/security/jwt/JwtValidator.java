package com.matoni.pricehub.security.jwt;

import com.matoni.pricehub.security.JwtAuthenticationToken;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

  private JwtParser parser;

  public JwtValidator(SecretKey jwtSignatureKey) {
    parser = Jwts.parser().verifyWith(jwtSignatureKey).build();
  }

  public JwtAuthenticationToken validateToken(String jwt) throws InvalidJwtException {
    Claims claims;

    try {
      claims = parser.parseSignedClaims(jwt).getPayload();
    } catch (JwtException | IllegalArgumentException e) {
      throw new InvalidJwtException("Invalid JWT", e);
    }

    String username = claims.getSubject();

    List<String> authorities = (List<String>) claims.get("authorities", List.class);
    var grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).toList();

    return new JwtAuthenticationToken(username, jwt, grantedAuthorities);
  }
}
