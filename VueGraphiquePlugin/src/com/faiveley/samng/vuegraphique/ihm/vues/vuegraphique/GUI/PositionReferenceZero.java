package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

public class PositionReferenceZero {
	
		int position=MARGE_LATERALE;

	    private static final PositionReferenceZero INSTANCE = new PositionReferenceZero();

	    /**
	     * La présence d'un constructeur privé supprime
	     * le constructeur public par défaut.
	     */
	    private PositionReferenceZero() {
	    	position=MARGE_LATERALE;
	    }
 
	    /**
	     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
	     * L'unique instanciation du singleton se fait avant
	     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
	     * Retourne l'instance du singleton.
	     */
	    public static PositionReferenceZero getInstance() {
	        return INSTANCE;
	    }

		public int getPosition() {
			return position;
		}

		public void setPosition(int position) {
			this.position = position;
		}
}
