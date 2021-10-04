package com.faiveley.samng.principal.sm.data.identificateurComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:12
 */
public abstract class AIdentificateurComposant {

	protected int code;
	protected String nom;

	public AIdentificateurComposant(){

	}

	/**
	 * @return
	 */
	public int getCode(){
		return this.code;
	}

	/**
	 * @return
	 */
	public String getNom(){
		return this.nom;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCode(int newVal){
		this.code = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNom(String newVal){
		this.nom = newVal;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("id = ").append(this.code)
		//.append(", name = ").append(nom)
		.append("; ");
		return buf.toString();
	}
}