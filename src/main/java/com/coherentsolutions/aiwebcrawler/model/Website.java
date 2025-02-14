package com.coherentsolutions.aiwebcrawler.model;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public record Website(String url, String title, String text, List<String> links) {

    public static Website fromUrl(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/117.0.0.0 Safari/537.36")
                .get();

        String title = document.title() != null ? document.title() : "No title found";

        Element body = document.body();
        String text = body != null ? body.text() : "";

        // Extract links
        List<String> links = document.select("a[href]")
                .stream()
                .map(linkElement -> linkElement.attr("abs:href"))
                .filter(link -> !link.isEmpty())
                .collect(Collectors.toList());

        return new Website(url, title, text, links);
    }

    public String getContents() {
        return "Webpage Title:\n" + title + "\nWebpage Contents:\n" + text + "\n";
    }
}