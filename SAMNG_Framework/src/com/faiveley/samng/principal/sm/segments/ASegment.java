package com.faiveley.samng.principal.sm.segments;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 24-déc.-2007 16:41:22
 */
public abstract class ASegment {

	//start and end messages ids
	private int endMsgId;
	private int startMsgId;
	
	//segment number
	private int numeroSegment;
	
	//segment valid
	private boolean valide;
	
	//segment has corrections
	private boolean hasCorrections = false;

	/**
	 * Return boolean value to say if the segment is valid 
	 * @return	is valid
	 */
	public boolean isValide() {
		return this.valide;
	}

	/**
	 * Sets the flag to valid or not
	 * @param valide
	 */
	public void setValide(boolean valide) {
		this.valide = valide;
	}

	public ASegment(){

	}

	/**
	 * Return the id of the end message
	 * @return	id end
	 */
	public int getEndMsgId(){
		return this.endMsgId;
	}

	/**
	 * Return the number of the segment
	 * @return	segment number
	 */
	public int getNumeroSegment(){
		return this.numeroSegment;
	}

	/**
	 * Returns the id of the start message
	 * @return	id start
	 */
	public int getStartMsgId(){
		return this.startMsgId;
	}

	/**
	 * Sets the id of the end message
	 * @param newVal	id
	 */
	public void setEndMsgId(int newVal){
		this.endMsgId = newVal;
	}

	/**
	 * Sets the number of the segment 
	 * @param newVal	nsgement number
	 */
	public void setNumeroSegment(int newVal){
		this.numeroSegment = newVal;
	}

	/**
	 * Sets the id of the start message
	 * @param newVal	id
	 */
	public void setStartMsgId(int newVal){
		this.startMsgId = newVal;
	}

	/**
	 * @return the hasCorrections
	 */
	public boolean hasCorrections() {
		return this.hasCorrections;
	}

	/**
	 * Sets flag to say if the current segment has any corrections made
	 * @param hasCorrections the hasCorrections to set
	 */
	public void setHasCorrections(boolean hasCorrections) {
		this.hasCorrections = hasCorrections;
	}

	
}