package com.faiveley.samng.principal.sm.data.variableComposant.JRU;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;



/**
 * @author olive
 * @version 1.0
 * @created 02-nov.-2010 16:43:04
 */
public class StructureDynamique extends VariableDynamique {

	public StructureDynamique(){

	}

	/**
	 * Retourne une instance de TableSousVariable par sa valeur(attribut valeur xml)
	 * cette instance ne contient que la définition des sous-variable, pas leur valeur
	 * 
	 * @param valeur
	 */
	public TableSousVariable getTableSousVariableReferenceByValeur(java.lang.String valeur){
		TableSousVariable tableSousVarRetour = null;
		try{
			List<AVariableComposant> listeTableSousVar = getListeTablesSousVariable();
			int i =0;
			boolean trouve = false;

			while(!trouve && i<listeTableSousVar.size()){
				TableSousVariable tableSousVar = (TableSousVariable) listeTableSousVar.get(i);
				if(tableSousVar.getValeur().equals(valeur)){
					trouve = true;
					tableSousVarRetour = tableSousVar;
				}
				i++;

			}
		}
		catch(Exception ex){

		}

		return tableSousVarRetour;
	}

	/**
	 * Retourne une instance de TableSousVariable correpondante à la
	 * TableSousVariableValorisée
	 */
	public TableSousVariable getTableSousVariableValorisee(){
		return (TableSousVariable) this.m_AVariableComposant.get(0);
	}

	@Override
	public StructureDynamique copy(){
		StructureDynamique structureDyn = new StructureDynamique();
		structureDyn.setDescripteur(this.getDescriptor());
		structureDyn.setVariableEntete(this.getVariableEntete());
		if (this.m_AVariableComposant != null) {
			structureDyn.m_AVariableComposant = new ArrayList<AVariableComposant>(this.m_AVariableComposant.size());
			for (AVariableComposant varC : this.m_AVariableComposant) {
				structureDyn.m_AVariableComposant.add(varC.copy());
			}
		}
		return structureDyn;
	}

	@Override
	public AVariableComposant[] getEnfants() {
		AVariableComposant[] tableauSousVarStrucureDyn = null;
		if(this.m_AVariableComposant!=null){
			TableSousVariable tableSousVariable = (TableSousVariable) this.m_AVariableComposant.get(0);
			List<AVariableComposant> listeSousVariable = tableSousVariable.getM_AVariableComposant();
			tableauSousVarStrucureDyn = new AVariableComposant[tableSousVariable.getNbSousVariables()];
			//tableauSousVarStrucureDyn[0] = this.variableEntete;
			int i=0;
			for (AVariableComposant variable : listeSousVariable) {
				if(variable instanceof Paquets ){
					if(((Paquets)variable).m_AVariableComposant !=null){
						tableauSousVarStrucureDyn[i] = variable;
					}
				}
				else
					tableauSousVarStrucureDyn[i] = variable;

				i++;
			}
		}

		return tableauSousVarStrucureDyn;
	}




	//	public StructureDynamique copierSansTableSousVar(){
	//		StructureDynamique structureDyn = new StructureDynamique();
	//		structureDyn.setDescripteur(this.getDescriptor());
	//		structureDyn.setVariableEntete(this.variableEntete);
	//		return structureDyn;
	//	}

}