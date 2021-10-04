package com.faiveley.samng.principal.sm.calculs;

import java.util.Calendar;

import com.faiveley.samng.principal.ihm.vues.search.Operation;

//DR26-3-a CL06
public class TempsAbsoluDatePartielle {

	Calendar calendrierPartiel;
	Calendar calendrierActu;

	String dateRecherchee;
	long absoluteTime;
	int limit;
	boolean anneeValide=false; //si l'année est validée, on peut faire intervenir les opérateurs comme >,>=,<,<=
	Operation op;

	public TempsAbsoluDatePartielle(Integer j, Integer m, Integer a, Integer h, Integer min, Integer sec, long absoluteTime, Operation op,int limit) {
		anneeValide=false;
		this.op=op;
		if (j!=null) {
			String jour=j!=null ? ajouterZeroSiBesoin(j+"") : "";
			dateRecherchee=jour;
			if (m!=null) {
				String mois=m!=null ? ajouterZeroSiBesoin(m+"") : "";
				dateRecherchee=dateRecherchee+"/"+mois;
				if (a!=null) {
					String annee=a!=null ? ajouterZeroSiBesoin(a+"") : "";
					dateRecherchee=dateRecherchee+"/"+annee;
					anneeValide=true;
					if (h!=null) {
						String heures=h!=null ? ajouterZeroSiBesoin(h+"") : "";
						dateRecherchee=dateRecherchee+" "+heures;
						if (min!=null) {
							String minutes=min!=null ? ajouterZeroSiBesoin(min+"") : "";
							dateRecherchee=dateRecherchee+":"+minutes;
							if (sec!=null) {
								String secondes=sec!=null ? ajouterZeroSiBesoin(sec+"") : "";
								dateRecherchee=dateRecherchee+":"+secondes;
							}
						}
					}
				}
			}
		}
		this.absoluteTime=absoluteTime;
		this.limit=limit;
	}

	public static String ajouterZeroSiBesoin(String s){
		if (s.length()==1) {
			return ("0"+s);
		}else{
			return s;
		}
	}
	
	public static String ajouterZeroSiBesoin(int s){
		return ajouterZeroSiBesoin(s+"");
	}
	
	public int compareDeuxCalendars(String cal1,String cal2){
		calendrierPartiel=Calendar.getInstance();
		calendrierPartiel.set(Integer.valueOf(cal1.substring(6, 10)), 
				Integer.valueOf(cal1.substring(3, 5))-1, 
				Integer.valueOf(cal1.substring(0, 2)), 
				Integer.valueOf(cal1.substring(11, 13)), 
				Integer.valueOf(cal1.substring(14, 16)), 
				Integer.valueOf(cal1.substring(17, 19)));
		calendrierActu=Calendar.getInstance();
		calendrierActu.set(Integer.valueOf(cal2.substring(6, 10)), 
				Integer.valueOf(cal2.substring(3, 5))-1, 
				Integer.valueOf(cal2.substring(0, 2)), 
				Integer.valueOf(cal2.substring(11, 13)), 
				Integer.valueOf(cal2.substring(14, 16)), 
				Integer.valueOf(cal2.substring(17, 19)));
		return (calendrierActu.compareTo(calendrierPartiel));
	}
	
	public boolean datePartielleMatch(){
		String label = ConversionTemps.getFormattedDate(absoluteTime,true);
		String labelCut=label.substring(0, this.limit);
		switch (op) {
		case Equal: {
			if (labelCut.equals(this.dateRecherchee)) {
				return true;
			}else{
				return false;			
			}
		}
		case Greater: {
			if (!anneeValide) {
				return false;
			}
			String completeLabel=completeIncompleteLabel(this.dateRecherchee);
			return compareDeuxCalendars(completeLabel, label)==1;
		}
		case GreaterOrEqual: {
			String completeLabel=completeIncompleteLabel(this.dateRecherchee);
			return compareDeuxCalendars(completeLabel, label)>=1;
		}
		case Less: {
			String completeLabel=completeIncompleteLabel(this.dateRecherchee);
			return compareDeuxCalendars(completeLabel, label)<1;
		}
		case LessOrEqual: {
			String completeLabel=completeIncompleteLabel(this.dateRecherchee);
			return compareDeuxCalendars(completeLabel, label)<=1;
		}
		case NotEqual: {
			if (!labelCut.equals(this.dateRecherchee)) {
				return true;
			}else{
				return false;			
			}
		}
		}
		
		return false;
	}

	private String completeIncompleteLabel(String lab){
		int longueur=lab.length();
		String ret="";
		if (longueur==10) {
			//JJ/MM/AAAA
			ret=lab+" 00:00:00.0";
		}else if (longueur==13) {
			//JJ/MM/AAAA HH
			ret=lab+":00:00.0";
		}else if (longueur==16) {
			//JJ/MM/AAAA HH:MM
			ret=lab+":00.0";
		}else if (longueur==19) {
			//JJ/MM/AAAA HH:MM:SS
			ret=lab+".0";
		}
		return ret;
	}

	//	public TempsAbsoluDatePartielle(int j, int m, long absoluteTime, Operation op,int limit) {
	//	dateRecherchee=j+"/"+m;
	//	this.absoluteTime=absoluteTime;
	//	this.limit=limit;
	//}
	//
	//public TempsAbsoluDatePartielle(int j, int m, int a, long absoluteTime, Operation op,int limit) {
	//	dateRecherchee=j+"/"+m+"/"+a;
	//	this.absoluteTime=absoluteTime;
	//	this.limit=limit;
	//}
	//
	//public TempsAbsoluDatePartielle(int j, int m, int a, int h, long absoluteTime, Operation op,int limit) {
	//	dateRecherchee=j+"/"+m+"/"+a+" "+h;
	//	this.absoluteTime=absoluteTime;
	//	this.limit=limit;
	//}
	//
	//public TempsAbsoluDatePartielle(int j, int m, int a, int h, int min, long absoluteTime, Operation op,int limit) {
	//	dateRecherchee=j+"/"+m+"/"+a+" "+h+":"+min;
	//	this.absoluteTime=absoluteTime;
	//	this.limit=limit;
	//}
	//
	//public TempsAbsoluDatePartielle(int j, int m, int a, int h, int min, int sec, long absoluteTime, Operation op,int limit) {
	//	dateRecherchee=j+"/"+m+"/"+a+" "+h+":"+min+":"+sec;
	//	this.absoluteTime=absoluteTime;
	//	this.limit=limit;
	//}
}
