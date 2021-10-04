package com.faiveley.samng.principal.sm.search;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.faiveley.samng.principal.ihm.vues.AVueTable;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.search.ASearchVariableDialog;
import com.faiveley.samng.principal.ihm.vues.search.Operation;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

public class SearchInFileUtil {

	private boolean searchChange=false;

	public boolean checkForVariable(SearchData data) {
		boolean found = false;
		if (data != null &&data.getDescriptorVariable()!=null) {
			try {
				String tempsAbs=com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2");
				if (data.getDescriptorVariable().getM_AIdentificateurComposant().getNom().equals(tempsAbs)) {
					//temps absolu
					Collection<Message> collMsg = FabriqueParcours.getInstance().getParcours().getData().getEnregistrement().getMessages();
					List<Message> messages = new ArrayList<Message>(collMsg);
					String[] msgErreur=new String[2];
					for (Message msg : messages) {
						String val = data.getValue();
						String s=ConversionTemps.getFormatFromResolutionTemps();

						try {
							Date d1 = new SimpleDateFormat(s).parse(val);
							//date complete
							if (AVueTable.checkSearchedVariableDate(msg.getAbsoluteTime()-d1.getTime(),data.getOperation(),msgErreur)) {
								found = true;
								break;
							}
						} catch (ParseException e) {
							//date partielle
							if (AbstractSelectionProviderVue.checkSearchedTempsAbsoluPartielle(val,data.getOperation(),msg.getAbsoluteTime(),msgErreur)) {
								found = true;
								break;
							}
						}
					}
				
				}else if ((GestionnaireDescripteurs.containsDescriptorVariable(
						data.getDescriptorVariable().getM_AIdentificateurComposant().getCode()) == null)
						&& (GestionnaireDescripteurs.containsDescriptorVariableComposee(
								data.getDescriptorVariable().getM_AIdentificateurComposant().getNom()) == null) ){
					found = false;
				} else {
					Collection<Message> collMsg = FabriqueParcours.getInstance().getParcours().getData().getEnregistrement().getMessages();
					List<Message> messages = new ArrayList<Message>(collMsg);
					Message msgDepart=messages.get(0);

					for (int i = 0; i < messages.size(); i++) {
						Message msg = messages.get(i);
						AVariableComposant var = msg.getVariable(data.getDescriptorVariable());
						if (data.getOperation()!=Operation.NoOperation && var != null) {
							if(data.getValue()!=null){
								if (checkSearchedVariable(var, data.getValue(), data.getOperation(),msgDepart)) {
									found = true;
									break;
								}
							}
							else {
								found = true;
								break;
							}
						}
					}
				}
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return found;
	}

	private boolean checkSearchedVariable (AVariableComposant var, String value, Operation op, Message msg) {

		boolean shouldSelect = false;
		switch (op) {
		case Equal:
		{
			int compare = var.compareValueWithStringValue(value);
			shouldSelect = compare == 0;

			break;
		}
		case Greater:
		{
			int compare = var.compareValueWithStringValue(value);
			shouldSelect = compare > 0; 
			break;
		}
		case GreaterOrEqual:
		{
			int compare = var.compareValueWithStringValue(value);
			shouldSelect = compare > 0 || compare == 0;
			break;
		}
		case Less:
		{
			int compare = var.compareValueWithStringValue(value);

			shouldSelect = compare < 0;
			break;
		}
		case LessOrEqual:
		{
			int compare = var.compareValueWithStringValue(value);
			shouldSelect = compare > 0 || compare == 0; 
			break;
		}
		case NotEqual:
		{
			int compare = var.compareValueWithStringValue(value);
			shouldSelect = compare != 0; 
			break;
		}
		case ShiftLeft:
		case Change: {
			shouldSelect=false;// valeur à retourner
			String valeur=var==null ? "" : var.toString();// valeur de la variable		

			if (value.equals(ASearchVariableDialog.TOUSLESCHANGEMENTS)) {
				//on cherche tous les changements
				String firstVal=msg.getVariable(var.getDescriptor()).toString();
				if (!var.toString().equals(firstVal)) {
					this.setSearchChange(false);
					shouldSelect=true;
				}
			}else if (value.contains(ASearchVariableDialog.DE)&&
					value.contains(ASearchVariableDialog.VERSAUTREVALEUR)) {
				//on cherche un changement d'une valeur vers n'importe quelle autre valeur

				int indiceValeur1=ASearchVariableDialog.DE.length();
				int indiceValeur2=value.indexOf(ASearchVariableDialog.VERSAUTREVALEUR);
				String val=value.substring(indiceValeur1+2, indiceValeur2-2);

				if(!this.isSearchChange()){
					//on cherche la 1ère valeur 
					if (val.equals(valeur)) {
						this.setSearchChange(true);
					}
				}else{
					//on cherche la 2ème valeur
					if (!val.equals(valeur)) {
						this.setSearchChange(false);
						shouldSelect=true;
					}
				}
			}else{
				//on cherche un changement en particulier
				int indice2=value.indexOf(ASearchVariableDialog.VERS);

				String valeur1=value.substring(ASearchVariableDialog.DE.length()+1, indice2-1);
				String valeur2=value.substring(indice2+1 + ASearchVariableDialog.VERS.length(), value.length());

				if(!this.isSearchChange()){
					//on cherche la 1ère valeur 
					if (valeur.equals(valeur1)) {
						this.setSearchChange(true);
					}
				}else{
					//on cherche la 2ème valeur
					if (valeur.equals(valeur2)) {
						this.setSearchChange(false);
						shouldSelect=true;
					}
				}
				break;
			}
		}
		default:
			break;
		}		
		return shouldSelect;
	}

	public void loadXml(String fileName) {
		try {
			GestionnairePool.getInstance().chargerFichierXml(fileName,"");
		} catch (ParseurXMLException e) {
			SamngLogger.getLogger().error("searchInFile.1" + " " + fileName, e);
		}
	}

	public boolean isSearchChange() {
		return searchChange;
	}

	public void setSearchChange(boolean searchChange) {
		this.searchChange = searchChange;
	}
}
