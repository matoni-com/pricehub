package com.matoni.pricehub.retailchain.service.store;

import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.entity.store.Store;
import com.matoni.pricehub.retailchain.repository.store.StoreRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreService {

  private final StoreRepository storeRepository;

  public Store findOrCreate(
      String storeCode,
      String name,
      String address,
      String city,
      String postalCode,
      RetailChain chain) {
    Optional<Store> existing = storeRepository.findByStoreCodeAndChainId(storeCode, chain.getId());
    return existing.orElseGet(
        () -> {
          Store store = new Store();
          store.setStoreCode(storeCode);
          store.setName(name);
          store.setAddress(address);
          store.setCity(city);
          store.setPostalCode(postalCode);
          store.setChain(chain);
          return storeRepository.save(store);
        });
  }
}
