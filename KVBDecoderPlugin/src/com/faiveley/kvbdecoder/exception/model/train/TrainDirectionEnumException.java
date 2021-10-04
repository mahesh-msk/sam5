package com.faiveley.kvbdecoder.exception.model.train;

import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;

public class TrainDirectionEnumException extends EventDecoderException {
	private static final long serialVersionUID = 6368862388766338241L;

	private static final String ERROR_EVENT_DECODING__INVALID_TRAIN_DIRECTION = "KVB.erreur.decodage.evenement.sensTrainInvalide";
	
	public TrainDirectionEnumException(String trainDirection) {
		super(ERROR_EVENT_DECODING__INVALID_TRAIN_DIRECTION, trainDirection);
	}
	
	public TrainDirectionEnumException( Throwable cause, String trainDirection) {
		super(cause, ERROR_EVENT_DECODING__INVALID_TRAIN_DIRECTION, trainDirection);
	}
}