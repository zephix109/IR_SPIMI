package model;

/**
 * The Class Token.
 */
public class Token {
	
	/** The term. */
	private String term;
	
	/** The doc id. */
	private String docId;
	
	/** The position. */
	private String position;
	
	/**
	 * Gets the position.
	 *
	 * @return the position
	 */
	public String getPosition() {
		return position;
	}
	
	/**
	 * Sets the position.
	 *
	 * @param position the new position
	 */
	public void setPosition(String position) {
		this.position = position;
	}
	
	/**
	 * Gets the term.
	 *
	 * @return the term
	 */
	public String getTerm() {
		return term;
	}
	
	/**
	 * Sets the term.
	 *
	 * @param term the new term
	 */
	public void setTerm(String term) {
		this.term = term;
	}
	
	/**
	 * Gets the doc id.
	 *
	 * @return the doc id
	 */
	public String getDocId() {
		return docId;
	}
	
	/**
	 * Sets the doc id.
	 *
	 * @param docId the new doc id
	 */
	public void setDocId(String docId) {
		this.docId = docId;
	}
	
}
