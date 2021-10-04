package com.faiveley.samng.principal.sm.data.descripteur;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:44
 */
public class DescripteurTableAssociation extends ADescripteurComposant {
	
	private short tailleBloc;
	/**
	 * taille du CRC de la table sur 1 ou 2 octets
	 */
	private int tailleCRC;

	public DescripteurTableAssociation(){

	}

	public short getTailleBloc(){
		return this.tailleBloc;
	}

	/**
	 * taille du CRC de la table sur 1 ou 2 octets
	 */
	public int getTailleCRC(){
		return this.tailleCRC;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setTailleBloc(short newVal){
		this.tailleBloc = newVal;
	}

	/**
	 * taille du CRC de la table sur 1 ou 2 octets
	 * 
	 * @param newVal
	 */
	public void setTailleCRC(int newVal){
		this.tailleCRC = newVal;
	}

//	map <event id, List<variable descriptor>>
	private HashMap<Integer, List<DescripteurVariable>> eventVars = new HashMap<Integer, List<DescripteurVariable>>();
	
	/**
	 * Add a new VarableDescriptor of a variable associated to the event
	 * @param varDesc		descriptor
	 * @param eventId		id of the event
	 * @return				true if the descriptor was not already added, false otherwise
	 */
	public boolean addVarToEvent( DescripteurVariable varDesc, int eventId) {
		List<DescripteurVariable> descriptors = null;
		if (this.eventVars.containsKey(eventId)) {
			descriptors = this.eventVars.get(eventId);
			
		} else {
			descriptors = new ArrayList<DescripteurVariable>(0);
			this.eventVars.put(eventId, descriptors);
		}
		
		return descriptors.add(varDesc);
	}
	
	
	public List<DescripteurVariable> getVariableDescriptors(int eventId) {
		return this.eventVars.get(eventId);
	}
	
}