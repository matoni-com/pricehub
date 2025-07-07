package com.matoni.pricehub.price.file.service;

import com.matoni.pricehub.integration.common.ZipExtractor;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Wraps a PriceFileDownloader and extracts CSVs from ZIPs. */
public class ZipUnpackingFileDownloader implements PriceFileDownloader {

  private final PriceFileDownloader delegate;
  private final ZipExtractor zipExtractor;

  public ZipUnpackingFileDownloader(PriceFileDownloader delegate, ZipExtractor zipExtractor) {
    this.delegate = delegate;
    this.zipExtractor = zipExtractor;
  }

  @Override
  public List<Path> downloadFiles(Set<String> alreadyProcessed) throws Exception {
    List<Path> downloaded = delegate.downloadFiles(alreadyProcessed);

    return downloaded.stream()
        .flatMap(
            path -> {
              File file = path.toFile();
              if (file.getName().endsWith(".zip")) {
                try {
                  return zipExtractor.extractCsvFiles(file, file.getParentFile().toPath()).stream()
                      .map(File::toPath);
                } catch (Exception e) {
                  throw new RuntimeException("Failed to unzip file: " + file.getName(), e);
                }
              } else {
                return Stream.of(path);
              }
            })
        .collect(Collectors.toList());
  }
}
