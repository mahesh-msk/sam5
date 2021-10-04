package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

/**
 * CL_C_2  : Le premier octet de l’entête définit le format de codage du fichier
 * de parcours.
 * CL_1 : Les deux octets 2 et 3 de l’entête définissent sa taille (n).
 * CL_2 : L’octet 4 de l’entête définit le nombre d’octets (x) pour coder le nom
 * du fichier XML.
 * CL_3 : Les x octets 5 à  5+x de l’entête définissent le nom du fichier XML.
 * CL_4 : Les deux octets 5+x+1 et 5+x+2 de l’entête définissent la taille des
 * blocs (n) utilisée pour l’enregistrement des messages.
 * @author Oiry Hervé
 * @version 1.0
 * @created 02-oct.-2007 13:10:56
 */
public class Entete extends AParcoursComposant {

	/**
	 * CRC de l'entete sur 2 octets(2 denriers de l'entete)
	 */
	private short CRC;
	private byte formatCodage;
	/**
	 * nom du fichier XML en octet sur x octets
	 */
	private String nomFichierXML;
	/**
	 * taille en octet de l'entete: 2 octets
	 */
	private short tailleEntete;
	/**
	 * taille en octets du nom de fichier xml sur un octet
	 */
	private byte tailleNomFichierXML;

	public Entete(){

	}

	public short getCRC(){
		return this.CRC;
	}

	public byte getFormatCodage(){
		return this.formatCodage;
	}

	/**
	 * nom du fichier XML en octet sur x octets
	 */
	public String getNomFichierXML(){
		return this.nomFichierXML;
	}

	/**
	 * taille en octet de l'entete
	 */
	public short getTailleEntete(){
		return this.tailleEntete;
	}

	public byte getTailleNomFichierXML(){
		return this.tailleNomFichierXML;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCRC(short newVal){
		this.CRC = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFormatCodage(byte newVal){
		this.formatCodage = newVal;
	}

	/**
	 * nom du fichier XML en octet sur x octets
	 * 
	 * @param newVal
	 */
	public void setNomFichierXML(String newVal){
		this.nomFichierXML = newVal;
	}

	/**
	 * taille en octet de l'entete
	 * 
	 * @param newVal
	 */
	public void setTailleEntete(short newVal){
		this.tailleEntete = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setTailleNomFichierXML(byte newVal){
		this.tailleNomFichierXML = newVal;
	}

}