package com.faiveley.samng.principal.sm.segments.ruptures;

import java.util.HashMap;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.tables.TablesRupturesExplorer;

/**
 * @author Olivier
 * @version 1.0
 * @created 09-janv.-2008 18:39:35
 */
public class TableRuptures {

	/**
	 * liste des id de messages correspondant à des ruptures de distance
	 */
	protected HashMap<Integer, TypeRupture> mapRupturesDistance;
	/**
	 * liste des id de messages correspondant à des ruptures de temps
	 */
	protected HashMap<Integer, TypeRupture> mapRupturesTemps;

	private static TableRuptures instance = new TableRuptures();
	
	/**
	 * Private constructor. Singleton class
	 *
	 */
	protected TableRuptures(){
	}
	
	/**
	 * Returns the sigle instance of this class
	 * Class is singleton
	 * @return	the instance
	 */
	public static TableRuptures getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return TablesRupturesExplorer.getInstance();
		}
		return instance;
	}
	

	/**
	 * ajoute un id de message correspondant à une rupture de temps
	 * 
	 * @param idMsg
	 */
	public boolean ajouterRuptureDistance(int idMsg, TypeRupture type){
		if (this.mapRupturesDistance == null) {
			this.mapRupturesDistance = new HashMap<Integer, TypeRupture>();
		}
		return this.mapRupturesDistance.put(idMsg, type) != null;
	}

	/**
	 * ajoute un id de message correspondant à une rupture de temps
	 * 
	 * @param idMsg
	 */
	public boolean ajouterRuptureTemps(int idMsg, TypeRupture type){
		if (this.mapRupturesTemps == null) {
			this.mapRupturesTemps = new HashMap<Integer, TypeRupture>();
		}
		return this.mapRupturesTemps.put(idMsg, type) != null;
	}

	/**
	 * Returns the distance ruptures
	 * @return 	map<message id, rupture>
	 */
	public HashMap<Integer, TypeRupture> getListeRupturesDistance(){
		return this.mapRupturesDistance;
	}

	/**
	 * Returns the time ruptures
	 * @return	map<message id, rupture>
	 */
	public HashMap<Integer, TypeRupture> getListeRupturesTemps(){
		return this.mapRupturesTemps;
	}

	/**
	 * Sets the distance ruptures
	 * @param newVal	map<message id, rupture>
	 */
	public void setMapRupturesDistance(HashMap<Integer, TypeRupture> newVal){
		this.mapRupturesDistance = newVal;
	}

	/**
	 * Sets the time ruptures
	 * @param newVal	map<message id, rupture>
	 */
	public void setMapRupturesTemps(HashMap<Integer, TypeRupture> newVal){
		this.mapRupturesTemps = newVal;
	}
	
	/**
	 * Returns the type of time rupture for a message  
	 * @param idMsg		the message id
	 * @return			the type of rupture or null if it's not a rupture associated
	 */
	public TypeRupture getRuptureTime(int idMsg) {
		return this.mapRupturesTemps != null 
			? this.mapRupturesTemps.get(idMsg) : null;
	}

	/**
	 * Returns the type of distance rupture for a message  
	 * @param idMsg		the message id
	 * @return			the type of rupture or null if it's not a rupture associated
	 */
	public TypeRupture getRuptureDistance(int idMsg) {
		return this.mapRupturesDistance != null 
			? this.mapRupturesDistance.get(idMsg) : null;
	}
	
	/**
	 * Clears the maps of distance and time ruptures
	 *
	 */
	public void clear() {
		if (this.mapRupturesDistance != null) {
			this.mapRupturesDistance.clear();
		}
		if (this.mapRupturesTemps != null) {
			this.mapRupturesTemps.clear();
		}
	}
}