/**
 * 
 */
package com.faiveley.samng.principal.ihm.vues.vuescorrections;

import java.util.HashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * Implementation of the distance correctinos view
 * 
 * @author meggy
 * 
 */
public class VueCorrectionsDistance extends VueCorrection {

	public static final String ID = "SAM.Vue.DistanceCorrections"; //$NON-NLS-1$

	// the names of the columns
	private static final String[] columnsNames = {
			Messages.getString("VueCorrectionsDistance.1"), Messages.getString("VueCorrectionsDistance.2"), Messages.getString("VueCorrectionsDistance.3"), Messages.getString("VueCorrectionsDistance.4") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.faiveley.samng.principal.ihm.vues.vuescorrections.VueCorrection#
	 * getColumnNames()
	 */
	@Override
	public String[] getColumnNames() {
		return columnsNames;
	}

	@Override
	protected String getError() {
		return Messages.getString("VueCorrection.11");
	}

	@Override
	protected void updateButtons() {
		if (TableSegments.getInstance().areDistanceCorrections()) {
			if (TableSegments.getInstance().isAppliedDistanceCorrections()) {
				if (!this.buttonApply.isDisposed()) {
					this.buttonApply.setEnabled(false);
				}
				if (!this.buttonDoNotApply.isDisposed()) {
					this.buttonDoNotApply.setEnabled(true);
				}
				updateTitle(true);
			} else {
				try {
					if (!this.buttonApply.isDisposed()) {
						this.buttonApply.setEnabled(true);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					if (!this.buttonDoNotApply.isDisposed()) {
						this.buttonDoNotApply.setEnabled(false);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				try {
					updateTitle(false);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
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
			// get the distance segments
			HashMap<Integer, SegmentDistance> segments = TableSegments
					.getInstance().getSegmentsDistance();
			this.rows = new Row[segments.size()];
			int colNo = getColumnNames().length;
			int i = 0;

			// creates the rows
			for (Integer segNo : segments.keySet()) {
				row = new Row(colNo);

				SegmentDistance seg = segments.get(segNo);

				// row.setValue(0, segNo.toString());
				// pour ne pas avoir des numéros commençant à zero
				int numSegment = segNo.intValue() + 1;
				row.setValue(0, "" + numSegment); //$NON-NLS-1$

				row.setValue(1, seg.getInitialTime() + " - " + seg.getEndTime()); //$NON-NLS-1$
				if (seg.getInitialDiameter() == 0) {
					row.setValue(2, "###"); //$NON-NLS-1$
					row.setValue(3, "###"); //$NON-NLS-1$
				} else {
					row.setValue(2, Double.toString(seg.getInitialDiameter()));

					row.setValue(3, Double.toString(seg.getDiameterCorrige()));
				}

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
		boolean changed = false;
		for (Row r : this.rows) {
			// find if there are any changes in the table
			if (!r.getValue(INDEX_INIT_VAL).equals(
					r.getValue(INDEX_CORRECTED_VAL))) {

				SegmentDistance seg = ((SegmentDistance) r.getData());
				seg.setDiameterCorrige(Double.parseDouble(r
						.getValue(INDEX_CORRECTED_VAL)));
				changed = true;
			}
		}

		if (changed) {

			GestionnaireCorrection.getInstance().applyDistanceChanges();
			TableSegments.getInstance().setAppliedDistanceCorrections(true);

			if (TableSegments.getInstance().areDistanceCorrections()) {
				ActivatorData.getInstance()
						.notifyRepereListeners(true,
								TypeRepere.vitesseCorrigee,
								TypeRepere.distanceCorrigee);
			}

		}
	}

	@Override
	protected void doNotApplyChanges() {
		for (Row r : this.rows) {
			SegmentDistance seg = ((SegmentDistance) r.getData());
			try {
				double correction = Double.parseDouble(r
						.getValue(INDEX_CORRECTED_VAL));
				seg.setDiameterCorrige(correction);
			} catch (Exception ex) {
				seg.setValide(false);
			}
		}

		int ret = 0;
		String strVBV = ""; //$NON-NLS-1$
		List<VariableVirtuelle> listeVBV = ActivatorData.getInstance()
				.getProviderVBVs().getGestionnaireVbvs().getListeVBV();
		for (VariableVirtuelle virtuelle : listeVBV) {
			if (GestionnairePool.getInstance().getVariable(
					TypeRepere.distanceCorrigee.getCode()) != null
					&& GestionnairePool.getInstance().getVariable(
							TypeRepere.vitesseCorrigee.getCode()) != null) {
				if (VariableVirtuelle.contenirVariable(
						virtuelle,
						GestionnairePool.getInstance().getVariable(
								TypeRepere.distanceCorrigee.getCode()))
						|| VariableVirtuelle.contenirVariable(
								virtuelle,
								GestionnairePool.getInstance().getVariable(
										TypeRepere.vitesseCorrigee.getCode()))) {
					strVBV += "\n" + virtuelle.getDescriptor().getM_AIdentificateurComposant().getNom(); //$NON-NLS-1$
				}
			}
		}
		if (!strVBV.equals("")) { //$NON-NLS-1$
			strVBV = Messages.getString("VueCorrectionsDistance.5") + strVBV; //$NON-NLS-1$ //$NON-NLS-2$

			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO
					| SWT.CANCEL);
			msgBox.setText(Messages.getString("VueCorrectionsDistance.7")); //$NON-NLS-1$
			msgBox.setMessage(strVBV); //$NON-NLS-1$
			ret = msgBox.open();
			if (ret == SWT.YES) {
				GestionnaireCorrection.getInstance()
						.doNotApplyDistanceChanges();
				TableSegments.getInstance()
						.setAppliedDistanceCorrections(false);

				ActivatorData.getInstance()
						.notifyRepereListeners(false,
								TypeRepere.distanceCorrigee,
								TypeRepere.vitesseCorrigee);

			}
		} else {

			GestionnaireCorrection.getInstance().doNotApplyDistanceChanges();
			TableSegments.getInstance().setAppliedDistanceCorrections(false);

			ActivatorData.getInstance().notifyRepereListeners(false,
					TypeRepere.distanceCorrigee, TypeRepere.vitesseCorrigee);
		}

		if (!TableSegments.getInstance().areDistanceCorrections()) {
			// try {
			// Graphe[] gr=new Graphe[0];
			// gr=FabriqueGraphe.getGraphes();
			// for (int j = 0; j < pos; j++) {
			// int nbVARGraphe=0;
			// nbVARGraphe=gr[j].getListeCourbe().size();

			// nbVarPrec=nbVarPrec+nbVARGraphe;
			// }
			// } catch (Exception e) {

			// }
		}

	}

	@Override
	public boolean verifierSaisie() {

		boolean correct = true;

		for (Row r : this.rows) {
			try {
				if (!r.getValue(INDEX_CORRECTED_VAL).equals("###")) //$NON-NLS-1$
					Double.parseDouble(r.getValue(INDEX_CORRECTED_VAL));
			} catch (NumberFormatException ex) {
				correct = false;
				r.setValue(INDEX_CORRECTED_VAL, r.getValue(INDEX_INIT_VAL));
			}
			this.viewer.refresh();
		}

		return correct;
	}

	@Override
	public boolean verifierPresenceCorrection() {
		try {
			for (TableItem tabItem : this.table.getItems()) {
				Row col = (Row) tabItem.getData();

				Double v1 = Double.valueOf(col.getValue(INDEX_INIT_VAL));
				Double v2 = Double.valueOf(col.getValue(INDEX_CORRECTED_VAL));
				if (!v1.equals(v2)) {
					return true;
				}
			}
		} catch (NumberFormatException ex) {
		}

		return false;
	}
}
