package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul;

import java.util.ArrayList;

public class InfosBullesMarqueurs {

	private ArrayList ListBullesDim;
	private ArrayList<String> ListBullesName;
	private int numeroMarker;
	
	private static final InfosBullesMarqueurs INSTANCE = new InfosBullesMarqueurs();

    /**
     * La pr�sence d'un constructeur priv� supprime
     * le constructeur public par d�faut.
     */
    private InfosBullesMarqueurs() {
    	ListBullesDim = new ArrayList();
    	ListBullesName = new ArrayList<String>();
    }
    
    
    /**
     * Dans ce cas pr�sent, le mot-cl� synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la m�thode getInstance(). Donc aucun risque d'acc�s concurrents.
     * Retourne l'instance du singleton.
     */
    public static InfosBullesMarqueurs getInstance() {
        return INSTANCE;
    }


	public ArrayList getListBullesDim() {
		return ListBullesDim;
	}


	public void setListBullesDim(ArrayList listBullesDim) {
		ListBullesDim = listBullesDim;
	}


	public ArrayList<String> getListBullesName() {
		return ListBullesName;
	}


	public void setListBullesName(ArrayList listBullesName) {
		ListBullesName = listBullesName;
	}


	public int getNumeroMarker() {
		return numeroMarker;
	}


	public void setNumeroMarker(int numeroMarker) {
		this.numeroMarker = numeroMarker;
	}
}

