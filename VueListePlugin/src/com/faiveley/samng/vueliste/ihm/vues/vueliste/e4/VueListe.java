package com.faiveley.samng.vueliste.ihm.vues.vueliste.e4;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.dialogs.RechercherEvenementAction;
import com.faiveley.samng.principal.ihm.actions.dialogs.RechercherMarqueurAction;
import com.faiveley.samng.principal.ihm.actions.filtre.ShowFilterWindowAction;
import com.faiveley.samng.principal.ihm.actions.vue.SetReferenceAction;
import com.faiveley.samng.principal.ihm.calcul.PositionMilieuViewer;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchEventListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchMarquerListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.progbar.BarreProgressionDialog;
import com.faiveley.samng.principal.ihm.vues.AVueTableContentProvider;
import com.faiveley.samng.principal.ihm.vues.AVueTableLabelProvider;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.DataViewsUtil;
import com.faiveley.samng.principal.ihm.vues.IMarqueursListener;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.VueListeContentProvider;
import com.faiveley.samng.principal.ihm.vues.VueTableColumnsIndices;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions.AjouterAnnotationAction;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuetoolbar.IVueToolbar;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.marqueurs.Marqueur;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.actions.exports.e4.ExporterVueListeAction;
import com.faiveley.samng.vueliste.ihm.actions.print.e4.ImprimerVueListeAction;
import com.faiveley.samng.vueliste.ihm.actions.table.e4.CollapseAllAction;
import com.faiveley.samng.vueliste.ihm.actions.table.e4.ExpandAllAction;
import com.faiveley.samng.vueliste.ihm.actions.vue.e4.ApplyFiltreAction;
import com.faiveley.samng.vueliste.ihm.actions.vue.e4.RechercherVariableListeAction;
import com.faiveley.samng.vueliste.ihm.actions.vue.e4.ShowVueFiltresListeAction;
import com.faiveley.samng.vueliste.ihm.vues.vuefiltre.e4.VueListeFiltre;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.Messages;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.VueListeLabelProvider;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.action.e4.ConfigListVueAction;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.e4.TreeKVBDetailViewer;


public class VueListe extends AbstractSelectionProviderVue implements PropertyChangeListener, IDataChangedListener, ISelectionListener, IMarqueursListener, IRepereChangedListener, ISearchMarquerListener, ISearchVariableListener, ICapturable, IVueToolbar,ISearchEventListener {
	/**
	 * Vue ID
	 */
	public static final String ID = "SAMNG.Vue.Liste.e4";

	/**
	 * D�claration des actions
	 */
	private boolean oneTimeOffset = true; // Le positionnement offset ne se fait qu'une fois
	private RechercherEvenementAction rechercherEvenementAction;
	private ShowFilterWindowAction ouvrirVueFiltreAction;
	private ConfigListVueAction ouvrirVueGestionColonneAction;
	private RechercherVariableListeAction rechercherVariableAction;
	private ExpandAllAction expandAction;
	private CollapseAllAction collapseAction;
	private FixedColumnTableViewerDetail fctvd;
	private VueListeLabelProvider labelProvider;
	private VueListeContentProvider contentProvider;
	
	/* synchronization of the table from the keyboard */
	private boolean syncFromKey = false;
	
	// Context menu
	private Menu popupMenu;

	private Listener menuSelListener;

	// List of column names
	private List<String> columnNames;

	private FixedColumnTableVueListe tblFix;

	// The data
	private VueData data;

	// The base manager of the view
	private GestionnaireVueListeBase gestionaireVue;

	// Parent
	private Composite top;

	// ToolBar
	private IActionBars bars;

	//
	private String initialPartName;
	
	// Dummy column to fix horizontal scrolling for scrollable table
	private TableColumn dummyCol;

	/**
	 * D�claration des actions
	 */
	private Action synchroVuesAction;

	private SetReferenceAction poserReferenceAction;

	private AjouterAnnotationAction ajoutAnnotationAction;

	private RechercherMarqueurAction marqueurSuivantAction;

	private RechercherMarqueurAction marqueurPrecedentAction;

	private ImprimerVueListeAction imprimerVueAction;

	private CapturerVueAction capturerVueAction;

	private Action exportSelectionAction;
	
	private List<AFiltreComposant> listeFiltreOrd;

	public VueListe() {
		// Load the data parcours 
		this.data = ActivatorData.getInstance().getVueData();
		this.columnNames = new ArrayList<String>(0);
		this.currentSelection = new MessageSelection();

		setGestionnaireVue(ActivatorVueListe.getDefault().getConfigurationMng());
		
		setPartName(Messages.getString("VueListe.11"));
		this.initialPartName=Messages.getString("VueListe.11");

		menuSelListener = new Listener() {
			public void handleEvent(Event e) {
				MenuItem menuItem = (MenuItem) e.widget;
				TypeMenuOptions menuId = (TypeMenuOptions)menuItem.getData();
				
				switch (menuId) {
					case DISPLAY_COLUMN_MNG:
						new ConfigListVueAction().run();
						break;
					case DISPLAY_FILTER_VIEW:
						new ShowVueFiltresListeAction().run();
						break;
					case ADD_ANNOTATION:
						new AjouterAnnotationAction().run();
						break;
					case NO_FILTER:
						ActivatorVueListe.getDefault().getFiltresProvider().setAppliedFilterName("");
						new ApplyFiltreAction().runWithEvent(null);
						break;
				}
			}
		};
	}


	/** D�claration d'action */
	public void makeActions(){
		// R�cup�ration de la fenetre active
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		// Ajout des actions
		synchroVuesAction = new Action() {
			public void run() {
				ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
				upSelection(true);
			}
		};
		
		synchroVuesAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_synchro.png"));
		synchroVuesAction.setText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.1"));
		synchroVuesAction.setToolTipText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.2"));
		synchroVuesAction.setEnabled(true);

		poserReferenceAction = new SetReferenceAction(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.3"), com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_reference.png"));
		poserReferenceAction.setEnabled(true);

		ajoutAnnotationAction = new AjouterAnnotationAction();
		ajoutAnnotationAction.setEnabled(true);

		marqueurSuivantAction = new RechercherMarqueurAction(ICommandIds.CMD_OPEN_SEARCH_MARQUER, com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.30"), com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_annotation_suivante.png"), true);

		marqueurPrecedentAction = new RechercherMarqueurAction(ICommandIds.CMD_OPEN_SEARCH_MARQUER, com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.31"), com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_annotation_precedente.png"), false);

		capturerVueAction = new CapturerVueAction(window, com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.7"));
		capturerVueAction.setEnabled(true);

		imprimerVueAction = new ImprimerVueListeAction(window, com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.8"));
		imprimerVueAction.setEnabled(true);

		setPartName(Messages.getString("VueListe.11"));

		ouvrirVueFiltreAction = new ShowFilterWindowAction(Messages.getString("VueListe.3"), VueListeFiltre.ID, ActivatorVueListe.getImageDescriptor("/icons/vueListe/vue_liste_filtre.png"));
		ouvrirVueGestionColonneAction = new ConfigListVueAction();

		rechercherVariableAction = new RechercherVariableListeAction(ICommandIds.CMD_OPEN_SEARCH_VAR, Messages.getString("VueListe.12"), com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_rechercher_signal.png"));

		rechercherEvenementAction = new RechercherEvenementAction(ICommandIds.CMD_OPEN_SEARCH_VAR, Messages.getString("VueListe.10"), com.faiveley.samng.vueliste.ihm.ActivatorVueListe.getImageDescriptor("/icons/vueListe/vue_liste_rechercher_evenement.png"));
		
		exportSelectionAction = new ExporterVueListeAction(window, Messages.getString("VueListe.0")); //$NON-NLS-1$
		exportSelectionAction.setEnabled(true);

		expandAction = new ExpandAllAction(ICommandIds.CMD_EXP, com.faiveley.samng.vueliste.ihm.actions.table.Messages.getString("ConfigListVueAction.2"), com.faiveley.samng.vueliste.ihm.ActivatorVueListe.getImageDescriptor("/icons/toolBar/expand2.png"), tblFix);
		expandAction.setEnabled(true);

		collapseAction = new CollapseAllAction(ICommandIds.CMD_COLL, com.faiveley.samng.vueliste.ihm.actions.table.Messages.getString("ConfigListVueAction.3"), com.faiveley.samng.vueliste.ihm.ActivatorVueListe.getImageDescriptor("/icons/toolBar/collapse.png"), tblFix);
		collapseAction.setEnabled(true);
	}

	/** Ajout des actions dans la toolbar */
	public final void createPartControl(final Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		this.top = new Composite(parent, SWT.NONE);

		// Add this vue as listener for data and repere changes
		ActivatorData.getInstance().addDataListener(this);
		ActivatorData.getInstance().addRepereListener(this);

		if (TableSegments.getInstance().isAppliedDistanceCorrections()) {
			this.gestionaireVue.updateRepere(TypeRepere.distanceCorrigee);
			this.gestionaireVue.updateRepere(TypeRepere.vitesseCorrigee);
		}
		
		if (TableSegments.getInstance().isAppliedTempCorrections()) {
			this.gestionaireVue.updateRepere(TypeRepere.tempsCorrigee);
		}

		if (getData() != null) {
			if (getData().getDataTable() != null) {
				// Create a table with visible headers and lines, and set the font that we created
				loadTable(false);
			}
		}

		// Register this as Selection provider and as selection listener
		if (this.tblFix != null) {
			getSite().setSelectionProvider(this);
			ISelectionService selService = getSite().getWorkbenchWindow().getSelectionService();
			selService.addPostSelectionListener(this);
		}
		
		System.out.println("TABLE RUPTURE TEMP : " + TableRuptures.getInstance().getListeRupturesTemps());
		System.out.println("TABLE RUPTURE DIST : " + TableRuptures.getInstance().getListeRupturesDistance());

		// Toolbar
		makeActions();
		
		// Initialisation de la toolbar
		this.bars = getViewSite().getActionBars();

		updateViewInfoLabel();
		// makeActions();

		// Ajout des actions � la toolbar
		ajoutActionToolBar(synchroVuesAction);
		ajoutActionToolBar(poserReferenceAction);
		ajoutSeparateurToolBar();
		ajoutActionToolBar(ouvrirVueFiltreAction);
		ajoutActionToolBar(ouvrirVueGestionColonneAction);
		ajoutSeparateurToolBar();
		ajoutActionToolBar(ajoutAnnotationAction);
		ajoutActionToolBar(marqueurPrecedentAction);
		ajoutActionToolBar(marqueurSuivantAction);

		ajoutActionToolBar(rechercherVariableAction);
		ajoutActionToolBar(rechercherEvenementAction);
		ajoutSeparateurToolBar();
		ajoutActionToolBar(imprimerVueAction);
		ajoutActionToolBar(capturerVueAction);
		ajoutActionToolBar(exportSelectionAction);
		ajoutActionToolBar(expandAction);
		ajoutActionToolBar(collapseAction);
		updateViewInfoLabel();
		
		this.initPartListener();
		
		if (GestionLineCommandParameters.getIndiceMsg() != -1 && oneTimeOffset) {
			selectionChanged(null, null);
		}
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
					ActivatorVueListe.getDefault().saveConfigurationVue();
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
	 * @param scrollToSelection, if true scroll to the last selected message
	 */
	protected void loadTable(boolean scrollToSelection) {
		// Dispose the table
		disposeTable();

		// Create the content provider
		ActivatorVueListe activatorVueListe = ActivatorVueListe.getDefault();
		contentProvider = new VueListeContentProvider(activatorVueListe.getConfigurationMng(), activatorVueListe);

		String filterName;

		boolean isvalidefilter = false;
		
		if (activatorVueListe.getFiltresProvider().getGestionnaireFiltres().getFiltre(activatorVueListe.getConfigurationMng().getFiltreApplique()) != null) {
			isvalidefilter=activatorVueListe.getFiltresProvider().verifierValiditeFiltre(activatorVueListe.getFiltresProvider().getGestionnaireFiltres().getFiltre(activatorVueListe.getConfigurationMng().getFiltreApplique()));
		} else {
			isvalidefilter=true;
		}

		// Set the filters
		if (!isvalidefilter) {
			filterName = null;
			String badfilter = activatorVueListe.getConfigurationMng().getFiltreApplique();
			activatorVueListe.getConfigurationMng().setFiltreApplique(null);
			activatorVueListe.getFiltresProvider().setAppliedFilterName(null);
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.YES);
			msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
			msgBox.setText(Messages.getString("VueListe.filtrenonvalideTitre"));
			msgBox.setMessage(badfilter + " : " + Messages.getString("VueListe.filtrenonvalideMessage2"));
			msgBox.open();
		} else {
			filterName = activatorVueListe.getConfigurationMng().getFiltreApplique();
		}

		contentProvider.setFilter(filterName);

		// Create the label provider
		labelProvider = new VueListeLabelProvider();

		FixedColumnTableViewerDetail fctvd=new FixedColumnTableViewerDetail(this.top, SWT.MULTI | SWT.FULL_SELECTION, this);
		this.fctvd = fctvd;
		
		// Create a new table
		create(contentProvider, labelProvider,  fctvd);

		// Reload
		this.top.layout();

		// Scroll to last selction 
		if (scrollToSelection && this.tblFix.getFixedTable().getItems().length > 0) {
			scrollToPreviousSelection();
		}
		
		try {
			this.expandAction.setFixedColumnTableViewerDetail(this.fctvd);
			this.collapseAction.setFixedColumnTableViewerDetail(this.fctvd);
		} catch (RuntimeException e) {}
	}

	/*
	 * (non-Javadoc)
	 * @see com.faiveley.samng.principal.ihm.listeners.ISearchEventListener#onSearchEvent(java.lang.String, boolean)
	 */
	public void onSearchEvent(String eventName, boolean next) {
		int rowIdx = -1;
		
		if (eventName != null) {
			Row row;
			
			try {
				row = (Row)this.tblFix.getSelection()[0].getData();
			} catch (Exception e) {
				this.tblFix.setSelection(0,null);
				row = (Row)this.tblFix.getSelection()[0].getData();
			}

			int crtSelMsgId = ((Message) row.getData()).getMessageId();

			// Get the messages
			// R�cup�ration uniquement des bons messages
			Collection<Message> collMsg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
			List<Message> messages = new ArrayList<Message>();
			messages.addAll(collMsg);

			if (next) { 
				// Searching for next event 
				for (int i = 0; i < messages.size(); i++) {
					Message msg = messages.get(i);
					
					// Search for the current selected message
					if (msg.getMessageId() <= crtSelMsgId) {
						continue;
					}

					if (msg.getEvenement() == null) {
						continue;
					}
					
					// Get next message that contains the event after the selected one
					String crtEvNom2 = msg.getEvenement().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					String crtEvNom = msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom();
					
					if (crtEvNom.equals(eventName) || crtEvNom2.equals(eventName)) {
						rowIdx = getRowIndexForMessageId(msg.getMessageId(), null);
						
						if (rowIdx != -1 || this.ajoutAnnotationAction == null) {
							this.tblFix.setSelection(rowIdx, null);
						}

						// Issue 740
						int indicePourSelectMessageAuMilieu = PositionMilieuViewer.getPosition(rowIdx);
						this.tblFix.setTopIndex(indicePourSelectMessageAuMilieu);
						
						break;
					}

				}
				
				// If no element was found : rowIdx == -1
				if (rowIdx == -1){
					MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
					msgBox.setText("");
					String msgBoxMessage;
					// If no filter is currently applied
					if (getAppliedFilter() == null) {
						msgBoxMessage = Messages.getString("VueListe.4");
					}
					else {
						msgBoxMessage = Messages.getString("VueListe.6");
					}
					msgBox.setMessage(msgBoxMessage);
					msgBox.open();
				}
			} else {
				// Searching for previous message
				int i = 0;
				boolean msgFound = false;
				while(!msgFound && i < messages.size()) {
					Message msg = messages.get(i);
					
					// If this is the current selected message we stop
					if (msg.getMessageId() == crtSelMsgId) {
						msgFound = true;
					} else {
						i++;
					}
				}

				boolean resultFound = false;
				int k = i - 1;
				while (!resultFound && k >= 0) {
					Message msg = messages.get(k);
					
					Langage langage = Activator.getDefault().getCurrentLanguage();
					String crtEvNom2 = msg.getEvenement().getNomUtilisateur().getNomUtilisateur(langage);
					String crtEvNom = msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom();
					
					if (crtEvNom.equals(eventName)||crtEvNom2.equals(eventName)) {
						rowIdx = getRowIndexForMessageId(msg.getMessageId(),null);
						
						if (rowIdx != -1 || this.ajoutAnnotationAction == null) {
							this.tblFix.setSelection(rowIdx, null);
						}

						// Issue 740
						int indicePourSelectMessageAuMilieu = PositionMilieuViewer.getPosition(rowIdx);
						this.tblFix.setTopIndex(indicePourSelectMessageAuMilieu);
						resultFound = true;
					}
					
					k--;
				}

				// If no element was found
				if (rowIdx == -1) {
					MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
					msgBox.setText("");
					String msgBoxMessage;
					// If no filter is currently applied
					if (getAppliedFilter() == null) {
						msgBoxMessage = Messages.getString("VueListe.5");
					}
					else {
						msgBoxMessage = Messages.getString("VueListe.7");
					}
					msgBox.setMessage(msgBoxMessage);
					msgBox.open();
				}
						
			}	
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.faiveley.samng.principal.ihm.listeners.ISearchEventListener#hasSelectedEvent()
	 */
	public boolean hasSelectedEvent() {
		// Returns true if it is a selection made in the table 
		
		if (this.data != null && this.data.getDataTable() != null && this.tblFix.getSelectionIndex() >= 0) {
			return true;
		}
		
		return false;
	}

	protected String getDefaultFilterName() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener#onSearchVariable(com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable, java.lang.String, com.faiveley.samng.principal.ihm.vues.search.Operation, boolean)
	 */
	@Override
	public void onSearchVariable(DescripteurVariable descrVar, String value, Operation op, boolean next) {
		boolean dateRecherchee = false;
		boolean trouveVarComposee = false;
		Row row = this.tblFix.getSelection() != null && this.tblFix.getSelection().length != 0 ? (Row) this.tblFix.getSelection()[0].getData() : null;
		Message msgCourant = (Message) row.getData();
		int crtSelMsgId = row != null ? msgCourant.getMessageId() : 0;
		
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}
		
		Collection<Message> collMsg = this.data.getDataTable().getEnregistrement().getMessages();
		List<Message> messages = new ArrayList<Message>();
		messages.addAll(collMsg);
		Message selMsg = null;
		String[] msgErr = new String[1];
		this.setSearchChange(false);
		Message msg = null;
		
		for (int i = (next ? 0 : (messages.size() - 1)); next ? i<messages.size() : i > -1; i = (next ? (i + 1) : (i - 1) )) { // Si next, on incremente sinon on decremente
			msg = messages.get(i);
			
			if (next && msg.getMessageId() < crtSelMsgId || ((!next) && msg.getMessageId() > crtSelMsgId)) {
				continue;
			}
			
			// si variable compos�e
			if (descrVar.getTypeVariable() == TypeVariable.VAR_COMPOSEE || descrVar.getTypeVariable() == TypeVariable.VAR_COMPLEXE) { // Si variable compos�e
				List <AVariableComposant> listVars=new ArrayList<AVariableComposant>();
				listVars.addAll(msg.getVariablesComplexe());
				listVars.addAll(msg.getVariablesComposee());
				
				for (AVariableComposant var : listVars) {
					if (descrVar == var.getDescriptor()) {
						if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var)) {
							trouveVarComposee = true;
							selMsg = msg;
							break;
						}
					}
				}
				
				if (trouveVarComposee) {
					break;
				}
			} else { // Si variable pas compos�e
				// si c'est une date
				if (descrVar.getM_AIdentificateurComposant().getCode() == TypeRepere.date.getCode()) { // Si c'est une date
					dateRecherchee = true;
					selMsg = searchVariableDate(msg, descrVar, value, op, msgErr, false);
					
					if (selMsg != null) {
						if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), value)) {
							break;
						}
					}
				} else if (descrVar != null && descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC && descrVar.getM_AIdentificateurComposant() != null && descrVar.getM_AIdentificateurComposant().getNom().equals(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2"))) {
					dateRecherchee = true;
					selMsg=searchVariableDate(msg, descrVar, value, op, msgErr,true);
					
					if (selMsg != null) {
						if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), value)) {
							break;
						}
					}
				} else if (descrVar.isVolatil()) { // Si c'est une variable volatile
					if (checkSearchedVariableVolatile(descrVar, msg, value, op, msgErr, msgCourant)){
						selMsg = msg;
						break;
					}
				} else { // Si c'est une variable simple, analogique ou discr�te
					AVariableComposant var = msg.getVariable(descrVar);
					
					if (var != null) {
						if (value == null || op == null){
							if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var)) {
								selMsg = msg;
								break;
							}
						}
						
						if (checkSearchedVariable(var, value, op, msgErr, msg, msgCourant)) {
							if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var)) {
								selMsg = msg;
								break;
							}
						}
					}
				} 
			}
		}

		if (selMsg != null) {
			int rowIdx = dateRecherchee ? getRowIndexForMessageDate(selMsg.getMessageId(), descrVar, next, msg.getAbsoluteTime(),msgCourant.getMessageId(),op,value,msgErr, ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null)) : getRowIndexForMessageId(selMsg.getMessageId(),descrVar);
			
			// Issue 740
			int indicePourSelectMessageAuMilieu=PositionMilieuViewer.getPosition(rowIdx);

			this.tblFix.setSelection(rowIdx,descrVar);
			this.tblFix.setTopIndex(indicePourSelectMessageAuMilieu);
			handleLineSelection(rowIdx);
			ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
			upSelection(true);
			
			if (msgErr != null && msgErr[0] != null) {
				afficherPasDeMsgCorrespondantCriteres(msgErr);
			}
		} else {
			afficherPasDeMsgCorrespondantCriteres(msgErr);
		}
	}

	protected void afficherPasDeMsgCorrespondantCriteres(String[] msgErr){
		if (msgErr[0] == null) {
			msgErr[0] = com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.25");
		}
		
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart() == VueListe.this.getSite().getPart()) {
			MessageBox msgBox = new MessageBox(VueListe.this.getSite().getShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText("");
			msgBox.setMessage(msgErr[0]);
			msgBox.open();
		}
	}

	/**
	 * Gets the parent
	 * 
	 * @return the parent
	 */
	public Composite getTop() {
		return this.top;
	}

	/**
	 * M�thode d'ajout d'une action dans le menu
	 * 
	 * @param action
	 */
	public void ajoutActionToolBar(Action action) {
		this.bars.getToolBarManager().add(action);
	}

	/**
	 * M�thode d'ajout d'une action dans la toolbar
	 * 
	 * @param action
	 */
	public void ajoutActionToolMenuBar(Action action) {
		this.bars.getMenuManager().add(action);
	}

	/** */
	public void ajoutSeparateurToolBar() {
		bars.getToolBarManager().add(new Separator());
	}

	public void upSelection(boolean refreshTable) {
		// Update selection
		updateSelectionMessage(refreshTable);
		((MessageSelection) VueListe.this.currentSelection).setUserSentSelection(true);
		fireSelectionChanged(VueListe.this.currentSelection);
		((MessageSelection) VueListe.this.currentSelection).setUserSentSelection(false);
	}

	/**
	 * Creates the view
	 * 
	 * @param contentProvider, the content provider
	 * @param labelProvider, the label provider
	 * @param table, the table
	 */
	protected void create(AVueTableContentProvider contentProvider, AVueTableLabelProvider labelProvider, FixedColumnTableVueListe table) {
		this.tblFix = table;
		FillLayout fillLayout = new FillLayout();
		GridData layoutData = new GridData(GridData.FILL_BOTH);
		this.top.setLayout(fillLayout);
		this.top.setLayoutData(layoutData);

		// Maybe it was already added
		if (!ActivatorData.getInstance().isDataListening(this)) {
			ActivatorData.getInstance().addDataListener(this);
		}

		try {
			// initialize the columns
			contentProvider.initializeColumns();
			this.columnNames = contentProvider.getColumnNames();
			VueTableColumnsIndices colsIndicesInfo = contentProvider.getColumnIndices();
			labelProvider.setColumnIndices(colsIndicesInfo);

			this.tblFix.setLabelProvider(labelProvider);

			BarreProgressionDialog barre = new BarreProgressionDialog("Chargement Vue Liste",contentProvider);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());

			try {
				dialog.run(true, true, barre);
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			barre = null;
			dialog = null;

			this.tblFix.setContentProvider(contentProvider);

			// Sets the header and lines visible
			this.tblFix.setHeaderVisible(true);
			this.tblFix.setLinesVisible(true);

			// Creates the listeners to display the tooltips
			createCellTooltip();
			
			// The system font will not display the lower 32 characters, so create one that will
			this.tblFix.setFont(this.data.getNormalFont());

			// Create the columns
			List<TableColumn> columns = createColumns(this.tblFix, this.columnNames, colsIndicesInfo.getLastFixedColumn());

			// Set a dummy input. The input is set by the content provider, but the mechanism of setting the input for the table has to be started
			this.tblFix.setInput("");

			// Update the columns width conform to the configuration
			updateAutoColumnsWidth(columns, colsIndicesInfo.getLastFixedColumn());
			addControlListenerToColumns(columns);

			this.tblFix.onColumnsAdded();
		} catch (Throwable t) {
			t.printStackTrace();
		}

		// Sets the selection listener on the table
		this.tblFix.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
				// Update selection
				updateSelectionMessage(true);
				
				/* If the preferences require synchronization with the Up and Down keys */
				if (syncFromKey == true) {
					((MessageSelection) VueListe.this.currentSelection).setUserSentSelection(true);
					fireSelectionChanged(VueListe.this.currentSelection);
					((MessageSelection) VueListe.this.currentSelection).setUserSentSelection(false);
					syncFromKey = false;
				}
			}
		});

		this.tblFix.addKeyListener(new KeyListener() {
			private boolean turbo=false;
			public void keyPressed(KeyEvent e) {
				// Synchro si pression sur bouton Entr�e
				if (e.keyCode == SWT.CR) {
					ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
					upSelection(true);
				}
				
				if (e.keyCode == SWT.SHIFT) {
					setTurbo(true);
				}
				
				if (e.keyCode == SWT.ARROW_LEFT) {
					if (isTurbo()) {
						VueListe.this.tblFix.fixedTable.showColumn(VueListe.this.tblFix.fixedTable.getColumn(0));
					}
				}
				
				if (e.keyCode == SWT.ARROW_RIGHT) {
					if (isTurbo()) {
						VueListe.this.tblFix.fixedTable.showColumn(VueListe.this.tblFix.fixedTable.getColumn(VueListe.this.tblFix.fixedTable.getColumns().length - 1));
					}
				}
				
				if ((e.keyCode == SWT.ARROW_UP) || (e.keyCode == SWT.ARROW_DOWN)) {
					if (Activator.getDefault().getPreferenceStore()
							.getBoolean(PreferenceConstants.KEY_SYNC_CHOICE)) {
						/* request synchronization of the table w/ Up or Down key
						 * according to preferences. Sync will be executed by 
						 * SelectionChangedListener */
						syncFromKey = true;
					}
				}
			}

			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.SHIFT) {
					setTurbo(false);
				}
			}

			public boolean isTurbo() {
				return turbo;
			}

			public void setTurbo(boolean turbo) {
				this.turbo = turbo;
			}
		});

		// Sets the mouse listener on the table
		this.tblFix.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1) { // Left button double click
					if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
							.equals(PreferenceConstants.MOUSE_SYNC_DOUBLE_CLICK)) {
						ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
						upSelection(true);
					}
				}
			}

			public void mouseDown(MouseEvent e) {}

			public void mouseUp(MouseEvent e) {
				if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
						.equals(PreferenceConstants.MOUSE_SYNC_SINGLE_CLICK)) {
					ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
					upSelection(true);
				}
			}
		});

		getData().addMarkersListener(this);
		
		// Update the current markers - we have nothing to deselect as the table was just created
		marquersListeChangement(getData().getMarkerMsgIds(), new int[0]);
		updateViewInfoLabel();

		// Now create the context menu
		createContextMenu();

		if (ActivatorData.getInstance().getSelectionVueListe() != -1) {
			this.tblFix.setSelection(ActivatorData.getInstance().getSelectionVueListe(), null);

			Message msg = getMessageFromCurrentSelection();
			
			if (msg != null) {
				((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
			}
		} else {
			if (ActivatorData.getInstance().getSelectedMsg() != null) {
				this.tblFix.setSelection(getRowIndexForMessageId(ActivatorData.getInstance().getSelectedMsg().getMessageId(), null), null);
			} else {
				try {
					this.tblFix.setSelection(0, null);
					Message msg = getMessageFromCurrentSelection();
					((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
				} catch (Exception ex) {}
			}
		}
		this.tblFix.refresh();
	}

	/**
	 * Workaround for scrolling table. We need to force always the presence of
	 * the horizontal scroll to avoid decalage with the fixed table when we are
	 * on the last line. Unfortunatelly, in SWT there is no way to force showing
	 * the horizontal scroll when the sum of columns widths is less than the
	 * table area
	 * 
	 */
	protected void checkForDummyColumn() {
		Rectangle area = tblFix.getScrollingTable().getClientArea();
		Point preferredSize = tblFix.getScrollingTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		
		int width = area.width - (2 * tblFix.getScrollingTable().getBorderWidth());

		if (preferredSize.y > (area.height + tblFix.getScrollingTable().getHeaderHeight())) {
			// Subtract the scrollbar width from the total column width if a vertical scrollbar will be required
			Point vBarSize = tblFix.getScrollingTable().getVerticalBar().getSize();
			width -= vBarSize.x;
		}

		Point oldSize = tblFix.getScrollingTable().getSize();
		width -= getScrollingColumnsWidths();
		width += 25;

		if (oldSize.x > area.width) {
			// Table is getting smaller so make the columns smaller first and then resize the table to match the client area width indexesColumn.setWidth(indexesColumn.getWidth());
			dummyCol.setWidth(width);
		} else {
			// Table is getting bigger so make the table bigger first and then make the columns wider to match the client area width
			dummyCol.setWidth(width);
		}
	}

	/**
	 * Computes the widths of the columns from the scrolling table. The width of
	 * the dummy column is not taken into consideration
	 * 
	 * @return the sum of columns widths
	 */
	private int getScrollingColumnsWidths() {
		int width = 0;
		
		for (TableColumn col : tblFix.getScrollingTable().getColumns()) {
			if (col != dummyCol) {
				width += col.getWidth();
			}
		}
		
		return width;
	}

	/**
	 * Creates the columns for the table
	 * 
	 * @param table, the table
	 * @return TableColumn[]
	 */
	protected List<TableColumn> createColumns(final FixedColumnTableVueListe table, List<String> columnsNames, int indexFixed) {
		List<TableColumn> columns = new ArrayList<TableColumn>();
		TableColumn col = null;
		int colWidth;
		int size = columnsNames.size();
		String colText;
		String colNom;
		ConfigurationColonne colCfg;

		// Add the an empt column as first column in fixed table.
		// This is a workaround for FixedColumnTable that do not refreshes
		// the first column text when a selection is made in VIRTUAL mode
		// The SWT table handles the first column different than the others
		addZeroLengthColumn(table.getFixedTable());
		
		for (int i = 0, n = size; i < n; i++) {
			// Create the TableColumn with right alignment
			col = new TableColumn(table.getFixedTable(), SWT.SINGLE	| SWT.LEFT);
			
			colNom = columnsNames.get(i);
			colWidth = this.gestionaireVue.getColonneLargeur(colNom);
			
			if (colWidth > 0) { // for the auto columns is the method
				col.setWidth(colWidth);
			}

			// Add new columns to columns list
			columns.add(col);

			colCfg = this.gestionaireVue.getColonne(colNom);
			
			if (colCfg != null) {
				colText = GestionnaireVueListeBase.getDisplayLabelForColumn(colCfg, false);
			} else {
				colText = colNom;
			}

			if (colText == null) {
				colText = colNom;
			}
			
			// This text will appear in the column header
			col.setText(colText);

			// Set the same text as tooltip
			col.setToolTipText(colText);
			col.setData(colNom);
		}
		return columns;
	}

	/**
	 * Adds an empty column that has width 0 and is not resizable. This is a
	 * workaround for FixedColumnTable that do not refreshes the first
	 * column text when a selection is made in VIRTUAL mode The SWT table
	 * handles the first column different than the others This should be called
	 * only for the first column of a table
	 * 
	 * @param table
	 */
	private void addZeroLengthColumn(Table table) {
		TableColumn col = new TableColumn(table, SWT.SINGLE | SWT.RIGHT);
		col.setWidth(0);
		col.setResizable(false);
	}

	/**
	 * Adds a control listener to all data columns of the internal table to
	 * handle resize notifications of the columns. This method updates the
	 * ConfigurationColonne width corresponding to the resized column and also
	 * updates the size of the last column (dummy column) that is added in order
	 * to have always a scroll in scrollable table (another workaround for SWT
	 * table)
	 * 
	 * @param columns
	 */
	protected void addControlListenerToColumns(List<TableColumn> columns) {
		for (TableColumn col : columns) {
			col.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					if (event.widget instanceof TableColumn) {
						TableColumn col = (TableColumn) event.widget;
						int width = col.getWidth();
						String colName = (String) col.getData();
						ConfigurationColonne colCfg = gestionaireVue.getColonne(colName);
						
						if (colCfg != null) {
							int ancienneLargeurColonne  = colCfg.getLargeur();
							colCfg.setLargeur(width);	
							
							if(ancienneLargeurColonne != width) {
								gestionaireVue.setChanged(true);
							}
						}
					}
				}
			});
		}
	}

	/**
	 * Updates the columns width for the columns that have auto flag
	 * 
	 * @param columns, the columns to update
	 */
	protected void updateAutoColumnsWidth(List<TableColumn> columns, int indexFixed) {
		int colWidth;
		int i = 0;
		String colName;
		
		for (TableColumn tblCol : columns) {
			colName = this.columnNames.get(i);
			
			// Check the width
			colWidth = this.gestionaireVue.getColonneLargeur(colName);

			// Sets the width
			if (colWidth <= 0) {
				tblCol.setWidth(this.gestionaireVue.getColonne(colName).getLargeurCalculee());
			}
			
			i++;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		this.data = null;
		
		
		disposeTable();
		super.dispose();
		ActivatorData.getInstance().removeDataListener(this);
		ActivatorData.getInstance().removeRepereListener(this);
	}

	/**
	 * Dispose the table
	 */
	public synchronized void disposeTable() {
		if (this.tblFix != null) {
			synchronized (this.tblFix) {
				
				try {
					if (!this.tblFix.isDisposed()) {
						if (!this.tblFix.getFixedTable().isDisposed()) {
							this.tblFix.getFixedTable().dispose();
						}
						
						this.tblFix.dispose();
						((FixedColumnTableViewerDetail) this.tblFix).getTreeDetailViewer().getTree().dispose();
						
						TreeKVBDetailViewer ttvKVB = ((FixedColumnTableViewerDetail) this.tblFix).getTreeKVBDetailViewer();
						
						if (ttvKVB != null) {
							ttvKVB.getTree().dispose();
						}
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					this.tblFix = null;
				}
			}
		}
		
		if (this.fctvd != null) {
			synchronized (this.fctvd) {
				try {
					if (!this.fctvd.isDisposed()) {
						if (!this.fctvd.getFixedTable().isDisposed()) {
							this.fctvd.getFixedTable().dispose();
						}
						
						this.fctvd.dispose();
						((FixedColumnTableViewerDetail)this.fctvd).getFixedTable().dispose();
					}
				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					this.fctvd = null;
				}
			}
		}
		
		if (this.labelProvider != null) {
			this.labelProvider.dispose();
			this.labelProvider = null;
		}
		
		if (this.contentProvider != null) {
			this.contentProvider.dispose();
			this.contentProvider = null;
		}
	}

	/**
	 * Reloads a table whose data did not changed
	 * 
	 */
	public void reloadTable() {
		loadTable(true);
		Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
		
		if (!(elements.length > 0)) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING);
			msgBox.setText("");
			msgBox.setMessage(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.32"));
			msgBox.open();
		}
	}

	/**
	 * Notification that a repere was changed
	 * 
	 * @param reperes, the list of reperes that were changed
	 */
	public void onRepereAdded(TypeRepere... reperes) {
		if (reperes != null) {
			int rowIdx = 0;
			int topIndex=0;
			boolean findIndice = true;

			try {
				int selId =ActivatorData.getInstance().getSelectedMsg().getMessageId();
				rowIdx = getRowIndexForMessageId(selId, null);
				topIndex=this.tblFix.fixedTable.getTopIndex();
			} catch (Exception e) {
				findIndice = false;
			}

			loadTable(true);

			if (findIndice) {
				this.tblFix.setSelection(rowIdx, null);
				this.tblFix.setTopIndex(topIndex);
				handleLineSelection(rowIdx);
			}

		}
	}

	/**
	 * 
	 * @param reperes, the list of reperes that were changed
	 */
	public void onRepereRemoved(TypeRepere... reperes) {
		if (reperes != null) {
			loadTable(true);
		}
	}

	/**
	 * Reloads the table due to a change of the data
	 */
	public void onDataChange() {
		this.gestionaireVue.clear();
		
		// If file becomes empty, must dispose table in  the view
		if (ActivatorData.getInstance().isFileEmpty())
		{
			disposeTable();			
			return;
		}
		// If there are distance or time corrections then the colums should be added to the table
		if (TableSegments.getInstance().isAppliedDistanceCorrections()) {
			this.gestionaireVue.ajouterRepere(TypeRepere.distanceCorrigee);
			this.gestionaireVue.ajouterRepere(TypeRepere.vitesseCorrigee);
		}
		
		if (TableSegments.getInstance().isAppliedTempCorrections()) {
			this.gestionaireVue.ajouterRepere(TypeRepere.tempsCorrigee);
		}
		
		// Loads the table and to not scroll
		loadTable(false);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public final void propertyChange(final PropertyChangeEvent evt) {
		// We just react on add events
		if ("MSG_SELECT".equals(evt.getPropertyName())) {
			this.tblFix.getFixedTable().setSelection(((Integer) evt.getNewValue()).intValue());
		}
	}

	public void setFocus() {
		if (this.tblFix != null) {
			this.tblFix.setFocus();
		}
	}

	/**
	 * Sets the manager of the vuew
	 * 
	 * @param viewMng
	 */
	public void setGestionnaireVue(GestionnaireVueListeBase viewMng) {
		this.gestionaireVue = viewMng;
	}

	/**
	 * Creates a tooltip for a table cell when the mouse is stopped over a cell.
	 * The tooltip will show the label of that cell
	 */
	protected void createCellTooltip() {
		// Disable native tooltip
		this.tblFix.getFixedTable().setToolTipText("");

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				
				switch (event.type) {
					case SWT.MouseDown:
						Event e = new Event();
						e.item = (TableItem) label.getData("_TABLEITEM");
						Table table = (Table) e.widget;
	
						// Assuming table is single select, set the selection as if the mouse down event went through to the table
						table.setSelection(new TableItem[] { (TableItem) e.item });
						table.notifyListeners(SWT.Selection, e);
						shell.dispose();
						table.setFocus();
						break;
					case SWT.MouseExit:
						shell.dispose();
						break;
				}
			}
		};

		Listener tableListener = new Listener() {
			Shell tip = null;
			Label label = null;

			public void handleEvent(Event event) {
				try {
					switch (event.type) {
					case SWT.Dispose:
					case SWT.KeyDown:
					case SWT.MouseMove: {
						if (this.tip == null) {
							break;
						}
						
						this.tip.dispose();
						this.tip = null;
						this.label = null;
						break;
					}
					case SWT.MouseHover: {
						Table table = (Table) event.widget;
						boolean isFixed = VueListe.this.tblFix.getFixedTable() == table;
						TableItem item = null;

						if (isFixed) {
							item = VueListe.this.tblFix.getFixedTable().getItem(new Point(event.x, event.y));
						} else {
							item = VueListe.this.tblFix.getScrollingTable().getItem(new Point(event.x, event.y));
						}
						
						if (item == null) {
							return;
						}

						int colNo = getColumnNumber(new Point(event.x, event.y), table, item);

						if (item != null && item.getText(colNo).length() > 0) {
							if (this.tip != null && !this.tip.isDisposed()) {
								this.tip.dispose();
							}
							
							// Create the shell to display the tooltip
							this.tip = new Shell(table.getDisplay().getActiveShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
							this.tip.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
							FillLayout layout = new FillLayout();
							layout.marginWidth = 2;
							this.tip.setLayout(layout);
							
							Message msg = (Message) ((Row) item.getData()).getData();
							String strMarqueur = null;
							
							if (ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msg.getMessageId()) != null) {
								strMarqueur = ((Marqueur) ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msg.getMessageId())).getCommentaire();
							}

							try {
								if (isFixed && getColumnNames(colNo-1).equalsIgnoreCase(GestionnaireVueListeBase.FLAG_COL_NAME_STR)) {
									table.setToolTipText(strMarqueur);
								} else {
									table.setToolTipText(item.getText(colNo));
								}
							} catch (Exception e) {
								table.setToolTipText("");
							}

							// Add listeners for mouse exit and mouse down
							try {
								this.label.addListener(SWT.MouseExit, labelListener);
								this.label.addListener(SWT.MouseDown, labelListener);

								// Get the coordinates where to display the tooltip
								Point size = this.tip.computeSize(SWT.DEFAULT,SWT.DEFAULT);

								// Returns the coordinates relativ to the table
								Rectangle rect = item.getBounds(0);
								Rectangle rect1 = table.getClientArea();

								// Get the coordinates relative to the display
								Point pt = table.toDisplay(rect.x, rect.y);
								Point pt1 = table.toDisplay(rect1.x, rect1.y);
								this.tip.setBounds(pt1.x + event.x + 12, pt.y + 20, size.x, size.y);
								this.tip.setVisible(true);
							} catch (NullPointerException e) {}
						} else {
							table.setToolTipText("");
						}
					}
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		};
		
		// Adds listeners
		this.tblFix.addListener(SWT.Dispose, tableListener);
		this.tblFix.addListener(SWT.KeyDown, tableListener);
		this.tblFix.addListener(SWT.MouseMove, tableListener);
		this.tblFix.addListener(SWT.MouseHover, tableListener);
	}

	/**
	 * Gets the number of the column where we can find the given point in the specified table
	 * 
	 * @param point
	 * @param table
	 * @return
	 */
	protected int getColumnNumber(Point point, Table table, TableItem item) {
		int colNo = 0;
		int size = table.getColumnCount();
		Rectangle rect;
		
		for (; colNo < size; colNo++) {
			rect = item.getBounds(colNo);
			
			if (rect.contains(point)) {
				break;
			}
		}
		return colNo;
	}

	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return this.columnNames;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnNames(int pos) {
		return this.columnNames.get(pos);
	}

	public FixedColumnTableViewerDetail getFctvd() {
		return fctvd;
	}
	
	/**
	 * Returns a list of labels for the columns (the titles of the columns)
	 * These differ from the column names (that are in the internal array) as
	 * these are the "user names" for the columns while columnNames array
	 * contains the unique identifiers of the columns (this is because columns
	 * configurations are saved but the language can also change)
	 * 
	 * @return
	 */
	public List<String> getColumnLabels() {
		List<String> colLabels = new ArrayList<String>(this.columnNames.size());
		
		for (String colName : this.columnNames) {
			colLabels.add(GestionnaireVueListeBase.getDisplayLabelForColumn(this.gestionaireVue.getColonne(colName), false));
		}
		
		return colLabels;
	}

	/**
	 * @return the data
	 */
	public VueData getData() {
		return this.data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(VueData data) {
		this.data = data;
	}

	/**
	 * @return the tblFix
	 */
	public FixedColumnTableVueListe getTable() {
		return this.tblFix;
	}


	/**
	 * @param tblFix : the tblFix to set
	 */
	public void setTable(FixedColumnTableVueListe table) {
		this.tblFix = table;
	}

	/**
	 * Get a row index by the id of the message id associated to that row
	 * 
	 * @param msgId : the searched message ID
	 * @return the found row index or -1 if no such message ID found in rows
	 */
	protected int getRowIndexForMessageId(int msgId, DescripteurVariable descripteurVariable) {
		IStructuredContentProvider tblContentProvider = (IStructuredContentProvider) this.tblFix.getContentProvider();
		Object[] elements = tblContentProvider == null ? new Object[0] : tblContentProvider.getElements(null);
		int rowId;
		Object rowData;
		int rowIdx = 0;
		int retIdx = -1;
		
		for (Object row : elements) {
			if (row instanceof Row) {
				rowData = ((Row) row).getData();
				
				if (rowData != null && rowData instanceof Message) {
					rowId = ((Message) rowData).getMessageId();
					
					if (rowId == msgId) {
						retIdx = rowIdx;
						
						if (this.ajoutAnnotationAction != null) {
							this.ajoutAnnotationAction.setEnabled(true);
						}
						
						break;
					}
				}
				
				rowIdx++;
			}
		}

		return retIdx;
	}

	/**
	 * Handler for notifications from other views that the selection changed in
	 * the source view
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// Positionnement offset
		if (this.tblFix != null) {
			if (GestionLineCommandParameters.getIndiceMsg() !=-1 && oneTimeOffset) {
				try {
					oneTimeOffset=false;
					int selId = GestionLineCommandParameters.getIndiceMsg();
	
					int rowIdx = getRowIndexForMessageId(selId,null);
	
					// Apr�s l'ouverture d'un fichier par ligne de commande, l'offset du nouveau fichier ouvert est doit �tre repositionn� sur la premi�re ligne de la vue. Pour cela on  utilise la valeur 0.
					if (selId == 0) {
						rowIdx = 0 ;
					}
	
					if (rowIdx==-1) {
						this.tblFix.setNotEquivalent(true);
						this.tblFix.setNotEquivalentForSearch(true);
					}
	
					while (selId > 0 && rowIdx == -1) {
						selId = selId - 1;
						rowIdx = getRowIndexForMessageId(selId, null);
						
						if (rowIdx != -1) {
							this.tblFix.setNotEquivalent(true);
						}
					}
					
					if (rowIdx == -1) {
						rowIdx = 0;
						this.tblFix.setNotEquivalent(true);
					}
	
					if (rowIdx != 0) {
						this.tblFix.setSelection(rowIdx, null);
						this.tblFix.setTopIndex(PositionMilieuViewer.getPosition(rowIdx));
						handleLineSelection(rowIdx);
						ActivatorData.getInstance().setSelectionVueListe(rowIdx);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				try {
					if (!this.tblFix.isNotEquivalentForSearch()) {
						this.tblFix.setNotEquivalent(false);
					} else {
						this.tblFix.setNotEquivalent(true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
	
				if ((selection instanceof MessageSelection) && (part != this) && !selection.isEmpty()) {
					if (!((MessageSelection) selection).getUserSendSelection()) {
						return;
					}
					
					int selId = ((MessageSelection) selection).getMessageId();
					int rowIdx = getRowIndexForMessageId(selId,null);
	
					while (selId > 0 && rowIdx == -1) {
						selId = selId - 1;
						rowIdx = getRowIndexForMessageId(selId,null);
						
						if (rowIdx != -1) {
							this.tblFix.setNotEquivalent(true);
						}
					}
					
					if (rowIdx == -1) {
						rowIdx = 0;
						this.tblFix.setNotEquivalent(true);
					}
	
					if (rowIdx != 0) {
						this.tblFix.setSelection(rowIdx,null);
						this.tblFix.setTopIndex(PositionMilieuViewer.getPosition(rowIdx));
						handleLineSelection(rowIdx);
					}
					ActivatorData.getInstance().setSelectionVueListe(rowIdx);
				}
			}
	
			createContextMenu();
		}
	}

	@Override
	public void setNotEquivalentForSearch(boolean b) {
		this.tblFix.setNotEquivalentForSearch(b);
	}

	/**
	 * Handler for notifications that the markers list changed
	 */
	public void marquersListeChangement(int[] msgIds, int[] oldMsgIds) {
		if (this.tblFix != null) {
			this.tblFix.refresh();
		}
	}

	/**
	 * Handler for notifications that a marker was added
	 */
	public void marqueurAjoutee(int msgId) {
		if (this.tblFix != null) {
			this.tblFix.refresh();
		}
	}

	/**
	 * Handler for notifications that a marker was deleted
	 */
	public void marqueurEffacee(int msgId) {
		if (this.tblFix != null) {
			this.tblFix.refresh();
		}
	}

	/**
	 * Updates the view title of this view
	 * 
	 */
	protected void updateViewInfoLabel() {
		String appliedFilter = this.gestionaireVue.getFiltreApplique();
		String tabName = this.initialPartName;
		boolean hasHiddenColumns = this.gestionaireVue.hasHiddenColumns();
		
		if (appliedFilter != null && !"".equals(appliedFilter.trim())) {
			tabName += " [" + appliedFilter + "]";
		} else {
			String newName=getDefaultFilterName();
			
			if (newName != null) {
				tabName += newName;
			}
		}
		
		if (hasHiddenColumns) {
			tabName += com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.19");
		}
		
		Message msg = getMessageFromCurrentSelection();
		
		if (msg != null) {
			String correctionsStr = DataViewsUtil.getCorrectionsTitleString(msg);
			
			if (correctionsStr != null) {
				tabName += correctionsStr;
			}
			
			// Issue 735 :
			((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
		}
		
		this.setPartName(tabName);
	}

	/**
	 * Returns the message from the current selection
	 * @return the message or null if not supported
	 */
	protected Message getMessageFromCurrentSelection() {
		Message message = null;
		
		if(this.tblFix != null){
			int selIdx = this.tblFix.getSelectionIndex();
			Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
			
			if (selIdx >= 0 && selIdx < elements.length) {
				Object tblItem = elements[selIdx];
				
				if (tblItem instanceof Row) {
					Object crtData = ((Row) tblItem).getData();
					
					if (crtData instanceof Message) {
						message = (Message) crtData;
					}
				}
			}
		}
		
		return message;
	}

	/**
	 * Updates the information current selection MessageSelection object that is
	 * notified to other views when a double click is performed
	 * 
	 */
	protected void updateSelectionMessage(boolean refreshTable) {
		// When a double click
		
		currentSelection = new MessageSelection();
		
		int selIdx = this.tblFix.getSelectionIndex();
		
		Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
		
		if (selIdx < 0 || selIdx >= elements.length) {
			return;
		}

		Message msg = getMessageFromCurrentSelection();
		
		if (msg != null && refreshTable) {
			((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
			this.tblFix.refreshTable(null);
		}
		
		handleLineSelection(selIdx); // Additional operations after the selection (update title, for example)
	}

	/**
	 * Scrolls to a previously saved selection
	 * 
	 */
	protected void scrollToPreviousSelection() {
		if (!this.currentSelection.isEmpty()) {
			int prevSelMsgIdx = ((MessageSelection) this.currentSelection).getMessageId();
			int rowIdx = getRowIndexForMessageId(prevSelMsgIdx, null);
			this.tblFix.setNotEquivalent(false);
			
			while (prevSelMsgIdx > 0 && rowIdx == -1) {
				prevSelMsgIdx = prevSelMsgIdx - 1;
				rowIdx = getRowIndexForMessageId(prevSelMsgIdx,null);
				
				if (rowIdx != -1) {
					this.tblFix.setNotEquivalent(true);
				}
			}
			
			prevSelMsgIdx = ((MessageSelection) this.currentSelection).getMessageId();
			
			while (prevSelMsgIdx > 0 && rowIdx == -1) {
				prevSelMsgIdx = prevSelMsgIdx + 1;
				rowIdx = getRowIndexForMessageId(prevSelMsgIdx, null);
				
				if (rowIdx != -1) {
					this.tblFix.setNotEquivalent(true);
				}
			}
			
			if (rowIdx != -1) {
				this.tblFix.setSelection(rowIdx, null);
				handleLineSelection(rowIdx);
			}
		}
	}

	/**
	 * Method for notification when has to be selected a marquer
	 * 
	 * @param next, if the next or previous marquer should be selected
	 */
	public String onSelectedMarquerNomChange(boolean next) {
		AMarqueur selMarquer = null;

		// Check for the current selected message
		Row row = (Row) this.tblFix.getSelection()[0].getData();
		int crtSelMsgId = ((Message) row.getData()).getMessageId();
		
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}

		boolean marqueurSelectionne = false;
		
		// Gets the marquers manager
		GestionnaireMarqueurs marquersGest = ActivatorData.getInstance().getGestionnaireMarqueurs();
		boolean marqueurTrouve = false;

		if (marquersGest != null) {
			int[] ids = marquersGest.getMarqueursIds();
			int nbMarqueurs = ids.length;
			
			// If current selected message is after the last marquer
			if (crtSelMsgId > ids[nbMarqueurs - 1]) {
				if (!next) {
					int j = nbMarqueurs - 1;
					// On recherche le bon marqueur en remontant dans la liste
					
					while (!marqueurTrouve && j >= 0) {
						selMarquer = marquersGest.getMarqueurParId(ids[j]);
						
						if (selMarquer != null	&& getRowIndexForMessageId(selMarquer.getIdMessage(), null) != -1) {
							marqueurTrouve = true;
						}
						
						j--;
					}
				}
			} else {
				if (!next) {
					if (crtSelMsgId > ids[0]) {
						int j = nbMarqueurs - 1;
						
						// on recherche le bon marqueur en remontant dans la liste
						while (!marqueurTrouve && j >= 0) {
							selMarquer = marquersGest.getMarqueurParId(ids[j]);
							
							if (ids[j] < crtSelMsgId && selMarquer != null && getRowIndexForMessageId(selMarquer.getIdMessage(),null) != -1) {
								marqueurTrouve = true;
							}
							
							j--;
						}
					}
				} else {
					if (crtSelMsgId < ids[nbMarqueurs - 1]) {
						int j = 0;
						// on recherche le bon marqueur en descendant dan la liste
						
						while (!marqueurTrouve && j < nbMarqueurs) {
							selMarquer = marquersGest.getMarqueurParId(ids[j]);
							
							if (ids[j] > crtSelMsgId && selMarquer != null && getRowIndexForMessageId(selMarquer.getIdMessage(),null) != -1) {
								marqueurTrouve = true;
							}
							j++;
						}
					}
				}
			}
			
			if (marqueurTrouve) {
				// on se place sur la bonne ligne
				int rowIdx = getRowIndexForMessageId(selMarquer.getIdMessage(), null);
				this.tblFix.setSelection(rowIdx,null);
				this.tblFix.setTopIndex(rowIdx);
				handleLineSelection(rowIdx);
			} else {
				marqueurSelectionne = true;
			}
		}

		if (marqueurSelectionne) {
			// if no marquer found display a message
			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.21")); //$NON-NLS-1$
			msgBox.open();
		}
		return selMarquer != null ? selMarquer.getNom() : ""; //$NON-NLS-1$
	}

	/**
	 * Method for notification when has to be selected a marquer
	 * 
	 * @param comment, the comment that should be searched for
	 * @param next, if the next or previous marquer should be selected
	 */
	public void onSelectedMarquerCommentChange(String comment, boolean next) {
		AMarqueur selMarquer = null;

		// Search for the current selcted message
		Row row = (Row) this.tblFix.getSelection()[0].getData();
		int crtSelMsgId = ((Message) row.getData()).getMessageId();
		
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}
		
		boolean isError = false;
		
		// Get the marquer manager
		GestionnaireMarqueurs marquersGest = ActivatorData.getInstance().getGestionnaireMarqueurs();
		
		if (marquersGest != null) {
			int[] ids = marquersGest.getMarqueursIds();
			Marqueur m = null;

			// If the selection is on a message after the last marquer
			if (crtSelMsgId > ids[ids.length - 1]) {
				if (!next) {
					// Selected message is after all the marquers
					for (int i = ids.length - 1; i >= 0; i--) {
						m = (Marqueur) marquersGest.getMarqueurParId(ids[i]);
						
						if (m.getCommentaire().indexOf(comment) >= 0) {
							selMarquer = m;
							break;
						}
					}
				}
			} else {
				// Selection is before last marquer
				for (int i = 0; i < ids.length; i++) {
					if (ids[i] >= crtSelMsgId) {
						if (next) {
							// If searching next marquer
							if (ids[i] == crtSelMsgId) {
								if (i < ids.length - 1) {
									for (; i < ids.length; i++) {
										m = (Marqueur) marquersGest.getMarqueurParId(ids[i + 1]);
										
										if (m.getCommentaire().indexOf(comment) >= 0) {
											selMarquer = m;
											break;
										}
									}
								}
							} else {
								for (; i < ids.length; i++) {
									m = (Marqueur) marquersGest.getMarqueurParId(ids[i]);
									
									if (m.getCommentaire().indexOf(comment) >= 0) {
										selMarquer = m;
										break;
									}
								}
							}
						} else {
							if (i > 0) {
								for (; i >= 0; i--) {
									m = (Marqueur) marquersGest.getMarqueurParId(ids[i - 1]);
									
									if (m.getCommentaire().indexOf(comment) >= 0) {
										selMarquer = m;
										break;
									}
								}
							}
						}
						
						break;
					}
				}
			}
			
			if (selMarquer != null) {
				// Have to select a marquer
				int rowIdx = getRowIndexForMessageId(selMarquer.getIdMessage(), null);
				this.tblFix.setSelection(rowIdx, null);
				this.tblFix.setTopIndex(rowIdx);
				handleLineSelection(rowIdx);
			} else {
				isError = true;
			}
		}
		
		if (isError) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText("");
			msgBox.setMessage(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.24"));
			msgBox.open();
		}
	}

	/**
	 * Gets the selection index
	 * @return
	 */
	public int getSelectionId() {
		int index = -1;
		
		if (this.data != null && this.data.getDataTable() != null) {
			index = this.tblFix.getSelectionIndex();
		}
		
		return index;
	}

	/**
	 * Handles the
	 * @param rowIdx
	 */
	protected void handleLineSelection(int rowIdx) {
		updateViewInfoLabel();
	}

	/**
	 * Returns the top composite for this view
	 */
	@Override
	public Composite getContenu() {
		return this.top;
	}

	/**
	 * Creates the context menu for this view
	 * 
	 */
	protected void createContextMenu() {
		if (this.menuSelListener != null) {
			try {
				this.popupMenu = new Menu(top);
			} catch (Exception e) {				
				return;
			}

			MenuItem item = new MenuItem(popupMenu, SWT.NONE);
			item.setText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.28"));
			item.setData(TypeMenuOptions.DISPLAY_COLUMN_MNG);
			item.addListener(SWT.Selection, menuSelListener);

			item = new MenuItem(popupMenu,SWT.SEPARATOR);

			item = new MenuItem(popupMenu, SWT.NONE);
			item.setText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.29"));
			item.setData(TypeMenuOptions.DISPLAY_FILTER_VIEW);
			item.addListener(SWT.Selection, menuSelListener);

			AFiltreComposant appliedFiltre = getAppliedFilter();
			OrdonnerFiltre.getInstance().setFiltreSelectListe(appliedFiltre);
			listeFiltreOrd = OrdonnerFiltre.getInstance().getListeFiltreListe();
			if(listeFiltreOrd == null) listeFiltreOrd = new ArrayList<AFiltreComposant>();

			listeFiltreOrd.clear() ; // To prevent from filter renaming + Issu 854
			
			AGestionnaireFiltres gestFiltres = (AGestionnaireFiltres) ActivatorVueListe.getDefault().getFiltresProvider().getGestionnaireFiltres();
			AFiltreComposant listeFiltre = gestFiltres.getListeFiltres();
			int nbFiltre =  listeFiltre.getEnfantCount();
			
			for(int i = 0; i < nbFiltre; i++){
				AFiltreComposant fCourant = listeFiltre.getEnfant(i);
				
				if (!listeFiltreOrd.contains(fCourant)) {
					listeFiltreOrd.add(fCourant);
				}
			}

			item = new MenuItem(popupMenu, SWT.CHECK);
			item.setText(Messages.getString("VueListe.13"));
			item.setData(TypeMenuOptions.NO_FILTER);
			item.addListener(SWT.Selection, menuSelListener);
			boolean condition = OrdonnerFiltre.getInstance().getFiltreSelectListe() == null;
			item.setSelection(condition);
			item.setEnabled(!condition);

			createMenuFiltre(item,listeFiltreOrd);

			item = new MenuItem(popupMenu,SWT.SEPARATOR);

			item = new MenuItem(popupMenu, SWT.NONE);
			item.setText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.33"));
			item.setData(TypeMenuOptions.ADD_ANNOTATION);
			item.addListener(SWT.Selection, menuSelListener);
			item.setEnabled(!GestionLineCommandParameters.isAnnot_Lect_seule());

			// Add more actions here if needed
			top.setMenu(this.popupMenu);
			tblFix.setMenu(this.popupMenu);
		}
	}

	private enum TypeMenuOptions {
		DISPLAY_COLUMN_MNG, DISPLAY_FILTER_VIEW, ADD_ANNOTATION, NO_FILTER
	}

	public void createMenuFiltre(MenuItem item,List<AFiltreComposant> listeFiltreOrd){
		AGestionnaireFiltres gestFiltres = (AGestionnaireFiltres) ActivatorVueListe.getDefault().getFiltresProvider().getGestionnaireFiltres();
		int limite = gestFiltres.getLimiteBridageFiltre();
		int nbFiltre = listeFiltreOrd.size();
		List<String> listFiltres=new ArrayList<String>();
		
		for (int i = 0; i < nbFiltre; i++) {
			AFiltreComposant fCourant = listeFiltreOrd.get(i);
			
			if ((fCourant != null)&&(ActivatorVueListe.getDefault().getFiltresProvider().filtrevalide(fCourant))) {
				if (listFiltres.size()<limite  && (!listFiltres.contains(fCourant.getNom()))) {
					item = new MenuItem(popupMenu, SWT.CHECK);
					item.setText(fCourant.getNom());
					item.setData(fCourant);
					
					item.addListener(SWT.Selection, new Listener() {
						@Override
						public void handleEvent(Event event) {
							MenuItem menuItem = (MenuItem)event.widget;
							AFiltreComposant f = (AFiltreComposant) menuItem.getData();
							List<AFiltreComposant> liste = OrdonnerFiltre.getInstance().getListeFiltreListe();
							
							if (liste.contains(f)) {
								ActivatorVueListe.getDefault().getFiltresProvider().setAppliedFilterName(f.getNom());
							} else {
								ActivatorVueListe.getDefault().getFiltresProvider().setAppliedFilterName("");
							}
							
							new ApplyFiltreAction().runWithEvent(null);
						}
					});
					
					boolean condition = OrdonnerFiltre.getInstance().getFiltreSelectListe() == fCourant;
					item.setSelection(condition);
					item.setEnabled(!condition);
					listFiltres.add(fCourant.getNom());
				}
			}			
		}
	}

	/**
	 * M�thode pour r�cup�rer le filtre courant appliqu�
	 * @return filtre appliqu� ou null
	 */
	public AFiltreComposant getAppliedFilter(){
		AbstractProviderFiltre provider = ActivatorVueListe.getDefault().getFiltresProvider();
		AGestionnaireFiltres filtreMng = (AGestionnaireFiltres) ActivatorVueListe.getDefault().getFiltresProvider().getGestionnaireFiltres();
		String appliedFilter = provider.getAppliedFilter();
		return filtreMng.getFiltre(appliedFilter);
	}
	
	@Override
	public void onSearchVariable(DescripteurVariable descrVar,
			String stringValue, String value, Operation op,
			boolean next) {
		this.onSearchVariable(descrVar, value, op, next);		
	}
}  
