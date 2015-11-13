package model;

import java.util.List;

/**
 * The Class Posting.
 */
public class Posting {
	
	/** The doc id. */
	private String docId;
	
	/** The positions. */
	private List<String> positions;
	
	
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
	
	/**
	 * Gets the positions.
	 *
	 * @return the positions
	 */
	public List<String> getPositions() {
		return positions;
	}
	
	/**
	 * Sets the positions.
	 *
	 * @param positions the new positions
	 */
	public void setPositions(List<String> positions) {
		this.positions = positions;
	}
	
	

}
