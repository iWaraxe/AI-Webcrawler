package com.coherentsolutions.aiwebcrawler.service;

import com.coherentsolutions.aiwebcrawler.model.BrochureRequest;
import com.coherentsolutions.aiwebcrawler.model.Website;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Service
public class SpringAIService {

    private final ChatModel chatModel;

    @Value("classpath:templates/link-prompt-template.st")
    private Resource linkPromptTemplate;

    @Value("classpath:templates/brochure_system_prompt.st")
    private Resource systemPromptTemplate;

    @Value("classpath:templates/brochure_user_prompt.st")
    private Resource userPromptTemplate;

    public SpringAIService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    private String loadTemplate(Resource template) throws IOException {
        return new String(Files.readAllBytes(template.getFile().toPath()));
    }

    public String generateLinksPrompt(Website website) {
        try {
            String template = loadTemplate(linkPromptTemplate);  // Load template content
            String links = String.join("\n", website.links());   // Convert the list of links to a single string

            // Replace placeholders with actual values
            return template
                    .replace("{url}", website.url())
                    .replace("{links}", links);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load or process the link prompt template", e);
        }
    }

    public String getRelevantLinks(String prompt) {
        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        Prompt chatPrompt = promptTemplate.create(Map.of());
        ChatResponse response = chatModel.call(chatPrompt);

        // Assuming the response contains JSON in the message content
        return response.getResult().getOutput().getContent();
    }

    public String generateBrochure(BrochureRequest request) {
        try {
            // Load templates
            String systemPrompt = loadTemplate(systemPromptTemplate);
            String userPrompt = loadTemplate(userPromptTemplate)
                    .replace("{companyName}", request.companyName())
                    .replace("{content}", request.content());

            // Combine both prompts
            String fullPrompt = systemPrompt + "\n\n" + userPrompt;

            // Create and send the chat request
            PromptTemplate promptTemplate = new PromptTemplate(fullPrompt);
            Prompt prompt = promptTemplate.create(Map.of());
            ChatResponse response = chatModel.call(prompt);

            return response.getResult().getOutput().getContent();
        } catch (IOException e) {
            throw new RuntimeException("Failed to load template", e);
        }
    }
}