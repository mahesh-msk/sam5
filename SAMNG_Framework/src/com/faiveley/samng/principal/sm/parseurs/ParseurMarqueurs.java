package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;

import noNamespace.ListeDocument;
import noNamespace.ListeDocument.Liste;
import noNamespace.MarqueurDocument.Marqueur;

/**
 * @author Cosmin Udroiu
 * 
 */
public class ParseurMarqueurs implements IParseurInterface {
	private String markersFileName;

	private ListeDocument markersDoc;

	private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><liste xsi:noNamespaceSchemaLocation=\"marqueurs.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></liste>";

	public ParseurMarqueurs() {
	}

	/**
	 * Parses an XML file
	 * 
	 * @param chemin
	 *            name of the XML file to be parsed
	 */
	public void parseRessource(String chemin,boolean explorer,int deb,int fin) {
		try {
			File file = new File(chemin);
			this.markersFileName = chemin;
			if (!((!file.exists() || file.length() == 0))) {
//				FileOutputStream fOut = new FileOutputStream(file);
//				new PrintStream(fOut).print(emptyXMLFileContent);
				this.markersDoc = ListeDocument.Factory.parse(file);
			}
		} catch (XmlException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * M�thode qui retourne l' annotation du parcours
	 * 
	 * @return
	 */
	public String chargerAnnotationParcours() {
		if (this.markersDoc == null) {
			try {
				File file = new File(this.markersFileName);
				if (file.exists()) {
					FileOutputStream fOut = new FileOutputStream(file);
					new PrintStream(fOut).print(emptyXMLFileContent);
					this.markersDoc = ListeDocument.Factory.parse(file);
				}else{
					return null;
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (XmlException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Liste xmlListMarkers = markersDoc.getListe();

		String m = xmlListMarkers.getMarqueurParcours();

		if (m != null)
			return m;
		else
			return null;

	}

	/**
	 * Enregiste l'annotation du parcours
	 * 
	 * @param annotation
	 * @return
	 */
	public boolean enregistrerAnnotationParcours(String annotation) {

		//TAGTAG
		createFileIfNotPresent();

		String annot = this.markersDoc.getListe().getMarqueurParcours();
		if (annot == null) {
			this.markersDoc.getListe().setMarqueurParcours(annotation);
		}
//		this.markersDoc.getListe().setMarqueurParcours(annotation);
		saveDocument();
		return true;
	}

	/**
	 * Loads the markers from the configured file name. A parseRessource call
	 * should be made previously calling this method
	 * 
	 * @return a list of loaded markers
	 */
	public List<AMarqueur> chargerMarqueurs() {
		int markerId;
		String markerComment;
		String markerName;
		com.faiveley.samng.principal.sm.marqueurs.Marqueur marker;

		List<AMarqueur> retMarkersList = new ArrayList<AMarqueur>(0);

		if (this.markersDoc == null) {
			return retMarkersList;
		}

		Liste xmlListMarkers = markersDoc.getListe();
		if (xmlListMarkers == null) {
			return retMarkersList;
		}

		List<Marqueur> xmlListMarkersList = xmlListMarkers.getMarqueurList();

		for (Marqueur xmlMarker : xmlListMarkersList) {
			try {
				markerId = xmlMarker.getIdMessage();
			} catch (Exception e) {
				System.err.println("Cannot read marker id: " + e.getMessage());
				continue;
			}

			try {
				markerComment = xmlMarker.getCommentaire();
			} catch (Exception e) {
				System.err.println("Cannot read marker comment: "+ e.getMessage());
				markerComment = Messages.getString("ParseurMarqueur.1") + " "+ markerId;
			}

			try {
				markerName = xmlMarker.getNom();
				if (markerName == null || "".equals(markerName.trim()))
					markerName = Messages.getString("ParseurMarqueur.1") + markerId;
			} catch (Exception e) {
				System.err.println("Cannot read marker comment: "+ e.getMessage());
				markerName = Messages.getString("ParseurMarqueur.1") + " "+ markerId;
			}
			marker = new com.faiveley.samng.principal.sm.marqueurs.Marqueur();
			marker.setIdMessage(markerId);
			marker.setNom(markerName);
			marker.setCommentaire(markerComment);
			retMarkersList.add(marker);
		}
		return retMarkersList;
	}

	public void createFileIfNotPresent(){
		try {
			File file = new File(this.markersFileName);
			if (!file.exists()) {
				FileOutputStream fOut = new FileOutputStream(file);
				new PrintStream(fOut).print(emptyXMLFileContent);
				this.markersDoc = ListeDocument.Factory.parse(file);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean enregistrerMarqueurs(List<AMarqueur> marqueurs) {

		//TAGTAG
		createFileIfNotPresent();

		this.markersDoc.getListe().getMarqueurList().clear();
		if (marqueurs == null) {
			return true;
		}
		if (marqueurs.size() > 0) {
			for (AMarqueur marker : marqueurs) {
				enregistrerMarqueur(marker);
			}
		}
		saveDocument();
		return true;
	}

	/**
	 * Saves a marker in the configured XML file
	 * 
	 * @param marqueur
	 *            the marker to be saved
	 * @return true if the marker was succesfully saved
	 */
	public boolean enregistrerMarqueur(AMarqueur marqueur) {
		if (this.markersDoc == null) {
			return false;
		}

		Marqueur xmlRecMaker = findMarkerById(marqueur.getIdMessage());

		if (xmlRecMaker == null) {
			xmlRecMaker = this.markersDoc.getListe().addNewMarqueur();
		}

		xmlRecMaker.setIdMessage(marqueur.getIdMessage());
		xmlRecMaker.setNom(marqueur.getNom());

		if (marqueur instanceof com.faiveley.samng.principal.sm.marqueurs.Marqueur) {
			xmlRecMaker
			.setCommentaire(((com.faiveley.samng.principal.sm.marqueurs.Marqueur) marqueur)
					.getCommentaire());
		}

		return saveDocument();
	}

	/**
	 * Removes a marker from the configured XML file name. When removing the
	 * marker the marker ID is considered as removal key
	 * 
	 * @param marqueur
	 *            the marker to be removed
	 * @return
	 */
	public boolean effacerMarqueur(AMarqueur marqueur) {
		Marqueur recMarqueur = findMarkerById(marqueur.getIdMessage());

		if (recMarqueur == null) {
			return false;
		}
		if (this.markersDoc!=null) {
			this.markersDoc.getListe().getMarqueurList().remove(recMarqueur);
		}

		return saveDocument();
	}

	/**
	 * Searches a marker in the XML file based on its ID
	 * 
	 * @param id
	 *            the ID of the marker to be searched
	 * @return the xml marker object found
	 */
	protected Marqueur findMarkerById(int id) {
		int markerId;

		if (this.markersDoc == null) {
			return null;
		}

		Liste xmlListMarkers = markersDoc.getListe();

		if (xmlListMarkers == null) {
			return null;
		}

		List<Marqueur> xmlListMarkersList = xmlListMarkers.getMarqueurList();

		for (Marqueur xmlMarker : xmlListMarkersList) {
			try {
				markerId = xmlMarker.getIdMessage();
			} catch (Exception e) {
				System.err.println("Cannot read marker id: " + e.getMessage());

				continue;
			}

			if (markerId == id) {
				return xmlMarker;
			}
		}

		return null; // Not found
	}

	/**
	 * Saves the current document in the specified XML file name
	 * 
	 * @return true if the save was successfull
	 */
	private boolean saveDocument() {
		try {
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSavePrettyPrint();
			xmlOptions.setSavePrettyPrintIndent(4);
			this.markersDoc.save(new File(this.markersFileName), xmlOptions);
			// : I think this is not ok ... Maybe another method is to have
			// the listeFiltres updated
			this.markersDoc = ListeDocument.Factory.parse(new File(
					this.markersFileName));
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}
}
