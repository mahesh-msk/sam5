package com.faiveley.samng.principal.sm.data.descripteur;

/**
 * CL_5 : L’octet 5+x+3 de l’entête définit le code Début de la table Data ;
 * CL_6 : L’octet 5+x+4 de l’entête définit le code Fin de la table Data
 * CL_7 : L’octet 5+x+5 de l’entête définit le code Continue Fin Bloc de la table
 * Data.
 * CL_8 : L’octet 5+x+6 de l’entête définit le code Continue Début Bloc de la
 * table Data.
 * CL_D_2  : L’octet 5+x+7 de l’entête définit le code Début Bloc Défaut de la
 * table Data.
 * CL_D_3  : L’octet 5+x+8 de l’entête définit le code Fin Bloc Défaut de la table
 * Data.
 * CL_D_4  : L’octet 5+x+9 de l’entête définit le code Continue Fin Bloc Défaut de
 * la table Data.
 * CL_D_5  : L’octet 5+x+10 de l’entête définit le code Continue Début Bloc Défaut
 * de la table Data.
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:39
 */
public class DescripteurCodeBloc extends ADescripteurComposant {

	private String codeContinuDebut;
	private String codeContinuDebutDefaut;
	private String codeContinuFin;
	private String codeContinuFinDefaut;
	private String codeDebut;
	private String codeFin;
	private String codeFinBloc;
	private String codeDebutDefaut;
	private String codeFinDefaut;

	public DescripteurCodeBloc(){

	}

	public String getCodeContinuDebut(){
		return this.codeContinuDebut;
	}

	public String getCodeContinuDebutDefaut(){
		return this.codeContinuDebutDefaut;
	}

	public String getCodeContinuFin(){
		return this.codeContinuFin;
	}

	public String getCodeContinuFinDefaut(){
		return this.codeContinuFinDefaut;
	}

	public String getCodeDebut(){
		return this.codeDebut;
	}

	public String getCodeFin(){
		return this.codeFin;
	}

	public String getCodeFinBloc(){
		return this.codeFinBloc;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeContinuDebut(String newVal){
		this.codeContinuDebut = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeContinuDebutDefaut(String newVal){
		this.codeContinuDebutDefaut = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeContinuFin(String newVal){
		this.codeContinuFin = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeContinuFinDefaut(String newVal){
		this.codeContinuFinDefaut = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeDebut(String newVal){
		this.codeDebut = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeFin(String newVal){
		this.codeFin = newVal;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setCodeFinBloc(String newVal){
		this.codeFinBloc = newVal;
	}

	/**
	 * @return the codeDebutDefaut
	 */
	public String getCodeDebutDefaut() {
		return this.codeDebutDefaut;
	}

	/**
	 * @param codeDebutDefaut the codeDebutDefaut to set
	 */
	public void setCodeDebutDefaut(String codeDebutDefaut) {
		this.codeDebutDefaut = codeDebutDefaut;
	}

	/**
	 * @return the codeFinDefaut
	 */
	public String getCodeFinDefaut() {
		return this.codeFinDefaut;
	}

	/**
	 * @param codeFinDefaut the codeFinDefaut to set
	 */
	public void setCodeFinDefaut(String codeFinDefaut) {
		this.codeFinDefaut = codeFinDefaut;
	}

}