package com.faiveley.samng.vuebinaire.ihm.vues;

import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;

public class BinaryRow extends Row{
	
	public BinaryRow() {
		super(0);
	}
	
	public BinaryRow(int no) {
		super(no);
	}
	/**
	 * Implementation of a row
	 * @author meggy
	 *
	 */
	public int msgId;
	private int blockId;
	private String[] labels;
	private ErrorType error;
	private boolean isBlue;

	/**
	 * @return the blockId
	 */
	public int getBlockId() {
		return this.blockId;
	}
	/**
	 * @param blockId the blockId to set
	 */
	public void setBlockId(int blockId) {
		this.blockId = blockId;
	}
	/**
	 * @return the label
	 */
	public String[] getLabels() {
		return this.labels;
	}
	/**
	 * @param label the label to set
	 */
	public void setLabels(String[] labels) {
		this.labels = labels;
	}
	/**
	 * @return the msgId
	 */
	public int getMsgId() {
		return this.msgId;
	}
	/**
	 * @param msgId the msgId to set
	 */
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	/**
	 * @return the isValid
	 */
	public ErrorType getError() {
		return this.error;
	}
	/**
	 * @param isValid the isValid to set
	 */
	public void setError(ErrorType error) {
		this.error = error;
	}

	/**
	 * @return the isBlue
	 */
	public boolean isBlue() {
		return this.isBlue;
	}
	/**
	 * @param isBlue the isBlue to set
	 */
	public void setBlue(boolean isBlue) {
		this.isBlue = isBlue;
	}

}
