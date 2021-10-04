package com.faiveley.samng.principal.ihm.vues;

import java.util.ArrayList;
import java.util.List;

public class VariablesSelect {

	private int msgId=-1;
	private List<Object> vars=new ArrayList<Object>(0);
	
	public boolean setVarSelectionnee(int msgId,Object ob){
		//si ce message n'est pas enregistré
		if (msgId!=this.msgId) {
			//on ajoute le msg et sa variable
			this.msgId=msgId;
			vars=new ArrayList<Object>(1);
			vars.add(ob);
			return true;
		}else{
			//sinon si la variable est déjà contenue on n'ajoute rien
			if (vars.contains(ob)) {
				return false;
			}else{
				//si elle n'est pas contenue, on l'ajoute
				vars.add(ob);
				return true;
			}
		}
	}
	
	public boolean contain(Object o){
		if (vars.contains(o)) {
			return true;
		}else{
			return false;
		}
	}
	
	public void reset(){
		msgId=-1;
		vars=new ArrayList<Object>(0);
	}
	
	public int getMsgId() {
		return msgId;
	}
	public void setMsgId(int msgId) {
		this.msgId = msgId;
	}
	public List<Object> getVars() {
		return vars;
	}
	public void setVars(List<Object> vars) {
		this.vars = vars;
	}
}
