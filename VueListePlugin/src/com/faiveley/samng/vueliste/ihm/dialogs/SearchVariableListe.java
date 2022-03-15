package com.faiveley.samng.vueliste.ihm.dialogs;

import java.util.LinkedHashMap;

import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.search.SearchVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.VueListe;

@Deprecated
public class SearchVariableListe extends SearchVariable{

	public SearchVariableListe(Shell parent) {
		super(parent);
	} 
	
	public SearchVariableListe(Shell parent,int style) {
		super(parent,style);
	}

	/**
	 * Fills the combobox of the variables names
	 * 
	 */
	protected void fillCombo() {
		VueListe.varSelectionnee.reset();
		this.comboVar.add(NO_VARIABLE);
		this.comboVar.add(ADV_SEARCH);

		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		if (p == null) {
			return;
		}
		Enregistrement enrg = p.getData().getEnregistrement();
		if (enrg != null) {
			if (this.values == null) {
				this.values = new LinkedHashMap<String, DescripteurVariable>();
			} else {
				this.values.clear();
			}
			
			//ajout de la variable Temps Absolu DR26-3-a CL01
			String str=com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2");
			DescripteurVariable descr=new DescripteurVariable();
			AIdentificateurComposant comp=new AIdentificateurComposant() {};
			descr.setTypeVariable(TypeVariable.VAR_ANALOGIC);
			descr.setM_AIdentificateurComposant(comp);
			comp.setCode(TypeRepere.tempsAbsolu.getCode());
			comp.setNom(str);
			if (!this.values.containsKey(str)) {
				this.comboVar.add(str);
				this.values.put(str, descr);
			}
			///////////////////////////////////
			
			for (Message msg : enrg.getMessages()) {
				if (msg.getVariablesAnalogique() != null) {									
					addVariables(msg.getVariablesAnalogique());
				}
				if (msg.getVariablesDiscrete() != null) {
					addVariables(msg.getVariablesDiscrete());
				}
				if (msg.getVariablesComplexe() != null) {
					addVariablesComposee(msg.getVariablesComplexe());
				}
				if (msg.getVariablesComposee() != null) {
					addVariables(msg.getVariablesComposee());
				}
			}
			addVariablesVolatilesVueListe(GestionnairePool.getInstance().getVariablesDynamiquesMap());
			
			// Suppression de la variable Vitesse_limite_KVB : elle est virtuelle : attach�e � aucun �v�nement
			this.values.remove(VitesseLimiteKVBService.getInstance().getTableLangueNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
		}
	}
}
