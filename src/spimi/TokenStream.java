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

/**
 * The Class TokenStream.
 */
public class TokenStream {
	
	/** The instance. */
	public static TokenStream instance;
	
	
	/**
	 * Instantiates a new token stream.
	 */
	private TokenStream() {}
	
	/**
	 * Gets the single instance of TokenStream.
	 *
	 * @return single instance of TokenStream
	 */
	public static TokenStream getInstance() {
		if(instance == null) {
			instance = new TokenStream();
		}
		return instance;
	}
	
	
	/** The token queue. */
	private Queue<Token> tokenQueue = new LinkedList<Token>();
	
	/** The root folder. */
	private File rootFolder;
	
	/** The document length table. */
	private Map<String, Integer> documentLengthTable = new HashMap<String, Integer>();
	
	/**
	 * Initialize.
	 *
	 * @param rootFolder the root folder
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
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
	
	/**
	 * Gets the document length table.
	 *
	 * @return the document length table
	 */
	public Map<String, Integer> getDocumentLengthTable() {
		return documentLengthTable;
	}

	/**
	 * Sets the document length table.
	 *
	 * @param documentLengthTable the document length table
	 */
	public void setDocumentLengthTable(Map<String, Integer> documentLengthTable) {
		this.documentLengthTable = documentLengthTable;
	}

	/**
	 * Next token.
	 *
	 * @return the token
	 */
	public Token nextToken() {
			
		

		return tokenQueue.poll();
	}
	
	/**
	 * Checks for next token.
	 *
	 * @return true, if successful
	 */
	public boolean hasNextToken() {
		if(tokenQueue.isEmpty()) {
			return false;
		}
		return true;
	}

	/**
	 * Gets the root folder.
	 *
	 * @return the root folder
	 */
	public File getRootFolder() {
		return rootFolder;
	}

	/**
	 * Sets the root folder.
	 *
	 * @param rootFolder the new root folder
	 */
	public void setRootFolder(File rootFolder) {
		this.rootFolder = rootFolder;
	}
	
}
