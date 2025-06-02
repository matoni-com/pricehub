package com.matoni.pricehub.integration.lidl;

import static org.assertj.core.api.Assertions.assertThat;

import com.matoni.pricehub.common.BaseIntegrationSuite;
import com.matoni.pricehub.price.file.entity.ProcessedFile;
import com.matoni.pricehub.price.file.repository.ProcessedFileRepository;
import com.matoni.pricehub.price.repository.PriceEntryRepository;
import com.matoni.pricehub.retailchain.entity.RetailChain;
import com.matoni.pricehub.retailchain.repository.RetailChainRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LidlPriceDataPipelineIntegrationTest extends BaseIntegrationSuite {

  @Autowired private LidlPriceDataPipeline pipeline;
  @Autowired private PriceEntryRepository priceEntryRepository;
  @Autowired private RetailChainRepository retailChainRepository;
  @Autowired private ProcessedFileRepository processedFileRepository;

  private static final String DOWNLOAD_DIR = "downloads";

  @BeforeEach
  void ensureLidlRetailChainExists() {
    retailChainRepository
        .findByName("Lidl")
        .orElseGet(
            () -> {
              RetailChain lidl = new RetailChain();
              lidl.setName("Lidl");
              return retailChainRepository.save(lidl);
            });
  }

  @AfterEach
  void cleanUp() throws Exception {
    // Delete all files in download directory after the test
    if (Files.exists(Paths.get(DOWNLOAD_DIR))) {
      Files.walk(Paths.get(DOWNLOAD_DIR))
          .map(Path::toFile)
          .sorted((f1, f2) -> -f1.compareTo(f2)) // delete children first
          .forEach(File::delete);
    }
  }

  @Test
  void it_should_download_extract_and_import_prices() throws Exception {

    RetailChain lidl = retailChainRepository.findByName("Lidl").orElseThrow();

    Set<String> processedZipNames =
        Set.of(
            "Popis_cijena_po_trgovinama_na_dan_15_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_16_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_17_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_18_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_19_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_20_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_21_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_22_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_23_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_24_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_25_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_26_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_27_05_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_28_05_2025.zip");

    List<ProcessedFile> filesToSeed =
        processedZipNames.stream()
            .map(
                name -> {
                  ProcessedFile pf = new ProcessedFile();
                  pf.setRetailChain(lidl); // assume 'lidl' is already fetched
                  pf.setFileName(name);
                  return pf;
                })
            .toList();

    processedFileRepository.saveAll(filesToSeed);

    // when
    pipeline.run();

    // then
    File downloadDir = new File(DOWNLOAD_DIR);
    assertThat(downloadDir.exists()).isTrue();

    File[] allFiles = downloadDir.listFiles();
    assertThat(allFiles).isNotEmpty();
    assertThat(allFiles).anyMatch(f -> f.getName().endsWith(".zip"));
    assertThat(allFiles).anyMatch(f -> f.getName().endsWith(".csv"));

    long count = priceEntryRepository.count();
    assertThat(count).isGreaterThan(0);
  }
}
