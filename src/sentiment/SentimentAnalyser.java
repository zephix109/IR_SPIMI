package sentiment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class SentimentAnalyser {
	
	private static SentimentAnalyser analyser = new SentimentAnalyser();
	private SentimentAnalyser() {};
	public static SentimentAnalyser getInstance() {
		if(analyser == null) {
			analyser = new SentimentAnalyser();
		}
		return analyser;
	}

	private HashMap<String, Integer> sentiment = new HashMap<String, Integer>(); 

    public int sentimentAnalysis(String token){
    	int resultScore=0;
		
		
		if(sentiment.containsKey(token)){
			resultScore +=sentiment.get(token);
		}			
		return resultScore;
    }
    
    public void getAfinn(String afinnData)throws FileNotFoundException, IOException{
  	  File file = new File(afinnData);
  	  BufferedReader reader = null;
  	  try {
  		  reader = new BufferedReader(new FileReader(file));
  		  String tempString = null; 		 
  		  String [] temp =null;
  		  while ((tempString = reader.readLine()) != null) { 
  			  String temp1 = new String();
  			  temp=tempString.split("\\s");  			 
  			  for(int i=0; i<temp.length-1;i++){
  				  temp1 += temp[i]+" ";
  			  }
  			  temp1= temp1.trim();
  			  sentiment.put(temp1, Integer.parseInt(temp[temp.length-1]));
  		  }
  		  reader.close();
  	  } catch (IOException e) {
  		  e.printStackTrace();
  	  } finally {
  		  if (reader != null) {
  			  try {
  				  reader.close();
  			  } catch (IOException e1) {
  			  }
  	      }
  	  }
    }

	public HashMap<String, Integer> getSentiment() {
		return sentiment;
	}

	public void setSentiment(HashMap<String, Integer> sentiment) {
		this.sentiment = sentiment;
	}
}
