package com.faiveley.samng.principal.ihm.listeners;

import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;

/**
 * Interface for repere change listener 
 * @author meggy
 *
 */
public interface IRepereChangedListener {

	/**
	 * When a repere is added/modified this method notifies the listeners 
	 * @param reper		the reperes that are added or modified
	 */
	public void onRepereAdded(TypeRepere... reper);

	/**
	 * When a repere is removed this method notifies the listeners 
	 * @param reper		the reperes that are removed
	 */
	public void onRepereRemoved(TypeRepere... reper);
}
