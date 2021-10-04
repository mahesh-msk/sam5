package com.faiveley.samng.principal.ihm.vues.configuration;

/**
 * Classe de gestion des colonnes pour les vues
 * Son role est :
 * - de stocker en m�moire JVM la configuration des colonnes d�finie par
 * l'utilisateur via la fenetre "Gestion des colonnes"
 * - d'enregistrer la configuration des colonnes  via le parseur xml:
 * ParseurConfigurationVue
 * - de stocker en m�moire JVM le filtre appliqu� sur la vue via la fenetre
 * "Gestion des filtres"
 * - d'enregistrer le filtre appliqu�  via le parseur xml:
 * ParseurConfigurationVue
 * Est instanci� en tatn que singleton et mis en tatn qu'attribut priv� dans
 * l'Activator de chaque plugin
 * @author olivier
 * @version 1.0
 * @updated 06-d�c.-2007 11:16:01
 */
public abstract class AGestionnaireConfigurationVue {

	/**
	 * d�fini si on utilise les noms courts ou les noms longs
	 */
	protected boolean nomsCourts;
	
	/**
	 * d�fini quel est le filtre applique sur la vue
	 * 
	 */
	protected String filtreApplique;
	

	public AGestionnaireConfigurationVue(){

	}

	public void finalize() throws Throwable {

	}
	
	public boolean usesShortNames() {
		return nomsCourts;
	}
	
	public void setUsesShortNames(boolean value) {
		this.nomsCourts = value;
	}

	public String getFiltreApplique(){
		return filtreApplique;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFiltreApplique(String newVal){
		filtreApplique = newVal;
	}

}