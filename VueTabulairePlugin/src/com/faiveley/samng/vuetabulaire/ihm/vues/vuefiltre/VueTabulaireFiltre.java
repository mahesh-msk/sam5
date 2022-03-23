package com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.filtre.ActionOpenCloseVue;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre.actions.ApplyFiltreAction;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueTabulaireFiltre extends ViewPart implements PropertyChangeListener, ISaveablePart2 {	
	private String askForSaveMsg = Messages.getString("VueTabulaireFiltre.1"); //$NON-NLS-1$
	
	private SashForm sashForm = null;

	private Composite leftPanelComposite;
	private VueTabulaireFiltreEditeur rightPanelComposite;
	private Composite mainComposite;

	public VueTabulaireFiltre() {
		
	
	}

	public void setFocus() {
	}

	public void propertyChange(PropertyChangeEvent evt) {
		// we just react on add events
		if("FILTER_APPLIED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterApplied(evt);
		}
		System.err.println("ev " + evt.getPropertyName()); //$NON-NLS-1$
	}
	
	protected SashForm getSashForm() {
		return this.sashForm;
	}

	/**
	 * This method initializes sashForm	
	 *
	 */
	private void createSashForm(Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		this.sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		this.sashForm.setBounds(new Rectangle(3, 4, 654, 294));
		this.leftPanelComposite = new FiltreTabulaireComposite(this, SWT.NONE, TypeFiltre.tabulaire);
		
		ActionOpenCloseVue closeAction = new ActionOpenCloseVue(Messages.getString("VueTabulaireFiltre.0"), ActivatorData.TABULAR_VUE_FILTRE_ID, ActionOpenCloseVue.ACTION_CLOSE); //$NON-NLS-1$
		((FiltreTabulaireComposite)this.leftPanelComposite).setCloseButtonAction(closeAction);
		rightPanelComposite = new VueTabulaireFiltreEditeur(this.sashForm, SWT.NONE, TypeFiltre.tabulaire);

		//Set the filter provider for the two views. We set first for the right view
		//as the left panel generates select messaged when he registers that we want 
		//to be captured by the right view.
		AbstractProviderFiltre provider = ActivatorVueTabulaire.getDefault().getFiltresProvider();
		((VueTabulaireFiltreEditeur)this.rightPanelComposite).setFiltersProvider(provider);
		((FiltreTabulaireComposite)this.leftPanelComposite).setFiltersProvider(provider);
		provider.addPropertyChangeListener(this);
		
	}

	@Override
	public void createPartControl(Composite parent) {
		this.setPartName(Messages.getString("VueTabulaireFiltreEditeur.0"));
		this.mainComposite = new Composite(parent, SWT.NONE);
		this.mainComposite.setLayout(new FillLayout());
		createSashForm(this.mainComposite);
	}

	public int promptToSaveOnClose() {
		return 1;
	}

	public void doSave(IProgressMonitor monitor) {
		this.rightPanelComposite.saveEditingFilter();
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return false;
		}
		
		return this.rightPanelComposite.checkEditingFilterChange();
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		AbstractProviderFiltre filtersMng = ActivatorVueTabulaire.getDefault().getFiltresProvider();
		AFiltreComposant curFilter = filtersMng.getFiltreCourant();
		if(!this.rightPanelComposite.checkEditingFilterChange())
			return true;

		MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO |SWT.CANCEL);
		msgBox.setText(Messages.getString("VueTabulaireFiltre.5")); //$NON-NLS-1$
		msgBox.setMessage(askForSaveMsg + " " + curFilter.getNom() + " ?"); //$NON-NLS-1$
		int res = msgBox.open();
		switch (res) {
		case SWT.YES:
			this.rightPanelComposite.saveEditingFilter();
			return true;
		case SWT.NO:
			filtersMng.filterEditingCanceled(curFilter);
			return true;
		case SWT.CANCEL:
			return false;
		default:
			return true;
		}
	}
	
	protected void onFilterApplied(PropertyChangeEvent evt) {
		
		
		new ApplyFiltreAction().run();
		
		// Must now close the filter window... Use E4 API 
		EPartService ps  = (EPartService) PlatformUI.getWorkbench().getService(EPartService.class);
		ps.hidePart(ps.getActivePart());

	}

	@Override
	public void dispose() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		super.dispose();
		this.leftPanelComposite.dispose();
		this.rightPanelComposite.dispose();
		AbstractProviderFiltre provider = ActivatorVueTabulaire.getDefault().getFiltresProvider();
		provider.removePropertyChangeListener(this);		
	}
}  
