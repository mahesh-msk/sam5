package com.faiveley.kvbdecoder.model.kvb.event;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.kvbdecoder.exception.InformationPointException;
import com.faiveley.kvbdecoder.exception.decoder.EventDecoderException;
import com.faiveley.kvbdecoder.model.atess.event.DescriptorEvent;
import com.faiveley.kvbdecoder.model.kvb.train.TrainData;
import com.faiveley.kvbdecoder.model.kvb.variable.KVBVariable;
import com.faiveley.kvbdecoder.services.decoder.UtilService;
import com.faiveley.kvbdecoder.services.message.MessageService;




public abstract class Event {
	// Codes ATESS des événements gérés par le décodeur
	public static final int BALISES123_EVENT_CODE = 117;
	public static final int BALISES45_EVENT_CODE = 118;
	public static final int BALISESN2_EVENT_CODE = 120;
	
	private DescriptorEvent eventDescriptor;
	private byte[] rawMessage;
	private String message;
	private TrainData trainData;
	private List<InformationPointException> informationPointExceptions;	

	public TrainData getTrainData() {
		return trainData;
	}

	public void setTrainData(TrainData trainData) {
		this.trainData = trainData;
	}

	public DescriptorEvent getEventDescriptor() {
		return eventDescriptor;
	}

	public void setEventDescriptor(DescriptorEvent eventDescriptor) {
		this.eventDescriptor = eventDescriptor;
	}
	
	public String getMessage() {
		return message;
	}
				
	public abstract KVBVariable getKVBVariable();

	public abstract void setKVBVariable(KVBVariable kvbVariable);
	
	public List<InformationPointException> getInformationPointExceptions() {
		return informationPointExceptions;
	}
	
	public void addInformationPointException(InformationPointException e) {
		informationPointExceptions.add(e);
	}

	public Event(byte[] rawMessage, TrainData trainData, DescriptorEvent eventDescriptor) throws EventDecoderException {
		this.trainData = trainData;
		this.eventDescriptor = eventDescriptor;
		this.rawMessage = rawMessage;
		
		if (this.rawMessage != null) {
			message = UtilService.bytesToHex(rawMessage);
			
			if (message.length() != eventDescriptor.getLength()) {
				throw new EventDecoderException(MessageService.ERROR_EVENT_DECODING__INVALID_LENGTH, String.valueOf(message.length()), String.valueOf(eventDescriptor.getLength()));
			}
		}
		
		informationPointExceptions = new ArrayList<InformationPointException>();
	}
		
	public static boolean isKVBEvent(int code) {		
		return code == BALISES123_EVENT_CODE  || code == BALISES45_EVENT_CODE || code == BALISESN2_EVENT_CODE;
	}
	
	public static boolean isAnalogEvent(int code) {		
		return code == BALISES123_EVENT_CODE  || code == BALISES45_EVENT_CODE;
	}
	
	public static boolean isNumericEvent(int code) {		
		return code == BALISESN2_EVENT_CODE;
	}
}
