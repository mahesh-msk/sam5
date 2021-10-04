package com.faiveley.samng.principal.sm.data.tableAssociationComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:23
 */
public class ParcoursComposite extends AParcoursComposant {
	private AParcoursComposant entete;
	private AParcoursComposant tableAssos;
	private AParcoursComposant data;
	private AParcoursComposant info;
	private AParcoursComposant reperes;
	
	public AParcoursComposant getEntete() {
		return entete;
	}
	public void setEntete(AParcoursComposant entete) {
		this.entete = entete;
	}
	public AParcoursComposant getTableAssos() {
		return tableAssos;
	}
	public void setTableAssos(AParcoursComposant tableAssos) {
		this.tableAssos = tableAssos;
	}
	public AParcoursComposant getData() {
		return data;
	}
	public void setDatas(AParcoursComposant data) {
		this.data = data;
	}
	public AParcoursComposant getInfo() {
		return info;
	}
	public void setInfo(AParcoursComposant info) {
		this.info = info;
	}
	public AParcoursComposant getReperes() {
		return reperes;
	}
	public void setReperes(AParcoursComposant reperes) {
		this.reperes = reperes;
	}

	public ParcoursComposite() {}
	
	public void clear(){
		this.entete = null;
		this.tableAssos = null;
		this.data = null;
		this.info = null;
		this.reperes = null;
	}
}