package com.faiveley.samng.vuegraphique.sm.gestionGraphes;

import java.util.ArrayList;
import java.util.List;

public class Graphe {
	private int numero; 
	private List<Courbe> listeCourbe;
	private TypeGraphe typeGraphe;
	
	public Graphe(){
		listeCourbe = new ArrayList<Courbe>();
	}
	public List<Courbe> getListeCourbe() {
		return listeCourbe;
	}
	
	public void setListeCourbe(List<Courbe> listeCourbe) {
		this.listeCourbe = listeCourbe;
	}
	
	public int getNumero() {
		return numero;
	}
	
	public void setNumero(int numero) {
		this.numero = numero;
	}

	public void ajouterCourbe(Courbe courbe){
		this.listeCourbe.add(courbe);
	}
	
	public void setTypeGraphe(TypeGraphe typeGraphe) {
		this.typeGraphe = typeGraphe;
	}
	
	public TypeGraphe getTypeGraphe() {
		return this.typeGraphe;
	}
}
