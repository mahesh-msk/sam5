package com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vueliste.sm.parseurs.ParseurConfigurationVueDetaillee;

/**
 * @author olivier
 * @version 1.0
 * @created 27-juill.-2010
 */
public class GestionnaireVueDetaillee implements IDataChangedListener {		
	public static final ColumnName VARIABLE_NAME_COL_NAME = new ColumnName("VARIABLE_NAME_COL_NAME", "FixedColumnTableViewerDetailColumn.0");
	public static final ColumnName CRUDE_VALUE_COL_NAME = new ColumnName("CRUDE_VALUE_COL_NAME", "FixedColumnTableViewerDetailColumn.1");
	public static final ColumnName DECODED_VALUE_COL_NAME = new ColumnName("DECODED_VALUE_COL_NAME", "FixedColumnTableViewerDetailColumn.2");
	
	public static final ColumnName KVB_TABLE_COL_NAME = new ColumnName("KVB_TABLE_COL_NAME", "FixedColumnTableViewerKVBDetailColumn.0");
	public static final ColumnName KVB_DECODED_VALUE_COL_NAME = new ColumnName("KVB_DECODED_VALUE_COL_NAME", "FixedColumnTableViewerKVBDetailColumn.1");
	
	protected List<String> colonnes = null;
	private String filenameCurrent = "";
	private ParseurConfigurationVueDetaillee parserCfg;
	protected LinkedHashMap<String, ConfigurationColonne> mapConfigurationColonne = new LinkedHashMap<String, ConfigurationColonne>();
	private boolean changed = false;
	
	public GestionnaireVueDetaillee() {}

	public void onDataChange() {
		loadFromFile(RepertoiresAdresses.configurationvuesdetaillee_CFG);
	}

	protected void loadFromFile(String vueSufix) {
		ParseurXML1 p = GestionnairePool.getInstance().getXMLParser();
		String xmlFileName = p != null ? p.getXmlFileName() : null;
		
		if (xmlFileName != null) {
			File file = new File(xmlFileName);
			String fileName = file.getName();
			int dotPos;
			
			if((dotPos = fileName.indexOf('.')) != -1) {
				fileName = fileName.substring(0, dotPos);
			}
			
			setFilenameCurrent(fileName);
			String fullCfgFileName = RepertoiresAdresses.getConfigurationVues() + fileName + vueSufix;

			sauvegarderConfiguration();
			this.parserCfg = new ParseurConfigurationVueDetaillee();
			
			try {
				this.parserCfg.parseRessource(fullCfgFileName);
			} catch (XmlException e) {
				File del = new File(fullCfgFileName);

				MessageBox msg = new MessageBox(new Shell(),SWT.ICON_ERROR);

				msg.setMessage(Messages.getString("GestionnaireVueListeBase.erreurXmlCfgMessage"));
				msg.setText(Messages.getString("GestionnaireVueListeBase.erreurXmlCfgTitre"));
				msg.open();
				del.delete();
				loadFromFile(vueSufix);
				
				return;
			} catch (IOException e) {}
		
			this.parserCfg.chargerConfigurationVue(this);
			this.changed = false;
			
			if (getConfigurationColonnes() == null || getConfigurationColonnes().length == 0) {
				ConfigurationColonne configColonne1 = new ConfigurationColonne();
				configColonne1.setAffiche(true);
				configColonne1.setLargeur(220);
				configColonne1.setNom(VARIABLE_NAME_COL_NAME.getLabel());
				
				ConfigurationColonne configColonne2 = new ConfigurationColonne();
				configColonne2.setAffiche(true);
				configColonne2.setLargeur(100);
				configColonne2.setNom(CRUDE_VALUE_COL_NAME.getLabel());
				
				ConfigurationColonne configColonne3 = new ConfigurationColonne();
				configColonne3.setAffiche(true);
				configColonne3.setLargeur(180);
				configColonne3.setNom(DECODED_VALUE_COL_NAME.getLabel());
				
				ConfigurationColonne configColonne4 = new ConfigurationColonne();
				configColonne4.setAffiche(true);
				configColonne4.setLargeur(250);
				configColonne4.setNom(KVB_TABLE_COL_NAME.getLabel());
				configColonne4.setIsKVB(true);
				
				ConfigurationColonne configColonne5 = new ConfigurationColonne();
				configColonne5.setAffiche(true);
				configColonne5.setLargeur(250);
				configColonne5.setNom(KVB_DECODED_VALUE_COL_NAME.getLabel());
				configColonne5.setIsKVB(true);
				
				this.ajouterColonneConfiguration(configColonne1);
				this.ajouterColonneConfiguration(configColonne2);
				this.ajouterColonneConfiguration(configColonne3);
				this.ajouterColonneConfiguration(configColonne4);
				this.ajouterColonneConfiguration(configColonne5);
			}
		}
	}

	public String getFilenameCurrent() {
		return filenameCurrent;
	}

	public void setFilenameCurrent(String filenameCurrent) {
		this.filenameCurrent = filenameCurrent;
	}
	
	public void sauvegarderConfiguration() {
		if (this.parserCfg != null && changed) {
			try {
				this.parserCfg.enregistrerConfigurationVue(this);
			} catch (XmlException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void ajouterColonneConfiguration(ConfigurationColonne cfgCol) {
		String colName = cfgCol.getNom();
		
		if (cfgCol == null || cfgCol.getNom() == null) {
			return;
		}

		if (this.mapConfigurationColonne.containsKey(colName)) {
			this.mapConfigurationColonne.remove(colName);
		}

		this.mapConfigurationColonne.put(colName, cfgCol);
	}
	
	public ConfigurationColonne[] getConfigurationColonnes() {
		return this.mapConfigurationColonne.values().toArray(new ConfigurationColonne[this.mapConfigurationColonne.size()]);
	}
	
	
	public int updateColumnsConfigurations (ConfigurationColonne[] colsCfg) {
		// We must have an array with exactly the same number of column configurations
		
		if(colsCfg == null || colsCfg.length != this.mapConfigurationColonne.size()) {
			return -1;
		}
		
		int i = 0;
		ConfigurationColonne newCfg;
		int changedColumnsCount = 0;
		
		for (ConfigurationColonne colCfg: this.mapConfigurationColonne.values()) {
			newCfg = colsCfg[i];
			i++;
			
			if(colCfg.isAffiche() == newCfg.isAffiche() && colCfg.getLargeur() == newCfg.getLargeur()) {
				continue;
			}
			
			colCfg.setAffiche(newCfg.isAffiche());
			colCfg.setLargeur(newCfg.getLargeur());
			changedColumnsCount++;
		}
	
		this.changed = true;

		return changedColumnsCount;
	}
	
	/**
	 * Renvoit une configuration de colonne par son nom
	 * @param nom
	 * @return
	 */
	public ConfigurationColonne getConfigurationColonneByNom(String nom) {
		return this.mapConfigurationColonne.get(nom);
	}
	
	public void clear(){
		this.mapConfigurationColonne.clear();
	}
	
	public boolean isChanged() {
		return changed;
	}

	public void setChanged(boolean changed) {
		this.changed = changed;
	}
	
	public int getLargeurTotaleColonnes() {
		ConfigurationColonne[] tabColonnes = getConfigurationColonnes();
		int largeur = 0;
		
		for (ConfigurationColonne configurationColonne : tabColonnes) {
			if (!configurationColonne.isKVB()) {
				largeur += configurationColonne.getLargeur();
			}
		}
		
		return largeur;
	}
		
	public static class ColumnName {
		private String name;
		private String label;
		
		public ColumnName(String name, String label) {
			this.name = name;
			this.label = Messages.getString(label);
		}
		
		public String getName() {
			return this.name;
		}
		
		public String getLabel() {
			return this.label;
		}
	}
}