package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;

/**
 * @author Olivier
 * @version 1.0
 * @created 14-janv.-2008 12:36:57
 */
public abstract class AGestionnaireAxes {

	public AGestionnaireAxes(){

	}

	public void finalize() throws Throwable {

	}

	/**
	 * récupère un axe à partir de son type: AXE_DISTANCE, AXE_DISTANCE_CORRIGEE,
	 * AXE_TEMPS, AXE_TEMPS_CORRIGE
	 * 
	 * @param typeAxe
	 */
	public abstract AxeX getAxeByType(TypeAxe typeAxe);

	/**
	 * initialise l'axe des distance à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - de la distance cumulée de tous les objets SegmentDistance(dans l'objet
	 * singleton TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeDistance(int largeurPixels);

	/**
	 * initialise l'axe des distances corrigées à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - de la distance cumulée de tous les objets SegmentDistance(dans l'objet
	 * singleton TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeDistanceCorrigee(int largeurPixels);

	/**
	 * initialise l'axe de temps à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - du temps cumulé de tous les objets SegmentTemps(dans l'objet singleton
	 * TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeTemps(int largeurPixels);

	/**
	 * initialise l'axe de temps à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - du temps cumulé de tous les objets SegmentTemps(dans l'objet singleton
	 * TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeTempsCorrige(int largeurPixels);

}