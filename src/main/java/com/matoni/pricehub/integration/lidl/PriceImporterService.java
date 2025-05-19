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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceImporterService {

  private final LidlCsvParser lidlCsvParser;
  private final StoreService storeService;
  private final ArticleService articleService;
  private final PriceEntryRepository priceEntryRepository;

  public void importFromLidlCsv(File file, RetailChain chain)
      throws IOException, CsvValidationException {
    String storeCode = lidlCsvParser.extractStoreCode(file.getName());
    String address = extractAddressFromFilename(file.getName());
    String city = extractCityFromFilename(file.getName());
    String postalCode = extractPostalCodeFromFilename(file.getName());

    Store store =
        storeService.findOrCreate(storeCode, "Lidl " + storeCode, address, city, postalCode, chain);

    List<PriceEntry> entries = new ArrayList<>();

    for (PriceEntry row : lidlCsvParser.parse(file, store)) {
      Article article =
          articleService.findOrCreate(
              row.getArticle().getProductCode(),
              row.getArticle().getName(),
              row.getArticle().getBarcode(),
              row.getArticle().getBrand(),
              row.getArticle().getUnit());

      LocalDate priceDate = row.getPriceDate();

      Optional<PriceEntry> existing =
          priceEntryRepository.findByStoreIdAndArticleIdAndPriceDate(
              store.getId(), article.getId(), priceDate);

      if (existing.isPresent()) {
        PriceEntry entry = existing.get();
        entry.setRetailPrice(row.getRetailPrice());
        entry.setPricePerUnit(row.getPricePerUnit());
        entry.setAnchorPrice(row.getAnchorPrice());
        priceEntryRepository.save(entry);
      } else {
        PriceEntry newEntry = new PriceEntry();
        newEntry.setStore(store);
        newEntry.setArticle(article);
        newEntry.setPriceDate(priceDate);
        newEntry.setRetailPrice(row.getRetailPrice());
        newEntry.setPricePerUnit(row.getPricePerUnit());
        newEntry.setAnchorPrice(row.getAnchorPrice());
        priceEntryRepository.save(newEntry);
      }
    }

    priceEntryRepository.saveAll(entries);
  }

  // Dummy parsing helpers — adjust to match filename format more accurately
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
