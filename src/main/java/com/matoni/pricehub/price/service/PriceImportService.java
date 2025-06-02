package com.matoni.pricehub.price.service;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.article.service.ArticleService;
import com.matoni.pricehub.integration.lidl.CsvParser;
import com.matoni.pricehub.price.entity.PriceEntry;
import com.matoni.pricehub.price.repository.PriceEntryRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.entity.store.Store;
import com.matoni.pricehub.retailchain.service.store.StoreService;
import com.opencsv.exceptions.CsvValidationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PriceImportService {

  private final CsvParser csvParser;
  private final StoreService storeService;
  private final ArticleService articleService;
  private final PriceEntryRepository priceEntryRepository;

  public void importFromCsv(File file, RetailChain chain, String strategy)
      throws IOException, CsvValidationException {

    String storeCode = csvParser.extractStoreCode(file.getName());
    String address = extractAddressFromFilename(file.getName());
    String city = extractCityFromFilename(file.getName());
    String postalCode = extractPostalCodeFromFilename(file.getName());

    Store store =
        storeService.findOrCreate(
            storeCode, chain.getName() + " " + storeCode, address, city, postalCode, chain);

    List<PriceEntry> rawEntries =
        switch (strategy.toLowerCase()) {
          case "lidl" -> csvParser.parseLidl(file, store);
          case "spar" -> csvParser.parseSpar(file, store);
          default -> throw new IllegalArgumentException("Unsupported parser strategy: " + strategy);
        };

    persistWithResolvedArticles(rawEntries, store);
  }

  private void persistWithResolvedArticles(List<PriceEntry> rawEntries, Store store) {
    List<Article> rawArticles = rawEntries.stream().map(PriceEntry::getArticle).distinct().toList();

    List<Article> resolvedArticles = articleService.findOrCreateAll(rawArticles);

    Map<String, Article> articleByProductCode =
        resolvedArticles.stream()
            .collect(Collectors.toMap(Article::getProductCode, Function.identity()));

    List<PriceEntry> finalEntries =
        rawEntries.stream()
            .map(
                row -> {
                  Article resolved = articleByProductCode.get(row.getArticle().getProductCode());
                  PriceEntry entry = new PriceEntry();
                  entry.setStore(store);
                  entry.setArticle(resolved);
                  entry.setPriceDate(row.getPriceDate());
                  entry.setRetailPrice(row.getRetailPrice());
                  entry.setPricePerUnit(row.getPricePerUnit());
                  entry.setAnchorPrice(row.getAnchorPrice());
                  return entry;
                })
            .toList();

    priceEntryRepository.upsertAll(finalEntries);
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
