package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlAnySimpleType.Factory;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.variables.OperateurLigneVariable;
import com.faiveley.samng.principal.sm.filtres.variables.ValeurLigneVariable;

import noNamespace.FiltreVuetabulaireDocument.FiltreVuetabulaire;
import noNamespace.LigneVariableDocument.LigneVariable;
import noNamespace.ListeFiltresVuetabulaireDocument;
import noNamespace.ListeFiltresVuetabulaireDocument.ListeFiltresVuetabulaire;
import noNamespace.ListeLignesVariableDocument.ListeLignesVariable;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ParseurFiltreTabulaire implements IParseurFiltre {

	private String filtersFileName;
	private ListeFiltresVuetabulaireDocument tabulaireFiltres;

	public ParseurFiltreTabulaire() {

	}

	/**
	 * 
	 * @param chemin
	 */
	public void parseRessource(String chemin,boolean explorer,int deb,int fin){
		File f=new File(chemin);
		if (f.exists()) {
			try {
				this.tabulaireFiltres = ListeFiltresVuetabulaireDocument.Factory.parse(f);
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
		ListeFiltresVuetabulaire xmlTabulaireFiltres = null;
		List<FiltreVuetabulaire> xmlTabulaireFiltresList;

		AFiltreComposant tabularViewFilter;
		AFiltreComposant varFilter;
		AFiltreComposant varOp;
		AFiltreComposant varVal;
		List<LigneVariable> xmlTabulaireLignesVarList;
		ListeLignesVariable xmlTabulaireLignesVar;
		String str;

		AFiltreComposant tabulaireViewFilters = new FiltreComposite();
		tabulaireViewFilters.setFiltreType(TypeFiltre.tabulaire);

		if (this.tabulaireFiltres == null) {
			return tabulaireViewFilters;
		}

		xmlTabulaireFiltres = this.tabulaireFiltres.getListeFiltresVuetabulaire();

		if (xmlTabulaireFiltres == null) {
			return tabulaireViewFilters;
		}

		xmlTabulaireFiltresList = xmlTabulaireFiltres.getFiltreVuetabulaireList();

		for (FiltreVuetabulaire filtre : xmlTabulaireFiltresList) {
			if (filtre == null) {
				continue;
			}

			//Create a new ListView filter
			tabularViewFilter = new FiltreComposite();

			if (filtre.getNom() == null) {
				System.err.println("List view filter name is null");

				continue;
			}

			str = filtre.getNom().getStringValue();

			if (str == null) {
				System.err.println("List view filter name is null");

				continue;
			}

			tabularViewFilter.setNom(str);
			tabularViewFilter.setFiltreType(TypeFiltre.tabulaire);
			//Add this filter to the ListView filters
			tabulaireViewFilters.ajouter(tabularViewFilter);

			xmlTabulaireLignesVar = filtre.getListeLignesVariable();

			if ((xmlTabulaireLignesVar != null) &&
					((xmlTabulaireLignesVarList = xmlTabulaireLignesVar.getLigneVariableList()) != null)) {
				//Set the active flag - not used anymore in tabular filters
				//varFiltersList.setFiltrable("true".equalsIgnoreCase(listLignesVar.getActif().getStringValue()));
				for (LigneVariable ligneVariable : xmlTabulaireLignesVarList) {
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
					if(ligneVariable.isSetOperateur() && ligneVariable.isSetValeur()) {
						varOp = new OperateurLigneVariable();
						varOp.setNom(ligneVariable.getOperateur().getStringValue());
						varVal = new ValeurLigneVariable();
						varVal.setNom(ligneVariable.getValeur().getStringValue());
					} else {	//we always add an opeator and a value
						varOp = new OperateurLigneVariable();
						varOp.setNom("");
						varOp.setFiltrable(false);
						varVal = new ValeurLigneVariable();
						varVal.setNom("");
						varVal.setFiltrable(false);
					}
					varFilter.ajouter(varOp);
					varFilter.ajouter(varVal);
					tabularViewFilter.ajouter(varFilter);
				}
			}
		}

		return tabulaireViewFilters;
	}

	public boolean enregistrerFiltre(AFiltreComposant filtre) {
		if(this.tabulaireFiltres == null)
			return false;

		FiltreVuetabulaire xmlRecFiltre = findFilterByName(filtre.getNom());
		ListeLignesVariable xmlListeLineVars;
		AFiltreComposant varOp;
		AFiltreComposant varVal;
		LigneVariable xmlLigneVar;
		XmlAnySimpleType name;

		if(xmlRecFiltre == null) {
			xmlRecFiltre = this.tabulaireFiltres.getListeFiltresVuetabulaire().addNewFiltreVuetabulaire();
			xmlListeLineVars = xmlRecFiltre.addNewListeLignesVariable();
		} else {
			//The best way is to remove all the events and lines filters from 
			//the current filter rather than perform a comparison for each of them
			xmlListeLineVars = xmlRecFiltre.getListeLignesVariable();
			xmlListeLineVars.getLigneVariableList().clear();
		}
		try {
			name = Factory.newInstance();
			name.setStringValue(filtre.getNom());
			xmlRecFiltre.setNom(name);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		int varFiltersListCount = filtre.getEnfantCount();
		LigneVariableFiltreComposite lineVarFiltre;
		for(int i = 0; i<varFiltersListCount; i++) {
			lineVarFiltre = (LigneVariableFiltreComposite)filtre.getEnfant(i);
			xmlLigneVar = xmlListeLineVars.addNewLigneVariable();
			try {
				name = Factory.newInstance();
				name.setStringValue(lineVarFiltre.getNom());
				xmlLigneVar.setNom(name);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if(lineVarFiltre.getEnfantCount() == 2) {
				varOp = lineVarFiltre.getEnfant(0);
				varVal = lineVarFiltre.getEnfant(1);
				if(varOp != null && varVal != null && varOp.getNom() != null && !"".equals(varOp.getNom())) {
					name = xmlLigneVar.addNewOperateur();
					name.setStringValue(varOp.getNom());
					name = xmlLigneVar.addNewValeur();
					name.setStringValue(varVal.getNom());
				}
			}
		}

		return saveDocument();
	}

	public boolean effacerFiltre(AFiltreComposant filtre) {
		FiltreVuetabulaire recFiltre = findFilterByName(filtre.getNom());
		if(recFiltre == null)
			return false;
		this.tabulaireFiltres.getListeFiltresVuetabulaire().getFiltreVuetabulaireList().remove(recFiltre);
//		OrdonnerFiltre.getInstance().getListeFiltreTabulaire().remove(filtre);
		return saveDocument();
	}

	protected FiltreVuetabulaire findFilterByName(String name) {
		String str;
		ListeFiltresVuetabulaire listFiltres = null;
		List<FiltreVuetabulaire> listFiltresList;

		if (this.tabulaireFiltres == null) {
			return null;
		}
		listFiltres = this.tabulaireFiltres.getListeFiltresVuetabulaire();
		if (listFiltres == null) {
			return null;
		}
		listFiltresList = listFiltres.getFiltreVuetabulaireList();
		for (FiltreVuetabulaire filtre : listFiltresList) {
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
			if(name.equals(str)) {
				return filtre;
			}
		}
		return null;	//Not found
	}

	private boolean saveDocument() {
		try {
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSavePrettyPrint();
			xmlOptions.setSavePrettyPrintIndent(4);
			this.tabulaireFiltres.save(new File(this.filtersFileName), xmlOptions);
			//: I think this is not ok ... Maybe another method is to have the listeFiltres updated
			this.tabulaireFiltres = ListeFiltresVuetabulaireDocument.Factory.parse(new File(this.filtersFileName));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
