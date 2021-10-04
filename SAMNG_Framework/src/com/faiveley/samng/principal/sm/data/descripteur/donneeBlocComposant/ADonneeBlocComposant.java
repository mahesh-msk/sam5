package com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:05
 */
public abstract class ADonneeBlocComposant {

	/**
	 * nombre d'octets de la donnée
	 */
	protected short nbOctets;
	protected String type;
	protected Object valeur;

	public ADonneeBlocComposant(){

	}

	/**
	 * 
	 * @param donnee
	 */
	public void ajouter(ADonneeBlocComposant donnee){

	}

	/**
	 * 
	 * @param indice
	 */
	public ADonneeBlocComposant getEnfant(int indice){
		return null;
	}

	/**
	 * nombre d'octets de la donnée
	 */
	public short getNbOctets(){
		return nbOctets;
	}

	public String getType(){
		return type;
	}

	public Object getValeur(){
		return valeur;
	}

	/**
	 * nombre d'octets de la donnée
	 * 
	 * @param newVal
	 */
	public void setNbOctets(short newVal){
		nbOctets = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setType(String newVal){
		type = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setValeur(Object newVal){
		valeur = newVal;
	}

	/**
	 * 
	 * @param donnee
	 */
	public void supprimer(ADonneeBlocComposant donnee){

	}

}