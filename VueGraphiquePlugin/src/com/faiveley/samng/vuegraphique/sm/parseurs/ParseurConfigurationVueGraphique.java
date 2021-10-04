package com.faiveley.samng.vuegraphique.sm.parseurs;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import noNamespace.FiltreAppliqueDocument.FiltreApplique;
import noNamespace.OptionsDocument.Options;
import noNamespace.VueDocument;
import noNamespace.VueDocument.Vue;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.ihm.vues.configuration.AGestionnaireConfigurationVue;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.parseurs.IParseurConfigurationVue;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeMode;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 04-févr.-2008 14:27:24
 */
public class ParseurConfigurationVueGraphique implements IParseurConfigurationVue {

	private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><vue xsi:noNamespaceSchemaLocation=\"configuration-vue-graphique.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></vue>";
	private VueDocument vueDocument;
	private String fileName;
	
	public ParseurConfigurationVueGraphique(){

	}

	public AGestionnaireConfigurationVue chargerConfigurationVue() {
		GestionnaireVueGraphique vueGraphiqueMng = new GestionnaireVueGraphique();
		
		String stringValue;
		Options.Axe.Enum xmlAxeEnum;
		noNamespace.OptionsDocument.Options.Mode.Enum xmlModeEnum;
		
		if(vueDocument != null) {
			Vue vue = this.vueDocument.getVue();
			if(vue != null) {
				//First get the applied filter
				FiltreApplique xmlFiltreAplied = vue.getFiltreApplique();
				// Then get the noms courts
				boolean nomsCourts = vue.getNomsCourts();
				vueGraphiqueMng.setUsesShortNames(nomsCourts);
				
				if(xmlFiltreAplied != null) {
					stringValue = xmlFiltreAplied.getNom();
					if(stringValue != null && !"".equals(stringValue.trim())) {
						vueGraphiqueMng.setFiltreApplique(stringValue);
					}
				}

				//get the options 
				Options xmlOptions = vue.getOptions();
				if(xmlOptions != null) {
					//get the axe type
					xmlAxeEnum = xmlOptions.getAxe();
					if(xmlAxeEnum != null) {
						TypeAxe typeAxe = TypeAxe.AXE_DISTANCE; 
						switch(xmlAxeEnum.intValue()) {
							case 1:
								if (!new ParseurAdapteur().inhiberAxeTempsVueGraphique()) {
									typeAxe = TypeAxe.AXE_TEMPS;
									break;
								}
							case 2:
								if (!new ParseurAdapteur().inhiberAxeDistanceVueGraphique()) {
									typeAxe = TypeAxe.AXE_DISTANCE;
									break;
								}
							case 3:
								typeAxe = TypeAxe.AXE_TEMPS_CORRIGE;
								break;
							case 4:
								typeAxe = TypeAxe.AXE_DISTANCE_CORRIGEE;
								break;
						}
						vueGraphiqueMng.setAxe(typeAxe);
					}
					//get the enable sampling mode
					vueGraphiqueMng.setEchantillonnage(xmlOptions.getEchantillonnage());
					
					//get the white background option
					vueGraphiqueMng.setFond_blanc(xmlOptions.getFondBlanc());

					//get the show legende option
					vueGraphiqueMng.setLegende(xmlOptions.getLegende());

					//get the stepped graph option
					vueGraphiqueMng.setMarches_escalier(xmlOptions.getMarchesEscalier());

					//get the show markers option
					vueGraphiqueMng.setMarqueurs(xmlOptions.getMarqueurs());

					//get the show ref0 option
					vueGraphiqueMng.setRef_zero_digit(xmlOptions.getRefZeroDigit());

					//get the show ruptures distance option
					vueGraphiqueMng.setRuptures_distances(xmlOptions.getRupturesDistance());

					//get the show ruptures time option
					vueGraphiqueMng.setRupture_temps(xmlOptions.getRupturesTemps());

					//get the mode option (line or point mode)
					xmlModeEnum = xmlOptions.getMode();

					if(xmlAxeEnum != null) {
						TypeMode typeMode = TypeMode.LINE; 
						switch(xmlModeEnum.intValue()) {
							case 1:
								typeMode = TypeMode.LINE;
								break;
							case 2:
								typeMode = TypeMode.POINT;
								break;
						}
						vueGraphiqueMng.setMode(typeMode);
					}
				}
			}
		}
		
		if (GestionnaireAxes.getInstance().getCurrentAxeType()==TypeAxe.AXE_TEMPS &&
				new ParseurAdapteur().inhiberAxeTempsVueGraphique()
				&& (!new ParseurAdapteur().inhiberAxeDistanceVueGraphique())) {
			vueGraphiqueMng.setAxe(TypeAxe.AXE_DISTANCE);
		}
		
		if (GestionnaireAxes.getInstance().getCurrentAxeType()==TypeAxe.AXE_DISTANCE &&
				new ParseurAdapteur().inhiberAxeDistanceVueGraphique()
				&& (!new ParseurAdapteur().inhiberAxeTempsVueGraphique())) {
			vueGraphiqueMng.setAxe(TypeAxe.AXE_TEMPS);
		}
		
		return vueGraphiqueMng;
	}

	public void enregistrerConfigurationVue(AGestionnaireConfigurationVue saveConfig) {
		GestionnaireVueGraphique config = (GestionnaireVueGraphique)saveConfig;
        if (this.vueDocument == null || config == null) {
            return;
        }
        
		Vue vue = this.vueDocument.getVue();
		if(vue == null) {
			vue = this.vueDocument.addNewVue();
		}
		vue.setNomsCourts(config.usesShortNames());
		
		FiltreApplique xmlFiltreAplied = vue.getFiltreApplique();
		if(xmlFiltreAplied == null) {
			xmlFiltreAplied = vue.addNewFiltreApplique();
		}
		xmlFiltreAplied.setNom(config.getFiltreApplique());
		
		Options xmlOptions = vue.getOptions();
		if(xmlOptions == null) {
			xmlOptions = vue.addNewOptions();
		}
		
		int enumVal = 1;
		switch(config.getAxe()) {
			case AXE_TEMPS:
				enumVal = 1;
				break;
			case AXE_DISTANCE:
				enumVal = 2;
				break;
			case AXE_TEMPS_CORRIGE:
				enumVal = 3;
				break;
			case AXE_DISTANCE_CORRIGEE:
				enumVal = 4;
				break;
		}
		xmlOptions.setAxe(Options.Axe.Enum.forInt(enumVal));

		//get the enable sampling mode
		xmlOptions.setEchantillonnage(config.isEchantillonnage());
		
		//get the white background option
		xmlOptions.setFondBlanc(config.isFond_blanc());

		//get the show legende option
		xmlOptions.setLegende(config.isLegende());

		//get the stepped graph option
		xmlOptions.setMarchesEscalier(config.isMarches_escalier());

		//get the show markers option
		xmlOptions.setMarqueurs(config.isMarqueurs());

		//get the show ref0 option
		xmlOptions.setRefZeroDigit(config.isRef_zero_digit());

		//get the show ruptures distance option
		xmlOptions.setRupturesDistance(config.isRuptures_distance());

		//get the show ruptures time option
		xmlOptions.setRupturesTemps(config.isRuptures_temps());

		//get the mode option (line or point mode)
		TypeMode typeMode = config.getMode();
		int mode = 1;	//default is LINE
		switch(typeMode) {
			case LINE:
				mode = 1;
				break;
			case POINT:
				mode = 2;
				break;
		}
		xmlOptions.setMode(noNamespace.OptionsDocument.Options.Mode.Enum.forInt(mode));

        saveDocument();
	}
	
	public void parseRessource(String chemin,boolean explorer,int deb,int fin) throws AExceptionSamNG {
		 try {
	            File file = new File(chemin);

	            if (!file.exists() || file.length() == 0) {
	                FileOutputStream fOut = new FileOutputStream(file);
	                new PrintStream(fOut).print(emptyXMLFileContent);
	            }

			this.vueDocument = VueDocument.Factory.parse(file);
			this.fileName = chemin;
		} catch (XmlException e) {
			
			e.printStackTrace();
	    } catch (IOException e) {
	        
	    }
	}
	
    /**
     * Saves the current document in the specified XML file name
     * @return true if the save was successfull
     */
    private boolean saveDocument() {
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setSavePrettyPrint();
            xmlOptions.setSavePrettyPrintIndent(4);
            this.vueDocument.save(new File(this.fileName), xmlOptions);
            //: I think this is not ok ... Maybe another method is to have the listeFiltres updated
            this.vueDocument = VueDocument.Factory.parse(new File(
                        this.fileName));
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
    
	public String getLastParsedFileName() {
		return this.fileName;
	}

}