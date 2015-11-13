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

import model.Posting;
import model.Token;

// TODO: Auto-generated Javadoc
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
	private Map<String, List<Posting>> dictionary;
	
	/** The output file id. */
	private int outputFileId = 0;
	

	/**
	 * Spimi invert.
	 *
	 * @param tokenStream the token stream
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File spimiInvert(TokenStream tokenStream) throws IOException {
		
		
		dictionary = new HashMap<String, List<Posting>>();
		
		long freeMemory = 10000;
		
		while(freeMemory >0) {
			
			freeMemory--;
			
			if(tokenStream.hasNextToken()) {
				
				Token token = tokenStream.nextToken();

				List<Posting> postingList;
				
				if(!dictionary.containsKey(token.getTerm())) {
					
					postingList = addToDictionary(dictionary, token.getTerm());
					
				} else {

					postingList = getPostingsList(dictionary, token.getTerm());
				}
				
				addToPostingsList(postingList, token);
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
	public File writeBlockToDisk(Map<String, List<Posting>> dictionary, String blockName) throws IOException {
		
		String blockPath = System.getProperty("user.dir") + File.separator + "blocks" +  File.separator;

		
        if (!(new File(blockPath)).exists()) {	
        	
    		new File(blockPath).mkdirs();
        }
        StringBuilder sb = new StringBuilder(); 
        
        for(Map.Entry<String, List<Posting>> entry: dictionary.entrySet()) {
        	sb.append(entry.getKey() + ":");
    		StringJoiner postingJoiner = new StringJoiner(",","",System.getProperty("line.separator"));
    		
        	for(Posting posting : entry.getValue()) {
        		
        		String s = posting.getDocId();
        		
        		StringJoiner positionJoiner = new StringJoiner("-", "@", "");
        		
        		for(String position : posting.getPositions()) {
        			positionJoiner.add(position);
        		}
        		
        		
        		postingJoiner.add(s.replace(".news", "") + positionJoiner.toString());		
        	}
        	
        	
        	sb.append(postingJoiner);
        }
        
        String savePath = blockPath + blockName + ".block";
        
		Files.write(Paths.get(savePath), sb.toString().getBytes(), StandardOpenOption.CREATE);
		
		return new File(savePath);

	}
	
	/**
	 * Adds the to dictionary.
	 *
	 * @param dictionary the dictionary
	 * @param term the term
	 * @return the list
	 */
	public List<Posting> addToDictionary(Map<String, List<Posting>> dictionary, String term) {
		
		List<Posting> newPostingList = new ArrayList<Posting>();

		dictionary.put(term, newPostingList);
		
		return newPostingList;
		
	}
	
	/**
	 * Gets the postings list.
	 *
	 * @param dictionary the dictionary
	 * @param term the term
	 * @return the postings list
	 */
	public List<Posting> getPostingsList(Map<String, List<Posting>> dictionary, String term) {
		
		return dictionary.get(term);	
	}
	
	/**
	 * Adds the to postings list.
	 *
	 * @param postingList the posting list
	 * @param token the token
	 */
	public void addToPostingsList(List<Posting> postingList , Token token) {
		
		if(docIdExists(postingList, token)) {
			for(Posting p : postingList) {
				if(p.getDocId().equalsIgnoreCase(token.getDocId())) {
					p.getPositions().add(token.getPosition());
				
				}
			}
		} else {
			Posting posting = new Posting();
			posting.setDocId(token.getDocId());
			
			List<String> positions = new ArrayList<String>();
			positions.add(token.getPosition());
			posting.setPositions(positions);
			postingList.add(posting);
		}	
		
		
	}
	
	/**
	 * Doc id exists.
	 *
	 * @param postingList the posting list
	 * @param token the token
	 * @return true, if successful
	 */
	public boolean docIdExists (List<Posting> postingList , Token token) {
		for(Posting p : postingList) {
			if(p.getDocId().equalsIgnoreCase(token.getDocId())) {
				return true;
			}
		}
		return false;
	}
}
