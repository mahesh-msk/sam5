package com.faiveley.samng.principal.ihm.vues.vuemarqueurs.configuration;


import java.util.HashMap;

import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireVueMarqueursExplorer;
import com.faiveley.samng.principal.sm.parseurs.ParseurConfigurationVueAnnotation;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * 
 * @author Olivier
 *
 */
public class GestionnaireVueMarqueurs {

	private HashMap<String,String> mapNomUtilisateurNomUnique;
	private static GestionnaireVueMarqueurs instance = new GestionnaireVueMarqueurs();
	public HashMap<String,Integer> mapColonneLargeur;
	private ParseurConfigurationVueAnnotation parseurConfig;


	//	private static String fichierConfigVueAnnotation;

	public void init(){
		mapNomUtilisateurNomUnique = new HashMap<String, String>();
		mapColonneLargeur=new HashMap<String, Integer>();
		parseurConfig = new ParseurConfigurationVueAnnotation();
	}

	/**
	 * Private constructor. Sigleton class
	 */
	protected GestionnaireVueMarqueurs(){

	}

	/** Suppression de l'instance */
	public void clear(){
		mapNomUtilisateurNomUnique=null;
		if (mapColonneLargeur!=null){
			mapColonneLargeur.clear();
			mapColonneLargeur=null;
		}
		parseurConfig=null;
		//		fichierConfigVueAnnotation=null;
		//		try {
		//			this.finalize();
		//		} catch (Throwable e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		//		//CHECK01
	}


	/**
	 * Méthode de récupération du singleton`
	 * @return
	 */
	public static GestionnaireVueMarqueurs getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireVueMarqueursExplorer.getInstance();
		}
		return instance;
	}

	/**
	 * Méthode de chargement des largeurs de colonne
	 * @throws AExceptionSamNG
	 */
	public void chargerLargeurColonnes() throws AExceptionSamNG{
		if(parseurConfig==null)
			parseurConfig = new ParseurConfigurationVueAnnotation();

		parseurConfig.parseRessource(RepertoiresAdresses.getConfigurationVuesAnnotations_XML(),false,0,-1);

		if(this.parseurConfig.vueAnnotation!=null && this.mapColonneLargeur.size()==0)
			this.mapColonneLargeur = parseurConfig.chargerLargeurColonnes();
	}


	/**
	 * Méthode d'enregistrement des largeurs de colonne
	 * @throws AExceptionSamNG
	 */
	public void enregistrerLargeurColonnes() throws AExceptionSamNG{
		if(parseurConfig==null)
			parseurConfig = new ParseurConfigurationVueAnnotation();

		//		if(fichierConfigVueAnnotation!=null)
		try {
			parseurConfig.parseRessource(RepertoiresAdresses.getConfigurationVuesAnnotations_XML(),false,0,-1);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(this.parseurConfig.vueAnnotation!=null && this.mapColonneLargeur!=null){
			parseurConfig.enregistrerLargeurColonnes(this.mapColonneLargeur);
		}
	}

	public HashMap<String, Integer> getMapColonneLargeur() {
		return mapColonneLargeur;
	}

	public void setMapColonneLargeur(HashMap<String, Integer> mapColonneLargeur) {
		this.mapColonneLargeur = mapColonneLargeur;
	}

	public HashMap<String, String> getMapNomUtilisateurNomUnique() {
		return mapNomUtilisateurNomUnique;
	}

	public void setMapNomUtilisateurNomUnique(
			HashMap<String, String> mapNomUtilisateurNomUnique) {
		this.mapNomUtilisateurNomUnique = mapNomUtilisateurNomUnique;
	}




}
