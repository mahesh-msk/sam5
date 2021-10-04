package com.faiveley.samng.principal.ihm.vues.vuemarqueurs;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueMarqueursDeltaValues extends ViewPart {
	public static final String ID = "SAMNG.Vue.Markers.VueMarqueursDeltaValues"; //$NON-NLS-1$

	private Color backColor = Display.getCurrent().getSystemColor(
			SWT.COLOR_WHITE);

	private Composite mainComposite;
	private String valeurIndefinie = "###";
	private Text markersDeltaValuesText;
	private String uniteDistance = "";
	private String uniteVitesse = "";
	private boolean calculerDiffDistance = false;

	public VueMarqueursDeltaValues() {

	}

	@Override
	public void createPartControl(Composite parent) {
		this.setPartName(Messages.getString("VueMarqueursDeltaValues.1"));
		this.mainComposite = new Composite(parent, SWT.NONE);
		this.mainComposite.setLayout(new FillLayout());
		this.markersDeltaValuesText = new Text(this.mainComposite, SWT.MULTI);
		this.markersDeltaValuesText.setEditable(false);
		this.markersDeltaValuesText.setBackground(this.backColor);

	}

	public void setMessagesIds(int[] msgsIds) {
		if (msgsIds == null || msgsIds.length != 2)
			return;
		this.markersDeltaValuesText.setText(""); //$NON-NLS-1$
		Message msg1 = getMessageForId(msgsIds[0]);
		Message msg2 = getMessageForId(msgsIds[1]);
		if (msg1 == null || msg2 == null)
			return;

		if (msg1.getAbsoluteTime() < msg2.getAbsoluteTime())
			displayMessagesInformation(msg1, msg2);
		else
			displayMessagesInformation(msg2, msg1);
	}
	
	private StringBuffer differenceTemps(StringBuffer strBuffer,Message msg1, Message msg2){
		strBuffer.append(Messages.getString("VueMarqueursDeltaValues.7")).append(Text.DELIMITER);
		strBuffer.append("").append(Text.DELIMITER);
		String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
		String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
		String minute = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
		String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
		String milliseconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");
		String timeDiffSTR = "";
		try {
			boolean calculerDiffTemps = false;
			// si les 2 messages sont dans le meme segments, on peut calculer la
			// différence de temps
			if (TableSegments.getInstance().getContainingTempSegment(
					msg1.getMessageId()).equals(
							TableSegments.getInstance().getContainingTempSegment(
									msg2.getMessageId())))
				calculerDiffTemps = true;
			// si il ne sont pas dans le meme segemtn mais si le temps relatif
			// est présent dans les deux, on peut calculer la différence de
			// temps
			else if (msg1.getVariable(TypeRepere.tempsRelatif.getCode()) != null
					&& msg2.getVariable(TypeRepere.tempsRelatif.getCode()) != null)
				calculerDiffTemps = true;

			if (calculerDiffTemps) {
				long timeDiff = msg2.getAbsoluteTime() - msg1.getAbsoluteTime();
				timeDiffSTR = ConversionTemps.getRelativeTimeAsString(timeDiff,jour,heure,minute,seconde,milliseconde);
			} else
				timeDiffSTR = valeurIndefinie;
		} catch (Exception e) {
			timeDiffSTR = valeurIndefinie;
		}

		strBuffer.append(Messages.getString("VueMarqueursDeltaValues.2") + timeDiffSTR)
		.append(Text.DELIMITER); //$NON-NLS-1$
		
		return strBuffer;
	}
	
	private StringBuffer differenceDistance(StringBuffer strBuffer,Message msg1, Message msg2){
		String distDiffSTR = "";
	
		try {
			float distDiff = (float) (msg2.getAccumulatedDistance() - msg1.getAccumulatedDistance());
			if(GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.distanceRelatif.getCode()) instanceof DescripteurVariableAnalogique)
				this.uniteDistance = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distanceRelatif.getCode()).getUnite();
			distDiffSTR = String.valueOf(distDiff) + " " + this.uniteDistance;

		} catch (Exception e) {
			distDiffSTR = valeurIndefinie;
		}
		strBuffer.append(Messages.getString("VueMarqueursDeltaValues.3") + distDiffSTR)
		.append(Text.DELIMITER); //$NON-NLS-1$
		
		return strBuffer;
	}
	
	private StringBuffer differenceDistanceCorrigee(StringBuffer strBuffer,Message msg1, Message msg2){
		if (TableSegments.getInstance().isAppliedDistanceCorrections()) {
			String distDiffCorSTR = "";
			try {
				if (calculerDiffDistance) {
					float distDiffCor = 0;
					distDiffCor = (float) (Float.valueOf(msg2.getDistanceCorrige()) 
							- Float.valueOf(msg1.getDistanceCorrige()));
					distDiffCorSTR = String.valueOf(distDiffCor) + " "	+ uniteDistance;
				} else
					distDiffCorSTR = valeurIndefinie;
			} catch (Exception e) {
				distDiffCorSTR = valeurIndefinie;
			}
			strBuffer.append(Messages.getString("VueMarqueursDeltaValues.13") + distDiffCorSTR)
			.append(Text.DELIMITER); //$NON-NLS-1$			
		}
		return strBuffer;
	}
	
	private StringBuffer differenceVitesse(StringBuffer strBuffer,Message msg1, Message msg2){
		String vitDiffSTR = "";
		boolean calculerDiffVit = false;
		try {

			// si les 2 messages sont dans le meme segments, on peut calculer la
			// différence de temps
			if (msg1.getVariable(TypeRepere.vitesse.getCode()) != null
					&& msg2.getVariable(TypeRepere.vitesse.getCode()) != null)
				calculerDiffVit = true;
			if (calculerDiffVit) {
				float vitDiff = Float.valueOf(msg2.getVariable(TypeRepere.vitesse.getCode()).getCastedValeur().toString()) 
				- Float.valueOf(msg1.getVariable(TypeRepere.vitesse.getCode()).getCastedValeur().toString());
				if(GestionnaireDescripteurs.getDescripteurVariable(TypeRepere.vitesse.getCode()) instanceof DescripteurVariableAnalogique)
					this.uniteVitesse = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.vitesse.getCode()).getUnite();
				vitDiffSTR = String.valueOf(vitDiff) + " " + uniteVitesse;
			} else
				vitDiffSTR = valeurIndefinie;

		} catch (Exception e) {
			vitDiffSTR = valeurIndefinie;
		}
		strBuffer
		.append(
				Messages.getString("VueMarqueursDeltaValues.14") + vitDiffSTR).append(Text.DELIMITER); //$NON-NLS-1$

		return strBuffer;
	}
	
	private StringBuffer differenceVitesseCorrigee(StringBuffer strBuffer,Message msg1, Message msg2){
		if (TableSegments.getInstance().isAppliedDistanceCorrections()) {

			String vitDiffCorSTR = "";
			boolean calculerDiffVitCor = false;
			try {

				// si les 2 messages sont dans le meme segments, on peut calculer la
				// différence de temps
				if (msg1.getVariable(TypeRepere.vitesseCorrigee.getCode()) != null
						&& msg2.getVariable(TypeRepere.vitesseCorrigee.getCode()) != null)
					calculerDiffVitCor = true;
				if (calculerDiffVitCor) {
					float vitDiffCor = Float.valueOf(msg2.getVariable(TypeRepere.vitesseCorrigee.getCode()).getCastedValeur().toString()) 
					- Float.valueOf(msg1.getVariable(TypeRepere.vitesseCorrigee.getCode()).getCastedValeur().toString());
					vitDiffCorSTR = String.valueOf(vitDiffCor) + " " + uniteVitesse;
				} else
					vitDiffCorSTR = valeurIndefinie;

			} catch (Exception e) {
				vitDiffCorSTR = valeurIndefinie;
			}
			strBuffer.append(Messages.getString("VueMarqueursDeltaValues.15") 
					+ " " + vitDiffCorSTR).append(Text.DELIMITER); //$NON-NLS-1$
		}
		return strBuffer;
	}
	
	private StringBuffer differenceVariables(StringBuffer strBuffer,Message msg1, Message msg2){
		List<VariableAnalogique> anaVars1 = msg1.getVariablesAnalogique();
		List<VariableAnalogique> anaVars2 = msg2.getVariablesAnalogique();
		List<Integer> listeCodeAnaVars1 = new ArrayList<Integer>();
		for (VariableAnalogique var : anaVars1) {
			listeCodeAnaVars1.add(var.getDescriptor().getM_AIdentificateurComposant().getCode());
		}
		
		VariableAnalogique var1=null;
		VariableAnalogique var2=null;
		double var1Val;
		double var2Val;
		double varDiff;
		boolean uneVarRenseignee = false;
		boolean firstVarRenseignee = false;
		// search for common analogic variables in the two messages
		if ((anaVars1 != null && anaVars1.size()>0) && (anaVars2 != null && anaVars2.size()>0)) {
			for (Integer code1 : listeCodeAnaVars1) {
				var2=null;
				var1=null;
				if(msg2.getVariable(code1)!=null)
				var2 = (VariableAnalogique) msg2.getVariable(code1);
				if(msg1.getVariable(code1)!=null)
				var1 = (VariableAnalogique)msg1.getVariable(code1);

				if (var1 == null || var2 == null)
					continue;

				if (code1 == TypeRepere.tempsRelatif.getCode()
						|| code1 == TypeRepere.distance.getCode()
						|| code1 == TypeRepere.distanceCorrigee.getCode()
						|| code1 == TypeRepere.distanceRelatif.getCode()
						|| code1 == TypeRepere.tempsCorrigee.getCode()
						|| code1 == TypeRepere.vitesseCorrigee.getCode()
						|| code1 == TypeRepere.vitesse.getCode()) {
					continue;

				} else {

					try {
						var1Val = (Long) var1.getCastedValeur();
						var2Val = (Long) var2.getCastedValeur();
					} catch (ClassCastException ex1) {

						try {

							var1Val = (Double) var1.getCastedValeur();
							var2Val = (Double) var2.getCastedValeur();
						} catch (ClassCastException ex2) {

							try {
								var1Val = (Float) var1.getCastedValeur();
								var2Val = (Float) var2.getCastedValeur();
							} catch (ClassCastException ex3) {

								try {
									var1Val = (Integer) var1.getCastedValeur();
									var2Val = (Integer) var2.getCastedValeur();
								} catch (ClassCastException ex4) {
									try {
										var1Val = (Short) var1
										.getCastedValeur();
										var2Val = (Short) var2
										.getCastedValeur();
									} catch (ClassCastException ex5) {
										var1Val = Double
										.parseDouble((String) var1
												.getCastedValeur());
										var2Val = Double
										.parseDouble((String) var2
												.getCastedValeur());
									}
								}
							}
						}
					}

					if (!firstVarRenseignee) {
						strBuffer.append("").append(Text.DELIMITER);
						strBuffer.append("").append(Text.DELIMITER);
						strBuffer
						.append(
								Messages
								.getString("VueMarqueursDeltaValues.8"))
								.append(Text.DELIMITER);
						strBuffer.append("").append(Text.DELIMITER);
						firstVarRenseignee = true;
					}

					varDiff = Arrondir((var2Val - var1Val), 2);
					BigDecimal b = new BigDecimal(varDiff);
					b = b.setScale(2, RoundingMode.DOWN);
					varDiff = b.doubleValue();
					String nomVar = var1.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					if (nomVar ==null)
						nomVar = var1.getDescriptor()
						.getM_AIdentificateurComposant()
						.getNom();
					strBuffer.append(
							Messages.getString("VueMarqueursDeltaValues.12")
							+ " "
							+ nomVar
							+ " = " + varDiff + " " + //$NON-NLS-1$ //$NON-NLS-2$
							((DescripteurVariableAnalogique) var1
									.getDescriptor()).getUnite())
									.append(Text.DELIMITER);
					uneVarRenseignee = true;
				}
			}
		}
		if (!uneVarRenseignee){
			strBuffer.append("").append(Text.DELIMITER);
			strBuffer.append(Messages.getString("VueMarqueursDeltaValues.18"))
			.append(Text.DELIMITER);
		}
			
		return strBuffer;
	}
	
	private void displayMessagesInformation(Message msg1, Message msg2) {

		StringBuffer strBuffer = new StringBuffer();
		
		strBuffer = differenceTemps(strBuffer,msg1,msg2);
		strBuffer = differenceDistance(strBuffer, msg1, msg2);
		strBuffer = differenceDistanceCorrigee(strBuffer, msg1, msg2);		
		strBuffer = differenceVitesse(strBuffer, msg1, msg2);
		strBuffer = differenceVitesseCorrigee(strBuffer, msg1, msg2);
		strBuffer = differenceVariables(strBuffer, msg1, msg2);
		
		strBuffer.append(Text.DELIMITER);

		this.markersDeltaValuesText.setText(strBuffer.toString());
	}

	@Override
	public void setFocus() {
	}

	/**
	 * Return the message with the given ID from the records table (loaded from
	 * binary file)
	 * 
	 * @param id
	 *            the id of the message to be searched
	 * @return
	 */
	private Message getMessageForId(int id) {
		AParcoursComposant dataTable = ActivatorData.getInstance().getVueData()
		.getDataTable();
		Enregistrement e = dataTable.getEnregistrement();		
		return e.getGoodMessage(id);
	}

	public double Arrondir(double nombre, int nbChiffresApVir) {

		return (Math.round(nombre * Math.pow(10, nbChiffresApVir)) * Math.pow(
				0.1, nbChiffresApVir));
	}

}
