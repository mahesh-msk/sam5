package com.faiveley.samng.principal.ihm.listeners;

/**
 * Interface for search by event listener 
 * @author meggy
 *
 */
public interface ISearchEventListener {

	/**
	 * Listener method for searching the event
	 * @param eventName		the event to search
	 * @param next			flag to say which event to search (next or previous)
	 */
	public void onSearchEvent(String eventName, boolean next);
	
	/**
	 * Returns true if is an event selected in a view that is a Search Event Listener
	 * @return
	 */
	public boolean hasSelectedEvent();
}
