package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import noNamespace.ColonneDocument.Colonne;
import noNamespace.ConfigurationColonnesDocument.ConfigurationColonnes;
import noNamespace.VueDocument;
import noNamespace.VueDocument.Vue;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueMultimediaBase;

public class ParseurConfigurationVueMultimedia {
	public static final String FILE_LIST_COLUMN_FLAG = "FILE_LIST";
	public static final String BEGIN_DATE_COLUMN_FLAG = "BEGIN_DATE";
	public static final String DURATION_COLUMN_FLAG = "DURATION";

	private VueDocument vueDocument;
	private File file;
	private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!DOCTYPE vue SYSTEM \"../config-multimedia.dtd\"><vue></vue>";

	/* Save */
	
	public void saveConfiguration(GestionnaireVueMultimediaBase configurationManager, String fileName) throws XmlException, IOException {
		if (fileName != null) {
			this.file = new File(fileName);

			boolean createFile = !this.file.exists() || this.file.length() == 0;
			
	        if (createFile) {
	        	this.file.createNewFile();
	        	
	            FileOutputStream fileOutput = new FileOutputStream(this.file);
	            new PrintStream(fileOutput).print(emptyXMLFileContent);
	        }
	        
	        this.vueDocument = VueDocument.Factory.parse(this.file);
	        
	        if (!createFile) {
	        	return;
	        }
		}
              
		Vue vue = this.vueDocument.getVue();
		ConfigurationColonnes columnsConfiguration;
		
		if (vue == null) {
			vue = this.vueDocument.addNewVue();
		}
		
		columnsConfiguration = vue.getConfigurationColonnes();

		if (columnsConfiguration == null) {
			columnsConfiguration = initializeColumns(configurationManager, vue);
		}
		
		List<Colonne> columnsList;
		
		if (columnsConfiguration != null) {
			if ((columnsList = new LinkedList<Colonne>(Arrays.asList(columnsConfiguration.getColonneArray()))) != null) {
				columnsList.clear();
			} 
		}
		
		ConfigurationColonne[] columnsConfigurationArray = configurationManager.getColumnsConfiguration();
		Colonne column;
		XmlAnySimpleType simpleType;
		
		for (ConfigurationColonne columnConfiguration : columnsConfigurationArray) {
			column = columnsConfiguration.addNewColonne();
			simpleType = column.addNewNom();
			simpleType.setStringValue(String.valueOf(columnConfiguration.getNom()));
			
			simpleType = column.addNewAffiche();
			simpleType.setStringValue(String.valueOf(columnConfiguration.isAffiche()));
			
			simpleType = column.addNewLargeur();
			simpleType.setStringValue(String.valueOf(columnConfiguration.getLargeur()));
		}
		
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setSavePrettyPrint();
            xmlOptions.setSavePrettyPrintIndent(4);
            this.vueDocument.save(this.file, xmlOptions);
            this.vueDocument = VueDocument.Factory.parse(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	private ConfigurationColonnes initializeColumns(GestionnaireVueMultimediaBase configurationManager, Vue vue) {
		configurationManager.initColumns();
		return vue.addNewConfigurationColonnes();
	}
	
	/* Load */
	
	public void loadConfigurationManager(GestionnaireVueMultimediaBase configurationManager) {
		XmlAnySimpleType simpleType;
		
		List<Colonne> columnsList;
		String columnName;
		String columnWidth;
		ConfigurationColonne columnConfiguration;
				
		if (this.vueDocument != null) {
			Vue xmlVue = this.vueDocument.getVue();
			 
			if (xmlVue != null) {
				// Loading columns configuration
				ConfigurationColonnes configurationColumns = xmlVue.getConfigurationColonnes();
				
				if (configurationColumns != null && (columnsList = Arrays.asList(configurationColumns.getColonneArray())) != null) {
					for (Colonne column : columnsList) {
						simpleType = column.getNom();
						
						if (simpleType == null) {
							continue;
						}
						
						columnName = simpleType.getStringValue();
						
						simpleType = column.getLargeur();
						
						if (simpleType == null) {
							continue;
						}
						
						columnWidth = simpleType.getStringValue();	
						
						simpleType = column.getAffiche();
						
						if (simpleType == null) {
							continue;
						}
						
						// Now fill the column description
						columnConfiguration = new ConfigurationColonne();
						columnConfiguration.setNom(columnName);
						columnConfiguration.setLargeur(Integer.parseInt(columnWidth));
						columnConfiguration.setAffiche(true);
						
						configurationManager.addColumnConfiguration(columnConfiguration);
					}
				}
			}
		}		
	}
}