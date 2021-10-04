package com.faiveley.kvbdecoder.exception.xml;

public class XMLFileMissingException extends XMLException {
	private static final long serialVersionUID = -4065643266393172442L;

	public XMLFileMissingException(String fileName, String key, String... values) {
		super(fileName, key, values);
	}
	
	public XMLFileMissingException(Throwable cause, String fileName, String key, String... values) {
		super(cause, fileName, key, values);
	}
}
