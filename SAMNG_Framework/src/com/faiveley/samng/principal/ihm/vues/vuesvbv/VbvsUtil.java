package com.faiveley.samng.principal.ihm.vues.vuesvbv;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.Operateur;

/**
 * Useful methods for VBV operations
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VbvsUtil {
	private static Operateur[] ALL_OPERATORS = new Operateur[] { Operateur.AND,
			Operateur.OR, Operateur.EQUALS, Operateur.DIFFERENT,
			Operateur.GREATER_THAN, Operateur.GREATER_THAN_OR_EQUALS,
			Operateur.LESS_THAN, Operateur.LESS_THAN_OR_EQUALS };

	private static Operateur[] BOOLEAN_OPERATORS = new Operateur[] {
			Operateur.AND, Operateur.OR, Operateur.EQUALS, Operateur.DIFFERENT };

	private static Operateur[] DISCRETE_OPERATORS = new Operateur[] {
			Operateur.EQUALS, Operateur.DIFFERENT };

	private static Operateur[] CONTINOUS_OPERATORS = new Operateur[] {
			Operateur.EQUALS, Operateur.DIFFERENT, Operateur.GREATER_THAN,
			Operateur.GREATER_THAN_OR_EQUALS, Operateur.LESS_THAN,
			Operateur.LESS_THAN_OR_EQUALS };

	/**
	 * Get all operators possible for a given VBV
	 * 
	 * @param var
	 * @return a table of Operateur object
	 */
	public static Operateur[] getPossibleOperatorsForOperand(
			AVariableComposant var) {
		if (var == null)
			return ALL_OPERATORS;
		if (var.getDescriptor().getType() == Type.boolean1
				|| var.getDescriptor().getType() == Type.boolean8)
			return BOOLEAN_OPERATORS;
		else if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
			if (var.getDescriptor().getType() == Type.boolean1
					|| var.getDescriptor().getType() == Type.boolean8)
				return DISCRETE_OPERATORS;
			else
				return CONTINOUS_OPERATORS;
		} else
			return CONTINOUS_OPERATORS;
	}

	/**
	 * Performs a filtering of the variables for the second operand acording to
	 * the given first variable
	 * 
	 * @param var
	 * @param mapAllVariables
	 *            map from the user name to the variable
	 * @return
	 */
	public static Map<String, AVariableComposant> getPossibleSecondOperandForOperand(
			AVariableComposant var, Map<String, Object> allVariables) {
		Map<String, AVariableComposant> retVarMap = new LinkedHashMap<String, AVariableComposant>();
		if (var == null)
			return retVarMap;
		Type firstVarType = var.getDescriptor().getType();
		if (var.getDescriptor().getM_AIdentificateurComposant().getCode() == TypeRepere.distanceCorrigee
				.getCode()
				|| var.getDescriptor().getM_AIdentificateurComposant()
						.getCode() == TypeRepere.vitesseCorrigee.getCode())
			firstVarType = Type.real32;

		Type secondVarType;
		AVariableComposant filterableVar;
		for (String name : allVariables.keySet()) {
			filterableVar = (AVariableComposant) allVariables.get(name);
			secondVarType = filterableVar.getDescriptor().getType();
			if (firstVarType == Type.boolean1 || firstVarType == Type.boolean8) {
				if (secondVarType == Type.boolean1
						|| secondVarType == Type.boolean8) {
					retVarMap.put(name, filterableVar);
				}
			} else if (firstVarType == Type.array || firstVarType == Type.BCD4
					|| firstVarType == Type.BCD8
					|| firstVarType == Type.intXbits
					|| firstVarType == Type.uintXbits
					|| firstVarType == Type.string
					|| firstVarType == Type.unixTimestamp) {
				if (firstVarType == secondVarType) // we add it only if is
													// exactly the same type
					retVarMap.put(name, filterableVar);
			} else { // just a value (int, real etc) are all types compatible
				if (secondVarType != null && secondVarType != Type.array
						&& secondVarType != Type.BCD4
						&& secondVarType != Type.BCD8
						&& secondVarType != Type.intXbits
						&& secondVarType != Type.uintXbits
						&& secondVarType != Type.string
						&& secondVarType != Type.unixTimestamp
						&& secondVarType != Type.boolean1
						&& secondVarType != Type.boolean8)
					retVarMap.put(name, filterableVar);
			}
		}
		if (firstVarType == Type.boolean1 || firstVarType == Type.boolean8) {
			try {
				DescripteurVariableDiscrete dvd = new DescripteurVariableDiscrete();
				dvd = (DescripteurVariableDiscrete) var.getDescriptor();
				String falseText = dvd.getLabels().get(
						Activator.getDefault().getCurrentLanguage()).get(0)
						.getLabel();
				String trueText = dvd.getLabels().get(
						Activator.getDefault().getCurrentLanguage()).get(1)
						.getLabel();
				retVarMap.put(trueText, null);
				retVarMap.put(falseText, null);
			} catch (RuntimeException e) {
				retVarMap.put(AVariableComposant.BOOL_STR_TRUE_VALUE, null);
				retVarMap.put(AVariableComposant.BOOL_STR_FALSE_VALUE, null);
			}
		}
		return retVarMap;
	}

	/**
	 * Tell if an operator is valid for one varaible given according to his type
	 * 
	 * @param var
	 * @param operator
	 * @return a boolean
	 */
	public static boolean isValidOperator(AVariableComposant var,
			Operateur operator) {
		Operateur[] operators = getPossibleOperatorsForOperand(var);
		for (Operateur op : operators) {
			if (op == operator)
				return true;
		}

		return false;
	}

	/**
	 * Test if value of the var is correct according to his type
	 * 
	 * @param var
	 * @param value
	 * @return
	 */
	public static boolean isValidValue(AVariableComposant var, String value) {
		if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
			VariableDiscrete varDiscrete = (VariableDiscrete) var;
			if(((DescripteurVariableDiscrete) varDiscrete
					.getDescriptor()).getLabels()!=null){
			List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete
					.getDescriptor()).getLabels().get(
					Activator.getDefault().getCurrentLanguage());
			if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
				boolean trouve = false;
				int i = 0;
				while (i < listeLabelValeurs.size() && !trouve) {
					if (listeLabelValeurs.get(i).getLabel().equals(value))
						return true;
					i++;
				}
			}
			}

		}
		Type varType = var.getDescriptor().getType();
		if (varType == Type.boolean1 || varType == Type.boolean8) {
			if (!AVariableComposant.BOOL_STR_TRUE_VALUE.equals(value)
					&& !AVariableComposant.BOOL_STR_FALSE_VALUE.equals(value))
				return false;
		}
		// : perform more validations here for the rest of the possible types of
		// data
		return true;
	}

	public static String getLabel(Object vbvObject) {
		String label = null;
		try {
			if (vbvObject != null) {
				if (vbvObject instanceof Operateur)
					label = ((Operateur) vbvObject).getStringValue();
				else if (vbvObject instanceof AVariableComposant) {
					Langage lang = Activator.getDefault().getCurrentLanguage();
					if (((AVariableComposant) vbvObject).getDescriptor()
							.getNomUtilisateur() == null)
						label = ((AVariableComposant) vbvObject)
								.getDescriptor()
								.getM_AIdentificateurComposant().getNom();

					else if (((AVariableComposant) vbvObject).getDescriptor()
							.getNomUtilisateur().getNomUtilisateur(lang) == null)
						label = ((AVariableComposant) vbvObject)
								.getDescriptor()
								.getM_AIdentificateurComposant().getNom();

					else
						label = ((AVariableComposant) vbvObject)
								.getDescriptor().getNomUtilisateur()
								.getNomUtilisateur(lang);

				} else {
					label = vbvObject.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return label;
	}
}
