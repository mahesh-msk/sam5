package com.faiveley.samng.principal.ihm.vues.search;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.ihm.listeners.ISearchMarquerListener;

/**
 * Implementation of the search by marquer dialog
 * @author meggy
 *
 */
public class SearchMarquer extends ASearchDialog {

	//control for search
	protected Text text = null;

	//flag to say if should be search for the comment or for the marquer name
	protected boolean searchByComment;
	
	/**
	 * Constructor
	 * @param parent
	 */
	public SearchMarquer(Shell parent) {
		super(parent);
	}
	
	/**
	 * Constructor
	 * @param parent
	 * @param style
	 */
	public SearchMarquer(Shell parent, int style) {
		super(parent, style);
	}
	
	/**
	 * creates the controls for the current dialog
	 */
	public void createControls(final Shell parent) {

		//text 
        this.text = new Text(parent, SWT.BORDER);
        this.text.setBounds(new Rectangle(87, 30, 229, 19));
        this.text.setEnabled(false);
        Label labelName = new Label(parent, SWT.NONE);
        labelName.setBounds(new Rectangle(25, 32, 38, 13));
        labelName.setText(Messages.getString("SearchMarquer.0")); //$NON-NLS-1$
        labelName.setToolTipText((Messages.getString("SearchMarquer.0")));
        
        labelName = new Label(parent, SWT.NONE);
        labelName.setBounds(new Rectangle(50, 80, 100, 13));
        labelName.setText(Messages.getString("SearchMarquer.1")); //$NON-NLS-1$
        labelName.setToolTipText((Messages.getString("SearchMarquer.1")));
        
        //checkbox Search By Comment
        final Button btnSearchByComment = new Button(parent, SWT.CHECK);
        btnSearchByComment.setBounds(new Rectangle(20, 80, 13, 16));
        btnSearchByComment.setSelection(false);
        btnSearchByComment.setEnabled(true);
        this.searchByComment = false;
        
        btnSearchByComment.addSelectionListener(new SelectionAdapter() {
        	@Override
        	public void widgetSelected(SelectionEvent event) {
        		SearchMarquer.this.searchByComment = btnSearchByComment.getSelection();
        		SearchMarquer.this.text.setEnabled(SearchMarquer.this.searchByComment);
        	}
        });
        
        //button "Previous" 
        Button buttonPrev = new Button(parent, SWT.NONE);
        buttonPrev.setBounds(new Rectangle(20, 120, 90, 23));
        buttonPrev.setText(Messages.getString("SearchMarquer.2")); //$NON-NLS-1$
        buttonPrev.setToolTipText((Messages.getString("SearchMarquer.2")));
        buttonPrev.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		ISearchMarquerListener listener = null;
        		
        		IViewReference[] vr = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
				for (IViewReference v : vr) {
					IViewPart view = v.getView(false);
					if (view instanceof ISearchMarquerListener) {
						listener = ((ISearchMarquerListener) view);
					}
				}
				if (listener != null) {
	        		if (SearchMarquer.this.searchByComment) {
	        			String comment = SearchMarquer.this.text.getText();
	        			listener.onSelectedMarquerCommentChange(comment, false);
	        		} else {
		        		String marquerName = ""; //$NON-NLS-1$
		        		marquerName = listener.onSelectedMarquerNomChange(false);
						SearchMarquer.this.text.setText(marquerName);
						SearchMarquer.this.text.setToolTipText((marquerName));
	        		}
				}
        	}
        });
        
        //button "Next"
        Button buttonNext = new Button(parent, SWT.NONE);
        buttonNext.setBounds(new Rectangle(120, 120, 90, 23));
        buttonNext.setText(Messages.getString("SearchMarquer.3")); //$NON-NLS-1$
        buttonNext.setToolTipText((Messages.getString("SearchMarquer.3")));
        buttonNext.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		ISearchMarquerListener listener = null;
        		
        		//looks for listeners in the current views
        		IViewReference[] vr = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
				for (IViewReference v : vr) {
					IViewPart view = v.getView(false);
					if (view instanceof ISearchMarquerListener) {
						listener = ((ISearchMarquerListener) view);
					}
				}
				if (listener != null) {
	        		if (SearchMarquer.this.searchByComment) {
	        			String comment = SearchMarquer.this.text.getText();
	        			listener.onSelectedMarquerCommentChange(comment, true);
	        		} else {
		        		String marquerName = ""; //$NON-NLS-1$
	        			marquerName = listener.onSelectedMarquerNomChange(true);
	        			SearchMarquer.this.text.setText(marquerName);
	        			SearchMarquer.this.text.setToolTipText((marquerName));
					}
				}
        	}
        });
        
        //button "Cancel"
        Button buttonCancel = new Button(parent, SWT.NONE);
        buttonCancel.setBounds(new Rectangle(240, 120, 90, 23));
        buttonCancel.setText(Messages.getString("SearchMarquer.4"));  //$NON-NLS-1$
        buttonCancel.setToolTipText((Messages.getString("SearchMarquer.4")));
        buttonCancel.addSelectionListener(new SelectionAdapter() {
        	public void widgetSelected(SelectionEvent e) {
        		parent.close();
        	}
        });
        
	}

	/**
	 * Returns the size of the dialog
	 */
	protected Point getSize() {
		return new Point(350, 200);
	}
	
}  //  @jve:decl-index=0:visual-constraint="10,10,344,156"
