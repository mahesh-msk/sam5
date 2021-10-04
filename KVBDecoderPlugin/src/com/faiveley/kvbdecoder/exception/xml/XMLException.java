package com.faiveley.kvbdecoder.exception.xml;

import com.faiveley.kvbdecoder.exception.EventException;
import com.faiveley.kvbdecoder.services.message.MessageService;


/**
 * Exception KVB relative à l'événement, et levée lors du chargement des données XML
 * 
 * @author jthoumelin
 *
 */
public class XMLException extends EventException {
	private static final long serialVersionUID = 6368862388766338241L;

	private static final String ERROR_XMLLOADING = "KVB.erreur.chargementXml";
	
	private String fileName;
	
	public XMLException(String fileName, String key, String... values) {
		super(key, values);
		this.fileName = fileName;
		buildXmlMessage();
	}

	public XMLException(Throwable cause, String fileName, String key, String... values) {
		super(cause, key, values);
		this.fileName = fileName;
		buildXmlMessage();
	}
		
	protected void buildMessage() {}

	protected void buildXmlMessage() {
		String detailedMsg;
		
		if (key == null && getCause() != null) {
			key = getCause().getClass().getName();
			detailedMsg = getCause().getMessage();
		} else {
			detailedMsg = String.format(MessageService.getServiceInstance().getString(key, XMLException.class), (Object[]) values);
		}
		
		msg = String.format(MessageService.getServiceInstance().getString(ERROR_XMLLOADING, XMLException.class), fileName, detailedMsg);
	}
}
