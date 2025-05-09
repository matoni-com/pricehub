package com.example.fulfilment.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private String username;
    private String jwt;

    public JwtAuthenticationToken(String jwt) {
        super(null);
        this.jwt = jwt;
        this.username = null;
        setAuthenticated(false);
    }

    public JwtAuthenticationToken(String username, String jwt, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.jwt = jwt;
        this.username = username;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
