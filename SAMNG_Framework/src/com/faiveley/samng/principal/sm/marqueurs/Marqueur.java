package com.faiveley.samng.principal.sm.marqueurs;

/**
 * @author olivier
 * @version 1.0
 * @created 06-déc.-2007 11:12:42
 */
public class Marqueur extends AMarqueur {

	private String commentaire;

	public Marqueur(){

	}

	public String getCommentaire(){
		return this.commentaire;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCommentaire(String newVal){
		this.commentaire = newVal;
	}
}