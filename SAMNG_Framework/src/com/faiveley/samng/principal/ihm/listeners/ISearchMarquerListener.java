package com.faiveley.samng.principal.ihm.listeners;

/**
 * Interface for search by marquer listener 
 * @author meggy
 *
 */
public interface ISearchMarquerListener {

	/**
	 * Method to inform the listeners that is searched by a marquer name
	 * @param next		flag to say that must be selected for next or previous marquer	 
	 * @return			the marquer name
	 */
	public String onSelectedMarquerNomChange(boolean next);

	/**
	 * Method to inform the listeners that is searched by a marquer comment
	 * @param next		flag to say that must be selected for next or previous marquer	 
	 * 
	 */
	public void onSelectedMarquerCommentChange(String comment, boolean next);
}
