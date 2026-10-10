package com.agent.engineering_agent.aiTools;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.agent.engineering_agent.service.ConversationMemoryService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class EngineeringAgentService {
 private final ChatClient chatClient;
private final ChatClient streamingChatClient;
 private final ConversationMemoryService memoryService;
 public String runAgent(String conversationId,String query,String repoUrl,String localPath) {
	 List<String>history= memoryService.getHistory(conversationId);
	 String response= chatClient.prompt()
			 .system("""
	                    Target repository URL: %s
	                    Local repository path: %s
	                    """.formatted(repoUrl, localPath))
	            .user(history+ "\n Current question: "+query)
	            .call()
	            .content();
	 memoryService.saveMessage(conversationId,"USER", query);
	 memoryService.saveMessage(conversationId, "AGENT", response);
	 return response;
 }
 public Flux<String> runAgentStream(String conversationId,String query,String repoUrl,String localPath){
	 List<String>history=memoryService.getHistory(conversationId);
	 memoryService.saveMessage(conversationId,"USER",query);
	  String retrievedContext=chatClient.prompt()
			 .system("""
			 		You are a code retrieval agent. You MUST execute available tools (like searchCodebase) to search for and extract relevant Java code snippets, class definitions, and methods.
                    OUTPUT ALL RETRIEVED SOURCE CODE SNIPPETS IN FULL as context. Do not synthesize a final answer yet.
			 		Target repository URL: %s
			 		Local repository path: %s
			 		Previous conversation: %s
			 		""".formatted(repoUrl,localPath,history))
			 .user(query)
			 .call()
			 .content();
		StringBuilder fullResponse=new StringBuilder();
		return streamingChatClient.prompt()
				.system("""
		                You are an expert AI Software Engineer.
		                Synthesize a detailed response to the user's question based strictly on the retrieved code context.
		                
		                CRITICAL REQUIREMENT:
		                Whenever you explain logic or reference a component, cite the exact file path, class name, and method name using the format:
		                `[FilePath > ClassName # methodName()]`
		                
		                Code Context:
		                %s
		                """.formatted(retrievedContext))
				.user(query)
				.stream()
				.content()
				.doOnNext(fullResponse::append)
				.doOnComplete(()->memoryService.saveMessage(conversationId,"AGENT", fullResponse.toString()));
 }
}
