package com.faiveley.samng.principal.ihm.vues.configuration;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;

/**
 * @author Cosmin Udroiu
 */
public class ColonnesConfigDialog extends Dialog {
	private static int WIDTH_COL_POS = 1;
	
	private String invalidColumnWidthMessage = Messages.getString("ColonnesConfigDialog.0");
	private Composite main;
	
	private boolean listAllColumnsState = true;
	private Button listAllColumnsBtn;
	
	private Table columnsCfgTable;
	
	private Button manualWidthButton;
	private Button automaticWidthButton;
	private Button hideButton;
	private Button displayButton;
	private Composite buttonsOperationsComposite;
	
	private Composite okCancelBtnsComposite;
	private Button cancelBtn;
	private Button okBtn;
	
	private boolean usesShortNames;
	
    protected TableColumn displayStatusColumn;
    protected TableColumn widthColumn;
    protected TableColumn nameColumn;
    private GestionnaireVueListeBase cfgListMng;
    private ConfigurationColonne[] initialColumnConfigurations;
    private int returnValue = SWT.CANCEL;
	public static final String FLAG_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.1");
	public static final String TIME_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.2");
	public static final String REL_TIME_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.3");
	public static final String REL_DIST_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.4");
	public static final String DIST_COR_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.5");
	public static final String TIME_COR_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.6");
	public static final String EVENT_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.13");
	public static final String VAR_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.14");
	public static final String ACC_DIST_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.7");
	
	public ColonnesConfigDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);	
	}
	
	public ColonnesConfigDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	public void setGestionnaireConfiguration(GestionnaireVueListeBase cfgMng) {
		this.cfgListMng = cfgMng;
		this.listAllColumnsState = cfgMng.isListAllColumns();
	}
	
    /**
     * Opens the dialog and returns the input
     *
     * @return String
     */
    public int open(boolean usesShortNames) {
    	this.usesShortNames = usesShortNames;
    	
        // Create the dialog window
        Shell shell = new Shell(getParent(), getStyle());
        shell.setText(getText());
        shell.setToolTipText(getText());
        createContents(shell);
        shell.pack();

        shell.setSize(475, 550);
        
        // Position the dialog in the center of the parent shell
        Rectangle parentBounds = getParent().getBounds();
        Rectangle childBounds = shell.getBounds();
        int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
        int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
        shell.setLocation (x, y);
       
        shell.open();

        Display display = getParent().getDisplay();

        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        // Return the entered value, or null
        return this.returnValue;
    }
    
    /**
     * Creates the dialog's contents
     *
     * @param shell the dialog window
     */
    private void createContents(final Shell shell) {
        shell.setLayout(new FillLayout());
        this.main = new Composite(shell, SWT.None);
		GridLayout mainCompositeLayout = new GridLayout();
		mainCompositeLayout.makeColumnsEqualWidth = true;
		this.main.setLayout(mainCompositeLayout);
		createTopButton();
		createColumnsCfgTable(shell);
		createButtonOperationsPanel(shell);
		createOkCancelButtonsPanel(shell);
    	initTableData();
    }
    
	private void createTopButton() {
		// Create the top button for selecting the list all columns mode
		this.listAllColumnsBtn = new Button(this.main, SWT.CHECK | SWT.LEFT);
		GridData listAllColumnsBtnLData = new GridData();
		listAllColumnsBtnLData.heightHint = 22;
		listAllColumnsBtnLData.grabExcessHorizontalSpace = true;
		listAllColumnsBtnLData.horizontalAlignment = GridData.FILL;
		this.listAllColumnsBtn.setLayoutData(listAllColumnsBtnLData);
		this.listAllColumnsBtn.setText(Messages.getString("ColonnesConfigDialog.1"));
		this.listAllColumnsBtn.setToolTipText(Messages.getString("ColonnesConfigDialog.1"));
		this.listAllColumnsBtn.setSelection(this.listAllColumnsState);
		
		this.listAllColumnsBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				listAllColumnsState = !listAllColumnsState;
				recreateTable();
			}
		});
	}
	
	private void createButtonOperationsPanel(final Shell shell) {
		// Create the panel for the two buttons
		this.buttonsOperationsComposite = new Composite(this.main, SWT.BORDER);
		FormLayout btnsOpsCompLayout = new FormLayout();
		GridData btnsOpsCompLData = new GridData();
		btnsOpsCompLData.grabExcessHorizontalSpace = true;
		btnsOpsCompLData.horizontalAlignment = GridData.FILL;
		btnsOpsCompLData.verticalAlignment = GridData.END;
		btnsOpsCompLData.heightHint = 50;
		this.buttonsOperationsComposite.setLayoutData(btnsOpsCompLData);
		this.buttonsOperationsComposite.setLayout(btnsOpsCompLayout);
		
		// Create the apply button
		this.displayButton = new Button(this.buttonsOperationsComposite, SWT.PUSH);
		FormData displayButtonLData = new FormData();
		displayButtonLData.height = 25;
		displayButtonLData.width = 50;
		
		// Position it near the center of the container panel
		displayButtonLData.right = new FormAttachment(20, -4);
		displayButtonLData.bottom = new FormAttachment(800, 1000, 0);
		this.displayButton.setLayoutData(displayButtonLData);
		this.displayButton.setText(Messages.getString("ColonnesConfigDialog.2"));
		this.displayButton.setToolTipText(Messages.getString("ColonnesConfigDialog.2"));
		
		this.displayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectTableItems(columnsCfgTable.getSelection(), true);
			}
		});

		// Create the close button
		this.hideButton = new Button(this.buttonsOperationsComposite, SWT.PUSH);
		FormData hideButtonLData = new FormData();
		hideButtonLData.height = 25;
		hideButtonLData.width = 50;
		
		// Align the close button to the right of the apply button
		hideButtonLData.left = new FormAttachment(this.displayButton, 4);
		hideButtonLData.bottom = new FormAttachment(800, 1000, 0);
		this.hideButton.setLayoutData(hideButtonLData);
		this.hideButton.setText(Messages.getString("ColonnesConfigDialog.3"));
		this.hideButton.setToolTipText(Messages.getString("ColonnesConfigDialog.3"));
		
		this.hideButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selectTableItems(columnsCfgTable.getSelection(), false);
			}
		});

		// Create the close button
		this.manualWidthButton = new Button(this.buttonsOperationsComposite, SWT.PUSH);
		FormData manualWidthButtonLData = new FormData();
		manualWidthButtonLData.height = 25;
		manualWidthButtonLData.width = 90;
		
		// Align the close button to the right of the apply button
		manualWidthButtonLData.left = new FormAttachment(80, -4);
		manualWidthButtonLData.bottom = new FormAttachment(800, 1000, 0);
		this.manualWidthButton.setLayoutData(manualWidthButtonLData);
		this.manualWidthButton.setText(Messages.getString("ColonnesConfigDialog.4"));
		this.manualWidthButton.setToolTipText(Messages.getString("ColonnesConfigDialog.4"));
		
		this.manualWidthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TableItem[] selItems = columnsCfgTable.getSelection();
				
				if (selItems == null || selItems.length == 0) {
					return;
				}
				
				InputDialog inputDlg = new InputDialog(shell, Messages.getString("ColonnesConfigDialog.5"), Messages.getString("ColonnesConfigDialog.6"), "", null);
				int res = inputDlg.open();
				
				if (res == org.eclipse.jface.window.Window.OK) {
					String value = inputDlg.getValue();
					if (!validateWidthValue(shell, value)) {
						return;
					}
					
					for (TableItem tblItem: selItems) {
						tblItem.setText(1, value);
					}
				}
			}
		});

		// Create the apply button
		this.automaticWidthButton = new Button(this.buttonsOperationsComposite, SWT.PUSH);
		FormData automaticWidthButtonLData = new FormData();
		automaticWidthButtonLData.height = 25;
		automaticWidthButtonLData.width = 120;
		
		// Position it near the center of the container panel
		automaticWidthButtonLData.right =  new FormAttachment(this.manualWidthButton, -4);
		automaticWidthButtonLData.bottom =  new FormAttachment(800, 1000, 0);
		this.automaticWidthButton.setLayoutData(automaticWidthButtonLData);
		this.automaticWidthButton.setText(Messages.getString("ColonnesConfigDialog.10"));
		this.automaticWidthButton.setToolTipText(Messages.getString("ColonnesConfigDialog.10"));
		
		this.automaticWidthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TableItem[] selItems = columnsCfgTable.getSelection();
				
				if (selItems == null || selItems.length == 0) {
					return;
				}
				
				for (TableItem tblItem: selItems) {
					tblItem.setText(1, Messages.getString("ColonnesConfigDialog.11"));
				}
			}
		});
	}
	
	private void createOkCancelButtonsPanel(final Shell shell) {
		// Create the panel for the two buttons
		this.okCancelBtnsComposite = new Composite(this.main, SWT.BORDER);
		FormLayout okCancelBtnsCompLayout = new FormLayout();
		GridData okCancelBtnsCompLData = new GridData();
		okCancelBtnsCompLData.grabExcessHorizontalSpace = true;
		okCancelBtnsCompLData.horizontalAlignment = GridData.FILL;
		okCancelBtnsCompLData.verticalAlignment = GridData.END;
		okCancelBtnsCompLData.heightHint = 50;
		this.okCancelBtnsComposite.setLayoutData(okCancelBtnsCompLData);
		this.okCancelBtnsComposite.setLayout(okCancelBtnsCompLayout);
		
		// Create the apply button
		this.okBtn = new Button(this.okCancelBtnsComposite, SWT.PUSH);
		FormData okBtnLData = new FormData();
		okBtnLData.height = 25;
		okBtnLData.width = 50;
		
		// Position it near the center of the container panel
		okBtnLData.right = new FormAttachment(48, -4);
		okBtnLData.bottom = new FormAttachment(800, 1000, 0);
		this.okBtn.setLayoutData(okBtnLData);
		this.okBtn.setText(Messages.getString("ColonnesConfigDialog.12"));
		this.okBtn.setToolTipText(Messages.getString("ColonnesConfigDialog.12"));
		
		this.okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				updateColumnConfigurations();
				returnValue = SWT.OK;
				shell.close();
			}
		});

		// Create the close button
		this.cancelBtn = new Button(this.okCancelBtnsComposite, SWT.PUSH);
		FormData cancelBtnLData = new FormData();
		cancelBtnLData.height = 25;
		cancelBtnLData.width = 50;
		
		// Align the close button to the right of the apply button
		cancelBtnLData.left = new FormAttachment(this.okBtn, 4);
		cancelBtnLData.bottom = new FormAttachment(800, 1000, 0);
		this.cancelBtn.setLayoutData(cancelBtnLData);
		this.cancelBtn.setText(Messages.getString("ColonnesConfigDialog.13"));
		this.cancelBtn.setToolTipText(Messages.getString("ColonnesConfigDialog.13"));
		
		this.cancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				returnValue = SWT.CANCEL;
				shell.close();
			}
		});
	}
	
	private void createColumnsCfgTable(final Shell shell) {
		// Create the configuration table
		GridData columnsCfgTableLData = new GridData();
		columnsCfgTableLData.verticalAlignment = GridData.FILL;
		columnsCfgTableLData.grabExcessVerticalSpace = true;
		columnsCfgTableLData.grabExcessHorizontalSpace = true;
		columnsCfgTableLData.horizontalAlignment = GridData.FILL;
		this.columnsCfgTable = new Table(this.main, SWT.BORDER | SWT.CHECK | SWT.MULTI | SWT.FULL_SELECTION);
		this.columnsCfgTable.setHeaderVisible(true);
		this.columnsCfgTable.setLinesVisible(true);
		this.columnsCfgTable.setLayoutData(columnsCfgTableLData);
		
		this.columnsCfgTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (e.detail == SWT.CHECK) {
					columnsCfgTable.setSelection((TableItem)e.item);
				}
			}
		});
		
		initTableColumns();
		
		final TableEditor editor = new TableEditor(this.columnsCfgTable);
		
		// The editor must have the same size as the cell and must not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		this.columnsCfgTable.addListener (SWT.MouseDown, new Listener () {
			public void handleEvent (Event event) {
				// Identify the selected row
				Rectangle clientArea = columnsCfgTable.getClientArea ();
				Point pt = new Point (event.x, event.y);
				int index = columnsCfgTable.getTopIndex();
				TableItem item = null;
				
				while (index < columnsCfgTable.getItemCount ()) {
					boolean visible = false;
					TableItem item1 = columnsCfgTable.getItem (index);
					Rectangle rect = item1.getBounds(WIDTH_COL_POS);
					
					if (rect.contains (pt)) {
						item = item1;
						break;
					}
					
					if (!visible && rect.intersects(clientArea)) {
						visible = true;
					}
					
					if (!visible) { 
						return;
					}
					
					index++;
				}
				
				if (item == null) {
					return;
				}

				// Clean up any previous editor control
				Control oldEditor = editor.getEditor();
				if (oldEditor != null) {
					oldEditor.dispose();
				}
		
				// The control that will be the editor must be a child of the Table
				final Text newEditor = new Text(columnsCfgTable, SWT.NONE);
				newEditor.setText(item.getText(WIDTH_COL_POS));
				
				newEditor.addFocusListener(new FocusListener() {
					public void focusGained(FocusEvent arg0) {}

					public void focusLost(FocusEvent arg0) {
						newEditor.dispose();
					}
				});
				
				newEditor.selectAll();
				newEditor.setFocus();
				editor.setEditor(newEditor, item, WIDTH_COL_POS);
				
				newEditor.addKeyListener(new KeyAdapter() {
		    		  public void keyPressed(KeyEvent event) {
		    			  switch (event.keyCode) {
		    			  case SWT.CR:
		    				  String newValue = newEditor.getText();
		    				  
		    				  if (!validateWidthValue(shell, newValue)) {
		    					  break;
		    				  }
		    				  
		    				  editor.getItem().setText(WIDTH_COL_POS, newValue);
		    			  case SWT.ESC:
		    				  newEditor.dispose();
		    				  break;
		    			  }
		    		  }
		    	  });
			}
		});
	}
	
    private void initTableColumns() {
    	// Create the indexes column
        this.displayStatusColumn = new TableColumn(this.columnsCfgTable, SWT.NONE);
        this.displayStatusColumn.setWidth(50);
        this.displayStatusColumn.setResizable(true);
        this.displayStatusColumn.setAlignment(SWT.CENTER);
        this.displayStatusColumn.setText(Messages.getString("ColonnesConfigDialog.14"));
        this.displayStatusColumn.setToolTipText((Messages.getString("ColonnesConfigDialog.14")));
        
        // Create the variables column
        this.widthColumn = new TableColumn(this.columnsCfgTable, SWT.NONE);
        this.widthColumn.setText(Messages.getString("ColonnesConfigDialog.15"));
        this.widthColumn.setToolTipText((Messages.getString("ColonnesConfigDialog.15")));
        this.widthColumn.setResizable(true);
        this.widthColumn.setAlignment(SWT.CENTER);
        this.widthColumn.setWidth(50);
    	
        // Create the operator column
        this.nameColumn = new TableColumn(this.columnsCfgTable, SWT.NONE);
        this.nameColumn.setText(Messages.getString("ColonnesConfigDialog.16"));
        this.nameColumn.setToolTipText((Messages.getString("ColonnesConfigDialog.16")));
        this.nameColumn.setWidth(200);
        this.nameColumn.setResizable(true);
        this.nameColumn.setAlignment(SWT.CENTER);
    }
    
    public void initTableData() {
    	ConfigurationColonne[] colsCfg = this.cfgListMng.getConfigurationColonnes();
    	this.initialColumnConfigurations = new ConfigurationColonne[colsCfg.length];
    	int i = 0;
    	
    	for (ConfigurationColonne colCfg : colsCfg) {
    		this.initialColumnConfigurations[i] = colCfg.clone();
    		i++;
    	}
    	recreateTable();
    }
    
    private void recreateTable() {
    	updateColumnConfigurations();
    	this.columnsCfgTable.removeAll();
    	TableItem item=null;
    	int i = 0;
    	String[] strVals = new String[] {"", "", ""};
    	String widthText;
    	
    	for (ConfigurationColonne colCfg : initialColumnConfigurations) {
    		if (this.listAllColumnsState || colCfg.isAffiche()) {
    			if (colCfg.getNom().startsWith("(V) ")) {
					String nomVBV = colCfg.getNom().replace("(V) ", "");
					
					if (ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(nomVBV) != null) {
						if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(nomVBV)) == null) {
							item = new TableItem(this.columnsCfgTable, SWT.NONE);
				    		widthText = colCfg.getLargeur() <= 0 ? Messages.getString("ColonnesConfigDialog.20") : String.valueOf(colCfg.getLargeur());
				    		strVals[1] = widthText;
				    		strVals[2] = GestionnaireVueListeBase.getDisplayLabelForColumn(colCfg, usesShortNames);
				    		item.setText(strVals);
				    		item.setChecked(colCfg.isAffiche());
				    		item.setData(this.initialColumnConfigurations[i]);
						}
					}
				} else if (!colCfg.getNom().startsWith("(V) ") 
    					&& !colCfg.getNom().equals(FLAG_COL_NAME_STR) 
    					&& !colCfg.getNom().equals(TIME_COL_NAME_STR)
    					&& !colCfg.getNom().equals(TIME_COR_COL_NAME_STR)
    					&&!colCfg.getNom().equals(REL_TIME_COL_NAME_STR)
    					&&!colCfg.getNom().equals(REL_DIST_COL_NAME_STR)
    					&&!colCfg.getNom().equals(DIST_COR_COL_NAME_STR)
    					&&!colCfg.getNom().equals(EVENT_COL_NAME_STR)
    					&&!colCfg.getNom().startsWith(VAR_COL_NAME_STR)
    					&&!colCfg.getNom().startsWith(ACC_DIST_COL_NAME_STR)
    					&& GestionnairePool.getInstance().getVariable(colCfg.getNom()) == null) {
    				if (ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(colCfg.getNom()) != null) {
						if (ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(colCfg.getNom())) == null) {
							item = new TableItem(this.columnsCfgTable, SWT.NONE);
				    		widthText = colCfg.getLargeur() <= 0 ? Messages.getString("ColonnesConfigDialog.20") : String.valueOf(colCfg.getLargeur());
				    		strVals[1] = widthText;
				    		strVals[2] = GestionnaireVueListeBase.getDisplayLabelForColumn(colCfg, usesShortNames);
				    		item.setText(strVals);
				    		item.setChecked(colCfg.isAffiche());
				    		item.setData(this.initialColumnConfigurations[i]);
						}
					}
    			} else {
		    		item = new TableItem(this.columnsCfgTable, SWT.NONE);
		    		widthText = colCfg.getLargeur() <= 0 ? Messages.getString("ColonnesConfigDialog.20") : String.valueOf(colCfg.getLargeur());
		    		strVals[1] = widthText;
		    		strVals[2] = GestionnaireVueListeBase.getDisplayLabelForColumn(colCfg, usesShortNames);
		    		item.setText(strVals);
		    		item.setChecked(colCfg.isAffiche());
		    		item.setData(this.initialColumnConfigurations[i]);
				}
    		}
    		
    		i++;
    	}
    }
    
    public ConfigurationColonne[] getCurrentConfigurations() {
    	return this.initialColumnConfigurations;
    }
    
    private void updateColumnConfigurations() {
    	TableItem[] tableItems = this.columnsCfgTable.getItems();
    	ConfigurationColonne colCfg;
    	String valueWidth;
    	int width;
    	
    	for (TableItem tblItem : tableItems) {
    		colCfg = (ConfigurationColonne)tblItem.getData();
    		colCfg.setAffiche(tblItem.getChecked());
    		valueWidth = tblItem.getText(1);
    		
    		if (Messages.getString("ColonnesConfigDialog.20").equals(valueWidth)) {
    			width = -1;	// A value of -1 means that is Auto
    		} else {
    			width = Integer.parseInt(valueWidth);
    		}
    		
    		colCfg.setLargeur(width);
    	}
    }
    
    private void selectTableItems(TableItem[] tblItems, boolean select) {
    	if (tblItems == null || tblItems.length == 0) {
    		return;
    	}
    	
    	for (TableItem tblItem: tblItems) {
    		tblItem.setChecked(select);
    	}
    }
    
    private boolean validateWidthValue(final Shell shell, String value) {
		int intValue = -1;
		
		if (value != null && !"".equals(value.trim())) {
			try {
				intValue = Integer.parseInt(value);
			} catch (Exception e) {}
		}
		
		if (intValue <= 0 && !Messages.getString("ColonnesConfigDialog.20").equals(value)) {
			MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msgBox.setText("");
			msgBox.setMessage(invalidColumnWidthMessage);
			msgBox.open();
			return false;
		}
		
		return true;
    }

	public boolean isListAllColumns() {
		return this.listAllColumnsState;
	}
}
