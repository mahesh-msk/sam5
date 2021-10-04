package com.faiveley.samng.principal.ihm.vues;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;

public class VariableExplorationUtils {

	public static String[] getFilteredValuesFromMessage(Message message, String nom, Map<String, AFiltreComposant> varNamesFilters) {
		String[] allValues = VariableExplorationUtils.getValuesFromMessage(
				message, nom);
		if (varNamesFilters != null && varNamesFilters.size() > 0) {
			List<String> filteredValues = new ArrayList<String>();
			for (String value : allValues) {
				boolean matchesFilter = FilterUtils.matchesFilters(value,
						varNamesFilters);
				if (matchesFilter) {
					filteredValues.add(value);
				}
			}
			return filteredValues.toArray(new String[filteredValues.size()]);
		} else {
			return allValues;
		}
	}
	
	public static String[] getValuesFromMessage(Message message, String nom) {
		List<String> values = new ArrayList<String>();
		
		List<VariableDiscrete> discreteVariables = message.getVariablesDiscrete();
		for (VariableDiscrete var : discreteVariables) {
			if (isSearchedVariable(var, nom)) {
				String value = var.toString();
				values.add(value);
			}
		}
		
		List<VariableAnalogique> analogicVariables = message.getVariablesAnalogique();
		for (VariableAnalogique var : analogicVariables) {
			if (isSearchedVariable(var, nom)) {
				String value = var.toString();
				values.add(value);
			}
		}
		
		List<VariableComplexe> complexVariables = message.getVariablesComplexe();
		for (VariableComplexe var : complexVariables) {
			exploreVariable(var, nom, values);
		}
		
		List<StructureDynamique> dynamicStructures = message.getStructuresDynamique();
		for (StructureDynamique struct : dynamicStructures) {
			exploreVariable(struct, nom, values);
		}
		
		List<TableauDynamique> dynamicTables = message.getTableauxDynamique();
		for (TableauDynamique table : dynamicTables) {
			exploreVariable(table, nom, values);
		}
		
		return values.toArray(new String[values.size()]);
	}
	
	public static List<Object> getCastedValuesFromMessage(Message message, DescripteurVariable descVar) {
		List<Object> values = new ArrayList<Object>();
		
		List<VariableDiscrete> discreteVariables = message.getVariablesDiscrete();
		for (VariableDiscrete var : discreteVariables) {
			if (isSearchedVariable(var, descVar)) {
				Object value = var.getCastedValeur();
				values.add(value);
			}
		}
		
		List<VariableAnalogique> analogicVariables = message.getVariablesAnalogique();
		for (VariableAnalogique var : analogicVariables) {
			if (isSearchedVariable(var, descVar)) {
				Object value = var.getCastedValeur();
				values.add(value);
			}
		}
		
		List<VariableDiscrete> virtualVariables = message.getVariablesVirtuelle();
		for (VariableDiscrete var : virtualVariables) {
			if (isSearchedVariable(var, descVar)) {
				Object value = var.getCastedValeur();
				values.add(value);
			}
		}
		
		List<VariableComposite> compositeVariables = message.getVariablesComposee();
		for (VariableComposite var : compositeVariables) {
			exploreVariable(var, descVar, values);
		}
		
		List<VariableComplexe> complexVariables = message.getVariablesComplexe();
		for (VariableComplexe var : complexVariables) {
			exploreVariable(var, descVar, values);
		}
		
		List<StructureDynamique> dynamicStructures = message.getStructuresDynamique();
		for (StructureDynamique struct : dynamicStructures) {
			exploreVariable(struct, descVar, values);
		}
		
		List<TableauDynamique> dynamicTables = message.getTableauxDynamique();
		for (TableauDynamique table : dynamicTables) {
			exploreVariable(table, descVar, values);
		}
		
		List<ChaineDynamique> dynamicChains = message.getChainesDynamique();
		for (ChaineDynamique chain : dynamicChains) {
			exploreVariable(chain, descVar, values);
		}
		
		return values;
	}
	
	public static AVariableComposant getVariable(DescripteurVariable descVar, Message message) {
		List<VariableDiscrete> discreteVariables = message.getVariablesDiscrete();
		for (VariableDiscrete var : discreteVariables) {
			if (isSearchedVariable(var, descVar)) {
				return var;
			}
		}
		
		List<VariableAnalogique> analogicVariables = message.getVariablesAnalogique();
		for (VariableAnalogique var : analogicVariables) {
			if (isSearchedVariable(var, descVar)) {
				return var;
			}
		}
		
		List<VariableDiscrete> virtualVariables = message.getVariablesVirtuelle();
		for (VariableDiscrete var : virtualVariables) {
			if (isSearchedVariable(var, descVar)) {
				return var;
			}
		}
		
		List<VariableComposite> compositeVariables = message.getVariablesComposee();
		for (VariableComposite var : compositeVariables) {
			AVariableComposant result = exploreVariable(var, descVar);
			if (result != null) {
				return result;
			}
		}
		
		List<VariableComplexe> complexVariables = message.getVariablesComplexe();
		for (VariableComplexe var : complexVariables) {
			AVariableComposant result = exploreVariable(var, descVar);
			if (result != null) {
				return result;
			}
		}
		
		List<StructureDynamique> dynamicStructures = message.getStructuresDynamique();
		for (StructureDynamique struct : dynamicStructures) {
			AVariableComposant result = exploreVariable(struct, descVar);
			if (result != null) {
				return result;
			}
		}
		
		List<TableauDynamique> dynamicTables = message.getTableauxDynamique();
		for (TableauDynamique table : dynamicTables) {
			AVariableComposant result = exploreVariable(table, descVar);
			if (result != null) {
				return result;
			}
		}
		
		List<ChaineDynamique> dynamicChains = message.getChainesDynamique();
		for (ChaineDynamique chain : dynamicChains) {
			AVariableComposant result = exploreVariable(chain, descVar);
			if (result != null) {
				return result;
			}
		}
		
		return null;
	}
	
	private static void exploreVariable(AVariableComposant var, String nom, List<String> values) {
		if (isSearchedVariable(var, nom)) {
			String value = var.toString();
			values.add(value);
		}
		if (var instanceof VariableComplexe) {
			VariableComplexe cVar = (VariableComplexe) var;
			exploreEnfants(cVar.getEnfants(), nom, values);
		} else if (var instanceof StructureDynamique) {
			StructureDynamique struct = (StructureDynamique) var;
			exploreVariable(struct.getVariableEntete(), nom, values);
			exploreEnfants(struct.getEnfants(), nom, values);
		} else if (var instanceof TableauDynamique) {
			TableauDynamique table = (TableauDynamique) var;
			exploreVariable(table.getVariableEntete(), nom, values);
			exploreEnfants(table.getEnfants(), nom, values);
		} else if (var instanceof Paquets) {
			Paquets paquets = (Paquets) var;
			exploreVariable(paquets.getVariableEntete(), nom, values);
			exploreEnfants(paquets.getEnfants(), nom, values);
		}
	}
	
	private static void exploreEnfants(AVariableComposant[] vars, String nom, List<String> values) {
		if (vars != null) {
			for (AVariableComposant varEnfant : vars) {
				exploreVariable(varEnfant, nom, values);
			}
		}
	}
	
	private static void exploreVariable(AVariableComposant var, DescripteurVariable descVar, List<Object> values) {
		if (isSearchedVariable(var, descVar)) {
			Object value = var.getCastedValeur();
			values.add(value);
		}
		
		if (var instanceof VariableComplexe) {
			VariableComplexe cVar = (VariableComplexe) var;
			exploreEnfants(cVar.getEnfants(), descVar, values);
		} else if (var instanceof VariableComposite) {
			VariableComposite cVar = (VariableComposite) var;
			exploreEnfants(cVar.getEnfants(), descVar, values);
		} else if (var instanceof StructureDynamique) {
			StructureDynamique struct = (StructureDynamique) var;
			exploreVariable(struct.getVariableEntete(), descVar, values);
			exploreEnfants(struct.getEnfants(), descVar, values);
		} else if (var instanceof TableauDynamique) {
			TableauDynamique table = (TableauDynamique) var;
			exploreVariable(table.getVariableEntete(), descVar, values);
			exploreEnfants(table.getEnfants(), descVar, values);
		} else if (var instanceof Paquets) {
			Paquets paquets = (Paquets) var;
			exploreVariable(paquets.getVariableEntete(), descVar, values);
			exploreEnfants(paquets.getEnfants(), descVar, values);
		} else if (var instanceof ChaineDynamique) {
			ChaineDynamique chaine = (ChaineDynamique) var;
			exploreVariable(chaine.getVariableEntete(), descVar, values);
			exploreEnfants(chaine.getEnfants(), descVar, values);
		}
	}
	
	private static void exploreEnfants(AVariableComposant[] vars, DescripteurVariable descVar, List<Object> values) {
		if (vars != null) {
			for (AVariableComposant varEnfant : vars) {
				exploreVariable(varEnfant, descVar, values);
			}
		}
	}
	
	private static AVariableComposant exploreVariable(AVariableComposant var, DescripteurVariable descVar) {
		if (isSearchedVariable(var, descVar)) {
			return var;
		}
		
		if (var instanceof VariableComplexe) {
			VariableComplexe cVar = (VariableComplexe) var;
			return exploreEnfants(cVar.getEnfants(), descVar);
		} else if (var instanceof StructureDynamique) {
			StructureDynamique struct = (StructureDynamique) var;
			AVariableComposant result = exploreVariable(struct.getVariableEntete(), descVar);
			if (result != null) {
				return result;
			}
			return exploreEnfants(struct.getEnfants(), descVar);
		} else if (var instanceof TableauDynamique) {
			TableauDynamique table = (TableauDynamique) var;
			AVariableComposant result = exploreVariable(table.getVariableEntete(), descVar);
			if (result != null) {
				return result;
			}
			return exploreEnfants(table.getEnfants(), descVar);
		} else if (var instanceof Paquets) {
			Paquets paquets = (Paquets) var;
			AVariableComposant result = exploreVariable(paquets.getVariableEntete(), descVar);
			if (result != null) {
				return result;
			}
			return exploreEnfants(paquets.getEnfants(), descVar);
		} else if (var instanceof ChaineDynamique) {
			ChaineDynamique chaine = (ChaineDynamique) var;
			AVariableComposant result = exploreVariable(chaine.getVariableEntete(), descVar);
			if (result != null) {
				return result;
			}
			return exploreEnfants(chaine.getEnfants(), descVar);
		}
		
		return null;
	}
	
	private static AVariableComposant exploreEnfants(AVariableComposant[] vars, DescripteurVariable descVar) {
		if (vars != null) {
			for (AVariableComposant varEnfant : vars) {
				AVariableComposant result = exploreVariable(varEnfant, descVar);
				if (result != null) {
					return result;
				}
			}
		}
		
		return null;
	}
	
	private static boolean isSearchedVariable(AVariableComposant var, String nom) {
		DescripteurVariable descVar = var.getDescriptor();
		return descVar.getM_AIdentificateurComposant() != null && nom.equals(descVar.getM_AIdentificateurComposant().getNom());
	}
	
	private static boolean isSearchedVariable(AVariableComposant var, DescripteurVariable descVar) {
		String descVarName = descVar.getM_AIdentificateurComposant().getNom();
		String varName = var.getDescriptor().getM_AIdentificateurComposant().getNom();
		return descVarName.equals(varName);
	}
}
