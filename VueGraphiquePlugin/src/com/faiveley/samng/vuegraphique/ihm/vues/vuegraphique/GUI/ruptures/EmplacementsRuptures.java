package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;

import java.util.ArrayList;

public class EmplacementsRuptures {
	private ArrayList<String[]> listTimeBreaksPixels;
	private ArrayList<String[]> listDistanceBreaksPixels;
	
	private static final EmplacementsRuptures INSTANCE = new EmplacementsRuptures();

    /**
     * La présence d'un constructeur privé supprime
     * le constructeur public par défaut.
     */
    private EmplacementsRuptures() {
    	listTimeBreaksPixels = new ArrayList<String[]>();
    	listDistanceBreaksPixels = new ArrayList<String[]>();
    }
    
    
    /**
     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
     * Retourne l'instance du singleton.
     */
    public static EmplacementsRuptures getInstance() {
        return INSTANCE;
    }


	public ArrayList<String[]> getListDistanceBreaksPixels() {
		return listDistanceBreaksPixels;
	}

	public ArrayList<String[]> getListTimeBreaksPixels() {
		return listTimeBreaksPixels;
	}
}
