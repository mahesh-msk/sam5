package com.faiveley.samng.principal.ihm.preferences;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import com.faiveley.samng.principal.ihm.Activator;
import org.eclipse.jface.preference.BooleanFieldEditor;

public class SynchronizationPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	
	/**
	 * Views synchronization choice w/ mouse : one or double click
	 */
	private RadioGroupFieldEditor syncMouseChoice = null;
	/**
	 * Views synchronization choice w/ keyboard : yes or no
	 */
	private BooleanFieldEditor syncKeyChoice = null;
	
	/**
	 * Create the preference page.
	 */
	public SynchronizationPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.getString("SynchronizationPreferencePage.desc")); //$NON-NLS-1$
	}
	
	/**
	 * Create contents of the preference page.
	 * syncChoice: RadioGroupFieldEditor, MOUSE SYNC CHOICE
	 * syncKeyChoice: BooleanFieldEditor, KEYBOARD SYNC CHOICE
	 */
	//@Override
	protected void createFieldEditors() {
		// Create the field editors
		
		syncMouseChoice = new RadioGroupFieldEditor(
				PreferenceConstants.MOUSE_SYNC_CHOICE,
				Messages.getString("SynchronizationPreferencePage.sync.0"), 1,
				new String[][] {
                    {Messages.getString("SynchronizationPreferencePage.sync.1"), 
                    	PreferenceConstants.MOUSE_SYNC_SINGLE_CLICK},
                    {Messages.getString("SynchronizationPreferencePage.sync.2"), 
                    	PreferenceConstants.MOUSE_SYNC_DOUBLE_CLICK}
            	}, getFieldEditorParent());
		
		addField(syncMouseChoice);
		
		syncKeyChoice = new BooleanFieldEditor(
				PreferenceConstants.KEY_SYNC_CHOICE, 
				Messages.getString("SynchronizationPreferencePage.sync.3"), 
				BooleanFieldEditor.DEFAULT, getFieldEditorParent());
		
		addField(syncKeyChoice);
	}

	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		// Initialize the preference page
	}
	
	@Override
	public boolean performOk() {
		/**
		 * params par défaut dans SAM 5.ini:
		-vmargs
		-Xms64m
		-Xmx64m
		-Duser.language=EN
		-Duser.country=en
		-Duser.mouseSync=syncDoubleClick
		-Duser.keySync=false
		**/
		
		/* call super class to set MOUSE_SYNC_CHOICE and KEY_SYNC_CHOICE in the Preference Store */
		super.performOk();
		
		/* update SAM5.ini file */
		String syncMouseStr = "-Duser.mouseSync="; //$NON-NLS-1$
		String syncKeyStr = "-Duser.keySync="; //$NON-NLS-1$
		
		syncMouseStr += this.syncMouseChoice.getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE);
		syncKeyStr += this.syncMouseChoice.getPreferenceStore().getString(PreferenceConstants.KEY_SYNC_CHOICE);
		
		try {
			String SAM5File = new URL(Platform.getInstallLocation().getURL() + "SAM5.ini").getPath();
			FileReader f1 = new FileReader(SAM5File);
			LineNumberReader lnr = new LineNumberReader(f1);
			String ligne;
			String old = "";
			
			/* update -Duser.mouseSync and -Duser.keySync if any */
			while ((ligne = lnr.readLine()) != null) {
				if (ligne.contains("-Duser.mouseSync=")) {
					old = old + syncMouseStr + "\r\n";
				} else if (ligne.contains("-Duser.keySync=")) {
					old = old + syncKeyStr + "\r\n";
				} else {
					old = old + ligne + "\n";
				}
			}
			/* add -Duser.mouseSync if not present */
			if (!old.contains("-Duser.mouseSync=")) {
				old = old + syncMouseStr + "\r\n";
			}
			/* add -Duser.keySync if not present */
			if (!old.contains("-Duser.keySync=")) {
				old = old + syncKeyStr + "\r\n";
			}
			
			FileWriter f = new FileWriter(SAM5File);
			String s = old;
			f.write(s);
			f.close();
		} catch(IOException e) {
			/* SAM5.ini file not found! */
			
			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_ERROR);
			msgBox.setText(Messages.getString("PreferencePage.error")); //$NON-NLS-1$
			msgBox.setMessage(Messages
					.getString("PreferencePage.errorChangingSync") + " " + e); //$NON-NLS-1$
			msgBox.open();
		}
		
		return true;
	}

}
