package com.faiveley.samng.principal.sm.calculs.referenceZero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Hashtable;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.vue.Messages;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTom4;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4;
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
public class CalculReferenceZeroTom4 extends ACalculReferenceZero {

	public CalculReferenceZeroTom4() {

	}

	public void calculerReferenceZero(int msgId, double PointRef) {
		String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
		String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
		String minute = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
		String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
		String milliseconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");
		try {

			VueData vueData = ActivatorData.getInstance().getVueData();

			Enregistrement enrg = null;
			if (vueData.getDataTable() == null|| vueData.getDataTable().getEnregistrement().getMessages().size()==0) {
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
			
			ListMessages listeMessages = enrg.getMessages();

			// get the reference time and distance
			Message m = listeMessages.getMessageById(msgId);
			Flag flagmsg = m.getFlag();
			Flag fl;
			if (flagmsg != null) {
				if (!(flagmsg.getLabel().contains(">"))) {
					String label;
					if (flagmsg.getLabel().contains("{")) {
						label = flagmsg.getLabel().replace("{", ">{");
					} else if (flagmsg.getLabel().contains("}")) {
						label = flagmsg.getLabel().replace("}", ">}");
					} else {
						label = flagmsg.getLabel().replace(flagmsg.getLabel(),
								">" + flagmsg.getLabel());
					}
					fl = new Flag(msgId, label, m.getEvenement()
							.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom());
					m.setFlag(fl);
				}
			} else {
				fl = new Flag(msgId, ">", m.getEvenement()
						.getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getNom());
				m.setFlag(fl);
			}

			double refTime = 0;
			double refDist = 0;
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
					refTime = m.getAbsoluteTime();
					refDist = m.getAbsoluteDistance();
					m.setRepereZero(true);
					vueData.setLastRepere0(m);
				}
			} else {
				throw new RuntimeException(
						"Selected message is not in the list of messages!!!!!");
			}

			AVariableComposant relVar = null;
						
			List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
			// creates the rows
			for (SegmentTemps seg : listeSegs) {

				TypeRupture rupture = TableRuptures.getInstance()
						.getRuptureTime(seg.getStartMsgId());
				if (rupture != null
						&& (rupture
								.compareTo(TypeRupture.RUPTURE_TEMP_CALCULEE) == 0)) {
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
					Message msgTemp = listeMessages.getMessageById(seg
							.getStartMsgId());
					if (msgTemp != null) {
						if (msgTemp != null) {
							if (msgTemp.getEvenement().isRuptureAcquisition()) {
								startId = seg.getStartMsgId();
							}
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
//								endId = (seg.getStartMsgId() - ConstantesParcoursBinaire.tailleBlocData);
								
								endId = TableSegments.getInstance().classerSegmentsTemps().get(new Integer(seg.getNumeroSegment()-1)).getEndMsgId();	
								break;
							}
						}
					
				}

			}

			// int numseg=m.ge

			long millisAvecChgt = 0;
			Hashtable<Integer, Long> decalageHashMap = new Hashtable<Integer, Long>();
			// parcours des messages pour récupérer les décalages relatifs aux
			// changements d'heures
			long decalageTps = 0;
			// this will return a copy of the variable temp and we change the
			// descriptor for it
			relVar = GestionnairePool.getInstance().getVariable(TypeRepere.tempsRelatif
					.getCode());

			for (Message msg :listeMessages) {

				if (relVar != null) {

					if (msg.getEvenement().isChangementHeure()) {
						ConversionTemps tc = new ConversionTempsTom4(
								"01/01/1990 00:00:00.000");

						double newDateValue = ConversionTemps
								.getDateFromTypeRepereDate(msg, TypeRepere.date);
						long newTempsValue = ConversionTemps
								.getTempsFromTypeRepereDate(msg,
										TypeRepere.date);

						tc.addDate(newDateValue);
						tc.addTime(newTempsValue);
						long millisSansChgt = tc.getCurrentDateAsMillis();

						long temps1 = ConversionTemps
								.getTempsFromTypeRepereDate(msg,
										TypeRepere.dateAvantChangement);
						double date1 = ConversionTemps
								.getDateFromTypeRepereDate(msg,
										TypeRepere.dateAvantChangement);

						tc.addDate(date1);
						tc.addTime(temps1);
						millisAvecChgt = tc.getCurrentDateAsMillis();

						// calcul du décalage lors d'un changement d'heure

						decalageTps = millisSansChgt - millisAvecChgt;

						decalageHashMap.put(msg.getMessageId(), decalageTps);
					}

				}

			}

			long decalageTmp = 0;
			for (Integer idMessage : decalageHashMap.keySet()) {

				if (m.getMessageId() > idMessage) {
					decalageTmp += decalageHashMap.get(idMessage);
				}

				else {
					decalageTmp -= decalageHashMap.get(idMessage);
				}
			}

			Message lastMsg = null;
			long tmpRelatif = 0;

			long deltaTempsRef0 = 0;
			long deltaTempsTempo = 0;
			long tampondeltaTempsTempo = 0;

			long date = -1;
			long dateLastMsg = -1;
			Hashtable<Integer, Long> deltaTempsMsgs = new Hashtable<Integer, Long>();

			// variables temporaires
			long tamponDate = -1;
			long tamponTemps = 0;
			long tempsLastMsg = 0;
			long temps = 0;
			long compteurTemps = 0;
			long compteurTempsLastMsg = 0;
			long tamponCptTemps = 0;
			long tamponCptTempsLastMsg = 0;
			long firstCptTemps = 0;
			long cumulTempsMax = 0;
			long lastHeureValue = 0;
			for (Message msg :listeMessages) {

				// the messages are ordonated and i should move forward to get
				// into
				// the segment
				if (msg.getMessageId() < startId) {
					if (lastStartId < startId) {
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.tempsRelatif.getCode());
						
						
						if (relVar != null) {
							
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
							
							
							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.distanceRelatif
										.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor

							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
					}

					continue;
				}
				// the message is after the segment. not interesting
				if (msg.getMessageId() > endId) {
					if (lastEndId > endId) {
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.tempsRelatif.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor

							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.distanceRelatif
										.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor

							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
						continue;
					}
					break;

				}

				// this will return a copy of the variable temp and we change
				// the
				// descriptor for it
				relVar = GestionnairePool.getInstance().getVariable(TypeRepere.tempsRelatif
						.getCode());

				if (relVar != null) {
					try {
						if (!msg.getEvenement().isChangementHeure()) {
							if (lastMsg == null) {
								deltaTempsTempo = 0;

							} else {
								

								if (lastMsg.getVariable(TypeRepere.date
										.getCode()) != null) {
									
									if(lastMsg.getEvenement().isRuptureAcquisition()){
									dateLastMsg = ConversionTemps
											.getDateFromTypeRepereDate(lastMsg,
													TypeRepere.date);
//									 on récupère le nombre de milliseconde
									// depuis le début de la journée de la
									// varaible dateheure
									tempsLastMsg = ConversionTemps
											.getTempsFromTypeRepereDate(
													lastMsg, TypeRepere.date);

									}
									else {
										if (lastMsg.getVariable(TypeRepere.temps
												.getCode()) != null) {

											compteurTempsLastMsg = (Long) lastMsg
													.getVariable(
															TypeRepere.temps
																	.getCode())
													.getCastedValeur()
													* ConstantesParcoursTom4.pasCptTps
													* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
											tamponCptTempsLastMsg = compteurTempsLastMsg;
										}else{
											
											if(lastMsg.getVariable(TypeRepere.tempsAvantChangement
												.getCode()) != null){
											
												tamponCptTempsLastMsg = 0;
											}else
											compteurTempsLastMsg = tamponCptTempsLastMsg;
										}
										dateLastMsg = tamponDate;
									}

									if (lastMsg.getVariable(TypeRepere.temps
											.getCode()) != null) {

										compteurTempsLastMsg = (Long) lastMsg
												.getVariable(
														TypeRepere.temps
																.getCode())
												.getCastedValeur()
												* ConstantesParcoursTom4.pasCptTps
												* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
										tamponCptTempsLastMsg = compteurTempsLastMsg;
									} else {

										compteurTempsLastMsg = tamponCptTempsLastMsg;
									}

								} else {
									
									if(lastMsg.getEvenement().isRazCompteurTemps()){
										
										compteurTempsLastMsg = 0;
										tamponCptTempsLastMsg = compteurTempsLastMsg;
									}
									
									
									if (lastMsg.getVariable(TypeRepere.temps
											.getCode()) != null) {

										compteurTempsLastMsg = (Long) lastMsg
												.getVariable(
														TypeRepere.temps
																.getCode())
												.getCastedValeur()
												* ConstantesParcoursTom4.pasCptTps
												* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
										tamponCptTempsLastMsg = compteurTempsLastMsg;
									} else {
										if(lastMsg.getVariable(TypeRepere.tempsAvantChangement
												.getCode()) != null){
											
												tamponCptTempsLastMsg = 0;
											}else{
												
											compteurTempsLastMsg = tamponCptTempsLastMsg;
											}

									}

									dateLastMsg = tamponDate;
									}
								

							
								
								
								if(msg.getEvenement().isRazCompteurTemps()){
								
									compteurTemps = 0;
									tamponCptTemps = compteurTemps;
								}
								
								else{
								
								
								if (msg.getVariable(TypeRepere.date.getCode()) != null) {

									
									// nombre de jours depuis date pivot
									if(msg.getEvenement().isRuptureAcquisition()){
										date = ConversionTemps
											.getDateFromTypeRepereDate(msg,
													TypeRepere.date);
//										 nombre de milliseconde depuis le début de
										// la journée de la variable date
										temps = ConversionTemps
												.getTempsFromTypeRepereDate(msg,
														TypeRepere.date);
										lastHeureValue = temps;
									}
									else{
										if(msg.getVariable(TypeRepere.temps
											.getCode()) != null){
											compteurTemps = (Long) msg.getVariable(
													TypeRepere.temps.getCode())
													.getCastedValeur()
													* ConstantesParcoursTom4.pasCptTps
													* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
											
											tamponCptTemps = compteurTemps;
										}
										else{
											compteurTemps = tamponCptTemps;
										}
										date = tamponDate;
									}
										
									// récupération de la valeur du compteur de
									// temps
									if (msg.getVariable(TypeRepere.temps
											.getCode()) != null) {

										compteurTemps = (Long) msg.getVariable(
												TypeRepere.temps.getCode())
												.getCastedValeur()
												* ConstantesParcoursTom4.pasCptTps
												* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
										
										tamponCptTemps = compteurTemps;
									} else {
										compteurTemps = tamponCptTemps;
									}

								} else {

									if (msg.getVariable(TypeRepere.temps
											.getCode()) != null) {
										compteurTemps = (Long) msg.getVariable(
												TypeRepere.temps.getCode())
												.getCastedValeur()
												* ConstantesParcoursTom4.pasCptTps
												* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
										tamponCptTemps = compteurTemps;
									} else {
										compteurTemps = tamponCptTemps;
									}

									date = tamponDate;
								}
								}
								

								// si l'on change de date, on doit incrémenter
								// le compteur/décrémenter le compteur
								// d'autant de jour
								// il faut que la date soit valorisée
								if (date != -1 && dateLastMsg != -1
										&& (date != dateLastMsg))
									temps += (date - dateLastMsg)
											* (24 * 3600 * 1000);

								if(temps==0 && tempsLastMsg>0)
									temps = tempsLastMsg;

								if (msg.getVariable(TypeRepere.date.getCode()) != null) {
									
									deltaTempsTempo = deltaTempsTempo + temps
											- tempsLastMsg + compteurTemps
											- compteurTempsLastMsg
											- firstCptTemps;
								} 
								else if(msg.getEvenement().isRazCompteurTemps()){
								
//									cumulTempsMax += ConstantesParcoursTom4.pasCptTps
//									* (long) (ConstantesParcoursTom4.resolutionTemps * 1000)
//									* ConstantesParcoursTom4.maxCptTps;
									deltaTempsTempo = deltaTempsTempo + temps
									- tempsLastMsg + ConstantesParcoursTom4.pasCptTps
									* (long) (ConstantesParcoursTom4.resolutionTemps * 1000)
									* ConstantesParcoursTom4.maxCptTps
									- compteurTempsLastMsg;
									
								}
									
								else {
									
									
									// on calcul le delta temps
									// deltaTempsCumul + temps message courant -
									// temps message précédent
									deltaTempsTempo = deltaTempsTempo + temps
											- tempsLastMsg + compteurTemps
											- compteurTempsLastMsg;
								}
								
								
								
								// on met en mémoire les valeurs des variables
								// temps
								tamponDate = date;
								tamponTemps = temps;
							}

						} else {

							
							
							if (lastMsg.getVariable(TypeRepere.temps
									.getCode()) != null) {

								compteurTempsLastMsg = (Long) lastMsg
										.getVariable(
												TypeRepere.temps
														.getCode())
										.getCastedValeur()
										* ConstantesParcoursTom4.pasCptTps
										* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
								tamponCptTempsLastMsg = compteurTempsLastMsg;
								
									
								
							}else if(lastMsg.getVariable(TypeRepere.tempsAvantChangement
										.getCode()) != null){
								compteurTempsLastMsg = 0;
								
								}
							else
								
								compteurTempsLastMsg = tamponCptTempsLastMsg;
							
							
							
							//pour les fichiers hsbc, la variable temps n'est pas valorisée mais  la variable tempsAvantChangement est valorisée et on doit l'utiliser en guise de compteur temps
							if (msg.getVariable(TypeRepere.tempsAvantChangement
									.getCode()) != null) {
								compteurTemps = new Long(msg.getVariable(
										TypeRepere.tempsAvantChangement.getCode()).toString())
										* (long)ConstantesParcoursTom4.pasCptTps
										* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
								tamponCptTemps = compteurTemps;
							} else {
								//pour les fichiers uk, il n'y a pas de tempsAvantChangement, mais la variable temps est elle valorisé
								if (msg.getVariable(TypeRepere.temps
										.getCode()) != null) {
									compteurTemps = (Long) msg.getVariable(
											TypeRepere.temps.getCode())
											.getCastedValeur()
											* ConstantesParcoursTom4.pasCptTps
											* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
									tamponCptTemps = compteurTemps;
								} else {
									compteurTemps = tamponCptTemps;
								}
							}

							date = tamponDate;
							
							
							
							
							
							
							
							
							
						
							deltaTempsTempo = deltaTempsTempo+ compteurTemps - compteurTempsLastMsg;
						
						}

					} catch (Exception e) {
						deltaTempsTempo = tampondeltaTempsTempo;
					}
					
					
					deltaTempsMsgs.put(new Integer(msg.getMessageId()),
							new Long(deltaTempsTempo));
					tampondeltaTempsTempo = deltaTempsTempo;
				}

				if (msg.getMessageId() == m.getMessageId()) {
					deltaTempsRef0 = deltaTempsTempo;
				}
				lastMsg = msg;
			}

			long lastTempRelatif = 0;
			for (Message msg : listeMessages) {

				// try {
				// Long tmpR = ((Long) deltaTempsMsgs
				// .get(new Integer(msg.getMessageId())))
				// - deltaTempsRef0;
				// if (tmpR==null) {
				// continue;
				// }
				// } catch (Exception e) {
				// continue;
				// }

				// the messages are ordonated and i should move forward to get
				// into
				// the segment
				if (msg.getMessageId() < startId) {
					if (lastStartId < startId) {
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.tempsRelatif.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.distanceRelatif
										.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
					}
					continue;
				}
				// the message is after the segment. not interesting
				if (msg.getMessageId() > endId) {
					if (lastEndId > endId) {
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.tempsRelatif.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor

							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
						relVar = GestionnairePool.getInstance()
								.getVariable(TypeRepere.distanceRelatif
										.getCode());
						if (relVar != null) {
							relVar.setValeur(VAR_SANS_VALEUR);//tagValCor

							// add variable to the message
							//msg.ajouterVariable(relVar);
							msg.modifierVariable(relVar);
						}
						continue;
					}
					break;

				}

				// this will return a copy of the variable temp and we change
				// the
				// descriptor for it
				relVar = GestionnairePool.getInstance().getVariable(TypeRepere.tempsRelatif
						.getCode());
				if (relVar != null) {

					String str = null;
					// pour le message ou l'on
					if (msg == m) {
						tmpRelatif = 0;
						str = ConversionTemps.getRelativeTimeAsString(0,jour,heure,minute,seconde,milliseconde);

					} else {

						if (!msg.getEvenement().isChangementHeure()) {
							if (lastMsg == null) {
								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg.getMessageId())))
										- deltaTempsRef0;

							} else {
								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg.getMessageId())))
										- deltaTempsRef0;
							}

						} else {

							long tempsAvtChgt = ConversionTemps
									.getTempsFromTypeRepereDate(msg,
											TypeRepere.dateAvantChangement);
							long dateAvtChgt = ConversionTemps
									.getDateFromTypeRepereDate(msg,
											TypeRepere.dateAvantChangement);

							ConversionTemps tc = new ConversionTempsTom4(
									"01/01/1990 00:00:00.000");

							tc.addDate(dateAvtChgt);
							tc.addTime(tempsAvtChgt);

							if (lastMsg == null) {

								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg.getMessageId())))
										- deltaTempsRef0;
							} else {

								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg.getMessageId())))
										- deltaTempsRef0;
							}

						}

					}

					str = ConversionTemps.getRelativeTimeAsString(tmpRelatif,jour,heure,minute,seconde,milliseconde);
					if (tmpRelatif == 0
							&& msg.getMessageId() < m.getMessageId())
						str = str.replace('+', '-');
					if (tmpRelatif == 0
							&& msg.getMessageId() == m.getMessageId())
						str = str.substring(1);
					str = str.replaceAll(",", ".");

					// String distanceRelativeString = new
					// String((byte[])msg.getVariable(TypeRepere.distanceRelatif.getCode()).getValeur());
					// String signe =null;
					// float valeur =0;
					// String distStr;
					// if(distanceRelativeString!=null){
					// signe = distanceRelativeString.substring(0, 1);
					// valeur = new Float(distanceRelativeString.substring(1,
					// distanceRelativeString.length()));
					// SegmentDistance segment =
					// TableSegments.getInstance().getContainingDistanceSegment(msg.getMessageId());
					// double valModif = segment.getDiameterCorrige();
					// double factor = valModif / segment.getInitialDiameter();
					// double distCorr = Math.abs(msg.getAbsoluteDistance() *
					// factor);
					// DecimalFormat fmt = new DecimalFormat("0.000");
					// String distanceRelativeStringCorrigee = signe +" "
					// +(fmt.format((double) distCorr)).replace(",",
					// ".");
					//					
					// distStr = distanceRelativeStringCorrigee;
					// //distCorrection.setValeur(distanceRelativeStringCorrigee.getBytes());
					// //distCorrection.setTypeValeur(Type.string);
					//					
					// }
					//				
					relVar.setValeur(str.getBytes());//tagValCor
					// add variable to the message
					//msg.ajouterVariable(relVar);
					msg.modifierVariable(relVar);

				} else {
					throw new RuntimeException(Messages.getString("ReferenceAction.3"));
				}

				relVar = GestionnairePool.getInstance().getVariable(TypeRepere.distanceRelatif.getCode());
				if (relVar != null) {
					double dif = msg.getAbsoluteDistance() - refDist + PointRef;
					BigDecimal b = new BigDecimal(dif);
					b.setScale(6, RoundingMode.DOWN);

					DecimalFormat fmt = new DecimalFormat("0.000");
					String s = fmt.format(b.floatValue());
					String str = (dif >= 0 ? "+" : "") + s;
					str = str.replaceAll(",", ".");
					relVar.setValeur(str.trim().getBytes());//tagValCor

					// add variable to the message
					//msg.ajouterVariable(relVar);
					msg.modifierVariable(relVar);
				} else {
					throw new RuntimeException(Messages
							.getString("ReferenceAction.4"));
				}
				lastMsg = msg;
			}
			
			lastStartId = startId;
			lastEndId = endId;
			System.gc();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
