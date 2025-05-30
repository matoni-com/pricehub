package com.matoni.pricehub.integration.lidl;

import static org.assertj.core.api.Assertions.assertThat;

import com.matoni.pricehub.common.BaseIntegrationSuite;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

    // when
    csvFileDownloadService.downloadZipFiles();

    // then
    File downloadDir = new File(DOWNLOAD_DIR);
    assertThat(downloadDir.exists()).isTrue();
    assertThat(downloadDir.listFiles()).isNotEmpty();
    for (File file : downloadDir.listFiles()) {
      assertThat(file.getName()).endsWith(".zip");
    }
  }
}
