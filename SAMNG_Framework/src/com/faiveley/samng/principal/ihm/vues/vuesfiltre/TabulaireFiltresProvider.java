package com.faiveley.samng.principal.ihm.vues.vuesfiltre;

import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.GestionnaireFiltresTabulaire;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class TabulaireFiltresProvider extends AbstractProviderFiltre {

	public TabulaireFiltresProvider() {
		super(new GestionnaireFiltresTabulaire(), TypeFiltre.tabulaire);
	}

	@Override
	public void onDataChange() {
		((GestionnaireFiltresTabulaire) filtersMng).filtreTabulaireParseur
				.parseRessource(RepertoiresAdresses.getFiltres_TabulairesXML(),false,0,-1);
		((AGestionnaireFiltres) filtersMng).listeFiltres = ((GestionnaireFiltresTabulaire) filtersMng).filtreTabulaireParseur
				.chargerFiltres();
		try {
			((AGestionnaireFiltres) filtersMng)
					.setFiltreDefault((FiltreComposite) ((AGestionnaireFiltres) filtersMng)
							.initialiserFiltreDefaut());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		super.onDataChange();
	}

	@Override
	public boolean isVitesseCorrigeeFiltrePerso(
			AFiltreComposant filtreApplique,
			LigneVariableFiltreComposite ligneCourante) {
		boolean trouve = false;
		for (int i = 0; i < filtreApplique.getEnfantCount(); i++) {
			ligneCourante = (LigneVariableFiltreComposite) filtreApplique
					.getEnfant(i);
			if (ligneCourante.getNom().equals(
					TypeRepere.vitesseCorrigee.getName())) {
				trouve = true;
			}
		}
		return trouve;
	}

	@Override
	protected void initNewFilter(AFiltreComposant newFiltre) {
		// Nothing to do here ...
	}

	@Override
	public boolean filtrevalide(AFiltreComposant filtre) {

		// si filtre null, on teste le filtre appliqué
		AFiltreComposant filtrecourant = filtersMng.getFiltreCourant();

		boolean isFiltreValide = true;

		if (filtre != null) {
			filtrecourant = filtre;
		}

		if (filtrecourant == null) {
			return true;
		}

		if (filtrecourant.getEnfantCount() == 0) {
			return false;
		}

		// Map<Integer,AVariableComposant> fpVariables = null;
		// fpVariables = GestionnairePool.getAllVariables();
		// VbvsProvider providerVbv = ActivatorData.getInstance().getProviderVBVs();

		List<AVariableComposant> fpVariables = Util
				.getInstance().getAllVariablesIncludeSubvars();

		if (filtrecourant != null && fpVariables != null) {
			int nbVar = filtrecourant.getEnfantCount();
			for (int j = 0; j < nbVar; j++) {
				String nameVar = filtrecourant.getEnfant(j).getNom();
				boolean varValide = false;
				if (nameVar != null) {
					// if
					// (GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null
					// &&
					// (!GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom().equals(nameVar)))
					// {
					// isFiltreValide=false;
					// return isFiltreValide;
					// }
					// if (GestionnaireDescripteurs
					// .getDescripteurVariableComposee(nameVar
					// .replace("[C]", "")) != null) {
					// varValide = true;
					// continue;
					// }
					if (nameVar.equals("vitesse_corrigee")
							|| nameVar.equals("distance_corrigee")|| VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(nameVar) ) {
						varValide = true;
						continue;
					}

					if (ActivatorData.getInstance().getProviderVBVs()
							.getGestionnaireVbvs().getVBV(nameVar) != null) {
						if (ActivatorData.getInstance().getProviderVBVs()
								.verifierValiditeVBV(
										ActivatorData.getInstance().getProviderVBVs()
												.getGestionnaireVbvs()
												.getVBV(nameVar)) == null) {
							varValide = true;
							continue;
						}
					} else {
						for (AVariableComposant var : fpVariables) {
							
								if ((var.getDescriptor()
										.getNomUtilisateur()
										.getNomUtilisateur(
												Activator.getDefault()
														.getCurrentLanguage()) != null && 
														var
										.getDescriptor()
										.getNomUtilisateur()
										.getNomUtilisateur(
												Activator.getDefault()
														.getCurrentLanguage())
										.equals(nameVar))
										|| (var.getDescriptor()
												.getM_AIdentificateurComposant()
												.getNom() !=null && var.getDescriptor()
												.getM_AIdentificateurComposant()
												.getNom().equals(nameVar))) {
									varValide = true;
									continue;
								}

						}
						if (!varValide && Util.getInstance().isVariableInXml(nameVar, true)) {
							varValide = true;
						}

					}
				}
				// if (!varValide) {
				// AVariableComposant varComp = GestionnairePool
				// .getComposeeVariables().get(nameVar);
				// if (varComp != null) {
				// boolean varComposeeValide = GestionnaireVariablesComposee
				// .isVariableComposeeRenseignee(varComp);
				// varValide = varComposeeValide;
				// break;
				// }
				// }
				if (!varValide) {
					return false;
				}
			}

		}
		return isFiltreValide;
	}

	@Override
	public boolean verifierValiditeFiltre(AFiltreComposant filtre) {
		return filtrevalide(filtre);
		// boolean isValide = true;
		// // Set<AVariableComposant> fpVariables =
		// GestionnairePool.getVariablesRenseignees();DR28_CL39
		// Map<Integer, AVariableComposant> fpVariables =
		// GestionnairePool.getAllVariables();
		//
		// if(filtre!=null && filtre.getEnfantCount()>0 && fpVariables!=null){
		//
		// LigneVariableFiltreComposite ligneVar =null;
		// List<String> listeNomsVars = new ArrayList<String> ();
		//
		// Iterator<Integer> it=fpVariables.keySet().iterator();
		// while (it.hasNext()) {
		// // for (AVariableComposant composant : fpVariables) {
		// Integer integer = (Integer) it.next();
		// AVariableComposant composant=fpVariables.get(integer);
		// if(composant.getDescriptor().getTypeVariable() ==
		// TypeVariable.VAR_COMPLEXE)
		// {
		// VariableComposite varComposite = (VariableComposite)composant;
		//
		// for (int k =0; k<varComposite.getVariableCount();k++){
		// listeNomsVars.add(varComposite.getEnfant(k).getDescriptor().getM_AIdentificateurComposant().getNom());
		// }
		// }else
		// listeNomsVars.add(composant.getDescriptor().getM_AIdentificateurComposant().getNom());
		// }
		//
		// VbvsProvider providerVbv=ActivatorData.getInstance().getProviderVBVs();
		// List <VariableVirtuelle>
		// listVbvs=providerVbv.getGestionnaireVbvs().getListeVBV();
		//
		// if
		// (GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null)
		// {
		// listeNomsVars.add(GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor()
		// .getM_AIdentificateurComposant().getNom());
		// }
		//
		// for (int i = 0; i < filtre.getEnfantCount(); i++) {
		// ligneVar = (LigneVariableFiltreComposite)(filtre.getEnfant(i));
		// AVariableComposant var =
		// GestionnairePool.getVariable(ligneVar.getNom());
		//
		// if (var!=null) {
		// if(!listeNomsVars.contains(var.getDescriptor().getM_AIdentificateurComposant().getNom())){
		// if(var.getDescriptor().getTypeVariable()==TypeVariable.VAR_COMPOSEE){
		// AVariableComposant varComp =
		// GestionnairePool.getComposeeVariables().get(ligneVar.getNom());
		// if
		// (!GestionnaireVariablesComposee.isVariableComposeeRenseignee(varComp))
		// {
		// return false;
		// }
		// }
		// }
		// }else{
		// boolean find=false;
		// for (VariableVirtuelle vbv : listVbvs) {
		// if
		// (vbv.getDescriptor().getM_AIdentificateurComposant().getNom().equals(ligneVar.getNom()))
		// {
		// find=true;
		// if (providerVbv.verifierValiditeVBV(vbv)!=null) {
		// return false;
		// }
		// }
		// }
		// if (!find) {
		// return false;
		// }
		// }
		// }
		// }
		// return isValide;
	}
}
