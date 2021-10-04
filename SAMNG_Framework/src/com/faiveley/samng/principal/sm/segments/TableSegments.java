package com.faiveley.samng.principal.sm.segments;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.tables.TableSegmentsExplorer;

/**
 * Classe contenant une HashMap de segmentsTemp: HashMap<numSegment,SegmentTemps>
 * singleton
 * @author Graton Olivier
 * @version 1.0
 * @created 07-déc.-2007 11:32:54
 */
public class TableSegments extends AParcoursComposant {

	//list of time segments
	protected HashMap<Integer,SegmentTemps> segmentsTemps;

	//list of distance segment
	protected HashMap<Integer,SegmentDistance> segmentsDistance;

	//the single instance of this table 
	private static TableSegments instance = new TableSegments();
	
	//flags to say if there are corrections made
	protected boolean enableDistanceCorrections;
	protected boolean enableTempCorrections;
	
	//flags to say if the corrections are applied or not
	protected boolean appliedDistanceCorrections;
	protected boolean appliedTempCorrections;
	
	protected boolean auMoinsUnSegmentDistanceValide;
	
	public boolean isAuMoinsUnSegmentDistanceValide() {
		return auMoinsUnSegmentDistanceValide;
	}

	public void setAuMoinsUnSegmentDistanceValide(boolean auMoinsUnSegmentValide) {
		this.auMoinsUnSegmentDistanceValide = auMoinsUnSegmentValide;
	}

	/**
	 * Méthode retournant un segment par son numéro
	 * @param numero
	 * @return un objet SegmentTemps
	 */
	public SegmentTemps getSegmentTemps(int numero){
		return this.segmentsTemps.get(Integer.valueOf(numero));
	}
	
	/**
	 * Returns the map of <segment number, time segment>
	 * @return
	 */
	public HashMap<Integer, SegmentTemps> getSegmentsTemps() {
		return this.segmentsTemps;
	}

	/**
	 * Sets the map of <segment number, time segment>
	 * @return
	 */
	public void setSegmentsTemps(HashMap<Integer, SegmentTemps> segments) {
		this.segmentsTemps = segments;
	}

	/**
	 * Adds a time segment
	 * @param segment	segment to add 
	 * @param fin 
	 * @param deb 
	 * @return			a segment if already exists one with the same number
	 */
	public SegmentTemps ajouterSegmentTemps(SegmentTemps segment) {
		int index = this.segmentsTemps.size();
		segment.setNumeroSegment(index);
//		segment.setOffsetDebut(deb);
//		segment.setOffsetFin(fin);
		return this.segmentsTemps.put(Integer.valueOf(index), segment);
	}
	
	public static int getSegmentDistanceById(int ID,TableSegments table){
		int msgStart;
		int msgStop;
		
		int size = table.getSegmentsDistance().size();
		for (int i = 0; i < size; i++) {
			msgStart=table.getSegmentDistance(i).getStartMsgId();
			msgStop=table.getSegmentDistance(i).getEndMsgId();
			if (msgStart<=ID	&& ID<=msgStop) {
				return i;
			}		
		}
		
		return -1;
	}
	
	public static int getSegmentTempsById(int ID,TableSegments table){
		int msgStart;
		int msgStop;
		
		int size = table.getSegmentsTemps().size();
		for (int i = 0; i < size; i++) {
			msgStart=table.getSegmentTemps(i).getStartMsgId();
			msgStop=table.getSegmentTemps(i).getEndMsgId();
			if (msgStart<=ID	&& ID<=msgStop) {
				return i;
			}		
		}
		
		return -1;
	}
	
	/**
	 * Returns the distance segment with the given number
	 * @param numero	the number of the segment
	 * @return			the distance segment 
	 */
	public SegmentDistance getSegmentDistance(int numero){
		return this.segmentsDistance.get(Integer.valueOf(numero));
	}
	
	/**
	 * Returns a map <segment number, distance segment>
	 * @return
	 */
	public HashMap<Integer, SegmentDistance> getSegmentsDistance() {
		return this.segmentsDistance;
	}

	/**
	 * Sets a map <segment number, distance segment>
	 * @param segments
	 */
	public void setSegmentsDistance(HashMap<Integer, SegmentDistance> segments) {
		this.segmentsDistance = segments;
	}

	
	/**
	 * Adds a distance segment
	 * @param segment	segment to add 
	 * @return			a segment if already exists one with the same number
	 */
	public SegmentDistance ajouterSegmentDistance(SegmentDistance segment) {
		int index = this.segmentsDistance.size();
		segment.setNumeroSegment(index);
		return this.segmentsDistance.put(Integer.valueOf(index), segment);
	}
	

	/**
	 * Private constructor. Sigleton class
	 * Creates the list of segments
	 */
	protected TableSegments(){
		this.segmentsTemps = new HashMap<Integer, SegmentTemps>();
		this.segmentsDistance = new HashMap<Integer, SegmentDistance>();
	}
	
	/**
	 * Méthode de récupération du singleton`
	 * @return
	 */
	public static TableSegments getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return TableSegmentsExplorer.getInstance();
		}
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear(){
		empty();
	}
	
	/**
	 * Clears the maps of distance and time segments
	 *
	 */
	public void empty() {
		//clears lists
		this.segmentsTemps.clear();
		this.segmentsDistance.clear();
		
		//reset flags
		this.enableDistanceCorrections = false;
		this.enableTempCorrections = false;

		this.appliedDistanceCorrections = false;
		this.appliedTempCorrections = false;
	}

	/**
	 * Returns true or false if there are or not distance corrections made
	 * @return the areDistanceCorrections
	 */
	public boolean areDistanceCorrections() {
		return this.enableDistanceCorrections;
	}

	/**
	 * Sets true or false if there are or not distance corrections made
	 * @param enableDistanceCorrections the areDistanceCorrections to set
	 */
	public void setEnableDistanceCorrections(boolean enableDistanceCorrections) {
		this.enableDistanceCorrections = enableDistanceCorrections;
	}

	/**
	 * Returns true or false if there are or not time corrections made
	 * @return the areTempCorrections
	 */
	public boolean areTempCorrections() {
		return this.enableTempCorrections;
	}

	/**
	 * Sets true or false if there are or not time corrections made
	 * @param enableTempCorrections the areTempCorrections to set
	 */
	public void setEnableTempCorrections(boolean enableTempCorrections) {
		this.enableTempCorrections = enableTempCorrections;
	}

	/**
	 * @return the appliedDistanceCorrections
	 */
	public boolean isAppliedDistanceCorrections() {
		return this.appliedDistanceCorrections;
	}

	/**
	 * @param appliedDistanceCorrections the appliedDistanceCorrections to set
	 */
	public void setAppliedDistanceCorrections(boolean appliedDistanceCorrections) {
		this.appliedDistanceCorrections = appliedDistanceCorrections;
	}

	/**
	 * @return the appliedTempCorrections
	 */
	public boolean isAppliedTempCorrections() {
		return this.appliedTempCorrections;
	}

	/**
	 * @param appliedTempCorrections the appliedTempCorrections to set
	 */
	public void setAppliedTempCorrections(boolean appliedTempCorrections) {
		this.appliedTempCorrections = appliedTempCorrections;
	}
	
	/**
	 * Returns a time segment where the given message id belongs
	 * @param msgId
	 * @return
	 */
	public SegmentTemps getContainingTempSegment(int msgId) {
		if(segmentsTemps != null) {
			for(SegmentTemps segTemp: segmentsTemps.values()) {
				if(msgId >= segTemp.getStartMsgId() && msgId <= segTemp.getEndMsgId()) {
					return segTemp;
				}
			}
		}
		return null;
	}

	/**
	 * Returns a distance segment where the given message id belongs
	 * @param msgId
	 * @return
	 */
	public SegmentDistance getContainingDistanceSegment(int msgId) {
		if(segmentsDistance != null) {
			for(SegmentDistance segDist: segmentsDistance.values()) {
				if(msgId >= segDist.getStartMsgId() && msgId <= segDist.getEndMsgId()) {
					return segDist;
				}
			}
		}
		return null;
	}
	
	
	/**
	 * Méthode qui vérifie si au moins un segment distance est valide
	 * @return
	 */
	public boolean segmentsDistanceValide(){
		Collection<SegmentDistance> segmentsDist = segmentsDistance.values();
		boolean isValide = false;
		for (SegmentDistance distance : segmentsDist) {
			if(distance.isValide())
				isValide = true;
		}
		return isValide;
	}
	
	
	/**
	 * Méthode qui vérifie si au moins un segment temps est valide
	 * @return 
	 */
	public boolean segmentsTempsValide(){
		Collection<SegmentTemps> segmentsTps = segmentsTemps.values();
		boolean isValide = false;
		for (SegmentTemps temps : segmentsTps) {
			if(temps.isValide())
				isValide = true;
		}
		return isValide;
	}
	
	public List <SegmentTemps> classerSegmentsTemps(){
		Collection<SegmentTemps> segments=TableSegments.getInstance().getSegmentsTemps().values();
		List<SegmentTemps> listeSegs = new ArrayList<SegmentTemps>(segments);
		java.util.Collections.sort(listeSegs);
		return listeSegs;
	}
}