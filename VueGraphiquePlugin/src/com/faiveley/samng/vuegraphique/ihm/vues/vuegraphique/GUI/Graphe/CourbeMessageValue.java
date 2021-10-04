package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class CourbeMessageValue {
	private int msgId;
	private Float variableValue;
	private boolean isPropagated;
	
	public CourbeMessageValue() {
	}
	
	public CourbeMessageValue(int arrIdx, int msgId, 
		Float variableValue, boolean isPropagated) {
		this.msgId = msgId;
		this.variableValue = variableValue;
		this.isPropagated = isPropagated;
	}
	
	public void setValue(Float variableValue) {
		this.variableValue = variableValue;
	}
	
	public Float getValue() {
		return this.variableValue;
	}
	
	public boolean isPropagated() {
		return this.isPropagated;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
}
