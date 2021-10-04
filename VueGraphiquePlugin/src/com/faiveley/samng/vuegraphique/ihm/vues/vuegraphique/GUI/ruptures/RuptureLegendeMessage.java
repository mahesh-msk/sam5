package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;


public class RuptureLegendeMessage {
	
	boolean displayTimeRuptureMsg=false;
	boolean displayDistanceRuptureMsg=false;
	
	private static final RuptureLegendeMessage INSTANCE = new RuptureLegendeMessage();

    /**
     * La présence d'un constructeur privé supprime
     * le constructeur public par défaut.
     */
    private RuptureLegendeMessage() {

    }
    
    
    /**
     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
     * Retourne l'instance du singleton.
     */
    public static RuptureLegendeMessage getInstance() {
        return INSTANCE;
    }


	public boolean isDisplayTimeRuptureMsg() {
		return displayTimeRuptureMsg;
	}


	public void setDisplayTimeRuptureMsg(boolean display) {
		this.displayTimeRuptureMsg = display;
	}


	public boolean isDisplayDistanceRuptureMsg() {
		return displayDistanceRuptureMsg;
	}


	public void setDisplayDistanceRuptureMsg(boolean displayDistanceRupture) {
		this.displayDistanceRuptureMsg = displayDistanceRupture;
	}
}
