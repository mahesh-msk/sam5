package com.faiveley.kvbdecoder.model.kvb.xml;

import java.util.HashMap;
import java.util.Map;

/**
 * Entité qui correspond à un noeud en XML
 * 
 * @author jthoumelin
 *
 */
public class KVBXmlVariable {
	private String type = null; // type du noeud XML
	private String id = null; // type du noeud XML (+ contenu de l'attribut valeur du noeud XML, si attribut valeur non nul)
	private String content = null; // L'équivalent du contenu textuel d'un noeud en XML
	private Map<String, KVBXmlVariable> childs = null;  // L'équivalent des noeuds fils en XML
	
	public KVBXmlVariable(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public void addChild(KVBXmlVariable child) {
		if (childs == null) {
			childs = new HashMap<String, KVBXmlVariable>();
		}
		
		childs.put(child.getId(), child);
	}
	
	public KVBXmlVariable getChild(String value) {
		return childs.get(value);
	}
	
	public boolean hasChilds() {
		return childs != null;
	}
}
