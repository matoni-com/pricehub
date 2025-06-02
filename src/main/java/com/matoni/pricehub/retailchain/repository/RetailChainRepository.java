package com.matoni.pricehub.retailchain.repository;

import com.matoni.pricehub.retailchain.entity.RetailChain;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RetailChainRepository extends JpaRepository<RetailChain, Long> {
  Optional<RetailChain> findByName(String name);
}
