package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.controller.dto.ProductResponse;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;

public class ProductControllerMapper {

    public static ProductCreateCommand toCommand(ProductCreateRequest request) {
        ProductCreateCommand command = new ProductCreateCommand();
        command.setMerchantCodeptId(request.getMerchantCodeptId());
        command.setWarehouseCodeptId(request.getWarehouseCodeptId());
        command.setMerchantSku(request.getMerchantSku());
        command.setManufacturerSku(request.getManufacturerSku());
        command.setManufacturerName(request.getManufacturerName());
        command.setEan(request.getEan());
        command.setItemName(request.getItemName());
        return command;
    }

    public static ProductResponse fromProductResult(ProductResult result) {
        ProductResponse response = new ProductResponse();
        response.setId(result.getId());
        response.setMerchantCodeptId(result.getMerchantCodeptId());
        response.setWarehouseCodeptId(result.getWarehouseCodeptId());
        response.setMerchantSku(result.getMerchantSku());
        response.setManufacturerSku(result.getManufacturerSku());
        response.setManufacturerName(result.getManufacturerName());
        response.setEan(result.getEan());
        response.setItemName(result.getItemName());
        response.setCreatedAt(result.getCreatedAt());
        response.setUpdatedAt(result.getUpdatedAt());
        return response;
    }
}