package com.faiveley.samng.principal.sm.parseurs;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML.TypeParseurExplorer;
import com.faiveley.samng.principal.sm.parseurs.adapteur.target.ParseurTarget;



public class TypeParseur {
	ParseurParcoursBinaire parser;
	public ParseurTarget target; 
	private static TypeParseur instance = new TypeParseur();

	protected TypeParseur(){
	}

	public static TypeParseur getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return TypeParseurExplorer.getInstance();
		}
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear(){
		parser=null;
	}

	public ParseurParcoursBinaire getParser() {
		return parser;
	}

	public void setParser(ParseurParcoursBinaire parser) {
		this.parser = parser;
	}

	public ParseurTarget getTarget() {
		return target;
	}

	public void setTarget(ParseurTarget target) {
		this.target = target;
	}

	
}
