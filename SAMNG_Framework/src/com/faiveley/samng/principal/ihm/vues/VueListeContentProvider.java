package com.faiveley.samng.principal.ihm.vues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueListeContentProvider extends AVueTableContentProvider {
	private static final String errMsgFilterNoEntry = Messages
	.getString("VueListeContentProvider.0"); //$NON-NLS-1$

	private Set<Integer> msgsIdsFilter;

	private Set<String> varNamesFilters;

	private boolean varFilterEnabled;

	private boolean filterAllVariables;

	private boolean isExport = false;

	protected int posColEvent = -1;

	private String filterName;

	private AbstractActivatorVue activatorVue;
	
	private int blockDefaultErrorMsgNumber = 0;

	public VueListeContentProvider(GestionnaireVueListeBase gestionnaire, AbstractActivatorVue activator) {
		this.gestionaireVue = gestionnaire;
		this.activatorVue = activator;
	}

	@Override
	public void setFilter(String filterName) {
		this.filterName = filterName;
		AGestionnaireFiltres filtersMng = activatorVue.getFiltresProvider().getGestionnaireFiltres();
		AFiltreComposant currentFilter = filtersMng.getFiltre(filterName);
		if (filterName != null && currentFilter != null) {
			this.msgsIdsFilter = filtersMng.getMessagesIds(this.data,currentFilter);
			this.varNamesFilters = filtersMng.getCurFiltreNomsVars(currentFilter);
			try {
				this.varFilterEnabled = currentFilter.isChoixVariable();
				//this.varFilterEnabled = currentFilter.getEnfant(1).isFiltrable();
			} catch (RuntimeException e) {

			}
		} else {
			this.msgsIdsFilter = null;
			this.varNamesFilters = null;
			this.varFilterEnabled = false;
		}
		// If the variablesFilter is enabled but we do not have any variables
		// filter
		// then we should filter all variables except the reperes
		this.filterAllVariables = (this.varFilterEnabled && (this.varNamesFilters == null || this.varNamesFilters
				.size() == 0));
	}

	/**
	 * Creates the list of the columns names
	 */
	protected int createColumnNamesList() {
		getColumnNames().clear(); // reset the column names
		if (this.data == null || this.data.getDataTable() == null) {
			return -1;
		}

		String colNom;
		List<String> tableColNames = getColumnNames();
		ConfigurationColonne[] colsCfg = this.gestionaireVue.getFilteredColumns(this.data);
		int i = 0;
		for (ConfigurationColonne colCfg : colsCfg) {
			colNom = colCfg.getNom();
			if (this.gestionaireVue.isFixedColumn(colNom)) {
				this.lastFixedColumn = i;
			}
			tableColNames.add(colCfg.getNom());
			i++;
		}
		// add also the index of the column event name
		this.posColEvent = tableColNames.indexOf(GestionnaireVueListeBase.EVENT_COL_NAME_STR);
		this.columnsIndicesInfos.setPosColEvent(this.posColEvent);
		this.columnsIndicesInfos.setLastFixedColumn(this.lastFixedColumn);

		if (tableColNames.size() == 0) {
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_WARNING | SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(errMsgFilterNoEntry);
			msgBox.open();
		}
		return this.lastFixedColumn;
	}

	public void loadContent(IProgressMonitor monitor) {
		boolean chargerMessagesMarqueur = false;
		ArrayList<Row> list = new ArrayList<Row>();
		AParcoursComposant dataTable = this.data.getDataTable();
		Enregistrement e;
		Message msg = null;
		int messagesCount;
		Evenement event;

		Row row;
		ActivatorData.getInstance().getVp().reset();
		e = dataTable.getEnregistrement();
		messagesCount = e.getGoodMessagesCount();
		
		// Calcul de la distance cumulée
		computeAccumulatedDistance(e, messagesCount);
		
		for (int k = 0; k < messagesCount; k++) {
			if(monitor!=null && monitor.isCanceled())
				break;
			ActivatorData.getInstance().getVp().setValeurProgressBar(k*100/(messagesCount));
			msg = e.getEnfant(k);
			
			if (msg.getError() != null && msg.getError().equals(ErrorType.BlockDefaut)) {
				blockDefaultErrorMsgNumber++;
				continue;
			}
			
			if (this.elements != null && chargerMessagesMarqueur) {
				row = (Row) this.elements[k];
				lastRow = row;
				setFlag(row, msg);
				// add new created row
				list.add(row);
			} else {
				if (filterName != null) {
					try {
						if (msg.getFlag().getLabel().contains("{")) {
							debFlag = true;
							addflag = addflag + "{";
							lastMsg = msg;
						}
						if (msg.getFlag().getLabel().contains("}")) {
							finFlag = true;
							addflag = addflag + "}";
							lastMsg = msg;
						}
					} catch (Exception e1) {
					}
				}
				
				// Check for event filter
				if ((this.msgsIdsFilter != null)
						&& !this.msgsIdsFilter.contains(Integer.valueOf(msg
								.getMessageId()))) {
					continue;
				}

				event = msg.getEvenement();

				// creates a map which stores the <column number, label>
				row = new Row(getColumnNames().size()); // HashMap<Integer,
				// String>();
				row.setData(msg);

				// put the event in its column
				if (this.posColEvent >= 0) {
					String eventName;
					if (event == null) {
						ErrorType errorType = msg.getError();
						switch(errorType) {
							case EventId : 
								eventName = Messages.getString("ErrorTypeEventId") + msg.getErrorInfo();
								break;
							case BadBlock :
								eventName = Messages.getString("ErrorTypeBadBlock");
								break;
							default:
								eventName = Messages.getString("ErrorTypeUnknownErrorType");
								break;
						}
					}
					else {
						if (event.getNomUtilisateur() != null)
							eventName = event.getNomUtilisateur().getNomUtilisateur(
									Activator.getDefault().getCurrentLanguage());
						else
							eventName = event.getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom();
	
						if (eventName == null) {
							eventName = event.getM_ADescripteurComposant().getM_AIdentificateurComposant().getNom();
						}
						// row.setValue(this.posColEvent, eventName);
					}
					setRowValue(row,this.columnNames.get(this.posColEvent),
							this.posColEvent, eventName);
				}

				if (event != null) {
					int varPos = 0;
	
					//affichage ordonné des variables		
					int codeEvenement = event.getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
					DescripteurComposite descComposite= GestionnaireDescripteurs.getMapEvenementVariables().get(codeEvenement);
	
					DescripteurVariable descripteurVariable = null;
					List<AVariableComposant> listeVariableSimpleTmp = null;
					List<VariableComposite> listeVariableComposeeTmp = null;
					List<AVariableComposant> listeVariableChaineDynamique = null;
					List<StructureDynamique> listeStructureDynamiqueTmp = null;
					if (descComposite==null) {
						continue;
					}
					try {
						for(int a =1; a<descComposite.getLength();a++){
							descripteurVariable = GestionnaireDescripteurs.getDescripteurVariable(((DescripteurVariable)descComposite.getEnfant(a)).getM_AIdentificateurComposant().getCode());
	
							if(descripteurVariable.getTypeVariable() == TypeVariable.VAR_ANALOGIC || descripteurVariable.getTypeVariable()==TypeVariable.VAR_DISCRETE){
								listeVariableSimpleTmp = new ArrayList<AVariableComposant>();
								listeVariableSimpleTmp.add(msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode()));
	
								if(msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode())!=null)
									varPos = addRowsForVariables(listeVariableSimpleTmp,row, varPos);
	
							}else if(descripteurVariable.getTypeVariable() == TypeVariable.VAR_COMPOSEE
									|| descripteurVariable.getTypeVariable()==TypeVariable.VAR_COMPLEXE){
								listeVariableComposeeTmp = new ArrayList<VariableComposite>();
								listeVariableComposeeTmp.add((VariableComposite)msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode()));
								if(msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode())!=null)
									varPos = addRowsForVariablesComposed(listeVariableComposeeTmp, row, varPos);
							}else if(descripteurVariable.getTypeVariable()==TypeVariable.CHAINE_DYNAMIQUE){
								listeVariableChaineDynamique = new ArrayList<AVariableComposant>();
								listeVariableChaineDynamique.add(msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode()));
								if(msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode())!=null)
									varPos = addRowsForVariablesChaineDynamique(listeVariableChaineDynamique, row, varPos);
							}else if(descripteurVariable.getTypeVariable()==TypeVariable.STRUCTURE_DYNAMIQUE){
								listeStructureDynamiqueTmp = new ArrayList<StructureDynamique>();
								listeStructureDynamiqueTmp.add((StructureDynamique)msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode()));
								if(msg.getVariable(descripteurVariable.getM_AIdentificateurComposant().getCode())!=null)
									varPos = addRowsForVariablesStructureDynamique(listeStructureDynamiqueTmp, row, varPos);
							}
						}
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}

				// set the time
				setRowTime(row, msg);
								
				// set the corrected distance
				setRowDistanceCorrected(row, msg);

				// set the corrected time
				setRowTimeCorrected(row, msg);

				// set the relative time
				setRowRelativeTime(row, msg);

				// set the relative distance
				setRowRelativeDistance(row, msg);
				
				// set the accumulated distance
				setRowAccumulatedDistance(row, msg, msg.getAccumulatedDistance());
				
				// set the flag
				setFlag(row, msg);
				
				lastRow = row;
				list.add(row);
			}
			if ((debFlag || finFlag) && lastRow != null) {
				if (this.posColFlag != -1) {
					int position = list.indexOf(lastRow);
					String t = lastRow.getValue(this.posColFlag);
					int intflag = -1;
					if (intflag < t.lastIndexOf(">")) {
						intflag = t.lastIndexOf(">");
					}
					if (intflag < t.lastIndexOf("{")) {
						intflag = t.lastIndexOf("{");
					}
					if (intflag < t.lastIndexOf("}")) {
						intflag = t.lastIndexOf("}");
					}
					if (intflag == -1) {
						t = addflag + t.subSequence(0, t.length());
					} else {
						t = t.subSequence(0, intflag) + addflag
						+ t.subSequence(intflag + 1, t.length());
					}
					setRowValue(lastRow, this.columnNames.get(this.posColFlag), this.posColFlag, t);
					// setRowValue(lastRow,
					// this.columnNames.get(this.posColFlag),
					// this.posColFlag,lastRow.getValue(this.posColFlag)+"{");
					debFlag = false;
					finFlag = false;
					list.set(position, lastRow);
				}
			}
		}
		
		if(monitor!=null && monitor.isCanceled())
			list = new ArrayList<Row>();
		
		this.elements = list.toArray(new Object[list.size()]);
		ActivatorData.getInstance().getPoolDonneesVues().put("tabVueListe", this.elements);
		ActivatorData.getInstance().getPoolDonneesVues().put("changeColVueList", new Boolean(false));
		if (this.filterName == null)
			ActivatorData.getInstance().getPoolDonneesVues().put("fullTabVueListe",this.elements);
		ActivatorData.getInstance().getPoolDonneesVues().put("correction", "");
		ActivatorData.getInstance().getPoolDonneesVues().put(new String("modifMarqueurListe"), false);
	}

	private void computeAccumulatedDistance(Enregistrement e, int messagesCount) {
		double segmentAccumulatedDistance = 0.0;
		double totalAccumulatedDistance = 0.0;
		Double initDistance = e.getEnfant(0).getAbsoluteDistance();
		
		/* Only for ATESS enregistrement */
		if (e.getMessages().get(0) instanceof AtessMessage) {
			boolean isInitDistanceFromCpt = false;
			
			/* get init_distance_from_cpt from /ressources/atess/atessDistance.properties */
			FileInputStream inStream = null;
			Properties props = new Properties();
			String atessDistanceProperties = RepertoiresAdresses.atessDistanceProperties;
			
			try {
				inStream = new FileInputStream(new File(atessDistanceProperties));
				props.load(inStream);
				isInitDistanceFromCpt = Boolean.parseBoolean((String) props.get("init_distance_from_cpt"));
			} catch (FileNotFoundException ex) {
				ex.printStackTrace();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		
			if (isInitDistanceFromCpt == true) {
				initDistance = 0.0;
			}
		}
		
		for (int k = 0; k < messagesCount; k++) {
			Message msg = e.getEnfant(k);
			
			BigDecimal bd = new BigDecimal(msg.getAbsoluteDistance());
			bd = bd.setScale(3, BigDecimal.ROUND_HALF_UP);
			
			double absoluteDistance = bd.doubleValue();
			double accumulatedDistance = 0.0;
			
			if (!msg.isErrorMessage()) {
				if (absoluteDistance >= segmentAccumulatedDistance) {
					segmentAccumulatedDistance = absoluteDistance;
					accumulatedDistance = totalAccumulatedDistance + segmentAccumulatedDistance;
				} else {
					totalAccumulatedDistance += segmentAccumulatedDistance;
					segmentAccumulatedDistance = 0.0;

					accumulatedDistance = totalAccumulatedDistance;
				}
			} else {
				accumulatedDistance = totalAccumulatedDistance + segmentAccumulatedDistance;
			}
			
			msg.setAccumulatedDistance(accumulatedDistance - initDistance);
		}
	}

	protected int addRowsForVariables(
			List<? extends AVariableComposant> listVariables, Row currentRow, int currentVarPos) {

		if (this.filterAllVariables) {
			return currentVarPos; // nothing to add in this case
		}

		String varNom;
		int index;
		//LinkedHashMap<Integer, ? extends AVariableComposant> listVars = listVariables;
		List<? extends AVariableComposant> listVars = listVariables;
		if ((listVars != null) && (listVars.size() > 0)) {
			for (AVariableComposant v : listVars) {
				varNom = v.getDescriptor().getM_AIdentificateurComposant().getNom();
				// if it is a fixed column variable don't display it here
				// unless it's temp variable which should be displayed if
				// appears in the message
				// date and temp should be displayed (then also don not check
				// for notDisplayeableVars)
				if (this.gestionaireVue.isFixedColumn(varNom)) { // &&
					// (!varNom.equals(TypeRepere.temps.getName()
					// ))) {
					continue;
				}

				// otherwise check if they pass the filter. If not, do not add
				// it to the row
				if (this.filterAllVariables
						|| (this.varFilterEnabled && ((this.varNamesFilters == null) || !this.varNamesFilters
								.contains(varNom)))) {
					continue;
				}

				// Finally, if all filters were passed or no filter, then add it
				// to the row
				// variables that are related to the event and are in the
				// scrollable table
				index = getColumnIndex(GestionnaireVueListeBase.VAR_COL_NAME_PREFIX
						+ currentVarPos);

				if (index >= 0) {
					setRowValue(currentRow, this.columnNames.get(index), index, getLabelForVariable(v));
				}
				currentVarPos++;
			}
		}
		return currentVarPos;
	}
	protected int addRowsForVariablesComposed(
			List<? extends VariableComposite> listVariables,
			Row currentRow, int currentVarPos) {

		if (this.filterAllVariables) {
			return currentVarPos; // nothing to add in this case
		}

		String varNom;
		int index = 0;
		List<? extends VariableComposite> listVars = listVariables;
		if ((listVars != null) && (listVars.size() > 0)) {
			for (VariableComposite v : listVars) {
				varNom = v.getDescriptor().getM_AIdentificateurComposant().getNom();


				// otherwise check if they pass the filter. If not, do
				// not add it to the row
				if (this.filterAllVariables	|| 
						(this.varFilterEnabled && ((this.varNamesFilters == null) 
								|| !this.varNamesFilters.contains(varNom)))) {
					continue;
				}

				// Finally, if all filters were passed or no filter,
				// then add it
				// to the row
				// variables that are related to the event and are in
				// the
				// scrollable table
				index = getColumnIndex(GestionnaireVueListeBase.VAR_COL_NAME_PREFIX	+ currentVarPos);

				if (index >= 0) {
					setRowValue(currentRow, columnNames.get(index),	index, v.getValeurBruteForVariableComposee());
				}
				currentVarPos++;
//				}
//				}
			}
		}
		return currentVarPos;
	}

	protected int addRowsForVariablesStructureDynamique(
			List<? extends StructureDynamique> listVariables, Row currentRow, int currentVarPos) {

		if (this.filterAllVariables) {
			return currentVarPos; // nothing to add in this case
		}

		String varNom;
		int index = 0;
		List<? extends StructureDynamique> listVars = listVariables;
		if ((listVars != null) && (listVars.size() > 0)) {
			for (StructureDynamique v : listVars) {
				varNom = v.getDescriptor().getM_AIdentificateurComposant().getNom();

				// otherwise check if they pass the filter. If not, do not add it to the row
				if (this.filterAllVariables	|| 
						(this.varFilterEnabled && ((this.varNamesFilters == null) 
								|| !this.varNamesFilters.contains(varNom)))) {
					continue;
				}

				// Finally, if all filters were passed or no filter, then add it to the row variables 
				//that are related to the event and are in the scrollable table
				index = getColumnIndex(GestionnaireVueListeBase.VAR_COL_NAME_PREFIX	+ currentVarPos);
				try {
					if (index >= 0) {
						setRowValue(currentRow, columnNames.get(index),	index, varNom+":"+v.getValeurHexa());
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
				currentVarPos++;
			}
		}
		return currentVarPos;
	}

	protected int addRowsForVariablesChaineDynamique(
			List<? extends AVariableComposant> listVariables,
			Row currentRow, int currentVarPos) {

		if (this.filterAllVariables) {
			return currentVarPos; // nothing to add in this case
		}

		String varNom;
		int index;
		//LinkedHashMap<Integer, ? extends AVariableComposant> listVars = listVariables;
		List<? extends AVariableComposant> listVars = listVariables;
		if ((listVars != null) && (listVars.size() > 0)) {
			for (AVariableComposant v : listVars) {
				for (int i = 0; i < 2; i++) {
					AVariableComposant var;
					ChaineDynamique chaineDynamique = (ChaineDynamique) v;
					if (i==0) {
						//L_TEXT
						varNom=chaineDynamique.getVariableEntete().getDescriptor().getM_AIdentificateurComposant().getNom();
						var=chaineDynamique.getVariableEntete();
					}else{
						//X_TEXT
						AVariableComposant avc = ((VariableComposite)chaineDynamique.getListeTablesSousVariable().get(0)).getEnfant(0);
						if(avc != null){
							varNom = avc.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());							
						}else{
							varNom = "";
						}
						var=v;
					}

					// otherwise check if they pass the filter. If not, do not add
					// it to the row
					if (this.filterAllVariables
							|| (this.varFilterEnabled && ((this.varNamesFilters == null) || !this.varNamesFilters
									.contains(varNom)))) {
						continue;
					}

					// Finally, if all filters were passed or no filter, then add it
					// to the row
					// variables that are related to the event and are in the
					// scrollable table
					index = getColumnIndex(GestionnaireVueListeBase.VAR_COL_NAME_PREFIX	+ currentVarPos);
					if (index >= 0) {
						setRowValue(currentRow, this.columnNames.get(index), index,
								varNom+":"+var.toString());
					}
					currentVarPos++;
				}
			}
		}
		return currentVarPos;
	}

	public boolean isExport() {
		return isExport;
	}

	public void setExport(boolean isExport) {
		this.isExport = isExport;
	}

	@Override
	protected String getIndiceMessage(Message msg, String flagStr) {
		if (flagStr.equals("")) {
			flagStr=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages().getIndiceMessageById(msg.getMessageId()) + (1 - blockDefaultErrorMsgNumber) + "";
		}
		return flagStr;
	}
}
