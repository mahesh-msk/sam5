package com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer;


public class ActivationExplorer  {


	private static ActivationExplorer instance = new ActivationExplorer();
	
	//si actif est à true alors le chargement d'un fichier est fait pour une exploration de fichier, pas pour une ouverture classique
	private boolean actif=false;
	private boolean ouvertureFichierPartielle=false;
	/**
	 * Private constructor. Sigleton class
	 */
	private ActivationExplorer(){

	}
	
	/**
	 * Méthode de récupération du singleton`
	 * @return
	 */
	public static ActivationExplorer getInstance(){
		return instance;
	}

	public boolean isActif() {
		return actif;
	}

//	public void setActif(boolean actif) {
//		this.actif = actif;
//	}

	public boolean isOuvertureFichierPartielle() {
		return ouvertureFichierPartielle;
	}

	public void setOuvertureFichierPartielle(boolean ouvertureFichierPartielle) {
		this.ouvertureFichierPartielle = ouvertureFichierPartielle;
	}
	
}