package com.example.fulfilment.service;

import com.example.fulfilment.entity.Product;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;

public class ProductServiceMapper {

    public static Product toEntity(ProductCreateCommand command) {
        Product product = new Product();
        product.setMerchantCodeptId(command.getMerchantCodeptId());
        product.setWarehouseCodeptId(command.getWarehouseCodeptId());
        product.setMerchantSku(command.getMerchantSku());
        product.setManufacturerSku(command.getManufacturerSku());
        product.setManufacturerName(command.getManufacturerName());
        product.setEan(command.getEan());
        product.setItemName(command.getItemName());
        return product;
    }

    public static ProductResult toResult(Product product) {
        ProductResult result = new ProductResult();
        result.setId(product.getId());
        result.setMerchantCodeptId(product.getMerchantCodeptId());
        result.setWarehouseCodeptId(product.getWarehouseCodeptId());
        result.setMerchantSku(product.getMerchantSku());
        result.setManufacturerSku(product.getManufacturerSku());
        result.setManufacturerName(product.getManufacturerName());
        result.setEan(product.getEan());
        result.setItemName(product.getItemName());
        result.setCreatedAt(product.getCreatedAt());
        result.setUpdatedAt(product.getUpdatedAt());
        return result;
    }
}
