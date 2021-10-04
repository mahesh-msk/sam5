package com.faiveley.samng.principal.ihm.vues;

/**
 * Implemetation of a table row 
 * @author meggy
 *
 */
public class Row {
	
	//the data that is behind the row
	private Object data;
	//the data behind each cell of the row
	private Object[] cellsData;
	//the validity state of the cell
	private boolean[] invalidCellsData;
	
	//the labels for the row
	private String[] strings;
	
	private boolean[] volatiles;
	
	/**
	 * Constructor
	 * @param no	number of labels
	 */
	public Row(int no) {
		this.strings = new String[no];
		this.volatiles = new boolean[no];
	}
	
	public void setCellsData(Object[] cellsData) {
		this.cellsData = cellsData;
	}

	/**
	 * Sets the value for the specified index 
	 * @param index		the index
	 * @param str		the label
	 */
	public void setValue(int index, String str) {
		this.strings[index] = str;
	}

	/**
	 * Gets the value at the specified index
	 * @param index		the index
	 * @return			the label
	 */
	public String getValue(int index) {
		return this.strings[index];
	}
	
	/**
	 * 
	 */
	public String[] getStrings() {
		return this.strings;
	}
	
	/**
	 * Sets the data for the cell at the specified index 
	 * @param index		the index
	 * @param obj		the data object
	 */
	public void setCellData(int index, Object obj) {
		if (this.cellsData == null) {
			this.cellsData = new Object[getNbData()];
		}
		this.cellsData[index] = obj;
	}

	/**
	 * Gets the data for the cell at the specified index
	 * @param index		the index
	 * @return			the data for the cell
	 */
	public Object getCellData(int index) {
		if (this.cellsData == null) {
			return null;
		}
		return this.cellsData[index];
	}

	/**
	 * Sets the cell valid/invalid state for the cell at the specified index.
	 * The components interested in this value should set this accordingly
	 * @param index		the index
	 * @param isInvalid		the validity state
	 */
	public void setCellInvalid(int index, boolean isInvalid) {
		if (invalidCellsData == null) {
			this.invalidCellsData = new boolean[getNbData()];
		}
		this.invalidCellsData[index] = isInvalid;
	}

	/**
	 * Gets the valid/invalid state for the cell at the specified index
	 * The components interested in this value should set this accordingly
	 * 
	 * @param index		the index
	 * @return			the validity state of the cell
	 */
	public boolean isCellInvalid(int index) {
		return invalidCellsData != null && this.invalidCellsData[index];
	}

	/**
	 * Sets the data
	 * @param data
	 */
	public void setData(Object data) {
		this.data = data;
	}
	
	/**
	 * Gets the data
	 * @return
	 */
	public Object getData() {
		return this.data;
	}
	/**
	 * Retourne le nombre de données dans une ligne
	 *  
	 * @return integer
	 */
	public int getNbData(){
		return this.strings.length;
	}
	
	public boolean isVolatile(int index) {
		return this.volatiles[index];
	}
	
	public boolean[] getVolatiles() {
		return this.volatiles;
	}
	
	public void setVolatileSeveralValues(int index, boolean value) {
		this.volatiles[index] = value;
	}
}
