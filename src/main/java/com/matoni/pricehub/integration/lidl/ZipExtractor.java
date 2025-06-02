package com.matoni.pricehub.integration.lidl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Component;

@Component
public class ZipExtractor {

  public List<File> extractCsvFiles(File zipFile, Path targetDir) throws IOException {
    List<File> extractedFiles = new ArrayList<>();

    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile))) {
      ZipEntry entry;
      while ((entry = zis.getNextEntry()) != null) {
        if (!entry.getName().endsWith(".csv")) continue;

        Path outputPath = targetDir.resolve(entry.getName());
        try (OutputStream os = Files.newOutputStream(outputPath)) {
          zis.transferTo(os);
        }
        extractedFiles.add(outputPath.toFile());
      }
    }

    return extractedFiles;
  }
}
