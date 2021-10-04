package com.faiveley.kvbdecoder.model.kvb.event;

import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.model.atess.event.DescriptorEvent;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBAnalogVariable;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;


public class AnalogEvent extends Event {
	private KVBAnalogVariable kvbVariable = null;
	
	public AnalogEvent(byte[] rawMessage, TrainData trainData, DescriptorEvent eventDescriptor) throws EventDecoderException {
		super(rawMessage, trainData, eventDescriptor);
	}

	@Override
	public KVBVariable getKVBVariable() {
		return kvbVariable;
	}

	@Override
	public void setKVBVariable(KVBVariable kvbVariable) {
		this.kvbVariable = (KVBAnalogVariable) kvbVariable;
	}
}
