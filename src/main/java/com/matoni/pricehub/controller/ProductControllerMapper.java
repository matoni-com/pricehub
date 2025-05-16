package com.matoni.pricehub.controller;

import com.matoni.pricehub.controller.dto.ProductCreateRequest;
import com.matoni.pricehub.controller.dto.ProductResponse;
import com.matoni.pricehub.service.dto.ProductCreateCommand;
import com.matoni.pricehub.service.dto.ProductResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductControllerMapper {
  ProductCreateCommand toCommand(ProductCreateRequest request);

  ProductResponse fromProductResult(ProductResult result);
}
