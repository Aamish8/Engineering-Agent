package com.agent.engineering_agent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableAsync
@SpringBootApplication
public class EngineeringAgentApplication {
	public static void main(String[] args) {
		SpringApplication.run(EngineeringAgentApplication.class, args);
	}
}
