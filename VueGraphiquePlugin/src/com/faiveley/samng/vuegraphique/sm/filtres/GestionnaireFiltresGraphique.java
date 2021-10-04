package com.faiveley.samng.vuegraphique.sm.filtres;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.RGB;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.GestionnaireCouleurs;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.Messages;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Reperes;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.GraphicConstants;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.OrdonnerFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeGraphique;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.filtres.variables.CouleurLigneVariable;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuegraphique.sm.parseurs.ParseurFiltreGraphique;


/**
 * @author Cosmin Udroiu
 * @version 1.0
 * @created 23-nov.-2007 15:24:36
 */
public class GestionnaireFiltresGraphique extends AGestionnaireFiltres {

	public ParseurFiltreGraphique filtreGraphiqueParseur = new ParseurFiltreGraphique();

	public GestionnaireFiltresGraphique() {
		filtreGraphiqueParseur.parseRessource(RepertoiresAdresses.getFiltres_GraphiquesXML(),false,0,-1);
		this.listeFiltres = filtreGraphiqueParseur.chargerFiltres();
		initialiserFiltreDefaut();
	}

	/**
	 * 
	 * @param filtre
	 */
	public void ajouterFiltre(AFiltreComposant filtre){
		super.ajouterFiltre(filtre);
		this.filtreGraphiqueParseur.enregistrerFiltre(filtre);
	}

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant supprimerFiltre(int indice) {
		AFiltreComposant filtre = getFiltre(indice);
		super.supprimerFiltre(indice);
		filtreGraphiqueParseur.effacerFiltre(filtre);				
		return filtre;
	}

	public AFiltreComposant supprimerFiltre(String nom){
		AFiltreComposant filtre = super.supprimerFiltre(nom);
		this.filtreGraphiqueParseur.effacerFiltre(filtre);
		OrdonnerFiltre.getInstance().getListeFiltreGraphique().remove(filtre) ;
		return filtre;
	}

	public Set<String> getCurFiltreNomsVars() {
		Set<String> retSet = new HashSet<String>();
		if(filtreCourant != null) {
			AFiltreComposant varFiltres;
			try {
				varFiltres = filtreCourant.getEnfant(1);
			} catch (RuntimeException e) {
				varFiltres = filtreCourant.getEnfant(0);
			}
			int varFiltresCount = varFiltres.getEnfantCount();
			for(int i = 0; i<varFiltresCount; i++) {
				retSet.add(varFiltres.getEnfant(i).getNom());
			}
		}
		return retSet;
	}

	public AFiltreComposant getFiltreDefaut() {
		return this.filtreDefault;
	}
	
	/**
	 * Duplique un filtre graphique en filtre tabulaire
	 * @param filtreGraphique
	 * @return
	 */
	public static List<AFiltreComposant> dupliquerFiltresGraphiquesFiltresTabulaires(List<AFiltreComposant> listeFiltresGraph){
		List<AFiltreComposant> listeFiltretab = new ArrayList<AFiltreComposant>();

		for (AFiltreComposant filtreGraph : listeFiltresGraph) {
			AFiltreComposant filtreTab = new FiltreComposite();
			filtreTab.setNom(filtreGraph.getNom() + Messages.getString("AbstractProviderFiltre.13") + " " + filtreGraph.getNom()); //$NON-NLS-1$
			//filtreTab.setNom(filtreGraph.getNom());
			filtreTab.setFiltreType(TypeFiltre.tabulaire);
			GraphiqueFiltreComposite grapheCourant;
			LigneVariableFiltreComposite ligneCourante;
			LigneVariableFiltreComposite ligneFiltreTabulaire;

			for(int i=0; i< filtreGraph.getEnfantCount();i++){
				grapheCourant = (GraphiqueFiltreComposite)filtreGraph.getEnfant(i);
				for(int j=0; j<grapheCourant.getEnfantCount();j++){
					ligneCourante = (LigneVariableFiltreComposite)grapheCourant.getEnfant(j);
					ligneFiltreTabulaire = new LigneVariableFiltreComposite();
					ligneFiltreTabulaire.setNom(ligneCourante.getNom());
					filtreTab.ajouter(ligneFiltreTabulaire);
				}
			}
			listeFiltretab.add(filtreTab);
		}

		return listeFiltretab;
	}


	/**
	 * Retourne un filtre à partir de l'analyse des variables présentes et valorisées dans le fichier binaire
	 * @returnun objet AFiltre composant de type graphique
	 */
	public AFiltreComposant initialiserFiltreDefaut() {
		int anaVarsCnt = 0;			//First analogic variables
		int digitalVarsCnt = 0;		//first digital variables
		boolean varVitessePresente=false;
		int codeVit=0;
		boolean varVitesseCorrigeePresente=false;
		int codeVitCor=0;
		GraphiqueFiltreComposite filter = null;
		CouleurLigneVariable newColor;
		AFiltreComposant newLineVar;
		Set<AVariableComposant> fpVariables = null;
		fpVariables=GestionnairePool.getInstance().getVariablesRenseignees();
		Map<Integer, AVariableComposant> allVariables = GestionnairePool.getInstance().getAllVariables();
		AVariableComposant vitesse=null;
		AVariableComposant vitesseCorrigee=null;

		if (fpVariables==null) {
			return null;
		}

		Iterator<AVariableComposant> p=null;
		try {
			p = fpVariables.iterator();
		} catch (RuntimeException e1) {
			// TODO Auto-generated catch block
			return null;
		}
		if (p!=null) {
			while (p.hasNext()) {
				//				for(AVariableComposant var: allVars.values()) {
				AVariableComposant var=(AVariableComposant)p.next();
				DescripteurVariable descrVar =var.getDescriptor();

				if (descrVar.getM_AIdentificateurComposant().getCode()== TypeRepere.vitesse.getCode()) {
					varVitessePresente=true;
					codeVit=var.getDescriptor().getM_AIdentificateurComposant().getCode();
					vitesse=allVariables.get(codeVit);
					try {
						if ((Boolean)ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")) {		
							varVitesseCorrigeePresente=true;			
							codeVitCor=TypeRepere.vitesseCorrigee.getCode();
							vitesseCorrigee=allVariables.get(codeVitCor);
						}
					} catch (Exception e) {
						// : handle exception
					}		
				}
			}
		}
		//we subtract one as the first graphic has only 3 variables
		//AVariableComposant[] anaVars = new AVariableComposant[GraphicConstants.MAX_GRAPHICS_COUNT * GraphicConstants.MAX_ANALOG_GRAPHIC_VARIABLES - 1];
		//		AVariableComposant[] anaVars = new AVariableComposant[GraphicConstants.MAX_GRAPHICS_COUNT * GraphicConstants.MAX_ANALOG_VARIABLES];
		AVariableComposant[] digitalVars = new AVariableComposant[GraphicConstants.MAX_GRAPHICS_COUNT * GraphicConstants.MAX_DISCRETE_VARIABLES];

		DescripteurVariable descrVar;
		Reperes reperes = GestionnairePool.getInstance().getReperes();
		Type type;
		boolean varUsed=false;

		//		for(AVariableComposant var: allVars.values()) {
		Iterator<AVariableComposant> k=fpVariables.iterator();
		while (k.hasNext()) {

			AVariableComposant var=(AVariableComposant)k.next();
			try {
				descrVar = var.getDescriptor();
				type = var.getTypeValeur();
				String varName=descrVar.getM_AIdentificateurComposant().getNom();

				//				if (anaVarsCnt==3 && varVitessePresente && !varVitesseCorrigeePresente && anaVarsCnt < anaVars.length) {
				//				anaVars[anaVarsCnt++] = vitesse;

				//				}

				//				if (anaVarsCnt==2 && varVitessePresente && varVitesseCorrigeePresente && anaVarsCnt < anaVars.length) {
				//				anaVars[anaVarsCnt++] = vitesse;
				//				anaVars[anaVarsCnt++] = vitesseCorrigee;				
				//				}


				//check if is a analogic variable and if is not a repere and is renseigne
				//				Set<AVariableComposant> fpVariables = null;
				//				fpVariables = Util.getMessagesVariables();

				//				if(descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC &&
				//						reperes.getRepere(varName) == null) {
				//
				//					if(anaVarsCnt < anaVars.length) {					
				//						for (AVariableComposant vars : fpVariables) {
				//							if (vars.getDescriptor().getM_AIdentificateurComposant().getNom().equals(varName)) {
				//								varUsed=false;
				//								for (int j = 0; j < anaVarsCnt; j++) {
				//									if (anaVars[j].getDescriptor().getM_AIdentificateurComposant().getNom()
				//											.equals(varName)) {
				//										varUsed=true;
				//									}										
				//								}
				//								if(!varUsed)
				//									anaVars[anaVarsCnt++] = var;
				//								break;
				//							}
				//						}
				//					}
				//
				//				} else 
				if(descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE && (type == Type.boolean1 ||
						type == Type.boolean8) && 
						reperes.getRepere(descrVar.getM_AIdentificateurComposant().getNom()) == null) {
					if(digitalVarsCnt < digitalVars.length) {
						for (AVariableComposant vars : fpVariables) {
							if (vars.getDescriptor().getM_AIdentificateurComposant().getNom().equals(varName)) {
								varUsed=false;
								for (int j = 0; j < digitalVarsCnt; j++) {
									if (digitalVars[j].getDescriptor().getM_AIdentificateurComposant().getNom()
											.equals(varName)) {
										varUsed=true;
									}										
								}
								if (!varUsed)
									digitalVars[digitalVarsCnt++] = var;
								break;
							}
						}				
					}
				} else if (descrVar.getTypeVariable() == TypeVariable.VAR_COMPLEXE 
						&& reperes.getRepere(descrVar.getM_AIdentificateurComposant().getNom()) == null){
					VariableComposite v = (VariableComposite) var;
					int nbChild=v.getVariableCount();

					for (AVariableComposant vars : fpVariables){ 
						if (vars.getDescriptor().getM_AIdentificateurComposant().getNom().equals(varName)) {

							for (int i = 0; i < nbChild; i++) {
								DescripteurVariable dVar=v.getEnfant(i).getDescriptor();
								type = dVar.getType();
								if (dVar.getTypeVariable()==TypeVariable.VAR_DISCRETE
										&& (type == Type.boolean1 ||type == Type.boolean8
										&& dVar.getType()!=Type.reserved)
										&& digitalVarsCnt < digitalVars.length) {

									varUsed=false;
									for (int j = 0; j < digitalVarsCnt; j++) {
										if (digitalVars[j].getDescriptor().getM_AIdentificateurComposant().getNom()
												.equals(dVar.getM_AIdentificateurComposant().getNom())) {
											varUsed=true;
										}										
									}
									if (!varUsed){
										digitalVars[digitalVarsCnt++] = v.getEnfant(i);
									}										
								}								
							}
						}
					}
				}
				//				}

			} catch (Exception e) {
				int t=0;
			}
			//check if the arrays are completelly filled
			//			if(anaVarsCnt == anaVars.length && digitalVarsCnt == digitalVars.length)
			if(digitalVarsCnt == digitalVars.length)
				break;
		}

		//		if (anaVarsCnt<3 && varVitessePresente) {
		//		anaVars[anaVarsCnt++] = vitesse;

		//		if (varVitesseCorrigeePresente) {
		//		anaVars[anaVarsCnt++] = vitesseCorrigee;
		//		}
		//		}


		//		if (varVitessePresente) {
		//			int decalage=0;
		//			boolean addVariable=false;
		//			if (varVitesseCorrigeePresente) {
		//				//recherche de l'indice d'insertion de la vitesse et vitesse corrigée
		//				int lastVarAna =0;
		//				for(int i=0; i< anaVars.length;i++){
		//					if(anaVars[i]==null)
		//						lastVarAna = i;
		//
		//				}
		//				if (anaVarsCnt>2) {
		//					if(lastVarAna>anaVars.length-2){
		//						anaVars[anaVars.length-1] = null;
		//						anaVars[anaVars.length-2] =null;
		//
		//					}else{
		//						if(anaVarsCnt==GraphicConstants.MAX_GRAPHICS_COUNT * GraphicConstants.MAX_ANALOG_VARIABLES-2){
		//							anaVars[anaVarsCnt++]=null;
		//							anaVars[anaVarsCnt++]=null;
		//						}
		//						else if (anaVarsCnt==GraphicConstants.MAX_GRAPHICS_COUNT * GraphicConstants.MAX_ANALOG_VARIABLES ){
		//							anaVars[anaVars.length-1] = null;
		//							anaVars[anaVars.length-2] =null;
		//						}
		//					}
		//					decalage=2;
		//				}else{
		//					anaVars[anaVarsCnt++] = vitesse;
		//					anaVars[anaVarsCnt++] = vitesseCorrigee;
		//					addVariable=true;
		//				}
		//			}else{
		//				if (anaVarsCnt>3) {
		//					if(anaVarsCnt<anaVars.length){
		//						try {
		//							anaVars[anaVarsCnt++]=null;
		//						} catch (Exception e) {
		//
		//						}		
		//					}
		//					decalage=3;
		//				}else{
		//					anaVars[anaVarsCnt++] = vitesse;
		//					addVariable=true;
		//				}
		//			}
		//			if (!addVariable) {
		//				for (int i = anaVarsCnt-1; i>decalage ; i--) {
		//					anaVars[i]=anaVars[i-(decalage==2?2:1)*1];
		//				}
		//				if (varVitesseCorrigeePresente) {
		//					anaVars[decalage+1] = vitesseCorrigee;
		//				}
		//				anaVars[decalage] = vitesse;
		//			}
		//		}


		filtreDefault = new FiltreComposite();		
		filtreDefault.setNom("Built-in default filter");
		filtreDefault.setFiltrable(true);
		filtreDefault.setFiltreType(TypeFiltre.graphique);
		filtreDefault.setSelectionnable(true);

		AVariableComposant var;
		int curPos;
		String strColor;
//		boolean variablesAnalogicFinished = false;
//		int firstDigitalGraphicPos = -1;
		RGB[] colors = GestionnaireCouleurs.getVariablesColors();

		//issue 818
		
		int i=0;
		if (vitesse!=null) {
			//création 1er graphe avec seulement la vitesse
			filter = new GraphiqueFiltreComposite();
			filter.setFiltrable(true);
			filter.setFiltreType(TypeFiltre.graphique);
			filter.setSelectionnable(true);
			filter.setNumero(i);
			filter.setNom("Graphic #" + i + " - Analog");
			filter.setTypeGraphique(TypeGraphique.analogique);

			var = vitesse;
			newLineVar = new LigneVariableFiltreComposite();
			
			if(!VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(var.getDescriptor().getM_AIdentificateurComposant().getCode())){
			newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
			filter.ajouter(newLineVar);
			}else if(VitesseLimiteKVBService.isTableKVBXMLexist()){
			    newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
			    filter.ajouter(newLineVar);
			}			
			
			//Add the color filter
			newColor = new CouleurLigneVariable();
			strColor = Integer.toHexString(GestionnaireCouleurs.getIntValue(colors[i]));
			newColor.setNom(strColor);
			newColor.setValeurHexa(strColor);
			newLineVar.ajouter(newColor);
			if(filter.getEnfantCount() > 0)
				filtreDefault.ajouter(filter);
			i++;
		}

		if (digitalVarsCnt>0) {
			//création 2nd graphe avec seulement les variables digitales
			filter = new GraphiqueFiltreComposite();
			filter.setFiltrable(true);
			filter.setFiltreType(TypeFiltre.graphique);
			filter.setSelectionnable(true);
			filter.setNumero(i);

			filter.setNom("Graphic #" + i + " - Digital");
			filter.setTypeGraphique(TypeGraphique.digital);
			for(int j = 0; j < GraphicConstants.MAX_DISCRETE_VARIABLES; j++) {
				//set the variable filter
				if(j < digitalVarsCnt) {
					var = digitalVars[j];
					newLineVar = new LigneVariableFiltreComposite();
					newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
					filter.ajouter(newLineVar);

					//Add the color filter
					newColor = new CouleurLigneVariable();
					newColor.setValeurHexa(Integer.toHexString(GestionnaireCouleurs.getIntValue(colors[j])));
					newLineVar.ajouter(newColor);
				} else {
					break;
				}
			}		
			if(filter.getEnfantCount() > 0)
				filtreDefault.ajouter(filter);	//issue 818
		}
		
		

		//		for(int i = 0; i < GraphicConstants.MAX_GRAPHICS_COUNT; i++) {
		//
		//			if (i==GraphicConstants.MAX_GRAPHICS_COUNT-1 && digitalVarsCnt>0) {
		//				variablesAnalogicFinished=true;
		//			}
		//
		//			filter = new GraphiqueFiltreComposite();
		//			filter.setFiltrable(true);
		//			filter.setFiltreType(TypeFiltre.graphique);
		//			filter.setSelectionnable(true);
		//			filter.setNumero(i);
		//			//			filter.setActif(true);
		//			if(!variablesAnalogicFinished) {	//Analogic Filter
		//				filter.setNom("Graphic #" + i + " - Analog");
		//				filter.setTypeGraphique(TypeGraphique.analogique);
		//				//For the first graphic we add only MAX_ANALOG_GRAPHIC_VARIABLES - 1 variables
		//				//because one variable (corrected speed) will be added dynamically 
		//				int graphicVarsCount = GraphicConstants.MAX_ANALOG_VARIABLES;
		//
		//				for(int j = 0; j < graphicVarsCount; j++) {
		//					//current possition in the first 12 variables
		//					curPos = i == 0 ? j : i * GraphicConstants.MAX_ANALOG_VARIABLES + j;	
		//					//set the variable filter
		//					if(curPos < anaVarsCnt) {
		//						var = anaVars[curPos];
		//						newLineVar = new LigneVariableFiltreComposite();
		//
		//
		//
		//						newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
		//
		//						filter.ajouter(newLineVar);
		//
		//						//Add the color filter
		//						newColor = new CouleurLigneVariable();
		//						strColor = Integer.toHexString(GestionnaireCouleurs.getIntValue(colors[j]));
		//						newColor.setNom(strColor);
		//						newColor.setValeurHexa(strColor);
		//						newLineVar.ajouter(newColor);
		//
		//					} else {
		//						variablesAnalogicFinished = true;
		//						break;
		//					}
		//				}
		//
		//			}
		//
		//			//if the variables analogic finished and we have no childs (we might get here due to a break and we are
		//			//on the last graphic but analogic graphic created no childs)
		//			if(variablesAnalogicFinished && filter.getEnfantCount() == 0) {				//Digital Filter
		//				if(firstDigitalGraphicPos == -1)
		//					firstDigitalGraphicPos = i;
		//				filter.setNom("Graphic #" + i + " - Digital");
		//				filter.setTypeGraphique(TypeGraphique.digital);
		//				for(int j = 0; j < GraphicConstants.MAX_DISCRETE_VARIABLES; j++) {
		//					//set the variable filter
		//					curPos = (i - firstDigitalGraphicPos) * GraphicConstants.MAX_DISCRETE_VARIABLES + j;
		//					if(curPos < digitalVarsCnt) {
		//						var = digitalVars[curPos];
		//						newLineVar = new LigneVariableFiltreComposite();
		//						newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
		//						filter.ajouter(newLineVar);
		//
		//
		//						//Add the color filter
		//						newColor = new CouleurLigneVariable();
		//						newColor.setValeurHexa(Integer.toHexString(GestionnaireCouleurs.getIntValue(colors[j])));
		//						newLineVar.ajouter(newColor);
		//					} else {
		//						break;
		//					}
		//				}
		//			}

		//			Collections.sort(filter.getM_ALigneVariableFiltreComposant());
		//		if(filter.getEnfantCount() > 0)
		//			filtreDefault.ajouter(filter);
		//	}
		//		int a=0;
		//		int b=1;
		//		GraphiqueFiltreComposite df=(GraphiqueFiltreComposite)(filtreDefault.getEnfant(a));
		//		LigneVariableFiltreComposite lv= (LigneVariableFiltreComposite)df.getM_ALigneVariableFiltreComposant().get(b);
		//		CouleurLigneVariable clv=(CouleurLigneVariable)lv.getEnfant(0);
		//		clv.getValeurHexa();

		return filtreDefault; 
	}



}