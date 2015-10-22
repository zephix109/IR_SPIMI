package spimi;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * The Class InvertedIndexer.
 */
public class InvertedIndexer {
	
	/** The instance. */
	public static InvertedIndexer instance;
	
	/**
	 * Instantiates a new inverted indexer.
	 */
	private InvertedIndexer() {}
	
	/**
	 * Gets the single instance of InvertedIndexer.
	 *
	 * @return single instance of InvertedIndexer
	 */
	public static InvertedIndexer getInstance() {
		if(instance == null) {
			instance = new InvertedIndexer();
		}
		return instance;
	}

	/** The dictionary. */
	private Map<String, List<String>> dictionary;
	
	private int outputFileId = 0;
	

	/**
	 * Spimi invert.
	 *
	 * @param rootFolder the root folder
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File spimiInvert(TokenStream tokenStream) throws IOException {
		
		
		dictionary = new HashMap<String, List<String>>();
		
		long freeMemory = 100000;
		
		while(freeMemory >0) {
			
			freeMemory--;
			
			if(tokenStream.hasNextToken()) {
				Token token = tokenStream.nextToken();

				if(dictionary.containsKey(token.term)) {
					if(!dictionary.get(token.term).contains(token.docId)) {
						dictionary.get(token.term).add(token.docId);
					}
				} else {
					List<String> newPostingsList = new ArrayList<String>();
					newPostingsList.add(token.docId);
					dictionary.put(token.term, newPostingsList);
				}
			}
			
		}
		
		File output = writeBlockToDisk(dictionary, String.valueOf(outputFileId));
		outputFileId ++;
		
		return output;
		
	}

	
	/**
	 * Write block to disk.
	 *
	 * @param dictionary the dictionary
	 * @param blockName the block name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File writeBlockToDisk(Map<String, List<String>> dictionary, String blockName) throws IOException {
		
		String blockPath = System.getProperty("user.dir") + File.separator + "blocks" +  File.separator;

		
        if (!(new File(blockPath)).exists()) {	
        	
    		new File(blockPath).mkdirs();
        }
        
        StringBuilder sb = new StringBuilder(); 
        
        for(Map.Entry<String, List<String>> entry: dictionary.entrySet()) {
        	sb.append(entry.getKey() + ":");
        	
    		StringJoiner sj = new StringJoiner(",","",System.getProperty("line.separator"));
        	for(String s : entry.getValue()) {
        		sj.add(s.replace(".news", "")); 			
        	}
        	sb.append(sj);
        }
        
        String savePath = blockPath + blockName + ".block";
        
		Files.write(Paths.get(savePath), sb.toString().getBytes(), StandardOpenOption.CREATE);
		
		return new File(savePath);

	}
	
	
}
