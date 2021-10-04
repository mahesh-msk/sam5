package com.faiveley.samng.principal.sm.data.enregistrement.tom4;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireSynchronisationGroupesExplorer;

public class GestionnaireSynchronisationGroupes {

	protected GestionnaireSynchronisationGroupes() {
		messagesSynchronises = new ArrayList<Message>(0);
	}

	private static GestionnaireSynchronisationGroupes instance = new GestionnaireSynchronisationGroupes();

	/**
	 * Liste des messages à synchroniser
	 */
	private List<Message> messagesSynchronises;
	/**
	 * Id du message servant de reference pour la synchronisation
	 */
	private Message msgReferenceSynchro = null;

	private boolean synchroEnCours = false;

	public Message getMsgReferenceSynchro() {
		return msgReferenceSynchro;
	}

	public void setMsgReferenceSynchro(Message msgReferenceSynchro) {
		this.msgReferenceSynchro = msgReferenceSynchro;
	}

	public boolean isSynchroEnCours() {
		return synchroEnCours;
	}

	public void setSynchroEnCours(boolean synchroEnCours) {
		this.synchroEnCours = synchroEnCours;
	}

	/**
	 * Retourne l'instance courante
	 * 
	 * @return
	 */
	public static GestionnaireSynchronisationGroupes getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireSynchronisationGroupesExplorer.getInstance();
		}
		return instance;
	}

	public List<Message> getMessagesSynchronises() {
		return messagesSynchronises;
	}

	public void setMessagesSynchronises(List<Message> messagesSynchronises) {
		this.messagesSynchronises = messagesSynchronises;
	}

	/**
	 * Ajout d'un message dans la liste de synchronisation
	 * 
	 * @param id
	 */
	public void ajouterMessageASynchroniser(Message msg) {
		this.messagesSynchronises.add(msg);
	}

	/**
	 * Méthode qui synchronise les événements d'un groupe
	 * 
	 */
	public void synchronisationMessages() {

		long tempsAbsoluReference = msgReferenceSynchro.getAbsoluteTime();
		double distanceAbsoluReference = msgReferenceSynchro
				.getAbsoluteDistance();

		for (Message msgCourant : this.messagesSynchronises) {
			msgCourant.setAbsoluteTime(tempsAbsoluReference);
			msgCourant.setAbsoluteDistance(distanceAbsoluReference);
		}
		clear();

	}

	public void clear() {
		// on vide ensuite les variables
		this.messagesSynchronises.clear();
		this.msgReferenceSynchro = null;
		this.synchroEnCours = false;
		// CHECK01
	}

}
