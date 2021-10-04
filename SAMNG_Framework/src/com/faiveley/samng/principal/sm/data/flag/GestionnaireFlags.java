package com.faiveley.samng.principal.sm.data.flag;

import java.util.HashMap;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireFlagsExplorer;

public class GestionnaireFlags {
	
	private static GestionnaireFlags instance = new GestionnaireFlags();

	public static GestionnaireFlags getInstance(){
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireFlagsExplorer.getInstance();
		}
		return instance;
	}
	
	public int ID_START = -1;
	
	public int ID_STOP = -1;
	
	public int ID_CHANGE_TIME_GPS = -1;
	public int ID_CHANGE_TIME_TOM = -1;
	public int ID_CHANGE_TIME_MNT = -1;
	public int ID_CHANGE_DAY = -1;
	public int ID_TC = -1;
	public int ID_DC = -1;
	
	private HashMap<Integer, Flag> flags;
	
	protected GestionnaireFlags() {
	}
	
	public void addFlag(Flag flag) {
		if (flags == null) {
			flags = new HashMap<Integer, Flag>();
		}
		flags.put(Integer.valueOf(flag.getId()), flag);
		
	}
	
	public Flag getFlag(int id) {
		return flags != null ? flags.get(Integer.valueOf(id)) : null;
	}
	
	public void clear() {
		if (flags != null) {
			flags.clear();
		}
		ID_START = -1;
		ID_STOP = -1;
		ID_CHANGE_TIME_GPS = -1;
		ID_CHANGE_TIME_TOM = -1;
		ID_CHANGE_TIME_MNT = -1;
		ID_CHANGE_DAY = -1;
		ID_TC = -1;
		ID_DC = -1;
	}
}
