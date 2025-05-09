package com.example.fulfilment.service;

import com.example.fulfilment.entity.Product;
import com.example.fulfilment.repository.ProductRepository;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResult saveProduct(ProductCreateCommand command) {
        Product product = ProductServiceMapper.toEntity(command);
        Product savedProduct = productRepository.save(product);
        return ProductServiceMapper.toResult(savedProduct);
    }

    public List<ProductResult> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductServiceMapper::toResult)
                .collect(Collectors.toList());
    }

    public Optional<ProductResult> getProductById(Long id) {
        return productRepository.findById(id).map(ProductServiceMapper::toResult);
    }
}
