package com.faiveley.samng.principal.sm.calculs.referenceZero;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;

import java.util.Hashtable;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.vue.Messages;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsJRU;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTomNg;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;

/**
 * Classe de calcul de la reference zero avec les fichiers TOM_NG
 * 
 * @author Olivier
 * 
 */
public class CalculReferenceZeroJRU extends ACalculReferenceZero {

	public CalculReferenceZeroJRU() {

	}

	public void calculerReferenceZero(int msgId, double PointRef) {
		String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
		String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
		String minutes = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
		String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
		String ms = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");

		VueData vueData = ActivatorData.getInstance().getVueData();
		Enregistrement enrg = null;
		if (vueData.getDataTable() == null || (vueData.getDataTable().getEnregistrement()!=null && vueData.getDataTable().getEnregistrement().getMessages().size()==0)) {
			// get parcours
			ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
			if (p == null) {
				return;
			}
			// get enregisrtrement
			enrg = p.getData().getEnregistrement();
			if (enrg == null) {
				return;
			}
		}else{
			enrg = vueData.getDataTable().getEnregistrement();
		}

		int codeTpsAvtChgt = TypeRepere.tempsAvantChangement.getCode();
		int codeTpsRel = TypeRepere.tempsRelatif.getCode();
		int codeDistRel = TypeRepere.distanceRelatif.getCode();
		int codeTemps = TypeRepere.temps.getCode();
		int codeDateAvtChgt = TypeRepere.dateAvantChangement.getCode();
		int codeDate = TypeRepere.date.getCode();
		
		ListMessages listeMessages = enrg.getMessages();

		// get the reference time and distance
		Message m = listeMessages.getMessageById(Integer.valueOf(msgId));

		if (m!=null) {
			Flag flagmsg=null;
			flagmsg = m.getFlag();

			Flag fl;
			if (flagmsg != null) {
				if (!(flagmsg.getLabel().contains(">"))) {
					String label;
					if (flagmsg.getLabel().contains("{")) {
						label = flagmsg.getLabel().replace("{", ">{");
					} else if (flagmsg.getLabel().contains("}")) {
						label = flagmsg.getLabel().replace("}", ">}");
					} else {
						label = flagmsg.getLabel().replace(flagmsg.getLabel(), ">" + flagmsg.getLabel());
					}
					fl = new Flag(msgId, label, m.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom());
					m.setFlag(fl);
				}
			} else {
				fl = new Flag(msgId, ">", m.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom());
				m.setFlag(fl);
			}
		}

		int startId = 0;
		int endId = 0;

		// : maybe we should take into account also the reset to no ref 0
		if (m != null) {
			if (vueData != null) {
				Message lastRepere = vueData.getLastRepere0();
				if (lastRepere == m && lastRepere.isRepereZero())
					return; // nothing to do if is the same message as ref0
				// Update previous repere flag to false
				if (lastRepere != null && lastRepere.isRepereZero()) {
					lastRepere.setRepereZero(false);
					Flag flagLastRep = lastRepere.getFlag();
					String label = null;
					if (flagLastRep.getLabel().contains(">")) {
						label = flagLastRep.getLabel().replace(">", "");
					}
					if (label == null) {
						lastRepere.setFlag(null);
					} else {
						flagLastRep.setLabel(label);
					}
				}
				// update the new repere and set in the vue data
				m.setRepereZero(true);
				vueData.setLastRepere0(m);
			}
		} else {
			throw new RuntimeException("Selected message is not in the list of messages!!!!!");
		}

		AVariableComposant relVar = null;
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		// creates the rows
		for (SegmentTemps seg : listeSegs) {
			TypeRupture rupture = TableRuptures.getInstance().getRuptureTime(seg.getStartMsgId());
			if (rupture != null && (rupture.compareTo(TypeRupture.RUPTURE_TEMP_CALCULEE) == 0)) {
				if (seg.getStartMsgId() <= msgId) {
					startId = seg.getStartMsgId();
					endId = 0;
				} else {
					break;
				}
			}
			if (seg.getStartMsgId() <= msgId && startId == 0) {
				startId = seg.getStartMsgId();
			}

			if (seg.getStartMsgId() <= msgId) {
				Message msgTemp = listeMessages.getMessageById(seg.getStartMsgId());
				if (msgTemp != null) {
					if (msgTemp.getEvenement().isRuptureAcquisition()) {
						startId = seg.getStartMsgId();
					}
				}
			}

			if (seg.getEndMsgId() >= msgId) {
				endId = seg.getEndMsgId();
			}

			if (seg.getStartMsgId() > msgId) {
				Message msgTemp = listeMessages.getMessageById(seg.getStartMsgId());
				if (msgTemp != null) {
					if (msgTemp.getEvenement().isRuptureAcquisition()) {
						endId = (seg.getStartMsgId() - tailleBlocData);
						break;
					}
				}
			}
		}

		long millisAvecChgt = 0;
		Hashtable<Integer, Long> decalageHashMap = new Hashtable<Integer, Long>();
		// parcours des messages pour récupérer les décalages relatifs aux
		// changements d'heures
		long decalageTps = 0;
		// this will return a copy of the variable temp and we change the
		// descriptor for it
		relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);

		for (Message msg : listeMessages) {
			if (relVar != null) {
				if (msg.getEvenement().isChangementHeure()) {
					ConversionTemps tc = new ConversionTempsTomNg("01/01/2000 00:00:00.000");

					//long temps = (Long) (msg.getVariable(codeTemps).getCastedValeur());
					long temps = ConversionTempsJRU.getTimeFromVariableTIME(msg.getVariable(codeTemps));

					//long date = (Long) (msg.getVariable(codeDate).getCastedValeur());
					long date = (long)ConversionTempsJRU.getDateFromVariableDATE(msg.getVariable(codeDate));

					tc.addDate(date);
					tc.addTime(temps);
					long millisSansChgt = tc.getCurrentDateAsMillis();

					//long temps1 = (Long) (msg.getVariable(codeTpsAvtChgt).getCastedValeur());
					long temps1 = ConversionTempsJRU.getTimeFromVariableTIME(msg.getVariable(codeTpsAvtChgt));

					//long date1 = (Long) (msg.getVariable(codeDateAvtChgt).getCastedValeur());
					long date1 = (long)ConversionTempsJRU.getDateFromVariableDATE(msg.getVariable(codeDateAvtChgt));

					tc.addDate(date1);
					tc.addTime(temps1);
					millisAvecChgt = tc.getCurrentDateAsMillis();

					// calcul du décalage lors d'un changement d'heure

					decalageTps = millisSansChgt - millisAvecChgt;
					decalageHashMap.put(msg.getMessageId(), decalageTps);
				}
			}
		}

		Message lastMsg = null;
		long tmpRelatif = 0;
		long deltaTempsRef0 = 0;
		long deltaTempsTempo = 0;
		long date = -1;
		long dateLastMsg = -1;
		Hashtable<Integer, Long> deltaTempsMsgs = new Hashtable<Integer, Long>();

		for (Message msg : listeMessages) {
			// the messages are ordonated and i should move forward to get into
			// the segment
			if (msg.getMessageId() < startId) {
				if (lastStartId < startId) {
					relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
					relVar = GestionnairePool.getInstance().getVariable(codeDistRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
				}
				continue;
			}
			// the message is after the segment. not interesting
			if (msg.getMessageId() > endId) {
				if (lastEndId > endId) {
					relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
					relVar = GestionnairePool.getInstance().getVariable(codeDistRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
					continue;
				}
				break;
			}

			// this will return a copy of the variable temp and we change the
			// descriptor for it
			relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
			if (relVar != null) {
				if (!msg.getEvenement().isChangementHeure()) {
					if (lastMsg == null) {
						deltaTempsTempo = 0;
					} else {
						// récupération des date et temps du message courant
						//long temps = (Long) (msg.getVariable(codeTemps).getCastedValeur());
						long temps = ConversionTempsJRU.getTimeFromVariableTIME(msg.getVariable(codeTemps));

						if (msg.getVariable(codeDate) != null) {
							//date = (Long) (msg.getVariable(codeDate).getCastedValeur());
							date = (long)ConversionTempsJRU.getDateFromVariableDATE(msg.getVariable(codeDate));
						}

						// récupération des date et temps du message précédent
						//long tempsLastMsg = (Long) (lastMsg.getVariable(codeTemps).getCastedValeur());
						long tempsLastMsg =  ConversionTempsJRU.getTimeFromVariableTIME(lastMsg.getVariable(codeTemps));

						if (lastMsg.getVariable(codeDate) != null) {
							//dateLastMsg = (Long) (lastMsg.getVariable(codeDate).getCastedValeur());
							dateLastMsg = (long)ConversionTempsJRU.getDateFromVariableDATE(lastMsg.getVariable(codeDate));
						}

						// si l'on change de date, on doit incrémenter le
						// compteur/décrémenter le compteur
						// d'autant de jour
						// il faut que la date soit valorisée
						if (date != -1 && dateLastMsg != -1 	&& (date == dateLastMsg + 1))
							temps += (date - dateLastMsg) * (24 * 3600 * 1000);

						// on calcul le delta temps
						// deltaTempsCumul + temps message courant - temps
						// message précédent
						deltaTempsTempo = deltaTempsTempo + temps - tempsLastMsg;
					}
				} else {
					//long tempsAvtChgt = (Long) (msg.getVariable(codeTpsAvtChgt).getCastedValeur());
					long tempsAvtChgt = ConversionTempsJRU.getTimeFromVariableTIME(lastMsg.getVariable(codeTpsAvtChgt));

					//long dateAvtChgt = (Long) (msg.getVariable(codeDateAvtChgt).getCastedValeur());
					long dateAvtChgt = (long)ConversionTempsJRU.getDateFromVariableDATE(lastMsg.getVariable(codeDateAvtChgt));

					ConversionTemps tc = new ConversionTempsTomNg("01/01/2000 00:00:00.000");

					tc.addDate(dateAvtChgt);
					tc.addTime(tempsAvtChgt);

					//long tempsLastMsg = (Long) (lastMsg.getVariable(codeTemps).getCastedValeur());
					long tempsLastMsg = ConversionTempsJRU.getTimeFromVariableTIME(lastMsg.getVariable(codeTemps));

					if (lastMsg.getVariable(codeDate) != null) {
						//dateLastMsg = (Long) (lastMsg.getVariable(codeDate).getCastedValeur());
						dateLastMsg = (long)ConversionTempsJRU.getDateFromVariableDATE(lastMsg.getVariable(codeDate));
					}

					if (dateLastMsg != -1 && (dateLastMsg != dateAvtChgt))
						tempsAvtChgt += (dateAvtChgt - dateLastMsg)	* (24 * 3600 * 1000);

					// deltaTempsCumul + temps message courant - temps
					// message précédent
					deltaTempsTempo = deltaTempsTempo + tempsAvtChgt - tempsLastMsg;
				}
				deltaTempsMsgs.put(new Integer(msg.getMessageId()), new Long(deltaTempsTempo));
			}

			if (msg.getMessageId() == m.getMessageId()) {
				deltaTempsRef0 = deltaTempsTempo;

			}
			lastMsg = msg;
		}

		int idMsg =0;
		for (Message msg : listeMessages) {
			idMsg = msg.getMessageId();
			// the messages are ordonated and i should move forward to get into
			// the segment
			if (idMsg < startId) {
				if (lastStartId < startId) {
					relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
					relVar = GestionnairePool.getInstance().getVariable(codeDistRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
				}
				continue;
			}
			// the message is after the segment. not interesting
			if (idMsg > endId) {
				if (lastEndId > endId) {
					relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
					relVar = GestionnairePool.getInstance().getVariable(codeDistRel);
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);// tagValCor
						msg.modifierVariable(relVar);
					}
					continue;
				}
				break;
			}

			// this will return a copy of the variable temp and we change the
			// descriptor for it
			relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
			if (relVar != null) {
				String str = null;
				// pour le message ou l'on
				if (msg == m) {
					tmpRelatif = 0;
					str = ConversionTemps.getRelativeTimeAsString(0,jour, heure,minutes,seconde,ms);
				} else {
					if (!msg.getEvenement().isChangementHeure()) {
						tmpRelatif = ((Long) deltaTempsMsgs.get(new Integer(idMsg))) - deltaTempsRef0;
					} else {
						//long tempsAvtChgt = (Long) (msg.getVariable(codeTpsAvtChgt).getCastedValeur());
						long tempsAvtChgt = ConversionTempsJRU.getTimeFromVariableTIME(msg.getVariable(codeTpsAvtChgt));

						//long dateAvtChgt = (Long) (msg.getVariable(codeDateAvtChgt).getCastedValeur());
						long dateAvtChgt = (long)ConversionTempsJRU.getDateFromVariableDATE(msg.getVariable(codeDateAvtChgt));

						ConversionTemps tc = new ConversionTempsTomNg("01/01/2000 00:00:00.000");
						tc.addDate(dateAvtChgt);
						tc.addTime(tempsAvtChgt);

						tmpRelatif = ((Long) deltaTempsMsgs.get(new Integer(idMsg))) - deltaTempsRef0;
					}
				}

				str = ConversionTemps.getRelativeTimeAsString(tmpRelatif,jour,heure,minutes,seconde,ms);
				if(tmpRelatif == 0){
					if( idMsg < m.getMessageId())
						str = str.replace('+', '-');
					else if(idMsg == m.getMessageId())
						str = str.substring(1);;
				}

				str = str.replaceAll(",", ".");
				relVar.setValeur(str.getBytes());// tagValCor
				msg.modifierVariable(relVar);
			} else {
				throw new RuntimeException(Messages.getString("ReferenceAction.3"));
			}
			lastMsg = msg;
		}
		lastStartId = startId;
		lastEndId = endId;
		System.gc();
	}
}
