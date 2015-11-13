package query;

public class Rater {
	
	private static Rater instance = new Rater();
	
	private Rater() {}
	
	public static Rater getInstance() {
		if(instance == null) {
			instance = new Rater();
		}
		return instance;
	}
	
	
	static final double k1 = 1.5;
	static final double b = 0.75;
	
	public double bmScore (double documentLength, int numberOfDocuments, double averageLengthOfDoc, 
			double documentFrequency, double termFrequency) {
		
		
		double numerator = (k1 + 1) * termFrequency;
		
		
		double denominator = k1 * ((1 - b) + b * (documentLength /averageLengthOfDoc)) + termFrequency;
		

		
		double score = Math.log(numberOfDocuments / documentFrequency) * (numerator/denominator);
		
		return score;
		
		
	}

}
