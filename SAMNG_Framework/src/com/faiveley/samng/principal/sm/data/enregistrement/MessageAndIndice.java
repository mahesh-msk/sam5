package com.faiveley.samng.principal.sm.data.enregistrement;

public class MessageAndIndice {

	private int ID;
	private Message msg;
	
	public MessageAndIndice(int id, Message msg) {
		super();
		ID = id;
		this.msg = msg;
	}
	
	public int getID() {
		return ID;
	}
	public void setID(int id) {
		ID = id;
	}
	public Message getMsg() {
		return msg;
	}
	public void setMsg(Message msg) {
		this.msg = msg;
	}
}
