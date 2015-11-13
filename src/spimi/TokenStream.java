package spimi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.StringTokenizer;

import model.Token;

public class TokenStream {
	
	public static TokenStream instance;
	
	
	private TokenStream() {}
	
	public static TokenStream getInstance() {
		if(instance == null) {
			instance = new TokenStream();
		}
		return instance;
	}
	
	
	private Queue<Token> tokenQueue = new LinkedList<Token>();
	private File rootFolder;
	private Map<String, Integer> documentLengthTable = new HashMap<String, Integer>();
	
	public void initialize(File rootFolder) throws IOException {
		
		this.setRootFolder(rootFolder);
		
		File[] folders = rootFolder.listFiles(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return new File(current, name).isDirectory();
			  }
		});
		
		for(File folder : folders) {
			File[] newsFiles = folder.listFiles(new FilenameFilter() {
				  @Override
				  public boolean accept(File current, String name) {
				    return name.toLowerCase().endsWith(".news");
				  }
			});
			
			for(File file : newsFiles) {
				
				int currentPosition = 0;
				
				String fileId = file.getName();
				
				
				
				int documentLength = 0;
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				
				
				while ((line = reader.readLine()) != null) {
				
					StringTokenizer tokenizer = new StringTokenizer(line);
					while(tokenizer.hasMoreTokens()) {
						
						String currentToken = tokenizer.nextToken();
						
						Token newToken = new Token();
						newToken.setTerm(currentToken);
						newToken.setDocId(fileId);
						newToken.setPosition(String.valueOf(currentPosition));
						
						currentPosition ++;
						tokenQueue.add(newToken);
						
						documentLength ++;
						
						
					}
				}
				reader.close();
				
				documentLengthTable.put(fileId.replace(".news", ""), documentLength);
			}
			
		}
		
	}
	
	public Map<String, Integer> getDocumentLengthTable() {
		return documentLengthTable;
	}

	public void setDocumentLengthTable(Map<String, Integer> documentLengthTable) {
		this.documentLengthTable = documentLengthTable;
	}

	public Token nextToken() {
			
		

		return tokenQueue.poll();
	}
	
	public boolean hasNextToken() {
		if(tokenQueue.isEmpty()) {
			return false;
		}
		return true;
	}

	public File getRootFolder() {
		return rootFolder;
	}

	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}
	
}
