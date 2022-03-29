/**
 * 
 */
package com.faiveley.samng.principal.ihm.vues.vuescorrections;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.calcul.TailleBouton;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.vues.ATableLabelProvider;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.segments.ASegment;

/**
 * Abstract class for the correction viewa
 * 
 * @author meggy
 * 
 */
public abstract class VueCorrection extends ViewPart implements
		IDataChangedListener, ICapturable {

	// contant indexes
	protected static final int INDEX_SEGMENT = 0;
	protected static final int INDEX_PERIOD = 1;
	protected static final int INDEX_INIT_VAL = 2;
	protected static final int INDEX_CORRECTED_VAL = 3;

	// constant field for column width
	private static final int WIDTH = 150;

	//
	private static Color redColor;

	// controls
	private Composite top = null;
	protected Table table = null;
	protected TableViewer viewer = null;

	protected Button buttonApply = null;
	protected Button buttonDoNotApply = null;
	private Button buttonCancel = null;

	// the table rows
	protected Row[] rows;

	private class ColorLabelProvider extends ATableLabelProvider {
		@Override
		public Color getBackground(Object element, int columnIndex) {
			return null;
		}

		@Override
		public Color getForeground(Object element, int columnIndex) {
			Color c = null;
			// if the segment is not valid set the red font, otherwise remains
			// on default
			if (!((ASegment) ((Row) element).getData()).isValide()) {

				redColor = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
				c = redColor;
			}
			return c;
		}
	}

	private class ApplyAdapter extends SelectionAdapter {
		private boolean apply;

		public ApplyAdapter(boolean apply) {
			this.apply = apply;
		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (verifierSaisie()) {
				if (apply) {
					applyChanges();
				} else {
					doNotApplyChanges();
				}
				PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.hideView(VueCorrection.this);
				GestionnaireCorrection.getInstance().saveCorrections(true);
			} else {
				MessageBox messageBox = new MessageBox(VueCorrection.this
						.getViewSite().getShell(), SWT.ICON_ERROR);
				messageBox.setMessage(VueCorrection.this.getError());
				messageBox.open();
			}

			updateButtons();
		}
	}

	/**
	 * Constructor
	 */
	public VueCorrection() {

	}

	public abstract boolean verifierSaisie();

	public abstract boolean verifierPresenceCorrection();

	/**
	 * Creates the view
	 * 
	 */
	protected void create() {

		// build the controls
		buildControls();

		this.viewer = new TableViewer(this.table);

		// set the modifier, the editor, the columns, the content and label
		// providers
		this.viewer.setCellModifier(new MyCellModifier());
		this.viewer.setCellEditors(new CellEditor[] { null, null, null,
				new TextCellEditor(this.table) });
		this.viewer.setColumnProperties(getColumnNames());

		this.viewer.setContentProvider(ArrayContentProvider.getInstance());
		this.viewer.setLabelProvider(new ColorLabelProvider());

		// set lines and header visible
		this.table.setHeaderVisible(true);
		this.table.setLinesVisible(true);

		// Create the columns
		createColumns();

		// load the data in the table
		this.viewer.setInput(getInput());

	}

	/**
	 * Creates the controls of this view
	 */
	private void buildControls() {
		// set top grid
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;
		gridLayout.marginTop = 10;
		gridLayout.marginBottom = 10;

		this.top.setLayout(gridLayout);

		// Table
		this.table = new Table(this.top, SWT.BORDER | SWT.FULL_SELECTION);
		GridData gridData = new GridData();

		gridData.verticalSpan = 4;
		gridData.horizontalSpan = 4;
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.heightHint = 400;
		gridData.widthHint = WIDTH * 5;

		this.table.setLayoutData(gridData);

		Label filler = new Label(this.top, SWT.NONE);
		gridData = new GridData();
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.END;
		gridData.grabExcessHorizontalSpace = false;
		gridData.heightHint = 20;
		gridData.widthHint = 200;
		filler.setLayoutData(gridData);

		// button Do Not Apply
		this.buttonDoNotApply = new Button(this.top, SWT.NONE);
		this.buttonDoNotApply.setText(Messages.getString("VueCorrection.0")); //$NON-NLS-1$
		this.buttonDoNotApply.setToolTipText((Messages
				.getString("VueCorrection.0"))); //$NON-NLS-1$

		gridData = new GridData();
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.END;
		gridData.grabExcessHorizontalSpace = false;
		gridData.heightHint = 25;
		gridData.widthHint = TailleBouton.CalculTailleBouton(Messages
				.getString("VueCorrection.0").length()); //80; //$NON-NLS-1$

		this.buttonDoNotApply.setLayoutData(gridData);

		// button Apply
		this.buttonApply = new Button(this.top, SWT.NONE);
		this.buttonApply.setText(Messages.getString("VueCorrection.1")); //$NON-NLS-1$
		this.buttonApply
				.setToolTipText((Messages.getString("VueCorrection.1"))); //$NON-NLS-1$

		gridData = new GridData();
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 1;
		gridData.horizontalAlignment = GridData.END;
		gridData.grabExcessHorizontalSpace = false;
		gridData.heightHint = 25;
		gridData.widthHint = TailleBouton.CalculTailleBouton(Messages
				.getString("VueCorrection.1").length());; //80; //$NON-NLS-1$

		this.buttonApply.setLayoutData(gridData);

		// set the buttons status
		updateButtons();

		// button Cancel
		this.buttonCancel = new Button(this.top, SWT.NONE);
		this.buttonCancel.setText(Messages.getString("VueCorrection.2")); //$NON-NLS-1$
		this.buttonCancel
				.setToolTipText((Messages.getString("VueCorrection.2"))); //$NON-NLS-1$

		gridData = new GridData();
		gridData.verticalSpan = 1;
		gridData.horizontalSpan = 1;
		gridData.grabExcessHorizontalSpace = false;
		gridData.heightHint = 25;
		gridData.widthHint = TailleBouton.CalculTailleBouton(Messages
				.getString("VueCorrection.2").length()); //80; //$NON-NLS-1$
		gridData.horizontalAlignment = GridData.BEGINNING;

		this.buttonCancel.setLayoutData(gridData);

		// selection listener for button Apply
		this.buttonApply.addSelectionListener(new ApplyAdapter(true));

		// selection listener for button Do Not Apply
		this.buttonDoNotApply.addSelectionListener(new ApplyAdapter(false));

		// selection listener for buton Cancel
		this.buttonCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow().getActivePage()
						.hideView(VueCorrection.this);
			}
		});
	}

	protected void updateTitle(boolean applied) {
		String crtTitle = getPartName();
		crtTitle += applied ? Messages.getString("VueCorrection.3") : Messages.getString("VueCorrection.4"); //$NON-NLS-1$ //$NON-NLS-2$
		setPartName(crtTitle);
	}
	
	@Override
	public final void createPartControl(Composite parent) {
		this.top = new Composite(parent, SWT.NONE);

		// add this as listener for changing data
		if (!ActivatorData.getInstance().isDataListening(this)) {
			ActivatorData.getInstance().addDataListener(this);
		}
		// create the view
		create();
	}
	
	@Override
	public void setFocus() {

	}

	@Override
	public void dispose() {

		// color is a win32 resource and must be disposed
		if (redColor != null) {
			redColor.dispose();
		}

		// dispose controls
		super.dispose();

		// remove listener
		ActivatorData.getInstance().removeDataListener(this);
	}

	/**
	 * Creates the colums for the table
	 * 
	 * @return the list of columns
	 */
	private List<TableColumn> createColumns() {
		TableColumn col = null;
		String[] columnsNames = getColumnNames();

		int size = columnsNames.length;
		List<TableColumn> columns = new ArrayList<TableColumn>(size);
		for (int i = 0; i < size; i++) {
			// Create the TableColumn with right alignment
			col = new TableColumn(this.table, SWT.SINGLE | SWT.RIGHT);

			if (i == INDEX_SEGMENT) {
				col.setWidth(50);
			} else if (i == INDEX_PERIOD) {
				col.setWidth(200);
			} else {
				col.setWidth(WIDTH);
			}

			// add new columns to columns list
			columns.add(col);

			// This text will appear in the column header
			col.setText(columnsNames[i]);
			// set the same text as tooltip
			col.setToolTipText(columnsNames[i]);

		}
		return columns;
	}

	/**
	 * Implementation of the method of the interface IDataChangedListener. On
	 * this method is received the notification when another parcours file was
	 * loaded
	 */
	@Override
	public void onDataChange() {
		// release current data loaded
		resetInput();

		// dispose the controls
		if (!this.table.isDisposed()) {
			this.table.dispose();
		}
		if (!this.buttonApply.isDisposed()) {
			this.buttonApply.dispose();
		}
		if (!this.buttonDoNotApply.isDisposed()) {
			this.buttonDoNotApply.dispose();
		}
		if (!this.buttonCancel.isDisposed()) {
			this.buttonCancel.dispose();
		}

		// creates new controls
		create();

		// reload view
		this.viewer.refresh();
		this.top.layout();
	}

	/**
	 * Implementation for the CellModifier used by table
	 * 
	 * @author meggy
	 * 
	 */
	private class MyCellModifier implements ICellModifier {

		@Override
		public boolean canModify(Object element, String property) {
			return (property.equals(getColumnNames()[INDEX_CORRECTED_VAL]));
		}

		@Override
		public Object getValue(Object element, String property) {
			// the 3rd column is editable, then should be red its value
			if ((property.equals(getColumnNames()[INDEX_CORRECTED_VAL]))) {
				Row row = (Row) element;
				return row.getValue(INDEX_CORRECTED_VAL);
			}

			// iterwise return the same input
			return element;
		}

		@Override
		public void modify(Object element, String property, Object value) {
			// sets the value for the 3rd column
			Row row = (Row) ((TableItem) element).getData();

			row.setValue(INDEX_CORRECTED_VAL, (String) value);

			VueCorrection.this.buttonApply
					.setEnabled(verifierPresenceCorrection());

			VueCorrection.this.viewer.refresh(row);
		}

	}

	/**
	 * Resets the current input of the table
	 * 
	 */
	protected void resetInput() {
		this.rows = null;
	}

	/**
	 * Returns the names of the colums
	 * 
	 * @return array of names
	 */
	protected abstract String[] getColumnNames();

	/**
	 * The functionality of the "Apply" button
	 * 
	 */
	protected abstract void applyChanges();

	/**
	 * returns the error string of the view
	 * 
	 */
	protected abstract String getError();

	/**
	 * The functionality of the "Do Not Apply" button
	 * 
	 */
	protected abstract void doNotApplyChanges();

	/**
	 * Sets the status of the buttons "Apply" and "Do not apply"
	 * 
	 */
	protected abstract void updateButtons();

	/**
	 * Returns the current input of the table
	 * 
	 * @return the rows
	 */
	protected abstract Row[] getInput();

	@Override
	public Composite getContenu() {
		return top;
	}

}
