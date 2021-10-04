package com.faiveley.kvbdecoder.exception.model.train;

import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;

public class TrainCategoryEnumException extends EventDecoderException {
	private static final long serialVersionUID = 6368862388766338241L;
	
	private static final String ERROR_EVENT_DECODING__INVALID_TRAIN_CATEGORY = "KVB.erreur.decodage.evenement.categorieTrainInvalide";

	public TrainCategoryEnumException(String trainCategory) {
		super(ERROR_EVENT_DECODING__INVALID_TRAIN_CATEGORY, trainCategory);
	}
	
	public TrainCategoryEnumException(Throwable cause, String trainCategory) {
		super(cause, ERROR_EVENT_DECODING__INVALID_TRAIN_CATEGORY, trainCategory);
	}
}