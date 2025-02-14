package com.coherentsolutions.aiwebcrawler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.List;

@Service
public class LinkProcessorService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<String> parseLinks(String jsonResponse) {
        try {
            // Extract JSON by finding the first '{'
            int jsonStart = jsonResponse.indexOf('[');
            if (jsonStart == -1) {
                throw new RuntimeException("JSON not found in response");
            }
            String pureJson = jsonResponse.substring(jsonStart).trim();

            // Convert JSON string to List<String>
            return objectMapper.readValue(pureJson, List.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON response", e);
        }
    }
}