package com.faiveley.samng.principal.sm.data.enregistrement;


/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:54
 */
public class Enregistrement {

	/**
	 * taille en octet de l'enregistrement Le premier octet d'un enregistrement
	 * est toujours un code identificateur.
	 */
	private int longueurEnregistrement;
	private ListMessages messages;
	private ListMessages badMessages;

	public Enregistrement() {
	}

	public ListMessages getMessages() {
		return this.messages;
	}

	public void setMessages(ListMessages messages) {
		this.messages = messages;
	}

	/**
	 * taille en octet de l'enregistrement Le premier octet d'un enregistrement
	 * est toujours un code identificateur.
	 */
	public int getLongueurEnregistrement() {
		return this.longueurEnregistrement;
	}

	/**
	 * taille en octet de l'enregistrement Le premier octet d'un enregistrement
	 * est toujours un code identificateur.
	 * 
	 * @param newVal
	 */
	public void setLongueurEnregistrement(int newVal) {
		this.longueurEnregistrement = newVal;
	}

	public boolean ajouter(Message msg) {
		if (msg == null)
			return false;
		if (this.messages == null) {
			this.messages = new ListMessages();
		}

		return this.messages.add(msg);
	}

	public Message getEnfant(int indice) {
		return this.messages != null ? this.messages.get(indice) : null;
	}

	public boolean supprimer(Message msg) {
		if (this.messages == null || msg == null)
			return false;
		return this.messages.remove(msg);
	}

	public int getGoodMessagesCount() {
		return this.messages != null ? this.messages.size() : 0;
	}

	public int getBadMessagesCount() {
		return this.badMessages != null ? this.badMessages.size() : 0;
	}

	public Message getGoodMessage(int msgId) {
		return this.messages != null ? this.messages.getMessageById(msgId)
				: null;
	}

	public boolean ajouterBadMessage(Message msg) {
		if (msg == null)
			return false;

		if (this.badMessages == null) {
			this.badMessages = new ListMessages();
		}

		return this.badMessages.add(msg);
	}

	public boolean supprimerBadMessage(Message msg) {
		if (this.badMessages == null || msg == null)
			return false;

		return this.badMessages.remove(msg);
	}

	public ListMessages getBadMessages() {
		return badMessages;
	}

	public void setBadMessages(ListMessages badMessages) {
		this.badMessages = badMessages;
	}
}