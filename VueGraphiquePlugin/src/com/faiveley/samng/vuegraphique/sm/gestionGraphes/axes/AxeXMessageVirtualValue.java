package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AxeXMessageVirtualValue {
	private int msgId;
	private double virtualValue;
	
	public AxeXMessageVirtualValue() {
		
	}
	
	public AxeXMessageVirtualValue(int msgId, double virtualValue) {
		this.msgId = msgId;
		this.virtualValue = virtualValue;
	}

	public int getMsgId() {
		return msgId;
	}

	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}

	public double getVirtualValue() {
		return virtualValue;
	}

	public void setVirtualValue(double virtualValue) {
		this.virtualValue = virtualValue;
	}
}
