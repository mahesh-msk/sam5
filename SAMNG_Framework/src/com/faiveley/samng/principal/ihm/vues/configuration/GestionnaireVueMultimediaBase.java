package com.faiveley.samng.principal.ihm.vues.configuration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.parseurs.ParseurConfigurationVueMultimedia;

public class GestionnaireVueMultimediaBase extends AGestionnaireConfigurationVue {
	public static final String FILE_LIST_COLUMN_NAME = Messages.getString("GestionnaireVueMultimediaBase.1");
	public static final String BEGIN_DATE_COLUMN_NAME = Messages.getString("GestionnaireVueMultimediaBase.2");
	public static final String DURATION_COLUMN_NAME = Messages.getString("GestionnaireVueMultimediaBase.3");
	
	protected List<String> columns = null;
	protected LinkedHashMap<String, ConfigurationColonne> columnConfigurationMap = new LinkedHashMap<String, ConfigurationColonne>();
		
	public ConfigurationColonne[] getColumnsConfiguration() {
		return this.columnConfigurationMap.values().toArray(new ConfigurationColonne[this.columnConfigurationMap.size()]);
	}
	
	public void addColumnConfiguration(ConfigurationColonne columnConfiguration) {
		if (columnConfiguration == null || columnConfiguration.getNom() == null) {
			return;
		}
		
		if (this.columnConfigurationMap.containsKey(columnConfiguration.getNom())) {
			this.columnConfigurationMap.remove(columnConfiguration.getNom());
		}

		this.columnConfigurationMap.put(columnConfiguration.getNom(), columnConfiguration);
	}
	
	public void initColumns() {
		if (this.columns == null) {
			this.columns = new ArrayList<String>(0);
			
			insertColumnConfiguration(ParseurConfigurationVueMultimedia.FILE_LIST_COLUMN_FLAG, 0);
			insertColumnConfiguration(ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG, 1);
			insertColumnConfiguration(ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG, 2);
		}
	}
	
	private void insertColumnConfiguration(String name, int position) {
		ConfigurationColonne columnConfiguration;
		
		if (!this.columnConfigurationMap.containsKey(name)) {
			columnConfiguration = new ConfigurationColonne();
			columnConfiguration.setAffiche(true);	
			columnConfiguration.setNom(name);
			columnConfiguration.setLargeur(0);
			addColumnConfiguration(columnConfiguration);
		}
	}
	
	public ConfigurationColonne getColonne(String columnName) {
		return this.columnConfigurationMap.get(columnName);
	}
	
	public int getColumnWidth(String columnName) {
		int width = 0;
		
		ConfigurationColonne columnConfiguration = this.columnConfigurationMap.get(columnName);
		
		if (columnConfiguration != null) {
			width = columnConfiguration.getLargeur();
		}

		return width;
	}
	
	public String getColumnText(String columnName) {		
		if (columnName.equals(ParseurConfigurationVueMultimedia.FILE_LIST_COLUMN_FLAG)) {
			return FILE_LIST_COLUMN_NAME;
		} else if (columnName.equals(ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG)) {
			return BEGIN_DATE_COLUMN_NAME;
		} else if (columnName.equals(ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG)) {
			return DURATION_COLUMN_NAME;
		} else {
			return "";
		}
	}
	
	public int getFilesListColumnsTotalWeight() {
		final Iterator<Entry<String, ConfigurationColonne>> it = this.columnConfigurationMap.entrySet().iterator();

		int filesListColumnsTotalWeight = 0;
		
		while (it.hasNext()) {
			final Entry<String, ConfigurationColonne> pair = it.next();
			
			if (ActivatorData.getInstance().isMultimediaFileAlone() && pair.getKey().equals(ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG)) {
				continue;
			}
			
			final ConfigurationColonne configurationColumn = pair.getValue();			
			filesListColumnsTotalWeight += configurationColumn.getLargeur();
		}
		
		return filesListColumnsTotalWeight;
	}
}
