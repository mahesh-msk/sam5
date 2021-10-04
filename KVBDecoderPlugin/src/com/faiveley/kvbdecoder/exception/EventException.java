package com.faiveley.kvbdecoder.exception;

/**
 * Exception KVB relative à l'événement
 * 
 * @author jthoumelin
 *
 */
public abstract class EventException extends KVBException {
	private static final long serialVersionUID = -8943574721391072370L;
		
	public EventException(String key, String... values) {
		super(key, values);
	}
	
	public EventException(Throwable cause, String key, String... values) {
		super(cause, key, values);
	}
}