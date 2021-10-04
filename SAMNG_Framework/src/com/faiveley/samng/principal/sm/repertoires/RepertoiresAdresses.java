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
		profil=INSTALL_SAM_PARAM+"profil/";
		profil_vide=profil+nomprofil_vide;
		ressources=INSTALL_SAM_PARAM+"ressources";
		logs=INSTALL_SAM_PARAM+"logs";
		logs_parser_log_TXT=logs+"/parser_log.txt";
		temp=INSTALL_SAM_PARAM+"temp";
		temp_captTemp_JPG=temp+"/captTemp.jpg";
		temp_directory=temp+"/work_directory.properties";
		
		FLAGS_FILE_DIR = ressources+"\\flags\\";

		xml=ressources+"/xml";

		ConfigurationJRU_XML=xml+"/ConfigurationJRU";

		langues=ressources+"/langues";

		languages_PROPERTIES=langues+"/languages.properties";
		languagesNaming_PROPERTIES=langues+"/languagesNaming.properties";
		defaultLanguage_PROPERTIES=langues+"/defaultLanguage.properties";

		bridage=ressources+"/bridage";
		IdConfig_PROPERTIES=bridage+"/IdConfig.properties";
		missions_PROPERTIES=bridage+"/missions.properties";
		FICPARAM_TYP_ENGIN_XML=bridage+"/FICPARAM_TYP_ENGIN.xml";
		filtresdefautsproperties=bridage+"/filtreDefautTabulaire.properties";
		filtresproperties=bridage+"/filtres.properties";

		JRU=ressources+"/JRU";
		JRU_liste_erreurs_bloquantes_JRU=JRU+"/liste_erreurs_bloquantes_JRU.properties";
		JRU_liste_tailleEntete_JRU=JRU+"/liste_tailleEntete_JRU.properties";
		JRU_liste_versions_JRU=JRU+"/liste_versions_JRU.properties";
		
		views=ressources+"/views";
		viewsSynchronizationProperties=views+"/viewsSynchronization.properties";
		
		atess=ressources+"/atess";
		atessDistanceProperties=atess+"/atessDistance.properties";
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
		return getProfil_actuel()+"/configuration-vues/";
	}
	
	public static String getConfigurationVueMultimedia() {
		File multimediaFolder = new File(getProfil() + "MULTIMEDIA/");
		
		if (!multimediaFolder.exists()) {
			multimediaFolder.mkdir();
		}
		
		return getProfil() + "MULTIMEDIA/configuration_vue_multimedia.xml";
	}
	
	public static String getConfigurationVuesAnnotations_XML(){
		return getConfigurationVues()+"configuration_vue_annotations.xml";
	}
	
	public static String getFiltres(){
		return getProfil_actuel()+"/filtres";
	}

	public static String getFiltres_GraphiquesXML(){
		return getFiltres()+"/filtres_graphique"+"/filtres_graphique.xml";
	}
	
	public static String getFiltres_ListesXML(){
		return getFiltres()+"/filtres_listes"+"/filtres_liste.xml";
	}
	
	public static String getFiltres_TabulairesXML(){
		return getFiltres()+"/filtres_tabulaire"+"/filtres_tabulaire.xml";
	}
	
	public static String getVBVs_XML(){
		return getProfil_actuel()+"/VBV/"+"vbvs.xml";
	}

	public static final String corrections=config+"/corrections";
	public static final String correctionsXML="_corrections.xml"; //OK

	public static final String graphic_colors=config+"/graphic_colors";
	public static final String graphic_colorsXML=graphic_colors+"/graphic_colors.xml"; //OK
	
	public static final String marqueurs=config+"/marqueurs";
	public static final String marqueursXML="_marqueurs.xml"; //OK
	
	public static final String user=config+"/user";
	public static final String user_PROPERTIES=user+"/user.properties"; //OK
	
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
