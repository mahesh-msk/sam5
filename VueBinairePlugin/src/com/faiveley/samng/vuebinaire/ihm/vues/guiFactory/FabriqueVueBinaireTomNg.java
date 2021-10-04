package com.faiveley.samng.vuebinaire.ihm.vues.guiFactory;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;
import com.faiveley.samng.vuebinaire.ihm.vues.BinaryRow;

/**
 * 
 * @author Olivier
 * 
 *         Classe de remplissage de la vue binaire pour les fichiers TomNg
 */
public class FabriqueVueBinaireTomNg extends AFabriqueVueBinaire {

	public ArrayList<BinaryRow> remplirTableau(IProgressMonitor monitor) {
		ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>(0);

		Enregistrement enrg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement();

		StringBuilder buf = new StringBuilder();
		String[] labels = null;
		byte[] dataMsg = null;
		BinaryRow row = null;

		// SUPPR_HASHMAP
		// Map<Integer, Message> mapIdGoodMsg = enrg.getGoodMapIdMessages();
		// List<Message> listeGoodMessages = enrg.getMessages();

		ListMessages listeGoodMessages2 = enrg.getMessages();
		//ajout olivier pour annulation vue binaire
		//modification des pourcentage de progression
		
		int nbGoodMessages = enrg.getGoodMessagesCount();
		int nbBadMessages = enrg.getBadMessagesCount();
		
		int nbMessagesProgressBar = nbGoodMessages + nbBadMessages;
		int pourcentGoodMessageProgressBar =  (nbGoodMessages * 100) / (nbMessagesProgressBar);
		int pourcentBadMessageProgressBar =  (nbBadMessages * 100) / (nbMessagesProgressBar);
		
		nbMessagesProgressBar = nbMessagesProgressBar *2;
		
//		tDebut = System.currentTimeMillis();
		int idMsg = 0;
		int cptProgress =0;
		// parcours des messages corrects
		if (listeGoodMessages2 != null && listeGoodMessages2.size() > 0
				&& !monitor.isCanceled()) {

			int offsetdeb=0;
			if (ConstantesParcoursBinaire.offsetDebut!=0) {
				offsetdeb=(listeGoodMessages2.get(0).getMessageId()-tailleEntete-tailleTableEvenement)/tailleBlocData;
			}
			
			// for each message create a number of rows
			for (Message msg1 : listeGoodMessages2) {
				
				ActivatorData.getInstance().getVp()
				.setValeurProgressBar(cptProgress * pourcentGoodMessageProgressBar / (nbMessagesProgressBar));
				
				idMsg = msg1.getMessageId();
				// msg = mapIdGoodMsg.get(id);
				int posBlock = (idMsg - (tailleEntete + tailleTableEvenement))/tailleBlocData;

				// get the data from the message
				dataMsg = msg1.getMessageData();

				// calculates the number of blocks
				int msgBlockNo = dataMsg.length/tailleBlocData;

				// create a row for each block
				for (int index = 0; index < msgBlockNo; index++) {
					if (monitor.isCanceled())
						break;
					// creates as many labels as the length of a block
					labels = new String[tailleBlocData];

					for (int k = index * tailleBlocData; k < dataMsg.length; k++) {
						int labelPos = k - index * tailleBlocData;

						// get the data coresponding to the position in the
						// block
						// and set the value of the label. the value is 1 octet
						String hexVal = Integer.toHexString(dataMsg[k] & 0xff);

						hexVal = hexVal.length() == 2 ? hexVal : "0" + hexVal; //$NON-NLS-1$

						if (GestionnairePool.getInstance().getMapByteHexa().get(dataMsg[k]) == null) {
							GestionnairePool.getInstance().getMapByteHexa().put(dataMsg[k],
									hexVal);
							labels[labelPos] = hexVal;
						} else {
							labels[labelPos] = GestionnairePool.getInstance()
									.getMapByteHexa().get(dataMsg[k]);
						}

						// labels[labelPos] = hexVal;

						// if(listeChaine.size()>0){
						// if(!listeChaine.contains(hexVal)){
						// listeChaine.add(hexVal);
						// labels[labelPos] = hexVal;
						// }
						// else{
						// labels[labelPos] =
						// listeChaine.get(listeChaine.indexOf(hexVal));
						// }
						// }else{
						// listeChaine.add(hexVal);
						// labels[labelPos] = hexVal;
						// }

						// if finised the data block extract from the mesasge
						// then we have reached the end of the message
						if (k + 1 == tailleBlocData * (index + 1)
								&& dataMsg.length > tailleBlocData) {
							break;
						}
					}

					// create the binary row
					row = new BinaryRow();
					row.setMsgId(idMsg);
					// calculates the position as start message + no of blocks *
					// length of the block
					// int pos = msg.getMessageId() + index * ConstantesParcoursBinaire.tailleBlocData;

					int pos = (idMsg - tailleEntete - tailleTableEvenement)	/ tailleBlocData + index + 1 - offsetdeb;

					// set the members of the row
					row.setBlockId(pos);
					row.setLabels(labels);
					row.setError(msg1.getError());

					// add the row
					if (posBlock + index >= rows.size()) {
						// if the block numeber is bigger than the number of
						// rows
						// then append the row to the list
						rows.add(row);
					} else {

						// if the block number is smaller than the number of
						// rows
						// then insert the row to the list
						try {
							rows.add(posBlock + index, row);
						} catch (Exception e) {
							continue;
						}
					}

					// reset buffer
					buf.setLength(0);
					row = null;
				}
				buf = new StringBuilder();
				msg1 = null;
				dataMsg = null;
				labels = null;
				cptProgress ++;
			}
		}
		//cptProgress =0;
		// SUPPR_HASHMAP
		int id = 0;
		// parcours des messages incorrects
		// Map<Integer, Message> mapIdBadMsg = enrg.getBadMapIdMessages();
		// Message[] listeBadMessages = enrg.getTabBadMessages();
		ListMessages listeBadMessages = enrg.getBadMessages();
		if (listeBadMessages != null && listeBadMessages.size() > 0
				&& !monitor.isCanceled()) {
			// for each message create a number of rows
			for (Message msgBad : listeBadMessages) {
				ActivatorData.getInstance().getVp()
				.setValeurProgressBar(cptProgress * pourcentBadMessageProgressBar / (nbMessagesProgressBar));
				id = msgBad.getMessageId();
				// msgBad = mapIdBadMsg.get(id);
				int posBlock = (id - (tailleEntete + tailleTableEvenement))
						/ tailleBlocData;

				// get the data from the message
				dataMsg = msgBad.getMessageData();

				// calculates the number of blocks
				int msgBlockNo = dataMsg.length / tailleBlocData;

				// create a row for each block
				for (int index = 0; index < msgBlockNo; index++) {
					
					// creates as many labels as the length of a block
					labels = new String[tailleBlocData];

					for (int k = index * tailleBlocData; k < dataMsg.length; k++) {
						if (monitor.isCanceled())
							break;
						// get the data coresponding to the position in the
						// block
						// and set the value of the label. the value is 1 octet
						String hexVal = Integer.toHexString(dataMsg[k] & 0xff);
						hexVal = hexVal.length() == 2 ? hexVal : "0" + hexVal; //$NON-NLS-1$

						int labelPos = k - index * tailleBlocData;
						// labels[labelPos] = hexVal;

						if (GestionnairePool.getInstance().getMapByteHexa().get(dataMsg[k]) == null) {
							GestionnairePool.getInstance().getMapByteHexa().put(dataMsg[k],
									hexVal);
							labels[labelPos] = hexVal;
						} else {
							labels[labelPos] = GestionnairePool.getInstance()
									.getMapByteHexa().get(dataMsg[k]);
						}

						// if(listeChaine.size()>0){
						// if(!listeChaine.contains(hexVal)){
						// listeChaine.add(hexVal);
						// labels[labelPos] = hexVal;
						// }
						// else{
						// labels[labelPos] =
						// listeChaine.get(listeChaine.indexOf(hexVal));
						// }
						// }else{
						// listeChaine.add(hexVal);
						// labels[labelPos] = hexVal;
						// }

						// if finised the data block extract from the mesasge
						// then we have reached the end of the message
						if (k + 1 == tailleBlocData * (index + 1)
								&& dataMsg.length > tailleBlocData) {
							break;
						}
					}

					// create the binary row
					row = new BinaryRow();
					row.setMsgId(msgBad.getMessageId());
					// calculates the position as start message + no of blocks *
					// length of the block
					// int pos = msg.getMessageId() + index * ConstantesParcoursBinaire.tailleBlocData;

					int pos = (id - tailleEntete - tailleTableEvenement)
							/ tailleBlocData + index + 1;

					// set the members of the row
					row.setBlockId(pos);
					row.setLabels(labels);
					row.setError(msgBad.getError());

// TODO TEST sans les bads messages
//					
//					// add the row
//					if (posBlock + index >= rows.size()) {
//						// if the block numeber is bigger than the number of
//						// rows
//						// then append the row to the list
//						rows.add(row);
//					} else {
//
//						// if the block number is smaller than the number of
//						// rows
//						// then insert the row to the list
//						rows.add(posBlock + index, row);
//					}

					// reset buffer
					buf.setLength(0);
					row = null;
				}
				buf = new StringBuilder();
				msgBad = null;
				dataMsg = null;
				labels = null;
				cptProgress ++;
			}
		}
//		tFin =  System.currentTimeMillis();
//		System.out.println("Creation  tableau rows (ms) :" + (tFin-tDebut));
		// int  pourcentProgressBarRestant = 100 - ActivatorData.getInstance().getVp().getValeurProgressBar();
		// int nbLignePourcent = rows.size();
		
		// set blue color for one message, and white for the other (1 blue, 1
		// white).
		// The color is not set when creating the rows, because first are added
		// the rows for the good messages, and after this the bad messages are
		// inserted
		// between the rows for bad massages
		boolean isBlue = false;

//		tDebut =System.currentTimeMillis();
		for (int i = 0; i < rows.size(); i++) {
			if (monitor.isCanceled())
				break;
//			ActivatorData.getInstance().getVp()
//					.setValeurProgressBar((i * 100 / (nbLignePourcent))+pourcentProgressBarRestant);
			BinaryRow ligne = rows.get(i);
			ligne.setBlue(isBlue);
			if ((i < rows.size() - 1)
					&& (ligne.getMsgId() != rows.get(i + 1).getMsgId())) {
				isBlue = !isBlue;
			}
			cptProgress ++;
		}
//		tFin =System.currentTimeMillis();
//		System.out.println("Coloration rows (ms) :" + (tFin-tDebut));
		if (monitor.isCanceled())
			rows = new ArrayList<BinaryRow>(0);
		return rows;
	}
}
