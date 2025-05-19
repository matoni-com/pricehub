package com.matoni.pricehub.retailchain.repository.store;

import com.matoni.pricehub.retailchain.entity.store.Store;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
  Optional<Store> findByStoreCodeAndChainId(String storeCode, Long chainId);
}
