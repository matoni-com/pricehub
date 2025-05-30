package com.matoni.pricehub.integration.lidl;

import java.io.IOException;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Component
public class LidlZipLinkParser {

  private static final String BASE_URL = "https://tvrtka.lidl.hr/cijene";

  public List<String> findZipFileUrls() throws IOException {
    Document doc = Jsoup.connect(BASE_URL).get();
    Elements links = doc.select("a[href$=.zip]");
    return links.stream().map(link -> link.absUrl("href")).toList();
  }
}
