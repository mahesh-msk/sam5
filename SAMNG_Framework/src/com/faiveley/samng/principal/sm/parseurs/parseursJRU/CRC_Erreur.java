package com.faiveley.samng.principal.sm.parseurs.parseursJRU;

import com.faiveley.samng.principal.sm.controles.ReturnCRC;

public class CRC_Erreur {
	private int indice;
	private ReturnCRC ret;
	
	public CRC_Erreur(int indice, ReturnCRC ret) {
		super();
		this.indice = indice;
		this.ret = ret;
	}

	public int getIndice() {
		return indice;
	}

	public ReturnCRC getRet() {
		return ret;
	}
}
