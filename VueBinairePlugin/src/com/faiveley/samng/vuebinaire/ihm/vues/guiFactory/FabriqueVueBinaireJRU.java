package com.faiveley.samng.vuebinaire.ihm.vues.guiFactory;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;

import java.math.BigInteger;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.formats.FormatJRU;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ConstantesParcoursJRU;
import com.faiveley.samng.vuebinaire.ihm.vues.BinaryRow;

public class FabriqueVueBinaireJRU extends AFabriqueVueBinaire {

	public static int tailleBloc = 10;

	public ArrayList<BinaryRow> remplirTableauJRU(IProgressMonitor monitor) {
		Enregistrement enrg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement();
		ListMessages listeBadMessages = enrg.getBadMessages();

		ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>(0);
		BinaryRow row = null;
		String[] labels = null;

		byte[] tableauOctets = 
//				Arrays.copyOfRange(
						TypeParseur.getInstance().getParser().getMessagebrut();
//						,
//				ConstantesParcoursBinaire.offsetDebut,ConstantesParcoursBinaire.offsetFin);

		//ArrayList<CRC_Erreur> crcEnreg = ((AParseurParcours) TypeParseur.getInstance().getParser()).listCRC_Error;
		String hexVal = null;

		int i = 0;
		int indiceLecture = 0;

		while (indiceLecture + 2 < tableauOctets.length && !monitor.isCanceled()) {
			int msgID = indiceLecture+ConstantesParcoursBinaire.offsetDebut;
			ActivatorData.getInstance().getVp().setValeurProgressBar(
							indiceLecture * 100 / (tableauOctets.length));
			row = new BinaryRow();

			int longueurM = 0;
			byte[] L_Message = new byte[2];
			L_Message[0] = tableauOctets[indiceLecture + 1];
			L_Message[1] = tableauOctets[indiceLecture + 2];
			// longueurM=(new BigInteger(L_Message).intValue()) >>
			// (16-ConstantesParcoursJRU.nbBitsLongueurL_Message);
			longueurM = (new BigInteger(L_Message).intValue() & 0xFFFF) >> (16 - ConstantesParcoursJRU.nbBitsLongueurL_Message);
			// issue 730

			int nblignes = longueurM % tailleBloc == 0 ? longueurM / tailleBloc
					: 1 + (longueurM / tailleBloc);
			for (int j = 0; j < nblignes; j++) {
				if (j == 0)
					msgID = indiceLecture+ConstantesParcoursBinaire.offsetDebut;
				labels = new String[tailleBloc];
				for (int k = 0; k < tailleBloc; k++) {
					hexVal = Integer.toHexString(tableauOctets[indiceLecture] & 0xff);
					hexVal = hexVal.length() == 2 ? hexVal : "0" + hexVal;

					try {
						if (GestionnairePool.getInstance().getMapByteHexa().get(
								tableauOctets[indiceLecture]) == null) {
							GestionnairePool.getInstance().getMapByteHexa().put(
									tableauOctets[indiceLecture], hexVal);
							labels[k] = hexVal;
						} else {
							labels[k] = GestionnairePool.getInstance().getMapByteHexa().get(
									tableauOctets[indiceLecture]);
						}
						indiceLecture++;

						if (k == tailleBloc - 1
								|| ((j * tailleBloc) + k + 1) == longueurM) {
							for (int index = k + 1; index < tailleBloc; index++) {
								labels[index] = "";
							}
							row = new BinaryRow();
							row.setMsgId(msgID);
							row.setBlockId(i + 1);
							row.setLabels(labels);
							rows.add(row);
							if (((j * tailleBloc) + k + 1) == longueurM) {
								break;
							}
						}
					} catch (RuntimeException e) {
						e.printStackTrace();
					}
					if (indiceLecture == tableauOctets.length) {
						break;
					}
				}
				if (indiceLecture == tableauOctets.length) {
					break;
				}
			}
			i++;
		}

		boolean isBlue = false;
		for (int j = 0; j < rows.size(); j++) {
			BinaryRow ligne = rows.get(j);
			ligne.setBlue(isBlue);
			if ((j < rows.size() - 1)
					&& (ligne.getBlockId() != rows.get(j + 1).getBlockId())) {
				isBlue = !isBlue;
			}
		}
		if (listeBadMessages != null && listeBadMessages.size() > 0) {
			for (Message msg : listeBadMessages) {
				System.out.println(msg.getMessageId());
			}
		}
		// parcours des lignes pour gestion des erreurs
		for (BinaryRow lignecourante : rows) {
			if (monitor.isCanceled())
				break;
			System.out.println(lignecourante.getMsgId());
			if (listeBadMessages != null && listeBadMessages.size() > 0) {
				Message badMsg = listeBadMessages.getMessageById(lignecourante
						.getMsgId());
				if (badMsg != null) {
					lignecourante.setError(badMsg.getError());
				}
			}
		}

		if (monitor.isCanceled())
			rows = new ArrayList<BinaryRow>(0);
		return rows;
	}

//	public ArrayList<BinaryRow> remplirTableauBRU() {
//		ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>();
//		BinaryRow row = null;
//		int debutLectureEnregistrement = 0;
//		int tailleEnregistrement = ConstantesParcoursBinaire.tailleBlocData;
//		String[] labels = null;
//
//		byte[] tableauOctets = TypeParseur.getInstance().getParser()
//				.getMessagebrut();
//
//		ArrayList<CRC_Erreur> crcEnreg = ((AParseurParcours) TypeParseur
//				.getInstance().getParser()).listCRC_Error;
//		String hexVal = null;
//
//		int i = 0;
//		int indiceLecture = debutLectureEnregistrement;
//		int indiceIdMessage = 0;
//		while (indiceLecture < tableauOctets.length) {
//			ActivatorData.getInstance().getVp()
//					.setValeurProgressBar(
//							indiceLecture * 100 / (tableauOctets.length));
//			row = new BinaryRow();
//			labels = new String[tailleEnregistrement];
//			if (tableauOctets[indiceLecture] == 86)
//				indiceIdMessage = indiceLecture;
//
//			row.setMsgId(indiceIdMessage);
//
//			for (int k = 0; k < tailleEnregistrement; k++) {
//
//				hexVal = Integer
//						.toHexString(tableauOctets[indiceLecture] & 0xff);
//				hexVal = hexVal.length() == 2 ? hexVal : "0" + hexVal;
//				// labels[k] = hexVal;
//
//				if (GestionnairePool.getInstance().getMapByteHexa().get(
//						tableauOctets[indiceLecture]) == null) {
//					GestionnairePool.getInstance().getMapByteHexa().put(
//							tableauOctets[indiceLecture], hexVal);
//					labels[k] = hexVal;
//				} else {
//					labels[k] = GestionnairePool.getInstance().getMapByteHexa().get(
//							tableauOctets[indiceLecture]);
//				}
//				indiceLecture++;
//				if (indiceLecture == tableauOctets.length) {
//					break;
//				}
//			}
//
//			row.setBlockId(i + 1);
//			row.setLabels(labels);
//
//			try {
//				if (crcEnreg.get(i) != null)
//					row.setError(ErrorType.CRC);
//			} catch (Exception e) {
//
//			}
//
//			rows.add(row);
//			i++;
//		}
//		boolean isBlue = false;
//		for (int j = 0; j < rows.size(); j++) {
//			BinaryRow ligne = rows.get(j);
//			ligne.setBlue(isBlue);
//			if ((j < rows.size() - 1)
//					&& (ligne.getMsgId() != rows.get(j + 1).getMsgId())) {
//				isBlue = !isBlue;
//			}
//		}
//		return rows;
//	}

	/**
	 * Méthode de remplissage de la vue binaire identique à la méthode utilisée
	 * pour les fichiers NG
	 * 
	 * @param monitor
	 * @return
	 */
	public ArrayList<BinaryRow> remplirTableauBRU2(IProgressMonitor monitor) {

		ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>(0);

		Enregistrement enrg = ActivatorData.getInstance().getVueData()
				.getDataTable().getEnregistrement();

		StringBuilder buf = new StringBuilder();
		String[] labels = null;
		byte[] dataMsg = null;
		BinaryRow row = null;

		ListMessages listeGoodMessages2 = enrg.getMessages();

		int idMsg = 0;
		// parcours des messages corrects
		if (listeGoodMessages2 != null && listeGoodMessages2.size() > 0) {

			// for each message create a number of rows
			for (Message msg1 : listeGoodMessages2) {
				if (monitor.isCanceled())
					break;
				idMsg = msg1.getMessageId();
				// msg = mapIdGoodMsg.get(id);
				int posBlock = idMsg / tailleBlocData;

				// get the data from the message
				dataMsg = msg1.getMessageData();

				// calculates the number of blocks
				int msgBlockNo = dataMsg.length / tailleBlocData;

				// create a row for each block
				for (int index = 0; index < msgBlockNo; index++) {
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

					int pos = idMsg / tailleBlocData + index + 1;

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
						rows.add(posBlock + index, row);
					}

					// reset buffer
					buf.setLength(0);
					row = null;
				}
				buf = new StringBuilder();
				msg1 = null;
				dataMsg = null;
				labels = null;
			}
		}

		int id = 0;
		// parcours des messages incorrects
		ListMessages listeBadMessages = enrg.getBadMessages();
		if (listeBadMessages != null && listeBadMessages.size() > 0) {
			// for each message create a number of rows
			for (Message msgBad : listeBadMessages) {
				if (monitor.isCanceled())
					break;
				id = msgBad.getMessageId();
				// msgBad = mapIdBadMsg.get(id);
				int posBlock = id / tailleBlocData;

				// get the data from the message
				dataMsg = msgBad.getMessageData();

				// calculates the number of blocks
				int msgBlockNo = dataMsg.length / tailleBlocData;

				// create a row for each block
				for (int index = 0; index < msgBlockNo; index++) {
					// creates as many labels as the length of a block
					labels = new String[tailleBlocData];

					for (int k = index * tailleBlocData; k < dataMsg.length; k++) {

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

					int pos = (id / tailleBlocData) + index + 1;

					// set the members of the row
					row.setBlockId(pos);
					row.setLabels(labels);
					row.setError(msgBad.getError());

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
						rows.add(posBlock + index, row);
					}

					// reset buffer
					buf.setLength(0);
					row = null;
				}
				buf = new StringBuilder();
				msgBad = null;
				dataMsg = null;
				labels = null;
			}
		}

		// set blue color for one message, and white for the other (1 blue, 1
		// white).
		// The color is not set when creating the rows, because first are added
		// the rows for the good messages, and after this the bad messages are
		// inserted
		// between the rows for bad massages
		boolean isBlue = false;

		for (int i = 0; i < rows.size(); i++) {
			if (monitor.isCanceled())
				break;
			ActivatorData.getInstance().getVp()
					.setValeurProgressBar(i * 100 / (rows.size()));
			BinaryRow ligne = rows.get(i);
			ligne.setBlue(isBlue);
			if ((i < rows.size() - 1)
					&& (ligne.getMsgId() != rows.get(i + 1).getMsgId())) {
				isBlue = !isBlue;
			}
		}

		if (monitor.isCanceled())
			rows = new ArrayList<BinaryRow>(0);
		return rows;

	}

	public ArrayList<BinaryRow> remplirTableau(IProgressMonitor monitor) {

		if (BridageFormats.getInstance().getFormatFichierOuvert("").getFjru() == FormatJRU.bru) {
			// return remplirTableauBRU();
			return remplirTableauBRU2(monitor);
		} else {
			return remplirTableauJRU(monitor);
		}
	}
}
