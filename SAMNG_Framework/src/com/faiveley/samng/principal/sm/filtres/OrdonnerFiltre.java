package com.faiveley.samng.principal.sm.filtres;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.filtres.OrdonnerFiltreExplorer;

public class OrdonnerFiltre {

	private static OrdonnerFiltre instance;
	
	private List<AFiltreComposant> listeFiltreTabulaire = new ArrayList<AFiltreComposant>(0);
	private List<AFiltreComposant> listeFiltreGraphique = new ArrayList<AFiltreComposant>(0);
	private List<AFiltreComposant> listeFiltreListe = new ArrayList<AFiltreComposant>(0);
	
	private AFiltreComposant filtreSelectTabulaire;
	private AFiltreComposant filtreSelectGraphique;
	private AFiltreComposant filtreSelectListe;
	
	protected OrdonnerFiltre(){
		
	}
	
	public static OrdonnerFiltre getInstance(){
		if(instance == null){
			instance = new OrdonnerFiltre();
			if (ActivationExplorer.getInstance().isActif()) {
				instance = new OrdonnerFiltreExplorer();
			}
		}
		if (ActivationExplorer.getInstance().isActif()) {
			OrdonnerFiltreExplorer.getInstance();
		}
		return instance;
	}

	public List<AFiltreComposant> getListeFiltreTabulaire() {
		return listeFiltreTabulaire;
	}

	public void setListeFiltreTabulaire(List<AFiltreComposant> listeFiltreTabulaire) {
		this.listeFiltreTabulaire = listeFiltreTabulaire;
	}

	public List<AFiltreComposant> getListeFiltreGraphique() {
		return listeFiltreGraphique;
	}

	public void setListeFiltreGraphique(List<AFiltreComposant> listeFiltreGraphique) {
		this.listeFiltreGraphique = listeFiltreGraphique;
	}

	public List<AFiltreComposant> getListeFiltreListe() {
		return listeFiltreListe;
	}

	public void setListeFiltreListe(List<AFiltreComposant> listeFiltreListe) {
		this.listeFiltreListe = listeFiltreListe;
	}

	public AFiltreComposant getFiltreSelectTabulaire() {
		return filtreSelectTabulaire;
	}

	public void setFiltreSelectTabulaire(AFiltreComposant filtreSelectTabulaire) {
		this.filtreSelectTabulaire = filtreSelectTabulaire;
	}

	public AFiltreComposant getFiltreSelectGraphique() {
		return filtreSelectGraphique;
	}

	public void setFiltreSelectGraphique(AFiltreComposant filtreSelectGraphique) {
		this.filtreSelectGraphique = filtreSelectGraphique;
	}

	public AFiltreComposant getFiltreSelectListe() {
		return filtreSelectListe;
	}

	public void setFiltreSelectListe(AFiltreComposant filtreSelectListe) {
		this.filtreSelectListe = filtreSelectListe;
	}
	
	public void replaceFilterGraphique(List<AFiltreComposant> liste,AFiltreComposant f){
		if(liste.contains(f)){
			liste.remove(f);
			liste.add(0,f);
		}else{
			liste.add(0,f);
		}
	}
	
	
}
