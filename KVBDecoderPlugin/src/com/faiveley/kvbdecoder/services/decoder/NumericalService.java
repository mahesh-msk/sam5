package com.faiveley.kvbdecoder.services.decoder;

import java.util.List;
import java.util.Map;

import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.exception.decoder.InformationPointDecoderException;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX14;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX1X4;
import com.faiveley.kvbdecoder.model.kvb.marker.MarkerX3X6X9;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.train.TrainDirectionEnum;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBNumericalVariable;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;
import com.faiveley.kvbdecoder.model.kvb.xml.DescriptorNumericalInformationPoint;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.message.MessageService;

/**
 * Service qui guide le décodage d'une balise numérique.
 * 
 * @author jthoumelin
 *
 */
public class NumericalService {
	private static final int BALISESN2_COMPLEXVARIABLE_CODE = 54;
	
	private static final String Y_LABEL = "Y";
	private static final String Z_LABEL = "Z";
	
	private static final int END_MESSAGE_BYTE = 15;
	
	/**
	 * Le singleton
	 */
	private static NumericalService SERVICE_INSTANCE = new NumericalService();
	
	/**
	 * Le constructeur vide
	 */
	private NumericalService() {}
	
	/**
	 * Obtention du singleton
	 */
	protected static NumericalService getServiceInstance() {
		return SERVICE_INSTANCE;
	}	
	
	/**
	 * Décodage d'une balise numérique
	 * 
	 * @param: v : la variable KVB numérique
	 * @param trainData : les informations sur le train
	 * @throws EventDecoderException 
	 */
	protected void decodeFromNumerical(KVBNumericalVariable v, TrainData trainData) throws EventDecoderException {				 
		 int codeVariable = v.getDescriptor().getCode();
		 
		 if (codeVariable == BALISESN2_COMPLEXVARIABLE_CODE) {
			String KVBMessage = v.getValue();
			int offset = 2;
			int ipIndex = 0;
			
			try {
				while (true) {
					int xsm_xsd = Integer.parseInt(KVBMessage.substring(offset, offset += 2), 16);
					
					if (xsm_xsd != END_MESSAGE_BYTE) {
						DescriptorNumericalInformationPoint descriptor = null;

						Map<Integer, DescriptorNumericalInformationPoint> xsmTable = KVBLoaderService.getServiceInstance().getNumericalIpDescriptorTableKeyXsm();
						Map<Integer, DescriptorNumericalInformationPoint> xcsTable = KVBLoaderService.getServiceInstance().getNumericalIpDescriptorTableKeyXcs();
						
						int xsm = InformationPoint.XSM_XCS_DEFAULT_VALUE;
						int xcs = InformationPoint.XSM_XCS_DEFAULT_VALUE;

						if (xsmTable.containsKey(xsm_xsd)) {
							descriptor = KVBLoaderService.getServiceInstance().getNumericalIpDescriptorTableKeyXsm().get(xsm_xsd);
							xsm = xsm_xsd;
						}
						
						if (xcsTable.containsKey(xsm_xsd)) {
							if (descriptor == null) {
								descriptor = KVBLoaderService.getServiceInstance().getNumericalIpDescriptorTableKeyXcs().get(xsm_xsd);
							}
							
							xcs = xsm_xsd;
						}
						
						if (descriptor != null) {
							TrainDirectionEnum trainDirection = trainData.getTrainDirection();
							boolean rightDirection = trainDirection.equals(TrainDirectionEnum.INCONNU) || xsm == xcs || trainDirection.equals(TrainDirectionEnum.SENS_DE_MARCHE) && xsm != InformationPoint.XSM_XCS_DEFAULT_VALUE ||  trainDirection.equals(TrainDirectionEnum.CONTRE_SENS) && xcs != InformationPoint.XSM_XCS_DEFAULT_VALUE;
							
							InformationPoint ip = buildIp(v, KVBMessage, offset, descriptor, xsm, xcs, rightDirection, ipIndex);
							ipIndex++;
							
							v.addInformationPoint(ip);
							offset += descriptor.getQuartets().size();
						} else {
							throw new InformationPointDecoderException(MessageService.ERROR_IP_DECODING__INVALID_XSM_XCS_VALUE, String.valueOf(xsm_xsd));
						}
					} else {
						break;
					}
				}
			} catch (InformationPointException e) {
				v.getParent().addInformationPointException(e);
			}
		} else {
			throw new EventDecoderException(MessageService.ERROR_EVENT_DECODING__INVALID_NUMERICAL_VARIABLE_CODE, String.valueOf(v.getParent().getEventDescriptor().getCode()), String.valueOf(codeVariable), String.valueOf(BALISESN2_COMPLEXVARIABLE_CODE));
		}
	 }
	 
 
	 /**
	  * Construction d'un point d'information pour une balise numérique
	  * 
	  * @param parent : l'événement parent
	  * @param KVBMessage : le message encodé
	  * @param offset : l'offset de lecture
	  * @param descriptor : le descripteur
	  * @param xsm : le xsm du point d'information (peut être nul)
	  * @param xsm : le xcs du point d'information (peut être nul)
	  * @param ipIndex : le numéro du point d'information au sein de l'événement
	  * @return : le point d'information
	  * @throws InformationPointDecoderException 
	  */
	 private InformationPoint buildIp(KVBVariable parent, String KVBMessage, int offset, DescriptorNumericalInformationPoint descriptor, int xsm, int xcs, boolean rightDirection, int ipIndex) throws InformationPointDecoderException {		 		 		 		
		Map<String, String> labels = KVBLoaderService.getServiceInstance().getInformationPointLabels(UtilService.join(",", descriptor.getXSequence()));
		 
		InformationPoint ip = new InformationPoint(parent, xsm, xcs, rightDirection, ipIndex);
		ip.setXSequence(UtilService.join(",", descriptor.getXSequence()));
		ip.setLabels(labels);
		
		int indexQuartets = 0;
		int indexX = 1;
	
		List<String> quartets = descriptor.getQuartets();
		
		for (Integer x : ((DescriptorNumericalInformationPoint) descriptor).getXSequence()) { // Création des objets Marker
			Marker m = null;
			
			UtilService.isValidX(x);
			
			if (UtilService.isX1X4(x)) {
				m =  new MarkerX1X4(ip, x);
			} else if (UtilService.isX3X6X9(x)) {
				m =  new MarkerX3X6X9(ip, x);
			} else if (UtilService.isX14(x)) {
				m = new MarkerX14(ip, x);
			} else {
				m = new Marker(ip, x);
			}
			
			// Détermination de X et Y
			if (x != Marker.MARKER_M_VALUE) {
				String q0 = quartets.get(indexQuartets);
				
				if (q0.equals(Y_LABEL + indexX)) {
					m.setY(KVBMessage.substring(offset, ++offset));
					
					String q1 = quartets.get(++indexQuartets);
					
					if (q1.equals(Z_LABEL + indexX)) {
						m.setZ(KVBMessage.substring(offset, ++offset));
						indexQuartets++;
					}
				} else if (q0.equals(Z_LABEL + indexX)) {
					m.setZ(KVBMessage.substring(offset, ++offset));
					indexQuartets++;
				}
			}
			
			ip.addMarker(m);
			indexX++;
		}
		
		return ip;
	}
}
