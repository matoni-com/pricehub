package com.example.fulfilment.controller;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.entity.Product;
import com.example.fulfilment.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;

class ProductControllerTests extends BaseIntegrationSuite {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void cleanDatabase() {
        productRepository.deleteAll();
    }

    @Test
    @WithMockUser
    void createProduct_shouldPersistProductToDatabase() throws Exception {
        ProductCreateRequest product = new ProductCreateRequest();
        product.setMerchantCodeptId("merchant1");
        product.setWarehouseCodeptId("warehouse1");
        product.setMerchantSku("sku123");
        product.setManufacturerSku("mSku123");
        product.setManufacturerName("Test Manufacturer");
        product.setEan("1234567890123");
        product.setItemName("Test Item");
        product.setIsActive(true);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk());

        assertThat(productRepository.findByMerchantSku("sku123"))
                .isPresent()
                .get()
                .satisfies(savedProduct -> {
                    assertThat(savedProduct.getMerchantCodeptId()).isEqualTo("merchant1");
                    assertThat(savedProduct.getWarehouseCodeptId()).isEqualTo("warehouse1");
                });
    }

    @Test
    @WithMockUser(authorities = {"READ_PRODUCT"})
    void getAllProducts_shouldReturnSavedProducts() throws Exception {
        // given
        Product product = new Product();
        product.setMerchantCodeptId("merchant-xyz");
        product.setWarehouseCodeptId("warehouse-abc");
        product.setMerchantSku("sku-001");
        product.setItemName("Test Product");
        product.setIsActive(true);

        productRepository.save(product);

        // when + then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].merchantCodeptId").value("merchant-xyz"))
                .andExpect(jsonPath("$[0].merchantSku").value("sku-001"))
                .andExpect(jsonPath("$[0].itemName").value("Test Product"));
    }

    @Test
    @WithMockUser(authorities = {"READ_PRODUCT"})
    void getProductById_shouldReturnProductResponse() throws Exception {
        // given
        Product product = new Product();
        product.setMerchantCodeptId("merchant-123");
        product.setWarehouseCodeptId("warehouse-456");
        product.setMerchantSku("sku-789");
        product.setItemName("Sample Product");
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);

        // when + then
        mockMvc.perform(get("/products/" + savedProduct.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.merchantCodeptId").value("merchant-123"))
                .andExpect(jsonPath("$.merchantSku").value("sku-789"))
                .andExpect(jsonPath("$.itemName").value("Sample Product"));
    }

    @Test
    @WithMockUser(authorities = {"READ_PRODUCT"})
    void getAllProducts_shouldReturnEmptyListWhenNoProductsExist() throws Exception {
        // when + then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @WithMockUser
    void createProduct_shouldReturnBadRequestWhenMandatoryFieldsAreMissing() throws Exception {
        ProductCreateRequest product = new ProductCreateRequest();
        // Leaving all fields empty to trigger validation errors

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.merchantCodeptId").value("Merchant Codept ID is required"))
                .andExpect(jsonPath("$.warehouseCodeptId").value("Warehouse Codept ID is required"))
                .andExpect(jsonPath("$.merchantSku").value("Merchant SKU is required"))
                .andExpect(jsonPath("$.manufacturerSku").value("Manufacturer SKU is required"))
                .andExpect(jsonPath("$.manufacturerName").value("Manufacturer Name is required"))
                .andExpect(jsonPath("$.ean").value("EAN is required"))
                .andExpect(jsonPath("$.itemName").value("Item Name is required"));
    }

    @Test
    @WithMockUser
    void createProduct_shouldReturnBadRequestWhenEanIsInvalid() throws Exception {
        ProductCreateRequest product = new ProductCreateRequest();
        product.setMerchantCodeptId("merchant1");
        product.setWarehouseCodeptId("warehouse1");
        product.setMerchantSku("sku123");
        product.setManufacturerSku("mSku123");
        product.setManufacturerName("Test Manufacturer");
        product.setEan("invalid-ean"); // Invalid EAN
        product.setItemName("Test Item");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.ean").value("EAN must be a 13-digit number"));
    }

    @Test
    @WithMockUser
    void createProduct_shouldReturnBadRequestWhenFieldExceedsMaxLength() throws Exception {
        ProductCreateRequest product = new ProductCreateRequest();
        product.setMerchantCodeptId("merchant1");
        product.setWarehouseCodeptId("warehouse1");
        product.setMerchantSku("a".repeat(51)); // Exceeds max length of 50
        product.setManufacturerSku("mSku123");
        product.setManufacturerName("Test Manufacturer");
        product.setEan("1234567890123");
        product.setItemName("Test Item");

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.merchantSku").value("Merchant SKU must not exceed 50 characters"));
    }

    @Test
    @WithMockUser
    void deactivateProduct_shouldSetIsActiveToFalse() throws Exception {
        // given
        Product product = new Product();
        product.setMerchantCodeptId("merchant-123");
        product.setWarehouseCodeptId("warehouse-456");
        product.setMerchantSku("sku-789");
        product.setItemName("Sample Product");
        product.setIsActive(true);

        Product savedProduct = productRepository.save(product);

        // when
        mockMvc.perform(patch("/products/" + savedProduct.getId() + "/deactivate"))
                .andExpect(status().isNoContent());

        // then
        Product updatedProduct = productRepository.findById(savedProduct.getId()).orElseThrow();
        assertThat(updatedProduct.getIsActive()).isFalse();
    }
}
