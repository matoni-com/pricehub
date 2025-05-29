package com.matoni.pricehub.integration.lidl;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.price.entity.PriceEntry;
import com.matoni.pricehub.retailchain.entity.store.Store;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.springframework.stereotype.Component;

@Component
public class LidlCsvParser {

  private static final Charset ENCODING = Charset.forName("windows-1250");
  private static final DateTimeFormatter DATE_FROM_FILENAME =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public List<PriceEntry> parse(File file, Store store) throws IOException, CsvValidationException {
    List<PriceEntry> entries = new ArrayList<>();

    try (CSVReader reader =
        new CSVReader(new InputStreamReader(new FileInputStream(file), ENCODING))) {
      String[] headers = reader.readNext(); // skip header

      String[] row;
      while ((row = reader.readNext()) != null) {
        Article article = new Article();
        article.setName(row[0]);
        article.setProductCode(row[1]);
        article.setBrand(row[5]);
        article.setBarcode(row[8] != null ? row[8].trim() : null);
        article.setUnit(row[3]);

        // You could also look up existing articles by product code to prevent duplicates

        PriceEntry priceEntry = new PriceEntry();
        priceEntry.setArticle(article);
        priceEntry.setStore(store);
        priceEntry.setPriceDate(extractDateFromFilename(file.getName()));
        priceEntry.setRetailPrice(parseBigDecimal(row[6]));
        priceEntry.setPricePerUnit(parseBigDecimal(row[7]));
        priceEntry.setAnchorPrice(parseBigDecimal(row[10]));

        entries.add(priceEntry);
      }
    }

    return entries;
  }

  public String extractStoreCode(String filename) {
    // e.g. "Supermarket 270_..." → "270"
    try {
      return filename.split("_")[1];
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not extract store code from filename: " + filename);
    }
  }

  public LocalDate extractDateFromFilename(String filename) {
    // e.g. "Supermarket 270_..._19.05.2025_7.15h.csv" → 2025-05-19
    try {
      String[] parts = filename.split("_");
      String datePart = parts[parts.length - 2];
      return LocalDate.parse(datePart, DATE_FROM_FILENAME);
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not extract date from filename: " + filename);
    }
  }

  private BigDecimal parseBigDecimal(String value) {
    try {
      return new BigDecimal(value.replace(",", "."));
    } catch (Exception e) {
      return BigDecimal.ZERO;
    }
  }
}
