package com.faiveley.samng.principal.sm.calculs.conversionTemps;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.conversionCodage.ConversionBase;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;

public class ConversionTempsAtess extends ConversionTemps {

	public ConversionTempsAtess(String startDate) {
		super(startDate);
	}

	public static long getDateFromTypeRepereDate(Message msg, TypeRepere tr) {
		long newDateValue = 0;
		Date dateComparee = new Date();
		Date datePivot = null;

		try {
			String date = (String) (msg.getVariable(tr.getCode())
					.getCastedValeur());
			dateComparee.setYear(Integer.valueOf(date.substring(4, 8)));
			dateComparee.setMonth(Integer.valueOf(date.substring(2, 4)) - 1);
			dateComparee.setDate(Integer.valueOf(date.substring(0, 2)) - 1);
			dateComparee.setHours(Integer.valueOf(date.substring(8, 10)));
			dateComparee.toString();
			datePivot = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1970");
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}

		try {
			newDateValue = getNbJoursDepuisDatePivotBCD(dateComparee, datePivot);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return newDateValue;
	}

	public static long getTempsFromTypeRepereDate(Message msg, TypeRepere trD) {
		long newTempsValue = 0;
		String date = String.valueOf((msg.getVariable(trD.getCode())
				.getCastedValeur()));
		newTempsValue = getNbMillisDepuisDebutJourneeATESS(date);// +temps;
		return newTempsValue;
	}

	public static long getNbMillisDepuisDebutJourneeATESS(String date) {

		int nbHeures = Integer.valueOf(date.substring(8, 10));

		return (nbHeures * 3600) * 1000;
	}

	public static double getDateFromHeureBCD(byte[] heure) {
		int annee = ConversionBase.HexaBCDToDecimal(heure[0]);
		int delta = annee > 80 ? 0 : 100;
		Date dateComparee = new Date();
		dateComparee.setYear(annee + delta);
		dateComparee.setMonth(ConversionBase.HexaBCDToDecimal(heure[1]) - 1);
		dateComparee.setDate(ConversionBase.HexaBCDToDecimal(heure[2]));
		dateComparee.setHours(ConversionBase.HexaBCDToDecimal(heure[3]));
		dateComparee.setMinutes(0);
		dateComparee.setSeconds(0);

		Date datePivot = null;
		try {
			datePivot = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/1970");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		double retour = 0;
		try {
			retour = ConversionTemps.getNbJoursDepuisDatePivotBCD(dateComparee,
					datePivot);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return retour;
	}
}
