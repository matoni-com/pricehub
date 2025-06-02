package com.matoni.pricehub.price.file.service;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface PriceFileDownloader {
  List<Path> downloadFiles(Set<String> alreadyProcessedFilenames) throws Exception;
}
