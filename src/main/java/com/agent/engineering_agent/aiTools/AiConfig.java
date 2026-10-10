package com.agent.engineering_agent.aiTools;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class AiConfig {
  @Bean
  public ChatClient chatClient(ChatClient.Builder builder,CodebaseTools codebaseTools
		  ,GitTools gitTool) {
	  return builder
			  .defaultSystem("""
	                    You are an autonomous AI Software Engineer.
	                    Use the available tools to inspect source code
	                    and solve user requests.
	                    	 """)
			  .defaultTools(codebaseTools,gitTool)
			  
			  .build();
  }
  @Bean
  public ChatClient streamingChatClient(ChatClient.Builder builder) {
      return builder
              .defaultSystem("You are a helpful assistant.")
              .build();
  }
}
