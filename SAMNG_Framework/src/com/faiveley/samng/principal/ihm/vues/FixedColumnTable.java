package com.faiveley.samng.principal.ihm.vues;

import java.util.List;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;

public class FixedColumnTable extends Composite {
	
	public FixedColumnTable(Composite parent, int style) {
		super(parent, SWT.NONE);
	}

	protected TableViewer fixedTableViewer;

	protected TableViewer scrollingTableViewer;

	protected Table fixedTable;

	protected Table scrollingTable;

	protected  boolean notEquivalent = false;
	protected  boolean notEquivalentForSearch = false;

	public void onColumnsAdded(List<String> columnNames) {
		TableColumn[] columns = this.fixedTable.getColumns();
		
		VueTabulaireContentProvider contentProvider = (VueTabulaireContentProvider) this.scrollingTableViewer.getContentProvider();
		ConfigurationColonne[] colonnes = contentProvider.gestionaireVue.getFilteredColumns(ActivatorData.getInstance().getVueData());
		
		int start = columns.length - 1;
		for (int i = start; i < columnNames.size(); i++) {
			String colNom = columnNames.get(i);
			
			ConfigurationColonne colCfg = getConfigurationColonneFromNom(colonnes, colNom);
			
			if(colCfg != null && colCfg.isVolatile()) {
				int scrollingTableIdx = i - start + 1;
				TableColumn tc = this.scrollingTable.getColumns()[scrollingTableIdx];
				FixedColumnTableViewerEditingSupport editingSupport = new FixedColumnTableViewerEditingSupport(this.scrollingTableViewer, i, colCfg.getNom());
				editingSupport.setVarNamesFilters(contentProvider.varNamesFilters);
				CellLabelProvider labelProvider = this.scrollingTableViewer.getLabelProvider(scrollingTableIdx);
				TableViewerColumn tvc = new TableViewerColumn(scrollingTableViewer, tc);
				tvc.setEditingSupport(editingSupport);
				tvc.setLabelProvider(labelProvider);
			}
		}
		
		for(TableColumn col: columns) {
			col.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					int widths = getFixedColumnsWidths();
					fixedTable.setSize(widths, fixedTable.getBounds().height);
					layout();
				}
			});
		}
	}
	
	private ConfigurationColonne getConfigurationColonneFromNom(ConfigurationColonne[] colonnes, String nom) {
		for (int i = 0; i < colonnes.length; i++) {
			if (nom.equals(colonnes[i].getNom())) {
				return colonnes[i];
			}
		}
		return null;
	}

	/**
	 * Computes the widths of the columns from the scrolling table.
	 * The width of the dummy column is not taken into consideration
	 * 
	 * @return the sum of columns widths
	 */
	protected int getFixedColumnsWidths() {
		int width = 0;
		for(TableColumn col: fixedTable.getColumns()) {
			width += col.getWidth();
		}
		return width;
	}


	protected void hookTables() {
		this.fixedTable.addListener(SWT.Selection,
				new Listener() {
			int cachedCount = -1;

			public void handleEvent(Event event) {
				int[] selectionIndices = FixedColumnTable.this.fixedTable.getSelectionIndices();


				if (this.cachedCount != -1
						&& selectionIndices.length < this.cachedCount) {
					FixedColumnTable.this.scrollingTable.setSelection(selectionIndices);
				}

				if(selectionIndices.length > 0){
				ActivatorData.getInstance().setSelectionVueTabulaire(selectionIndices[0]);
				}

				this.cachedCount = selectionIndices.length;
			}
		});

		this.scrollingTable.addListener(SWT.Selection,
				new Listener() {
			int cachedCount = -1;

			public void handleEvent(Event event) {

				int[] selectionIndices = FixedColumnTable.this.scrollingTable.getSelectionIndices();

				if (this.cachedCount != -1
						//TAG MIGRATION && selectionIndices.length < this.cachedCount) {
						&& selectionIndices.length <= this.cachedCount) {
					FixedColumnTable.this.fixedTable.setSelection(selectionIndices);
				}
				ActivatorData.getInstance().setSelectionVueTabulaire(selectionIndices[0]);
				this.cachedCount = selectionIndices.length;
			}
		});

		// On Windows, the selection is gray if the table does not have focus.
		// To make both tables appear in focus, draw teh selection background
		// here.
		// This part only works on version 3.2 or later.
		Listener eraseListener = new Listener() {

			public void handleEvent(Event event) {

				if ((event.detail & SWT.SELECTED) != 0) {
					GC gc = event.gc;
					Rectangle rect = event.getBounds();

					if (notEquivalent || notEquivalentForSearch) {
						gc.setForeground(getDisplay().getSystemColor(
								SWT.COLOR_LIST_SELECTION_TEXT));
						gc.setBackground(getDisplay().getSystemColor(
								SWT.COLOR_RED));
						gc.fillRectangle(rect);
						event.detail &= ~SWT.SELECTED;
					}else{
						gc.setForeground(getDisplay().getSystemColor(
								SWT.COLOR_LIST_SELECTION_TEXT));
						gc.setBackground(getDisplay().getSystemColor(
								SWT.COLOR_LIST_SELECTION));
						gc.fillRectangle(rect);
						event.detail &= ~SWT.SELECTED;
					}

					//fixedTable.setTopIndex(scrollingTable.getTopIndex());
					if (event.index == 0) {
						if (event.widget == FixedColumnTable.this.scrollingTable) {
							//FixedColumnTable.this.fixedTable.setTopIndex(FixedColumnTable.this.scrollingTable.getTopIndex());

							int[] scrollingIndices = FixedColumnTable.this.scrollingTable.getSelectionIndices();

							//FixedColumnTable.this.fixedTable.setSelection(scrollingIndices);

							int[] fixedIndices = FixedColumnTable.this.fixedTable.getSelectionIndices();

							if (scrollingIndices.length != fixedIndices.length) {
								FixedColumnTable.this.fixedTable.setSelection(scrollingIndices);

							} else {
								for (int i = 0; i < scrollingIndices.length; i++) {
									if (scrollingIndices[0] != fixedIndices[0]) {
										FixedColumnTable.this.fixedTable.setSelection(scrollingIndices);
										break;
									}
								}
							}
						} else {
							FixedColumnTable.this.scrollingTable.setTopIndex(FixedColumnTable.this.fixedTable.getTopIndex());

							int[] scrollingIndices = FixedColumnTable.this.scrollingTable.getSelectionIndices();
							int[] fixedIndices = FixedColumnTable.this.fixedTable.getSelectionIndices();
							//FixedColumnTable.this.scrollingTable.setSelection(fixedIndices);
							if (scrollingIndices.length != fixedIndices.length) {
								FixedColumnTable.this.scrollingTable.setSelection(fixedIndices);

							} else {
								for (int i = 0; i < fixedIndices.length; i++) {
									if (scrollingIndices[i] != fixedIndices[i]) {
										FixedColumnTable.this.scrollingTable.setSelection(fixedIndices);
										break;
									}
								}
							}
						}
					}
				}

			}
		};


		this.fixedTable.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent arg0) {
				notEquivalent = false;

			}

			public void mouseDown(MouseEvent arg0) {

				notEquivalent = false;


			}

			public void mouseUp(MouseEvent arg0) {
				notEquivalent = false;

			}

		});
		this.scrollingTable.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent arg0) {
				notEquivalent = false;

			}

			public void mouseDown(MouseEvent arg0) {
				notEquivalent = false;

			}

			public void mouseUp(MouseEvent arg0) {
				notEquivalent = false;

			}

		});
		this.fixedTable.addListener(SWT.EraseItem, eraseListener);
		this.scrollingTable.addListener(SWT.EraseItem,
				eraseListener);

		// Make vertical scrollbars scroll together
		ScrollBar vBarLeft = this.fixedTable.getVerticalBar();
		vBarLeft.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				FixedColumnTable.this.scrollingTable.setTopIndex(
						FixedColumnTable.this.fixedTable.getTopIndex());
			}
		});
		ScrollBar vBarRight = this.scrollingTable.getVerticalBar();
		vBarRight.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				FixedColumnTable.this.fixedTable.setTopIndex(
						FixedColumnTable.this.scrollingTable.getTopIndex());
			}
		});
	}

	public void setNotEquivalent(boolean incert){
		notEquivalent = incert;
	}
	

	public void setNotEquivalentForSearch(boolean b) {
		notEquivalentForSearch = b;
	}


	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.fixedTableViewer.addSelectionChangedListener(listener);
		this.scrollingTableViewer.addSelectionChangedListener(listener);
	}

	public void insert(Object element, int position) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.insert(element, position);
		this.scrollingTableViewer.insert(element, position);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void add(Object element) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.add(element);
		this.scrollingTableViewer.add(element);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void add(Object[] elements) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.add(elements);
		this.scrollingTableViewer.add(elements);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void remove(Object element) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.remove(element);
		this.scrollingTableViewer.remove(element);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void remove(Object[] elements) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.remove(elements);
		this.scrollingTableViewer.remove(elements);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void setHeaderVisible(boolean show) {
		getFixedTable().setHeaderVisible(show);
		getScrollingTable().setHeaderVisible(show);
	}

	public void setLinesVisible(boolean show) {
		getFixedTable().setLinesVisible(show);
		getScrollingTable().setLinesVisible(show);
	}

	public Table getFixedTable() {
		return this.fixedTable;
	}

	public Table getScrollingTable() {
		return this.scrollingTable;
	}

	public void setLabelProvider(ILabelProvider provider) {
		this.fixedTableViewer.setLabelProvider(provider);
		this.scrollingTableViewer.setLabelProvider(new WrappedLabelProvider(
				provider));
	}

	public void setContentProvider(IStructuredContentProvider provider) {
		this.fixedTableViewer.setContentProvider(provider);
		this.scrollingTableViewer.setContentProvider(provider);
	}


	public IContentProvider getContentProvider() {
		return this.fixedTableViewer.getContentProvider();
	}

	public IBaseLabelProvider getLabelProvider() {
		return this.fixedTableViewer.getLabelProvider();
	}

	public void setInput(Object input) {
		//try {
			this.fixedTable.setRedraw(false);
			this.scrollingTable.setRedraw(false);

			try {
				this.fixedTableViewer.setInput(input);
			} catch (RuntimeException e) {
				e.printStackTrace();				
			}

			try {
				this.scrollingTableViewer.setInput(input);
			} catch (RuntimeException e) {
				e.printStackTrace();	
			}

			try{
				this.fixedTable.setRedraw(true);
				this.scrollingTable.setRedraw(true);
			}catch (Exception e) {
				e.printStackTrace();
			}
	}

	public void refresh() {
		try{
			this.fixedTable.setRedraw(false);
			this.scrollingTable.setRedraw(false);

			if(this.fixedTableViewer!=null)
				this.fixedTableViewer.refresh();
			if(this.scrollingTableViewer!=null)
				this.scrollingTableViewer.refresh();

			this.fixedTable.setRedraw(true);
			this.scrollingTable.setRedraw(true);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}


	/**
	 * @param element
	 * @param updateLabels
	 * @see org.eclipse.jface.viewers.StructuredViewer#refresh(java.lang.Object, boolean)
	 */
	public void refresh(Object element, boolean updateLabels) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.refresh(element, updateLabels);
		this.scrollingTableViewer.refresh(element, updateLabels);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void refresh(boolean updateLabels) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.refresh(updateLabels);
		this.scrollingTableViewer.refresh(updateLabels);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);

	}

	public void addFilter(ViewerFilter filter) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.addFilter(filter);
		this.scrollingTableViewer.addFilter(filter);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void removeFilter(ViewerFilter filter) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.removeFilter(filter);
		this.scrollingTableViewer.removeFilter(filter);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void setCompa(ViewerComparator comparator) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.setComparator(comparator);
		this.scrollingTableViewer.setComparator(comparator);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);
	}

	public void refresh(Object element) {
		this.fixedTable.setRedraw(false);
		this.scrollingTable.setRedraw(false);

		this.fixedTableViewer.refresh(element);
		this.scrollingTableViewer.refresh(element);

		this.fixedTable.setRedraw(true);
		this.scrollingTable.setRedraw(true);

	}

	public void addListener(int eventType, Listener listener) {
		this.fixedTable.addListener(eventType, listener);
		this.scrollingTable.addListener(eventType, listener);
	}

	public int getSelectionIndex() {
		return this.fixedTable.getSelectionIndex();
	}

	public void setSelection(int index,DescripteurVariable descripteurVariable) {
		if(index < 0 || index >= this.fixedTable.getItemCount())
			return;
		this.fixedTable.deselectAll();
		this.scrollingTable.deselectAll();
		this.fixedTable.setSelection(index);
		this.scrollingTable.setSelection(index);
		this.refreshTable(index,descripteurVariable);
	}

	public TableItem[] getSelection() {
		return this.fixedTable.getSelection();
	}

	public void setTopIndex(int idx) {
		if(idx < 0 || idx >= this.fixedTable.getItemCount())
			idx = 0;
		this.fixedTable.setTopIndex(idx);
		this.scrollingTable.setTopIndex(idx);
	}

	protected class WrappedLabelProvider extends LabelProvider implements
	ILabelProvider, IFontProvider, IColorProvider, ITableLabelProvider,
	ITableColorProvider, ITableFontProvider {
		protected ILabelProvider labelProvider;

		protected IColorProvider colorProvider;

		protected IFontProvider fontProvider;

		protected ITableLabelProvider tableLabelProvider;

		protected ITableColorProvider tableColorProvider;

		protected ITableFontProvider tableFontProvider;

		public WrappedLabelProvider(ILabelProvider provider) {
			this.labelProvider = provider;

			if (provider instanceof IColorProvider) {
				this.colorProvider = (IColorProvider) provider;
			}

			if (provider instanceof IFontProvider) {
				this.fontProvider = (IFontProvider) provider;
			}

			if (provider instanceof ITableLabelProvider) {
				this.tableLabelProvider = (ITableLabelProvider) provider;
			}

			if (provider instanceof ITableColorProvider) {
				this.tableColorProvider = (ITableColorProvider) provider;
			}

			if (provider instanceof ITableFontProvider) {
				this.tableFontProvider = (ITableFontProvider) provider;
			}
		}

		public Image getImage(Object element) {
			return this.labelProvider.getImage(element);
		}

		public String getText(Object element) {
			return this.labelProvider.getText(element);
		}

		public Font getFont(Object element) {
			if (this.fontProvider != null) {
				this.fontProvider.getFont(element);
			}

			return null;
		}

		public Color getBackground(Object element) {
			if (this.colorProvider != null) {
				return this.colorProvider.getBackground(element);
			}

			return null;
		}

		public Color getForeground(Object element) {
			if (this.colorProvider != null) {
				return this.colorProvider.getForeground(element);
			}
			return null;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (this.tableLabelProvider != null) {
				return this.tableLabelProvider.getColumnImage(element, columnIndex
						+ FixedColumnTable.this.fixedTable.getColumnCount());
			}
			return getImage(element);
		}

		public String getColumnText(Object element, int columnIndex) {
			if (this.tableLabelProvider != null) {
				return this.tableLabelProvider.getColumnText(element, columnIndex
						+ FixedColumnTable.this.fixedTable.getColumnCount());
			}
			return getText(element);
		}

		public Color getBackground(Object element, int columnIndex) {
			if (this.tableColorProvider != null) {
				return this.tableColorProvider.getBackground(element, columnIndex
						+ FixedColumnTable.this.fixedTable.getColumnCount());
			}

			return getBackground(element);
		}

		public Color getForeground(Object element, int columnIndex) {
			if (this.tableColorProvider != null) {
				return this.tableColorProvider.getForeground(element, columnIndex
						+ FixedColumnTable.this.fixedTable.getColumnCount());
			}

			return getForeground(element);
		}

		public Font getFont(Object element, int columnIndex) {
			if (this.tableFontProvider != null) {
				int fixedColumnTableColCount = FixedColumnTable.this.fixedTable.getColumnCount();
				
				return this.tableFontProvider.getFont(element, columnIndex
						+ fixedColumnTableColCount);
			}

			return getFont(element);
		}
	}

	public void setMenu(Menu menu) {
		super.setMenu(menu);
		this.fixedTable.setMenu(menu);
		this.scrollingTable.setMenu(menu);
	}

	public void refreshTable(int msg,DescripteurVariable descr){

	}

	public boolean isNotEquivalent() {
		return notEquivalent;
	}

	public boolean isNotEquivalentForSearch() {
		return notEquivalentForSearch;
	}
}

