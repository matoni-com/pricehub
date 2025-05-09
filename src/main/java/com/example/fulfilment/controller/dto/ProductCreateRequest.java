package com.example.fulfilment.controller.dto;

import lombok.Data;

@Data
public class ProductCreateRequest {
    private String merchantCodeptId;
    private String warehouseCodeptId;
    private String merchantSku;
    private String manufacturerSku;
    private String manufacturerName;
    private String ean;
    private String itemName;
}
