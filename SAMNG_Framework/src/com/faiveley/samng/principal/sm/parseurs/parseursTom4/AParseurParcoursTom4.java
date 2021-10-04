package com.faiveley.samng.principal.sm.parseurs.parseursTom4;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.codeFF;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleBlocData;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.controles.CRC16;
import com.faiveley.samng.principal.sm.controles.IStrategieControle;
import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurCodeBloc;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurEvenement;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Poids;
import com.faiveley.samng.principal.sm.data.descripteur.Temporelle;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.ADonneeBlocComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.tom4.GestionnaireSynchronisationGroupes;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurEvenement;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableEvtVar;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BadArgumentInFileException;
import com.faiveley.samng.principal.sm.erreurs.BadEventCodeException;
import com.faiveley.samng.principal.sm.erreurs.BadFileLengthException;
import com.faiveley.samng.principal.sm.erreurs.BadHeaderInfoException;
import com.faiveley.samng.principal.sm.erreurs.BadStructureFileException;
import com.faiveley.samng.principal.sm.erreurs.ParseurBinaireException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.AParseurParcours;
import com.faiveley.samng.principal.sm.parseurs.Messages;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:27
 */
public abstract class AParseurParcoursTom4 extends AParseurParcours {

	public AParseurParcoursTom4() {

	}

	/**
	 * 
	 * Load the codes
	 * 
	 * CL_5 : L’octet 5+x+3 = > le code Debut de la table Data ; CL_6 : L’octet
	 * 5+x+4 = > le code Fin de la table Data CL_7 : L’octet 5+x+5 = > le code
	 * Continue Fin Bloc de la table Data. CL_8 : L’octet 5+x+6 = > le code
	 * Continue Debut Bloc de la table Data. CL_D_2 : L’octet 5+x+7 = > le code
	 * Début Bloc Defaut de la table Data. CL_D_3 : L’octet 5+x+8 = > le code
	 * Fin Bloc Defaut de la table Data. CL_D_4 : L’octet 5+x+9 = > le code
	 * Continue Fin Bloc Defaut de la table Data. CL_D_5 : L’octet 5+x+10 = > le
	 * code Continue Debut Bloc Defaut de la table Data.
	 * 
	 * 
	 * @param codeName
	 *            the code name
	 * @param from
	 *            starting position in the byte[]
	 * @return the descriptor created
	 */
	protected ADescripteurComposant chargerDescripteursCodeBloc()
			throws BadHeaderInfoException {

		// TODO: remplir les descripteurs avec les conditions de passage à
		// message suivant
		DescripteurCodeBloc descr = new DescripteurCodeBloc();

		return descr;
	}


	public void clear() {
		// TODO Auto-generated method stub
		derniereValeurHeure = 0;
		derniereValeurDate = 0;
		derniereValeurCptTemps = 0;
		cumulTempsMax = 0;
		cumulTemps = 0;
		lastCptTempsTimeChange = 0;
		nomFichierBinaire = "";
		premiereDateRencontree = false;
		nbBloc = 0;
	}


	/**
	 * charge l'ensemble des messages et les place dans un objet Data
	 * 
	 * Returns 2 arrays with messages: first with good messages, second with bad
	 * massages
	 */
	protected Message[][] chargerData(int deb,int fin) throws ParseurBinaireException {
		crcEnregistrements = new HashMap<Integer, Boolean>();

		boolean msgsToLoad=true;//utilisé pour le chargement partiel. 
		//Si à false, on attend d'être arrivé à l'offset de début pour commencer à charger les messages

		if (deb!=0 ||(fin!=-1 && fin !=0)) {
			msgsToLoad=false;
		}

		List<Message> goodMsgs = new ArrayList<Message>();
		List<Message> badMsgs = new ArrayList<Message>();

		TableEvtVar table = (TableEvtVar) FabriqueParcours.getInstance().getParcours().getTableAssos();
		descrTable = (DescripteurComposite) table.getDescripteur(0);
		lastMessage = null;

		// on défini la position de lecture de l'octet à l'octet de début du
		// premier enregistrement
		int pos = ConstantesParcoursTom4.tailleConfiguration;
		//		if (deb!=0) {
		//			pos=deb;
		//		}

		int tailleTableauMessage = this.message.length;

		if (fin!=-1 && fin!=0) {
			tailleTableauMessage=fin;
		}

		// lorsque l'on atteint la fin du fichier, peu importe ce qui a été lu
		// on doit s'arreter
		if (pos >= tailleTableauMessage){
			SamngLogger.getLogger().error(Messages.getString("errors.blocking.invalidFileStructure1"));
			ActivatorData.getInstance().getPoolDonneesVues().put("fichierVide",new String("true"));
			throw new BadFileLengthException(Messages
					.getString("errors.blocking.invalidFileStructure1")); //$NON-NLS-1$
		}

		// booléen arretatn la lecture du fichier
		boolean finLecture = false;

		// la longueur de chaque enregistrement doit etre lu dans un champs de
		// l'entete:ConstantesParcoursBinaire.tailleBlocDataFp
		// compteur permettant de s'arreter lorsque l'on a atteint la fin de
		// l'enregistrement
		// le compteur va de 1 à ConstantesParcoursBinaire.tailleBlocData-2
		int cpt = 1;
		int cptBlocs = 1;
		int posDebutEnreg = pos;
		// System.out.println("Enregistrement n°" + cptEnregistrement
		// + ", octet de debut: " + pos);

		// calcul CRC premier enregistrement
		int crc = 0;
		byte[] crcEnreg = new byte[2];
		boolean crcEnregValide = false;
		// on récupère la valeur du crc
		crcEnreg = Arrays.copyOfRange(this.message, posDebutEnreg + tailleBlocData - 2, posDebutEnreg + tailleBlocData);
		crc = new BigInteger(crcEnreg).intValue();

		crcEnregValide = verifierCRCBloc(crc, Arrays.copyOfRange(this.message, posDebutEnreg, posDebutEnreg + tailleBlocData - 2));

		if (!crcEnregValide && (deb!=0 ||(fin!=-1 && fin !=0))) {
			crcEnregValide=true;
		}
		//		if (msgsToLoad) {
		crcEnregistrements.put(cptBlocs, crcEnregValide);
		// System.out.println("CRC validation:" + crcEnregValide);
		//		}

		//		VueProgressBar.getInstance().fin = this.message.length - pos;
		while (!finLecture && !Thread.interrupted()){ 
			//&& !VueProgressBar.getInstance().isEscaped) {
			//			VueProgressBar.getInstance().setCourant(pos);
			ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

			// si on arrive au niveau du crc de l'enregistrement on doit aller à
			// l'enregistrement suivant
			if (cpt > tailleBlocData - 2 || !crcEnregValide ) {
				if((tailleBlocData - cpt + 1 + pos)>=tailleTableauMessage -1)
					finLecture = true;
				else{
					cptBlocs++;
					posDebutEnreg += tailleBlocData;
					pos += (tailleBlocData - cpt + 1);
					cpt = 1;

					// à chaque nouvel enregistrement on calcul le CRC
					// on récupère la valeur du crc
					crcEnreg = Arrays.copyOfRange(this.message, posDebutEnreg + tailleBlocData - 2, posDebutEnreg + tailleBlocData);
					crc = new BigInteger(crcEnreg).intValue();

					crcEnregValide = verifierCRCBloc(crc, Arrays.copyOfRange(this.message, posDebutEnreg, posDebutEnreg
							+ tailleBlocData - 2));										

					// System.out.println("CRC validation:" + crcEnregValide);
					//					if (msgsToLoad) {
					crcEnregistrements.put(cptBlocs, crcEnregValide);
					//					}
				}
			}

			while (cpt <= tailleBlocData - 2 && !finLecture && crcEnregValide) {
				ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
				ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

				if (pos>=deb) {
					msgsToLoad=true;
				}
				if (pos>=fin && (fin!=-1 && fin!=0)) {
					msgsToLoad=false;
				}

				// if the blocks structure is ok load the message
				Message msg = new Message();

				// on ne commence à lire un message que si l'octet a une
				// valeur
				// différente de -1

				if (this.message[pos] != -1) {
					// chargement du message
					try {
						msg = chargerMessage(pos, false);
						msg.setOffsetDebut(pos);
						msg.setOffsetFin(pos+msg.getLongueur());
						this.tc.getFormatedTime();
						// changement de la position de lecture
						pos += msg.getLongueur();
						// incrémentation du compteur
						cpt += msg.getLongueur();
					} catch (ParseurBinaireException ex) {
						// System.out
						// .println("evt non trouvé dans table evt/var: id =
						// "
						// + this.message[pos]); 
						//						if (deb!=0) {
						//							pos++;
						//							cpt++;
						//						}else{//cas normal
						pos += tailleBlocData - cpt + 1;
						cpt = tailleBlocData;							
						//						}												
					}
				} else {
					// lorsque l'on rencontre un octet égal à -1
					// on incrémente la position et le compteur de 1

					//					if (deb!=0) {//gestion ouverture partielle
					//						int posix=pos;
					//						do{
					//							posix++;
					//						}while (this.message[posix]==-1);
					//						//on passe sur le CRC
					//						pos=posix+2;
					//						cpt=0;
					//					}else{//cas normal
					pos++;
					cpt++;
					//					}					
				}

				if (msg.getEvenement() != null && msgsToLoad) {

					goodMsgs.add(msg);
					long cptTempsMsgReference = 0;
					long tempsAbsoluMsgReference = 0;
					long cumulCptTemps1 = 0;
					Message lastMsgCalculTemps = null;
					long cptTempsMsgCourant = 0;
					long cumulRazCptTemps = 0;

					// on doit calculer le temps absolu des messages
					// sans temps absolu(aucune variable date heure
					// connu depuis le début pour ces messages)
					if (lastMessage != null && msg.getAbsoluteTime() > 0
							&& lastMessage.getAbsoluteTime() == 0) {
						msg.setEvNonDate(true);
						if (msg.getVariable(TypeRepere.temps.getCode()) != null)
							// on doit prendre la valeur du compteur
							// temps du message de référence pour faire
							// la différence de temps
							// cptTpsReference -
							// cptTpsMessageSansTempsAbsolu
							cptTempsMsgReference = (Long) msg.getVariable(
									TypeRepere.temps.getCode())
									.getCastedValeur()
									* ConstantesParcoursTom4.pasCptTps
									* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
						// on récupère le temps absolu du message de
						// reference
						tempsAbsoluMsgReference = msg.getAbsoluteTime();

						int i = goodMsgs.size() - 2;
						while (i >= 0
								&& goodMsgs.get(i).getAbsoluteTime() == 0) {

							if(goodMsgs.get(i).getEvenement().isRazCompteurTemps())	{
								long derniereValeurCompteurTempsAvtRaz = 0;
								int k = i;
								while(goodMsgs.get(k).getVariable(TypeRepere.temps.getCode()) == null && k>0){
									k--;
								}
								cptTempsMsgCourant = ConstantesParcoursTom4.pasCptTps
										* ConstantesParcoursTom4.maxCptTps
										* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

								cumulRazCptTemps += ConstantesParcoursTom4.pasCptTps
										* ConstantesParcoursTom4.maxCptTps
										* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

								derniereValeurCompteurTempsAvtRaz = (Long)goodMsgs.get(k).getVariable(TypeRepere.temps.getCode()).getCastedValeur()
										* ConstantesParcoursTom4.pasCptTps * (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

								cumulCptTemps1 = cptTempsMsgReference - (cptTempsMsgCourant- derniereValeurCompteurTempsAvtRaz);

								goodMsgs.get(i).setAbsoluteTime(tempsAbsoluMsgReference	- cumulCptTemps1);
							}
							else{
								if (goodMsgs.get(i).getVariable(
										TypeRepere.temps.getCode()) != null) {

									// on récupère la valeur du compteur
									// temps du message sans temps absolu
									// pour faire la différence de temps:
									// cptTpsReference -
									// cptTpsMessageSansTempsAbsolu
									cptTempsMsgCourant = ((Long) goodMsgs
											.get(i).getVariable(
													TypeRepere.temps.getCode())
													.getCastedValeur()
													* ConstantesParcoursTom4.pasCptTps * (long) (ConstantesParcoursTom4.resolutionTemps * 1000));
									// on conserve la différence de compteur
									// temps pour la reporter sur le message
									// précédent
									cumulCptTemps1 = cptTempsMsgReference - cptTempsMsgCourant + cumulRazCptTemps;

									goodMsgs.get(i).setAbsoluteTime(tempsAbsoluMsgReference - cumulCptTemps1);
								}
							}
							// on conserve le message pour pouvoir
							// tester ensuite si c'est un message de
							// razCompteurTemps
							lastMsgCalculTemps = goodMsgs.get(i);
							// on décrémente le compteur pour aller au
							// message précédent
							i--;
						}
					}
					lastMessage = msg;
				} 
			}
		}
		lastMessage = null;
		lastSegTemp = null;
		lastSegDistance = null;

		for (int j = 0; j < goodMsgs.size(); j++) {
			try {
				Evenement ev = goodMsgs.get(j).getEvenement();
				//gestion de la synchronisation temps distance des événements
				//su l'événement n'est pas a synchroniser
				if(!ev.isASychroniser()&&!ev.isReferenceSynchro()){
					//on synchronise les événements si une synchronisation était en cours
					if(GestionnaireSynchronisationGroupes.getInstance().isSynchroEnCours()){
						GestionnaireSynchronisationGroupes.getInstance().synchronisationMessages();
					}
				}
				else{
					GestionnaireSynchronisationGroupes.getInstance().setSynchroEnCours(true);
					if(ev.isASychroniser()){
						GestionnaireSynchronisationGroupes.getInstance().ajouterMessageASynchroniser(goodMsgs.get(j));
					}
					else{
						GestionnaireSynchronisationGroupes.getInstance().setMsgReferenceSynchro(goodMsgs.get(j));
					}
				}
				setFlags(goodMsgs.get(j));
			} catch (Exception e) {
				SamngLogger.getLogger().error(this, e);
			}
			lastMessage = goodMsgs.get(j);
		}
		if(lastSegDistance!=null){
			enregistrerSegmentDistance(lastMessage);
		}
		if (lastSegTemp != null) {
			enregistrerSegmentTemps(lastMessage);
		}
		//		if (VueProgressBar.getInstance().isEscaped) {
		//			return null;
		//		}
		// creates the array with bad and good messages
		Message[][] msgs = new Message[2][];
		msgs[0] = goodMsgs.toArray(new Message[goodMsgs.size()]);
		msgs[1] = badMsgs.toArray(new Message[badMsgs.size()]);
		return msgs;
	}
	
	protected void chargerDataExplore() throws ParseurBinaireException 
	{
		// Tampon des messages représentants un boud de segment de temps, 
		// mais qui n'ont pas encore d'absolute time		
		List<Message> undatedMsg = new ArrayList<Message>();		
		
		lastMessage = null; // Dernier message traité

		// on défini la position de lecture de l'octet à l'octet de début du
		// premier enregistrement
		int pos = ConstantesParcoursTom4.tailleConfiguration;
		int cpt = 1; // Compteur d'octets traité dans un enregistrement : varie de 1 à 31
		
		// tailleTableauMessage contient le nombre d'octets du fichier à explorer
		int tailleTableauMessage = this.message.length;
		
		// Table de décodage des messages : utilisée par chargerMessage()
		TableEvtVar table = (TableEvtVar) FabriqueParcours.getInstance().getParcours().getTableAssos();		
		descrTable = (DescripteurComposite) table.getDescripteur(0);

		// Flag de fin de lecture
		boolean finLecture = false;
		
  // Traitement des CRC des messages : si invalide, le message est ignoré
/***************************************/		
		int posDebutEnreg = pos;
		// calcul CRC premier enregistrement
		int crc = 0;
		byte[] crcEnreg = new byte[2];
		boolean crcEnregValide = false;
		// on récupère la valeur du crc
		crcEnreg = Arrays.copyOfRange(this.message, posDebutEnreg + tailleBlocData - 2, posDebutEnreg + tailleBlocData);
		crc = new BigInteger(crcEnreg).intValue();

		crcEnregValide = verifierCRCBloc(crc, Arrays.copyOfRange(this.message, posDebutEnreg, posDebutEnreg + tailleBlocData - 2));

		if (!crcEnregValide) {
			crcEnregValide=true;
		}
/***************************************/		

		// la longueur de chaque enregistrement doit etre lu dans un champs de
		// l'entete:ConstantesParcoursBinaire.tailleBlocDataFp
		// compteur permettant de s'arreter lorsque l'on a atteint la fin de
		// l'enregistrement
		// le compteur va de 1 à ConstantesParcoursBinaire.tailleBlocData-2		
		//while (!finLecture && !Thread.interrupted())
		while (!finLecture && !monitor.isCanceled())
		{ 
			ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
			ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

			// si on arrive au niveau du crc de l'enregistrement on doit aller à
			// l'enregistrement suivant
			if (cpt > tailleBlocData - 2 || !crcEnregValide )
			{
				if((tailleBlocData - cpt + 1 + pos) >= tailleTableauMessage -1)
				{
					finLecture = true;
				}
				else
				{
					pos += (tailleBlocData - cpt + 1);
					cpt = 1;
					
					
					posDebutEnreg += tailleBlocData;

					// à chaque nouvel enregistrement on calcul le CRC
					// on récupère la valeur du crc
					crcEnreg = Arrays.copyOfRange(this.message, posDebutEnreg + tailleBlocData - 2, posDebutEnreg + tailleBlocData);
					crc = new BigInteger(crcEnreg).intValue();

					crcEnregValide = verifierCRCBloc(crc, Arrays.copyOfRange(this.message, posDebutEnreg, posDebutEnreg
							+ tailleBlocData - 2));					
				}
			}
			
			while (cpt <= tailleBlocData - 2 && !finLecture  && crcEnregValide) 
			{
				ActivatorData.getInstance().getVp().setValeurProgressBar(pos*100/(tailleTableauMessage));
				ActivatorData.getInstance().getVpExportExplorer().setValeurProgressBar(pos,tailleTableauMessage);

				// if the blocks structure is ok load the message
				Message msg = new Message();

				// on ne commence à lire un message que si l'octet a une
				// valeur
				// différente de -1

				if (this.message[pos] != -1) 
				{
					// chargement du message
					try 
					{
						msg = chargerMessage(pos, true);
						msg.setOffsetDebut(pos);
						msg.setOffsetFin(pos+msg.getLongueur());
						
						this.tc.getFormatedTime();
						
						// changement de la position de lecture
						pos += msg.getLongueur();
						// incrémentation du compteur
						cpt += msg.getLongueur();
					} 
					catch (ParseurBinaireException ex) 
					{
						// System.out
						// .println("evt non trouvé dans table evt/var: id =
						// "
						// + this.message[pos]); 
						pos += tailleBlocData - cpt + 1;
						cpt = tailleBlocData;																		
					}
				} 
				else 
				{
					// lorsque l'on rencontre un octet égal à -1
					// on incrémente la position et le compteur de 1
					pos++;
					cpt++;					
				}

				if (msg.getEvenement() != null) 
				{
					// Evènement datant = premier évènement possédant un absolute time
					long cptTempsMsgReference = 0;
					long tempsAbsoluMsgReference = 0;
					long cumulCptTemps1 = 0;
					long cptTempsMsgCourant = 0;
					long cumulRazCptTemps = 0;
					
					
					if (undatedMsg.isEmpty())
						undatedMsg.add(lastMessage);
						
					undatedMsg.add(msg);

					// Si les messages précédents n'avaient pas d'AbsoluteTime et que celui que l'on vient de récupérer en a un...
					// segBounds.get(1) : on teste le "1", car dans le cas particulier où le msg est le premier du fichier,
					// segBounds.get(1) est null : pas de prédécesseur. Il y aura toujours un segBounds.get(1), car l'ouverture d'un 
					// segment est liée à la fermeture du précédent
					if (msg.getAbsoluteTime() > 0 && (undatedMsg.get(undatedMsg.size() - 2) != null) && (undatedMsg.get(undatedMsg.size() - 2).getAbsoluteTime() == 0))
					{
						msg.setEvNonDate(true);
						
						if (msg.getVariable(TypeRepere.temps.getCode()) != null)
							// on doit prendre la valeur du compteur
							// temps du message de référence pour faire
							// la différence de temps
							// cptTpsReference -
							// cptTpsMessageSansTempsAbsolu
							cptTempsMsgReference = (Long) msg.getVariable(
									TypeRepere.temps.getCode())
									.getCastedValeur()
									* ConstantesParcoursTom4.pasCptTps
									* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
						// on récupère le temps absolu du message de
						// reference
						tempsAbsoluMsgReference = msg.getAbsoluteTime();

						// -1 car indice commence à 0
						int i = undatedMsg.size() - 2;
						
						// Si le dernier msg de segBounds est l'évènement datant, il est déjà daté ! 
						//if (segBounds.get(i) == msg) i--;
						
						// Tant qu'il y a des évènements à dater
						// Dans le cas particulier du début de fichier, segBounds.get(0) = null et i = 0
						while (i >= 0 && undatedMsg.get(i) != null && undatedMsg.get(i).getAbsoluteTime() == 0)
						{
							if(undatedMsg.get(i).getEvenement().isRazCompteurTemps())	
							{
								long derniereValeurCompteurTempsAvtRaz = 0;
								int k = i;
								
								while(undatedMsg.get(k).getVariable(TypeRepere.temps.getCode()) == null && k>0)
								{
									k--;
								}
								
								cptTempsMsgCourant = ConstantesParcoursTom4.pasCptTps
										* ConstantesParcoursTom4.maxCptTps
										* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

								cumulRazCptTemps += ConstantesParcoursTom4.pasCptTps
										* ConstantesParcoursTom4.maxCptTps
										* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

								derniereValeurCompteurTempsAvtRaz = (Long)undatedMsg.get(k).getVariable(TypeRepere.temps.getCode()).getCastedValeur()
										* ConstantesParcoursTom4.pasCptTps * (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

								cumulCptTemps1 = cptTempsMsgReference - (cptTempsMsgCourant- derniereValeurCompteurTempsAvtRaz);

								undatedMsg.get(i).setAbsoluteTime(tempsAbsoluMsgReference - cumulCptTemps1);
							}
							else
							{
								if (undatedMsg.get(i).getVariable(TypeRepere.temps.getCode()) != null) 
								{
									// on récupère la valeur du compteur
									// temps du message sans temps absolu
									// pour faire la différence de temps:
									// cptTpsReference -
									// cptTpsMessageSansTempsAbsolu
									cptTempsMsgCourant = ((Long) undatedMsg
											.get(i).getVariable(
													TypeRepere.temps.getCode())
													.getCastedValeur()
													* ConstantesParcoursTom4.pasCptTps * (long) (ConstantesParcoursTom4.resolutionTemps * 1000));
									// on conserve la différence de compteur
									// temps pour la reporter sur le message
									// précédent
									cumulCptTemps1 = cptTempsMsgReference - cptTempsMsgCourant + cumulRazCptTemps;

									undatedMsg.get(i).setAbsoluteTime(tempsAbsoluMsgReference - cumulCptTemps1);
								}
							}							

							// on décrémente le compteur pour aller au
							// message précédent
							i--;
						}						
					}
					
					if (msg.getAbsoluteTime() != 0)
					{
						for (int idx = 0 ; idx < undatedMsg.size() -1 ; idx++)
						{
							// Fermer le segment précédent et ouvrir un nouveau
							// Dans le cas du premier msg, lastMessage est null, mais il faut l'enregistrer
							// pour rendre l'algo homogene					
							setSegmentsTemp(undatedMsg.get(idx + 1), undatedMsg.get(idx));					
						}
						
						undatedMsg.clear();
					}
					
					lastMessage = msg;
				} 
			}
		}

		// Pour le dernier segment de temps, c'est le dernier message du parcours qui le ferme
		if (lastSegTemp != null) 
			enregistrerSegmentTemps(lastMessage);
	}	

	/**
	 * Checks the CRC of the message
	 * 
	 * @param crc
	 *            the value of CRC
	 * @param message
	 *            the bytes for which the CRC is calculated
	 * @return true if CRC is ok, false otherwise
	 */
	protected boolean verifierCRCBloc(int crc, byte[] donneesBloc) {
		IStrategieControle controlTable = new CRC16();
		boolean valid = true;

		if (!controlTable.controlerCRC(crc, donneesBloc)) {
			valid = false;
		}
		return valid;
	}

	/**
	 * Loads a message
	 * 
	 * @param msg
	 *            an instance of the message
	 * @param start
	 *            the start position for the message
	 * @return the message filled
	 * @throws ParseurBinaireException
	 *             throws an exception if some error occurs
	 */
	protected Message chargerMessage(int start, boolean isExploration) throws ParseurBinaireException {
		double distance = 0;

		double newDateValue = 0;
		long newHeureValue = 0;
		double cptTempsCourant = 0;
		double newDistanceValue = 0;

		double diametreRoue = 0;
		double dateAvantChgt = 0;
		double tempsAvantChgt = 0;

		boolean newDateHeureEncountered = false;
		boolean newCptTpsEncountered = false;
		boolean newDistanceEncoutered = false;
		boolean newChgtEncountered = false;

		Message msg = new Message();

		// set the message ID as the start address of the message block
		msg.setMessageId(start - ConstantesParcoursTom4.tailleConfiguration);

		// get event id
		// l'id est un entier non signé donc on doit remplir un tableau d'octet
		// avec l'octet de poids fort à 0
		byte[] tabIdEvt = new byte[2];
		tabIdEvt[0] = 0;
		tabIdEvt[1] = this.message[start];
		BigInteger bInt = new BigInteger(tabIdEvt);
		int id = bInt.intValue();

		//		USE_HASHMAP
		//	DescripteurComposite descrComp = mapCodeEvtDescComposite.get(id);

		// get all variables descriptors from TableEvtVar
		//		TableEvtVar table = (TableEvtVar) FabriqueParcours.getInstance()
		//		.getParcours().getComposant(INDEXES.INDEX_TABLE_ASSOC);
		//		DescripteurComposite descrTable = (DescripteurComposite) table
		//		.getDescripteur(0);
		DescripteurComposite descrComp = null;
		DescripteurEvenement descrEvent = null;

		// get the event id in the table
		int length = descrTable.getLength();
		for (int i = 0; i < length; i++) {
			descrComp = (DescripteurComposite) descrTable.getEnfant(i);
			descrEvent = (DescripteurEvenement) descrComp.getEnfant(0);

			if (descrEvent != null && descrEvent.getM_AIdentificateurComposant().getCode() == id) {
				break;
			}
			descrComp = null;
		}

		// the code read in the message is not found in the table
		if (descrComp == null) {
			msg.setError(ErrorType.EventId);
			throw new BadEventCodeException(Messages.getString("errors.nonblocking.invalidEventId") //$NON-NLS-1$
					+ "; " + Messages.getString("errors.nonblocking.eventId1")+ id	+ "; "
					// + //$NON-NLS-1$ //$NON-NLS-2$
					// Messages
					// .getString("errors.nonblocking.blockStart1") + " " +
					// (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) /
					// ConstantesParcoursBinaire.tailleBlocData) + 1)
					); //$NON-NLS-1$ //$NON-NLS-2$
		}

		Evenement ev = GestionnairePool.getInstance().getEvent(id);
		// System.out.println("Evénement");
		DescripteurEvenement descEvt = (DescripteurEvenement) ev.getM_ADescripteurComposant();
		// System.out.println("code: " + descEvt.getCode() + " nom: "
		// + descEvt.getNom() + " \n");

		// cas où l'évévement est dans la table evt/var et dans le fichier xml

		if (descEvt.getNom() == null) {
			DescripteurEvenement descrEvt = new DescripteurEvenement();

			IdentificateurEvenement identifEvt = new IdentificateurEvenement();

			identifEvt.setCode(id);
			identifEvt.setNom("Event code: " + id);

			descrEvt.setCode(id);
			descrEvt.setNom("Event code: " + id);
			descrEvt.setCaractTemporelle(Temporelle.COPY_DOWN);

			descrEvt.setM_AIdentificateurComposant(identifEvt);

			ev.setM_ADescripteurComposant(descrEvt);
			ev.setChangementHeure(false);

			TableLangueNomUtilisateur tblLangues = new TableLangueNomUtilisateur();
			tblLangues.setNomUtilisateur(Langage.FR, "Code de l'événement: "+ id);
			tblLangues.setNomUtilisateur(Langage.EN, "Event code: " + id);

			ev.setNomUtilisateur(tblLangues);
			GestionnairePool.getInstance().ajouterEvenement(ev);

			msg.setError(ErrorType.EventId);
			SamngLogger.getLogger().warn(Messages.getString("errors.nonblocking.notXmlFoundEvent") //$NON-NLS-1$
					+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + " " + //$NON-NLS-1$ //$NON-NLS-2$
					Messages.getString("errors.nonblocking.blockStart1") + " " + ((((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData)) + 1)); //$NON-NLS-1$ //$NON-NLS-2$
		} else {
			// check if is set the nom utilisateur
			if (ev.getNomUtilisateur() == null || ev.getNomUtilisateur().size() == 0) {
				msg.setError(ErrorType.XMLRelated);
				throw new BadArgumentInFileException(Messages.getString("errors.nonblocking.invalidXmlUsersList") //$NON-NLS-1$
						+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + "; " + //$NON-NLS-1$ //$NON-NLS-2$
						Messages.getString("errors.nonblocking.blockStart1") + " " + (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1)); //$NON-NLS-1$ //$NON-NLS-2$
			}

			// check if is set the caractereTemporelle
			if (descEvt.getCaractTemporelle() == null) {
				msg.setError(ErrorType.XMLRelated);
				throw new BadArgumentInFileException(
						Messages.getString("errors.nonblocking.invalidXmlEvCaractTemp") //$NON-NLS-1$
						+ "; " + Messages.getString("errors.nonblocking.eventId1") + id + "; " + //$NON-NLS-1$ //$NON-NLS-2$
						Messages.getString("errors.nonblocking.blockStart1") + " " + (((msg.getMessageId() - (tailleEntete + tailleTableEvenement)) / tailleBlocData) + 1)); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

		// set the message event
		msg.setEvenement(ev);

		int size = descrComp.getLength();
		int pos = 1;
		DescripteurVariable descVar = null;
		int codeVar = 0;
		int variablesLength = 0;
		for (int i = 1; i < size; i++) {
			// get the code of the variable from the
			descVar = (DescripteurVariable) descrComp.getEnfant(i);
			codeVar = descVar.getM_AIdentificateurComposant().getCode();

			// load the variable and add it to the message
			AVariableComposant var = GestionnairePool.getInstance().getVariable(codeVar);
			// if (var != null)
			// System.out.println("variable "
			// + (i - 1)
			// + " "
			// + var.getDescriptor().getM_AIdentificateurComposant()
			// .getNom() + " ,lg variable "
			// + var.getDescriptor().getTailleOctets());
			// else
			// System.out.println("variable " + (i - 1) + " " + codeVar
			// + " ,lg variable indéfinie");

			if (var == null) {

				// if variable not found throw an error
				// msg.setError(ErrorType.EventId);
				// throw new BadVariableCodeException(
				// Messages
				// .getString("errors.nonblocking.invalidVariableId")
				// //$NON-NLS-1$
				// + "; " //$NON-NLS-1$
				// + Messages
				// .getString("errors.nonblocking.variableId1") //$NON-NLS-1$
				// + +codeVar
				// + "; " //$NON-NLS-1$
				// + Messages
				// .getString("errors.nonblocking.eventId1") //$NON-NLS-1$
				// + id
				// + Messages
				// .getString("errors.nonblocking.blockStart1") + " "
				// //$NON-NLS-1$ //$NON-NLS-2$
				// + (((msg.getMessageId() - (tailleEntete +
				// tailleTableEvenement)) / ConstantesParcoursBinaire.tailleBlocData) + 1));
			} else {
				// add variable to the current message
				msg.ajouterVariable(var);

				byte[] value = null;

				if (var.getDescriptor().getTailleOctets() == 1) {
					value = new byte[1];
					value[0] = this.message[pos + start];
				} else {
					value = Arrays.copyOfRange(this.message, pos + start, var.getDescriptor().getTailleOctets()	+ pos + start);
				}
				pos += var.getDescriptor().getTailleOctets();
				try {
					value = setVariableValue(var, value);
				} catch (Exception e) {

				}

				if (codeVar == TypeRepere.date.getCode()) {
					try {
						// pour pouvoir utiliser la classe de calcul du temps absolu:ConversionTemps et ses méthodes addDate et addTime, on calcule:
						//- le nombre de jour depuis la date pivot spécifié dans le constructeur de ConversionTemps(paramètre de addate)
						//- le nombre de milliseconde depuis le début de la journée(paramètre de addTime)
						newDateValue = ConversionTemps.getNbJoursDepuisDatePivot("01011990", var.toString().substring(0, 8));
						newDateHeureEncountered = true;
						newHeureValue = ConversionTemps.getNbMillisDepuisDebutJournee(var.toString());
					} catch (ParseException e) {
						e.printStackTrace();
					}
				} else if (codeVar == TypeRepere.tempsAvantChangement.getCode()) {
					tempsAvantChgt = Double.parseDouble(var.toString())
							* ConstantesParcoursTom4.pasCptTps
							* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
					newChgtEncountered = true;
				} else if (codeVar == TypeRepere.temps.getCode()) {
					cptTempsCourant = Double.parseDouble(var.toString())
							* ConstantesParcoursTom4.pasCptTps
							* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
					newCptTpsEncountered = true;
				} else if (codeVar == TypeRepere.distance.getCode()) {
					newDistanceValue = Double.parseDouble(var.toString())
							* ConstantesParcoursTom4.pasCptDistance;
					newDistanceEncoutered = true;

				} else if (codeVar == TypeRepere.diametreRoue.getCode()) {
					diametreRoue = Double.parseDouble(var.toString());
					msg.setDiametreRoue(diametreRoue);
				}

				variablesLength += var.getDescriptor().getTailleOctets();
			}
		}

		//Algorithme de gestion du temps
		if (ev.isRuptureAcquisition()) {

			// on remet tous les compteurs de temps  et les variable globale utilisée à 0
			cumulTempsMax = 0;
			lastCptTempsTimeChange = 0;
			derniereValeurCptTemps = 0;
			derniereValeurHeure = 0;
			derniereValeurDate = 0;
			cumulTemps = 0;
			cumulDistance =0;
			derniereValeurCptDistance =0;
			cumulDistanceMax =0;


			// la variable dateHeure est à présent valorisée
			if (!premiereDateRencontree)
				premiereDateRencontree = true;
			// on défini le temps absolu à l'aide :
			// du nombre de jour depuis la date pivot  + nombre de millisecondes
			this.tc.addDate(newDateValue);
			this.tc.addTime(newHeureValue);

			//on stocke la date et l'heure dans 2 variables
			derniereValeurDate = newDateValue;
			derniereValeurHeure = newHeureValue;

			//msg.resetComteurTemps();

		}
		// si l'événement est un événement de remise à 0(razCompteurtemps) 
		// cas des événements marqués de razCompteurTemps dans le fichier xml
		// on incrémente un compteur de temps pour stocker les différentes remise à zéro: cumulTempsMax
		// le nombre de millisecondes ajouté dépend de plusieurs champs de la
		// configuration du fichier:
		// pasCptTps: facteur multiplicateur du maxCptTps
		// maxCptTps: valeur maximum du compteur de temps en secondes
		// resolutionTemps: résolution du temps(0.1 seconde ou 1 seconde)
		else if (ev.isRazCompteurTemps()) {

			//le temps voulu est en milliseconde donc on multiplie par 1000
			cumulTempsMax += ConstantesParcoursTom4.pasCptTps
					* (long) (ConstantesParcoursTom4.resolutionTemps * 1000)
					* ConstantesParcoursTom4.maxCptTps;

			// la valeur du cumul detemps est égale à :
			//cumul temps +  valeur courante du compteur temps - dernière valeur du compteur temps
			cumulTemps += cptTempsCourant  - derniereValeurCptTemps; 


			this.tc.addTime(derniereValeurHeure + cumulTempsMax + cumulTemps);

			// on remet à zéro la dernière valeur du compteur temps
			derniereValeurCptTemps = cptTempsCourant;

			if(newDistanceEncoutered){
				cumulDistance += newDistanceValue - derniereValeurCptDistance;
				derniereValeurCptDistance = newDistanceValue;
			}
		}
		//cas des événements marqués de razCompteurDistance dans le fichier xml
		else if(ev.isRazCompteurDistance()){

			double coefDir=0;
			try {
				coefDir=GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getCoefDirecteur();
			} catch (Exception e) {
				coefDir=1;
			}

			cumulDistanceMax += ConstantesParcoursTom4.pasCptDistance
					* ConstantesParcoursTom4.maxCptDistance*coefDir;

			cumulDistance =  cumulDistanceMax;

			if(newCptTpsEncountered){
				//la valeur du cumul detemps est égale à :
				//cumul temps +  valeur courante du compteur temps - dernière valeur du compteur temps
				cumulTemps += cptTempsCourant  - derniereValeurCptTemps; 
				this.tc.addTime(derniereValeurHeure + cumulTemps + cumulTempsMax);
				derniereValeurCptTemps = cptTempsCourant;
			}


			derniereValeurCptDistance =0;

		}

		// cas des événements de changement d'heure 
		// on ne doit ici pas prendre en compte le cumul du compteur temps 
		//mais on ajoute la valeur de la variable LatchTemps(tempsAvantChgt) pour les événements suivants
		else if (ev.isChangementHeure()) {
			this.tc.addDate(newDateValue);


			//on remet les cumul de temps  à 0
			cumulTemps = 0;
			cumulTempsMax = 0;
			//on stocke les valeurs des date, heure et compteur temps
			derniereValeurCptTemps = cptTempsCourant;
			derniereValeurDate = newDateValue;
			derniereValeurHeure = newHeureValue;

			//si la varaible LatchTemps est valorisée newChgtEncountered = true
			// on ajoute la valeur du compteur temps avant changement c'est à dire la valeur de LatchTemps
			//			if (newChgtEncountered){
			//			this.tc.addTime(newHeureValue + tempsAvantChgt);	
			//			cumulTemps += tempsAvantChgt;
			//			}
			//			else{
			this.tc.addTime(newHeureValue);
			//}
			//si la distance est valorisée on incrémente le compteur distance
			//la valeur du cumul de distance est égale à :
			//cumul distance +  valeur courante du compteur distance - dernière valeur du compteur distance
			if(newDistanceEncoutered){
				cumulDistance += newDistanceValue - derniereValeurCptDistance;
				derniereValeurCptDistance = newDistanceValue;
			}


		} 
		//tous les autres cas
		else {

			//si la variable dateHeure est valorisée
			if (newDateHeureEncountered) {
				if (!premiereDateRencontree) {
					premiereDateRencontree = true;
					cumulTemps = 0;
					this.tc.addDate(newDateValue);
					this.tc.addTime(newHeureValue);

					if (newCptTpsEncountered) {
						derniereValeurCptTemps = cptTempsCourant;
					}
					derniereValeurDate = newDateValue;
					derniereValeurHeure = newHeureValue;
				}else if(newCptTpsEncountered){
					cumulTemps += cptTempsCourant  - derniereValeurCptTemps; 

					this.tc.addTime(derniereValeurHeure + cumulTemps + cumulTempsMax);
					derniereValeurCptTemps = cptTempsCourant;
				}

			} else {
				if (premiereDateRencontree && newCptTpsEncountered) {

					cumulTemps += cptTempsCourant  - derniereValeurCptTemps; 

					this.tc.addTime(derniereValeurHeure + cumulTemps + cumulTempsMax);
					derniereValeurCptTemps = cptTempsCourant;
				}
			}
			//			si la distance est valorisée on incrémente le compteur distance
			//la valeur du cumul detemps est égale à :
			//cumul distance +  valeur courante du compteur distance - dernière valeur du compteur distance
			if(newDistanceEncoutered){
				cumulDistance +=newDistanceValue - derniereValeurCptDistance;
				derniereValeurCptDistance = newDistanceValue;
			}

		}

		ParseurAdapteur padapt=new ParseurAdapteur();
		padapt.gererEvNonDate(msg);

		if (premiereDateRencontree)
			msg.setAbsoluteTime(this.tc.getCurrentDateAsMillis());


		msg.setAbsoluteDistance(cumulDistance);

		msg.setLongueur(variablesLength + 1);






		// System.out.println("temps absolu du message "
		// + this.tc.getFormatedTime());
		// System.out.println("longueur du message " + msg.getLongueur());
		msg.deepTrimToSize();
		return msg;

	}

	/**
	 * Sets the flag loaded from the file and calculates the rupture of time and
	 * distance
	 * 
	 * @param crtMsg
	 *            crurrent message
	 * @throws ParseurBinaireException
	 *             if any exception occured
	 */
	protected void setFlags(Message crtMsg) throws ParseurBinaireException {

		// set the flag for the current event
		Flag flag = this.loadedFlags.get(crtMsg.getEvenement()
				.getM_ADescripteurComposant().getM_AIdentificateurComposant()
				.getNom());

		crtMsg.setFlag(flag);

		// if (this.lastMessage == null) {
		// Flag f = new Flag(0, "{", crtMsg.getEvenement() //$NON-NLS-1$
		// .getM_ADescripteurComposant()
		// .getM_AIdentificateurComposant().getNom());
		// crtMsg.setFlag(f);
		//
		// } else {
		// if (flag != null && flag.getLabel().contains("{")
		// && lastMessage.getFlag() != null
		// && lastMessage.getFlag().getLabel().contains("{")) {
		// crtMsg.setFlag(null);
		// }
		// }

		if (crtMsg.getFlag() != null) {
			if (lastMessage != null && crtMsg.getFlag().getLabel().contains("{")) { //$NON-NLS-1$

				Flag flagLastMsg = lastMessage.getFlag();

				if(flagLastMsg==null ||!flagLastMsg.getLabel().contains("{")){
					Flag fl = new Flag(0, "}", lastMessage.getEvenement() //$NON-NLS-1$
							.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom());
					if (flagLastMsg != null) {
						flagLastMsg.appendFlag(fl);
					} else {
						lastMessage.setFlag(fl);
					}
				}else{
					if(lastMessage.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()!=crtMsg.getEvenement().getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode()){
						Flag fl = new Flag(0, crtMsg.getFlag().getLabel().replace("{",""), lastMessage.getEvenement() //$NON-NLS-1$
								.getM_ADescripteurComposant()
								.getM_AIdentificateurComposant().getNom());


						crtMsg.setFlag(fl);
					}



				}


			}
		}
		setSegmentsTemp(crtMsg, this.lastMessage);
		setSegmentsDistance(crtMsg, this.lastMessage);

		// if time rupture for this message, set flag for this message
		TypeRupture rupture = TableRuptures.getInstance().getRuptureTime(
				crtMsg.getMessageId());
		if (rupture == TypeRupture.RUPTURE_TEMP_CALCULEE && dateValide) {
			// if (rupture == TypeRupture.RUPTURE_TEMP_CALCULEE && dateValide &&
			// !lastMessage.getEvenement().isChangementHeure()) {
			Flag newFlag = GestionnaireFlags.getInstance().getFlag(GestionnaireFlags.getInstance().ID_TC);

			if (flag != null) {
				// if exists flag, then append to it
				flag.appendFlag(newFlag);
			} else {
				// if not exist flag create one
				crtMsg.setFlag(newFlag);

			}

		}

		// if distance rupture for this message, set flag for this message
		rupture = TableRuptures.getInstance().getRuptureDistance(
				crtMsg.getMessageId());
		if (rupture == TypeRupture.RUPTURE_DIST_CALCULEE) {
			// if (rupture == TypeRupture.RUPTURE_DIST_CALCULEE &&
			// !lastMessage.getEvenement().isChangementHeure()) {

			flag = crtMsg.getFlag();
			Flag newFlag = GestionnaireFlags.getInstance().getFlag(GestionnaireFlags.getInstance().ID_DC);

			if (flag != null) {
				// if exists flag, then append to it
				if (!crtMsg.getEvenement().isChangementHeure())
					flag.appendFlag(newFlag);
			} else {
				// if not exist flag create one
				crtMsg.setFlag(newFlag);

			}
		}
	}

	/**
	 * Extract the byte[] value for a variable and convert it conform to the
	 * value of the 1er-poids-octet and 1er-poids-bit
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the byte[] that contains the value as is in the binary file
	 * @throws BadStructureFileException
	 */
	protected byte[] setVariableValue(AVariableComposant var, byte[] valueOrig) throws BadStructureFileException {
		long retVal = 0;
		Type typeVar;
		byte value[] = null;
		int posVar = 0;
		Poids poidsPremierBitVar = null;
		Poids poidsPremierOctetVar = null;
		Type typeVariable = null;

		if (var instanceof VariableComposite) {


			// it is a composed variable
			VariableComposite varComp = ((VariableComposite) var);
			AVariableComposant v = null;
			int remainedBits = 0;
			int size = varComp.getVariableCount();

			for (int k = 0; k < size; k++) {
				// extract the value for each variable
				v = varComp.getEnfant(k);
				v.setParent(var);
				poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
				poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
				typeVariable = v.getDescriptor().getType();


				int vo_size = valueOrig.length;
				value = new byte[vo_size];
				value=Arrays.copyOfRange(valueOrig, 0, vo_size);
				//				for (int ii = 0; ii < vo_size; ii++) {
				//					value[ii] = valueOrig[ii];
				//				}

				// inversion des octets et des bits de la variable complexe
				if (varComp.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
					value = reverseBits(value);
				}

				if (varComp.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
					value = reverseOctets(value);
				}

				typeVar = v.getDescriptor().getType();
				// if it's a value given in bits not octets, then gets the bits
				if (typeVar == Type.uintXbits || typeVar == Type.BCD4
						|| typeVar == Type.BCD8 || typeVar == Type.intXbits
						|| typeVar == Type.boolean1) {
					retVal = setXbitsValue2(v, value, posVar, remainedBits);
					remainedBits = (int) (retVal & 0xFFFFFFFF);
					posVar = (int) ((retVal >> 32) & 0xFFFFFFFF);
					//v.setValeur(null);

				}
				// cas d'une variable de type reserved
				else if (typeVar == Type.reserved) {

					// on ne récupère aucune valeur pour ce type de variable

					// on teste si le nombre de bits actuels à lire est
					// inférieur ou supérieur à la taille de la variable
					// reserved
					int diffBits = remainedBits + v.getDescriptor().getTailleBits();
					// s'il est necessaire d'avancer d'un ou plusieurs octets(si
					// diffBits>8)
					int nbOctets = Math.abs(diffBits) / 8;
					if (nbOctets > 0) {
						// on calcul le nombre de bits restants
						remainedBits = Math.abs(diffBits) % 8;
						// on avance d'autant d'octets que nécessaire
						posVar = posVar + nbOctets;
					} else {

						remainedBits = Math.abs(remainedBits
								+ v.getDescriptor().getTailleBits());

					}


				} else {
					// if the previous var was a bitwise variable, and the
					// remained bits are different
					// than 0 we should increment the posVar position as this
					// was not made before

					// cas o`u la variable commence au début d'un octet
					if (remainedBits == 0) {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + v.getDescriptor().getTailleOctets());

						if (poidsPremierOctetVar == Poids.LSB)
							valTemp = reverseOctets(valTemp);

						if (poidsPremierBitVar == Poids.LSB)
							valTemp = reverseBits(valTemp);

						v.setValeur(valTemp);
					}
					// cas où la varaible commence à un nb bit donné dans
					// l'octet
					else {

						byte valTemp[] = Arrays.copyOfRange(value, posVar,
								posVar + v.getDescriptor().getTailleOctets()
								+ 1);
						int mask = (int) Math.pow(2, 8 - remainedBits) - 1;
						// on met à 0 tous les bits inutiles sur le premier
						// octet
						valTemp[0] = (byte) (valTemp[0] & mask);
						// on place le tableau de byte dans un BigInteger
						BigInteger bigInt = new BigInteger(valTemp);
						// on fait un décalage d'autant de bits que necessaire
						bigInt = bigInt.shiftRight(8 - remainedBits);

						byte valVar[] = bigInt.toByteArray();
						if (poidsPremierOctetVar == Poids.LSB)
							valVar = reverseOctets(valVar);

						if (poidsPremierBitVar == Poids.LSB)
							valVar = reverseBits(valVar);

						v.setValeur(valVar);
					}

					posVar += (v.getDescriptor().getTailleOctets());

				}
			}

			var.setValeur(value);
		} else {
			poidsPremierBitVar = var.getDescriptor().getPoidsPremierBit();
			poidsPremierOctetVar = var.getDescriptor().getPoidsPremierOctet();
			typeVariable = var.getDescriptor().getType();
			int vo_size = valueOrig.length;
			value = new byte[vo_size];
			value=Arrays.copyOfRange(valueOrig, 0, vo_size);
			//			for (int ii = 0; ii < vo_size; ii++) {
			//				value[ii] = valueOrig[ii];
			//			}

			// reverse the order of octets or bytes if the poids are set on LSB
			if (var.getDescriptor().getPoidsPremierBit() == Poids.LSB) {
				value = reverseBits(value);
			}
			if (var.getDescriptor().getPoidsPremierOctet() == Poids.LSB) {
				value = reverseOctets(value);
			}

			// set the value
			try{
				var.setValeur(value);
			}
			catch(NullPointerException ex){

			}
		}
		return value;
	}

	/**
	 * Récupère la valeur d'une sous-variable(dans une varaible complexe)
	 * 
	 * @param v
	 *            la varaible dont on doit définir la valeur
	 * @param value
	 *            le tableau d'octets contenant la valeur
	 * @param posVar
	 *            la position dans le tableau d'octets de la variable complexe
	 * @param startBitIdx
	 *            le nombre de bits restant dans l'octet
	 * @return a long containing the new posVar on the first 32 bits and the
	 *         number of bits remained from the last octet on the last 32 bits
	 */
	protected long setXbitsValue2(AVariableComposant v, byte[] value, int posVar,
			int startBitIdx) {
		// récupération de la longueur de la variable
		int length = v.getDescriptor().getTailleBits();
		byte b = 0;
		int mask;
		int extractedBytesCnt = 0;
		int positionsToShift = 0;
		Poids poidsPremierBitVar = v.getDescriptor().getPoidsPremierBit();
		Poids poidsPremierOctetVar = v.getDescriptor().getPoidsPremierOctet();
		Type typeVariable = v.getDescriptor().getType();
		int tailleVar = v.getDescriptor().getTailleBits();
		// anciennes spécifications
		// // chaque fois que l'on recommence un nouvel octet,
		// // on considère que la varaible précédente(dans l'octet n'est ni en
		// // poids-1er bit = MSB ni LSB)
		// if (startBitIdx == 0) {
		// firstPartLSB = false;
		// firstPartMSB = false;
		// }
		// cas où la variable est entièrement stockée dans l'octet courant
		// longueur de la variable inférieure au nombre de bits restant dans
		// l'octet
		int remainedBits;
		if (length <= 8 - startBitIdx) {

			// récupération du nombre de bits restant dans l'octet
			remainedBits = 8 - startBitIdx;

			// ajout olivier
			// cas d'une variable en poids-1er-bit=LSB
			if (poidsPremierBitVar == Poids.LSB) {
				// on inverse les bits de tout le tableau d'octet
				byte[] bitsReverse = reverseBits(value);

				// cas d'un BCD4
				{

					// nouvelles spécifications: on décale d'autant de bits que
					// la
					// le bit de début de lecture(étant donné que l'on inverse

					b = (byte) (bitsReverse[posVar] >>> startBitIdx);

				}

				/*
				 * anciennes spécifications // si la varaible précédente dans le
				 * meme octet n'est pas en // MSB // if (firstPartLSB) { // //
				 * si on est pas au début de l'octet // if (startBitIdx > 0) { // //
				 * on récupère les bits de droite // // l'octet et on décale //
				 * b = (byte) (bitsReverse[posVar] >>> remainedBits -length ); // } // } // } //
				 * la première varable dans l'octet est en poids-1er-bit=LSB //
				 * firstPartLSB = true;
				 */
			}

			else{

				// nouvelles spécifications
				b = (byte) (value[posVar] >>> remainedBits - length);

				/*
				 * anciennes spécifications // if (firstPartMSB) { // if
				 * (startBitIdx > 0) { // b = (byte) (value[posVar] >>>
				 * remainedBits - length); // } // } // } // la première
				 * variable dans l'octet est en poids-1er-bit=MSB //
				 * firstPartMSB = true;
				 */

			}

			// on calcule le masque pour récupérer seulement les bits qui nous
			// interressent
			mask = (int) Math.pow(2, length) - 1;
			// on applique le masque pour récupérer seulement les bits qui nous
			// interressent
			cacheMemory[0] = (byte) (b & mask);

			// un seul octet a été extrait
			extractedBytesCnt = 1;

			// on met à jour l'index du bit à prendre dans l'octet
			startBitIdx = startBitIdx + length;

		}

		// cas où la variable a une longueur supérieure
		// au nombre de bits restant dans l'octet
		else {

			int octetCourant = posVar;

			// récupération de ce qui reste dans le premier octet de la variable
			remainedBits = 8 - startBitIdx;

			// cas d'une variable en MSB
			if (poidsPremierBitVar == Poids.MSB) {

				// nouvelles spécifications
				b = (byte) (value[posVar]);

				// anciennes spécifications
				// if (startBitIdx > 0) {
				// // on prend les bits tout à gauche
				// // calcul du masque
				// mask = (int) (255 - (Math.pow(2, startBitIdx) - 1));
				// // décalage
				// b = (byte) ((value[octetCourant] & mask) >>> startBitIdx);
				// cacheMemory[extractedBytesCnt] = b;
				// } else {
				// // sinon on prend les bits de droite
				// mask = (int) (Math.pow(2, remainedBits) - 1);
				// b = (byte) ((value[octetCourant] & mask));
				// cacheMemory[extractedBytesCnt] = b;
				// }

			}

			// cas d'une variable en LSB
			else{

				// nouvelles spécifications
				// on récupère l'octet entier
				b = (byte) (value[posVar]);

				// anciennes spécifications
				// b = bitsReverse[octetCourant];
				// if (startBitIdx > 0) {
				// b = (byte) (b >>> startBitIdx);
				// }
			}

			// on ne prend que les bits qui nous interressent
			mask = (int) Math.pow(2, remainedBits) - 1;
			// on met l'octet dans le tableau d'octets temporaire
			cacheMemory[extractedBytesCnt] = (byte) ((b & mask));

			// on incrémente pour obtenir le prochain octet
			extractedBytesCnt++;
			// on met a jour le nombre de bits restant pour la variable
			length -= remainedBits;

			boolean varTerminee = false;
			while (!varTerminee) {

				// l'octet courant change d'indice
				octetCourant = posVar + extractedBytesCnt;
				// si le nombre de bits restants de la variable est inférieure à
				// 8
				if (length - 8 <= 0) {

					startBitIdx = length;

					positionsToShift = 8 - length;

					varTerminee = true;

				} else {
					// on récupère l'octet entier

					length -= 8;

					if (length <= 0)
						varTerminee = true;

				}

				cacheMemory[extractedBytesCnt] = (byte) (value[octetCourant]);
				extractedBytesCnt++;
			}
		}

		// on créé un BigInteger qui permet de contenir toute la variable et de
		// faire des décalages à gauche ou à droite
		// cacheMemory : tableau d'octets de taille fixe
		byte[] valuesByteArray = Arrays.copyOfRange(cacheMemory, 0,
				extractedBytesCnt);

		if (tailleVar <= 8) {
			if (valuesByteArray.length == 2) {

				BigInteger bigInt = new BigInteger(valuesByteArray);
				bigInt = bigInt.shiftRight(positionsToShift);
				// cas où la variabe est autre qu'un intxbits

				byte tabVar[] = bigInt.toByteArray();
				byte tabVar2[] = new byte[1];
				if (poidsPremierBitVar == Poids.MSB) {
					mask = (int) Math.pow(2, tailleVar) - 1;
				}
				else {
					tabVar = reverseBits(tabVar);
					mask = (int) Math.pow(2, tailleVar) - 1;
					if (tabVar.length > 1)
						tabVar[0] = (byte) (tabVar[1] >> (8 - tailleVar));
					else
						tabVar[0] = (byte) (tabVar[0] >> (8 - tailleVar));

				}
				tabVar2[0] = (byte) (tabVar[0] & mask);

				v.setValeur(tabVar2);
			} else {
				v.setValeur(valuesByteArray);
			}
		}

		else {
			if (positionsToShift > 0) {

				byte[] valueArr = null;

				BigInteger bigInt = null;
				// cas de tout les entiers signés
				// il sont enregistrés en complément à 2 dans le fichier de
				// parcours
				if (typeVariable == Type.intXbits || typeVariable == Type.int16 || typeVariable == Type.int24
						|| typeVariable == Type.int32 || typeVariable == Type.int64 || typeVariable == Type.int8) {
					// on créé un BigInteger signé
					bigInt = new BigInteger(valuesByteArray);


				}
				// on créé un BigInteger non signé
				//t == Type.uintXbits || t == Type.uint8 || t == Type.uint16
				//|| t == Type.uint24 || t == Type.uint32
				//|| t == Type.uint64 || t == Type.real32
				//|| t == Type.real64 || t == Type.dateHeureBCD
				else {
					bigInt = new BigInteger(1, valuesByteArray);


				}
				bigInt = bigInt.shiftRight(positionsToShift);
				valueArr = bigInt.toByteArray();

				if (poidsPremierBitVar == Poids.LSB) {
					valueArr = reverseBits(valueArr);
					int taille = tailleVar;
					if (taille % 8 != 0
							&& ((valueArr.length < (taille / 8)) || (valueArr.length > (taille / 8)))) {
						int decalage = 8 - (tailleVar % 8);
						valueArr[0] = (byte) (valueArr[0] >>> decalage);
					}

				}

				if (poidsPremierOctetVar == Poids.LSB)
					valueArr = reverseOctets(valueArr);

				v.setValeur(valueArr);
			} else {

				if (poidsPremierBitVar == Poids.LSB)
					valuesByteArray = reverseBits(valuesByteArray);

				if (poidsPremierOctetVar == Poids.LSB)
					valuesByteArray = reverseOctets(valuesByteArray);

				v.setValeur(valuesByteArray);
			}
		}
		// cas des intxbits: on doit reproduire le complément à 2 sur l'octet de
		// poids fort
		if (typeVariable == Type.intXbits) {
			byte[] valeurVariable = (byte[]) v.getValeur();//tagValCor

			byte octetSigne = valeurVariable[0];
			int tailleVariable = tailleVar;

			if (tailleVar < 8) {

				int maskOctetSigne = (int) Math.pow(2, tailleVar - 1);
				octetSigne = (byte) (octetSigne & maskOctetSigne);

				if (octetSigne > 0) {
					int maskOctet = (int) Math.pow(2, tailleVar) - 1;
					valeurVariable[0] = (byte) (valeurVariable[0] + (255 - maskOctet));

					v.setValeur(valeurVariable);

				}

			}
			if ((tailleVar > 8) && (tailleVar % 8 > 0)) {

				int indiceBitsigne = tailleVar % 8;
				int maskOctetSigne = (int) Math.pow(2, indiceBitsigne - 1);
				octetSigne = (byte) (octetSigne & maskOctetSigne);

				if (octetSigne > 0) {
					int maskOctet = (int) Math.pow(2, indiceBitsigne) - 1;
					valeurVariable[0] = (byte) (valeurVariable[0] + (255 - maskOctet));

					v.setValeur(valeurVariable);
				}
			}

		}

		// on met à jour l'indice de début de lecture pour la variable suivante
		if (startBitIdx >= 8)
			startBitIdx = 0;

		if (startBitIdx > 0) {
			// if there were some bits used from a byte, but not all the byte,
			// this byte has left
			// some bits that are used by another variable = > the position must
			// be decremented
			posVar = posVar + extractedBytesCnt - 1; // if we still have
			// bits, we should
			// remain on the last
			// byte
		} else {
			posVar = posVar + extractedBytesCnt; // if no bits in the last
			// byte, go to next after
			// the last
		}

		// we return from here both the remained bits and the incremented pos
		// var
		// in order to avoid making the same computations outside
		long retVal = (((long) posVar) << 32) | startBitIdx;

		return retVal;
	}

	/**
	 * Gets a number of bits from the given byte
	 * 
	 * @param crtByte
	 *            the byte to extract the bits
	 * @param offset
	 *            the offset to start from
	 * @param length
	 *            the number of bits to extract
	 * @return in a byte only the bits extracted
	 */
	protected byte getBits(byte crtByte, int offset, int length) {
		byte bits = 0;
		// get the usefull bits from the current byte
		int mask = 0x0000;
		for (int i = offset; i < length + offset; i++) {
			// if is a used bit then set 1 on that position of the mask
			// offset = 3 => 1110 0000
			mask += Math.pow(2, i);
		}
		bits = (byte) (crtByte & mask); // offset = 3 => bits = xxx0 0000
		return bits;
	}

	/**
	 * Loads the association table of events and variables
	 * 
	 * @return the assoctiation table as a parcour
	 * @throws ParseurBinaireException
	 *             throws an exception if any occurs
	 */
	protected ADescripteurComposant chargerTableEvtVariable()
			throws ParseurBinaireException {

		// voir doc Config_TOM_UK_529494.103t.doc page 83 pour tous les détails
		// TODO: gérer les exceptions listés dans doc(bloquantes, non bloquantes)

		// creation of a DescripteurComposite for contain all associations
		// codeEvt codeVar1 codeVar2 codeVar3 etc.
		ADescripteurComposant descripteurTable = new DescripteurComposite();

		int pos = ConstantesParcoursTom4.octetDebutTableEvtVar;

		// récupération de la liste des événements chargés à partir du xml:
		// l'ordre des événements dans le xml doit etre le meme que celui du
		// fichier de parcours
		List<Evenement> listeEvenements = GestionnairePool.getInstance().getListeEvenements();

		// chaque tableau de correspondance nom evt - codes var fait 41 octets
		// (32:nom evt, 8: codes variables(1 otet par codevar))
		byte[] tabEvtVar = new byte[41];
		// on incrémente un indice d'événement pour pouvoir avoir les événement
		// du binaire et les événements du xml dans le meme ordre


		//System.out.println("début chargement table evt variables");

		int cptCodeEvt = 0;
		boolean finLecture = false;
		//tagtag
		while (pos < (ConstantesParcoursTom4.octetDebutTableEvtVar + tailleTableEvenement)
				&& !finLecture) {

			// on récupère un tableau de 41 octets
			tabEvtVar = Arrays.copyOfRange(this.message, pos - 1, pos + 40);
			String evtName = new String(Arrays.copyOfRange(tabEvtVar, 0,
					tailleBlocData)).trim();

			//			System.out
			//			.println("--------------------------------------------------");



			//System.out.println("Evénement: " + evtName);

			// creation of a DescripteurComposite for each assoctiation
			// codeEvt
			// codeVar1 codeVar2 codeVar3 etc...
			ADescripteurComposant descComp = new DescripteurComposite();

			if (tabEvtVar[33] != -1) {
				DescripteurEvenement descEvt = (DescripteurEvenement) GestionnairePool.getInstance().getEvent(cptCodeEvt).getM_ADescripteurComposant();
				if (descEvt != null) {

					//					System.out.println("code: " + descEvt.getCode() + " nom: "
					//					+ descEvt.getNom());

					descComp.ajouter(descEvt);
					byte[] b = new byte[2];

					int codeVar = 0;
					int posVar = 33;
					BigInteger bInt;
					do {
						b[0] = 0;
						b[1] = tabEvtVar[posVar];
						bInt = new BigInteger(b);
						codeVar = bInt.intValue();

						// on ne rajoute le descripteur de la variable que si
						// celle-ci
						if (codeVar != 0 && codeVar != -1) {
							AVariableComposant varCourante = GestionnairePool.getInstance().getVariable(codeVar);

							if (varCourante == null) {
								//								System.out.println("variable introuvable:code "
								//								+ codeVar);
								DescripteurVariable descVar = new DescripteurVariable();
								IdentificateurVariable identVar = new IdentificateurVariable();
								identVar.setCode(codeVar);
								identVar.setNom(codeVar + "");
								descVar.setM_AIdentificateurComposant(identVar);
								descComp.ajouter(descVar);
							} else {
								//								System.out
								//								.println("nom variable: "
								//								+ varCourante
								//								.getDescriptor()
								//								.getM_AIdentificateurComposant()
								//								.getNom() + " code :"
								//								+ codeVar);
								descComp.ajouter(varCourante.getDescriptor());
							}
						}
						posVar++;
					} while (posVar < 41);

					if (codeVar != -1)
						descripteurTable.ajouter(descComp);
				}
			}

			//System.out.println("compteur code evenement :" + cptCodeEvt);

			pos += 40;
			cptCodeEvt++;




		}

		return descripteurTable;
	}

	
	protected boolean isItSegmentTempBeginning(Message crtMsg, Message prevMsg) 
	{
		// si l'événement est un événement de rupture d'acquisition ou bien le premier message du parcours
		if (prevMsg == null
				|| crtMsg.getEvenement().isRuptureAcquisition()
				&& !prevMsg.getEvenement().isRuptureAcquisition()
				&& prevMsg.getEvenement().getM_ADescripteurComposant()
				.getM_AIdentificateurComposant().getCode() != crtMsg
				.getEvenement().getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getCode()) 
		{
			return(true);
		}
		else if (prevMsg != null) 
		{
			// on vérifie si il y a un changement d'heure
			boolean isTimeEvtChgt = crtMsg.getEvenement().isChangementHeure();

			if (isTimeEvtChgt) 
			{
				return(true);
			}
			else if (crtMsg.getAbsoluteTime() < prevMsg.getAbsoluteTime()
						&& !prevMsg.getEvenement().isChangementHeure()) 
			{
				return(true);
			}
			else if (prevMsg.isMessageIncertitude()
						&& !crtMsg.isMessageIncertitude()) 
			{
				return(true);

			} else if (crtMsg.getEvenement().isRuptureAcquisition()
						&& prevMsg.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode() == crtMsg
						.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode()) 
			{
				return(true);
			}
		}
		
		return(false);
	}	
	
	/**
	 * Calculates the time segments
	 * 
	 * @param crtMsg
	 *            current message
	 * @param prevMsg
	 *            previous message
	 */
	protected void setSegmentsTemp(Message crtMsg, Message prevMsg) {
		if (crtMsg != null) {

			// si l'événement est un événement de rupture d'acquisition ou bien le premier message du parcours
			if (prevMsg == null
					|| crtMsg.getEvenement().isRuptureAcquisition()
					&& !prevMsg.getEvenement().isRuptureAcquisition()
					&& prevMsg.getEvenement().getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getCode() != crtMsg
					.getEvenement().getM_ADescripteurComposant()
					.getM_AIdentificateurComposant().getCode()) {
				// si on est sur les messages de 2 à n
				if (prevMsg != null) {

					// si un segment a déjà été créé on teste si l'événement
					// précédent n'est pas le début du segment
					// si c'est le cas on enregistre le segment et on en créé un
					// autre
					if (this.lastSegTemp != null) {
						TableRuptures.getInstance().ajouterRuptureTemps(
								crtMsg.getMessageId(),
								TypeRupture.RUPTURE_TOTALE_TEMPS_ABSOLU);
						TableRuptures.getInstance().ajouterRuptureDistance(
								crtMsg.getMessageId(),
								TypeRupture.RUPTURE_TOTALE_DISTANCE_ABSOLU);
						enregistrerSegmentTemps(prevMsg);
						SegmentTemps segment = new SegmentTemps();
						segment.setStartMsgId(crtMsg.getMessageId());
						segment.setTempInitial(crtMsg.getAbsoluteTime());
						segment.setOffsetDebut(crtMsg.getOffsetDebut());
						segment.setTempCorrige(segment.getTempInitial());
						this.lastSegTemp = segment;

					}

				}
				// on est sur le message 1: on créé un segment
				else {

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}
			}

			else if (prevMsg != null) {

				// on vérifie si il y a un changement d'heure
				boolean isTimeEvtChgt = crtMsg.getEvenement()
						.isChangementHeure();

				if (isTimeEvtChgt) {
					// si il y a un segment précédent
					if (this.lastSegTemp != null) {
						enregistrerSegmentTemps(prevMsg);
					}

					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.CHANGEMENT_HEURE_RUPTURE);

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}

				else if (crtMsg.getAbsoluteTime() < prevMsg.getAbsoluteTime()
						&& !prevMsg.getEvenement().isChangementHeure()) {
					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.RUPTURE_TEMP_CALCULEE);
					enregistrerSegmentTemps(prevMsg);

					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				} else if (prevMsg.isMessageIncertitude()
						&& !crtMsg.isMessageIncertitude()) {
					enregistrerSegmentTemps(prevMsg);
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;

				} else if (crtMsg.getEvenement().isRuptureAcquisition()
						&& prevMsg.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode() == crtMsg
						.getEvenement().getM_ADescripteurComposant()
						.getM_AIdentificateurComposant().getCode()) {
					TableRuptures.getInstance().ajouterRuptureTemps(
							crtMsg.getMessageId(),
							TypeRupture.RUPTURE_TOTALE_TEMPS_ABSOLU);
					TableRuptures.getInstance().ajouterRuptureDistance(
							crtMsg.getMessageId(),
							TypeRupture.RUPTURE_TOTALE_DISTANCE_ABSOLU);
					enregistrerSegmentTemps(prevMsg);
					// a new segment starts
					SegmentTemps segment = new SegmentTemps();
					segment.setStartMsgId(crtMsg.getMessageId());
					segment.setTempInitial(crtMsg.getAbsoluteTime());
					segment.setOffsetDebut(crtMsg.getOffsetDebut());
					segment.setTempCorrige(segment.getTempInitial());
					this.lastSegTemp = segment;
				}

			}

		} else {
			// close the segment
			if (this.lastSegTemp != null) {
				enregistrerSegmentTemps(prevMsg);
			}
		}
	}

	/**
	 * Checks if there is the corect number of FF bytes
	 * 
	 * @param start
	 *            the start position
	 * @param noFF
	 *            number of bytes of FF
	 * @return true if the bytes contains only FF, false otherwise
	 */
	protected boolean checkOctetsFF(int start, int noFF) {
		boolean valid = true;
		int crt = 0;
		while (crt < noFF) {
			if ((this.message[start + crt] & 0xff) != codeFF) {
				valid = false;
				break;
			}
			crt++;
		}
		return valid;
	}

	/**
	 * Parses the binary file
	 * 
	 * @param fileName
	 *            name of the file
	 */
	public void parseRessource(String fileName,boolean explorer,int deb,int fin) throws AExceptionSamNG {
		//reset des variables
		this.cumulTempsMax = 0;
		this.derniereValeurCptTemps = 0;
		this.derniereValeurHeure = 0;
		this.derniereValeurDate = 0;
		this.lastCptTempsTimeChange = 0;
		this.derniereValeurCptDistance = 0;
		this.cumulDistance =0;
		this.cumulDistanceMax =0;
		this.lastMessage = null;
		this.cumulTemps = 0;
		this.lastSegDistance = null;
		this.lastSegTemp = null;
		this.premiereDateRencontree = false;

		AFabriqueParcoursAbstraite factory;
		//		if (explorer) {
		//			TableSegmentsExplorer.getInstance().empty();
		//			TablesRupturesExplorer.getInstance().clear();
		//			factory = FabriqueParcoursExplorer.getInstance();
		//		}else{
		//			TableSegments.getInstance().empty();
		//			TableRuptures.getInstance().clear();
		//			factory = FabriqueParcours.getInstance();
		//		}	

		TableSegments.getInstance().empty();
		TableRuptures.getInstance().clear();
		factory = FabriqueParcours.getInstance();

		//		if (deb!=0 || fin != -1) {
		//			this.message = Arrays.copyOfRange(loadBinaryFile(new File(fileName),0,-1), deb, fin);
		//		}else{
		this.message = loadBinaryFile(new File(fileName),0,-1);
		//		}

		this.nomFichierBinaire = fileName;

		traitementFichier(factory, fileName,explorer);

		//TODO: vérifier si cela fonctionne
		// if(!verifierCRCConfiguration()){
		// String errStr =
		// Messages.getString("errors.blocking.badConfigurationCrc");
		// throw new BadHeaderCrcException(errStr);
		// }

		//		deb=calculOffsetDebut(tailleBlocData, deb);
		
		if (explorer)
		{
			chargerDataExplore();
		}
		else
		{
			Message[][] messages = chargerData(deb,fin);
			// create DATA
			factory.creerData(messages);
			//		if (explorer) {
			factory.creerReperes(GestionnairePool.getInstance().getReperes());
			//		}
		}	
	}

	private int calculOffsetDebut(short tailleBlocData, int offsetDebut) {
		for (int i = offsetDebut; i > offsetDebut-tailleBlocData; i--) {
			if (i%tailleBlocData==0) {
				return i;
			}
		}
		return offsetDebut;
	}

	protected boolean verifierCRCConfiguration() {
		// vérification du crc de la configuration
		// à activer
		ADonneeBlocComposant crc = chargerCRCConfiguration();

		IStrategieControle control = new CRC16();

		if (!control.controlerCRC(((Integer) crc.getValeur()).intValue(),
				Arrays.copyOfRange(this.message, 0,
						ConstantesParcoursTom4.tailleConfiguration - 2))) {

			return false;
		} else
			return true;

	}

	@Override
	protected AVariableComposant chargerReperes() {
		// TODO Raccord de méthode auto-généré
		return null;
	}

	/**
	 * charge le CRC enregistré de l'entete dans un objet ADonneeBlocComposant
	 */
	protected abstract ADonneeBlocComposant chargerCRCConfiguration();

	public HashMap<Integer, Boolean> getCrcEnregistrements() {
		return crcEnregistrements;
	}

	public void setCrcEnregistrements(HashMap<Integer, Boolean> crcEnregistrements) {
		this.crcEnregistrements = crcEnregistrements;
	}

	/**
	 * Méthode de récupération du nom du fichier xml à charger
	 * @param nomFichier
	 * @return nom du fichier xml contenant le type de parseur à utiliser
	 * @throws IOException 
	 * @throws ParseurXMLException 
	 * @throws Exception
	 */
	public String getNomFichierXml(String nomFichier) throws IOException, ParseurXMLException{

		File  f = new File(nomFichier);
		InputStream is;
		long length = f.length();
		byte bytes[] = new byte[(int)length];  
		is = new FileInputStream(f);
		is.read(bytes, 0, 4);
		byte[] tmpId = Arrays.copyOfRange(bytes, 0, 4);

		String idCfg = "";
		for (int i=0; i < tmpId.length; i++) {
			idCfg += Integer.toString( ( tmpId[i] & 0xff ) + 0x100, 16).substring( 1 );
		}

		Properties p = new Properties();
		File f2 = new File(CHEMIN_FICHIER_IDCONFIG);
		if(!f2.exists()) {
			throw new ParseurXMLException(Messages.getString("errors.blocking.errorLoadingIdConfigFile") + CHEMIN_FICHIER_IDCONFIG,true);
		}

		InputStream stream = new FileInputStream(f2.getAbsolutePath());
		p.load(stream);
		String nomFichierXml= (String)p.get(idCfg);
		return nomFichierXml;
	}

}