package com.agent.engineering_agent.service;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import com.agent.engineering_agent.entity.CodeRepository;
import com.agent.engineering_agent.repository.CodeRepositoryRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeRepositoryService {
  private final CodeRepositoryRepository repository;
  private final GitService gitService;
  public CodeRepository registerAndClone(String repoUrl,String branch,String localPath) {
	  CodeRepository codeRepository=CodeRepository.builder()
			  						.repoUrl(repoUrl)
			  						.branch(branch)
			  						.localPath(localPath)
			  						.status("CLONING")
			  						.createdAt(LocalDateTime.now())
			  						.build();
	  codeRepository=repository.save(codeRepository);
	  try {
		  gitService.cloneRepository(repoUrl, branch, localPath);
		  codeRepository.setStatus("READY");
		  }
	  catch(Exception e) {
		  codeRepository.setStatus("Failed");
		  e.printStackTrace();
	  }
	  return repository.save(codeRepository);
  }
}
