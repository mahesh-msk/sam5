package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

public class LabelsAxeAbscisse {
		
	public Graduation labels[];
	
	private static final LabelsAxeAbscisse INSTANCE = new LabelsAxeAbscisse();

    /**
     * La présence d'un constructeur privé supprime
     * le constructeur public par défaut.
     */
    private LabelsAxeAbscisse() {

    }
    
    
    /**
     * Dans ce cas présent, le mot-clé synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la méthode getInstance(). Donc aucun risque d'accès concurrents.
     * Retourne l'instance du singleton.
     */
    public static LabelsAxeAbscisse getInstance() {
        return INSTANCE;
    }


	public Graduation[] getLabels() {
		return labels;
	}


	public void setLabels(Graduation[] labels) {
		this.labels = labels;
	}
}
