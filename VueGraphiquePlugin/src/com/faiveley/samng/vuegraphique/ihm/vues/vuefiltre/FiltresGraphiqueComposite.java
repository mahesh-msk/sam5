package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.FiltresListeComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;

public class FiltresGraphiqueComposite extends FiltresListeComposite{

	public FiltresGraphiqueComposite(Composite parent, int style, TypeFiltre filterType) {
		super(parent, style, filterType);
		
	}
	
	protected void createFiltersButtonOperationsPanel() {
		filtersButtonsOperationsComposite = new Composite(this, SWT.BORDER);
		FillLayout filtersBtnsOpsCompositeLayout = new FillLayout(org.eclipse.swt.SWT.HORIZONTAL);
		GridData filtersBtnsOpsCompositeLData = new GridData();
		filtersBtnsOpsCompositeLData.minimumWidth=300;
		filtersBtnsOpsCompositeLData.grabExcessHorizontalSpace = true;
		filtersBtnsOpsCompositeLData.verticalAlignment = GridData.END;
		filtersBtnsOpsCompositeLData.horizontalAlignment = GridData.FILL;
		filtersBtnsOpsCompositeLData.heightHint = 27;
		filtersButtonsOperationsComposite.setLayoutData(filtersBtnsOpsCompositeLData);
		filtersButtonsOperationsComposite.setLayout(filtersBtnsOpsCompositeLayout);
		//Create the operations buttons 
		createNewFilterButton = new Button(filtersButtonsOperationsComposite, SWT.PUSH | SWT.CENTER);
		createNewFilterButton.setText(com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages.getString("FiltresListeComposite.1")); //$NON-NLS-1$
		createNewFilterButton.setToolTipText((com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages.getString("FiltresListeComposite.1"))); //$NON-NLS-1$
		createNewFilterButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	filtersProvider.createNewFilter();
            }
        });
		
		duplicateFilterButton = new Button(filtersButtonsOperationsComposite, SWT.PUSH | SWT.CENTER);
		duplicateFilterButton.setText(com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages.getString("FiltresListeComposite.2")); //$NON-NLS-1$
		duplicateFilterButton.setToolTipText((com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages.getString("FiltresListeComposite.2"))); //$NON-NLS-1$
		duplicateFilterButton.setEnabled(false);
		duplicateFilterButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
				int selIdx = filtersList.getSelectionIndex();
				if(selIdx < 0 || selIdx >= filtersList.getItemCount())
					return;
				filtersProvider.duplicateFilter(filtersList.getItem(selIdx));
            }
        });
		String libelleDupliquer = com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages.getString("FiltresGraphiqueComposite.2"); //$NON-NLS-1$
		duplicateFilterGraphicToTabular = new Button(filtersButtonsOperationsComposite, SWT.PUSH | SWT.CENTER);
		duplicateFilterGraphicToTabular.setText(libelleDupliquer); //$NON-NLS-1$
		duplicateFilterGraphicToTabular.setToolTipText(libelleDupliquer);
		duplicateFilterGraphicToTabular.setEnabled(false);
		duplicateFilterGraphicToTabular.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
            	int[] selIndices = filtersList.getSelectionIndices();
				if(selIndices.length < 0 || selIndices.length  > filtersList.getItemCount())
					return;
				List<AFiltreComposant> filtresADupliquer = new ArrayList<AFiltreComposant>();
				for (int i : selIndices) {
					AFiltreComposant filtreGraphique = filtersProvider.getFiltreByNom(filtersList.getItem(i));
					
					filtresADupliquer.add(filtreGraphique);
				}
				List<AFiltreComposant>  filtresDupliques = GestionnaireFiltresGraphique.dupliquerFiltresGraphiquesFiltresTabulaires(filtresADupliquer);
				ActivatorData.getInstance().notifyDuplicateFiltreListeners(filtresDupliques);
				
            }
            
        });
		
		
		
		deleteFilterButton = new Button(filtersButtonsOperationsComposite, SWT.PUSH | SWT.CENTER);
		deleteFilterButton.setText(com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages.getString("FiltresListeComposite.3")); //$NON-NLS-1$
		deleteFilterButton.setToolTipText((com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages.getString("FiltresListeComposite.3"))); //$NON-NLS-1$
		deleteFilterButton.setEnabled(false);
		deleteFilterButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
				//int selIdx = filtersList.getSelectionIndex();
            	String[] selString = filtersList.getSelection();
        		for (int i = selString.length-1; i >= 0; i--) {
        			filtersProvider.deleteFilter(selString[i]);
        		}
        		
            }
        });
	}
	protected void enableView(boolean enabled) {
		filtersList.setEnabled(enabled);
		createNewFilterButton.setEnabled(enabled);
		duplicateFilterButton.setEnabled(enabled);
		duplicateFilterGraphicToTabular.setEnabled(enabled);
		deleteFilterButton.setEnabled(enabled);
		if(filtersProvider!=null && filtersProvider.getFiltreCourant() !=null)
			applyFilterButton.setEnabled(filtersProvider.filtrevalide(filtersProvider.getFiltreCourant()));
		else
			applyFilterButton.setEnabled(enabled);

	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if ("FILTER_SAVED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterSaved(evt);
		} else if("FILTER_EDIT_CANCEL".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterEditCancel(evt);
		} else if("FILTER_CREATED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterCreated(evt);
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
			String appliedFilter = this.filtersProvider.getAppliedFilter();
			if(appliedFilter == null || !filtersList.getItem(filtersList.getSelectionIndex()).equals(appliedFilter)) {			
					MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING);
					msgBox.setText(Messages.getString("FiltresGraphiqueComposite.0")); //$NON-NLS-1$
					msgBox.setMessage(Messages.getString("FiltresGraphiqueComposite.1")); //$NON-NLS-1$
					msgBox.open();
			}else{
				if (filtersList.getItem(filtersList.getSelectionIndex()).equals(appliedFilter)) {
					onFilterApplied();
				}
			}
		}
		
		checkSelectionConsistency();
		updateApplyButtonLabel();
	}
}
