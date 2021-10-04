package com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML;

import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;

public class ParseurXML1Explorer extends ParseurXML1 {
	
	private static ParseurXML1 instance;
	
	public static ParseurXML1 getInstance() {
		if (instance==null) {
			if (BridageFormats.getInstance().getFormatFichierOuvert("")!=null){
				FormatSAM fS=BridageFormats.getInstance().getFormatFichierOuvert("");
				if (fS==FormatSAM.TOMNG || fS==FormatSAM.ATESS || fS==FormatSAM.TOM4) {
					
						instance=new ParseurXML_NG_UK_ATESS_Explorer();
					
					return instance;
				}else if (fS==FormatSAM.JRU) {
					
						instance=new ParseurXML_JRUExplorer();
					
					return instance;
				}else
					return null;
			}else
				return null;
		}else{
			return instance;
		}
	}
}
