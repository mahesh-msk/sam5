package com.faiveley.samng.principal.sm.parseurs.parseursJRU;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireLongueurPacketExplorer;

/**
 * Classe permettant de gérer les longueurs de chaque paquet: 
 * longueur calculée : somme des tailles des variables du paquet
 * longueur enregistrée : longueur spécifiée par la variable L_PACKET
 * @author olive
 *
 */
public class GestionnaireLongueurPacket {


	private static GestionnaireLongueurPacket instance = new GestionnaireLongueurPacket();
	
	private long longueurEnregistreePacketCourant;
	private long longueurCalculeePacketCourant;
	
	
	
	/**
	 * Private constructor. Sigleton class
	 */
	protected GestionnaireLongueurPacket(){
		this.longueurEnregistreePacketCourant = 0;
		this.longueurCalculeePacketCourant = 0;
		
	}
	
	/**
	 * Méthode de récupération du singleton`
	 * @return
	 */
	public static GestionnaireLongueurPacket getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireLongueurPacketExplorer.getInstance();
		}
		return instance;
	}

	
	
	/**
	 * Méthode qui incrémente la longueur réelle du packet
	 * @param nbBits
	 */
	public void incrementerLongueurCalculeePacketCourant(String nomVariable,
			long nbBits) {
		
		this.longueurCalculeePacketCourant += nbBits;
//		System.out.println(nomVariable + ", taille : " + nbBits );
//		System.out.println("longueur totale calculee packet " + this.longueurCalculeePacketCourant);
//		System.out.println("longueur entregistree packet " + this.longueurEnregistreePacketCourant);
	}

	public long getLongueurCalculeePacketCourant() {
		return longueurCalculeePacketCourant;
	}

	public void setLongueurCalculeePacketCourant(long longueurCalculeePacketCourant) {
		this.longueurCalculeePacketCourant = longueurCalculeePacketCourant;
	}

	public long getLongueurEnregistreePacketCourant() {
		return longueurEnregistreePacketCourant;
	}

	public void setLongueurEnregistreePacketCourant(
			long longueurEnregistreePacketCourant) {
		this.longueurEnregistreePacketCourant = longueurEnregistreePacketCourant;
	}
	
	public void clear() {
		longueurCalculeePacketCourant = 0;
		longueurEnregistreePacketCourant = 0;
	}

	
}
