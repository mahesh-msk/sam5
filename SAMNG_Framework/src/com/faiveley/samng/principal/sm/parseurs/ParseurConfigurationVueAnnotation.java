package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;

import noNamespace.ColDocument.Col;
import noNamespace.ConfigurationColonnesAnnotationsDocument.ConfigurationColonnesAnnotations;
import noNamespace.VueAnnotationDocument;
/**
 * 
 * @author Olivier
 *
 */
public class ParseurConfigurationVueAnnotation implements IParseurInterface{

	public VueAnnotationDocument vueAnnotation;
	private String nomFichier;
	private static String emptyXMLFileContent = "<vue-annotation xsi:noNamespaceSchemaLocation=\"configuration-vue-annotations.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" +
			"<configuration-colonnes-annotations>" +
			"<col largeur=\"150\" nom=\"temps\"/>" +
			"<col largeur=\"150\" nom=\"distance\"/>" +
			"<col largeur=\"150\" nom=\"distance_corrigee\"/>" +
			"<col largeur=\"150\" nom=\"nom_marqueur\"/>" +
			"<col largeur=\"150\" nom=\"temps_corrigee\"/>" +
			"</configuration-colonnes-annotations>" +
			"</vue-annotation>";

	public void parseRessource(String chemin,boolean explorer,int deb,int fin) throws AExceptionSamNG {
		try {
			File file = new File(chemin);
			if (!file.exists() || file.length() == 0) {
				FileOutputStream fOut = new FileOutputStream(file);
				new PrintStream(fOut).print(emptyXMLFileContent);
				fOut.close();	               
			}
			this.nomFichier = chemin;
			this.vueAnnotation = VueAnnotationDocument.Factory.parse(file);
			saveDocument();
		} catch (Exception e){
			this.vueAnnotation=null;
		}
	}

	public ParseurConfigurationVueAnnotation() {

	}

	/**
	 * M�thode de chargement des largeurs de colonnes 
	 * @return une map d'association nomColonne/margeurColonne
	 */
	public HashMap<String, Integer> chargerLargeurColonnes(){
		HashMap<String, Integer> hashColonneLargeur = new HashMap<String, Integer>();
		try{
			List<Col> listeColonnesXml = this.vueAnnotation.getVueAnnotation().getConfigurationColonnesAnnotations().getColList();
			for (Col colonne : listeColonnesXml) {
				hashColonneLargeur.put(colonne.getNom().getStringValue(), Integer.valueOf(colonne.getLargeur().getStringValue()));
			}
		} 
		catch(Exception ex){
			return hashColonneLargeur;
		}
		return hashColonneLargeur;
	}

	/**
	 * M�thode d'enregistrement des largeurs de colonnes 
	 * @author Olivier
	 * @param 
	 * @return 
	 */
	public void enregistrerLargeurColonnes(HashMap<String, Integer> mapLargeurColonne){
		if(mapLargeurColonne !=null && mapLargeurColonne.size()>0){
			XmlAnySimpleType simpleType;
			Set<String> nomsColonnes = mapLargeurColonne.keySet();
			Col coll;

			ConfigurationColonnesAnnotations configurationColonnesAnnotation = this.vueAnnotation.getVueAnnotation().getConfigurationColonnesAnnotations();
			List<Col> listeColonnesXml =  configurationColonnesAnnotation.getColList();
			listeColonnesXml.clear();
			for (String string : nomsColonnes) {
				if(string!=null){
					coll = configurationColonnesAnnotation.addNewCol();
					simpleType = coll.addNewLargeur();
					simpleType.setStringValue(mapLargeurColonne.get(string).toString());
					simpleType = coll.addNewNom();
					simpleType.setStringValue(string);	
				}
			}
			saveDocument();
		} 
	}


	/**
	 * Saves the current document in the specified XML file name
	 * @return true if the save was successfull
	 */
	private boolean saveDocument() {
		try {
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSavePrettyPrint();
			xmlOptions.setSavePrettyPrintIndent(4);
			this.vueAnnotation.save(new File(this.nomFichier), xmlOptions);

		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}
		return true;
	}
}