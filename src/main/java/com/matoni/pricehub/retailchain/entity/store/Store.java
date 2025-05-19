package com.matoni.pricehub.retailchain.entity.store;

import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.utils.entity.TimestampedEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stores")
@Getter
@Setter
public class Store extends TimestampedEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String storeCode;
  private String name;
  private String address;
  private String city;
  private String postalCode;

  @ManyToOne private RetailChain chain;
}
