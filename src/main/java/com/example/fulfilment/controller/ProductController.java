package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.controller.dto.ProductResponse;
import com.example.fulfilment.entity.Product;
import com.example.fulfilment.service.ProductService;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        ProductCreateCommand productCreateCommand = ProductControllerMapper.toCommand(request);
        ProductResult savedProduct = productService.saveProduct(productCreateCommand);
        ProductResponse productResponse = ProductControllerMapper.fromProductResult(savedProduct);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResult> products = productService.getAllProducts();
        List<ProductResponse> productResponses = products.stream()
                .map(ProductControllerMapper::fromProductResult)
                .toList();
        return ResponseEntity.ok(productResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ProductControllerMapper::fromProductResult)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
