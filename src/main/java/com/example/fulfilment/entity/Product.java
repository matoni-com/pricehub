package com.example.fulfilment.entity;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "products")
@Getter
@Setter
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

  @Column(nullable = false)
  private Boolean isActive;

  @Nullable private String manufacturerSku;

  @Nullable private String manufacturerName;

  @Nullable private String ean;

  @Nullable private String itemName;

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

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
