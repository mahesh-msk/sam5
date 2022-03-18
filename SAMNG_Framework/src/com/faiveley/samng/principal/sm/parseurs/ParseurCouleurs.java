package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.graphics.RGB;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurCouleursExplorer;

import noNamespace.CouleurDocument.Couleur;
import noNamespace.ListeCouleursDocument;
import noNamespace.ListeCouleursDocument.ListeCouleurs;

/**
 * @author Olivier
 * @version 1.0
 * @created 08-janv.-2008 17:37:35
 * impl�ment�e par Olivier
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
			
			// OPCoach : FIx the path to the color file. Must get it from Bundle 
			// chemin is relative to bundle...
			Bundle b = FrameworkUtil.getBundle(getClass());
			URL u = b.getEntry(chemin);
			URI u2 = FileLocator.resolve(u).toURI();
			File colorFile = new File(u2);
			
			ListeCouleursDocument couleurDoc = ListeCouleursDocument.Factory.parse(colorFile);
			this.couleurs = couleurDoc.getListeCouleurs();
		} catch (Exception e) {
		    System.err.println("[ERREUR] "+ParseurCouleurs.class.getSimpleName()+" [line 80] : " +e.getMessage());
		}
	}
}