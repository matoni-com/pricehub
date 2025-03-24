package com.example.fulfilment.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Optional;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String merchantCodeptId;

    @Column(nullable = false)
    private String warehouseCodeptId;

    @Column(nullable = false)
    private String merchantSku;

    @Nullable
    private String manufacturerSku;

    @Nullable
    private String manufacturerName;

    @Nullable
    private String ean;

    @Nullable
    private String itemName;

    public Optional<String> getManufacturerSkuOptional() {
        return Optional.ofNullable(manufacturerSku);
    }

    public Optional<String> getManufacturerNameOptional() {
        return Optional.ofNullable(manufacturerName);
    }

    public Optional<String> getEanOptional() {
        return Optional.ofNullable(ean);
    }

    public Optional<String> getItemNameOptional() {
        return Optional.ofNullable(itemName);
    }
}
