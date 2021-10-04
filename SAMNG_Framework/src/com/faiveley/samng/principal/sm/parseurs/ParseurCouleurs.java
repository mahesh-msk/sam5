package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import noNamespace.CouleurDocument.Couleur;
import noNamespace.ListeCouleursDocument;
import noNamespace.ListeCouleursDocument.ListeCouleurs;

import org.eclipse.swt.graphics.RGB;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurCouleursExplorer;

/**
 * @author Olivier
 * @version 1.0
 * @created 08-janv.-2008 17:37:35
 * implémentée par Olivier
 */
public class ParseurCouleurs implements IParseurInterface {

	private static ParseurCouleurs instance = new ParseurCouleurs();
	private ListeCouleurs couleurs = null;


	
	/**
	 * @return		the only instance of the parser
	 */
	public static ParseurCouleurs getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return ParseurCouleursExplorer.getInstance();
		}
		return instance;
	}
	
	/** Suppression de l'instance */
	public void clear(){
		couleurs=null;
	}
	
	
	protected ParseurCouleurs(){

	}

	
	public Map<String, RGB> chargerCouleurs(){
		List<Couleur> listeCouleurs = this.couleurs.getCouleurList();
		Map<String, RGB> listeRGB = new LinkedHashMap<String, RGB>();
		RGB newRgb;
		String colorName;
		for(Couleur coul: listeCouleurs) {
			colorName = coul.getNom();
			newRgb = new RGB(coul.getRedCode().intValue(),coul.getGreenCode().intValue(),coul.getBlueCode().intValue());
			listeRGB.put(colorName, newRgb);
		}
		return listeRGB;
	}

	/**
	 * 
	 * @param chemin
	 */
	public void parseRessource(String chemin,boolean explorer,int deb,int fin){
		try {
			//CIU - before parsing a new file the members must be cleared otherwise if an exception
			//		is thrown in the parser, we migth use later invalid informations (Pay attention 
			//		that we are using a singleton)
			//		The most relevant case is when we have a valid binary file loaded with a valid XML
			//		and after that we load another binary file but the XML for it does not exists
			this.couleurs = null;
			
			ListeCouleursDocument couleurDoc = ListeCouleursDocument.Factory.parse(new File(chemin));
			this.couleurs = couleurDoc.getListeCouleurs();
		} catch (Exception e) {
		    System.err.println("[ERREUR] "+ParseurCouleurs.class.getSimpleName()+" [line 80] : " +e.getMessage());
		}
	}
}