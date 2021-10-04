/**
 * 
 */
package com.faiveley.samng.principal.sm.calculs;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;

/**
 * @author meggy
 *
 */
public interface ICalculRuptures {

	 public boolean calculRuptureTemps(AVariableComposant var1, AVariableComposant var2);
	 
	 public boolean calculRuptureDistance(AVariableComposant var1, AVariableComposant var2);
}
