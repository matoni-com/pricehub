package com.matoni.pricehub.integration.lidl;

import static org.assertj.core.api.Assertions.assertThat;

import com.matoni.pricehub.common.BaseIntegrationSuite;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CsvFileDownloadServiceTests extends BaseIntegrationSuite {

  @Autowired private CsvFileDownloadService csvFileDownloadService;

  private static final String DOWNLOAD_DIR = "downloads";

  @AfterEach
  void cleanUp() throws Exception {
    // Clean up downloaded files after the test
    Files.walk(Paths.get(DOWNLOAD_DIR)).map(Path::toFile).forEach(File::delete);
  }

  @Test
  void it_should_download_zip_files() throws Exception {
    // given
    // Mock the BASE_URL or use a test server to serve .zip files
    // Example: WireMock or a local test server

    Set<String> processedZipNames =
        Set.of(
            "Popis_cijena_po_trgovinama_na_dan_01_06_2025.zip",
            "Popis_cijena_po_trgovinama_na_dan_02_06_2025.zip",
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

    // when
    csvFileDownloadService.downloadZipFiles(processedZipNames);

    // then
    File downloadDir = new File(DOWNLOAD_DIR);
    assertThat(downloadDir.exists()).isTrue();
    assertThat(downloadDir.listFiles()).isNotEmpty();
    for (File file : downloadDir.listFiles()) {
      assertThat(file.getName()).endsWith(".zip");
    }
  }
}
