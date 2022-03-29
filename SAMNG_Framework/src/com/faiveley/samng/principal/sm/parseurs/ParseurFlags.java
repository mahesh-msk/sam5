package com.faiveley.samng.principal.sm.parseurs;



import java.io.File;
import java.util.HashMap;
import java.util.List;

import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurFlagsExplorer;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

import noNamespace.ListeFlagsDocument;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 06-d�c.-2007 10:07:33
 */
public class ParseurFlags implements IParseurInterface {

	private HashMap<String, Flag> flagsEvents = null;
	private static ParseurFlags instance = new ParseurFlags();

	/**
	 * Private constructor. Only this class creates an inastance of itself.
	 * Singleton class.
	 */
	protected ParseurFlags(){
		flagsEvents=new HashMap<String, Flag>();
	}

	/**
	 * Returns the single instance of the parser.Parser is a singleton
	 * @return	the instance
	 */
	public static ParseurFlags getInstance() {
		
		if (instance==null) {
			if (ActivationExplorer.getInstance().isActif()) {
				return new ParseurFlagsExplorer();
			}
			instance = new ParseurFlags();
		}
		
		if (ActivationExplorer.getInstance().isActif()) {
			return ParseurFlagsExplorer.getInstance();
		}
		return instance;
	}

	/** Suppression de l'instance */
	public void clear(){
		instance = null;
		if (flagsEvents!=null) {
			flagsEvents.clear();
		}		
		flagsEvents=null;
		//CHECK01
	}

	/**
	 * Returns a map of <event name, flag>
	 * @return
	 */
	public HashMap<String, Flag> chargerFlags(){
		return this.flagsEvents;
	}

	/**
	 * Parses the flags xml file with the given name 
	 * @param fileName	the name of the file	
	 */
	public void parseRessource(String fileName,boolean explorer,int deb,int fin){
		int maxId = 0;
		try {
			ListeFlagsDocument fDoc = ListeFlagsDocument.Factory.parse(new File(fileName));
			List<noNamespace.FlagDocument.Flag> flags = fDoc.getListeFlags().getFlagList();

			//creates the lost of flags
			this.flagsEvents = new HashMap<String, Flag>();

			for (noNamespace.FlagDocument.Flag f : flags) {
				//creata each flag
				Flag flg = new Flag(f.getId(), f.getLibelle(), f.getNomEvt());
				this.flagsEvents.put(f.getNomEvt(), flg);
				maxId = maxId > f.getId() ? maxId : f.getId();
				//				if (f.getNomEvt().equals("EnrStart")) {
				//					//start event
				//					ID_START = f.getId();	
				//				} else if (f.getNomEvt().equals("ChgtJour")) {
				//					//change day event
				//					ID_CHANGE_DAY = f.getId();
				//				
				//				} else if (f.getNomEvt().equals("EvChgHeureGPS")){
				//					//change time GPS event
				//					ID_CHANGE_TIME_GPS = f.getId();
				//				
				//				} else if (f.getNomEvt().equals("EvChgHeureTOM")) {
				//					//change time TOM event
				//					ID_CHANGE_TIME_TOM = f.getId();
				//				
				//				} else if (f.getNomEvt().equals("EvChgHeureMNT")) {
				//					//change time MNT event
				//					ID_CHANGE_TIME_MNT = f.getId();
				//				} else if (f.getLibelle().equals("}")) {
				//					//stop event
				//					ID_STOP = f.getId();
				//				} else 

				if (f.getNomEvt().equals("RuptureTempsCalculee")) { //$NON-NLS-1$
					//rupture temps
					GestionnaireFlags.getInstance().ID_TC = f.getId();
				}else if (f.getNomEvt().equals("RuptureDistanceCalculee")) { //$NON-NLS-1$
					//rupture temps
					GestionnaireFlags.getInstance().ID_DC = f.getId();
				}
				GestionnaireFlags.getInstance().addFlag(flg);
			}

		} catch (Exception e) {
			SamngLogger.getLogger().error(this, e);
		}
		Flag flg = null;
		//if a known flag was not loaded from the flags xml file then create it
		if (GestionnaireFlags.getInstance().ID_START == -1) {
			//start event
			//			SamngLogger.getLogger().error("Start event is not set. Set by default to \"{\"");
			//			ID_START = ++maxId;
			//			flg = new Flag(ID_START, "{", "EnrStart");
			//			GestionnaireFlags.addFlag(flg);
			//			this.flagsEvents.put(flg.getEventName(), flg);

		}
		if (GestionnaireFlags.getInstance().ID_STOP == -1) {
			//stop event
			//			SamngLogger.getLogger().error("Stop event is not set. Set by default to \"}\"");
			//			ID_STOP = ++maxId;
			//			flg = new Flag(ID_STOP, "}", "");
			//			GestionnaireFlags.addFlag(flg);
			//			this.flagsEvents.put(flg.getEventName(), flg);
		}
		if (GestionnaireFlags.getInstance().ID_CHANGE_DAY == -1) {
			//change day event
			/*SamngLogger.getLogger().error("Change day event is not set. Set by default to \"T\"");
			ID_CHANGE_DAY = ++maxId;
			flg = new Flag(ID_CHANGE_DAY, "T", "ChgtJour");
			GestionnaireFlags.addFlag(flg);
			this.flagsEvents.put(flg.getEventName(), flg);*/
		}
		if (GestionnaireFlags.getInstance().ID_CHANGE_TIME_GPS == -1) {
			//change time gps event
			//SamngLogger.getLogger().error(Messages.getString("ParseurFlags.2")); //$NON-NLS-1$
			GestionnaireFlags.getInstance().ID_CHANGE_TIME_GPS = ++maxId;
			flg = new Flag(GestionnaireFlags.getInstance().ID_CHANGE_TIME_GPS, "T", "EvChgHeureGPS"); //$NON-NLS-1$ //$NON-NLS-2$
			GestionnaireFlags.getInstance().addFlag(flg);
			this.flagsEvents.put(flg.getEventName(), flg);
		}
		if (GestionnaireFlags.getInstance().ID_CHANGE_TIME_TOM == -1) {
			//change time TOM event
			//SamngLogger.getLogger().error(Messages.getString("ParseurFlags.5")); //$NON-NLS-1$
			GestionnaireFlags.getInstance().ID_CHANGE_TIME_TOM = ++maxId;
			flg = new Flag(GestionnaireFlags.getInstance().ID_CHANGE_TIME_TOM, "T", "EvChgHeureTOM"); //$NON-NLS-1$ //$NON-NLS-2$
			GestionnaireFlags.getInstance().addFlag(flg);
			this.flagsEvents.put(flg.getEventName(), flg);
		}
		if (GestionnaireFlags.getInstance().ID_CHANGE_TIME_MNT == -1) {
			//change time MNT event
			//SamngLogger.getLogger().error(Messages.getString("ParseurFlags.8")); //$NON-NLS-1$
			GestionnaireFlags.getInstance().ID_CHANGE_TIME_MNT = ++maxId;
			flg = new Flag(GestionnaireFlags.getInstance().ID_CHANGE_TIME_MNT, "T", "EvChgHeureMNT"); //$NON-NLS-1$ //$NON-NLS-2$
			GestionnaireFlags.getInstance().addFlag(flg);
			this.flagsEvents.put(flg.getEventName(), flg);
		}
		if (GestionnaireFlags.getInstance().ID_TC== -1) {
			//change time MNT event
			GestionnaireFlags.getInstance().ID_TC = ++maxId;
			flg = new Flag(GestionnaireFlags.getInstance().ID_TC, "TC", "RuptureTempsCalculee"); //$NON-NLS-1$ //$NON-NLS-2$
			GestionnaireFlags.getInstance().addFlag(flg);
			this.flagsEvents.put(flg.getEventName(), flg);
		}if (GestionnaireFlags.getInstance().ID_DC== -1) {
			//change time MNT event
			GestionnaireFlags.getInstance().ID_DC = ++maxId;
			flg = new Flag(GestionnaireFlags.getInstance().ID_DC, "DC", "RuptureDistanceCalculee"); //$NON-NLS-1$ //$NON-NLS-2$
			GestionnaireFlags.getInstance().addFlag(flg);
			this.flagsEvents.put(flg.getEventName(), flg);
		}
	}

	public static void main(String[] args) {
		ParseurFlags parser = new ParseurFlags();
		//TAG REP KO
		//		parser.parseRessource("ressources\\flags\\flags.xml");
		parser.parseRessource(RepertoiresAdresses.FLAGS_FILE_DIR+"flags.xml",false,0,-1);
		// HashMap<String, Flag> flags = parser.chargerFlags();
//		for (Flag f : flags.values()) {
//
//		}
	}
}