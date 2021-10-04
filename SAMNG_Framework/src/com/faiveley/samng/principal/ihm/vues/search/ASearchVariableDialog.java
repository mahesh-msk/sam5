package com.faiveley.samng.principal.ihm.vues.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Evenement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.TableSousVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;

public abstract class ASearchVariableDialog extends ASearchDialog {

	// constants
	protected static final String NO_VARIABLE = Messages.getString("SearchVariable.9");
	protected static final String ADV_SEARCH = Messages.getString("SearchVariable.10");
	protected static final String NOM = Messages.getString("SearchVariable.6");
	protected static final String OPE = Messages.getString("SearchVariable.7");
	protected static final String VALEUR = Messages.getString("SearchVariable.8");

	public static final String DE=Messages.getString("ASearchVariableDialog.8");
	public static final String VERS=Messages.getString("ASearchVariableDialog.9");
	public static final String TOUSLESCHANGEMENTS=Messages.getString("ASearchVariableDialog.10");
	public static final String VERSAUTREVALEUR=Messages.getString("ASearchVariableDialog.11");

	private static final String[] valuesList = new String[] {
		" ", Messages.getString("ASearchVariableDialog.6"), Messages.getString("ASearchVariableDialog.7") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	private static final String INFOBULLE_COMBO_VAL=Messages.getString("InfoBulleComboVal1")+"\n"
			+Messages.getString("InfoBulleComboVal2")+"\n"
			+Messages.getString("InfoBulleComboVal3");

	// common controls
	protected Combo comboVar = null;

	protected Combo comboOperation = null;

	protected Scrollable textValue = null;

	protected Shell parent = null;

	// flag to say if the last variable was discrete
	protected boolean isLastValueDiscrete = false;

	protected boolean booleanValue = false;

	protected boolean lastSearchVarCombo = false;

	protected Set<String> noValueVariables;

	// map <name of the variable, descriptor of the variable>
	protected LinkedHashMap<String, DescripteurVariable> values = null;
	protected HashMap<TableItem, String> tableitemLongNameTable = new HashMap<TableItem, String>();

	public ASearchVariableDialog(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

	}

	public ASearchVariableDialog(Shell parent, int style) {
		super(parent, style);

	}

	@Override
	protected void createControls(final Shell parentShell) {
		this.parent = parentShell;

		// combo "Name"
		Label label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(20, 25, 94, 13));
		label.setText(NOM);
		label.setToolTipText(NOM);
		createComboVar();


		// combo "Operation"
		label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(290, 25, 83, 13));
		label.setText(OPE);
		label.setToolTipText(OPE);
		createComboOperation();

		// Value
		label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(400, 25, 120, 13));
		label.setText(VALEUR);
		label.setToolTipText(VALEUR);
		this.textValue = new Text(this.parent, SWT.BORDER);
		this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
		this.textValue.setEnabled(false);
		((Text)this.textValue).setText("");
		((Text)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
		////////////////////////////////////////////////////
		if(ASearchVariableDialog.this.values!=null)
			setSelectableValues(ASearchVariableDialog.this.values.keySet().toArray(new String[ASearchVariableDialog.this.values.size()]));

		setAppelant(this.getClass().getName());
		setTypeRecherche("Variable"); //$NON-NLS-1$

		////////////////////////////////////////////////////////////////


		this.variableName = new Text(this.parent, SWT.BORDER);
		this.variableName.setBounds(20, 45, 250, 21);
		this.variableName.setText(""); //$NON-NLS-1$
		this.variableName.setEditable(false);
		this.variableName.setToolTipText(Messages.getString("InfoBulleComboVar"));

		this.selectTextLabel = new Label(this.parent, SWT.NONE);
		this.selectTextLabel.setBounds(20, 80, 250, 12);

		this.selectText = new Text(this.parent, SWT.BORDER);
		this.selectText.setBounds(20, 95, 250, 21);
		this.selectText.setText(""); //$NON-NLS-1$
		this.selectText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				filterText = selectText.getText();
				List<String> filteredValues = filterItems(filterText);
				initTableValues(filteredValues);
			}
		});

		// stockage de la dernière recherche
		if (ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant() + this.getTypeRecherche()) != null)
			this.selectText.setText((String) ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant() + this.getTypeRecherche()));

		this.selectText.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				itemsTable.deselectAll();
			}

			public void focusLost(FocusEvent e) {
			}
		});

		GridData itemsTableLData = new GridData();
		itemsTableLData.verticalAlignment = GridData.FILL;
		itemsTableLData.horizontalAlignment = GridData.FILL;
		itemsTableLData.grabExcessVerticalSpace = true;
		itemsTableLData.grabExcessHorizontalSpace = true;

		this.itemsTable = new Table(this.parent, SWT.BORDER	| SWT.SINGLE | SWT.FULL_SELECTION);
		this.itemsTable.setLayoutData(itemsTableLData);
		this.itemsTable.setHeaderVisible(true);
		this.itemsTable.setBounds(20, 120, 250, 300);

		this.tableColumn = new TableColumn(itemsTable, SWT.NONE);
		//		this.tableColumn.setText(com.faiveley.samng.principal.ihm.vues.search.dialogs.Messages.getString("RechercheDialog.5")); //$NON-NLS-1$
		//		this.tableColumn.setToolTipText((com.faiveley.samng.principal.ihm.vues.search.dialogs.Messages
		//		.getString("RechercheDialog.5")));
		this.tableColumn.setResizable(true);
		this.tableColumn.setAlignment(SWT.CENTER);
		this.tableColumn.setWidth(250);

		initTableValues(this.selectableValuesList); // try an initialization
		// : add a listener for selection to disable the OK button

		this.itemsTable.addMouseListener(new MouseListener() {
			public void mouseDoubleClick(MouseEvent e) {

			}

			public void mouseDown(MouseEvent e) {
				if (e.button == 1) { // left button click
					ASearchVariableDialog.this.selectValue();
					prechargementOperateursValeurs();
				}
			}

			public void mouseUp(MouseEvent e) {
			}
		});

		if (this.filterText != null) {
			// this setText will also fire a modification event
			selectText.setText(this.filterText);
			selectText.setToolTipText((this.filterText));
		}
	}

	public void prechargementOperateursValeurs(){
		//récupération de l'index de la table
		int selIdx = itemsTable.getSelectionIndex();
		if (selIdx < 0 || selIdx >= itemsTable.getItemCount())
			return;
		TableItem tblItem = itemsTable.getItem(selIdx);
		//nom de la variable
		String varName = (String) tblItem.getData(tblItem.getText());

		if (!tableitemLongNameTable.isEmpty()) {
			varName = tableitemLongNameTable.get(tblItem);
		}
		
		//récupération du message
		DescripteurVariable descr = this.values.get(varName);
		final IWorkbenchPart activePart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();

		if (activePart instanceof ISelectionProvider) {
			ISelectionProvider selProvider = (ISelectionProvider) activePart;
			ISelection sel = selProvider.getSelection();
			if (!(sel instanceof MessageSelection))
				return;
			MessageSelection msgSel = (MessageSelection) sel;
			if (msgSel == null || msgSel.isEmpty()) {
				return;
			}

			int msgId = msgSel.getMessageId();
			Message message=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(msgId);

			//récupération de la valeur de la variable
			AVariableComposant var=message.getVariable(descr);

			///////////////////////////////////////////////////////////////////////////

			// get the variable with this name
			//				DescripteurVariable descr = this.values.get(varName);
			if (descr != null && (descr.getTypeVariable() == TypeVariable.VAR_COMPOSEE || descr.getTypeVariable() == TypeVariable.VAR_DISCRETE
					|| descr.getTypeVariable() == TypeVariable.VAR_VIRTUAL )){ 

				this.textValue.dispose();;
				this.textValue = new Combo(this.parent, SWT.NONE);
				((Combo)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
				this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
				Combo combo = (Combo) this.textValue;

				if(descr.getTypeVariable() == TypeVariable.VAR_COMPOSEE){
					List<LabelValeur> labels = Util.getInstance().getLabelsForVariable(descr);
					//We should have at least two values
					boolean isDoublon = false;
					if(labels != null && labels.size() > 1) {
						for (LabelValeur valeur : labels) {
							isDoublon = false;
							//ajout olivier: si l'on veut différencier plusieurs associations de valeurs associées au meme label(ex:UNDEFINED)
							//on doit rajouter les valeurs après le label: UNDEFINED(1,0,0,1), UNDEFINED(0,0,0,1) etc...
							//combo.add(valeur.getLabel()+"(" +(String)valeur.getValeurs() +")");
							//sinon on ne met que le label(ex:UNDEFINED) et la recherche s'arrete dès que le label est trouvé meme si plusieurs associations de valeurs lui sont associées
							for (int i = 0; i < combo.getItemCount(); i++) {
								if (combo.getItem(i).equals(valeur.getLabel())) {
									isDoublon = true;
								}
							}
							if (!isDoublon) {
								combo.add(valeur.getLabel());
							}
						}
					}
				}else if (descr.getTypeVariable() == TypeVariable.VAR_DISCRETE){
					if (((DescripteurVariableDiscrete) descr).getLabels() != null) {
						List<LabelValeur> listeLabels = ((DescripteurVariableDiscrete) descr).getLabels().get(Activator.getDefault().getCurrentLanguage());
						// Ne pas ajouter le label $retirer$ qui permet de retirer une valeur à une variable à l'affichage
						if ((listeLabels != null) && !listeLabels.get(0).getLabel().equals("$retirer$")) {
							combo.add(" "); //$NON-NLS-1$
							for (LabelValeur valeur : listeLabels) {
								if(valeur.getLabel().equals(Messages.getString("ASearchVariableDialog.7")))
									combo.add(new String(valeur.getLabel()).toLowerCase());
								else
									combo.add(valeur.getLabel());
							}
						}
					}
				}
				//si la variable est de type virtuel alors on n'a que deux valeurs possibles:TRUE et false
				else if(descr.getTypeVariable() == TypeVariable.VAR_VIRTUAL){
					for (String value : valuesList) {
						combo.add(value);
					}
				}
				this.parent.layout();
			}else if (descr != null && descr.getTypeVariable() == TypeVariable.VAR_ANALOGIC){
				this.textValue.dispose();
				this.textValue = new Text(this.parent, SWT.BORDER);
				((Text)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
				this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
				this.parent.layout();
			}

			///////////////////////////////////////////////////////////////////////////

			if (var!=null && descr != null) {
				//on a une valeur a affecter : DR26-2 CL-00 

				prechargerCombo(var, descr);
			}else{
				//sinon on peut peut être affecter la date
				if (descr != null && descr.getTypeVariable() == TypeVariable.VAR_ANALOGIC
						&& descr.getM_AIdentificateurComposant()!=null 
						&& descr.getM_AIdentificateurComposant().getNom().equals(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2"))) {
					try {
						long dateLong=message.getAbsoluteTime();
						String date=ConversionTemps.getFormattedDate(dateLong, true);
						if (dateLong>0) {
							//on a une valeur a affecter : DR26-2 CL-00 
							this.comboOperation.select(1);
							//DR26-3-a CL03
							((Text) this.textValue).setText(date);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
					if (descr!=null) {
						//on précharge les valeurs propagées
						AVariableComposant varValorise=null;
						List<Message> allMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
						for (Message msg : allMessages) {
							if (msg.getVariable(descr)!=null) {
								varValorise=msg.getVariable(descr);
							}
							if (msg.getMessageId()>=message.getMessageId()) {
								break;
							}
						}
						if (varValorise!=null) {
							prechargerCombo(varValorise, descr);
						}
					}else{
						this.comboOperation.select(0);
						if (this.textValue instanceof Text) {
							((Text) this.textValue).setText("");
							((Text)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
						}else if(this.textValue instanceof Combo){
							((Combo) this.textValue).setText("");
							((Combo)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
						}
						this.textValue.setEnabled(false);
					}
				}
			}
		}
	}

	public void prechargerCombo(AVariableComposant var, DescripteurVariable descr){
		//select "="
		this.comboOperation.select(1);

		if (descr.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
			((Text) this.textValue).setText(var.getCastedValeur()+"");
			((Text)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
		}else if(descr.getTypeVariable() == TypeVariable.VAR_DISCRETE){
			Combo combo=((Combo) this.textValue);
			
			String val = null ;
			
			// Préremplir la case valeur par celle du message sélectionné
			// Dans le cas des variables NID_XX, il faut retirer, à la valeur affichée, la valeur de 
			// la table de label, ex : "15" -> remove "f" 
			if (((DescripteurVariableDiscrete) descr).getLabels() != null) {
				List<LabelValeur> listeLabels = ((DescripteurVariableDiscrete) descr).getLabels().get(Activator.getDefault().getCurrentLanguage());				
				
				if ((listeLabels != null) && listeLabels.get(0).getLabel().equals("$retirer$")) {			
					val = var.toString() ;
				} else {
					val=var.getCastedValeur()+"";
				}
			}
			else {
				val=var.getCastedValeur()+"";
			}
				
			int nbItems=combo.getItemCount();
			if (nbItems>0) {
				try {
					combo.select(Integer.valueOf(val)+1);
				} catch (NumberFormatException e) {
					combo.setText(val);
				}
			}else{
				combo.setText(val);
			}
			((Combo)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
		}else if(descr.getTypeVariable() == TypeVariable.VAR_COMPOSEE){
			Combo combo=((Combo) this.textValue);
			((Combo)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
			combo.select(Integer.valueOf(var.getCastedValeur()+"")+1);
		}
		this.textValue.setEnabled(true);
	}

	public void selectValue(){
		int selIdx = itemsTable.getSelectionIndex();
		if (selIdx < 0 || selIdx >= itemsTable.getItemCount())
			return;
		TableItem tblItem = itemsTable.getItem(selIdx);
		selectedValue = (String) tblItem.getData(tblItem.getText());
		selectedItem = tblItem;
		variableName.setText(tblItem.getText());
		this.isLastValueDiscrete = updateComboOperation(true);
		////////////////////

		activerBoutonsRecherche();
		
		if (ASearchVariableDialog.this.comboOperation != null) {
			ASearchVariableDialog.this.comboOperation.setEnabled(true);
		}

		////////////////////

		if (ASearchVariableDialog.this.filterText == null)
			setFilterText("");
		ActivatorData.getInstance().getPoolDonneesVues().put(ASearchVariableDialog.this.getAppelant() + ASearchVariableDialog.this.getTypeRecherche(),
				ASearchVariableDialog.this.filterText);

		ActivatorData.getInstance().getPoolDonneesVues().put(ASearchVariableDialog.this.getAppelant() + ASearchVariableDialog.this.getTypeRecherche() + "select",selIdx);
	}

	/**
	 * Returns the size of the dialog
	 */
	protected Point getSize() {
		return new Point(600, 200);
	}

	/**
	 * This method initializes comboVar
	 * 
	 */
	private void createComboVar() {
		this.comboVar = new Combo(this.parent, SWT.NONE);
		this.comboVar.setVisible(false);
		this.comboVar.setBounds(new Rectangle(20, 45, 250, 21));
		fillCombo();

		this.comboVar.addSelectionListener(new ComboSelection());

		// permet de remettre la selection sur <Recherche> si la varaible avait
		// été trouvée par recherche
		this.comboVar.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event arg0) {
				if (lastSearchVarCombo) {
					String[] items = ASearchVariableDialog.this.comboVar.getItems();
					for (int i = 0; i < items.length; i++) {
						if (items[i].equals(ADV_SEARCH)) {
							ASearchVariableDialog.this.comboVar.select(i);
						}
					}
				}
			}
		});
	}

	/**
	 * Fills the combobox of the variables names
	 * 
	 */
	protected void fillCombo() {
		this.comboVar.add(NO_VARIABLE);
		this.comboVar.add(ADV_SEARCH);

		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		if (p == null) {
			return;
		}
		Enregistrement enrg = p.getData().getEnregistrement();
		if (enrg != null) {
			if (this.values == null) {
				this.values = new LinkedHashMap<String, DescripteurVariable>();
			} else {
				this.values.clear();
			}

			//ajout de la variable Temps Absolu DR26-3-a CL01
			String str=com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2");
			DescripteurVariable descr=new DescripteurVariable();
			AIdentificateurComposant comp=new AIdentificateurComposant() {
			};
			descr.setTypeVariable(TypeVariable.VAR_ANALOGIC);
			descr.setM_AIdentificateurComposant(comp);
			comp.setCode(TypeRepere.tempsAbsolu.getCode());
			comp.setNom(str);
			if (!this.values.containsKey(str)) {
				this.comboVar.add(str);
				this.values.put(str, descr);
			}
			///////////////////////////////////			

			for (Message msg : enrg.getMessages()) {
				if (msg.getVariablesAnalogique() != null) {
					addVariablesFiltrees(msg.getVariablesAnalogique());
				}
				if (msg.getVariablesDiscrete() != null) {
					addVariablesFiltrees(msg.getVariablesDiscrete());
				}
				if (msg.getVariablesComplexe() != null) {
					addVariablesComposeeFiltrees(msg.getVariablesComplexe());
				}

				if (msg.getVariablesComposee() != null) {
					addVariablesComposeeFiltrees(msg.getVariablesComposee());
				}
			}
			
			Map<Integer, AVariableComposant> mapVariablesDynamiques = GestionnairePool.getInstance().getVariablesDynamiquesMap();
			addVariablesVolatiles(mapVariablesDynamiques);			
			
			// ajout des variables composées à la liste des variables
			Map<String, AVariableComposant> composeeVars = GestionnairePool.getInstance().getComposeeVariables();
			for (AVariableComposant compVar : composeeVars.values()) {
				addVariable(compVar);
			}
			//			//ajout de la variable Temps Absolu
			//			AVariableComposant tempsAbsolu=GestionnairePool.getVariable(TypeRepere.temps.getCode());
			//			addVariable(tempsAbsolu);
		}
	}

	private void addVariableDuFiltre(AVariableComposant var) {
		if (var==null || var.getDescriptor()==null || var.getDescriptor().getNomUtilisateur()==null || 
				var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage())==null) {
			return;
		}
		String str = var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

		if (str == null) {
			str = var.getDescriptor().getM_AIdentificateurComposant().getNom();
		}
		if(var.getDescriptor().getTypeVariable()==TypeVariable.VAR_COMPOSEE && var.getDescriptor().getM_AIdentificateurComposant().getCode()!=TypeRepere.tempsRelatif.getCode())
			str = "[C]" + str; //$NON-NLS-1$

		if (!this.values.containsKey(str)) {
			//si la variable est dans le filtre applique DR26_3A_CL10
			if (this.getVariablesDuFiltre().contains(var.getDescriptor().getM_AIdentificateurComposant().getNom())) {
				this.comboVar.add(str);
				this.values.put(str, var.getDescriptor());
			}
		}
	}

	/**
	 * Adds a variable to the combo and the list of values
	 * 
	 * @param var
	 */
	protected void addVariable(AVariableComposant var) {
		String str = null;
		if (var==null || var.getDescriptor()==null || var.getDescriptor().getNomUtilisateur()==null || 
				var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage())==null) {
			return;
		}
		str = var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

		if (str == null) {
			str = var.getDescriptor().getM_AIdentificateurComposant().getNom();
		}
		if(var.getDescriptor().getTypeVariable()==TypeVariable.VAR_COMPOSEE && var.getDescriptor().getM_AIdentificateurComposant().getCode()!=TypeRepere.tempsRelatif.getCode())
			str = "[C]" + str; //$NON-NLS-1$

		if (!this.values.containsKey(str)) {
			this.comboVar.add(str);
			this.values.put(str, var.getDescriptor());
		}
	}

	/**
	 * Adds all variables
	 * 
	 * @param c
	 */
	protected void addVariables(Collection<? extends AVariableComposant> c) {
		for (AVariableComposant var : c) {
			addVariable(var);
		}
	}

	/**
	 * Adds variables volatiles
	 * 
	 * @param map
	 */
	protected void addVariablesVolatiles(Map<Integer, AVariableComposant> map) {

		for(Entry<Integer, AVariableComposant> entry : map.entrySet()) {
			//			Integer cle = entry.getKey();
			addVariablesVolatilesDynamique(entry.getValue());
		}
	}
	
	protected void addVariablesVolatilesDynamique(AVariableComposant aVariableComposant) {

		AVariableComposant volat = null;
		if (aVariableComposant instanceof VariableDynamique) {
			VariableDynamique var = (VariableDynamique) aVariableComposant;
			volat=var.getVariableEntete();
			addVariableDuFiltre(volat);
			
			int nbSousVars=var.getListeTablesSousVariable()==null ? 0 : var.getListeTablesSousVariable().size();
			for (int i = 0; i < nbSousVars; i++) {
				addVariablesVolatilesDynamique(var.getListeTablesSousVariable().get(i));
			}
		}else if (aVariableComposant instanceof VariableDiscrete || aVariableComposant instanceof VariableAnalogique) {
			addVariableDuFiltre(aVariableComposant);
		}else if (aVariableComposant instanceof VariableComplexe){
			int nbChildren = ((VariableComplexe)aVariableComposant).getVariableCount();
			for (int i = 0 ; i < nbChildren ; i++){
				addVariablesVolatilesDynamique(((VariableComplexe)aVariableComposant).getEnfant(i));
			}
		}else if (aVariableComposant instanceof TableSousVariable) {
			TableSousVariable tSousVars = (TableSousVariable) aVariableComposant;
			int nbSousVars=tSousVars.getM_AVariableComposant()==null ? 0 : tSousVars.getM_AVariableComposant().size();
			for (int i = 0; i < nbSousVars; i++) {
				addVariablesVolatilesDynamique(tSousVars.getEnfant(i));
			}
		}
	}
	
	/**
	 * Adds variables volatiles
	 * 
	 * @param map
	 */
	protected void addVariablesVolatilesVueListe(Map<Integer, AVariableComposant> map) {

		for(Entry<Integer, AVariableComposant> entry : map.entrySet()) {
			//			Integer cle = entry.getKey();
			addVariablesVolatilesDynamiqueVueListe(entry.getValue());
		}
	}
	
	protected void addVariablesVolatilesDynamiqueVueListe(AVariableComposant aVariableComposant) {

		AVariableComposant volat = null;
		if (aVariableComposant instanceof VariableDynamique) {
			VariableDynamique var = (VariableDynamique) aVariableComposant;
			volat=var.getVariableEntete();
			addVariable(volat);
			
			int nbSousVars=var.getListeTablesSousVariable()==null ? 0 : var.getListeTablesSousVariable().size();
			for (int i = 0; i < nbSousVars; i++) {
				addVariablesVolatilesDynamiqueVueListe(var.getListeTablesSousVariable().get(i));
			}
		}else if (aVariableComposant instanceof VariableDiscrete || aVariableComposant instanceof VariableAnalogique) {
			addVariable(aVariableComposant);
		}else if (aVariableComposant instanceof VariableComplexe){
			int nbChildren = ((VariableComplexe)aVariableComposant).getVariableCount();
			for (int i = 0 ; i < nbChildren ; i++){
				addVariablesVolatilesDynamiqueVueListe(((VariableComplexe)aVariableComposant).getEnfant(i));
			}
		}else if (aVariableComposant instanceof TableSousVariable) {
			TableSousVariable tSousVars = (TableSousVariable) aVariableComposant;
			int nbSousVars=tSousVars.getM_AVariableComposant()==null ? 0 : tSousVars.getM_AVariableComposant().size();
			for (int i = 0; i < nbSousVars; i++) {
				addVariablesVolatilesDynamiqueVueListe(tSousVars.getEnfant(i));
			}
		}
	}

	/**
	 * Adds variables du filtre
	 * 
	 * @param c
	 * @param listVars 
	 */
	protected void addVariablesFiltrees(Collection<? extends AVariableComposant> c) {
		for (AVariableComposant var : c) {
			addVariableDuFiltre(var);
		}
	}

	/**
	 * Adds a complexe variable by adding its subvariables
	 * 
	 * @param c
	 */
	protected void addVariablesComposeeFiltrees(Collection<? extends VariableComposite> c) {
		for (VariableComposite varC : c) {
			addVariableComposeeFiltree(varC);
		}
	}

	/**
	 * Adds a complexe variable by adding its subvariables
	 * 
	 * @param c
	 */
	protected void addVariablesComposee(Collection<? extends VariableComposite> c) {
		for (VariableComposite varC : c) {
			addVariableComposee(varC);
		}
	}

	/**
	 * Adds all complexe variables
	 * 
	 * @param varC
	 */
	protected void addVariableComposeeFiltree(VariableComposite varC) {
		int size = varC.getVariableCount();
		for (int i = 0; i < size; i++) {
			AVariableComposant var = varC.getEnfant(i);
			addVariableDuFiltre(var);
		}
	}

	/**
	 * Adds all complexe variables
	 * 
	 * @param varC
	 */
	protected void addVariableComposee(VariableComposite varC) {
		int size = varC.getVariableCount();
		for (int i = 0; i < size; i++) {
			AVariableComposant var = varC.getEnfant(i);
			addVariable(var);
		}
	}

	/**
	 * This method initializes comboOperation
	 * 
	 */
	private void createComboOperation() {
		this.comboOperation = new Combo(this.parent, SWT.BORDER);
		this.comboOperation.setBounds(new Rectangle(280, 45, 93, 21));
		this.isLastValueDiscrete = updateComboOperation(true);
		this.comboOperation.addSelectionListener(new ComboSelection2());
		this.comboOperation.setEnabled(false);
		this.comboOperation.setToolTipText(
				"=    "+Messages.getString("InfoBulleComboOpe1")
				+"\n\u2260    "+Messages.getString("InfoBulleComboOpe2")
				+"\n\u0394    "+Messages.getString("InfoBulleComboOpe3")
				+"\n>    "+Messages.getString("InfoBulleComboOpe4")
				+"\n\u2265    "+Messages.getString("InfoBulleComboOpe5")
				+"\n<    "+Messages.getString("InfoBulleComboOpe6")
				+"\n\u2264    "+Messages.getString("InfoBulleComboOpe7")
				+"\n< <  "+Messages.getString("InfoBulleComboOpe8"));
	}

	/**
	 * Updates the combobox for operations coresponding to the selected variable
	 * type
	 * 
	 * @param init
	 *            true if it's the initialisation of the combo
	 * @return boolean value to say if the combo was set for a discrete variable
	 */
	protected boolean updateComboOperation(boolean init) {
		boolean isDiscrete = false;
		String varName = this.selectedValue;
		if (varName != NO_VARIABLE && varName != ADV_SEARCH && this.values != null) {

			// get the variable with this name
			DescripteurVariable descr = this.values.get(varName);
			if (descr != null
					&& ((descr.getTypeVariable() == TypeVariable.VAR_DISCRETE && (descr.getType() == Type.boolean1 || descr.getType() == Type.boolean8)) 
							|| descr.getType() == Type.string 
							|| descr.getTypeVariable() == TypeVariable.VAR_VIRTUAL 
							//					|| descr.getType() == Type.uint8 .... 
							|| TypeVariable.VAR_COMPOSEE == descr.getTypeVariable())) {

				if (init || (!this.isLastValueDiscrete)) {
					this.comboOperation.removeAll();
					for (Operation o : EnumSet.range(Operation.NoOperation,	Operation.NotEqual)) {
						this.comboOperation.add(o.value());
					}
					//on ajoute l'opérateur changement si c'est une variable discrète
					if (descr.getTypeVariable() == TypeVariable.VAR_DISCRETE) {
						this.comboOperation.add(Operation.Change.value());
					}
				}
				isDiscrete = true;
				booleanValue = true;
			} else {
				if (init || this.isLastValueDiscrete) {
					this.comboOperation.removeAll();

					///////////////
					for (Operation o : EnumSet.range(Operation.NoOperation,Operation.ShiftLeft)) {
						this.comboOperation.add(o.value());
					}
					

					//////////////////////
					//on ajoute l'opérateur changement si c'est une variable discrète
					if (descr!=null){ 
						if (descr.getTypeVariable() == TypeVariable.VAR_DISCRETE) {
							this.comboOperation.add(Operation.Change.value());						
						
							// Pour les variables de type NID_XX, les opérateurs autorisés sont : 
							// "NoOperation", "Equal" et "NotEqual" 
							try {
								if (((DescripteurVariableDiscrete)descr).getLabels().
										get(Activator.getDefault().getCurrentLanguage()).
										get(0).getLabel().equals("$retirer$")) {
									
									for (Operation o : EnumSet.range(Operation.Greater, Operation.Change)) {
										this.comboOperation.remove(o.value()) ;
									}
								}
							} catch (Exception e) {
								// La variable ne possède pas de table de labels
							}
						}
					}
				}
				isDiscrete = false;
			}
		}
		return isDiscrete;
	}

	/**
	 * Updates the control for value (text or combo control)
	 * 
	 * @return boolean value to say if the combo was set for a discrete variable
	 */
	protected boolean updateValue() {

		String lastVal="";
		if (this.textValue instanceof Text) {
			lastVal=((Text)this.textValue).getText();
		}else if (this.textValue instanceof Combo) {
			lastVal=((Combo)this.textValue).getText();
		}

		boolean isDiscrete = false;
		String varName = this.selectedValue;
		if (varName != NO_VARIABLE && varName != ADV_SEARCH) {

			if (usesShortNames) {
				TableItem selectedItem = this.selectedItem;
				varName = this.tableitemLongNameTable.get(selectedItem);
			}
			// get the variable with this name
			DescripteurVariable descr = this.values.get(varName);
			if (descr != null &&
					( descr.getTypeVariable() == TypeVariable.VAR_COMPOSEE 
					|| descr.getTypeVariable() == TypeVariable.VAR_DISCRETE
					|| descr.getTypeVariable() == TypeVariable.VAR_VIRTUAL )
					){ 
				//					&& (&& (descr
				//					.getType() == Type.boolean1 || descr.getType() == Type.boolean8)) || descr
				//					.getTypeVariable() == TypeVariable.VAR_VIRTUAL | )) {

				this.textValue.dispose();
				this.textValue = new Combo(this.parent, SWT.NONE);
				((Combo)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
				this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
				Combo combo = (Combo) this.textValue;

				if(descr.getTypeVariable() == TypeVariable.VAR_COMPOSEE){
					List<LabelValeur> labels = Util.getInstance().getLabelsForVariable(descr);
					//We should have at least two values
					boolean isDoublon = false;
					if(labels != null && labels.size() > 1) {
						for (LabelValeur valeur : labels) {
							isDoublon = false;
							//ajout olivier: si l'on veut différencier plusieurs associations de valeurs associées au meme label(ex:UNDEFINED)
							//on doit rajouter les valeurs après le label: UNDEFINED(1,0,0,1), UNDEFINED(0,0,0,1) etc...
							//combo.add(valeur.getLabel()+"(" +(String)valeur.getValeurs() +")");
							//sinon on ne met que le label(ex:UNDEFINED) et la recherche s'arrete dès que le label est trouvé meme si plusieurs associations de valeurs lui sont associées
							for (int i = 0; i < combo.getItemCount(); i++) {
								if (combo.getItem(i).equals(valeur.getLabel())) {
									isDoublon = true;
								}
							}
							if (!isDoublon) {
								combo.add(valeur.getLabel());
							}
						}
					}
				}else if (descr.getTypeVariable() == TypeVariable.VAR_DISCRETE){
					if (((DescripteurVariableDiscrete) descr).getLabels() != null) {
						List<LabelValeur> listeLabels = ((DescripteurVariableDiscrete) descr).getLabels().get(Activator.getDefault().getCurrentLanguage());
						if (listeLabels != null) {
							combo.add(" "); //$NON-NLS-1$
							if(this.comboOperation.getText().equals(Operation.Change.value())){
								if (descr.getType()==Type.boolean1 || descr.getType()==Type.boolean8) {
									String item1=DE+" "+GuillemetsAnglais.getG1()
											+listeLabels.get(0).getLabel()+GuillemetsAnglais.getG2()+ " "

									  +VERS+" "+GuillemetsAnglais.getG1()+listeLabels.get(1).getLabel()
									  +" "+GuillemetsAnglais.getG2();
									String item2=DE+" "+GuillemetsAnglais.getG1()+listeLabels.get(1).getLabel() 
											+" "+GuillemetsAnglais.getG2()
											+VERS+" "+GuillemetsAnglais.getG1()+ listeLabels.get(0).getLabel()+" "+GuillemetsAnglais.getG2();
									combo.add(item1);
									combo.add(item2);
									combo.add(TOUSLESCHANGEMENTS);
								}else{
									for (LabelValeur valeur : listeLabels) {
										// Dans le cas des variables NID_XX il ne faut pas remplir 
										// la combobox des labels de la table de label
										// Le label $retirer$ permet de retirer la valeur 
										// correspondante à la variable lorsqu'elle est affichée
										if (!valeur.getLabel().equals("$retirer$")) {
											combo.add(DE+" "+GuillemetsAnglais.getG1()+valeur.getLabel()
													+GuillemetsAnglais.getG2()+ " "+VERSAUTREVALEUR);
										}
									}
									combo.add(TOUSLESCHANGEMENTS);
								}
							}else{							
								for (LabelValeur valeur : listeLabels) {
									// Dans le cas des variables NID_XX il ne faut pas remplir 
									// la combobox des labels de la table de label
									// Le label $retirer$ permet de retirer la valeur 
									// correspondante à la variable lorsqu'elle est affichée
									if (!valeur.getLabel().equals("$retirer$")) {
										if(valeur.getLabel().equals(Messages.getString("ASearchVariableDialog.7")))
											combo.add(new String(valeur.getLabel()).toLowerCase());
										else
											combo.add(valeur.getLabel());
									}
								}
							}
						}
					}else{
						//variable discrete mais sans table de labels
						if(this.comboOperation.getText().equals(Operation.Change.value())){
							combo.add(" ");
							combo.add(TOUSLESCHANGEMENTS);
						}
					}
				}
				//si la variable est de type virtuel alors on n'a que deux valeurs possibles:TRUE et false
				else if(descr.getTypeVariable() == TypeVariable.VAR_VIRTUAL){
					for (String value : valuesList) {
						combo.add(value);
					}
				}
				this.parent.layout();
				isDiscrete = true;
				String[] itemsPourLaCombo=combo.getItems();
				for (int i = 0; i < itemsPourLaCombo.length; i++) {
					if (lastVal.equals(itemsPourLaCombo[i])) {
						//si l'ancienne valeur correspond aux items de la combo, on la garde
						combo.setText(lastVal);
						break;
					}
				}
			} else {
				if(!isDiscrete){
					this.textValue.dispose();
					this.textValue = new Text(this.parent, SWT.BORDER);
					((Text)this.textValue).setToolTipText(INFOBULLE_COMBO_VAL);
					this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
					this.parent.layout();
					((Text)this.textValue).setText(lastVal);
				}
				isDiscrete = false;
			}
		}
		return isDiscrete;
	}

	/**
	 * Combo selection listener implementation for the variable combo
	 * 
	 * @author meggy
	 * 
	 */
	class ComboSelection extends SelectionAdapter {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			AbstractSelectionProviderVue.sensRecherche=0;
			AbstractSelectionProviderVue.varSelectionnee.reset();
			ASearchVariableDialog.this.comboVar.select(0);
		}

		/**
		 * if a value is selected
		 */
		public void widgetSelected(SelectionEvent selEvent) {
			AbstractSelectionProviderVue.sensRecherche=0;
			AbstractSelectionProviderVue.varSelectionnee.reset();
			String selEventData = ASearchVariableDialog.this.selectedValue;
			// if label <Search> is selected open a dialog for advanced search
			if (selEventData.equals(ADV_SEARCH)) {
				lastSearchVarCombo = true;

				RechercheDialog dlg = new RechercheDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				dlg.setSelectableValues(ASearchVariableDialog.this.values.keySet().toArray(new String[ASearchVariableDialog.this.values.size()]));
				dlg.setAppelant(this.getClass().getName());
				dlg.setTypeRecherche("Variable"); //$NON-NLS-1$

				String str = dlg.open();
				String[] items = ASearchVariableDialog.this.comboVar.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].equals(str)) {
						ASearchVariableDialog.this.comboVar.select(i);
						activerBoutonsRecherche();
					}
				}
				if (str != null)
					ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName() + "Variable", str);

				ASearchVariableDialog.this.isLastValueDiscrete = updateComboOperation(false) | updateValue();
				ASearchVariableDialog.this.comboOperation.setEnabled(true);
				if (selEventData.equals(NO_VARIABLE) || str == null) {
					ASearchVariableDialog.this.comboOperation.setEnabled(false);
					ASearchVariableDialog.this.textValue.setEnabled(false);
				}
			} else if (selEventData.equals(NO_VARIABLE)) {
				desactiverBoutonsRecherche();
				lastSearchVarCombo = false;
				ASearchVariableDialog.this.comboOperation.setEnabled(false);
				// do nothing
			} else {
				activerBoutonsRecherche();
				updateComboOperation(true);
				ASearchVariableDialog.this.comboOperation.setEnabled(true);
				lastSearchVarCombo = false;
			}
			ASearchVariableDialog.this.textValue.setEnabled(false);
		}
	}

	/**
	 * Combo selection listener implementation for the operation combo
	 * 
	 * @author meggy
	 * 
	 */
	class ComboSelection2 extends SelectionAdapter {

		public void widgetDefaultSelected(SelectionEvent arg0) {
			AbstractSelectionProviderVue.sensRecherche=0;
			AbstractSelectionProviderVue.varSelectionnee.reset();
			ASearchVariableDialog.this.comboOperation.select(0);
		}

		/**
		 * if a value is selected
		 */
		public void widgetSelected(SelectionEvent selEvent) {
			AbstractSelectionProviderVue.sensRecherche=0;
			AbstractSelectionProviderVue.varSelectionnee.reset();
			String selEventData = ASearchVariableDialog.this.comboOperation.getText();
			
			// if label <Search> is selected open a dialog for advanced search
			if (!selEventData.equals(" ")) { //$NON-NLS-1$
				ASearchVariableDialog.this.textValue.setEnabled(true);
				// update the combo for operation and the control for value
				ASearchVariableDialog.this.isLastValueDiscrete = updateValue();
			} else {
				ASearchVariableDialog.this.textValue.setEnabled(false);
			}
		}
	}

	public abstract void desactiverBoutonsRecherche();

	public abstract void activerBoutonsRecherche();

	/////////////////////////////////////

	private List<String> filterItems(String subStr) {
		ArrayList<String> retList = new ArrayList<String>();
		if (subStr == null || subStr.trim().length() == 0)
			return this.selectableValuesList;
		for (String str : this.selectableValuesList) {
			// meggy : modified to be case insensitive
			if (str.toLowerCase().contains(subStr.toLowerCase()))
				retList.add(str);
		}
		retList.trimToSize();
		return retList;
	}

	protected void initTableValues(List<String> values) {
		tableitemLongNameTable.clear();
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i)==TypeRepere.distanceRelatif.getName()||
					values.get(i)==TypeRepere.tempsRelatif.getName()	) {
				values.remove(i);
			}
		}

		if (this.itemsTable == null)
			return;
		this.itemsTable.removeAll();
		TableItem itemTable;
		Set<AVariableComposant> fpVariables = null;
		Set<Evenement> fpEvenements = null;
		Set<String> messageEventsNames = new HashSet<String>();
		if (this.typeRecherche.equals("Variable")) {
			fpVariables = GestionnairePool.getInstance().getVariablesRenseignees();
			if(fpVariables!=null){
				for (AVariableComposant var : fpVariables) {				
					addVariableNameToSet(var);				
				}
			}
		}

		if (this.typeRecherche.equals("Event")) {
			fpEvenements = Util.getInstance().getMessagesEvents();
			if(fpEvenements!=null){
				for (Evenement event : fpEvenements) {
					if (event != null) {
						messageEventsNames.add(event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
					}
				}
			}
		}
		List<String> nomVariables = new ArrayList<String>(values);
		java.util.Collections.sort(nomVariables);
		for (String filteredValue : nomVariables) {

			itemTable = new TableItem(this.itemsTable, SWT.NONE);
			String vitesseLimiteKVBNomUtilisateur = VitesseLimiteKVBService.getInstance().getTableLangueNomUtilisateur()
					.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
			if (vitesseLimiteKVBNomUtilisateur.equals(filteredValue)) {
				itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
			} else if (filteredValue !=null && filteredValue.contains("(V)")) {
				if(ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(filteredValue.replace("(V) ", "")))!=null)
				{	itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
				else {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				}
			} else {
				if (fpVariables != null) {
					if (!this.messageVariablesNames.contains(filteredValue))
						if(GestionnaireDescripteurs.getDescripteurVariableComposee(filteredValue.replace("[C]", ""))==null)
							itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						else itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					else {
						itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					}
				}
			}
			if (fpEvenements != null) {
				if (!messageEventsNames.contains(filteredValue))
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				else {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				}
			}
			// USE SHORT NAMES HERE
			String displayValue = filteredValue;
			if (this.usesShortNames) {
				String shortName = Util.getInstance().getNomCourtFromNomUtilisateur(displayValue);
				tableitemLongNameTable.put(itemTable, displayValue);
				displayValue = shortName;
			}
			itemTable.setText(displayValue);
			itemTable.setData(displayValue, filteredValue);
	}
	}


	protected void initTableValues2(List<String> values) {
		for (int i = 0; i < values.size(); i++) {
			if (values.get(i)==TypeRepere.distanceRelatif.getName()||
					values.get(i)==TypeRepere.tempsRelatif.getName()	) {
				values.remove(i);
			}
		}
		if (this.itemsTable == null)
			return;
		this.itemsTable.removeAll();
		TableItem itemTable;
		List<AVariableComposant> fpVariables = null;
		List<Evenement> fpEvenements = null;
		Set<String> messageEventsNames = new HashSet<String>();
		if (this.typeRecherche.equals("Variable")) {
			fpVariables = Util.getInstance().getAllVariables();
			if(fpVariables!=null){
				for (AVariableComposant var : fpVariables) {				
					addVariableNameToSet(var);	
					values.add(var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
				}
			}
		}
		if (typeRecherche.equals("Event")) {
			fpEvenements = Util.getInstance().getAllEvents();
			if(fpEvenements!=null){
				for (Evenement event : fpEvenements) {
					messageEventsNames.add(event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
					values.add(event.getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
				}
			}
		}
		List<String> nomVariables = new ArrayList<String>(values);
		java.util.Collections.sort(nomVariables);
		for (String filteredValue : nomVariables) {
			itemTable = new TableItem(this.itemsTable, SWT.NONE);
			if (filteredValue !=null && filteredValue.contains("(V)")) {
				if(ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBV(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getVBV(filteredValue.replace("(V) ", "")))!=null)
				{	itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				}
				else {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
				}
			} else {
				if (fpVariables != null) {
					if (!this.messageVariablesNames.contains(filteredValue))
						if(GestionnaireDescripteurs.getDescripteurVariableComposee(filteredValue.replace("[C]", ""))==null)
							itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						else itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					else {
						itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));
					}
				}
			}
			if (fpEvenements != null) {
				if (!messageEventsNames.contains(filteredValue))
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
				else {
					itemTable.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_BLACK));

				}
			}
			itemTable.setText(0, filteredValue);
		}
	}

	private void addVariableNameToSet(AVariableComposant var) {
		TypeVariable typeVar = var.getDescriptor().getTypeVariable();
		if (typeVar == TypeVariable.VAR_ANALOGIC || typeVar == TypeVariable.VAR_DISCRETE) {
			messageVariablesNames.add(var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()));
		} else if (typeVar == TypeVariable.VAR_COMPLEXE
				|| typeVar == TypeVariable.VAR_COMPOSEE) {
			addVariablesComposeeNamesToSet((VariableComposite)var);
		}
	}

	private void addVariablesComposeeNamesToSet(VariableComposite var) {
		int size = var.getVariableCount();
		ArrayList<String> dejapresent = new ArrayList<String>(0);

		for (int i = 0; i < size; i++) {
			AVariableComposant subVar = var.getEnfant(i);
			if (!dejapresent.contains(subVar.getDescriptor().getM_AIdentificateurComposant().getNom())) {
				dejapresent.add(subVar.getDescriptor().getM_AIdentificateurComposant().getNom());
				addVariableNameToSet(subVar);
			}
		}
	}

	protected TableColumn tableColumn;

	protected Text selectText;

	protected Text variableName;

	protected Label selectTextLabel;

	//	private Composite mainComposite;

	protected String selectedValue;
	
	protected TableItem selectedItem;

	protected List<String> selectableValuesList = new ArrayList<String>(0);

	protected String filterText;

	private Set<String> messageVariablesNames = new HashSet<String>();

	public void setSelectableValues(String[] values) {
		this.selectableValuesList.clear();
		if (values == null)
			throw new IllegalArgumentException(Messages.getString("RechercheDialog.3")); //$NON-NLS-1$
		for (String val : values) {
			if (val != null)
				this.selectableValuesList.add(val);
		}
		initTableValues(selectableValuesList);
	}

	/**
	 * Sets the text for the filtering
	 * 
	 * @param filterText
	 */
	public void setFilterText(String filterText) {
		this.filterText = filterText;
	}

	/**
	 * Returns the filtering text (the text that is displayed in the select
	 * text)
	 * 
	 * @return
	 */
	public String getFilterText() {
		return this.filterText;
	}
}
