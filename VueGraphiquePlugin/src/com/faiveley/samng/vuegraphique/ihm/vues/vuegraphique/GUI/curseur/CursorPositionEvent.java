package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class CursorPositionEvent {
	private int sourceGrapheNr;
	private int xPos;
	private int yPos;
	private Message firstMessage;
	private Message currentMessage;
	private boolean isDoubleClick;
	
	
	
	public CursorPositionEvent(Message firstMessage, Message msg, int abscisse,
			int numero, boolean isDoubleClick2) {
		this.firstMessage=firstMessage;		//cursorEvent.firstMessage = messages.get(0);
		setCurrentMessage(msg);
//		this.currentMessage = msg;//		cursorEvent.currentMessage = msg;
		this.xPos = abscisse;//		cursorEvent.xPos = closestMsgPixelInfo.getAbscisse();//xPos;
		this.sourceGrapheNr = numero;//		cursorEvent.sourceGrapheNr = grapheCourante.getNumero();
		this.isDoubleClick = isDoubleClick2;//		cursorEvent.isDoubleClick = isDoubleClick;
	}
	
	public CursorPositionEvent(Message firstMessage, Message msgSelectionner,int getpositionCurseur) {
		this.firstMessage=firstMessage;
		setCurrentMessage(msgSelectionner);
//		this.currentMessage = msgSelectionner;
		this.xPos = getpositionCurseur;
	}
	
	public int getSourceGrapheNr() {
		return sourceGrapheNr;
	}
	public void setSourceGrapheNr(int sourceGrapheNr) {
		this.sourceGrapheNr = sourceGrapheNr;
	}
	public int getxPos() {
		return xPos;
	}
	public void setxPos(int xPos) {
		this.xPos = xPos;
	}
	public int getyPos() {
		return yPos;
	}
	public void setyPos(int yPos) {
		this.yPos = yPos;
	}
	public Message getFirstMessage() {
		return firstMessage;
	}
	public void setFirstMessage(Message firstMessage) {
		this.firstMessage = firstMessage;
	}
	public Message getCurrentMessage() {
		return currentMessage;
	}
	public void setCurrentMessage(Message currentMessage) {
		this.currentMessage = currentMessage;
	}
	public boolean isDoubleClick() {
		return isDoubleClick;
	}
	public void setDoubleClick(boolean isDoubleClick) {
		this.isDoubleClick = isDoubleClick;
	}
}
