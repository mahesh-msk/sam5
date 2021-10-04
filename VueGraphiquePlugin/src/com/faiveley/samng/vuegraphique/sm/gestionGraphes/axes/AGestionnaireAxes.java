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
	 * r�cup�re un axe � partir de son type: AXE_DISTANCE, AXE_DISTANCE_CORRIGEE,
	 * AXE_TEMPS, AXE_TEMPS_CORRIGE
	 * 
	 * @param typeAxe
	 */
	public abstract AxeX getAxeByType(TypeAxe typeAxe);

	/**
	 * initialise l'axe des distance � partir:
	 * - de la largeur en pixels de la partie r�serv�e aux graphes
	 * - de la distance cumul�e de tous les objets SegmentDistance(dans l'objet
	 * singleton TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeDistance(int largeurPixels);

	/**
	 * initialise l'axe des distances corrig�es � partir:
	 * - de la largeur en pixels de la partie r�serv�e aux graphes
	 * - de la distance cumul�e de tous les objets SegmentDistance(dans l'objet
	 * singleton TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeDistanceCorrigee(int largeurPixels);

	/**
	 * initialise l'axe de temps � partir:
	 * - de la largeur en pixels de la partie r�serv�e aux graphes
	 * - du temps cumul� de tous les objets SegmentTemps(dans l'objet singleton
	 * TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeTemps(int largeurPixels);

	/**
	 * initialise l'axe de temps � partir:
	 * - de la largeur en pixels de la partie r�serv�e aux graphes
	 * - du temps cumul� de tous les objets SegmentTemps(dans l'objet singleton
	 * TableSegments )
	 * 
	 * @param largeurPixels
	 */
	public abstract void initialiserAxeTempsCorrige(int largeurPixels);

}