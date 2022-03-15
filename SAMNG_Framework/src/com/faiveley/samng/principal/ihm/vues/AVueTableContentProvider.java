package com.faiveley.samng.principal.ihm.vues;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.TableSegments;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public abstract class AVueTableContentProvider extends ATableContentProvider {
	public VueData data;
	public GestionnaireVueListeBase gestionaireVue;
	public List<String> columnNames;
	public List<Type> columnTypeValeur=new ArrayList<Type>(0);

	public int posColRelDist = -1;
	public int posColFlag = -1;
	public int posColTime = -1;
	public int posColRelTime = -1;
	public int posColCorTime = -1;
	public int posColCorSpeed = -1;
	public int posColCorDist = -1; 
	public int posColAccDist = -1;
	
	public int lastFixedColumn = -1;
	public VueTableColumnsIndices columnsIndicesInfos;

	public boolean debFlag = false;
	public boolean finFlag = false;
	public String addflag = "";
	public Message lastMsg = null;
	public Row lastRow = null;

	protected static final String VAR_SANS_VALEUR= "###";


	public AVueTableContentProvider() {
		this.data = ActivatorData.getInstance().getVueData();
		this.columnNames = new ArrayList<String>(0);
	}

	public VueTableColumnsIndices getColumnIndices() {
		return this.columnsIndicesInfos;
	}

	public abstract void setFilter(String filterName);

	/**
	 * Creates the list of the columns names
	 * @return the index of the last fixed column
	 */
	protected abstract int createColumnNamesList();

	/**
	 * Initializes the positions for known columns.
	 * This should be called after columns are created
	 *
	 */
	protected void initColumnsPositions() {
		this.posColFlag = this.columnNames.indexOf(GestionnaireVueListeBase.FLAG_COL_NAME_STR);
		this.posColTime = this.columnNames.indexOf(GestionnaireVueListeBase.TIME_COL_NAME_STR);
		this.posColRelTime = this.columnNames.indexOf(TypeRepere.tempsRelatif.getName());
		this.posColCorTime = this.columnNames.indexOf(TypeRepere.tempsCorrigee.getName());
		this.posColRelDist = this.columnNames.indexOf(TypeRepere.distanceRelatif.getName());
		this.posColCorDist = this.columnNames.indexOf(TypeRepere.distanceCorrigee.getName());
		this.posColCorSpeed = this.columnNames.indexOf(TypeRepere.vitesseCorrigee.getName());
		this.posColAccDist = this.columnNames.indexOf(GestionnaireVueListeBase.ACC_DIST_COL_NAME_STR);

		this.columnsIndicesInfos.setPosColFlag(this.posColFlag);
		this.columnsIndicesInfos.setPosColTime(this.posColTime);
		this.columnsIndicesInfos.setPosColRelTime(this.posColRelTime);
		this.columnsIndicesInfos.setPosColCorTime(this.posColCorTime);
		this.columnsIndicesInfos.setPosColRelDist(this.posColRelDist);
		this.columnsIndicesInfos.setPosColCorDist(this.posColCorDist);
		this.columnsIndicesInfos.setPosColCorSpeed(this.posColCorSpeed);
		this.columnsIndicesInfos.setPosColAccDist(this.posColAccDist);
	}

	/**
	 * Creates the columns and initializes the known columns positions
	 *
	 */
	public void initializeColumns() {
		this.columnsIndicesInfos = new VueTableColumnsIndices();
		createColumnNamesList();
		initColumnsPositions();
	}


	/** 
	 * Sets the corrected time on the time column of a row from a given message
	 * @param row
	 * @param msg
	 */
	public void setRowTime(Row row, Message msg) {
		if (this.posColTime >= 0) {
			String timeStr = VAR_SANS_VALEUR;
			
			if (msg != null && !msg.isErrorMessage()) {
				//We know that the value of the time is time millis since 1 Jan 1970
				timeStr = ConversionTemps.getFormattedDate(msg.getAbsoluteTime(), true);
				try {
					if (timeStr.startsWith("01/01/2000") && msg.getVariable(TypeRepere.date.getCode())==null) {
						timeStr.replaceAll("01/01/2000", "##/##/####");
					}
					if (timeStr.startsWith("01/01/1970 00:00:00") && msg.getVariable(TypeRepere.date.getCode())==null) {
						timeStr = "##/##/#### ##:##:##";
					}
				} catch (RuntimeException e) {
					// TODO Auto-generated catch block
					ConversionTemps.getFormattedDate(msg.getAbsoluteTime(), true);
				}

				if(msg.isMessageIncertitude()){
					timeStr =  "##/##/#### ##" + timeStr.substring(13, timeStr.length());
				}
			}
			setRowValue(row, this.columnNames.get(this.posColTime), this.posColTime, timeStr);
		}
	}

	/** 
	 * Sets the corrected time on the time column of a row from a given message
	 * @param row
	 * @param msg
	 */
	public void setRowTimeCorrected(Row row, Message msg) {		
		if (this.posColCorTime >= 0) {
			String timeStr = VAR_SANS_VALEUR;
			
			if (msg != null && !msg.isErrorMessage()) {
				AVariableComposant tempCorigee = msg.getVariable(TypeRepere.tempsCorrigee.getCode());

				if (tempCorigee != null) {
					//We know that the value of the corrected time is set as string 


					timeStr = new String((byte[])tempCorigee.getValeur());//tagValCor


					if(msg.isMessageIncertitude()){
						timeStr =  "##/##/#### ##" + timeStr.substring(13, timeStr.length());

					}
				}
				else{
//					We know that the value of the corrected time is set as string 
					timeStr = ConversionTemps.getFormattedDate(msg.getAbsoluteTime(), true);

					if(msg.isMessageIncertitude()){
						timeStr =  "##/##/#### ##" + timeStr.substring(13, timeStr.length());

					}
				}
			}

			setRowValue(row, this.columnNames.get(this.posColCorTime), this.posColCorTime, timeStr);
		}
	}
	
	public void setRowAccumulatedDistance(Row row, Message msg, Double accumulatedDistance) {
		if (this.posColAccDist >= 0) {
			String accumulatedDistanceStr = VAR_SANS_VALEUR;
			
			if (msg != null && !msg.isErrorMessage()) {
				accumulatedDistanceStr = String.format(Locale.US, "%.3f", accumulatedDistance);
			}
			setRowValue(row, this.columnNames.get(this.posColAccDist), this.posColAccDist, accumulatedDistanceStr);
		}
	}	

	/** 
	 * Sets the corrected time on the time column of a row from a given message
	 * @param row
	 * @param msg
	 */
	public void setRowDistanceCorrected(Row row, Message msg) {
		if (this.posColCorDist >= 0) {
			String distStr = VAR_SANS_VALEUR;
			
			if (msg != null && !msg.isErrorMessage()) {
				//AVariableComposant distCorigee = msg.getVariable(TypeRepere.distanceCorrigee.getCode());

				//comportement avant changement : la distance corrigee affichée était le cumul non la distance relative corrigée
				//	if (distCorigee != null) {
//				double d= (Double)distCorigee.getCastedValeur();

//				DecimalFormat fmt = new DecimalFormat("0.000");
//				distStr = (fmt.format((double) d)).replace(",",
//				".");	

				//We know that the value of the corrected time is set as string 
				//distStr = new String((byte[])distCorigee.getValeur());
				//}

				if(msg.getVariable(TypeRepere.distanceRelatif.getCode())!=null){

					String distanceRelativeString = new String((byte[])msg.getVariable(TypeRepere.distanceRelatif.getCode()).getValeur());//tagValCor
					String signe =null;

					if(distanceRelativeString!=null){

						signe = distanceRelativeString.substring(0, 1);
						//valeur = Float.valueOf(distanceRelativeString.substring(1, distanceRelativeString.length()));

						SegmentDistance segment = TableSegments.getInstance().getContainingDistanceSegment(msg.getMessageId());
						double valModif = segment.getDiameterCorrige();
						double factor = valModif / segment.getInitialDiameter();
						double distCorr=0 ;
						try{
							distCorr = Math.abs(Float.valueOf(distanceRelativeString) * factor);
							DecimalFormat fmt = new DecimalFormat("0.000");
							String distanceRelativeStringCorrigee = signe  +(fmt.format((double) distCorr)).replace(",",
							".");

							distStr = distanceRelativeStringCorrigee;
						}
						catch(NumberFormatException ex){

						}
					}
				}
			}

			setRowValue(row, this.columnNames.get(this.posColCorDist), this.posColCorDist, distStr);
		}
	}

	/** 
	 * Sets the corrected time on the time columng of a row from a given message
	 * @param row
	 * @param msg
	 */
	public void setRowRelativeTime(Row row, Message msg) {
		if (this.posColRelTime >= 0) {
			//We know that the value of the corrected time is time millis since 1 Jan 1970
			String timeStr = VAR_SANS_VALEUR;
			
			if (msg != null && !msg.isErrorMessage()) {
				AVariableComposant tempRel = msg.getVariable(TypeRepere.tempsRelatif.getCode());

				if (tempRel != null) {
					//We know that the value of the corrected time is set as string 
					try {
//						timeStr = String.valueOf(ConversionTemps.getTempsFromTypeRepereTemps(msg, TypeRepere.tempRelatif, TypeRepere.date));
						timeStr = new String((byte[])tempRel.getValeur());//tagValCor
						//					timeStr=ConversionTemps.getStringTempsFromTypeRepereTemps(msg, TypeRepere.tempRelatif, TypeRepere.date);
					} catch (Exception e) {

					}
				}
			}

			//set the value
			setRowValue(row, this.columnNames.get(this.posColRelTime), this.posColRelTime, timeStr);
		}
	}

	/** 
	 * Sets the relative distance on the time column of a row from a given message
	 * @param row
	 * @param msg
	 */
	public void setRowRelativeDistance(Row row, Message msg) {
		if (this.posColRelDist >= 0) {
			//We know that the value of the corrected time is time millis since 1 Jan 1970
			String distStr = VAR_SANS_VALEUR;
			
			if (msg != null && !msg.isErrorMessage()) {
				AVariableComposant distRel = msg.getVariable(TypeRepere.distanceRelatif.getCode());

				if (distRel != null) {
					//We know that the value of the corrected time is set as string 
					distStr = new String((byte[])distRel.getValeur());//tagValCor
				}
			}

			setRowValue(row, this.columnNames.get(this.posColRelDist), this.posColRelDist, distStr);			
		}
	}

	/** 
	 * Sets the relative distance on the time column of a row from a given message
	 * @param row
	 * @param msg
	 */
	public void setFlag(Row row, Message msg) {
		//if(this.posColRelDist >= 0) {
			//We know that the value of the corrected time is time millis since 1 Jan 1970
			String flagStr = "";
			if(msg != null) {
				Flag flg = msg.getFlag();

				if (flg != null) {
					//We know that the value of the corrected time is set as string 
					flagStr = flg.getLabel();
				}

			}
			String ajouter = "";
			if(this.posColFlag!=-1){
				if (debFlag || finFlag) {
					boolean finflag2=false;
					boolean debflag2=false;
					boolean isadded = false;


					for (int i = 0; i < addflag.length(); i++) {
						for (int j = 0; j < flagStr.length(); j++) {
							if (flagStr.charAt(j) == addflag.charAt(i) && addflag.charAt(i)=='{') {
								if (!debflag2) {
									debflag2=true;
									continue;
								}else{
									isadded=true;
								}
							}else if (flagStr.charAt(j) == addflag.charAt(i) && addflag.charAt(i)=='}') {
								if (!finflag2) {
									finflag2=true;
									continue;
								}else{
									isadded=true;
								}
							}
						}
						if (!flagStr.contains(""+addflag.charAt(i))) {
							isadded=true;
						}
						if (isadded) {
							ajouter = ajouter + addflag.charAt(i);
							isadded=false;
						}
					}

					addflag="";
					debFlag = false;
					finFlag = false;
				}
				flagStr=ajouter+flagStr;
				if (flagStr.contains(">")) {
					flagStr=flagStr.replace(">", "");
					flagStr=">"+flagStr;
				}
				flagStr=getIndiceMessage(msg,flagStr);
				setRowValue(row, this.columnNames.get(this.posColFlag), this.posColFlag, flagStr);
			}
	//	}
	}
	
	abstract protected String getIndiceMessage(Message msg,String flagStr);

	protected int getColumnIndex(String strColumnName) {
		if(this.columnNames.indexOf(strColumnName)==-1 && strColumnName.equals(TypeRepere.vitesseCorrigee.getName()))
			strColumnName = com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("NomRepere.4");
		return this.columnNames.indexOf(strColumnName);
	}

	/**
	 * @return the columnNames
	 */
	public List<String> getColumnNames() {
		return this.columnNames;
	}

	/**
	 * Gets the label for a variable
	 * @param v
	 * @return
	 */
	public String getLabelForVariable(AVariableComposant v) {
		DescripteurVariable descrVar = v.getDescriptor();
		String varNom = this.gestionaireVue.getVarNom(descrVar);

		StringBuilder buf = new StringBuilder(varNom).append(":").append(v.toString());
		if (descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
			buf.append(" ").append(((DescripteurVariableAnalogique)descrVar).getUnite());
		}
		return buf.toString();
	}

	

	/**
	 * Sets the row value and also computes for the columns with automatic width the
	 * size of the column according to each cell text 
	 * 
	 * @param row
	 * @param colNom
	 * @param index
	 * @param value
	 */
	public void setRowValue(Row row, String colNom, int index, String value) {
		ConfigurationColonne colCfg = gestionaireVue.getColonne(colNom);
		if(colCfg != null) {
			int textWidth =0;
			if(colCfg.getLargeur() <= 0) {
				int t = new String(value).length();
				int tailleNomColonne = colNom.length();
				if(tailleNomColonne>t)
					t = tailleNomColonne;
				
				int espaceMoyenCaractere = ActivatorData.getInstance().getEspaceMoyenCaractere();
				int tailleMoyenneCaractere = ActivatorData.getInstance().getTailleMoyenneCaractere();		
				textWidth = tailleMoyenneCaractere*(t+2)+espaceMoyenCaractere+t+10;		
				if(colCfg.getLargeurCalculee() < textWidth)
					colCfg.setLargeurCalculee(textWidth);
			}
		}
		row.setValue(index, value);
	}

	public List<String> getColumnLabels() {
		List<String> colLabels = new ArrayList<String>(this.columnNames.size());
		for(String colName: this.columnNames) {
			colLabels.add(GestionnaireVueListeBase.
					getDisplayLabelForColumn(this.gestionaireVue.getColonne(colName), false));
		}

		return colLabels;
	}
}
