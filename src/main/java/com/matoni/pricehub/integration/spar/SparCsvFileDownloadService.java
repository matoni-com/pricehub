package com.matoni.pricehub.integration.spar;

import com.matoni.pricehub.integration.common.FileWriter;
import com.matoni.pricehub.price.file.service.AbstractFileDownloadService;
import com.matoni.pricehub.price.file.service.PriceFileDownloader;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class SparCsvFileDownloadService extends AbstractFileDownloadService
    implements PriceFileDownloader {

  private final SparCsvLinkParser linkParser;
  private final FileWriter fileWriter;

  public SparCsvFileDownloadService(
      WebClient webClient,
      @Value("${spar.csv.download-dir:downloads/spar}") String downloadDir,
      SparCsvLinkParser linkParser,
      FileWriter fileWriter) {
    super(webClient, downloadDir);
    this.linkParser = linkParser;
    this.fileWriter = fileWriter;
  }

  @Override
  protected List<String> getFileUrls(Set<String> alreadyProcessed) throws Exception {
    LocalDate today = LocalDate.now();

    log.info("🔍 Fetching CSV file URLs for date: {}", today);
    List<String> allUrls = linkParser.findCsvFileUrls(today);

    List<String> filtered =
        allUrls.stream()
            .filter(
                url -> {
                  String filename = getFileNameFromUrl(url);
                  boolean isUnprocessed = filename != null && !alreadyProcessed.contains(filename);
                  if (!isUnprocessed) {
                    log.debug("⏭ Skipping already processed file: {}", filename);
                  }
                  return isUnprocessed;
                })
            .collect(Collectors.toList());

    log.info("✅ Found {} new CSV files to download", filtered.size());
    return filtered;
  }

  @Override
  protected Mono<Void> writeToDisk(FileDownloadInfo info, Flux<DataBuffer> data) {
    return fileWriter.writeToTempThenMove(data, info.tempPath(), info.targetPath());
  }

  private String getFileNameFromUrl(String url) {
    if (url == null || !url.contains("/")) {
      log.warn("⚠️ Skipping malformed URL: '{}'", url);
      return null;
    }
    return url.substring(url.lastIndexOf('/') + 1);
  }
}
