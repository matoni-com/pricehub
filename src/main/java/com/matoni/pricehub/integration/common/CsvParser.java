package com.matoni.pricehub.integration.common;

import com.matoni.pricehub.article.entity.Article;
import com.matoni.pricehub.price.entity.PriceEntry;
import com.matoni.pricehub.retailchain.entity.store.Store;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CsvParser {

  private static final Logger log = LoggerFactory.getLogger(CsvParser.class);
  private static final DateTimeFormatter DATE_FROM_FILENAME =
      DateTimeFormatter.ofPattern("dd.MM.yyyy");
  private static final int MIN_COLUMNS = 11;

  public List<PriceEntry> parseLidl(File file, Store store)
      throws IOException, CsvValidationException {
    return parseInternal(file, store, Encoding.LIDL);
  }

  public List<PriceEntry> parseSpar(File file, Store store)
      throws IOException, CsvValidationException {
    return parseInternal(file, store, Encoding.SPAR);
  }

  private List<PriceEntry> parseInternal(File file, Store store, Encoding encoding)
      throws IOException, CsvValidationException {

    List<PriceEntry> entries = new ArrayList<>();
    Charset charset = encoding.charset;

    System.out.printf("📄 Parsing %s using encoding: %s%n", file.getName(), charset);

    LocalDate priceDate;
    try {
      priceDate = extractDateFromFilename(file.getName());
    } catch (Exception e) {
      System.err.printf(
          "❌ Could not extract date from filename: %s -> %s%n", file.getName(), e.getMessage());
      return entries;
    }

    int errorCount = 0;
    int errorLogLimit = 10;

    try (CSVReader reader =
        new CSVReaderBuilder(new InputStreamReader(new FileInputStream(file), charset))
            .withCSVParser(new CSVParserBuilder().withSeparator(encoding.delimiter).build())
            .build()) {

      reader.readNext(); // skip header
      String[] row;
      int lineNumber = 1;

      while ((row = reader.readNext()) != null) {
        lineNumber++;

        for (int i = 0; i < row.length; i++) {
          if (row[i] != null) row[i] = row[i].trim();
        }

        if (row.length < MIN_COLUMNS) continue;

        try {
          int retailPriceIdx = encoding == Encoding.LIDL ? 5 : 6;
          int pricePerUnitIdx = encoding == Encoding.LIDL ? 8 : 7;
          int anchorPriceIdx = encoding == Encoding.LIDL ? 11 : 10;

          BigDecimal retailPrice =
              parseBigDecimal(row[retailPriceIdx], "retailPrice", lineNumber, file.getName());
          BigDecimal pricePerUnit =
              parseBigDecimal(row[pricePerUnitIdx], "pricePerUnit", lineNumber, file.getName());
          BigDecimal anchorPrice =
              parseBigDecimal(row[anchorPriceIdx], "anchorPrice", lineNumber, file.getName());

          if (retailPrice == null || pricePerUnit == null || anchorPrice == null) {
            if (errorCount++ < errorLogLimit) {
              System.err.printf(
                  "⚠️ Skipping row %d in %s due to invalid price data%n",
                  lineNumber, file.getName());
            }
            continue;
          }

          createPriceEntry(store, row, priceDate, retailPrice, pricePerUnit, anchorPrice, entries);

        } catch (Exception e) {
          if (errorCount++ < errorLogLimit) {
            System.err.printf(
                "❌ Error parsing row %d in %s: %s%n", lineNumber, file.getName(), e.getMessage());
          }
        }
      }

      if (errorCount > errorLogLimit) {
        System.err.printf(
            "⚠️ Suppressed %d additional errors for %s%n",
            errorCount - errorLogLimit, file.getName());
      }
    }

    return entries;
  }

  private static void createPriceEntry(
      Store store,
      String[] row,
      LocalDate priceDate,
      BigDecimal retailPrice,
      BigDecimal pricePerUnit,
      BigDecimal anchorPrice,
      List<PriceEntry> entries) {
    Article article = new Article();
    article.setName(row[0]);
    article.setProductCode(row[1]);
    article.setBrand(row[2]);
    article.setUnit(row[4]);
    article.setBarcode((row[11] != null && !row[11].isEmpty()) ? row[11] : null);

    PriceEntry priceEntry = new PriceEntry();
    priceEntry.setArticle(article);
    priceEntry.setStore(store);
    priceEntry.setPriceDate(priceDate);
    priceEntry.setRetailPrice(retailPrice);
    priceEntry.setPricePerUnit(pricePerUnit);
    priceEntry.setAnchorPrice(anchorPrice);

    entries.add(priceEntry);
  }

  public String extractStoreCode(String filename) {
    try {
      if (filename.toLowerCase().contains("spar") || filename.toLowerCase().contains("interspar")) {
        // SPAR format logic
        String[] parts = filename.split("_");
        String city = parts[1];
        String storeId = parts[parts.length - 5];
        return city + "_" + storeId;
      } else if (filename.toLowerCase().contains("supermarket")) {
        // Lidl format logic
        // e.g. Supermarket 130_Put Lore_4_21000_Split_1_01.06.2025_7.15h.csv
        String[] parts = filename.split("_");
        // Assuming parts[0] is 'Supermarket 130', and parts[4] is postal code
        String storeNumber = parts[0].replace("Supermarket ", "").trim(); // "130"
        String postalCode = parts[4]; // "21000"
        return "lidl_" + storeNumber + "_" + postalCode;
      } else {
        throw new IllegalArgumentException("Unrecognized filename pattern: " + filename);
      }
    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Could not extract store code from filename: " + filename, e);
    }
  }

  public LocalDate extractDateFromFilename(String filename) {
    try {
      // Try Lidl format first: ..._dd.MM.yyyy_...
      if (filename.matches(".*_\\d{2}\\.\\d{2}\\.\\d{4}_.*")) {
        String[] parts = filename.split("_");
        String datePart =
            Arrays.stream(parts)
                .filter(p -> p.matches("\\d{2}\\.\\d{2}\\.\\d{4}"))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException("No dd.MM.yyyy date found in: " + filename));
        return LocalDate.parse(datePart, DATE_FROM_FILENAME);
      }

      // Otherwise, try SPAR format: ..._yyyyMMdd_...
      if (filename.matches(".*_\\d{8}_.*")) {
        String[] parts = filename.split("_");
        String datePart =
            Arrays.stream(parts)
                .filter(p -> p.matches("\\d{8}"))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException("No yyyyMMdd date found in: " + filename));
        return LocalDate.parse(datePart, DateTimeFormatter.BASIC_ISO_DATE);
      }

      throw new IllegalArgumentException("No recognizable date format in: " + filename);
    } catch (Exception e) {
      throw new IllegalArgumentException("Could not extract date from filename: " + filename, e);
    }
  }

  private BigDecimal parseBigDecimal(
      String value, String columnName, int lineNumber, String filename) {
    try {
      if (value == null || value.trim().isEmpty()) {
        throw new IllegalArgumentException("empty value");
      }

      String cleaned =
          value
              .replaceAll("\\.", "") // remove dots (thousands separator, if present)
              .replace(",", ".") // replace decimal comma
              .replace("\"", "") // strip quotes
              .trim();

      BigDecimal result = new BigDecimal(cleaned);

      if (result.abs().compareTo(new BigDecimal("99999999.99")) >= 0) {
        System.err.printf(
            "⚠️  Overflow risk on row %d (%s): %s = %s%n",
            lineNumber, filename, columnName, result);
      }

      return result;

    } catch (Exception e) {
      System.err.printf(
          "❌ Invalid number on row %d (%s): [%s = %s]%n", lineNumber, filename, columnName, value);
      return BigDecimal.ZERO; // or null if you want to skip the row
    }
  }

  private enum Encoding {
    LIDL(Charset.forName("windows-1252"), ','),
    SPAR(Charset.forName("windows-1252"), ';');

    public final Charset charset;
    public final char delimiter;

    Encoding(Charset charset, char delimiter) {
      this.charset = charset;
      this.delimiter = delimiter;
    }
  }
}
