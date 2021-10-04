package com.faiveley.samng.principal.sm.segments;

import com.faiveley.samng.principal.sm.calculs.ConversionTemps;


/**
 * @author Graton Olivier
 * @version 1.0
 * @created 07-déc.-2007 11:32:49
 * Classe de gestion d'un segment de temps c'est à dire l'intervalle de messages  
 */
public class SegmentTemps extends ASegment implements Comparable<SegmentTemps>{

	//the start, final and corrected time
	private String tempCorrige;
	private long tempInitial;
	private long tempFinal;

	//EFE : Ajout des attributs offsetDebut et offsetFin
	private int offsetDebut;
	private int offsetFin;

	public SegmentTemps(){
		setValide(true);
	}

	public int compareTo(SegmentTemps o) {
		Integer num1 =o.getNumeroSegment(); 
		Integer num2 =this.getNumeroSegment();

		return  -num1.compareTo(num2);
	}

	/**
	 * Returns the time of the end segment
	 * @return the temp
	 */

	public String getTempFinal() 
	{
		return ConversionTemps.getFormattedDate(this.tempFinal,true);
	}
	
	public long getTempFinalToLong() 
	{
		return this.tempFinal;
	}	

	/**
	 * Sets the time of the end segment
	 * @param temp the temp to set
	 */
	public void setTempFinal(long temp) 
	{
		this.tempFinal = temp;
	}

	/**
	 * Sets the corrected time 
	 * @param tempsCorrige the tempsCorrige to set
	 */
	public void setTempCorrige(String tempsCorrige) {
		this.tempCorrige = tempsCorrige;
	}

	/**
	 * Sets the time of the start message
	 * @param tempsInitial the tempsInitial to set
	 */
	public void setTempInitial(long tempsInitial) {
		this.tempInitial = tempsInitial;
	}

	/**
	 * Returns the corrected time
	 * @return the tempsCorrige
	 */
	public String getTempCorrige() {
		return this.tempCorrige;
	}

	/**
	 * Returns the time of the start message
	 * @return the tempsInitial
	 */
	
	public String getTempInitial() {
		return ConversionTemps.getFormattedDate(this.tempInitial, true);
	}
	
	public long getTempInitialToLong() {
		return this.tempInitial;
	}

	/**
	 * @return le offsetDebut
	 */
	public int getOffsetDebut() {
		return offsetDebut;
	}

	/**
	 * @param offsetDebut le offsetDebut à définir
	 */
	public void setOffsetDebut(int offsetDebut) {
		this.offsetDebut = offsetDebut;
	}
	
	/**
	 * @return le offsetFin
	 */
	public int getOffsetFin() {
		return offsetFin;
	}

	/**
	 * @param offsetFin le offsetFin à définir
	 */
	public void setOffsetFin(int offsetFin) {
		this.offsetFin = offsetFin;
	}	
}
