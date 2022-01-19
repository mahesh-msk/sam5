package com.faiveley.samng.principal.sm.corrections;

import java.io.File;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TempResolutionEnum;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireCorrectionExplorer;
import com.faiveley.samng.principal.sm.parseurs.ParseurCorrections;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class GestionnaireCorrection {
	// Loaded corrections
	private HashMap<Integer, SegmentDistance> loadedDistanceSegments = new HashMap<Integer, SegmentDistance>();
	private HashMap<Integer, SegmentTemps> loadedTempSegments = new HashMap<Integer, SegmentTemps>();
	private ParseurCorrections parsCor = null;

	// The single instance of this singleton
	private static GestionnaireCorrection instance = new GestionnaireCorrection();

	/**
	 * Constructor. Loads the corrections
	 */
	protected GestionnaireCorrection() {}

	/**
	 * Returns the single instance of this class. Sngleton class.
	 * 
	 * @return the instance
	 */
	public static GestionnaireCorrection getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireCorrectionExplorer.getInstance();
		}
		return instance;
	}

	/** Suppression de l'instance */
	public void clear() {
		loadedDistanceSegments.clear();
		loadedTempSegments.clear();
		parsCor = null;
	}

	/**
	 * Méthode de chargement des segments de temps
	 */
	public void chargerCorrections() {
		if (this.parsCor != null) {
			this.loadedDistanceSegments = this.parsCor.chargerSegmentsDistance();
			this.loadedTempSegments = this.parsCor.chargerSegmentsTemps();
		}
	}

	/**
	 * Apply the corrections loaded from the corrections xml to the currently created segments
	 * 
	 * @throws ParseException
	 */
	public void applyCorrections() throws ParseException {
		InfosFichierSamNg info = (InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier();

		if (info != null) {
			String filename = info.getNomFichierParcoursBinaire();
			loadCorrections(filename);

			// Load the corrections from the xml file
			loadCorrections(filename);
		}

		if (this.parsCor != null) {
			chargerCorrections();
		}

		// Apply the corrections on the current segments
		checkCorrectionsDistance();
		checkCorrectionsTemp();

		if (TableSegments.getInstance().areTempCorrections() || TableSegments.getInstance().areDistanceCorrections()) {
			ActivatorData.getInstance().getProgressBar().stop();
			applyTempChanges();
			applyDistanceChanges();

			if (TableSegments.getInstance().areDistanceCorrections()) {
				TableSegments.getInstance().setAppliedDistanceCorrections(true);

			}
			
			if (TableSegments.getInstance().areTempCorrections()) {
				TableSegments.getInstance().setAppliedTempCorrections(true);
			}
		}
	}

	/**
	 * Verify each time segment if has corrections made
	 */
	public void applyTempChanges() {
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		ListMessages listeMessages = getParcoursMessages();
		
		// Creates the rows
		for (SegmentTemps segTemp : listeSegs) {
			// Looks for any changes values of the segments
			if (!segTemp.getTempCorrige().equals(segTemp.getTempInitial())) {
				ActivatorData.getInstance().getPoolDonneesVues().put("correction", "applyCorrection");
				ActivatorData.getInstance().getPoolDonneesVues().put("axeTpsCorrige", Boolean.valueOf(true));

				// Calculates the difference
				long difference = ConversionTemps.calculatePeriodAsLong(segTemp.getTempInitial(), segTemp.getTempCorrige());

				// Enables the corrections
				segTemp.setHasCorrections(true);
				TableSegments.getInstance().setEnableTempCorrections(true);

				// Set the values for all the messages that are related to the segment
				if (listeMessages != null && listeMessages.size() > 0) {
					Message msg = listeMessages.getMessageById(segTemp.getStartMsgId());

					String crtTime = null;

					if (msg != null) {
						int indiceMessage = listeMessages.getIndiceMessageById(segTemp.getStartMsgId());
						boolean finBoucle = false;
						TempResolutionEnum resoTemps = ((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).getTempResolution();
						
						do {
							// Calculates the corrected time
							crtTime = ConversionTemps.getFormattedDate(msg.getAbsoluteTime(), true);

							// Sets the corrected time in a AVariableComposant
							AVariableComposant tempCorrection = GestionnairePool.getInstance().getVariable(TypeRepere.tempsCorrigee.getCode());

							if (tempCorrection != null) {
								String valeur = ConversionTemps.addPeriod(crtTime, difference);
								switch (resoTemps) {
									case RESOLUTION_0_01:
										try {
											valeur = valeur.substring(0, 22);
										} catch (Exception ex) {}
										break;
									case RESOLUTION_0_1:
										try {
											valeur = valeur.substring(0, 21);
										} catch (Exception ex) {}
										break;
									case RESOLUTION_1:
										try {
											valeur = valeur.substring(0, 19);
										} catch (Exception ex) {}
										break;
								}
								
								tempCorrection.setValeur(valeur.getBytes());
								
								// Add variable to the message
								msg.setTempsCorrige(valeur);
								msg.modifierVariable(tempCorrection);
							}

							// The end of the segment was reached
							if (msg.getMessageId() == segTemp.getEndMsgId()) {
								finBoucle = true;
							} else {
								indiceMessage++;
								msg = listeMessages.get(indiceMessage);
							}
						} while (!finBoucle);
					}
				}
			}
		}
	}

	/**
	 * Remove all the corrections from the messages
	 */
	public void doNotApplyTempChanges() {
		ActivatorData.getInstance().getPoolDonneesVues().put("axeTpsCorrige", Boolean.valueOf(false));
		ActivatorData.getInstance().getPoolDonneesVues().put("correction", "disapplyCorrection");
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		
		// Creates the rows
		for (SegmentTemps segTemp : listeSegs) {
			
			// Looks for any changes values of the segments
			if (segTemp.hasCorrections()) {
				TableSegments.getInstance().setEnableTempCorrections(true);
				// Set the values for all the messages that are related to the segment
				ListMessages listeMessages = getParcoursMessages();
				
				if (listeMessages != null) {
					Message msg = null;
					msg = listeMessages.getMessageById(segTemp.getStartMsgId());
					
					if (msg != null) {
						int indiceMessage = listeMessages.getIndiceMessageById(segTemp.getStartMsgId());
						boolean finBoucle = false;

						do {
							// Gets the corrected time variable from the pool
							AVariableComposant tempCorrection = GestionnairePool.getInstance().getVariable(TypeRepere.tempsCorrigee.getCode());

							if (tempCorrection != null) {
								// Removes variable to the message
								msg.supprimerVariable(tempCorrection);
							}

							// The end of the segment was reached
							if (msg.getMessageId() == segTemp.getEndMsgId()) {
								finBoucle = true;
							}
							
							else{
								indiceMessage++;
								if (indiceMessage<listeMessages.size()) {
									msg = listeMessages.get(indiceMessage);
								}
							}
						} while (!finBoucle);
					}
				}	
			}
		}
	}

	/**
	 * Verify each distance segment if has corrections made
	 */
	public void applyDistanceChanges() {
		HashMap<Integer, SegmentDistance> segDistMap = TableSegments.getInstance().getSegmentsDistance();

		for (SegmentDistance segDist : segDistMap.values()) {
			// Find if there are any changes in the table
			if (segDist.getDiameterCorrige() != segDist.getInitialDiameter()) {
				ActivatorData.getInstance().getPoolDonneesVues().put("axeDistanceCorrige", Boolean.valueOf(true));
				ActivatorData.getInstance().getPoolDonneesVues().put("correction", "applyCorrection");
				
				// Calculate the difference
				double valModif = segDist.getDiameterCorrige();
				double factor = valModif / segDist.getInitialDiameter();

				segDist.setHasCorrections(true);
				TableSegments.getInstance().setEnableDistanceCorrections(true);

				// Set the values for all the messages that are related to the segment
				ListMessages listeMessages = getParcoursMessages();
				
				if (listeMessages != null) {
					Message msg = listeMessages.getMessageById(segDist.getStartMsgId());

					if (msg != null) {
						double dist = -1;
						double speed = -1;
						int indiceMessage = listeMessages.getIndiceMessageById(segDist.getStartMsgId());
						boolean finBoucle = false;
						do {
							// Calculate the corrected speed
							AVariableComposant varSpeed = msg.getVariable(TypeRepere.vitesse.getCode());
							if (varSpeed != null) {
								try {
									String speedChaine = varSpeed.toString();
									Float varSpeedValue = Float.valueOf(speedChaine);
									speed = Math.abs(varSpeedValue * factor);
								} catch (Exception e) {
									speed = -1;
								}
							}

							// Calculate the corrected distance
							dist = Math.abs(msg.getAbsoluteDistance() * factor);

							// Create the variable for distance correction
							AVariableComposant distCorrection = GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode());
							
							if (distCorrection != null && dist != -1) {
								double coefDirDistance = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getCoefDirecteur()	+ GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getOrdonneeOrigine();

								int tmpInt = (int) coefDirDistance;
								coefDirDistance = coefDirDistance - (float) tmpInt;
								coefDirDistance = AVariableComposant.arrondir(coefDirDistance, 3);
								double div = 1 / coefDirDistance;
								int multi1 = (int) div;
								int nbDecimales = new String(multi1 + "").length() - 1;

								if (nbDecimales > 0) {
									dist = AVariableComposant.arrondir(dist, nbDecimales);
								}

								DecimalFormat fmt = new DecimalFormat("0.000");
								String s = (fmt.format((double) dist)).replace(",", ".");
								msg.setDistanceCorrige(s);
								distCorrection.setValeur(s.getBytes());
								distCorrection.setTypeValeur(Type.string);

								// Add variable to the message
								msg.modifierVariable(distCorrection);
								dist = -1;
							}

							// Create the variable for distance correction
							AVariableComposant speedCorrection = GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode());

							if (speedCorrection != null && speed != -1) {
								speed = AVariableComposant.arrondir(speed, 3);
								speedCorrection.setValeur(Float.toString((float) speed).getBytes());
								speedCorrection.setTypeValeur(Type.string);

								// Add variable to the message
								msg.modifierVariable(speedCorrection);
								speed = -1;
							}

							// The end of the segment was riched
							if (msg.getMessageId() == segDist.getEndMsgId()) {
								finBoucle = true;
							} else{
								indiceMessage++;
								if (indiceMessage<listeMessages.size()) {
									msg = listeMessages.get(indiceMessage);
								}
							}
						} while (!finBoucle);
					}
				}
			}
		}
	}

	/**
	 * Remove the correction distance variables from all the messages
	 */
	public void doNotApplyDistanceChanges() {
		ActivatorData.getInstance().getPoolDonneesVues().put("axeDistanceCorrige",Boolean.valueOf(false));
		ActivatorData.getInstance().getPoolDonneesVues().put("correction","disapplyCorrection");
		HashMap<Integer, SegmentDistance> segDistMap = TableSegments.getInstance().getSegmentsDistance();

		for (SegmentDistance segDist : segDistMap.values()) {
			
			// Find if there are any changes in the table
			if (segDist.hasCorrections()) {
				TableSegments.getInstance().setEnableDistanceCorrections(true);

				// Set the values for all the messages that are related to the segment
				ListMessages listeMessages = getParcoursMessages();
				
				if (listeMessages != null) {
					Message msg = null;
					msg = listeMessages.getMessageById(segDist.getStartMsgId());

					if (msg != null) {
						int indiceMessage = listeMessages.getIndiceMessageById(segDist.getStartMsgId());
						boolean finBoucle = false;
						do {
							// Get the variable for correction distance from the pool
							AVariableComposant distCorrection = msg.getVariable(TypeRepere.distanceCorrigee.getCode());
							if (distCorrection != null) {

								// Remove variable to the message
								msg.supprimerVariable(distCorrection);
							}

							// Get the variable for correction speed from pool
							AVariableComposant speedCorrection = GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode());

							if (speedCorrection != null) {
								// Remove variable to the message
								msg.supprimerVariable(speedCorrection);
							}

							// The end of the segment was riched
							if (msg.getMessageId() == segDist.getEndMsgId()) {
								finBoucle = true;
							} else {
								indiceMessage++;
								
								if(indiceMessage<listeMessages.size()) {
									msg = listeMessages.get(indiceMessage);
								}
							}
						} while (!finBoucle);
					}
				}
			}
		}
	}

	/**
	 * Saves the distance and time corrections in an xml file
	 */
	public void saveCorrections(boolean forcerCreationXML) {
		if (this.parsCor == null) {
			InfosFichierSamNg info = (InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier();

			if (info != null) {
				String filename = info.getNomFichierParcoursBinaire();
				loadCorrections(filename);
			}
		} else{
			// Save distance corrections
			this.parsCor.enregistrerSegmentsDistance(getDistanceCorrections(),forcerCreationXML);

			// Save time corrections
			this.parsCor.enregistrerSegmentsTemps(getTempCorrections(),forcerCreationXML);
		}
	}

	/**
	 * Returns the good messages from the current loaded parour file
	 * 
	 * @return map <message id, message>
	 */
	private ListMessages getParcoursMessages() {
		// Get parcours
		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		
		if (p == null) {
			return null;
		}
		
		// Get enregisrtrement
		Enregistrement enrg = p.getData().getEnregistrement();
		
		if (enrg == null) {
			return null;
		}

		// Returns the good messages
		return enrg.getMessages();
	}

	/**
	 * Gets the time segments that have corrections made
	 * 
	 * @return map<segment number, time segment>
	 */
	private HashMap<Integer, SegmentTemps> getTempCorrections() {
		HashMap<Integer, SegmentTemps> correctionsToSave = new HashMap<Integer, SegmentTemps>();
		// Get current segments
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		
		// Creates the rows
		for (SegmentTemps segTemp : listeSegs) {
			// Check if there is any correction on this segment and add it to the list of corrections
			correctionsToSave.put(segTemp.getNumeroSegment(), segTemp);

		}
		
		if (correctionsToSave.size() == 0 && loadedTempSegments.size() > 0) {
			correctionsToSave.putAll(loadedTempSegments);
		}
		
		return correctionsToSave;
	}

	/**
	 * Gets the distance segments that have corrections made
	 * 
	 * @return map<segment number, distance segment>
	 */
	private HashMap<Integer, SegmentDistance> getDistanceCorrections() {
		HashMap<Integer, SegmentDistance> correctionsToSave = new HashMap<Integer, SegmentDistance>();
		
		// Get current segments
		HashMap<Integer, SegmentDistance> currentSegments = TableSegments.getInstance().getSegmentsDistance();

		for (SegmentDistance segDist : currentSegments.values()) {
			// Check if there is any correction on this segment and add it to the list of corrections
			correctionsToSave.put(segDist.getNumeroSegment(), segDist);
		}

		if (correctionsToSave.size() == 0 && loadedDistanceSegments.size() > 0) {
			correctionsToSave.putAll(loadedDistanceSegments);
		}

		return correctionsToSave;

	}

	/**
	 * Teste si des corrections de temps existent pour le fichier binaire
	 * 
	 * @return
	 */
	public boolean correctionsTempsExistantes(String filename) {

		boolean change = false;
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		
		// Creates the rows
		loadCorrections(filename);
		chargerCorrections();
		HashMap<Integer, SegmentTemps> segTempsMapSaved = getLoadedTempSegments();

		for (SegmentTemps segTmp : listeSegs) {
			// Find if there are any changes in the table
			if (segTempsMapSaved.size() > 0) {
				if (segTempsMapSaved.get(Integer.valueOf(segTmp.getNumeroSegment())) != null) {
					// On doit tester les 2 dates en tenant compte de la resolution
					SimpleDateFormat format = ConversionTemps.FORMATER;
					
					try {
						Date date1 = format.parse(segTmp.getTempCorrige());
						Date date2 = format.parse(segTempsMapSaved.get(Integer.valueOf(segTmp.getNumeroSegment())).getTempCorrige());
						
						if (!date1.equals(date2)) {
							change = true;
						}
					} catch (ParseException e) {
						change = true;
					}
				}
			}
		}
		
		return change;
	}

	/**
	 * Teste si des corrections de distance existent pour le fichier binaire
	 * 
	 * @return
	 */
	public boolean correctionsDistanceExistantes(String filename) {
		boolean change = false;
		HashMap<Integer, SegmentDistance> segDistMap = TableSegments.getInstance().getSegmentsDistance();
		
		// Creates the rows
		loadCorrections(filename);
		chargerCorrections();
		HashMap<Integer, SegmentDistance> segDistMapSaved = getLoadedDistanceSegments();

		for (SegmentDistance segDist : segDistMap.values()) {
			// Find if there are any changes in the table
			if (segDistMapSaved.size() > 0) {
				if (segDistMapSaved.get(Integer.valueOf(segDist.getNumeroSegment())) != null) {
					if (segDist.getDiameterCorrige() != segDistMapSaved.get(Integer.valueOf(segDist.getNumeroSegment())).getDiameterCorrige()) {
						change = true;
					}
				}
			}
		}
		
		return change;
	}

	/**
	 * Teste si des corrections existent pour le fichier binaire
	 * 
	 * @return
	 */
	public boolean correctionsExistantes(String filename) {
		boolean change = false;
		HashMap<Integer, SegmentDistance> segDistMap = TableSegments.getInstance().getSegmentsDistance();
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		
		// Creates the rows
		loadCorrections(filename);
		chargerCorrections();
		HashMap<Integer, SegmentDistance> segDistMapSaved = getLoadedDistanceSegments();
		HashMap<Integer, SegmentTemps> segTempsMapSaved = getLoadedTempSegments();

		for (SegmentDistance segDist : segDistMap.values()) {
			// Find if there are any changes in the table
			if (segDistMapSaved.size() > 0) {
				if (segDistMapSaved.get(Integer.valueOf(segDist.getNumeroSegment())) != null) {
					if (segDist.getDiameterCorrige() != segDistMapSaved.get(Integer.valueOf(segDist.getNumeroSegment())).getDiameterCorrige()) {
						change = true;
					}
				}
			}
		}
		for (SegmentTemps segTmp : listeSegs) {
			// Find if there are any changes in the table
			if (segTempsMapSaved.size() > 0) {
				if (segTempsMapSaved.get(Integer.valueOf(segTmp.getNumeroSegment())) != null) {
					// on doit tester les 2 dates en tenant compte de la resolution
					SimpleDateFormat format = ConversionTemps.FORMATER;
					
					try {
						if (segTmp.getTempCorrige() == null) {
							change = false;
						} else{
							String tempCorrige = segTmp.getTempCorrige();
							String tempCorrigeSauvegarde = segTempsMapSaved.get(Integer.valueOf(segTmp.getNumeroSegment())).getTempCorrige();

							if (tempCorrige.length()<tempCorrigeSauvegarde.length()) {
								while (tempCorrige.length()<tempCorrigeSauvegarde.length()) {
									tempCorrige += "0";
								}
							}

							Date date1 = format.parse(tempCorrige);
							Date date2 = format.parse(tempCorrigeSauvegarde);
							
							if (!date1.equals(date2)) {
								change = true;
							}
						}
					} catch (ParseException e) {
						change = true;
					}
				}
			}
		}
		
		return change;
	}

	/**
	 * Loads the corrections xml file associated with the current loaded binary file
	 */
	private void loadCorrections(String filename) {
		File file = new File(filename);

		String fileName = file.getName();
		String folder = file.getParent();

		int dotPos;
		
		if ((dotPos = fileName.indexOf('.')) != -1) {
			fileName = fileName.substring(0, dotPos);
		}

		String CorrectionFileName = folder + File.separator + fileName + RepertoiresAdresses.correctionsXML;

		try {
			// Parse the corrections xml
			this.parsCor = ParseurCorrections.getInstance();
			this.parsCor.parseRessource(CorrectionFileName,false,0,-1);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if there are any loaded time corrections from the xml file and apply them on the currently created time segments
	 */
	private void checkCorrectionsTemp() {
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		
		// Creates the rows
		for (SegmentTemps segTemp : listeSegs) {
			// Check if there is any correction on this segment loaded from the xml
			if (this.loadedTempSegments != null) {
				SegmentTemps correctedTempLoaded = this.loadedTempSegments.get(segTemp.getNumeroSegment());
				
				// If found a correction then set it on this created segment
				if (correctedTempLoaded != null) {
					segTemp.setTempCorrige(correctedTempLoaded.getTempCorrige());
					
					if (!segTemp.getTempInitial().equals(segTemp.getTempCorrige())) {
						segTemp.setHasCorrections(true);
						TableSegments.getInstance().setEnableTempCorrections(true);
					}
				}
			}
		}
	}

	public void chargerSegments() {
		checkCorrectionsDistance();
		checkCorrectionsTemp();
	}

	/**
	 * Check if there are any loaded distance corrections from the xml file and apply them on the currently created distance segments
	 */
	private void checkCorrectionsDistance() {
		// Get current segments
		HashMap<Integer, SegmentDistance> currentSegments = TableSegments.getInstance().getSegmentsDistance();

		for (SegmentDistance segDist : currentSegments.values()) {
			// Check if there is any correction on this segment loaded from the xml
			if (this.loadedTempSegments != null) {
				SegmentDistance correctedDistLoaded = this.loadedDistanceSegments.get(segDist.getNumeroSegment());
				
				// If found a correction then set it on this created segment
				if (correctedDistLoaded != null) {
					segDist.setDiameterCorrige(correctedDistLoaded.getDiameterCorrige());
					
					if (segDist.getInitialDiameter() != segDist.getDiameterCorrige()) {
						segDist.setHasCorrections(true);
						TableSegments.getInstance().setEnableDistanceCorrections(true);
					}
				}
			}
		}
	}

	public HashMap<Integer, SegmentDistance> getLoadedDistanceSegments() {
		return loadedDistanceSegments;
	}

	public void setLoadedDistanceSegments(
			HashMap<Integer, SegmentDistance> loadedDistanceSegments) {
		this.loadedDistanceSegments = loadedDistanceSegments;
	}

	public HashMap<Integer, SegmentTemps> getLoadedTempSegments() {
		return loadedTempSegments;
	}

	public void setLoadedTempSegments(HashMap<Integer, SegmentTemps> loadedTempSegments) {
		this.loadedTempSegments = loadedTempSegments;
	}
}
