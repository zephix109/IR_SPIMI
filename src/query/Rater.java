package query;

/**
 * The Class Rater.
 */
public class Rater {
	
	/** The instance. */
	private static Rater instance = new Rater();
	
	/**
	 * Instantiates a new rater.
	 */
	private Rater() {}
	
	/**
	 * Gets the single instance of Rater.
	 *
	 * @return single instance of Rater
	 */
	public static Rater getInstance() {
		if(instance == null) {
			instance = new Rater();
		}
		return instance;
	}
	
	
	/** The Constant k1. */
	static final double k1 = 1.5;
	
	/** The Constant b. */
	static final double b = 0.75;
	
	/**
	 * Bm score.
	 *
	 * @param documentLength the document length
	 * @param numberOfDocuments the number of documents
	 * @param averageLengthOfDoc the average length of doc
	 * @param documentFrequency the document frequency
	 * @param termFrequency the term frequency
	 * @return the double
	 */
	public double bmScore (double documentLength, int numberOfDocuments, double averageLengthOfDoc, 
			double documentFrequency, double termFrequency) {
		
		
		double numerator = (k1 + 1) * termFrequency;
		
		
		double denominator = k1 * ((1 - b) + b * (documentLength /averageLengthOfDoc)) + termFrequency;
		

		
		double score = Math.log(numberOfDocuments / documentFrequency) * (numerator/denominator);
		
		return score;
		
		
	}

}
