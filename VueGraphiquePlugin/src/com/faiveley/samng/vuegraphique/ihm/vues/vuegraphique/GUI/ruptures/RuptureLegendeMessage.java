package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;


public class RuptureLegendeMessage {
	
	boolean displayTimeRuptureMsg=false;
	boolean displayDistanceRuptureMsg=false;
	
	private static final RuptureLegendeMessage INSTANCE = new RuptureLegendeMessage();

    /**
     * La pr�sence d'un constructeur priv� supprime
     * le constructeur public par d�faut.
     */
    private RuptureLegendeMessage() {

    }
    
    
    /**
     * Dans ce cas pr�sent, le mot-cl� synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la m�thode getInstance(). Donc aucun risque d'acc�s concurrents.
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
