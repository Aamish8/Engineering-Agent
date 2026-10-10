package com.agent.engineering_agent.controller;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.agent.engineering_agent.aiTools.EngineeringAgentService;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
public class EngineeringAgentController {
	private final EngineeringAgentService service;
	@GetMapping(value="/agent/stream" ,produces=MediaType.TEXT_PLAIN_VALUE)
	public Flux<String> runAgentStream(@RequestParam String conversationId,@RequestParam String question,@RequestParam String repoUrl
							,@RequestParam String localPath) {
		return service.runAgentStream(conversationId,question,repoUrl,localPath);
	}
}
