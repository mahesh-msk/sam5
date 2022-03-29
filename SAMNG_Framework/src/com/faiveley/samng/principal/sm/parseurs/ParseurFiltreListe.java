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
import com.faiveley.samng.principal.sm.filtres.LigneEvenementFiltre;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;

import noNamespace.FiltreVuelisteDocument.FiltreVueliste;
import noNamespace.LigneEvenementDocument.LigneEvenement;
import noNamespace.LigneVariableDocument.LigneVariable;
import noNamespace.ListeFiltresVuelisteDocument;
import noNamespace.ListeFiltresVuelisteDocument.ListeFiltresVueliste;
import noNamespace.ListeLignesEvenementDocument.ListeLignesEvenement;
import noNamespace.ListeLignesVariableDocument.ListeLignesVariable;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 23-nov.-2007 15:47:07
 */
public class ParseurFiltreListe implements IParseurFiltre {

	private String filtersFileName;
	private ListeFiltresVuelisteDocument listeFiltres;

	public ParseurFiltreListe() {

	}

	/**
	 * 
	 * @param chemin
	 */
	public void parseRessource(String chemin,boolean explorer,int deb,int fin) {
		File f=new File(chemin);
		if (f.exists()) {
			try {
				this.listeFiltres = ListeFiltresVuelisteDocument.Factory.parse(f);
				this.filtersFileName = chemin;
			} catch (XmlException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}catch(Exception ex){
				System.err.println("Impossible de charger le fichier :" + chemin);
			}
		}
	}

	public AFiltreComposant chargerFiltres() {
		ListeFiltresVueliste listFiltres = null;
		List<FiltreVueliste> listFiltresList;

		AFiltreComposant listViewFilter;
		ListeLignesEvenement listLignesEv;
		List<LigneEvenement> listLignesEvList;
		AFiltreComposant evFilter;
		AFiltreComposant varFilter;
		AFiltreComposant evFiltersList;
		AFiltreComposant varFiltersList;
		List<LigneVariable> listLignesVarList;
		ListeLignesVariable listLignesVar;
		String choixVariable=null;
		String str;

		AFiltreComposant listViewFilters = new FiltreComposite();
		listViewFilters.setFiltreType(TypeFiltre.liste);

		if (this.listeFiltres == null) {
			return listViewFilters;
		}

		listFiltres = this.listeFiltres.getListeFiltresVueliste();

		if (listFiltres == null) {
			return listViewFilters;
		}

		listFiltresList = listFiltres.getFiltreVuelisteList();

		for (FiltreVueliste filtre : listFiltresList) {
			if (filtre == null) {
				continue;
			}

			// Create a new ListView filter
			listViewFilter = new FiltreComposite();

			if (filtre.getNom() == null) {
				System.err.println("List view filter name is null");

				continue;
			}

			str = filtre.getNom().getStringValue();

			if (str == null) {
				System.err.println("List view filter name is null");

				continue;
			}

			if(filtre.getChoixVariable()!=null){
				//modification olivier gestion filtres listes
				//ajout d'un attribut choixvariable dans l'objet filtre
				choixVariable = filtre.getChoixVariable().getStringValue();
			}



			listViewFilter.setNom(str);
			//gestion des filtres ancienne version
			if(choixVariable==null)
				listViewFilter.setChoixVariable(false);
			else
				listViewFilter.setChoixVariable(Boolean.parseBoolean(choixVariable));
			listViewFilter.setFiltreType(TypeFiltre.liste);
			// add the list of events and variables even if we don't have any
			// configured
			evFiltersList = new FiltreComposite();
			varFiltersList = new FiltreComposite();
			listViewFilter.ajouter(evFiltersList);
			listViewFilter.ajouter(varFiltersList);
			// Add this filter to the ListView filters
			listViewFilters.ajouter(listViewFilter);

			// Load the event filters for this filter
			listLignesEv = filtre.getListeLignesEvenement();

			if ((listLignesEv != null)
					&& ((listLignesEvList = listLignesEv
					.getLigneEvenementList()) != null)) {
				for (LigneEvenement ligneEvenement : listLignesEvList) {
					if (ligneEvenement == null) {
						System.err.println("liste evenement is null");

						continue;
					}

					if (ligneEvenement.getNom() == null) {
						System.err.println("liste evenement name is null");

						continue;
					}

					str = ligneEvenement.getNom().getStringValue();

					if (str == null) {
						System.err.println("liste evenement name is null");

						continue;
					}

					evFilter = new LigneEvenementFiltre();
					evFilter.setNom(str);
					evFiltersList.ajouter(evFilter);
				}
			}

			listLignesVar = filtre.getListeLignesVariable();

			if ((listLignesVar != null)
					&& ((listLignesVarList = listLignesVar
					.getLigneVariableList()) != null)) {
				// Set the active flag
				varFiltersList.setFiltrable("true".equalsIgnoreCase(listLignesVar.getActif().getStringValue()));
				for (LigneVariable ligneVariable : listLignesVarList) {
					if (ligneVariable == null) {
						System.err.println("liste evenement is null");
						continue;
					}

					if (ligneVariable.getNom() == null) {
						System.err.println("liste evenement name is null");
						continue;
					}

					str = ligneVariable.getNom().getStringValue();
					if (str == null) {
						System.err.println("liste evenement name is null");
						continue;
					}

					varFilter = new LigneVariableFiltreComposite();
					varFilter.setNom(str);
					varFiltersList.ajouter(varFilter);
				}
			}
		}
		return listViewFilters;
	}

	public boolean enregistrerFiltre(AFiltreComposant filtre) {
		if (this.listeFiltres == null)
			return false;

		FiltreVueliste xmlRecFiltre = findFilterByName(filtre.getNom());
		ListeLignesEvenement xmlListeLineEvents;
		ListeLignesVariable xmlListeLineVars;
		AFiltreComposant evFiltersList;
		AFiltreComposant varFiltersList = null;

		if (xmlRecFiltre == null) {
			xmlRecFiltre = this.listeFiltres.getListeFiltresVueliste().addNewFiltreVueliste();
			xmlListeLineEvents = xmlRecFiltre.addNewListeLignesEvenement();
			xmlListeLineVars = xmlRecFiltre.addNewListeLignesVariable();
		} else {
			// The best way is to remove all the events and lines filters from
			// the current filter rather than perform a comparison for each of
			// them
			xmlListeLineEvents = xmlRecFiltre.getListeLignesEvenement();
			xmlListeLineVars = xmlRecFiltre.getListeLignesVariable();
			xmlListeLineEvents.getLigneEvenementList().clear();
			xmlListeLineVars.getLigneVariableList().clear();
		}
		try {
			XmlAnySimpleType name = Factory.newInstance();
			name.setStringValue(filtre.getNom());
			xmlRecFiltre.setNom(name);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		//modification olivier gestion filtres listes
		//ajout d'un attribut choixvariable dans l'objet filtre
		try {
			XmlAnySimpleType choixVariable = Factory.newInstance();
			if(filtre.isChoixVariable())
				choixVariable.setStringValue("true");
			else
				choixVariable.setStringValue("false");
			xmlRecFiltre.setChoixVariable(choixVariable);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		evFiltersList = filtre.getEnfant(0);
		int evFiltersListCount = evFiltersList.getEnfantCount();
		LigneEvenementFiltre lineEvFiltre;
		for (int i = 0; i < evFiltersListCount; i++) {
			lineEvFiltre = (LigneEvenementFiltre) evFiltersList.getEnfant(i);
			LigneEvenement xmlLigneEv = xmlListeLineEvents.addNewLigneEvenement();
			try {
				XmlAnySimpleType name = Factory.newInstance();
				name.setStringValue(lineEvFiltre.getNom());
				xmlLigneEv.setNom(name);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		//		try {
		//			filtre.getEnfant(1);
		//		} catch (Exception e) {
		//			try {
		//				filtre.getEnfant(0);
		//			} catch (RuntimeException e1) {
		//				filtre.ajouter(new FiltreComposite());
		//			}
		//		}
		if (filtre.getEnfantCount() > 1) {
			varFiltersList = filtre.getEnfant(1);
			if (varFiltersList != null) {
				if (!varFiltersList.isFiltrable()) {
					XmlAnySimpleType active = xmlListeLineVars.addNewActif();
					active.setStringValue("false");
				}
				int varFiltersListCount = varFiltersList.getEnfantCount();
				LigneVariableFiltreComposite lineVarFiltre;
				for (int i = 0; i < varFiltersListCount; i++) {
					lineVarFiltre = (LigneVariableFiltreComposite) varFiltersList.getEnfant(i);
					LigneVariable xmlLigneEv = xmlListeLineVars.addNewLigneVariable();
					try {
						XmlAnySimpleType name = Factory.newInstance();
						name.setStringValue(lineVarFiltre.getNom());
						xmlLigneEv.setNom(name);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		return saveDocument();
	}

	public boolean effacerFiltre(AFiltreComposant filtre) {
		FiltreVueliste recFiltre = findFilterByName(filtre.getNom());
		if (recFiltre == null)
			return false;
		this.listeFiltres.getListeFiltresVueliste().getFiltreVuelisteList().remove(recFiltre);
//		OrdonnerFiltre.getInstance().getListeFiltreListe().remove(filtre);
		return saveDocument();
	}

	protected FiltreVueliste findFilterByName(String name) {
		String str;
		ListeFiltresVueliste listFiltres = null;
		List<FiltreVueliste> listFiltresList;

		if (this.listeFiltres == null) {
			return null;
		}
		listFiltres = this.listeFiltres.getListeFiltresVueliste();
		if (listFiltres == null) {
			return null;
		}
		listFiltresList = listFiltres.getFiltreVuelisteList();
		for (FiltreVueliste filtre : listFiltresList) {
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
		return null; // Not found
	}

	private boolean saveDocument() {
		try {
			XmlOptions xmlOptions = new XmlOptions();
			xmlOptions.setSavePrettyPrint();
			xmlOptions.setSavePrettyPrintIndent(4);
			this.listeFiltres.save(new File(this.filtersFileName), xmlOptions);
			// : I think this is not ok ... Maybe another method is to have the
			// listeFiltres updated
			this.listeFiltres = ListeFiltresVuelisteDocument.Factory.parse(new File(this.filtersFileName));		
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}