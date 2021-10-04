package com.faiveley.samng.principal.sm.filtres.gestionnaires;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 23-nov.-2007 15:24:31
 */
public abstract class AGestionnaireFiltres  {

	protected AFiltreComposant filtreCourant;
	public AFiltreComposant listeFiltres;
	protected FiltreComposite filtreDefault;

	public AGestionnaireFiltres() {
		//Create the list of filters 
		this.listeFiltres = new FiltreComposite();
	}

	public void finalize() throws Throwable {
	}

	/**
	 * 
	 * @param filtre
	 */
	public void ajouterFiltre(AFiltreComposant filtre) {
		if(filtre == null)
			throw new IllegalArgumentException("Trying to add a null filter");
		this.listeFiltres.ajouter(filtre);
	}

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant getFiltre(int indice) {
		return this.listeFiltres.getEnfant(indice);
	}
	
	public AFiltreComposant getFiltre(String nom) {
		int filtreIdx = getFiltreIndex(nom);
		if(filtreIdx < 0)
			return null;
		return this.listeFiltres.getEnfant(filtreIdx);
	}
	
	public int getFiltreIndex(String nom) {
		if(nom != null) {
			int filtreNo = listeFiltres.getEnfantCount();
			for(int i = 0; i<filtreNo; i++) {
				if(nom.equals(listeFiltres.getEnfant(i).getNom())) {
					return i;
				}
			}
		}
		return -1;
	}

	public AFiltreComposant getFiltreCourant(){
		return filtreCourant;
	}

	public AFiltreComposant getListeFiltres(){
		return listeFiltres;
	}

	/**
	 * 
	 * @param listeBaseFiltre
	 * @param listeFiltre
	 */
	public AFiltreComposant majListeVariables(AFiltreComposant listeBaseFiltre, AFiltreComposant listeFiltre){
		return null;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFiltreCourant(AFiltreComposant newVal){
		filtreCourant = newVal;
	}

	/**
	 * 
	 * @param indice
	 */
	public AFiltreComposant supprimerFiltre(int indice) {
		AFiltreComposant filtre = this.listeFiltres.getEnfant(indice);
		if(filtre != null){
			this.listeFiltres.supprimer(filtre);
		}
		return filtre;
	}

	public AFiltreComposant supprimerFiltre(String nom){
		AFiltreComposant filtre = getFiltre(nom);
		if(filtre != null){
			this.listeFiltres.supprimer(filtre);
		}
		return filtre;
	}
	
	public AFiltreComposant dupliquerFiltre(AFiltreComposant filtre, String newVal) {
		if(filtre == null)
			return null;
		AFiltreComposant duplicateFiltre = filtre.clone();
		duplicateFiltre.setNom(newVal);
		return duplicateFiltre;
	}
	
	abstract public AFiltreComposant initialiserFiltreDefaut();

	public FiltreComposite getFiltreDefault() {
		return filtreDefault;
	}

	public void setFiltreDefault(FiltreComposite filtreDefault) {
		this.filtreDefault = filtreDefault;
	}
	
	/**
	 * Méthode qui va chercher dans le fichier bridage/bridageFiltre.properties le nombre
	 * de filtre à afficher dans le menu contextuel
	 * @return int nombre de filtres à afficher dans le menu contextuel
	 */
	public int getLimiteBridageFiltre(){
		java.util.Properties prop = new java.util.Properties();
		InputStream input = null;
		try{
			input = new FileInputStream(RepertoiresAdresses.filtresproperties);
			
			prop.load(input);
			
			String brid = prop.getProperty("maxFiltreContextuel");
			if(brid != null){
				try{
					int nb = Integer.valueOf(brid);
					if(nb>-1){
						return nb;
					}
				}catch(Exception e){
				}
			}
		}catch(IOException e){
		}finally{
			if(input != null){
				try{
					input.close();
				}catch (IOException e) {
				}
			}
		}
		return 0;
	}

	/**
	 * Returns a set of messages ids that passed the current events filter
	 * @param vueData
	 * @return
	 */
	public Set<Integer> getMessagesIds(VueData vueData, AFiltreComposant filter) {
		Set<Integer> retIdsSet = new HashSet<Integer>();
		if(vueData == null)
			return retIdsSet;
		//Use a hashset of names in order to perform searches much faster
		//rather than to make strings comparisons
		Set<String> evNamesSet = getCurFilterNomsEvenements(filter);
		if(evNamesSet.size() == 0)
			return retIdsSet;
		
		Message msg;
		Evenement event;
		AParcoursComposant dataTable = vueData.getDataTable();
		Enregistrement e = dataTable.getEnregistrement();
		int msgsCount = e.getGoodMessagesCount();
		for (int k = 0; k < msgsCount; k++) {
			msg = e.getEnfant(k);
			event = msg.getEvenement();
			if(event == null || !evNamesSet.contains(event.getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom()))
				continue;
			retIdsSet.add(msg.getMessageId());
		}
		return retIdsSet;
	}

	public Set<String> getCurFiltreNomsVars(AFiltreComposant filter) {
		Set<String> retSet = new HashSet<String>();
		if(filter != null) {
			AFiltreComposant varFiltres;
			try {
				varFiltres = filter.getEnfant(1);
			} catch (RuntimeException e) {
				varFiltres = filter.getEnfant(0);
			}
			int varFiltresCount = varFiltres.getEnfantCount();
			for(int i = 0; i<varFiltresCount; i++) {
				retSet.add(varFiltres.getEnfant(i).getNom());
			}
		}
		return retSet;
	}

	private Set<String> getCurFilterNomsEvenements(AFiltreComposant filter) {
		Set<String> retSet = new HashSet<String>();
		if(filter != null) {
			AFiltreComposant evFiltres = filter.getEnfant(0);
			int evFiltresCount = evFiltres.getEnfantCount();
			for(int i = 0; i<evFiltresCount; i++) {
				retSet.add(evFiltres.getEnfant(i).getNom());
			}
		}
		return retSet;
	}

	/**
	 * Returns the map from the variable filter name to the variable filter object
	 * @param filter
	 * @return
	 */
	public Map<String, AFiltreComposant> getFiltreNomsVars(AFiltreComposant filter) {
		//we use a linked hash map in order to preserve the order
		Map<String, AFiltreComposant> retSet = new LinkedHashMap<String, AFiltreComposant>();
		if(filter != null) {
			int varFiltresCount = filter.getEnfantCount();
			AFiltreComposant lineFilter;
			for(int i = 0; i<varFiltresCount; i++) {
				lineFilter = filter.getEnfant(i);
				retSet.put(lineFilter.getNom(), lineFilter);
			}
		}
		return retSet;
	}
}

