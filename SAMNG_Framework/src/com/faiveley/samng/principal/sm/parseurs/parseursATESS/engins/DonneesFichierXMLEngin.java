package com.faiveley.samng.principal.sm.parseurs.parseursATESS.engins;

import java.util.ArrayList;
import java.util.List;

public class DonneesFichierXMLEngin {
	private int selection_type_engin;
	private List<TypeEngin> listeTypeEngins=new ArrayList<TypeEngin>(0);
	
	public DonneesFichierXMLEngin() {
		this.selection_type_engin=-1;
		this.listeTypeEngins.clear();
	}
	
	public int getSelection_type_engin() {
		return selection_type_engin;
	}
	public void setSelection_type_engin(int selection_type_engin) {
		this.selection_type_engin = selection_type_engin;
	}
	public List<TypeEngin> getListeTypeEngins() {
		return listeTypeEngins;
	}
	public void setListeTypeEngins(List<TypeEngin> listeTypeEngins) {
		this.listeTypeEngins = listeTypeEngins;
	}
	
	public String getFichierXMLByTypeEngin(String typeEngin){
		for (TypeEngin typeE : this.listeTypeEngins) {
			if (typeE.getNom().toUpperCase().equals(typeEngin.toUpperCase())) {
				return typeE.getNomFichierXML();
			}
		}
		return null;
	}
	
	public String getFichierXMLByLibelle(String libelle){
		for (TypeEngin typeE : this.listeTypeEngins) {
			if (typeE.getLibelle().equals(libelle)) {
				return typeE.getNomFichierXML();
			}
		}
		return null;
	}
	
	public String getTypeEnginByLibelle(String libelle){
		for (TypeEngin typeE : this.listeTypeEngins) {
			if (typeE.getLibelle().equals(libelle)) {
				return typeE.getNom();
			}
		}
		return null;
	}
	
	public String[] getListLibelles(){
		String[] list=new String[this.listeTypeEngins.size()];
		int indice=0;
		for (TypeEngin typeE : this.listeTypeEngins) {
			if (typeE.getLibelle()!=null) {
				list[indice]=typeE.getLibelle();
			}
			indice++;
		}
		return list;
	}
}
