package com.example.fulfilment.controller;

import com.example.fulfilment.controller.dto.ProductCreateRequest;
import com.example.fulfilment.controller.dto.ProductResponse;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductControllerMapper {
  ProductCreateCommand toCommand(ProductCreateRequest request);

  ProductResponse fromProductResult(ProductResult result);
}
