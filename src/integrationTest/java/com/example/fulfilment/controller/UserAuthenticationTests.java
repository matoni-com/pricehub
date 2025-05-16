package com.example.fulfilment.controller;

import static net.javacrumbs.jsonunit.spring.JsonUnitResultMatchers.json;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.entity.Authority;
import com.example.fulfilment.entity.User;
import com.example.fulfilment.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

public class UserAuthenticationTests extends BaseIntegrationSuite {

  @Autowired private MockMvc mockMvc;
  @Autowired private BCryptPasswordEncoder passwordEncoder;
  @Autowired private UserRepository userRepository;

  @BeforeAll
  public void populateUser() {
    User user = new User("johndoe", passwordEncoder.encode("12345"));
    user.addAuthority(new Authority("some_authority"));

    userRepository.save(user);
  }

  @AfterAll
  public void cleanUser() {
    userRepository.deleteAll();
  }

  @Test
  public void authenticateWithValidCreds() throws Exception {
    String expectedResponse =
        """
            {
                "access_token": "${json-unit.any-string}",
                "token_type": "Bearer",
                "expires_in": 600
            }
            """;

    mockMvc
        .perform(
            post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "username": "johndoe",
                    "password": "12345"
                }
                """))
        .andExpect(status().isOk())
        .andExpect(json().isEqualTo(expectedResponse));
  }

  @Test
  public void authenticateWithInvalidPassword() throws Exception {
    mockMvc
        .perform(
            post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "username": "johndoe",
                    "password": "invalid"
                }
                """))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void authenticateWithNonExistingUser() throws Exception {
    mockMvc
        .perform(
            post("/authenticate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    """
                {
                    "username": "notexist",
                    "password": "12345"
                }
                """))
        .andExpect(status().isUnauthorized());
  }
}
