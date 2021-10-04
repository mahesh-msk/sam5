package com.faiveley.samng.principal.sm.data.variableComposant;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.List;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.conversionCodage.ConversionBase;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:24
 */
public abstract class AVariableComposant {
	public static final int BOOLEAN_FALSE_VALUE = 0;

	public static final int BOOLEAN_TRUE_VALUE = 1;

	public static final String BOOL_STR_TRUE_VALUE = Messages.getString("AVariableComposant.0");

	public static final String BOOL_STR_FALSE_VALUE =Messages.getString("AVariableComposant.1");

	public static final String NO_VALUE = "###";

	public static final String NULL_VALUE = "null";

	private DescripteurVariable descripteur;

	private byte[] valeur;

	private Type typeValeur;

	private Double doubleCastedValue;

	private boolean escalier = false;

	private AVariableComposant parent;

	public AVariableComposant() {
	}

	public DescripteurVariable getDescriptor() {
		return this.descripteur;
	}

	public void setDescripteur(DescripteurVariable descriptor) {
		this.descripteur = descriptor;
	}

	/**
	 * @return Returns the valeur.
	 */
	public Object getValeur() {
		return this.valeur;
	}

	/**
	 * @param valeur
	 *            The valeur to set.
	 */
	public void setValeur(byte[] valeur) {
		this.valeur = valeur;
	}

	/**
	 * @return the typeValue
	 */
	public Type getTypeValeur() {
		return this.typeValeur;
	}

	/**
	 * @param typeValue
	 *            the typeValue to set
	 */
	public void setTypeValeur(Type typeValue) {
		this.typeValeur = typeValue;
	}

	public Object getCastedValeur() {
		if(getValeurObjet()!=null)
			return getValeurObjet();
		else if(this.valeur!=null && this.typeValeur == Type.string)
			return new String((byte[])this.valeur);
		else return null;

	}

	private Double getVariableAnalogic(byte[]val) {
		if (this.doubleCastedValue == null) {
			double d = 0;
			DescripteurVariableAnalogique dA = (DescripteurVariableAnalogique) getDescriptor();
			if (this.getDescriptor().getType() == Type.real32
					|| this.getDescriptor().getType() == Type.real64) {
				byte[] b = val;
				ByteBuffer bb = ByteBuffer.wrap(b);
				if (b.length > 4) {
					d = bb.getDouble();
				} else {
					d = bb.getFloat();
				}
			} else
				d = new BigInteger(1, (byte[]) val).doubleValue();

			d = d * dA.getCoefDirecteur() + dA.getOrdonneeOrigine();
			this.doubleCastedValue = Double.valueOf(d);
		}
		return this.doubleCastedValue;
	}

	@Override
	public String toString() {
		StringBuffer str = new StringBuffer();

		switch (this.descripteur.getTypeVariable()) {
		case VAR_ANALOGIC:
			Object valeurVarAna = getValeurObjet();
			if (this.getDescriptor().getM_AIdentificateurComposant().getCode() == TypeRepere.distance
					.getCode()) {
				if (valeurVarAna instanceof Double) {
					DecimalFormat fmt = new DecimalFormat("0.000");
					String s = fmt.format(valeurVarAna);
					str.append(s.replace(',', '.'));
				} else {
					if (valeurVarAna != null) {
						try {
							str.append(valeurVarAna.toString());
						} catch (Exception e) {
						}
					} else {
						str.append(NO_VALUE);
					}
				}
			} else {
				if (valeurVarAna != null)
					str.append(valeurVarAna.toString());
				else
					str.append(NO_VALUE);
			}
			break;
		case VAR_DISCRETE:
			TableValeurLabel valeurLabel = ((DescripteurVariableDiscrete) this.descripteur)
					.getLabels();
			Object valeurVar = getValeurObjet();
			if (valeurLabel != null && valeurVar != null) {
				List<LabelValeur> listeLabelvaleur = valeurLabel.get(Activator
						.getDefault().getCurrentLanguage());
				if (listeLabelvaleur != null) {
					int size = listeLabelvaleur.size();
					boolean trouve = false;
					int i = 0;
					Object valeur = null;
					String label = null;

					// Si la table de label est une table de suppression de
					// caractère...
					// Variable de type NID_XXX. Codage BCD.
					if (listeLabelvaleur.get(0).getLabel().equals("$retirer$")) {
						// Obtention de la valeur en hexa dans une string
						label = getHexString((byte[]) this.getValeur());
						String digitValueStr;
						// Seule 1 valeur d'exclusion est autorisée
						// Le caractère exclu sera supprimer de la chaîne
						// affichée
						// ex: 15 : suppression des 'f' de fin de la string
						// affichée
						valeur = listeLabelvaleur.get(0).getValeurs();

						// Pour chaque digit de la string affichée...
						for (i = label.length() - 1; i >= 0; i--) {
							// Convertion de la valeur hexa en valeur décimal
							// dans une string
							digitValueStr = Integer.toString(Character
									.getNumericValue(label.charAt(i)));

							// Si le digit n'est pas égal au caractère à
							// exclure...
							if (!valeur.toString().equals(digitValueStr)) {
								// On prend toute la chaîne sauf les caractère à
								// exclure.
								label = label.substring(0, i + 1);
								break;
							}
						}
						if (i <= -1) {
							label = "";
						}
						trouve = true;
					} else {
						while (!trouve && i < size) {
							valeur = listeLabelvaleur.get(i).getValeurs();
							label = listeLabelvaleur.get(i).getLabel();
							if (valeur.toString().equals(valeurVar.toString())) {
								trouve = true;
								label = listeLabelvaleur.get(i).getLabel();
							}
							i++;
						}
					}
					if (trouve)
						str.append(label);
				}
			} else if (typeValeur == Type.boolean1
					|| typeValeur == Type.boolean8) {
				if (valeurVar != null)
					str.append((Integer) valeurVar == 0 ? BOOL_STR_FALSE_VALUE
							: BOOL_STR_TRUE_VALUE);
				else
					str.append(NULL_VALUE);
			} else {
				if (valeurVar != null)
					str.append(valeurVar.toString());
				else
					str.append(NULL_VALUE);
			}
			break;
		case VAR_VIRTUAL:
			Object valeurObjet = getValeurObjet();
			if (typeValeur == Type.boolean1 || typeValeur == Type.boolean8) {
				if (valeurObjet != null)
					str.append((Integer) valeurObjet == 0 ? BOOL_STR_FALSE_VALUE
							: BOOL_STR_TRUE_VALUE);
				else
					str.append(NULL_VALUE);
			} else {
				str.append(valeurObjet.toString());
			}
			break;
		case VAR_COMPLEXE:
		case VAR_COMPOSEE: {
			VariableComposite varComposite = ((VariableComposite) this);
			int length = varComposite.getVariableCount();
			AVariableComposant var;
			DescripteurVariable descrVar;

			for (int i = 0; i < length; i++) {
				var = varComposite.getEnfant(i);
				descrVar = var.getDescriptor();
				str.append("[")
						.append(descrVar.getM_AIdentificateurComposant()
								.getNom()).append(":").append(var.toString());
				str.append("];");
			}
			break;
		}
		default:
			break;
		}

		return str.toString();
	}

	protected void copyTo(AVariableComposant var) {
		var.descripteur = this.descripteur;
		var.typeValeur = this.typeValeur;
		var.escalier = this.escalier;
	}

	/**
	 * Returns 0 if the two values are equal, -1 if the given value is less than
	 * the current value and 1 if the given value is greater than the current
	 * object value
	 * 
	 * @param value
	 * @return
	 */
	public int compareValueWithStringValue(String value) {

		if (getValeurObjet() == null && value == null)
			return 0;

		if (getValeurObjet() != null) {		
			if (value.contains(".")||value.contains(",")) {
				Double var=new Double(getValeurObjet().toString());
				return var.compareTo(Double.valueOf(value));
			}else if(this.getDescriptor().getM_AIdentificateurComposant().getNom().equals(TypeRepere.vitesseCorrigee.getName())){
				Double var=new Double(getValeurObjet().toString());
				return var.compareTo(Double.valueOf(value));
			}

			if(value.equals(Messages.getString("AVariableComposant.1")))
				value = "0";
			else  if (value.equals(Messages.getString("AVariableComposant.0"))) {
				value = "1";
			}
			if(getValeurObjet() instanceof Integer){
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels() != null) {
						List<LabelValeur> listeLabelValeurs=((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels().get(Activator.getDefault().getCurrentLanguage());
						int valeurVariableLabel =0;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = Integer.parseInt((String)listeLabelValeurs.get(i).getValeurs());
									return ((Integer)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Integer)getValeurObjet()).compareTo(Integer.valueOf(value));
			}else if (getValeurObjet() instanceof Long) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if(((DescripteurVariableDiscrete) varDiscrete
							.getDescriptor()).getLabels()!=null){
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete.getDescriptor()).getLabels().get(Activator.getDefault().getCurrentLanguage());
						Long valeurVariableLabel =0l;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								String label = listeLabelValeurs.get(i).getLabel();
								if (label.equals(value)){
									valeurVariableLabel = Long.parseLong((String)listeLabelValeurs.get(i).getValeurs());
									return ((Long)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Long)getValeurObjet()).compareTo(Long.valueOf(value));

			}else if (getValeurObjet() instanceof Short) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels() != null) {
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete.getDescriptor()).getLabels().get(Activator.getDefault().getCurrentLanguage());
						Short valeurVariableLabel =0;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = Short.parseShort((String)listeLabelValeurs.get(i).getValeurs());
									return ((Short)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Short)getValeurObjet()).compareTo(Short.valueOf(value));

			}else if (getValeurObjet() instanceof Double) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels() != null) {
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete.getDescriptor()).getLabels().get(Activator.getDefault().getCurrentLanguage());
						Double valeurVariableLabel =0d;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = Double.parseDouble((String)listeLabelValeurs.get(i).getValeurs());
									return ((Double)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Double)getValeurObjet()).compareTo(Double.valueOf(value));

			}else if (getValeurObjet() instanceof Float) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels() != null) {
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete.getDescriptor()).getLabels().get(Activator.getDefault().getCurrentLanguage());
						Float valeurVariableLabel =0f;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = Float.parseFloat((String)listeLabelValeurs.get(i).getValeurs());
									return ((Float)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Float)getValeurObjet()).compareTo(Float.valueOf(value));

			}else if (getValeurObjet() instanceof Byte) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels() != null) {
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete
								.getDescriptor()).getLabels().get(
										Activator.getDefault().getCurrentLanguage());
						Byte valeurVariableLabel =0;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = Byte.parseByte((String)listeLabelValeurs.get(i).getValeurs());
									return ((Byte)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Byte)getValeurObjet()).compareTo(Byte.valueOf(value));

			}else if (getValeurObjet() instanceof Boolean) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete)varDiscrete.getDescriptor()).getLabels() != null) {
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete
								.getDescriptor()).getLabels().get(
										Activator.getDefault().getCurrentLanguage());
						Boolean valeurVariableLabel =false;
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = Boolean.parseBoolean((String)listeLabelValeurs.get(i).getValeurs());
									return ((Boolean)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				return ((Boolean)getValeurObjet()).compareTo(Boolean.valueOf(value));

//				}else if (valeurObjet instanceof Byte[]) {
//				return Byte[].valueOf(value).compareTo((Byte[])valeurObjet);
			}else if (getValeurObjet() instanceof String) {
				if(this.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE){
					VariableDiscrete varDiscrete = (VariableDiscrete) this;
					if (((DescripteurVariableDiscrete) varDiscrete.getDescriptor()).getLabels()!=null) {
						List<LabelValeur> listeLabelValeurs = ((DescripteurVariableDiscrete) varDiscrete.getDescriptor()).getLabels().get(Activator.getDefault().getCurrentLanguage());
						String valeurVariableLabel ="";
						if (listeLabelValeurs != null && listeLabelValeurs.size() > 0) {
							boolean trouve = false;
							int i = 0;
							while (i < listeLabelValeurs.size() && !trouve) {
								if (listeLabelValeurs.get(i).getLabel().equals(value)){
									valeurVariableLabel = ((String)listeLabelValeurs.get(i).getValeurs());
									return ((String)getValeurObjet()).compareTo(valeurVariableLabel);
								}
								i++;
							}
						}
					}
				}
				if(((String)getValeurObjet()).trim().equals(String.valueOf(value).trim()))
					return 0;
				else return -1;
			}
		}
		return 0;
	}

	/**
	 * Fonction de calcul d'un arrondi sur un double
	 * 
	 * @param value
	 * @param decimalPlaces
	 * @return un double arrondi à decimalPlaces après la virgule
	 */
	public static double arrondir(double value, int decimalPlaces) {
		if (decimalPlaces < 0) {
			return value;
		}
		double augmentation = Math.pow(10, decimalPlaces);
		return Math.round(value * augmentation) / augmentation;
	}

	public abstract AVariableComposant copy();

	public Object getValeurObjet() {
		return getValeurObjet2((byte[])this.valeur);
	}

	public String getValeurBruteChaineVariableDiscrete(){
		return getValeurObjet().toString();
	}

	private Object getValeurObjet2(byte [] valeurOctets){
		double valTemp = 0;
		ByteBuffer bb=null;
		Object varObject = null;


		if (valeurOctets != null) {
			byte[] val = (byte[]) valeurOctets;



			switch (this.typeValeur) {
			case array:
				// : ???? what type is this? array of what?

				break;
			case BCD4:
				varObject = Byte.valueOf(val[0]);
				break;
			case BCD8: {
				int firstNibble = val[0] & 0xF;
				int lastNibble = ((val[0] & 0xF0) >>> 4);
				varObject = lastNibble * 10 + firstNibble;
				break;
			}
			case boolean1:
				varObject = val[0] != 0 ? BOOLEAN_TRUE_VALUE
						: BOOLEAN_FALSE_VALUE;
				break;
			case boolean8:
				varObject = new BigInteger(1, val).byteValue() != 0 ? BOOLEAN_TRUE_VALUE
						: BOOLEAN_FALSE_VALUE;
				break;

			case int8:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(val).byteValue();
				}
				break;
			case uint8:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(1, val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(1, val).shortValue();
				}
				break;
			case int16:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(val).shortValue();
				}
				break;
			case uint16:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(1, val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;		
					}

				} else {
					varObject = new BigInteger(1, val).longValue();
				}

				// if (this.descripteur.getTypeVariable() ==
				// TypeVariable.VAR_ANALOGIC) {
				// varObject = getVariableAnalogic();
				// } else {
				// varObject = Integer.valueOf(new BigInteger(1,
				// val).intValue());
				// }
				break;

			case real32:
				bb = ByteBuffer.wrap(val);

				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {



					if(((DescripteurVariableAnalogique) this.getDescriptor())
							.getNbDecimales() > 0){
						bb = ByteBuffer.wrap(val);

						varObject = (float)arrondir(((double) bb.getFloat())
								* ((DescripteurVariableAnalogique) this
										.getDescriptor()).getCoefDirecteur()
										+ ((DescripteurVariableAnalogique) this
												.getDescriptor()).getOrdonneeOrigine(),
												((DescripteurVariableAnalogique) this
														.getDescriptor()).getNbDecimales());
					}
					else {

						try{	
							valTemp = (double) arrondir(
									bb.getFloat()
									* ((DescripteurVariableAnalogique) this
											.getDescriptor())
											.getCoefDirecteur()
											+ ((DescripteurVariableAnalogique) this
													.getDescriptor())
													.getOrdonneeOrigine(), 7);
							varObject= valTemp;
						}
						catch(Exception ex){
							String s  = new String(val);	
							varObject = s;

						}






					}


				} else {
					varObject =  bb.getFloat();
				}
				break;

			case real64:

				bb = ByteBuffer.wrap(val);

//				if(this.descripteur.getM_AIdentificateurComposant().getCode()==TypeRepere.distanceCorrigee.getCode())
//				varObject = ByteBuffer.wrap(val).getDouble();
//				else 
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {


					if(((DescripteurVariableAnalogique) this.getDescriptor())
							.getNbDecimales() > 0){

						varObject = arrondir(((double) bb.getDouble())
								* ((DescripteurVariableAnalogique) this
										.getDescriptor()).getCoefDirecteur()
										+ ((DescripteurVariableAnalogique) this
												.getDescriptor()).getOrdonneeOrigine(),
												((DescripteurVariableAnalogique) this
														.getDescriptor()).getNbDecimales());
					}
					else {

						valTemp = arrondir(((double) bb.getDouble())
								* ((DescripteurVariableAnalogique) this
										.getDescriptor()).getCoefDirecteur()
										+ ((DescripteurVariableAnalogique) this
												.getDescriptor()).getOrdonneeOrigine(),
												15);
						varObject = valTemp;



					}

				} else {
					varObject = bb.getDouble();
				}

				break;
			case string:
				// varObject = new String(val);

				try {
					varObject = new String(val, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					//  Auto-generated catch block
					e.printStackTrace();
				}

				break;

			case int24:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(val).intValue();
				}
				break;
			case int32:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(val).intValue();
				}
				break;
			case int64:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(val).longValue();
				}
				break;
			case intXbits:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}
				} else {
					varObject = new BigInteger(val).longValue();
				}
				break;
			case uint24:

				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {

					BigInteger bigInt = new BigInteger(1, val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = valTemp;	


					}
				} else {
					varObject = new BigInteger(1, val).longValue();
				}


				break;
			case uint32:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(1, val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	
					}

				} else {
					varObject = new BigInteger(1, val).longValue();
				}
				break;
			case uint64:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(1, val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	

					}
				} else {
					varObject = new BigInteger(1, val).longValue();
				}
				break;
			case uintXbits:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					BigInteger bigInt = new BigInteger(1, val);

					double ordonneeOrigine = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getOrdonneeOrigine();

					double coefDir = ((DescripteurVariableAnalogique) this
							.getDescriptor()).getCoefDirecteur();

					valTemp = bigInt.longValue() * coefDir + ordonneeOrigine;

					//teste si le résultat directeur est un entier
					if(valTemp == Math.floor(valTemp))
					{
						varObject = new Double(valTemp).longValue();
					} else{
						varObject = (float)valTemp;	


					}

				} else {
					varObject = new BigInteger(1, val).longValue();
				}
				break;
			case unixTimestamp:
				if (this.descripteur.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					varObject = getVariableAnalogic(val);
				} else {
					varObject = Long
					.valueOf(new BigInteger(1, val).longValue());
				}
				break;

			case dateHeureBCD:
				//anciens TOM
				if (val.length==6) {			
					//valeur date sous forme de chaine
					//Par exemple, le 29 juin 1993 à 13h 58mn 30s est codé 290693135830 hexa.
					String f = "";
					String strTmp ="";
					byte b[] = new byte[1];
					for(int i=0;i<6;i++){
						b[0] = val[i];
						strTmp = Integer.toHexString(new BigInteger(b).intValue());

						if(strTmp.length()==1)
							strTmp = "0" +strTmp;
						if(i==2){
							if(Integer.parseInt(strTmp)<90)
								strTmp = "20" + strTmp;
							else 
								strTmp = "19" + strTmp;
						}
						f += strTmp;
					}
					varObject = f;
					break;	
				}else{
					//ATESS
					if (val.length==4) {
						String affiche = "";
						String strTmp ="";
						byte b[] = new byte[1];

						//jour					
						b[0] = val[2];					
						strTmp = Integer.toHexString(new BigInteger(b).intValue());
//						strTmp = String.valueOf(b[0]);				
						if(strTmp.length()==1)
							strTmp = "0" +strTmp;
						affiche += strTmp;

						//mois					
						b[0] = val[1];
						strTmp = ConversionBase.HexaBCDToDecimal(b[0])+"";
//						strTmp = String.valueOf(b[0]);					
						if(strTmp.length()==1)
							strTmp = "0" +strTmp;
						affiche += strTmp;

						//annee	
						b[0] = val[0];						
						strTmp = ConversionBase.HexaBCDToDecimal(b[0])+"";
//						strTmp = String.valueOf(b[0]);

						if(Integer.parseInt(strTmp)<70)
							if(strTmp.length()==1)
								strTmp = "200" + strTmp;
							else
								strTmp = "20" + strTmp;
						else 
							strTmp = "19" + strTmp;

						affiche += strTmp;


						//heure					
						b[0] = val[3];			
						strTmp = ConversionBase.HexaBCDToDecimal(b[0])+"";;


						String heureD=String.valueOf(strTmp);
						if (heureD.length()==1)
							heureD="0"+heureD;
						else
							if(heureD.length()==0)
								heureD="00";					

						affiche += heureD;
						varObject = affiche;
						break;
					}
				}

			default:
				break;
			}
		}

		return varObject;
//		if(GestionnairePool.getValeursVarObject().get(this.valeurObjet)==null){
//		this.valeurObjet = varObject;
//		GestionnairePool.getValeursVarObject().put(this.valeurObjet , this.valeurObjet );
//		}
//		else{
//		this.valeurObjet = GestionnairePool.getValeursVarObject().get(varObject);
//		}

	}

	public static String getHexString(byte[] b) {
		try {
			String result = "";
			for (int i=0; i < b.length; i++) {
				result += Integer.toString((b[i] & 0xff) + 0x100,16).substring(1);
			}
			return result;
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "0";
		}
	}


	/**
	 * Gets the label for a variable
	 * @param v
	 * @return
	 */
	public String getValeurBruteForVariableComposee() {

		if (this.getValeur()!=null) {
			try {
				Langage lang = Activator.getDefault().getCurrentLanguage();
				String nomVariable = this.getDescriptor().getNomUtilisateur().getNomUtilisateur(lang);
				String text = nomVariable+ " : 0x"+getHexString((byte[])this.getValeur());
				return text;
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				return "###";
			}
		}else{
			return "###";
		}
	}

	public AVariableComposant getParent() {
		return parent;
	}

	public void setParent(AVariableComposant parent) {
		this.parent = parent;
	}

	public boolean isEscalier() {
	    return escalier;
	}

	public void setEscalier(boolean escalier) {
	    this.escalier = escalier;
	}
		
}