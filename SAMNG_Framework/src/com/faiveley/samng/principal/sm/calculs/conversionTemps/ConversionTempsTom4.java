package com.faiveley.samng.principal.sm.calculs.conversionTemps;

import java.text.ParseException;

import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;

public class ConversionTempsTom4 extends ConversionTemps {
	public ConversionTempsTom4(String startDate) {
		super(startDate);
	}

	public static long getDateFromTypeRepereDate(Message msg, TypeRepere tr) {
		String date = (String) (msg.getVariable(tr.getCode()).getCastedValeur());
		String dateEnJour = date.substring(0, 8);
		long newDateValue = 0;
		String origine = "01011990";
		try {
			newDateValue = ConversionTemps.getNbJoursDepuisDatePivot(origine,
					dateEnJour);

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return newDateValue;
	}

	public static long getTempsFromTypeRepereDate(Message msg, TypeRepere trD) {

		String date = String.valueOf((msg.getVariable(trD.getCode())
				.getCastedValeur()));
		long newTempsValue = 0;
		try {
			newTempsValue = ConversionTemps.getNbMillisDepuisDebutJournee(date);// +temps;

		} catch (ParseException e) {

			e.printStackTrace();
		}
		return newTempsValue;
	}

}
