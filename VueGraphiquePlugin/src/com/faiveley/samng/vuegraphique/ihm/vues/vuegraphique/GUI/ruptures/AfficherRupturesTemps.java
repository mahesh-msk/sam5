package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;

public class AfficherRupturesTemps {

	boolean clic=false;
	
	private static final AfficherRupturesTemps INSTANCE = new AfficherRupturesTemps();

    /**
     * La pr�sence d'un constructeur priv� supprime
     * le constructeur public par d�faut.
     */
    private AfficherRupturesTemps() {
    
    }
    
    
    /**
     * Dans ce cas pr�sent, le mot-cl� synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la m�thode getInstance(). Donc aucun risque d'acc�s concurrents.
     * Retourne l'instance du singleton.
     */
    public static AfficherRupturesTemps getInstance() {
        return INSTANCE;
    }


	public boolean isDeClic() {
		return clic;
	}


	public void setDeClic(boolean clic) {
		this.clic = clic;
	}
}
