package com.matoni.pricehub.controller.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

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
