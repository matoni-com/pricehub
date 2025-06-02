package com.matoni.pricehub.integration.lidl;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

/*
The goal of this service is to:

1) Go to the Lidl price page,

2) Find all .zip file links,

3) Download those .zip files concurrently using a thread pool,

4) Retry downloads with backoff on failures,

5) Use a .part temporary file, and rename it once successful.
 */

@Slf4j
@Service
public class CsvFileDownloadService {

  private static final String BASE_URL = "https://tvrtka.lidl.hr/cijene";

  private final WebClient webClient;
  private final String downloadDir;
  private final Scheduler downloadScheduler;
  private final LidlZipLinkParser linkParser;
  private final ZipFileWriter zipFileWriter;
  private final ZipExtractor zipExtractor;

  public CsvFileDownloadService(
      WebClient webClient,
      @Value("${lidl.csv.download-dir:downloads}") String downloadDir,
      LidlZipLinkParser linkParser,
      ZipFileWriter zipFileWriter,
      ZipExtractor zipExtractor) {
    this.webClient = webClient;
    this.downloadDir = downloadDir;
    this.linkParser = linkParser;
    this.zipFileWriter = zipFileWriter;
    this.zipExtractor = zipExtractor;
    this.downloadScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));
  }

  public List<Path> downloadZipFiles(Set<String> alreadyProcessedFileNames) throws Exception {
    log.info("Starting download of .zip files from {}", BASE_URL);

    Files.createDirectories(Paths.get(downloadDir));
    log.info("Ensured download directory exists: {}", downloadDir);

    List<String> zipUrls = linkParser.findZipFileUrls();

    List<String> filteredUrls =
        zipUrls.stream()
            .filter(
                url -> {
                  try {
                    String fileName = Paths.get(new URL(url).getPath()).getFileName().toString();
                    return !alreadyProcessedFileNames.contains(fileName);
                  } catch (MalformedURLException e) {
                    log.warn("Skipping malformed URL: {}", url, e);
                    return false;
                  }
                })
            .toList();

    if (filteredUrls.isEmpty()) {
      log.info("No new .zip files to download.");
      return List.of();
    }

    log.info("Found {} .zip file(s) to download", zipUrls.size());

    List<Path> downloadedPaths =
        Flux.fromIterable(zipUrls).flatMap(this::downloadFile, 4).collectList().block();

    log.info("All .zip files downloaded successfully.");
    return downloadedPaths;
  }

  private Mono<Path> downloadFile(String zipUrl) {
    return Mono.fromCallable(
            () -> {
              String filename = Paths.get(new URL(zipUrl).getPath()).getFileName().toString();
              Path tempPath = Paths.get(downloadDir, filename + ".part");
              Path targetPath = Paths.get(downloadDir, filename);
              return new FileDownloadInfo(zipUrl, tempPath, targetPath);
            })
        .flatMap(this::downloadWithRetry)
        .subscribeOn(downloadScheduler);
  }

  private Mono<Path> downloadWithRetry(FileDownloadInfo info) {
    Flux<DataBuffer> data =
        webClient
            .get()
            .uri(info.url())
            .retrieve()
            .bodyToFlux(DataBuffer.class)
            .timeout(Duration.ofSeconds(30));

    return zipFileWriter
        .writeToTempThenMove(data, info.tempPath(), info.targetPath())
        .thenReturn(info.targetPath()) // just return the .zip path
        .retryWhen(
            Retry.backoff(3, Duration.ofSeconds(2))
                .doBeforeRetry(
                    retrySignal ->
                        log.warn(
                            "Retrying {} (attempt {}): {}",
                            info.url(),
                            retrySignal.totalRetries() + 1,
                            retrySignal.failure().toString())));
  }

  private record FileDownloadInfo(String url, Path tempPath, Path targetPath) {}
}
