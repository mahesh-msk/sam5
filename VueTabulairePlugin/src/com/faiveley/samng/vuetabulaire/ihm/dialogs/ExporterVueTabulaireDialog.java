package com.faiveley.samng.vuetabulaire.ihm.dialogs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;



/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ExporterVueTabulaireDialog extends Dialog {
	private static String noFilterSelectedError = Messages.getString("ExporterVueTabulaireDialog.0"); //$NON-NLS-1$
	private Composite main;
	private List<String> filterNames = new ArrayList<String>(0);
	private Label headerLabel;
	private org.eclipse.swt.widgets.List filtersNamesList;
	private Button hideButton;
	private Button displayButton;
	private Composite buttonsOperationsComposite;
	private Composite okCancelBtnsComposite;
	private Button exportBtn;
	private Button annulerBtn;
	
    private int returnValue = SWT.CANCEL;
    private String selectedFilterName;
	
	public ExporterVueTabulaireDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);		
	}
	
	public ExporterVueTabulaireDialog(Shell parent, int style) {
		super(parent, style);
	}
	
    /**
     * Opens the dialog and returns the input
     *
     * @return String
     */
    public int open() {
        // Create the dialog window
        Shell shell = new Shell(getParent(), getStyle());
        shell.setText(Messages.getString("ExporterVueTabulaireDialog.5")); //$NON-NLS-1$
       
        createContents(shell);
        shell.pack();

        shell.setSize(475, 550);
        //position the dialog in the center of the parent shell
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
		createTopLabel();
		createColumnsCfgTable();
		createButtonOperationsPanel(shell);
		createOkCancelButtonsPanel(shell);
    	initTableData();
    }
    
	private void createTopLabel() {
		// Create the top button for selecting the list all columns mode
		this.headerLabel = new Label(this.main, SWT.LEFT);
		GridData listAllColumnsBtnLData = new GridData();
		listAllColumnsBtnLData.heightHint = 22;
		listAllColumnsBtnLData.grabExcessHorizontalSpace = true;
		listAllColumnsBtnLData.horizontalAlignment = GridData.FILL;
		this.headerLabel.setLayoutData(listAllColumnsBtnLData);
		this.headerLabel.setText(Messages.getString("TabulaireExportExport.2")); //$NON-NLS-1$
	}
	
	private void createButtonOperationsPanel(final Shell shell) {
		//create the panel for the two buttons
		this.buttonsOperationsComposite = new Composite(this.main, SWT.BORDER);
		FormLayout btnsOpsCompLayout = new FormLayout();
		GridData btnsOpsCompLData = new GridData();
		btnsOpsCompLData.grabExcessHorizontalSpace = true;
		btnsOpsCompLData.horizontalAlignment = GridData.FILL;
		btnsOpsCompLData.verticalAlignment = GridData.END;
		btnsOpsCompLData.heightHint = 50;
		this.buttonsOperationsComposite.setLayoutData(btnsOpsCompLData);
		this.buttonsOperationsComposite.setLayout(btnsOpsCompLayout);
		
		//Create the apply button
		this.displayButton = new Button(this.buttonsOperationsComposite, SWT.PUSH);
		FormData displayButtonLData = new FormData();
		displayButtonLData.height = 25;
		//displayButtonLData.width = 100;
		//position it near the center of the container panel
		displayButtonLData.right =  new FormAttachment(50, -4);
		displayButtonLData.bottom =  new FormAttachment(800, 1000, 0);
		this.displayButton.setLayoutData(displayButtonLData);
		this.displayButton.setText(Messages.getString("TabulaireExportExport.3")); //$NON-NLS-1$
		this.displayButton.setToolTipText(Messages.getString("TabulaireExportExport.3")); //$NON-NLS-1$
		this.displayButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				int selIdx = filtersNamesList.getSelectionIndex();
				if(selIdx == -1) {
					MessageBox msgBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
					msgBox.setText(""); //$NON-NLS-1$
					msgBox.setMessage(noFilterSelectedError);
					msgBox.open();
					return;

				}
				selectedFilterName = filtersNamesList.getItem(selIdx);
			}
		});

		//Create the close button
		this.hideButton = new Button(this.buttonsOperationsComposite, SWT.PUSH);
		FormData hideButtonLData = new FormData();
		hideButtonLData.height = 25;
		//hideButtonLData.width = 100;
		//align the close button to the right of the apply button
		hideButtonLData.left =  new FormAttachment(this.displayButton, 4);
		hideButtonLData.bottom =  new FormAttachment(800, 1000, 0);
		this.hideButton.setLayoutData(hideButtonLData);
		this.hideButton.setText(Messages.getString("TabulaireExportExport.4")); //$NON-NLS-1$
		this.hideButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if(selectedFilterName == null) {
					MessageBox msgBox = new MessageBox(shell, SWT.ICON_INFORMATION | SWT.OK);
					msgBox.setText(""); //$NON-NLS-1$
					msgBox.setMessage(noFilterSelectedError);
					msgBox.open();
				}
				selectedFilterName = null;
			}
		});
	}
	
	private void createOkCancelButtonsPanel(final Shell shell) {
		//create the panel for the two buttons
		this.okCancelBtnsComposite = new Composite(this.main, SWT.BORDER);
		FormLayout okCancelBtnsCompLayout = new FormLayout();
		GridData okCancelBtnsCompLData = new GridData();
		okCancelBtnsCompLData.grabExcessHorizontalSpace = true;
		okCancelBtnsCompLData.horizontalAlignment = GridData.FILL;
		okCancelBtnsCompLData.verticalAlignment = GridData.END;
		okCancelBtnsCompLData.heightHint = 50;
		this.okCancelBtnsComposite.setLayoutData(okCancelBtnsCompLData);
		this.okCancelBtnsComposite.setLayout(okCancelBtnsCompLayout);
		
		//Create the apply button
		this.exportBtn = new Button(this.okCancelBtnsComposite, SWT.PUSH);
		FormData okBtnLData = new FormData();
		okBtnLData.height = 25;
		//okBtnLData.width = 100;
		//position it near the center of the container panel
		okBtnLData.right =  new FormAttachment(50, -4);
		okBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.exportBtn.setLayoutData(okBtnLData);
		this.exportBtn.setText(Messages.getString("TabulaireExportExport.1"));  //$NON-NLS-1$
		this.exportBtn.setToolTipText(Messages.getString("TabulaireExportExport.1"));//$NON-NLS-1$
		this.exportBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				returnValue = SWT.OK;
				shell.close();
			}
		});
		
		
//		Create the cancel button
		this.annulerBtn = new Button(this.okCancelBtnsComposite, SWT.PUSH);
		FormData annulerBtnLData = new FormData();
		annulerBtnLData.height = 25;
		//position it left the container panel
		annulerBtnLData.right =  new FormAttachment(65, -4);
		annulerBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.annulerBtn.setLayoutData(annulerBtnLData);
		this.annulerBtn.setText(Messages.getString("ExporterVueTabulaireDialog.1")); //$NON-NLS-1$
		this.annulerBtn.setToolTipText(Messages.getString("ExporterVueTabulaireDialog.1")); //$NON-NLS-1$
		this.annulerBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				
				shell.close();
			}
		});
		
		
		
		
	}
	
	private void createColumnsCfgTable() {
		//Create the configuration table
		GridData columnsCfgTableLData = new GridData();
		columnsCfgTableLData.verticalAlignment = GridData.FILL;
		columnsCfgTableLData.grabExcessVerticalSpace = true;
		columnsCfgTableLData.grabExcessHorizontalSpace = true;
		columnsCfgTableLData.horizontalAlignment = GridData.FILL;
		this.filtersNamesList = new org.eclipse.swt.widgets.List(this.main, SWT.BORDER | SWT.CHECK | SWT.MULTI
		        						| SWT.FULL_SELECTION);
		this.filtersNamesList.setLayoutData(columnsCfgTableLData);
		this.filtersNamesList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
	}
	
    private void initTableData() {
    	for(String filterName: filterNames) {
    		filtersNamesList.add(filterName);
    	}
    }

    public void setFiltersNames(List<String> filterNames) {
    	this.filterNames = filterNames;
    }
    
	public String getSelectedFilterName() {
		return selectedFilterName;
	}

    public static void main(String[] argv) {
    	new ExporterVueTabulaireDialog(new Shell()).open();
    }
}
