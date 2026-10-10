package com.agent.engineering_agent.aiTools;

import java.io.IOException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import com.agent.engineering_agent.service.GitService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class GitTools {
  private final GitService gitService;
  @Tool(description = """
          Analyze the latest Git commit in a repository.
          Use this tool when the user asks about the latest commit,
          recent changes, modified files, added code, or deleted code.
          """)
  public String getLatestCommitDiff(
		  @ToolParam(description="Local path of the latest cloned git repository")String localPath) throws NoHeadException, IOException, GitAPIException {
	 return  gitService.getLatestCommitDiff(localPath);
  }
}
