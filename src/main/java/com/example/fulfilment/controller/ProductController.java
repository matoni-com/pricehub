package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.controller.dto.ProductResponse;
import com.example.fulfilment.service.ProductService;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

  private final ProductService productService;
  private final ProductControllerMapper productControllerMapper;

  public ProductController(
      ProductService productService, ProductControllerMapper productControllerMapper) {
    this.productService = productService;
    this.productControllerMapper = productControllerMapper;
  }

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(
      @Valid @RequestBody ProductCreateRequest request) {
    ProductCreateCommand productCreateCommand = productControllerMapper.toCommand(request);
    ProductResult savedProduct = productService.saveProduct(productCreateCommand);
    ProductResponse productResponse = productControllerMapper.fromProductResult(savedProduct);
    return ResponseEntity.ok(productResponse);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    List<ProductResult> products = productService.getAllProducts();
    List<ProductResponse> productResponses =
        products.stream().map(productControllerMapper::fromProductResult).toList();
    return ResponseEntity.ok(productResponses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
    return productService
        .getProductById(id)
        .map(productControllerMapper::fromProductResult)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PatchMapping("/{id}/deactivate")
  public ResponseEntity<Void> deactivateProduct(@PathVariable Long id) {
    productService.deactivateProduct(id);
    return ResponseEntity.noContent().build();
  }
}
