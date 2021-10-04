package com.faiveley.samng.vuegraphique.sm.filtres;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class NommeeFiltreComposant {
	private String userName = "";
	private AFiltreComposant filtre;
	
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
		for(int i = 0; i<2; i++) {
			if(!this.filtre.getEnfant(i).getNom().equals(filtre.filtre.getEnfant(i).getNom()))
				return false;
		}
		return true;
	}
}
