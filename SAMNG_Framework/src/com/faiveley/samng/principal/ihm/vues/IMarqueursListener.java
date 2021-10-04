package com.faiveley.samng.principal.ihm.vues;


/**
 * 
 * @author Cosmin Udroiu
 * 
 * Markers support for data views. If a view supports markers displaying 
 * then it should implement this interface
 *
 */
public interface IMarqueursListener {

	/**
	 * Notifies that the list of marker messages IDs changed 
	 * @param msgIds
	 */
	public void marquersListeChangement(int[] msgIds, int[] oldMsgIds);
	
	/**
	 * Notifies the view that a marker was set for the given message ID
	 * @param msgId the message ID
	 */
	public void marqueurAjoutee(int msgId);

	/**
	 * Notifies the view that a marker was removed for the given message ID
	 * @param msgId the message ID
	 */
	public void marqueurEffacee(int msgId);
}
