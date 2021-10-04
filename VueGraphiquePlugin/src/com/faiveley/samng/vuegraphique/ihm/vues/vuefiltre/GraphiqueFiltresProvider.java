package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre;

import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class GraphiqueFiltresProvider extends AbstractProviderFiltre {

	public GraphiqueFiltresProvider() {
		super(new GestionnaireFiltresGraphique(), TypeFiltre.graphique);
	}

	@Override
	public boolean isVitesseCorrigeeFiltrePerso(
			AFiltreComposant filtreApplique,
			LigneVariableFiltreComposite ligneCourante) {
		boolean trouve = false;
		GraphiqueFiltreComposite grapheCourant;
		for (int i = 0; i < filtreApplique.getEnfantCount(); i++) {
			grapheCourant = (GraphiqueFiltreComposite) filtreApplique
					.getEnfant(i);
			grapheCourant.setParent((FiltreComposite) filtreApplique);
			for (int j = 0; j < grapheCourant.getEnfantCount(); j++) {
				ligneCourante = (LigneVariableFiltreComposite) grapheCourant
						.getEnfant(j);
				if (ligneCourante.getNom().equals(
						TypeRepere.vitesseCorrigee.getName())) {
					trouve = true;
				}
			}
		}
		return trouve;
	}

	@Override
	protected void initNewFilter(AFiltreComposant newFiltre) {
		// Nothing to do here ...
	}

	@Override
	public void onDataChange() {
		// File file = new File(RepertoiresAdresses.getFiltres_GraphiquesXML());
		((GestionnaireFiltresGraphique) filtersMng).filtreGraphiqueParseur
				.parseRessource(RepertoiresAdresses.getFiltres_GraphiquesXML(),false,0,-1);
		((GestionnaireFiltresGraphique) filtersMng).listeFiltres = ((GestionnaireFiltresGraphique) filtersMng).filtreGraphiqueParseur
				.chargerFiltres();
		((GestionnaireFiltresGraphique) filtersMng).initialiserFiltreDefaut();
		super.onDataChange();
	}

	@Override
	public boolean filtrevalide(AFiltreComposant filtre) {
		// si filtre null, on teste le filtre appliqué
		boolean isFiltreValide = true;
		AFiltreComposant filtrecourant = filtersMng.getFiltreCourant();

		if (filtre != null) {
			filtrecourant = filtre;
		}
		
		if (filtrecourant==null) {
			return true;
		}

		if (filtrecourant != null && filtrecourant.getEnfantCount() > 0) {
			int nbGraphes = filtrecourant.getEnfantCount();
			for (int i = 0; i < nbGraphes; i++) {
				int nbVar = filtrecourant.getEnfant(i).getEnfantCount();
				for (int j = 0; j < nbVar; j++) {
					boolean varValide = false;
					String nameVar = filtrecourant.getEnfant(i).getEnfant(j)
							.getNom();
//					if (GestionnaireDescripteurs
//							.getDescripteurVariableComposee(nameVar.replace("[C]",
//									"")) != null) {
//						varValide = true;
//						continue;
//					}
					if (nameVar.equals("vitesse_corrigee")
							|| nameVar.equals("distance_corrigee") || VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(nameVar)) {
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
					}
					// else if
					// (GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null
					// && (!GestionnairePool.getVariable
					// (TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom().equals(nameVar)))
					// {
					// isFiltreValide=false; return isFiltreValide;
					// }

					else {
						List<AVariableComposant> fpVariables = Util.getInstance().getAllVariablesIncludeSubvars();

						// Map<Integer,AVariableComposant> fpVariables = null;
						// fpVariables = GestionnairePool.getAllVariables();
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
					}

					if (!varValide) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return isFiltreValide;
	}

	@Override
	public boolean verifierValiditeFiltre(AFiltreComposant var) {
		return filtrevalide(var);
	}

	// @Override
	// public boolean verifierValiditeFiltre(AFiltreComposant filtre) {
	// boolean isValide = true;
	// if(filtre!=null && filtre.getEnfantCount()>0){
	//
	// // Set<AVariableComposant> fpVariables =
	// GestionnairePool.getVariablesRenseignees();DR28_CL39
	// Map<Integer, AVariableComposant> fpVariables =
	// GestionnairePool.getAllVariables();
	//
	//
	// LigneVariableFiltreComposite ligneVar =null;
	// GraphiqueFiltreComposite graphe;
	// List<String> listeNomsVars = new ArrayList<String> ();
	// if (fpVariables!=null){
	// Iterator<Integer> it=fpVariables.keySet().iterator();
	// while (it.hasNext()) {
	// // for (AVariableComposant composant : fpVariables) {
	// Integer integer = (Integer) it.next();
	// AVariableComposant composant=fpVariables.get(integer);
	//
	// if(composant.getDescriptor().getTypeVariable() ==
	// TypeVariable.VAR_COMPLEXE)
	// {
	// VariableComposite varComposite = (VariableComposite)composant;
	// for (int k =0; k<varComposite.getVariableCount();k++){
	// listeNomsVars.add(varComposite.getEnfant(k).getDescriptor().getM_AIdentificateurComposant().getNom());
	// }
	// }
	// else
	// listeNomsVars.add(composant.getDescriptor().getM_AIdentificateurComposant().getNom());
	// }
	// try {
	// if
	// (GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null)
	// {
	// listeNomsVars.add(GestionnairePool.getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor()
	// .getM_AIdentificateurComposant().getNom());
	// }
	// } catch (Exception e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// }
	//
	// for(int j=0; j<filtre.getEnfantCount(); j++) {
	// graphe = (GraphiqueFiltreComposite)filtre.getEnfant(j);
	// graphe.setParent((FiltreComposite)filtre);
	// for (int i = 0; i < graphe.getEnfantCount(); i++) {
	// ligneVar = (LigneVariableFiltreComposite)(graphe.getEnfant(i));
	// AVariableComposant var = GestionnairePool.getVariable(ligneVar.getNom());
	// if (var!=null) {
	// if(!listeNomsVars.contains(var.getDescriptor().getM_AIdentificateurComposant().getNom())){
	// // isValide =true;
	// // break;
	// return false;
	// }
	// }else{
	// VariableVirtuelle varVit =
	// ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(ligneVar.getNom());
	//
	// if(varVit!=null){
	// if(ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(varVit)==null){
	// isValide =false;
	// break;
	// }
	// }else{
	// return false;
	// }
	// }
	// }
	// }
	// }
	// return isValide;
	// }

}
