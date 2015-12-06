package query;

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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringJoiner;

import model.Posting;
import spimi.DocumentSplitter;
import spimi.InvertedIndexer;
import spimi.TokenStream;

// TODO: Auto-generated Javadoc
/**
 * The Class Driver.
 */
public class Driver {
	
	/** The document length table. */
	static Map<String, Integer> documentLengthTable = new HashMap<String, Integer>();
	
	/** The number of documents. */
	static int numberOfDocuments = 0;
	
	/** The average length of doc. */
	static double averageLengthOfDoc;
	
	
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

	  
	    
//	    //Split files
//	    System.out.print("Splitting Files...");
//	    File[] files = root.listFiles(new FilenameFilter() {
//	    	@Override
//	    	public boolean accept(File dir, String name) {
//	            return name.toLowerCase().endsWith(".sgm");
//	        }
//	    });
//		
//	    for(File file : files) {
//	    	
//	    	int numberOfDocumentsInSgm = DocumentSplitter.getInstance().parseSgmFile(file);
//	    	
//	    	numberOfDocuments += numberOfDocumentsInSgm;
//	    	
//	    }
//	    
//	    System.out.println("done!");
	    
	    //Construct block inverted index by SPIMI
	    System.out.print("Building inveted index...");
	    
	    TokenStream stream = TokenStream.getInstance();
	    stream.initialize(root);
	    while(stream.hasNextToken()) {
	    	InvertedIndexer indexer = InvertedIndexer.getInstance();
	    	indexer.spimiInvert(stream);
	    }
	    
	    documentLengthTable = stream.getDocumentLengthTable();
	    numberOfDocuments = stream.getNumberOfDocuments();
	    
	    long sumOfDocumentLength = 0;
	    
	    for(int i : documentLengthTable.values()) {
	    	sumOfDocumentLength += i;
	    }
	    
	    averageLengthOfDoc = sumOfDocumentLength / numberOfDocuments;
	    
	    System.out.println("done!");

		//Merge blocks 
		System.out.print("Merging blocks...");
		
		String blockPath = System.getProperty("user.dir") + File.separator + "blocks" +  File.separator;
		Map<String, List<Posting>> dictionary = mergeBlocks(new File(blockPath));
	    System.out.println("done!");
	    System.out.println();
	    
	    
	    //Remove stop words
//	    dictionary = removeStopWords(dictionary, 10);
	    


		//Query
		System.out.println("------------Dictionary Ready For Query------------");
		System.out.println("Number Of Terms = " + dictionary.size());
		int numOfPostings = 0;
		for(List<Posting> postingsList :dictionary.values()) {
			numOfPostings += postingsList.size();
		}
		System.out.println("Number Of Postings = " + numOfPostings  );
		System.out.println("--------------------------------------------------");
		

		Boolean flag = true;
		
		while(flag) {
			
			System.out.println();
			System.out.println("Please input your query:");
			
			String originalInput = sc.nextLine();
			String query = originalInput.toLowerCase();
			query = query.replaceAll("[\\p{Punct}]", " ");
			query = query.replaceAll("\\d"," ");
			System.out.println(query);
			
			
			System.out.println("------------Query for term [" + originalInput + "]--------------");
			
			rankedQuery(dictionary, query);
			
			System.out.println("--------------------------------------------------");


//			String[] querySplit = query.split("\\s+");
//
//			if(querySplit.length > 1) {
//				if(!phraseQuery(dictionary, querySplit)){
//					System.out.println("cannot find");
//				}
//			} else {
//				wordQuery(dictionary, query);
//			}
					
		}	
		sc.close();		
	}
	
	
	/**
	 * Ranked query.
	 *
	 * @param dictionary the dictionary
	 * @param query the query
	 */
	public static void rankedQuery(Map<String, List<Posting>> dictionary, String query) {
		
		System.out.println(query);
		
		String[] querySplit = query.split("\\s+");
		
		Map<String, Double> rankedResult = new HashMap<String, Double>();
		
		for(String word : querySplit) {
			
			if(dictionary.containsKey(word)) {
				
				List<Posting> postingList = dictionary.get(word);
				
				double documentFrequency = postingList.size();
				
				for(Posting posting : postingList) {
					
					String docId = posting.getDocId().trim();
					
					double score = Rater.getInstance().bmScore(documentLengthTable.get(docId), numberOfDocuments,
							documentFrequency, averageLengthOfDoc, posting.getPositions().size());
					
					if(rankedResult.containsKey(docId)) {
						
						double newScore = score + rankedResult.get(docId);
						rankedResult.put(docId, newScore);
					} else {
						
						rankedResult.put(docId, score);
					}
					
				}	
				
			}
			
		}
		
		//Sort result	
		if(rankedResult == null || rankedResult.isEmpty()) {
			
			System.out.println("Can not find any document related to the query.");
			
		} else {
			Map<String, Double> sortedResult = sortMapByValue(rankedResult);
			
			System.out.println();
			System.out.println("Totally " + sortedResult.size() + " relevent documents found." );

			//Print out top 20 result
			List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>> (sortedResult.entrySet());
			
			int numberOfResult;
			
			if(entryList.size() < 20) {
				numberOfResult = entryList.size();
			} else {
				numberOfResult = 20;
			}
			
	
			
			System.out.println("The top " + numberOfResult + " ranked documents are: ");
			
			for(int i=0; i<numberOfResult; i++) {
				System.out.println("Document ID: " + entryList.get(i).getKey() + ", Score:  " + entryList.get(i).getValue());
			}
		
//			// Print out all result
//			for(Map.Entry<String, Double> entry : sortedResult.entrySet()) {
//				
//				
//				System.out.println("Document ID: " + entry.getKey() + ", Score:  " + entry.getValue());
//
//				
//			}

		
		
		}
		
		
	}
	
	/**
	 * Phrase query.
	 *
	 * @param dictionary the dictionary
	 * @param querySplit the query split
	 * @return true, if successful
	 */
	public static boolean phraseQuery(Map<String, List<Posting>> dictionary, String[] querySplit) {
		
		List<String> resultList = new ArrayList<String>();
		
		List<Map<String, List<String>>> phraseQueryList = new ArrayList<Map<String, List<String>>>();
		
		for(String s : querySplit) {
			if(!dictionary.containsKey(s)) {

				return false;
			} else {
				
				Map<String, List<String>> postingMap = new HashMap<String, List<String>>();
				
				for(Posting posting : dictionary.get(s)) {
					postingMap.put(posting.getDocId(), posting.getPositions());
				}

				phraseQueryList.add(postingMap);
				
			}
		}
			
		Map<String, List<String>> beginWordMap = phraseQueryList.get(0);
		
		for(Map.Entry<String, List<String>> entry : beginWordMap.entrySet()) {
			
			String beginWordDocId = entry.getKey();
			
			boolean flag = true;
			
			for(int i=1; i<phraseQueryList.size(); i++) {
				if(!phraseQueryList.get(i).containsKey(beginWordDocId)) {
					flag = false;
				}
			}
			
			if(flag) {
				
				for(String position : entry.getValue()) {
					int beginPosition = Integer.valueOf(position);
					
					for(int i=1; i<phraseQueryList.size(); i++) {
						
						
						for(Map.Entry<String, List<String>> otherEntry : phraseQueryList.get(i).entrySet()) {
							if(otherEntry.getKey().equalsIgnoreCase(beginWordDocId)) {
								
								if(!otherEntry.getValue().contains(String.valueOf((beginPosition + i)))) {
									flag = false;
								} 
							}
							
						}
						
					}
				}
			}
			
			
			if(flag) {
				
				resultList.add(beginWordDocId);
			}	
		}	
		
		if(resultList.size() <= 0) {

			return false;
		} else {
			System.out.print(resultList);
		}
		
		return true;
	}

	/**
	 * Word query.
	 *
	 * @param dictionary the dictionary
	 * @param query the query
	 */
	public static void wordQuery(Map<String, List<Posting>> dictionary, String query) {
		
		if(dictionary.containsKey(query)) {
			
			System.out.println(dictionary.get(query).size() + " documents contains term " + "["+query  +"];");
			System.out.println("Found term in these documents(ID):");
			
			List<Posting> postingList = dictionary.get(query);
			
			StringJoiner sj = new StringJoiner(",", "[", "]");
			for(Posting p : postingList) {
				sj.add(p.getDocId());
			}
			System.out.println(sj);
			System.out.println("--------------------------------------------------");

		} else {
			System.out.println("Cannot find term in dictionary!");
			System.out.println("--------------------------------------------------");

		}	
		
	}

	/**
	 * Merge blocks.
	 *
	 * @param blocks the blocks
	 * @return the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static Map<String, List<Posting>> mergeBlocks(File blocks) throws IOException {
		
		Map<String, List<Posting>> finalDictionary = new HashMap<String, List<Posting>>();

		File[] blockList = blocks.listFiles(new FilenameFilter() {
			  @Override
			  public boolean accept(File current, String name) {
			    return name.toLowerCase().endsWith(".block");
			  }
		});
		
		for(File block : blockList) {
			
			Map<String, List<Posting>> subDictionary = new HashMap<String, List<Posting>>();

			
			BufferedReader reader = new BufferedReader(new FileReader(block)); 
			
			String line;
			
			
			while ((line = reader.readLine()) != null) {
				
				String[] element = line.split(":");
	
				String[] postingSplit = element[1].split(",");
				
				List<Posting> postingList = new ArrayList<Posting>();

				
				for(String postingString : postingSplit) {
					
					Posting posting = new Posting();
					
					
					String[] docIdSplit = postingString.split("@");
					
					String[] positionSplit = docIdSplit[1].split("-");
					
					List<String> positionList = new ArrayList<String>(Arrays.asList(positionSplit));

					
					posting.setDocId(docIdSplit[0]);
					posting.setPositions(positionList);	
					
					postingList.add(posting);
					
				}			
				
				subDictionary.put(element[0], postingList);	
				
			}
			
			for(Map.Entry<String, List<Posting>> entry : subDictionary.entrySet()) {
				
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
	 * @param dict the dict
	 * @param number the number
	 * @return the map
	 */
	public static Map<String, List<Posting>> removeStopWords(Map<String, List<Posting>> dict, int number) {
		
		Map<List<Posting>, String> reverseMap = new HashMap<List<Posting>, String>();
		
		List<List<Posting>> tempList = new ArrayList<List<Posting>>();
		
		int id = 0;
		
		for(Map.Entry<String, List<Posting>> entry : dict.entrySet()) {
			
			Posting posting = new Posting();
			posting.setDocId(String.valueOf(id));
			
			
			entry.getValue().add(posting);
			
			id++;
			
			reverseMap.put(entry.getValue(), entry.getKey());
			tempList.add(entry.getValue());
		}
		
		Collections.sort(tempList, new Comparator<List<Posting>>(){
			@Override
			public int compare(List<Posting> o1, List<Posting> o2) {
				return o2.size() - o1.size();
			}
		});
				
		
		
		for(int i=0; i< number; i++) {
			List<Posting> listToRemove = tempList.get(i);
			String keyToRemove = reverseMap.get(listToRemove);
			reverseMap.remove(listToRemove);
			dict.remove(keyToRemove);
		}
		
		return dict;
		
	}
	
	/**
	 * Sort map by value.
	 *
	 * @param map the map
	 * @return the map
	 */
	public static Map<String, Double> sortMapByValue(Map<String, Double> map) {
		
		if (map == null || map.isEmpty()) {
            return null;
        }
		
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
        List<Map.Entry<String, Double>> entryList = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(entryList, new MapValueComparator());
 
        Iterator<Map.Entry<String, Double>> iter = entryList.iterator();
        Map.Entry<String, Double> tmpEntry = null;
        while (iter.hasNext()) {
            tmpEntry = iter.next();
            sortedMap.put(tmpEntry.getKey(), tmpEntry.getValue());
        }
        return sortedMap;
	}
	
	/**
	 * The Class MapValueComparator.
	 */
	static class MapValueComparator implements Comparator<Map.Entry<String, Double>> {
		 
	    /**
    	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    	 */
    	@Override
	    public int compare(Entry<String, Double> me1, Entry<String, Double> me2) {
	 
	        return me2.getValue().compareTo(me1.getValue());
	    }
	}

}
