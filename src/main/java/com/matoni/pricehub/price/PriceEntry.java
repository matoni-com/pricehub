package com.matoni.pricehub.price;

import com.matoni.pricehub.entity.Product;
import com.matoni.pricehub.retailchain.store.Store;
import com.matoni.pricehub.utils.entity.TimestampedEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.*;

@Entity
@Table(name = "price_entries")
@Getter
@Setter
public class PriceEntry extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne private Store store;

  @ManyToOne private Product product;

  private LocalDate priceDate;
  private BigDecimal retailPrice;
  private BigDecimal pricePerUnit;
  private BigDecimal anchorPrice;
}
