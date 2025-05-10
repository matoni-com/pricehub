package com.example.fulfilment.service;

import com.example.fulfilment.entity.Product;
import com.example.fulfilment.service.dto.ProductCreateCommand;
import com.example.fulfilment.service.dto.ProductResult;
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
