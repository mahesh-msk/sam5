package com.faiveley.samng.vuetabulaire.sm.filtres;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class NommeeFiltreComposant {
	private String userName = "";
	private AFiltreComposant filtre;
	private AVariableComposant parent;
	
	public NommeeFiltreComposant() {
		
	}

	public NommeeFiltreComposant(String userName, AFiltreComposant filtre) {
		this.userName = userName;
		this.filtre = filtre;
	}

	public String getNomUtilisateur() {
		return userName;
	}

	public void setNomUtilisateur(String userName) {
		this.userName = userName;
	}

	public AFiltreComposant getFiltre() {
		return filtre;
	}

	public void setFilter(AFiltreComposant filtre) {
		this.filtre = filtre;
	}
	
	public AVariableComposant getParent() {
		return parent;
	}

	public void setParent(AVariableComposant parent) {
		this.parent = parent;
	}

	public boolean equals(Object filtreObj) {
		if(filtre == null)
			return false;
		if(this == filtreObj)
			return true;
		if(!(filtreObj instanceof NommeeFiltreComposant))
			return false;
		NommeeFiltreComposant filtre = (NommeeFiltreComposant)filtreObj;
		if(!this.userName.equals(filtre.userName))
			return false;
		
		try {
			for(int i = 0; i<2; i++) {
				if(!this.filtre.getEnfant(i).getNom().equals(filtre.filtre.getEnfant(i).getNom()))
					return false;
			}
		}catch (Exception e) {
			return false;
		}

		return true;
	}
}
