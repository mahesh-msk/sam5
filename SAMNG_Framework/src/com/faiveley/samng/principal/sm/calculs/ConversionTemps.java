package com.faiveley.samng.principal.sm.calculs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsAtess;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsJRU;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTom4;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTomNg;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TempResolutionEnum;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.AParseurParcoursTom4;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;

public class ConversionTemps {

	private static final long MSECONDS_IN_MIN = 60 * 1000;
	private static final long MSECONDS_IN_HOUR = 60 * MSECONDS_IN_MIN;	
	private static final long MSECONDS_IN_DAY = 24 * MSECONDS_IN_HOUR;
	private static TempResolutionEnum tempResolution = TempResolutionEnum.RESOLUTION_0_001;

	private Calendar startCalendar;
	private Calendar currentCalendar;

	public static SimpleDateFormat FORMATER = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSS");

	private Date originDate;

	public ConversionTemps(String startDate) {
		try {
			this.originDate = FORMATER.parse(startDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void addDate(double date) {
		this.startCalendar = Calendar.getInstance();
		this.startCalendar.setTime(this.originDate);
		this.startCalendar.add(Calendar.DAY_OF_YEAR, (int)date);
	}

	public void addTime(double time) {
		//create each time an instance of the calendar in order to have a correct computation
		this.currentCalendar = Calendar.getInstance();
		Date date = this.startCalendar != null ? this.startCalendar.getTime() : this.originDate;
		this.currentCalendar.setTime(date);

		int msSinceStartOfDay = (int)time;
		int seconds = msSinceStartOfDay / 1000;
		int hours = seconds/3600;

		this.currentCalendar.set(Calendar.HOUR_OF_DAY, hours);
		this.currentCalendar.set(Calendar.SECOND, seconds - hours*3600);
		this.currentCalendar.set(Calendar.MILLISECOND, msSinceStartOfDay - seconds * 1000);
	}

	/**
	 * Méthode permettant d'ajouter un long au calendrier
	 * @param time
	 */
	public void addTime(long time) {
		//create each time an instance of the calendar in order to have a correct computation
		this.currentCalendar = Calendar.getInstance();
		Date date = this.startCalendar != null ? this.startCalendar.getTime() : this.originDate;
		this.currentCalendar.setTimeInMillis(time + date.getTime() + date.getHours()*3600*1000 + date.getMinutes()*60*1000 + date.getSeconds()*1000 );	
	}

	public String getFormatedTime() {
		String time = null;
		if(this.currentCalendar != null) {
			time = getFormattedDate(this.currentCalendar.getTime());
		}
		return time;
	}



	public static String calculatePeriodAsString(String initialTime, String currentTime) {
		Calendar time = Calendar.getInstance();
		Date initial = null;
		Date current = null;
		try {
			initial = FORMATER.parse(initialTime);
			current = FORMATER.parse(currentTime);
			time.setTime(current);
			time.add(Calendar.MILLISECOND, - (int)initial.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return FORMATER.format(time.getTime());
	}

	public static long calculatePeriodAsLong(String initialTime, String currentTime) {
		Calendar time = Calendar.getInstance();
		Date initial = null;
		Date current = null;
		long t = 0;

		if (initialTime.length()==22) {
			initialTime=initialTime+"0";
		}

		if (currentTime.length()==22) {
			currentTime=currentTime+"0";
		}

		if (initialTime.length()==19) {
			initialTime=initialTime+".000";
		}

		if (currentTime.length()==19) {
			currentTime=currentTime+".000";
		}

		try {
			initial = FORMATER.parse(initialTime);
			current = FORMATER.parse(currentTime);
		} catch (ParseException e) {			
			t =-1;
		}
		try{
			t = current.getTime() - initial.getTime();
			time.setTime(current);}
		catch(NullPointerException ex){
			t =-1;
		}
		//time.add(Calendar.MILLISECOND, - (int)initial.getTime());

		//return time.getTime().getTime();
		return t;
	}

	public static String addPeriod(String initialTime, long addTime) {
		Calendar time = Calendar.getInstance();
		try {
			if(initialTime.length()==19)
				initialTime +=".0";
			int nbdigitMilli=initialTime.split("\\.")[initialTime.split("\\.").length-1].length();
			if (nbdigitMilli==2) {
				initialTime=initialTime+"0";
			}else if (nbdigitMilli==1) {
				initialTime=initialTime+"00";

			}
			time.setTime(FORMATER.parse(initialTime));
			long timeCurrent = time.getTimeInMillis();
			timeCurrent += addTime;
			time.setTimeInMillis(timeCurrent);

		} catch (ParseException e) {
			e.printStackTrace();
		}

		//int i = (int)addTime;

		//BigDecimal f = new BigDecimal(time.getTimeInMillis());
		//String strDate =FORMATER.format(new Date(f.longValue()));



		BigDecimal f = new BigDecimal(time.getTimeInMillis());
		String strDate =FORMATER.format(new Date(f.longValue()));
		switch (tempResolution) {
		case RESOLUTION_1:	
			try {
				f =f.divide(new BigDecimal(10000));
			} catch (Exception e) {
				e.printStackTrace();
			}

			f = f.setScale(1,RoundingMode.DOWN);
			f = f.multiply(new BigDecimal(10000));
			strDate = FORMATER.format(new Date(f.longValue()));
			strDate=  strDate.substring(0, strDate.length() - 4);
			break;
		case RESOLUTION_0_1:
			f =f.divide(new BigDecimal(1000));
			f = f.setScale(1,RoundingMode.DOWN);
			f = f.multiply(new BigDecimal(1000));
			strDate = FORMATER.format(new Date(f.longValue()));
			strDate=  strDate.substring(0, strDate.length() - 2);
			break;
		case RESOLUTION_0_01:
			f =f.divide(new BigDecimal(100));
			f = f.setScale(1,RoundingMode.DOWN);
			f = f.multiply(new BigDecimal(100));
			strDate = FORMATER.format(new Date(f.longValue()));
			strDate=  strDate.substring(0, strDate.length() - 1);
			break;
		case RESOLUTION_0_001:
			f =f.divide(new BigDecimal(10));
			f = f.setScale(1,RoundingMode.DOWN);
			f = f.multiply(new BigDecimal(10));
			strDate = FORMATER.format(new Date(f.longValue()));
			strDate=  strDate.substring(0, strDate.length());
			break;
		default:
			strDate=FORMATER.format(new Date(time.getTimeInMillis()));
			break;

		}




		//		if(((InfosFichierSamNg)FabriqueParcours.getInstance().getParcours().getInfo()).getTempResolution()  != TempResolutionEnum.RESOLUTION_0_001) {
		//			switch (tempResolution) {
		//			case RESOLUTION_1:	
		//				f =f.divide(new BigDecimal(10000));
		//				f = f.setScale(1,RoundingMode.DOWN);
		//				f = f.multiply(new BigDecimal(10000));
		//				strDate = (FORMATER.format(new Date(f.longValue()))).substring(0, strDate.length() - 4);
		//				break;	
		//			case RESOLUTION_0_1:
		//				f =f.divide(new BigDecimal(1000));
		//				f = f.setScale(1,RoundingMode.DOWN);
		//				f = f.multiply(new BigDecimal(1000));
		//				strDate = (FORMATER.format(new Date(f.longValue()))).substring(0, strDate.length() - 2);
		//				break;	
		//			case RESOLUTION_0_01:
		//				f =f.divide(new BigDecimal(100));
		//				f = f.setScale(1,RoundingMode.DOWN);
		//				f = f.multiply(new BigDecimal(100));
		//				strDate = (FORMATER.format(new Date(f.longValue()))).substring(0, strDate.length() - 1);
		//				break;
		//			}
		//		}




		return strDate;
	}

	/**
	 * Méthode qui calcule le nombre de jours depuis une date pivot
	 * exemple: avec les fichiers TOM anciens(uk,dis et hsbc) la date pivot est 010190
	 * @param datePivot
	 * @param dateComparee
	 * @return
	 * @throws ParseException
	 */
	public static int getNbJoursDepuisDatePivot(String datePivot, String dateComparee) throws ParseException{

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) {
			return ConversionTempsAtess.getNbJoursDepuisDatePivot(datePivot,dateComparee);
		}else{

			Date d1 = new SimpleDateFormat("ddMMyyyy").parse(dateComparee);
			Date d2 = new SimpleDateFormat("ddMMyyyy")
			.parse(datePivot);
			long millis1 = d1.getTime();
			long millis2 = d2.getTime();
			//on fait la différence des 2 dates
			long diff = millis1 - millis2;
			//on calule le nombre de jour entre la date pivot et la date
			int nbJours = (int)(diff / (24 * 3600 * 1000));
			if (diff % 86400000 > 0)
				nbJours++;
			return nbJours;
		}
	}
	/**
	 * Méthode qui calcule le nombre de millisecondes depuis le debut de la journée à partir d'une date au format : ddMMyyyyHHmmss 
	 * @param date au format ddMMyyyyHHmmss
	 * @return
	 * @throws ParseException 
	 */
	public static long getNbMillisDepuisDebutJournee(String date) throws ParseException{

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) {
			return ConversionTempsAtess.getNbMillisDepuisDebutJournee(date);
		}else{
			Date d1 = new SimpleDateFormat("ddMMyyyy").parse(date.substring(0, 8));
			Date d3 = new SimpleDateFormat("ddMMyyyyHHmmss").parse(date);
			long millis1 = d1.getTime();

			long millis3 = d3.getTime();
			long diff = millis3 - millis1;
			return diff;
		}
	}


	public Date getCurrentDate() {
		return this.currentCalendar != null ? this.currentCalendar.getTime() : this.originDate;
	}

	public long getCurrentDateAsMillis() {
		return getCurrentDate().getTime();
	}

	public static String getFormattedDate(Date date) {
		return (date == null) ? "###" : FORMATER.format(date);
	}

	public static String getFormattedDate(long date) {
		return getFormattedDate(date, false);
	}

	public static String getFormatFromResolutionTemps() {
		try{
			tempResolution = ((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).getTempResolution();
		}catch(Exception ex){
			tempResolution = TempResolutionEnum.RESOLUTION_0_001;
		}

		switch (tempResolution) {
		case RESOLUTION_1:	
			return "dd/MM/yyyy HH:mm:ss";
		case RESOLUTION_0_1:
			return "dd/MM/yyyy HH:mm:ss.S";
		case RESOLUTION_0_01:
			return "dd/MM/yyyy HH:mm:ss.SS";
		case RESOLUTION_0_001:
			return "dd/MM/yyyy HH:mm:ss.SSS";
		default:
			return "";
		}
	}
	
	public static String getAjoutZerosFromResolutionTemps() {
		try{
			tempResolution = GestionnairePool.getInstance().getXMLParser().getInfoData().getTempResolution();
		}catch(Exception ex){
			tempResolution = TempResolutionEnum.RESOLUTION_0_001;
		}

		switch (tempResolution) {
		case RESOLUTION_1:	
			return "";
		case RESOLUTION_0_1:
			return ".0";
		case RESOLUTION_0_01:
			return ".00";
		case RESOLUTION_0_001:
			return ".000";
		default:
			return "";
		}
	}

	public static String getFormattedDate(long date, boolean useResolutionTemp) {


		//SUPPR_GOULET_GETFORMATTEDDATE
		try{
			tempResolution = ((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo()).getTempResolution();
		}catch(Exception ex){
			tempResolution = TempResolutionEnum.RESOLUTION_0_001;
		}

		String strDate = null;

		if(useResolutionTemp) {
			BigDecimal f = new BigDecimal(date);
			switch (tempResolution) {
			case RESOLUTION_1:	
				try {
					f =f.divide(new BigDecimal(10000));
				} catch (Exception e) {
					e.printStackTrace();
				}

				f = f.setScale(1,RoundingMode.DOWN);
				f = f.multiply(new BigDecimal(10000));
				strDate = FORMATER.format(new Date(f.longValue()));
				strDate=  strDate.substring(0, strDate.length() - 4);
				break;
			case RESOLUTION_0_1:
				f =f.divide(new BigDecimal(1000));
				f = f.setScale(1,RoundingMode.DOWN);
				f = f.multiply(new BigDecimal(1000));
				strDate = FORMATER.format(new Date(f.longValue()));
				strDate=  strDate.substring(0, strDate.length() - 2);
				break;
			case RESOLUTION_0_01:
				f =f.divide(new BigDecimal(100));
				f = f.setScale(1,RoundingMode.DOWN);
				f = f.multiply(new BigDecimal(100));
				strDate = FORMATER.format(new Date(f.longValue()));
				strDate=  strDate.substring(0, strDate.length() - 1);
				break;
			case RESOLUTION_0_001:
				f =f.divide(new BigDecimal(10));
				f = f.setScale(1,RoundingMode.DOWN);
				f = f.multiply(new BigDecimal(10));
				strDate = FORMATER.format(new Date(f.longValue()));
				strDate=  strDate.substring(0, strDate.length());
				break;
			default:
				strDate=FORMATER.format(new Date(date));
				break;

			}
		}
		//		}else{
		//			 strDate=FORMATER.format(new Date(date));
		//		}

		return strDate;
	}

	public static String getRelativeTimeAsString(long relTime, String libelleJour, String libelleHeure, String libelleMinute, String libelleSeconde,String libelleMilliseconde) {
		long time = Math.abs(relTime);

		int days = (int)(time / MSECONDS_IN_DAY); 
		long days_ms = days * MSECONDS_IN_DAY;
		long remainingMs = time-days_ms;

		int hours = (int)(remainingMs/MSECONDS_IN_HOUR);
		long hours_ms = hours * MSECONDS_IN_HOUR;
		remainingMs -= hours_ms;

		int minutes = (int)(remainingMs/MSECONDS_IN_MIN);
		long minutes_ms = minutes * MSECONDS_IN_MIN;
		remainingMs -= minutes_ms;

		int seconds = (int)(remainingMs/1000);
		remainingMs -= seconds * 1000;

		String str ="";
		str += relTime < 0 ? "-" : "+";

		if(days != 0) {
			str += days+libelleJour+" ";
		} 

		str += hours+libelleHeure+" "+minutes+libelleMinute+" "+seconds+libelleSeconde+" ";

		StringBuilder buf = new StringBuilder(str); 


		TempResolutionEnum resol=ParseurXML1.getInstance().getInfoData().getTempResolution();

		if (resol!=TempResolutionEnum.RESOLUTION_1) {
			if (resol==TempResolutionEnum.RESOLUTION_0_1) {
				buf.append(getFromResolution(remainingMs+"",1,libelleMilliseconde));
			}else if (resol==TempResolutionEnum.RESOLUTION_0_01) {
				buf.append(getFromResolution(remainingMs+"",2,libelleMilliseconde));
			}else if (resol==TempResolutionEnum.RESOLUTION_0_001) {
				buf.append(getFromResolution(remainingMs+"",3,libelleMilliseconde));	
			}else if (resol==TempResolutionEnum.RESOLUTION_0_0001) {
				buf.append(getFromResolution(remainingMs+"",4,libelleMilliseconde));
			}else if (resol==TempResolutionEnum.RESOLUTION_0_00001) {
				buf.append(getFromResolution(remainingMs+"",5,libelleMilliseconde));
			}
		}
		return buf.toString();
	}

	private static String getFromResolution(String entier,int lengthWanted, String ms){
		int length=entier.length();
		String returnString="";
		String zeros="";
		if (length==lengthWanted) {
			returnString=entier;
		}else if (length>=lengthWanted) {
			returnString=entier.substring(0,lengthWanted);
		}else if (length<=lengthWanted) {
			int nbzeroToAdd=lengthWanted-length;

			for (int i = 0; i < nbzeroToAdd; i++) {
				zeros=zeros+"0";
			}
			returnString=entier+zeros;
		}
		if(lengthWanted==2 && Integer.parseInt(entier)<100){
			returnString="0" + entier;
			returnString = returnString.substring(0,2);
		}
		if(lengthWanted==1 && Integer.parseInt(entier)<100){
			returnString="0";
		}
		if (lengthWanted>=3) {
			if(lengthWanted==3)
				zeros="";
			if(lengthWanted==4)
				zeros="0";
			if(lengthWanted==5)
				zeros="00";

			if(Integer.parseInt(entier)<100){
				String tmpString = "";
				if(Integer.parseInt(entier)==0){
					tmpString =entier+ ms + " "+zeros;
				}
				else if(Integer.parseInt(entier)<10){
					tmpString ="00"+entier+ms+" "+zeros;
				}
				else{
					tmpString ="0"+entier+ms+" "+zeros;
				}
				returnString = tmpString;
			}
			else{
				returnString=returnString.substring(0,3)+ms+" "
						+returnString.substring(3,returnString.length());

			}
		}
		return returnString;
	}

	/**
	 * Function to get the correct time for strings got with 
	 * 		getFormattedDate(long date, boolean useResolutionTemp) method
	 * @param time
	 * @param useResolutionTemp
	 * @return
	 */
	public static long getMillis(String time, boolean useResolutionTemp) {
		long t = -1;
		try {
			if(useResolutionTemp && tempResolution != TempResolutionEnum.RESOLUTION_0_001) {
				switch (tempResolution) {

				case RESOLUTION_1:
					time += ".000";
					break;
				case RESOLUTION_0_1:
					time += "00";
					break;
				case RESOLUTION_0_01:
					time += "0";
					break;

				default:

					break;
				}
			}
			t = FORMATER.parse(time).getTime();
		} catch (Exception e) {
			SamngLogger.getLogger().error("Incorrect format to parse", e);
		}
		return t;
	}

	public static long getMillis(String time) {
		return getMillis(time, false);
	}

	public static void setResolutionTemp(TempResolutionEnum tempRes) {
		tempResolution = tempRes;
	}

	public static long getDateFromTypeRepereDate(Message msg,TypeRepere tr){
		if (TypeParseur.getInstance().getParser() instanceof AParseurParcoursTom4) {
			return ConversionTempsTom4.getDateFromTypeRepereDate(msg, tr);
		}

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursSamng) {
			return ConversionTempsTomNg.getDateFromTypeRepereDate(msg, tr);
		}

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) {
			return ConversionTempsAtess.getDateFromTypeRepereDate(msg, tr);
		}
		return 0;
	}

	public static long getTempsFromTypeRepereDate(Message msg,TypeRepere trD){
		if (TypeParseur.getInstance().getParser() instanceof AParseurParcoursTom4) {
			return ConversionTempsTom4.getTempsFromTypeRepereDate(msg, trD);
		}

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursSamng) {
			return ConversionTempsTomNg.getTempsFromTypeRepereDate(msg,  trD);
		}

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) {
			return ConversionTempsAtess.getTempsFromTypeRepereDate(msg,  trD);
		}

		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU) {
			return ConversionTempsJRU.getTempsFromTypeRepereDate(msg,  trD);
		}

		return 0;
	}

	public static int getNbJoursDepuisDatePivotBCD(Date d1, Date d2) throws ParseException {
		//correction ISSUE 780 : la différence entre 2 dates ne tenaient 
		//pas compte des passages heures d'été/heures d'hiver 
		//algorithme alternatif utilisant la classe Calendar
		//commentaire: d'autre algorithme de calcul de différences entre 2 dates ont potentiellement le meme probleme
		long nbJours;
		final long MILLISECONDS_PER_DAY = 1000 * 60 * 60 * 24;
		Calendar aCal = Calendar.getInstance();
		aCal.setTime(d2);
		long aFromOffset = aCal.get(Calendar.DST_OFFSET);
		aCal.setTime(d1);
		long aToOffset = aCal.get(Calendar.DST_OFFSET);
		long aDayDiffInMili = (d1.getTime() + aToOffset) - (d2.getTime() + aFromOffset);
		nbJours = ((long) aDayDiffInMili / MILLISECONDS_PER_DAY);

		return (int) nbJours;
	}
}
