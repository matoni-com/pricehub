package com.example.fulfilment.service.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProductCreateCommand {
    String merchantCodeptId;
    String warehouseCodeptId;
    String merchantSku;
    String manufacturerSku;
    String manufacturerName;
    String ean;
    String itemName;
    Boolean isActive;
}
