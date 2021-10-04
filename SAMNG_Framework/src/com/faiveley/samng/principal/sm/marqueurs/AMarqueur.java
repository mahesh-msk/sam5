package com.faiveley.samng.principal.sm.marqueurs;

/**
 * @author olivier
 * @version 1.0
 * @created 06-déc.-2007 11:12:38
 */
public abstract class AMarqueur {

	protected int idMessage;
	protected String nom;
	

	public AMarqueur(){

	}

	public void finalize() throws Throwable {

	}

	public int getIdMessage(){
		return idMessage;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setIdMessage(int newVal){
		idMessage = newVal;
	}

	public String getNom(){
		return this.nom;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNom(String newVal){
		this.nom = newVal;
	}
}