package query;

import java.util.HashMap;
import java.util.Map;

public class Rater {
	
	Map<String, Integer> documentLengthTable = new HashMap<String, Integer>();
	int numberOfDocuments = 0;
	double averageLengthOfDoc;
	
	static final double k1 = 1.5;
	static final double b = 0.75;
	
	public double bmScore (Map<String, Integer> documentLengthTable, int numberOfDocuments, double averageLengthOfDoc) {
		
		return 0;
	}

}
