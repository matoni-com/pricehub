package com.matoni.pricehub.integration.lidl;

import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
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

  public CsvFileDownloadService(
      WebClient webClient, @Value("${lidl.csv.download-dir:downloads}") String downloadDir) {
    this.webClient = webClient;
    this.downloadDir = downloadDir;
    this.downloadScheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(4));
  }

  public void downloadZipFiles() throws Exception {
    log.info("Starting download of .zip files from {}", BASE_URL);

    Files.createDirectories(Paths.get(downloadDir));
    log.info("Ensured download directory exists: {}", downloadDir);

    List<String> zipUrls = getFullUrlsOfZipFiles();

    if (zipUrls.isEmpty()) {
      throw new IllegalStateException("No .zip file found on the page");
    }

    log.info("Found {} .zip file(s) to download", zipUrls.size());

    Flux.fromIterable(zipUrls)
        .flatMap(this::downloadFile, 4) // parallelism = 4
        .then()
        .block();

    log.info("All .zip files downloaded successfully.");
  }

  private static List<String> getFullUrlsOfZipFiles() throws IOException {
    Document doc = Jsoup.connect(BASE_URL).get();
    Elements links = doc.select("a[href$=.zip]");
    List<String> zipUrls = links.stream().map(link -> link.absUrl("href")).toList();
    return zipUrls;
  }

  private Mono<Void> downloadFile(String zipUrl) {
    return Mono.fromCallable(
            () -> {
              String filename = Paths.get(new URL(zipUrl).getPath()).getFileName().toString();
              Path tempPath = Paths.get(downloadDir, filename + ".part");
              Path targetPath = Paths.get(downloadDir, filename);
              return new FileDownloadInfo(zipUrl, tempPath, targetPath);
            })
        .flatMap(this::downloadWithRetry)
        .subscribeOn(downloadScheduler); // run in thread pool
  }

  private Mono<Void> downloadWithRetry(FileDownloadInfo info) {
    return DataBufferUtils.write(
            webClient
                .get()
                .uri(info.url())
                .retrieve()
                .bodyToFlux(DataBuffer.class)
                .timeout(Duration.ofSeconds(30)), // timeout for entire download
            info.tempPath(),
            StandardOpenOption.CREATE)
        .doOnSuccess(
            unused -> {
              try {
                Files.move(info.tempPath(), info.targetPath(), StandardCopyOption.REPLACE_EXISTING);
                log.info("Downloaded and saved to {}", info.targetPath());
              } catch (IOException e) {
                log.error("Failed to move file to final location: {}", info.targetPath(), e);
              }
            })
        .doOnError(
            e -> {
              log.error("Download failed for {}: {}", info.url(), e.toString());
              try {
                Files.deleteIfExists(info.tempPath());
              } catch (IOException ignored) {
              }
            })
        .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)).transientErrors(true));
  }

  private record FileDownloadInfo(String url, Path tempPath, Path targetPath) {}
}
