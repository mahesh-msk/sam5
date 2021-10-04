package com.faiveley.samng.principal.ihm.vues.vuescorrections;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.swt.widgets.TableItem;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TempResolutionEnum;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * Implements the view for time correction
 * 
 * @author meggy
 * 
 */
public class VueCorrectionsTemps extends VueCorrection {

	// the ID
	public static final String ID = "SAM.Vue.TimeCorrections"; //$NON-NLS-1$

	// the columns names
	private static final String[] columnsNames = {
			Messages.getString("VueCorrectionsTemps.1"), Messages.getString("VueCorrectionsTemps.2"), Messages.getString("VueCorrectionsTemps.3"), Messages.getString("VueCorrectionsTemps.4") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/**
	 * @return the columnsNames
	 */
	@Override
	public String[] getColumnNames() {
		return columnsNames;
	}

	@Override
	protected String getError() {
		return Messages.getString("VueCorrection.10");
	};

	@Override
	protected void updateButtons() {
		if (TableSegments.getInstance().areTempCorrections()) {
			if (TableSegments.getInstance().isAppliedTempCorrections()) {
				if (!this.buttonApply.isDisposed()) {
					this.buttonApply.setEnabled(false);
				}
				if (!this.buttonDoNotApply.isDisposed()) {
					this.buttonDoNotApply.setEnabled(true);
				}
				updateTitle(true);
			} else {
				if (!this.buttonApply.isDisposed()) {
					this.buttonApply.setEnabled(true);
				}
				if (!this.buttonDoNotApply.isDisposed()) {
					this.buttonDoNotApply.setEnabled(false);
				}
				updateTitle(false);
			}
		} else {
			if (!this.buttonApply.isDisposed()) {
				this.buttonApply.setEnabled(false);
			}
			if (!this.buttonDoNotApply.isDisposed()) {
				this.buttonDoNotApply.setEnabled(false);
			}
			updateTitle(false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.faiveley.samng.principal.ihm.vues.vuescorrections.VueCorrection#getInput
	 * ()
	 */
	@Override
	protected Row[] getInput() {
		if (this.rows == null) {
			Row row = null;
			// gets the time segments

			List<SegmentTemps> listeSegs = TableSegments.getInstance()
					.classerSegmentsTemps();

			this.rows = new Row[listeSegs.size()];
			int colNo = getColumnNames().length;
			int i = 0;

			// creates the rows
			int numSegment = 0;
			for (SegmentTemps seg : listeSegs) {
				row = new Row(colNo);

				numSegment = seg.getNumeroSegment() + 1;
				// row.setValue(0, segNo.toString());
				// pour ne pas avoir des numéros commençant à zero
				row.setValue(INDEX_SEGMENT, "" + numSegment);

				row.setValue(INDEX_PERIOD,
						seg.getTempInitial() + " - " + seg.getTempFinal()); //$NON-NLS-1$
				row.setValue(INDEX_INIT_VAL, seg.getTempInitial());
				row.setValue(INDEX_CORRECTED_VAL, seg.getTempCorrige());

				row.setData(seg);
				this.rows[i++] = row;
			}
		}
		return this.rows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.faiveley.samng.principal.ihm.vues.vuescorrections.VueCorrection#
	 * applyChanges()
	 */
	@Override
	protected void applyChanges() {
		ActivatorData.getInstance().setCorrectionTempsApplied(true);
		boolean changed = false;
		for (Row r : this.rows) {
			// looks for any changes values of the segments
			if (!r.getValue(INDEX_INIT_VAL).equals(
					r.getValue(INDEX_CORRECTED_VAL))) {

				// enables the corrections
				SegmentTemps seg = ((SegmentTemps) r.getData());
				seg.setTempCorrige(r.getValue(INDEX_CORRECTED_VAL));

				changed = true;
			}
		}

		if (changed) {

			try {
				GestionnaireCorrection.getInstance().applyTempChanges();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			TableSegments.getInstance().setAppliedTempCorrections(true);

			if (TableSegments.getInstance().areTempCorrections()) {
				ActivatorData.getInstance().notifyRepereListeners(true,
						TypeRepere.tempsCorrigee);

			}

		}
	}

	@Override
	protected void doNotApplyChanges() {
		for (Row r : this.rows) {
			// enables the corrections
			SegmentTemps seg = ((SegmentTemps) r.getData());
			seg.setTempCorrige(r.getValue(INDEX_CORRECTED_VAL));

		}

		GestionnaireCorrection.getInstance().doNotApplyTempChanges();
		TableSegments.getInstance().setAppliedTempCorrections(false);

		ActivatorData.getInstance().notifyRepereListeners(false,
				TypeRepere.tempsCorrigee);

	}

	/**
	 * Vérifie la saisie de la date
	 * 
	 * @return boolean true si toutes les saisies sont correctes
	 */
	@Override
	public boolean verifierSaisie() {

		TempResolutionEnum resolutionTemps = ((InfosFichierSamNg) FabriqueParcours
				.getInstance().getParcours().getInfo()).getTempResolution();

		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"
				+ getMsFormat(resolutionTemps));

		boolean correct = true;
		for (Row r : this.rows) {
			String chaine = r.getValue(INDEX_CORRECTED_VAL);
			String valueOrigin = r.getValue(INDEX_INIT_VAL);
			try {
				// On ajoute les millisecondes si ilm n'y en a pas et qu'il en
				// faut
				if ((resolutionTemps != TempResolutionEnum.RESOLUTION_1)
						&& (chaine.indexOf(".") == -1)) {
					chaine += ".";
				}

				// On enlève les zéros en trop à la fin
				chaine = chaine.replaceAll("0+$", "");

				// On ajoute des zéros manquants à la fin (ou on remet ceux
				// qu'on a enlevé)
				while (chaine.length() < valueOrigin.length()) {
					chaine += "0";
				}

				if (chaine.length() != valueOrigin.length())
					correct = false;

				if (correct) {
					if (Integer.parseInt(chaine.substring(17, 19)) > 60)
						correct = false;

					if (Integer.parseInt(chaine.substring(14, 16)) > 60)
						correct = false;

					if (Integer.parseInt(chaine.substring(11, 13)) > 24)
						correct = false;

					if (Integer.parseInt(chaine.substring(0, 2)) > 31)
						correct = false;

					if (Integer.parseInt(chaine.substring(0, 2)) > 31)
						correct = false;

					if (Integer.parseInt(chaine.substring(3, 5)) > 12)
						correct = false;
				}
			}

			catch (NumberFormatException ex) {
				correct = false;
			}

			try {
				if (correct) {
					format.parse(chaine);
				}
			} catch (ParseException e) {
				correct = false;
			}

			if (!correct) {
				r.setValue(INDEX_CORRECTED_VAL, valueOrigin);
			} else {
				// On sauvegarde avec la chaine modifiée (des zéros en plus ou
				// moins)
				r.setValue(INDEX_CORRECTED_VAL, chaine);
			}
		}

		this.viewer.refresh();

		return correct;
	}

	private static String getMsFormat(TempResolutionEnum resolutionTemps) {
		switch (resolutionTemps) {
		case RESOLUTION_0_1:
			return ".S";
		case RESOLUTION_0_01:
			return ".SS";
		case RESOLUTION_0_001:
			return ".SSS";
		case RESOLUTION_0_0001:
			return ".SSSS";
		case RESOLUTION_0_00001:
			return ".SSSSS";
		}
		return "";
	}

	@Override
	public boolean verifierPresenceCorrection() {
		for (TableItem tabItem : table.getItems()) {
			Row col = (Row) tabItem.getData();
			String value1 = col.getValue(INDEX_INIT_VAL);
			String value2 = col.getValue(INDEX_CORRECTED_VAL);
			if (value1.length() < value2.length()) {
				while (value1.length() < value2.length())
					value1 += "0";
			}
			if (!value2.equals(value1)) {
				return true;
			}
		}

		return false;
	}
}