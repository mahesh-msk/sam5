package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI;

public class VitesseCorrigeePresenteDansFiltre {
		

		public boolean notDraw=false;
		public int indiceGraphe=-1;
		
		private static final VitesseCorrigeePresenteDansFiltre INSTANCE = new VitesseCorrigeePresenteDansFiltre();

	    /**
	     * La pr�sence d'un constructeur priv� supprime
	     * le constructeur public par d�faut.
	     */
	    private VitesseCorrigeePresenteDansFiltre() {

	    }

	    public static VitesseCorrigeePresenteDansFiltre getInstance() {
	        return INSTANCE;
	    }
}
