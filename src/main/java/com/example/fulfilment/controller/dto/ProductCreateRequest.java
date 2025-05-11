package com.example.fulfilment.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductCreateRequest {

    @NotBlank(message = "Merchant Codept ID is required")
    private String merchantCodeptId;

    @NotBlank(message = "Warehouse Codept ID is required")
    private String warehouseCodeptId;

    @NotBlank(message = "Merchant SKU is required")
    @Size(max = 50, message = "Merchant SKU must not exceed 50 characters")
    private String merchantSku;

    @NotBlank(message = "Manufacturer SKU is required")
    private String manufacturerSku;

    @NotBlank(message = "Manufacturer Name is required")
    private String manufacturerName;

    @NotBlank(message = "EAN is required")
    @Pattern(regexp = "\\d{13}", message = "EAN must be a 13-digit number")
    private String ean;

    @NotBlank(message = "Item Name is required")
    @Size(max = 100, message = "Item Name must not exceed 100 characters")
    private String itemName;

    private Boolean isActive = true;
}
