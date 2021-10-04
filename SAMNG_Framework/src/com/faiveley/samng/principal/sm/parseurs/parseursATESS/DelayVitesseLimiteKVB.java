package com.faiveley.samng.principal.sm.parseurs.parseursATESS;

/**
 * Objet d�di� au retard d'application de la vitesse limite KVB pour une
 * distance
 * 
 * @author bleclerc
 * 
 */
public class DelayVitesseLimiteKVB {

    /**
     * Kilom�tre � laquelle appliquer la vitesse limite KVB
     */
    private Double distanceApplicationVitesseLimiteKVB = null;

    /**
     * Vitesse limite KVB � appliquer au kilom�tre sp�cifique
     */
    private vitesseLimiteKVB applicationVitesseLimiteKVB = null;

    /**
     * Constructeur vide
     */
    public DelayVitesseLimiteKVB() {
	this(null, null);
    }

    /**
     * Constructeur avec kilom�tre et vitesse
     * @param distanceApplicationVitesseLimiteKVB
     * @param applicationVitesseLimiteKVB
     */
    public DelayVitesseLimiteKVB(Double distanceApplicationVitesseLimiteKVB, vitesseLimiteKVB applicationVitesseLimiteKVB) {
	this.distanceApplicationVitesseLimiteKVB = distanceApplicationVitesseLimiteKVB;
	this.applicationVitesseLimiteKVB = applicationVitesseLimiteKVB;
    }

    /**
     * Accesseur Kilom�tre � laquelle appliquer la vitesse limite KVB
     * @return
     */
    public Double getDistanceApplicationVitesseLimiteKVB() {
	return distanceApplicationVitesseLimiteKVB;
    }
    
    /**
     * Accesseur Kilom�tre � laquelle appliquer la vitesse limite KVB
     * @return
     */
    public void setDistanceApplicationVitesseLimiteKVB(Double distanceApplicationVitesseLimiteKVB) {
	this.distanceApplicationVitesseLimiteKVB = distanceApplicationVitesseLimiteKVB;
    }
    
    /**
     * Accesseur Vitesse limite KVB � appliquer au kilom�tre sp�cifique
     * @return
     */
    public vitesseLimiteKVB getApplicationVitesseLimiteKVB() {
	return applicationVitesseLimiteKVB;
    }
    
    /**
     * Accesseur Vitesse limite KVB � appliquer au kilom�tre sp�cifique
     * @return
     */
    public void setApplicationVitesseLimiteKVB(vitesseLimiteKVB applicationVitesseLimiteKVB) {
	this.applicationVitesseLimiteKVB = applicationVitesseLimiteKVB;
    }

}
