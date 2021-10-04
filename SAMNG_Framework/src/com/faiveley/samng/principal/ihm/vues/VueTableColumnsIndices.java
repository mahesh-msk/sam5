package com.faiveley.samng.principal.ihm.vues;


/**
 * A class encapsulating the columns positions in a VueTable.
 * Various plugings can use a subset of these members 
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueTableColumnsIndices {
	protected int posColRelDist = -1;
	protected int posColFlag = -1;
	protected int posColTime = -1;
	protected int posColRelTime = -1;
	protected int posColCorTime = -1;
	protected int posColCorSpeed = -1;
	protected int posColCorDist = -1;
	protected int posColAccDist = -1;

	protected int posColEvent = -1;	//used by vue liste
	
	protected int lastFixedColumn = -1;

	public int getLastFixedColumn() {
		return lastFixedColumn;
	}

	public void setLastFixedColumn(int lastFixedColumn) {
		this.lastFixedColumn = lastFixedColumn;
	}

	public int getPosColCorDist() {
		return posColCorDist;
	}

	public void setPosColCorDist(int posColCorDist) {
		this.posColCorDist = posColCorDist;
	}
	
	public int getPosColAccDist() {
		return posColAccDist;
	}

	public void setPosColAccDist(int posColAccDist) {
		this.posColAccDist = posColAccDist;
	}

	public int getPosColCorSpeed() {
		return posColCorSpeed;
	}

	public void setPosColCorSpeed(int posColCorSpeed) {
		this.posColCorSpeed = posColCorSpeed;
	}

	public int getPosColCorTime() {
		return posColCorTime;
	}

	public void setPosColCorTime(int posColCorTime) {
		this.posColCorTime = posColCorTime;
	}

	public int getPosColEvent() {
		return posColEvent;
	}

	public void setPosColEvent(int posColEvent) {
		this.posColEvent = posColEvent;
	}

	public int getPosColFlag() {
		return posColFlag;
	}

	public void setPosColFlag(int posColFlag) {
		this.posColFlag = posColFlag;
	}

	public int getPosColRelDist() {
		return posColRelDist;
	}

	public void setPosColRelDist(int posColRelDist) {
		this.posColRelDist = posColRelDist;
	}

	public int getPosColRelTime() {
		return posColRelTime;
	}

	public void setPosColRelTime(int posColRelTime) {
		this.posColRelTime = posColRelTime;
	}

	public int getPosColTime() {
		return posColTime;
	}

	public void setPosColTime(int posColTime) {
		this.posColTime = posColTime;
	}
}
