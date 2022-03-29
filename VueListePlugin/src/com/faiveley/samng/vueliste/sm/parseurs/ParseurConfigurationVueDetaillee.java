package com.faiveley.samng.vueliste.sm.parseurs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;

import noNamespace.ColonneDocument.Colonne;
import noNamespace.ConfigurationColonnesDocument.ConfigurationColonnes;
import noNamespace.FiltreAppliqueDocument.FiltreApplique;
import noNamespace.VueDocument;
import noNamespace.VueDocument.Vue;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 17-d�c.-2007 11:53:07
 */
public class ParseurConfigurationVueDetaillee{
	private VueDocument vueDocument;
	private String vueXmlName;
	private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE vue SYSTEM \"../config-listetabulaire.dtd\"><vue></vue>";

	public ParseurConfigurationVueDetaillee() {}

	public void chargerConfigurationVue(GestionnaireVueDetaillee gestionnaireConfiguration) {
		XmlAnySimpleType simpleType;
		List<Colonne> xmlColonesList;
		String colName;
		String colSize;
		String colDisplayed;
		ConfigurationColonne colCfg;
				
		String nomUtilisateurColonne = null;
		
		if (this.vueDocument != null) {
			Vue xmlVue = this.vueDocument.getVue();
			
			if (xmlVue != null) {				
				// Loading columns configuration
				ConfigurationColonnes xmlCfgColumns = xmlVue.getConfigurationColonnes();
				
				if (xmlCfgColumns != null && (xmlColonesList = xmlCfgColumns.getColonneList()) != null) {
					for (Colonne xmlCol : xmlColonesList) {
						simpleType = xmlCol.getNom();
						
						if (simpleType == null) {
							continue;
						}
						
						colName = simpleType.getStringValue();
	
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
												
						if (colName.equals(GestionnaireVueDetaillee.VARIABLE_NAME_COL_NAME.getName())) {
							nomUtilisateurColonne = GestionnaireVueDetaillee.VARIABLE_NAME_COL_NAME.getLabel();
						} else if (colName.equals(GestionnaireVueDetaillee.CRUDE_VALUE_COL_NAME.getName())) {
							nomUtilisateurColonne = GestionnaireVueDetaillee.CRUDE_VALUE_COL_NAME.getLabel();
						} else if (colName.equals(GestionnaireVueDetaillee.DECODED_VALUE_COL_NAME.getName())) {
							nomUtilisateurColonne = GestionnaireVueDetaillee.DECODED_VALUE_COL_NAME.getLabel();
						} else if (colName.equals(GestionnaireVueDetaillee.KVB_TABLE_COL_NAME.getName())) {
							nomUtilisateurColonne = GestionnaireVueDetaillee.KVB_TABLE_COL_NAME.getLabel();
							colCfg.setIsKVB(true);
						} else if (colName.equals(GestionnaireVueDetaillee.KVB_DECODED_VALUE_COL_NAME.getName())) {
							nomUtilisateurColonne = GestionnaireVueDetaillee.KVB_DECODED_VALUE_COL_NAME.getLabel();
							colCfg.setIsKVB(true);
						}
						
						colCfg.setNom(nomUtilisateurColonne);
						colCfg.setLargeur(Integer.parseInt(colSize));
						colCfg.setAffiche(Boolean.parseBoolean(colDisplayed));
						
						gestionnaireConfiguration.ajouterColonneConfiguration(colCfg);
					}
				}
			}
		}
	}
	
	/**
	 * @param config
	 * @throws IOException 
	 * @throws XmlException 
	 */
	public void enregistrerConfigurationVue(GestionnaireVueDetaillee config) throws XmlException, IOException {
        if (this.vueDocument == null) {
        	parseRessource(this.vueXmlName, true);
        }
        
        if (this.vueDocument == null) {
        	return;
        }
        
        List<Colonne> xmlColonesList;
        ConfigurationColonnes xmlCfgColumns;
        FiltreApplique appliedFilter;
        XmlAnySimpleType simpleType;
		Vue xmlVue = this.vueDocument.getVue();
		
		if (xmlVue == null) {
			xmlVue = this.vueDocument.addNewVue();
			appliedFilter = xmlVue.addNewFiltreApplique();
			simpleType = appliedFilter.addNewNom();
			xmlCfgColumns = xmlVue.addNewConfigurationColonnes();
		} else {
			appliedFilter = xmlVue.getFiltreApplique();
			
			if (appliedFilter == null) {
				appliedFilter = xmlVue.addNewFiltreApplique();
			}
			
			xmlCfgColumns = xmlVue.getConfigurationColonnes();
			
			if (xmlCfgColumns != null) {
				if ((xmlColonesList = xmlCfgColumns.getColonneList()) != null) {
					xmlColonesList.clear();
				} 
			} else {
				xmlCfgColumns = xmlVue.addNewConfigurationColonnes();
			}
		}

		ConfigurationColonne[] colonnesCfgs = ((GestionnaireVueDetaillee) config).getConfigurationColonnes();
		Colonne xmlColCfg;
		String nomColonneUnique = null;
		
		for (ConfigurationColonne colCfg: colonnesCfgs) {
			xmlColCfg = xmlCfgColumns.addNewColonne();
			simpleType = xmlColCfg.addNewNom();
			
			if (colCfg.getNom().equals(GestionnaireVueDetaillee.VARIABLE_NAME_COL_NAME.getLabel())) {
				nomColonneUnique = GestionnaireVueDetaillee.VARIABLE_NAME_COL_NAME.getName();
			} else if (colCfg.getNom().equals(GestionnaireVueDetaillee.CRUDE_VALUE_COL_NAME.getLabel())) {
				nomColonneUnique = GestionnaireVueDetaillee.CRUDE_VALUE_COL_NAME.getName();
			} else if (colCfg.getNom().equals(GestionnaireVueDetaillee.DECODED_VALUE_COL_NAME.getLabel())) {
				nomColonneUnique = GestionnaireVueDetaillee.DECODED_VALUE_COL_NAME.getName();
			} else if (colCfg.getNom().equals(GestionnaireVueDetaillee.KVB_TABLE_COL_NAME.getLabel())) {
				nomColonneUnique = GestionnaireVueDetaillee.KVB_TABLE_COL_NAME.getName();
			} else if (colCfg.getNom().equals(GestionnaireVueDetaillee.KVB_DECODED_VALUE_COL_NAME.getLabel())) {
				nomColonneUnique = GestionnaireVueDetaillee.KVB_DECODED_VALUE_COL_NAME.getName();
			}
			
			simpleType.setStringValue(nomColonneUnique);
			
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
		parseRessource(chemin, false);
	}
	
	private void parseRessource(String chemin, boolean createFile) throws XmlException, IOException {
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

    
    public void ajouterColonneConfiguration(ConfigurationColonne cfgCol) {
		if (cfgCol == null || cfgCol.getNom() == null) {
			return;
		}
	}
}