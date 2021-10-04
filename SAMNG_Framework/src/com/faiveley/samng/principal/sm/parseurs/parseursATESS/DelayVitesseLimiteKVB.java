package com.faiveley.samng.principal.sm.parseurs.parseursATESS;

/**
 * Objet dédié au retard d'application de la vitesse limite KVB pour une
 * distance
 * 
 * @author bleclerc
 * 
 */
public class DelayVitesseLimiteKVB {

    /**
     * Kilomètre à laquelle appliquer la vitesse limite KVB
     */
    private Double distanceApplicationVitesseLimiteKVB = null;

    /**
     * Vitesse limite KVB à appliquer au kilomètre spécifique
     */
    private vitesseLimiteKVB applicationVitesseLimiteKVB = null;

    /**
     * Constructeur vide
     */
    public DelayVitesseLimiteKVB() {
	this(null, null);
    }

    /**
     * Constructeur avec kilomètre et vitesse
     * @param distanceApplicationVitesseLimiteKVB
     * @param applicationVitesseLimiteKVB
     */
    public DelayVitesseLimiteKVB(Double distanceApplicationVitesseLimiteKVB, vitesseLimiteKVB applicationVitesseLimiteKVB) {
	this.distanceApplicationVitesseLimiteKVB = distanceApplicationVitesseLimiteKVB;
	this.applicationVitesseLimiteKVB = applicationVitesseLimiteKVB;
    }

    /**
     * Accesseur Kilomètre à laquelle appliquer la vitesse limite KVB
     * @return
     */
    public Double getDistanceApplicationVitesseLimiteKVB() {
	return distanceApplicationVitesseLimiteKVB;
    }
    
    /**
     * Accesseur Kilomètre à laquelle appliquer la vitesse limite KVB
     * @return
     */
    public void setDistanceApplicationVitesseLimiteKVB(Double distanceApplicationVitesseLimiteKVB) {
	this.distanceApplicationVitesseLimiteKVB = distanceApplicationVitesseLimiteKVB;
    }
    
    /**
     * Accesseur Vitesse limite KVB à appliquer au kilomètre spécifique
     * @return
     */
    public vitesseLimiteKVB getApplicationVitesseLimiteKVB() {
	return applicationVitesseLimiteKVB;
    }
    
    /**
     * Accesseur Vitesse limite KVB à appliquer au kilomètre spécifique
     * @return
     */
    public void setApplicationVitesseLimiteKVB(vitesseLimiteKVB applicationVitesseLimiteKVB) {
	this.applicationVitesseLimiteKVB = applicationVitesseLimiteKVB;
    }

}
