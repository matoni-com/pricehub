package com.matoni.pricehub.integration.lidl;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.article.service.ArticleService;
import com.matoni.pricehub.price.entity.PriceEntry;
import com.matoni.pricehub.price.repository.PriceEntryRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.entity.store.Store;
import com.matoni.pricehub.retailchain.service.store.StoreService;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceImportService {

  private final CsvParser csvParser;
  private final StoreService storeService;
  private final ArticleService articleService;
  private final PriceEntryRepository priceEntryRepository;

  public void importFromLidlCsv(File file, RetailChain chain)
      throws IOException, CsvValidationException {

    String storeCode = csvParser.extractStoreCode(file.getName());
    String address = extractAddressFromFilename(file.getName());
    String city = extractCityFromFilename(file.getName());
    String postalCode = extractPostalCodeFromFilename(file.getName());

    Store store =
        storeService.findOrCreate(storeCode, "Lidl " + storeCode, address, city, postalCode, chain);

    for (PriceEntry row : csvParser.parse(file, store)) {
      Article article =
          articleService.findOrCreate(
              row.getArticle().getProductCode(),
              row.getArticle().getName(),
              row.getArticle().getBarcode(),
              row.getArticle().getBrand(),
              row.getArticle().getUnit());

      PriceEntry entry = new PriceEntry();
      entry.setStore(store);
      entry.setArticle(article);
      entry.setPriceDate(row.getPriceDate());
      entry.setRetailPrice(row.getRetailPrice());
      entry.setPricePerUnit(row.getPricePerUnit());
      entry.setAnchorPrice(row.getAnchorPrice());

      // Upsert logic: avoid SELECT-before-INSERT
      priceEntryRepository.upsertPriceEntry(entry);
    }
  }

  // Dummy parsing helpers — adjust these later
  private String extractAddressFromFilename(String filename) {
    return "Unknown address";
  }

  private String extractCityFromFilename(String filename) {
    return "Unknown city";
  }

  private String extractPostalCodeFromFilename(String filename) {
    return "00000";
  }
}
