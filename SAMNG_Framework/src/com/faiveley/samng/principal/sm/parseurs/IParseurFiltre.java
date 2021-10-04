package com.faiveley.samng.principal.sm.parseurs;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

/**
 * @author Administrateur
 * @version 1.0
 * @created 23-nov.-2007 15:50:23
 */
public interface IParseurFiltre extends IParseurInterface {



	public  AFiltreComposant chargerFiltres();

	/**
	 * 
	 * @param filtre
	 */
	public  boolean enregistrerFiltre(AFiltreComposant filtre);

	/**
	 * méthode permettant de charger le fichier xml et l'instancier sous forme d'objet
	 * représentant le fichier
	 * 
	 * @param chemin
	 */
	public  void parseRessource(String chemin,boolean explorer,int deb,int fin);

}