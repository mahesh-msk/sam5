package com.faiveley.samng.principal.sm.fabriques;

import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ATableAssociationComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AssociationCompParcoursComp;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Data;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Entete;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableEvtVar;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.data.FabriqueParcoursExplorer;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:03
 */
public class FabriqueParcours extends AFabriqueParcoursAbstraite {	
	private static FabriqueParcours instance = new FabriqueParcours();
	
	protected FabriqueParcours(){}
	
	/**
	 * @return
	 */
	public static FabriqueParcours getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return FabriqueParcoursExplorer.getInstance();
		}
		
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear() {
		if (parcours != null) {
			parcours.clear();
			parcours = null;
		}
	}
	
	/**
	 * An array of Message[] and contains an array of good messages, and an array of bad messages
	 * @param messages
	 */
	public AParcoursComposant creerData(Message[][] messages) {
		Data data = new Data();
		if(messages != null && messages.length > 0) {
			Enregistrement enr = new Enregistrement();
			
			for (Message msg : messages[0]) {
				enr.ajouter(msg);				
			}
			
			for (Message msg : messages[1]) {
				enr.ajouterBadMessage(msg);
			}
			
			data.setEnregistrement(enr);		
		}
		
		this.parcours.setDatas(data);
	
		return data;
	}

	/**
	 * Créer une instance de la classe Entete et ce qui la compose :
	 * ADescripteurComposant
	 * 
	 * @param descripteur
	 * @param donneesEntete
	 */
	public AParcoursComposant creerEntete(ADescripteurComposant descripteur, AParcoursComposant donneesEntete) {
		Entete entete = new Entete();
		entete.ajouterDescripteur(descripteur);
		entete.ajouter(donneesEntete);
		
		// Add Entete to the Parcours
		if (this.parcours != null) {
			this.parcours.setEntete(entete);
		} else {
			System.err.println("first create the parcours");
		}
		return entete;
	}

	/**
	 * Crééer une instance de la classe ParcoursComposite
	 * 
	 */
	public AParcoursComposant creerParcours() {
		this.parcours = new ParcoursComposite();
		
		return this.parcours;
	}
		
	/**
	 * 
	 * @param descripteursEvt
	 */
	public AParcoursComposant creerTableEvtVar(ADescripteurComposant descripteursEvt) {
		TableEvtVar tableEvtVar = new TableEvtVar();
		ATableAssociationComposant tableAssoc = new AssociationCompParcoursComp();
		tableEvtVar.setM_ATableAssociationComposant(tableAssoc);
		tableEvtVar.ajouterDescripteur(descripteursEvt);
		
		if (this.parcours != null) {
			this.parcours.setTableAssos(tableEvtVar);
		} else {
			System.err.println("First create the parcours");
		}
		
		return tableEvtVar;
	}
	
	public AParcoursComposant creerInfoFichier(AParcoursComposant infos) {
		try {
			this.parcours.setInfo(infos);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		return infos;
	}

	public AParcoursComposant creerReperes(AParcoursComposant reperes) {
		this.parcours.setReperes(reperes);
		
		return reperes;
	}
}