package com.example.fulfilment.service.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductResult {
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
