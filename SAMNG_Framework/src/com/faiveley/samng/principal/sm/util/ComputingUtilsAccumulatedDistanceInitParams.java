package com.faiveley.samng.principal.sm.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * Objet d'initialisation pour le calcul de la distance cumulée   
 * @author bleclerc
 *
 */
public class ComputingUtilsAccumulatedDistanceInitParams {
	private Double initDistance = 0.0;
	private Double segmentAccumulatedDistance = 0.0;
	private Double totalAccumulatedDistance = 0.0;
	
	public ComputingUtilsAccumulatedDistanceInitParams(Double initDistance){
		
		/* get init_distance_from_cpt from /ressources/atess/adsDistance.properties */
		FileInputStream inStream = null;
		Properties props = new Properties();
		String atessDistanceProperties = RepertoiresAdresses.atessDistanceProperties;
		
		boolean isInitDistanceFromCpt = false;
		
		try {
			inStream = new FileInputStream(new File(atessDistanceProperties));
			props.load(inStream);
			isInitDistanceFromCpt = Boolean.parseBoolean((String) props.get("init_distance_from_cpt"));
		} catch (FileNotFoundException ex) {
		    ex.printStackTrace();
		} catch (IOException ex) {
		    ex.printStackTrace();
		}
		
		if (isInitDistanceFromCpt == false) {
			this.initDistance = initDistance;
		}
	}
	
	public Double getInitDistance() {
		return initDistance;
	}
	public void setInitDistance(Double initDistance) {
		this.initDistance = initDistance;
	}
	public Double getSegmentAccumulatedDistance() {
		return segmentAccumulatedDistance;
	}
	public void setSegmentAccumulatedDistance(Double segmentAccumulatedDistance) {
		this.segmentAccumulatedDistance = segmentAccumulatedDistance;
	}
	public Double getTotalAccumulatedDistance() {
		return totalAccumulatedDistance;
	}
	public void setTotalAccumulatedDistance(Double totalAccumulatedDistance) {
		this.totalAccumulatedDistance = totalAccumulatedDistance;
	}
	
	
	
}
