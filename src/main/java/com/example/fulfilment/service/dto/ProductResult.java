package com.example.fulfilment.service.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductResult {
    private Long id;
    private String merchantCodeptId;
    private String warehouseCodeptId;
    private String merchantSku;
    private String manufacturerSku;
    private String manufacturerName;
    private String ean;
    private String itemName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
