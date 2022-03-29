package com.faiveley.samng.principal.ihm.vues.vuemarqueurs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.IMarqueursListener;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions.MesureDeltaValeurMarqueurAction;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions.SupprimerMarqueurAction;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.configuration.GestionnaireVueMarqueurs;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;
import com.faiveley.samng.principal.sm.marqueurs.Marqueur;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueMarqueurs extends AbstractSelectionProviderVue implements ISelectionListener, 
IMarqueursListener, ICapturable,IRepereChangedListener,ISaveablePart2, IDataChangedListener {
	public static final String ID = "SAMNG.Vue.Markers.VueMarqueurs"; //$NON-NLS-1$

	private int CURRENT_POS=0;
	private int TIME_COL_POS;
	private int TIMECOR_COL_POS;
	private int DISTCOR_COL_POS;
	private int NAME_COL_POS;
	private int ACC_DIST_COL_POS;

	private int [] messageSelected=new int[1];

	private boolean timeCorrectionapplied;
	private boolean distanceCorrectionapplied;
	private SashForm sashForm;
	private Composite mainComposite;
	private TableColumn distanceCorrectedColumn;
	private TableColumn accumulatedDistanceColumn;
	private TableColumn markerNameColumn;
	private TableColumn timeColumn;
	private TableColumn timeCorrectedColumn;
	private TableColumn dummyColumn;
	private Table markersTable;
	private Composite commentComposite;
	private Text commentTextArea;
	private Composite commentOkCancelBtnsComposite;
	private Button commentOkBtn;
	private Button commentCancelBtn;
	private static String dieses="###";
	private TableRowComparator comparator = new TableRowComparator();
	private int[] dataColumnsWidth = {150, 150, 150, 150, 150, 150};

	//	ToolBar
	private IActionBars bars; 
	//d�claration des actions
	private SupprimerMarqueurAction supprimerMarqueurAction;
	private MesureDeltaValeurMarqueurAction mesurerDeltaValeurMarqueurAction;
	private ImprimerVueAction imprimerVueAction ;
	private CapturerVueAction capturerVueAction ;
	private Action synchroVuesAction;
	private ArrayList<Action> listeActionsAuditrice;
	private HashMap<String, String> mapNomUniqueNomUtilisateur;
	
	boolean oneTimeOffset=true;//le positionnement offset ne se fait qu'une fois


	public VueMarqueurs() {
		this.setPartName(Messages.getString("VueMarqueurs.0")); //$NON-NLS-1$
		this.currentSelection = new MessageSelection();
		ActivatorData.getInstance().addDataListener(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		messageSelected[0]=-1;

		if (getDistanceCorrectionApply())
			distanceCorrectionapplied=true;
		else
			distanceCorrectionapplied=false;

		if (getTimeCorrectionApply())
			timeCorrectionapplied=true;
		else
			timeCorrectionapplied=false;


		this.sashForm = new SashForm(parent, SWT.VERTICAL | SWT.BORDER);
		this.sashForm.setBounds(new Rectangle(3, 4, 654, 294));

		this.mainComposite = new Composite(this.sashForm, SWT.NONE);
		this.mainComposite.setLayout(new FillLayout());
		createMarkersTable();
		updateTable();
		createCommentComponents();

		this.sashForm.setMaximizedControl(this.mainComposite);

		if(this.markersTable!=null && this.markersTable.getColumnCount()>0){
			ActivatorData.getInstance().getVueData().addMarkersListener(this);

			getSite().setSelectionProvider(this);
			ISelectionService selService = getSite().getWorkbenchWindow().getSelectionService();
			selService.addPostSelectionListener(this);
		}
		//ajout des actions � la toolbar
		makeActions();
		this.bars = getViewSite().getActionBars();
		ajoutActionToolBar(supprimerMarqueurAction);
		ajoutActionToolBar(mesurerDeltaValeurMarqueurAction);
		ajoutActionToolBar(imprimerVueAction);
		ajoutActionToolBar(capturerVueAction);
		ajoutActionToolBar(synchroVuesAction);
		
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
				try {
					GestionnaireVueMarqueurs.getInstance().enregistrerLargeurColonnes();
				} catch (AExceptionSamNG e) {
					MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING);
					msgBox.setMessage(Messages.getString("VueMarqueurs.16")); //$NON-NLS-1$
					msgBox.setText(Messages.getString("VueMarqueurs.17")); //$NON-NLS-1$
					msgBox.open();
				}
				finally {
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

	/** D�claration d'action */
	public void makeActions(){
//		r�cup�ration de la fenetre active
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

		listeActionsAuditrice = new ArrayList<Action>(0);

		this.supprimerMarqueurAction = new SupprimerMarqueurAction();
		this.mesurerDeltaValeurMarqueurAction= new MesureDeltaValeurMarqueurAction();
		this.imprimerVueAction = new ImprimerVueAction(window,Messages.getString("VueMarqueurs.1") ); //$NON-NLS-1$
		this.capturerVueAction = new CapturerVueAction(window,Messages.getString("VueMarqueurs.2") ); //$NON-NLS-1$


//		ajout des actions		
		synchroVuesAction = new Action(){
			public void run(){

				Message msg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(((MessageSelection)currentSelection).getMessageId());
				ActivatorData.getInstance().setSelectedMsg(msg);
				((MessageSelection)currentSelection).setUserSentSelection(true);
				fireSelectionChanged((MessageSelection)currentSelection);
				((MessageSelection)currentSelection).setUserSentSelection(false);

			}
		};
		synchroVuesAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/vueToolBars/vues_commun_synchro.png")); //$NON-NLS-1$
		synchroVuesAction.setText(Messages.getString("VueMarqueurs.4")); //$NON-NLS-1$
		synchroVuesAction.setToolTipText(Messages.getString("VueMarqueurs.5")); //$NON-NLS-1$
		synchroVuesAction.setEnabled(false);
		listeActionsAuditrice.add(synchroVuesAction);
	}

	/**
	 * M�thode d'ajout d'une action dans le menu
	 * @param action
	 */
	public void ajoutActionToolBar(Action action) {
		this.bars.getToolBarManager().add(action);
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
		this.bars.getMenuManager().add(new Separator());
	}


	@Override
	public void setFocus() {
	}

	private boolean getTimeCorrectionApply(){
		boolean apply=false;
		if(ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige")!=null) //$NON-NLS-1$
			if (ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige").equals(true)){ //$NON-NLS-1$
				apply=true;
			}
		return apply;
	}

	private boolean getDistanceCorrectionApply(){
		boolean apply=false;
		if(ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")!=null) //$NON-NLS-1$
			if (ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige").equals(true)){ //$NON-NLS-1$
				apply=true;
			}
		return apply;
	}

	private void createMarkersTable() {

		this.markersTable = new Table(this.mainComposite, SWT.MULTI | SWT.FULL_SELECTION);
		this.markersTable.setHeaderVisible(true);
		this.markersTable.setLinesVisible(true);
		this.markersTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				updateComment();
			}
		});
		this.markersTable.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					supprimerMarqueurAction.run();
				}
				// TODO Raccord de m�thode auto-g�n�r�

			}

			public void keyReleased(KeyEvent e) {
				// TODO Raccord de m�thode auto-g�n�r�

			}

		});
		this.markersTable.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				if(e.button == 1) {	//left button double click
					((MessageSelection)currentSelection).setUserSentSelection(true);
					fireSelectionChanged(currentSelection);
					((MessageSelection)currentSelection).setUserSentSelection(false);
				}
			}
			public void mouseDown(MouseEvent e) {}

			public void mouseUp(MouseEvent e) {
				for(int i=0; i<listeActionsAuditrice.size();i++){
					listeActionsAuditrice.get(i).setEnabled(true);
				}

			}
		});

		this.mapNomUniqueNomUtilisateur = new HashMap<String, String>();

		if(ActivatorData.getInstance().getVueData().getDataTable()!=null){
			//Add a dummy column on first possition as Table in SWT alignes
			//the text of the first column to the left. We want Time column 
			//text to be aligned also to center but if we don't do this it
			//will not happen
			this.dummyColumn = new TableColumn(this.markersTable, SWT.NONE);
			this.dummyColumn.setText(""); //$NON-NLS-1$
			this.dummyColumn.setWidth(0);
			this.dummyColumn.setResizable(false);

			Langage langue = Activator.getDefault().getCurrentLanguage();

			String nomUtilisateur;
			CURRENT_POS = 0;
			if (GestionnairePool.getInstance().getVariable(TypeRepere.temps.getCode())!=null) {
				CURRENT_POS++;
				TIME_COL_POS=CURRENT_POS;
				this.timeColumn = new TableColumn(this.markersTable, SWT.NONE);
				//String nomUtilisateur = GestionnairePool.getVariable(TypeRepere.temps.getCode()).getDescriptor().getNomUtilisateur().getNomUtilisateur(langue);
				this.timeColumn.setText(Messages.getString("VueMarqueurs.7")); //$NON-NLS-1$
				//this.timeColumn.setText(nomUtilisateur); 
				this.timeColumn.setToolTipText((Messages.getString("VueMarqueurs.7"))); //$NON-NLS-1$
				this.timeColumn.setWidth(this.dataColumnsWidth[TIME_COL_POS-1]);
				this.timeColumn.setAlignment(SWT.CENTER);
				this.timeColumn.setResizable(true);

				this.mapNomUniqueNomUtilisateur.put(Messages.getString("VueMarqueurs.7"), "temps"); //$NON-NLS-1$ //$NON-NLS-2$

				if(timeCorrectionapplied){
					CURRENT_POS++;
					TIMECOR_COL_POS=CURRENT_POS;
					//    	timeCorrectionapplied=1;
					this.timeCorrectedColumn = new TableColumn(this.markersTable, SWT.NONE);
					this.timeCorrectedColumn.setWidth(this.dataColumnsWidth[TIMECOR_COL_POS-1]);
					this.timeCorrectedColumn.setAlignment(SWT.CENTER);
					this.timeCorrectedColumn.setResizable(true);
					nomUtilisateur = GestionnairePool.getInstance().getVariable(TypeRepere.tempsCorrigee.getCode()).getDescriptor().getNomUtilisateur().getNomUtilisateur(langue);
					this.timeCorrectedColumn.setText(nomUtilisateur); //$NON-NLS-1$
					//this.timeCorrectedColumn.setText(Messages.getString("VueMarqueurs.13")); //$NON-NLS-1$
					this.timeCorrectedColumn.setToolTipText(nomUtilisateur);
					//this.timeCorrectedColumn.setToolTipText((Messages.getString("VueMarqueurs.13"))); //$NON-NLS-1$
					this.mapNomUniqueNomUtilisateur.put(nomUtilisateur, "temps_corrigee"); //$NON-NLS-1$
				}
			}

			if (GestionnairePool.getInstance().getVariable(TypeRepere.distance.getCode())!=null) {
				CURRENT_POS++;
				ACC_DIST_COL_POS = CURRENT_POS;
				this.accumulatedDistanceColumn = new TableColumn(this.markersTable, SWT.NONE);
				nomUtilisateur = Messages.getString("VueMarqueurs.10");
				this.accumulatedDistanceColumn.setText(nomUtilisateur);
				this.accumulatedDistanceColumn.setWidth(this.dataColumnsWidth[ACC_DIST_COL_POS-1]);
				this.accumulatedDistanceColumn.setAlignment(SWT.CENTER);
				this.accumulatedDistanceColumn.setResizable(true);
				this.mapNomUniqueNomUtilisateur.put(nomUtilisateur, "accumulated_distance");
				
				//CURRENT_POS++; // CPIGNON: Pourquoi ce ++ ici? Comment� car utilit� non percue et explique un d�calage qui ne devrait pas avoir lieu

//				if(distanceCorrectionapplied){
//					CURRENT_POS++;
//					DISTCOR_COL_POS=CURRENT_POS;
//					this.distanceCorrectedColumn = new TableColumn(this.markersTable, SWT.NONE);
//					this.distanceCorrectedColumn.setWidth(this.dataColumnsWidth[DISTCOR_COL_POS-1]);
//					this.distanceCorrectedColumn.setAlignment(SWT.CENTER);
//					this.distanceCorrectedColumn.setResizable(true);
//					nomUtilisateur = GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode()).getDescriptor().getNomUtilisateur().getNomUtilisateur(langue);
//					this.distanceCorrectedColumn.setText(nomUtilisateur); //$NON-NLS-1$
//					//this.distanceCorrectedColumn.setText(Messages.getString("VueMarqueurs.14")); //$NON-NLS-1$
//					this.distanceCorrectedColumn.setToolTipText(nomUtilisateur);
//
//					//this.distanceCorrectedColumn.setToolTipText((Messages.getString("VueMarqueurs.14")));			 //$NON-NLS-1$
//					this.mapNomUniqueNomUtilisateur.put(nomUtilisateur, "distance_corrigee"); //$NON-NLS-1$
//				}
			}

			CURRENT_POS++;
			NAME_COL_POS=CURRENT_POS;
			this.markerNameColumn = new TableColumn(this.markersTable, SWT.NONE);
			this.markerNameColumn.setText(Messages.getString("VueMarqueurs.9")); //$NON-NLS-1$
			this.markerNameColumn.setToolTipText((Messages.getString("VueMarqueurs.9"))); //$NON-NLS-1$
			this.markerNameColumn.setWidth(this.dataColumnsWidth[NAME_COL_POS-1]);
			this.markerNameColumn.setAlignment(SWT.CENTER);
			this.markerNameColumn.setResizable(true);

			this.mapNomUniqueNomUtilisateur.put(Messages.getString("VueMarqueurs.9"), "nom_marqueur"); //$NON-NLS-1$ //$NON-NLS-2$

			//ajout d'un gestionnaire de redimensionnement de colonne pour sauvegarder les largeurs de colonnes

			ControlAdapter controlAdapteurRedimensionnementColonnes = new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					if (event.widget instanceof TableColumn) {
						enregistrerConfigurationColonne();
					}
				}
			};
			for (TableColumn colonne : this.markersTable.getColumns()) {
				colonne.addControlListener(controlAdapteurRedimensionnementColonnes);
			}

			try {
				GestionnaireVueMarqueurs.getInstance().init();
				GestionnaireVueMarqueurs.getInstance().chargerLargeurColonnes();
				HashMap<String, Integer> mapColonnesLargeurs = GestionnaireVueMarqueurs.getInstance().getMapColonneLargeur();
				HashMap<String, Integer> mapColonnesLargeursTmp = new HashMap<String, Integer>();
				for (String nomUnique : mapColonnesLargeurs.keySet()) {
					mapColonnesLargeursTmp.put(nomUnique, mapColonnesLargeurs.get(nomUnique));
				}

				if(mapColonnesLargeurs!= null && mapColonnesLargeurs.size()>0)
				{
					TableColumn[] colonnes = this.markersTable.getColumns();
					for (TableColumn column : colonnes) {{
						String nomUser = column.getText();
						String nomUnique = this.mapNomUniqueNomUtilisateur.get(nomUser);

						if(mapColonnesLargeursTmp.get(nomUnique)!=null)
							column.setWidth(mapColonnesLargeursTmp.get(nomUnique).intValue());
						
					}
					}
				}
				GestionnaireVueMarqueurs.getInstance().setMapColonneLargeur(mapColonnesLargeursTmp);
			} catch (AExceptionSamNG e1) {
				e1.printStackTrace();
			}  
		}
	}

	private void updateComment() {
		String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
		String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
		String minute = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
		String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
		String milliseconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");
		int[] selIndices = markersTable.getSelectionIndices();
		if(selIndices == null || selIndices.length == 0) {
			((MessageSelection)currentSelection).setMessagesIds(null);
			sashForm.setMaximizedControl(mainComposite);

			try {
				if (messageSelected[0]!=-1	) {
					markersTable.setSelection(messageSelected);
					messageSelected[0]=-1;
				}
			} catch (Exception e) {
				// : handle exception
			}
		}
		else {
			TableItem tblItem;
			int[] selIds = new int[selIndices.length];
			String firstComment = ""; //$NON-NLS-1$
			AMarqueur marker;
			for(int i = 0; i<selIndices.length; i++) {
				tblItem = markersTable.getItem(selIndices[i]);
				marker = (AMarqueur)tblItem.getData();
				selIds[i] = marker.getIdMessage();
				if(i == 0)
					firstComment = ((Marqueur)marker).getCommentaire();
			}
			((MessageSelection)currentSelection).setMessagesIds(selIds);
			sashForm.setMaximizedControl(null);
			commentTextArea.setText(firstComment);


			String newTitle=Messages.getString("VueMarqueurs.0"); //$NON-NLS-1$
			String timeComplementTitle=""; //$NON-NLS-1$
			String distanceComplementTitle=""; //$NON-NLS-1$

			if ((!timeCorrectionapplied) && (!distanceCorrectionapplied) || selIndices.length>1) {
				this.setPartName(Messages.getString("VueMarqueurs.0")); //$NON-NLS-1$
			}else{
				if (timeCorrectionapplied && selIndices.length==1) {

					try {
						String tempsInitial=this.markersTable.getItem(selIndices[0]).getText(1);
						String tempsCourant=this.markersTable.getItem(selIndices[0]).getText(TIMECOR_COL_POS);
						Long timeDiff=ConversionTemps.calculatePeriodAsLong(tempsInitial, tempsCourant);
						String timeDiffSTR=ConversionTemps.getRelativeTimeAsString(timeDiff,jour,heure,minute,seconde,milliseconde);
						timeComplementTitle=" ["+ Messages.getString("VueMarqueursDeltaValues.19")+" "+timeDiffSTR+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					} catch (Exception e) {
						timeComplementTitle=" ["+ Messages.getString("VueMarqueursDeltaValues.19")+" "+	"###"+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}
				}
				if (distanceCorrectionapplied && selIndices.length==1) {

					try {
						int markerNum=selIndices[0];
						int tab[]=new int[0];
						tab=ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueursIds();
						int markerID=tab[markerNum];
						int IDmsg=ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(markerID).getIdMessage();
						SegmentDistance segDist = TableSegments.getInstance().getContainingDistanceSegment(IDmsg);

						double diff = segDist.getDiameterCorrige() - segDist.getInitialDiameter();
						String	distanceDiffSTR = String.valueOf(diff);
						if (diff>0) {
							distanceDiffSTR="+"+distanceDiffSTR; //$NON-NLS-1$
						}

						String uniteDiam=""; //$NON-NLS-1$
						try {
							uniteDiam = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.diametreRoue.getCode()).getUnite();			
						} catch (Exception e) {
							// : handle exception
						}

						distanceComplementTitle=" ["+ Messages.getString("VueMarqueursDeltaValues.20")+" "+distanceDiffSTR+" "+uniteDiam+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					} catch (Exception e) {
						distanceComplementTitle=" ["+ Messages.getString("VueMarqueursDeltaValues.20")+" "+"###"+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
					}
				}
			}
			newTitle=newTitle+timeComplementTitle+distanceComplementTitle;
			this.setPartName(newTitle);
		}
	}

	private void createCommentComponents() {
		this.commentComposite = new Composite(this.sashForm, SWT.NONE);
		GridLayout mainCompositeLayout = new GridLayout();
		mainCompositeLayout.makeColumnsEqualWidth = true;
		this.commentComposite.setLayout(mainCompositeLayout);
		createCommentTextArea();
		createOkCancelButtonsPanel();
	}

	private void createCommentTextArea() {
		//Create the configuration table
		GridData columnsCfgTableLData = new GridData();
		columnsCfgTableLData.verticalAlignment = GridData.FILL;
		columnsCfgTableLData.horizontalAlignment = GridData.FILL;
		columnsCfgTableLData.grabExcessVerticalSpace = true;
		columnsCfgTableLData.grabExcessHorizontalSpace = true;
		this.commentTextArea = new Text(this.commentComposite, SWT.BORDER | SWT.MULTI);
		this.commentTextArea.setEditable(!GestionLineCommandParameters.isAnnot_Lect_seule());
		this.commentTextArea.setLayoutData(columnsCfgTableLData);
		this.commentTextArea.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent event) {
				int selIdx = markersTable.getSelectionIndex();
				if(selIdx == -1)
					return;
				String originalCommentText = ((Marqueur)markersTable.getItem(selIdx).getData()).getCommentaire();
				boolean differentText = !commentTextArea.getText().equals(originalCommentText);
				commentOkBtn.setEnabled(differentText);
				commentCancelBtn.setEnabled(differentText);
			}
		});
	}

	private void createOkCancelButtonsPanel() {
		//create the panel for the two buttons
		this.commentOkCancelBtnsComposite = new Composite(this.commentComposite, SWT.BORDER);
		FormLayout okCancelBtnsCompLayout = new FormLayout();
		GridData okCancelBtnsCompLData = new GridData();
		okCancelBtnsCompLData.grabExcessHorizontalSpace = true;
		okCancelBtnsCompLData.horizontalAlignment = GridData.FILL;
		okCancelBtnsCompLData.verticalAlignment = GridData.END;
		okCancelBtnsCompLData.heightHint = 50;
		this.commentOkCancelBtnsComposite.setLayoutData(okCancelBtnsCompLData);
		this.commentOkCancelBtnsComposite.setLayout(okCancelBtnsCompLayout);

		//Create the apply button
		this.commentOkBtn = new Button(this.commentOkCancelBtnsComposite, SWT.PUSH);
		FormData okBtnLData = new FormData();
		okBtnLData.height = 25;
		//okBtnLData.width = 50;
		//position it near the center of the container panel
		okBtnLData.right =  new FormAttachment(48, -4);
		okBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.commentOkBtn.setLayoutData(okBtnLData);
		this.commentOkBtn.setText(Messages.getString("VueMarqueurs.11")); //$NON-NLS-1$
		this.commentOkBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int selIdx = markersTable.getSelectionIndex();
				if(selIdx == -1)
					return;
				TableItem itemInfo = markersTable.getItem(selIdx);
				Marqueur marker = (Marqueur)itemInfo.getData();
				marker.setCommentaire(commentTextArea.getText());
				ActivatorData.getInstance().getGestionnaireMarqueurs().setModifie(true);
				commentOkBtn.setEnabled(false);
				commentCancelBtn.setEnabled(false);
			}
		});

		//Create the close button
		this.commentCancelBtn = new Button(this.commentOkCancelBtnsComposite, SWT.PUSH);
		FormData cancelBtnLData = new FormData();
		cancelBtnLData.height = 25;
		//cancelBtnLData.width = 50;
		//align the close button to the right of the apply button
		cancelBtnLData.left =  new FormAttachment(this.commentOkBtn, 4);
		cancelBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.commentCancelBtn.setLayoutData(cancelBtnLData);
		this.commentCancelBtn.setText(Messages.getString("VueMarqueurs.12")); //$NON-NLS-1$
		this.commentCancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int selIdx = markersTable.getSelectionIndex();
				if(selIdx == -1)
					return;
				String originalCommentText = ((Marqueur)markersTable.getItem(selIdx).getData()).getCommentaire();
				commentTextArea.setText(originalCommentText);
				commentOkBtn.setEnabled(false);
				commentCancelBtn.setEnabled(false);
			}
		});
		commentOkBtn.setEnabled(false);
		commentCancelBtn.setEnabled(false);
	}

	/**
	 * Updates the markers table
	 *
	 */
	private void updateTable() {

		TableItem tblItem;
		String[] itemText = new String[NAME_COL_POS+1];

		List<TableRowData> rowData = createRowData();
		Collections.sort(rowData, comparator);
		itemText[0] = ""; //$NON-NLS-1$

		for(TableRowData row: rowData) {
			tblItem = new TableItem(this.markersTable, SWT.NONE);
			Color correctionColor=new Color(Display.getCurrent(),255, 183, 161);
			tblItem.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
			//Update the time column
			//We know that the value of the corrected time is time millis since 1 Jan 1970
			if (TIME_COL_POS!=0) {
				itemText[TIME_COL_POS] = ConversionTemps.getFormattedDate(row.msg.getAbsoluteTime(), true);
			}
			
			if (ACC_DIST_COL_POS!=0) {
				BigDecimal accBD = (new BigDecimal(row.msg.getAccumulatedDistance())).setScale(3, RoundingMode.HALF_UP);
				itemText[ACC_DIST_COL_POS] = String.valueOf(accBD.doubleValue());
			}
			
			//Update the distance column
			itemText[NAME_COL_POS] = row.marker.getNom();
			String str=null;
			tblItem.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

			if(timeCorrectionapplied){
				str = row.msg.getTempsCorrige();
				if(str==null)
					str =itemText[TIME_COL_POS]; //$NON-NLS-1$
				itemText[TIMECOR_COL_POS] = str;
				//String.valueOf(row.msg.getVariable(TypeRepere.tempCorrigee.getCode()));
				tblItem.setBackground(TIMECOR_COL_POS,correctionColor);
			}

//			if(distanceCorrectionapplied){		   
//				str = String.valueOf(row.msg.getDistanceCorrige());
//				if(str.equals("null")) //$NON-NLS-1$
//					str =dieses; //$NON-NLS-1$
//				itemText[DISTCOR_COL_POS] = str;
//				//String.valueOf(row.msg.getVariable(TypeRepere.distanceCorrigee.getCode()));
//				tblItem.setBackground(DISTCOR_COL_POS,correctionColor);
//			}		
			//add the table item

			tblItem.setText(itemText);
			tblItem.setData(row.marker);			
		}

		final TableEditor editor = new TableEditor(this.markersTable);
		//The editor must have the same size as the cell and must
		//not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		this.markersTable.addListener (SWT.MouseDown, new Listener () {
			public void handleEvent (Event event) {
				// Identify the selected row
				Rectangle clientArea = markersTable.getClientArea ();
				Point pt = new Point (event.x, event.y);
				int index = markersTable.getTopIndex ();
				TableItem item = null;

				while (index < markersTable.getItemCount ()) {
					boolean visible = false;
					TableItem item1 = markersTable.getItem (index);
					Rectangle rect = item1.getBounds (NAME_COL_POS);
					if (rect.contains (pt)) {
						item = item1;
						break;
					}
					if (!visible && rect.intersects (clientArea)) {
						visible = true;
					}
					if (!visible) 
						return;
					index++;
				}

				if(item == null)
					return;

				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null) 
					oldEditor.dispose();

				// The control that will be the editor must be a child of the Table
				final Text newEditor = new Text(markersTable, SWT.NONE);
				newEditor.setText(item.getText(NAME_COL_POS));
				newEditor.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent arg0) {
					}

					public void focusLost(FocusEvent arg0) {
						AMarqueur marker = (AMarqueur)editor.getItem().getData();
						String newMarkerName = newEditor.getText();
						if(ActivatorData.getInstance().getGestionnaireMarqueurs().isMarkerName(newMarkerName)) {
							newEditor.dispose();
							return;
						}
						//si le nom change on signale un changement dans la liste des marqueurs
						if(!marker.getNom().equals(newMarkerName))
							ActivatorData.getInstance().getGestionnaireMarqueurs().setModifie(true);
						editor.getItem().setText(NAME_COL_POS, newMarkerName);
						marker.setNom(newMarkerName);
						newEditor.dispose();
					}
				});
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, NAME_COL_POS);
				newEditor.addKeyListener(new KeyAdapter() {
					public void keyPressed(KeyEvent event) {
						switch (event.keyCode) {
						case SWT.CR:
							AMarqueur marker = (AMarqueur)editor.getItem().getData();
							String newMarkerName = newEditor.getText();
							if(ActivatorData.getInstance().getGestionnaireMarqueurs().isMarkerName(newMarkerName))
								return;
							editor.getItem().setText(NAME_COL_POS, newMarkerName);
//							si le nom change on signale un changement dans la liste des marqueurs
							if(!marker.getNom().equals(newMarkerName))
								ActivatorData.getInstance().getGestionnaireMarqueurs().setModifie(true);
							marker.setNom(newMarkerName);
						case SWT.ESC:
							newEditor.dispose();
							break;
						}
					}
				});
			}
		});

		updateComment();
	}

	/**
	 * Creates a temporary list of row data needed for sorting
	 * @return
	 */
	private List<TableRowData> createRowData() {
		Message msg = null;
		ArrayList<TableRowData> rowData = new ArrayList<TableRowData>();
		AMarqueur[] markers = ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurs();
		for(AMarqueur marker: markers) {
			if(!(marker instanceof Marqueur))
				continue;
			msg = getMessageForId(marker.getIdMessage());
			if(msg == null) {	//markers that cannot be found in data are not displayed
				continue;
			}
			rowData.add(new TableRowData((Marqueur)marker, msg));
		}
		rowData.trimToSize();
		return rowData;
	}

	/**
	 * Return the message with the given ID from the records table (loaded from binary file)
	 * @param id the id of the message to be searched
	 * @return
	 */
	private Message getMessageForId(int id) {
		AParcoursComposant dataTable = ActivatorData.getInstance().getVueData().getDataTable();
		Enregistrement e = dataTable.getEnregistrement();
		return e.getGoodMessage(id);
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		//avoid handling events sent by this view

		//positionnement offset
		if (GestionLineCommandParameters.getIndiceMsg()!=-1 && oneTimeOffset) {
			try {
				oneTimeOffset=false;
				int selId = GestionLineCommandParameters.getIndiceMsg();
				TableItem[] tableItems = this.markersTable.getItems();
				int tblItemMsgId;
				for(TableItem tblItem: tableItems) {
					tblItemMsgId = ((AMarqueur)tblItem.getData()).getIdMessage();
					if(tblItemMsgId == selId) {
						this.markersTable.setSelection(tblItem);
						return;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			//traitement normal
		}else if((selection instanceof MessageSelection) && (part.equals(this)) && !selection.isEmpty()) {
			int selId = ((MessageSelection)selection).getMessageId();
			TableItem[] tableItems = this.markersTable.getItems();
			int tblItemMsgId;
			for(TableItem tblItem: tableItems) {
				tblItemMsgId = ((AMarqueur)tblItem.getData()).getIdMessage();
				if(tblItemMsgId == selId) {
					this.markersTable.setSelection(tblItem);
					return;
				}
			}
		}
//		updateComment();
//		updateMarkersTable();
		if (!markersTable.isDisposed()) {
			try {
				messageSelected = markersTable.getSelectionIndices();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		if (((!timeCorrectionapplied) && getTimeCorrectionApply())	|| (timeCorrectionapplied && !getTimeCorrectionApply()))
//			updateMarkersTable();
//
//		if (((!distanceCorrectionapplied) && getDistanceCorrectionApply())	|| (distanceCorrectionapplied && !getDistanceCorrectionApply()))
//			updateMarkersTable();

	}

	public void onRepereRemoved(TypeRepere... reper) {
		updateMarkersTable();
	}

	public void onRepereAdded(TypeRepere... reper) {	
		updateMarkersTable();
	}

	public void marquersListeChangement(int[] msgIds, int[] oldMsgIds) {
		updateTable();
	}

	public void marqueurAjoutee(int msgId) {
		updateMarkersTable();
	}

	public void marqueurEffacee(int msgId) {
		updateMarkersTable();
	}

	private void updateMarkersTable() {

		timeCorrectionapplied=false;
		distanceCorrectionapplied=false;
		//first save the column widths
		if(getTimeCorrectionApply()){
			timeCorrectionapplied=true;
			this.timeCorrectedColumn = new TableColumn(this.markersTable, SWT.NONE);
			this.timeCorrectedColumn.setWidth(this.dataColumnsWidth[TIME_COL_POS]);
			this.timeCorrectedColumn.setAlignment(SWT.CENTER);
			this.timeCorrectedColumn.setResizable(true);
			this.timeCorrectedColumn.setText(Messages.getString("VueMarqueurs.13")); //$NON-NLS-1$
			this.timeCorrectedColumn.setToolTipText((Messages.getString("VueMarqueurs.13"))); //$NON-NLS-1$
		}

		if(getDistanceCorrectionApply()){
			distanceCorrectionapplied=true;
			this.distanceCorrectedColumn = new TableColumn(this.markersTable, SWT.NONE);
			this.distanceCorrectedColumn.setWidth(this.dataColumnsWidth[DISTCOR_COL_POS]);
			this.distanceCorrectedColumn.setAlignment(SWT.CENTER);
			this.distanceCorrectedColumn.setResizable(true);
			this.distanceCorrectedColumn.setText(Messages.getString("VueMarqueurs.14")); //$NON-NLS-1$
			this.distanceCorrectedColumn.setToolTipText((Messages.getString("VueMarqueurs.14")));			 //$NON-NLS-1$
		}
		
		if (TIME_COL_POS!=0) {
			this.dataColumnsWidth[TIME_COL_POS-1] = this.timeColumn.getWidth();
			if (timeCorrectionapplied) {
				this.dataColumnsWidth[TIME_COL_POS] = this.timeCorrectedColumn.getWidth();
			}
		}
		
		this.dataColumnsWidth[NAME_COL_POS-1] = this.markerNameColumn.getWidth();
		//dispose the table
		this.markersTable.dispose();
		//recreate the table and update it with current data

		createMarkersTable();
		updateTable();
		this.mainComposite.layout();
	}

	private class TableRowData {
		public Marqueur marker;
		public Message msg;
		public TableRowData(Marqueur marker, Message msg) {
			this.marker = marker;
			this.msg = msg;
		}
	}
	private class TableRowComparator implements Comparator<TableRowData> {
		public int compare(TableRowData o1, TableRowData o2) {
			return (int)(o1.msg.getAbsoluteTime() - o2.msg.getAbsoluteTime());
		}
	}

	@Override
	public void dispose() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		ActivatorData.getInstance().getVueData().removeMarkersListener(this);
		super.dispose();
	}

	public Composite getContenu() {
		//  Auto-generated method stub

		return this.sashForm;
	}

	public int promptToSaveOnClose() {
		//  Ap�ndice de m�todo generado autom�ticamente
		return 0;
	}

	public void doSave(IProgressMonitor monitor) {
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		//  Ap�ndice de m�todo generado autom�ticamente
		return false;
	}

	public boolean isSaveAsAllowed() {
		//  Ap�ndice de m�todo generado autom�ticamente
		return true;
	}

	public boolean isSaveOnCloseNeeded() {
		/***
		 * Lors de la fermeture de la vue on enregistre la configuration de celle-ci en m�moire(pas dans le xml)
		 */
		if(!this.markersTable.isDisposed())
			enregistrerConfigurationColonne();
		return true;
	}

	public void onDataChange() {
		if (!this.markersTable.isDisposed()) {
			enregistrerConfigurationColonne();
		}
	}

	/**
	 * M�thode qui enregistre la configuration de celle-ci en m�moire(pas dans le xml)
	 *
	 */
	private void enregistrerConfigurationColonne(){
		TableColumn[] colonnes = this.markersTable.getColumns();
		for (TableColumn column : colonnes) {
			int largeur = column.getWidth();
			String nomUser = column.getText();
			String nomUnique = VueMarqueurs.this.mapNomUniqueNomUtilisateur.get(nomUser);
			if (GestionnaireVueMarqueurs.getInstance().getMapColonneLargeur()!=null) {
				GestionnaireVueMarqueurs.getInstance().getMapColonneLargeur().put(nomUnique, Integer.valueOf(largeur));
			}
		}
	}
}
