package com.faiveley.samng.vuetabulaire.ihm.actions.impression;

import java.util.List;

import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.actions.print.ImpressionVueTableau;
import com.faiveley.samng.principal.ihm.vues.AVueTableContentProvider;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.VueTabulaire;

public class ImpressionVueTabulaire extends ImpressionVueTableau {

	@Override
	protected AVueTableContentProvider getContentProvider() {
		VueTabulaireContentProvider contentProvider = new VueTabulaireContentProvider(ActivatorVueTabulaire.getDefault().getConfigurationMng(), ActivatorVueTabulaire.getDefault());
		//set the filter name to the content provider
		contentProvider.setFilter(ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter());
		//do not ignore columns that are not displayable
		contentProvider.setAddNonDisplayableColumns(false);
		contentProvider.initializeColumns();
		//VueProgressBar.getInstance().start();
		contentProvider.setExport(true);
		contentProvider.loadContent(null);
		contentProvider.setExport(false);
		return contentProvider;
	}

	@Override
	public List<String> getColumnLabels() {
		IWorkbenchWindow window = ActivatorVueTabulaire.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueTabulaire vuetabulaire;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueTabulaire.ID)) {
				vuetabulaire = (VueTabulaire) ivr[t].getPart(false);
				return vuetabulaire.getColumnLabels();
			}
		}
		return null;
	}

	@Override
	protected TableItem[] getSelectionTableItem() {
		IWorkbenchWindow window = ActivatorVueTabulaire.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueTabulaire vuetabulaire;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueTabulaire.ID)) {
				vuetabulaire = (VueTabulaire) ivr[t].getPart(false);
				return vuetabulaire.tblFix.getSelection();
			}
		}
		return null;
	}
}
