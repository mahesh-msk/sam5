package com.faiveley.samng.principal.ihm.vues.configuration;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.IVbvChangeListener;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.parseurs.ParseurConfigurationVueListe;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * @author olivier
 * @version 1.0
 * @created 06-d�c.-2007 11:23:49
 */
public class GestionnaireVueListeBase extends AGestionnaireConfigurationVue 
implements IRepereChangedListener, IVbvChangeListener {
	public static final int 	POS_FLAG 		= 0;
	public static final int 	POS_TIME		= 1;

	/**
	 * Strings to be displayed in the fixed columns
	 */
	public static final String FLAG_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.1"); //$NON-NLS-1$
	public static final String TIME_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.2"); //$NON-NLS-1$
	public static final String REL_TIME_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.3"); //$NON-NLS-1$
	public static final String REL_DIST_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.4"); //$NON-NLS-1$
	public static final String DIST_COR_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.5"); //$NON-NLS-1$
	public static final String TIME_COR_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.6"); //$NON-NLS-1$
	public static final String ACC_DIST_COL_NAME_STR = Messages.getString("GestionnaireVueListeBase.7");

	public static final String EVENT_COL_NAME_STR = Messages.getString("GestionnaireVueListe.0"); //$NON-NLS-1$
	public static final String VAR_COL_NAME_PREFIX = Messages.getString("GestionnaireVueListe.1"); //$NON-NLS-1$

	public static final String FLAG_ID = "Flag"; //$NON-NLS-1$
	public static final String TIME_ID = "Absolute Time"; //$NON-NLS-1$
	public static final String REL_TIME_ID = "Relative Time"; //$NON-NLS-1$
	public static final String REL_DIST_ID = "Relative Distance"; //$NON-NLS-1$
	public static final String DIST_COR_ID = "Corrected Distance"; //$NON-NLS-1$
	public static final String TIME_COR_ID = "Corrected Time"; //$NON-NLS-1$

	//configuration taille colonnes
	protected static final int WIDTH_FLAG 		= 65;
	protected static final int WIDTH_TIME		= 150;
	protected static final int WIDTH_DIST		= 130;
	protected static final int WIDTH_EVENT		= 200;
	protected static final int WIDTH_ACC_DIST   = 165;
	protected static final int WIDTH = 100;

	private ParseurConfigurationVueListe parserCfg;

	//protected Set<TypeRepere> displayedReperesTypes = new HashSet<TypeRepere>();
	protected LinkedHashMap<String, ConfigurationColonne> mapConfigurationColonne = new LinkedHashMap<String, ConfigurationColonne>();
	protected boolean isChanged = false;
	private boolean isListAllColumns = true;

	protected List<String> fixedColumns = null;
	protected List<String> notDisplayableVars = new ArrayList<String>(0);
	protected boolean isVbvListener; 

	protected List<String> listReperesRemoved = new ArrayList<String>(0);
	
	private String filenameCurrent="";

	public String getFilenameCurrent() {
		return filenameCurrent;
	}

	public void setFilenameCurrent(String filenameCurrent) {
		this.filenameCurrent = filenameCurrent;
	}

	public GestionnaireVueListeBase() {

		//permet de ne pas afficher certaines varaibles rep�res
		//this.notDisplayableVars.add(TypeRepere.date.getName());
		//add this instance as a repere listener. When a vue is created then this will be removed from listeners
		//bacause the vue has to receive the notification and inform the gestionnaire, otherwise (if both the vue 
		//and the gestionnaire are listening for this events) it is posibile that the gestionnaire to be notified
		//after the vue and in this case the vue will reload the table, but without any changes in the gestionnaire
		addRepereListener();
	}

	protected void setVbvListener(boolean isListener) {
		this.isVbvListener = isListener;
		if(isListener) {
			ActivatorData.getInstance().getProviderVBVs().addVbvListener(this);
			List<VariableVirtuelle> vbvs = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getListeVBV();
			String vbvName;
			for(VariableVirtuelle vbv: vbvs) {
				vbvName = vbv.getDescriptor().getM_AIdentificateurComposant().getNom();
				onVbvAdded(vbvName, vbvName);
			}
		}
	}

	/**
	 * Add this instance as repere listener
	 *
	 */
	private void addRepereListener() {
		System.err.println("addRepereListener(): " + this); //$NON-NLS-1$
		ActivatorData.getInstance().addRepereListener(this);
	}


	/**
	 * Removes this instance from reperes listeners
	 *
	 */
	private void removeRepereListener() {
		System.err.println("removeRepereListener(): " + this); //$NON-NLS-1$
		ActivatorData.getInstance().removeRepereListener(this);
	}

	public void removeRepere(TypeRepere reper) {

		if (reper != null) {
			String nom = null;
			ConfigurationColonne col = null;

			if (reper.compareTo(TypeRepere.tempsCorrigee) == 0) {
				nom = TypeRepere.tempsCorrigee.getName(); //tgl
				if (isFixedColumn(nom)) {
					col = this.mapConfigurationColonne.get(nom);
				}

			} else if (reper.compareTo(TypeRepere.distanceCorrigee) == 0){
				if (GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode())!=null) {
					nom = GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom();
					//nom = c.getName();
					if (isFixedColumn(nom)) {
						col = this.mapConfigurationColonne.get(nom);
					}
				}

			} else if (reper.compareTo(TypeRepere.vitesseCorrigee) == 0){

				if (GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode())!=null) {
//					nom = TypeRepere.vitesseCorrigee.getName();
					nom = GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

					//this.notDisplayableVars.add(nom);
					col = this.mapConfigurationColonne.get(nom);
				}
			}
			
			if (col != null) {
				col.setAffiche(false);
			}
			this.mapConfigurationColonne.remove(reper.getName());
			this.fixedColumns.remove(reper.getName());
			this.listReperesRemoved.add(reper.getName());
		}
	}

	/**
	 * Add a new repere in the vue. 
	 * This can be time corrected, distance corrected, speed corrected
	 *  
	 * @param reper 	the repere to add
	 */
	public void ajouterRepere(TypeRepere reper) {
		String nom = null;
		int index = -1;
		ConfigurationColonne col = null;

		if (reper.compareTo(TypeRepere.tempsCorrigee) == 0) {
			nom = TypeRepere.tempsCorrigee.getName(); //tgl

			if (!isFixedColumn(nom)) {
				//get index of the reper temp
				index = getFixedColumnIndex(TIME_COL_NAME_STR);

				//add it to fixed columns
				col = insertFixedColumn(nom, index + 1);
				//col.setLargeur(WIDTH_TIME);
			} else {
				col = this.mapConfigurationColonne.get(nom);
			}

		} else if (reper.compareTo(TypeRepere.distanceCorrigee) == 0){
			if (GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode())!=null) {
				nom = GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom();
				//nom = c.getName();

				if (!isFixedColumn(nom)) {
					//get index of the reper temp
					index = getFixedColumnIndex(TypeRepere.distanceRelatif.getName());

					if(index == -1){
						nom = TypeRepere.vitesseCorrigee.getName();
						index = getFixedColumnIndex(nom);
					}

					//add it to fixed columns
					col = insertFixedColumn(nom, index + 1);
					//col.setLargeur(WIDTH_DIST);
				} else {
					col = this.mapConfigurationColonne.get(nom);

				}
			}

		} else if (reper.compareTo(TypeRepere.vitesseCorrigee) == 0){
			if (GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode())!=null) {
//				nom = TypeRepere.vitesseCorrigee.getName();
				nom = GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

				//this.notDisplayableVars.add(nom);

				col = this.mapConfigurationColonne.get(nom);

				if(col == null){
					nom = TypeRepere.vitesseCorrigee.getName();
					col = this.mapConfigurationColonne.get(nom);
				}

				if (col == null) {
					//index = getFixedColumnIndex(TypeRepere.distanceRelatif.getName());

					col = new ConfigurationColonne();
					col.setAffiche(true);	
					col.setNom(nom);
					col.setLargeur(WIDTH);

					//add the column configuration
					ajouterColonneConfiguration(col);
				} else {
					//set visible
					col = this.mapConfigurationColonne.get(nom);	
				}
			}
		}
		if (col != null) {
			col.setAffiche(true);
		}
	}


	public void updateRepere(TypeRepere reper) {
		this.listReperesRemoved.remove(reper.getName()); //tgl
		if (!this.mapConfigurationColonne.containsKey(reper.getName())) {
			ajouterRepere(reper);
		}
	}

	/**
	 * Create and add a new fixed column
	 * @param nom	the column name
	 */
	private ConfigurationColonne insertFixedColumn(String nom, int position) {
		ConfigurationColonne colCfg = null;
		if (this.fixedColumns == null) {
			this.fixedColumns = new ArrayList<String>(0);
		} 
		if (!this.fixedColumns.contains(nom)) {

			//if position is in range then insert, otherwise just append
			if (position != -1 && position < this.fixedColumns.size()) {
				this.fixedColumns.add(position, nom);
			} else {
				this.fixedColumns.add(nom);
			}
		}

		if (!this.mapConfigurationColonne.containsKey(nom)){
			colCfg = new ConfigurationColonne();
			colCfg.setAffiche(true);	
			colCfg.setNom(nom);
			try {
				if(nom.equals(FLAG_COL_NAME_STR)) {
					colCfg.setLargeur(WIDTH_FLAG);
				} else if(nom.equals(TIME_COL_NAME_STR)) {
					colCfg.setLargeur(WIDTH_TIME);
				} else if(nom.equals(TypeRepere.tempsRelatif.getName())) {
					colCfg.setLargeur(WIDTH_TIME);
				} else if(nom.equals(TypeRepere.distanceRelatif.getName())) {
					colCfg.setLargeur(WIDTH_DIST);
				} else if (nom.equals(ACC_DIST_COL_NAME_STR)) {
					colCfg.setLargeur(WIDTH_ACC_DIST);
				} else {
					colCfg.setLargeur(WIDTH);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			//add the column configuration
			ajouterColonneConfiguration(colCfg);
		} else {
			colCfg = this.mapConfigurationColonne.get(nom);
		}

		return colCfg;
	}

	/**
	 * Gets the index of a fixed column
	 * @param nom	the name of the column
	 * @return		the index, or -1 if not exists
	 */
	private int getFixedColumnIndex(String nom) {
		int i = 0;
		if (this.fixedColumns != null) {
			for (String col : this.fixedColumns) {
				if (col.equals(nom)) {
					return i;
				}
				i++;
			}
		}
		return -1;
	}

	/**
	 * Check if the column with the given name is in the list of the fixed columns
	 * @param nom	the name
	 * @return		true or false if the column is fixed or not
	 */
	public boolean isFixedColumn(String nom) {
		boolean contains = false;
		ConfigurationColonne col = this.mapConfigurationColonne.get(nom);
		if(col==null && nom.equals(TypeRepere.vitesseCorrigee.getName())){
			col = this.mapConfigurationColonne.get(com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("NomRepere.4"));
		}
		//get the column config by name
		
		if (col != null) {
			if (this.fixedColumns != null) {
				//check if the column is in the fixed columns list
				if (this.fixedColumns.contains(nom)) {
					contains = true;
				}
			}
		}
		return contains;		
	}

	/**
	 * Returns true is the variable is one of the not displayable variables
	 * @param nom	the variable name
	 * @return		true should not be displayed, 
	 * 				false if the variable should be displayed if it's not not filtered
	 */
	public boolean isNotDisplayableVar(String nom) {
		return this.notDisplayableVars.contains(nom);
	}

	public void ajouterColonneConfiguration(ConfigurationColonne cfgCol) {
		if(cfgCol == null || cfgCol.getNom() == null)
			return;
		
		if (this.mapConfigurationColonne.containsKey(cfgCol.getNom())) {
			this.mapConfigurationColonne.remove(cfgCol.getNom());
		}

		this.mapConfigurationColonne.put(cfgCol.getNom(), cfgCol);
		
	}

	public ConfigurationColonne[] getConfigurationColonnes() {
		return this.mapConfigurationColonne.values().toArray(
				new ConfigurationColonne[this.mapConfigurationColonne.size()]);
	}

	public ConfigurationColonne getColonne(String colNom) {
		return this.mapConfigurationColonne.get(colNom);
	}

	public int getAllColumnsCount() {
		return this.mapConfigurationColonne.size();
	}

	public boolean hasHiddenColumns() {
		for(ConfigurationColonne colCfg: this.mapConfigurationColonne.values()) {
			if(!colCfg.isAffiche() 
					&& 
					(			(GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode())!=null && !colCfg.getNom().equals(GestionnairePool.getInstance().getVariable(TypeRepere.distanceCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom())) 
							&& (GestionnairePool.getInstance().getVariable(TypeRepere.tempsCorrigee.getCode())!=null && !colCfg.getNom().equals(GestionnairePool.getInstance().getVariable(TypeRepere.tempsCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom()))
							&& (GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode())!=null && !colCfg.getNom().equals(GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode()).getDescriptor().getM_AIdentificateurComposant().getNom()))
					)
			)
			return true;
		}
		return false;
	}

	/**
	 * Specifies if all columns should be displayed or hidden columns should not be displayed
	 * @return
	 */
	public boolean isListAllColumns() {
		return isListAllColumns;
	}

	/**
	 * Specifies if all columns should be displayed or hidden columns should not be displayed
	 * @param isListAllColumns
	 */
	public void setListAllColumns(boolean isListAllColumns) {
		this.isListAllColumns = isListAllColumns;
	}

	public String[] getAllColumnsNames() {
		return this.mapConfigurationColonne.keySet().toArray(
				new String[this.mapConfigurationColonne.size()]);
	}

	public String getVarNom(DescripteurVariable descrVar) {
		String varNom = null;
		if (descrVar.getNomUtilisateur() != null) {
			varNom = descrVar.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
		}
		if (varNom == null) {
			varNom = descrVar.getM_AIdentificateurComposant().getNom();
		}
		return varNom;
	}

	@Override
	public void setFiltreApplique(String newVal) {
		if(newVal == this.filtreApplique)
			return;
		boolean setNewFilter = false;
		if(newVal != null && !newVal.equals(this.filtreApplique)) {
			setNewFilter = true;
		} else {
			if(!this.filtreApplique.equals(newVal))
				setNewFilter = true;
		}
		if(setNewFilter) {
			super.setFiltreApplique(newVal);
			this.isChanged = true;
		}
	}

	public int getColonneLargeur(String colNom) {
		int largeur = WIDTH;
		ConfigurationColonne colCfg = this.mapConfigurationColonne.get(colNom);
		if(colCfg != null)
			largeur = colCfg.getLargeur();
		return largeur;
	}

	/**
	 * Initializes this gestionnaire from another gestionnaire
	 * 
	 * @param cfg
	 */
	public void init(GestionnaireVueListeBase cfg) {
		//First reset the configuration
		this.isChanged = false;
		this.filtreApplique = null;
		clear();
		this.mapConfigurationColonne.clear();

		//here we must create first the columns from the data and then
		//		check the loaded columns and update them
		//We are using no filter so set filter name to null and isUsingFilter to false
		List<ConfigurationColonne> colCfgs = getColonnes(ActivatorData.getInstance().getVueData(), null, false);

		int size = colCfgs.size();
		for(int i = 0; i < size; i++) {
			ConfigurationColonne colCfg = colCfgs.get(i);
			ajouterColonneConfiguration(colCfg);
		}

		if(cfg != null) {
			this.filtreApplique = cfg.getFiltreApplique();
			this.setUsesShortNames(cfg.usesShortNames());
			ConfigurationColonne[] colsCfg = cfg.getConfigurationColonnes();
			for(ConfigurationColonne colCfg: colsCfg) {
				ajouterColonneConfiguration(colCfg);
			}
			cfg.removeRepereListener();
		}

		//ordonnancement des colonnes dans le gestionnaire de colonnes
		LinkedHashMap<String, ConfigurationColonne> mapTmp = new LinkedHashMap<String, ConfigurationColonne>();
		ConfigurationColonne colonneFlag = this.mapConfigurationColonne.get(FLAG_COL_NAME_STR);
		ConfigurationColonne colonneTempsAbsolu= this.mapConfigurationColonne.get(TIME_COL_NAME_STR);
		ConfigurationColonne colonneTempsCorrige= this.mapConfigurationColonne.get(TIME_COR_COL_NAME_STR);
		ConfigurationColonne colonneTempsRelatif= this.mapConfigurationColonne.get(REL_TIME_COL_NAME_STR);
		ConfigurationColonne colonneDistanceRelative= this.mapConfigurationColonne.get(REL_DIST_COL_NAME_STR);
		ConfigurationColonne colonneDistanceCorrigee= this.mapConfigurationColonne.get(DIST_COR_COL_NAME_STR);
		if(colonneFlag!=null)
		mapTmp.put(colonneFlag.getNom(), colonneFlag);
		if(colonneTempsAbsolu!=null)
		mapTmp.put(colonneTempsAbsolu.getNom(), colonneTempsAbsolu);
		if(colonneTempsCorrige!=null)
		mapTmp.put(colonneTempsCorrige.getNom(), colonneTempsCorrige);
		if(colonneTempsRelatif!=null)
		mapTmp.put(colonneTempsRelatif.getNom(), colonneTempsRelatif);
		if(colonneDistanceRelative!=null)
		mapTmp.put(colonneDistanceRelative.getNom(), colonneDistanceRelative);
		if(colonneDistanceCorrigee!=null)
		mapTmp.put(colonneDistanceCorrigee.getNom(), colonneDistanceCorrigee);
		
		List<ConfigurationColonne> listeColonnesVbv = new ArrayList<ConfigurationColonne>(0);
		for (ConfigurationColonne colonne : this.mapConfigurationColonne.values()) {
			if(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(colonne.getNom())!=null)
				listeColonnesVbv.add(colonne);
			else if(!mapTmp.containsKey(colonne.getNom())&& !colonne.getNom().startsWith("newVariable")){
				mapTmp.put(colonne.getNom(), colonne);
			}
		}
		
		for (ConfigurationColonne colonne : listeColonnesVbv) {
			mapTmp.put(colonne.getNom(), colonne);
		}
		this.mapConfigurationColonne = mapTmp;
	}

	protected boolean fileHasDistanceCorrections(){
		ArrayList<String> chemins = ActivatorData.getInstance().getListRunFileToMultipleExport();
		int nbChemins=chemins.size();
		if (nbChemins>0) {
			for (int i = 0; i < nbChemins; i++) {
				if (GestionnaireCorrection.getInstance().correctionsDistanceExistantes(chemins.get(i)))
					return true;
			}
			return false;
		}else{
			return TableSegments.getInstance().isAppliedDistanceCorrections();
		}
	}

	protected boolean fileHasTimeCorrections(){
		ArrayList<String> chemins = ActivatorData.getInstance().getListRunFileToMultipleExport();
		int nbChemins=chemins.size();
		if (nbChemins>0) {
			for (int i = 0; i < nbChemins; i++) {
				if (GestionnaireCorrection.getInstance().correctionsTempsExistantes(chemins.get(i)))
					return true;
			}
			return false;
		}else{
			return TableSegments.getInstance().isAppliedTempCorrections();
		}
	}

	/**
	 * Initializes the fixed columns configurations
	 * Override this function if you want to add other fixed columns
	 *
	 */
	protected List<String> getFixedColumns() {
		if (this.fixedColumns == null) {
			this.fixedColumns = new ArrayList<String>(0);
		}

		int pos = 0;
		ConfigurationColonne colCfg = insertFixedColumn(FLAG_COL_NAME_STR, pos);
		pos++;

		colCfg = insertFixedColumn(TIME_COL_NAME_STR, pos);
		pos++;

		if(fileHasTimeCorrections()){
			colCfg = insertFixedColumn(TypeRepere.tempsCorrigee.getName()!=null ?
					TypeRepere.tempsCorrigee.getName() : TypeRepere.tempsCorrigee.name()
					, pos); //tgl
			colCfg.setAffiche(true);
			pos++;
		}
		
		colCfg = insertFixedColumn(TypeRepere.tempsRelatif.getName()!=null ?
				TypeRepere.tempsRelatif.getName() : TypeRepere.tempsRelatif.name()
				, pos);
		pos++;
		
		if (!(TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU)) {
			colCfg = insertFixedColumn(ACC_DIST_COL_NAME_STR, pos);
			pos++;
		}

		if (!(TypeParseur.getInstance().getParser()instanceof ParseurParcoursJRU)) {
			colCfg = insertFixedColumn(TypeRepere.distanceRelatif.getName()!=null ?
					TypeRepere.distanceRelatif.getName() : TypeRepere.distanceRelatif.name()
					, pos);
			pos++;
		}		

		if(fileHasDistanceCorrections()){
			colCfg = insertFixedColumn(TypeRepere.distanceCorrigee.getName()!=null ?
					TypeRepere.distanceCorrigee.getName() : TypeRepere.distanceCorrigee.name()
					, pos);
			colCfg.setAffiche(true);	
			pos++;
		}

		return this.fixedColumns;
	}


	protected List<ConfigurationColonne> getFixedColumnsConfiguration() {
		ArrayList<ConfigurationColonne> list = new ArrayList<ConfigurationColonne>(0);
		List<String> fixedColumns = getFixedColumns();
		for (String nom: fixedColumns) {
			ConfigurationColonne col = this.mapConfigurationColonne.get(nom);
			if (col != null) {
				list.add(col);
			}
		}
		list.trimToSize();
		return list;
	}
	/**
	 * This method performs a custom initialization of the columns.
	 * It should be overridden by the classes derived from this one 
	 *
	 */
	protected List<ConfigurationColonne> getColonnes(VueData vueData, 
			String filterName, boolean useFilter) {
		return getFixedColumnsConfiguration();
	}

	/**
	 * Returns the filtered columns for a view taking into account the current
	 * applied filter and ignoring columns that are not displayable
	 * @param vueData
	 * @return
	 */
	public ConfigurationColonne[] getFilteredColumns(VueData vueData) {
		return getFilteredColumns(vueData, this.filtreApplique, false);
	}

	/**
	 * Gets the configured columns specifying additional flags.
	 * 
	 * @param vueData the parcours data 
	 * @param filterName a filter name
	 * @param allColumns specifies if all columns should be added regardless of their display status
	 * @return a list of columns
	 */
	public ConfigurationColonne[] getFilteredColumns(VueData vueData, 
			String filterName, boolean allColumns) {
		List<ConfigurationColonne> firstFilteredColCfgs = getColonnes(vueData, filterName, true);
		List<ConfigurationColonne> toRemoveCols = new ArrayList<ConfigurationColonne>(0);
		boolean supprimerColonneDistanceCorrigee = false;
		if(ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")!=null && !(Boolean)ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")) {
			supprimerColonneDistanceCorrigee = true;
		}
		ConfigurationColonne existingColCfg;
		for(ConfigurationColonne colCfg: firstFilteredColCfgs) {
			existingColCfg = this.mapConfigurationColonne.get(colCfg.getNom());
			if(existingColCfg != null) {
				if(colCfg.getNom().equals(TypeRepere.distanceCorrigee.getName()) && supprimerColonneDistanceCorrigee){
					toRemoveCols.add(colCfg);
				}
				else{
				//if we are not interested in getting all columns
				//and the column is not displayable, it is added to the list of 
				//columns to be removed
				if(!allColumns && !existingColCfg.isAffiche())
					toRemoveCols.add(colCfg);
				else {
					//only the largeur must be set as the name is the same and the 
					//affiche flag is true
					colCfg.setLargeur(existingColCfg.getLargeur());
				}
				}
			} else {
				
				//if the column does not exists, add the column to the configuration
				existingColCfg = new ConfigurationColonne();
				existingColCfg.setAffiche(true);	
				existingColCfg.setNom(colCfg.getNom());
				existingColCfg.setLargeur(WIDTH);

				//add the column configuration
				ajouterColonneConfiguration(existingColCfg);
				
			}
		}
		//remove the non displayable columns (second level filter)
		firstFilteredColCfgs.removeAll(toRemoveCols);
		return firstFilteredColCfgs.toArray(new ConfigurationColonne[firstFilteredColCfgs.size()]);
	}

	public int updateColumnsConfigurations (ConfigurationColonne[] colsCfg) {
		//We must have an array with exactly the same number of column configurations
		if(colsCfg == null || colsCfg.length != this.mapConfigurationColonne.size())
			return -1;
		int i = 0;
		ConfigurationColonne newCfg;
		int changedColumnsCount = 0;
		for(ConfigurationColonne colCfg: this.mapConfigurationColonne.values()) {
			newCfg = colsCfg[i];
			i++;
			if(colCfg.isAffiche() == newCfg.isAffiche() && colCfg.getLargeur() == newCfg.getLargeur())
				continue;
			colCfg.setAffiche(newCfg.isAffiche());
			colCfg.setLargeur(newCfg.getLargeur());
			changedColumnsCount++;
		}
		if(changedColumnsCount > 0)
			this.isChanged = true;

		return changedColumnsCount;
	}

	public void enregistrerConfigurationVue(String vueSufix, String viewName){
		ParseurXML1 p = GestionnairePool.getInstance().getXMLParser();
		String xmlFileName = p != null ? p.getXmlFileName() : null;
		if (xmlFileName != null) {


			File file = new File(xmlFileName);
			String fileName = file.getName();
			int dotPos;
			if((dotPos = fileName.indexOf('.')) != -1)
				fileName = fileName.substring(0, dotPos);
			String fullCfgFileName = RepertoiresAdresses.getConfigurationVues() + fileName + vueSufix;


			this.parserCfg = new ParseurConfigurationVueListe();
			try {
				this.parserCfg.parseRessource(fullCfgFileName);
				this.parserCfg.enregistrerConfigurationVue(this);
			} catch (XmlException e) {
				
			}
			catch (IOException e) {
			}

		}
	}



	protected void loadFromFile(String vueSufix, String viewName) {
		ParseurXML1 p = GestionnairePool.getInstance().getXMLParser();
		String xmlFileName = p != null ? p.getXmlFileName() : null;
		if (xmlFileName != null) {
			File file = new File(xmlFileName);
			String fileName = file.getName();
			int dotPos;
			if((dotPos = fileName.indexOf('.')) != -1)
				fileName = fileName.substring(0, dotPos);
			setFilenameCurrent(fileName);
			String fullCfgFileName = RepertoiresAdresses.getConfigurationVues() + fileName + vueSufix;
			//do not make anything if is the same xml file name
//			if(this.parserCfg != null && fullCfgFileName.equals(this.parserCfg.getLastParsedFileName())) {
//			//Do nothing
//			} else {
			this.parserCfg = new ParseurConfigurationVueListe();
			try {
				this.parserCfg.parseRessource(fullCfgFileName);
			} catch (XmlException e) {
				File del = new File(fullCfgFileName);

				MessageBox msg = new MessageBox(new Shell(),SWT.ICON_ERROR);

				msg.setMessage(Messages.getString("GestionnaireVueListeBase.erreurXmlCfgMessage"));
				msg.setText(Messages.getString("GestionnaireVueListeBase.erreurXmlCfgTitre"));
				msg.open();
				del.delete();
				loadFromFile(vueSufix, viewName);
				return;
			} catch (IOException e) {
				// TODO Bloc catch auto-g�n�r�
			}
			GestionnaireVueListeBase cfgView = (GestionnaireVueListeBase)this.parserCfg.chargerConfigurationVue();			
			cfgView.getAllColumnsNames();
			init(cfgView);
			//}
		}
		//if there are distance or time corrections then the colums should be added to the table
		if (fileHasDistanceCorrections()) {
			ajouterRepere(TypeRepere.distanceCorrigee);
			ajouterRepere(TypeRepere.vitesseCorrigee);
		}
		if (fileHasTimeCorrections()) {
			ajouterRepere(TypeRepere.tempsCorrigee);
		}
	}

	public void checkForSave(String viewName) {

		if(this.parserCfg != null && isChanged) {
//			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().
//			getActiveWorkbenchWindow().getShell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
//			msgBox.setText(viewName + Messages.getString("GestionnaireVueListeBase.9")); //$NON-NLS-1$
//			msgBox.setMessage(Messages.getString("GestionnaireVueListeBase.10") + viewName + "?"); //$NON-NLS-1$ //$NON-NLS-2$
//			int ret = msgBox.open();
//			if(ret == SWT.YES) {
			try {
				this.parserCfg.enregistrerConfigurationVue(this);
			} catch (XmlException e) {
				// TODO Bloc catch auto-g�n�r�
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Bloc catch auto-g�n�r�
				e.printStackTrace();
			}
			this.isChanged = false;
//			}
		}
	}

	public void clear() {
		if (this.mapConfigurationColonne.get(TypeRepere.tempsCorrigee.getName()) != null) { //tgl
			this.mapConfigurationColonne.get(TypeRepere.tempsCorrigee.getName()).setAffiche(false);//tgl
			this.mapConfigurationColonne.remove(TypeRepere.tempsCorrigee.getName());//tgl
		}
		if (this.mapConfigurationColonne.get(TypeRepere.distanceCorrigee.getName()) != null) {//tgl
			this.mapConfigurationColonne.get(TypeRepere.distanceCorrigee.getName()).setAffiche(false);//tgl
			this.mapConfigurationColonne.remove(TypeRepere.distanceCorrigee.getName());//tgl
		}
		if (this.mapConfigurationColonne.get(TypeRepere.vitesseCorrigee.getName()) != null) {//tgl
			this.mapConfigurationColonne.get(TypeRepere.vitesseCorrigee.getName()).setAffiche(false);//tgl
			this.mapConfigurationColonne.remove(TypeRepere.vitesseCorrigee.getName());//tgl
		}
		if(this.fixedColumns != null)
			this.fixedColumns.clear();
		this.fixedColumns = null;
	}

	public static String getDisplayLabelForColumn(ConfigurationColonne column, boolean usesShortNames) {
		String colName;
		if(column!=null) {
			colName = column.getNom();
		} else {
			return null;
		}

		String colText = colName;
		AVariableComposant var = GestionnairePool.getInstance().getVariable(colName);
		if(VitesseLimiteKVBService.VITESSE_LIMITE_KVB_NAME.equals(colName)){
			var = VitesseLimiteKVBService.getInstance().getVariable();
		}
		if(var == null) {
			//if the variable is null, then it might be a VBV
			VariableVirtuelle vbv = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(colName);
			if(vbv != null) {
				colText = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVbvLabel(vbv);
			}
		} else {
			//if a real variable, we extract the user name
			DescripteurVariable varDescr = var.getDescriptor(); 
			if(TIME_COL_NAME_STR.equals(column.getNom())) {
				colText = TIME_COL_NAME_STR;
			} else if(TypeRepere.tempsRelatif.getName().equals(column.getNom())) {
				colText = REL_TIME_COL_NAME_STR;
			} else if(TypeRepere.tempsCorrigee.getName().equals(column.getNom())) { //tgl
				colText = TIME_COR_COL_NAME_STR;
			} else if(!(TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU) && TypeRepere.distanceRelatif.getName().equals(column.getNom())) {
				colText = REL_DIST_COL_NAME_STR;
			} else if(!(TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU) && TypeRepere.distanceCorrigee.getName().equals(column.getNom())) {
				colText = DIST_COR_COL_NAME_STR;
			}else {
				colText = varDescr.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
				AVariableComposant varParent = var.getParent();
				if (usesShortNames && varParent != null && varParent.getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
					String colTextParent = varParent.getDescriptor().getNomUtilisateur()
							.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					
					colText = colText.substring(colTextParent.length() + 1);
				}
			}
			//if an analogic variable, we add also the unit
			if(varDescr instanceof DescripteurVariableAnalogique) {
				DescripteurVariableAnalogique anaVarDescr = (DescripteurVariableAnalogique)varDescr;
				String unite = anaVarDescr.getUnite();
				if(unite != null && !unite.equals("") && !"".equals(unite.trim())&&!colText.equals(TIME_COR_COL_NAME_STR)&& 
						!colText.equals(REL_TIME_COL_NAME_STR)
				) { 
					colText += " (" + unite + ")"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			else if(var instanceof VariableComposite && var.getDescriptor().getM_AIdentificateurComposant().getCode()!=TypeRepere.tempsRelatif.getCode()){
				colText = "[C]"+colText;
			}
		}
		return colText;
	}



	public void onRepereAdded(TypeRepere... reperes) {

		if(reperes != null) {
			for (TypeRepere reper : reperes) {
				//add this reper in the list of colums to be displayed
				updateRepere(reper);				
			}
		}
		// maybe a removal should be made also
	}

	public void onRepereRemoved(TypeRepere... reperes) {
		if(reperes != null) {
			for (TypeRepere reper : reperes) {
				//remove this reper in the list of colums to be displayed
				removeRepere(reper);				
			}
		}

	}

	public void onVbvAdded(String vbvName, String oldVbvName) {
		if(this.isVbvListener) {

			ConfigurationColonne existingColCfg = null;
			if(!vbvName.equals(oldVbvName)) {
				existingColCfg = this.mapConfigurationColonne.get(oldVbvName);
				if(existingColCfg != null) {
					this.mapConfigurationColonne.remove(oldVbvName);
					existingColCfg.setNom(vbvName);
					this.mapConfigurationColonne.put(vbvName, existingColCfg);
				}
			} if(this.mapConfigurationColonne.get(vbvName) == null) {
				//if the column does not exists, add the column to the configuration
				existingColCfg = new ConfigurationColonne();
				existingColCfg.setAffiche(true);	
				existingColCfg.setNom(vbvName);
				existingColCfg.setLargeur(WIDTH);
				//add the column configuration
				ajouterColonneConfiguration(existingColCfg);
			}
		}
	
		try {
			ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().enregistrerVBV();
		} catch (ParseurXMLException e1) {
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(), SWT.YES );
			msgBox.setText(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreurParseurVBV.1"));
			msgBox.setMessage(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreurParseurVBV.2"));
			msgBox.open();
			
		}
	}

	public void onVbvRemoved(String vbvName) {
		if(this.isVbvListener){
			this.mapConfigurationColonne.remove(vbvName);

		}
		try {
			ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().enregistrerVBV();
		} catch (ParseurXMLException e1) {
			MessageBox msgBox = new MessageBox(PlatformUI.getWorkbench().
					getActiveWorkbenchWindow().getShell(), SWT.YES );
			msgBox.setText(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreurParseurVBV.1"));
			msgBox.setMessage(com.faiveley.samng.principal.ihm.actions.fichier.Messages.getString("ErreurParseurVBV.2"));
			msgBox.open();
			
		}
	}

	public boolean isChanged() {
		return isChanged;
	}

	public void setChanged(boolean isChanged) {
		this.isChanged = isChanged;
	}
}