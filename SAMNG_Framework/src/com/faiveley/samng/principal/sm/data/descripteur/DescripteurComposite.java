package com.faiveley.samng.principal.sm.data.descripteur;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:41
 */
public class DescripteurComposite extends ADescripteurComposant {

	private List<ADescripteurComposant> m_ADescripteurComposant;

	public DescripteurComposite(){
		
	}

	/**
	 * 
	 * @param composant
	 */
	public void ajouter(ADescripteurComposant composant){
		if (this.m_ADescripteurComposant == null) {
			this.m_ADescripteurComposant = new ArrayList<ADescripteurComposant>(1);
		}
		this.m_ADescripteurComposant.add(composant);
	}

	/**
	 * 
	 * @param indice
	 */
	public ADescripteurComposant getEnfant(int indice){
		return this.m_ADescripteurComposant != null ? 
				this.m_ADescripteurComposant.get(indice) : null;
	}

	/**
	 * 
	 * @param composant
	 */
	public void supprimer(ADescripteurComposant composant){
		if (this.m_ADescripteurComposant != null) {
			this.m_ADescripteurComposant.remove(composant);
		}
	}
	
	/**
	 * @return
	 */
	public int getLength() {
		return this.m_ADescripteurComposant.size();
	}
}