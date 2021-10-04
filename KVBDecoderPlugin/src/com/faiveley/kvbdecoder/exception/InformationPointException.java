package com.faiveley.kvbdecoder.exception;

import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;

/**
 * Exception KVB levée relative au point d'information
 * 
 * @author jthoumelin
 *
 */
public abstract class InformationPointException extends KVBException {
	private static final long serialVersionUID = 2920393071669969104L;
	
	private InformationPoint ip = null;
	
		
	public InformationPointException(String key, String... values) {
		super(key, values);
	}

	public InformationPointException(Throwable cause, String key, String... values) {
		super(cause, key, values);
	}

	public InformationPointException(String key, InformationPoint ip) {		
		super(key, ip.getXSequence());
		setIp(ip);
	}

	public InformationPoint getIp() {
		return ip;
	}

	private void setIp(InformationPoint ip) {
		this.ip = ip;
	}
}
