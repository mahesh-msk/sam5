package com.faiveley.samng.principal.sm.parseurs.parseursJRU;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireLongueurMessageExplorer;

/**
 * Classe permettant de gérer les longueurs de chaque message: 
 * 
 * longueurEnregistreeMessageCourant : somme des tailles des variables du message
 * longueurCalculeeMessageCourant: longueur spécifiée par la variable L_MESSAGE_JRU
 * 
 * Variables utilisées pour les messages internes à d'autres messages: exemple MESSAGE FROM RBC
 * longueurEnregistreeMessageInterneCourant : somme des tailles des variables du message
 * longueurCalculeeMessageInterneCourant: longueur spécifiée par la variable L_MESSAGE
 * @author olive
 *
 */
public class GestionnaireLongueurMessage {


	private static GestionnaireLongueurMessage instance = new GestionnaireLongueurMessage();
	
	private int longueurEnregistreeMessageCourant;
	private int longueurCalculeeMessageCourant;
	
	private int longueurEnregistreeMessageInterneCourant;
	private int longueurCalculeeMessageInterneCourant;
	
	/**
	 * Private constructor. Sigleton class
	 */
	protected GestionnaireLongueurMessage(){
		this.longueurEnregistreeMessageCourant = 0;
		this.longueurCalculeeMessageCourant = 0;
		this.longueurEnregistreeMessageInterneCourant=0;
		this.longueurCalculeeMessageInterneCourant=0;
	}
	
	/**
	 * Méthode de récupération du singleton`
	 * @return
	 */
	public static GestionnaireLongueurMessage getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireLongueurMessageExplorer.getInstance();
		}
		return instance;
	}

	public int getLongueurCalculeeMessageCourant() {
		return longueurCalculeeMessageCourant;
	}

	public void setLongueurCalculeeMessageCourant(int longueurCalculeeMessageCourant) {
		this.longueurCalculeeMessageCourant = longueurCalculeeMessageCourant;
	}

	public int getLongueurEnregistreeMessageCourant() {
		return longueurEnregistreeMessageCourant;
	}

	public void setLongueurEnregistreeMessageCourant(
			int longueurEnregistreeMessageCourant) {
		this.longueurEnregistreeMessageCourant = longueurEnregistreeMessageCourant;
	}
	
	/**
	 * Méthode qui incrémente la longueur réelle du message
	 * @param nbBits
	 */
	public void incrementerLongueurCalculeeMessageCourant(String nomVariable,
			int nbBits) {
		
		this.longueurCalculeeMessageCourant += nbBits;
//		System.out.println("nomVariable: " + nomVariable);
//		System.out.println("longueurVariable: " + nbBits  + ", longueur totale calculee :" + longueurCalculeeMessageCourant);
	
	}
	
	/**
	 * Méthode qui incrémente la longueur réelle du message
	 * @param nbBits
	 */
	public void incrementerLongueurCalculeeMessageInterneCourant(String nomVariable,
			int nbBits) {
		
		this.longueurCalculeeMessageInterneCourant += nbBits;
		//System.out.println("nomVariable: " + nomVariable);
		//System.out.println("longueurVariable: " + nbBits  + ", longueur totale calculee message interne:" + longueurCalculeeMessageInterneCourant);

	}
	
	
	public int getLongueurCalculeeMessageInterneCourant() {
		return longueurCalculeeMessageInterneCourant;
	}

	public void setLongueurCalculeeMessageInterneCourant(
			int longueurCalculeeMessageInterneCourant) {
		this.longueurCalculeeMessageInterneCourant = longueurCalculeeMessageInterneCourant;
	}

	public int getLongueurEnregistreeMessageInterneCourant() {
		return longueurEnregistreeMessageInterneCourant;
	}

	public void setLongueurEnregistreeMessageInterneCourant(
			int longueurEnregistreeMessageInterneCourant) {
		this.longueurEnregistreeMessageInterneCourant = longueurEnregistreeMessageInterneCourant;
	}
	
	public void clear() {
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		CHECK01
	}
}
