package com.agent.engineering_agent.service;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.patch.FormatError;
import org.eclipse.jgit.revwalk.RevCommit;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class GitService {
 public void cloneRepository(String repoUrl,String branch,String localPath) throws InvalidRemoteException, TransportException, GitAPIException {
	 File destination=new File(localPath);
	 Git.cloneRepository()
	 .setURI(repoUrl)
	 .setBranch(branch)
	 .setDirectory(destination)
	 .call()
	 .close();
 }
 public RevCommit getLatestCommit(String localPath) throws IOException, NoHeadException, GitAPIException {
	 Git git=Git.open(new File(localPath));
	 return git.log().setMaxCount(1).call().iterator().next();
 }
 public String getLatestCommitDiff(String localPath) throws IOException, NoHeadException, GitAPIException {
	 Git git=Git.open(new File(localPath));
	 RevCommit latestCommit=git.log().setMaxCount(1).call().iterator().next();
	 if(latestCommit.getParentCount()==0)return "Latest commit is the initial commit. No previous commit to compare";
	 ByteArrayOutputStream output=new ByteArrayOutputStream();
	  RevCommit parentCommit=latestCommit.getParent(0);
	  DiffFormatter formatter=new DiffFormatter(output);
	  formatter.setRepository(git.getRepository());
	  List<DiffEntry>diffs=formatter.scan(parentCommit.getTree(),latestCommit.getTree());
	  for(DiffEntry diff:diffs) {
		  formatter.format(diff);
	  }
	  return output.toString(StandardCharsets.UTF_8);
 }
}
