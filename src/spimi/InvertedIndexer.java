package spimi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.StringTokenizer;

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

	/**
	 * Spimi invert.
	 *
	 * @param rootFolder the root folder
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public File spimiInvert (File rootFolder) throws IOException {
		
		String blockSavingPath = "";
		
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
			
			dictionary = new HashMap<String, List<String>>();
			
			for(File file : newsFiles) {
				
				String fileID = file.getName();
				
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String line;
				
				
				while ((line = reader.readLine()) != null) {
				
					StringTokenizer tokenizer = new StringTokenizer(line);
					while(tokenizer.hasMoreTokens()) {
						
						String currentToken = tokenizer.nextToken();
						
						if(dictionary.containsKey(currentToken)) {
							
							if(!dictionary.get(currentToken).contains(fileID)) {
								dictionary.get(currentToken).add(fileID);
							}
							
						} else {
							List<String> newPostingsList = new ArrayList<String>();
							newPostingsList.add(fileID);
							dictionary.put(currentToken, newPostingsList);
							
						}
					}
				}
				
				reader.close();
				
			}
			
			blockSavingPath = writeBlockToDisk(dictionary, folder.getName());
		}
		
		return new File(blockSavingPath);
	}
	
	/**
	 * Write block to disk.
	 *
	 * @param dictionary the dictionary
	 * @param blockName the block name
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public String writeBlockToDisk(Map<String, List<String>> dictionary, String blockName) throws IOException {
		
		String savePath = System.getProperty("user.dir") + File.separator + "blocks" +  File.separator;

		
        if (!(new File(savePath)).exists()) {	
        	
    		new File(savePath).mkdirs();
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
        
        
		Files.write(Paths.get(savePath + blockName + ".block"), sb.toString().getBytes(), StandardOpenOption.CREATE);
		
		return savePath;

	}
	
	
}
