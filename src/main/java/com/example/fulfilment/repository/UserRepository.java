package com.example.fulfilment.repository;

import com.example.fulfilment.entity.User;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findByUsername(String username);
}
