package com.faiveley.samng.principal.ihm.vues.search;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.calcul.TailleBouton;
import com.faiveley.samng.principal.ihm.listeners.ISearchInFileExecutorListener;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveAccueil;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ChargeurParcours;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.AdaptateurParseurXML;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.search.Result;
import com.faiveley.samng.principal.sm.search.SearchData;
import com.faiveley.samng.principal.sm.search.SearchInFileUtil;
import com.faiveley.samng.principal.sm.search.SearchInFoldersExecutor;

/**
 * Search in file dialog
 * @author meggy
 *
 */
public class SearchInFile extends ASearchVariableDialog2 implements ISearchInFileExecutorListener {

	public static final String cheminXML=RepertoiresAdresses.xml;

	//fenetre d'affichage des r�sultats
	private Shell shellInfo;
	private Label labelShellInfo;
	
	//contant values for return of the open dialog
	public static int RET_RELEASE = 0;
	public static int RET_LOAD = 1;

	//contant strings
	private static final String SEARCH = Messages.getString("SearchInFile.0"); //$NON-NLS-1$
	private static final String STOP = Messages.getString("SearchInFile.1"); //$NON-NLS-1$
	private static final String OK = Messages.getString("SearchInFile.27"); //$NON-NLS-1$
	private static final String CANCEL = Messages.getString("SearchInFile.28"); //$NON-NLS-1$
	private static final String SEARCH_IN = Messages.getString("SearchInFile.2"); //$NON-NLS-1$
	private static final String INCL_SUBFOLDERS = Messages.getString("SearchInFile.3"); //$NON-NLS-1$
	private static final String SEARCH_FOLDERS = Messages.getString("SearchInFile.4"); //$NON-NLS-1$
	private static final String XML_SELECT = Messages.getString("SearchInFile.5"); //$NON-NLS-1$
	private static final String SUBFOLDERS = "..."; //$NON-NLS-1$

	//constant value for width of the column
	private static final int WIDTH = 150; 

	//table
	protected Table tableResults = null;
	private TableViewer viewer = null;

	//file
	protected File selectedFile = null;
	protected boolean checkSubfolders = false;
	protected boolean selectDirectory = false;

	//flag to say if the xml was loaded
	boolean isXmlLoaded = false;

	//flag to say 
	protected boolean shouldReleaseData = false;

	private SearchData dataSearch = null;

	//search executor
	protected SearchInFoldersExecutor executor = null;

	//controls
	protected Button buttonSearch = null;
	protected Button buttonStop = null;

	protected Button buttonSearchFolder;
	protected Button buttonSearchIn;
	protected Button buttonCheckIncludeSubfolder;

	protected Button buttonOk;
	protected Button buttonCancel;

	private String xmlFileName = null;


	/**
	 * Constructor
	 * @param parent
	 */
	public SearchInFile(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * Constructor
	 * @param parent
	 * @param style
	 */
	public SearchInFile(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Creates the controls in the current dialog
	 */
	@Override
	public  void createControls(final Shell par){
		super.createControls(par);
		
		this.comboVar.setEnabled(false);
		this.comboVar.addListener(SWT.Selection, new Listener(){

			public void handleEvent(Event arg0) {
				if(arg0.type==SWT.Selection){
					if(SearchInFile.this.comboVar.getItem(SearchInFile.this.comboVar.getSelectionIndex()).equals(NO_VARIABLE)){
						if(buttonSearch.isEnabled())
							buttonSearch.setEnabled(false);
					}
					else{
						if(SearchInFile.this.selectedFile!=null)
							if(!buttonSearch.isEnabled())
								buttonSearch.setEnabled(true);
					}
				}
			}
		});

		this.comboOperation.setEnabled(false);
		this.textValue.setEnabled(false);

		//button "Load xml"

		Button buttonXml = new Button(this.parent, SWT.NONE);
		int xmlWidth=TailleBouton.CalculTailleBouton(XML_SELECT.length());
		buttonXml.setBounds(new Rectangle(418, 80, xmlWidth, 23));			//(480, 75, 76, 23)
		buttonXml.setText(XML_SELECT);
		buttonXml.setToolTipText((XML_SELECT));

		//when clicking the "Load XML" button
		buttonXml.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				//creates a file dialog to open the binary files 
				FileDialog dialog = new FileDialog(SearchInFile.this.parent, SWT.OPEN );

				//set the posible extensions of the binary files : ".tbf" and ".cbf"
				dialog.setFilterExtensions(new String[] { "*.xml"}); //$NON-NLS-1$

				dialog.setFilterPath(cheminXML);

				dialog.setFilterNames(new String[] { "*(*.xml)"}); //$NON-NLS-1$
				String fileName = dialog.open();
				if (fileName != null) {
					xmlFileName = new File(fileName).getName();

					boolean xmlAssocieValide = AdaptateurParseurXML.verifierValiditeFichierXmlAssocie(fileName);


					if(xmlAssocieValide){
						AdaptateurParseurXML.definirParseurXML(fileName);
						new SearchInFileUtil().loadXml(fileName);
					}
					else{
						MessageBox msgBox2 = new MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING | SWT.OK);
						msgBox2.setText(Messages.getString("SearchInFile.29")); //$NON-NLS-1$
						msgBox2.setMessage(Messages.getString("SearchInFile.29")); //$NON-NLS-1$
						msgBox2.open();
					}
					updateGUIAfterLoadXml(fileName);
				} 
			}
		});

		//button search
		this.buttonSearch = new Button(this.parent, SWT.NONE);
		int widthSearch=TailleBouton.CalculTailleBouton(SEARCH.length());
		this.buttonSearch.setBounds(new Rectangle(418, 110, widthSearch, 23));			//(480, 110, 76, 23)
		this.buttonSearch.setText(SEARCH);
		this.buttonSearch.setToolTipText((SEARCH));
		this.buttonSearch.setEnabled(false);

		//when pressing "Search"
		this.buttonSearch.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				//create and start the executor
				SearchInFile.this.executor = new SearchInFoldersExecutor(SearchInFile.this,xmlFileName);
				SearchInFile.this.executor.run();
			}
		});

		//button Stop
		this.buttonStop = new Button(this.parent, SWT.NONE);
		int widthStop=TailleBouton.CalculTailleBouton(SEARCH.length());
		this.buttonStop.setBounds(new Rectangle(418, 140, widthStop, 23));				//(480, 140, 76, 23)
		this.buttonStop.setText(STOP);
		this.buttonStop.setToolTipText((STOP));
		this.buttonStop.setEnabled(false);

		//when pressing "Stop"
		this.buttonStop.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				if (SearchInFile.this.executor != null) {
					SearchInFile.this.executor.cancel();
				}
				SearchInFile.this.buttonSearch.setEnabled(true);
				SearchInFile.this.buttonStop.setEnabled(false);
			}
		});

		//Bouton Ok
		this.buttonOk = new Button(this.parent, SWT.NONE);
		int widthOk=TailleBouton.CalculTailleBouton(OK.length());
		this.buttonOk.setBounds(new Rectangle(418, 400, widthOk, 23));	
		this.buttonOk.setText(OK);
		this.buttonOk.setToolTipText((OK));
		this.buttonOk.setEnabled(false);
		//when pressing "Ok"
		this.buttonOk.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				TableItem[] items = SearchInFile.this.tableResults.getSelection();
				try {
					Result result = (Result)items[0].getData();
					String fileName = new File(result.getValue(1),  result.getValue(0)).getAbsolutePath();

					ChargeurParcours.loadBinaryFile(fileName,0,-1,false);
					ChargeurParcours.initializePools(fileName,true);
					SearchInFile.this.shouldReleaseData = false;
					parent.close();

					IWorkbenchWindow window=Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
					String DefaultView = PerspectiveAccueil.getID();
					IPerspectiveDescriptor p = window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(DefaultView); 
					window.getActivePage().setPerspective(p);
				} catch (AExceptionSamNG e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		//Bouton Cancel
		this.buttonCancel = new Button(this.parent, SWT.NONE);
		int widthCancel=TailleBouton.CalculTailleBouton(CANCEL.length());
		this.buttonCancel.setBounds(new Rectangle(418+widthOk, 400, widthCancel, 23));	
		this.buttonCancel.setText(CANCEL);
		this.buttonCancel.setToolTipText((CANCEL));
		this.buttonCancel.setEnabled(true);
		//when pressing "Cancel"
		this.buttonCancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				parent.close();
			}
		});

		//the "Search In"
		Label label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(20, 92, 54, 13));
		label.setText(SEARCH_IN);
		label.setToolTipText((SEARCH_IN));

		final Text textSearchIn = new Text(this.parent, SWT.BORDER);
		textSearchIn.setBounds(new Rectangle(75, 90, 270, 19));
		textSearchIn.setEnabled(false);

		this.buttonSearchIn = new Button(this.parent, SWT.NONE);
		this.buttonSearchIn.setBounds(new Rectangle(355, 88, 33, 23));
		this.buttonSearchIn.setText(SUBFOLDERS);
		this.buttonSearchIn.setToolTipText((SUBFOLDERS));
		this.buttonSearchIn.setEnabled(false);
		this.buttonSearchIn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				if (SearchInFile.this.selectDirectory) {
					DirectoryDialog dlg = new DirectoryDialog(SearchInFile.this.parent, SWT.NONE);

					// Set the initial filter path according
					// to anything they've selected or typed in
					//DR28_CL36 
					dlg.setFilterPath(RepertoiresAdresses.RepertoireTravail);
//					dlg.setFilterPath(RepertoiresAdresses.INSTALL_SAM_PARAM);

					// Change the title bar text
					dlg.setText(Messages.getString("SearchInFile.9")); //$NON-NLS-1$

					// Customizable message displayed in the dialog
					dlg.setMessage(Messages.getString("SearchInFile.10")); //$NON-NLS-1$

					// Calling open() will open and run the dialog.
					// It will return the selected directory, or
					// null if user cancels
					String dir = dlg.open();
					if (dir != null) {
						// Set the text box to the new selection
						textSearchIn.setText(dir);
						//set the "Search" button enable
						SearchInFile.this.buttonSearch.setEnabled(true);
						SearchInFile.this.selectedFile = new File(dir);
					} else {
						SearchInFile.this.selectedFile = null;
					}
				} else {
					//creates a file dialog to open the binary files 
					FileDialog dialog = new FileDialog(SearchInFile.this.parent, SWT.OPEN );

					//set the posible extensions of the binary files : ".tbf" and ".cbf"
					//					String formats = ("*.tbf" + ";" 
					//					+ "*.cbf" + ";" 
					//					+ "*.ltb" + ";" 
					//					+ "*.lkb" + ";" 
					//					+ "*.lwb" + ";" 
					//					+ "*.ftb" + ";" 
					//					+ "*.lpb" + ";" 
					//					+ "*.fkb").toString(); 

					List <String> extensions = BridageFormats.getInstance().getExtensionsValides(); 
					String formats="";
					int nbExt=extensions.size();
					List <String> extvues=new ArrayList<String>();
					if (nbExt>0) {
						formats=formats+extensions.get(0);
						extvues.add(extensions.get(0));
						for (int i = 0; i < nbExt; i++) {
							boolean alreadyWritten=false;
							for (int j = 0; j < nbExt; j++) {							
								if (extvues.get(j)!=null && extensions.get(i).equalsIgnoreCase(extvues.get(j))) {
									alreadyWritten=true;
								} 
							}
							if (!alreadyWritten) {
								formats=formats+extensions.get(i);
							}
						}
					}

					dialog.setFilterExtensions(new String[] { formats,"*.*" });
					//DR28_CL36 
					dialog.setFilterPath(RepertoiresAdresses.RepertoireTravail);
					dialog.setFilterNames(new String[] {
							com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ActionFichierOuvrir.3") 
							//							+ " (*.tbf,*.cbf,*.ltb,*.lkb,*.lwb,*.ftb,*.fkb,*.lpb)"
							+ " ("+formats.replace(';','.')  +")"
							, com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ActionFichierOuvrir.5") + " (*.*)" });
					String fileName = dialog.open();
					if (fileName != null) {
						//if have a file selected then enable "Search" and set the "Text"
						textSearchIn.setText(fileName);
						if(!SearchInFile.this.comboVar.getItem(SearchInFile.this.comboVar.getSelectionIndex()).equals(NO_VARIABLE))
							SearchInFile.this.buttonSearch.setEnabled(true);
						SearchInFile.this.selectedFile = new File(fileName);
					} else {
						SearchInFile.this.selectedFile = null;
					}
				}
			}
		});

		//"Search Folders"
		label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(50, 125, 130, 13));
		label.setText(SEARCH_FOLDERS);

		//checkbox "Search Folder" 
		this.buttonSearchFolder = new Button(this.parent, SWT.CHECK);
		this.buttonSearchFolder.setBounds(new Rectangle(20, 125, 13, 16));
		this.buttonSearchFolder.setSelection(true);
		this.buttonSearchFolder.setEnabled(false);
		this.selectDirectory = this.buttonSearchFolder.getSelection();

		//checkbox "Include subfolders"
		label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(50, 145, 130, 13));
		label.setText(INCL_SUBFOLDERS);

		this.buttonCheckIncludeSubfolder = new Button(this.parent, SWT.CHECK);
		this.buttonCheckIncludeSubfolder.setBounds(new Rectangle(20, 145, 13, 16));
		this.buttonCheckIncludeSubfolder.setEnabled(false);

		this.buttonSearchFolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				SearchInFile.this.selectDirectory = SearchInFile.this.buttonSearchFolder.getSelection();
				SearchInFile.this.buttonCheckIncludeSubfolder.setEnabled(SearchInFile.this.selectDirectory);
			}
		});

		this.buttonCheckIncludeSubfolder.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				SearchInFile.this.checkSubfolders = SearchInFile.this.buttonCheckIncludeSubfolder.getSelection();
			}
		});

		//table of results
		createTable();
	}


	/**
	 * Creates the table of results
	 *
	 */
	private void createTable() {
		this.tableResults = new Table(this.parent, SWT.FULL_SELECTION | SWT.BORDER| SWT.VIRTUAL);
		this.tableResults.setHeaderVisible(true);
		this.tableResults.setLinesVisible(true);

		this.tableResults.setBounds(new Rectangle(20, 175, 540, 220));

		TableColumn tc = new TableColumn(this.tableResults, SWT.NONE);
		tc.setText(Messages.getString("SearchInFile.15")); //$NON-NLS-1$
		tc.setToolTipText((Messages.getString("SearchInFile.15")));    
		tc.setWidth(WIDTH);

		tc = new TableColumn(this.tableResults, SWT.NONE);
		tc.setText(Messages.getString("SearchInFile.16")); //$NON-NLS-1$
		tc.setToolTipText((Messages.getString("SearchInFile.16")));
		tc.setWidth(WIDTH);

		tc = new TableColumn(this.tableResults, SWT.NONE);
		tc.setText(Messages.getString("SearchInFile.17")); //$NON-NLS-1$
		tc.setToolTipText((Messages.getString("SearchInFile.17")));
		tc.setWidth(WIDTH);

		tc = new TableColumn(this.tableResults, SWT.NONE);
		tc.setText(Messages.getString("SearchInFile.18")); //$NON-NLS-1$
		tc.setToolTipText((Messages.getString("SearchInFile.18")));
		tc.setWidth(WIDTH);

		tc = new TableColumn(this.tableResults, SWT.NONE);
		tc.setText(Messages.getString("SearchInFile.19")); //$NON-NLS-1$
		tc.setToolTipText((Messages.getString("SearchInFile.19")));
		tc.setWidth(WIDTH);

		this.viewer = new TableViewer(this.tableResults);

		//set the content and label providers
		this.viewer.setContentProvider(new MyContentProvider());
		this.viewer.setLabelProvider(new MyLabelProvider());

		if (TableResultats.getInstance().getResults()!=null) {
			List<Result> results=TableResultats.getInstance().getResults();
			setTableInput(results.toArray(new Result[results.size()]) );
		}

		this.tableResults.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
			public void widgetSelected(SelectionEvent arg0) {
				buttonOk.setEnabled(true);
			}
		});
		this.tableResults.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
				//when double click on a result this should load the selected binary file
				TableItem[] items = SearchInFile.this.tableResults.getSelection();
				try {
					Result result = (Result)items[0].getData();
					String fileName = new File(result.getValue(1),  result.getValue(0)).getAbsolutePath();

					BridageFormats.getInstance().setFormatFichierOuvert(null);
					ChargeurParcours.loadBinaryFile(fileName,0,-1,false);
					ChargeurParcours.initializePools(fileName,true);
					SearchInFile.this.shouldReleaseData = false;
					parent.close();

					//					IWorkbenchWindow window=Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
					//
					//					String DefaultView = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getWorkbench()
					//					.getPerspectiveRegistry().getDefaultPerspective();
					//					IPerspectiveDescriptor p = window.getWorkbench().getPerspectiveRegistry().
					//					findPerspectiveWithId(DefaultView); 
					//window.getActivePage().setPerspective(p);
									
					IWorkbenchWindow window=Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
					String DefaultView = PerspectiveAccueil.getID();
					IPerspectiveDescriptor p = window.getWorkbench().getPerspectiveRegistry().findPerspectiveWithId(DefaultView); 
					window.getActivePage().setPerspective(p);
				} catch (AExceptionSamNG e) {
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				finally{
					//VueWaitBar.getInstance().stop();
				}
			}
		});
	}

	/**
	 * Overrides the open to return a value which says if the last parcours file 
	 * that was loaded by search operation should or should be not released
	 */
	@Override
	public int open() {
		super.open();
		return this.shouldReleaseData ? RET_RELEASE : RET_LOAD;
	}

	/**
	 * Sets the new input in the table
	 * @param results	the input
	 */
	protected void setTableInput(Result[] results) {
		this.viewer.setInput(results);
		this.tableResults.layout();
	}

	/**
	 * Returns the size of the dialog
	 */
	@Override
	protected Point getSize() {
		return new Point(600, 500);
	}

	/**
	 * Updates the status of the controls after the xml is loadded
	 * @param fileName	name of the xml file that was loaded
	 */
	protected void updateGUIAfterLoadXml(String fileName) {
		SearchInFile.this.isXmlLoaded = true;
		this.values.clear();
		this.comboVar.removeAll();

		fillCombo();
		this.parent.setText(Messages.getString("SearchInFile.20") + new File(fileName).getName()); //$NON-NLS-1$

		this.comboVar.setEnabled(true);
		this.comboOperation.setEnabled(true);
		this.textValue.setEnabled(true);

		this.buttonSearchIn.setEnabled(true);
		this.buttonSearchFolder.setEnabled(true);
		this.buttonCheckIncludeSubfolder.setEnabled(true);
	}


	/**
	 * Updates the status of the controls when searching is started/stopped
	 * @param isStarted		the search is started or stopped
	 */
	protected void updateGUIWhenSearching(boolean isStarted) {
		this.buttonSearch.setEnabled(!isStarted);
		this.buttonStop.setEnabled(isStarted);
		this.buttonSearchIn.setEnabled(!isStarted);
		this.buttonSearchFolder.setEnabled(!isStarted);

		if (isStarted) {
			this.tableResults.removeAll();
			this.tableResults.layout();
		}
	}

	/**
	 * ISearchInFileExecutorListener Listener method. Is notified when a search is going to start
	 */
	public boolean onStartExecution() {
		creerShellInfo();
		loadCurrentData();
		if (this.dataSearch == null 
				|| ((this.dataSearch.getOperation()!=null && !this.dataSearch.getOperation().name().equals(" "))  && this.dataSearch.getValue()==null)
				)
		{
			MessageBox msgBox = new MessageBox(this.parent, 
					SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("SearchInFile.22")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}

		//set buttons status when starts a search 
		updateGUIWhenSearching(true);

		this.executor.setCheckSubfolders(this.checkSubfolders);
		this.executor.setRootFileName(this.selectedFile);
		this.executor.setDataToSearch(this.dataSearch);

		//		VueProgressBar progrBar = Activator.getDefault().getProgressBar();

		//		VueWaitBar.getInstance().setRect(SearchInFile.this.parent.getShell().getBounds());
		//		VueWaitBar.getInstance().start(false);		
		return true;
	}

	public void creerShellInfo(){
		Display display = Display.getCurrent();
		shellInfo = new Shell (display);
		shellInfo.setLayout (new RowLayout (SWT.VERTICAL));
		int sizeX=350;
		int sizeY=150;
		shellInfo.setSize(sizeX,sizeY);
		Rectangle rect=Display.getCurrent().getBounds();
		int posX=rect.width/2-sizeX/2;
		int posY=rect.height/2-sizeY/2;
		shellInfo.setLocation(posX,posY+50);
		shellInfo.setText(Messages.getString("rechercheFichier.0"));
		Composite comp=new Composite(shellInfo, SWT.NONE);
		comp.setBounds(0, 0, 200, 50);
		comp.setVisible(true);
		shellInfo.setImage(com.faiveley.samng.principal.ihm.Activator
				.getDefault().getImage("/icons/toolBar/vues_commun_rechercher_signal.png"));
		labelShellInfo=new Label(comp, SWT.NONE);
		labelShellInfo.setBounds(20, 20, 300, 80);
		labelShellInfo.setVisible(true);
	}
	
	/**
	 * ISearchInFileExecutorListener Listener method. Listen for the execution of search to finish
	 */
	@Override
	public void onRefresh(final Result result) {
		this.parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				shellInfo.open();
				labelShellInfo.setText(result.getFileName()+" ...");
			}
		});    	
	}
	
	/**
	 * ISearchInFileExecutorListener Listener method. Listen for the execution of search to finish
	 */
	
	public void onRefresh2(final Result result) {
		this.parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (result != null) {
					Result[] tabRes=new Result[1];
					tabRes[0]=result;
					setTableInput(tabRes);
					SearchInFile.this.viewer.refresh();
					SearchInFile.this.tableResults.layout();
					updateGUIWhenSearching(true);
				}
			}
		});    	
	}

	/**
	 * ISearchInFileExecutorListener Listener method. Listen for the execution of search to finish
	 */
	public void onFinishExecution(final boolean succes) {
		this.parent.getDisplay().asyncExec(new Runnable() {
			public void run() {
				shellInfo.close();
				shellInfo.dispose();
				if (succes) {
					List<Result> results = SearchInFile.this.executor.getResults(); 
					if (results != null && results.size() > 0) {
						setTableInput(results.toArray(new Result[results.size()]) );
						TableResultats.getInstance().setResults(results);
					} else {
						MessageBox msgBox = new MessageBox(SearchInFile.this.parent, SWT.ICON_WARNING | SWT.OK);
						msgBox.setText(""); //$NON-NLS-1$
						msgBox.setMessage(Messages.getString("SearchInFile.24")); //$NON-NLS-1$
						msgBox.open();
					}
				} else {
					MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), 
							SWT.ICON_WARNING | SWT.OK);
					msgBox.setText(""); //$NON-NLS-1$
					msgBox.setMessage(Messages.getString("SearchInFile.26")); //$NON-NLS-1$
					msgBox.open();
				}

				//set buttons status when finished a search
				updateGUIWhenSearching(false);
			}

		});    	
	}


	/**
	 * Loads the current data to be searched in a SearchData object
	 *
	 */
	protected void loadCurrentData() {
		this.dataSearch = new SearchData();
		//get variable
		String varName = SearchInFile.this.comboVar.getText();
		if ((!varName.equals(NO_VARIABLE)) && (!varName.equals(ADV_SEARCH))) {
			this.dataSearch.setDescriptorVariable(SearchInFile.this.values.get(varName));
		}

		//get operation
		for (Operation o : EnumSet.range(Operation.NoOperation, Operation.ShiftLeft)) {
			if (o.value().equals(SearchInFile.this.comboOperation.getText())) {
				this.dataSearch.setOperation(o);
				break;
			}
		}

		//get value
		String val = null;
		if (SearchInFile.this.textValue instanceof Text) {
			val = ((Text)SearchInFile.this.textValue).getText();
			//			try {
			//				Double.parseDouble(val);
			//
			//			} catch (Exception ex) {
			//				val = null;
			//			}
			
			// Pour rep�rer une variable de type NID_XX, il faut regarder si la variable est de 
			// type discrete, et qu'elle possede une table de label contenant le label "$retirer$".
			// La valeur associ�e a ce label doit �tre retir�e de la valeur lors de son affichage.
			// A contrario, cette valeur doit �tre ajout�e pour les traitements.
			if(dataSearch.getDescriptorVariable()!=null){
				if (dataSearch.getDescriptorVariable().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					TableValeurLabel valeurLabel = ((DescripteurVariableDiscrete) dataSearch.getDescriptorVariable()).getLabels();
					if (valeurLabel != null) {
						List<LabelValeur> listeLabelvaleur = valeurLabel.get(Activator.getDefault().getCurrentLanguage());
						if (listeLabelvaleur != null) {									
							int i = 0;
							
							// Si la table de label est une table de suppression de caract�re...
							// Variable de type NID_XXX. Codage BCD (/4).
							if (listeLabelvaleur.get(0).getLabel().equals("$retirer$")) {
								int bcdSize = (dataSearch.getDescriptorVariable().getTailleBits() / 4) ;
									
								String val2 = null ;
									
								if (val.contains("...")) {
									val2 = val.substring(val.indexOf("...") + "...".length()) ;
									val = val.substring(0, val.indexOf("...")) ;
								}
									
								// Ajout des caract�res retir�s � l'affichage (ex : 'f')
								for (i = val.length() ; i < bcdSize ; i++) {
									val = val + Integer.toString(Integer.parseInt((String) listeLabelvaleur.get(0).getValeurs()), 16) ;										
								}
									
								// Conversion de la cha�ne en valeur
								val = Long.toString(Long.parseLong(val, 16)) ;
									
								if (val2 != null) {
									// Ajout des caract�res retir�s � l'affichage (ex : 'f')
									for (i = val2.length() ; i < bcdSize ; i++) {
										val2 = val2 + Integer.toString(Integer.parseInt((String) listeLabelvaleur.get(0).getValeurs()), 16) ;										
									}
										
									val2 = Long.toString(Long.parseLong(val2, 16)) ;
										
									val = val + "..." + val2 ;
								}
							}
						}
					}
				}
			}
			
		} else {
			val = ((Combo)SearchInFile.this.textValue).getText();
			if (val.trim().equals("")) { //$NON-NLS-1$
				val = null;
			}
		}
		this.dataSearch.setValue(val);
	}

	/**
	 * Fills the combo of variables
	 */
	protected void fillCombo() {
		if (this.comboVar.getItemCount() == 0) {
			this.comboVar.add(NO_VARIABLE);
			this.comboVar.add(ADV_SEARCH);
		}

		List<AVariableComposant> vars = Util.getInstance().getAllVariables();
		if (this.values == null) {
			this.values = new LinkedHashMap<String, DescripteurVariable>();
		} else {
			this.values.clear();
			this.comboVar.removeAll();
			this.comboVar.add(NO_VARIABLE);
			this.comboVar.add(ADV_SEARCH);
		}

		if(vars!=null){
			for (AVariableComposant var : vars) {
				if(!(var instanceof VariableDynamique)){
					if (!(var instanceof VariableComposite)) {
						addVariable(var);
					} else {
						addVariableComposee((VariableComposite)var);
					}
				}
			}	
		}
		//ajout de la variable Temps Absolu DR26-3-a CL01
		String str=com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2");
		DescripteurVariable descr=new DescripteurVariable();
		AIdentificateurComposant comp=new AIdentificateurComposant() {};
		descr.setTypeVariable(TypeVariable.VAR_ANALOGIC);
		descr.setM_AIdentificateurComposant(comp);
		comp.setCode(TypeRepere.tempsAbsolu.getCode());
		comp.setNom(str);
		if (!this.values.containsKey(str)) {
			this.comboVar.add(str);
			this.values.put(str, descr);
		}
	}

	/**
	 * The content provider for the table of results
	 * @author meggy
	 *
	 */
	class MyContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			return (Object[])inputElement;
		}

		public void dispose() {}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {}

	}

	/**
	 * The lavel provider for the table of results
	 * @author meggy
	 *
	 */
	class MyLabelProvider extends LabelProvider 
	implements ITableLabelProvider {


		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/* (non-Javadoc)
		 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			Result res = ((Result)element);
			String str = ""; //$NON-NLS-1$
			if(columnIndex >= 0 && res != null && columnIndex <= res.getNoValues() ) {
				str = res.getValue(columnIndex);
			}
			return str;
		}				
	}

	@Override
	public void desactiverBoutonsRecherche() {


	}

	@Override
	public void activerBoutonsRecherche() {


	}

	public void fermerVues(){

		try{

			((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).setNomFichierParcoursBinaire(null);

			if(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getPerspective().getId().equals(PerspectiveAccueil.getID()))
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().savePerspective();

		}
		catch(Exception ex){

		}

		Activator.getDefault().hideAllViews();
		//Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().closeAllPerspectives(false, true);

		ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
	}

	public Label getLabelShellInfo() {
		return labelShellInfo;
	}

	public void setLabelShellInfo(Label labelShellInfo) {
		this.labelShellInfo = labelShellInfo;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10,590,504"
