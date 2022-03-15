package com.faiveley.samng.vueliste.ihm.vues.vueliste;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;

@Deprecated
public abstract class FixedColumnTableVueListe extends Composite {
	public FixedColumnTableVueListe(Composite parent, int style) {
		super(parent, SWT.NONE);
	}
	
	protected TableViewer fixedTableViewer;
	protected TableViewer scrollingTableViewer;
	protected Table fixedTable;
	protected Table scrollingTable;
	
	protected  boolean notEquivalent = false;
	protected  boolean notEquivalentForSearch = false;
	
	public void onColumnsAdded() {
		TableColumn[] columns = this.fixedTable.getColumns();
		for(TableColumn col: columns) {
			col.addControlListener(new ControlAdapter() {
				public void controlResized(ControlEvent event) {
					layout();
				}
			});
		}
	}
	
	/**
	 * Computes the widths of the columns from the scrolling table.
	 * The width of the dummy column is not taken into consideration
	 * 
	 * @return the sum of columns widths
	 */
	protected int getFixedColumnsWidths() {
		int width = 0;
		
		for (TableColumn col: fixedTable.getColumns()) {
			width += col.getWidth();
		}
		
		return width;
	}

	protected void hookTables() {
		this.fixedTable.addListener(SWT.Selection, new Listener() {
				public void handleEvent(Event event) {
					int[] selectionIndices = FixedColumnTableVueListe.this.fixedTable.getSelectionIndices();
					ActivatorData.getInstance().setSelectionVueListe(selectionIndices[0]);
				}
			}
		);

		// On Windows, the selection is gray if the table does not have focus.
		// To make both tables appear in focus, draw teh selection background here.
		// This part only works on version 3.2 or later.
		Listener eraseListener = new Listener() {
			public void handleEvent(Event event) {
				if ((event.detail & SWT.SELECTED) != 0) {
					GC gc = event.gc;
					Rectangle rect = event.getBounds();
					
					if (notEquivalent) {
						gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
						gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));
						gc.fillRectangle(rect);
						event.detail &= ~SWT.SELECTED;
					} else {
						gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION_TEXT));
						gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_LIST_SELECTION));
						gc.fillRectangle(rect);
						event.detail &= ~SWT.SELECTED;
					}
				}
			}
		};
			
		this.fixedTable.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent arg0) {
				notEquivalent = false;
			}

			public void mouseDown(MouseEvent arg0) {
				ActivatorVueListe.getDefault().setVueFocus(ActivatorVueListe.FOCUS_VUE_LISTE);
				notEquivalent = false;
			}

			public void mouseUp(MouseEvent arg0) {
				notEquivalent = false;
			}
		});
		
		this.fixedTable.addListener(SWT.EraseItem, eraseListener);
	}

	public void setNotEquivalent(boolean incert){
		notEquivalent = incert;
	}
	
	public void setNotEquivalentForSearch(boolean incert){
		notEquivalentForSearch = incert;
	}
	
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		this.fixedTableViewer.addSelectionChangedListener(listener);
	}

	public void insert(Object element, int position) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.insert(element, position);		
		this.fixedTable.setRedraw(true);
	}
	
	public void add(Object element) {
		this.fixedTable.setRedraw(false);
		this.fixedTableViewer.add(element);		
		this.fixedTable.setRedraw(true);
	}
	
	public void add(Object[] elements) {
		this.fixedTable.setRedraw(false);	
		this.fixedTableViewer.add(elements);		
		this.fixedTable.setRedraw(true);
	}
	
	public void remove(Object element) {
		this.fixedTable.setRedraw(false);	
		this.fixedTableViewer.remove(element);		
		this.fixedTable.setRedraw(true);
	}
	
	public void remove(Object[] elements) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.remove(elements);		
		this.fixedTable.setRedraw(true);
	}
	
	public void setHeaderVisible(boolean show) {
		getFixedTable().setHeaderVisible(show);
	}

	public void setLinesVisible(boolean show) {
		getFixedTable().setLinesVisible(show);
	}

	public Table getFixedTable() {
		return this.fixedTable;
	}

	public Table getScrollingTable() {
		return this.scrollingTable;
	}

	public void setLabelProvider(ILabelProvider provider) {
		this.fixedTableViewer.setLabelProvider(provider);
	}

	public void setContentProvider(IStructuredContentProvider provider) {
		this.fixedTableViewer.setContentProvider(provider);
	}

	public IContentProvider getContentProvider() {
		return this.fixedTableViewer.getContentProvider();
	}

	public IBaseLabelProvider getLabelProvider() {
		return this.fixedTableViewer.getLabelProvider();
	}

	public void setInput(Object input) {
		this.fixedTable.setRedraw(false);
		
		try {
			this.fixedTableViewer.setInput(input);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
		this.fixedTable.setRedraw(true);
	}

	public void refresh() {
		this.fixedTable.setRedraw(false);
		
		if (this.fixedTableViewer != null) {
			this.fixedTableViewer.refresh();
		}
		
		this.fixedTable.setRedraw(true);		
	}
	
	/**
	 * @param element
	 * @param updateLabels
	 * @see org.eclipse.jface.viewers.StructuredViewer#refresh(java.lang.Object, boolean)
	 */
	public void refresh(Object element, boolean updateLabels) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.refresh(element, updateLabels);		
		this.fixedTable.setRedraw(true);
	}

	public void refresh(boolean updateLabels) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.refresh(updateLabels);		
		this.fixedTable.setRedraw(true);		
	}
	
	public void addFilter(ViewerFilter filter) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.addFilter(filter);		
		this.fixedTable.setRedraw(true);
	}
	
	public void removeFilter(ViewerFilter filter) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.removeFilter(filter);		
		this.fixedTable.setRedraw(true);
	}
	
	public void setCompa(ViewerComparator comparator) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.setComparator(comparator);		
		this.fixedTable.setRedraw(true);
	}
	
	public void refresh(Object element) {
		this.fixedTable.setRedraw(false);		
		this.fixedTableViewer.refresh(element);		
		this.fixedTable.setRedraw(true);		
	}
	
	public void addListener(int eventType, Listener listener) {
		this.fixedTable.addListener(eventType, listener);
	}
	
	public int getSelectionIndex() {
		return this.fixedTable.getSelectionIndex();
	}
	
	public boolean setSelection(int index, DescripteurVariable descripteurVariable) {
		if (index < 0 || index >= this.fixedTable.getItemCount()) {
			return false;
		}
		
		this.fixedTable.deselectAll();
		this.fixedTable.setSelection(index);
		this.refreshTable(descripteurVariable);
		
		return true;
	}
	
	public TableItem[] getSelection() {
		return this.fixedTable.getSelection();
	}
	
	public void setTopIndex(int idx) {
		if (idx < 0 || idx >= this.fixedTable.getItemCount()) {
			idx = 0;
		}
		
		this.fixedTable.setTopIndex(idx);
	}
	
	protected class WrappedLabelProvider extends LabelProvider implements ILabelProvider, IFontProvider, IColorProvider, ITableLabelProvider, ITableColorProvider, ITableFontProvider {
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
				return this.tableLabelProvider.getColumnImage(element, columnIndex + FixedColumnTableVueListe.this.fixedTable.getColumnCount());
			}
			
			return getImage(element);
		}

		public String getColumnText(Object element, int columnIndex) {
			if (this.tableLabelProvider != null) {
				return this.tableLabelProvider.getColumnText(element, columnIndex + FixedColumnTableVueListe.this.fixedTable.getColumnCount());
			}
			
			return getText(element);
		}

		public Color getBackground(Object element, int columnIndex) {
			if (this.tableColorProvider != null) {
				return this.tableColorProvider.getBackground(element, columnIndex + FixedColumnTableVueListe.this.fixedTable.getColumnCount());
			}

			return getBackground(element);
		}

		public Color getForeground(Object element, int columnIndex) {
			if (this.tableColorProvider != null) {
				return this.tableColorProvider.getForeground(element, columnIndex + FixedColumnTableVueListe.this.fixedTable.getColumnCount());
			}

			return getForeground(element);
		}

		public Font getFont(Object element, int columnIndex) {
			if (this.tableFontProvider != null) {
				return this.tableFontProvider.getFont(element, columnIndex + FixedColumnTableVueListe.this.fixedTable.getColumnCount());
			}

			return getFont(element);
		}
	}
	
	@Override
	public void setMenu(Menu menu) {
		super.setMenu(menu);
		this.fixedTable.setMenu(menu);
	}
	
	public void refreshTable(DescripteurVariable descr) {}

	public boolean isNotEquivalent() {
		return notEquivalent;
	}

	public boolean isNotEquivalentForSearch() {
		return notEquivalentForSearch;
	}
}
