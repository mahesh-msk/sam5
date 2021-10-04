package com.faiveley.samng.principal.ihm.vues.vuesfiltre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class FiltresListeComposite extends Composite implements PropertyChangeListener {

	protected Label filtersListLabel;

	protected List filtersList;

	protected Button deleteFilterButton;
	protected Button duplicateFilterButton;
	protected Button createNewFilterButton;
	protected Composite filtersButtonsOperationsComposite;
	protected Composite filtersApplyCloseButtonsComposite;
	protected Button closeFiltersViewButton;
	protected Action closeFilterViewAction;
	protected Button applyFilterButton;
	protected Button duplicateFilterGraphicToTabular;
	protected TypeFiltre acceptedFilterType;
	protected AbstractProviderFiltre filtersProvider; 
	protected int prevSelectedIndex = -1;
	protected String oldName;
	protected boolean updateLabel;

	public FiltresListeComposite(Composite parent, int style, TypeFiltre filterType) {
		super(parent, style);

		GridLayout mainCompositeLayout = new GridLayout();
		mainCompositeLayout.makeColumnsEqualWidth = true;
		setLayout(mainCompositeLayout);

		createTopLabel();
		createFiltersList();
		createFiltersButtonOperationsPanel();
		createApplyCloseButtonsPanel();
		this.acceptedFilterType = filterType;





	}


	public void setFiltersProvider(AbstractProviderFiltre provider) {
		//: remove the listener on dispose
		provider.addPropertyChangeListener(this);
		filtersProvider = provider;
		provider.updateFiltersList(this);
		onFilterApplied();	//update the apply button label and selection
		//vérification de la validité des filtres
		java.util.List<AFiltreComposant> listeFiltreNonValide = this.filtersProvider.verifierValiditeFiltres();
		if(listeFiltreNonValide!=null && listeFiltreNonValide.size()>0){
			String chaineVerificationFiltre=""; //$NON-NLS-1$
			for (AFiltreComposant composant : listeFiltreNonValide) {
				chaineVerificationFiltre+=composant.getNom()+"\n"; //$NON-NLS-1$
			}
			chaineVerificationFiltre=Messages.getString("FiltresListeComposite.18") +"\n"+chaineVerificationFiltre; //$NON-NLS-1$ //$NON-NLS-2$
			MessageBox msgBox = new MessageBox(this.getShell(),SWT.ICON_WARNING);
			msgBox.setText(Messages.getString("FiltresListeComposite.19")); //$NON-NLS-1$
			msgBox.setMessage(chaineVerificationFiltre);
			msgBox.open();
		}
	}

	protected void createTopLabel() {
		// Create the top label for the left panel
		filtersListLabel = new Label(this, SWT.SHADOW_IN | SWT.CENTER | SWT.WRAP | SWT.BORDER);
		GridData filtersListLabelLData = new GridData();
		filtersListLabelLData.heightHint = 22;
		filtersListLabelLData.grabExcessHorizontalSpace = true;
		filtersListLabelLData.horizontalAlignment = GridData.FILL;
		filtersListLabel.setLayoutData(filtersListLabelLData);
		filtersListLabel.setText(Messages.getString("FiltresListeComposite.0")); //$NON-NLS-1$
		filtersListLabel.setToolTipText((Messages.getString("FiltresListeComposite.0"))); //$NON-NLS-1$

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
		createNewFilterButton.setText(Messages.getString("FiltresListeComposite.1")); //$NON-NLS-1$
		createNewFilterButton.setToolTipText((Messages.getString("FiltresListeComposite.1"))); //$NON-NLS-1$
		createNewFilterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				filtersProvider.createNewFilter();
			}
		});

		duplicateFilterButton = new Button(filtersButtonsOperationsComposite, SWT.PUSH | SWT.CENTER);
		duplicateFilterButton.setText(Messages.getString("FiltresListeComposite.2")); //$NON-NLS-1$
		duplicateFilterButton.setToolTipText((Messages.getString("FiltresListeComposite.2"))); //$NON-NLS-1$
		duplicateFilterButton.setEnabled(false);
		duplicateFilterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				int selIdx = filtersList.getSelectionIndex();
				if(selIdx < 0 || selIdx >= filtersList.getItemCount())
					return;
				filtersProvider.duplicateFilter(filtersList.getItem(selIdx));
			}
		});





		deleteFilterButton = new Button(filtersButtonsOperationsComposite, SWT.PUSH | SWT.CENTER);
		deleteFilterButton.setText(Messages.getString("FiltresListeComposite.3")); //$NON-NLS-1$
		deleteFilterButton.setToolTipText((Messages.getString("FiltresListeComposite.3"))); //$NON-NLS-1$
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

	protected void createApplyCloseButtonsPanel() {
		//create the panel for the two buttons
		filtersApplyCloseButtonsComposite = new Composite(this, SWT.BORDER);
		FormLayout filtersApplyCloseBtnsCompLayout = new FormLayout();
		GridData filtersApplyCloseBtnsCompLData = new GridData();
		filtersApplyCloseBtnsCompLData.grabExcessHorizontalSpace = true;
		filtersApplyCloseBtnsCompLData.horizontalAlignment = GridData.FILL;
		filtersApplyCloseBtnsCompLData.verticalAlignment = GridData.END;
		filtersApplyCloseBtnsCompLData.heightHint = 90;
		filtersApplyCloseButtonsComposite.setLayoutData(filtersApplyCloseBtnsCompLData);
		filtersApplyCloseButtonsComposite.setLayout(filtersApplyCloseBtnsCompLayout);

		//Create the apply button
		applyFilterButton = new Button(filtersApplyCloseButtonsComposite, SWT.PUSH);
		FormData applyFilterButtonLData = new FormData();
		applyFilterButtonLData.height = 25;
		//applyFilterButtonLData.width = 90;
		//position it near the center of the container panel
		applyFilterButtonLData.right =  new FormAttachment(48, -4);
		applyFilterButtonLData.bottom =  new FormAttachment(638, 1000, 0);
		int nbLettersApplique=Messages.getString("FiltresListeComposite.16").length();
		int nbLettersDeselect=Messages.getString("FiltresListeComposite.17").length();
		int nbLettersMax=(nbLettersApplique>nbLettersDeselect)?nbLettersApplique:nbLettersDeselect;
		applyFilterButton.getSize().x=nbLettersMax*12;
		applyFilterButton.setLayoutData(applyFilterButtonLData);
		applyFilterButton.setText(Messages.getString("FiltresListeComposite.4")); //$NON-NLS-1$
		applyFilterButton.setToolTipText((Messages.getString("FiltresListeComposite.4"))); //$NON-NLS-1$
		applyFilterButton.setEnabled(false);
		applyFilterButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				super.widgetSelected(event);
				try {
					filtersProvider.applyCurrentFilter(false);
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		//Create the close button
		closeFiltersViewButton = new Button(filtersApplyCloseButtonsComposite, SWT.PUSH);
		FormData closeFiltersViewButtonLData = new FormData();
		closeFiltersViewButtonLData.height = 25;
		//closeFiltersViewButtonLData.width = 50;
		//align the close button to the right of the apply button
		closeFiltersViewButtonLData.left =  new FormAttachment(applyFilterButton, 4);
		closeFiltersViewButtonLData.bottom =  new FormAttachment(638, 1000, 0);
		closeFiltersViewButton.setLayoutData(closeFiltersViewButtonLData);
		closeFiltersViewButton.setText(Messages.getString("FiltresListeComposite.5")); //$NON-NLS-1$
		closeFiltersViewButton.setToolTipText((Messages.getString("FiltresListeComposite.5"))); //$NON-NLS-1$
		closeFiltersViewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				super.widgetSelected(event);
				FiltresListeComposite.this.closeFilterViewAction.run();
			}
		});
	}

	protected void createFiltersList() {
		//Create the filters list of the left panel
		GridData filtersListLData = new GridData();
		filtersListLData.verticalAlignment = GridData.FILL;
		filtersListLData.grabExcessVerticalSpace = true;
		filtersListLData.grabExcessHorizontalSpace = true;
		filtersListLData.horizontalAlignment = GridData.FILL;
		filtersList = new List(this, SWT.BORDER |SWT.MULTI |SWT.H_SCROLL |SWT.V_SCROLL); //issue 657
		filtersList.setLayoutData(filtersListLData);
		filtersList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
//				int selIdx = filtersList.getSelectionIndex();
//				if(selIdx < 0 || selIdx >= filtersList.getItemCount())
//					return;
//				if(selIdx != prevSelectedIndex) {
//				filtersProvider.filterSelected(filtersList.getItem(selIdx), true);
//				prevSelectedIndex = selIdx;
//				}
				String[] selIdx = filtersList.getItems();
				String[] selString = filtersList.getSelection();
				if(selString.length>1)
					filtersProvider.filterMultiSelected(filtersList);
				else{

					for (int i = selIdx.length-1; i >=0 ; i--) {
						for (int j = selString.length-1; j >=0 ; j--) {
							if (selString[j].equals(selIdx[i])) {
								if(i < 0 || i >= filtersList.getItemCount())
									return;
								//if(i != prevSelectedIndex) {
								filtersProvider.filterSelected(filtersList.getItem(i), true);
								prevSelectedIndex = i;
								applyFilterButton.setEnabled(filtreValide(filtersList.getItem(i)));
								//}
							}
						}
					}
				}
			}
		});
		this.filtersList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyevent) {
			}
			public void keyReleased(KeyEvent keyevent) {
				if(keyevent.keyCode == SWT.DEL) {
					String[] selString = filtersList.getSelection();
					for (int i = selString.length-1; i >= 0; i--) {
						filtersProvider.deleteFilter(selString[i]);
					}
				}
			}
		});
	}

	public void setCloseButtonAction(Action action) {
		if(action != null)
			closeFilterViewAction = action;
	}

	public void propertyChange(PropertyChangeEvent evt) {
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
		}else if("FILTER_SELECTED".equals(evt.getPropertyName())) {
			onFilterSelected(evt);
		}


		checkSelectionConsistency();
		updateApplyButtonLabel();
	}

	/** 
	 * Handles the FILTER_SAVED event. It enables also the buttons for create, 
	 * duplicate and delete and sends to the apply action a message to apply also 
	 * the saved filter
	 * @param evt
	 */
	protected void onFilterSaved(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant)evt.getNewValue();
		if(filtre == null || filtre.getFiltreType() != acceptedFilterType)
			return;
		int selIdx = filtersList.getSelectionIndex();
		if(selIdx == -1)
			throw new RuntimeException(Messages.getString("FiltresListeComposite.12")); //$NON-NLS-1$
		filtersList.setItem(selIdx, filtre.getNom());
		//tendu
		enableView(true);
	}

	protected void onFilterEditCancel(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant)evt.getOldValue();
		if(filtre == null || filtre.getFiltreType() != acceptedFilterType)
			return;
		int selIdx = filtersList.getSelectionIndex();
		if(selIdx == -1)
			throw new RuntimeException(Messages.getString("FiltresListeComposite.13")); //$NON-NLS-1$
		Object newFiltre = evt.getNewValue();
		if(newFiltre == null) {	//we had a new filter that was canceled
			filtersList.remove(selIdx);
			if(filtersList.getItemCount() > 0) {
				//select the new added filter
				filtersList.setSelection(0);
				//: It seems that List does not fires an selection event after the previous call
				//		maybe we can use the public available Widget.notifyListeners method for an explicit, 
				//		programmtically enforced event notification (in this case with
				//		type SWT.Selection).
				filtersProvider.filterSelected(filtersList.getItem(0), true);
				prevSelectedIndex = 0;
			} else {
				prevSelectedIndex = -1;
			}
		} else {
			filtersList.setItem(selIdx, filtre.getNom());
			filtersProvider.filterSelected(filtersList.getItem(selIdx), true);
			prevSelectedIndex = selIdx;
		}

		enableView(true);
	}

	protected void onFilterCreated(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant)evt.getNewValue();
		if(filtre == null)
			return;
		
		int nbItems=filtersList.getItemCount();
		
		for (int i = 0; i < nbItems; i++) {
			if (filtersList.getItem(i).equals(filtre.getNom())) {
				return;
			}
		}
		
		
		
		//add the new filter name to the filters list
		filtersList.add(filtre.getNom());
		//select the new added filter
		int selIdx = filtersList.getItemCount() - 1;
		filtersList.setSelection(selIdx);
		filtersProvider.filterSelected(filtersList.getItem(selIdx), false);
		prevSelectedIndex = selIdx;

	}
	
	public void onFilterSelected(PropertyChangeEvent evt){
		FiltreComposite filtre=(FiltreComposite)evt.getNewValue();
		String nomFiltre=filtre.getNom();
		duplicateFilterButton.setEnabled(true);
		if (filtreValide(nomFiltre)) {			
			applyFilterButton.setEnabled(true);
		}else{
			applyFilterButton.setEnabled(false);
		}
	}


	public boolean filtreValide(String nom){
		java.util.List<AFiltreComposant> listeFiltreNonValide = this.filtersProvider.verifierValiditeFiltres();
		
		if(listeFiltreNonValide!=null && listeFiltreNonValide.size()>0){
			for (AFiltreComposant composant : listeFiltreNonValide) {
				if (composant.getNom().equals(nom)) {
					return false;
				}
			}
			return true;
		}else{
			return true;
		}
	}



	protected void onFilterDeleted(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant)evt.getOldValue();
		OrdonnerFiltre.getInstance().getListeFiltreTabulaire().remove(filtre);
		if(filtre == null || filtre.getFiltreType() != acceptedFilterType)
			return;

		//removes the filter name from the filters list
		//int selIdx = filtersList.getSelectionIndex();
		String[] selIdx = filtersList.getItems();
		for (int i = selIdx.length-1; i >=0 ; i--) {
			if (filtre.getNom().equals(selIdx[i])) {
				filtersList.remove(i);
			}
		}
		if(filtersList.getItemCount() > 0) {
			//select the new added filter
			filtersList.setSelection(0);
			//: It seems that List does not fires an selection event after the previous call
			//		maybe we can use the public available Widget.notifyListeners method for an explicit, 
			//		programmtically enforced event notification (in this case with
			//		type SWT.Selection).
			filtersProvider.filterSelected(filtersList.getItem(0), true);
			prevSelectedIndex = 0;
		} else {
			prevSelectedIndex = -1;
			createNewFilterButton.setEnabled(true);
			duplicateFilterButton.setEnabled(false);
			deleteFilterButton.setEnabled(false);
			applyFilterButton.setEnabled(false);
			try {
				duplicateFilterGraphicToTabular.setEnabled(false);
			} catch (RuntimeException e) {
//				e.printStackTrace();
			}
		}

	}

	protected void onFiltersUpdate(PropertyChangeEvent evt) {


		AFiltreComposant currentFiltres = (AFiltreComposant)evt.getNewValue();
		//we should have a non-null value here
		if(currentFiltres == null)
			throw new IllegalArgumentException(Messages.getString("FiltresListeComposite.15")); //$NON-NLS-1$
		//check if this list is intended for another filters view
		if(currentFiltres.getFiltreType() != acceptedFilterType)
			return;		//is not for us
		filtersList.removeAll();	//clear the current list of filters
		int filtersCount = currentFiltres.getEnfantCount();
		AFiltreComposant curFiltre;
		ArrayList<String> tempTrier = new ArrayList<String>();

		for(int i = 0; i<filtersCount; i++) {
			curFiltre = currentFiltres.getEnfant(i);

			if(curFiltre != null)
				tempTrier.add(curFiltre.getNom());
		}
		java.util.Collections.sort(tempTrier);
		for (int j = 0; j < tempTrier.size(); j++) {
			filtersList.add(tempTrier.get(j));
		}



//		for(int i = 0; i<filtersCount; i++) {
//		curFiltre = currentFiltres.getEnfant(i);
//		if(curFiltre != null)
//		filtersList.add(curFiltre.getNom());
//		}
		if(filtersCount > 0) {
			
			if (evt.getOldValue() != null) {
				filtersList.setSelection(filtersList.indexOf((String)evt.getOldValue()));
				filtersProvider.filterSelected(filtersList.getItem(filtersList.indexOf((String)evt.getOldValue())), true);
			}else{
				filtersList.setSelection(0);
				filtersProvider.filterSelected(filtersList.getItem(0), true);
			}

			prevSelectedIndex = 0;
			
			enableView(true);	//enable delete and duplicate buttons
		} else {
			prevSelectedIndex = -1;
		}

	}

	protected void onFilterContentChanged(PropertyChangeEvent evt) {
		AFiltreComposant filtre = (AFiltreComposant)evt.getNewValue();
		if(filtre == null || filtre.getFiltreType() != acceptedFilterType){
			return;
		}
		if(filtre.getFiltreType() != acceptedFilterType){
			return;
		}
		Boolean changeState = (Boolean)evt.getOldValue();
		if(changeState == null)
			changeState = true;
		enableView(changeState);
	}

	public void updateApplyButtonLabel() {

		double nbLts=Messages.getString("FiltresListeComposite.16").length();
		int nbBlancs=(int)Math.floor(nbLts/2)+1;
		String space="";
		for (int i = 0; i < nbBlancs; i++) {
			space=space+" ";
		}
		String text = space+Messages.getString("FiltresListeComposite.16"); //$NON-NLS-1$
		String appliedFilter;
		int selIdx = filtersList.getSelectionIndex();

		//if we have a selection
		if(selIdx >= 0 && selIdx < filtersList.getItemCount()) {
			if(filtersList.getSelectionCount()==1){

				//if we have an applied filter
				if((appliedFilter = this.filtersProvider.getAppliedFilter()) != null) {
					applyFilterButton.setData("apply");
					//check if the applied filter name is the same with the selection
					if(filtersList.getItem(selIdx).equals(appliedFilter)) {
						//if so, the label of the button changes 
						applyFilterButton.setData("deselect");
						text = Messages.getString("FiltresListeComposite.17"); //$NON-NLS-1$
						oldName=appliedFilter;
					}
				}
			}
		}
		applyFilterButton.setText(text);
		applyFilterButton.setToolTipText((text));
	}

	protected void checkSelectionConsistency() {
		int selIdx = filtersList.getSelectionIndex();
		if(selIdx >= 0 && selIdx < filtersList.getItemCount()) {
			if(filtersProvider.getFiltreCourant() == null) {
				filtersProvider.filtersMng.setFiltreCourant(filtersProvider.filtersMng.getFiltre(selIdx));
			}
		} else {
			filtersProvider.filtersMng.setFiltreCourant(null);
		}
	}

	protected void onFilterApplied() {
		String appliedFilter;
		if((appliedFilter = this.filtersProvider.getAppliedFilter()) != null) {
			//select the filter if is not selected
			int i = 0;
			for(String filterName: filtersList.getItems()) {
				if(appliedFilter.equals(filterName)||appliedFilter.equals(oldName)) {
					if(prevSelectedIndex != i) {
						this.filtersList.setSelection(i);
						filtersProvider.filterSelected(filterName, true);
						prevSelectedIndex = i;
					}
					break;
				}
				i++;
			}
		}

//		updateApplyButtonLabel();
	}

	protected void enableView(boolean enabled) {
		filtersList.setEnabled(enabled);
		createNewFilterButton.setEnabled(enabled);
		duplicateFilterButton.setEnabled(enabled);
		deleteFilterButton.setEnabled(enabled);
		if(filtersProvider!=null && filtersProvider.getFiltreCourant() != null)
			applyFilterButton.setEnabled(filtersProvider.filtrevalide(filtersProvider.getFiltreCourant()));
		else
			applyFilterButton.setEnabled(enabled);
	}

	@Override
	public void dispose() {
		super.dispose();
		this.filtersProvider.removePropertyChangeListener(this);
	}


}
