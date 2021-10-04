package com.faiveley.samng.principal.sm.data.variableComposant;

import java.util.ArrayList;

public class VariableComplexe extends VariableComposite {
	
	/**
	 * Vérifie la taille d'une variable complexe par rapport aux total des tailles des sous variables
	 * 
	 * @return
	 */
	public boolean verifierTailleVariable(){
		int totalVariableSize=0;
		
		int nbVar = this.getVariableCount();
		
		//cas des sous variables de type 2 : on ne doit pas faire de verification des tailles
		// clause CL_J_6
		if(nbVar>0 && this.getEnfant(0).getDescriptor().getOffsetComposant()!=null) {
				return true;
		}
		
		for(int k=0; k<nbVar;k++){
			AVariableComposant v = this.getEnfant(k);
			totalVariableSize += v.getDescriptor().getTailleBits();
		}
		
		if (totalVariableSize != this.getDescriptor().getTailleBits())
			return false;
		else
			return true;	
	}
	
	
	public VariableComplexe copy () {
		VariableComplexe var = new VariableComplexe();
		copyTo(var);
		if (this.m_AVariableComposant != null) {
			var.m_AVariableComposant = new ArrayList<AVariableComposant>(this.m_AVariableComposant.size());
			for (AVariableComposant varC : this.m_AVariableComposant) {
				var.m_AVariableComposant.add(varC.copy());
			}
		}
		
		return var;
	}
	
}
