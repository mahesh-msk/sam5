package com.faiveley.samng.principal.sm.data.identificateurComposant;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:06
 */
public class IdentificateurEvenement extends AIdentificateurComposant {

	protected List<AIdentificateurComposant> m_IdentificateurVariable;

	public IdentificateurEvenement(){
	}

	/**
	 * @param idComp
	 * @return
	 */
	public boolean ajouter(AIdentificateurComposant idComp) {
		if (this.m_IdentificateurVariable == null) {
			this.m_IdentificateurVariable = new ArrayList<AIdentificateurComposant>(1);
		}
		return this.m_IdentificateurVariable.add(idComp);
	}

	/**
	 * @param indice
	 * @return
	 */
	public AIdentificateurComposant getEnfant(int indice) {
		return this.m_IdentificateurVariable != null ? 
				this.m_IdentificateurVariable.get(indice) : null;
	}

	/**
	 * @param idComp
	 * @return
	 */
	public boolean supprimer(AIdentificateurComposant idComp) {
		return this.m_IdentificateurVariable != null ? 
				this.m_IdentificateurVariable.remove(idComp) : false;
	}
	
	

}