package com.faiveley.samng.principal.sm.filtres.gestionnaires;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.parseurs.ParseurFiltreListe;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;


/**
 * @author Graton Olivier
 * @version 1.0
 * @created 23-nov.-2007 15:24:36
 */
public class GestionnaireFiltresListe extends AGestionnaireFiltres {
	
	public ParseurFiltreListe filtreListeParseur = new ParseurFiltreListe();
	
	public GestionnaireFiltresListe() {
		filtreListeParseur.parseRessource(RepertoiresAdresses.getFiltres_ListesXML(),false,0,-1);
		this.listeFiltres = filtreListeParseur.chargerFiltres();
	}

	public void finalize() throws Throwable {
		super.finalize();
	}
	
	@Override
	public AFiltreComposant initialiserFiltreDefaut() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 
	 * @param filtre
	 */
	public void ajouterFiltre(AFiltreComposant filtre){
		super.ajouterFiltre(filtre);
		this.filtreListeParseur.enregistrerFiltre(filtre);
		//: here save the filter into the XML
	}

	/**
	 * 
	 * @param baseFiltre
	 * @param listeFiltre
	 */
	public AFiltreComposant majListeEvenements(AFiltreComposant baseFiltre, AFiltreComposant listeFiltre){
		return null;
	}

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant supprimerFiltre(int indice) {
		AFiltreComposant filtre = getFiltre(indice);
		super.supprimerFiltre(indice);
		filtreListeParseur.effacerFiltre(filtre);		
		return filtre;
	}
	
	public AFiltreComposant supprimerFiltre(String nom){
		AFiltreComposant filtre = super.supprimerFiltre(nom);
		this.filtreListeParseur.effacerFiltre(filtre);
		OrdonnerFiltre.getInstance().getListeFiltreListe().remove(filtre) ;
		return filtre;
	}
}