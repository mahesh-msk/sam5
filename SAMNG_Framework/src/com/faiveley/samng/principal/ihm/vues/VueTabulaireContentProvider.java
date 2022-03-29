package com.faiveley.samng.principal.ihm.vues;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.enregistrement.atess.AtessMessage;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueTabulaireContentProvider extends AVueTableContentProvider {
	
	public final static String COMBO_INDICATOR = "> ";

	protected static final String errMsgFilterNoEntry = Messages
	.getString("VueTabulaireContentProvider.0"); //$NON-NLS-1$

	protected Map<String, AFiltreComposant> varNamesFilters;

	protected String filterName;

	protected boolean addNonDisplayableColumns = false;

	protected Map<String, AVariableComposant> mapNameToPropagatedVariables = new HashMap<String, AVariableComposant>();

	protected boolean isExport = false;

	private AbstractActivatorVue activator;
	
	private int blockDefaultErrorMsgNumber = 0;

	public VueTabulaireContentProvider(GestionnaireVueListeBase gestionnaire, AbstractActivatorVue activator) {
		this.gestionaireVue = gestionnaire;
		this.activator = activator;
	}

	/**
	 * Creates the list of the columns names
	 */
	protected int createColumnNamesList() {
		this.columnNames.clear(); // reset the column names
		if (this.data == null || this.data.getDataTable() == null) {
			return -1;
		}
		String colNom;
		ConfigurationColonne[] colsCfg;
		if (this.addNonDisplayableColumns) {
			// get all possible columns (this is usually used for export)
			colsCfg = this.gestionaireVue.getFilteredColumns(this.data,this.filterName, true);
		} else {
			colsCfg = this.gestionaireVue.getFilteredColumns(this.data);
		}
		int i = 0;

		// initialize the column names from the extracted column configurations
		// and also compute the last fixed column index
		List<String> listeNomsColonne = new ArrayList<String>();
		for (ConfigurationColonne colCfg : colsCfg) {
			colNom = colCfg.getNom();

			if (this.gestionaireVue.isFixedColumn(colNom)) {
				this.lastFixedColumn = i;
			}
			if(!listeNomsColonne.contains(colCfg.getNom())){
				this.columnNames.add(colCfg.getNom());
				this.columnTypeValeur.add(colCfg.getTypeVar());
				listeNomsColonne.add(colCfg.getNom());
			}
			i++;
		}
		// save the position of the last fixed column in the buffer
		this.columnsIndicesInfos.setLastFixedColumn(this.lastFixedColumn);

		if (this.columnNames.size() == 0) {
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_WARNING
					| SWT.OK);
			msgBox.setText(""); //$NON-NLS-1$
			msgBox.setMessage(errMsgFilterNoEntry);
			msgBox.open();
		}
		this.columnNames = ordonateColumnsFromFilter();

		if (ActivatorData.getInstance().getPoolDonneesVues().get(
		"axeDistanceCorrige") != null) {

			if (!(Boolean) ActivatorData.getInstance().getPoolDonneesVues().get(
			"axeDistanceCorrige")) {
				// String strVBV = "";
				List<String> l = new ArrayList<String>();
				for (String colName : columnNames) {
					VariableVirtuelle var = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(
							colName);
					if (var != null) {
						if (isChildCorrection(var)) {
							// strVBV+= "\n" +colName;
							l.add(colName);
						}
					}
				}
				columnNames.removeAll(l);
			}
		}
		return this.lastFixedColumn;
	}

	protected boolean isChildCorrection(VariableVirtuelle vbv) {
		boolean val = false;

		boolean distanceCorrigeePresente = false;
		boolean vitesseCorrigeePresente = false;
		if(GestionnairePool.getInstance()
				.getVariable(TypeRepere.distanceCorrigee.getCode())!=null)
			distanceCorrigeePresente = true;
		if(GestionnairePool.getInstance()
				.getVariable(TypeRepere.vitesseCorrigee.getCode())!=null)
			vitesseCorrigeePresente = true;
		if ((distanceCorrigeePresente && VariableVirtuelle.contenirVariable(vbv, GestionnairePool.getInstance()
				.getVariable(TypeRepere.distanceCorrigee.getCode())))
				|| (vitesseCorrigeePresente && VariableVirtuelle.contenirVariable(vbv, GestionnairePool.getInstance()
						.getVariable(TypeRepere.vitesseCorrigee.getCode())))) {
			val = true;
		} else if (vbv.getVariableCount() > 0) {
			AVariableComposant vbv0 = vbv.getEnfant(0);
			if ((vbv != null) && (vbv0 instanceof VariableVirtuelle)) {
				val = isChildCorrection((VariableVirtuelle) vbv0);
			}

			if ((val == false) && vbv.getVariableCount() > 1) {
				AVariableComposant vbv1 = vbv.getEnfant(1);
				if (vbv1 instanceof VariableVirtuelle) {
					val = isChildCorrection((VariableVirtuelle) vbv1);
				} else {
					val = false;
				}
			}
		}
		return val;
	}

	/**
	 * Ordonates the columns according to the order from filter
	 * 
	 */
	protected List<String> ordonateColumnsFromFilter() {
		List<String> columns;
		if (this.varNamesFilters != null && this.varNamesFilters.size() > 0) {
			columns = new ArrayList<String>(this.columnNames.size());
			if (this.columnNames.size() > 0) {
				List<String> orderedFilterColumnsNames = new ArrayList<String>(
						this.varNamesFilters.keySet());
				// first add in the columns the fixed columns
				for (int i = 0; i < this.columnNames.size(); i++) {
					if (i <= lastFixedColumn)
						columns.add(this.columnNames.get(i)); // we handle
					// only the
					// variables
					// from filter
					else {
						break;
					}
				}
				// now we take the variables names (in the right table)
				for (int i = 0; i < orderedFilterColumnsNames.size(); i++) {
					if (this.columnNames.contains(orderedFilterColumnsNames
							.get(i)))
						columns.add(orderedFilterColumnsNames.get(i));
				}
				// in this moment we have the columns ordered but what if we
				// have some columns that were
				// extracted but are not in the filters???

				if (this.gestionaireVue.getFiltreApplique()!=null && !this.gestionaireVue.getFiltreApplique().equals("defaut")) {
					for (int i = 0; i < this.columnNames.size(); i++) {
						if (!columns.contains(columnNames.get(i)))
							columns.add(columnNames.get(i));
					}
				}
			}
		} else {
			columns = this.columnNames;
		}
		return columns;
	}

	/**
	 * Notifies the content provider that the filter changed.
	 */
	@Override
	public void setFilter(String filterName) {
		AGestionnaireFiltres filtersMng;
		AFiltreComposant filter=null;

		this.filterName = filterName;

		filtersMng = (AGestionnaireFiltres) activator.getFiltresProvider().getGestionnaireFiltres();

		if ((filterName==null || filterName.equals("defaut"))&&filtersMng.getFiltreDefault()!=null) {
			if (filtersMng.getFiltreDefault().getEnfantCount()!=0) {
				filter=filtersMng.getFiltreDefault().getEnfant(0);
			}else{
				filter=null;
			}
		}else{
			filter = filtersMng.getFiltre(filterName);
		}

		if (filterName != null && filter != null) {
			this.varNamesFilters = filtersMng.getFiltreNomsVars(filter);
			if(ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")!=null && !(Boolean)ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige"))
				this.varNamesFilters.remove(TypeRepere.vitesseCorrigee.getName());
		} else {
			this.varNamesFilters = null;
			this.filterName = null; // an invalid filter name or really null
		}
	}

	/**
	 * Loads the content of this provider from the parcours data according to
	 * the configured columns and filter
	 */
	public void loadContent(IProgressMonitor monitor) {
		boolean chargerMessagesMarqueur = false;

		// initializeColumns();
		ArrayList<Row> list = new ArrayList<Row>();
		Message msg = null;
		Row row;
		HashMap<String, RowVariable> mapVariables = new HashMap<String, RowVariable>();
		
		AParcoursComposant dataTable = this.data.getDataTable();

		if(dataTable!=null){
			//int nonRepAddedVarsCnt;
			int columnsCount = this.columnNames.size();
			//Row prevRow = null;
			ActivatorData.getInstance().getVp().reset();				
			Enregistrement e = dataTable.getEnregistrement();
			int msgsCount = e.getGoodMessagesCount();

			computeAccumulatedDistance(e, msgsCount);
			
			for (int k = 0; k < msgsCount; k++) {
				if(monitor!=null && monitor.isCanceled())
					break;
				ActivatorData.getInstance().getVp().setValeurProgressBar(k*100/(msgsCount));
				msg = e.getEnfant(k);

				if (msg.getError() != null && (msg.getError().equals(ErrorType.BlockDefaut))) {
					blockDefaultErrorMsgNumber++;
					continue;
				}
				
				if (msg.getError() != null && msg.getError() == ErrorType.CRC) {
					continue;
				}
				
				if (this.elements != null && chargerMessagesMarqueur) {
					row = (Row) this.elements[k];
					setFlag(row, msg);
					// add new created row
					list.add(row);
				}else{
					//nonRepAddedVarsCnt = 0;
					boolean unevarTrouve = true;
					if(unevarTrouve){
						row = new Row(columnsCount);
						row.setData(msg);
						updateMapVariablesMessage(msg, mapVariables);

						// variables that are related to the event and are in the
						// scrollable table

//						nonRepAddedVarsCnt += updateRowForVariables(msg.getVariablesAnalogique(), row);
//						nonRepAddedVarsCnt += updateRowForVariablesComplexe(msg.getVariablesComplexe(), row);
//						nonRepAddedVarsCnt += updateRowForVariables(msg.getVariablesDiscrete(), row);
//						nonRepAddedVarsCnt += updateRowForVariables(msg.getVariablesVirtuelle(), row);

						// The variables composee are a special case as they are
						// computed dynamically
						// to rows
						//nonRepAddedVarsCnt += updateRowForVariablesComposee(msg,row);

						// If we have no filter applied for this view or if is
						// applied
						// and the
						// number of non reperes variables are greater than 0 then
						// apply
						// add this row
						// : I think the correct version is (nonRepAddedVarsCnt > 0
						// && nonRepAddedVarsCnt == columnsCount - lastFixedColumn -
						// 1)
						// instead of nonRepAddedVarsCnt > 0

						if (filterName != null) {
							try {
								if (msg.getFlag()!=null) {
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
								}
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}

						boolean matchFilters = true;
						boolean isAtLeastOneValorisee = true;
						if (this.varNamesFilters != null) {
							matchFilters = matchFiltersMap(mapVariables, msg);
							isAtLeastOneValorisee = checkAtLeastOneValorisee(mapVariables);
						}
						
						if (this.varNamesFilters == null || (matchFilters && isAtLeastOneValorisee)) {

							// set the time
							setRowTime(row, msg);
						
							// set the corrected distance
							setRowDistanceCorrected(row, msg);

							// set the corrected time
							setRowTimeCorrected(row, msg);

							// set the corrected speed
							try {
								setRowSpeedCorrected(row, msg);
							} catch (Exception e2) {
								System.out.println("probl�me vitesse corrig�e");
							}

							// set the relative time
							setRowRelativeTime(row, msg);

							// set the relative distance
							setRowRelativeDistance(row, msg);

							if (msg.equals(lastMsg)) {
								debFlag = false;
								finFlag = false;
							}
							
							// set the accumulated distance							
							setRowAccumulatedDistance(row, msg, msg.getAccumulatedDistance());
							
							// set the flag
							setFlag(row, msg);
							lastRow = row;

							// add new created row
							list.add(row);

							setRowValues(row, mapVariables);
							// propagate values
							//propagateRowValues(row, prevRow);
							//prevRow = row;
						}
					}
				}
			}

			if (this.posColFlag != -1) {
				if ((debFlag || finFlag) && lastRow != null) {
					if (this.posColFlag != -1) {
						int position = list.indexOf(lastRow);
						String t = lastRow.getValue(this.posColFlag);
						if (t != null) {
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
								t = t.subSequence(0, intflag)+addflag+t.subSequence(intflag + 1, t.length());
							}
							setRowValue(lastRow, this.columnNames
									.get(this.posColFlag), this.posColFlag, t);
							// setRowValue(lastRow,
							// this.columnNames.get(this.posColFlag),
							// this.posColFlag,lastRow.getValue(this.posColFlag)+"{");
							debFlag = false;
							finFlag = false;
						}
						list.set(position, lastRow);
					}
				}
			}

			if ((isExport)){
				this.elements = list.toArray(new Object[list.size()]);
				return;
			}

			if(monitor!=null && monitor.isCanceled())
				list = new ArrayList<Row>();
				
			
			this.elements = list.toArray(new Object[list.size()]);

			ActivatorData.getInstance().getPoolDonneesVues().put("correction", "");
			ActivatorData.getInstance().getPoolDonneesVues().put("changeColVueTab",Boolean.valueOf(false));
			ActivatorData.getInstance().getPoolDonneesVues().put("tabVueTabulaire",this.elements);
			ActivatorData.getInstance().getPoolDonneesVues().put(new String("modifMarqueurTab"), false);

			if (this.filterName == null)
				ActivatorData.getInstance().getPoolDonneesVues().put("fullTabVueTabulaire", this.elements);
			ActivatorData.getInstance().getPoolDonneesVues().put("mapPropagatedValues",this.mapNameToPropagatedVariables);
		}
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

	private void updateMapVariablesMessage(Message msg, HashMap<String, RowVariable> mapVariables) {
		// Setting all the variables in the map to non valorisee
		setVariablesToNonValorisee(mapVariables);
		
		updateMapVariables(mapVariables, msg.getVariablesAnalogique());
		updateMapVariables(mapVariables, msg.getVariablesDiscrete());
		List<VariableComplexe> variablesComplexes = msg.getVariablesComplexe();
		updateMapVariablesComplexes(mapVariables, variablesComplexes);
		updateMapVariables(mapVariables, msg.getVariablesComposee());
		updateMapVariables(mapVariables, msg.getVariablesVirtuelle());
		updateMapVariablesDynamiques(mapVariables, msg.getStructuresDynamique());
		updateMapVariablesDynamiques(mapVariables, msg.getTableauxDynamique());
		updateMapVariablesDynamiques(mapVariables, msg.getChainesDynamique());
	}

	private void updateMapVariablesDynamiques(
			HashMap<String, RowVariable> mapVariables,
			List<? extends AVariableComposant> variablesDynamiques) {
		for (AVariableComposant var : variablesDynamiques) {
			updateMapSingleVariableDynamique(mapVariables, var);
		}
	}

	private void updateMapSingleVariableDynamique(
			HashMap<String, RowVariable> mapVariables, AVariableComposant var) {
		if (var instanceof VariableDiscrete || var instanceof VariableAnalogique) {
			String varName = var.getDescriptor().getM_AIdentificateurComposant().getNom();
			if (!mapVariables.containsKey(varName)) {
				RowVariable rowVar = new RowVariable(var, true);
				mapVariables.put(varName, rowVar);
			} else {
				RowVariable rowVar = mapVariables.get(varName);
				rowVar.setVar(var);
				rowVar.setValorisee(true);
			}
		} else if (var instanceof VariableDynamique) {
			VariableDynamique varDyn = (VariableDynamique)var;
			updateMapSingleVariableDynamique(mapVariables, varDyn.getVariableEntete());
			AVariableComposant[] enfants = varDyn.getEnfants();
			if (enfants != null) {
				updateMapVariablesDynamiques(mapVariables, Arrays.asList(enfants));
			}
		} else if (var instanceof VariableComplexe) {
			VariableComplexe varComplexe = (VariableComplexe)var;
			AVariableComposant[] enfants = varComplexe.getEnfants();
			if (enfants != null) {
				updateMapVariablesDynamiques(mapVariables, Arrays.asList(enfants));
			}
		}
	}

	private void setVariablesToNonValorisee(
			HashMap<String, RowVariable> mapVariables) {
		for(Entry<String, RowVariable> entry : mapVariables.entrySet()) {
			RowVariable rowVar = entry.getValue();
			rowVar.setValorisee(false);
		}
	}

	private void updateMapVariables(HashMap<String, RowVariable> mapVariables, List<? extends AVariableComposant> listVariables) {
		if (listVariables == null) {
			return;
		}
		
		for(AVariableComposant var : listVariables) {
			updateMapSingleVariable(mapVariables, var);
		}
	}
	
	private void updateMapSingleVariable(HashMap<String, RowVariable> mapVariables, AVariableComposant var) {
		String varName = var.getDescriptor().getM_AIdentificateurComposant().getNom();
		RowVariable rowVar = mapVariables.get(varName);
		if (rowVar != null) {
			rowVar.setVar(var);
			rowVar.setValorisee(true);
		}
		else {
			rowVar = new RowVariable(var, true);
			mapVariables.put(varName, rowVar);
		}
	}
	
	private void updateMapVariablesComplexes(HashMap<String, RowVariable> mapVariables, List<? extends VariableComposite> listVariablesComplexes) {
		if (listVariablesComplexes == null) {
			return;
		}
		
		for(VariableComposite var : listVariablesComplexes) {
			updateMapVariableComplexe(mapVariables, var);
		}
	}
	
	private void updateMapVariableComplexe(
			HashMap<String, RowVariable> mapVariables, VariableComposite var) {
		for(AVariableComposant subVar : var.getEnfants()) {
			if (subVar instanceof VariableComposite) {
				updateMapVariableComplexe(mapVariables, (VariableComposite)subVar);
			}
			else {
				updateMapSingleVariable(mapVariables, subVar);
			}
		}
	}

	private boolean matchFiltersMap(HashMap<String, RowVariable> map, Message message) {
		int nbFiltersMatch = 0;
		for(Entry<String, AFiltreComposant> filterEntry : this.varNamesFilters.entrySet()) {
			AFiltreComposant filter = filterEntry.getValue();
			// If we do not have an operator for this filter then it matches
			if(filter.getEnfantCount()==0) {
				nbFiltersMatch++;
			}
			else {
				// If we have an operator but it is a null or empty operator
				// or there is no value specified then it matches
				String operator = filter.getEnfant(0).getNom();
				String filterOpValue = filter.getEnfant(1).getNom();
				if (operator == null || "".equals(operator.trim())
						|| filterOpValue == null || "".equals(filterOpValue.trim())) {
					nbFiltersMatch++;
				}
				else {
					// We search for the filter variable inside our map
					RowVariable rowVar = map.get(filter.getNom());
					if (rowVar != null) {
						DescripteurVariable descVar = rowVar.getVar().getDescriptor();
						boolean isVolatile = descVar.isVolatil();
						boolean matchesFilter = false;
						if (!isVolatile) {
							// If the variable matches the filter criteria
							matchesFilter = matchesVariableValueWithFilterValue(rowVar, operator, filterOpValue);
						} else {
							String[] values = VariableExplorationUtils.getValuesFromMessage(message, filter.getNom());
							matchesFilter = matchesVariableVolatileValuesWithFilterValue(values, operator, filterOpValue);
						}
						if (matchesFilter) {
							nbFiltersMatch++;
						}
					}
				}
			}
		}
		
		return nbFiltersMatch == this.varNamesFilters.size();
	}
	
	private boolean matchesVariableVolatileValuesWithFilterValue(String[] values, String operator, String filterOpValue) {
		for (String val : values) {
			boolean matches = FilterUtils.matchesVariableVolatileValueWithFilterValue(val, operator, filterOpValue);
			if (matches) {
				return true;
			}
		}
		
		return false;
	}

	private boolean matchesVariableValueWithFilterValue(RowVariable rowVar, String operator, String filterOpValue) {
		char opVal = operator.charAt(0);
		AVariableComposant var = rowVar.getVar();
		boolean result = false;
		switch (opVal) {
			case '=':
				result = var.compareValueWithStringValue(filterOpValue) == 0;
				break;
			case '\u2260': // !=
				result = var.compareValueWithStringValue(filterOpValue) != 0;
				break;
			case '>':
				result = var.compareValueWithStringValue(filterOpValue) > 0;
				break;
			case '\u2265': // >=
				result = var.compareValueWithStringValue(filterOpValue) >= 0;
				break;
			case '<':
				if (operator.length() == 2 && operator.charAt(1) == '<') { 
					// we have <<
					int dotsIdx = filterOpValue.indexOf("..");
					if (dotsIdx == -1) { // force displaying if such an invalid value
						return true;
					}
					String firstIntevalValue = filterOpValue.substring(0, dotsIdx);
					if (var.compareValueWithStringValue(firstIntevalValue) < 0)
						return false;
					String secondIntevalValue = filterOpValue
					.substring(dotsIdx + 2);
					if (var.compareValueWithStringValue(secondIntevalValue) > 0)
						return false;
				} else { // we have a simple <
					result = var.compareValueWithStringValue(filterOpValue) < 0;
				}
				break;
			case '\u2264': // <=
				result = var.compareValueWithStringValue(filterOpValue) <= 0;
				break;
			default: result = true;
				break;
		}
		return result;
	}
	
	private boolean checkAtLeastOneValorisee(
			HashMap<String, RowVariable> mapVariables) {
		for(Entry<String, AFiltreComposant> filterEntry : this.varNamesFilters.entrySet()) {
			AFiltreComposant filter = filterEntry.getValue();
			RowVariable rowVar = mapVariables.get(filter.getNom());
			if (rowVar != null && rowVar.isValorisee()) {
				return true;
			}
		}

		return false;
	}
	
	private void setRowValues(Row currentRow, HashMap<String, RowVariable> mapVariables) {
		int rowCellsCount = currentRow.getNbData();
		
		for(int i = 0; i < rowCellsCount; i++) {
			String columName =  this.columnNames.get(i);
			if (currentRow.getValue(i) == null) {
				RowVariable rowVar = mapVariables.get(columName);
				if (rowVar != null) {
					String value = null;
					if (rowVar.getVar().getDescriptor().isVolatil()) {
						if (rowVar.isValorisee()) {
							String[] values = VariableExplorationUtils.getValuesFromMessage((Message) currentRow.getData(), columName);
							AFiltreComposant varNameFilter = varNamesFilters.get(columName);
							List<String> filteredValues = FilterUtils.matchingValues(values, varNameFilter);
							if (!filteredValues.isEmpty()) {
								value = filteredValues.get(0);
							}
							currentRow.setVolatileSeveralValues(i, filteredValues.size() > 1); // Vaut vrai si la variable volatile a plusieurs valorisations pour cet �v�nement
						} else {
							value = getLabelForVariable(rowVar.getVar());
						}
					} else {
						value = getLabelForVariable(rowVar.getVar());
						currentRow.setVolatileSeveralValues(i, false);
					}
					setRowValue(currentRow, columName, i, value);
					if (!rowVar.isValorisee()) {
						currentRow.setCellInvalid(i, true);
					}
				} else {
					setRowValue(currentRow, columName, i, VAR_SANS_VALEUR);
					currentRow.setCellInvalid(i, true);
				}
			}
		}
	}

	/**
	 * Update the current row from the given variables list
	 * 
	 * @param listVariables
	 * @param currentRow
	 * @return the number of variables added to the given row
	 */
//	protected int updateRowForVariables(
//			List<? extends AVariableComposant> listVariables,
//			Row currentRow) {
//		int nonRepAddedVarsCnt = 0;
//		if (listVariables != null && listVariables.size() > 0) {
//			for (AVariableComposant v : listVariables) {
//				// if we have reperes, then add them to their corresponding
//				// position
//				// otherwise check if they pass the filter
//				String nom = v.getDescriptor().getM_AIdentificateurComposant()
//				.getNom();
//
//				// should not be a fixed column. The notDisplayableVars don't
//				// have a column created,
//				// then it's not necesarry to test for them. Also if we test to
//				// not set the notDisplayableVars
//				// we can have some problems in propagating the values
//				if (this.gestionaireVue.isFixedColumn(nom)) {
//					continue;
//				}
//
//				if (updateRowForVariable(v, currentRow)) {
//					nonRepAddedVarsCnt++;
//				}
//			}
//		}
//		return nonRepAddedVarsCnt;
//	}

	/**
	 * Updates the current row from the list of the given complex variables
	 * 
	 * @param listVariables
	 * @param currentRow
	 * @return the number of variables added to the given row
	 */
//	protected int updateRowForVariablesComplexe(
//			List<? extends VariableComposite> listVariables,
//			Row currentRow) {
//		int nonRepAddedVarsCnt = 0;
//		if (listVariables != null && listVariables.size() > 0) {
//			for (VariableComposite v : listVariables) {
//				if (!v.getDescriptor().getM_AIdentificateurComposant().getNom().equals(TypeRepere.tempsRelatif.getName())) {
//					for (int i = 0; i < v.getVariableCount(); i++) {
//						if (v.getEnfant(i).getDescriptor().getType() != Type.reserved) {
//							if (updateRowForVariable(v.getEnfant(i), currentRow)) {
//								nonRepAddedVarsCnt++;
//							}
//						}
//					}
//				}
//			}
//		}
//		return nonRepAddedVarsCnt;
//	}

	/**
	 * Second version of updateRowForVariablesComposee Normally, only one of
	 * them should be kept ... this one is not looking in a message but instead
	 * it looks in the propagated values of the subvariables that may appear in
	 * a row
	 * 
	 * Updates the row with the variables composee that might be associated with
	 * the given message. Normally, variables composee are associated with a
	 * message if the message contains at least one variable contained in the
	 * variable composee. Though, variables composee are added to the row if
	 * they pass the filter and if the message contain all the variables from
	 * the variable composee.
	 * 
	 * @param msg
	 * @param currentRow
	 * @return
	 */
//	protected int updateRowForVariablesComposee(Message msg, Row currentRow) {
//		int nonRepAddedVarsCnt = 0;
//		List<AVariableComposant> varComposee = GestionnaireVariablesComposee
//		.checkForVariablesComposee(msg);
//		LabelValeur valueLabel;
//		String varNom;
//
//		// check all variables composee for this message
//		for (AVariableComposant var : varComposee) {
//			valueLabel = null;
//			// For the variables composee the filtering is made separately here
//			// in order to avoid a lot of other computations
//			if (this.varNamesFilters != null) {
//				String variableName = var.getDescriptor()
//				.getM_AIdentificateurComposant().getNom();
//				AFiltreComposant lineFilter = this.varNamesFilters
//				.get(variableName);
//				if (lineFilter == null)
//					continue; // filtered variable
//				// If we do not have an operator for this filter then display
//				// the variable
//				String operator = lineFilter.getEnfant(0).getNom();
//				if (operator != null && !"".equals(operator.trim())) { //$NON-NLS-1$
//					String filterOpValue = lineFilter.getEnfant(1).getNom();
//					if (filterOpValue != null
//							&& !"".equals(filterOpValue.trim())) { //$NON-NLS-1$
//						// compute the current value label
//						valueLabel = getLabelForVariableComposee(
//								(VariableComposite) var, msg);
//						if (valueLabel == null) {
//							continue; // nothing to update here or to add
//							// (variable filtered)
//						}
//						boolean isFiltered = false;
//						char opVal = operator.charAt(0);
//						switch (opVal) {
//						case '=':
//							if (!filterOpValue.equals(valueLabel.getValeurs()))
//								isFiltered = true;
//							break;
//						case '\u2260': // !=
//							if (filterOpValue.equals(valueLabel.getValeurs()))
//								isFiltered = true;
//							break;
//						}
//						if (isFiltered) // the value for this variable did not
//							// passed the filter
//							continue;
//					}
//				}
//			}
//			// if we do not have a filter applied and we got here we have this
//			// value null so we must compute it
//			if (valueLabel == null)
//				valueLabel = getLabelForVariableComposee(
//						(VariableComposite) var, msg);
//			// if it is not null
//			if (valueLabel != null) {
//				varNom = var.getDescriptor().getM_AIdentificateurComposant()
//				.getNom();
//				int index = getColumnIndex(varNom);
//				if (index >= 0) {
//					setRowValue(currentRow, varNom, index, valueLabel
//							.getLabel());
//					// currentRow.setValue(index, valueLabel.getLabel());
//					currentRow.setCellData(index, var);
//					nonRepAddedVarsCnt++;
//				}
//			}
//		}
//		return nonRepAddedVarsCnt;
//	}

	/**
	 * Updates the current row with the value of the given variable
	 * 
	 * @param var
	 * @param currentRow
	 * @return
	 */
//	protected boolean updateRowForVariable(AVariableComposant var, Row currentRow) {
//		boolean ret = false;
//
//		// save the last value for propagation
//		String varNom = var.getDescriptor().getM_AIdentificateurComposant()
//		.getNom();
//		//this.mapNameToPropagatedVariables.put(varNom, var);
//
//		// otherwise check if they pass the filter. If not, do not add it to the
//		// row
//		if (checkFilterForVariable(var)) {
//			// Finally, if all filters were passed or no filter, then add it to
//			// the row
//			// variables that are related to the event and are in the scrollable
//			// table
//			int index = getColumnIndex(varNom);
//			if (index >= 0) {
//
//				setRowValue(currentRow, varNom, index, getLabelForVariable(var));
//				currentRow.setCellData(index, var);
//				ret = true;
//			}
//		}
//		return ret;
//	}

	/**
	 * Checks a variable to see if it passes the current applied filter. If a
	 * true value is returned then the variable passed the filter and it should
	 * be displayed. Variables composee do not use this function but instead the
	 * filter is checked when the value is added in the row
	 * 
	 * 
	 * @param variable
	 *            the variable to be checked if it passes the filter
	 * @return true if the variable is not filtered and it should be displayed
	 */
//	protected boolean checkFilterForVariable(AVariableComposant variable) {
//		if (this.varNamesFilters == null)
//			return true;
//		// First check the filter for variable name
//		String variableName = variable.getDescriptor()
//		.getM_AIdentificateurComposant().getNom();
//
//		AFiltreComposant lineFilter = this.varNamesFilters.get(variableName);
//
//		if(lineFilter ==null && variableName.equals(TypeRepere.vitesseCorrigee.getName()))
//			lineFilter = this.varNamesFilters.get(Messages.getString("NomRepere.4"));
//
//		if (lineFilter == null)
//			return false;
//		
//		// If we do not have an operator for this filter then display the
//		// variable
//		if(lineFilter.getEnfantCount()==0)
//			return true;
//		String operator = lineFilter.getEnfant(0).getNom();
//		if (operator == null || "".equals(operator.trim())) //$NON-NLS-1$
//			return true;
//		String filterOpValue = lineFilter.getEnfant(1).getNom();
//		if (filterOpValue == null || "".equals(filterOpValue.trim())) //$NON-NLS-1$
//			return true;
//		char opVal = operator.charAt(0);
//		switch (opVal) {
//		case '=':
//			return variable.compareValueWithStringValue(filterOpValue) == 0;
//		case '\u2260': // !=
//			return variable.compareValueWithStringValue(filterOpValue) != 0;
//		case '>':
//			return variable.compareValueWithStringValue(filterOpValue) > 0;
//		case '\u2265': // >=
//			return variable.compareValueWithStringValue(filterOpValue) >= 0;
//		case '<':
//			if (operator.length() == 2 && operator.charAt(1) == '<') { // we
//				// have
//				// <<
//				int dotsIdx = filterOpValue.indexOf(".."); //$NON-NLS-1$
//				if (dotsIdx == -1) { // force displaying if such an invalid
//					// value
//
//					return true;
//				}
//				String firstIntevalValue = filterOpValue.substring(0, dotsIdx);
//				if (variable.compareValueWithStringValue(firstIntevalValue) < 0)
//					return false;
//				String secondIntevalValue = filterOpValue
//				.substring(dotsIdx + 2);
//				if (variable.compareValueWithStringValue(secondIntevalValue) > 0)
//					return false;
//			} else { // we have a simple <
//				return variable.compareValueWithStringValue(filterOpValue) < 0;
//			}
//			break;
//		case '\u2264': // <=
//			return variable.compareValueWithStringValue(filterOpValue) <= 0;
//		default:
//			return true;
//		}
//
//		return true;
//	}

	/**
	 * Returns the label value for a variable (this can be Ouvert/Fermee for
	 * example for a discrete variable)
	 */
	public String getLabelForVariable(AVariableComposant v) {
		return v.toString();
	}

	/**
	 * Sets the corrected time on the time columng of a row from a given message
	 * 
	 * @param row
	 * @param msg
	 */
	protected void setRowSpeedCorrected(Row row, Message msg) {
		String speedStr = VAR_SANS_VALEUR; //$NON-NLS-1$
		if (msg != null) {
			AVariableComposant speedCorigee = msg.getVariable(TypeRepere.vitesseCorrigee.getCode());

			if (speedCorigee != null) {
				// We know that the value of the corrected time is set as string
				speedStr = new String((byte[]) speedCorigee.getValeur());//tagValCor
			}
		}

		// this.posColCorSpeed = -1;
		if (this.posColCorSpeed < 0) {
			AVariableComposant var = GestionnairePool.getInstance().getVariable(TypeRepere.vitesseCorrigee.getCode());
			if (var!=null) {
				String nom = var.getDescriptor().getM_AIdentificateurComposant().getNom();
				this.posColCorSpeed = getColumnIndex(nom);
				this.columnsIndicesInfos.setPosColCorSpeed(this.posColCorSpeed);
			}
		}
		if (this.posColCorSpeed >= 0) {
			setRowValue(row, this.columnNames.get(this.posColCorSpeed),this.posColCorSpeed, speedStr);
			// row.setValue(this.posColCorSpeed, speedStr);
		}
	}

	/**
	 * Specifies if the non displayable column should be ignored or not when the
	 * list of the columns is created. This is useful for the export where all
	 * the columns should be exported
	 * 
	 * @param addNonDisplayableColumns
	 */
	public void setAddNonDisplayableColumns(boolean addNonDisplayableColumns) {
		this.addNonDisplayableColumns = addNonDisplayableColumns;
	}

	/**
	 * Propagates values from the previous row to the current row for the fields
	 * that were not filled
	 * 
	 * @param currentRow
	 * @param prevRow
	 */
	protected void propagateRowValues(Row currentRow, Row prevRow) {

		if (TypeParseur.getInstance().getParser()instanceof ParseurParcoursAtess) {
			propagateRowValuesForATESS(currentRow,prevRow);
		}else{

			int rowCellsCount = currentRow.getNbData();
			Object prevCellData;
			Object curCellData;
			Message msg;

			for (int i = 0; i < rowCellsCount; i++) {
				// get the current cell data
				curCellData = currentRow.getCellData(i);
				msg = (Message) currentRow.getData();

				if (msg.getEvenement().isRuptureAcquisition()) {
					if (allowValuesPropagations(i) && !variableDansMessage(i,msg)) {
						setRowValue(currentRow,this.columnNames.get(i), i, VAR_SANS_VALEUR);
						currentRow.setCellData(i, curCellData);
//						currentRow.setCellInvalid(i, false);
						AVariableComposant var=(AVariableComposant)curCellData;
//						var.setValeurChaine(getValNulle());
						this.mapNameToPropagatedVariables.put(this.columnNames.get(i),var);
					}

					// if we do not have a cell data here
				}else if (curCellData == null) {
					if ((msg.getFlag() != null)
							&& (msg.getFlag().getLabel().contains("{"))) {
						if (allowValuesPropagations(i)) {
							setRowValue(currentRow, this.columnNames.get(i), i,	VAR_SANS_VALEUR);
							currentRow.setCellData(i, curCellData);
						}
					} else {
						// get the cell data for the previous cell
						prevCellData = prevRow != null ? prevRow.getCellData(i)	: null;
						// if we have a previous cell data
						if (prevCellData != null) {
							// propagate the value,set the cell data to the current
							// row
							// cell
							// and mark the cell as invalid
							setRowValue(currentRow, this.columnNames.get(i), i,	prevRow.getValue(i));
							currentRow.setCellData(i, prevCellData);
							currentRow.setCellInvalid(i, true);
						} else {
							if (prevRow != null) {
								// la colonne flag est la seule o� l'on ne doit pas
								// propager de valeur et o� il n'y a pas forc�ment
								// de valeur
								if (i > posColFlag && (prevRow.getValue(i) != null)	&& !prevRow.getValue(i).equals(VAR_SANS_VALEUR)) {
									// get the cell propagated value by variable
									// user name
									prevCellData = this.mapNameToPropagatedVariables.get(this.columnNames.get(i));
									// if we have a previous cell data
									if (prevCellData != null) {
										// propagate the value,set the cell data to
										// the
										// current
										// row cell
										// and mark the cell as invalid
										setRowValue(currentRow,this.columnNames.get(i),i,getLabelForVariable((AVariableComposant) prevCellData));
										currentRow.setCellData(i, prevCellData);
										currentRow.setCellInvalid(i, true);
									}
								} else {
									if (allowValuesPropagations(i)) {
										setRowValue(currentRow, this.columnNames.get(i), i, VAR_SANS_VALEUR); //$NON-NLS-1$
										currentRow.setCellInvalid(i, true);
									}
								}
							} else {
								if (allowValuesPropagations(i)) {
									setRowValue(currentRow,this.columnNames.get(i), i, VAR_SANS_VALEUR); //$NON-NLS-1$
									currentRow.setCellInvalid(i, true);
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean variableDansMessage(int i, Message msg) {
		String nomVar = this.getColumnNames().get(i);
		AVariableComposant variable = GestionnairePool.getInstance().getVariable(nomVar);
		return variable != null && VariableExplorationUtils.getVariable(variable.getDescriptor(), msg) != null;
	}

	/**
	 * Propagates values from the previous row to the current row for the fields
	 * that were not filled
	 * 
	 * @param currentRow
	 * @param prevRow
	 */
	protected void propagateRowValuesForATESS(Row currentRow, Row prevRow) {

		int rowCellsCount = currentRow.getNbData();
		Object prevCellData;
		Object curCellData;
		Message msg;

		for (int i = 0; i < rowCellsCount; i++) {

			// get the current cell data
			curCellData = currentRow.getCellData(i);
			msg = (Message) currentRow.getData();

			if (msg.getEvenement().isRuptureAcquisition()) {			
				if (allowValuesPropagations(i) && !variableDansMessage(i,msg)) {
					setRowValue(currentRow,this.columnNames.get(i), i, getValNulle(i));
					currentRow.setCellData(i, curCellData);
					currentRow.setCellInvalid(i, true);
					AVariableComposant var=(AVariableComposant)curCellData;
//					var.setValeurChaine(getValNulle());
					this.mapNameToPropagatedVariables.put(this.columnNames.get(i),var);
				}
				// if we do not have a cell data here
			}else if (curCellData == null) {

				if ((msg.getFlag() != null)	&& (msg.getFlag().getLabel().contains("{"))) {	
					if (allowValuesPropagations(i)) {
						setRowValue(currentRow, this.columnNames.get(i), i,	VAR_SANS_VALEUR);
						currentRow.setCellData(i, curCellData);
					}

				} else {

					// get the cell data for the previous cell
					prevCellData = prevRow != null ? prevRow.getCellData(i)	: null;
					// if we have a previous cell data
					if (prevCellData != null) {
						// propagate the value,set the cell data to the current
						// row
						// cell
						// and mark the cell as invalid

						setRowValue(currentRow, this.columnNames.get(i), i,	prevRow.getValue(i));
						currentRow.setCellData(i, curCellData);
						currentRow.setCellInvalid(i, true);
					} else if(allowValuesPropagations(i)){
						if (prevRow != null) {
							// la colonne flag est la seule o� l'on ne doit pas
							// propager de valeur et o� il n'y a pas forc�ment
							// de valeur
							if (i > posColFlag && (prevRow.getValue(i) != null) && !prevRow.getValue(i).equals(VAR_SANS_VALEUR)) {		
								// get the cell propagated value by variable
								// user name
								prevCellData = this.mapNameToPropagatedVariables.get(this.columnNames.get(i));
								// if we have a previous cell data
								if (prevCellData != null) {	
									// propagate the value,set the cell data to
									// the
									// current
									// row cell
									// and mark the cell as invalid
									setRowValue(currentRow,this.columnNames.get(i),i,
											getLabelForVariable((AVariableComposant) prevCellData));
									currentRow.setCellData(i, prevCellData);
									currentRow.setCellInvalid(i, true);
								}else{
									setRowValue(currentRow,this.columnNames.get(i),i,getValNulle(i));						//
									////////////////////zzzzzzzzzzzzzzzzzzzzz
									currentRow.setCellData(i, prevCellData);
									currentRow.setCellInvalid(i, true);
								}			
							} else {	
								if (allowValuesPropagations(i)) {
									setRowValue(currentRow, this.columnNames.get(i), i, getValNulle(i));
									currentRow.setCellInvalid(i, true);
								}	
							}
						} else {
							if (allowValuesPropagations(i)) {
								setRowValue(currentRow,this.columnNames.get(i), i, getValNulle(i));
								currentRow.setCellInvalid(i, true);
							}
						}
					}
				}
			}
		}
	}

	public String getValNulle(int i){
//		AVariableComposant var=GestionnairePool.getVariable(this.columnNames.get(i));
		Type typeValeur=this.columnTypeValeur.get(i);
		if (typeValeur!=null) {

			String valeur = null;
			valeur= GestionnairePool.getInstance().getMapNomVariableValeurzeroVariable().get(this.columnNames.get(i));
			if(valeur==null){
				if (typeValeur==Type.string) {
					return "";
				}

				else if(typeValeur==Type.boolean1 || typeValeur==Type.boolean8){
					return AVariableComposant.BOOL_STR_FALSE_VALUE;
				}
				else{
					return "0";
				}
			}else{
				return valeur;
			}
		}
		return "";
	}

	/**
	 * Tells if the propagation of values is allowed for this column. Usually,
	 * columns that do not allow propagation are columns fixed and columns with
	 * corrections (speed, distance, time)
	 * 
	 * @param colNo
	 * @return
	 */
	protected boolean allowValuesPropagations(int colNo) {
		if (colNo > this.lastFixedColumn && colNo != this.posColCorSpeed
				&& colNo != this.posColCorTime && colNo != this.posColCorDist)
			return true;
		return false;
	}

	/**
	 * Get the label for a variable composee for a given message. If all
	 * variables that are in that variable are present in the file then the
	 * values are taken and see what label is matching them
	 * 
	 * @param var
	 * @param message
	 * @return
	 */
	protected LabelValeur getLabelForVariableComposee(VariableComposite var,
			Message message) {
		StringBuilder str = new StringBuilder();
		AVariableComposant subvar;
		AVariableComposant msgSubVar;
		int count = var.getVariableCount();
		for (int i = 0; i < count; i++) {
			subvar = var.getEnfant(i);
			msgSubVar = message.getVariable(subvar.getDescriptor());
			if (msgSubVar == null) {
				// search it in propagated values
				msgSubVar = this.mapNameToPropagatedVariables.get(subvar
						.getDescriptor().getM_AIdentificateurComposant()
						.getNom());
				if (msgSubVar == null)
					return null; // if it is not found in propagated values
			}
			str.append(msgSubVar.getCastedValeur());
			if (i != count - 1)
				str.append(","); //$NON-NLS-1$
		}
		String subVarsValue = str.toString();

		List<LabelValeur> labelValues = Util.getInstance().getLabelsForVariable(var
				.getDescriptor());
		String cmpVal;
		if (labelValues != null) {
			for (LabelValeur valLabel : labelValues) {
				cmpVal = (String) valLabel.getValeurs();
				if (cmpVal.equals(subVarsValue))
					return valLabel;
			}
		}
		return null;
	}

	public boolean isExport() {
		return isExport;
	}

	public void setExport(boolean isExport) {
		this.isExport = isExport;
	}

	@Override
	protected String getIndiceMessage(Message msg, String flagStr) {
		//DR26-3-a CL11
		if (flagStr.equals("")) {
			flagStr=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages().getIndiceMessageById(msg.getMessageId())+(1 - blockDefaultErrorMsgNumber)+"";
		}
		return flagStr;
	}

	public Map<String, AFiltreComposant> getVarNamesFilters() {
		return varNamesFilters;
	}
	
}
