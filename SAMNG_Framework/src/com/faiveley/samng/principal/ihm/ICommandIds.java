package com.faiveley.samng.principal.ihm;

/**
 * Interface defining the application's command IDs. Key bindings can be defined
 * for specific commands. To associate an action with a command, use
 * IAction.setActionDefinitionId(commandId).
 * 
 * @see org.eclipse.jface.action.IAction#setActionDefinitionId(String)
 */
public interface ICommandIds {
	/** */
	
	String CMD_EXP = "SAMNG.expand";
	String CMD_COLL = "SAMNG.collapse";
	String CMD_CONFIG_VUE_DETAILLEE = "SAMNG.config_vue_detaillee";
	String CMD_HELP = "SAMNG.help";
	
	String CMD_OPEN = "SAMNG.open";

	String CMD_OPEN_VUE_TEMP_COR = "SAMNG.openTempCorrection";
	
	String CMD_OPEN_VUE_DIST_COR = "SAMNG.openDistCorrection";
	
	String CMD_SET_REF = "SAMNG.setZeroRef";
	
	String CMD_OPEN_VBV = "SAMNG.openVBV";
	
	String CMD_OPEN_MARQUERS = "SAMNG.openMarquers";
	
	String CMD_OPEN_SEARCH_VAR = "SAMNG.openSearchVariable";
	String CMD_OPEN_SEARCH_EVENT = "SAMNG.openSearchEvent";
	String CMD_OPEN_SEARCH_INFILE = "SAMNG.openSearchInFile";
	String CMD_OPEN_SEARCH_MARQUER = "SAMNG.openSearchMarquer";
	
	String CMD_OPEN_COFIG_LIST = "SAMNG.openConfigListVue";
	
	String CMD_OPEN_COFIG_TABULAIRE = "SAMNG.openConfigTabulaireVue";
	
	String CMD_OPEN_VUE_BINAIRE = "SAMNG.openVueBinaire";
	String CMD_OPEN_VUE_GRAPHIQUE = "SAMNG.openVueGraphique";
	String CMD_OPEN_VUE_LIST = "SAMNG.openVueList";
	String CMD_OPEN_VUE_TABULAIRE = "SAMNG.openVueTabulaire";
	
	/** */
	String CMD_OPEN_MESSAGE = "SAMNG.openMessage";
	String CMD_CLOSE_MESSAGE = "SAMNG.closeMessage";
	String CMD_QUIT_MESSAGE = "SAMNG.quitMessage";
	
	/** */
	String CMD_SAVES_ANNOTATIONS = "SAMNG.savesAnnotations";

	/** */
	String CMD_VIEW_ERRORS_MESSAGE = "SAMNG.viewErrorsMessage";
	
	String CMD_VIEW_INFOS_PARCOURS= "SAMNG.viewInfosParcours";
	
	String CMD_CAPTURE_VIEW_MESSAGE ="SAMNG.captureViewMessage";
	
	String CMD_PRINT_VIEW_MESSAGE= "SAMNG.printViewMessage";
	String CMD_PRINT_SCREEN= "SAMNG.printViewScreen";
	
	String CMD_EXPORT_FILE= "SAMNG.exportFile";
	
	String CMD_MULTIPLE_EXPORT_FILE= "SAMNG.multipleExportFile";
	
	String CMD_PREVIOUS_ZOOM= "SAMNG.previousZoom";
	
	String CMD_ABSCISSE_TEMPS="SAMNG.timeAbscisse";
	String CMD_SAVE_RAPPORT_DEFAUT="SAMNG.saveRapportDefauts";
	
	String CMD_IMPORT_PROFIL = "SAMNG.importProfil";
}
