package com.example.fulfilment.controller;

import com.example.fulfilment.common.BaseIntegrationSuite;
import com.example.fulfilment.entity.Product;
import com.example.fulfilment.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.BeforeEach;

@AutoConfigureMockMvc
class ProductControllerIntegrationTest extends BaseIntegrationSuite {

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
    void createProduct_shouldPersistProductToDatabase() throws Exception {
        Product product = new Product();
        product.setMerchantCodeptId("merchant1");
        product.setWarehouseCodeptId("warehouse1");
        product.setMerchantSku("sku123");
        product.setManufacturerSku("mSku123");
        product.setManufacturerName("Test Manufacturer");
        product.setEan("1234567890123");
        product.setItemName("Test Item");

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
    void getAllProducts_shouldReturnSavedProducts() throws Exception {
        // given
        Product product = new Product();
        product.setMerchantCodeptId("merchant-xyz");
        product.setWarehouseCodeptId("warehouse-abc");
        product.setMerchantSku("sku-001");
        product.setItemName("Test Product");

        productRepository.save(product);

        // when + then
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].merchantCodeptId").value("merchant-xyz"))
                .andExpect(jsonPath("$[0].merchantSku").value("sku-001"))
                .andExpect(jsonPath("$[0].itemName").value("Test Product"));
    }

}
