package com.matoni.pricehub.service;

import com.matoni.pricehub.entity.Product;
import com.matoni.pricehub.service.dto.ProductCreateCommand;
import com.matoni.pricehub.service.dto.ProductResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductServiceMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  Product toEntity(ProductCreateCommand command);

  ProductResult toResult(Product product);
}
