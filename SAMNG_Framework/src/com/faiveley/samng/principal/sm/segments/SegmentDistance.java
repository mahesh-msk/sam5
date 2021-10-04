package com.faiveley.samng.principal.sm.segments;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 07-déc.-2007 11:32:49
 * Classe de gestion d'un segment de temps c'est à dire l'intervalle de messages  
 */
public class SegmentDistance extends ASegment{

	//initial and corrected diameter
	private double diameterCorrige;
	private double diameterInitial;
	

	//start and end time
	private String initialTime;
	private String endTime;

	public SegmentDistance(){
	}

	/**
	 * Returns the corrected diameter
	 * @return the diameterCorrige
	 */
	public double getDiameterCorrige() {
		return this.diameterCorrige;
	}


	/**
	 * Stes the corrected diameter
	 * @param distanceCorrige the distanceCorrige to set
	 */
	public void setDiameterCorrige(double distanceCorrige) {
		this.diameterCorrige = distanceCorrige;
	}


	/**
	 * Gets the initial diameter
	 * @return the diameterInitial
	 */
	public double getInitialDiameter() {
		return this.diameterInitial;
	}

	/**
	 * Sets the initial diameter
	 * @param distanceInitial the distanceInitial to set
	 */
	public void setInitialDiameter(double distanceInitial) {
		this.diameterInitial = distanceInitial;
	}
	
	/**
	 * Returns the time of the end message
	 * @return the endTime
	 */
	public String getEndTime() {
		return this.endTime;
	}

	/**
	 * Sets the time of the end message 
	 * @param endTime the endTime to set
	 */
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	/**
	 * Returns the time of the start message
	 * @return the initialTime
	 */
	public String getInitialTime() {
		return this.initialTime;
	}

	/**
	 * Sets the time of the start message
	 * @param initialTime the initialTime to set
	 */
	public void setInitialTime(String initialTime) {
		this.initialTime = initialTime;
	}	
}