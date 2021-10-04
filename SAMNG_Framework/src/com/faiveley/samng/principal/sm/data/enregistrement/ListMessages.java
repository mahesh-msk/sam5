package com.faiveley.samng.principal.sm.data.enregistrement;

import java.util.ArrayList;

public class ListMessages extends ArrayList<Message> {
	private static final long serialVersionUID = -253682056489307067L;

	public ListMessages() {
	}

	public int getIndiceMessageById(int msgId) {
		int min = 0;
		int max = size() - 1;
		
		// recherche dychotomique sur les messageId pour trouver l'indice
		while (min <= max) {
			int middle = (max + min) / 2;
			int middleId = this.get(middle).getMessageId();
			if (msgId == middleId) {
				return middle;
			} else if (middleId < msgId) {
				min = middle + 1;
			} else {
				max = middle - 1;
			}
		}
		
		return -1;
	}

	public Message getMessageByIdExact(int id) {
		int i = 0;
		boolean trouve = false;
		Message msg = null;
		while (!trouve && i < this.size()) {
			if (this.get(i).getMessageId() == id) {
				trouve = true;
				msg = this.get(i);
			}
			i++;
		}
		return msg;
	}
	
	public MessageAndIndice getMessageAndIndiceByIdExact(int id) {
		int i = 0;
		boolean trouve = false;
		Message msg = null;
		while (!trouve && i < this.size()) {
			if (this.get(i).getMessageId() == id) {
				trouve = true;
				msg = this.get(i);
			}
			i++;
		}
		return new MessageAndIndice(i-1,msg);
	}

	public Message getMessageById(int ID) {
		int indice = getIndiceMessageById(ID);
		return (indice != -1 ? this.get(indice) : null);
	}

}
