package com.faiveley.samng.vueliste.ihm.vues.vuefiltre.e4;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
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
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.FiltresListeComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.actions.vue.e4.ApplyFiltreAction;
import com.faiveley.samng.vueliste.ihm.vues.vuefiltre.Messages;
import com.faiveley.samng.vueliste.ihm.vues.vuefiltre.VueListeFiltreEditeur;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueListeFiltre extends ViewPart implements PropertyChangeListener,
		ISaveablePart2 {
	public static final String ID = "SAMNG.Vue.Filtre.VueListeFiltre.e4"; //$NON-NLS-1$

	private SashForm sashForm = null;

	private String askForSaveMsg = Messages.getString("VueListeFiltre.1"); //$NON-NLS-1$

	private Composite leftPanelComposite;

	private VueListeFiltreEditeur rightPanelComposite;

	private Composite mainComposite;

	public VueListeFiltre() {
		
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("FILTER_APPLIED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onFilterApplied(evt);

		}
	}

	/**
	 * This method initializes sashForm
	 * 
	 */
	private void createSashForm(Composite parent) {
		sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		sashForm.setBounds(new Rectangle(3, 4, 654, 294));
		leftPanelComposite = new FiltresListeComposite(sashForm, SWT.NONE,
				TypeFiltre.liste);
		ActionOpenCloseVue closeAction = new ActionOpenCloseVue(
				Messages.getString("VueListeFiltre.3"), ID, ActionOpenCloseVue.ACTION_CLOSE); //$NON-NLS-1$
		((FiltresListeComposite) leftPanelComposite)
				.setCloseButtonAction(closeAction);

		rightPanelComposite = new VueListeFiltreEditeur(sashForm, SWT.NONE,
				TypeFiltre.liste);

		// Set the filter provider for the two views. We set first for the right
		// view
		// as the left panel generates select messaged when he registers that we
		// want
		// to be captured by the right view.
		AbstractProviderFiltre provider = ActivatorVueListe.getDefault()
				.getFiltresProvider();
		((VueListeFiltreEditeur) rightPanelComposite)
				.setFiltersProvider(provider);
		((FiltresListeComposite) leftPanelComposite)
				.setFiltersProvider(provider);
		provider.addPropertyChangeListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		setPartName(Messages.getString("VueListeFiltre.0"));
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		createSashForm(mainComposite);
		
	}

	@Override
	public void setFocus() {
		// Auto-generated method stub

	}

	public int promptToSaveOnClose() {
		return 1;
	}

	public void doSave(IProgressMonitor monitor) {
		rightPanelComposite.saveEditingFilter();
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return false;
		}
		
		return rightPanelComposite.checkEditingFilterChange();
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		AbstractProviderFiltre filtersMng = ActivatorVueListe.getDefault().getFiltresProvider();
		AFiltreComposant curFilter = filtersMng.getFiltreCourant();
		if(!this.rightPanelComposite.checkEditingFilterChange())
			return true;

		MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO |SWT.CANCEL);
		msgBox.setText(Messages.getString("VueListeFiltre.4")); //$NON-NLS-1$
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
		leftPanelComposite.dispose();
		rightPanelComposite.dispose();
		AbstractProviderFiltre provider = ActivatorVueListe.getDefault()
				.getFiltresProvider();
		provider.removePropertyChangeListener(this);
	}

	
}
