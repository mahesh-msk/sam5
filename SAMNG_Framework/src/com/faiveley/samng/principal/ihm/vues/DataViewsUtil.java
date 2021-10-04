package com.faiveley.samng.principal.ihm.vues;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class DataViewsUtil {
	
	/**
	 * Gets the text for the corrections to be appended to the view title
	 *  
	 * @return
	 */
	public static String getCorrectionsTitleString(Message msg) {
		String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
		String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
		String minute = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
		String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
		String milliseconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");
		String ret = null;
		String timeCorrectionStr = null;
		String wheelDiameterCorrectionStr = null;
		
		if(TableSegments.getInstance().isAppliedTempCorrections()) {
			timeCorrectionStr = "###"; //$NON-NLS-1$
			if(msg != null) {
				AVariableComposant tempCorigee = msg.getVariable(TypeRepere.tempsCorrigee.getCode());
				if(tempCorigee != null ) {
					timeCorrectionStr =  new String((byte[])tempCorigee.getValeur());//tagValCor
					if(!ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige").equals(true))
						timeCorrectionStr="###";
					if(!"###".equals(timeCorrectionStr)) { //$NON-NLS-1$
						String tempsInitial=TableSegments.getInstance().getContainingTempSegment(msg.getMessageId()).getTempInitial();
						String tempsCorrige=TableSegments.getInstance().getContainingTempSegment(msg.getMessageId()).getTempCorrige();
						
						long diff=ConversionTemps.calculatePeriodAsLong(tempsInitial,tempsCorrige);
						timeCorrectionStr=ConversionTemps.getRelativeTimeAsString(diff,jour,heure,minute,seconde,milliseconde);
					}
				} 
			}
			timeCorrectionStr = Messages.getString("DataViewsUtil.2") + timeCorrectionStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(TableSegments.getInstance().isAppliedDistanceCorrections()) {
			if (wheelDiameterCorrectionStr == null)
				wheelDiameterCorrectionStr = "###"; //$NON-NLS-1$
			if(msg != null) {
				AVariableComposant distCorigee = msg.getVariable(TypeRepere.distanceCorrigee.getCode());
				if(distCorigee != null) {
					wheelDiameterCorrectionStr = new String((byte[])distCorigee.getValeur());//tagValCor
					if(!ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige").equals(true))
						wheelDiameterCorrectionStr="###";
					if(!"###".equals(wheelDiameterCorrectionStr)) { //$NON-NLS-1$
						SegmentDistance segDist = TableSegments.getInstance().
											getContainingDistanceSegment(msg.getMessageId());
						if(segDist != null) {
							double diff = segDist.getDiameterCorrige() - segDist.getInitialDiameter();
							
							wheelDiameterCorrectionStr = String.valueOf(diff);
						}
					}
				} 
			}
			wheelDiameterCorrectionStr = Messages.getString("DataViewsUtil.6") + wheelDiameterCorrectionStr + ")"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(timeCorrectionStr != null)
			ret = timeCorrectionStr;
		if(wheelDiameterCorrectionStr != null) {
			if(ret == null)
				ret = wheelDiameterCorrectionStr;
			else 
				ret += wheelDiameterCorrectionStr;
		}
		
		return ret;
	}

}
