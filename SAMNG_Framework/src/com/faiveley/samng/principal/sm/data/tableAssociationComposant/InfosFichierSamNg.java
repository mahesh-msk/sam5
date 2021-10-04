package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

/**
 * classe associant un parcours binaire à son fichier XML associé
 * @author Graton Olivier
 * @version 1.0
 * @created 29-oct.-2007 15:27:14
 */
public class InfosFichierSamNg extends AParcoursComposant {

	private String CRCFichierXML;
	/**
	 * nom du fichier de parcours binaire
	 */
	private String nomFichierParcoursBinaire;
	/**
	 * nom du fichier XML
	 */
	private String nomFichierXml;
	
	private String versionXML;
	
	private String titreProjet;
	
	private String numplan;
	
	private TempResolutionEnum tempResolution = TempResolutionEnum.RESOLUTION_0_001;

	public InfosFichierSamNg(){

	}

	public String getCRCFichierXML(){
		return this.CRCFichierXML;
	}

	/**
	 * Gets the absolute path
	 * nom du fichier de parcours binaire
	 */
	public String getNomFichierParcoursBinaire(){
		return this.nomFichierParcoursBinaire;
	}

	/**
	 * nom du fichier XML
	 */
	public String getNomFichierXml(){
		return this.nomFichierXml;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCRCFichierXML(String newVal){
		this.CRCFichierXML = newVal;
	}

	/**
	 * nom du fichier de parcours binaire
	 * 
	 * @param newVal
	 */
	public void setNomFichierParcoursBinaire(String newVal){
		this.nomFichierParcoursBinaire = newVal;
	}

	/**
	 * nom du fichier XML
	 * 
	 * @param newVal
	 */
	public void setNomFichierXml(String newVal){
		this.nomFichierXml = newVal;
	}

	/**
	 * @return the numplan
	 */
	public String getNumplan() {
		return this.numplan;
	}

	/**
	 * @param numplan the numplan to set
	 */
	public void setNumplan(String numplan) {
		this.numplan = numplan;
	}

	/**
	 * @return the titreProjet
	 */
	public String getTitreProjet() {
		return this.titreProjet;
	}

	/**
	 * @param titreProjet the titreProjet to set
	 */
	public void setTitreProjet(String titreProjet) {
		this.titreProjet = titreProjet;
	}

	/**
	 * @return the versionXML
	 */
	public String getVersionXML() {
		return this.versionXML;
	}

	/**
	 * @param versionXML the versionXML to set
	 */
	public void setVersionXML(String versionXML) {
		this.versionXML = versionXML;
	}
	
	/**
	 * Sets the temp resolution
	 * 
	 * @param tempRes
	 */
	public void setTempResolution(TempResolutionEnum tempRes) {
		this.tempResolution = tempRes;
	}
	
	/**
	 * Returns the temp resolution
	 * @return
	 */
	public TempResolutionEnum getTempResolution() {
		return this.tempResolution;
	}

	
}