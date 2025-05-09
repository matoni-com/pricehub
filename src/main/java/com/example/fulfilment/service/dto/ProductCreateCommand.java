package com.example.fulfilment.service.dto;

import lombok.Data;

@Data
public class ProductCreateCommand {
    private String merchantCodeptId;
    private String warehouseCodeptId;
    private String merchantSku;
    private String manufacturerSku;
    private String manufacturerName;
    private String ean;
    private String itemName;
}
