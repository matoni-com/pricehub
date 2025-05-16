package com.matoni.pricehub.service;

import com.matoni.pricehub.entity.Product;
import com.matoni.pricehub.repository.ProductRepository;
import com.matoni.pricehub.service.dto.ProductCreateCommand;
import com.matoni.pricehub.service.dto.ProductResult;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductServiceMapper productServiceMapper;

  public ProductService(
      ProductRepository productRepository, ProductServiceMapper productServiceMapper) {
    this.productRepository = productRepository;
    this.productServiceMapper = productServiceMapper;
  }

  public ProductResult saveProduct(ProductCreateCommand command) {
    Product product = productServiceMapper.toEntity(command);
    Product savedProduct = productRepository.save(product);
    return productServiceMapper.toResult(savedProduct);
  }

  public List<ProductResult> getAllProducts() {
    return productRepository.findAll().stream()
        .map(productServiceMapper::toResult)
        .collect(Collectors.toList());
  }

  public Optional<ProductResult> getProductById(Long id) {
    return productRepository.findById(id).map(productServiceMapper::toResult);
  }

  public void deactivateProduct(Long id) {
    Product product =
        productRepository
            .findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
    product.setIsActive(false);
    productRepository.save(product);
  }
}
