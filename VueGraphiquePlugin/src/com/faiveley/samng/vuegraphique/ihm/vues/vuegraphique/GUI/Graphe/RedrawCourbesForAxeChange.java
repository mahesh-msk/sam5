package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

	public class RedrawCourbesForAxeChange {
		boolean redraw;

	    private static final RedrawCourbesForAxeChange INSTANCE = new RedrawCourbesForAxeChange();

	    /**
	     * La pr�sence d'un constructeur priv� supprime
	     * le constructeur public par d�faut.
	     */
	    private RedrawCourbesForAxeChange() {
	    
	    }
 
	    /**
	     * Dans ce cas pr�sent, le mot-cl� synchronized n'est pas utile.
	     * L'unique instanciation du singleton se fait avant
	     * l'appel de la m�thode getInstance(). Donc aucun risque d'acc�s concurrents.
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
