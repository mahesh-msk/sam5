package com.faiveley.samng.principal.sm.data.enregistrement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:16
 */
public class Message {

	private int longueur;
	protected int messageId;
	private ErrorType error;
	private Evenement evenement;
	private boolean isRepere0;
	private boolean isMessageIncertitude;
	private boolean evNonDate = false;
	private Flag flag;
	private long absoluteTime;
	private double absoluteDistance;
	private double accumulatedDistance;
	private double diametreRoue;
	private String distanceCorrigee;
	private String tempsCorrige;
	protected byte[] messageData;
	private double cumulDistance;
	private int offsetDebut;
	private int offsetFin;
	private String errorInfo;

	private ArrayList<VariableDiscrete> listeVariablesDiscrete;
	private ArrayList<VariableAnalogique> listeVariablesAnalogique;
	private ArrayList<VariableDiscrete> listeVariablesVirtuelle;
	private ArrayList<VariableComplexe> listeVariablesComplexe;
	private ArrayList<VariableComposite> listeVariablesComposee;
	private ArrayList<StructureDynamique> listeStructureDynamique;
	private ArrayList<TableauDynamique> listeTableauDynamique;
	private ArrayList<ChaineDynamique> listeChaineDynamique;

	public Message() {

	}

	public Evenement getEvenement() {
		return evenement;
	}

	public void setEvenement(Evenement event) {
		evenement = event;
	}

	/**
	 * @return the flag
	 */
	public Flag getFlag() {
		return flag;
	}

	/**
	 * @param flag
	 *            the flag to set
	 */
	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	/**
	 * longueur en octet du message
	 * maximum 18 octets
	 * longueurEvenement + cumulLongueurVariables
	 */
	public int getLongueur(){
		return this.longueur;
	}

	/**
	 * longueur en octet du message
	 * maximum 18 octets
	 * longueurEvenement + cumulLongueurVariables
	 * 
	 * @param newVal
	 */
	public void setLongueur(int newVal){
		this.longueur = newVal;
	}
	
	/**
	 * Set id as the start address of the message
	 * @param id
	 */
	public void setMessageId(int id) {
		this.messageId = id;
	}
	
	/**
	 * The start adress of the message
	 * @return
	 */
	public int getMessageId() {
		return this.messageId;
	}

	/**
	 * @return the isCRCValid
	 */
	public boolean isCRCValid() {
		return error == null
				|| (error != null && !error.equals(ErrorType.CRC));
	}

	public List<VariableAnalogique> getVariablesAnalogique() {
		if (listeVariablesAnalogique == null) {
			return new ArrayList<VariableAnalogique>(0);
		}
		return listeVariablesAnalogique;
	}

	public List<VariableDiscrete> getVariablesDiscrete() {
		if (listeVariablesDiscrete == null) {
			return new ArrayList<VariableDiscrete>(0);
		}
		return listeVariablesDiscrete;
	}

	public List<VariableDiscrete> getVariablesVirtuelle() {
		if (listeVariablesVirtuelle == null) {
			return new ArrayList<VariableDiscrete>(0);
		}
		return listeVariablesVirtuelle;
	}

	public List<VariableComplexe> getVariablesComplexe() {
		if (listeVariablesComplexe == null) {
			return new ArrayList<VariableComplexe>(0);
		}
		return listeVariablesComplexe;
	}

	public List<VariableComposite> getVariablesComposee() {
		if (listeVariablesComposee == null) {
			return new ArrayList<VariableComposite>(0);
		}
		return listeVariablesComposee;
	}

	public AVariableComposant getVariable(String nom) {
		List<AVariableComposant> listeToutesVariables = new ArrayList<AVariableComposant>();
		if (listeVariablesAnalogique != null)
			listeToutesVariables.addAll(listeVariablesAnalogique);

		if (listeVariablesDiscrete != null)
			listeToutesVariables.addAll(listeVariablesDiscrete);

		if (listeVariablesVirtuelle != null)
			listeToutesVariables.addAll(listeVariablesVirtuelle);

		if (listeVariablesComplexe != null)
			listeToutesVariables.addAll(listeVariablesComplexe);

		if (listeVariablesComposee != null)
			listeToutesVariables.addAll(listeVariablesComposee);

		if (this.listeStructureDynamique != null)
			listeToutesVariables.addAll(this.listeStructureDynamique);

		if (this.listeTableauDynamique != null)
			listeToutesVariables.addAll(this.listeTableauDynamique);

		if (this.listeChaineDynamique != null)
			listeToutesVariables.addAll(this.listeChaineDynamique);
		
		for (AVariableComposant var : listeToutesVariables) {
			if (var.getDescriptor().getM_AIdentificateurComposant().getNom().equals(nom)) {
				return var;
			}
		}

		return null;
	}
	
	public AVariableComposant getVariable(int code) {
		List<AVariableComposant> listeToutesVariables = new ArrayList<AVariableComposant>();
		if (listeVariablesAnalogique != null)
			listeToutesVariables.addAll(listeVariablesAnalogique);

		if (listeVariablesDiscrete != null)
			listeToutesVariables.addAll(listeVariablesDiscrete);

		if (listeVariablesVirtuelle != null)
			listeToutesVariables.addAll(listeVariablesVirtuelle);

		if (listeVariablesComplexe != null)
			listeToutesVariables.addAll(listeVariablesComplexe);

		if (listeVariablesComposee != null)
			listeToutesVariables.addAll(listeVariablesComposee);

		if (this.listeStructureDynamique != null)
			listeToutesVariables.addAll(this.listeStructureDynamique);

		if (this.listeTableauDynamique != null)
			listeToutesVariables.addAll(this.listeTableauDynamique);

		if (this.listeChaineDynamique != null)
			listeToutesVariables.addAll(this.listeChaineDynamique);
		
		for (AVariableComposant var : listeToutesVariables) {
			if (var.getDescriptor().getM_AIdentificateurComposant().getCode() == code) {
				return var;
			}
		}

		return null;
	}

	public void resetComteurTemps() {
		AVariableComposant relVar = null;
		relVar = GestionnairePool.getInstance().getVariable(
				TypeRepere.temps.getCode());
		if (relVar != null) {
			relVar.setValeur("0".getBytes());// tagValCor

			// add variable to the message
			ajouterVariable(relVar);
		}
	}

	public AVariableComposant getVariable(DescripteurVariable descrVar) {
		AVariableComposant variable = null;
		List<AVariableComposant> listeVariablesNormales = new ArrayList<AVariableComposant>();
		List<AVariableComposant> listeVariablesComposite = new ArrayList<AVariableComposant>();
		List<VariableDynamique> listeVariablesDynamiques = new ArrayList<VariableDynamique>();

		if (descrVar != null) {
			int code = descrVar.getM_AIdentificateurComposant().getCode();
			String nom = descrVar.getM_AIdentificateurComposant().getNom();
			
			boolean trouve = false;
			if (listeVariablesAnalogique != null)
				listeVariablesNormales.addAll(listeVariablesAnalogique);

			if (listeVariablesDiscrete != null)
				listeVariablesNormales.addAll(listeVariablesDiscrete);

			if (listeVariablesVirtuelle != null)
				listeVariablesNormales.addAll(listeVariablesVirtuelle);

			if (listeVariablesComplexe != null)
				listeVariablesComposite.addAll(listeVariablesComplexe);

			if (listeVariablesComposee != null)
				listeVariablesComposite.addAll(listeVariablesComposee);
			
			if (listeStructureDynamique != null)
				listeVariablesDynamiques.addAll(listeStructureDynamique);

			if (listeTableauDynamique != null)
				listeVariablesDynamiques.addAll(listeTableauDynamique);

			if (listeChaineDynamique != null)
				listeVariablesDynamiques.addAll(listeChaineDynamique);

			int index = 0;
			while (!trouve && index < listeVariablesNormales.size()) {
				if (listeVariablesNormales.get(index).getDescriptor().getM_AIdentificateurComposant().getCode() == code) {
					variable = listeVariablesNormales.get(index);
					trouve = true;
				}
				index++;
			}

			index = 0;
			while (!trouve && index < listeVariablesDynamiques.size()) {
				if (listeVariablesDynamiques.get(index).getDescriptor().getM_AIdentificateurComposant().getCode() == code) {
					variable = listeVariablesDynamiques.get(index);
					trouve = true;
				} else if(listeVariablesDynamiques.get(index).getVariableEntete().getDescriptor().getM_AIdentificateurComposant().getCode() == code) {
					variable = listeVariablesDynamiques.get(index).getVariableEntete();
					trouve = true;
				}
				index++;
			}
			
			if(!trouve){
				for (VariableDynamique varD : listeVariablesDynamiques) {
					int nbSousVar = varD.getListeTablesSousVariable() == null ? 0 : varD.getListeTablesSousVariable().size();
					for (int i = 0; i < nbSousVar; i++) {
						TableSousVariable tsv = (TableSousVariable) varD.getListeTablesSousVariable().get(i);
						int nbSousVars = tsv.getNbSousVariables();
					
						for (int j = 0; j < nbSousVars; j++) {
							AVariableComposant var = tsv.getM_AVariableComposant().get(j);
						
							if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPOSEE || var.getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
								int nbChildren = ((com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe) var).getVariableCount();
								AVariableComposant var2;
							
								for (int k = 0; k < nbChildren; k++) {
								
									var2 = ((com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe) var).getEnfant(k);
								
									if (descrVar == var2.getDescriptor()) {
										variable = var2;
										trouve = true;
										break;
									}
								}
							}
							else {
								if (var.getDescriptor().getM_AIdentificateurComposant().getCode() == code) {
									variable = var;
									trouve = true;
								}
							}
						}
					}
				}
			}

			if (listeVariablesComposite.size() > 0 && !trouve) {
				if (listeVariablesComposite != null && listeVariablesComposite.size() > 0) {
					int size = 0;
					for (AVariableComposant v : listeVariablesComposite) {
						VariableComposite varC = (VariableComposite) v;
						size = varC.getVariableCount();
						for (int j = 0; j < size; j++) {
							AVariableComposant var = varC.getEnfant(j);
							if (var.getDescriptor().getM_AIdentificateurComposant().getNom().equals(nom)) {
								variable = var;
								break;
							}
						}
						if (variable != null) {
							break;
						}
					}
				}
			}
		}
		return variable;
	}

	/**
	 * @Author Olivier Graton Modifie la valeur d'une variable si elle existe
	 *         dans le message Sinon ajoute la variable dans le message
	 * @param var
	 */
	public void modifierVariable(AVariableComposant var) {
		AVariableComposant searchedVar = getVariable(var.getDescriptor()
				.getM_AIdentificateurComposant().getCode());
		if (searchedVar != null) {
			searchedVar.setValeur((byte[]) var.getValeur());
		} else {
			ajouterVariable(var);
		}
	}

	public void ajouterVariable(AVariableComposant comp) {
		TypeVariable type = comp.getDescriptor().getTypeVariable();
		switch (type) {
		case VAR_ANALOGIC:
			if (listeVariablesAnalogique == null)
				listeVariablesAnalogique = new ArrayList<VariableAnalogique>(1);

			listeVariablesAnalogique.add((VariableAnalogique) comp);
			break;
		case VAR_DISCRETE:
			if (listeVariablesDiscrete == null)
				listeVariablesDiscrete = new ArrayList<VariableDiscrete>(1);

			listeVariablesDiscrete.add((VariableDiscrete) comp);
			break;
		case VAR_VIRTUAL:
			if (listeVariablesVirtuelle == null)
				listeVariablesVirtuelle = new ArrayList<VariableDiscrete>(1);

			listeVariablesVirtuelle.add((VariableDiscrete) comp);
			break;
		case VAR_COMPLEXE:
			if (listeVariablesComplexe == null)
				listeVariablesComplexe = new ArrayList<VariableComplexe>(1);
			listeVariablesComplexe.add((VariableComplexe) comp);
			break;
		case VAR_COMPOSEE:
			if (listeVariablesComposee == null)
				listeVariablesComposee = new ArrayList<VariableComposite>(1);
			listeVariablesComposee.add((VariableComposite) comp);
			break;
		case STRUCTURE_DYNAMIQUE:
			if (listeStructureDynamique == null)
				listeStructureDynamique = new ArrayList<StructureDynamique>(1);
			listeStructureDynamique.add((StructureDynamique) comp);
			break;
		case TABLEAU_DYNAMIQUE:
			if (listeTableauDynamique == null)
				listeTableauDynamique = new ArrayList<TableauDynamique>(1);
			listeTableauDynamique.add((TableauDynamique) comp);
			break;
		case CHAINE_DYNAMIQUE:
			if (listeChaineDynamique == null)
				listeChaineDynamique = new ArrayList<ChaineDynamique>(1);
			listeChaineDynamique.add((ChaineDynamique) comp);
			break;
		}
	}

	public void supprimerVariable(AVariableComposant comp) {
		if (comp.getDescriptor().getTypeVariable() != TypeVariable.VAR_VIRTUAL) {
			int code = comp.getDescriptor().getM_AIdentificateurComposant()
					.getCode();
			int i = 0;
			if (listeVariablesAnalogique != null) {
				while (i < listeVariablesAnalogique.size()) {
					if (listeVariablesAnalogique.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeVariablesAnalogique.remove(i);
					}
					i++;
				}
			}
			i = 0;
			if (listeVariablesDiscrete != null) {
				while (i < listeVariablesDiscrete.size()) {
					if (listeVariablesDiscrete.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeVariablesDiscrete.remove(i);
					}
					i++;
				}
			}
			i = 0;
			if (listeVariablesVirtuelle != null) {
				while (i < listeVariablesVirtuelle.size()) {
					if (listeVariablesVirtuelle.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeVariablesVirtuelle.remove(i);
					}
					i++;
				}
			}
			i = 0;
			if (listeVariablesComplexe != null) {
				while (i < listeVariablesComplexe.size()) {
					if (listeVariablesComplexe.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeVariablesComplexe.remove(i);
					}
					i++;
				}
			}
			i = 0;
			if (listeVariablesComposee != null) {
				while (i < listeVariablesComposee.size()) {
					if (listeVariablesComposee.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeVariablesComposee.remove(i);
					}
					i++;
				}
			}
			i = 0;
			if (listeStructureDynamique != null) {
				while (i < listeStructureDynamique.size()) {
					if (listeStructureDynamique.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeStructureDynamique.remove(i);
					}
					i++;
				}
			}

			i = 0;
			if (listeTableauDynamique != null) {
				while (i < listeTableauDynamique.size()) {
					if (listeTableauDynamique.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeTableauDynamique.remove(i);
					}
					i++;
				}
			}

			i = 0;
			if (listeChaineDynamique != null) {
				while (i < listeChaineDynamique.size()) {
					if (listeChaineDynamique.get(i).getDescriptor()
							.getM_AIdentificateurComposant().getCode() == code) {
						listeChaineDynamique.remove(i);
					}
					i++;
				}
			}
		} else {
			String nom = comp.getDescriptor().getM_AIdentificateurComposant()
					.getNom();
			int i = 0;
			while (i < listeVariablesVirtuelle.size()) {
				if (listeVariablesVirtuelle.get(i).getDescriptor()
						.getM_AIdentificateurComposant().getNom().equals(nom)) {
					listeVariablesVirtuelle.remove(i);
				}
				i++;
			}
		}
	}

	public int getVariablesCountWithoutUnderVariables() {
		int nbVar = 0;
		if (getVariablesAnalogique() != null) {
			nbVar += getVariablesAnalogique().size();
		}
		if (getVariablesDiscrete() != null) {
			nbVar += getVariablesDiscrete().size();
		}
		if (getVariablesComplexe() != null) {
			nbVar += getVariablesComplexe().size();
		}
		if (getStructuresDynamique() != null) {
			nbVar += getStructuresDynamique().size();
		}
		if (getTableauxDynamique() != null) {
			nbVar += getTableauxDynamique().size();
		}
		if (getChainesDynamique() != null) {
			nbVar += getChainesDynamique().size();
		}
		return nbVar;
	}

	public int getVariablesCount() {
		int nbVar = 0;
		if (getVariablesAnalogique() != null) {
			nbVar += getVariablesAnalogique().size();
		}
		if (getVariablesDiscrete() != null) {
			nbVar += getVariablesDiscrete().size();
		}

		if (getVariablesComplexe() != null) {
			Collection<VariableComplexe> listeVarComp = this
					.getVariablesComplexe();
			for (VariableComplexe complexe : listeVarComp) {
				nbVar += complexe.getVariableCount();
			}
		}

		return nbVar;
	}

	public void setAbsoluteTime(long time) {
		absoluteTime = time;
	}

	public long getAbsoluteTime() {
		return absoluteTime;
	}

	public void setAbsoluteDistance(double distance) {
		absoluteDistance = distance;
	}

	public double getAbsoluteDistance() {
		return absoluteDistance;
	}
	
	public void setAccumulatedDistance(double distance) {
		this.accumulatedDistance = distance;
	}
	
	public double getAccumulatedDistance() {
		return this.accumulatedDistance;
	}

	/**
	 * @return the diametreRoue
	 */
	public double getDiametreRoue() {
		return diametreRoue;
	}

	/**
	 * @param diametreRoue
	 *            the diametreRoue to set
	 */
	public void setDiametreRoue(double diametreRoue) {
		this.diametreRoue = diametreRoue;
	}

	/**
	 * @return the diametreCorigee
	 */
	public String getDistanceCorrige() {
		return distanceCorrigee;
	}

	public String getTempsCorrige() {
		return tempsCorrige;
	}

	public void setTempsCorrige(String tempsCorrige) {
		this.tempsCorrige = tempsCorrige;
	}

	/**
	 * @param diametreCorigee
	 *            the diametreCorigee to set
	 */
	public void setDistanceCorrige(String distanceCorrige) {
		distanceCorrigee = distanceCorrige;
	}

	/**
	 * @return the messageData
	 */
	public byte[] getMessageData() {
		return messageData;
	}

	/**
	 * @param messageData
	 *            the messageData to set
	 */
	public void setMessageData(byte[] messageData) {
		this.messageData = messageData;
	}

	/**
	 * @return the isRepere0
	 */
	public boolean isRepereZero() {
		return isRepere0;
	}

	/**
	 * @param isRepere0
	 *            the isRepere0 to set
	 */
	public void setRepereZero(boolean isRepereZero) {
		isRepere0 = isRepereZero;
	}

	/**
	 * @return the isEvenementValid
	 */
	public boolean isEvenementValid() {
		return error == null
				|| (error != null && !error.equals(ErrorType.EventId));
	}

	public void setError(ErrorType error) {
		this.error = error;
	}

	public ErrorType getError() {
		return error;
	}

	public boolean isMessageIncertitude() {
		return isMessageIncertitude;
	}

	public void setMessageIncertitude(boolean isMessageIncertitude) {
		this.isMessageIncertitude = isMessageIncertitude;
	}

	public double getCumulDistance() {
		return cumulDistance;
	}

	public void setCumulDistance(double cumulDistance) {
		this.cumulDistance = cumulDistance;
	}

	public boolean isEvNonDate() {
		return evNonDate;
	}

	public void setEvNonDate(boolean evNonDate) {
		this.evNonDate = evNonDate;
	}

	public List<ChaineDynamique> getChainesDynamique() {
		if (listeChaineDynamique == null) {
			return new ArrayList<ChaineDynamique>(0);
		}
		return listeChaineDynamique;
	}

	public List<StructureDynamique> getStructuresDynamique() {
		if (listeStructureDynamique == null) {
			return new ArrayList<StructureDynamique>(0);
		}
		return listeStructureDynamique;
	}

	public List<TableauDynamique> getTableauxDynamique() {
		if (listeTableauDynamique == null) {
			return new ArrayList<TableauDynamique>(0);
		}
		return listeTableauDynamique;
	}

	public int getOffsetDebut() {
		return offsetDebut;
	}

	public void setOffsetDebut(int offsetDebut) {
		this.offsetDebut = offsetDebut;
	}

	public int getOffsetFin() {
		return offsetFin;
	}

	public void setOffsetFin(int offsetFin) {
		this.offsetFin = offsetFin;
	}
	
	public void deepTrimToSize() {
		if (listeVariablesAnalogique != null) {
			listeVariablesAnalogique.trimToSize();
		}

		if (listeVariablesDiscrete != null) {
			listeVariablesDiscrete.trimToSize();
		}

		if (listeVariablesVirtuelle != null) {
			listeVariablesVirtuelle.trimToSize();
		}

		if (listeVariablesComplexe != null) {
			listeVariablesComplexe.trimToSize();
		}

		if (listeVariablesComposee != null) {
			listeVariablesComposee.trimToSize();
		}

		if (listeStructureDynamique != null) {
			listeStructureDynamique.trimToSize();
		}

		if (listeTableauDynamique != null) {
			listeTableauDynamique.trimToSize();
		}

		if (this.listeChaineDynamique != null) {
			listeChaineDynamique.trimToSize();
		}
	}
	
	public String getErrorInfo() {
		return errorInfo;
	}
	
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}

	public boolean isLengthValid() {
		return error == null
				|| (error != null && !error.equals(ErrorType.BadLength));
	}

	public boolean isBlockValid() {
		return error == null
				|| (error != null && !error.equals(ErrorType.BadBlock));
	}
	
	public boolean isErrorMessage() {
		return !isCRCValid() || !isLengthValid() || !isBlockValid() || !isEvenementValid();
	}
}