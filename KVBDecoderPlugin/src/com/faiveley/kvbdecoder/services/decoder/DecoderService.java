package com.faiveley.kvbdecoder.services.decoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.exception.model.train.TrainCategoryEnumException;
import com.faiveley.kvbdecoder.model.atess.event.DescriptorEvent;
import com.faiveley.kvbdecoder.model.atess.variable.DescriptorComplexVariable;
import com.faiveley.kvbdecoder.model.atess.variable.DescriptorVariable;
import com.faiveley.kvbdecoder.model.kvb.event.AnalogEvent;
import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.event.NumericalEvent;
import com.faiveley.kvbdecoder.model.kvb.ip.GroupingEnum;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX14;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX1X4;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX3X6X9;
import com.faiveley.kvbdecoder.model.kvb.train.TrainCategoryEnum;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBAnalogVariable;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBNumericalVariable;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;
import com.faiveley.kvbdecoder.model.kvb.xml.DecodingTable;
import com.faiveley.kvbdecoder.model.kvb.xml.KVBXmlVariable;
import com.faiveley.kvbdecoder.services.decoder.sam.DecoderSAMService;
import com.faiveley.kvbdecoder.services.loader.AtessLoaderService;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;

/**
 * Service pour le décodage d'une balise, qu'elle soit analogique comme numérique.
 * 
 * @author jthoumelin
 *
 */
public class DecoderService {
	public static final String LABEL_ALERT_TEXT_INVALID_FOR_TRAIN_CATEGORY = "Alerte.CategorieTrainInvalide";
	
	/**
	 * Le singleton
	 */
	private static DecoderService SERVICE_INSTANCE = new DecoderService();
	
	/**
	 * Le constructeur vide
	 */
	private DecoderService() {}
	
	/**
	 * Obtention du singleton
	 */
	public static DecoderService getServiceInstance() {
		return SERVICE_INSTANCE;
	}
	
	/**
	 * Obtention du code de l'événement
	 * 
	 * @param encodedEvent : l'événement codé en binaire
	 * @return le code de l'événement
	 */
	public int getEventCode(byte[] encodedEvent) {
		return DecoderSAMService.getServiceInstance().gestionID(encodedEvent);
	}
	
	/**
	 * Obtenir la structure de l'événement.
	 * 
	 * @param eventCode : le code d'événement
	 * @return la stucture de l'événement.
	 */
	public DescriptorEvent getEventDescriptor(int eventCode) {
		return AtessLoaderService.getServiceInstance().getAtessEvents().get(String.valueOf(eventCode));
	}

	/**
	 * Construire l'événement
	 * 
	 * @param encodedEvent : l'événement codé en binaire
	 * @param trainData : les informations sur le train
	 * @param eventDescriptor : le descripteur d'événement
	 * @return l'événement
	 * @throws EventDecoderException 
	 * @throws TrainCategoryEnumException 
	 */
	public Event buildEvent(byte[] encodedEvent, TrainData trainData, DescriptorEvent eventDescriptor) throws EventDecoderException, TrainCategoryEnumException {
		Event event = null;
		
		if (Event.isAnalogEvent(eventDescriptor.getCode())) {
			event = new AnalogEvent(encodedEvent, trainData, eventDescriptor);
		} else if (Event.isNumericEvent(eventDescriptor.getCode())) {
			event = new NumericalEvent(encodedEvent, trainData, eventDescriptor);
		}
		
		int eventLength = eventDescriptor.getLength();
		int offset = 2; // On ignore les deux premiers quartets, qui correspondent au code de l'événement, déjà déterminé
		
		for (String variableCode : eventDescriptor.getVariables()) {
			DescriptorVariable variableDescriptor = AtessLoaderService.getServiceInstance().getAtessVariables().get(variableCode);
			
			int size = -1;
			
			if (variableDescriptor instanceof DescriptorComplexVariable) { // Dans le cas d'une variable complexe
				size = ((DescriptorComplexVariable) variableDescriptor).getSize() * 2;
				
				if (event instanceof AnalogEvent) {
					event.setKVBVariable(new KVBAnalogVariable(event, variableDescriptor, event.getMessage().substring(offset, offset + size)));
				} else if (event instanceof NumericalEvent) {
					event.setKVBVariable(new KVBNumericalVariable(event, variableDescriptor, event.getMessage().substring(offset, offset + size)));
				}
			} else { // Dans le cas d'une variable non complexe, on se contente de décaler l'offset
				size = variableDescriptor.getType().getSize() * 2;
				
				if (size < 0) {
					throw new EventDecoderException(MessageService.ERROR_EVENT_DECODING__UNKNOWN_VARIABLE_SIZE, variableDescriptor.getType().toString());
				}				
			}
			
			offset += size;
			
			if (eventLength == offset) {
				break;
			}
		}
		
		KVBVariable kvbVariable = event.getKVBVariable();
		
		if (kvbVariable != null) {			
			if (event instanceof AnalogEvent) {
				AnalogService.getDecoderInstance().decodeFromAnalog((KVBAnalogVariable) kvbVariable); // Détermination du point d'information pour une balise analogique
			} else if (event instanceof NumericalEvent) {
				NumericalService.getServiceInstance().decodeFromNumerical((KVBNumericalVariable) kvbVariable, trainData); // Détermination des points d'information pour une balise numérique
			}
			
			for (InformationPoint ip : event.getKVBVariable().getInformationPoints()) {
				if (ip.isRightDirection()) {
					try {
						decodeInformationPoint(ip, event.getTrainData().getTrainCategory()); // Décodage du ou des point(s) d'information
					} catch (InformationPointException e) {
						ip.setCorrupted(true); // Si une erreur se produit lors du décodage, le point d'information est renseigné comme corrompu
						event.addInformationPointException(e);
					}
				}
			}
		}
		
		return event;
	}
	
	/**
	 * Décodage d'un point d'information
	 * 
	 * @param ip : le point d'information
	 * @param trainCategory : la catégorie de train
	 */
	protected void decodeInformationPoint(InformationPoint ip, TrainCategoryEnum trainCategory) throws InformationPointDecoderException {
		List<Marker> markers = ip.getMarkers();
		
		Marker previousMarker = null; // La balise qui précède la balise en cours de traitement
		
		for (int i = 0; i < markers.size(); i++) {
			Marker m = markers.get(i);
			int x = m.getX();
						
			markerSwitch:
			switch (x) {
				case 0:
				case 15:
					// Inutilisé : ignorer
					break;
				case 2:
				case 10:
				case 11:
				case 12:
					// Balise des points d'information CSSP : ignorer
					break;
				case -1:
					break; // X=M : ignorer
				case 1:
				case 4:
					decodeX1X4((MarkerX1X4) m);
					break;
				case 3:
					decodeX3(ip, (MarkerX3X6X9) m);
					break;
				case 5:
					decodeX5(m);
					break;
				case 6:
					decodeX6(ip, (MarkerX3X6X9) m);
					break;
				case 7:
					decodeX7(m);
					break;
				case 8:
				case 13:
					// Dans le cas X=8 ou X=13, le groupement doit être connu et doit respecter un certain format
					
					GroupingEnum grouping = ip.getGrouping();
					
					if (grouping == null) {
						throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__MISSING_GROUPING, String.valueOf(x));
					} else if (previousMarker == null) {
						throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__MISSING_PRECEDING_MARKER, String.valueOf(x));
					} else if (previousMarker.getX() != 3 && previousMarker.getX() != 6) {
						throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_PRECEDING_MARKER, String.valueOf(x));
					} else {
						// Obtention des balises formant le groupement
						Marker s1 = null;
						Marker s2 = null;
						Marker s3 = null;
						
						switch (grouping) {
							case G:
								break markerSwitch;
							case GS1:
								s1 = m;
								break;
							case GS3:
								s3 = m;
								break;
							case GS1S2S3:
								s1 = m;
								s2 = markers.get(++i); // La balise suivante (S2)
								int s2X = s2.getX();
								s3 = markers.get(++i); // La balise suivante (S3)
								int s3X = s3.getX();
	
								if (s2X != 8 && s2X != 13) {
									throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_S2_MARKER, String.valueOf(s2X));
								}
								if (s3X != 8 && s3X != 13) {
									throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_S3_MARKER, String.valueOf(s3X));
								}
								
								break;
							default:
								break;
						}
						
						decodeX8X13(ip, s1, s2, s3, trainCategory.toString(), (MarkerX3X6X9) previousMarker);
					}
					
					break;
				case 9:
					decodeX9((MarkerX3X6X9) m);
					break;
				case 14:
					if (previousMarker != null && previousMarker.getX() == 9) { // Cas général
						decodeX14General((MarkerX14) m, (MarkerX3X6X9) previousMarker);
					} else { // Cas TIVD multi-taux
						decodeX14TIVDMulti((MarkerX14) m);
					}
					break;
				default:
					break;
			}
			
			previousMarker = m;
		}
	}
	
	/**
	 * Décode une balise avec X=1 ou 4	
	 * 
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX1X4(MarkerX1X4 m) throws InformationPointDecoderException {
		DecodingTable X1X4Table= KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4);
		m.setCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4);
		
		DecodingTable X1X4VETable= KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VE);
		DecodingTable X1X4VBTable= KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X1X4VB);

		int x = m.getX();
		int y = m.getY();
		int z = m.getZ();
		
		// Signal GSFC
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_X_TAG + x);
		m.addValue(decodeX(X1X4Table.getTable(), parameters), false);
		
		parameters.clear();
		
		// VE
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		m.setVe(decodeX(X1X4VETable.getTable(), parameters));
		
		parameters.clear();
		
		// VB
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		m.setVb(decodeX(X1X4VBTable.getTable(), parameters));
	}
		
	/**
	 * Décode une balise avec X=3 ou 6
	 * 
	 * @param ip : le point d'information à compléter (le groupement est obtenu dans la méthode)
	 * @param m : la balise
	 * @param tableCode : le nom de la table de décodage
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX3X6(InformationPoint ip, MarkerX3X6X9 m, String tableCode) throws InformationPointDecoderException {
		DecodingTable X3X6Table = KVBLoaderService.getServiceInstance().getDecodingTable(tableCode);
		m.setCode(tableCode);
		m.setUnit(X3X6Table);

		int y = m.getY();
		int z = m.getZ();
		
		// Vitesse
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);		
		m.setDistanceOrSpeed(decodeX(X3X6Table.getTable(), parameters));
		
		parameters.clear();
		
		// Limitation
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_LIMITATION_TAG);
		m.addValue(decodeX(X3X6Table.getTable(), parameters), false);
		
		parameters.clear();
		
		// Groupement
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_GROUPEMENT_TAG);
		String grouping = decodeX(X3X6Table.getTable(), parameters);		
		m.addValue(grouping, false);

		ip.setGrouping(grouping); // Le groupement est informé au niveau du point d'information, afin d'être utilisé ultérieurement lors du décodage d'autres balises
	}
	
	/**
	 * Décode une balise avec X=3
	 * 
	 * @param ip : le point d'information à compléter (le groupement est obtenu dans la méthode)
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX3(InformationPoint ip, MarkerX3X6X9 m) throws InformationPointDecoderException {
		decodeX3X6(ip, m, KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X3);
	}
		
	/**
	 * Décode une balise avec X=5	
	 * 
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX5(Marker m) throws InformationPointDecoderException {
		DecodingTable X5Table= KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5);
		m.setCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X5);

		int y = m.getY();
		int z = m.getZ();
		
		// Frontière de zone
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		m.addValue(decodeX(X5Table.getTable(), parameters), false);
		
		parameters.clear();
		
		// Direction
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_DIRECTION_TAG);
		m.addValue(decodeX(X5Table.getTable(), parameters), false);
	}
		
	/**
	 * Décode une balise avec X=6
	 * 
	 * @param ip : le point d'information à compléter (le groupement est obtenu dans la méthode)
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX6(InformationPoint ip, MarkerX3X6X9 m) throws InformationPointDecoderException {
		decodeX3X6(ip, m, KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X6);
	}
	
	/**
	 * Décode une balise avec X=7
	 * 
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX7(Marker m) throws InformationPointDecoderException {
		DecodingTable X7Table = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7);
		m.setCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X7);
		m.setUnit(X7Table);
		
		int y = m.getY();
		int z = m.getZ();
		
		// Vitesse
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		m.addValue(decodeX(X7Table.getTable(), parameters), true);
		
		parameters.clear();
		
		// Limitation
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_LIMITATION_TAG);
		m.addValue(decodeX(X7Table.getTable(), parameters), true);
	}
	
	/**
	 * Décode une balise avec X=8 ou 13
	 * 
	 * @param ip : le point d'information
	 * @param s1 : la balise S1 du groupement (peut être nulle)
	 * @param s2 : la balise S2 du groupement (peut être nulle)
	 * @param s3 : la balise S3 du groupement (peut être nulle)
	 * @param category : la catégorie de train
	 * @param groupementFirstMarker : la première balise du groupement (X=3 ou 6)
	 * @throws InformationPointDecoderException
	 */
	private void decodeX8X13(InformationPoint ip, Marker s1, Marker s2, Marker s3, String category, MarkerX3X6X9 groupementFirstMarker) throws InformationPointDecoderException {
		DecodingTable X8X13InfosTable = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13BALISESIGNEVARIABLE);
		DecodingTable X8X13GapTable = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13ECART);

		// Détermination de la balise à considérer
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_CATEGORIE_TAG + category);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_BALISE_TAG);
		
		Integer markerNumber = null;
		String markerNumberString = decodeX(X8X13InfosTable.getTable(), parameters);
				
		try {
			markerNumber = Integer.parseInt(markerNumberString);
		} catch (NumberFormatException e) {
			throw new InformationPointDecoderException(e, MessageService.ERROR_IP_DECODING__INVALID_MARKER_NUMBER, markerNumberString);
		}
		
		Marker m = null;
		
		switch (markerNumber) {
			case 1:
				m = s1;
				break;
			case 2:
				m = s2;
				break;
			case 3:
				m = s3;
				break;
			default:
				throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_MARKER_NUMBER_VALUE, markerNumberString);
		}
		
		if (m != null) {
			m.setCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X8X13BALISESIGNEVARIABLE);
			
			parameters.clear();
			
			int x = m.getX();
			int y = m.getY();
			int z = m.getZ();
			
			// Symbole
			parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_CATEGORIE_TAG + category);
			parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_X_TAG + x);
			String symbol = decodeX(X8X13InfosTable.getTable(), parameters);
			
			if (symbol.equals(MarkerX3X6X9.SYMBOL_MINUS) || symbol.equals(MarkerX3X6X9.SYMBOL_PLUS)) {
				groupementFirstMarker.setSymbol(symbol);
			} else {
				throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_SIGNE_SYMBOL, symbol, MarkerX3X6X9.SYMBOL_MINUS, MarkerX3X6X9.SYMBOL_PLUS);
			}
			
			parameters.clear();
			
			// Détermination de la variable à considérer
			parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_CATEGORIE_TAG + category);
			String yValue = y != 0 ? KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_ATTRIBUTE_VALEUR_VALUE_NOT0 : KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_ATTRIBUTE_VALEUR_VALUE_0;
			parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + yValue);
			String variable = decodeX(X8X13InfosTable.getTable(), parameters);
			
			int yzValue = -1;
			
			if (variable.equals(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG)) {
				yzValue = y;
			} else if (variable.equals(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG)) {
				yzValue = z;
			} else {
				throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_VARIABLE_TO_CONSIDER, variable, KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG, KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG);
			}
			
			parameters.clear();
			
			// Écart de vitesse
			parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_YZ_TAG + yzValue);
			groupementFirstMarker.setGap(decodeX(X8X13GapTable.getTable(), parameters)); // Application de l'écart à la première balise du groupement (G)
		} else {
			// La balise d'écart ne fournit pas d'écart, mais le TIVE reste cependant actif
			//ip.setAlertText(LABEL_ALERT_TEXT_INVALID_FOR_TRAIN_CATEGORY); // Si la balise à considérer n'existe pas dans l'événement
		}
	}
	
	/**
	 * Décode une balise avec X=9
	 * 
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX9(MarkerX3X6X9 m) throws InformationPointDecoderException {
		DecodingTable X9Table = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9);
		m.setCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X9);
		m.setUnit(X9Table);

		int y = m.getY();
		int z = m.getZ() == Marker.X_Y_Z_DEFAULT_VALUE ? 0 : m.getZ();
				
		// Distance
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		m.setDistanceOrSpeed(decodeX(X9Table.getTable(), parameters));
	}
	
	/**
	 * Décode une balise avec X=14, dans le cas où la balise précédente est X=9 (cas général)
	 * Décode la distance
	 * 
	 * @param m : la balise
	 * @param m9 : la balise X=9 précédente
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX14General(MarkerX14 m, MarkerX3X6X9 m9) throws InformationPointDecoderException {		
		DecodingTable X14GeneralTable = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14GENERAL);

		int yDist = m9.getY();
		int yDecl = m.getY();
		
		// Écart de distance
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_YDIST_TAG + yDist);
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_YDECL_TAG + yDecl);
		
		String gap = decodeX(X14GeneralTable.getTable(), parameters);
		
		m9.setSymbol(MarkerX3X6X9.SYMBOL_PLUS);
		m9.setGap(gap); // Application de l'écart à la balise X=9
	
		decodeX14Declivite(m); // Détermination de la déclivité
	}
	
	/**
	 * Décode une balise avec X=14, dans le cas où la balise précédente n'est pas X=9 (cas TIVD Multi Taux)
	 * Décode la distance
	 * 
	 * @param m : la balise 
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX14TIVDMulti (MarkerX14 m) throws InformationPointDecoderException {		
		DecodingTable X14TIVDMultiTable = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD);
				
		m.setDistanceCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14TIVD);
		m.setDistanceUnit(X14TIVDMultiTable);
		
		int y = m.getY();
	
		// Distance
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Y_TAG + y);
		m.setDistanceValue(decodeX(X14TIVDMultiTable.getTable(), parameters));
		
		decodeX14Declivite(m); // Détermination de la déclivité
	}
	
	/**
	 * Décode une balise avec X=14
	 * Décode la déclivité
	 * 
	 * @param m : la balise
	 * @throws InformationPointDecoderException 
	 */
	private void decodeX14Declivite(MarkerX14 m) throws InformationPointDecoderException {
		int z = m.getZ();
		
		DecodingTable X14DeclivityTable = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14DECLIVITE);	
		m.setCode(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14DECLIVITE);
		m.setUnit(X14DeclivityTable);
		
		// Déclivité
		List<String> parameters = new ArrayList<String>();
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		String declivity = decodeX(X14DeclivityTable.getTable(), parameters);
		
		parameters.clear();
		
		// Inclinaison
		DecodingTable X14InclinationTable = KVBLoaderService.getServiceInstance().getDecodingTable(KVBLoaderService.XML_TABLE_DECODAGE_ATTRIBUTE_CODE_VALUE_X14INCLINAISON);	
		parameters.add(KVBLoaderService.XML_TABLE_DECODAGE_XVAR_Z_TAG + z);
		String inclination = decodeX(X14InclinationTable.getTable(), parameters);
		
		m.addInclination(inclination, declivity, true);
	}

	/**
	 * Pour une balise donnée, obtient une valeur à partir d'une liste de paramètres
	 * 
	 * @param table : la table de décodage de la balise
	 * @param parameters : les paramètres
	 * @return la valeur
	 * @throws InformationPointDecoderException 
	 */
	private String decodeX (Map<String, KVBXmlVariable> table, List<String> parameters) throws InformationPointDecoderException {
		int i = 0;
		int parameterLevel = 0;
		
		KVBXmlVariable target = table.get(parameters.get(i));
		
		if (target != null) { // Codage inconnu
			parameterLevel++;
			
			for (i = 1; i < parameters.size(); i++) {
				if (target.hasChilds()) { // L'exploration continue dans les fils
					target = target.getChild(parameters.get(i));
					
					if (target == null) { // Codage inconnu
						break;
					}
					
					parameterLevel++;
				} else { // L'exploration s'arrête
					parameterLevel++;
					break;
				}
			}
		}
		
		if (parameterLevel != parameters.size()) {
			throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__UNKNOWN_ENCODING);
		}
				
		return target.getContent();
	}
}
