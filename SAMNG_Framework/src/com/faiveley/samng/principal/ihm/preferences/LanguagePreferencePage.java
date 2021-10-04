package com.faiveley.samng.principal.ihm.preferences;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class LanguagePreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {
	private RadioGroupFieldEditor choixLangue = null;
	private ArrayList<String> codes = new ArrayList<String>(0);

	/** */
	public LanguagePreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription(Messages.getString("PreferencePage.0")); //$NON-NLS-1$
	}

	private String getNomLangue(String code) {
		FileReader f = null;
		String pays = "";
		boolean langueTrouvee = false;
		String nomLangue = "";
		try {
			f = new FileReader(RepertoiresAdresses.languagesNaming_PROPERTIES);

			BufferedReader bfrd = new BufferedReader(f);
			try {
				while (!(pays == null) && !langueTrouvee) {
					pays = bfrd.readLine();

					if (!(pays == null)) {
						String[] dec = pays.split("=");
						if (dec[0].equals(code.toLowerCase())) {
							nomLangue = dec[1];
						}
					}
				}

			} catch (Exception ex) {
				MessageBox msgBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setMessage(Messages.getString("PreferencePage.17"));
				msgBox.open();
			}

		} catch (FileNotFoundException e) {
			MessageBox msgBox0 = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			msgBox0.setMessage(Messages.getString("PreferencePage.18"));
			msgBox0.open();
		}

		return nomLangue;
	}

	private void getListLanguages() {
		FileReader f = null;
		int indice = 0;
		String pays = "";
		try {
			f = new FileReader(RepertoiresAdresses.languages_PROPERTIES);

			BufferedReader bfrd = new BufferedReader(f);
			try {
				while (!(pays == null)) {
					pays = bfrd.readLine();

					if (!(pays == null)) {
						String[] dec = pays.split("=");
						if (dec[1].equals("1") && dec.length == 2) {
							codes.add(indice, dec[0]);
							indice++;
						}
					}
				}

			} catch (Exception ex) {
				MessageBox msgBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setMessage(Messages.getString("PreferencePage.17"));
				msgBox.open();
			}

		} catch (FileNotFoundException e) {
			MessageBox msgBox0 = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			msgBox0.setMessage(Messages.getString("PreferencePage.18"));
			msgBox0.open();
		}
	}

	public void displayLangueNotFound(String code) {
		MessageBox msgBox = new MessageBox(Display.getCurrent()
				.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
		msgBox.setMessage(code + " : "
				+ Messages.getString("PreferencePage.15"));
		msgBox.open();
	}

	public String[][] getLabelsAndValues() {
		String[][] s = null;
		ArrayList<String> listLangues = new ArrayList<String>();
		ArrayList<String> listCodes = new ArrayList<String>();

		int nbLanguesActivees = 0;

		if (codes.size() != 0) {
			nbLanguesActivees = codes.size();
		}
		int nbLanguesActiveesRenseignees = 0;
		if (nbLanguesActivees == 0) {
			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
			msgBox.setMessage(Messages.getString("PreferencePage.16"));
			msgBox.open();
		}

		for (int i = 0; i < nbLanguesActivees; i++) {
			String code = codes.get(i).toString();
			String langue = "";

			try {
				langue = getNomLangue(code);
				if (langue.startsWith("!") && langue.endsWith("!")) {
					displayLangueNotFound(code);
					// displayLangueNotFound("1 "+code);
				} else if (langue != "") {
					listLangues.add(langue);
					listCodes.add(code);
					nbLanguesActiveesRenseignees++;
				}
			} catch (Exception e) {
				displayLangueNotFound(code);
				// displayLangueNotFound("2 "+code+ e.getMessage());
			}
		}

		if (nbLanguesActiveesRenseignees == 0) {
			s = new String[1][2];
			s[0][0] = "Defaut";
			s[0][1] = "DEF";
		} else {
			int nbLangues = listLangues.size();
			s = new String[nbLangues][2];
			for (int i = 0; i < nbLangues; i++) {
				s[i][0] = listLangues.get(i).toString();
				s[i][1] = listCodes.get(i).toString();
			}
		}
		return s;
	}

	/**
	 * Creates the field editors. Field editors are abstractions of the common
	 * GUI blocks needed to manipulate various types of preferences. Each field
	 * editor knows how to save and restore itself.
	 */
	public final void createFieldEditors() {
		getListLanguages();
		String[][] labelsAndValues = getLabelsAndValues();
		choixLangue = new RadioGroupFieldEditor(PreferenceConstants.LANG_CHOICE,
				Messages.getString("PreferencePage.3"), 1, labelsAndValues,

				getFieldEditorParent());
		addField(choixLangue);

	}

	/**
	 * @param workbench
	 *            1
	 */
	public void init(final IWorkbench workbench) {

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
		
		/* get curreent LANG_CHOICE stored in the Preference Store */
		String previousLangStr = "-Duser.language=" + this.choixLangue.getPreferenceStore()
				.getString(PreferenceConstants.LANG_CHOICE).toLowerCase();
		
		/* call super class to update LANG_CHOICE in the Preference Store */
		super.performOk();
		
		/* update SAM5.ini file */
		String chaineLangue = "-Duser.language="; //$NON-NLS-1$
		String chainePays = "-Duser.country="; //$NON-NLS-1$

		chaineLangue += this.choixLangue.getPreferenceStore()
				.getString(PreferenceConstants.LANG_CHOICE).toLowerCase();
		chainePays += this.choixLangue.getPreferenceStore()
				.getString(PreferenceConstants.LANG_CHOICE).toUpperCase();

		try {
			String SAM5File = new URL(Platform.getInstallLocation().getURL() + "SAM5.ini").getPath();
			FileReader f1 = new FileReader(SAM5File);
			LineNumberReader lnr = new LineNumberReader(f1);
			String ligne;
			String old = "";
			
			/* update -Duser.language and -Duser.country if any */
			while ((ligne = lnr.readLine()) != null) {
				if (ligne.contains("-Duser.language=")) {
					old = old + chaineLangue + "\r\n";
				} else if (ligne.contains("-Duser.country=")) {
					old = old + chainePays + "\r\n";
				} else {
					old = old + ligne + "\n";
				}
			}
			/* add -Duser.language if not present */
			if (!old.contains("-Duser.language=")) {
				old = old + chaineLangue + "\r\n";
			}
			/* add -Duser.country if not present */
			if (!old.contains("-Duser.country=")) {
				old = old + chainePays + "\r\n";
			}
			
			FileWriter f = new FileWriter(SAM5File);
			String s = old;
			f.write(s);
			f.close();
			
			/* update language needs SAM to be restarted  */
			if (!previousLangStr.equals(chaineLangue)) {
				MessageBox msgBox = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.OK);
				msgBox.setText(Messages.getString("PreferencePage.20")); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("PreferencePage.19")); //$NON-NLS-1$
				msgBox.open();
			}
		} catch (IOException e) {
			/* SAM5.ini file not found! */
			
			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_ERROR);
			msgBox.setText(Messages.getString("PreferencePage.error")); //$NON-NLS-1$
			msgBox.setMessage(Messages
					.getString("PreferencePage.errorChangingLanguage") + " " + e); //$NON-NLS-1$
			msgBox.open();
		}

		return true;
	}
}