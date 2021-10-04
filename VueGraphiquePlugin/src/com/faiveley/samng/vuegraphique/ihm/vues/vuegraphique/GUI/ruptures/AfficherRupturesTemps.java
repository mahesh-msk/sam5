package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;

public class AfficherRupturesTemps {

	boolean clic=false;
	
	private static final AfficherRupturesTemps INSTANCE = new AfficherRupturesTemps();

    /**
     * La présence d'un constructeur privé supprime
     * le constructeur public par défaut.
     */
    private AfficherRupturesTemps() {
    
    }
    
    
    /**
     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
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
