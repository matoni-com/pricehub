package com.example.fulfilment.service;

import com.example.fulfilment.entity.Product;
import com.example.fulfilment.repository.ProductRepository;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductServiceMapper productServiceMapper;

    public ProductService(ProductRepository productRepository, ProductServiceMapper productServiceMapper) {
        this.productRepository = productRepository;
        this.productServiceMapper = productServiceMapper;
    }

    public ProductResult saveProduct(ProductCreateCommand command) {
        Product product = productServiceMapper.toEntity(command);
        Product savedProduct = productRepository.save(product);
        return productServiceMapper.toResult(savedProduct);
    }

    public List<ProductResult> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productServiceMapper::toResult)
                .collect(Collectors.toList());
    }

    public Optional<ProductResult> getProductById(Long id) {
        return productRepository.findById(id).map(productServiceMapper::toResult);
    }

    public void deactivateProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + id));
        product.setIsActive(false);
        productRepository.save(product);
    }
}
