package com.faiveley.samng.principal.sm.data.tableAssociationComposant;
import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:14
 */
public abstract class AParcoursComposant {
	
	protected List<ADescripteurComposant> m_ADescripteurComposant;
	protected List<AParcoursComposant> m_AParcoursComposant;
	protected Enregistrement m_Enregistrement;
	
	public AParcoursComposant(){
	}

	/**
	 * 
	 * @param parcComp
	 */
	public void ajouter(AParcoursComposant parcComp){
		if (this.m_AParcoursComposant == null) {
			this.m_AParcoursComposant = new ArrayList<AParcoursComposant>(1);
		}
		this.m_AParcoursComposant.add(parcComp);
	}

	/**
	 * 
	 * @param indice
	 */
	public AParcoursComposant getEnfant(int indice){
		return this.m_AParcoursComposant != null ?
				this.m_AParcoursComposant.get(indice) : null;
	}

	/**
	 * 
	 * @param parcComp
	 */
	public void supprimer(AParcoursComposant parcComp){
		if (this.m_AParcoursComposant != null) {
			this.m_AParcoursComposant.remove(parcComp);
		}
	}

	/**
	 * @param descr
	 * @return
	 */
	public boolean ajouterDescripteur(ADescripteurComposant descr) {
		if (this.m_ADescripteurComposant == null) {
			this.m_ADescripteurComposant = new ArrayList<ADescripteurComposant>(1);
		}
		return this.m_ADescripteurComposant.add(descr);
	}

	/**
	 * @param indice
	 * @return
	 */
	public ADescripteurComposant getDescripteur(int indice) {
		return this.m_ADescripteurComposant != null ? 
				this.m_ADescripteurComposant.get(indice) : null;
	}

	/**
	 * @param descr
	 * @return
	 */
	public boolean supprimerDescripteur(ADescripteurComposant descr) {
		return this.m_ADescripteurComposant != null ? 
				this.m_ADescripteurComposant.remove(descr) : false;
	}
	
	public void setEnregistrement(Enregistrement enr) {
		m_Enregistrement = enr;
	}
	
	public Enregistrement getEnregistrement() {
		return m_Enregistrement;
	}
}