package com.faiveley.samng.principal.ihm.vues;

import java.util.Map;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

public class FixedColumnTableViewerEditingSupport extends EditingSupport {

	private CellEditor textEditor;
	private TableViewer tableViewer;
	private String nom;
	private int column;
	private String[] values;
	private boolean isCombo;
	private Map<String, AFiltreComposant> varNamesFilters;

	public FixedColumnTableViewerEditingSupport(ColumnViewer viewer,
			int column, String nom) {
		super(viewer);
		this.tableViewer = (TableViewer) viewer;
		this.column = column;
		this.nom = nom;
		this.textEditor = new TextCellEditor(tableViewer.getTable());
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		Row row = (Row) element;
		this.values = getValuesFromRow(row);
		this.isCombo = this.values.length > 1;
		if (isCombo) {
			return new ComboBoxCellEditor(tableViewer.getTable(), values);
		} else {
			return textEditor;
		}
	}

	@Override
	protected boolean canEdit(Object element) {
		Row row = (Row) element;
		this.values = getValuesFromRow(row);
		this.isCombo = this.values.length > 1;
		return isCombo;
	}

	@Override
	protected Object getValue(Object element) {
		if (element instanceof Row) {
			Row row = (Row) element;
			String value = row.getValue(column);
			if (isCombo) {
				return getValueIndex(value);
			} else {
				return value;
			}
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Row row = (Row) element;
		if (isCombo) {
			Integer index = (Integer) value;
			row.setValue(column, this.values[index]);
		}
		tableViewer.update(element, null);
	}

	public Map<String, AFiltreComposant> getVarNamesFilters() {
		return varNamesFilters;
	}

	public void setVarNamesFilters(Map<String, AFiltreComposant> varNamesFilters) {
		this.varNamesFilters = varNamesFilters;
	}

	private int getValueIndex(String value) {
		for (int i = 0; i < this.values.length; i++) {
			if (this.values[i].equals(value)) {
				return i;
			}
		}
		return -1;
	}

	public String[] getValuesFromRow(Row row) {
		Message message = (Message) row.getData();
		return VariableExplorationUtils.getFilteredValuesFromMessage(message, this.nom, this.varNamesFilters);
	}

	public void setCombo(boolean isCombo) {
		this.isCombo = isCombo;
	}

	public void setValues(String[] values) {
		this.values = values;
	}
}
