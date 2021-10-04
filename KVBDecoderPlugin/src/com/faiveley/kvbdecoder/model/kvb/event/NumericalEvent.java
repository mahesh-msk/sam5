package com.faiveley.kvbdecoder.model.kvb.event;

import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.model.atess.event.DescriptorEvent;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBNumericalVariable;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;


public class NumericalEvent extends Event {
	private KVBNumericalVariable kvbVariable = null;
	
	public NumericalEvent(byte[] rawMessage, TrainData trainData, DescriptorEvent eventDescriptor) throws EventDecoderException {
		super(rawMessage, trainData, eventDescriptor);
	}
	
	@Override
	public KVBVariable getKVBVariable() {
		return kvbVariable;
	}

	@Override
	public void setKVBVariable(KVBVariable kvbVariable) {
		this.kvbVariable = (KVBNumericalVariable) kvbVariable;
	}
}
