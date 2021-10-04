package com.faiveley.samng.principal.ihm.vues.vuemarqueurs;

import org.eclipse.jface.dialogs.IInputValidator;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;




/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class MarqueurCommentaireDialog extends Dialog {

	private Composite main;
	
	private Text textArea;
	private Text label;
	
	private Composite okCancelBtnsComposite;
	private Button cancelBtn;
	private Button okBtn;
	
    protected TableColumn displayStatusColumn;
    protected TableColumn widthColumn;
    protected TableColumn nameColumn;
    private int returnValue = SWT.CANCEL;
    private String strValue = "";
    private String dialogTitle;
    private String dialogMessage;
    private String initialValue;
	
	public MarqueurCommentaireDialog(Shell parent) {
		this(parent, "", "", "", null);		
	}
	
	public MarqueurCommentaireDialog(Shell parent, String dialogTitle, String dialogMessage, 
			String initialValue, IInputValidator validator) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL | SWT.RESIZE);
	    this.dialogTitle = dialogTitle;
	    this.dialogMessage = dialogMessage;
	    this.initialValue = initialValue;
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
        createContents(shell);
        shell.setText(this.dialogTitle);
        this.label.setText(this.dialogMessage);
        this.textArea.setText(this.initialValue);
        shell.pack();
        shell.setSize(254, 254);
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
		createLabel();
		createTextArea();
		createOkCancelButtonsPanel(shell);
    }
    
	private void createLabel() {
		// Create the top button for selecting the list all columns mode
		this.label = new Text(this.main, SWT.SINGLE);
		GridData listAllColumnsBtnLData = new GridData();
		listAllColumnsBtnLData.heightHint = 22;
		listAllColumnsBtnLData.grabExcessHorizontalSpace = true;
		listAllColumnsBtnLData.horizontalAlignment = GridData.FILL;
		this.label.setLayoutData(listAllColumnsBtnLData);
		this.label.setEditable(false);
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
		this.okBtn = new Button(this.okCancelBtnsComposite, SWT.PUSH);
		FormData okBtnLData = new FormData();
		okBtnLData.height = 25;
		//okBtnLData.width = 50;
		//position it near the center of the container panel
		okBtnLData.right =  new FormAttachment(48, -4);
		okBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.okBtn.setLayoutData(okBtnLData);
		this.okBtn.setText(Messages.getString("VueMarqueurs.11"));
		this.okBtn.setToolTipText((Messages.getString("VueMarqueurs.11")));
		this.okBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				returnValue = SWT.OK;
				strValue = textArea.getText();
				shell.close();
			}
		});

		//Create the close button
		this.cancelBtn = new Button(this.okCancelBtnsComposite, SWT.PUSH);
		FormData cancelBtnLData = new FormData();
		cancelBtnLData.height = 25;
		//cancelBtnLData.width = 50;
		//align the close button to the right of the apply button
		cancelBtnLData.left =  new FormAttachment(this.okBtn, 4);
		cancelBtnLData.bottom =  new FormAttachment(800, 1000, 0);
		this.cancelBtn.setLayoutData(cancelBtnLData);
		this.cancelBtn.setText((Messages.getString("VueMarqueurs.12")));
		this.cancelBtn.setToolTipText((Messages.getString("VueMarqueurs.12")));
		this.cancelBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				returnValue = SWT.CANCEL;
				shell.close();
			}
		});
	}
	
	private void createTextArea() {
		//Create the configuration table
		GridData columnsCfgTableLData = new GridData();
		columnsCfgTableLData.verticalAlignment = GridData.FILL;
		columnsCfgTableLData.grabExcessVerticalSpace = true;
		columnsCfgTableLData.grabExcessHorizontalSpace = true;
		columnsCfgTableLData.horizontalAlignment = GridData.FILL;
		this.textArea = new Text(this.main, SWT.BORDER | SWT.MULTI);
		this.textArea.setLayoutData(columnsCfgTableLData);
		this.textArea.setFocus();
	}
	
	public String getValue() {
		return strValue;
	}
}
