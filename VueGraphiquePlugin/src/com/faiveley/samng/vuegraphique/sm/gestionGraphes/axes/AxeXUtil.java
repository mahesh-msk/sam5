package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.segments.ASegment;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AxeXUtil {
    private static long TEMPS_3_JOURS = 3 * 24 * 3600 * 1000;
    private static long TEMPS_1_HEURE = 3600 * 1000;
    private static long TEMPS_3_MN = 3 * 60 * 1000;

	public static double getAxeXValue(Message message, TypeAxe typeAxe) {
		switch (typeAxe) {
			case AXE_TEMPS:
				return message.getAbsoluteTime();
			case AXE_DISTANCE:
				return message.getAbsoluteDistance();	
			case AXE_TEMPS_CORRIGE:
				//check if the message is in a segment with corrected time
				AVariableComposant tempCorigee = message.getVariable(TypeRepere.tempsCorrigee.getCode());
				if(tempCorigee != null) {
					return ConversionTemps.getMillis( new String((byte[])tempCorigee.getValeur()));//tagValCor
				} else {
					//otherwise return the absolute time
					return message.getAbsoluteTime();
				}
			case AXE_DISTANCE_CORRIGEE:
				//check if the message is in a segment with corrected distance
				AVariableComposant distanceCorigee = message.getVariable(TypeRepere.distanceCorrigee.getCode());
				if(distanceCorigee != null) {
//					double d= (Double)distanceCorigee.getCastedValeur();
//					DecimalFormat fmt = new DecimalFormat("0.000");
//					String distStr = (fmt.format((double) d)).replace(",",
//							".");	
//					return Double.parseDouble(distStr);
					return Double.parseDouble(new String((byte[])distanceCorigee.getValeur()));//tagValCor
				} else {
					//otherwise return the absolute distance
					return message.getAbsoluteDistance();
				}
			default: 
				return message.getAbsoluteTime();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static HashMap<Integer, ASegment> getSegments(TypeAxe typeAxe) {
		HashMap<Integer, ASegment> segments = null;
		if(typeAxe == TypeAxe.AXE_TEMPS || typeAxe == TypeAxe.AXE_TEMPS_CORRIGE)
			segments = (HashMap)TableSegments.getInstance().getSegmentsTemps();
		else if(typeAxe == TypeAxe.AXE_DISTANCE  || typeAxe == TypeAxe.AXE_DISTANCE_CORRIGEE)
			segments = (HashMap)TableSegments.getInstance().getSegmentsDistance();
		return segments;
	}
	
	public static ASegment getSegment(HashMap<Integer, ASegment> segments, int numero, TypeAxe typeAxe) {
		ASegment segment = null;
		if(typeAxe == TypeAxe.AXE_TEMPS || typeAxe == TypeAxe.AXE_TEMPS_CORRIGE)
			segment = TableSegments.getInstance().getSegmentTemps(numero);
		else if(typeAxe == TypeAxe.AXE_DISTANCE  || typeAxe == TypeAxe.AXE_DISTANCE_CORRIGEE)
			segment = TableSegments.getInstance().getSegmentDistance(numero);
		return segment;
	}
	
	public static int computeFormatValeur(double cumul, TypeAxe typeAxe) {
		int formatValeur = 0;
        if(typeAxe == TypeAxe.AXE_TEMPS || typeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
	        if (cumul <= TEMPS_3_MN) {
	        	formatValeur = 0; //"ss:d";
	        } else {
	            if (cumul <= TEMPS_1_HEURE) {
	            	formatValeur = 1; //"mm:ss";
	            } else {
	                if (cumul <= TEMPS_3_JOURS) {
	                	formatValeur = 2; //"HH:mm";
	                } else {
	                	formatValeur = 3; //"JJ/MM/AAAA HH";
	                }
	            }
	        }
        }
        return formatValeur;
	}
	
	public static String getConvertedValueForXAxis(boolean firstgrad,double coefgrad,double origine,Object currentValue2){
		TypeAxe typeAxe = GestionnaireAxes.getInstance().getCurrentAxeType();
		String label="";	
		if(typeAxe == TypeAxe.AXE_TEMPS || typeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
	        label=ConversionTemps.getFormattedDate((Long)currentValue2,true);
				
	        switch (GestionnaireAxes.getInstance().getCurrentAxeX().getFormateValeur()) {
	        case 0:
	            label = label.substring(17, 19) + "s" + label.substring(20, 21);
	            break;
	
	        case 1:
	            label = label.substring(14, 16) + "mn" + label.substring(17, 19);
	            break;
	
	        case 2:
	            label = label.substring(10, 13) + "h" + label.substring(14, 16);
	            break;
	
	        case 3:
	            label = label.substring(0, 13) + "h";
	
	            break;
	        }
	        return label;
		} else {
			
			double firstgradcoef=0;
			double coef=1;
			try {
				coef=GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getCoefDirecteur();			
			} catch (Exception e) {
				
			}
			if (coefgrad<coef)
				coefgrad=coef;			
			
			coefgrad=0.01;
			if (firstgrad){	
				firstgradcoef=coefgrad*0.01;
				float lab=	(float)((Math.round(((Double)currentValue2)/firstgradcoef))*firstgradcoef);
				return String.valueOf(lab);
			}else{
				float lab=	(float)((Math.round(((Double)currentValue2)/coefgrad))	*coefgrad);						
				return String.valueOf(lab);
			}
		}
    }
	
	public static String getConvertedValue(double currentValue, double origine, 
			TypeAxe typeAxe, int valueFormat,boolean firstgrad,double coefgrad) {
		if(typeAxe == TypeAxe.AXE_TEMPS || typeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
	        String label=ConversionTemps.getFormattedDate((long)(currentValue+origine),true);
	
	        switch (valueFormat) {
	        case 0:
	            label = label.substring(17, 19) + "s" + label.substring(20, 21);
	            break;
	
	        case 1:
	            label = label.substring(14, 16) + "mn" + label.substring(17, 19);
	            break;
	
	        case 2:
	            label = label.substring(10, 13) + "h" + label.substring(14, 16);
	            break;
	
	        case 3:
	            label = label.substring(0, 13) + "h";
	
	            break;
	        }
	        return label;
		} else {
			
			double firstgradcoef=0;
			double coef=GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getCoefDirecteur();
			if (coefgrad<coef)
				coefgrad=coef;
						
			if (firstgrad){	
				firstgradcoef=coefgrad*0.01;
				float label=	(float)((Math.round((currentValue)/firstgradcoef))*firstgradcoef);
//				float label=	(float)((Math.round((currentValue + origine)/firstgradcoef))*firstgradcoef);
				
				return String.valueOf(label);
			}else{
				float label=	(float)(origine+(Math.round(currentValue/coefgrad))	*coefgrad);						
//				float label=	(float)((Math.round((currentValue + origine)/coefgrad))	*coefgrad);						
				return String.valueOf(label);
			}
		}
    }
	
	
	
	
	/**
	 * Returns the breaks (ruptures) for the given axe type
	 * @param typeAxe the type of axe
	 * @return the map containing ruptures
	 */
	public static HashMap<Integer, TypeRupture> getRuptures(TypeAxe typeAxe) {
		HashMap<Integer, TypeRupture> ruptures;
		if(typeAxe == TypeAxe.AXE_TEMPS || typeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
			ruptures = TableRuptures.getInstance().getListeRupturesTemps();
		} else {
			ruptures = TableRuptures.getInstance().getListeRupturesDistance();
		}
		return ruptures;
	}
	
	public static List<Integer> getRuptures(TypeAxe typeAxe, int startMsgId, int endMsgId) {
		HashMap<Integer, TypeRupture> ruptures = getRuptures(typeAxe);
        AParcoursComposant data = ActivatorData.getInstance().getVueData()
									.getDataTable();
        
//		récupération uniquement des bons messages
		ListMessages messages = data.getEnregistrement().getMessages();
        
		//récupération de tous les messages
//		List<Message> messages = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
		
        Message prevRuptureMsg;
        int msgIdx;

		//Maybe a type rupture is needed a Linked-HashMap is needed to be returned 
		List<Integer> msgIds = new ArrayList<Integer>();
		msgIds.add(startMsgId);		//add the first msg ID
		if(ruptures != null) {
			for(Integer msgId: ruptures.keySet()) {
				if(msgId > startMsgId && msgId < endMsgId) {
					//we have a break in the segment so, we have to add the previous message
					//before the rupture and the rupture itself
					msgIdx = messages.indexOf(data.getEnregistrement().getGoodMessage(msgId));
					prevRuptureMsg = messages.get(msgIdx - 1);
					//add the previous message before rupture
					//check that this msg ID does not exists already in list
					if(!msgIds.contains(prevRuptureMsg.getMessageId())) {
						msgIds.add(prevRuptureMsg.getMessageId());
					}
					//add now the rupture itself
					//check that this msg ID does not exists already in list
					if(!msgIds.contains(msgId)) {
						msgIds.add(msgId);
					}
				}
			}
		}
		//if not already added the last id, add it 
		if(!msgIds.contains(endMsgId))
			msgIds.add(endMsgId);
		return msgIds;
	}
}
