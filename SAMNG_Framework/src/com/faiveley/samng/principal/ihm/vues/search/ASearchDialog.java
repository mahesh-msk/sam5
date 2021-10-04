package com.faiveley.samng.principal.ihm.vues.search;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public abstract class ASearchDialog extends Dialog {

	private List<String> variablesDuFiltre;

	private Shell shell;
	
	protected boolean usesShortNames;
	
	public ASearchDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public ASearchDialog(Shell parent, int style) {
		super(parent, style);
	}
	

	 /**
     * Opens the dialog and returns the input
     *
     * @return int
     */
    public int open() {
        // Create the dialog window
        this.shell = new Shell(getParent(), getStyle());
        this.shell.setText(Messages.getString(("SearchInFile.0")));
        createControls(this.shell);
        this.shell.pack();
        
        //set the size of the dialog
        this.shell.setSize(getSize().x, 475);
        //position the dialog in the center of the parent shell
        Rectangle parentBounds = getParent().getBounds();
        Rectangle childBounds = this.shell.getBounds();
        int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
        int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
        this.shell.setLocation (x, y);

        this.shell.open();
        Display display = getParent().getDisplay();
        while (!this.shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }

        // Return the entered value, or null
        return 0;
    }
    
    private String appelant;
	public String getAppelant() {
		return appelant;
	}

	public void setAppelant(String appelant) {
		this.appelant = appelant;
	}
    
	protected String typeRecherche;

	public String getTypeRecherche() {
		return typeRecherche;
	}

	public void setTypeRecherche(String typeRecherche) {
		this.typeRecherche = typeRecherche;
	}
	
	protected Table itemsTable;
	
    /**
     * Override to create the controls
     * @param parent
     */
    protected abstract void createControls(final Shell parent);

    
    /**
     * Override to return the size
     * @return
     */
	protected abstract Point getSize();

	public List<String> getVariablesDuFiltre() {
		return variablesDuFiltre;
	}

	public void setVariablesDuFiltre(List<String> variablesDuFiltre) {
		this.variablesDuFiltre = variablesDuFiltre;
	}
}
