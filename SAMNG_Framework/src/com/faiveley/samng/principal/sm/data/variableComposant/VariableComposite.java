package com.faiveley.samng.principal.sm.data.variableComposant;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.descripteur.Type;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:47
 */
public class VariableComposite extends AVariableComposant {

	public List<AVariableComposant> m_AVariableComposant;

	public VariableComposite(){
		
	}

	/**
	 * 
	 * @param variable
	 */
	public void ajouter(AVariableComposant variable){
		if (this.m_AVariableComposant == null) {
			this.m_AVariableComposant = new ArrayList<AVariableComposant>(1);
		}
			
		this.m_AVariableComposant.add(variable);
	}

	/**
	 * 
	 * @param enfant
	 */
	public AVariableComposant getEnfant(int enfant){
		return this.m_AVariableComposant != null ? 
				this.m_AVariableComposant.get(enfant) : null;
	}
	
	public Object[] getEnfantsWithoutReserved(){
		int nbvar=0;
		ArrayList<AVariableComposant> vars;
		if (this instanceof VariableComplexe) {
			nbvar=this.m_AVariableComposant.size();
			vars=new ArrayList<AVariableComposant>();
			for (int i = 0; i < nbvar; i++) {
				if (this.getEnfant(i).getTypeValeur()!=Type.reserved) {
					vars.add(this.getEnfant(i));
				}
			}
			return vars.toArray();
		}
		return null;		
	}
	
	public AVariableComposant[] getEnfants(){
		int nbvar=0;
		AVariableComposant[] vars;
		if (this instanceof VariableComplexe) {
			nbvar=this.m_AVariableComposant.size();
			vars=new AVariableComposant[nbvar];
			for (int i = 0; i < nbvar; i++) {
				vars[i] = this.getEnfant(i);
			}
			return vars;
		}
		return null;		
	}

	/**
	 * 
	 * @param variable
	 */
	public void supprimer(AVariableComposant variable){
		if (this.m_AVariableComposant != null) {
			this.m_AVariableComposant.remove(variable);
		}
	}
	
	/**
	 * @return the number of variables 
	 */
	public int getVariableCount() {
		return this.m_AVariableComposant != null ? 
				this.m_AVariableComposant.size() : 0;
	}

	public VariableComposite copy () {
		VariableComposite var = new VariableComposite();
		copyTo(var);
		if (this.m_AVariableComposant != null) {
			var.m_AVariableComposant = new ArrayList<AVariableComposant>(this.m_AVariableComposant.size());
			for (AVariableComposant varC : this.m_AVariableComposant) {
				var.m_AVariableComposant.add(varC.copy());
			}
		}
		return var;
	}

	public List<AVariableComposant> getM_AVariableComposant() {
		return m_AVariableComposant;
	}

	public void setM_AVariableComposant(List<AVariableComposant> variableComposant) {
		m_AVariableComposant = variableComposant;
	}
}