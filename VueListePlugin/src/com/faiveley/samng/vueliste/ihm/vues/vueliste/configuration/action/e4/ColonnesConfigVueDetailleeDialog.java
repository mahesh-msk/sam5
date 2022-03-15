package com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.action.e4;

import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;

/**
 * @author Cosmin Udroiu
 */
public class ColonnesConfigVueDetailleeDialog extends Dialog {
	private static int WIDTH_COL_POS = 1;
	
	private String invalidColumnWidthMessage = com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.0");
	private Composite main;

	private Table columnsCfgTable;
	
	private Button manualWidthButton;
	private Button automaticWidthButton;
	private Button hideButton;
	private Button displayButton;
	private Composite buttonsOperationsComposite;
	
	private Composite okCancelBtnsComposite;
	private Button cancelBtn;
	private Button okBtn;
	
    protected TableColumn displayStatusColumn;
    protected TableColumn widthColumn;
    protected TableColumn nameColumn;
    private GestionnaireVueDetaillee cfgListMng;
    private ConfigurationColonne[] initialColumnConfigurations;
    private int returnValue = SWT.CANCEL;
    private FixedColumnTableViewerDetail tableauVueDetaillee = null;
	
    public ColonnesConfigVueDetailleeDialog(Shell parent,FixedColumnTableViewerDetail tableauVueDetaillee) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);	
		this.tableauVueDetaillee= tableauVueDetaillee;
	}
	
	public ColonnesConfigVueDetailleeDialog(Shell parent, int style) {
		super(parent, style);
	}
	
	public void setGestionnaireConfiguration(GestionnaireVueDetaillee cfgMng) {
		this.cfgListMng = cfgMng;
	}
	
    /**
     * Opens the dialog and returns the input
     *
     * @return String
     */
    public int open() {
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

        shell.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				for (ConfigurationColonne colonne : initialColumnConfigurations) {
					ColonnesConfigVueDetailleeDialog.this.cfgListMng.ajouterColonneConfiguration(colonne);
				}	
			}
        });
        
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

		createColumnsCfgTable(shell);
		createButtonOperationsPanel(shell);
		createOkCancelButtonsPanel(shell);
    	initTableData();
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
		this.displayButton.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.2"));
		this.displayButton.setToolTipText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.2"));
		
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
		this.hideButton.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.3"));
		this.hideButton.setToolTipText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.3"));
		
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
		this.manualWidthButton.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.4"));
		this.manualWidthButton.setToolTipText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.4"));
		
		this.manualWidthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TableItem[] selItems = columnsCfgTable.getSelection();
				
				if (selItems == null || selItems.length == 0) {
					return;
				}
				
				InputDialog inputDlg = new InputDialog(shell, com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.5"), com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.6"), "", null);
				int res = inputDlg.open();
				
				if (res == org.eclipse.jface.window.Window.OK) {
					String value = inputDlg.getValue();
					
					if (!validateWidthValue(shell, value)) {
						return;
					}
					
					for (TableItem tblItem : selItems) {
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
		automaticWidthButtonLData.right = new FormAttachment(this.manualWidthButton, -4);
		automaticWidthButtonLData.bottom = new FormAttachment(800, 1000, 0);
		this.automaticWidthButton.setLayoutData(automaticWidthButtonLData);
		this.automaticWidthButton.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.10"));
		this.automaticWidthButton.setToolTipText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.10"));
		
		this.automaticWidthButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TableItem[] selItems = columnsCfgTable.getSelection();
				
				if (selItems == null || selItems.length == 0) {
					return;
				}
				
				for (TableItem tblItem : selItems) {
					tblItem.setText(1, com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.11"));
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
		okBtnLData.right =  new FormAttachment(48, -4);
		okBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.okBtn.setLayoutData(okBtnLData);
		this.okBtn.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.12"));
		this.okBtn.setToolTipText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.12"));
		
		this.okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				updateColumnConfigurations(true);
				returnValue = SWT.OK;
				shell.close();
				ConfigurationColonne[] colsCfg = ColonnesConfigVueDetailleeDialog.this.getCurrentConfigurations();
				ActivatorVueListe.getDefault().getConfigurationVueDetaillee().updateColumnsConfigurations(colsCfg);
				ColonnesConfigVueDetailleeDialog.this.tableauVueDetaillee.refreshTable(null);
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
		this.cancelBtn.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.13"));
		this.cancelBtn.setToolTipText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.13"));
		
		this.cancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				updateColumnConfigurations(false);
				returnValue = SWT.CANCEL;
				shell.close();
				ColonnesConfigVueDetailleeDialog.this.tableauVueDetaillee.refreshTable(null);
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
					columnsCfgTable.setSelection((TableItem) e.item);
				}
			}
		});
		
		initTableColumns();
		
		final TableEditor editor = new TableEditor(this.columnsCfgTable);
		
		// The editor must have the same size as the cell and must not be any smaller than 50 pixels.
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;
		editor.minimumWidth = 50;

		this.columnsCfgTable.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent (Event event) {
				// Identify the selected row
				Rectangle clientArea = columnsCfgTable.getClientArea();
				Point pt = new Point (event.x, event.y);
				int index = columnsCfgTable.getTopIndex();
				TableItem item = null;
				
				while (index < columnsCfgTable.getItemCount()) {
					boolean visible = false;
					TableItem item1 = columnsCfgTable.getItem (index);
					Rectangle rect = item1.getBounds (WIDTH_COL_POS);
					
					if (rect.contains(pt)) {
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
        this.displayStatusColumn.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.14"));
        this.displayStatusColumn.setToolTipText((com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.14")));
        
        // Create the variables column
        this.widthColumn = new TableColumn(this.columnsCfgTable, SWT.NONE);
        this.widthColumn.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.15"));
        this.widthColumn.setToolTipText((com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.15")));
        this.widthColumn.setResizable(true);
        this.widthColumn.setAlignment(SWT.CENTER);
        this.widthColumn.setWidth(50);
        
    	// Create the operator column
        this.nameColumn = new TableColumn(this.columnsCfgTable, SWT.NONE);
        this.nameColumn.setText(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.16"));
        this.nameColumn.setToolTipText((com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.16")));
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
    	updateColumnConfigurations(false);
    	this.columnsCfgTable.removeAll();
    	TableItem item;
    	int i = 0;
    	String[] strVals = new String[] {"", "", ""};
    	String widthText;
    	
    	for (ConfigurationColonne colCfg : initialColumnConfigurations) {
    		item = new TableItem(this.columnsCfgTable, SWT.NONE);
    		widthText = colCfg.getLargeur() <= 0 ? com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.20") : String.valueOf(colCfg.getLargeur()); //$NON-NLS-1$
    		strVals[1] = widthText;
    		strVals[2] = colCfg.getNom();
    		item.setText(strVals);
    		item.setChecked(colCfg.isAffiche());
    		item.setData(this.initialColumnConfigurations[i]);
    		i++;
    	}
    }
    
    public ConfigurationColonne[] getCurrentConfigurations() {
    	return this.initialColumnConfigurations;
    }
    
    private void updateColumnConfigurations(boolean modifier) {
    	TableItem[] tableItems = this.columnsCfgTable.getItems();
    	ConfigurationColonne colCfg;
    	String valueWidth;
    	int width;
    	this.cfgListMng.clear();
    	
    	for (TableItem tblItem : tableItems) {
    		colCfg = (ConfigurationColonne)tblItem.getData();
    		colCfg.setAffiche(tblItem.getChecked());
    		
    		if (modifier) {
    			valueWidth = tblItem.getText(1);
    			
	    		if (com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.20").equals(valueWidth)) {
	    			width = -1;	// A value of -1 means that is auto
	    		} else {
	    			width = Integer.parseInt(valueWidth);
	    		}
	    		
	    		colCfg.setLargeur(width);
    		}
    		
    		this.cfgListMng.ajouterColonneConfiguration(colCfg);
    	}
    }
    
    private void selectTableItems(TableItem[] tblItems, boolean select) {
    	if (tblItems == null || tblItems.length == 0) {
    		return;
    	}
    	
    	for (TableItem tblItem : tblItems) {
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
		
		if (intValue <= 0 && !com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("ColonnesConfigDialog.20").equals(value)) {
			MessageBox msgBox = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			msgBox.setText("");
			msgBox.setMessage(invalidColumnWidthMessage);
			msgBox.open();
			
			return false;
		}
		
		return true;
    }
}
