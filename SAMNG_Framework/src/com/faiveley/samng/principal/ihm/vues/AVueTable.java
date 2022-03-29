package com.faiveley.samng.principal.ihm.vues;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
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
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.dialogs.RechercherMarqueurAction;
import com.faiveley.samng.principal.ihm.actions.vue.SetReferenceAction;
import com.faiveley.samng.principal.ihm.calcul.PositionMilieuViewer;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchMarquerListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.progbar.BarreProgressionDialog;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions.AjouterAnnotationAction;
import com.faiveley.samng.principal.ihm.vues.vuetoolbar.IVueToolbar;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnaireVariablesComposee;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.marqueurs.Marqueur;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;

/**
 * Abstract class for a view which represents its data in a table
 * 
 * @author meggy
 * 
 */
public abstract class AVueTable extends AbstractSelectionProviderVue implements PropertyChangeListener, IDataChangedListener, ISelectionListener, IMarqueursListener, IRepereChangedListener, ISearchMarquerListener, ISearchVariableListener, ICapturable, IVueToolbar {

	boolean oneTimeOffset = true;// le positionnement offset ne se fait qu'une
									// fois
	// context menu
	protected Menu popupMenu;

	protected Listener menuSelListener;

	// list of column names
	protected List<String> columnNames;

	public FixedColumnTable tblFix;

	// the data
	protected VueData data;

	// the base manager of the view
	protected GestionnaireVueListeBase gestionaireVue;

	// parent
	protected Composite top;

	// ToolBar
	protected IActionBars bars;

	//
	protected String initialPartName;

	// the index of the last selected message
	protected int lastSelOffsetFromTop = 0;

	protected boolean usesShortNames;

	// dummy column to fix horizontal scrolling for scrollable table
	private TableColumn dummyCol;

	/* synchronization of the table from the keyboard */
	private boolean syncFromKey = false;
	
	// //////////////////
	private boolean multiSelect = false;
	private int IdSelect = -1;
	private int[] idsSelected;

	private boolean selectCntrl = false;
	private boolean CntrlA = false;

	private List<AFiltreComposant> listeFiltreOrd;
	
	private AVueTableContentProvider contentProvider;
	
	private int valueCounter;
	private int previousSelectedMessageId;

	public boolean isCntrlA() {
		return CntrlA;
	}

	public void setCntrlA(boolean cntrlA) {
		CntrlA = cntrlA;
	}

	public boolean isSelectCntrl() {
		return selectCntrl;
	}

	public int[] getIdsSelected() {
		return idsSelected;
	}

	protected void setSelectCntrl(boolean b) {
		this.selectCntrl = b;
	}

	public void setIdsSelected(int[] idsSelected) {
		this.idsSelected = idsSelected;
	}

	public int getIdSelect() {
		return IdSelect;
	}

	public void setIdSelect(int[] idsSelected) {
		IdSelect = AVueTable.this.tblFix.getSelectionIndex();
		this.idsSelected = idsSelected;
	}

	/**
	 * D�claration des actions
	 */
	protected Action synchroVuesAction;

	protected SetReferenceAction poserReferenceAction;

	protected AjouterAnnotationAction ajoutAnnotationAction;

	protected Action annotationPrecedenteAction;

	protected Action annotationSuivanteAction;

	protected RechercherMarqueurAction marqueurSuivantAction;

	protected RechercherMarqueurAction marqueurPrecedentAction;

	protected Action imprimerVueAction;

	protected CapturerVueAction capturerVueAction;

	protected Action exportSelectionAction;

	protected ArrayList<Action> listeActionsAuditrice;

	/** */
	public AVueTable() {
		// load the parcours data
		this.data = ActivatorData.getInstance().getVueData();
		this.columnNames = new ArrayList<String>(0);
		this.currentSelection = new MessageSelection();
		this.valueCounter = 0;
		this.previousSelectedMessageId = -1;
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

	public void upSelection() {
		// update selection
		updateSelectionMessage(true);
		((MessageSelection) AVueTable.this.currentSelection).setUserSentSelection(true);
		fireSelectionChanged(AVueTable.this.currentSelection);
		((MessageSelection) AVueTable.this.currentSelection).setUserSentSelection(false);
		((MessageSelection) AVueTable.this.currentSelection).setShouldNotTriggerAudio(false);
	}

	public void makeActions() {
		// r�cup�ration de la fenetre active
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		listeActionsAuditrice = new ArrayList<Action>(0);

		// ajout des actions
		synchroVuesAction = new Action() {
			public void run() {
				ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
				upSelection();
			}
		};
		synchroVuesAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_synchro.png")); //$NON-NLS-1$
		synchroVuesAction.setText(Messages.getString("AVueTable.1")); //$NON-NLS-1$
		synchroVuesAction.setToolTipText(Messages.getString("AVueTable.2")); //$NON-NLS-1$
		synchroVuesAction.setEnabled(true);

		poserReferenceAction = new SetReferenceAction(Messages.getString("AVueTable.3"), com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolbar/vues_commun_reference.png")); //$NON-NLS-1$
		poserReferenceAction.setEnabled(true);

		ajoutAnnotationAction = new AjouterAnnotationAction();
		ajoutAnnotationAction.setEnabled(true);

		marqueurSuivantAction = new RechercherMarqueurAction(ICommandIds.CMD_OPEN_SEARCH_MARQUER, Messages.getString("AVueTable.30"), com.faiveley.samng.principal.ihm.Activator //$NON-NLS-1$
				.getImageDescriptor("/icons/toolBar/vues_commun_annotation_suivante.png"), true); //$NON-NLS-1$

		marqueurPrecedentAction = new RechercherMarqueurAction(ICommandIds.CMD_OPEN_SEARCH_MARQUER, Messages.getString("AVueTable.31"), com.faiveley.samng.principal.ihm.Activator //$NON-NLS-1$
				.getImageDescriptor("/icons/toolBar/vues_commun_annotation_precedente.png"), false); //$NON-NLS-1$

		capturerVueAction = new CapturerVueAction(window, Messages.getString("AVueTable.7")); //$NON-NLS-1$
		// capturerVueAction.setEnabled(capturerVueAction.isEnabled());
		capturerVueAction.setEnabled(true);
	}

	/**
	 * Initialize the common controls
	 */
	public void createPartControl(final Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {
			return;
		}

		// this.initialPartName = getPartName();
		this.top = new Composite(parent, SWT.NONE);

		// add this vue as listener for data and repere changes
		ActivatorData.getInstance().addDataListener(this);
		ActivatorData.getInstance().addRepereListener(this);

		// : maybe these shoul be set in content provider???
		// if there are distance or time corrections then the colums should be
		// added to the table
		if (TableSegments.getInstance().isAppliedDistanceCorrections()) {
			this.gestionaireVue.updateRepere(TypeRepere.distanceCorrigee);
			this.gestionaireVue.updateRepere(TypeRepere.vitesseCorrigee);
		}
		if (TableSegments.getInstance().isAppliedTempCorrections()) {
			this.gestionaireVue.updateRepere(TypeRepere.tempsCorrigee);
		}

		if (getData() != null) {
			if (getData().getDataTable() != null) {
				// Create a table with visible headers and lines,
				// and set the font that we created
				loadTable(false);
			}
		}
		if (this.tblFix != null) {
			// Register this as Selection provider and as selection listener
			getSite().setSelectionProvider(this);
			ISelectionService selService = getSite().getWorkbenchWindow().getSelectionService();
			selService.addPostSelectionListener(this);
		}

		System.out.println("TABLE RUPTURE TEMP : " + TableRuptures.getInstance().getListeRupturesTemps()); //$NON-NLS-1$
		System.out.println("TABLE RUPTURE DIST : " + TableRuptures.getInstance().getListeRupturesDistance()); //$NON-NLS-1$

		// toolbar
		makeActions();
		// initialisation de la toolbar
		this.bars = getViewSite().getActionBars();
		updateViewInfoLabel();

		if (GestionLineCommandParameters.getIndiceMsg() != -1 && oneTimeOffset)
			selectionChanged(null, null);
	}

	/**
	 * Creates the view
	 * 
	 * @param contentProvider
	 *            the content provider
	 * @param labelProvider
	 *            the lable provider
	 * @param table
	 *            the table
	 */
	protected void create(AVueTableContentProvider contentProvider, AVueTableLabelProvider labelProvider, FixedColumnTable table, boolean usesShortNames) {
		this.contentProvider = contentProvider;
		this.tblFix = table;
		this.top.setLayout(new FillLayout());

		// maybe it was already added
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
			BarreProgressionDialog barre = new BarreProgressionDialog("", contentProvider);
			ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getDefault().getActiveShell());

			try {
				dialog.run(true, true, barre);
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			barre = null;
			dialog = null;

			// load the data in the table
			// contentProvider.loadContent();

			this.tblFix.setContentProvider(contentProvider);

			// sets the header and lines visible
			this.tblFix.setHeaderVisible(true);
			this.tblFix.setLinesVisible(true);

			// creates the listeners to display the tooltips
			createCellTooltip();
			// The system font will not display the lower 32
			// characters, so create one that will
			this.tblFix.setFont(this.data.getNormalFont());

			// Create the columns
			List<TableColumn> columns = createColumns(this.tblFix, this.columnNames, colsIndicesInfo.getLastFixedColumn());

			// set a dummy input. The input is set by the content provider,
			// but the mechanism of setting the input for the table has to be
			// started
			this.tblFix.setInput("");
			// this.tblFix.setInput("aa");

			// update the columns width conform to the configuration
			updateAutoColumnsWidth(columns, colsIndicesInfo.getLastFixedColumn());
			addControlListenerToColumns(columns);
			this.tblFix.onColumnsAdded(this.columnNames);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		// sets the selection listener on the table
		// this.tblFix.addSelectionChangedListener(new
		// ISelectionChangedListener() {
		// public void selectionChanged(SelectionChangedEvent event) {
		// // update selection
		// // updateSelectionMessage(true);
		//
		// }
		// });

		this.tblFix.addKeyListener(new KeyListener() {
			private boolean turbo = false;

			public void keyPressed(KeyEvent e) {
				// System.out.println("code= "
				// +e.keyCode+"  char: "+e.character+" stateMask= "+e.stateMask);
				// synchro si pression sur bouton Entr�e
				if (e.keyCode == SWT.CR) {
					ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
					upSelection();
				}
				if (e.keyCode == SWT.SHIFT) {
					setTurbo(true);
					setMultiSelect(true);
					setIdSelect(AVueTable.this.tblFix.fixedTable.getSelectionIndices());
				}
				if (e.keyCode == SWT.ARROW_LEFT) {
					if (isTurbo()) {
						AVueTable.this.tblFix.scrollingTable.showColumn(AVueTable.this.tblFix.scrollingTable.getColumn(0));
					}
				}
				if (e.keyCode == SWT.ARROW_RIGHT) {
					if (isTurbo()) {
						AVueTable.this.tblFix.scrollingTable.showColumn(AVueTable.this.tblFix.scrollingTable.getColumn(AVueTable.this.tblFix.scrollingTable.getColumns().length - 1));
					}
				}
				if (e.keyCode == SWT.CTRL) {
					setSelectCntrl(true);
					setIdSelect(AVueTable.this.tblFix.fixedTable.getSelectionIndices());
				}
				if ((e.keyCode == Integer.valueOf('a') + SWT.CTRL) || (e.keyCode == Integer.valueOf('A') + SWT.CTRL)) {
					if (isSelectCntrl()) {
						int count = AVueTable.this.tblFix.fixedTable.getItemCount();
						int tab[] = new int[count];
						for (int i = 0; i < count; i++) {
							tab[i] = i;
						}
						setIdSelect(tab);
						setCntrlA(true);
						upSelection();
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
				// System.out.println("Realeases : code= "
				// +e.keyCode+"  char: "+e.character);
				if (e.keyCode == SWT.SHIFT) {
					setTurbo(false);
					setMultiSelect(false);
				}
				if (e.keyCode == SWT.CTRL) {
					setSelectCntrl(false);
				}
				if (e.keyCode == Integer.valueOf('a') || e.keyCode == Integer.valueOf('A')) {
					setCntrlA(false);
				}
			}

			public boolean isTurbo() {
				return turbo;
			}

			public void setTurbo(boolean turbo) {
				this.turbo = turbo;
			}
		});

		this.tblFix.fixedTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
				updateSelectionMessage(true);
				
				/* If the preferences require synchronization with the Up and Down keys */
				if (syncFromKey == true) {
					((MessageSelection) AVueTable.this.currentSelection).setUserSentSelection(true);
					fireSelectionChanged(AVueTable.this.currentSelection);
					((MessageSelection) AVueTable.this.currentSelection).setUserSentSelection(false);
					((MessageSelection) AVueTable.this.currentSelection).setShouldNotTriggerAudio(false);
					syncFromKey = false;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		this.tblFix.scrollingTable.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
				updateSelectionMessage(false);
				
				/* If the preferences require synchronization with the Up and Down keys */
				if (syncFromKey == true) {
					((MessageSelection) AVueTable.this.currentSelection).setUserSentSelection(true);
					fireSelectionChanged(AVueTable.this.currentSelection);
					((MessageSelection) AVueTable.this.currentSelection).setUserSentSelection(false);
					((MessageSelection) AVueTable.this.currentSelection).setShouldNotTriggerAudio(false);
					syncFromKey = false;
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// sets the mouse listener on the table
		this.tblFix.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1) { // left button double click
					if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
							.equals(PreferenceConstants.MOUSE_SYNC_DOUBLE_CLICK)) {
						ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
						upSelection();
					}
				}
			}

			public void mouseDown(MouseEvent e) {

			}

			public void mouseUp(MouseEvent e) {
				for (int i = 0; i < listeActionsAuditrice.size(); i++) {
					listeActionsAuditrice.get(i).setEnabled(true);
				}
				
				if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
						.equals(PreferenceConstants.MOUSE_SYNC_SINGLE_CLICK)) {
					ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
					upSelection();
				}
			}
		});

		getData().addMarkersListener(this);
		// Update the current markers - we have nothing to deselect as the table
		// was just created

		// ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueursIds();

		marquersListeChangement(getData().getMarkerMsgIds(), new int[0]);

		updateViewInfoLabel();

		// Fix for scrolling table. We need to have always the horizontal scoll
		// in
		// the scrolling table for avoid decalage when the table do not have
		// scroll
		dummyCol = new TableColumn(table.getScrollingTable(), SWT.SINGLE | SWT.RIGHT);
		dummyCol.setResizable(false);
		table.getScrollingTable().addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				checkForDummyColumn();
			}
		});

		// Now create the context menu
		createContextMenu();

		if (ActivatorData.getInstance().getSelectedMsg() != null) {
			MessageSelection messageSelection = (MessageSelection) this.currentSelection;
			messageSelection.setShouldNotTriggerAudio(true);
			this.tblFix.setSelection(getRowIndexForMessageId(ActivatorData.getInstance().getSelectedMsg().getMessageId(), null), null);

		} else {
			try {
				this.tblFix.setSelection(0, null);
				Message msg = getMessageFromCurrentSelection();
				((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
			} catch (Exception ex) {

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
	private void checkForDummyColumn() {
		Rectangle area = tblFix.getScrollingTable().getClientArea();
		Point preferredSize = tblFix.getScrollingTable().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		int width = area.width - (2 * tblFix.getScrollingTable().getBorderWidth());

		if (preferredSize.y > (area.height + tblFix.getScrollingTable().getHeaderHeight())) {
			// Subtract the scrollbar width from the total column width
			// if a vertical scrollbar will be required
			Point vBarSize = tblFix.getScrollingTable().getVerticalBar().getSize();
			width -= vBarSize.x;
		}

		Point oldSize = tblFix.getScrollingTable().getSize();
		width -= getScrollingColumnsWidths();
		width += 25;

		if (oldSize.x > area.width) {
			// table is getting smaller so make the columns
			// smaller first and then resize the table to
			// match the client area width
			// indexesColumn.setWidth(indexesColumn.getWidth());
			dummyCol.setWidth(width);
		} else {
			// table is getting bigger so make the table
			// bigger first and then make the columns wider
			// to match the client area width
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
			if (col != dummyCol)
				width += col.getWidth();
		}
		return width;
	}

	/**
	 * Creates the columns for the table
	 * 
	 * @param table
	 *            the table
	 * @return TableColumn[]
	 */
	protected List<TableColumn> createColumns(final FixedColumnTable table, List<String> columnsNames, int indexFixed) {
		ArrayList<TableColumn> columns = new ArrayList<TableColumn>();
		TableColumn col = null;
		int colWidth;
		int size = columnsNames.size();
		String colText;
		String colNom;
		ConfigurationColonne colCfg;

		// Add the an empty column as first column in fixed table.
		// This is a workaround for FixedColumnTable that do not refreshes
		// the first column text when a selection is made in VIRTUAL mode
		// The SWT table handles the first column different than the others
		addZeroLengthColumn(table.getFixedTable());
		for (int i = 0, n = size; i < n; i++) {
			// Create the TableColumn with right alignment
			if (i <= indexFixed) {
				// for the fixed columns of the table
				col = new TableColumn(table.getFixedTable(), SWT.SINGLE | SWT.RIGHT);
				// col.setResizable(false);
			} else {
				if (i == indexFixed + 1) {
					// Add the an empty column as first column in scrollable
					// table.
					// This is a workaround for FixedColumnTable that do
					// not refreshes
					// the first column text when a selection is made in VIRTUAL
					// mode
					// The SWT table handles the first column different than the
					// others
					addZeroLengthColumn(table.getScrollingTable());
				}
				// for the scrollable columns of the table
				col = new TableColumn(table.getScrollingTable(), SWT.SINGLE | SWT.RIGHT);
			}
			colNom = columnsNames.get(i);
			colWidth = this.gestionaireVue.getColonneLargeur(colNom);
			if (colWidth > 0) // for the auto columns is the method
				// updateAutoColumnsWidth
				col.setWidth(colWidth);

			// add new columns to columns list
			columns.add(col);

			colCfg = this.gestionaireVue.getColonne(colNom);
			if (colCfg != null) {
				colText = GestionnaireVueListeBase.getDisplayLabelForColumn(colCfg, usesShortNames);
			} else {
				colText = colNom;
			}

			if (colText == null) {
				colText = colNom;
			}
			// This text will appear in the column header
			col.setText(colText);

			// set the same text as tooltip
			// col.setToolTipText(colText);
			col.setToolTipText(GestionnaireVueListeBase.getDisplayLabelForColumn(colCfg, false));
			col.setData(colNom);
		}
		columns.trimToSize();
		return columns;
	}

	/**
	 * Adds an empty column that has width 0 and is not resizable. This is a
	 * workaround for FixedColumnTable that do not refreshes the first column
	 * text when a selection is made in VIRTUAL mode The SWT table handles the
	 * first column different than the others This should be called only for the
	 * first column of a table
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
	private void addControlListenerToColumns(List<TableColumn> columns) {
		for (TableColumn col : columns) {
			col.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					if (event.widget instanceof TableColumn) {
						TableColumn col = (TableColumn) event.widget;
						int width = col.getWidth();
						String colName = (String) col.getData();
						ConfigurationColonne colCfg = gestionaireVue.getColonne(colName);
						if (colCfg != null) {
							int ancienneLargeurColonne = colCfg.getLargeur();
							colCfg.setLargeur(width);
							if (ancienneLargeurColonne != width)
								gestionaireVue.setChanged(true);
						}
						checkForDummyColumn();
					}
				}
			});
		}
	}

	/**
	 * Updates the columns width for the columns that have auto flag
	 * 
	 * @param columns
	 *            the colums to update
	 */
	private void updateAutoColumnsWidth(List<TableColumn> columns, int indexFixed) {
		int colWidth;

		int i = 0;
		String colName;
		for (TableColumn tblCol : columns) {
			colName = this.columnNames.get(i);
			// check the width
			colWidth = this.gestionaireVue.getColonneLargeur(colName);

			// sets the width
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
						if (!this.tblFix.getFixedTable().isDisposed())
							this.tblFix.getFixedTable().dispose();
						if (!this.tblFix.getScrollingTable().isDisposed()) {
							this.tblFix.getScrollingTable().dispose();
						}
						this.tblFix.dispose();
					}

				} catch (Exception e) {
					System.err.println(e.getMessage());
				} finally {
					this.tblFix = null;
				}
			}
		}
	}

	/**
	 * Loads the table and go to the current scroll position if requested
	 * 
	 * @param scrollToSelection
	 */
	protected abstract void loadTable(boolean scrollToSelection);

	/**
	 * Reloads a table whose data did not changed
	 * 
	 */
	public void reloadTable() {
		loadTable(true);
		Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
		if (!(elements.length > 0)) {
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("AVueTable.32")); //$NON-NLS-1$
			msgBox.open();
		}
	}

	/**
	 * Notification that a repere was changed
	 * 
	 * @param reperes
	 *            the list of reperes that were changed
	 */
	public void onRepereAdded(TypeRepere... reperes) {
		if (reperes != null) {

			int rowIdx = 0;
			int topIndex = 0;
			boolean findIndice = true;
			//
			// for (TypeRepere reper : reperes) {
			// //add this reper in the list of colums to be displayed
			// this.gestionaireVue.ajouterRepere(reper); }
			//
			// a new colums was added then reload the table.

			try {
				int selId = ActivatorData.getInstance().getSelectedMsg().getMessageId();
				MessageSelection messageSelection = (MessageSelection) this.currentSelection;
				messageSelection.setShouldNotTriggerAudio(true);
				rowIdx = getRowIndexForMessageId(selId, null);
				topIndex = this.tblFix.fixedTable.getTopIndex();
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
	 * Notification that a repere was removed
	 * 
	 * @param reperes
	 *            the list of reperes that were changed
	 */
	public void onRepereRemoved(TypeRepere... reperes) {
		if (reperes != null) {
			/*
			 * for (TypeRepere reper : reperes) { //add this reper in the list
			 * of colums to be displayed
			 * this.gestionaireVue.removeRepere(reper); }
			 */
			// a new colums was added then reload the table.
			loadTable(true);
		}
	}

	/**
	 * Reloads the table due to a change of the data
	 */
	public void onDataChange() {
		this.gestionaireVue.clear();

		// if there are distance or time corrections then the colums should be
		// added to the table
		if (TableSegments.getInstance().isAppliedDistanceCorrections()) {
			this.gestionaireVue.ajouterRepere(TypeRepere.distanceCorrigee);
			this.gestionaireVue.ajouterRepere(TypeRepere.vitesseCorrigee);
		}
		if (TableSegments.getInstance().isAppliedTempCorrections()) {
			this.gestionaireVue.ajouterRepere(TypeRepere.tempsCorrigee);
		}
		// loads the table and to not scroll
		// if (((InfosFichierSamNg)
		// FabriqueParcours.getInstance().getParcours().getInfo()).getNomFichierParcoursBinaire()!=null){
		loadTable(false);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	public final void propertyChange(final PropertyChangeEvent evt) {
		// we just react on add events
		if ("MSG_SELECT".equals(evt.getPropertyName())) { //$NON-NLS-1$
			this.tblFix.getFixedTable().setSelection(((Integer) evt.getNewValue()).intValue());

		}
	}

	/** */
	public void setFocus() {
		if (this.tblFix != null)
			this.tblFix.setFocus();
		// do nothing.
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
		this.tblFix.getFixedTable().setToolTipText(""); //$NON-NLS-1$
		this.tblFix.getScrollingTable().setToolTipText(""); //$NON-NLS-1$

		// Implement a "fake" tooltip
		final Listener labelListener = new Listener() {
			public void handleEvent(Event event) {
				Label label = (Label) event.widget;
				Shell shell = label.getShell();
				switch (event.type) {
				case SWT.MouseDown:
					Event e = new Event();
					e.item = (TableItem) label.getData("_TABLEITEM"); //$NON-NLS-1$
					Table table = (Table) e.widget;

					// Assuming table is single select, set the selection as if
					// the mouse down event went through to the table
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

						if (this.tip == null)
							break;
						this.tip.dispose();
						this.tip = null;
						this.label = null;
						break;
					}
					case SWT.MouseHover: {
						Table table = (Table) event.widget;
						boolean isFixed = AVueTable.this.tblFix.getFixedTable() == table;
						TableItem item = null;

						if (isFixed) {
							item = AVueTable.this.tblFix.getFixedTable().getItem(new Point(event.x, event.y));
						} else {
							item = AVueTable.this.tblFix.getScrollingTable().getItem(new Point(event.x, event.y));
						}
						if (item == null)
							return;

						int colNo = getColumnNumber(new Point(event.x, event.y), table, item);

						if (item != null && item.getText(colNo).length() > 0) {
							if (this.tip != null && !this.tip.isDisposed())
								this.tip.dispose();
							// create the shell to display the tooltip
							this.tip = new Shell(table.getDisplay().getActiveShell(), SWT.ON_TOP | SWT.NO_FOCUS | SWT.TOOL);
							this.tip.setBackground(table.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
							FillLayout layout = new FillLayout();
							layout.marginWidth = 2;
							this.tip.setLayout(layout);

							// create the tooltip
							// this.label = new Label(this.tip, SWT.NONE);
							// this.label.setForeground(table.getDisplay()
							// .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
							// this.label.setBackground(table.getDisplay()
							// .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
							// this.label.setData("_TABLEITEM", item); //$NON-NLS-1$
							// label.setText (item.getText(isFixed? colNo :
							// colNo +
							// POS_EVENT + 1));
							// this.label.setText(item.getText(colNo));
							// this.label.setVisible(false);

							// if(item.getText(colNo) && colNo==1)

							Message msg = (Message) ((Row) item.getData()).getData();
							String strMarqueur = null;
							if (ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msg.getMessageId()) != null)
								strMarqueur = ((Marqueur) ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msg.getMessageId())).getCommentaire();

							// if (strMarqueur != null)
							// table.setToolTipText(strMarqueur);
							// else
							try {
								if (isFixed && getColumnNames(colNo - 1).equalsIgnoreCase(GestionnaireVueListeBase.FLAG_COL_NAME_STR))
									table.setToolTipText(strMarqueur);
								else
									table.setToolTipText(item.getText(colNo));
							} catch (Exception e) {
								// TODO Auto-generated catch block
								table.setToolTipText("");
							}

							// add listeners for mouse exit and mouse down
							try {
								this.label.addListener(SWT.MouseExit, labelListener);
								this.label.addListener(SWT.MouseDown, labelListener);

								// get the coordinates where to display the
								// tooltip
								Point size = this.tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);

								// returns the coordinates relativ to the table
								Rectangle rect = item.getBounds(0);
								Rectangle rect1 = table.getClientArea();

								// get the coordinates relative to the display
								Point pt = table.toDisplay(rect.x, rect.y);
								Point pt1 = table.toDisplay(rect1.x, rect1.y);
								this.tip.setBounds(pt1.x + event.x + 12, pt.y + 20, size.x, size.y);
								this.tip.setVisible(true);
							} catch (NullPointerException e) {
								// TODO Auto-generated catch block
								// e.printStackTrace();
							}

						} else {
							table.setToolTipText("");
						}

					}
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		// adds listeners
		this.tblFix.addListener(SWT.Dispose, tableListener);
		this.tblFix.addListener(SWT.KeyDown, tableListener);
		this.tblFix.addListener(SWT.MouseMove, tableListener);
		this.tblFix.addListener(SWT.MouseHover, tableListener);
	}

	/**
	 * Gets the number of the column where we can find the given point in the
	 * specified table
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
			colLabels.add(GestionnaireVueListeBase.getDisplayLabelForColumn(this.gestionaireVue.getColonne(colName), usesShortNames));
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
	public FixedColumnTable getTable() {
		return this.tblFix;
	}

	/**
	 * @param tblFix
	 *            the tblFix to set
	 */
	public void setTable(FixedColumnTable table) {
		this.tblFix = table;
	}

	/**
	 * Get a row index by the id of the message id associated to that row
	 * 
	 * @param msgId
	 *            the searched message ID
	 * @return the found row index or -1 if no such message ID found in rows
	 */
	protected int getRowIndexForMessageId(int msgId, DescripteurVariable descripteurVariable) {
		Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
		int rowId;
		Object rowData;
		int rowIdx = 0;
		int retIdx = -1;
		try {
			for (Object row : elements) {
				if (row instanceof Row) {
					rowData = ((Row) row).getData();
					if (rowData != null && rowData instanceof Message) {
						rowId = ((Message) rowData).getMessageId();
						if (rowId == msgId) {
							retIdx = rowIdx;
							if (this.ajoutAnnotationAction != null) {
								this.ajoutAnnotationAction.setEnabled(true);
								this.tblFix.setSelection(retIdx, descripteurVariable);// tag100
								this.upSelection();
							}
							break;
						}
					}
					rowIdx++;
				}
			}
		} catch (Exception ex) {
			retIdx = -1;
		}

		return retIdx;
	}

	/**
	 * Handler for notifications from other views that the selection changed in
	 * the source view
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {

		if (tblFix == null)
			return; 
		
		// positionnement offset
		if (GestionLineCommandParameters.getIndiceMsg() != -1 && oneTimeOffset) {
			try {

				oneTimeOffset = false;
				int idMessageSelectionne = GestionLineCommandParameters.getIndiceMsg();
				int indexLigneASelectionner = getRowIndexForMessageId(idMessageSelectionne, null);

				// Apr�s l'ouverture d'un fichier par ligne de commande,
				// l'offset du nouveau fichier
				// ouvert doit �tre repositionn� sur la premi�re ligne de la
				// vue. Pour cela on
				// utilise la valeur 0.
				if (idMessageSelectionne == 0) {
					indexLigneASelectionner = 0;
				}

				if (indexLigneASelectionner == -1) {
					this.tblFix.setNotEquivalent(true);
					this.tblFix.setNotEquivalentForSearch(true);
				}

				if (indexLigneASelectionner != -1) {
					this.tblFix.setSelection(indexLigneASelectionner, null);
					// issue 740
					this.tblFix.setTopIndex(PositionMilieuViewer.getPosition(indexLigneASelectionner));
					this.lastSelOffsetFromTop = indexLigneASelectionner - this.tblFix.fixedTable.getTopIndex();
					handleLineSelection(indexLigneASelectionner);
				}

				ActivatorData.getInstance().setSelectionVueTabulaire(indexLigneASelectionner);
				this.tblFix.refresh();
			} catch (Exception e) {
				e.printStackTrace();
			}

			// traitement normal
		} else if (this.tblFix != null) {
			// avoid handling events sent by this view

			try {
				// issue 738
				if (!this.tblFix.isNotEquivalentForSearch()) {
					this.tblFix.setNotEquivalent(false);
				} else {
					this.tblFix.setNotEquivalent(true);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if ((selection instanceof MessageSelection) && (part != this) && !selection.isEmpty()) {
				if (!((MessageSelection) selection).getUserSendSelection())
					return;
				int idMessageSelectionne = ((MessageSelection) selection).getMessageId();
				int indexLigneASelectionner = getRowIndexForMessageId(idMessageSelectionne, null);

				while (idMessageSelectionne > 0 && indexLigneASelectionner == -1) {
					idMessageSelectionne = idMessageSelectionne - 1;
					indexLigneASelectionner = getRowIndexForMessageId(idMessageSelectionne, null);
					if (indexLigneASelectionner != -1) {

						this.tblFix.setNotEquivalent(true);
					}
				}
				if (indexLigneASelectionner == -1) {
					indexLigneASelectionner = 0;
					this.tblFix.setNotEquivalent(true);
				}

				if (indexLigneASelectionner != -1) {
					this.tblFix.setSelection(indexLigneASelectionner, null);
					// issue 740
					this.tblFix.setTopIndex(PositionMilieuViewer.getPosition(indexLigneASelectionner));
					this.lastSelOffsetFromTop = indexLigneASelectionner - this.tblFix.fixedTable.getTopIndex();
					handleLineSelection(indexLigneASelectionner);
				}

				ActivatorData.getInstance().setSelectionVueTabulaire(indexLigneASelectionner);
				this.tblFix.refresh();
			}
		}
		createContextMenu();
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

	abstract protected String getDefaultFilterName();

	/**
	 * Updates the view title of this view
	 * 
	 */
	protected void updateViewInfoLabel() {
		String appliedFilter = this.gestionaireVue.getFiltreApplique();
		String tabName = this.initialPartName;
		boolean hasHiddenColumns = this.gestionaireVue.hasHiddenColumns();
		if (appliedFilter != "defaut" && appliedFilter != null && !"".equals(appliedFilter.trim())) { //$NON-NLS-1$
			tabName += " [" + appliedFilter + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			String newName = getDefaultFilterName();
			if (newName != null) {
				tabName += newName; //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		if (hasHiddenColumns) {
			tabName += Messages.getString("AVueTable.19"); //$NON-NLS-1$
		}
		Message msg = getMessageFromCurrentSelection();
		if (msg != null) {
			String correctionsStr = DataViewsUtil.getCorrectionsTitleString(msg);
			if (correctionsStr != null)
				tabName += correctionsStr;
			// issue 735 :
			((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
		}
		this.setPartName(tabName);
	}

	/**
	 * Returns the message from the current selection
	 * 
	 * @return the message or null if not supported
	 */
	protected Message getMessageFromCurrentSelection() {
		Message message = null;
		if (this.tblFix != null) {
			// issue 735
			int selIdx = this.tblFix.getSelectionIndex();
			// int selIdx = this.tblFix.scrollingTable.getSelectionIndex();
			// System.out.println(this.tblFix.getSelectionIndex());
			// System.out.println(this.tblFix.fixedTable.getSelectionIndex());
			// System.out.println(this.tblFix.scrollingTable.getSelectionIndex());

			Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
			if (selIdx >= 0 && selIdx < elements.length) {
				Object tblItem = elements[selIdx];
				if (tblItem instanceof Row) {
					Object crtData = ((Row) tblItem).getData();
					if (crtData instanceof Message)
						message = (Message) crtData;
				}
			}
		}
		// System.out.println(message==null ? "null" : message.getMessageId());
		// System.out.println();
		return message;
	}

	/**
	 * Updates the information current selection MessageSelection object that is
	 * notified to other views when a double click is performed
	 * 
	 */
	protected void updateSelectionMessage(boolean fixtable) {
		int id = 0;
		int idSelect = 0;
		// when a double click
		int idFixTable = this.tblFix.getSelectionIndex();
		int idScrollTable = this.tblFix.scrollingTable.getSelectionIndex();
		int selectionIndices[] = null;
		
		currentSelection = new MessageSelection();

		if (fixtable) {
			id = idFixTable;
			selectionIndices = AVueTable.this.tblFix.fixedTable.getSelectionIndices();
		} else {
			id = idScrollTable;
			selectionIndices = AVueTable.this.tblFix.scrollingTable.getSelectionIndices();
		}
		idSelect = id;
		Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
		if (idSelect < 0 || idSelect >= elements.length)
			return;
		Message msg = getMessageFromCurrentSelection();
		if (msg != null) {
			((MessageSelection) this.currentSelection).setMessageId(msg.getMessageId());
			// TAG MIGRATION :
			// if (isMultiSelect() || isSelectCntrl()) {
			// idSelect=getIdSelect();
			// int
			// idsSelected[]=getSelectedIds(id,idSelect,this.idsSelected,multiSelect,CntrlA);
			this.tblFix.scrollingTable.setSelection(selectionIndices);// tag100
			this.tblFix.fixedTable.setSelection(selectionIndices);// 100
			// setIdSelect(AVueTable.this.tblFix.fixedTable.getSelectionIndices());
			// multiSelect=false;
			// }else{
			// this.tblFix.scrollingTable.setSelection(idSelect);
			// this.tblFix.fixedTable.setSelection(idSelect);
			// int [] idS=new int[1];idS[0]=idSelect;
			// setIdSelect(idS);
			// }
			this.tblFix.refreshTable(id, null);
		}
		this.lastSelOffsetFromTop = id - this.tblFix.fixedTable.getTopIndex();
		handleLineSelection(id); // additional operations after the
		// selection (update title, for example)
	}

	// private int[] getSelectedIds(int id1, int id2, int[] idsSelected, boolean
	// multi, boolean cntrlA) {
	// if (cntrlA) {
	// return idsSelected;
	// }
	// int nbIdsSelected=idsSelected.length; //nombre de messages d�j�
	// s�lectionn�s
	// if (multi) {
	// //S�lection avec Shift
	// int nbId1ToId2=Math.abs(id2-id1)+1; //nombre de messages de id1 � id2
	// int nbIds=nbId1ToId2+nbIdsSelected; // nombre de messages � s�lectionner
	// int[] ids=new int[nbIds]; //tableau � renvoyer
	// if (id1>id2) { //si id1>id2 on les inverse
	// int tampon=id1;
	// id1=id2;
	// id2=tampon;
	// }
	// for (int i=id1; i<id2+1; i++) { //on place toutes les lignes de id1 � id2
	// ids[i-id1]=i;
	// }
	// if (nbIdsSelected>0) { //on ajoute les lignes d�j� s�lection�es
	// for (int i=0; i<nbIdsSelected; i++) {
	// ids[i+nbId1ToId2]=idsSelected[i];
	// }
	// }
	// Arrays.sort(ids);// on trie les lignes dans l'ordre
	// return ids;
	// }else{
	// //S�lection avec CTRL
	// int ids[]=new int[nbIdsSelected+2];
	// ids[0]=id1;
	// ids[1]=id2;
	// for (int i = 0; i < nbIdsSelected; i++) {
	// ids[2+i]=idsSelected[i];
	// }
	// Arrays.sort(ids);// on trie les lignes dans l'ordre
	// return ids;
	// }
	// }

	/**
	 * Scrolls to a previously saved selection
	 * 
	 */
	protected void scrollToPreviousSelection() {
		if (!this.currentSelection.isEmpty()) {
			MessageSelection messageSelection = (MessageSelection) this.currentSelection;
			messageSelection.setShouldNotTriggerAudio(true);
			int prevSelMsgIdx = messageSelection.getMessageId();
			int rowIdx = getRowIndexForMessageId(prevSelMsgIdx, null);
			this.tblFix.setNotEquivalent(false);
			while (prevSelMsgIdx > 0 && rowIdx == -1) {
				prevSelMsgIdx = prevSelMsgIdx - 1;
				rowIdx = getRowIndexForMessageId(prevSelMsgIdx, null);
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

				this.lastSelOffsetFromTop = rowIdx - this.tblFix.fixedTable.getTopIndex();
				handleLineSelection(rowIdx);

			}

			// if (prevSelMsgIdx == -1) {
			// this.tblFix.setSelection(0);
			// this.lastSelOffsetFromTop = 0;
			// handleLineSelection(0);
			// return;
			// }
			// this.tblFix.setSelection(prevSelMsgIdx);
			// this.tblFix
			// .setTopIndex(prevSelMsgIdx < this.lastSelOffsetFromTop ? 0
			// : prevSelMsgIdx - this.lastSelOffsetFromTop);
			// handleLineSelection(prevSelMsgIdx);
		}
	}

	/**
	 * Method for notification when has to be selected a marquer
	 * 
	 * @param next
	 *            if the next or previous marquer should be selected
	 */
	public String onSelectedMarquerNomChange(boolean next) {
		AMarqueur selMarquer = null;

		// check for the current selected message
		Row row = (Row) this.tblFix.getSelection()[0].getData();
		int crtSelMsgId = ((Message) row.getData()).getMessageId();
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}

		boolean marqueurSelectionne = false;
		// gets the marquers manager
		GestionnaireMarqueurs marquersGest = ActivatorData.getInstance().getGestionnaireMarqueurs();
		boolean marqueurTrouve = false;

		if (marquersGest != null) {

			int[] ids = marquersGest.getMarqueursIds();
			int nbMarqueurs = ids.length;
			// if current selected message is after the last marquer
			if (crtSelMsgId > ids[nbMarqueurs - 1]) {
				if (!next) {
					int j = nbMarqueurs - 1;
					// on recherche le bon marqueur en remontant dans la liste
					while (!marqueurTrouve && j >= 0) {
						selMarquer = marquersGest.getMarqueurParId(ids[j]);
						if (selMarquer != null && getRowIndexForMessageId(selMarquer.getIdMessage(), null) != -1)
							marqueurTrouve = true;
						j--;
					}

				}

			} else {
				if (!next) {

					if (crtSelMsgId > ids[0]) {
						int j = nbMarqueurs - 1;
						// on recherche le bon marqueur en remontant dans la
						// liste
						while (!marqueurTrouve && j >= 0) {
							selMarquer = marquersGest.getMarqueurParId(ids[j]);
							if (ids[j] < crtSelMsgId && selMarquer != null && getRowIndexForMessageId(selMarquer.getIdMessage(), null) != -1)
								marqueurTrouve = true;
							j--;
						}

					}
				} else {
					if (crtSelMsgId < ids[nbMarqueurs - 1]) {
						int j = 0;
						// on recherche le bon marqueur en descendant dan la
						// liste
						while (!marqueurTrouve && j < nbMarqueurs) {
							selMarquer = marquersGest.getMarqueurParId(ids[j]);
							if (ids[j] > crtSelMsgId && selMarquer != null && getRowIndexForMessageId(selMarquer.getIdMessage(), null) != -1)
								marqueurTrouve = true;
							j++;
						}
					}
				}
			}
			if (marqueurTrouve) {
				// on se place sur la bonne ligne
				int rowIdx = getRowIndexForMessageId(selMarquer.getIdMessage(), null);
				this.tblFix.setSelection(rowIdx, null);
				this.tblFix.setTopIndex(rowIdx);
				handleLineSelection(rowIdx);
			} else {
				marqueurSelectionne = true;
			}
		}
		if (marqueurSelectionne) {
			// if no marquer found display a message
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("AVueTable.21")); //$NON-NLS-1$
			msgBox.open();
		}
		return selMarquer != null ? selMarquer.getNom() : ""; //$NON-NLS-1$
	}

	/**
	 * Method for notification when has to be selected a marquer
	 * 
	 * @param comment
	 *            the comment that should be searched for
	 * @param next
	 *            if the next or previous marquer should be selected
	 */
	public void onSelectedMarquerCommentChange(String comment, boolean next) {
		AMarqueur selMarquer = null;

		// serach for the current selcted message
		Row row = (Row) this.tblFix.getSelection()[0].getData();
		int crtSelMsgId = ((Message) row.getData()).getMessageId();
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
				// have to select a marquer
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
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("AVueTable.24")); //$NON-NLS-1$
			msgBox.open();
		}

	}

	public boolean isMultiSelect() {
		return multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	/**
	 * Gets the selection index
	 * 
	 * @return
	 */
	public int getSelectionId() {
		int index = -1;
		if (this.data != null && this.data.getDataTable() != null) {
			index = this.tblFix.getSelectionIndex();
		}
		return index;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener#
	 * onSearchVariable
	 * (com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable,
	 * java.lang.String, com.faiveley.samng.principal.ihm.vues.search.Operation,
	 * boolean)
	 */
	/**
	 * Recherche d'une variable
	 */
	@Override
	public void onSearchVariable(DescripteurVariable descrVar, String value, Operation op, boolean next) {
		this.onSearchVariable(descrVar, null, value, op, next);
	}

	protected void afficherPasDeMsgCorrespondantCriteres(String[] msgErr) {
		if (msgErr[0] == null) {
			msgErr[0] = com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.25"); //$NON-NLS-1$
		}
		if (Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart() == AVueTable.this.getSite().getPart()) {
			MessageBox msgBox = new MessageBox(AVueTable.this.getSite().getShell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(msgErr[0]);
			msgBox.open();
		}
	}

	@Override
	public void selectMessage(int retIdx, DescripteurVariable descripteurVariable) {
		if (this.ajoutAnnotationAction != null) {
			this.ajoutAnnotationAction.setEnabled(true);
			this.tblFix.setSelection(retIdx, descripteurVariable);
			this.upSelection();
		}
	}

	@Override
	public void setNotEquivalent(boolean b) {
		this.tblFix.setNotEquivalent(b);
	}

	@Override
	public void setNotEquivalentForSearch(boolean b) {
		this.tblFix.setNotEquivalentForSearch(b);
	}

	/**
	 * Get the label for a variable composee for a given message. If all
	 * variables that are in that variable are present in the file then the
	 * values are taken and see what label is matching them
	 * 
	 * @param var
	 * @param message
	 * @return
	 */
	protected LabelValeur getLabelForVariableComposee(VariableComposite var, Message message) {

		StringBuilder str = new StringBuilder();
		AVariableComposant subvar;
		AVariableComposant msgSubVar;
		int count = var.getVariableCount();
		for (int i = 0; i < count; i++) {
			subvar = var.getEnfant(i);
			msgSubVar = message.getVariable(subvar.getDescriptor());
			if (msgSubVar == null) {
				List<Message> messages = this.data.getDataTable().getEnregistrement().getMessages();
				for (int j = messages.size() - 1; j > 0; j--) {
					if (messages.get(j).getMessageId() >= message.getMessageId()) {
						continue;
					}

					if (messages.get(j).getVariable(subvar.getDescriptor()) != null) {
						msgSubVar = messages.get(j).getVariable(subvar.getDescriptor());
						break;
					}
				}
			}
			if (msgSubVar == null)
				return null;
			str.append(msgSubVar.getCastedValeur());
			if (i != count - 1)
				str.append(","); //$NON-NLS-1$
		}
		String subVarsValue = str.toString();

		List<LabelValeur> labelValues = Util.getInstance().getLabelsForVariable(var.getDescriptor());
		String cmpVal;
		if (labelValues != null) {
			for (LabelValeur valLabel : labelValues) {
				cmpVal = (String) valLabel.getValeurs();
				if (cmpVal.equals(subVarsValue))
					return valLabel;
			}
		}
		return null;
	}

	/**
	 * Handles the
	 * 
	 * @param rowIdx
	 */
	protected void handleLineSelection(int rowIdx) {

		updateViewInfoLabel();
	}

	/**
	 * Returns the top composite for this view
	 */
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
				System.out.println("this.popupMenu = new Menu(top);");
				// e.printStackTrace();
				return;
			}

			MenuItem item = new MenuItem(popupMenu, SWT.NONE);
			item.setText(Messages.getString("AVueTable.28")); //$NON-NLS-1$
			item.setData(TypeMenuOptions.DISPLAY_COLUMN_MNG);
			item.addListener(SWT.Selection, menuSelListener);

			item = new MenuItem(popupMenu, SWT.CHECK);
			item.setText(Messages.getString("AVueTable.35")); //$NON-NLS-1$
			item.setData(TypeMenuOptions.USE_SHORT_NAMES);
			item.addListener(SWT.Selection, menuSelListener);
			item.setSelection(this.usesShortNames);

			item = new MenuItem(popupMenu, SWT.SEPARATOR);

			item = new MenuItem(popupMenu, SWT.NONE);
			item.setText(Messages.getString("AVueTable.29")); //$NON-NLS-1$
			item.setData(TypeMenuOptions.DISPLAY_FILTER_VIEW);
			item.addListener(SWT.Selection, menuSelListener);

			AFiltreComposant filterApplied = getAppliedFilter();
			// Synchro between filter list and Ordonner list
			OrdonnerFiltre.getInstance().setFiltreSelectTabulaire(filterApplied);

			listeFiltreOrd = OrdonnerFiltre.getInstance().getListeFiltreTabulaire();
			if (listeFiltreOrd == null) {
				listeFiltreOrd = new ArrayList<AFiltreComposant>(0);
			}

			item = new MenuItem(popupMenu, SWT.CHECK);
			item.setText(Messages.getString("AVueTable.34"));
			item.setData(TypeMenuOptions.NO_FILTER);
			item.addListener(SWT.Selection, menuSelListener);
			boolean condition = OrdonnerFiltre.getInstance().getFiltreSelectTabulaire() == null;
			item.setSelection(condition);
			item.setEnabled(!condition);

			listeFiltreOrd.clear(); // To prevent from filter renaming + Issu
									// 854
			createMenuFiltre(item, listeFiltreOrd);

			item = new MenuItem(popupMenu, SWT.SEPARATOR);

			item = new MenuItem(popupMenu, SWT.NONE);
			item.setText(Messages.getString("AVueTable.33")); //$NON-NLS-1$
			item.setData(TypeMenuOptions.ADD_ANNOTATION);
			item.addListener(SWT.Selection, menuSelListener);
			item.setEnabled(!GestionLineCommandParameters.isAnnot_Lect_seule());

			// : add more actions here if needed
			top.setMenu(this.popupMenu);
			tblFix.setMenu(this.popupMenu);
		}
	}

	protected enum TypeMenuOptions {
		DISPLAY_COLUMN_MNG, DISPLAY_FILTER_VIEW, ADD_ANNOTATION, NO_FILTER, USE_SHORT_NAMES
	}

	public abstract void createMenuFiltre(MenuItem item, List<AFiltreComposant> listeFiltreOrd);

	public abstract AFiltreComposant getAppliedFilter();
	
	@Override
	public void onSearchVariable(DescripteurVariable descrVar,
			String stringValue, String value, Operation op,
			boolean next) {
		boolean dateRecherchee = false;
		boolean trouveVarComposee = false;
		Row row = this.tblFix.getSelection() != null && this.tblFix.getSelection().length != 0 ? (Row) this.tblFix.getSelection()[0].getData() : null;
		Message message = (Message) row.getData();
		int crtSelMsgId = row != null ? message.getMessageId() : 0;
		if (crtSelMsgId == -1) {
			crtSelMsgId = 0;
		}
		
		// Collection<Message> collMsg = this.data.getDataTable().getEnregistrement().getMessages(); 
		//List<Message> messages = new ArrayList<Message>(collMsg);

		List<Message> messages = new ArrayList<Message>();
		Object[] elements = ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null);
		for (Object element : elements) {
			if (element instanceof Row) {
				Object rowMsg = ((Row) element).getData();
				if (rowMsg instanceof Message) {
					messages.add((Message) rowMsg);
				}
			}
		}

		Message selMsg = null;
		String[] msgErr = new String[1];
		this.setSearchChange(false);
		Message msg = null;
		for (int i = next ? 0 : (messages.size() - 1); next ? i < messages.size() : i > -1; i = (next ? (i + 1) : (i - 1))) { // si next, on incremente sinon on decremente
			msg = messages.get(i);
			if (next && msg.getMessageId() < crtSelMsgId || ((!next) && msg.getMessageId() > crtSelMsgId)) {
				continue;
			}
			else if (msg.getMessageId() == crtSelMsgId) {
				if (!(this.contentProvider instanceof VueTabulaireContentProvider) || !descrVar.isVolatil()) {
					continue;
				} else {
					VueTabulaireContentProvider contentProvider = (VueTabulaireContentProvider) this.contentProvider;
					String[] values = VariableExplorationUtils.getFilteredValuesFromMessage(msg, 
							descrVar.getM_AIdentificateurComposant().getNom(), contentProvider.varNamesFilters);
					
					int occurences = this.countOccurences(values, stringValue);
					
					if (crtSelMsgId != this.previousSelectedMessageId) {
						if (next) {
							this.valueCounter = 0;
						} else {
							this.valueCounter = occurences - 1;
						}
					}
					
					this.previousSelectedMessageId = crtSelMsgId;
					if (next) {
						boolean nextMessage = occurences <= 1 || this.valueCounter == occurences - 1;					
						if (nextMessage) {
							continue;
						} else {
							this.valueCounter++;
						}
					} else {
						boolean previousMessage = occurences <= 1 || this.valueCounter == 0;
						if (previousMessage) {
							continue;
						} else {
							this.valueCounter--;
						}
					}
				}
			}

			// si variable compos�e
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
								LabelValeur valueLabel = getLabelForVariableComposee((VariableComposite) composant, msg);
								// issue 662
								if (valueLabel != null && value != null) {
									if (value.equals(valueLabel.getLabel()) && op == Operation.Equal || (!value.equals(valueLabel.getLabel())) && op == Operation.NotEqual) {
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
			} else { // Si variable pas compos�e
				// si c'est une date
				if (descrVar.getM_AIdentificateurComposant().getCode() == TypeRepere.date.getCode()) {
					dateRecherchee = true;
					selMsg = searchVariableDate(msg, descrVar, value, op, msgErr, false);
					if (selMsg != null) {
						break;
					}
				} else if (descrVar != null && descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC && descrVar.getM_AIdentificateurComposant() != null && descrVar.getM_AIdentificateurComposant().getNom().equals(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2"))) {
					dateRecherchee = true;
					selMsg = searchVariableDate(msg, descrVar, value, op, msgErr, true);
					if (selMsg != null) {
						break;
					}
				} else { // Si c'est une variable simple, analogique ou discr�te
					AVariableComposant var = VariableExplorationUtils.getVariable(descrVar, msg);
					
					if (var != null) {
						if (value == null || op == null) {
							selMsg = msg;
							break;
						}
						if (checkSearchedVariable(var, value, op, msgErr, msg, message)) {
							selMsg = msg;
							break;
						}
					}
				}
			}
		}

		if (selMsg != null) {
			int rowIdx = dateRecherchee ? getRowIndexForMessageDate(selMsg.getMessageId(), descrVar, next, msg.getAbsoluteTime(), message.getMessageId(), op, value, msgErr, ((IStructuredContentProvider) this.tblFix.getContentProvider()).getElements(null)) 
					: getRowIndexForMessageId(selMsg.getMessageId(), descrVar);
			// issue 740
			int indicePourSelectMessageAuMilieu = PositionMilieuViewer.getPosition(rowIdx);
			this.tblFix.setSelection(rowIdx, null);
			this.tblFix.setTopIndex(indicePourSelectMessageAuMilieu);
			handleLineSelection(rowIdx);
			ActivatorData.getInstance().setSelectedMsg(getMessageFromCurrentSelection());
			upSelection();

			// CPN : CE BOUT DE CODE SERT A SELECTIONNER LA BONNE VALEUR DANS LA LISTE DEROULANTE LORS DE LA RECHERCHE D'UNE VALEUR D'UNE
			// VARIABLE VOLATILE QUI A PLUSIEURS VALORISATIONS DANS LA VUE TABULAIRE
			
			if (value != null) {
			
				// S�lection de la bonne valeur dans la liste d�roulante (si liste d�roulante)
				AVariableComposant var = VariableExplorationUtils.getVariable(descrVar, selMsg);
				String nomColonne = var.getDescriptor().getM_AIdentificateurComposant().getNom();
	
				Integer numColonne = 0;
				for (String colonne : this.columnNames) {
					if (nomColonne.equals(colonne)) {
						break;
					}
					numColonne++;
				}
	
				FixedColumnTableViewerEditingSupport fixedTblVES = new FixedColumnTableViewerEditingSupport(this.tblFix.scrollingTableViewer, numColonne, nomColonne);
	
				String[] valuesFromRow = fixedTblVES.getValuesFromRow((Row) elements[rowIdx]);
				fixedTblVES.setValues(valuesFromRow);
	
				if (valuesFromRow != null && valuesFromRow.length > 1) {
					fixedTblVES.setCombo(true);
				}
	
				Integer valueIndex = 0;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descrVarDiscrete = ((DescripteurVariableDiscrete) var.getDescriptor());
					TableValeurLabel labels = descrVarDiscrete.getLabels();
					if (labels != null) {
						List<LabelValeur> listeLabelValeurs = labels.get(Activator.getDefault().getCurrentLanguage());
						String[] valuesToIndex = getValues(valuesFromRow, listeLabelValeurs);
						for (int i = 0; i < valuesToIndex.length; i++) {
							if (value.equals((valuesToIndex)[i])) {
								valueIndex = i;
								break;
							}
						}
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					for (int i = 0; i < valuesFromRow.length; i++) {
						if (value.equals((valuesFromRow)[i])) {
							valueIndex = i;
							break;
						}
					}
				}
	
				fixedTblVES.setValue(elements[rowIdx], valueIndex);
			
			}
			
			// FIN DU CODE POUR SELECTIONNER LA BONNE VALEUR DANS LA LISTE DEROULANTE

			if (msgErr != null && msgErr[0] != null) {
				afficherPasDeMsgCorrespondantCriteres(msgErr);
			}
		} else {
			afficherPasDeMsgCorrespondantCriteres(msgErr);
		}
	}
	
	private int countOccurences(String[] values, String value) {
		int result = 0;
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				result++;
			}
		}
		return result;
	}

}