package com.faiveley.samng.principal.sm.data.enregistrement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class GestionnaireVariablesComposee {

	/**
	 * Returns a list of variables composee if the current message contains
	 * variables that are contained in the definition of one or more defined
	 * composee variables from XML file
	 * 
	 * @param msg
	 * @return
	 */
	public static List<AVariableComposant> checkForVariablesComposee(Message msg) {
		Map<String, AVariableComposant> defVarComposee = GestionnairePool.getInstance()
				.getComposeeVariables();
		ArrayList<AVariableComposant> retList = new ArrayList<AVariableComposant>();
		
		for (AVariableComposant var : defVarComposee.values()) {
			VariableComposite compVar = (VariableComposite) var;
			int childs = compVar.getVariableCount();
			boolean addVar = false;
			for (int i = 0; i < childs; i++) {
				AVariableComposant subVar = compVar.getEnfant(i);
				if (msg.getVariable(subVar.getDescriptor()) != null) {
					addVar = true;
					break;
				}
			}
			if (addVar) {
				retList.add(var);
			}
		}
		retList.trimToSize();
		
		return retList;
	}

	public static boolean isVariableComposeeRenseignee(AVariableComposant var) {
		List<Message> listeMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
		boolean trouve = false;
		int i = 0;
		Message message = null;
		
		VariableComposite compVar = (VariableComposite) var;
		int childs = 0;
		AVariableComposant subVar = null;
		while (i < listeMessages.size() && !trouve) {
			message = listeMessages.get(i);			
			childs = compVar.getVariableCount();
			int j = 0;
			while (j < childs && !trouve) {
				subVar = compVar.getEnfant(j);
				if (message.getVariable(subVar.getDescriptor()) != null) {
					trouve = true;
				}

				j++;
			}

			i++;
		}
		return trouve;
	}
}
