package com.faiveley.samng.principal.sm.calculs.conversionTemps;

import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;

public class ConversionTempsTomNg extends ConversionTemps {

	public ConversionTempsTomNg(String startDate) {
		super(startDate);
	}

	public static long getDateFromTypeRepereDate(Message msg, TypeRepere tr) {
		if (msg.getVariable(tr.getCode()) == null) {
			return 0;
		}
		long date = (Long) (msg.getVariable(TypeRepere.date.getCode())
				.getCastedValeur());

		return date;
	}

	public static long getTempsFromTypeRepereTemps(Message msg, TypeRepere trT,
			TypeRepere trD) {
		if (msg.getVariable(trT.getCode()) == null
				|| msg.getVariable(trD.getCode()) == null) {
			return 0;
		}
		long temps = Long.valueOf(msg.getVariable(trT.getCode())
				.getCastedValeur().toString());
		return temps;
	}
}
