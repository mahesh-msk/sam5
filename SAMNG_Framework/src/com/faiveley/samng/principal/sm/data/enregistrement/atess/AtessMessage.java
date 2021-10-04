package com.faiveley.samng.principal.sm.data.enregistrement.atess;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.kvbdecoder.decoder.KVBDecoder;
import com.faiveley.kvbdecoder.decoder.KVBDecoderResult;
import com.faiveley.kvbdecoder.exception.EventException;
import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.exception.decoder.TrainCategoryDecoderException;
import com.faiveley.kvbdecoder.model.kvb.event.Event;
import com.faiveley.kvbdecoder.model.kvb.train.TrainCategoryEnum;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.train.TrainDirectionEnum;
import com.faiveley.kvbdecoder.services.loader.KVBLoaderService.TrainInformation;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;

public class AtessMessage extends Message {	
	private static final String ERROR_ATESS_MESSAGE_DECODING__MISSING_TRAIN_EVENT = "messageAtess.decodage.evenementTrainAbsent";
	private static final String ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_EVENT = "messageAtess.decodage.evenementTrainInvalide";
	private static final String ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_CLASS = "messageAtess.decodage.classeTrainInvalide";
	private static final String ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_LENGTH = "messageAtess.decodage.longueurTrainInvalide";
	private static final String ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_LIMIT_SPEED = "messageAtess.decodage.vitesseLimiteTrainInvalide";
	private static final String ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_GAMMA_R1 = "messageAtess.decodage.gammaR1TrainInvalide";
	private static final String ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_GAMMA_R2 = "messageAtess.decodage.gammaR2TrainInvalide";
		
	private AtessMessage trainKvb = null;
	private TrainDirectionEnum trainDirection = TrainDirectionEnum.INCONNU;
	
	private AtessMessageTrainInfo trainInfo = null;
	private KVBDecoderResult decodedEvent = null;
	private List<AtessMessageErrorString> decodingErrors = new ArrayList<AtessMessageErrorString>();
	
	private boolean undatedMessage = false;
		
	public boolean isUndatedMessage() {
		return undatedMessage;
	}

	public void setUndatedMessage(boolean undatedMessage) {
		this.undatedMessage = undatedMessage;
	}

	public AtessMessage getTrainKvb() {
		return trainKvb;
	}

	public void setTrainKvb(AtessMessage trainKvb) {
		this.trainKvb = trainKvb;
	}
	
	public TrainDirectionEnum getTrainDirection() {
		return this.trainDirection;
	}
	
	public void setTrainDirection(TrainDirectionEnum trainDirection) {
		this.trainDirection = trainDirection;
	}
		
	public AtessMessageTrainInfo getTrainInfo() {
		return trainInfo;
	}
	
	public KVBDecoderResult getDecodedEvent() {
		return decodedEvent;
	}
	
	public List<AtessMessageErrorString> getDecodingErrors() {
		return this.decodingErrors;
	}
	
	public void decodeKVBMessage() {
		if (decodedEvent == null) {
			boolean validTrainData = false;
						
			try {
				setTrainData();
				validTrainData = true;
			} catch (AtessMessageException e) {
				boolean sameMsgFound = false;
				
				// Ne pas réafficher une erreur déjà signalée
				if (!decodingErrors.isEmpty()) {
					for (AtessMessageErrorString errorMsg : decodingErrors) {
						if (errorMsg.getValue().equals(e.getMsg())) {
							sameMsgFound = true;
							break;
						}
					}					
				}
				
				if (!sameMsgFound) {
					decodingErrors.add(new AtessMessageErrorString(e.getMsg()));
				}
			}
			
			if (validTrainData) {
				KVBDecoder decoder = KVBDecoder.getDecoderInstance();
				TrainInformation ti = null;
				
				Long trainClassValue = (Long) trainInfo.getTrainClass().getValeurObjet();
				Long trainSpeedValue = (Long) trainInfo.getTrainLimitSpeed().getValeurObjet();
				
				try {
					ti = decoder.getTrainInformation(String.valueOf(trainClassValue));
				} catch (TrainCategoryDecoderException e) {
					boolean sameMsgFound = false;
					
					// Ne pas réafficher une erreur déjà signalée
					if (!decodingErrors.isEmpty()) {
						for (AtessMessageErrorString errorMsg : decodingErrors) {
							if (errorMsg.getValue().equals(e.getMsg())) {
								sameMsgFound = true;
								break;
							}
						}					
					}
					
					if (!sameMsgFound) {
						decodingErrors.add(new AtessMessageErrorString(e.getMsg()));
					}
				}
				
				if (ti != null) {
					TrainCategoryEnum trainCategory = null;
					
					try {
						trainCategory = ti.getCategory(trainSpeedValue);
					} catch (TrainCategoryDecoderException e) {
						boolean sameMsgFound = false;
						
						// Ne pas réafficher une erreur déjà signalée
						if (!decodingErrors.isEmpty()) {
							for (AtessMessageErrorString errorMsg : decodingErrors) {
								if (errorMsg.getValue().equals(e.getMsg())) {
									sameMsgFound = true;
									break;
								}
							}					
						}
						
						if (!sameMsgFound) {
							decodingErrors.add(new AtessMessageErrorString(e.getMsg()));
						}						
					}
					
					if (trainCategory != null) {
						trainInfo.setTrainCategoryLabel(trainCategory);
						
						decodedEvent = decoder.decodeEvent(messageData, new TrainData(trainCategory, trainDirection));
						
						Event event = decodedEvent.getEvent();
						EventException eventError = decodedEvent.getEventError();
						
						if (eventError != null) {
							decodingErrors.add(new AtessMessageErrorString(eventError.getMsg()));
						}
						
						if (event != null) {
							for (InformationPointException ipError : event.getInformationPointExceptions()) {
								decodingErrors.add(new AtessMessageErrorString(ipError.getMsg()));
							}
						}
					}
				}
			}
		}
	}
		
	private void setTrainData() throws AtessMessageException {
		if (trainKvb != null) {			
			VariableDiscrete trainClass;
			VariableAnalogique trainLength;
			VariableAnalogique trainLimitSpeed;
			VariableAnalogique trainGammaR1;
			VariableAnalogique trainGammaR2;
									
			List<VariableComplexe> complexVariables = trainKvb.getVariablesComplexe();
			
			if (complexVariables.size() == 1) {
				VariableComplexe cv = complexVariables.get(0);
				AVariableComposant[] complexVariableSubVariables = cv.getEnfants();
				
				if (complexVariableSubVariables.length == 6) {
					AVariableComposant v1 = complexVariableSubVariables[1];
					
					if (v1 instanceof VariableDiscrete) {
						trainClass = (VariableDiscrete) v1;
					} else {
						throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_CLASS);
					}
					
					AVariableComposant v2 = complexVariableSubVariables[2];
					
					if (v2 instanceof VariableAnalogique) {
						trainLength = (VariableAnalogique) v2;
					} else {
						throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_LENGTH);
					}
					
					AVariableComposant v3 = complexVariableSubVariables[3];
					
					if (v3 instanceof VariableAnalogique) {
						trainLimitSpeed = (VariableAnalogique) v3;
					} else {
						throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_LIMIT_SPEED);
					}
					
					AVariableComposant v4 = complexVariableSubVariables[4];
					
					if (v4 instanceof VariableAnalogique) {
						trainGammaR1 = (VariableAnalogique) v4;
					} else {
						throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_GAMMA_R1);
					}
					
					AVariableComposant v5 = complexVariableSubVariables[5];
					
					if (v5 instanceof VariableAnalogique) {
						trainGammaR2 = (VariableAnalogique) v5;
					} else {
						throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_GAMMA_R2);
					}
					
					trainInfo = new AtessMessageTrainInfo(trainClass, trainLength, trainLimitSpeed, trainGammaR1, trainGammaR2);
				} else {
					throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_EVENT);
				}
			} else {
				throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__INVALID_TRAIN_EVENT);
			}
		} else {
			throw new AtessMessageException(ERROR_ATESS_MESSAGE_DECODING__MISSING_TRAIN_EVENT);
		}
	}
	
	public class AtessMessageTrainInfo {
		private VariableDiscrete trainClass;
		private VariableAnalogique trainLength;
		private VariableAnalogique trainLimitSpeed;
		private VariableAnalogique trainGammaR1;
		private VariableAnalogique trainGammaR2;
		private TrainCategoryEnum trainCategory;
		
		public AtessMessageTrainInfo(VariableDiscrete trainClass, VariableAnalogique trainLength, VariableAnalogique trainLimitSpeed, VariableAnalogique trainGammaR1, VariableAnalogique trainGammaR2) {
			this.trainClass = trainClass;
			this.trainLength = trainLength;
			this.trainLimitSpeed = trainLimitSpeed;
			this.trainGammaR1 = trainGammaR1;
			this.trainGammaR2 = trainGammaR2;
		}

		public VariableAnalogique getTrainLimitSpeed() {
			return trainLimitSpeed;
		}

		public VariableDiscrete getTrainClass() {
			return trainClass;
		}
		
		public void setTrainCategoryLabel(TrainCategoryEnum trainCategory) {
			this.trainCategory = trainCategory;
		}
		
		public Object[] getChilds() {
			return new Object[] {trainClass, trainLength, trainLimitSpeed, trainGammaR1, trainGammaR2, trainCategory};
		}
	}
	
	/**
	 * Classe identique à la classe String.
	 * Elle permet d'avoir des instances différenciables d'instances de String simple (ou d'instances d'autres classes construites de la même manière que AtessMessageErrorString).
	 * Cela est utile dans le cas d'un contexte où les objets sont seulement connus comme étant Object. Exemple: ITableLabelProvider. 
	 */
	public class AtessMessageErrorString {
		private String value;
		
		public AtessMessageErrorString(String value) {
			this.value = value;
		}
		
		public String getValue() {
			return this.value;
		}
	}
}

