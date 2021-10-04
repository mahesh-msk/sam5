package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Legende;

import java.util.ArrayList;

public class InfosBullesLegende {
	
	private ArrayList ListBullesDim;
	private ArrayList<String> ListBullesName;
	private ArrayList ListBullesNumGraphe;
	private int numeroVariable;
	
	private static final InfosBullesLegende INSTANCE = new InfosBullesLegende();

    /**
     * La présence d'un constructeur privé supprime
     * le constructeur public par défaut.
     */
    private InfosBullesLegende() {
    	ListBullesDim = new ArrayList();
    	ListBullesName = new ArrayList<String>();
    	ListBullesNumGraphe = new ArrayList();
    }
    
    
    /**
     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
     * Retourne l'instance du singleton.
     */
    public static InfosBullesLegende getInstance() {
        return INSTANCE;
    }

	public ArrayList getListBullesDim() {
		return ListBullesDim;
	}
	
	public ArrayList<String> getListBullesName() {
		return ListBullesName;
	}

	public ArrayList getListBullesNumGraphe() {
		return ListBullesNumGraphe;
	}

	public int getNumeroVariable() {
		return numeroVariable;
	}

	public void setNumeroVariable(int num) {
		numeroVariable = num;
	}
}
