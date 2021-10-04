package com.faiveley.samng.vuegraphique.sm.parseurs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import noNamespace.FiltreVuegraphiqueDocument.FiltreVuegraphique;
import noNamespace.GraphiqueDocument.Graphique;
import noNamespace.GraphiqueDocument.Graphique.Type;
import noNamespace.LigneVariableDocument.LigneVariable;
import noNamespace.ListeFiltresVuegraphiqueDocument;
import noNamespace.ListeFiltresVuegraphiqueDocument.ListeFiltresVuegraphique;
import noNamespace.ListeLignesVariableDocument.ListeLignesVariable;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlAnySimpleType.Factory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeGraphique;
import com.faiveley.samng.principal.sm.filtres.variables.CouleurLigneVariable;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.IParseurFiltre;


/**
 *
 * @author Cosmin Udroiu
 *
 */
public class ParseurFiltreGraphique implements IParseurFiltre {
	private String filtersFileName;
	private ListeFiltresVuegraphiqueDocument graphiqueFiltres;

	public ParseurFiltreGraphique() {
	}

	/**
	 *
	 * @param chemin
	 */
	public void parseRessource(String chemin,boolean explorer,int deb,int fin) {
		File f=new File(chemin);
		if (f.exists()) {
			try {
				this.graphiqueFiltres = ListeFiltresVuegraphiqueDocument.Factory.parse(f);
				this.filtersFileName = chemin;
			} catch (XmlException e) {
				//  Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch(Exception ex){
				System.err.println("Impossible de charger le fichier :" + chemin);
			}
		}
	}

	public AFiltreComposant chargerFiltres() {
		ListeFiltresVuegraphique listFiltres = null;
		List<FiltreVuegraphique> xmlGraphiqueFiltresList;

		List<Graphique> xmlListGraphiques;
		GraphiqueFiltreComposite graphiqueFiltre;

		AFiltreComposant graphiqueViewFilter;
		AFiltreComposant varFilter;
		CouleurLigneVariable varCouleur;
		List<LigneVariable> xmlGraphiqueLignesVarList;
		ListeLignesVariable xmlGraphiqueLignesVar;
		String str;
		String strColor;
		XmlAnySimpleType xmlSimpleType;

		AFiltreComposant graphiqueViewFilters = new FiltreComposite();
		graphiqueViewFilters.setFiltreType(TypeFiltre.graphique);

		if (this.graphiqueFiltres == null) {
			return graphiqueViewFilters;
		}

		listFiltres = this.graphiqueFiltres.getListeFiltresVuegraphique();

		if (listFiltres == null) {
			return graphiqueViewFilters;
		}

		xmlGraphiqueFiltresList = listFiltres.getFiltreVuegraphiqueList();

		for (FiltreVuegraphique xmlFiltre : xmlGraphiqueFiltresList) {
			if (xmlFiltre == null) {
				continue;
			}

			//Create a new ListView filter
			graphiqueViewFilter = new FiltreComposite();
			xmlSimpleType = xmlFiltre.getNom();
			if (xmlSimpleType == null) {
				System.err.println("Graphic view filter name is null");

				continue;
			}

			str = xmlSimpleType.getStringValue();

			if (str == null || "".equals(str.trim())) {
				System.err.println("Graphic view filter name is null or empty");

				continue;
			}

			graphiqueViewFilter.setNom(str);
			graphiqueViewFilter.setFiltreType(TypeFiltre.graphique);
			//Add this filter to the ListView filters
			graphiqueViewFilters.ajouter(graphiqueViewFilter);

			xmlListGraphiques = xmlFiltre.getGraphiqueList();

			if (xmlListGraphiques != null) {
				for (Graphique xmlGraphique : xmlListGraphiques) {
					graphiqueFiltre = new GraphiqueFiltreComposite();

					try {
						graphiqueFiltre.setActif(new Boolean(xmlGraphique.getActif().getStringValue()).booleanValue());                 	
					} catch (Exception e) {
						graphiqueFiltre.setActif(true);
					}

					graphiqueFiltre.setNom(xmlGraphique.getNom().getStringValue());
					try {
						graphiqueFiltre.setNumero(Integer.parseInt(xmlGraphique.getNumero().getStringValue()));
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}

					if(xmlGraphique.getType()!=null){
						switch(xmlGraphique.getType().intValue()){

						case Type.INT_ANALOGIQUE:
							graphiqueFiltre.setTypeGraphique(TypeGraphique.analogique);
							break;
						case Type.INT_DIGITAL:
							graphiqueFiltre.setTypeGraphique(TypeGraphique.digital);
							break;


						}
					}

					graphiqueViewFilter.ajouter(graphiqueFiltre);

					xmlGraphiqueLignesVar = xmlGraphique.getListeLignesVariable();

					if ((xmlGraphiqueLignesVar != null) &&
							((xmlGraphiqueLignesVarList = xmlGraphiqueLignesVar.getLigneVariableList()) != null)) {
						//Set the active flag - not used anymore in tabular filters
						//varFiltersList.setFiltrable("true".equalsIgnoreCase(listLignesVar.getActif().getStringValue()));
						for (LigneVariable ligneVariable : xmlGraphiqueLignesVarList) {
							if (ligneVariable == null) {
								System.err.println("line variable is null");

								continue;
							}

							if (ligneVariable.getNom() == null) {
								System.err.println("variable name is null");

								continue;
							}

							str = ligneVariable.getNom().getStringValue();

							if (str == null) {
								System.err.println("variable name name is null");

								continue;
							}

							varFilter = new LigneVariableFiltreComposite();
							varFilter.setNom(str);
							strColor = "0";
							varCouleur = new CouleurLigneVariable();
							if (ligneVariable.isSetCouleur()) {
								strColor = ligneVariable.getCouleur()
										.getStringValue();
							} else { //we always add an opeator and a value
								varCouleur.setFiltrable(false);
							}
							varCouleur.setNom(strColor);
							varCouleur.setValeurHexa(strColor);

							varFilter.ajouter(varCouleur);
							graphiqueFiltre.ajouter(varFilter);
						}
					}
				}
			}
		}

		return graphiqueViewFilters;
	}

	public boolean enregistrerFiltre(AFiltreComposant filtre) {
		if (this.graphiqueFiltres == null) {
			return false;
		}

		FiltreVuegraphique xmlRecFiltre = findFilterByName(filtre.getNom());
		ListeLignesVariable xmlListeLineVars;
		AFiltreComposant varCouleur;
		LigneVariable xmlLigneVar;
		XmlAnySimpleType name;

		if (xmlRecFiltre == null) {
			xmlRecFiltre = this.graphiqueFiltres.getListeFiltresVuegraphique()
					.addNewFiltreVuegraphique();
		} else {
			//The best way is to remove all the events and lines filters from 
			//the current filter rather than perform a comparison for each of them
			xmlRecFiltre.getGraphiqueList().clear();
		}
		name = Factory.newInstance();
		name = xmlRecFiltre.addNewNom();
		name.setStringValue(filtre.getNom());

		int graphicFiltersListCount = filtre.getEnfantCount();
		GraphiqueFiltreComposite graphiqueFiltre;
		Graphique xmlGraphique;
		LigneVariableFiltreComposite lineVarFiltre;

		for (int i = 0; i < graphicFiltersListCount; i++) {
			graphiqueFiltre = (GraphiqueFiltreComposite) filtre.getEnfant(i);
			xmlGraphique = xmlRecFiltre.addNewGraphique();
			name = xmlGraphique.addNewNom();
			name.setStringValue(graphiqueFiltre.getNom());
			name = xmlGraphique.addNewNumero();
			name.setStringValue(Integer.toString(graphiqueFiltre.getNumero()));



			name=xmlGraphique.addNewActif();
			name.setStringValue(new Boolean(graphiqueFiltre.isActif()).toString());


			if(graphiqueFiltre.getTypeGraphique()!=null){
				if(graphiqueFiltre.getTypeGraphique().equals(TypeGraphique.analogique))
					xmlGraphique.setType(Type.ANALOGIQUE);

				if(graphiqueFiltre.getTypeGraphique().equals(TypeGraphique.digital))
					xmlGraphique.setType(Type.DIGITAL);
			}
			else{
				if(graphiqueFiltre.getNom().contains(com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages.getString("GraphiqueFiltresEditorTable.34")))
					xmlGraphique.setType(Type.DIGITAL);
				else if (graphiqueFiltre.getNom().contains(com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages.getString("GraphiqueFiltresEditorTable.33")))
					xmlGraphique.setType(Type.ANALOGIQUE);
				else{
					if(graphiqueFiltre.getEnfantCount()>0){
						LigneVariableFiltreComposite premiereLigneGraphe = (LigneVariableFiltreComposite)graphiqueFiltre.getEnfant(0);
						AVariableComposant varComp = GestionnairePool.getInstance().getVariable(premiereLigneGraphe.getNom());
						if(varComp.getDescriptor().getTypeVariable()==TypeVariable.VAR_ANALOGIC)
							xmlGraphique.setType(Type.ANALOGIQUE);
						if(varComp.getDescriptor().getTypeVariable()==TypeVariable.VAR_DISCRETE || varComp.getDescriptor().getTypeVariable()==TypeVariable.VAR_VIRTUAL)
							xmlGraphique.setType(Type.DIGITAL);
					}
					else{
						name=xmlGraphique.addNewActif();
						name.setStringValue(new Boolean(false).toString());
					}
				}

			}



			xmlListeLineVars = xmlGraphique.addNewListeLignesVariable();

			for(int j = 0; j < graphiqueFiltre.getEnfantCount(); j++) {
				xmlLigneVar = xmlListeLineVars.addNewLigneVariable(); 
				lineVarFiltre = (LigneVariableFiltreComposite)graphiqueFiltre.getEnfant(j);
				name = xmlLigneVar.addNewNom();
				name.setStringValue(lineVarFiltre.getNom());
				if (lineVarFiltre.getEnfantCount() == 1) {
					varCouleur = lineVarFiltre.getEnfant(0);

					if ((varCouleur != null) && (varCouleur.getNom() != null)) {
						name = xmlLigneVar.addNewCouleur();
						name.setStringValue(varCouleur.getNom());
					}
				}
			}
		}

		return saveDocument();
	}

	public boolean effacerFiltre(AFiltreComposant filtre) {
		FiltreVuegraphique recFiltre = findFilterByName(filtre.getNom());

		if (recFiltre == null) {
			return false;
		}

		this.graphiqueFiltres.getListeFiltresVuegraphique().getFiltreVuegraphiqueList()
		.remove(recFiltre);
		
//		OrdonnerFiltre.getInstance().getListeFiltreGraphique().remove(filtre);

		return saveDocument();
	}

	protected FiltreVuegraphique findFilterByName(String name) {
		String str;
		ListeFiltresVuegraphique listFiltres = null;
		List<FiltreVuegraphique> listFiltresList;

		if (this.graphiqueFiltres == null) {
			return null;
		}

		listFiltres = this.graphiqueFiltres.getListeFiltresVuegraphique();

		if (listFiltres == null) {
			return null;
		}

		listFiltresList = listFiltres.getFiltreVuegraphiqueList();

		for (FiltreVuegraphique filtre : listFiltresList) {
			if (filtre == null) {
				continue;
			}

			if (filtre.getNom() == null) {
				continue;
			}

			str = filtre.getNom().getStringValue();

			if (str == null) {
				continue;
			}

			if (name.equals(str)) {
				return filtre;
			}
		}

		return null; //Not found
	}

	private boolean saveDocument() {
		try {
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSavePrettyPrint();
			xmlOptions.setSavePrettyPrintIndent(4);
			this.graphiqueFiltres.save(new File(this.filtersFileName), xmlOptions);
			//: I think this is not ok ... Maybe another method is to have the listeFiltres updated
			this.graphiqueFiltres = ListeFiltresVuegraphiqueDocument.Factory.parse(new File(
					this.filtersFileName));
		} catch (Exception e) {
			e.printStackTrace();

			return false;
		}

		return true;
	}
}
