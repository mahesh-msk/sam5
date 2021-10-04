package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

public class LabelsAxeAbscisse {
		
	public Graduation labels[];
	
	private static final LabelsAxeAbscisse INSTANCE = new LabelsAxeAbscisse();

    /**
     * La pr�sence d'un constructeur priv� supprime
     * le constructeur public par d�faut.
     */
    private LabelsAxeAbscisse() {

    }
    
    
    /**
     * Dans ce cas pr�sent, le mot-cl� synchronized n'est pas utile.
     * L'unique instanciation du singleton se fait avant
     * l'appel de la m�thode getInstance(). Donc aucun risque d'acc�s concurrents.
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
