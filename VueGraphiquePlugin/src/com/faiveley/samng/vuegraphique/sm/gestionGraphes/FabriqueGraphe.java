package com.faiveley.samng.vuegraphique.sm.gestionGraphes;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.VariableExplorationUtils;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.GestionnaireCouleurs;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeGraphique;
import com.faiveley.samng.principal.sm.filtres.variables.CouleurLigneVariable;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.CourbeMessageValue;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;

public class FabriqueGraphe {
	public static int MARGE_HAUT = 10;
	public static int MARGE_BAS = 30;
	public static int MARGE_LATERALE = 80;
	private static Map<Integer, Graphe> mapGraphes = new LinkedHashMap<Integer, Graphe>();

	private FabriqueGraphe() {
	}
	
	public static boolean variableDansMessage(Message msg,AVariableComposant avar){

		AVariableComposant var=null;
		try {
			var = msg.getVariable(avar.getDescriptor());
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (var!=null) {
			return true;
		}
		return false;
	}

	private static void initialiserDonneesGraphes(Graphe graphe) {
		
		if (TypeParseur.getInstance().getParser()instanceof ParseurParcoursAtess) {
			// A. It's an ATESS journey file: specific initialization
			initialiserDonneesGraphesForATESS(graphe);
		} else if (graphe.getTypeGraphe() == TypeGraphe.ANALOGIC) {
			// B. It's an ANALOGIC graph from a TOM journey file or unified journey file: 
			// take into account volatile variables
			Message msg;
			int msgId;
			//int courbeVarCode;
			AVariableComposant courbeVar;

			//Map<Integer, CourbeMessageValue> listValeur;
			List<CourbeMessageValue>[] listValeur;
			boolean firstVarNotNull;
			Double min;
			Double max;
			
			List<Float> curValues;
			DescripteurVariable descrVar;

//			r�cup�ration uniquement des bons messages
			ListMessages messages = ActivatorData.getInstance().getVueData()
			.getDataTable().getEnregistrement().getMessages();

			int nbEvent = messages.size();
			//m�thode initialiser graphe
			List<Courbe> listeCourbes = graphe.getListeCourbe();

//			long ttStart = System.currentTimeMillis();
			for (Courbe courbeCourante : listeCourbes) {
				//listValeur = new LinkedH-ashMap<Integer, CourbeMessageValue>();
				listValeur = new ArrayList[nbEvent];
				firstVarNotNull = false;
				boolean ruptAcquEnCours=false;
				min = 0D;
				max = 0D;

				courbeVar = courbeCourante.getVariable();
				descrVar = courbeVar.getDescriptor();
				//courbeVarCode = descrVar.getM_AIdentificateurComposant().getCode();
				
				Float lastCurValue = null;
				
				for (int i = 0; i < nbEvent; i++) {
					msg = messages.get(i);
					msgId = msg.getMessageId();
					//: this is a very time consuming operation a String comparisons are made inside
					List<Object> castedValues = VariableExplorationUtils.getCastedValuesFromMessage(msg, descrVar);
					
					if (!castedValues.isEmpty()) {
						ruptAcquEnCours=false;
						listValeur[i] = new ArrayList<CourbeMessageValue>(castedValues.size());
						curValues = new ArrayList<Float>(castedValues.size());
						curValues = getFloatValuesFromListObject(castedValues);

						for (Float curValue : curValues) {
							listValeur[i].add(new CourbeMessageValue(i, msgId, curValue, false));
							if (firstVarNotNull == false) {
							    	min = max = curValue == null ? null : curValue.doubleValue();
								firstVarNotNull = true;
							} else {
								if ((curValue != null && max != null && curValue > max )|| (max == null && curValue != null)) {
									max = curValue.doubleValue();
								} 
								if ((curValue != null && min != null && curValue < min)|| (min == null && curValue != null)) {
									min = curValue.doubleValue();
								}
							}
							lastCurValue = curValue;
						}
					} else {
//						if(firstVarNotNull != false)
//							listValeur[i] = new CourbeMessageValue(i, msgId, curValue, true);
						
						if(msg.getEvenement() != null && (msg.getEvenement().isRuptureAcquisition() || ruptAcquEnCours)){
//							listValeur[i] = new CourbeMessageValue(i, msgId, 0, true);
							listValeur[i] = new ArrayList<CourbeMessageValue>(1);
							ruptAcquEnCours=true;
						}else{
							// In case of multiple values (volatile variable), we take the last one in memory
							Float value = lastCurValue;
							listValeur[i] = new ArrayList<CourbeMessageValue>(1);
							listValeur[i].add(new CourbeMessageValue(i, msgId, value, true));
						}
					}
				}
				
				if(max == min) {
                		    if (max == 0D) {
                			max = 1D;
                		    } else if (max > 0D) {
                			min = 0D; // from 0 to Y
                		    } else {
                			max = 0D; // from -Y to 0
                		    }
                		}
                		
				if(min == null || min > 0){
				    min = 0D;
				}
				if(max == null || max < 0){
				    max = 0D;
				}
				
				courbeCourante.setValeurs(listValeur);
				courbeCourante.setMaxDomainValeur(max);
				courbeCourante.setMinDomainValeur(min);

			}
//			long ttStop = System.currentTimeMillis();
//			System.err.println("Initializing curves took " + (ttStop - ttStart)); //$NON-NLS-1$
		} else if (graphe.getTypeGraphe() == TypeGraphe.DIGITAL) {
			// C. It's a DIGITAL graph from a TOM journey file or unified journey file:
			// there's no volatile variable
			
			Message msg;
			int msgId;
			//int courbeVarCode;
			AVariableComposant var;
			AVariableComposant courbeVar;
			Object castedValue;

			//Map<Integer, CourbeMessageValue> listValeur;
			CourbeMessageValue[] listValeur;
			boolean firstVarNotNull;
			Double min;
			Double max;
			Float curValue;
			DescripteurVariable descrVar;

//			r�cup�ration uniquement des bons messages
			ListMessages messages = ActivatorData.getInstance().getVueData()
			.getDataTable().getEnregistrement().getMessages();

			int nbEvent = messages.size();
			//m�thode initialiser graphe
			List<Courbe> listeCourbes = graphe.getListeCourbe();

//			long ttStart = System.currentTimeMillis();
			for (Courbe courbeCourante : listeCourbes) {
				//listValeur = new LinkedH-ashMap<Integer, CourbeMessageValue>();
				listValeur = new CourbeMessageValue[nbEvent];
				firstVarNotNull = false;
				boolean ruptAcquEnCours=false;
				min = 0D;
				max = 0D;
				curValue = null;

				courbeVar = courbeCourante.getVariable();
				descrVar = courbeVar.getDescriptor();
				//courbeVarCode = descrVar.getM_AIdentificateurComposant().getCode();

				for (int i = 0; i < nbEvent; i++) {
					msg = messages.get(i);
					msgId = msg.getMessageId();
					//: this is a verry time consuming operation a String comparisons are made inside 
					var = msg.getVariable(descrVar);
					if (var != null) {
						ruptAcquEnCours=false;
						castedValue = var.getCastedValeur();
						curValue = getFloatValueFromObject(castedValue);
						listValeur[i] = new CourbeMessageValue(i, msgId, curValue, false);
						//listValeur.put(msgId, new CourbeMessageValue(curValue, false));
						if (firstVarNotNull == false) {
						    min = max = curValue == null ? null : curValue.doubleValue();
							firstVarNotNull = true;
						} else {
							if ((curValue != null && max != null && curValue > max )|| (max == null && curValue != null)) {
								max = curValue.doubleValue();
							} 
							if ((curValue != null && min != null && curValue < min)|| (min == null && curValue != null)) {
								min = curValue.doubleValue();
							}
						}
					} else {
//						if(firstVarNotNull != false)
//							listValeur[i] = new CourbeMessageValue(i, msgId, curValue, true);
						
						if(msg.getEvenement() != null && (msg.getEvenement().isRuptureAcquisition() || ruptAcquEnCours)){
//							listValeur[i] = new CourbeMessageValue(i, msgId, 0, true);
							ruptAcquEnCours=true;
						}else{
							listValeur[i] = new CourbeMessageValue(i, msgId, curValue, true);
						}
					}
				}
				
				if(max == min) {
                		    if (max == 0D) {
                			max = 1D;
                		    } else if (max > 0D) {
                			min = 0D; // from 0 to Y
                		    } else {
                			max = 0D; // from -Y to 0
                		    }
                		}
                		
				if(min == null || min > 0){
				    min = 0D;
				}
				if(max == null || max < 0){
				    max = 0D;
				}

				courbeCourante.setValeursSimples(listValeur);
				courbeCourante.setMaxDomainValeur(max);
				courbeCourante.setMinDomainValeur(min);
			}
		}
	}
	
	private static void initialiserDonneesGraphesForATESS(Graphe graphe) {
		Message msg;
		int msgId;
		//int courbeVarCode;
		AVariableComposant var;
		AVariableComposant courbeVar;
		Object castedValue;

		Courbe cKVB = null, cVitesse = null, cDTKVB = null;

		//Map<Integer, CourbeMessageValue> listValeur;
		CourbeMessageValue[] listValeur;
		boolean firstVarNotNull;
		double min;
		double max;
		Float curValue;
		DescripteurVariable descrVar;

//		r�cup�ration uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData()
		.getDataTable().getEnregistrement().getMessages();


		//r�cup�ration de tous les messages
//		List<Message> msgs = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
		int nbEvent = messages.size();
		//m�thode initialiser graphe
		List<Courbe> listeCourbes = graphe.getListeCourbe();

		long ttStart = System.currentTimeMillis();
		for (Courbe courbeCourante : listeCourbes) {
			//listValeur = new LinkedH-ashMap<Integer, CourbeMessageValue>();
			listValeur = new CourbeMessageValue[nbEvent];
			firstVarNotNull = false;
			min = 0;
			max = 0;
			curValue = null;
			boolean ruptAcquEnCours=false;

			courbeVar = courbeCourante.getVariable();
			descrVar = courbeVar.getDescriptor();
			//courbeVarCode = descrVar.getM_AIdentificateurComposant().getCode();

			for (int i = 0; i < nbEvent; i++) {
				msg = messages.get(i);
				msgId = msg.getMessageId();
				//: this is a very time consuming operation a String comparisons are made inside 
				var = msg.getVariable(descrVar);

				if (var != null) {
					ruptAcquEnCours=false;
					castedValue = var.getCastedValeur();
					curValue = getFloatValueFromObject(castedValue);
					
					if (msg.getEvenement().isRuptureAcquisition() && !variableDansMessage(msg,var)) {
					    listValeur[i] = new CourbeMessageValue(i, msgId, null, true);
					} else{
						listValeur[i] = new CourbeMessageValue(i, msgId, curValue, false);
					}
					
					//listValeur.put(msgId, new CourbeMessageValue(curValue, false));
					if (curValue != null && firstVarNotNull == false) {
						max = curValue;
						min = curValue;
						firstVarNotNull = true;
					} else {
						if (curValue != null && curValue > max) {
							max = curValue;
						} 
						if (curValue != null && curValue < min) {
							min = curValue;
						}
					}
				} else {
					if(msg.getEvenement().isRuptureAcquisition() || ruptAcquEnCours){
						listValeur[i] = new CourbeMessageValue(i, msgId, null, true);
						curValue=null;
						ruptAcquEnCours=true;
					}else{
						listValeur[i] = new CourbeMessageValue(i, msgId, curValue, true);
					}
				}
			}
			if(max == min) {
				if(max == 0)
					max = 1;
				else if(max > 0)
					min = 0;		//from 0 to Y
					else 
						max = 0;	//from -Y to 0
			}
			if(min > 0)
				min = 0;
			if(max < 0)
				max = 0;
			
			
			// Si la courbe est celle de la vitesse limite KVB ou la vitese limite issue des donn�es train KVB, et que la vitesse (SNCF) est aussi affich�e,
			// son axe des ordonn�es doit �tre identique � celui de la vitesse (SNCF) pour pouvoir avoir une analyse direct des d�passements
			if (courbeCourante.getVariable() == VitesseLimiteKVBService.getInstance().getVariable() && VitesseLimiteKVBService.isTableKVBXMLexist()) {
				cKVB = courbeCourante;					
			} else if (courbeCourante.getVariable().getDescriptor() == GestionnairePool.getInstance().getReperes().getRepere(TypeRepere.vitesse).getDescriptor()) {
				cVitesse = courbeCourante;
			} else if (courbeCourante.getVariable() == GestionnairePool.getInstance().getVariable("Dt.vitesseLimiteTrain")) {
				cDTKVB = courbeCourante;
			}
			
			//ATESS journey file: only simple values, not volatile variables
			courbeCourante.setValeursSimples(listValeur);
			courbeCourante.setMaxDomainValeur(max);
			courbeCourante.setMinDomainValeur(min);

		}
		
		// Si la courbe est celle de la vitesse limite KVB ou la vitese limite issue des donn�es train KVB, et que la vitesse (SNCF) est aussi affich�e,
		// son axe des ordonn�es doit �tre identique � celui de la vitesse (SNCF) pour pouvoir avoir une analyse direct des d�passements
		if (cVitesse != null) {
			
			Double maxDomainValeur = cVitesse.getMaxDomainValeur(), 
				   minDomainValeur = cVitesse.getMinDomainValeur();			
			
			if (cKVB != null) {
				if (cDTKVB != null){
					maxDomainValeur = Math.max(Math.max(cKVB.getMaxDomainValeur(), cVitesse.getMaxDomainValeur()), cDTKVB.getMaxDomainValeur());
					minDomainValeur = Math.min(Math.min(cKVB.getMinDomainValeur(), cVitesse.getMinDomainValeur()), cDTKVB.getMinDomainValeur());
					cDTKVB.getVariable().setEscalier(true);
					cDTKVB.setMaxDomainValeur(maxDomainValeur);
					cDTKVB.setMinDomainValeur(minDomainValeur);
				} else {
					maxDomainValeur = Math.max(cKVB.getMaxDomainValeur(), cVitesse.getMaxDomainValeur());
					minDomainValeur = Math.min(cKVB.getMinDomainValeur(), cVitesse.getMinDomainValeur());
				}
				
				cKVB.setMaxDomainValeur(maxDomainValeur);
				cKVB.setMinDomainValeur(minDomainValeur);
				
			} else if (cDTKVB != null){
				maxDomainValeur = Math.max(cDTKVB.getMaxDomainValeur(), cVitesse.getMaxDomainValeur());
				minDomainValeur = Math.min(cDTKVB.getMinDomainValeur(), cVitesse.getMinDomainValeur());
				cDTKVB.getVariable().setEscalier(true);
				cDTKVB.setMaxDomainValeur(maxDomainValeur);
				cDTKVB.setMinDomainValeur(minDomainValeur);
				cVitesse.setMaxDomainValeur(maxDomainValeur);
				cVitesse.setMinDomainValeur(minDomainValeur);
			}
			
			cVitesse.setMaxDomainValeur(maxDomainValeur);
			cVitesse.setMinDomainValeur(minDomainValeur);
		}
		
		long ttStop = System.currentTimeMillis();
		System.err.println("Initializing curves took " + (ttStop - ttStart)); //$NON-NLS-1$
	}

	public static Graphe initialiserGraphe(int width, int height, Graphe graphe) {
		//m�thode initialiser graphe
		List<Courbe> listeCourbes = graphe.getListeCourbe();
		double resoVerticale;
		for (Courbe courbeCourante : listeCourbes) {
			if(graphe.getTypeGraphe() == TypeGraphe.ANALOGIC) {
				resoVerticale = (courbeCourante.getMaxValeur() - courbeCourante.getMinValeur()) / 
				(height - (MARGE_HAUT + MARGE_BAS));
			} else {
				resoVerticale = (courbeCourante.getMaxValeur() - courbeCourante.getMinValeur()) / 
				(height - (MARGE_HAUT + MARGE_BAS));
			}
			if(resoVerticale == 0)
				resoVerticale = 1;

			courbeCourante.setResoVerticale(resoVerticale);
		}

		return graphe;
	}
	
//	private static void supprimerVariablesNonRenseignees(GraphiqueFiltreComposite graphicFilter){
//		for (int i = 0; i < graphicFilter.getEnfantCount(); i++) {
//			AFiltreComposant varFilter = graphicFilter.getEnfant(i);
//			AVariableComposant var=GestionnairePool.getVariable(varFilter.getNom());
//			if (!Util.isVariableDansParcours(var)) {
////				graphicFilter.supprimer(varFilter);
//				varFilter.setVariableRenseigneeDansParcours(false);
//			}
//		}
//	}
	
	public static void gestionGraphesRenseignes() {
		AbstractProviderFiltre filterProvider = ActivatorVueGraphique.getDefault().getFiltresProvider();
		GestionnaireFiltresGraphique filterMng = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault()
				.getFiltresProvider().getGestionnaireFiltres();

		String appliedFilterName = filterProvider.getAppliedFilter();
		AFiltreComposant appliedFilter = filterMng.getFiltre(appliedFilterName);
		boolean filtreValide = ActivatorVueGraphique.getDefault().getFiltresProvider().verifierValiditeFiltre(appliedFilter);
		if (appliedFilter == null || !filtreValide) {
			appliedFilter = filterMng.getFiltreDefaut();
		}
		if (appliedFilter != null) {
			for (int i = 0; i < appliedFilter.getEnfantCount(); i++) {
				for (int j = 0; j < appliedFilter.getEnfant(i).getEnfantCount(); j++) {
					AFiltreComposant varFilter = appliedFilter.getEnfant(i).getEnfant(j);
//					AVariableComposant var=GestionnairePool.getVariable(varFilter.getNom());
					if (!Util.getInstance().isVariableDansParcours(varFilter.getNom(),false)) {
						varFilter.setVariableRenseigneeDansParcours(false);
					}
				}
			}
		}
	}

	/**
	 * Extracts from the current applied filter the variables and their associated
	 * RGB colors. If the currently applied filter is not set then the defaut filter
	 * from the GestionnaireFiltresGraphique is used instead
	 *
	 * @return a map from the variable code to its RGB color
	 */
	public static Graphe creerGraphe(int numero,int nbGraphes) {
		GraphiqueFiltreComposite graphicFilter = getGrapheFiltre(numero);
//		supprimerVariablesNonRenseignees(graphicFilter);
//		gestionGraphesRenseignes();
		Graphe graphe = new Graphe();
		graphe.setNumero(numero);
		if(graphicFilter.getTypeGraphique()==TypeGraphique.digital) //$NON-NLS-1$
			graphe.setTypeGraphe(TypeGraphe.DIGITAL);
		else
			graphe.setTypeGraphe(TypeGraphe.ANALOGIC);

		if (graphicFilter != null) {
			AFiltreComposant varFilter;
			CouleurLigneVariable varColor;
			AVariableComposant var;
			String varFilterName;
			int courvesNb = graphicFilter.getEnfantCount();

			for (int i = 0; i < courvesNb; i++) {
				varFilter = graphicFilter.getEnfant(i);
				varColor = (CouleurLigneVariable) varFilter.getEnfant(0);
				varFilterName = varFilter.getNom();
				var = GestionnairePool.getInstance().getVariable(varFilterName);
				//if the variable is null then search it in the list of VBVs
				if(var == null && !VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(varFilterName)) {
					var = ActivatorData.getInstance().getProviderVBVs().
					getGestionnaireVbvs().getVBV(varFilterName);
				}
				if (var == null && VitesseLimiteKVBService.isTableKVBXMLexist() && VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(varFilterName)) {
					var = VitesseLimiteKVBService.getInstance().getVariable();
				}
				//if found (real variable or VBV)
				if(var != null && varFilter.isVariableRenseigneeDansParcours()) {
					Courbe courbe = new Courbe();
					courbe.setNum(i);
					courbe.setVariable(var);
					courbe.setCouleur(GestionnaireCouleurs.getRgbForHexValue(varColor.getValeurHexa()));
					graphe.ajouterCourbe(courbe);
				}
			}
		}

		initialiserDonneesGraphes(graphe);
		mapGraphes.put(numero, graphe);
		return graphe;
	}

	public static Graphe getGraphe(int numero) {
		return mapGraphes.get(numero);
	}

	public static Graphe[] getGraphes() {
		return mapGraphes.values().toArray(new Graphe[mapGraphes.size()]);
	}

	public static GraphiqueFiltreComposite getGrapheFiltre(int numero) {
		GraphiqueFiltreComposite graphique = null;
		GestionnaireFiltresGraphique filterMng = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault()
		.getFiltresProvider().getGestionnaireFiltres();
		//get current applied filter
		String appliedFilterName;

		boolean isvalidefilter = false;
		if (ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres().getFiltre(ActivatorVueGraphique.getDefault().getConfigurationMng().getFiltreApplique())!=null) {
			isvalidefilter=ActivatorVueGraphique.getDefault().getFiltresProvider().filtrevalide(null);
		}else{
			isvalidefilter=true;
		}
//		VueProgressBar.getInstance().stop();
		if (!isvalidefilter) {
//			if(VueWaitBar.getInstance()!=null && VueWaitBar.getInstance().isWorking())
//				VueWaitBar.getInstance().stop();
			appliedFilterName=null;
			String badfilter = ActivatorVueGraphique.getDefault().getConfigurationMng().getFiltreApplique();
			ActivatorVueGraphique.getDefault().getConfigurationMng().setFiltreApplique(null);
			ActivatorVueGraphique.getDefault().getFiltresProvider().setAppliedFilterName(null);
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.YES);
			msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
			msgBox.setText(Messages.getString("FabriqueGraphe.filtrenonvalideTitre"));  //$NON-NLS-1$
			msgBox.setMessage(badfilter+ " : " + Messages.getString("FabriqueGraphe.filtrenonvalideMessage2")); //$NON-NLS-1$ //$NON-NLS-2$
			msgBox.open();
//			VueWaitBar.getInstance().start();
		}else{
			appliedFilterName = ActivatorVueGraphique.getDefault().getConfigurationMng().getFiltreApplique();
		}

		AFiltreComposant appliedFilter = filterMng.getFiltre(appliedFilterName);

		//if no applied filter, get the default filter
		if (appliedFilter == null) {
			appliedFilter = filterMng.getFiltreDefaut();
		}

		if (appliedFilter != null) {
			int graphesNb = appliedFilter.getEnfantCount();
			//search the graphic with the given number
			for (int i = 0; i < graphesNb; i++) {
				graphique = (GraphiqueFiltreComposite) appliedFilter.getEnfant(i);

				if (numero == i) {
					graphique = (GraphiqueFiltreComposite) appliedFilter.getEnfant(i);
					break;
				}
			}
		}
		return graphique;
	}

	public static GraphiqueFiltreComposite getGrapheFiltre2(int numero) {
		GraphiqueFiltreComposite graphique = null;
		GestionnaireFiltresGraphique filterMng = (GestionnaireFiltresGraphique) ActivatorVueGraphique.getDefault()
		.getFiltresProvider().getGestionnaireFiltres();
		//get current applied filter
		String appliedFilterName;

		boolean isvalidefilter = false;
		if (ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres().getFiltre(ActivatorVueGraphique.getDefault().getConfigurationMng().getFiltreApplique())!=null) {
			isvalidefilter=ActivatorVueGraphique.getDefault().getFiltresProvider().filtrevalide(null);
		}else{
			isvalidefilter=true;
		}
//		VueProgressBar.getInstance().stop();
		if (!isvalidefilter) {
			appliedFilterName=null;
			String badfilter = ActivatorVueGraphique.getDefault().getConfigurationMng().getFiltreApplique();
			ActivatorVueGraphique.getDefault().getConfigurationMng().setFiltreApplique(null);
			ActivatorVueGraphique.getDefault().getFiltresProvider().setAppliedFilterName(null);
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(), SWT.ICON_WARNING | SWT.YES);
			msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.OK);
			msgBox.setText(Messages.getString("FabriqueGraphe.filtrenonvalideTitre"));  //$NON-NLS-1$
			msgBox.setMessage(badfilter+ " : " + Messages.getString("FabriqueGraphe.filtrenonvalideMessage2")); //$NON-NLS-1$ //$NON-NLS-2$
			msgBox.open();
		}else{
			appliedFilterName = ActivatorVueGraphique.getDefault().getConfigurationMng().getFiltreApplique();
		}

		AFiltreComposant appliedFilter = filterMng.getFiltre(appliedFilterName);

		//if no applied filter, get the default filter
		if (appliedFilter == null) {
			appliedFilter = filterMng.getFiltreDefaut();
		}

		if (appliedFilter != null) {
			//search the graphic with the given number
			try {
				graphique = (GraphiqueFiltreComposite) appliedFilter.getEnfant(numero);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}
		return graphique;
	}

	public static double getDoubleValueFromObject(Object obj) {
		if(obj instanceof Number)
			return ((Number)obj).doubleValue();
		else if(obj instanceof String) {
			try {
				return Double.parseDouble((String)obj);
			} catch (NumberFormatException e) {
			}
		}
		return 0;
	}
	
	private static List<Float> getFloatValuesFromListObject(List<Object> objects) {
		List<Float> floatValues = new ArrayList<Float>(objects.size());
		
		for(Object obj : objects) {
			Float value = getFloatValueFromObject(obj);
			floatValues.add(value);
		}
		
		return floatValues;
	}

	public static Float getFloatValueFromObject(Object obj) {
		if(obj instanceof Number)
			return ((Number)obj).floatValue();
		else if(obj instanceof String) {
			try {
				return Float.parseFloat((String)obj);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	public static void removeGraphe(int numero) {
		mapGraphes.remove(numero);
	}
}
