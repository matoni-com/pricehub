package com.example.fulfilment.repository;

import com.example.fulfilment.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
  Optional<Product> findByMerchantSku(String merchantSku);
}
