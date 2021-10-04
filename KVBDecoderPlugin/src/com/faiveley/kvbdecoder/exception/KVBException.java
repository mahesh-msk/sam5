package com.faiveley.kvbdecoder.exception;

/**
 * Exception KVB
 * 
 * @author jthoumelin
 *
 */
public abstract class KVBException extends Exception {
	private static final long serialVersionUID = -8229800325523537618L;

	protected String key;
	protected String[] values;
	protected String msg;

	public KVBException(String key, String... values) {
		super();
		this.key = key;
		this.values = values;
		buildMessage();
	}

	public KVBException(Throwable cause, String key, String... values) {
		super(cause);
		this.key = key;
		this.values = values;
		buildMessage();
	}
		
	public String getKey() {	
		return key;
	}
	
	public String getMsg() {
		return msg;
	}
	
	protected abstract void buildMessage();
}
