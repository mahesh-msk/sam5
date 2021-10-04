package com.faiveley.samng.principal.sm.filtres;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireBaseFiltres;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:05
 */
public abstract class AFiltreComposant implements Comparable<AFiltreComposant> {

	private AFiltreComposant etatInitial;
	protected boolean filtrable;
	protected String nom;
	protected boolean selectionnable;
	private TypeFiltre type;
	private boolean choixVariable;
	public GestionnaireBaseFiltres m_GestionnaireFiltres;
	private boolean variableRenseigneeDansParcours=true;

	public AFiltreComposant(){

	}

	public AFiltreComposant(AFiltreComposant source) {
		this.etatInitial = source.etatInitial;
		this.filtrable = source.filtrable;
		this.m_GestionnaireFiltres = source.m_GestionnaireFiltres;
		this.nom = source.nom;
		this.selectionnable = source.selectionnable;
		this.type = source.type;
		this.choixVariable=source.choixVariable;
	}

	public void finalize() throws Throwable {

	}
	
	/**
	 * 
	 * @param filtreComp
	 */
	public abstract void ajouter(AFiltreComposant filtreComp);

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant getEnfant(int indice){
		return null;
	}
	
	public int getEnfantCount() {
		return 0;
	}

	public String getNom(){
		return nom;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNom(String newVal){
		nom = newVal;
	}

	/**
	 * 
	 * @param filtreComp
	 */
	public abstract void supprimer(AFiltreComposant filtreComp);
	
	public abstract void supprimer(int indice);


	public boolean isFiltrable(){
		return filtrable;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFiltrable(boolean newVal){
		filtrable = newVal;
	}

	/**
	 * 
	 * @param comp
	 */
	public abstract AFiltreComposant getEnfant(AFiltreComposant comp);

	/**
	 * Mise à jour liste évènements
	 * 
	 * @param BaseFiltreEV
	 * @param CourantEV
	 */
	public AFiltreComposant majListeEvenements(AFiltreComposant BaseFiltreEV, AFiltreComposant CourantEV){
		return null;
	}

	/**
	 * 
	 * @param CourantVAR
	 * @param BaseFiltreVAR
	 */
	public AFiltreComposant majListeVariables(AFiltreComposant CourantVAR, AFiltreComposant BaseFiltreVAR){
		return null;
	}

	public boolean isSelectionnable(){
		return selectionnable;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setSelectionnable(boolean newVal){
		selectionnable = newVal;
	}
	
	public TypeFiltre getFiltreType() { 
		return this.type;
	}

	public void setFiltreType(TypeFiltre filtreType) { 
		 this.type = filtreType;
	}
	
	public abstract void removeAll();
	
	public abstract AFiltreComposant clone();
	
	/**Comparaison de deux lignes de variable**/
	public int compareTo(AFiltreComposant ligneVar) {
		Langage langue = Activator.getDefault().getCurrentLanguage();
		
		String nomObj2 = ligneVar.getNom();
		nomObj2 = GestionnairePool.getInstance().getVariable(nomObj2).getDescriptor().getNomUtilisateur().getNomUtilisateur(langue);
		String nomObj1 = this.getNom();
		nomObj1 = GestionnairePool.getInstance().getVariable(nomObj1).getDescriptor().getNomUtilisateur().getNomUtilisateur(langue);
		return nomObj1.compareTo(nomObj2);
	}

	public boolean isChoixVariable() {
		return choixVariable;
	}

	public void setChoixVariable(boolean choixVariable) {
		this.choixVariable = choixVariable;
	}
	
	public boolean isVariableRenseigneeDansParcours() {
		return variableRenseigneeDansParcours;
	}

	public void setVariableRenseigneeDansParcours(
			boolean variableRenseigneeDansParcours) {
		this.variableRenseigneeDansParcours = variableRenseigneeDansParcours;
	}
}