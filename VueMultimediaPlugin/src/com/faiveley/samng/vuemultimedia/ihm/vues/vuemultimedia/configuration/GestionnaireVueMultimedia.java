package com.faiveley.samng.vuemultimedia.ihm.vues.vuemultimedia.configuration;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueMultimediaBase;
import com.faiveley.samng.principal.ihm.vues.configuration.Messages;
import com.faiveley.samng.principal.sm.parseurs.ParseurConfigurationVueMultimedia;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class GestionnaireVueMultimedia extends GestionnaireVueMultimediaBase {	
	private ParseurConfigurationVueMultimedia parserConfiguration;
	
	/* Save */
	public void saveToFile(String fileName) throws XmlException, IOException  {
		if (this.parserConfiguration != null) {
			try {
				this.parserConfiguration.saveConfiguration(this, fileName);
			} catch (XmlException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/* Load */
	public void loadFromFile() {		
		this.parserConfiguration = new ParseurConfigurationVueMultimedia();
		String fileName = RepertoiresAdresses.getConfigurationVueMultimedia();
		
		try {
			saveToFile(fileName);
		} catch (XmlException e) {
			MessageBox message = new MessageBox(new Shell(),SWT.ICON_ERROR);
			message.setMessage(Messages.getString("GestionnaireVueListeBase.erreurXmlCfgMessage"));
			message.setText(Messages.getString("GestionnaireVueListeBase.erreurXmlCfgTitre"));
			message.open();
			
			File toDelete = new File(fileName);
			toDelete.delete();
			
			loadFromFile();
			
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		clear();
		
		this.parserConfiguration.loadConfigurationManager(this);
	}
	
	public void clear() {
		if (this.columnConfigurationMap.get(ParseurConfigurationVueMultimedia.FILE_LIST_COLUMN_FLAG) != null) {
			this.columnConfigurationMap.get(ParseurConfigurationVueMultimedia.FILE_LIST_COLUMN_FLAG).setAffiche(false);
			this.columnConfigurationMap.remove(ParseurConfigurationVueMultimedia.FILE_LIST_COLUMN_FLAG);
		}
		
		if (this.columnConfigurationMap.get(ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG) != null) {
			this.columnConfigurationMap.get(ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG).setAffiche(false);
			this.columnConfigurationMap.remove(ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG);
		}
		
		if (this.columnConfigurationMap.get(ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG) != null) {
			this.columnConfigurationMap.get(ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG).setAffiche(false);
			this.columnConfigurationMap.remove(ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG);
		}
		
		this.columnConfigurationMap.clear();
	}
}
