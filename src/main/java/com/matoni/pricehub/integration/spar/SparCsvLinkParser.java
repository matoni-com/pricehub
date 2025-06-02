package com.matoni.pricehub.integration.spar;

import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class SparCsvLinkParser {

  private static final String BASE_URL = "https://www.spar.hr/usluge/cjenici";

  public List<String> findCsvFileUrls() throws IOException {
    Document doc = Jsoup.connect(BASE_URL).get();
    Elements links = doc.select("a[href$=.csv]");
    return links.stream().map(link -> link.absUrl("href")).toList();
  }
}
