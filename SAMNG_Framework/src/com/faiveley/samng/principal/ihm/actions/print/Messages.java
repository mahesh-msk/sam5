package com.faiveley.samng.principal.ihm.actions.print;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;


public class Messages {
	private Messages() {
	}
	
	/**
	 * 
	 * @param key
	 *            1
	 * @return String
	 */
	public static String getString(final String key) {

		String langue =  Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.LANG_CHOICE);

		String BUNDLE_NAME = Messages.class.getPackage().getName() + (langue.equals("DEF") ? ".messages" :  ".messages_" + new String(langue)); //$NON-NLS-1$
		
		ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
		
		try {

			return RESOURCE_BUNDLE.getString(key);

		} catch (MissingResourceException e) {
			//return '!' + key + '!';
			BUNDLE_NAME = Messages.class.getPackage().getName() + ".messages"; //$NON-NLS-1$
			RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
			
			try {
				
				return RESOURCE_BUNDLE.getString(key);
				
			} catch (MissingResourceException ex) {
				
				return '!' + key + '!';
			}
		}
	}
}
