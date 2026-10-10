package com.agent.engineering_agent.aiTools;

import com.agent.engineering_agent.service.VectorStoreService;
import lombok.RequiredArgsConstructor;

import org.springframework.ai.document.Document;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CodebaseTools {

    private final VectorStoreService vectorStoreService;

    @Tool(description = """
            Search the indexed Java repository for source code relevant to the user's request.
            Find actual implementations, methods, classes, controllers, repositories,
            configurations, and tests.
            When investigating how something works, prefer actual implementation code
            over interfaces, DTOs, or test classes.
            """)
       public String searchCodebase(
            @ToolParam(description = "The search query or technical concept to look up in the code") String query,
            @ToolParam(description = "Optional repository URL to scope the search") String repoUrl) {

        List<Document> docs = vectorStoreService.similaritySearch(query, repoUrl, 10);

        if (docs.isEmpty()) {
            return "No relevant code chunks found.";
        }
        return formatSearchResults(docs);
    }
    public String formatSearchResults(List<Document> documents) {
        StringBuilder sb = new StringBuilder();
        for (Document doc : documents) {
            Map<String, Object> metadata = doc.getMetadata();
            sb.append(String.format("""
                    --- CHUNK START ---
                    File Path: %s
                    Class: %s
                    Method: %s
                    Code:
                    %s
                    --- CHUNK END ---
                    
                    """,
                    metadata.getOrDefault("filePath", "N/A"),
                    metadata.getOrDefault("className", "N/A"),
                    metadata.getOrDefault("methodName", "N/A"),
                    doc.getText()
            ));
        }
        return sb.toString();
    }
}