package com.faiveley.samng.vuemultimedia.ihm.vues.vuemultimedia;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.Timer;

import org.apache.xmlbeans.XmlException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;

import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.discovery.NativeDiscovery;
import uk.co.caprica.vlcj.player.MediaPlayer;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.vuetoolbar.IVueToolbar;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.multimedia.MultimediaFile;
import com.faiveley.samng.principal.sm.parseurs.ParseurConfigurationVueMultimedia;
import com.faiveley.samng.vuemultimedia.ihm.ActivatorVueMultimedia;

// Useful sources:
// 	- http://caprica.github.io/vlcj/javadoc/3.10.1/uk/co/caprica/vlcj/player/MediaPlayer.html
// 	- https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/basic/PlayerControlsPanel.java

public class VueMultimedia extends AbstractSelectionProviderVue implements IVueToolbar, IDataChangedListener, ISelectionListener, ICapturable {	
	/** Static fields **/
	public static final String ID = "SAMNG.Vue.Multimedia";
	
	private static final String[] COLUMN_NAMES = new String[] {ParseurConfigurationVueMultimedia.FILE_LIST_COLUMN_FLAG, ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG, ParseurConfigurationVueMultimedia.DURATION_COLUMN_FLAG};

	private static final String PLAY_BUTTON_LABEL = Messages.getString("VueMultimedia.0");
	private static final String PAUSE_BUTTON_LABEL = Messages.getString("VueMultimedia.1");
	private static final String PREVIOUS_BUTTON_LABEL = Messages.getString("VueMultimedia.2");
	private static final String NEXT_BUTTON_LABEL = Messages.getString("VueMultimedia.3");
	private static final String VOLUME_SCALE_LABEL = Messages.getString("VueMultimedia.4");
	private static final String POSITION_SCALE_LABEL = Messages.getString("VueMultimedia.5");
	private static final String FILE_LABEL_LABEL = Messages.getString("VueMultimedia.6");
	private static final String FILE_CORRUPTED = Messages.getString("VueMultimedia.7");
	
	private static final int POSITION_SCALE_STEP_NUMBER = 10000;
	private static final int UPDATE_SCALE_DELAY_INTERVAL = 1000;
	
	private static final int VOLUME_SCALE_STEP_NUMBER = 200;
	private static final int VOLUME_SCALE_DEFAULT_STEP_VALUE = 50;

	public static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SS");
	
	public static Color TAB_BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
	public static Color PLAYER_BACKGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_GREEN);
	public static Color PLAYER_FOREGROUND_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_WHITE);
	public static Color NOT_PLAYABLE_COLOR = Display.getDefault().getSystemColor(SWT.COLOR_RED);

	public static int PLAYER_MIN_WIDTH = 500;
	public static int PLAYER_MIN_HEIGHT = 100;
	public static int PLAYER_SPACING = 6;
	
	/** Model **/
	private List<MultimediaFile> multimediaFiles = null;
	private int fileIndex = 0;

	/** IHM **/
	private IActionBars bars = null;	
	private Action synchroVuesAction = null;
	private ImprimerVueAction imprimerVueAction = null;
	
	private SashForm sashForm = null;
		private Composite filesComposite = null;
			private Table filesTable = null;
		private TabFolder mediaPlayerTabFolder = null;
			private TabItem mediaPlayerTabItem = null;
				private Frame mediaFrame = null;
					private EmbeddedMediaPlayerComponent mediaPlayer = null;
				private ScrolledComposite mediaPlayerControlScrolledComposite = null;
					private Composite mediaPlayerControlGridComposite = null;
						private Composite mediaPlayerControlComposite = null;
							private Composite topControlButtonsComposite = null;
								private Scale positionScale = null;
							private Composite bottomControlButtonsComposite = null;
								private Composite navigateControlButtonsComposite = null;
									private Button playPauseButton = null;
									private Button previousButton = null;
									private Button nextButton = null;
								private Composite volumeControlButtonsComposite = null;
									private Label volumeImage = null;
									private Scale volumeScale = null;
								private Composite timeControlComposite = null;
									private Label timeLabel = null;
					
	/** Time management **/
	private Timer updateScaleTimer;
	private ActionListener updateScaleActionListener;
	private boolean mediaPlayerOnPause = false;
	private String totalTimeForCurrentFile = null;
	
	private boolean isVisible;
	
	/** Constructor **/
	public VueMultimedia() {
		initView();
	}
	
	private void initView() {
		this.multimediaFiles = ActivatorData.getInstance().getMultimediaFiles();
		this.currentSelection = new MessageSelection();
	}
	
	/** Interface methods **/
	@Override
	public void onDataChange() {
		if (!this.filesTable.isDisposed()) {
			initView();
			fillFilesTable();
			prepareMedia();
		}
	}
	
	@Override
	public void setFocus() {
	}
	
	/** Components initialization **/		
	public final void createPartControl(final Composite parent) {
		if (!ActivatorData.getInstance().hasMultimediaFiles()) {			
			return;
		}
		
		this.sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		this.sashForm.setLayout(new FillLayout());
		
		this.filesComposite = new Composite(this.sashForm, SWT.NONE);
		this.filesComposite.setLayout(new FillLayout());
		
		RowLayout rowLayout = new RowLayout(SWT.VERTICAL);
		rowLayout.spacing = 5;
		
		this.mediaPlayerTabFolder = new TabFolder(sashForm, SWT.EMBEDDED | SWT.H_SCROLL);	
		this.mediaPlayerTabFolder.setLayout(rowLayout);
		
		this.mediaPlayerTabItem = new TabItem(this.mediaPlayerTabFolder, SWT.NONE);
		
		this.createFilesTable();
		this.createMediaPlayer();
		
		this.bars = getViewSite().getActionBars();
		this.makeActions();
		
		this.initPartListener();
		
		this.sashForm.addListener(SWT.Resize, new Listener() {
			int init = 0;
			
	        @Override
	        public void handleEvent(Event e) {	        	
	        	if (init < 2) {
	        		init++;
	        		
	        		sashForm.setWeights(new int[] {50, 50});
	        		
	        		int filesListWeight = ActivatorVueMultimedia.getDefault().getConfigurationManager().getFilesListColumnsTotalWeight();
	        		int filesListWeightInPercent = (100 * filesListWeight) / (sashForm.getChildren()[0].getSize().x + sashForm.getChildren()[1].getSize().x);
	        			        		
	        		do {
		        		if (filesListWeightInPercent >= 0 && filesListWeightInPercent <= 100) {
		        			sashForm.setWeights(new int[] {filesListWeightInPercent, 100 - filesListWeightInPercent});
		        		}
		        		
		        		filesListWeightInPercent++;
	        		} while (filesTable.getHorizontalBar().getVisible());
	        	}
	        }
	    });
		
		getSite().setSelectionProvider(this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(this);
	}
	
	public void makeActions() {
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		
		synchroVuesAction = new Action() {
			public void run() {
				synchronizeToOtherViews();
			}
		};
		
		synchroVuesAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/toolBar/vues_commun_synchro.png"));
		synchroVuesAction.setText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.1"));
		synchroVuesAction.setToolTipText(com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.2"));
		synchroVuesAction.setEnabled(!ActivatorData.getInstance().isMultimediaFileAlone());
		
		imprimerVueAction = new ImprimerVueAction(window, com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.8"));
		imprimerVueAction.setEnabled(true);
		
		ajoutActionToolBar(synchroVuesAction);
		ajoutActionToolBar(imprimerVueAction);
	}
	
	public void ajoutActionToolBar(Action action) {
		this.bars.getToolBarManager().add(action);
	}

	public void ajoutActionToolMenuBar(Action action) {
		this.bars.getMenuManager().add(action);
	}

	public void ajoutSeparateurToolBar() {
		this.bars.getToolBarManager().add(new Separator());
	}
	
	private void initPartListener() {
		final IViewPart thisPart = this;
		getSite().getPage().addPartListener(new IPartListener2() {
			@Override
			public void partVisible(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				
				if (part == thisPart) {
					isVisible = true;
				}
			}
			
			@Override
			public void partOpened(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partInputChanged(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partHidden(IWorkbenchPartReference partRef) {
				IWorkbenchPart part = partRef.getPart(false);
				
				if (part == thisPart) {
					isVisible = false;
				}
			}
			
			@Override
			public void partDeactivated(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partClosed(IWorkbenchPartReference partRef) {
				if (partRef.getId().equals(ID)) {
					try {
						ActivatorVueMultimedia.getDefault().getConfigurationManager().saveToFile(null);
					} catch (XmlException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					getSite().getPage().removePartListener(this);
				}
			}
			
			@Override
			public void partBroughtToTop(IWorkbenchPartReference partRef) {
			}
			
			@Override
			public void partActivated(IWorkbenchPartReference partRef) {
			}
		});
	}
		
	private void createFilesTable() {
		// Maybe it was already added
		if (!ActivatorData.getInstance().isDataListening(this)) {
			ActivatorData.getInstance().addDataListener(this);
		}
		
		ActivatorVueMultimedia.getDefault().getConfigurationManager().loadFromFile();
		
		this.filesTable = new Table(this.filesComposite, SWT.FULL_SELECTION);
		this.filesTable.setHeaderVisible(true);
		this.filesTable.setLinesVisible(true);
		
		this.filesTable.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileIndex = filesTable.getSelectionIndex();
				reload();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		
		this.filesTable.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {				
				if (e.button == 1 && !ActivatorData.getInstance().isMultimediaFileAlone()) {
					if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
							.equals(PreferenceConstants.MOUSE_SYNC_DOUBLE_CLICK)) {
						synchronizeToOtherViews();
					}
				}
			}

			@Override
			public void mouseDown(MouseEvent e) {
			}

			@Override
			public void mouseUp(MouseEvent e) {	
				if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
						.equals(PreferenceConstants.MOUSE_SYNC_SINGLE_CLICK)) {
					synchronizeToOtherViews();
				}
			}
		});
		
		Map<String, TableColumn> columns = new HashMap<String, TableColumn>();
		
		for (String columnName : COLUMN_NAMES) {
			if (ActivatorData.getInstance().isMultimediaFileAlone() && columnName.equals(ParseurConfigurationVueMultimedia.BEGIN_DATE_COLUMN_FLAG)) {
				continue;
			}
			
			TableColumn column = new TableColumn(this.filesTable, SWT.SINGLE | SWT.CENTER);
			
			column.setWidth(ActivatorVueMultimedia.getDefault().getConfigurationManager().getColumnWidth(columnName));
			column.setText(ActivatorVueMultimedia.getDefault().getConfigurationManager().getColumnText(columnName));
			
			columns.put(columnName, column);
		}
		
		addControlListenerToColumns(columns);
		fillFilesTable();
	}
	
	private void fillFilesTable() {
		this.filesTable.removeAll();
		
		if (this.multimediaFiles != null) {
			for (int i = 0; i < this.multimediaFiles.size(); i++) {
				MultimediaFile multimediaFile = multimediaFiles.get(i);
				
				TableItem item = new TableItem(this.filesTable, SWT.NULL);
				
				int index = 0;
				item.setText(index++, multimediaFile.getFile() != null ? multimediaFile.getFile().getName() : "");
				
				if (!ActivatorData.getInstance().isMultimediaFileAlone()) {
				    item.setText(index++, multimediaFile.getAbsoluteTime() != null ? ConversionTemps.getFormattedDate(multimediaFile.getAbsoluteTime(), true) : "");
				}
				
			    item.setText(index++, multimediaFile.getDurationToString() != null ? multimediaFile.getDurationToString() : "");
			    
			    if (multimediaFile.isPlayable() == false) {
			    	item.setBackground(NOT_PLAYABLE_COLOR);
			    	item.setForeground(PLAYER_FOREGROUND_COLOR);
			    }
			}
		}

		this.filesTable.select(0);
		
		for (TableColumn c : this.filesTable.getColumns()) {			
			if (c.getWidth() == 0) {
				c.pack();
			}
		}
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
	private void addControlListenerToColumns(Map<String, TableColumn> columns) {
		final Iterator<Entry<String, TableColumn>> it = columns.entrySet().iterator();

		while (it.hasNext()) {
			final Entry<String, TableColumn> pair = it.next();
			final String columnName = pair.getKey();
			final TableColumn column = pair.getValue();
			
			column.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					if (event.widget instanceof TableColumn) {
						TableColumn column = (TableColumn) event.widget;
						int width = column.getWidth();
						
						ConfigurationColonne columnConfiguration = ActivatorVueMultimedia.getDefault().getConfigurationManager().getColonne(columnName);
						
						if (columnConfiguration != null) {
							columnConfiguration.setLargeur(width);
						}
					}
				}
			});
		}
	}
	
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if ((selection instanceof MessageSelection) && (!(part instanceof VueMultimedia)) && !selection.isEmpty() && this.multimediaFiles != null && this.multimediaFiles.size() > 0) {
			if (!((MessageSelection) selection).getUserSendSelection()) {
				return;
			}
			
			MessageSelection messageSelection = (MessageSelection) selection;
			if (!messageSelection.shouldNotTriggerAudio()) {
				setPositionFromSelectedMessage(((MessageSelection) selection).getMessageId());
			}
		}
	}
	
	/** Synchronization **/
	
	private void setPositionFromSelectedMessage(int selectedMessageId) {
		if (!this.isVisible) {
			return;
		}
		
		int index;			
		for (index = 0; index < this.multimediaFiles.size(); index++) {				
			if (this.multimediaFiles.get(index).getMessage().getMessageId() > selectedMessageId) {
				if (index > 0) {
					index--;
				}
				
				break;
			}
		}
		
		if (index == this.multimediaFiles.size()) {
			index--;
		}
		
		this.fileIndex = index;
		reload();
		this.filesTable.setSelection(this.fileIndex);
		
		long multimediaFileAbsoluteTime = this.multimediaFiles.get(index).getAbsoluteTime();
		long selectedMessageAbsoluteTime = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(selectedMessageId).getAbsoluteTime();
		long offset = selectedMessageAbsoluteTime - multimediaFileAbsoluteTime;
		
		start();
		
		if (offset > 0) {				
			setPositionFromOffset(offset);
			updateScalePosition();
		}
	}
	
	private void synchronizeToOtherViews() {
		MultimediaFile selectedMultimediaFile = this.multimediaFiles.get(this.fileIndex);
		Message selectedMessage = selectedMultimediaFile.getMessage();
		long durationInMilliSeconds = this.mediaPlayer.getMediaPlayer().getLength();
	
		if (durationInMilliSeconds > 0) {
			selectedMessage = findSelectedMessageFromPosition(selectedMessage, selectedMultimediaFile.getIndexInParcoursList(), this.mediaPlayer.getMediaPlayer().getPosition() * durationInMilliSeconds);
		}
		
		ActivatorData.getInstance().setSelectedMsg(selectedMessage);
		
		((MessageSelection) this.currentSelection).setUserSentSelection(true);
		((MessageSelection) this.currentSelection).setMessageId(selectedMessage.getMessageId());
		fireSelectionChanged(this.currentSelection);
		((MessageSelection) this.currentSelection).setUserSentSelection(false);
	}
	
	private Message findSelectedMessageFromPosition(Message selectedMessage, int indexInParcoursList, float position) {
		Message newSelectedMessage = selectedMessage;
		
		long startAbsoluteTime = selectedMessage.getAbsoluteTime();
		Float gap = null;
		
		ListIterator<Message> it = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages().listIterator(indexInParcoursList);

		while (it.hasNext()) {
			Message currentMessage = it.next();
			
			long currentOffset = currentMessage.getAbsoluteTime() - startAbsoluteTime;
			float currentGap = position - currentOffset;
			
			if (currentGap > 0) {
				newSelectedMessage = currentMessage;
				gap = currentGap;
			} else {
				if (gap != null && Math.abs(currentGap) < Math.abs(gap)) {
					newSelectedMessage = currentMessage;
				}
				
				break;
			}
		}
		
		return newSelectedMessage;
	}
	
	private void createMediaPlayer() {		
		// The frame
		new NativeDiscovery().discover();
		this.mediaFrame = SWT_AWT.new_Frame(mediaPlayerTabFolder);
		
		this.mediaPlayer = new EmbeddedMediaPlayerComponent();
						
		this.mediaFrame.add(mediaPlayer);
		this.mediaFrame.setVisible(false);
		this.mediaFrame.pack();
						
		createMediaPlayerControls();
		createUpdateScaleRunnable();
		
		prepareMedia();
	}
		
	private void createMediaPlayerControls() {						
		this.mediaPlayerControlScrolledComposite = new ScrolledComposite(this.mediaPlayerTabFolder, SWT.H_SCROLL | SWT.V_SCROLL);		
		this.mediaPlayerControlScrolledComposite.setLayout(new RowLayout(SWT.VERTICAL));
		this.mediaPlayerControlScrolledComposite.setBackground(TAB_BACKGROUND_COLOR);
		this.mediaPlayerControlScrolledComposite.setExpandHorizontal(true);
		this.mediaPlayerControlScrolledComposite.setExpandVertical(true);
		this.mediaPlayerControlScrolledComposite.setMinSize(PLAYER_MIN_WIDTH, PLAYER_MIN_HEIGHT);
		
		this.mediaPlayerControlGridComposite = new Composite(this.mediaPlayerControlScrolledComposite, SWT.NONE);
		this.mediaPlayerControlGridComposite.setBackground(TAB_BACKGROUND_COLOR);
		
		GridLayout mediaPlayerGridLayout = new GridLayout();
		mediaPlayerGridLayout.marginHeight = 0;
		mediaPlayerGridLayout.marginWidth = 0;
		mediaPlayerGridLayout.horizontalSpacing = 0;
		mediaPlayerGridLayout.verticalSpacing = 0;
		
		this.mediaPlayerControlGridComposite.setLayout(mediaPlayerGridLayout);
		
		this.mediaPlayerControlComposite = new Composite(this.mediaPlayerControlGridComposite, SWT.NONE);
		this.mediaPlayerControlComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, true, 0, 0));
		
		GridLayout mediaPlayerControlGridLayout = new GridLayout();
		mediaPlayerControlGridLayout.marginHeight = 0;
		mediaPlayerControlGridLayout.marginWidth = 0;
		mediaPlayerControlGridLayout.horizontalSpacing = 0;
		mediaPlayerControlGridLayout.verticalSpacing = 0;
		
		this.mediaPlayerControlComposite.setLayout(mediaPlayerControlGridLayout);		

		this.topControlButtonsComposite = new Composite(this.mediaPlayerControlComposite, SWT.NONE);
		this.bottomControlButtonsComposite = new Composite(this.mediaPlayerControlComposite, SWT.NONE);
		this.navigateControlButtonsComposite = new Composite(this.bottomControlButtonsComposite, SWT.NONE);
		this.volumeControlButtonsComposite = new Composite(this.bottomControlButtonsComposite, SWT.NONE);
		this.timeControlComposite = new Composite(this.bottomControlButtonsComposite, SWT.NONE);
		
		GridLayout mediaPlayerTopControlGridLayout = new GridLayout();
		mediaPlayerTopControlGridLayout.marginHeight = 0;
		mediaPlayerTopControlGridLayout.marginWidth = PLAYER_SPACING;
		mediaPlayerTopControlGridLayout.horizontalSpacing = 0;
		mediaPlayerTopControlGridLayout.verticalSpacing = 0;
		mediaPlayerTopControlGridLayout.marginTop = PLAYER_SPACING / 2;
		mediaPlayerTopControlGridLayout.marginBottom = PLAYER_SPACING / 2;
		
		this.topControlButtonsComposite.setLayout(mediaPlayerTopControlGridLayout);
		GridData topControlButtonsGridData = new GridData(SWT.FILL, SWT.FILL, true, true, 0, 0);
		this.topControlButtonsComposite.setLayoutData(topControlButtonsGridData);
		
		GridLayout mediaPlayerBottomControlGridLayout = new GridLayout();
		mediaPlayerBottomControlGridLayout.marginHeight = 0;
		mediaPlayerBottomControlGridLayout.marginWidth = PLAYER_SPACING;
		mediaPlayerBottomControlGridLayout.horizontalSpacing = PLAYER_SPACING;
		mediaPlayerBottomControlGridLayout.verticalSpacing = 0;
		mediaPlayerBottomControlGridLayout.marginTop = PLAYER_SPACING / 2;
		mediaPlayerBottomControlGridLayout.marginBottom = PLAYER_SPACING / 2;
		mediaPlayerBottomControlGridLayout.numColumns = 3;
		mediaPlayerBottomControlGridLayout.makeColumnsEqualWidth = true;
		
		this.bottomControlButtonsComposite.setLayout(mediaPlayerBottomControlGridLayout);
		GridData bottomControlButtonsGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		this.bottomControlButtonsComposite.setLayoutData(bottomControlButtonsGridData);
		
		GridLayout navigateControlGridLayout = new GridLayout();
		navigateControlGridLayout.horizontalSpacing = PLAYER_SPACING;
		navigateControlGridLayout.numColumns = 3;
		this.navigateControlButtonsComposite.setLayout(navigateControlGridLayout);
		
		GridLayout volumeControlGridLayout = new GridLayout();
		volumeControlGridLayout.marginWidth = PLAYER_SPACING;
		volumeControlGridLayout.numColumns = 2;
		this.volumeControlButtonsComposite.setLayout(volumeControlGridLayout);
		this.volumeControlButtonsComposite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
			
		this.timeControlComposite.setLayout(new GridLayout());
		this.timeControlComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, true));
		
		this.positionScale = new Scale(this.topControlButtonsComposite, SWT.NONE);
		this.positionScale.setToolTipText(POSITION_SCALE_LABEL);
		this.positionScale.setMinimum(0);
		this.positionScale.setMaximum(POSITION_SCALE_STEP_NUMBER);
		this.positionScale.setSelection(0);
		GridData positionScaleGridData = new GridData(SWT.FILL, SWT.CENTER, true, true);		
		this.positionScale.setLayoutData(positionScaleGridData);
		
		this.playPauseButton = new Button(this.navigateControlButtonsComposite, SWT.CENTER);
		
		this.previousButton = new Button(this.navigateControlButtonsComposite, SWT.CENTER);
		this.previousButton.setImage(ActivatorVueMultimedia.getImageDescriptor("/icons/media_player/control_start.png").createImage());
		this.previousButton.setToolTipText(PREVIOUS_BUTTON_LABEL);
		
		this.nextButton = new Button(this.navigateControlButtonsComposite, SWT.CENTER);
		this.nextButton.setImage(ActivatorVueMultimedia.getImageDescriptor("/icons/media_player/control_end.png").createImage());
		this.nextButton.setToolTipText(NEXT_BUTTON_LABEL);
				
		this.volumeImage = new Label(this.volumeControlButtonsComposite, SWT.CENTER);
		this.volumeImage.setImage(ActivatorVueMultimedia.getImageDescriptor("/icons/media_player/sound.png").createImage());
		
		this.volumeScale = new Scale(this.volumeControlButtonsComposite, SWT.CENTER);
		this.volumeScale.setToolTipText(VOLUME_SCALE_LABEL);
		this.volumeScale.setMinimum(0);
		this.volumeScale.setMaximum(VOLUME_SCALE_STEP_NUMBER);
		this.volumeScale.setSelection(VOLUME_SCALE_DEFAULT_STEP_VALUE);
		
		setVolumeFromVolumeScale();
		
		this.timeLabel = new Label(this.timeControlComposite, SWT.CENTER);
		FontDescriptor fd = FontDescriptor.createFrom(this.timeLabel.getFont()).setHeight(11);
		Font font = fd.createFont(this.timeLabel.getDisplay());
		this.timeLabel.setFont(font);
		
		
		this.mediaPlayerControlScrolledComposite.setContent(this.mediaPlayerControlGridComposite);
		this.mediaPlayerTabItem.setControl(this.mediaPlayerControlScrolledComposite);
		
		this.playPauseButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				String toolTipText = playPauseButton.getToolTipText();
				
				if (toolTipText.equals(PLAY_BUTTON_LABEL)) {
					start();
				} else if (toolTipText.equals(PAUSE_BUTTON_LABEL)) {
					pause();
				}
			}
		});
		
		this.previousButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (fileIndex > 0) {
					fileIndex--;
					reload();
					filesTable.setSelection(fileIndex);
				}
			}
		});
		
		this.nextButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				if (multimediaFiles.size() > fileIndex + 1) {
					fileIndex++;
					reload();
					filesTable.setSelection(fileIndex);
				}
			}
		});
				
		this.volumeScale.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setVolumeFromVolumeScale();
			}
		});
		
		this.positionScale.addMouseListener(new MouseListener() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {				
			}

			@Override
			public void mouseDown(MouseEvent e) {
				if (mediaPlayer.getMediaPlayer().isPlaying()) {
                    mediaPlayerOnPause = true;
                    pause();
                }
			}

			@Override
			public void mouseUp(MouseEvent e) {
				setPositionFromPositionScale();
				
				if (mediaPlayerOnPause) {
					start();
					mediaPlayerOnPause = false;
				}
			}
			
		});
	}
	
	public void createUpdateScaleRunnable() {
		final UpdateScaleRunnable updateScale = new UpdateScaleRunnable(this.mediaPlayer.getMediaPlayer());
		
		this.updateScaleActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Display.getDefault().syncExec(updateScale);
			}
	    };
		
		this.updateScaleTimer = new Timer(UPDATE_SCALE_DELAY_INTERVAL, this.updateScaleActionListener);
	}
	
	/** Updates on IHM **/
	
	private void initPlayButton() {
		if (this.playPauseButton != null) {
			this.playPauseButton.setImage(ActivatorVueMultimedia.getImageDescriptor("/icons/media_player/control_play.png").createImage());
			this.playPauseButton.setToolTipText(PLAY_BUTTON_LABEL);
		}
	}
	
	private void initPauseButton() {
		if (this.playPauseButton != null) {
			this.playPauseButton.setImage(ActivatorVueMultimedia.getImageDescriptor("/icons/media_player/control_pause.png").createImage());
			this.playPauseButton.setToolTipText(PAUSE_BUTTON_LABEL);
		}
	}
	    
    private void updateScalePosition(int value) {
    	if (!this.positionScale.isDisposed()) {
        	this.positionScale.setSelection(value);
    	}
    }
    
    private void updateScalePosition() {
    	updateScalePosition((int) (this.mediaPlayer.getMediaPlayer().getPosition() * POSITION_SCALE_STEP_NUMBER));
    }
    
    private void updateTimeLabel(long milliseconds) {
    	if (!this.timeLabel.isDisposed()) {
    		MultimediaFile currentFile = this.multimediaFiles.get(this.fileIndex);
        	if (currentFile.isPlayable()) {
        		this.timeLabel.setText(String.format("%s / %s", MultimediaFile.durationtoString(this.mediaPlayer.getMediaPlayer().getTime()), totalTimeForCurrentFile));
        	} else {
        		this.timeLabel.setText("00:00:00 / 00:00:00");
        	}
    	}
    }
    
    private void updateTimeLabel() {
    	updateTimeLabel(this.mediaPlayer.getMediaPlayer().getTime());
    }
    	
	/** Media Player management **/
	
	private void reload() {
		stop();
		prepareMedia();
	}
    
	private void stop() {
		this.mediaPlayer.getMediaPlayer().stop();
		this.updateScaleTimer.stop();
	}
	
	private void prepareMedia() {
		if (this.multimediaFiles != null) {
			MultimediaFile currentFile = this.multimediaFiles.get(this.fileIndex);
			
			if (currentFile.isPlayable()) {
				this.mediaPlayerTabItem.setText(FILE_LABEL_LABEL + " " + currentFile.getFile().getName());
			} else {
				this.mediaPlayerTabItem.setText(FILE_LABEL_LABEL + " " + FILE_CORRUPTED + " " + currentFile.getFile().getName());
			}
			
			this.mediaPlayer.getMediaPlayer().prepareMedia(currentFile.getFile().getAbsolutePath());
			this.playPauseButton.setEnabled(currentFile.isPlayable());
			this.previousButton.setEnabled(this.fileIndex > 0);
			this.nextButton.setEnabled(this.fileIndex < this.multimediaFiles.size() - 1);
			this.totalTimeForCurrentFile = currentFile.getDurationToString();
			
			this.positionScale.setEnabled(false);
			this.volumeScale.setEnabled(false);
			updateScalePosition(0);
			updateTimeLabel(0);
			initPlayButton();
		}
	}
	
	private void start() {
		this.mediaPlayer.getMediaPlayer().start();
		this.updateScaleTimer.start();
		
		if (!this.mediaPlayerOnPause) {
			this.positionScale.setEnabled(true);
			this.volumeScale.setEnabled(true);
		}
		
		initPauseButton();
	}
		
	private void pause() {
		this.mediaPlayer.getMediaPlayer().pause();		
		this.updateScaleTimer.stop();
		initPlayButton();
	}
	
	private void setVolumeFromVolumeScale() {
		this.mediaPlayer.getMediaPlayer().setVolume(this.volumeScale.getSelection());
	}
		
	private void setPositionFromPositionScale() {
		this.mediaPlayer.getMediaPlayer().setPosition((float) this.positionScale.getSelection() / (float) POSITION_SCALE_STEP_NUMBER);
	}
	
	private void setPositionFromOffset(long offset) {
		float proportion = ((float) offset) / ((float) this.mediaPlayer.getMediaPlayer().getLength());
		
		if (proportion > 1) {
			proportion = 1;
		}
		
		this.mediaPlayer.getMediaPlayer().setPosition(proportion);
	}
		
	/** On dispose **/
    
	public void dispose() {		
		this.multimediaFiles = null;
		
		this.updateScaleTimer.stop();
		this.updateScaleTimer.removeActionListener(updateScaleActionListener);
		
		stop();
		this.mediaPlayer.getMediaPlayer().release();
		this.mediaPlayer.release();
		this.mediaFrame.remove(this.mediaPlayer);
		this.mediaPlayer = null;
		
		super.dispose();
		ActivatorData.getInstance().removeDataListener(this);
	}
	
	/** UpdateScaleRunnable **/
	
	private final class UpdateScaleRunnable implements Runnable {
	    private final MediaPlayer mediaPlayer;

	    private UpdateScaleRunnable(MediaPlayer mediaPlayer) {
	        this.mediaPlayer = mediaPlayer;
	    }

	    @Override
	    public void run() {
	    	try {
	            if (mediaPlayer.isPlaying()) {
		    		updateTimeLabel();
	            	updateScalePosition();
	            } else {
	            	reload();
	            }
	    	} catch (Error e) {
	    		// Do nothing
	    	}
	    }
	}

	/** Print **/
	
	@Override
	public Composite getContenu() {
		return this.sashForm;
	}
}