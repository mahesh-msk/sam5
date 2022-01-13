package com.faiveley.samng.principal.sm.missions;

import java.io.File;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import com.faiveley.samng.principal.sm.missions.jaxb.TypeDocument;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeListeMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeListeRegroupementTemps;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRefMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRegroupementTemps;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeSegment;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class ParseurMissions {

	public static boolean creerFichierMissionNonJRU(String nomFichierParcours, HashMap<Integer,SegmentTemps> listeSegments) throws DatatypeConfigurationException{
		boolean isValide = true;

		TypeDocument doc = new TypeDocument();
		TypeListeMission listeMissions = new TypeListeMission();
		TypeListeRegroupementTemps listeRegroupementTemps = new TypeListeRegroupementTemps();

		doc.setListeMission(listeMissions);
		doc.setListeRegoupementTemps(listeRegroupementTemps);

		String datePrev = "datePrev"; //$NON-NLS-1$
		String dateRegTpsPrev = "dateRegTpsPrev"; //$NON-NLS-1$

		int numMission = 0;
		int numSegment = 0;
		int numRegTps = 0;

		TypeMission missionCurrent = null;
		TypeRegroupementTemps trt = null;		

		int size = listeSegments.size();
		for (int i = 0 ; i < size ; i++){
			SegmentTemps seg = listeSegments.get(i);
			String dateCurrent = seg.getTempInitial().substring(0, 10);

			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(seg.getTempInitialToLong());
			XMLGregorianCalendar xcDeb = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
			gc.setTimeInMillis(seg.getTempFinalToLong());
			XMLGregorianCalendar xcFin = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);



			// Nouvelle mission
			if (!dateCurrent.equals(datePrev)){
				datePrev = new String(dateCurrent);
				missionCurrent = new TypeMission();
				listeMissions.getMission().add(missionCurrent);

				// Debut Mission
				missionCurrent.setDateDebut(xcDeb);
				missionCurrent.setOffsetDebut(seg.getOffsetDebut());
				missionCurrent.setId("m" + numMission); //$NON-NLS-1$
				missionCurrent.setNumero(new BigInteger(""+numMission)); //$NON-NLS-1$
				missionCurrent.setIdMessageDebut(new BigInteger("" + seg.getStartMsgId())); //$NON-NLS-1$
				numMission++;

				// Traitement des regroupements de temps
				String dateRegrTpsCurrent = dateCurrent.substring(3);
//				System.out.println("dateRegrTpsCurrent = " + dateRegrTpsCurrent);
				if (!dateRegrTpsCurrent.equals(dateRegTpsPrev)){
					dateRegTpsPrev = new String(dateRegrTpsCurrent);

					trt = new TypeRegroupementTemps();
					listeRegroupementTemps.getRegroupementTemps().add(trt);

					trt.setId("r" + numRegTps); //$NON-NLS-1$
					trt.setMois(xcDeb);
					trt.setNumero(new BigInteger(""+numRegTps)); //$NON-NLS-1$
					numRegTps++;
				}
				TypeRefMission refMission = new TypeRefMission();
				refMission.setReference(missionCurrent);
				trt.getListeMission().getMission().add(refMission);


				numSegment = 0;
			}
			// Même mission car date identique
			else{

			}

			// Fin Mission
			missionCurrent.setDateFin(xcFin);
			missionCurrent.setOffsetFin(seg.getOffsetFin());

			// gestion de chaque segment
			TypeSegment segment = new TypeSegment();
			segment.setDateDebut(xcDeb);
			segment.setDateFin(xcFin);
			segment.setOffsetDebut(seg.getOffsetDebut());
			segment.setOffsetFin(seg.getOffsetFin());
			segment.setNumero(new BigInteger(""+numSegment++)); //$NON-NLS-1$
			segment.setIdMessageDebut(new BigInteger("" + seg.getStartMsgId())); //$NON-NLS-1$

			missionCurrent.getListeSegment().getSegment().add(segment);

		}

		String packageName = "com.faiveley.samng.principal.sm.missions.jaxb"; //$NON-NLS-1$

		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(packageName);
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.valueOf(true));
			File xmlFile = new File(nomFichierParcours+".xml");
			jaxbMarshaller.marshal(doc, xmlFile); //$NON-NLS-1$
		} catch (JAXBException e) {
			// TODO Bloc catch généré automatiquement
			e.printStackTrace();
		}
		return isValide;
	}

	public static void lancerParseurMissions(File f, Job job) {

		HashMap<Integer, SegmentTemps> listeSegments = null;
		try {
			System.out.println("Création du fichier XML"); //$NON-NLS-1$

			listeSegments = TableSegments.getInstance().getSegmentsTemps();
//			System.out.println("Nombre de segments : " +listeSegments.size()); //$NON-NLS-1$
//			System.out.println("Nom du fichier : " +f.getName()); //$NON-NLS-1$
		} catch (Exception e1) {
			System.out.println("Problème lors de la création du fichier xml des missions"); //$NON-NLS-1$
			e1.printStackTrace();
		}
		
		//if (!(TypeParseur.getInstance().getParser()instanceof ParseurParcoursJRU)){	
			if (listeSegments==null) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					MessageDialog.openError(Display.getCurrent().getActiveShell(),
							Messages.ParseurMissions_32, Messages.ParseurMissions_33);			
				}
			});
						
			if (job!=null) {
				job.cancel();
			}	
			return;
		}
		//}


		// Pour les fichiers JRU, le fichier de mission est créé dans chargerDataExplore()
		try {
		if (!(TypeParseur.getInstance().getParser()instanceof ParseurParcoursJRU)) {
				System.out.println("Fichier NON JRU"); //$NON-NLS-1$
				ParseurMissions.creerFichierMissionNonJRU(f.getAbsolutePath(), listeSegments);
				System.out.println("Fin traitement Fichier NON JRU"); //$NON-NLS-1$
				}
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}		
	}
}
