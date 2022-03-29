package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurCorrectionsExplorer;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;

import noNamespace.ListeSegmentsDistanceDocument.ListeSegmentsDistance;
import noNamespace.ListeSegmentsDocument;
import noNamespace.ListeSegmentsDocument.ListeSegments;
import noNamespace.ListeSegmentsTempsDocument.ListeSegmentsTemps;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 06-f�vr.-2008 18:36:32
 */
public class ParseurCorrections implements IParseurInterface {
	// Empty xml file content
	private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><liste-segments xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"corrections.xsd\"></liste-segments>"; 

	// The xml document loaded
	private ListeSegmentsDocument correctionsDoc = null;

	// The name of the xml file
	private String correctionsFileName;

	// The single instance of this class
	private static ParseurCorrections instance = new ParseurCorrections();

	/**
	 * Private constructor. Singleton class
	 */
	protected ParseurCorrections() {}

	/**
	 * Returns the single instance of this class
	 * 
	 * @return	the instance
	 */
	public static ParseurCorrections getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return ParseurCorrectionsExplorer.getInstance();
		}
		
		return instance;
	}

	/** 
	 * Suppression de l'instance
	 */
	public void clear() {
		correctionsDoc = null;
		correctionsFileName = null;
	}


	/**
	 * Loads the distance segments from the corrections xml file.
	 * This segments contains only the number of the segment and the correcter diameter
	 * @return a map<segment number, distance segment>
	 */
	public  HashMap<Integer,SegmentDistance> chargerSegmentsDistance(){
		HashMap<Integer, SegmentDistance> segDist = new HashMap<Integer, SegmentDistance>();

		if (this.correctionsDoc != null) {
			// Get the list of segments loaded from xml  
			ListeSegments segs = this.correctionsDoc.getListeSegments();
			
			if (segs != null && this.correctionsDoc.getListeSegments().getListeSegmentsDistance() != null) {
				List<noNamespace.SegmentDistanceDocument.SegmentDistance> fileSegDist = this.correctionsDoc.getListeSegments().getListeSegmentsDistance().getSegmentDistanceList();
				
				// Create a segment in Sam format
				for (noNamespace.SegmentDistanceDocument.SegmentDistance s : fileSegDist) {
					SegmentDistance seg = new SegmentDistance();
					seg.setDiameterCorrige(s.getDiametre());
					seg.setNumeroSegment(s.getNumero());

					// Add segment
					segDist.put(Integer.valueOf(seg.getNumeroSegment()), seg);
				}
			}
		}

		return segDist;
	}


	/**
	 * Loads the time segments from the corrections xml file
	 * This segments contains only the numaber of the segment and the correcter time
	 * @return		a map<segment number, time segment>
	 */
	public  HashMap<Integer,SegmentTemps> chargerSegmentsTemps(){
		HashMap<Integer, SegmentTemps> segTemp = new HashMap<Integer, SegmentTemps>();

		if (this.correctionsDoc != null) {
			// Get the list of segments loaded from xml  
			ListeSegments segs = this.correctionsDoc.getListeSegments();
			
			if (segs != null && this.correctionsDoc.getListeSegments().getListeSegmentsTemps() != null) {
				List<noNamespace.SegmentTempsDocument.SegmentTemps> fileSegTemp = this.correctionsDoc.getListeSegments().getListeSegmentsTemps().getSegmentTempsList();
				
				// Create a segment in Sam format
				for (noNamespace.SegmentTempsDocument.SegmentTemps s : fileSegTemp) {
					SegmentTemps seg = new SegmentTemps();

					// Convert the time in a string value
					Calendar c = s.getTemps();
					String time = "";
					
					try {
						time = ConversionTemps.FORMATER.format(c.getTime());
					} catch (RuntimeException e) {}
					
					seg.setTempCorrige(time);
					seg.setNumeroSegment(s.getNumero());

					// Add segment
					segTemp.put(Integer.valueOf(seg.getNumeroSegment()), seg);
				}
			}
		}

		return segTemp;
	}

	/**
	 * Saves the corrections of the current distance segments in the xml file 
	 * @param listSegmentsDistance	a map <segment number, distance segment>
	 */
	public boolean enregistrerSegmentsDistance( HashMap<Integer,SegmentDistance> listSegmentsDistance,boolean forcerCreationXML){
		if (this.correctionsDoc == null) {
			if (!forcerCreationXML) {
				return false;
			}
			
			File file = new File(this.correctionsFileName);
			
			// Check the file existence
			try {
				if ((!file.exists()) || (file.length() == 0)) {
					FileOutputStream fOut = new FileOutputStream(file);
					new PrintStream(fOut).print(emptyXMLFileContent);
					this.correctionsDoc = ListeSegmentsDocument.Factory.parse(file);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (XmlException e) {
				e.printStackTrace();
			}
		}

		ListeSegmentsDistance listSegDist = null;
		
		// Get the current list of segments from the xml document 
		ListeSegments listSegments = this.correctionsDoc.getListeSegments();
		
		if (listSegments == null) {
			listSegments = this.correctionsDoc.addNewListeSegments();
		} else {
			listSegDist = listSegments.getListeSegmentsDistance();
			
			if (listSegDist == null) {
				listSegDist = listSegments.addNewListeSegmentsDistance();
			} else {
				List<noNamespace.SegmentDistanceDocument.SegmentDistance> segDistList = listSegDist.getSegmentDistanceList();
				// Clears the current list
				segDistList.clear();			
			}
		} 

		if (listSegDist != null) {
			for (SegmentDistance seg : listSegmentsDistance.values()) {
				// Add a new segment
				noNamespace.SegmentDistanceDocument.SegmentDistance s = listSegDist.addNewSegmentDistance();
				s.setDiametre((int)seg.getDiameterCorrige());
				s.setNumero(seg.getNumeroSegment());
			}
		}
		
		// Saves the xml document
		return listSegDist != null ? saveDocument() : false;
	}

	/**
	 * Saves the corrections of the current distance segments in the xml file
	 * 
	 * @param listSegmentsTemps	a map <segment number, temp segment>
	 */
	public boolean enregistrerSegmentsTemps( HashMap<Integer,SegmentTemps> listSegmentsTemps, boolean forcerCreationXML) {
		if (this.correctionsDoc == null) {
			if (!forcerCreationXML) {
				return false;
			}
			File file = new File(this.correctionsFileName);
			
			// Check the file existence
			try {
				if ((!file.exists()) || (file.length() == 0)) {
					// Parse the file
					FileOutputStream fOut = new FileOutputStream(file);
					new PrintStream(fOut).print(emptyXMLFileContent);
					this.correctionsDoc = ListeSegmentsDocument.Factory.parse(file);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
			} catch (XmlException e) {}
		}

		ListeSegmentsTemps listSegTemp = null;
		
		// Get the current list of segments from the xml document 
		ListeSegments listSegments = this.correctionsDoc.getListeSegments();

		if (listSegments == null) {
			listSegments = this.correctionsDoc.addNewListeSegments();
		} else {
			listSegTemp = listSegments.getListeSegmentsTemps();
			if (listSegTemp == null) {
				listSegTemp = listSegments.addNewListeSegmentsTemps();
			} else {
				List<noNamespace.SegmentTempsDocument.SegmentTemps> segTempList = listSegTemp.getSegmentTempsList();

				// Clears the current list
				segTempList.clear();			
			}
		} 

		if (listSegTemp != null) {
			for (SegmentTemps seg : listSegmentsTemps.values()) {
				// Add a new segment
				noNamespace.SegmentTempsDocument.SegmentTemps s = listSegTemp.addNewSegmentTemps();
				
				try {
					String tempCorrige = seg.getTempCorrige();
										
					if (tempCorrige.length() == 19) {
						tempCorrige += ".";
					}
										
					if (tempCorrige.length() > 19 && tempCorrige.length() < 23) {
						while (tempCorrige.length() < 23) {
							tempCorrige += "0";
						}
					}

					Date d = ConversionTemps.FORMATER.parse(tempCorrige);
					Calendar c = Calendar.getInstance();
					c.setTime(d);
					s.setTemps(c);
					s.setNumero(seg.getNumeroSegment());
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		// Saves the xml document
		return listSegTemp != null ? saveDocument() : false;
	}

	/**
	 * Parses the corrections file
	 * @param fileName
	 */
	public void parseRessource(String fileName,boolean explorer,int deb,int fin) {
		// Empty file name =>return
		if (fileName == null || fileName.length() == 0) {
			return;
		}

		try {
			this.correctionsFileName = fileName;
			File file = new File(fileName);
			// Check the file existance
			
			if (!((!file.exists()) || (file.length() == 0))) {
				// Parse the file
				this.correctionsDoc = ListeSegmentsDocument.Factory.parse(file);
			}

		} catch (XmlException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
			this.correctionsDoc.save(new File(this.correctionsFileName), xmlOptions);
			//I think this is not ok: maybe another method is to have the listeCorrections updated
			
			this.correctionsDoc = ListeSegmentsDocument.Factory.parse(new File(this.correctionsFileName));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

}