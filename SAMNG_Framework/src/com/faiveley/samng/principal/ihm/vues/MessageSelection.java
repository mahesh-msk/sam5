package com.faiveley.samng.principal.ihm.vues;

import org.eclipse.jface.viewers.ISelection;

/**
 * 
 * @author Cosmin Udroiu
 */
public class MessageSelection implements ISelection {
	private int[] messageIds = null;
	private boolean userSentSelection = false;
	private boolean shouldNotTriggerAudio = false;
	
	public MessageSelection() {}
	
	public MessageSelection(int msgId) {
		setMessageId(msgId);
	}
	
	public void setMessageId(int msgId) {
		this.messageIds = new int[1];
		this.messageIds[0] = msgId;
	}
	
	public int getMessageId() {
		return isEmpty() ? -1 : this.messageIds[0];
	}
	
	public void setMessagesIds(int[] msgsIds) {
		this.messageIds = msgsIds;
	}
	
	public int[] getMessagesIds() {
		int[] arrRet;
		
		if(isEmpty()) {
			arrRet = new int[0];
		} else {
			arrRet = new int[this.messageIds.length];
			System.arraycopy(this.messageIds, 0, arrRet, 0, this.messageIds.length);
		}
		
		return this.messageIds;
	}

	public boolean isEmpty() {
		return this.messageIds == null || this.messageIds.length == 0;
	}
	
	public void setUserSentSelection(boolean userSent) {
		this.userSentSelection = userSent;
	}
	
	public boolean getUserSendSelection() {
		return this.userSentSelection;
	}
	
	public boolean shouldNotTriggerAudio() {
		return this.shouldNotTriggerAudio;
	}

	public void setShouldNotTriggerAudio(boolean shouldTriggerAudio) {
		this.shouldNotTriggerAudio = shouldTriggerAudio;		
	}
}
