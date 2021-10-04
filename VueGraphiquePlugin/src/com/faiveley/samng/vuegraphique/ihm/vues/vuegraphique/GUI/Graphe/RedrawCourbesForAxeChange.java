package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

	public class RedrawCourbesForAxeChange {
		boolean redraw;

	    private static final RedrawCourbesForAxeChange INSTANCE = new RedrawCourbesForAxeChange();

	    /**
	     * La présence d'un constructeur privé supprime
	     * le constructeur public par défaut.
	     */
	    private RedrawCourbesForAxeChange() {
	    
	    }
 
	    /**
	     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
	     * L'unique instanciation du singleton se fait avant
	     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
	     * Retourne l'instance du singleton.
	     */
	    public static RedrawCourbesForAxeChange getInstance() {
	        return INSTANCE;
	    }

		public boolean isRedraw() {
			return redraw;
		}

		public void setRedraw(boolean redraw) {
			this.redraw = redraw;
		}	    
	}
