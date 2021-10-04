package com.faiveley.samng.principal.sm.util;

import java.math.BigDecimal;

import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;

/**
 * Classe utile pour centraliser la manière de calculer la distance cumulée
 * @author bleclerc
 *
 */
public class ComputingUtils {

	/**
	 * 
	 * @param e : Enregistrement = tous les évenements du fichier de parcour
	 * @param messagesCount
	 */
	public static void computeAccumulatedDistance(Enregistrement e, int messagesCount) {
		ComputingUtilsAccumulatedDistanceInitParams params = new ComputingUtilsAccumulatedDistanceInitParams(e.getEnfant(0).getAbsoluteDistance());
		for (int k = 0; k < messagesCount; k++) {
			computeAccumulatedDistance(e.getEnfant(k), params);
		}
	}
	
	/**
	 * 
	 * @param msg
	 * @param initDistance
	 * @param segmentAccumulatedDistance
	 * @param totalAccumulatedDistance
	 */
	public static void computeAccumulatedDistance(Message msg, ComputingUtilsAccumulatedDistanceInitParams params) {
		BigDecimal bd = new BigDecimal(msg.getAbsoluteDistance());
		bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
		
		double absoluteDistance = bd.doubleValue();
		double accumulatedDistance = 0.0;
		
		if (!msg.isErrorMessage()) {
			if (absoluteDistance >= params.getSegmentAccumulatedDistance()) {
				params.setSegmentAccumulatedDistance(absoluteDistance);
				accumulatedDistance = params.getTotalAccumulatedDistance() + params.getSegmentAccumulatedDistance();
			} else {
				params.setTotalAccumulatedDistance(params.getTotalAccumulatedDistance() + params.getSegmentAccumulatedDistance());
				params.setSegmentAccumulatedDistance(0.0);

				accumulatedDistance = params.getTotalAccumulatedDistance();
			}
		} else {
			accumulatedDistance = params.getTotalAccumulatedDistance() + params.getSegmentAccumulatedDistance();
		}
		
		msg.setAccumulatedDistance(accumulatedDistance - params.getInitDistance());
	}
}
