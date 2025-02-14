package com.coherentsolutions.aiwebcrawler.controller;

import com.coherentsolutions.aiwebcrawler.model.BrochureRequest;
import com.coherentsolutions.aiwebcrawler.model.UrlRequest;
import com.coherentsolutions.aiwebcrawler.model.Website;
import com.coherentsolutions.aiwebcrawler.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api")
public class WebCrawlerController {

    private final SpringAIService springAIService;
    private final DetailFetcherService detailFetcherService;


    public WebCrawlerController(SpringAIService springAIService,
                                DetailFetcherService detailFetcherService) {
        this.springAIService = springAIService;
        this.detailFetcherService = detailFetcherService;
    }

    @PostMapping("/getRelevantLinks")
    public ResponseEntity<String> getRelevantLinks(@RequestBody String url) {
        try {
            Website website = Website.fromUrl(url);
            String prompt = springAIService.generateLinksPrompt(website);
            String jsonResponse = springAIService.getRelevantLinks(prompt);
            return ResponseEntity.ok(jsonResponse);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error processing request: " + e.getMessage());
        }
    }

    @PostMapping("/generateBrochure")
    public ResponseEntity<String> generateBrochure(@RequestBody UrlRequest urlRequest) {
        try {
            // Step 1: Get relevant links using SpringAIService
            Website website = Website.fromUrl(urlRequest.url());
            String linksPrompt = springAIService.generateLinksPrompt(website);
            String jsonResponse = springAIService.getRelevantLinks(linksPrompt);

            // Step 2: Fetch detailed content for all relevant links
            String combinedContent = detailFetcherService.getAllDetails(jsonResponse);

            // Step 3: Generate brochure using the content with SpringAIService
            String brochure = springAIService.generateBrochure(new BrochureRequest(website.title(),combinedContent));

            // Return the generated brochure
            return ResponseEntity.ok(brochure);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating brochure: " + e.getMessage());
        }
    }
}