package com.matoni.pricehub.security.jwt;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class JwtProvider {

  @Autowired private SecretKey jwtSignatureKey;

  public String createToken(Authentication auth, long expirationPeriodInMilliseconds) {
    var username = auth.getName();
    var authorities = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

    Date now = new Date();
    Date expirationTime = new Date(now.getTime() + expirationPeriodInMilliseconds);

    JwtBuilder builder =
        Jwts.builder()
            .subject(username)
            .issuedAt(now)
            .expiration(expirationTime)
            .claim("authorities", authorities)
            .signWith(jwtSignatureKey);

    return builder.compact();
  }
}
