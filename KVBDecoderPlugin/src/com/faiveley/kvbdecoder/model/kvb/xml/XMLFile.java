package com.faiveley.kvbdecoder.model.kvb.xml;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;

public class XMLFile {
	private static final XPathFactory factory = XPathFactory.newInstance();
	
	private Document doc;

	private XPath xpath;
	
	public XMLFile(Document doc) {
		this.doc = doc;
		xpath = factory.newXPath();
	}

	public Document getDoc() {
		return doc;
	}

	public XPath getXpath() {
		return xpath;
	}
}
