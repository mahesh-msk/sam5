package com.faiveley.samng.vueliste.ihm.actions.print.e4;

import java.util.List;

import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.actions.print.ImpressionVueTableau;
import com.faiveley.samng.principal.ihm.vues.AVueTableContentProvider;
import com.faiveley.samng.principal.ihm.vues.VueListeContentProvider;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.VueListe;

public class ImpressionVueListe extends ImpressionVueTableau {

	@Override
	protected AVueTableContentProvider getContentProvider() {
		ActivatorVueListe activatorVueListe = ActivatorVueListe.getDefault();
		VueListeContentProvider contentProvider = new VueListeContentProvider(activatorVueListe.getConfigurationMng(), activatorVueListe);
		//set the filter name to the content provider
		contentProvider.setFilter(activatorVueListe.getFiltresProvider().getAppliedFilter());
		//do not ignore columns that are not displayable
//		contentProvider.setAddNonDisplayableColumns(false);
		contentProvider.initializeColumns();
		//VueProgressBar.getInstance().start();
		contentProvider.setExport(true);
		contentProvider.loadContent(null);
		contentProvider.setExport(false);
		return contentProvider;
	}

	@Override
	public List<String> getColumnLabels() {
		IWorkbenchWindow window = ActivatorVueListe.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueListe vueliste;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueListe.ID)) {
				vueliste = (VueListe) ivr[t].getPart(false);
				return vueliste.getColumnLabels();
			}
		}
		return null;
	}

	@Override
	protected TableItem[] getSelectionTableItem() {
		IWorkbenchWindow window = ActivatorVueListe.getDefault()
				.getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueListe vueliste;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueListe.ID)) {
				vueliste = (VueListe) ivr[t].getPart(false);
				return vueliste.getTable().getSelection();
			}
		}
		return null;
	}
}
