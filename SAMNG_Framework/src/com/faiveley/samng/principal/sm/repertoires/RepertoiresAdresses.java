package com.faiveley.samng.principal.sm.repertoires;

import java.io.File;

public class RepertoiresAdresses {

	private static String nom_profil_actuel="";

	public static String RepertoireTravail="";
	
	public static String INSTALL_SAM_PARAM="";
	
	public static String doc="";
	
	public static String profil="";
	public static String profil_vide="";
	
	public static String ressources="";
	
	public static String logs="";
	public static String logs_parser_log_TXT="";

	public static String temp="";
	public static String temp_captTemp_JPG="";
	public static String temp_directory="";
	
	public static String FLAGS_FILE_DIR="";

	public static String xml="";

	public static String ConfigurationJRU_XML="";

	public static String langues="";

	public static String languages_PROPERTIES="";
	public static String languagesNaming_PROPERTIES="";
	public static String defaultLanguage_PROPERTIES="";
	public static String missions_PROPERTIES="";

	public static String bridage=ressources="";
	public static String IdConfig_PROPERTIES="";
	public static String FICPARAM_TYP_ENGIN_XML="";
	public static String filtresdefautsproperties="";
	public static String filtresproperties="";

	public static String JRU="";
	public static String JRU_liste_erreurs_bloquantes_JRU="";
	public static String JRU_liste_tailleEntete_JRU="";
	public static String JRU_liste_versions_JRU="";
	
	public static String views="";
	public static String viewsSynchronizationProperties="";
	
	public static String atess="";
	public static String atessDistanceProperties="";
	
	public static void creerPath(String PathINSTALL_SAM_PARAM){
		File user=new File(PathINSTALL_SAM_PARAM);
		if (!user.exists()) {
			user.mkdirs();
		}
	}
	
	public static void initPaths(String PathINSTALL_SAM_PARAM){
		creerPath(PathINSTALL_SAM_PARAM);
		INSTALL_SAM_PARAM=PathINSTALL_SAM_PARAM;
		doc=INSTALL_SAM_PARAM+"doc";
		profil=INSTALL_SAM_PARAM+"profil" + File.separator;
		profil_vide=profil+nomprofil_vide;
		ressources=INSTALL_SAM_PARAM+"ressources";
		logs=INSTALL_SAM_PARAM+"logs";
		logs_parser_log_TXT=logs+ File.separator + "parser_log.txt";
		temp=INSTALL_SAM_PARAM+"temp";
		temp_captTemp_JPG=temp+ File.separator+"captTemp.jpg";
		temp_directory=temp+ File.separator + "work_directory.properties";
		
		FLAGS_FILE_DIR = ressources+File.separator+"flags"+File.separator;

		xml=ressources+ File.separator + "xml";

		ConfigurationJRU_XML=xml+ File.separator + "ConfigurationJRU";

		langues=ressources+ File.separator + "langues";

		languages_PROPERTIES=langues+ File.separator + "languages.properties";
		languagesNaming_PROPERTIES=langues+ File.separator + "languagesNaming.properties";
		defaultLanguage_PROPERTIES=langues+ File.separator + "defaultLanguage.properties";

		bridage=ressources+ File.separator + "bridage";
		IdConfig_PROPERTIES=bridage+ File.separator + "IdConfig.properties";
		missions_PROPERTIES=bridage+ File.separator + "missions.properties";
		FICPARAM_TYP_ENGIN_XML=bridage+ File.separator + "FICPARAM_TYP_ENGIN.xml";
		filtresdefautsproperties=bridage+ File.separator + "filtreDefautTabulaire.properties";
		filtresproperties=bridage+ File.separator + "filtres.properties";

		JRU=ressources+ File.separator + "JRU";
		JRU_liste_erreurs_bloquantes_JRU=JRU+ File.separator + "liste_erreurs_bloquantes_JRU.properties";
		JRU_liste_tailleEntete_JRU=JRU+ File.separator + "liste_tailleEntete_JRU.properties";
		JRU_liste_versions_JRU=JRU+ File.separator + "liste_versions_JRU.properties";
		
		views=ressources+ File.separator + "views";
		viewsSynchronizationProperties=views+ File.separator + "viewsSynchronization.properties";
		
		atess=ressources+ File.separator + "atess";
		atessDistanceProperties=atess+ File.separator + "atessDistance.properties";
	}
	
	public static final String config="config";
	
	public static final String nomprofil_vide="_empty";
	
	public static final String configurationvuesgraphiques_CFG="_graphique_cfg.xml";
	public static final String configurationvueslistes_CFG="_liste_cfg.xml";
	public static final String configurationvuesdetaillee_CFG="_vue_detaillee_cfg.xml";
	public static final String configurationvuestabulaires_CFG="_tabulaire_cfg.xml";
	
	public static String getProfil_actuel(){
		return getProfil()+getNom_profil_actuel();
	}
		
	public static String getConfigurationVues(){
		return getProfil_actuel()+ File.separator + "configuration-vues"+ File.separator;
	}
	
	public static String getConfigurationVueMultimedia() {
		File multimediaFolder = new File(getProfil() + "MULTIMEDIA"+ File.separator);
		
		if (!multimediaFolder.exists()) {
			multimediaFolder.mkdir();
		}
		
		return getProfil() + "MULTIMEDIA" + File.separator + "configuration_vue_multimedia.xml";
	}
	
	public static String getConfigurationVuesAnnotations_XML(){
		return getConfigurationVues()+"configuration_vue_annotations.xml";
	}
	
	public static String getFiltres(){
		return getProfil_actuel()+ File.separator + "filtres";
	}

	public static String getFiltres_GraphiquesXML(){
		return getFiltres()+ File.separator + "filtres_graphique"+ File.separator + "filtres_graphique.xml";
	}
	
	public static String getFiltres_ListesXML(){
		return getFiltres()+ File.separator + "filtres_listes"+ File.separator + "filtres_liste.xml";
	}
	
	public static String getFiltres_TabulairesXML(){
		return getFiltres()+ File.separator + "filtres_tabulaire"+ File.separator + "filtres_tabulaire.xml";
	}
	
	public static String getVBVs_XML(){
		return getProfil_actuel()+ File.separator + "VBV"+ File.separator + "vbvs.xml";
	}

	public static final String corrections=config+ File.separator + "corrections";
	public static final String correctionsXML="_corrections.xml"; //OK

	public static final String graphic_colors=config+ File.separator + "graphic_colors";
	public static final String graphic_colorsXML=graphic_colors+ File.separator + "graphic_colors.xml"; //OK
	
	public static final String marqueurs=config+ File.separator + "marqueurs";
	public static final String marqueursXML="_marqueurs.xml"; //OK
	
	public static final String user=config+ File.separator + "user";
	public static final String user_PROPERTIES=user+ File.separator + "user.properties"; //OK
	
	public static String getProfil() {
		return profil;
	}
	
	public static String getNom_profil_actuel() {
		return nom_profil_actuel;
	}
	public static void setNom_profil_actuel(String nom_profil_actuel) {
		RepertoiresAdresses.nom_profil_actuel = nom_profil_actuel;
	}
}
