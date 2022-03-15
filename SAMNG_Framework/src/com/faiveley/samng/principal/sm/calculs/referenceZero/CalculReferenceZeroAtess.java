package com.faiveley.samng.principal.sm.calculs.referenceZero;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.vue.Messages;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsAtess;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ConstantesParcoursATESS;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;

public class CalculReferenceZeroAtess extends ACalculReferenceZero {

	double refDist;
	int startId,endId;
	Message lastMsg;
	long tmpRelatif;
	long deltaTempsRef0,deltaTempsTempo,tampondeltaTempsTempo;
	long date,dateLastMsg;
	Hashtable<Integer, Long> deltaTempsMsgs = new Hashtable<Integer, Long>();
	boolean firstTimeCountValue=false;

	// variables temporaires
	long tamponDate;
	long tempsLastMsg;
	long tamponTemps;
	long temps;
	long compteurTemps;
	long compteurTempsLastMsg;
	long tamponCptTemps;
	long tamponCptTempsLastMsg;

	public CalculReferenceZeroAtess() {
		// TODO Auto-generated constructor stub
	}

	public void calculerReferenceZero(int msgId, double PointRef) {
		try {
			VueData vueData = ActivatorData.getInstance().getVueData();


			Enregistrement enrg = null;
			if (vueData.getDataTable() == null || vueData.getDataTable().getEnregistrement().getMessages().size()==0) {
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
			Message m=initialiserFlags(listeMessages, msgId);

			initVars1();
			updatePreviousRepereFlag(m, vueData);
			AVariableComposant relVar = null;		
			initSegmentsTemps(msgId, listeMessages);
			initDates(relVar, listeMessages, m);
			initVars2();

			gestionTemps(listeMessages, relVar, m);

			ajouterVariable(listeMessages, relVar, m, PointRef);

			lastStartId = getStartId();
			lastEndId = getEndId();
			System.gc();

		} catch (Exception e) {
//			System.out.println("ezae "+msgId);
		}
	}
	
	
	public HashMap<String, Object> calculerTempsEtDistanceCumulee(int msgStartId, int msgEndId,double pointRef) { 
		HashMap<String,Object> hashMapTempsDistance = null;
		
		try {
			VueData vueData = ActivatorData.getInstance().getVueData();


			Enregistrement enrg = null;
			if (vueData.getDataTable() == null) {
				// get parcours
				ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
				if (p == null) {
					return null;
				}
				// get enregisrtrement
				enrg = p.getData().getEnregistrement();
				if (enrg == null) {
					return null;
				}
			}else{
				enrg = vueData.getDataTable().getEnregistrement();
			}
			
			ListMessages listeMessages = enrg.getMessages();
			Message m = listeMessages.getMessageById(msgStartId);

			
			this.startId=msgStartId;
			this.endId=msgEndId;
			AVariableComposant relVar = null;		
		
			initDates(relVar, listeMessages, m);
			initVars2();

			gestionTemps(listeMessages, relVar, m);

			hashMapTempsDistance = calculerDistanceTempsCumules(listeMessages, relVar, m,pointRef);

			lastStartId = this.startId;
			lastEndId = this.endId;

		} catch (Exception e) {

		}
		
		return hashMapTempsDistance;
	}
	
	public Enregistrement initCalcul(){
		//get parcours
		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		if (p == null) {
			return null;
		}
		// get enregisrtrement
		Enregistrement enrg = p.getData().getEnregistrement();
		if (enrg == null) {
			return null;
		}
		return enrg;
	}
	
	public Message initialiserFlags(ListMessages listeMessages,int msgId){
//		get the reference time and distance
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
				fl = new Flag(msgId, label, m.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom());
				m.setFlag(fl);
			}
		} else {
			fl = new Flag(msgId, ">", m.getEvenement()
					.getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getNom());
			m.setFlag(fl);
		}
		return m;
	}

	public void initVars1(){
		setRefDist(0);
		setStartId(0);
		setEndId(0);
	}

	public void updatePreviousRepereFlag(Message m,VueData vueData){
		//maybe we should take into account also the reset to no ref 0
		if (m != null) {
			Message lastRepere = vueData.getLastRepere0();
			if (lastRepere == m && lastRepere.isRepereZero()) {
				// is the same message as ref0
				// still get absolute distance of this event for ref dist
				setRefDist(m.getAbsoluteDistance());
				return;
			}
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
			setRefDist(m.getAbsoluteDistance());
			m.setRepereZero(true);
			vueData.setLastRepere0(m);
		}
	}

	public void initSegmentsTemps(int msgId,ListMessages listeMessages){
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		// creates the rows
		for (SegmentTemps seg : listeSegs) {
			TypeRupture rupture = TableRuptures.getInstance().getRuptureTime(seg.getStartMsgId());
			if (rupture != null
					&& (rupture.compareTo(TypeRupture.RUPTURE_TEMP_CALCULEE) == 0)) {
				if (seg.getStartMsgId() <= msgId) {
					setStartId(seg.getStartMsgId());
					setEndId(0);
				} else {
					break;
				}
			}

			if (seg.getStartMsgId() <= msgId && getStartId() == 0) {
				setStartId(seg.getStartMsgId());
			}

			if (seg.getStartMsgId() <= msgId) {
				Message msgTemp = listeMessages.getMessageById(seg.getStartMsgId());
				if (msgTemp != null) {
					if (msgTemp != null) {
						if (msgTemp.getEvenement().isRuptureAcquisition()) {
							setStartId(seg.getStartMsgId());
						}
					}
				}
			}

			if (seg.getEndMsgId() >= msgId) {
				setEndId(seg.getEndMsgId());
			}

			if (seg.getStartMsgId() > msgId) {
				Message msgTemp = listeMessages.getMessageById(seg.getStartMsgId());

				if (msgTemp != null) {
					if (msgTemp.getEvenement().isRuptureAcquisition()) {
						setEndId(TableSegments.getInstance().classerSegmentsTemps().get(Integer.valueOf(seg.getNumeroSegment()-1)).getEndMsgId());
						break;
					}
				}
			}
		}
	}

	public void initDates(AVariableComposant relVar,ListMessages listeMessages,Message m){
		long millisAvecChgt = 0;
		Hashtable<Integer, Long> decalageHashMap = new Hashtable<Integer, Long>();
		// parcours des messages pour récupérer les décalages relatifs aux
		// changements d'heures
		long decalageTps = 0;
		// this will return a copy of the variable temp and we change the
		// descriptor for it
		relVar = GestionnairePool.getInstance().getVariable(getCodeTR());

		for (Message msg : listeMessages) {
			if (relVar != null) {
				if (msg.getEvenement().isChangementHeure()) {
					ConversionTemps tc = new ConversionTempsAtess("01/01/1970 00:00:00.000");

					double newDateValue = getDate(msg);
					long newTempsValue = getTemps(msg)+getCompteurTemps(msg);

					tc.addDate(newDateValue);
					tc.addTime(newTempsValue);
					long millisSansChgt = tc.getCurrentDateAsMillis();

					long temps1 = getTempsAvantChangement(msg)+getCompteurTempsAvantChangement(msg);		
					double date1 = getDateAvantChangement(msg);

					tc.addDate(date1);
					tc.addTime(temps1);
					millisAvecChgt = tc.getCurrentDateAsMillis();

					// calcul du décalage lors d'un changement d'heure
					decalageTps = millisSansChgt - millisAvecChgt;
					decalageHashMap.put(msg.getMessageId(), decalageTps);
				}
			}
		}
	}

	public void initVars2(){
		setLastMsg(null);
		setTmpRelatif(0);
		setDeltaTempsRef0(0);
		resetDeltaTempsTempo();
		setTampondeltaTempsTempo(0);
		setDate(-1);
		setDateLastMsg(-1);
		setDeltaTempsMsgs(new Hashtable<Integer, Long>());
		// variables temporaires
		setTamponDate(-1);
		setTempsLastMsg(0);
		setTemps(0);
		setCompteurTemps(0);
		setCompteurTempsLastMsg(0);
		setTamponCptTemps(0);
		setTamponCptTempsLastMsg(0);
	}
	
	public void gestionTemps(List<Message> listeMessages,AVariableComposant relVar,Message m){
		
		for (Message msg : listeMessages) {
			
			// the messages are ordonated and i should move forward to get
			// into
			// the segment
			if (msg.getMessageId() < getStartId()) {
				msgIDAvantStartID(relVar, msg);
				continue;
			}
			// the message is after the segment. not interesting
			if (msg.getMessageId() > getEndId()) {
				if (msgIDApresEndID(relVar, msg))
					continue;
				break;
			}
			
			// this will return a copy of the variable temp and we change
			// the
			// descriptor for it
			relVar = GestionnairePool.getInstance().getVariable(getCodeTR());

			
			
			if (relVar != null) {
				try {
					if (msg.getEvenement().isChangementHeure()) {
						evIsChangmtHeure(msg);
					} else {
						if (getLastMsg()!=null) {
							evIsNotChangmtHeure(msg);
						}
					}
				} catch (Exception e) {
					setDeltaTempsTempo(getTampondeltaTempsTempo());
				}
				deltaTempsMsgs.put(Integer.valueOf(msg.getMessageId()),Long.valueOf(getDeltaTempsTempo()));
				setTampondeltaTempsTempo(getDeltaTempsTempo());
				
				if (msg.getVariable(getCodeD())!=null) {
					setTamponTemps(getTemps(msg));
					setDate((long)getDate(msg));
				}
			}

			if (msg.getMessageId() == m.getMessageId()) {
				setDeltaTempsRef0(getDeltaTempsTempo());
			}
			setLastMsg(msg);
			
			if (msg.getVariable(getCodeT())!=null) {
				firstTimeCountValue=true;
				setTamponCptTemps(getCompteurTemps(msg)*1000);
				setTamponCptTempsLastMsg(getCompteurTemps(msg)*1000);
			}
		}
	}

	public void evIsChangmtHeure(Message msg){
		
			
		if (getLastMsg() == null) {
			resetDeltaTempsTempo();
		} else {
			if (getLastMsg().getVariable(getCodeD()) != null) {
				lastMsgIsDate();				
			} else {
				if(getLastMsg().getEvenement().isRazCompteurTemps()){
					setCompteurTempsLastMsg(0);
					fillTamponCptTempsLastMsg();
				}
				if (getLastMsg().getVariable(getCodeT()) != null) {
					setCompteurTempsLastMsg((Long) getLastMsg().getVariable(getCodeT()).getCastedValeur()* getCoefTemps());
					fillTamponCptTempsLastMsg();
				} else {
					if(getLastMsg().getVariable(getCodeTAC()) != null){
						setTamponCptTempsLastMsg(0);
					}else{
						setCompteurTempsLastMsg(getTamponCptTempsLastMsg());
					}
				}
				setDateLastMsg(getTamponDate());
			}

			if(msg.getEvenement().isRazCompteurTemps()){
				setCompteurTemps(0);
				setTamponCptTemps(getCompteurTemps());
			}

			else{
				if (msg.getVariable(getCodeD()) != null) {
					// nombre de jours depuis date pivot
					if(msg.getEvenement().isRuptureAcquisition()){
						setDate((long)getDate(msg));
//						nombre de milliseconde depuis le début de
						// la journée de la variable date
						setTemps(getTemps(msg));
					}
					else{
						if(msg.getVariable(getCodeT()) != null){
							fillCompteurTemps(msg);
							fillTamponCptTemps();
						}
						else{
							fillCompteurTempsWithTampon();
						}
						setDate((long)getDate(msg));
					}	

					// récupération de la valeur du compteur de
					// temps
					if (msg.getVariable(getCodeT()) != null) {
						fillCompteurTemps(msg);
						fillTamponCptTemps();
					} else {
						fillCompteurTempsWithTampon();
					}
				} else {
					if (msg.getVariable(getCodeT()) != null) {
						fillCompteurTemps(msg);
						fillTamponCptTemps();
					} else {
						fillCompteurTempsWithTampon();
					}
					setDate(getTamponDate());
				}
			}

			// si l'on change de date, on doit incrémenter
			// le compteur/décrémenter le compteur
			// d'autant de jour
			// il faut que la date soit valorisée
			if (getDate() != -1 && getDateLastMsg() != -1	&& (getDate() != getDateLastMsg()))
				setTemps (getTemps()+(getDate()-getDateLastMsg())* (24 * 3600 * 1000));

			if(getTemps()==0 && getTempsLastMsg()>0)
				setTemps(getTempsLastMsg());

			
			if(msg.getEvenement().isRazCompteurTemps()){
				setDeltaTempsTempo(getDeltaTempsTempo() + getTemps()
						- getTempsLastMsg() + getCoefTemps()* ConstantesParcoursATESS.maxCptTps
						- getCompteurTempsLastMsg());
			
			}else{
				if (msg.getVariable(getCodeD()) != null) {
//				setDeltaTempsTempo(getDeltaTempsTempo() + getTemps()
//						- getTempsLastMsg() + getCompteurTemps()
//						- getCompteurTempsLastMsg());
				}else  
				// on calcul le delta temps
				// deltaTempsCumul + temps message courant -
				// temps message précédent
				setDeltaTempsTempo(getDeltaTempsTempo() + getTemps()
						- getTempsLastMsg() + getCompteurTemps()
						- getCompteurTempsLastMsg());
			}
			// on met en mémoire les valeurs des variables
			// temps
			setTamponDate(getDate());
		}
	}
	
	public void evIsNotChangmtHeure(Message msg){
		try {
			
			if (getLastMsg().getVariable(getCodeT()) != null) {
				setCompteurTempsLastMsg((Long) getLastMsg().getVariable(getCodeT()).getCastedValeur()* getCoefTemps());
				fillTamponCptTempsLastMsg();
			}else if(getLastMsg().getVariable(getCodeTAC())!= null){
				setCompteurTempsLastMsg(0);
			}else
				setCompteurTempsLastMsg(getTamponCptTempsLastMsg());
			//pour les fichiers hsbc, la variable temps n'est pas valorisée mais  la variable tempsAvantChangement est valorisée et on doit l'utiliser en guise de compteur temps
			if (msg.getVariable(getCodeTAC()) != null && msg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()==15) {
				setCompteurTemps(Long.valueOf(msg.getVariable(getCodeTAC()).toString())* getCoefTemps());
				fillTamponCptTemps();
			} else {
				//pour les fichiers uk, il n'y a pas de tempsAvantChangement, mais la variable temps est elle valorisé
				if (msg.getVariable(getCodeT()) != null) {
					fillCompteurTemps(msg);
					fillTamponCptTemps();
				} else {
					fillCompteurTempsWithTampon();
				}
			}		
			setDate(getTamponDate());
	
			long compensationFirstValueCpt=0;
			if (msg.getVariable(getCodeT())!=null && !firstTimeCountValue) {
				compensationFirstValueCpt=(Long) msg.getVariable(getCodeT()).getCastedValeur()*getCoefTemps();
			}
			
			long ecart=0;
			if (msg.getVariable(getCodeD())!=null) {
				long nbH=getTemps(msg);
				ecart=nbH-getTamponTemps();
				setTamponTemps(nbH);
			}
			if (msg.getEvenement().isRazCompteurTemps()) {
				ecart=3600000-getCompteurTempsLastMsg();
				setCompteurTemps(0);
				setTamponCptTemps(0);
				setTamponCptTempsLastMsg(0);
				setDate((long)getDate(msg));
				setCompteurTempsLastMsg(0);
			}
			setDeltaTempsTempo(getDeltaTempsTempo()+getCompteurTemps()-getCompteurTempsLastMsg()+ecart-compensationFirstValueCpt);
			
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}
	
	public boolean msgIDApresEndID(AVariableComposant relVar,Message msg){
		if (lastEndId > getEndId()) {
			relVar = GestionnairePool.getInstance().getVariable(getCodeTR());
			if (relVar != null) {
				relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
				// add variable to the message
				//msg.ajouterVariable(relVar);
				msg.modifierVariable(relVar);
			}
			relVar = GestionnairePool.getInstance().getVariable(getCodeDR());
			if (relVar != null) {
				relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
				// add variable to the message
				//msg.ajouterVariable(relVar);
				msg.modifierVariable(relVar);
			}
			return true;
		}
		return false;
	}
	
	public void msgIDAvantStartID(AVariableComposant relVar,Message msg){
		if (lastStartId < getStartId()) {
			relVar = GestionnairePool.getInstance().getVariable(getCodeTR());
			if (relVar != null) {
				relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
				// add variable to the message
				//msg.ajouterVariable(relVar);
				msg.modifierVariable(relVar);
			}
			relVar = GestionnairePool.getInstance().getVariable(getCodeDR());
			if (relVar != null) {
				relVar.setValeur(VAR_SANS_VALEUR);//tagValCor
				// add variable to the message
				//msg.ajouterVariable(relVar);
				msg.modifierVariable(relVar);
			}
		}
	}

	public void lastMsgIsDate(){
		if(getLastMsg().getEvenement().isRuptureAcquisition()){
			setDateLastMsg((long)getDate(getLastMsg()));
//			on récupère le nombre de milliseconde
			// depuis le début de la journée de la
			// varaible dateheure
			setTempsLastMsg(getTemps(getLastMsg()));
		}
		else {
			if (getLastMsg().getVariable(getCodeT()) != null) {
				setCompteurTempsLastMsg((Long) getLastMsg().getVariable(getCodeT()).getCastedValeur()* getCoefTemps());
				fillTamponCptTempsLastMsg();
			}else{
				if(getLastMsg().getVariable(getCodeTAC()) != null){
					setTamponCptTempsLastMsg(0);
				}else
					setCompteurTempsLastMsg(getTamponCptTempsLastMsg());
			}
			setDateLastMsg((long)getDate(lastMsg));
		}

		if (getLastMsg().getVariable(getCodeT()) != null) {
			setCompteurTempsLastMsg((Long) getLastMsg().getVariable(getCodeT()).getCastedValeur()* getCoefTemps());
			fillTamponCptTempsLastMsg();
		} else {
			setCompteurTempsLastMsg(getTamponCptTempsLastMsg());
		}
	}
	
	public void ajouterVariable(ListMessages listeMessages,AVariableComposant relVar,Message m,double pointRef){
		String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
		String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
		String minute = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
		String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
		String milliseconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");
		
		for (Message msg : listeMessages) {
			
			if (msg.getMessageId() < getStartId()) {
				if (lastStartId < getStartId()) {
					relVar = GestionnairePool.getInstance().getVariable(getCodeTR());
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);
						//msg.ajouterVariable(relVar);
						msg.modifierVariable(relVar);
					}
					relVar = GestionnairePool.getInstance().getVariable(getCodeDR());
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);
						//msg.ajouterVariable(relVar);
						msg.modifierVariable(relVar);
					}
				}
				continue;
			}
			// the message is after the segment. not interesting
			if (msg.getMessageId() > getEndId()) {
				if (lastEndId > getEndId()) {
					relVar = GestionnairePool.getInstance().getVariable(getCodeTR());
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);
						//msg.ajouterVariable(relVar);
						msg.modifierVariable(relVar);
					}
					relVar = GestionnairePool.getInstance().getVariable(getCodeDR());
					if (relVar != null) {
						relVar.setValeur(VAR_SANS_VALEUR);
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
			relVar = GestionnairePool.getInstance().getVariable(getCodeTR());
			if (relVar != null) {

				String str = null;
				if (msg == m) {
					setTmpRelatif(0);
					str = ConversionTemps.getRelativeTimeAsString(0,jour,heure,minute,seconde,milliseconde);
				} else {
					if (!msg.getEvenement().isChangementHeure()) {
						fillTmpRelatif(msg);
					} else {
						long tempsAvtChgt = getTempsAvantChangement(msg);
						long dateAvtChgt = (long)getDateAvantChangement(msg);
						ConversionTemps tc = new ConversionTempsAtess("01/01/1970 00:00:00.000");
						tc.addDate(dateAvtChgt);
						tc.addTime(tempsAvtChgt+getCompteurTempsAvantChangement(msg));
						fillTmpRelatif(msg);
					}
				}

				str = ConversionTemps.getRelativeTimeAsString(getTmpRelatif(),jour,heure,minute,seconde,milliseconde);
				if (getTmpRelatif() == 0	&& msg.getMessageId() < m.getMessageId())
					str = str.replace('+', '-');
				if (getTmpRelatif() == 0	&& msg.getMessageId() == m.getMessageId())
					str = str.substring(1);
				str = str.replaceAll(",", ".");

				relVar.setValeur(str.getBytes());
				// add variable to the message

				//msg.ajouterVariable(relVar);
				msg.modifierVariable(relVar);
				
			} else {
				throw new RuntimeException(Messages.getString("ReferenceAction.3"));
			}
			relVar = GestionnairePool.getInstance().getVariable(getCodeDR());
			if (relVar != null) {
				double dif = msg.getAbsoluteDistance() - getRefDist() + pointRef;
				BigDecimal b = new BigDecimal(dif);
				b.setScale(6, RoundingMode.DOWN);
				DecimalFormat fmt = new DecimalFormat("0.000");
				String s = fmt.format(b.floatValue());
				String str = (dif >= 0 ? "+" : "") + s;
				str = str.replaceAll(",", ".");
				relVar.setValeur(str.trim().getBytes());
				//msg.ajouterVariable(relVar);
				msg.modifierVariable(relVar);
			} else {
				throw new RuntimeException(Messages.getString("ReferenceAction.4"));
			}
			setLastMsg(msg);
		}
	}

	public HashMap<String,Object> calculerDistanceTempsCumules(ListMessages listeMessages,AVariableComposant relVar,Message m, double pointRef){
		
		long tempsCumule = 0;
		double distanceCumulee=0;
		for (Message msg : listeMessages) {
			
			if (msg.getMessageId() < getStartId()) {
				if (lastStartId < getStartId()) {
				
				}
				continue;
			}
			// the message is after the segment. not interesting
			if (msg.getMessageId() > getEndId()) {
				if (lastEndId > getEndId()) {
					continue;
				}
				break;
			}

			// this will return a copy of the variable temp and we change
			// the
			// descriptor for it
			relVar = GestionnairePool.getInstance().getVariable(getCodeTR());
			if (relVar != null) {
				if (msg == m) {
					setTmpRelatif(0);
					tempsCumule = this.tmpRelatif;
					
				} else {
					if (!msg.getEvenement().isChangementHeure()) {
						setTmpRelatif(((Long) deltaTempsMsgs.get(Integer.valueOf(msg.getMessageId())))- getDeltaTempsRef0());
						tempsCumule = this.tmpRelatif;
					} else {
						long tempsAvtChgt = getTempsAvantChangement(msg);
						long dateAvtChgt = (long)getDateAvantChangement(msg);
						ConversionTemps tc = new ConversionTempsAtess("01/01/1970 00:00:00.000");
						tc.addDate(dateAvtChgt);
						tc.addTime(tempsAvtChgt+getCompteurTempsAvantChangement(msg));
						setTmpRelatif(((Long) deltaTempsMsgs.get(Integer.valueOf(msg.getMessageId())))- getDeltaTempsRef0());
						tempsCumule = this.tmpRelatif;
					}
				}

				
			} else {
				throw new RuntimeException(Messages.getString("ReferenceAction.3"));
			}
			relVar = GestionnairePool.getInstance().getVariable(getCodeDR());
			if (relVar != null) {
				double dif = msg.getAbsoluteDistance() - getRefDist() + pointRef;
				BigDecimal b = new BigDecimal(dif);
				b.setScale(6, RoundingMode.DOWN);
				distanceCumulee = b.floatValue();
			} else {
				throw new RuntimeException(Messages.getString("ReferenceAction.4"));
			}
			setLastMsg(msg);
		}
		
		HashMap<String,Object> mapTempsDistance= new HashMap<String, Object>();
		mapTempsDistance.put("tempsCumule",tempsCumule );
		mapTempsDistance.put("distanceCumule",distanceCumulee );
		return mapTempsDistance;
	}
	
	
	
	
	public long getCompteurTemps() {
		return compteurTemps;
	}

	public void fillCompteurTemps(Message msg) {
		setCompteurTemps((Long) msg.getVariable(getCodeT()).getCastedValeur()*getCoefTemps());
	}

	public void fillCompteurTempsWithTampon() {
		setCompteurTemps(getTamponCptTemps());
	}

	public void setCompteurTemps(long compteurTemps) {
		this.compteurTemps = compteurTemps;
	}

	public long getCompteurTempsLastMsg() {
		return compteurTempsLastMsg;
	}

	public void setCompteurTempsLastMsg(long compteurTempsLastMsg) {
		this.compteurTempsLastMsg = compteurTempsLastMsg;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public long getDateLastMsg() {
		return dateLastMsg;
	}

	public void setDateLastMsg(long dateLastMsg) {
		this.dateLastMsg = dateLastMsg;
	}

	public void setDeltaTempsMsgs(Hashtable<Integer, Long> deltaTempsMsgs) {
		this.deltaTempsMsgs = deltaTempsMsgs;
	}

	public long getDeltaTempsRef0() {
		return deltaTempsRef0;
	}

	public void setDeltaTempsRef0(long deltaTempsRef0) {
		this.deltaTempsRef0 = deltaTempsRef0;
	}

	public long getDeltaTempsTempo() {
		return deltaTempsTempo;
	}

	public void setDeltaTempsTempo(long deltaTempsTempo) {
		this.deltaTempsTempo = deltaTempsTempo;
	}

	public int getEndId() {
		return endId;
	}

	public void setEndId(int endId) {
		this.endId = endId;
	}

	public Message getLastMsg() {
		return lastMsg;
	}

	public void setLastMsg(Message lastMsg) {
		this.lastMsg = lastMsg;
	}

	public double getRefDist() {
		return refDist;
	}

	public void setRefDist(double refDist) {
		this.refDist = refDist;
	}

	public int getStartId() {
		return startId;
	}

	public void setStartId(int startId) {
		this.startId = startId;
	}

	public long getTamponCptTemps() {
		return tamponCptTemps;
	}

	public void fillTamponCptTemps() {
		setTamponCptTemps(getCompteurTemps());
	}

	public void setTamponCptTemps(long tamponCptTemps) {
		this.tamponCptTemps = tamponCptTemps;
	}

	public long getTamponCptTempsLastMsg() {
		return tamponCptTempsLastMsg;
	}

	public void fillTamponCptTempsLastMsg() {
		setTamponCptTempsLastMsg(getCompteurTempsLastMsg());
	}

	public void setTamponCptTempsLastMsg(long tamponCptTempsLastMsg) {
		this.tamponCptTempsLastMsg = tamponCptTempsLastMsg;
	}

	public long getTamponDate() {
		return tamponDate;
	}

	public void setTamponDate(long tamponDate) {
		this.tamponDate = tamponDate;
	}

	public long getTampondeltaTempsTempo() {
		return tampondeltaTempsTempo;
	}

	public void setTampondeltaTempsTempo(long tampondeltaTempsTempo) {
		this.tampondeltaTempsTempo = tampondeltaTempsTempo;
	}

	public long getTemps() {
		return temps;
	}

	public void setTemps(long temps) {
		this.temps = temps;
	}

	public long getTempsLastMsg() {
		return tempsLastMsg;
	}

	public void setTempsLastMsg(long tempsLastMsg) {
		this.tempsLastMsg = tempsLastMsg;
	}

	public long getTmpRelatif() {
		return tmpRelatif;
	}

	public void setTmpRelatif(long tmpRelatif) {
		this.tmpRelatif = tmpRelatif;
	}
	
	public void fillTmpRelatif(Message msg) {
		setTmpRelatif(((Long) deltaTempsMsgs.get(Integer.valueOf(msg.getMessageId())))- getDeltaTempsRef0());
	}

	public long getTemps(Message msg){
		return ConversionTemps.getTempsFromTypeRepereDate(msg,TypeRepere.date);
		//		try {
//			String str=msg.getVariable(TypeRepere.date.getCode()).toString();
//			long res=ConversionTempsAtess.getNbMillisDepuisDebutJourneeATESS(str);
//			return res;
//		} catch (ParseException e) {
//			e.printStackTrace();
//			return 0;
//		}
	}

	public long getCompteurTemps(Message msg){
		return ((Long)msg.getVariable(getCodeT()).getCastedValeur());
	}
	
	public long getCompteurTempsAvantChangement(Message msg){
		return ((Long)msg.getVariable(getCodeTAC()).getCastedValeur());
	}
	
	public long getTempsAvantChangement(Message msg){
		return ConversionTemps.getTempsFromTypeRepereDate(msg,TypeRepere.dateAvantChangement);
	}

	public double getDate(Message msg){
		return ConversionTemps.getDateFromTypeRepereDate(msg,TypeRepere.date);
	}

	public double getDateAvantChangement(Message msg){
		return ConversionTemps.getDateFromTypeRepereDate(msg,TypeRepere.dateAvantChangement);
	}

	public int getCodeTR(){
		return TypeRepere.tempsRelatif.getCode();
	}
	public int getCodeDR(){
		return TypeRepere.distanceRelatif.getCode();
	}
	public int getCodeT(){
		return TypeRepere.temps.getCode();
	}
	public int getCodeTAC(){
		return TypeRepere.tempsAvantChangement.getCode();
	}	
	public int getCodeD(){
		return TypeRepere.date.getCode();
	}

	public long getCoefTemps(){
		return ((long)ConstantesParcoursATESS.pasCptTps * 
				(long) (ConstantesParcoursATESS.resolutionTemps * 1000));
	}

	public long getTamponTemps() {
		return tamponTemps;
	}

	public void setTamponTemps(long tamponTemps) {
		this.tamponTemps = tamponTemps;
	}
	public void resetDeltaTempsTempo(){
		setDeltaTempsTempo(0);
	}
}