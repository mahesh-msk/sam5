package com.faiveley.samng.principal.ihm.vues;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.search.ASearchVariableDialog;
import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.calculs.TempsAbsoluDatePartielle;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class AbstractSelectionProviderVue extends ViewPart implements ISelectionProvider {
	protected List<ISelectionChangedListener> selectionListeners;
	public ISelection currentSelection; 
	/**
	 * permet d'utilisr l'op�rateur change
	 * false : on cherche la premi�re valeur
	 * true : la 1�re valeur est trouv�e, on cherche la 2�me
	 */
	protected boolean searchChange=false;

	//OPT_B_1  : stocke la derni�re variable s�lectionn�e (permet dans une suite de Suivant de recherche variable de regarder d'abord dans le message courant)
	public static VariablesSelect varSelectionnee=new VariablesSelect();

	public static int sensRecherche=0; //0:ind�termin�   1:suivant   -1:pr�c�dent

	protected boolean partialDate=false;//la recherche de date est partielle ou non

	public boolean isSearchChange() {
		return searchChange;
	}

	public void setSearchChange(boolean searchChange) {
		this.searchChange = searchChange;
	}

	public AbstractSelectionProviderVue() {
		this.selectionListeners = new LinkedList<ISelectionChangedListener>();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (listener != null) {
			this.selectionListeners.add(listener);
		}
	}

	public ISelection getSelection() {
		return this.currentSelection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		if (listener != null) {
			this.selectionListeners.remove(listener);
		}
	}

	public void setSelection(ISelection selection) {
	}

	public void fireSelectionChanged(ISelection data) {
		SelectionChangedEvent event = new SelectionChangedEvent(this, data);
		
		for (ISelectionChangedListener listener : this.selectionListeners) {
			listener.selectionChanged(event);
		}
	}
	
	public void fireOffsetMsg(int indiceMsg) {
		SelectionChangedEvent event = new SelectionChangedEvent(this, new MessageSelection());
		
		for (ISelectionChangedListener listener : this.selectionListeners) {
			listener.selectionChanged(event);
		}
	}

	protected Message searchVariableDate(Message msg, DescripteurVariable descrVar, String value, Operation op, String[] msgErr,boolean tempsAbsolu){
		AVariableComposant var = msg.getVariable(descrVar);
		if ((var != null)||tempsAbsolu) {
			if (value == null || op == null) {
				return msg;
			}
			String val = value.toString();
			String s=ConversionTemps.getFormatFromResolutionTemps();

			///////////////
			String label = ConversionTemps.getFormattedDate((long) msg.getAbsoluteTime(),true);
			if (op == Operation.ShiftLeft) {
				val = val.replace(",", ".");
				int splito = val.indexOf("...");
				long absolTime1 = ConversionTemps.calculatePeriodAsLong(val.substring(0, splito).toString(), label);
				long absolTime2 = ConversionTemps.calculatePeriodAsLong(val.substring(splito + 3, val.length()),label);
				if (absolTime1>=0 && absolTime2<=0) {
					return msg;
				}
			}else{
				///////////////
				try {
					partialDate=false;
					Date d1 = new SimpleDateFormat(s).parse(val);
					Date d2 = new SimpleDateFormat(s).parse(label);
					//date complete
					if (checkSearchedVariableDate(d2.getTime()-d1.getTime(),op,msgErr)) {
						return msg;
					}
				} catch (ParseException e) {
					//date partielle
					partialDate=true;
					if (checkSearchedTempsAbsoluPartielle(val,op,msg.getAbsoluteTime(),msgErr)) {
						return msg;
					}
				}
			}
		}
		return null;
	}

	public static boolean checkSearchedVariableDate(long diff, Operation op, String[] msgErr) {
		boolean shouldSelect = false;
		switch (op) {
		case Equal: {
			// shouldSelect = diff > 0 && diff < (24 * 3600 * 1000);
			shouldSelect = diff == 0;
			break;
		}
		case Greater: {
			shouldSelect = diff > 0;
			break;
		}
		case GreaterOrEqual: {
			shouldSelect = diff > 0 || diff == 0;
			break;
		}
		case Less: {
			shouldSelect = diff < 0;
			break;
		}
		case LessOrEqual: {
			shouldSelect = diff < 0 || diff == 0;
			break;
		}
		case NotEqual: {
			shouldSelect = diff < 0 || diff > 0;
			break;
		}
		default:
			msgErr[0] = com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.27"); //$NON-NLS-1$
			break;
		}
		return shouldSelect;
	}

	protected boolean checkSearchedVariableVolatile(DescripteurVariable descr, Message msg, String value, Operation op, String[] msgErr, Message message) {
		boolean ret=false;
		List<StructureDynamique> structuresD=msg.getStructuresDynamique();
		List<TableauDynamique> tableauxD=msg.getTableauxDynamique();
		List<ChaineDynamique> chainesD=msg.getChainesDynamique();
		List<VariableAnalogique> varsA=msg.getVariablesAnalogique();
		List<VariableDiscrete> varsD=msg.getVariablesDiscrete();

		for (ChaineDynamique chaineDynamique : chainesD) {
			if (checkSearchedVariableVolatileDynamiqueEntete(descr, msg, value, op, msgErr, message, chaineDynamique.getVariableEntete())) {
				return true;
			}
			if (checkSearchedVariableVolatileDynamique(descr, msg, value, op, msgErr, message, chaineDynamique)) {
				return true;
			}
		}

		for (StructureDynamique structDynamique : structuresD) {
			if (checkSearchedVariableVolatileDynamiqueEntete(descr, msg, value, op, msgErr, message, structDynamique.getVariableEntete())) {
				return true;
			}
			if (checkSearchedVariableVolatileDynamique(descr, msg, value, op, msgErr, message, structDynamique)) {
				return true;
			}
		}

		for (TableauDynamique tabDynamique : tableauxD) {
			if (checkSearchedVariableVolatileDynamiqueEntete(descr, msg, value, op, msgErr, message, tabDynamique.getVariableEntete())) {
				return true;
			}
			if (checkSearchedVariableVolatileDynamique(descr, msg, value, op, msgErr, message, tabDynamique)) {
				return true;
			}
		}

		for (VariableAnalogique varAna : varsA) {
			if (descr==varAna.getDescriptor()) {
				if (checkSearchedVariable(varAna, value, op, msgErr, msg, message)){
					if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), varAna)) {
						return true;
					}
				}
			}
		}

		for (VariableDiscrete vard : varsD) {
			if (descr==vard.getDescriptor()) {
				if (checkSearchedVariable(vard, value, op, msgErr, msg, message)){
					if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), vard)) {
						return true;
					}
				}
			}
		}

		List <AVariableComposant> listVars=new ArrayList<AVariableComposant>();
		listVars.addAll(msg.getVariablesComplexe());
		listVars.addAll(msg.getVariablesComposee());
		for (AVariableComposant var2 : listVars) {
			if (descr==var2.getDescriptor()) {
				if (value == null || op == null)
					if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var2)) {
						return true;
					}
				if (checkSearchedVariable(var2, value, op, msgErr, msg, message)){
					if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var2)) {
						return true;
					}
				}
			}
		}
		return ret;
	}

	protected boolean checkSearchedVariableVolatileDynamique(DescripteurVariable descr, Message msg, String value, Operation op, String[] msgErr, 
			Message message, VariableDynamique varD) {

		boolean ret=false;

		if (varD.getDescriptor().equals(descr)) {
			if (value == null || op == null){
				if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), varD)) {
					return true;
//				}else{
//					return false;
				}
			}
			if (checkSearchedVariable(varD, value, op, msgErr, msg, message)) {
				if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), varD)) {
					return true;
//				}else{
//					return false;
				}
			}
		}

		int nbSousVar=varD.getListeTablesSousVariable()==null ? 0 : varD.getListeTablesSousVariable().size();

		for (int i = 0; i < nbSousVar; i++) {
			TableSousVariable tsv=(TableSousVariable) varD.getListeTablesSousVariable().get(i);
				int nbSousVars=tsv.getNbSousVariables();
			for (int j = 0; j < nbSousVars; j++) {
				AVariableComposant var=tsv.getM_AVariableComposant().get(j);
				
				if (var instanceof VariableDynamique) {
					if (checkSearchedVariableVolatileDynamiqueEntete(descr, msg, value, op, msgErr, message, ((VariableDynamique)(var)).getVariableEntete())) {
						return true;
					}
					if (((((VariableDynamique)var).getVariableEntete().getValeurBruteChaineVariableDiscrete()).compareTo("0") != 0) && checkSearchedVariableVolatileDynamique(descr, msg, value, op, msgErr, message, ((VariableDynamique)(var)))) {
						return true;
					}
				}

				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPOSEE || var.getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
 
					int nbChildren = ((com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe)var).getVariableCount(); 
					AVariableComposant var2;
					
					for (int k = 0 ; k < nbChildren ; k++) {
						
						var2 = ((com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe)var).getEnfant(k);
						
						if (descr==var2.getDescriptor()) {
							if (value == null || op == null){
								if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var2)) {
									return true;
//								}else{
//									return false;
								}
							}
							if (checkSearchedVariable(var2, value, op, msgErr, msg, message)){
								if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var2)) {
									return true;
//								}else{
//									return false;
								}
							}
						}
					}
				}

				if (var instanceof VariableDiscrete || var instanceof VariableAnalogique) {
					if (var.getDescriptor().equals(descr)) {
						if (value == null || op == null) {
							if (varD instanceof ChaineDynamique) {
								var=varD;
							}
							if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var)) {
								return true;
//							}else{
//								return false;
							}
						}
						if (checkSearchedVariable(var, value, op, msgErr, msg, message)){
							if (varD instanceof ChaineDynamique) {
								var=varD;
							}
							if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), var)) {
								return true;
//							}else{
//								return false;
							}
						}
					}
				}
			}			
		}
		return ret;
	}

	protected boolean checkSearchedVariableVolatileDynamiqueEntete(DescripteurVariable descr, Message msg, String value, Operation op, String[] msgErr, 
			Message message, AVariableComposant varD) {

		if (varD.getDescriptor().equals(descr)) {
			if (value == null || op == null){
				if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), varD)) {
					return true;
//				}else{
//					return false;
				}
			}
			if (checkSearchedVariable(varD, value, op, msgErr, msg, message)){
				if (varSelectionnee.setVarSelectionnee(msg.getMessageId(), varD)) {
					return true;
//				}else{
//					return false;
				}
			}
		}
		return false;
	}

	/**
	 * Check if the variable has the value which satisfies the operation
	 * 
	 * @param var
	 *            the variable
	 * @param value
	 *            the value
	 * @param op
	 *            the operation
	 * @param msgErr
	 *            the message error
	 * @param message 
	 * @return true or false if the variable has a value which satisfies the
	 *         operation
	 */
	protected boolean checkSearchedVariable(AVariableComposant var, String value,
			Operation op, String[] msgErr, Message msg, Message msgCourant) {

		boolean shouldSelect = false;

		if (op==null || value=="") {
			return true;
		}

		switch (op) {
		case Equal: {
			int compare = -1;
			if (var.getDescriptor().isVolatil()) {
				String[] labels = VariableExplorationUtils.getValuesFromMessage(msg, var.getDescriptor().getM_AIdentificateurComposant().getNom());
				boolean containsSearchedValue = false;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descr = (DescripteurVariableDiscrete) var.getDescriptor();
					TableValeurLabel valeursLabels = descr.getLabels();
					if (valeursLabels != null) {
						// V�rification dans le cas des type boolean dont la valeut est "Vrai/faux" que l'on compare �  0/1
						if(Type.boolean1.equals(var.getTypeValeur()) || Type.boolean8.equals(var.getTypeValeur()) ){
							compare = var.compareValueWithStringValue(value);
						} else {
							/* issue 1026 correction */
//							List<LabelValeur> listeLabelValeurs = valeursLabels
//									.get(Activator.getDefault()
//											.getCurrentLanguage());
//							String[] values = getValues(labels, listeLabelValeurs);
//							containsSearchedValue = Arrays.asList(values).contains(value) || Arrays.asList(labels).contains(value);
//							compare = containsSearchedValue ? 0 : -1;
							compare = var.compareValueWithStringValue(value);
						}
					} else {
						compare = var.compareValueWithStringValue(value);
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					containsSearchedValue = Arrays.asList(labels).contains(value);
					compare = containsSearchedValue ? 0 : -1;
				}
			} else {
				compare = var.compareValueWithStringValue(value);
			}
			shouldSelect = compare == 0;
			break;
		}
		case Greater: {
			int compare = -1;
			if (var.getDescriptor().isVolatil()) {
				String[] labels = VariableExplorationUtils.getValuesFromMessage(msg, var.getDescriptor().getM_AIdentificateurComposant().getNom());
				boolean containsGreaterValue = false;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descr = (DescripteurVariableDiscrete) var.getDescriptor();
					TableValeurLabel valeursLabels = descr.getLabels();
					if (valeursLabels != null) {
						// V�rification dans le cas des type boolean dont la valeut est "Vrai/faux" que l'on compare �  0/1
						if(Type.boolean1.equals(var.getTypeValeur()) || Type.boolean8.equals(var.getTypeValeur()) ){
							compare = var.compareValueWithStringValue(value);
						} else {
							List<LabelValeur> listeLabelValeurs = valeursLabels.get(Activator.getDefault().getCurrentLanguage());
							String[] values = getValues(labels, listeLabelValeurs);
							Integer testValue = Integer.parseInt(value);
							for (String val : values) {
								Integer valInt = Integer.parseInt(val);
								if (valInt > testValue) {
									containsGreaterValue = true;
									break;
								}
							}
							compare = containsGreaterValue ? 1 : 0;
						}
					} else {
						compare = var.compareValueWithStringValue(value);
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					for (String label : labels) {
						int result = compareAnalogicVariableWithStringValue(var, label, value);
						if (result > 0) {
							containsGreaterValue = true;
							break;
						}
					}
					compare = containsGreaterValue ? 1 : 0;
				}
			} else {
				compare = var.compareValueWithStringValue(value);
			}
			shouldSelect = compare > 0;
			break;
		}
		case GreaterOrEqual: {
			int compare = -1;
			if (var.getDescriptor().isVolatil()) {
				String[] labels = VariableExplorationUtils.getValuesFromMessage(msg, var.getDescriptor().getM_AIdentificateurComposant().getNom());
				boolean containsGreaterOrEqualValue = false;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descr = (DescripteurVariableDiscrete) var.getDescriptor();
					TableValeurLabel valeursLabels = descr.getLabels();
					if (valeursLabels != null) {
						// V�rification dans le cas des type boolean dont la valeut est "Vrai/faux" que l'on compare �  0/1
						if(Type.boolean1.equals(var.getTypeValeur()) || Type.boolean8.equals(var.getTypeValeur()) ){
							compare = var.compareValueWithStringValue(value);
						} else {
							List<LabelValeur> listeLabelValeurs = valeursLabels.get(Activator.getDefault().getCurrentLanguage());
							String[] values = getValues(labels, listeLabelValeurs);
							Integer testValue = Integer.parseInt(value);
							for (String val : values) {
								Integer valInt = Integer.parseInt(val);
								if (valInt >= testValue) {
									containsGreaterOrEqualValue = true;
									break;
								}
							}
							compare = containsGreaterOrEqualValue ? 1 : -1;
						}
					}else{
						compare = var.compareValueWithStringValue(value);
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					for (String label : labels) {
						int result = compareAnalogicVariableWithStringValue(var, label, value);
						if (result >= 0) {
							containsGreaterOrEqualValue = true;
							break;
						}
					}
					compare = containsGreaterOrEqualValue ? 1 : -1;
				}
			} else {
				compare = var.compareValueWithStringValue(value);
			}
			shouldSelect = compare >= 0;
			break;
		}
		case Less: {
			int compare = 0;
			if (var.getDescriptor().isVolatil()) {
				String[] labels = VariableExplorationUtils.getValuesFromMessage(msg, var.getDescriptor().getM_AIdentificateurComposant().getNom());
				boolean containsLessValue = false;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descr = (DescripteurVariableDiscrete) var.getDescriptor();
					TableValeurLabel valeursLabels = descr.getLabels();
					if (valeursLabels != null) {
						// V�rification dans le cas des type boolean dont la valeut est "Vrai/faux" que l'on compare �  0/1
						if(Type.boolean1.equals(var.getTypeValeur()) || Type.boolean8.equals(var.getTypeValeur()) ){
							compare = var.compareValueWithStringValue(value);
						} else {
							List<LabelValeur> listeLabelValeurs = valeursLabels.get(Activator.getDefault().getCurrentLanguage());
							String[] values = getValues(labels, listeLabelValeurs);
							Integer testValue = Integer.parseInt(value);
							for (String val : values) {
								Integer valInt = Integer.parseInt(val);
								if (valInt < testValue) {
									containsLessValue = true;
									break;
								}
							}
							compare = containsLessValue ? -1 : 0;
						}
					}else{
						compare = var.compareValueWithStringValue(value);
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					for (String label : labels) {
						int result = compareAnalogicVariableWithStringValue(var, label, value);
						if (result < 0) {
							containsLessValue = true;
							break;
						}
					}
					compare = containsLessValue ? -1 : 0;
				}
			} else {
				compare = var.compareValueWithStringValue(value);
			}
			shouldSelect = compare < 0;
			break;
		}
		case LessOrEqual: {
			int compare = -1;
			if (var.getDescriptor().isVolatil()) {
				String[] labels = VariableExplorationUtils.getValuesFromMessage(msg, var.getDescriptor().getM_AIdentificateurComposant().getNom());
				boolean containsLessOrEqualValue = false;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descr = (DescripteurVariableDiscrete) var.getDescriptor();
					TableValeurLabel valeursLabels = descr.getLabels();
					if (valeursLabels != null) {
						// V�rification dans le cas des type boolean dont la valeut est "Vrai/faux" que l'on compare �  0/1
						if(Type.boolean1.equals(var.getTypeValeur()) || Type.boolean8.equals(var.getTypeValeur()) ){
							compare = var.compareValueWithStringValue(value);
						} else {
							List<LabelValeur> listeLabelValeurs = valeursLabels.get(Activator.getDefault().getCurrentLanguage());
							String[] values = getValues(labels, listeLabelValeurs);
							Integer testValue = Integer.parseInt(value);
							for (String val : values) {
								Integer valInt = Integer.parseInt(val);
								if (valInt <= testValue) {
									containsLessOrEqualValue = true;
									break;
								}
							}
							compare = containsLessOrEqualValue ? -1 : 1;
						}
					}else{
						compare = var.compareValueWithStringValue(value);
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					for (String label : labels) {
						int result = compareAnalogicVariableWithStringValue(var, label, value);
						if (result <= 0) {
							containsLessOrEqualValue = true;
							break;
						}
					}
					compare = containsLessOrEqualValue ? -1 : 1;
				}
			} else {
				compare = var.compareValueWithStringValue(value);
			}
			shouldSelect = compare <= 0;
			break;
		}
		case NotEqual: {
			int compare = -1;
			if (var.getDescriptor().isVolatil()) {
				String[] labels = VariableExplorationUtils.getValuesFromMessage(msg, var.getDescriptor().getM_AIdentificateurComposant().getNom());
				boolean containsDifferentValue = false;
				if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
					DescripteurVariableDiscrete descr = (DescripteurVariableDiscrete) var.getDescriptor();
					TableValeurLabel valeursLabels = descr.getLabels();
					if (valeursLabels != null) {
						// V�rification dans le cas des type boolean dont la valeut est "Vrai/faux" que l'on compare �  0/1
						if(Type.boolean1.equals(var.getTypeValeur()) || Type.boolean8.equals(var.getTypeValeur()) ){
							compare = var.compareValueWithStringValue(value);
						} else {
							List<LabelValeur> listeLabelValeurs = valeursLabels.get(Activator.getDefault().getCurrentLanguage());
							String[] values = getValues(labels, listeLabelValeurs);
							
							boolean valueString;
							try {
								Integer.parseInt(value);
								valueString = false;
							} catch (NumberFormatException e) {
								valueString = true;
							}
							
							if(!valueString){
								for (String val : values) {
									if (!value.equals(val)) {
										containsDifferentValue = true;
										break;
									}
								}								
							}else {
								// Cas ou il faut directement comparer avec les labels
								for (String label : labels) {
									if (!value.equals(label)) {
										containsDifferentValue = true;
										break;
									}
								}								
							}
											
							compare = containsDifferentValue ? 1 : 0;
						}
					} else {
						compare = var.compareValueWithStringValue(value);
					}
				} else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					for (String val : labels) {
						if (!value.equals(val)) {
							containsDifferentValue = true;
							break;
						}
					}
					compare = containsDifferentValue ? 1 : 0;
				}
			} else {
				compare = var.compareValueWithStringValue(value);
			}
			shouldSelect = compare != 0;
			break;
		}
		case ShiftLeft: {
			value = value.replace(",", ".");
			int splito = value.indexOf("...");
			String val1 = Double.valueOf(value.substring(0, splito)).toString();
			String val2 = Double.valueOf(value.substring(splito + 3, value.length())).toString();
			int compare1 = var.compareValueWithStringValue(val1);
			int compare2 = var.compareValueWithStringValue(val2);
			shouldSelect = compare1 == 1 && compare2 == -1;
			break;
		}
		case Change: {
			shouldSelect=false;// valeur � retourner
			String valeur=var.toString();// valeur de la variable		
			AVariableComposant currentMsgVar=msgCourant.getVariable(var.getDescriptor());
			String firstVal=currentMsgVar==null ? "" : currentMsgVar.toString();

			if (value.equals(ASearchVariableDialog.TOUSLESCHANGEMENTS)) {
				//on cherche tous les changements
				if (!valeur.equals(firstVal)) {
					this.setSearchChange(false);
					shouldSelect=true;
				}
			}else if (value.contains(ASearchVariableDialog.DE)&&
					value.contains(ASearchVariableDialog.VERSAUTREVALEUR)) {
				//on cherche un changement d'une valeur vers n'importe quelle autre valeur
				int indiceValeur1=ASearchVariableDialog.DE.length();
				int indiceValeur2=value.indexOf(ASearchVariableDialog.VERSAUTREVALEUR);
				String val=value.substring(indiceValeur1+2, indiceValeur2-2);

				if (firstVal.equals(val)) {
					this.setSearchChange(true);
				}

				if(!this.isSearchChange()){
					//on cherche la 1�re valeur 
					if (val.equals(valeur)||firstVal.equals(val)) {
						this.setSearchChange(true);
					}
				}else{
					//on cherche la 2�me valeur
					if (!val.equals(valeur)) {
						this.setSearchChange(false);
						shouldSelect=true;
					}
				}
			}else{
				//on cherche un changement en particulier
				int indice2=value.indexOf(ASearchVariableDialog.VERS);
				String valeur1=value.substring(ASearchVariableDialog.DE.length()+2, indice2-2);
				String valeur2=value.substring(indice2+2 + ASearchVariableDialog.VERS.length(), value.length()-2);

				if (firstVal.equals(valeur1)) {
					this.setSearchChange(true);
				}

				if(!this.isSearchChange()){
					//on cherche la 1�re valeur 
					if (valeur.equals(valeur1)) {
						this.setSearchChange(true);
					}
				}else{
					//on cherche la 2�me valeur
					if (valeur.equals(valeur2)) {
						this.setSearchChange(false);
						shouldSelect=true;
					}
				}
				break;
			}
		}
		}
		return shouldSelect;
	}
	
	protected String[] getValues(String[] labels, List<LabelValeur> labelsValeurs) {
		String[] values = new String[labels.length];
		
		for (int i = 0; i < values.length; i++) {
			String label = labels[i];
			String value = getValeurForLabel(label, labelsValeurs);
			values[i] = value;
		}
		
		return values;
	}
	
	private String getValeurForLabel(String label, List<LabelValeur> labelsValeurs) {
		for (LabelValeur labelValeur : labelsValeurs) {
			if (label.equals(labelValeur.getLabel())) {
				return (String) labelValeur.getValeurs();
			}
		}
		return null;
	}
	
	private int compareAnalogicVariableWithStringValue(AVariableComposant aVar, String value1, String value2) {
		if (aVar.getValeurObjet() instanceof Integer) {
			return Integer.valueOf(value1).compareTo(Integer.valueOf(value2));
		} else if (aVar.getValeurObjet() instanceof Long) {
			return Long.valueOf(value1).compareTo(Long.valueOf(value2));
		} else if (aVar.getValeurObjet() instanceof Short) {
			return Short.valueOf(value1).compareTo(Short.valueOf(value2));
		} else if (aVar.getValeurObjet() instanceof Double) {
			return Double.valueOf(value1).compareTo(Double.valueOf(value2));
		} else if (aVar.getValeurObjet() instanceof Float) {
			return Float.valueOf(value1).compareTo(Float.valueOf(value2));
		} else if (aVar.getValeurObjet() instanceof Byte) {
			return Byte.valueOf(value1).compareTo(Byte.valueOf(value2));
		} else if (aVar.getValeurObjet() instanceof Boolean) {
			return Boolean.valueOf(value1).compareTo(Boolean.valueOf(value2));
		} else {
			if(value1.trim().equals(value2.trim())) {
				return 0;
			} else {
				return -1;
			}
		}
	}

	protected int getIndiceMessagePlusProcheEnDate(int rowIdx,Message msg1,Message msg2,long t,boolean next){
		long t1=msg1.getAbsoluteTime();
		long t2=msg2.getAbsoluteTime();
		if (Math.abs(t1-t)<Math.abs(t2-t)) {
			//le message precedent a une date plus proche
			return (next ? rowIdx-1 : rowIdx+1);
		}else{
			//le message suivant a une date plus proche
			return (rowIdx);
		}
	}

	/**
	 * Get a row index by the id of the message id associated to that row
	 * 
	 * @param msgId
	 *            the searched message ID
	 * @param messageIDSelectNow 
	 * @param value 
	 * @param op 
	 * @param msgErr 
	 * @param elements 
	 * @param time 
	 * @return the found row index or -1 if no such message ID found in rows
	 */
	protected int getRowIndexForMessageDate(int msgId, DescripteurVariable descripteurVariable, boolean next, 
			long t, int messageIDSelectNow, Operation op, String value, String[] msgErr, Object[] elements) {
		int rowId;
		Object rowData;
		int rowIdx = next ? 0 : elements.length-1;
		int retIdx = -1;
		try{
			setNotEquivalentForSearch(false);
			for (int i=next ? 0 : (elements.length-1); next ? i<elements.length : i>-1; i= (next ? (i+1) : (i-1) )) {	//si next, on incremente sinon on decremente
				Object row=elements[i];

				if (row instanceof Row) {
					rowData = ((Row) row).getData();
					if (rowData != null && rowData instanceof Message) {
						rowId = ((Message) rowData).getMessageId();

						//si on a trouv� ou d�pass� l'ID recherch�
						if (next ? (rowId >= msgId) : (rowId <= msgId)) {

							//si l'ID est trouv� on s�lectionne le message
							if (rowId == msgId) {
								retIdx = rowIdx;
								break;
							}else{
								//si on a d�passs� l'ID

								//s�lection des messages suivants et pr�c�dents :
								int idPrec=next ? rowIdx-1 : rowIdx+1;
								//si c'est le premier, on reste sur le premier msg
								if (rowIdx<=0) {
									idPrec=0;
								}else if (rowIdx>=elements.length-1) {
									//si c'est le dernier, on reste sur le dernier msg	
									idPrec=elements.length-1;
								}
								Message msg1=((Message)((Row)(elements[idPrec])).getData());
								Message msg2=(Message) rowData;
								Date d1 = null;
								if (!partialDate) {
									String s=ConversionTemps.getFormatFromResolutionTemps();
									d1 = new SimpleDateFormat(s).parse(value);
								}


								///////////////////////////

								long timeMsgPrec=0;
								long timeMsgSuiv=0;

								timeMsgPrec=msg1.getAbsoluteTime();
								timeMsgSuiv=msg2.getAbsoluteTime();

								// si la date du msg Suivant correspond aux crit�res on le s�lectionne
								if (partialDate ? checkSearchedTempsAbsoluPartielle(value,op,timeMsgSuiv,msgErr) :
									(checkSearchedVariableDate(timeMsgSuiv-d1.getTime(),op,msgErr)) 
										) {
									retIdx=rowIdx;
									break;
								}else if (partialDate ? checkSearchedTempsAbsoluPartielle(value,op,timeMsgPrec,msgErr) :
									(checkSearchedVariableDate(timeMsgPrec-d1.getTime(),op,msgErr)) 
										){
									//sinon si la date du msg Precedent correspond aux crit�res on le s�lectionne
									retIdx = next ? rowIdx-1 : rowIdx+1;

									//sauf 

									//si c'est le premier, on reste sur le premier msg
									if (retIdx<=0) {
										retIdx=0;
									}else if (retIdx>=elements.length-1) {
										//si c'est le dernier, on reste sur le dernier msg	
										retIdx=elements.length-1;
									}

									msgErr[0]=com.faiveley.samng.principal.ihm.vues.Messages.getString("AVueTable.25");
									break;
								}else{
									//DR26_3A_CL08
									//sinon on doit s�lectionner la date la plus proche
									setNotEquivalentForSearch(true);
									//on doit chercher la date la plus proche	
									retIdx=getIndiceMessagePlusProcheEnDate(rowIdx, msg1, msg2, retIdx,next);
									break;
								}
							}
						}
					}
					if (next) {
						rowIdx++;
					}else{
						rowIdx--;
					}
				}
			}
		}catch(Exception ex){
			retIdx = -1;
		}
		selectMessage(retIdx,descripteurVariable);

		return retIdx;
	}

	public void setNotEquivalent(boolean b){

	}

	public void setNotEquivalentForSearch(boolean b){

	}

	public void selectMessage(int retIdx, DescripteurVariable descripteurVariable){

	}

	public static boolean checkSearchedTempsAbsoluPartielle(String date, Operation op, long absoluteTime, String[] msgErr) {		
		boolean dateMalFormee=false;//si la date n'est pas recuperable => true
		boolean operateurIncompatibleWithPartialDate=false;
		int longueurChaine=date.length();
		int limitJour=2;
		int limitMois=5;
		int limitAnnee=10;
		int limitHeure=13;
		int limitMinutes=16;
		int limitSecondes=19;
		String jour="";
		String mois="";
		String annee="";
		String heure="";
		String minutes="";
		String secondes="";
		int j,m,a,h,min,sec;

		try {
			if (limitJour<=longueurChaine) {
				jour=date.substring(0,limitJour);
				j=Integer.valueOf(jour);
				//le jour est valide
				if (limitMois<=longueurChaine) {
					mois=date.substring(limitJour+1,limitMois);
					m=Integer.valueOf(mois);
					//le mois est valide 
					if (!(date.charAt(limitJour)+"").equals("/")) {
						dateMalFormee=true;
					}else if (limitAnnee<=longueurChaine) {
						annee=date.substring(limitMois+1,limitAnnee);
						a=Integer.valueOf(annee);
						//l'ann�e est valide
						if (!(date.charAt(limitMois)+"").equals("/")) {
							dateMalFormee=true;
						}else if (limitHeure<=longueurChaine) {
							heure=date.substring(limitAnnee+1, limitHeure);
							h=Integer.valueOf(heure);
							//l'heure est valide
							if (!(date.charAt(limitAnnee)+"").equals(" ")) {
								dateMalFormee=true;
							}else if (limitMinutes<=longueurChaine) {
								minutes=date.substring(limitHeure+1, limitMinutes);
								//les minutes sont valides
								min=Integer.valueOf(minutes);
								if (!(date.charAt(limitHeure)+"").equals(":")) {
									dateMalFormee=true;
								}else if (limitSecondes<=longueurChaine) {
									secondes=date.substring(limitMinutes+1, limitSecondes);
									sec=Integer.valueOf(secondes);
									//les secondes sont valides
									if (!(date.charAt(limitMinutes)+"").equals(":")) {
										dateMalFormee=true;
									}else{
										//la date JJ/MM/AAAA HH:MM:SS est bien  form�e
										TempsAbsoluDatePartielle tadp=new TempsAbsoluDatePartielle(j,m,a,h,min,sec,absoluteTime,op,limitSecondes);
										return tadp.datePartielleMatch();
									}
								}else{
									//la date JJ/MM/AAAA HH:MM est bien  form�e
									TempsAbsoluDatePartielle tadp=new TempsAbsoluDatePartielle(j,m,a,h,min,null,absoluteTime,op,limitMinutes);
									return tadp.datePartielleMatch();
								}
							}else{
								//la date JJ/MM/AAAA HH est bien  form�e
								TempsAbsoluDatePartielle tadp=new TempsAbsoluDatePartielle(j,m,a,h,null,null,absoluteTime,op,limitHeure);
								return tadp.datePartielleMatch();
							}
						}else{
							//la date JJ/MM/AAAA est bien  form�e
							TempsAbsoluDatePartielle tadp=new TempsAbsoluDatePartielle(j,m,a,null,null,null,absoluteTime,op,limitAnnee);
							return tadp.datePartielleMatch();
						}
					}else{
						if (op==Operation.Equal || op==Operation.NotEqual) {
							//la date JJ/MM est bien  form�e
							TempsAbsoluDatePartielle tadp=new TempsAbsoluDatePartielle(j,m,null,null,null,null,absoluteTime,op,limitMois);
							return tadp.datePartielleMatch();
						}else{
							operateurIncompatibleWithPartialDate=true;
						}
					}
				}else{
					if (op==Operation.Equal || op==Operation.NotEqual) {
						//la date JJ est bien  form�e
						TempsAbsoluDatePartielle tadp=new TempsAbsoluDatePartielle(j,null,null,null,null,null,absoluteTime,op,limitJour);
						return tadp.datePartielleMatch();
					}else{
						operateurIncompatibleWithPartialDate=true;
					}
				}
			}else{
				dateMalFormee=true;
			}
		} catch (NumberFormatException e) {
			dateMalFormee=true;
		}
		if (dateMalFormee) {// si date inexploitable
			msgErr[0] = Messages.getString("DateIncorrecte.0");
		}
		if (operateurIncompatibleWithPartialDate) {
			msgErr[0] = Messages.getString("OperateurIncompatible.0");
		}
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
}
