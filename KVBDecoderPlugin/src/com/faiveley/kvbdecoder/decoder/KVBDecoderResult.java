package com.faiveley.kvbdecoder.decoder;

import com.faiveley.kvbdecoder.exception.EventException;
import com.faiveley.kvbdecoder.model.kvb.event.Event;


/**
 * Classe qui permet deux stocker deux résultats en un: le message décodé, une éventuelle erreur.
 * 
 * @author jthoumelin
 *
 */
public class KVBDecoderResult {
	private Event event;
	private EventException eventError;
	
	public KVBDecoderResult() {}
	
	public Event getEvent() {
		return event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public EventException getEventError() {
		return eventError;
	}

	public void setEventError(EventException eventError) {
		this.eventError = eventError;
	}
}
