package com.faiveley.samng.vuebinaire.ihm.vues;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.vues.ATableLabelProvider;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.formats.FormatJRU;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.vuebinaire.ihm.BarreProgressionDialog;
import com.faiveley.samng.vuebinaire.ihm.vues.guiFactory.AFabriqueVueBinaire;
import com.faiveley.samng.vuebinaire.ihm.vues.guiFactory.FabriqueVueBinaireATESS;
import com.faiveley.samng.vuebinaire.ihm.vues.guiFactory.FabriqueVueBinaireJRU;
import com.faiveley.samng.vuebinaire.ihm.vues.guiFactory.FabriqueVueBinaireTom4;
import com.faiveley.samng.vuebinaire.ihm.vues.guiFactory.FabriqueVueBinaireTomNg;

/**
 * Implementation of the binary view
 * 
 * @author meggy
 * 
 */
public class VueBinaire extends AbstractSelectionProviderVue implements
PropertyChangeListener, IDataChangedListener, ISelectionListener,
ICapturable{

	private boolean oneTimeOffset=true;//le positionnement offset ne se fait qu'une fois
	
	/* synchronization of the table from the keyboard */
	private boolean syncFromKey = false;
	
	// the view ID
	public static final String ID = "SAMNG.Vue.Binaire"; //$NON-NLS-1$

	// controls
	private Composite top = null;

	private Table table = null;

	// the rows
	private BinaryRow[] rows = null;

	// the data
	private static VueData data;

	// last selected message
	private int lastSelOffsetFromTop = 0;

	// the columns used for representing the bytes of the message
	private TableColumn[] bytesCols = null;

	private int[] colsWidths = null;

	// private static GC gc;

	// ToolBar
	private IActionBars bars;

	// d�claration des actions
	private ImprimerVueAction imprimerVueAction;
	private CapturerVueAction capturerVueAction;
	private Action synchroVuesAction;
	private ArrayList<Action> listeActionsAuditrice;
	private static TableViewer viewer = null;
	private ArrayList<BinaryRow> tableRows = null;
	private static Color couleurBlockDefauts;
	private static Color couleurCRC;
	private static Color couleurEventId;
	private static Color couleurFFBlockDefault1;
	private static Color couleurLigneBleue;
	private static Color couleurXMLRelated;
	private static Color couleurBadBlock;
	private static Color couleurBadLength;

	/** */
	public VueBinaire() {

		FormatSAM formatFichierOuvert = BridageFormats.getInstance().getFormatFichierOuvert("");
		if (formatFichierOuvert != null) {

			if (formatFichierOuvert.getFjru() == FormatJRU.jru) {
				tailleBlocData = (short) FabriqueVueBinaireJRU.tailleBloc;
			}

			data = ActivatorData.getInstance().getVueData();

			// initialisation des couleurs
			couleurBlockDefauts = new Color(Display.getCurrent(), new RGB(204,153, 255));
			couleurCRC = new Color(Display.getCurrent(), new RGB(255, 0, 0));
			couleurEventId = new Color(Display.getCurrent(), new RGB(255, 128,64));
			couleurFFBlockDefault1 = new Color(Display.getCurrent(), new RGB(255, 255, 153));
			couleurLigneBleue = new Color(Display.getCurrent(), new RGB(153,204, 255));
			couleurBadBlock = new Color(Display.getCurrent(), new RGB(192, 192,192));
			couleurXMLRelated = new Color(Display.getCurrent(), new RGB(0, 192,192));
			couleurBadLength = new Color(Display.getCurrent(), new RGB(255, 0,0));
			setPartName(Messages.getString("VueBinaire.0"));
			this.currentSelection = new MessageSelection();
		}
	}

	

	/**
	 * 
	 * @param parent
	 *            1
	 */
	public final void createPartControl(final Composite parent) {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		System.gc();
		this.top = new Composite(parent, SWT.NONE);
		this.top.setLayout(new FillLayout());

		ActivatorData.getInstance().addDataListener(this);
		try {
			loadTable();
		} catch (Exception ex) {
			//ex.printStackTrace();
		}
		if (this.rows != null && this.rows.length > 0) {
			// Register this as Selection provider and as selection listener
			getSite().setSelectionProvider(this);
			ISelectionService selService = getSite().getWorkbenchWindow()
					.getSelectionService();
			selService.addPostSelectionListener(this);
		}else{
			//			dispose();
		}

		makeActions();
		this.bars = getViewSite().getActionBars();
		ajoutActionToolBar(synchroVuesAction);
		ajoutSeparateurToolBar();
		ajoutActionToolBar(imprimerVueAction);
		ajoutActionToolBar(capturerVueAction);
		
		if (GestionLineCommandParameters.getIndiceMsg()!=-1 && oneTimeOffset)
			selectionChanged(null, null) ;
	}

	/** Ajout des actions */
	private void makeActions() {
		// r�cup�ration de la fenetre active
		IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		setPartName(Messages.getString("VueBinaire.0"));
		listeActionsAuditrice = new ArrayList<Action>(0);
		// ajout des actions
		this.capturerVueAction = new CapturerVueAction(window,
				Messages.getString("VueBinaire.1")); //$NON-NLS-1$
		this.capturerVueAction.setEnabled(capturerVueAction.isEnabled());

		this.imprimerVueAction = new ImprimerVueAction(window,
				Messages.getString("VueBinaire.2")); //$NON-NLS-1$
		this.imprimerVueAction.setEnabled(imprimerVueAction.isEnabled());

		// ajout des actions
		synchroVuesAction = new Action() {
			public void run() {
				updateSelectionMessage();
				((MessageSelection) VueBinaire.this.currentSelection).setUserSentSelection(true);
				fireSelectionChanged(VueBinaire.this.currentSelection);
				((MessageSelection) VueBinaire.this.currentSelection).setUserSentSelection(false);
			}
		};
		String textSynchro = Messages.getString("VueBinaire.3"); //$NON-NLS-1$
		synchroVuesAction
		.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_synchro.png")); //$NON-NLS-1$
		synchroVuesAction.setText(textSynchro);
		synchroVuesAction.setToolTipText(textSynchro);
		synchroVuesAction.setEnabled(true);
		// listeActionsAuditrice.add(synchroVuesAction);
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
		this.bars.getToolBarManager().add(new Separator());
	}

	/**
	 * Creates and loads the table
	 * 
	 */
	private void loadTable() {
		disposeTable();
		// Activator.getDefault().getProgressBar().start();
		// VueWaitBar.getInstance().setRect(
		// PlatformUI.getWorkbench()
		// .getActiveWorkbenchWindow().getShell().getBounds());
		// VueWaitBar.getInstance().start();
		// this.table = new Table(this.top, SWT.VIRTUAL | SWT.SINGLE |
		// SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		// this.table.setLayout(new FillLayout());

		// creates the viewer associated
		viewer = new TableViewer(this.top, SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION);
		viewer.setUseHashlookup(true);

		this.table = viewer.getTable();

		this.table.setLayout(new FillLayout());

		// creates the columns
		TableColumn tc = new TableColumn(this.table, SWT.NONE);
		tc.setWidth(50);
		tc.setText(Messages.getString("VueBinaire.5")); //$NON-NLS-1$
		tc.setToolTipText((Messages.getString("VueBinaire.5")));

		this.bytesCols = new TableColumn[tailleBlocData];
		this.colsWidths = new int[tailleBlocData];

		for (int i = 0; i < tailleBlocData; i++) {
			this.bytesCols[i] = new TableColumn(this.table, SWT.NONE);
			// this.bytesCols[i].setWidth(40);
			// this.bytesCols[i].setText(" ");
		}

		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		// sets the label provider
		viewer.setLabelProvider(new ATableLabelProvider() {

			@Override
			public String getColumnText(Object element, int columnIndex) {
				BinaryRow r = ((BinaryRow) element);
				String label = null;
				// returns the label of the column
				if (columnIndex == 0) {
					label = String.valueOf(r.getBlockId());
				} else {
					label = r.getLabels()[columnIndex - 1];
				}
				return label;
			}

			@Override
			public Color getBackground(Object element, int columnIndex) {
				Color c = null;
				BinaryRow row = (BinaryRow) element;

				// set the color conform to specifications
				if (row.getError() != null
						&& !row.getError().equals(ErrorType.XMLRelated)) {
					switch (row.getError()) {
					case BlockDefaut:
						c = couleurBlockDefauts;
						break;

					case CRC:
						c = couleurCRC;
						break;

					case EventId:
						c = couleurEventId;
						break;

					case FFBlockDefault:
						int indexS = (row.getError().getStartPosition() - ((row
								.getBlockId() - 1)
								* tailleBlocData
								+ tailleEntete + tailleTableEvenement));

						// int indexS = ((row.getError().getStartPosition() -
						// row.getBlockId()) % ConstantesParcoursBinaire.tailleBlocData ) +1;
						int indexE = indexS + row.getError().getLength();
						if (columnIndex >= indexS + 1
								&& columnIndex < indexE + 1) {
							c = couleurFFBlockDefault1;
						} else {
							if (row.isBlue()) {
								c = couleurLigneBleue;
								;
							}
						}
						break;

					case BadBlock:
						c = couleurBadBlock;
						break;

					case XMLRelated:
						c = couleurXMLRelated;
						break;

					case BadLength:
						c = couleurBadLength;
						break;
					default:
						break;
					}

				} else {

					if (row.isBlue()) {
						c = couleurLigneBleue;
					}
				}
				return c;
			}
		});

		Listener eraseListener = new Listener() {

			@Override
			public void handleEvent(Event event) {

				if ((event.detail & SWT.SELECTED) != 0) {
					GC gc = event.gc;
					Rectangle rect = event.getBounds();

					gc.setForeground(Display.getCurrent().getSystemColor(
							SWT.COLOR_LIST_SELECTION_TEXT));
					gc.setBackground(Display.getCurrent().getSystemColor(
							SWT.COLOR_LIST_SELECTION));
					gc.fillRectangle(rect);
					event.detail &= ~SWT.SELECTED;
				}

			}

		};
		table.addListener(SWT.EraseItem, eraseListener);

		// adds a listener to update the selection
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				updateSelectionMessage();
				
				/* If the preferences require synchronization with the Up and Down keys */
				if (syncFromKey == true) {
					((MessageSelection) VueBinaire.this.currentSelection).setUserSentSelection(true);
					fireSelectionChanged(VueBinaire.this.currentSelection);
					((MessageSelection) VueBinaire.this.currentSelection).setUserSentSelection(false);
					syncFromKey = false;
				}
			}
		});

		this.table.addKeyListener(new KeyListener() {

			@Override
			public void keyPressed(KeyEvent e) {
				// synchro si pression sur bouton Entr�e
				if (e.keyCode == SWT.CR) {
					updateSelectionMessage();
					
					((MessageSelection) VueBinaire.this.currentSelection)
					.setUserSentSelection(true);
					fireSelectionChanged(VueBinaire.this.currentSelection);
					((MessageSelection) VueBinaire.this.currentSelection)
					.setUserSentSelection(false);
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

			@Override
			public void keyReleased(KeyEvent e) {
			}

		});

		// add mouse listener to update the selection
		this.table.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {
				if (e.button == 1) { // left button double click	
					if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
							.equals(PreferenceConstants.MOUSE_SYNC_DOUBLE_CLICK)) {
						updateSelectionMessage();
	
						((MessageSelection) VueBinaire.this.currentSelection)
						.setUserSentSelection(true);
						fireSelectionChanged(VueBinaire.this.currentSelection);
						((MessageSelection) VueBinaire.this.currentSelection)
						.setUserSentSelection(false);
					}
				}
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
				for (int i = 0; i < listeActionsAuditrice.size(); i++) {
					listeActionsAuditrice.get(i).setEnabled(true);
				}
				
				if (e.button == 1) { // left button double click
					if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
							.equals(PreferenceConstants.MOUSE_SYNC_SINGLE_CLICK)) {
						updateSelectionMessage();
	
						((MessageSelection) VueBinaire.this.currentSelection)
						.setUserSentSelection(true);
						fireSelectionChanged(VueBinaire.this.currentSelection);
						((MessageSelection) VueBinaire.this.currentSelection)
						.setUserSentSelection(false);
					}
				}
			}
		});

		// set the content provider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		if (ActivatorData.getInstance().getPoolDonneesVues().get("binaryRows") == null
				|| ((BinaryRow[]) ActivatorData.getInstance().getPoolDonneesVues()
						.get("binaryRows")).length == 0) {

			// set the input
			this.rows = getInput();
			if(this.rows.length>0)
				ActivatorData.getInstance().getPoolDonneesVues().put("binaryRows", this.rows);
		} else {
			this.rows = (BinaryRow[]) ActivatorData.getInstance().getPoolDonneesVues().get("binaryRows");
		}

		// if(this.rows.length>0){
		// VueWaitBar.getInstance().setRect(
		// PlatformUI.getWorkbench()
		// .getActiveWorkbenchWindow().getShell().getBounds());
		// VueWaitBar.getInstance().start();
		// }

		if (this.rows != null) {
			viewer.setInput(this.rows);

			for (int i = 0; i < colsWidths.length; i++) {
				if (colsWidths[i] == 0)
					colsWidths[i] = 40;
				bytesCols[i].setWidth(colsWidths[i]);
			}
			// set table visible
			this.table.setVisible(true);

			// Activator.getDefault().getProgressBar().stop();
			// if(this.rows.length>0)
			// VueWaitBar.getInstance().stop();

			if (ActivatorData.getInstance().getSelectedMsg() != null) {
				this.table.setSelection(getRowIndexForMessageId(ActivatorData.getInstance().getSelectedMsg().getMessageId()));
			} else {

				this.table.setSelection(0);

			}
		}
	}

	/*private static GC getGC() {
		if (gc == null)
			gc = new GC(Display.getCurrent());

		return gc;
	}*/

	/**
	 * Dispose the table
	 * 
	 */
	public synchronized void disposeTable() {
		if (this.table != null) {
			synchronized (this.table) {
				try {
					if (this.table != null && !this.table.isDisposed()) {
						this.table.dispose();

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					this.table = null;
				}
			}
		}
	}

	/**
	 * Updates the selection
	 * 
	 */
	protected void updateSelectionMessage() {
		// when a double click
		int selIdx = this.table.getSelectionIndex();
		Object[] elements = this.rows;
		if (selIdx < 0 || selIdx >= elements.length)
			return;
		BinaryRow row = this.rows[selIdx];

		// sets the current selection
		((MessageSelection) this.currentSelection).setMessageId(row.getMsgId());

		this.lastSelOffsetFromTop = selIdx - this.table.getTopIndex();
		try {
			ActivatorData.getInstance().setSelectedMsg(
					ActivatorData.getInstance().getVueData().getDataTable()
					.getEnregistrement().getGoodMessage(row.getMsgId()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * After updates the table scrolls the table to the last selection
	 * 
	 */
	protected void scrollToPreviousSelection() {
		if (!this.currentSelection.isEmpty()) {
			int prevSelMsgIdx = getRowIndexForMessageId(((MessageSelection) this.currentSelection)
					.getMessageId());
			if (prevSelMsgIdx == -1) {
				this.table.setSelection(0);
				this.lastSelOffsetFromTop = 0;
				return;
			}
			this.table.setSelection(prevSelMsgIdx);
			this.table
			.setTopIndex(prevSelMsgIdx < this.lastSelOffsetFromTop ? 0
					: prevSelMsgIdx - this.lastSelOffsetFromTop);
		}
	}

	/**
	 * Listen on a property change. If message selected set the selection
	 */
	public final void propertyChange(final PropertyChangeEvent evt) {
		// we just react on add events
		if ("MSG_SELECT".equals(evt.getPropertyName())) { //$NON-NLS-1$
			this.table.setSelection(((Integer) evt.getNewValue()).intValue());
		}
	}

	/**
	 * Constructs and returns the current input
	 * 
	 * @return the rows
	 */
	private BinaryRow[] getInput() {
		tableRows = new ArrayList<BinaryRow>(0);
		AFabriqueVueBinaire fabriqueVue = null;
		if ((data != null && data.getDataTable() != null
				&& data.getDataTable().getEnregistrement() != null)
				||fabriqueVue==null
				) {

			ParseurParcoursBinaire parser = TypeParseur.getInstance().getParser();
			if (parser instanceof ParseurParcoursSamng) {
				fabriqueVue = new FabriqueVueBinaireTomNg();
			} else if (parser instanceof ParseurParcoursAtess) {
				fabriqueVue = new FabriqueVueBinaireATESS();
			} else if (parser instanceof ParseurParcoursJRU) {
				fabriqueVue = new FabriqueVueBinaireJRU();
			} else {
				fabriqueVue = new FabriqueVueBinaireTom4();
			}
		}

		// BarreProgressionChargementVue b = new
		// BarreProgressionChargementVue("barre progression", fabriqueVue);
		//
		// b.schedule();
		BarreProgressionDialog barre = new BarreProgressionDialog(
				"barre progression", fabriqueVue);
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display
				.getDefault().getActiveShell());
		try {
			dialog.run(true, true, barre);
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		if(!dialog.getProgressMonitor().isCanceled())
			tableRows = (ArrayList<BinaryRow>) barre.getLignesTableaux();

		// tableRows = fabriqueVue.remplirTableau();

		BinaryRow[] tabLigne = null;
		if (tableRows.size() > 0)
			// int textWidth = getGC().stringExtent(hexVal).x;
			tabLigne = tableRows.toArray(new BinaryRow[tableRows.size()]);

		tableRows.clear();
		return tabLigne;
	}

	/**
	 * Create the rows from the list of messages
	 * 
	 * @param mapIdMsg
	 *            map <message id, message>
	 * @param rows
	 *            the list of rows in which to add
	 */
	/*private void getMessages(Map<Integer, Message> mapIdMsg,
			ArrayList<BinaryRow> rows) {
		if (mapIdMsg == null || mapIdMsg.size() == 0) {
			return;
		}

		StringBuilder buf = new StringBuilder();
		Message msg = null;
		String[] labels = null;
		byte[] dataMsg = null;
		BinaryRow row = null;

		// for each message create a number of rows
		for (Integer id : mapIdMsg.keySet()) {
			msg = mapIdMsg.get(id);
			int posBlock = (id - (tailleEntete + tailleTableEvenement))
					/ tailleBlocData;

			// get the data from the message
			dataMsg = msg.getMessageData();

			// calculates the number of blocks
			int msgBlockNo = dataMsg.length / tailleBlocData;

			// create a row for each block
			for (int index = 0; index < msgBlockNo; index++) {
				// creates as many labels as the length of a block
				labels = new String[tailleBlocData];

				for (int k = index * tailleBlocData; k < dataMsg.length; k++) {

					// get the data coresponding to the position in the block
					// and set the value of the label. the value is 1 octet
					String hexVal = Integer.toHexString(dataMsg[k] & 0xff);
					hexVal = hexVal.length() == 2 ? hexVal : "0" + hexVal; //$NON-NLS-1$

					int labelPos = k - index * tailleBlocData;
					labels[labelPos] = hexVal;

					// calculate teh column size taking in account the witdh of
					// the new label
					// gets the widht of the text, add to it some pixels because
					// in the cell
					// there should be some pixels on the left and on the right
					// of the text
					// and compare with the old width of the column, and if this
					// is smaller
					// then set the new value as the text width
					int textWidth = getGC().stringExtent(hexVal).x;
					textWidth = textWidth + (textWidth / 3 * 4) - 4;
					// put more pixels in order to avoid addition of "..."
					if (this.colsWidths[labelPos] < textWidth)
						this.colsWidths[labelPos] = textWidth + 5;

					// if finised the data block extract from the mesasge
					// then we have reached the end of the message
					if (k + 1 == tailleBlocData * (index + 1)
							&& dataMsg.length > tailleBlocData) {
						break;
					}
				}

				// create the binary row
				row = new BinaryRow();
				row.setMsgId(msg.getMessageId());
				// calculates the position as start message + no of blocks *
				// length of the block
				// int pos = msg.getMessageId() + index * ConstantesParcoursBinaire.tailleBlocData;

				int pos = (msg.getMessageId() - tailleEntete - tailleTableEvenement)
						/ tailleBlocData + index + 1;

				// set the members of the row
				row.setBlockId(pos);
				row.setLabels(labels);
				row.setError(msg.getError());

				// add the row
				if (posBlock + index >= rows.size()) {
					// if the block numeber is bigger than the number of rows
					// then append the row to the list
					rows.add(row);
				} else {

					// if the block number is smaller than the number of rows
					// then insert the row to the list
					rows.add(posBlock + index, row);
				}

				// reset buffer
				buf.setLength(0);
				row = null;
			}
			buf = new StringBuilder();
			msg = null;
			dataMsg = null;
			labels = null;
		}
	}*/


	/**
	 * Listener method to be informed when new bunary file was loaded in the
	 * application
	 */
	public void onDataChange() {
		disposeTable();
//		if (((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).getNomFichierParcoursBinaire()!=null){
			loadTable();
//		}
		this.top.layout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.ISelectionListener#selectionChanged(org.eclipse.ui.
	 * IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (this.rows == null) {
			return;
		}


		//positionnement offset
		if (GestionLineCommandParameters.getIndiceMsg()!=-1 && oneTimeOffset) {
			try {
				oneTimeOffset=false;
				int selId = GestionLineCommandParameters.getIndiceMsg();
				// avoid handling events sent by this view

				int rowIdx = getRowIndexForMessageId(selId);
				
				// Apr�s l'ouverture d'un fichier par ligne de commande, l'offset du nouveau fichier 
				// ouvert doit �tre repositionn� sur la premi�re ligne de la vue. Pour cela on 
				// utilise la valeur 0.
				if (selId == 0) {
					rowIdx = 0 ;
				}

				if (rowIdx != -1) {
					this.table.setSelection(rowIdx);
					this.lastSelOffsetFromTop = rowIdx - this.table.getTopIndex();
				} else {
					int cptRechercheId = selId;
					while (rowIdx == -1 && cptRechercheId > 0) {
						selId--;
						rowIdx = getRowIndexForMessageId(selId);
					}
					if (rowIdx != -1) {
						try {
							this.table.setSelection(rowIdx);
							this.lastSelOffsetFromTop = rowIdx
									- this.table.getTopIndex();
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							System.out.println("pos: " + rowIdx);
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			//traitement normal
		}else 

			// avoid handling events sent by this view
			if ((selection instanceof MessageSelection) && (part != this)
					&& !selection.isEmpty()) {
				// selection was changed in other views then set the selection
				if (!((MessageSelection) selection).getUserSendSelection())
					return;

				if (ActivatorData.getInstance().getSelectedMsg() == null) {
					return;
				}

				int selId = ActivatorData.getInstance().getSelectedMsg()
						.getMessageId();
				int rowIdx = getRowIndexForMessageId(selId);

				if (rowIdx != -1) {
					this.table.setSelection(rowIdx);
					this.lastSelOffsetFromTop = rowIdx - this.table.getTopIndex();
				} else {
					int cptRechercheId = selId;
					while (rowIdx == -1 && cptRechercheId > 0) {
						cptRechercheId--;
						rowIdx = getRowIndexForMessageId(cptRechercheId);
					}
					if (rowIdx != -1) {
						try {
							this.table.setSelection(rowIdx);
							this.lastSelOffsetFromTop = rowIdx
									- this.table.getTopIndex();
						} catch (RuntimeException e) {
							// TODO Auto-generated catch block
							// e.printStackTrace();
							System.out.println("pos: " + rowIdx);
						}
					}
				}
			}
	}

	/**
	 * Get a row index by the id of the message id associated to that row
	 * 
	 * @param msgId
	 *            the searched message ID
	 * @return the found row index or -1 if no such message ID found in rows
	 */
	protected int getRowIndexForMessageId(int msgId) {
		int rowId;
		int rowIdx = 0;
		int retIdx = -1;
		for (BinaryRow row : this.rows) {
			rowId = row.getMsgId();
			if (rowId == msgId) {
				retIdx = rowIdx;
				break;
			}
			rowIdx++;

		}
		return retIdx;
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
		
		// this.top = null;
		try {
			this.rows = null;
			data = null;
			this.bytesCols = null;
			this.bars = null;
			this.imprimerVueAction = null;
			this.capturerVueAction = null;// d�claration des actions
			this.synchroVuesAction = null;
			this.listeActionsAuditrice = null;
			viewer = null;
			if (this.tableRows != null) {
				this.tableRows.clear();
				this.tableRows = null;
			}
			//
			disposeTable();
			// this.table.dispose();
			this.top.dispose();
			this.top = null;
			super.dispose();
			ActivatorData.getInstance().removeDataListener(this);
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	
	@Override
	public Composite getContenu() {
		return top;
	}

}
