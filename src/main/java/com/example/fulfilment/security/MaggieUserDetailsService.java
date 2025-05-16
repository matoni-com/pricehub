package com.example.fulfilment.security;

import com.example.fulfilment.entity.Authority;
import com.example.fulfilment.entity.User;
import com.example.fulfilment.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class MaggieUserDetailsService implements UserDetailsService {

  private UserRepository userRepository;

  @Transactional
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user =
        userRepository
            .findByUsername(username)
            .orElseThrow(
                () -> new UsernameNotFoundException("Username: " + username + " not found!"));

    UserDetails userDetails =
        org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(
                user.getAuthorities().stream().map(Authority::getAuthority).toArray(String[]::new))
            .build();

    return userDetails;
  }
}
