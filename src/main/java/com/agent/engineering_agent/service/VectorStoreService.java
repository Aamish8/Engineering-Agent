package com.agent.engineering_agent.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import com.agent.engineering_agent.dto.AstCodeChunk;

import lombok.RequiredArgsConstructor;
import java.util.*;

@Service
@RequiredArgsConstructor
public class VectorStoreService {
  private final VectorStore vectorStore;
  public void vectorStore(List<AstCodeChunk>chunks,String repoUrl,String filePath){
	  List<Document>documents=new ArrayList<>();
	  for(int i=0;i<chunks.size();i++) {
		  AstCodeChunk chunk=chunks.get(i);
		  Map<String,Object>metadata=Map.of(
				  "repoUrl",repoUrl,
				  "filePath",filePath,
				  "className",chunk.getClassName(),
				  "methodName",chunk.getMethodName(),
				  "chunkIndex",i
				  );
		  Document doc=new Document(chunk.getCode(),metadata);
		  documents.add(doc);
	  }
	  vectorStore.add(documents);
  }
  public List<Document> similaritySearch(String query,String repoUrl,int topK){
	   SearchRequest.Builder request=SearchRequest.builder()
			   .query(query)
			   .topK(topK);
	   if(repoUrl!=null&&!repoUrl.isBlank()) {
		   request.filterExpression("repoUrl == '" + repoUrl + "'");
	   }
	  return vectorStore.similaritySearch(request.build());
  }
}
