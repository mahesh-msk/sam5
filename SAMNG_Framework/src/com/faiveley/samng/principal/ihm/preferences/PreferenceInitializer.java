package com.faiveley.samng.principal.ihm.preferences;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;


/**.
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	/** */
	public final void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String langue = System.getProperty("user.country");
  
		if(langue == null) {
			store.setDefault(PreferenceConstants.LANG_CHOICE, "DEF");
		} else {
			store.setDefault(PreferenceConstants.LANG_CHOICE, System.getProperty("user.country"));
		}
  
		/* get default value for SYNC_CHOICE from /ressources/viewsSynchronization.properties */
		FileInputStream inStream = null;
		Properties props = new Properties();
  
		String pathDefaultMouseSyncViews = RepertoiresAdresses.viewsSynchronizationProperties;
		String defaultMouseSyncViews = "syncDoubleClick";
		Boolean defaultKeySyncViews = false;
		
		try {
			inStream = new FileInputStream(new File(pathDefaultMouseSyncViews));
			props.load(inStream);
			defaultMouseSyncViews = (String) props.get("default_mouse_sync");
			defaultKeySyncViews = Boolean.parseBoolean((String) props.get("default_key_sync"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			store.setDefault(PreferenceConstants.MOUSE_SYNC_CHOICE, defaultMouseSyncViews);
			store.setDefault(PreferenceConstants.KEY_SYNC_CHOICE, defaultKeySyncViews);
		}
	}
}
