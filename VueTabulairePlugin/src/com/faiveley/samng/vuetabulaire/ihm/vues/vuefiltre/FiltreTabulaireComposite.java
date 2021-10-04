package com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre;

import java.beans.PropertyChangeEvent;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.listeners.IDuplicationFiltreListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.FiltresListeComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;

public class FiltreTabulaireComposite extends FiltresListeComposite implements IDuplicationFiltreListener{
	private final VueTabulaireFiltre tabularVue;
		
	public FiltreTabulaireComposite(VueTabulaireFiltre tabularVue, int style, TypeFiltre filterType) {
		super(tabularVue.getSashForm(), style, filterType);
		
		this.tabularVue = tabularVue;
		ActivatorData.getInstance().addDuplicationFiltreListener(this);
	}

	
	/**
	 * Méthode executée lorsqu'un composant 
	 */
	public void onFiltreDuplique(java.util.List<AFiltreComposant> listeFiltres) {
		if(listeFiltres!=null && listeFiltres.size()>0)
		filtersProvider.createNewFiltersDuplicated(listeFiltres);
		
		/* Forcer la sauvegarde
		 * Attention, le paramètre monitor vaut null car il est pour le moment inutilisé par la méthode doSave.
		 */
		tabularVue.doSave(null);
	}
	
	protected void onFilterDuplicated(PropertyChangeEvent evt) {
		filtersProvider.setLastCreatedFilter(null);
		onFilterCreated(evt);
		onFilterSaved(evt);
		enableView(true);
	}
	public void propertyChange(PropertyChangeEvent evt) {
		if ("FILTER_SAVED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterSaved(evt);
			enableView(true);
		} else if("FILTER_EDIT_CANCEL".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterEditCancel(evt);
		} else if("FILTER_CREATED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterCreated(evt);
			enableView(true);
		} 
		else if("FILTER_DUPLICATED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterDuplicated(evt);
		} 
		else if("FILTER_DELETED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterDeleted(evt);
		} else if("FILTERS_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFiltersUpdate(evt);
		} else if("FILTER_CONTENT_CHANGED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterContentChanged(evt);
		} else if("FILTER_APPLIED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterApplied();
		}
		else if("FILTER_NOT_APPLIED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING);
			msgBox.setText(Messages.getString("FiltreTabulaireComposite.0")); //$NON-NLS-1$
			//msgBox.setMessage("If you want to apply this filter, apply wheel diameter correction before (filter contains corrected speed variable).");
			msgBox.setMessage(Messages.getString("FiltreTabulaireComposite.1")); //$NON-NLS-1$
			msgBox.open();
			return;
		}
		updateApplyButtonLabel();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.filtersProvider.removePropertyChangeListener(this);
		ActivatorData.getInstance().removeDuplicationFiltreListener(this);
		
	}
}
