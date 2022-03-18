package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.actions.dialogs.RechercherMarqueurAction;
import com.faiveley.samng.principal.ihm.actions.filtre.ShowFilterWindowAction;
import com.faiveley.samng.principal.ihm.actions.vue.SetReferenceAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchMarquerListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.DataViewsUtil;
import com.faiveley.samng.principal.ihm.vues.IMarqueursListener;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.VariableExplorationUtils;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions.AjouterAnnotationAction;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.IVbvChangeListener;
import com.faiveley.samng.principal.ihm.vues.vuetoolbar.IVueToolbar;
import com.faiveley.samng.principal.sm.controles.util.XMLName;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnaireVariablesComposee;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.GraphicConstants;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.marqueurs.Marqueur;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.GraphiqueFiltresProvider;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.VueGraphiqueFiltre;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.actions.ApplyFiltreAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.PositionReferenceZero;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.VitesseCorrigeePresenteDansFiltre;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GestionnaireGraphesNotifications;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.IGrapheCursorListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.Curseur;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.ValuedCursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul.InfosBullesMarqueurs;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures.RupturesLegendePosition;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.AZoomComposant;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.AnnulerZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomX;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.AxeDistanceAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.AxeTempsAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.ChangeAxeTypeAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.DigitalRef0Action;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.DisplayDistanceBreaksAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.DisplayLegendAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.DisplayMarkersAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.DisplayTimeBreaksAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.PointModeAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.RechercherVariableGraphiqueAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.ShowVueFiltresGraphiqueAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.SteppedGraphAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.WhiteBackgroundAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions.ZoomAction;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites.GraphesAndInfoComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites.LegendeComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeMode;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;






/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueGraphique extends AbstractSelectionProviderVue implements 
IDataChangedListener, ISelectionListener, 
IMarqueursListener, IGrapheCursorListener,
IRepereChangedListener, ICapturable, IVueToolbar,
ISearchMarquerListener, ISelectionProvider, PropertyChangeListener, 
IVbvChangeListener,ISearchVariableListener{
	
	private boolean usesShortNames;
	
	public static final String ID = "SAMNG.Vue.Graphique"; //$NON-NLS-1$

	boolean oneTimeOffset=true;//le positionnement offset ne se fait qu'une fois

	/**
	 * D�claration des actions
	 */

	public AjouterAnnotationAction ajouterAnnotationAction;
	public CapturerVueAction capturerVueAction;
	public ImprimerVueAction imprimerVueAction;
	public AxeTempsAction axeTempsAction;
	public AxeDistanceAction axeDistanceAction;
	public ZoomAction previousZoomAction;
	public ZoomAction nextZoomAction;
	public ZoomAction manualZoomAction;
	public SetReferenceAction setRefAction;
	public ShowFilterWindowAction ouvrirVueFiltreAction;
	public ArrayList<Action> listeActionsAuditrice;
	public RechercherMarqueurAction marqueurSuivantAction;
	public RechercherMarqueurAction marqueurPrecedentAction;
	public RechercherVariableGraphiqueAction rechercherVariableAction;

	protected List<Action> listeActions = new ArrayList<Action>();


	//	ToolBar
	protected IActionBars bars;

	public static SashForm sashForm = null;
	public static GraphesAndInfoComposite leftPanelComposite = null;
	public static Composite rightPanelComposite = null;
	public static Composite mainComposite = null;
	public MenuSelectionListener menuSelListener = new MenuSelectionListener();
	public Menu popupMenu;
	public String initialPartName;

	public Action synchroVuesAction;

	// Sebastien
	// the data
	public static VueData data = null;
	
	private List<AFiltreComposant> listeFiltreOrd;
	
	/** */
	public VueGraphique() {
		if (data == null || data.isEmpty() ) {
			VueGraphique.data = ActivatorData.getInstance().getVueData();
		}

		ActivatorData.getInstance().getProviderVBVs().addPropertyChangeListener(this);
		this.setPartName(Messages.getString("VueGraphique.53")); //$NON-NLS-1$
		//BarreProgression.getInstance().setName(Messages.getString("VueGraphique.53"));
		//ActivatorData.getInstance().getProviderVBVs().addVbvListener(this);
		GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();
		this.usesShortNames = viewMng.usesShortNames();
		ActivatorVueGraphique.getDefault().setUsesShortNames(usesShortNames);
	}
	private void close(){
		if (leftPanelComposite!=null) {
			leftPanelComposite.dispose();
		}
		if (rightPanelComposite!=null) {
			rightPanelComposite.dispose();
		}
		if (sashForm!=null) {
			sashForm.dispose();
		}
		if (mainComposite!=null) {
			mainComposite.dispose();
		}
	}

	public void setPartNameExtern(String partName){
		this.setPartName(partName);
	}

	public void initPartListener() {

		getSite().getPage().addPartListener( new IPartListener2() {

			public void partActivated(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�
			}

			public void partBroughtToTop(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�
			}

			public void partClosed(IWorkbenchPartReference partRef) {
				TailleVue.getInstance().setX(-1);
				TailleVue.getInstance().setY(-1);
				if (partRef.getId().equals(ID)) {
					ActivatorVueGraphique.getDefault().saveConfigurationVue();
					getSite().getPage().removePartListener(this);
					close();
				}
			}

			public void partDeactivated(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�
			}

			public void partHidden(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�
			}

			public void partInputChanged(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�

			}

			public void partOpened(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�
			}

			public void partVisible(IWorkbenchPartReference partRef) {
				// TODO Raccord de m�thode auto-g�n�r�
			}
		});
	}

	/**
	 * @param parent
	 *            1
	 */
	public final void createPartControl(final Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		try {
			if (!ActivatorData.getInstance().getVueData().isEmpty()) {
				this.currentSelection = new MessageSelection();
				this.initialPartName = Messages.getString("VueGraphique.53"); //$NON-NLS-1$
				mainComposite = new Composite(parent, SWT.NONE);
				mainComposite.setLayout(new FillLayout());
				ActivatorData.getInstance().getPoolDonneesVues().put("mutex_tooltip_annotation","false"); //$NON-NLS-1$ //$NON-NLS-2$
				ActivatorData.getInstance().getPoolDonneesVues().put("mutex_tooltip_annotation_legende","false"); //$NON-NLS-1$ //$NON-NLS-2$
				ActivatorData.getInstance().getPoolDonneesVues().put(new String("ajouterUnMarqueur"), false);
				ActivatorData.getInstance().getPoolDonneesVues().put(new String("ReferenceZero"), false);
				RupturesLegendePosition.getInstance().setMutex(false);

				Curseur.getInstance().setCurseurVisible(false);
				checkFilter();
				//int nbSegValidDistance=verifierSegmentsDistance();

				//				VueWaitBar.getInstance().setRect(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getBounds());
				//				VueWaitBar.getInstance().start();

				//				VueProgressBar.getInstance().start();
				//				VueProgressBar.getInstance().fin=2;
				//				VueProgressBar.getInstance().setCourant(1);			
				//TableSegments.getInstance().setAuMoinsUnSegmentDistanceValide(nbSegValidDistance>0);
				makeActions();
				createSashForm(mainComposite);
				ActivatorData.getInstance().addDataListener(this);
				ActivatorData.getInstance().addRepereListener(this);
				ActivatorData.getInstance().getVueData().addMarkersListener(this);
				GestionnaireGraphesNotifications.addGrapheCursorListener(this);  

				//Register this as Selection provider and as selection listener
				getSite().setSelectionProvider(this);
				ISelectionService selService = getSite().getWorkbenchWindow().getSelectionService();
				selService.addPostSelectionListener(this);

				//initialisation de la toolbar
				this.bars = getViewSite().getActionBars();
				if(ActivatorData.getInstance().getVueData().isEmpty())
					this.setPartName(Messages.getString("VueGraphique.53")); //$NON-NLS-1$

				//ajout des actions � la toolbar
				ajoutActionToolBar(synchroVuesAction);
				ajoutActionToolBar(setRefAction);
				ajoutActionToolBar(ouvrirVueFiltreAction);
				ajoutActionToolBar(ajouterAnnotationAction);
				ajoutActionToolBar(marqueurPrecedentAction);
				ajoutActionToolBar(marqueurSuivantAction);	
				ajoutActionToolBar(rechercherVariableAction);
				ajoutActionToolBar(axeTempsAction);
				//if (TableSegments.getInstance().isAuMoinsUnSegmentDistanceValide()) {
				ajoutActionToolBar(axeDistanceAction);
				//}			
				ajoutActionToolBar(previousZoomAction);
				ajoutActionToolBar(nextZoomAction);
				ajoutActionToolBar(manualZoomAction);
				ajoutActionToolBar(imprimerVueAction);
				ajoutActionToolBar(capturerVueAction);

				AnnulerZoom.getInstance().setZoomAnnule(false);

				this.initPartListener();
				
				if (GestionLineCommandParameters.getIndiceMsg()!=-1 && oneTimeOffset)
					selectionChanged(null, null) ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** D�claration d'action */
	public void makeActions(){

		//r�cup�ration de la fenetre active
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();

		listeActionsAuditrice = new ArrayList<Action>();

		this.setRefAction = new SetReferenceAction(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.3"),   //$NON-NLS-1$
				com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_reference.png"));
		this.setRefAction.setEnabled(true);
		this.ajouterAnnotationAction = new AjouterAnnotationAction();
		this.ajouterAnnotationAction.setText(Messages.getString("VueGraphique.3")); //$NON-NLS-1$
		this.ajouterAnnotationAction.setToolTipText(Messages.getString("VueGraphique.4")); //$NON-NLS-1$
		this.ajouterAnnotationAction.setEnabled(true);
		this.ajouterAnnotationAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_ajouter_annotation.png")); //$NON-NLS-1$

		this.axeTempsAction= new AxeTempsAction();
		this.axeTempsAction.setText(Messages.getString("VueGraphique.5")); //$NON-NLS-1$
		this.axeTempsAction.setToolTipText(Messages.getString("VueGraphique.6")); //$NON-NLS-1$
		this.axeTempsAction.setImageDescriptor(com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique
				.getImageDescriptor("/icons/vueGraphique/vue_graphique_abscisse_temps.png")); //$NON-NLS-1$
		this.axeTempsAction.setId(ICommandIds.CMD_ABSCISSE_TEMPS);
		this.axeTempsAction.setActionDefinitionId(ICommandIds.CMD_ABSCISSE_TEMPS);
		this.axeTempsAction.setEnabled(!new ParseurAdapteur().inhiberAxeTempsVueGraphique());

		this.axeDistanceAction= new AxeDistanceAction();
		this.axeDistanceAction.setText(Messages.getString("VueGraphique.7")); //$NON-NLS-1$
		this.axeDistanceAction.setToolTipText(Messages.getString("VueGraphique.8")); //$NON-NLS-1$
		this.axeDistanceAction.setImageDescriptor(com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique
				.getImageDescriptor("/icons/vueGraphique/vue_graphique_abscisse_distance.png")); //$NON-NLS-1$
		this.axeDistanceAction.setEnabled(!new ParseurAdapteur().inhiberAxeDistanceVueGraphique());

		this.previousZoomAction = new ZoomAction(TypeMenuOptions.PREVIOUS_ZOOM);
		this.previousZoomAction.setText(Messages.getString("VueGraphique.9")); //$NON-NLS-1$
		this.previousZoomAction.setToolTipText(Messages.getString("VueGraphique.10"));		 //$NON-NLS-1$
		this.previousZoomAction.setImageDescriptor(com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique
				.getImageDescriptor("/icons/vueGraphique/vue_graphique_zoom_precedent.png")); //$NON-NLS-1$
		this.previousZoomAction.setId(ICommandIds.CMD_PREVIOUS_ZOOM);
		this.previousZoomAction.setActionDefinitionId(ICommandIds.CMD_PREVIOUS_ZOOM);

		this.nextZoomAction = new ZoomAction(TypeMenuOptions.NEXT_ZOOM);
		this.nextZoomAction.setText(Messages.getString("VueGraphique.11")); //$NON-NLS-1$
		this.nextZoomAction.setToolTipText(Messages.getString("VueGraphique.12")); //$NON-NLS-1$
		this.nextZoomAction.setImageDescriptor(com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique
				.getImageDescriptor("/icons/vueGraphique/vue_graphique_zoom_suivant.png")); //$NON-NLS-1$

		this.manualZoomAction = new ZoomAction(TypeMenuOptions.MANUAL_ZOOM);
		this.manualZoomAction.setText(Messages.getString("VueGraphique.13")); //$NON-NLS-1$
		this.manualZoomAction.setToolTipText(Messages.getString("VueGraphique.14")); //$NON-NLS-1$
		this.manualZoomAction.setImageDescriptor(com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique
				.getImageDescriptor("/icons/vueGraphique/vue_graphique_zoom_perso.png")); //$NON-NLS-1$

		this.capturerVueAction = new CapturerVueAction(window,Messages.getString("VueGraphique.15") ); //$NON-NLS-1$

		this.imprimerVueAction = new ImprimerVueAction(window,Messages.getString("VueGraphique.16") ); //$NON-NLS-1$

		this.ouvrirVueFiltreAction = new ShowFilterWindowAction(Messages.getString("VueGraphique.17"),  //$NON-NLS-1$
				VueGraphiqueFiltre.ID, ActivatorVueGraphique
				.getImageDescriptor("/icons/vueGraphique/vue_graphique_filtre.png")); //$NON-NLS-1$

		this.synchroVuesAction = new Action(){
			public void run(){
				upSelection();
			}
		};
		this.synchroVuesAction.setText(Messages.getString("VueGraphique.18")); //$NON-NLS-1$
		this.synchroVuesAction.setToolTipText(Messages.getString("VueGraphique.18")); //$NON-NLS-1$
		//this.synchroVuesAction.setToolTipText(Messages.getString("VueGraphique.19")); //$NON-NLS-1$
		this.synchroVuesAction.setEnabled(true);
		this.synchroVuesAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_synchro.png")); //$NON-NLS-1$

		rechercherVariableAction = new RechercherVariableGraphiqueAction(
				ICommandIds.CMD_OPEN_SEARCH_VAR,
				Messages.getString("VueGraphique.2"), //$NON-NLS-1$
				com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_rechercher_signal.png"), usesShortNames); //$NON-NLS-1$

		marqueurPrecedentAction = new RechercherMarqueurAction(ICommandIds.CMD_OPEN_SEARCH_MARQUER, Messages.getString("VueGraphique.52"),com.faiveley.samng.principal.ihm.Activator //$NON-NLS-1$
				.getImageDescriptor("/icons/toolBar/vues_commun_annotation_precedente.png"),false); //$NON-NLS-1$
		marqueurSuivantAction = new RechercherMarqueurAction(ICommandIds.CMD_OPEN_SEARCH_MARQUER, Messages.getString("VueGraphique.51"),com.faiveley.samng.principal.ihm.Activator //$NON-NLS-1$
				.getImageDescriptor("/icons/toolBar/vues_commun_annotation_suivante.png"),true); //$NON-NLS-1$

		if( listeActions.size()==0){
			listeActions.clear();
			listeActions = new ArrayList<Action>();
			ParseurAdapteur padapt=new ParseurAdapteur();
			if (!padapt.inhiberAxeTempsVueGraphique()) {
				listeActions.add(this.axeTempsAction);
			}
			if (!padapt.inhiberAxeDistanceVueGraphique()) {
				listeActions.add(this.axeDistanceAction);
			}

			listeActions.add(this.previousZoomAction);
			listeActions.add(this.manualZoomAction);
			listeActions.add(this.nextZoomAction);
			listeActions.add(this.capturerVueAction);
			listeActions.add(this.imprimerVueAction);
			listeActions.add(this.marqueurPrecedentAction);
			listeActions.add(this.marqueurSuivantAction);
		}
		listeActionsAuditrice.add(ajouterAnnotationAction);
		listeActionsAuditrice.add(setRefAction);
		listeActionsAuditrice.add(synchroVuesAction);
	}


	/**
	 * M�thode d'ajout d'une action dans la toolbar
	 * @param action
	 */
	public void ajoutActionToolMenuBar(Action action) {
		this.bars.getMenuManager().add(action);
	}

	/** */
	public void ajoutSeparateurToolBar() {
		bars.getMenuManager().add(new Separator());
	}

	/**
	 * M�thode d'ajout d'une action dans le menu
	 * @param action
	 */
	public void ajoutActionToolBar(Action action) {
		this.bars.getToolBarManager().add(action);
		//this.bars.getMenuManager().add(action);
	}

	protected void updateSelectionMessage() {
		Message msg = getMessageFromCurrentSelection();
		if (msg != null){
			((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
			ActivatorData.getInstance().setSelectedMsg(msg);
		}
	}

	public void upSelection() {
		// update selection

		//		updateSelectionMessage();
		((MessageSelection)currentSelection).setUserSentSelection(true);
		fireSelectionChanged(currentSelection);
		((MessageSelection)currentSelection).setUserSentSelection(false);
	}

	public void createSashForm(Composite parent) {
		try {
			FabriqueGraphe.gestionGraphesRenseignes();
			createContextMenu();
			sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
			sashForm.setBounds(new Rectangle(3, 4, 654, 294));

			int numGraphes = getFilterGraphesNr();
			if (numGraphes==0) {
				MessageBox msgBox = new MessageBox(getViewSite().getShell(), SWT.ICON_INFORMATION | SWT.OK);
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("VueGraphique.47") +  //$NON-NLS-1$
						" "+Messages.getString("VueGraphique.48"));  //$NON-NLS-1$
				msgBox.open();
				this.setPartName(Messages.getString("VueGraphique.53"));  //$NON-NLS-1$

				this.capturerVueAction.setVueVide(true);
				this.imprimerVueAction.setVueVide(true);
				for (Action action : listeActions) {
					if(action!=null)
						action.setEnabled(false);
				}
			}else{
				this.capturerVueAction.setVueVide(false);
				this.imprimerVueAction.setVueVide(false);
				for (Action action : listeActions) {
					if(action!=null)
						action.setEnabled(true);
				}
			}
			leftPanelComposite = null;
			rightPanelComposite = null;
			if(numGraphes>0){
				if(!ActivatorData.getInstance().getVueData().isEmpty())	{
					leftPanelComposite = new GraphesAndInfoComposite(sashForm, SWT.NONE, numGraphes);	
					((GraphesAndInfoComposite)leftPanelComposite).addPropertyChangeListener(this);
					rightPanelComposite = new LegendeComposite(sashForm, SWT.NONE, numGraphes, this.usesShortNames);
					((LegendeComposite)rightPanelComposite).addPropertyChangeListener(this);

					leftPanelComposite.setMenu(this.popupMenu);
					rightPanelComposite.setMenu(this.popupMenu);

					sashForm.setWeights(new int[] { 4, 1});
					displayRighPanel();
				}
			}
		} catch (Exception e) {
			System.out.println("pb createSashForm");
			e.printStackTrace();
		}
	}

	public void onDataChange() {
		this.setPartName(Messages.getString("VueGraphique.53")); //$NON-NLS-1$
		if(!ActivatorData.getInstance().getVueData().isEmpty())	{
			updateViewInfoLabel();
		}
		//		if (((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).getNomFichierParcoursBinaire()!=null){
		refresh();
		//		}
	}

	public void displayRighPanel() {
		GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();
		if(viewMng.isLegende()) {
			sashForm.setMaximizedControl(null);
		} else {
			sashForm.setMaximizedControl(leftPanelComposite);
		}
	}


	public static int getFilterGraphesNr() {
		int numGraphes = 0;
		AbstractProviderFiltre filterProvider = ActivatorVueGraphique.getDefault().getFiltresProvider();
		GestionnaireFiltresGraphique filterMng = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault()
				.getFiltresProvider().getGestionnaireFiltres();

		String appliedFilterName = filterProvider.getAppliedFilter();
		AFiltreComposant appliedFilter = filterMng.getFiltre(appliedFilterName);
		boolean filtreValide = ActivatorVueGraphique.getDefault().getFiltresProvider().verifierValiditeFiltre(appliedFilter);
		if (appliedFilter == null || !filtreValide) {
			appliedFilter = filterMng.getFiltreDefaut();
		}
		if (appliedFilter != null) {
			for (int i = 0; i < appliedFilter.getEnfantCount(); i++) {
				if (((GraphiqueFiltreComposite)(appliedFilter.getEnfant(i))).isActif() && 
						((GraphiqueFiltreComposite)(appliedFilter.getEnfant(i))).contientUneVariableRenseignee()) {
					numGraphes++;
				}
			}
		}
		if (numGraphes<=GraphicConstants.MAX_GRAPHICS_COUNT) {
			return numGraphes;
		}else{
			return GraphicConstants.MAX_GRAPHICS_COUNT;
		}
	}

	public void refresh() {
		try {
			if (!(leftPanelComposite == null && rightPanelComposite == null)) {
				leftPanelComposite.dispose();
				rightPanelComposite.dispose();
			}
			updateViewInfoLabel();

			sashForm.dispose();
			createSashForm(mainComposite);
			mainComposite.layout();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void changeSelec() {
		final IWorkbenchPart activePart = Activator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getActivePage().getActivePart();

		if (activePart instanceof ISelectionProvider) {
			ISelectionProvider selProvider = (ISelectionProvider) activePart;
			ISelection sel = selProvider.getSelection();
			if (!(sel instanceof MessageSelection))
				return;
			selectionChanged(activePart,sel);
		}		
	}

	public void redrawGraphes(boolean redrawCourbes) {
		try {
			leftPanelComposite.redrawGraphes(redrawCourbes);
		} catch (Exception e) {

		}
	}

	public void applyFilter() {
		checkFilter();
		refresh();
	}

	/**
	 * A shortcut method to obtain the current applied filter
	 * @return the applied filter or null if no filter is applied
	 */
	private AFiltreComposant getAppliedFilter() {
		AbstractProviderFiltre filterProvider = ActivatorVueGraphique.getDefault()
				.getFiltresProvider();
		GestionnaireFiltresGraphique filterMng = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault()
				.getFiltresProvider().getGestionnaireFiltres();
		String appliedFilterName = filterProvider.getAppliedFilter();
		return filterMng.getFiltre(appliedFilterName);
	}

	/**
	 * Checks the type of the current filter (if is an user filter or a default filter)
	 * If is the default filter, a message box is displayed.
	 *
	 */
	private void checkFilter() {
		if (getAppliedFilter() == null) {
			//we will have the default filter, so, notify the user
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell().getShell(), SWT.ICON_INFORMATION | SWT.OK);
			//getViewSite().getShell()
			//Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell()
			//Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().getShell()
			msgBox.setText(Messages.getString("VueGraphique.53")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VueGraphique.21") + //$NON-NLS-1$
					Messages.getString("VueGraphique.22")); //$NON-NLS-1$

			//			if(VueProgressBar.getInstance()!=null)
			//				VueProgressBar.getInstance().stop();
			try {
				Runtime.getRuntime().gc();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				msgBox.open();
				Runtime.getRuntime().gc();
				//				VueProgressBar.getInstance().stop();
			} catch (Exception e) {
				e.printStackTrace();
			}		
		}
		updateViewInfoLabel();
	}

	/**
	 * Updates the label for this view according to the applied/not applied filter
	 *
	 */
	private void updateViewInfoLabel() {
		AFiltreComposant appliedFilter = getAppliedFilter();
		String tabName = this.initialPartName;
		if(appliedFilter == null) {
			String currentXmlFileName = null;
			try {
				AFabriqueParcoursAbstraite factory = FabriqueParcours.getInstance();
				currentXmlFileName = new File(((InfosFichierSamNg)factory.getParcours().
						getInfo()).getNomFichierXml()).
						getName().toLowerCase();
			} catch (Exception e) {
			}
			currentXmlFileName=XMLName.updateCurrentXmlName();
			if(currentXmlFileName != null){
				tabName +=" [" + Messages.getString("VueGraphique.0") + " " + currentXmlFileName + "]" +"  "+ getNomAxe().toString(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			}
		} else {
			tabName += " [" + appliedFilter.getNom() + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		Message msg = getMessageFromCurrentSelection();
		String correctionsStr = DataViewsUtil.getCorrectionsTitleString(msg);
		if(correctionsStr != null)
			tabName += correctionsStr;

		String uniteDist=null;

		try {
			uniteDist = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getUnite();		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		if (uniteDist==null)			
			uniteDist="";

		if (SetReferenceAction.pointRef!=null) {
			tabName += " "+com.faiveley.samng.principal.ihm.actions.vue.Messages.getString("ReferenceAction.5")
					+" : "+SetReferenceAction.pointRef+" "+uniteDist;
			majInfoBullePointRef(SetReferenceAction.pointRef);
		}
		this.setPartName(tabName);
	}

	private void majInfoBullePointRef(double pointRef){
		System.out.println();
	}

	private String getNomAxe(){
		String axe=GestionnaireAxes.getInstance().getCurrentAxeType().toString();
		String nom_axe=""; //$NON-NLS-1$
		if (axe.equals(TypeAxe.AXE_TEMPS.toString())) {
			nom_axe=Messages.getString("VueGraphique.54"); //$NON-NLS-1$
		}else if (axe.equals(TypeAxe.AXE_TEMPS_CORRIGE.toString())) {
			nom_axe=Messages.getString("VueGraphique.55"); //$NON-NLS-1$
		}else if (axe.equals(TypeAxe.AXE_DISTANCE.toString())) {
			nom_axe=Messages.getString("VueGraphique.56"); //$NON-NLS-1$
		}else if (axe.equals(TypeAxe.AXE_DISTANCE_CORRIGEE.toString())){
			nom_axe=Messages.getString("VueGraphique.57"); //$NON-NLS-1$
		}
		return nom_axe;
	}

	@Override
	public void dispose() {
		//		this.data=null;
		ActivatorData.getInstance().getVueData().removeMarkersListener(this);
		ActivatorData.getInstance().removeRepereListener(this);
		ActivatorData.getInstance().removeDataListener(this);
		GestionnaireGraphesNotifications.removeGrapheCursorListener(this);
		if (leftPanelComposite!=null) {
			leftPanelComposite.dispose();
		}
		if (rightPanelComposite!=null) {
			rightPanelComposite.dispose();
		}
		super.dispose();
	}

	private void createContextMenu() {
		try {
			this.popupMenu = new Menu(mainComposite);
		} catch (Exception e) {
			System.out.print("this.popupMenu = new Menu(top);");
//			e.printStackTrace();
			return;
		}

		boolean axeTempsInhibe = new ParseurAdapteur().inhiberAxeTempsVueGraphique();
		boolean axeDistanceInhibe = new ParseurAdapteur().inhiberAxeDistanceVueGraphique();

		MenuItem itemAxeTemps = new MenuItem(popupMenu, SWT.RADIO);
		itemAxeTemps.setText(Messages.getString("VueGraphique.27")); //$NON-NLS-1$

		itemAxeTemps.setData(TypeMenuOptions.DISPLAY_AXE_TEMPS);
		itemAxeTemps.addListener(SWT.Selection, menuSelListener);
		itemAxeTemps.setSelection(GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_TEMPS);
		itemAxeTemps.setEnabled(!axeTempsInhibe);

		MenuItem itemAxeTempsCorrige = new MenuItem(popupMenu, SWT.RADIO);
		itemAxeTempsCorrige.setText(Messages.getString("VueGraphique.28")); //$NON-NLS-1$
		itemAxeTempsCorrige.setData(TypeMenuOptions.DISPLAY_AXE_TEMPS_CORRIGE);
		itemAxeTempsCorrige.addListener(SWT.Selection, menuSelListener);
		itemAxeTempsCorrige.setEnabled(!axeTempsInhibe);
		itemAxeTempsCorrige.setSelection(GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_TEMPS_CORRIGE);

		//correction issue 561 : si l'axe temps corrig� �tait selectionn� pour le pr�c�dent fichier 
		//mais qu'il n'y a pas de correction de temps pour le pr�sent fichier, alors l'axe selectionn� doit etre l'axe temps
		//et si l'axe temps n'est pas inhib�
		if(GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_TEMPS_CORRIGE && !axeTempsInhibe){
			if(ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige")==null || ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige").equals(false) ){
				itemAxeTemps.setSelection(true);
				itemAxeTempsCorrige.setSelection(false);
			}
		}

		if(ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige")==null) //$NON-NLS-1$
			itemAxeTempsCorrige.setEnabled(false);
		else if (ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige").equals(false)) //$NON-NLS-1$
			itemAxeTempsCorrige.setEnabled(false);

		MenuItem itemAxeDistance = new MenuItem(popupMenu, SWT.RADIO | SWT.BORDER);
		itemAxeDistance.setText(Messages.getString("VueGraphique.29")); //$NON-NLS-1$
		itemAxeDistance.setData(TypeMenuOptions.DISPLAY_AXE_DISTANCE);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		itemAxeDistance.setEnabled(!axeDistanceInhibe);
		itemAxeDistance.setSelection(GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_DISTANCE);

		MenuItem itemAxeDistanceCorrigee = new MenuItem(popupMenu, SWT.RADIO);
		itemAxeDistanceCorrigee.setText(Messages.getString("VueGraphique.30")); //$NON-NLS-1$
		itemAxeDistanceCorrigee.setData(TypeMenuOptions.DISPLAY_AXE_DISTANCE_CORRIGE);
		itemAxeDistanceCorrigee.addListener(SWT.Selection, menuSelListener);
		itemAxeDistanceCorrigee.setEnabled(!axeDistanceInhibe);
		itemAxeDistanceCorrigee.setSelection(GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_DISTANCE_CORRIGEE);

		//correction issue 561 : si l'axe distance corrig�e �tait selectionn� pour le pr�c�dent fichier 
		//mais qu'il n'y a pas de correction de distance pour le pr�sent fichier, alors l'axe selectionn� doit etre l'axe distance
		if(GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_DISTANCE_CORRIGEE && !axeDistanceInhibe){
			if(ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")==null || ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige").equals(false) ){
				itemAxeDistance.setSelection(!new ParseurAdapteur().inhiberAxeDistanceVueGraphique());
				itemAxeDistanceCorrigee.setSelection(false);
			}
		}

		if(ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")==null) //$NON-NLS-1$
			itemAxeDistanceCorrigee.setEnabled(false);
		else if (ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige").equals(false)) //$NON-NLS-1$
			itemAxeDistanceCorrigee.setEnabled(false);
		//		else itemAxeDistance.setEnabled(true);
		
		itemAxeDistance = new MenuItem(popupMenu, SWT.SEPARATOR);

		itemAxeDistance = new MenuItem(popupMenu, SWT.NONE);
		itemAxeDistance.setText(Messages.getString("VueGraphique.36")); //$NON-NLS-1$
		itemAxeDistance.setData(TypeMenuOptions.FILTER_MANAGEMENT);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		
		AFiltreComposant filtreApplied = getAppliedFilter();
		// Synchro between filter list and Ordonner list
		OrdonnerFiltre.getInstance().setFiltreSelectGraphique(filtreApplied);
		listeFiltreOrd = OrdonnerFiltre.getInstance().getListeFiltreGraphique();
		if(listeFiltreOrd == null) {
			listeFiltreOrd = new ArrayList<AFiltreComposant>();
		}
		
		listeFiltreOrd.clear() ; // To prevent from filter renaming + Issu 854
		
		GestionnaireFiltresGraphique gestFiltre = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres();
		AFiltreComposant listeFiltre = gestFiltre.getListeFiltres();

		int nbFiltre = listeFiltre.getEnfantCount();
		for(int i = 0; i < nbFiltre; i++){
			AFiltreComposant filtreCourant = listeFiltre.getEnfant(i);
			if(!listeFiltreOrd.contains(filtreCourant)) 
				listeFiltreOrd.add(filtreCourant);
		}
		
		itemAxeDistance = new MenuItem(popupMenu, SWT.CHECK);
		itemAxeDistance.setText(Messages.getString("VueGraphique.72"));
		itemAxeDistance.setData(TypeMenuOptions.NO_FILTER);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		boolean condition = OrdonnerFiltre.getInstance().getFiltreSelectGraphique() == null;
		itemAxeDistance.setSelection(condition);
		itemAxeDistance.setEnabled(!condition);
		
		createMenuFiltre(itemAxeDistance,listeFiltreOrd);

		itemAxeDistance = new MenuItem(popupMenu, SWT.SEPARATOR);

		itemAxeDistance = new MenuItem(popupMenu, SWT.NONE);
		itemAxeDistance.setText(Messages.getString("VueGraphique.31")+ "\t" + //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("VueGraphique.58")); //$NON-NLS-1$
		itemAxeDistance.setData(TypeMenuOptions.PREVIOUS_ZOOM);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		itemAxeDistance.setAccelerator(SWT.CTRL +'b');

		itemAxeDistance = new MenuItem(popupMenu, SWT.NONE);
		itemAxeDistance.setText(Messages.getString("VueGraphique.32")+ "\t" + //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("VueGraphique.59")); //$NON-NLS-1$
		itemAxeDistance.setData(TypeMenuOptions.NEXT_ZOOM);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		itemAxeDistance.setAccelerator(SWT.CTRL+'n');

		itemAxeDistance = new MenuItem(popupMenu, SWT.NONE);
		itemAxeDistance.setText(Messages.getString("VueGraphique.33")+ "\t" + //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("VueGraphique.60")); //$NON-NLS-1$
		itemAxeDistance.setData(TypeMenuOptions.MAIN_ZOOM);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		itemAxeDistance.setAccelerator(SWT.CTRL+'p');

		itemAxeDistance = new MenuItem(popupMenu, SWT.NONE);
		itemAxeDistance.setText(Messages.getString("VueGraphique.34")+ "\t" + //$NON-NLS-1$ //$NON-NLS-2$
				Messages.getString("VueGraphique.61")); //$NON-NLS-1$
		itemAxeDistance.setData(TypeMenuOptions.MANUAL_ZOOM);
		itemAxeDistance.addListener(SWT.Selection, menuSelListener);
		itemAxeDistance.setAccelerator(SWT.CTRL+'m');

		itemAxeDistance = new MenuItem(popupMenu, SWT.SEPARATOR);

		itemAxeDistance = new MenuItem(popupMenu, SWT.CASCADE);
		itemAxeDistance.setText(Messages.getString("VueGraphique.35")); //$NON-NLS-1$

		Menu optionsMenu = new Menu(popupMenu);
		itemAxeDistance.setMenu(optionsMenu);
		createOptionsContextMenu(optionsMenu);        
		
		mainComposite.setMenu(popupMenu);
	}
	
	private void createMenuFiltre(MenuItem itemAxeDistance,List<AFiltreComposant> listeFiltreOrd){
		GestionnaireFiltresGraphique gestFiltre = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres();
		int limite = gestFiltre.getLimiteBridageFiltre();
		int nbFiltre = listeFiltreOrd.size();
		List<String> listFiltres=new ArrayList<String>();
		for(int i = 0; i < nbFiltre; i++){
			AFiltreComposant filtreCourant = listeFiltreOrd.get(i);
			if((filtreCourant != null)&&(ActivatorVueGraphique.getDefault().getFiltresProvider().filtrevalide(filtreCourant))){		
				if(listFiltres.size()<limite && (!listFiltres.contains(filtreCourant.getNom()))){
					itemAxeDistance = new MenuItem(popupMenu, SWT.CHECK);
					itemAxeDistance.setText(filtreCourant.getNom());
					itemAxeDistance.setData(filtreCourant);
					itemAxeDistance.addListener(SWT.Selection,new MenuSelectionListener(){
						@Override
						public void handleEvent(Event e) {
							MenuItem menuItem = (MenuItem)e.widget;
							AFiltreComposant f = (AFiltreComposant) menuItem.getData();

							ActivatorVueGraphique.getDefault().getFiltresProvider().setAppliedFilterName(f.getNom());
							new ApplyFiltreAction().run();

						}
					});
					boolean condition = OrdonnerFiltre.getInstance().getFiltreSelectGraphique() == filtreCourant;
					itemAxeDistance.setSelection(condition);
					itemAxeDistance.setEnabled(!condition);
					listFiltres.add(filtreCourant.getNom());
				}
			}
		}
	}

	private void createOptionsContextMenu(Menu parent) {
		GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();

		MenuItem item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.37")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.STEPPED_GRAPH);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isMarches_escalier());

		item = new MenuItem(parent, SWT.SEPARATOR);

		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.38")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.DIGITAL_ZERO_REF);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isRef_zero_digit());

		item = new MenuItem(parent, SWT.SEPARATOR);

		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.39")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.DISPLAY_LABEL);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isLegende());
		
		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.73"));
		item.setData(TypeMenuOptions.USE_SHORT_NAMES);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(this.usesShortNames);

		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.40")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.DISPLAY_TIME_BREAKS);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isRuptures_temps());

		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.41")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.DISPLAY_DISTANCE_BREAKS);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isRuptures_distance());

		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.42")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.DISPLAY_MARKERS);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isMarqueurs());

		item = new MenuItem(parent, SWT.SEPARATOR);

		item = new MenuItem(parent, SWT.RADIO);
		item.setText(Messages.getString("VueGraphique.43")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.LINE_MODE);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.getMode() == TypeMode.LINE);

		item = new MenuItem(parent, SWT.RADIO);
		item.setText(Messages.getString("VueGraphique.44")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.POINT_MODE);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.getMode() == TypeMode.POINT);

		item = new MenuItem(parent, SWT.SEPARATOR);

		item = new MenuItem(parent, SWT.CHECK);
		item.setText(Messages.getString("VueGraphique.45")); //$NON-NLS-1$
		item.setData(TypeMenuOptions.WHITE_BACKGROUND);
		item.addListener(SWT.Selection, menuSelListener);
		item.setSelection(viewMng.isFond_blanc());
	}

	private void afficherCurseurSiVisible(){
		Curseur.getInstance().setAddCursorAfterRedraw(Curseur.getInstance().getCurseurVisible());
	}

	public void afficherAxeTemps(){
		if(GestionnaireAxes.getInstance().getCurrentAxeType() != TypeAxe.AXE_TEMPS) {
			afficherCurseurSiVisible();
			AZoomComposant AZ=GestionnaireZoom.getZoomCourant();
			new ChangeAxeTypeAction(TypeAxe.AXE_TEMPS).run();
			GestionnaireZoom.ajouterZoom(AZ);
			updateViewInfoLabel();
		}
	}

	public void afficherAxeDistance(){
		if(GestionnaireAxes.getInstance().getCurrentAxeType() != TypeAxe.AXE_DISTANCE) {
			afficherCurseurSiVisible();
			AZoomComposant AZ=GestionnaireZoom.getZoomCourant();
			new ChangeAxeTypeAction(TypeAxe.AXE_DISTANCE).run();
			GestionnaireZoom.ajouterZoom(AZ);
			updateViewInfoLabel();
		}
	}

	private void afficherAxeTempsCorrige(){
		if(GestionnaireAxes.getInstance().getCurrentAxeType() != TypeAxe.AXE_TEMPS_CORRIGE) {
			afficherCurseurSiVisible();
			AZoomComposant AZ=GestionnaireZoom.getZoomCourant();
			new ChangeAxeTypeAction(TypeAxe.AXE_TEMPS_CORRIGE).run();
			GestionnaireZoom.ajouterZoom(AZ);
			updateViewInfoLabel();
		}
	}

	private void afficherAxeDistanceCorrige(){
		if(GestionnaireAxes.getInstance().getCurrentAxeType() != TypeAxe.AXE_DISTANCE_CORRIGEE) {
			afficherCurseurSiVisible();
			AZoomComposant AZ=GestionnaireZoom.getZoomCourant();
			new ChangeAxeTypeAction(TypeAxe.AXE_DISTANCE_CORRIGEE).run();
			GestionnaireZoom.ajouterZoom(AZ);
			updateViewInfoLabel();
		}
	}

	private class MenuSelectionListener implements Listener {

		public void handleEvent(Event e) {
			GestionnaireVueGraphique viewMng = ActivatorVueGraphique.getDefault().getConfigurationMng();
			MenuItem menuItem = (MenuItem)e.widget;
			TypeMenuOptions menuId = (TypeMenuOptions)menuItem.getData();
			switch (menuId) {
			case DISPLAY_AXE_TEMPS:
			{		 
				afficherAxeTemps();
				break;
			}
			case DISPLAY_AXE_DISTANCE: 
			{
				afficherAxeDistance();
				break;
			}
			case DISPLAY_AXE_TEMPS_CORRIGE:
			{
				afficherAxeTempsCorrige();
				break;
			}
			case DISPLAY_AXE_DISTANCE_CORRIGE: 
			{
				afficherAxeDistanceCorrige();
				break;
			}

			case PREVIOUS_ZOOM:
			case NEXT_ZOOM:
			case MAIN_ZOOM:
			case MANUAL_ZOOM:
				new ZoomAction(menuId).run();
				break;
			case FILTER_MANAGEMENT:
				new ShowVueFiltresGraphiqueAction().run();
				break;
			case NO_FILTER:
				ActivatorVueGraphique.getDefault().getFiltresProvider().setAppliedFilterName("");
				new ApplyFiltreAction().runWithEvent(null);
				break;
			case STEPPED_GRAPH:
				if(viewMng.isMarches_escalier() != menuItem.getSelection())
					new SteppedGraphAction(menuItem.getSelection()).run();
				break;
			case DIGITAL_ZERO_REF:
				if(viewMng.isRef_zero_digit() != menuItem.getSelection())
					new DigitalRef0Action(menuItem.getSelection()).run();
				break;

			case DISPLAY_LABEL:
				if(viewMng.isLegende() != menuItem.getSelection())
					new DisplayLegendAction(menuItem.getSelection()).run();
				break;
			case USE_SHORT_NAMES:
				usesShortNames = !usesShortNames;
				rechercherVariableAction.usesShortNames(usesShortNames);
				viewMng.setUsesShortNames(usesShortNames);
				ActivatorVueGraphique.getDefault().setUsesShortNames(usesShortNames);
				ActivatorVueGraphique.getDefault().saveConfigurationVue();
				refresh();
				break;
			case DISPLAY_TIME_BREAKS:
				if(viewMng.isRuptures_temps() != menuItem.getSelection())
					new DisplayTimeBreaksAction(menuItem.getSelection()).run();
				break;
			case DISPLAY_DISTANCE_BREAKS:
				if(viewMng.isRuptures_distance() != menuItem.getSelection())
					new DisplayDistanceBreaksAction(menuItem.getSelection()).run();
				break;
			case DISPLAY_MARKERS:
				if(viewMng.isMarqueurs() != menuItem.getSelection()) 
					new DisplayMarkersAction(menuItem.getSelection()).run();
				break;
			case LINE_MODE:
				if(viewMng.getMode() != TypeMode.LINE)
					new PointModeAction(false).run();
				break;
			case POINT_MODE:
				if(viewMng.getMode() != TypeMode.POINT)
					new PointModeAction(true).run();
				break;
			case WHITE_BACKGROUND:
				if(viewMng.isFond_blanc() != menuItem.getSelection()) {
					afficherCurseurSiVisible();
					AZoomComposant AZ=GestionnaireZoom.getZoomCourant();
					new WhiteBackgroundAction(menuItem.getSelection()).run();
					GestionnaireZoom.ajouterZoom(AZ);
					updateViewInfoLabel();
				}	
				break;
			}
		}
	}

	/**
	 * Handler for notifications that selection in another view changed
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		//positionnement offset
		if (GestionLineCommandParameters.getIndiceMsg()!=-1 && oneTimeOffset) {
			try {
				oneTimeOffset=false;
				int selId = GestionLineCommandParameters.getIndiceMsg();
				//			r�cup�ration uniquement des bons messages
				ListMessages messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();

				AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
				CursorPositionEvent cursorEvent = new CursorPositionEvent(
						messages.get(0),data.getEnregistrement().getGoodMessage(selId),-1,-1,false);
				GestionnaireGraphesNotifications.notifyCursorPositionChanged(cursorEvent);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//traitement normal
		}else if((selection instanceof MessageSelection) && (part != this) && !selection.isEmpty()) {
			if(!((MessageSelection)selection).getUserSendSelection())
				return;
			int selId = ((MessageSelection)selection).getMessageId();
			//			r�cup�ration uniquement des bons messages
			ListMessages messages = null;
			try {
				messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
			} catch (Exception e) {
				e.printStackTrace();
			}


			AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
			CursorPositionEvent cursorEvent = new CursorPositionEvent(
					messages.get(0),data.getEnregistrement().getGoodMessage(selId),-1,-1,false
					);
			GestionnaireGraphesNotifications.notifyCursorPositionChanged(cursorEvent);
		}
		
		//#Issue 856
		//refresh();    		
		createContextMenu();
		if (leftPanelComposite != null) {
			leftPanelComposite.setMenu(this.popupMenu);
		}
		if (rightPanelComposite != null) {
			rightPanelComposite.setMenu(this.popupMenu);
		}
	}

	/**
	 * Handler for notifications that the list of marker changed
	 */
	public void marquersListeChangement(int[] msgIds, int[] oldMsgIds) {
		redrawGraphes(true);
	}

	/**
	 * Handler for notifications that a marker was added
	 */
	public void marqueurAjoutee(int msgId) {
		redrawGraphes(true);
	}

	/**
	 * Handler for notifications that a marker was deleted
	 */
	public void marqueurEffacee(int msgId) {
		redrawGraphes(true);
	}

	/**
	 * Handler for notifications that the cursor was moved in one of the graphes
	 */
	public void cursorPositionChanged(CursorPositionEvent event) {
		if(event instanceof ValuedCursorPositionEvent)
			return;

		//		r�cup�ration uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();


		//r�cup�ration de tous les messages
		//		List<Message> messages = ActivatorData.getInstance().getVueData()
		//		.getDataTable().getEnregistrement(0).getMessages();

		((MessageSelection)this.currentSelection).setMessageId(event.getCurrentMessage().getMessageId());
		if(event.isDoubleClick()) {
			upSelection();
		}
		updateViewInfoLabel();
		ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
		for(int i=0; i<listeActionsAuditrice.size();i++){

			listeActionsAuditrice.get(i).setEnabled(true);
		}
		if (GestionnaireZoom.getZoomCourant()!=null) {
			//if (!Curseur.getInstance().getCurseurVisible()&&GestionnaireZoom.getInstance().getZoomCourant()!=null) {
			try {
				ZoomX currentZoomX = (ZoomX)((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0);
				int firstID=currentZoomX.getFirstVisibleMsgId();
				int lastID=currentZoomX.getLastVisibleMsgId();
				if (firstID>event.getCurrentMessage().getMessageId()||lastID<event.getCurrentMessage().getMessageId()) {
					int ecartID=lastID-firstID;
					int newFirstID,newLastID,newCursorID;
					newCursorID=event.getCurrentMessage().getMessageId();
					newFirstID=newCursorID-ecartID/2;
					newLastID=newCursorID+ecartID/2;
					AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
					Enregistrement enr = data.getEnregistrement();
					int nbmessages = messages.size();
					int ID0=enr.getEnfant(0).getMessageId();
					int ID1=enr.getEnfant(messages.size()-1).getMessageId();
					boolean firstIDValid=false;
					boolean lastIDValid=false;

					if (newFirstID<ID0)
						newFirstID=ID0;
					else{
						for (int i = 0; i < nbmessages; i++) {
							try {
								if (messages.get(i).getMessageId()>newFirstID) {
									newFirstID=messages.get(i).getMessageId();
									firstIDValid=true;
									break;
								}								
							} catch (Exception e) {

							}						
						}
						if (!firstIDValid) {
							newFirstID=ID0;
						}
					}

					if (newLastID>ID1)
						newLastID=ID1;
					else{
						for (int i = nbmessages-1; i > -1; i--) {
							try {
								if (messages.get(i).getMessageId()<newLastID) {
									newLastID=messages.get(i).getMessageId();
									lastIDValid=true;
									break;
								}
							} catch (Exception e) {

							}							
						}
						if (!lastIDValid) {
							newLastID=ID1;
						}
					}

					double startValue=GestionnaireAxes.getAxeXValue(enr.getGoodMessage(newFirstID));
					double endValue=GestionnaireAxes.getAxeXValue(enr.getGoodMessage(newLastID));				
					AxeX axe=GestionnaireAxes.getInstance().getCurrentAxeX();
					GestionnaireZoom.creerZoomX(axe, newFirstID, newLastID, startValue, endValue);
					Curseur.getInstance().setEv(event);
					Curseur.getInstance().setMsgId(event.getCurrentMessage().getMessageId());
					Curseur.getInstance().setCurseurVisible(true);
				}
			} catch (Exception e) {

			}
		}
	}

	/**
	 * Returns a message from the current selection. If no selection or invalid message
	 * is returned null
	 * 
	 * @return
	 */
	private Message getMessageFromCurrentSelection() {
		Message msg = null;
		if(!((MessageSelection)this.currentSelection).isEmpty()) {
			int msgId = ((MessageSelection)this.currentSelection).getMessageId();
			msg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(msgId);
		}
		return msg;
	}

	/**
	 * Handler for notifications that a reper was added
	 */
	public void onRepereAdded(TypeRepere... reper) {

		boolean msgBoxRupDistExist=false;

		for (TypeRepere repere : reper) {
			if(repere.equals(TypeRepere.tempsCorrigee)){
				this.popupMenu.getItem(1).setEnabled(true);
			}
			if(repere.equals(TypeRepere.distanceCorrigee)){
				this.popupMenu.getItem(3).setEnabled(true);
				VitesseCorrigeePresenteDansFiltre.getInstance().notDraw=false;
			}
		}
		//msgBoxRupDistExist=true;
		if (ActivatorData.getInstance().getPoolDonneesVues().get("ajouterUnMarqueur").equals(true)
				||ActivatorData.getInstance().getPoolDonneesVues().get("ReferenceZero").equals(new String ("true"))){
			if (ActivatorData.getInstance().getPoolDonneesVues().get("ajouterUnMarqueur").equals(true)) {			 
				if (Curseur.getInstance().getCurseurVisible()){
					int pix=Curseur.getInstance().getpositionCurseur()-MARGE_LATERALE;
					int idMessage=Curseur.getInstance().getMsgId();
					if(ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId((int)idMessage)!=null)	{
						InfosBullesMarqueurs.getInstance().getListBullesName().add(ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId((int)idMessage).getNom());
						InfosBullesMarqueurs.getInstance().getListBullesDim().add(pix);
						ActivatorData.getInstance().getPoolDonneesVues().put(new String("ajouterUnMarqueur"), false);
					}
					else{
						idMessage=(int)ActivatorData.getInstance().getGestionnaireMarqueurs().getDernierMarqueurAjoute().getIdMessage();
						InfosBullesMarqueurs.getInstance().getListBullesName().add(ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId((int)idMessage).getNom());
						InfosBullesMarqueurs.getInstance().getListBullesDim().add(pix);
						ActivatorData.getInstance().getPoolDonneesVues().put(new String("ajouterUnMarqueur"), false);
					}
					redrawGraphes(false);
				}
			}else{
				if (Curseur.getInstance().getEv()!=null) {
					PositionReferenceZero.getInstance().setPosition(Curseur.getInstance().getEv().getxPos()-MARGE_LATERALE);
					ActivatorData.getInstance().getPoolDonneesVues().put("ReferenceZero", "false"); 	
					redrawGraphes(false);
					this.upSelection();
				}
			}
		}else{
			ActivatorVueGraphique.getDefault().getFiltresProvider().onDataChange();
			refresh();
		}		 
		//redrawGraphes(true);
	}

	/**
	 * Handler for notifications that a reper was removed
	 */
	public void onRepereRemoved(TypeRepere... reper) {
		if(ActivatorVueGraphique.getDefault().getConfigurationMng().getAxe().equals(TypeAxe.AXE_TEMPS_CORRIGE)){
			ActivatorVueGraphique.getDefault().getConfigurationMng().setAxe(TypeAxe.AXE_TEMPS);
			this.popupMenu.getItem(0).setSelection(true);
			this.popupMenu.getItem(1).setSelection(false);
		}


		if(ActivatorVueGraphique.getDefault().getConfigurationMng().getAxe().equals(TypeAxe.AXE_DISTANCE_CORRIGEE)){
			ActivatorVueGraphique.getDefault().getConfigurationMng().setAxe(TypeAxe.AXE_DISTANCE);
			this.popupMenu.getItem(2).setSelection(true);
			this.popupMenu.getItem(3).setSelection(false);
		}


		for (TypeRepere repere : reper) {
			if(repere.equals(TypeRepere.tempsCorrigee)){
				this.popupMenu.getItem(1).setEnabled(false);

			}
			if(repere.equals(TypeRepere.distanceCorrigee)){
				this.popupMenu.getItem(3).setEnabled(false);
				checkVarVitesseCorrigeePresente();
			}
		}

		ActivatorVueGraphique.getDefault().getFiltresProvider().onDataChange();
		refresh();
	}

	public void checkVarVitesseCorrigeePresente(){
		VitesseCorrigeePresenteDansFiltre.getInstance().notDraw=false;
		if (!TableSegments.getInstance().isAppliedDistanceCorrections()) {

			//			AbstractProviderFiltre filterProvider = ActivatorVueGraphique.getDefault().getFiltresProvider();
			//			GestionnaireFiltresGraphique filterMng = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres();
			//			String appliedFilterName = filterProvider.getAppliedFilter();
			AFiltreComposant appliedFilter = getAppliedFilter();
			if (appliedFilter!=null) {

				int nbGraphes=appliedFilter.getEnfantCount();
				for (int i = 0; i < nbGraphes; i++) {
					AFiltreComposant graph=appliedFilter.getEnfant(i);
					int nbVar=graph.getEnfantCount();
					for (int j = 0; j < nbVar; j++) {
						if (graph.getEnfant(j).getNom().equals(TypeRepere.vitesseCorrigee.getName())){						 
							VitesseCorrigeePresenteDansFiltre.getInstance().notDraw=true;
							VitesseCorrigeePresenteDansFiltre.getInstance().indiceGraphe=i;
							MessageBox msgBox = new MessageBox(this.getViewSite().getShell(), SWT.ICON_WARNING | SWT.OK);
							msgBox.setText(""); //$NON-NLS-1$
							msgBox.setMessage(Messages.getString("VueGraphique.71"));  
							msgBox.open();

							GraphiqueFiltresProvider gFilterProvider=(GraphiqueFiltresProvider)ActivatorVueGraphique.getDefault().getFiltresProvider();
							gFilterProvider.setAppliedFilterName(null);
							gFilterProvider.applyFilterdefault();
							refresh();							
							redrawGraphes(true);
						}
					}						
				}
			}
		}
	}

	public Composite getContenu() {
		//  Auto-generated method stub
		return mainComposite;
	}

	public void onSelectedMarquerCommentChange(String comment, boolean next) {
		AMarqueur selMarquer = null;

		Message currentmsg = getMessageFromCurrentSelection();
		int crtSelMsgId = currentmsg != null ? currentmsg.getMessageId() : 0;
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}
		boolean isError = false;
		// get the marquer manager
		GestionnaireMarqueurs marquersGest = ActivatorData.getInstance().getGestionnaireMarqueurs();
		if (marquersGest != null) {

			int[] ids = marquersGest.getMarqueursIds();
			Marqueur m = null;

			// if the selection is on a message after the last marquer
			if (crtSelMsgId > ids[ids.length - 1]) {
				if (!next) {
					// selected message is after all the marquers
					for (int i = ids.length - 1; i >= 0; i--) {
						m = (Marqueur) marquersGest.getMarqueurParId(ids[i]);
						if (m.getCommentaire().indexOf(comment) >= 0) {
							selMarquer = m;
							break;
						}
					}
				}
			} else {
				// selection is before last marquer
				for (int i = 0; i < ids.length; i++) {
					if (ids[i] >= crtSelMsgId) {
						if (next) {
							// if searching next marquer
							if (ids[i] == crtSelMsgId) {
								if (i < ids.length - 1) {
									for (; i < ids.length; i++) {
										m = (Marqueur) marquersGest
												.getMarqueurParId(ids[i + 1]);
										if (m.getCommentaire().indexOf(comment) >= 0) {
											selMarquer = m;
											break;
										}
									}
								}
							} else {

								for (; i < ids.length; i++) {
									m = (Marqueur) marquersGest
											.getMarqueurParId(ids[i]);
									if (m.getCommentaire().indexOf(comment) >= 0) {
										selMarquer = m;
										break;
									}
								}
							}
						} else {
							if (i > 0) {
								for (; i >= 0; i--) {
									m = (Marqueur) marquersGest
											.getMarqueurParId(ids[i - 1]);
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
			//			r�cup�ration uniquement des bons messages
			ListMessages messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();

			//r�cup�ration de tous les messages
			//			List<Message> messages = ActivatorData.getInstance().getVueData()
			//			.getDataTable().getEnregistrement(0).getMessages();
			if (selMarquer != null) {

				AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
				CursorPositionEvent cursorEvent = new CursorPositionEvent(
						messages.get(0),data.getEnregistrement().getGoodMessage(selMarquer.getIdMessage()),-1,-1,false
						);
//				cursorEvent.firstMessage = messages.get(0);
//				cursorEvent.currentMessage = data.getEnregistrement(0).getGoodMessage(selMarquer.getIdMessage());
//				cursorEvent.xPos = -1;	//unknown
//				cursorEvent.sourceGrapheNr = -1;	//unknown
				GestionnaireGraphesNotifications.notifyCursorPositionChanged(cursorEvent);
			} else {
				isError = true;
			}
		}

		if (isError) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage("No marquer");  //$NON-NLS-1$
			msgBox.open();
		}
	}


	public String onSelectedMarquerNomChange(boolean next) {
		AMarqueur selMarquer = null;

		Message currentmsg = getMessageFromCurrentSelection();
		int crtSelMsgId = currentmsg != null ? currentmsg.getMessageId() : 0;
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}

		boolean isError = false;
		// gets the marquers manager
		GestionnaireMarqueurs marquersGest = ActivatorData.getInstance().getGestionnaireMarqueurs();
		if (marquersGest != null) {
			int[] ids = marquersGest.getMarqueursIds();

			// if current selected message is after the last marquer
			if (crtSelMsgId > ids[ids.length - 1]) {
				if (!next) {
					selMarquer = marquersGest
							.getMarqueurParId(ids[ids.length - 1]);
				}
			} else {
				// current selection is before the last marquer
				for (int i = 0; i < ids.length; i++) {
					// search for the first marquer after the current selected
					// message
					if (ids[i] >= crtSelMsgId) {
						// if searching for "next marquer"
						if (next) {
							// if the current selected message is the same with
							// the marquer
							// "next" marquer is the next in list
							if (ids[i] == crtSelMsgId) {
								if (i < ids.length - 1) {
									selMarquer = marquersGest
											.getMarqueurParId(ids[i + 1]);
								}
							} else {
								// next marquer is the current marquer
								selMarquer = marquersGest
										.getMarqueurParId(ids[i]);
							}
						} else {
							// searching for "previous" marquer
							if (i > 0) {
								selMarquer = marquersGest
										.getMarqueurParId(ids[i - 1]);
							}
						}
						break;
					}
				}
			}
			//			r�cup�ration uniquement des bons messages
			ListMessages messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();

			//r�cup�ration de tous les messages
			//			List<Message> messages = ActivatorData.getInstance().getVueData()
			//			.getDataTable().getEnregistrement(0).getMessages();
			if (selMarquer != null) {
				AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
				CursorPositionEvent cursorEvent = new CursorPositionEvent(
						messages.get(0),data.getEnregistrement().getGoodMessage(selMarquer.getIdMessage()),-1,-1,false
						);
//				cursorEvent.firstMessage = messages.get(0);
//				cursorEvent.currentMessage = data.getEnregistrement(0).getGoodMessage(selMarquer.getIdMessage());
//				cursorEvent.xPos = -1;	//unknown
//				cursorEvent.sourceGrapheNr = -1;	//unknown
				Curseur.getInstance().setpositionCurseur(-1);
				Curseur.getInstance().setMsgId(selMarquer.getIdMessage());
				GestionnaireGraphesNotifications.notifyCursorPositionChanged(cursorEvent);
				redrawGraphes(false);
			} else {
				isError = true;
			}
		}

		if (isError) {
			// if no marquer found display a message
			MessageBox msgBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VueGraphique.50")); //$NON-NLS-1$
			msgBox.open();
		}

		return selMarquer != null ? selMarquer.getNom() : ""; //$NON-NLS-1$
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName().equals("MAXIMISER_LEGENDE")) //$NON-NLS-1$
			sashForm.setWeights(new int[]{0,5});
		else if(evt.getPropertyName().equals("MAXIMISER_GRAPHES")) //$NON-NLS-1$
			sashForm.setWeights(new int[]{5,0});
		else if(evt.getPropertyName().equals("NORMAL_LEGENDE") ||evt.getPropertyName().equals("NORMAL_GRAPHES") ){ //$NON-NLS-1$ //$NON-NLS-2$
			sashForm.setWeights(new int[]{4,1});
		}
	}

	/**
	 * Fonction qui v�rifie si tous les segments sont valides 
	 * et alerte l'utilisateur si �a n'est pas le cas.
	 *
	 */
	private int verifierSegmentsDistance(){
		HashMap<Integer, SegmentDistance> mapSegmentsDistance = TableSegments.getInstance().getSegmentsDistance();
		int nbSegValid=0;
		Collection<SegmentDistance> listeSegmentsDistance = mapSegmentsDistance.values();
		String chaine=""; //$NON-NLS-1$
		for (SegmentDistance distance : listeSegmentsDistance) {
			if(!distance.isValide()){
				chaine+=(distance.getNumeroSegment()+1)+ "\n"; //$NON-NLS-1$
			}else{
				nbSegValid++;
			}
		}
		if(nbSegValid!=0){
			if(!chaine.equals("")){ //$NON-NLS-1$
				chaine = Messages.getString("VueGraphique.62") + chaine + " \n" +"." + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						Messages.getString("VueGraphique.64"); //$NON-NLS-1$
				MessageBox msgBox = new MessageBox(Display.getCurrent()
						.getActiveShell(),SWT.OK);
				msgBox.setMessage(chaine);
				msgBox.setText(Messages.getString("VueGraphique.65")); //$NON-NLS-1$
				msgBox.open();

			}
		}
		return nbSegValid;
	}

	/**
	 * V�rifie si il y a une variable � afficher dans la vue graphique
	 * 
	 * @return false si aucune variable, true sinon
	 */
	private boolean isVariableToDisplay(){
		return false;
	}

	@Override
	public void setFocus() {

	}
	public void onVbvAdded(String vbvName, String oldVbvName) {
		redrawGraphes(true);

	}
	public void onVbvRemoved(String vbvName) {
		redrawGraphes(true);

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener#onSearchVariable(com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable,
	 *      java.lang.String,
	 *      com.faiveley.samng.principal.ihm.vues.search.Operation, boolean)
	 */
	public void onSearchVariable(DescripteurVariable descrVar, String value, Operation op, boolean next) {
		boolean trouveVarComposee = false;
		boolean dateRecherchee=false;
		int crtSelMsgId = Curseur.getInstance().getMsgId();
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}
		Enregistrement enr=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement();
		ListMessages messages = enr.getMessages();
		Message msgDepart=enr.getGoodMessage(crtSelMsgId);

		Message selMsg = null;
		String[] msgErr = new String[1];
		this.setSearchChange(false);

		Message msg=null;
		for (int i=next ? 0 : (messages.size()-1); next ? i<messages.size() : i>-1; i= (next ? (i+1) : (i-1) )) {	//si next, on incremente sinon on decremente
			msg = messages.get(i);
			if (next && msg.getMessageId() <= crtSelMsgId
					||
					((!next) && msg.getMessageId() >= crtSelMsgId)) {
				continue;
			}

			//si variable compos�e
			if (descrVar.getTypeVariable() == TypeVariable.VAR_COMPOSEE) {
				List<AVariableComposant> listVarComp = GestionnaireVariablesComposee.checkForVariablesComposee(msg);
				if (listVarComp != null) {
					if (listVarComp.size() > 0) {
						for (AVariableComposant composant : listVarComp) {
							if (composant.getDescriptor().equals(descrVar)) {
								if (value == null) {
									selMsg = msg;
									trouveVarComposee = true;
								}
								LabelValeur valueLabel = Util.getLabelForVariableComposee((VariableComposite) composant, msg);
								if (valueLabel != null) {
									if (value.equals(valueLabel.getLabel())) {
										selMsg = msg;
										msg.getMessageId();
										trouveVarComposee = true;
									}
								}
								break;
							}
						}
					}
				}
				if (trouveVarComposee) {
					break;
				}
			} else {
				//si c'est une date
				if (descrVar.getM_AIdentificateurComposant().getCode() == TypeRepere.date.getCode()) {
					dateRecherchee=true;
					selMsg=searchVariableDate(msg, descrVar, value, op, msgErr,false);
					if (selMsg!=null) {
						break;
					}
				} else if(descrVar != null && descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC
						&& descrVar.getM_AIdentificateurComposant()!=null 
						&& descrVar.getM_AIdentificateurComposant().getNom().equals(com.faiveley.samng.principal.ihm.vues.configuration.
								Messages.getString("GestionnaireVueListeBase.2"))) {
					dateRecherchee=true;
					//					if (msg.getMessageId()==34968) {
					//						int re=0;
					//					}
					selMsg=searchVariableDate(msg, descrVar, value, op, msgErr,true);
					if (selMsg!=null) {
						break;
					}
				} else {
					AVariableComposant var = VariableExplorationUtils.getVariable(descrVar, msg);
					if (var != null) {
						if (value == null || op == null) {
							selMsg = msg;
							break;
						}
						if (checkSearchedVariable(var, value, op, msgErr, msg, msgDepart)) {
							selMsg = msg;
							break;
						}
					}
				}
			}
		}

		if (selMsg != null) {
			CursorPositionEvent cursorEvent = new CursorPositionEvent(
					messages.get(0),selMsg,-1,-1,false
					);
//			cursorEvent.firstMessage = messages.get(0);
//			cursorEvent.currentMessage = selMsg;
//			cursorEvent.xPos = -1;	//unknown
//			cursorEvent.sourceGrapheNr = -1;	//unknown
			GestionnaireGraphesNotifications.notifyCursorPositionChanged(cursorEvent);
			Curseur.getInstance().setMsgId(selMsg.getMessageId());
		} else {
			if (msgErr[0] == null) {
				msgErr[0] =Messages.getString("VueGraphique.66"); //$NON-NLS-1$
			}
			if (Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()
					==VueGraphique.this.getSite().getPart()) {
				MessageBox msgBox = new MessageBox(VueGraphique.this.getSite().getShell(), SWT.ICON_WARNING | SWT.OK );
				msgBox.setText(""); //$NON-NLS-1$
				msgBox.setMessage(msgErr[0]);
				msgBox.open();
			}			
		}
	}

	//	private boolean checkSearchedVariableDate(long diff, Operation op, String[] msgErr) {
	//
	//		boolean shouldSelect = false;
	//		switch (op) {
	//		case Equal: {
	//			shouldSelect = diff > 0 && diff < (24 * 3600 * 1000);
	//			break;
	//		}
	//		case Greater: {
	//			shouldSelect = diff > (24 * 3600 * 1000);
	//			break;
	//		}
	//		case GreaterOrEqual: {
	//			shouldSelect = diff > 0 || diff == 0;
	//			break;
	//		}
	//		case Less: {
	//			shouldSelect = diff < 0;
	//			break;
	//		}
	//		case LessOrEqual: {
	//			shouldSelect = diff < 0 || (diff < (24 * 3600 * 1000));
	//			break;
	//		}
	//		case NotEqual: {
	//			shouldSelect = diff < 0 || (diff > (24 * 3600 * 1000));
	//			break;
	//		}
	//		case ShiftLeft:
	//
	//		default:
	//			msgErr[0] = Messages.getString("AVueTable.27"); //$NON-NLS-1$
	//			break;
	//		}
	//
	//		return shouldSelect;
	//	}

	@Override
	public IWorkbenchPartSite getSite() {
		// TODO Auto-generated method stub
		return super.getSite();
	}

	@Override
	public Object getAdapter(Class adapter) {
		// TODO Auto-generated method stub
		return super.getAdapter(adapter);
	}

	@Override
	public ISelection getSelection() {
		// TODO Auto-generated method stub
		return super.getSelection();
	}

	@Override
	public void showBusy(boolean busy) {
		// TODO Auto-generated method stub
		super.showBusy(busy);
	}
	
	@Override
	public void onSearchVariable(DescripteurVariable descrVar,
			String stringValue, String value, Operation op,
			boolean next) {
		this.onSearchVariable(descrVar, value, op, next);		
	}

} 
