package com.example.fulfilment.controller.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class ProductResponse {
    Long id;
    String merchantCodeptId;
    String warehouseCodeptId;
    String merchantSku;
    String manufacturerSku;
    String manufacturerName;
    String ean;
    String itemName;
    Boolean isActive;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}
