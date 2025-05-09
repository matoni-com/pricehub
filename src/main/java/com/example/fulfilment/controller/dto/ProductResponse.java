package com.example.fulfilment.controller.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductResponse {
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
