package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import noNamespace.ColonneDocument.Colonne;
import noNamespace.ConfigurationColonnesDocument.ConfigurationColonnes;
import noNamespace.FiltreAppliqueDocument.FiltreApplique;
import noNamespace.VueDocument;
import noNamespace.VueDocument.Vue;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.ihm.vues.configuration.AGestionnaireConfigurationVue;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 17-déc.-2007 11:53:07
 */
public class ParseurConfigurationVueListe implements IParseurConfigurationVue {
	private VueDocument vueDocument;
	private String vueXmlName;
	private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE vue SYSTEM \"../config-listetabulaire.dtd\"><vue></vue>";
	
	public ParseurConfigurationVueListe() {}

	public AGestionnaireConfigurationVue chargerConfigurationVue() {
		XmlAnySimpleType simpleType;
		List<Colonne> xmlColonesList;
		String colName;
		String colSize;
		String colDisplayed;
		ConfigurationColonne colCfg;
		GestionnaireVueListeBase gestionnaireConfiguration = new GestionnaireVueListeBase();
		
		if (this.vueDocument != null) {
			Vue xmlVue = this.vueDocument.getVue();
			 
			if (xmlVue != null) {
				 // Load the applied filter
				 FiltreApplique xmlAppliedFilter = xmlVue.getFiltreApplique();
				 
				 boolean nomsCourts = xmlVue.getNomsCourts();
				 gestionnaireConfiguration.setUsesShortNames(nomsCourts);
				 
				 if(xmlAppliedFilter != null && (simpleType = xmlAppliedFilter.getNom()) != null) {
					 String appliedFilterName = simpleType.getStringValue();
				
					 if(appliedFilterName != null && "".equals(appliedFilterName.trim())) {
						 appliedFilterName = null;
					 }
					 
					 gestionnaireConfiguration.setFiltreApplique(appliedFilterName);
				}
				
				// Loading columns configuration
				ConfigurationColonnes xmlCfgColumns = xmlVue.getConfigurationColonnes();
				
				if (xmlCfgColumns != null && (xmlColonesList = Arrays.asList(xmlCfgColumns.getColonneArray())) != null) {
					for(Colonne xmlCol : xmlColonesList) {
						simpleType = xmlCol.getNom();
						
						if (simpleType == null) {
							continue;
						}
						
						colName = simpleType.getStringValue();
						
						colName = chargerNomColonnesFixesInv(colName);
						
						simpleType = xmlCol.getLargeur();
						
						if (simpleType == null) {
							continue;
						}
						
						colSize = simpleType.getStringValue();
						
						simpleType = xmlCol.getAffiche();
						
						if (simpleType == null) {
							continue;
						}
						
						colDisplayed = simpleType.getStringValue();
						
						// Now fill the column description
						colCfg = new ConfigurationColonne();
						colCfg.setNom(colName);
						colCfg.setLargeur(Integer.parseInt(colSize));
						colCfg.setAffiche(Boolean.parseBoolean(colDisplayed));
						gestionnaireConfiguration.ajouterColonneConfiguration(colCfg);
					}
				}
			}
		}
		
		return gestionnaireConfiguration;
	}
	
	public String chargerNomColonnesFixes(String name){
		if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.FLAG_COL_NAME_STR.trim())) {
			return GestionnaireVueListeBase.FLAG_ID;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.TIME_COL_NAME_STR.trim())) {
			return GestionnaireVueListeBase.TIME_ID;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.REL_TIME_COL_NAME_STR.trim())) {
			return GestionnaireVueListeBase.REL_TIME_ID;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.REL_DIST_COL_NAME_STR.trim())) {
			return GestionnaireVueListeBase.REL_DIST_ID;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.DIST_COR_COL_NAME_STR.trim())) {
			return GestionnaireVueListeBase.DIST_COR_ID;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.TIME_COR_COL_NAME_STR.trim())) {
			return GestionnaireVueListeBase.TIME_COR_ID;
		} else {
			return name;
		}
	}
	
	public String chargerNomColonnesFixesInv(String name){
		if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.FLAG_ID.trim())) {
			return GestionnaireVueListeBase.FLAG_COL_NAME_STR;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.TIME_ID.trim())) {
			return GestionnaireVueListeBase.TIME_COL_NAME_STR;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.REL_TIME_ID.trim())) {
			return GestionnaireVueListeBase.REL_TIME_COL_NAME_STR;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.REL_DIST_ID.trim())) {
			return GestionnaireVueListeBase.REL_DIST_COL_NAME_STR;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.DIST_COR_ID.trim())) {
			return GestionnaireVueListeBase.DIST_COR_COL_NAME_STR;
		} else if (name.trim().equalsIgnoreCase(GestionnaireVueListeBase.TIME_COR_ID.trim())) {
			return GestionnaireVueListeBase.TIME_COR_COL_NAME_STR;
		} else {
			return name;
		}
	}

	/**
	 * 
	 * @param config
	 * @throws IOException 
	 * @throws XmlException 
	 */
	public void enregistrerConfigurationVue(AGestionnaireConfigurationVue config) throws XmlException, IOException {
        if (this.vueDocument == null) {
        	parseRessource(this.vueXmlName, true,0,-1);
        }
        
        if (this.vueDocument == null) {	// If cannot create the file
        	return;
        }
              
        List<Colonne> xmlColonesList;
        ConfigurationColonnes xmlCfgColumns;
        FiltreApplique appliedFilter;
        XmlAnySimpleType simpleType;
		this.vueDocument.setVue(null);
		Vue xmlVue = this.vueDocument.getVue();
		
		if (xmlVue == null) {
			xmlVue = this.vueDocument.addNewVue();
			xmlVue.setNomsCourts(false);
			appliedFilter = xmlVue.addNewFiltreApplique();
			simpleType = appliedFilter.addNewNom();
			xmlCfgColumns = xmlVue.addNewConfigurationColonnes();
			
		} else {
			xmlVue.setNomsCourts(config.usesShortNames());
			appliedFilter = xmlVue.getFiltreApplique();
			
			if(appliedFilter == null) {
				appliedFilter = xmlVue.addNewFiltreApplique();
			}
			
			xmlCfgColumns = xmlVue.getConfigurationColonnes();
			
			if (xmlCfgColumns != null) {
				if ((xmlColonesList = new LinkedList<Colonne>(Arrays.asList(xmlCfgColumns.getColonneArray()))) != null) {
					xmlColonesList.clear();
				}
			} else {
				xmlCfgColumns = xmlVue.addNewConfigurationColonnes();
			}
		}
		
		if ((simpleType = appliedFilter.getNom()) == null) {
			simpleType = appliedFilter.addNewNom();
		}
		
		simpleType.setStringValue(config.getFiltreApplique() != null ? config.getFiltreApplique() : "");

		ConfigurationColonne[] colonnesCfgs = ((GestionnaireVueListeBase)config).getConfigurationColonnes();
		Colonne xmlColCfg;
		
		for (ConfigurationColonne colCfg : colonnesCfgs) {
			xmlColCfg = xmlCfgColumns.addNewColonne();
			simpleType = xmlColCfg.addNewNom();
			simpleType.setStringValue(chargerNomColonnesFixes(colCfg.getNom()));
			
			simpleType = xmlColCfg.addNewAffiche();
			simpleType.setStringValue(String.valueOf(colCfg.isAffiche()));
			
			simpleType = xmlColCfg.addNewLargeur();
			simpleType.setStringValue(String.valueOf(colCfg.getLargeur()));
		}

		saveDocument();
	}

	/**
	 * 
	 * @param chemin
	 * @throws IOException 
	 * @throws XmlException 
	 */
	public void parseRessource(String chemin) throws XmlException, IOException {
		parseRessource(chemin, false, 0, -1);
	}
	
	public void parseRessource(String chemin, boolean createFile, int deb, int fin) throws XmlException, IOException {
        File file = new File(chemin);
        
        if (createFile) {
            if (!file.exists() || file.length() == 0) {
                FileOutputStream fOut = new FileOutputStream(file);
                new PrintStream(fOut).print(emptyXMLFileContent);
            }
        }

        this.vueXmlName = chemin;
        this.vueDocument = VueDocument.Factory.parse(file);
	}
	
	public String getLastParsedFileName() {
		return this.vueXmlName;
	}
	
    /**
     * Saves the current document in the specified XML file name
     * @return true if the save was successful
     */
    private boolean saveDocument() {
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setSavePrettyPrint();
            xmlOptions.setSavePrettyPrintIndent(4);
            this.vueDocument.save(new File(this.vueXmlName), xmlOptions);
          
            this.vueDocument = VueDocument.Factory.parse(new File(this.vueXmlName));
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
}