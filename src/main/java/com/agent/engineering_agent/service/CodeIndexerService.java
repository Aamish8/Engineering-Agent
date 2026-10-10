package com.agent.engineering_agent.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.agent.engineering_agent.dto.AstCodeChunk;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CodeIndexerService {
	private final VectorStoreService vectorStoreService;
	private final JdbcTemplate jdbc;
	@Transactional
	public void deleteExistingVectorsForRepo(String repoUrl) {
		String sql="DELETE FROM vector_store WHERE metadata->>'repoUrl'= ?";
		jdbc.update(sql,repoUrl);
	}
   public List<File> scanJavaFiles(String localPath) throws IOException{
	   try(Stream<Path>paths=Files.walk(Path.of(localPath))){
		 return paths.filter(Files::isRegularFile)
				 .filter(path->path.toString().endsWith(".java"))
				 .map(Path::toFile)
				 .collect(Collectors.toList()); 
	   }
   }
   public List<AstCodeChunk> chunkJavaFilesWithAST(File file) throws FileNotFoundException{
	   List<AstCodeChunk>chunks=new ArrayList<>();
	   CompilationUnit cu=StaticJavaParser.parse(file);
	   String packageName=cu.getPackageDeclaration().
			   map(pd->pd.getNameAsString())
			   .orElse("");
	   List<ClassOrInterfaceDeclaration>classes=cu.findAll(ClassOrInterfaceDeclaration.class);
	   for(ClassOrInterfaceDeclaration clazz:classes) {
		   String className=clazz.getNameAsString();
		   for(MethodDeclaration method:clazz.getMethods()) {
			   StringBuilder chunk=new StringBuilder();
			   if(!packageName.isEmpty()) {
				   chunk.append("Package ");
				   chunk.append(packageName);
				   chunk.append(";\n\n");
			   }
			   chunk.append("// Class: ");
			   chunk.append(className);
			   chunk.append("\n");
			   chunk.append(method.toString());
			   chunks.add(new AstCodeChunk(chunk.toString(),className,method.getNameAsString()));
		   }
		   if(clazz.getMethods().isEmpty()) {
			   StringBuilder chunk=new StringBuilder();
			   if(!packageName.isEmpty()) {
				   chunk.append("Package ").append(packageName).append(":\n\n");
			   }
			   chunk.append(clazz.toString());
			   chunks.add(new AstCodeChunk(chunk.toString(),className,"CLASS DECLARATION"));
		   }
	   }
	   return chunks;
   }
   @Async
   public void processAndIndexAync(String repoUrl,String localPath) throws IOException {
	   List<File> list = scanJavaFiles(localPath);
	   deleteExistingVectorsForRepo(repoUrl);
	   StringBuilder manifestBuilder=new StringBuilder();
	   manifestBuilder.append("Repository file manifest and inventory for repo: ").append(repoUrl).append("\n");
	   manifestBuilder.append("Total Java Files: ").append(list.size()).append("\n");
	   manifestBuilder.append("List of all Java classes/files in this repository: \n");
	   for(File file:list) {
		   manifestBuilder.append("- ").append(file.getName()).append("\n");
	   }
	   AstCodeChunk manifestCodeChunk=new AstCodeChunk(manifestBuilder.toString(),"RepositoryManifest","getFileInventory");
	   vectorStoreService.vectorStore(List.of(manifestCodeChunk), repoUrl, localPath+ "/MANIFEST");
	   for(File file:list) {
		   List<AstCodeChunk>chunk=chunkJavaFilesWithAST(file);
		  if(!chunk.isEmpty())vectorStoreService.vectorStore(chunk, repoUrl, file.getPath());
	   }
   }
}
