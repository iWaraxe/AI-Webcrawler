package com.coherentsolutions.aiwebcrawler.service;

import com.coherentsolutions.aiwebcrawler.model.Website;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetailFetcherService {

    private final LinkProcessorService linkProcessorService;

    public DetailFetcherService(LinkProcessorService linkProcessorService) {
        this.linkProcessorService = linkProcessorService;
    }

    public String getAllDetails(String jsonResponse) {
        StringBuilder result = new StringBuilder("Landing page:\n");

        // Parse the JSON response to get the list of links
        List<String> links = linkProcessorService.parseLinks(jsonResponse);
        System.out.println("Found links: " + links);

        // Visit each link and fetch the content
        for (String link : links) {
            try {
                Website website = Website.fromUrl(link);
                result.append("\n\n").append(link).append("\n");
                result.append(website.getContents());
            } catch (Exception e) {
                result.append("\n\nFailed to fetch content from: ").append(link).append("\n");
            }
        }

        return result.toString();
    }
}