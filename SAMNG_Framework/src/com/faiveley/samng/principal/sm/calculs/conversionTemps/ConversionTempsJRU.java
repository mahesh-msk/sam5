package com.faiveley.samng.principal.sm.calculs.conversionTemps;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;

public class ConversionTempsJRU extends ConversionTemps {
	
	public ConversionTempsJRU(String startDate) {
		super(startDate);
	}
	
//	public static double getDateFromVariableDATE(byte[] date){
//		byte[] tab=new byte[2];
//		tab=date;
//		int annee=2000+new BigInteger(tab).shiftRight(9).intValue();
//		int mois=new BigInteger(tab).shiftRight(5).intValue() & 0x0f;
//		int jours=new BigInteger(tab).intValue() & 0x1f;;
//		
//		Date dateComparee=new Date();
//		dateComparee.setYear(annee);
//		dateComparee.setMonth(mois);
//		dateComparee.setDate(jours);
//		dateComparee.setHours(0);
//		dateComparee.setMinutes(0);
//		dateComparee.setSeconds(0);
//
//		dateComparee.toString();
//		Date datePivot=null;
//		try {
//			datePivot=new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		double retour=0;
//		try {
//			retour=ConversionTemps.getNbJoursDepuisDatePivotBCD(dateComparee,datePivot);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}		
//		return retour;
//	}

	public static double getDateFromVariableDATE(AVariableComposant varDate){
		VariableComposite varComp = (VariableComposite)varDate;
		AVariableComposant anneeDate= varComp.getM_AVariableComposant().get(0);
		int annee = 2000+ new BigInteger((byte[])anneeDate.getValeur()).intValue();
		AVariableComposant moisDate= varComp.getM_AVariableComposant().get(1);
		int mois= new BigInteger((byte[])moisDate.getValeur()).intValue() & 0x0f;
		AVariableComposant joursDate= varComp.getM_AVariableComposant().get(2);
		int jours= new BigInteger((byte[])joursDate.getValeur()).intValue() & 0x1f;
		
		Date dateComparee=new Date();
		
		dateComparee.setYear(annee-1900);
		dateComparee.setMonth(mois-1);
		dateComparee.setDate(jours);
		dateComparee.setHours(0);
		dateComparee.setMinutes(0);
		dateComparee.setSeconds(0);

		dateComparee.toString();
		Date datePivot=null;
		try {
			datePivot=new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000");
		} catch (Exception e) {
			e.printStackTrace();
		}
		double retour=0;
		try {
			retour=ConversionTemps.getNbJoursDepuisDatePivotBCD(dateComparee,datePivot);
		} catch (Exception e) {
			// TODO: handle exception
		}		
		return retour;
	}
	
	public static double getTimeFromVariableTIME(byte[] time){
		byte[] tab=new byte[4];
		tab=time;
		int heures=new BigInteger(tab).shiftRight(17).intValue();
		int minutes=new BigInteger(tab).shiftRight(11).intValue() & 0x3f;
		int secondes=new BigInteger(tab).shiftRight(5).intValue() & 0x3f;
		int tts=50*new BigInteger(tab).intValue() & 0x1f;;
		double ret=((((heures*60)+minutes)*60)+secondes)*1000+tts;
		return ret;
	}
	
	/**
	 * Méthode qui calcule le temps absolu à partir de la variable complexe TIME
	 * @param varTime
	 * @return un long
	 */
	public static long getTimeFromVariableTIME(AVariableComposant varTime){
		VariableComposite varComp = (VariableComposite)varTime;
		int heures= new BigInteger(((byte[])varComp.getM_AVariableComposant().get(0).getValeur())).intValue();
		int minutes=new BigInteger(((byte[])varComp.getM_AVariableComposant().get(1).getValeur())).intValue();
		int secondes=new BigInteger(((byte[])varComp.getM_AVariableComposant().get(2).getValeur())).intValue();
		//int millisecondes=new BigInteger(((byte[])varComp.getM_AVariableComposant().get(3).getValeur())).intValue();
		int millisecondes=Integer.parseInt(varComp.getM_AVariableComposant().get(3).toString());
		long ret=((((heures*60)+minutes)*60)+secondes)*1000+millisecondes;
		return ret;
	}
}
