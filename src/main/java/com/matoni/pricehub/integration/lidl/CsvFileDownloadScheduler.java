package com.matoni.pricehub.integration.lidl;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CsvFileDownloadScheduler {

  private final PriceImportService priceImportService;

  public CsvFileDownloadScheduler(PriceImportService priceImportService) {
    this.priceImportService = priceImportService;
  }

  @Scheduled(cron = "0 0 4 * * *") // Runs every day at 4:00 AM
  public void scheduleCsvDownload() {
    System.out.println("Running CSV download and import at 4:00 AM...");
    // Add logic to download the CSV file and call the import service
    // Example:
    // File csvFile = downloadCsvFile();
    // priceImporterService.importFromLidlCsv(csvFile, retailChain);
  }
}
