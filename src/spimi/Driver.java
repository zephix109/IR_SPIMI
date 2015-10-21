package spimi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * The Class Driver.
 */
public class Driver {
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void main(String[] args) throws IOException {
		
		System.out.println("Input Folder: ");
		
		Scanner sc = new Scanner(System.in);
		String rootPath = sc.nextLine();
		
		File root = new File(rootPath);

	  
	    
	    //Split files
	    System.out.print("Splitting Files...");
	    File[] files = root.listFiles(new FilenameFilter() {
	    	@Override
	    	public boolean accept(File dir, String name) {
	            return name.toLowerCase().endsWith(".sgm");
	        }
	    });
		
	    for(File file : files) {
	    	DocumentSplitter.getInstance().parseSgmFile(file);
	    }
	    System.out.println("done!");
	    
	    //Construct block inverted index by SPIMI
	    System.out.print("Building inveted index...");
	    String savePath = System.getProperty("user.dir") + File.separator + "reuters" + File.separator;
	    
		File blocks = InvertedIndexer.getInstance().spimiInvert(new File(savePath));
	    System.out.println("done!");

		//Merge blocks 
		System.out.print("Merging blocks...");
		Map<String, List<String>> dictionary = mergeBlocks(blocks);
	    System.out.println("done!");
	    System.out.println();
	    
	    //Remove stop words
	    dictionary = removeStopWords(dictionary, 100);

		//Query
		System.out.println("------------Dictionary Ready For Query------------");
		System.out.println("Number Of Terms = " + dictionary.size());
		int numOfPostings = 0;
		for(List<String> postingsList :dictionary.values()) {
			numOfPostings += postingsList.size();
		}
		System.out.println("Number Of Postings = " + numOfPostings  );
		System.out.println("--------------------------------------------------");
		

		Boolean flag = true;
		while(flag) {
			
			System.out.println("Please input your query:");
			
			String originalInput = sc.nextLine();
			String query = originalInput.toLowerCase();
			System.out.println("------------Query for term [" + originalInput + "]--------------");

			if(dictionary.containsKey(query)) {
				System.out.println(dictionary.get(query).size() + " documents contains term " + "["+originalInput  +"];");
				System.out.println("Found term in these documents(ID):");
				System.out.println(dictionary.get(query));
				System.out.println("--------------------------------------------------");

			} else {
				System.out.println("Cannot find term in dictionary!");
				System.out.println("--------------------------------------------------");

			}			
		}	
		sc.close();		
	}
	
	/**
	 * Merge blocks.
	 *
	 * @param blocks the blocks
	 * @return the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Map<String, List<String>> mergeBlocks(File blocks) throws IOException {
		
		Map<String, List<String>> finalDictionary = new HashMap<String, List<String>>();

		File[] blockList = blocks.listFiles(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return name.toLowerCase().endsWith(".block");
			  }
		});
		
		for(File block : blockList) {
			
			Map<String, List<String>> subDictionary = new HashMap<String, List<String>>();

			
			BufferedReader reader = new BufferedReader(new FileReader(block)); 
			
			String line;
			
			
			while ((line = reader.readLine()) != null) {
				
				String[] tuple = line.split(":");
	
				List<String> postingsList = new ArrayList<String>(Arrays.asList(tuple[1].split(",")));
				subDictionary.put(tuple[0], postingsList);	
				
			}
			
			for(Map.Entry<String, List<String>> entry : subDictionary.entrySet()) {
				
				if(finalDictionary.containsKey(entry.getKey())) {
					finalDictionary.get(entry.getKey()).addAll(entry.getValue());
				} else {
					finalDictionary.put(entry.getKey(), entry.getValue());
				}	
			}
			reader.close();
		}
		return finalDictionary;		
	}
	
	/**
	 * Removes the stop words.
	 * Gets the stop words by sorting postings' lists by their sizes. 
	 *
	 * @param dic the dict
	 * @param number the number
	 * @return the map
	 */
	public static Map<String, List<String>> removeStopWords(Map<String, List<String>> dict, int number) {
		
		Map<List<String>, String> reverseMap = new HashMap<List<String>, String>();
		
		List<List<String>> tempList = new ArrayList<List<String>>();
		
		int id = 0;
		for(Map.Entry<String, List<String>> entry : dict.entrySet()) {
			
			
			entry.getValue().add(String.valueOf(id));
			
			id++;
			reverseMap.put(entry.getValue(), entry.getKey());
			tempList.add(entry.getValue());
		}
		
		Collections.sort(tempList, new Comparator<List<String>>(){
			@Override
			public int compare(List<String> o1, List<String> o2) {
				return o2.size() - o1.size();
			}
		});
				
		
		
		for(int i=0; i< number; i++) {
			List<String> listToRemove = tempList.get(i);
			String keyToRemove = reverseMap.get(listToRemove);
			reverseMap.remove(listToRemove);
			dict.remove(keyToRemove);
		}
		
		return dict;
		
	}

}
