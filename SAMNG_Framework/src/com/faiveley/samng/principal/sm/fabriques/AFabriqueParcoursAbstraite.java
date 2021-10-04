package com.faiveley.samng.principal.sm.fabriques;

import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:10
 */
public abstract class AFabriqueParcoursAbstraite {

	protected ParcoursComposite parcours;
	
	//protected Semaphore semaphore = new Semaphore(1, true);

	
	/**
	 * 
	 * @param messages
	 */
	public abstract AParcoursComposant creerData(Message[][] messages);
	
	/**
	 * retourne instance de la classe Entete et ce qui la compose :
	 * ADescripteurComposant
	 * 
	 * @param descripteur
	 * @param donneesEntete
	 */
	public abstract AParcoursComposant creerEntete(ADescripteurComposant descripteur, AParcoursComposant donneesEntete);

	/**
	 * Crééer une instance de la classe ParcoursComposite
	 * 
	 */
	public abstract AParcoursComposant creerParcours();

	/**
	 * 
	 * @param descripteursEvt
	 */
	public abstract AParcoursComposant creerTableEvtVar(ADescripteurComposant descripteursEvt);

	
	public abstract AParcoursComposant creerInfoFichier(AParcoursComposant infos);
	public abstract AParcoursComposant creerReperes(AParcoursComposant reperes);
	
	public ParcoursComposite getParcours(){
		return this.parcours;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setParcours(ParcoursComposite newVal){
		this.parcours = newVal;
	}

}