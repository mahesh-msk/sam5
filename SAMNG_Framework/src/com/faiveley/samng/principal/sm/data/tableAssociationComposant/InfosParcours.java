package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

import java.util.List;
/**
 * Classe contenant toutes les informations parcours à utiliser
 * @author Olivier
 *
 */
public class InfosParcours extends AParcoursComposant {

	List<String> listeInformations;

	public InfosParcours() {
		super();
	}

	public InfosParcours(List<String> listeInformations) {
		super();
		this.listeInformations = listeInformations;
	}
	
	public void clear(){
		if (listeInformations!=null) {
			listeInformations.clear();
		}
		
	}
	/**
	 * Nom de l'information à rechercher
	 * @param nomInfo
	 * @return
	 */
	public String getInformation(String nomInfo){
		boolean trouve = false;
		int i=0;
		String information = null;
		while((!trouve) && (i<listeInformations.size())){
			if(listeInformations.get(i).equals(nomInfo)){
				information = listeInformations.get(i);
				trouve = true;
			}
			i++;
		}
		return information;
	}

	public List<String> getListeInformations() {
		return listeInformations;
	}

	public void setListeInformations(List<String> listeInformations) {
		this.listeInformations = listeInformations;
	}
	
	
	
}
