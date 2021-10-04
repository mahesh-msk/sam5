package com.faiveley.samng.vuegraphique.sm.gestionGraphes;

import java.util.List;

import org.eclipse.swt.graphics.RGB;

import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.CourbeMessageValue;



public class Courbe {
	private Double resoVerticale;
	private Double minDomainValeur;
	private Double maxDomainValeur;
	private Double minValeur;	//if no zoom, this should be equal with minDomainValeur
	private Double maxValeur;	//if no zoom, this should be equal with maxDomainValeur
	private List<CourbeMessageValue>[] valeurs;
	private CourbeMessageValue[] valeursSimples;
	private RGB couleur;
	private AVariableComposant variable;
	private int num;
	
	public RGB getCouleur() {
		return couleur;
	}
	
	public void setCouleur(RGB couleur) {
		this.couleur = couleur;
	}
	
	public Courbe() {
		super();
	}
	
	public Double getMaxValeur() {
		return maxValeur;
	}

	public void setMaxValeur(Double maxValeur) {
		this.maxValeur = maxValeur;
	}

	public Double getMinValeur() {
		return minValeur;
	}
	
	public void setMinValeur(Double minValeur) {
		this.minValeur = minValeur;
	}
	
	public Double getMaxDomainValeur() {
		return maxDomainValeur;
	}
	
	public void setMaxDomainValeur(Double maxValeur) {
		this.maxValeur = maxValeur;
		this.maxDomainValeur = maxValeur;
	}
	
	public Double getMinDomainValeur() {
		return minDomainValeur;
	}
	
	public void setMinDomainValeur(Double minValeur) {
		this.minValeur = minValeur;
		this.minDomainValeur = minValeur;
	}
	
	public Double getResoVerticale() {
		return resoVerticale;
	}
	
	public void setResoVerticale(Double resoVerticale) {
		this.resoVerticale = resoVerticale;
	}
	
	public List<CourbeMessageValue>[] getValeurs() {
		return valeurs;
	}
	
	public void setValeurs(List<CourbeMessageValue>[] valeurs) {
		this.valeurs = valeurs;
	}
	
	public CourbeMessageValue[] getValeursSimples() {
		return valeursSimples;
	}
	
	public void setValeursSimples(CourbeMessageValue[] valeurs) {
		this.valeursSimples = valeurs;
	}
	
	public AVariableComposant getVariable() {
		return variable;
	}
	
	public void setVariable(AVariableComposant variable) {
		this.variable = variable;
	}
	
	public int getNum() {
		return num;
	}
	
	public void setNum(int num) {
		this.num = num;
	}
}
