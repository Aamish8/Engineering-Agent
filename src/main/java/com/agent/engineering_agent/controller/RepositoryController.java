package com.agent.engineering_agent.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.agent.engineering_agent.entity.CodeRepository;
import com.agent.engineering_agent.repository.CodeRepositoryRepository;
import com.agent.engineering_agent.service.CodeIndexerService;
import com.agent.engineering_agent.service.CodeRepositoryService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class RepositoryController {
	private final CodeRepositoryService codeRepositoryService;
    private final CodeIndexerService codeIndexerService;
    private final CodeRepositoryRepository codeRepository;
    @PostMapping("/repo/clone")
    public ResponseEntity<CodeRepository>cloneRepository(@RequestParam String repoUrl,
    										@RequestParam(defaultValue="main") String branch,
    										@RequestParam String localPath){
    	CodeRepository codeRepository= codeRepositoryService.registerAndClone(repoUrl, branch, localPath);
    	return ResponseEntity.ok().body(codeRepository);
 }
    @PostMapping("/index/{id}")
    public ResponseEntity<Map<String,String>> indexRepository(@PathVariable Long id) throws IOException {
        CodeRepository repository=codeRepository.findById(id)
        				.orElseThrow(()->new RuntimeException("Repository not found with id: "+id));
        codeIndexerService.processAndIndexAync(repository.getRepoUrl(),repository.getLocalPath());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of(
        		"status","Accepted",
        		"message", "Indexing started in background for repo ID: " + id
        		));
    }
    @GetMapping("/repos")
    public ResponseEntity<List<CodeRepository>> getAllRepositories() {
        return ResponseEntity.ok(codeRepository.findAll());
    }
}
