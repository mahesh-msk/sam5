package com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire;



import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.filtre.ShowFilterWindowAction;
import com.faiveley.samng.principal.ihm.actions.vue.SetReferenceAction;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;
import com.faiveley.samng.principal.ihm.listeners.ISearchVariableVirtuele;
import com.faiveley.samng.principal.ihm.vues.AVueTable;
import com.faiveley.samng.principal.ihm.vues.FixedColumnTableViewer;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions.AjouterAnnotationAction;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.sm.controles.util.XMLName;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.actions.ExporterVueTabulaireSelectionAction;
import com.faiveley.samng.vuetabulaire.ihm.actions.RechercherVariableTabulaireAction;
import com.faiveley.samng.vuetabulaire.ihm.actions.ShowVueFiltresTabularAction;
import com.faiveley.samng.vuetabulaire.ihm.actions.impression.ImprimerVueTabulaireAction;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuefiltre.actions.ApplyFiltreAction;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.configuration.actions.ConfigTabulaireVueAction;


/** */
public class VueTabulaire extends AVueTable
implements ISearchVariableVirtuele {
	/** View ID*/
	public static final String ID = "SAMNG.Vue.Tabulaire"; //$NON-NLS-1$

	/**
	 * D�claration des actions
	 */
	private ShowFilterWindowAction ouvrirVueFiltreAction;
	private ConfigTabulaireVueAction ouvrirVueGestionColonneAction;
	protected RechercherVariableTabulaireAction rechercherVariableAction;

	//private ArrayList<Action> listeActionsAuditrice;

	/** */
	public VueTabulaire() {
		//sets the view manager
		setGestionnaireVue(ActivatorVueTabulaire.getDefault().getConfigurationMng());
		setPartName(Messages.getString("VueTabulaire.3"));
		//		ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName("defaut");
		this.initialPartName=Messages.getString("VueTabulaire.3");
		this.usesShortNames = gestionaireVue.usesShortNames();
		ActivatorVueTabulaire.getDefault().setUsesShortNames(usesShortNames);

		menuSelListener = new Listener() {
			public void handleEvent(Event e) {
				MenuItem menuItem = (MenuItem)e.widget;
				TypeMenuOptions menuId = (TypeMenuOptions)menuItem.getData();
				switch (menuId) {
				case DISPLAY_COLUMN_MNG:
					ConfigTabulaireVueAction configTabulaireVueAction = new ConfigTabulaireVueAction();
					configTabulaireVueAction.usesShortNames(usesShortNames);
					configTabulaireVueAction.run();
					break;
				case DISPLAY_FILTER_VIEW:
					new ShowVueFiltresTabularAction().run();
					break;
				case ADD_ANNOTATION:
					new AjouterAnnotationAction().run();
					break;	
				case NO_FILTER:
					OrdonnerFiltre.getInstance().setFiltreSelectTabulaire(null);
					ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName(null);
					new ApplyFiltreAction().runWithEvent(null);
					break;
				case USE_SHORT_NAMES:
					// On toggle le bool�en
					usesShortNames = !usesShortNames;
					ActivatorVueTabulaire.getDefault().setUsesShortNames(usesShortNames);
					// On applique les changements aux fen�tres de dialogue
					ouvrirVueGestionColonneAction.usesShortNames(usesShortNames);
					rechercherVariableAction.usesShortNames(usesShortNames);
					// On applique la modif sur la configuration
					gestionaireVue.setUsesShortNames(usesShortNames);
					gestionaireVue.setChanged(true);

					// On save la configuration
					ActivatorVueTabulaire.getDefault().saveConfigurationVue();
					// On recharge le tableau
					reloadTable();
					break;
				}
			}
		};
	}

	/** D�claration d'action */
	public void makeActions(){
		super.makeActions();
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		setPartName(Messages.getString("VueTabulaire.3"));
		//synchroVuesAction;
		poserReferenceAction = new SetReferenceAction(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.3"),
				com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_reference.png"));

		ouvrirVueFiltreAction = new ShowFilterWindowAction(Messages.getString("VueTabulaire.1"),  //$NON-NLS-1$
				ActivatorData.TABULAR_VUE_FILTRE_ID, ActivatorVueTabulaire.getImageDescriptor("/icons/vueTabulaire/vue_tabulaire_filtre.png"));
		ouvrirVueGestionColonneAction = new ConfigTabulaireVueAction();
		
		rechercherVariableAction = new RechercherVariableTabulaireAction(
				ICommandIds.CMD_OPEN_SEARCH_VAR,
				Messages.getString("VueTabulaire.4"), //$NON-NLS-1$
				com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_rechercher_signal.png"), usesShortNames); //$NON-NLS-1$

		exportSelectionAction = new ExporterVueTabulaireSelectionAction(window, Messages.getString("VueTabulaire.0")); //$NON-NLS-1$
		exportSelectionAction.setEnabled(true);

		imprimerVueAction = new ImprimerVueTabulaireAction(window, com.faiveley.samng.principal.ihm.vues.Messages
				.getString("AVueTable.8")); //$NON-NLS-1$
		imprimerVueAction.setEnabled(true);
	}

	/** Ajout des actions dans la toolbar */
	public final void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		makeActions();
		//ajout des actions � la toolbar
		ajoutActionToolBar(synchroVuesAction);
		ajoutActionToolBar(poserReferenceAction);
		ajoutSeparateurToolBar();
		ajoutActionToolBar(ouvrirVueFiltreAction);
		ajoutActionToolBar(ouvrirVueGestionColonneAction);
		ajoutActionToolBar(ajoutAnnotationAction);
		ajoutActionToolBar(marqueurPrecedentAction);
		ajoutActionToolBar(marqueurSuivantAction);
		ajoutActionToolBar(rechercherVariableAction);
		ajoutSeparateurToolBar();
		ajoutActionToolBar(imprimerVueAction);
		ajoutActionToolBar(capturerVueAction);
		ajoutActionToolBar(exportSelectionAction);
		updateViewInfoLabel();
		
		this.initPartListener();
	}

	private void initPartListener() {
		getSite().getPage().addPartListener(new IPartListener2() {
			
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
			
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
			
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
			
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if (partRef.getId().equals(ID)) {
					ActivatorVueTabulaire.getDefault().saveConfigurationVue();
					getSite().getPage().removePartListener(this);
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
				// TODO Stub de la m�thode g�n�r� automatiquement
			}
		});	
	}

	/**
	 * Loads the table and scroll to the previous selection
	 * @param scrollToSelection		if true scroll to the last selected message
	 */
	protected void loadTable(boolean scrollToSelection) {

		if (!scrollToSelection) {
			this.lastSelOffsetFromTop = 0;	
		}

		//dispose the current table
		disposeTable();

		ActivatorVueTabulaire activator = ActivatorVueTabulaire.getDefault();
		if (verifierAffichageVitesseCorrigee()) {
			//			ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName(null);
			//			String currentAppliedFilterName = ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter();
			//			ActivatorVueTabulaire.getDefault().getConfigurationMng().setFiltreApplique(currentAppliedFilterName);
			//			this.elements = (Object[]) Activator.getDefault()
			//			.getPoolDonneesVues().get("fullTabVueTabulaire");


			//			new ApplyFiltreAction().run();
			//			return;

			//			ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName(null);
			String currentAppliedFilterName = activator.getFiltresProvider().getAppliedFilter();
			activator.getConfigurationMng().setFiltreApplique(currentAppliedFilterName);
		}	

		//create the content provider
		VueTabulaireContentProvider contentProvider = new VueTabulaireContentProvider(activator.getConfigurationMng(),activator);
		String nomFiltreApplique = null;
		//		if(ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter()!=null)
		nomFiltreApplique = activator.getFiltresProvider().getAppliedFilter();
		//		else nomFiltreApplique = ActivatorVueTabulaire.getDefault().getConfigurationMng().getFiltreApplique();

		String currentFilter;

		if (nomFiltreApplique==null||nomFiltreApplique.equals("defaut")) {
			currentFilter="defaut";
		}else{

			boolean isvalidefilter=false;
			if (activator.getFiltresProvider().getGestionnaireFiltres().getFiltre(nomFiltreApplique)!=null) {
				isvalidefilter=activator.getFiltresProvider().filtrevalide(activator.getFiltresProvider().getGestionnaireFiltres().getFiltre(nomFiltreApplique));
			}else if (activator.getFiltresProvider().getAppliedFilter()==null) {
				isvalidefilter=true;
			}

			//set the filters
			if (!isvalidefilter) {
				currentFilter=null;
				String badfilter = activator.getConfigurationMng().getFiltreApplique();
				activator.getConfigurationMng().setFiltreApplique(null);
				activator.getFiltresProvider().setAppliedFilterName(null);
				MessageBox msgBox = new MessageBox(Activator.getDefault().getWorkbench().
						getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.YES);
				msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
				msgBox.setText(Messages.getString("VueTabulaire.filtrenonvalideTitre"));  //$NON-NLS-1$
				msgBox.setMessage(badfilter + ":" + Messages.getString("VueTabulaire.filtrenonvalideMessage1"));  //$NON-NLS-1$
				msgBox.open();
				//contentProvider.setFilter(null);
				//contentProvider.loadContent();
			}else{
				currentFilter = nomFiltreApplique;
			}
		}

		contentProvider.setFilter(currentFilter);

		//create the label provider
		VueTabulaireLabelProvider labelProvider = new VueTabulaireLabelProvider();

		//create a new table
		create(contentProvider, labelProvider, 
				new FixedColumnTableViewer(this.top, SWT.MULTI|SWT.FULL_SELECTION), this.usesShortNames);

		//mise � jour de la selection
		if(ActivatorData.getInstance().getSelectionVueTabulaire()!=-1){
			this.tblFix.setSelection(ActivatorData.getInstance().getSelectionVueTabulaire(),null);

			Message msg = getMessageFromCurrentSelection();
			if(msg!=null)
				((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
		}
		else{
			if (ActivatorData.getInstance().getSelectedMsg() != null) {
				this.tblFix.setSelection(getRowIndexForMessageId(ActivatorData.getInstance().getSelectedMsg().getMessageId(),null),null);

			} else {
				try {
					this.tblFix.setSelection(0,null);
					Message msg = getMessageFromCurrentSelection();
					((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
				} catch (Exception ex) {

				}
			}
		}
		this.tblFix.refresh();


		int width = 0;
		for(TableColumn col: this.tblFix.getScrollingTable().getColumns()) {
			width += col.getWidth();
		}

		this.tblFix.getScrollingTable().setSize(width, this.tblFix.getBounds().height);

		//refresh
		this.top.layout();

		//stop the progress bar

		//		if(VueProgressBar.getInstance()!=null)
		//			VueProgressBar.getInstance().stop();
		this.tblFix.getFixedTable().getItems();
		// scroll to previous selection
		if (scrollToSelection &&this.tblFix.getFixedTable().getItems().length>0 ) {
			scrollToPreviousSelection();
		}
		if(ActivatorData.getInstance().getSelectionVueTabulaire()!=-1){
			this.tblFix.setSelection(ActivatorData.getInstance().getSelectionVueTabulaire(),null);

			Message msg = getMessageFromCurrentSelection();
			if(msg!=null)
				((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
		}		
		this.tblFix.refresh();

	}
	private boolean verifierAffichageVitesseCorrigee(){

		AGestionnaireFiltres filtersMng = (AGestionnaireFiltres) ActivatorVueTabulaire
				.getDefault().getFiltresProvider().getGestionnaireFiltres();
		AFiltreComposant filter = filtersMng.getFiltre(ActivatorVueTabulaire.getDefault().getConfigurationMng().getFiltreApplique());

		if(filter!=null && filtersMng.getFiltreNomsVars(filter).size()>0 && GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode())!=null && 
				filtersMng.getFiltreNomsVars(filter).containsKey(GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom()))
		{
			if(!(Boolean)ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")){

				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_WARNING
						| SWT.OK);
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("VueTabulaireVariableCorrigee.0"));
				msgBox.open();

				return true;
			}
			return true;
		}else{
			return false;
		}
	}

	@Override
	protected String getDefaultFilterName() {

		String currentXmlFileName=XMLName.updateCurrentXmlName();
		if(currentXmlFileName != null && currentXmlFileName!=""){
			return (" [" + Messages.getString("VueTabulaire.5")+ " " + currentXmlFileName + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
		}else{
			return null;
		}
	}

	@Override
	public void createMenuFiltre(MenuItem item,List<AFiltreComposant> listeFiltreOrd) {
		AGestionnaireFiltres gestFiltres = (AGestionnaireFiltres) ActivatorVueTabulaire.getDefault().getFiltresProvider().getGestionnaireFiltres();
		AFiltreComposant listeFiltres = gestFiltres.getListeFiltres();
		int nb = listeFiltres.getEnfantCount();
		for(int j = 0; j < nb ; j++){
			AFiltreComposant fTemp = listeFiltres.getEnfant(j);
			if(!listeFiltreOrd.contains(fTemp))listeFiltreOrd.add(fTemp);
		}
		int limite = gestFiltres.getLimiteBridageFiltre();
		int nbFiltre = listeFiltreOrd.size();

		
		List<String> listFiltres=new ArrayList<String>();
	
		
		for(int i = 0; i < nbFiltre; i++){
			AFiltreComposant fCourant = listeFiltreOrd.get(i);
			if((fCourant != null)&&(ActivatorVueTabulaire.getDefault().getFiltresProvider().filtrevalide(fCourant))){
				if(listFiltres.size()<limite && (!listFiltres.contains(fCourant.getNom()))){
					item = new MenuItem(popupMenu,SWT.CHECK);
					item.setText(fCourant.getNom());
					item.setData(fCourant);
					item.addListener(SWT.Selection, new Listener() {

						@Override
						public void handleEvent(Event event) {
							MenuItem menuItem = (MenuItem)event.widget;
							AFiltreComposant f = (AFiltreComposant) menuItem.getData();

							List<AFiltreComposant> liste = OrdonnerFiltre.getInstance().getListeFiltreTabulaire();
							if(liste.contains(f)){
								ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName(f.getNom());
							}else{
								ActivatorVueTabulaire.getDefault().getFiltresProvider().setAppliedFilterName(null);
							}
							new ApplyFiltreAction().runWithEvent(null);
						}
					});
					boolean condition = OrdonnerFiltre.getInstance().getFiltreSelectTabulaire() == fCourant;
					item.setSelection(condition);
					item.setEnabled(!condition);
					listFiltres.add(fCourant.getNom());
				}
			}
		}
	}

	@Override
	public AFiltreComposant getAppliedFilter() {
		AbstractProviderFiltre provider = ActivatorVueTabulaire.getDefault().getFiltresProvider();
		AGestionnaireFiltres gest = (AGestionnaireFiltres) provider.getGestionnaireFiltres();
		String appliedFilter = provider.getAppliedFilter();
		return gest.getFiltre(appliedFilter);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
}  
