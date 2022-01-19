package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.vue.SetReferenceAction;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.missions.ParseurMissions;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class ChargeurParcours {

	public static void loadBinaryFile(String fileName,int deb,int fin, boolean lancerExplorer) throws AExceptionSamNG,ParseurXMLException {

		SamngLogger.emptyLogFile();
		GestionnaireFlags.getInstance().clear();
		ParseurXML1.setParseurXML();

		//reset the list of current segments
		//TableSegments.getInstance().empty();

		final ParseurParcoursBinaire parser;

		try {
			FormatSAM format=BridageFormats.getInstance().getFormatFichierOuvert(fileName);

			if (format==FormatSAM.TOMNG) { //$NON-NLS-1$ //$NON-NLS-2$
				parser = ParseurParcoursSamng.getInstance();
				TypeParseur.getInstance().setParser(parser);
				parser.parseRessource(fileName,false,deb,fin);
			}else if (format==FormatSAM.TOM4) {
				//on récupère le modèle d'enregistreur pour utiliser le bon parseur
				String nomFichierXml = ParseurParcoursTomUk.getInstance().getNomFichierXml(fileName);
				//cas où aucun nom ne correspond à l'id de la configuration présent dans le fichier binaire
				if(nomFichierXml==null)
					throw new ParseurXMLException(Messages.getString("ChargeurParcours.7"),true);
				//cas où le fichier xml est absent
				File f = new File(RepertoiresAdresses.xml + File.separator + nomFichierXml);
				if(!f.exists())
					throw new ParseurXMLException(Messages.getString("ChargeurParcours.9") + nomFichierXml,true);

				ParseurXML1 parseur = ParseurXML1.getInstance(fileName);
				parseur.parseRessource(RepertoiresAdresses.xml + File.separator + nomFichierXml,false,0,-1);

				String modele = parseur.chargerType();
				if(modele.equals("TOM_UK")){
					parser = ParseurParcoursTomUk.getInstance();
					TypeParseur.getInstance().setParser(parser);
					parser.parseRessource(fileName,false,deb,fin);
				}else if(modele.equals("TOM_DIS")){
					parser = ParseurParcoursTomDIS.getInstance();
					TypeParseur.getInstance().setParser(parser);
					parser.parseRessource(fileName,false,deb,fin);
				}else if(modele.equals("TOM_HSBC")){
					parser = ParseurParcoursTomHSBC.getInstance();
					TypeParseur.getInstance().setParser(parser);
					parser.parseRessource(fileName,false,deb,fin);
				}
			}else if(format==FormatSAM.ATESS) {
				parser = ParseurParcoursAtess.getInstance();
				TypeParseur.getInstance().setParser(parser);
				parser.parseRessource(fileName,false,deb,fin);
			}else if(format==FormatSAM.JRU) {
				parser = ParseurParcoursJRU.getInstance();
				TypeParseur.getInstance().setParser(parser);
				parser.parseRessource(fileName,false,deb,fin);
			}

			if (lancerExplorer) {
				ParseurMissions.lancerParseurMissions(new File(fileName),null);
			}
			//EFE: Ancien : Traitement à faire à partir de la liste des messages pour fichiers des missions 
			
			//			System.out.println("Création du fichier XML");	
			//			ListMessages listeMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement(0).getMessages();

			//			ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
			//			if (p == null) {
			//				return;
			//			}
			//			// get enregisrtrement
			//			Enregistrement enrg = p.getData().getEnregistrement(0);
			//			if (enrg == null) {
			//				return;
			//			}
			//
			//			Message firstMsg = enrg.getMessages() != null ? enrg.getMessages().get(0) : null;
			//			if (firstMsg != null) {
			//				ACalculReferenceZero calcRef=ACalculReferenceZero.definirCalculByParseur();	
			//				calcRef.calculerReferenceZero(firstMsg.getMessageId(),0);
			//			}
			//			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void initializePools(String fileName, boolean correctionApplied) {
		if(correctionApplied){
			try{
				//before notifying the activator load the corrections
				GestionnaireCorrection.getInstance().applyCorrections();
			}
			catch(ParseException ex){

			}
		}

		//set the reference zero to the first message by default
		SetReferenceAction.creerReferenceZeroDefaut();

		ParcoursComposite parcoursComposite = FabriqueParcours.getInstance().getParcours();

		//notify the Activator about the new data loaded
		ActivatorData.getInstance().setParcoursData(parcoursComposite);
		ActivatorData.getInstance().getGestionnaireMarqueurs().chargerMarqueurs(fileName);
		ActivatorData.getInstance().getProviderVBVs().chargerVBV(fileName);
		ActivatorData.getInstance().getProviderVBVs().chargerVBVDansMessages();
		try {
			InfosFichierSamNg infoFile;
			try{
				infoFile = (InfosFichierSamNg)parcoursComposite.getInfo();
			}catch(Exception ex){
				infoFile = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier());
			}
			ConversionTemps.setResolutionTemp(infoFile.getTempResolution());
		} catch (Exception e) {
			e.printStackTrace();
		}

		//: CIU - this dialog can be used also (according to some input parameters)
		//	to be used later for opening also XML files so, when an XML file will be opened
		//	the following function should be called also after the XML file is parsed
		//		ActivatorData.getInstance().getGestionnaireMarqueurs().chargerMarqueurs(fileName);
		//		ActivatorData.getInstance().getProviderVBVs().chargerVBV(fileName);
		//		ActivatorData.getInstance().getProviderVBVs().chargerVBVDansMessages();

		//set to the data the loaded marker ids 
		ActivatorData.getInstance().getVueData().setMarkerIds(
				ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueursIds());
	}
}
