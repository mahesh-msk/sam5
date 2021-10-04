package com.faiveley.samng.principal.ihm.vues;

import com.faiveley.samng.principal.data.ActivatorData;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public abstract class AVueTableLabelProvider extends ATableLabelProvider {
	protected int lastFixedColumn = -1;
	protected VueData vueData;
	protected int posColCorDist = -1;
	protected int posColCorTemp = -1;
	
	public AVueTableLabelProvider() {
		this.vueData = ActivatorData.getInstance().getVueData();
	}
	
	public abstract void setColumnIndices(VueTableColumnsIndices columnsIndices);
	
	/**
	 * Check if the column index represents a column that will display data or
	 * is one of the columns that are artificially inserted in order to avoid 
	 * SWT table bug for the first column that is not displayed in VIRTUAL mode for
	 * TableFixedViewer
	 * 
	 * @param columnIndex
	 * @return
	 */
	protected boolean isDisplayableColumnIndex(int columnIndex) {
		if(columnIndex == 0 || columnIndex == this.lastFixedColumn + 2)
			return false;
		return true;
	}
	
	/**
	 * Translate the column index to a row column index (as we might 
	 * have columns that do not display data but are artificially inserted in order to avoid 
	 * SWT table bug for the first column that is not displayed in VIRTUAL mode for
	 * TableFixedViewer
	 * @param columnIndex
	 * @return
	 */
	protected int getCorrectedColumnIndex(int columnIndex) {
		if(columnIndex <= this.lastFixedColumn + 1) {
			columnIndex--;
		} else {
			columnIndex -=2;
		}
		return columnIndex;
	}
}
