package com.faiveley.samng.principal.sm.marqueurs;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import com.faiveley.samng.principal.sm.parseurs.ParseurMarqueurs;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * Classe de gestion des marqueurs et annotations
 * Son role est :
 * - de gerer les marqueurs et annotations que l'utilisateur rentre via les
 * fenetre de gestion des marqueurs(modification, ajout, suppression)
 * - d'enregistrer les marqueurs et annotations si l'utilisateur fait "Enregistrer
 * les remarques utilisateurs"
 * @author olivier
 * @version 1.0
 * @created 06-d�c.-2007 11:12:40
 */
public class GestionnaireMarqueurs {
	private List<AMarqueur> listeMarqueurs = new Vector<AMarqueur>();
	private Map<Integer, AMarqueur> mapMarqueurs = new LinkedHashMap<Integer, AMarqueur>();
	private ParseurMarqueurs parser;
	private List<AMarqueur> initialListeMarqueurs = new Vector<AMarqueur>();
	private static String newMarkerNamePrefix = "NewMarker_";
	private boolean modifie = false;
	private AMarqueur dernierMarqueurAjoute = null;
	
	public AMarqueur getDernierMarqueurAjoute() {
		return dernierMarqueurAjoute;
	}

	public void setDernierMarqueurAjoute(Marqueur dernierMarqueurAjoute) {
		this.dernierMarqueurAjoute = dernierMarqueurAjoute;
	}

	public GestionnaireMarqueurs(){
	}

	class MarquerComparator implements Comparator<AMarqueur> {

		public int compare(AMarqueur marquer0, AMarqueur marquer1) {
			if (marquer0.getIdMessage() > marquer1.getIdMessage()) {
				return 1;
			} else if (marquer0.getIdMessage() < marquer1.getIdMessage()) {
				return -1;
			}
			return 0;
		}
	}
	/**
	 * ajoute un marqueur dans listeMarqueurs
	 * 
	 * @param marqueur
	 */
	public boolean ajouterMarqueur(AMarqueur marqueur) {
		if(marqueur != null) {
			for (AMarqueur m : this.listeMarqueurs) {
				if (marqueur.idMessage == m.getIdMessage()) {
					return false;
				}
			}
			
			this.modifie = true;
			this.listeMarqueurs.add(marqueur);
			this.mapMarqueurs.put(marqueur.idMessage, marqueur);
			Collections.sort(this.listeMarqueurs, new MarquerComparator());
			
			dernierMarqueurAjoute = marqueur;
			
		
			return true;
			
		}
		return false;
	}
	
	public void effacerMarqueur(AMarqueur marqueur) {
		if(marqueur != null) {
			this.modifie = true;
			this.listeMarqueurs.remove(marqueur);
			mapMarqueurs.remove(marqueur.idMessage);
			
		}
	}
	
	public AMarqueur[] getMarqueurs() {
		return this.listeMarqueurs.toArray(new AMarqueur[this.listeMarqueurs.size()]);
	}
	
	public int[] getMarqueursIds() {
		int[] markerIds = new int[this.listeMarqueurs.size()];
		for(int i = 0; i<this.listeMarqueurs.size(); i++) {
			markerIds[i] = this.listeMarqueurs.get(i).idMessage;
		}
		return markerIds;
	}
	
	public int[] getSortedMarqueursIds() {
		
		int[] markerIds = new int[this.listeMarqueurs.size()];
		for(int i = 0; i<this.listeMarqueurs.size(); i++) {
			markerIds[i] = this.listeMarqueurs.get(i).idMessage;
		}
		return markerIds;
	}
	
	public AMarqueur getMarqueurParId(int msgId) {
		return mapMarqueurs.get(msgId);
	}
	
	public AMarqueur getMarqueurById(int msgId) {
		int i=0;
		boolean trouve=false;
	
		while(!trouve && i<listeMarqueurs.size()){
			if (listeMarqueurs.get(i).getIdMessage()==msgId) {
				trouve=true;
			}
			i++;
		}
		if(trouve)
		return listeMarqueurs.get(i);
		else 
			return null;
	}
	
	
	/**
	 * Charge l'annotation du fichier de parcours
	 * @return
	 */
	public String chargerAnnotationParcours(){
		return this.parser.chargerAnnotationParcours();
	}
	
	
	/**
	 * Enregistre une annotation
	 * @param annotation
	 * @return
	 */
	public boolean enregistrerAnnotationParcours(String annotation){
		return this.parser.enregistrerAnnotationParcours(annotation);
	}
	
	
	/**
	 * charge tous les marqueurs en m�moire en utilisant la classe ParseurMarqueurs
	 * charge � partir du fichier xml correspondant au fichier binaire
	 * 
	 * @param fichier
	 */
	public void chargerMarqueurs(String fichier) {
		File file = new File(fichier);
		
		String fileName = file.getName();
		String folder= file.getParent();
		
		
		int dotPos;
		if((dotPos = fileName.indexOf('.')) != -1)
			fileName = fileName.substring(0, dotPos);
		
		String fullMarkersFileName = folder+ File.separator + fileName + RepertoiresAdresses.marqueursXML;
		this.parser = new ParseurMarqueurs();
		this.parser.parseRessource(fullMarkersFileName,false,0,-1);
		this.listeMarqueurs = this.parser.chargerMarqueurs();
		
		for (AMarqueur marqueur : this.listeMarqueurs) {
			this.mapMarqueurs.put(Integer.valueOf(marqueur.getIdMessage()), marqueur);
			this.initialListeMarqueurs.add(marqueur);
		}
		
	}

	/**
	 * 
	 * enregistre dans le fichier xml toutes les annotations et tous les marqueurs en
	 * utilisant ParseurMarqueurs
	 * enregistre dans le fichier xml correspondant au fichier binaire
	 * 
	 * @param fichier
	 */
	public boolean enregistrerRemarques(String fichier) {
		boolean ret = false;
		if(parser != null){
			ret = this.parser.enregistrerMarqueurs(this.listeMarqueurs);
			ret = true;
			this.modifie = false;
		}
		return ret;
	}
	
	public String generateUniqueMarkerName() {
		int i = 1;
		String newMarkerName;
		while(true) {
			newMarkerName = newMarkerNamePrefix + i;
			if(!isMarkerName(newMarkerName))
				return newMarkerName; 
			i++;
		}
	}
	
	public boolean isMarkerName(String markerName) {
		if(markerName == null || "".equals(markerName.trim()))
			return true;
		for(AMarqueur marker: this.listeMarqueurs) {
			if(markerName.equals(marker.getNom()))
				return true;
		}
		return false;
	}
	
	public boolean isEmpty() {
		return (this.listeMarqueurs == null || this.listeMarqueurs.isEmpty());
	}

	/**
	 * D�fini si une sauvegarde des marqueurs est n�cessaire
	 * @return
	 */
	public boolean isModifications(){
//		if(this.listeMarqueurs.size()!= this.initialListeMarqueurs.size()){
//			return true;
//		}else{
			return modifie;
		//}
			
		
	}

	public boolean isModifie() {
		return modifie;
	}

	public void setModifie(boolean modifie) {
		this.modifie = modifie;
	}

	public void clear() {
		listeMarqueurs.clear();
		mapMarqueurs.clear();
		parser=null;
		initialListeMarqueurs.clear();
		dernierMarqueurAjoute = null;		
	}
}