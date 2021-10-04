package com.faiveley.samng.principal.ihm.vues.search;

import java.util.Calendar;
import java.util.Collection;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.calculs.TempsAbsoluDatePartielle;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

public abstract class ASearchVariableDialog2 extends ASearchDialog {

	// constants
	protected static final String NO_VARIABLE = Messages
			.getString("SearchVariable.9"); //$NON-NLS-1$

	protected static final String ADV_SEARCH = Messages
			.getString("SearchVariable.10"); //$NON-NLS-1$

	protected static final String nom = Messages.getString("SearchVariable.6"); //$NON-NLS-1$

	protected static final String ope = Messages.getString("SearchVariable.7"); //$NON-NLS-1$

	protected static final String valeur = Messages.getString("SearchVariable.8"); //$NON-NLS-1$

	private static final String[] valuesList = new String[] {
		" ", Messages.getString("ASearchVariableDialog.6"), Messages.getString("ASearchVariableDialog.7") }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

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

	String tempsAbs=com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.2");

	// map <name of the variable, descriptor of the variable>
	protected LinkedHashMap<String, DescripteurVariable> values = null;

	public ASearchVariableDialog2(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	public ASearchVariableDialog2(Shell parent, int style) {
		super(parent, style);
	}

	@Override
	protected void createControls(final Shell parentShell) {
		this.parent = parentShell;

		// combo "Name"
		Label label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(20, 25, 94, 13));
		label.setText(nom);
		label.setToolTipText(nom);
		createComboVar();

		// combo "Operation"
		label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(290, 25, 83, 13));
		label.setText(ope);
		label.setToolTipText(ope);
		createComboOperation();

		// Value
		label = new Label(this.parent, SWT.NONE);
		label.setBounds(new Rectangle(400, 25, 120, 13));
		label.setText(valeur);
		label.setToolTipText(valeur);
		this.textValue = new Text(this.parent, SWT.BORDER);
		this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
		this.textValue.setEnabled(false);
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
		this.comboVar.setBounds(new Rectangle(20, 45, 250, 21));
		fillCombo();

		this.comboVar.addSelectionListener(new ComboSelection());

		// permet de remettre la selection sur <Recherche> si la varaible avait
		// été trouvée par recherche
		this.comboVar.addListener(SWT.MouseDown, new Listener() {
			public void handleEvent(Event arg0) {
				if (lastSearchVarCombo) {
					String[] items = ASearchVariableDialog2.this.comboVar.getItems();
					for (int i = 0; i < items.length; i++) {
						if (items[i].equals(ADV_SEARCH)) {
							ASearchVariableDialog2.this.comboVar.select(i);
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
			
			for (Message msg : enrg.getMessages()) {
				if (msg.getVariablesAnalogique() != null) {
					addVariables(msg.getVariablesAnalogique());
				}
				if (msg.getVariablesDiscrete() != null) {
					addVariables(msg.getVariablesDiscrete());
				}
				if (msg.getVariablesComplexe() != null) {
					addVariablesComposee(msg.getVariablesComplexe());
				}

				if (msg.getVariablesComposee() != null) {
					addVariablesComposee(msg.getVariablesComposee());
				}
			}
			// ajout des variables composées à la liste des variables
			Map<String, AVariableComposant> composeeVars = GestionnairePool.getInstance()
					.getComposeeVariables();
			for (AVariableComposant compVar : composeeVars.values()) {
				addVariable(compVar);
			}
		}
	}

	/**
	 * Adds a variable to the combo and the list of values
	 * 
	 * @param var
	 */
	protected void addVariable(AVariableComposant var) {
		String str = var.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
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
		String varName = this.comboVar.getText();
		if (varName != NO_VARIABLE && varName != ADV_SEARCH	&& this.values != null) {

			// get the variable with this name
			DescripteurVariable descr = this.values.get(varName);
			if (descr != null
					&& ((descr.getTypeVariable() == TypeVariable.VAR_DISCRETE && 
					(descr.getType() == Type.boolean1 || descr.getType() == Type.boolean8)) 
					|| descr.getType() == Type.string 
					|| descr.getTypeVariable() == TypeVariable.VAR_VIRTUAL 
					|| TypeVariable.VAR_COMPOSEE == descr.getTypeVariable())) {

				if (init || (!this.isLastValueDiscrete)) {
					this.comboOperation.removeAll();
					for (Operation o : EnumSet.range(Operation.NoOperation, Operation.NotEqual)) {
						this.comboOperation.add(o.value());
					}
				}
				isDiscrete = true;
				booleanValue = true;
			} else {
				//si temps absolu
				if (varName.equals(tempsAbs)) {
					this.comboOperation.removeAll();
					for (Operation o : EnumSet.range(Operation.NoOperation, Operation.LessOrEqual)) {
						this.comboOperation.add(o.value());
					}
					//on positionne l'opérateur à  = 
					this.comboOperation.select(1);
					//on positionne la valeur à la date du PC
					//					Date dateActu=new Date(System.currentTimeMillis());
					Calendar c=Calendar.getInstance();
					c.setTimeInMillis(System.currentTimeMillis());
					this.textValue.dispose();
					this.textValue = new Text(this.parent, SWT.BORDER);
					this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
					this.textValue.setEnabled(true);
					String millis=ConversionTemps.getAjoutZerosFromResolutionTemps();
					String datJour=TempsAbsoluDatePartielle.ajouterZeroSiBesoin(c.get(Calendar.DAY_OF_MONTH))+"/"
							+TempsAbsoluDatePartielle.ajouterZeroSiBesoin(c.get(Calendar.MONTH)+1)+"/"
							+c.get(Calendar.YEAR)+" "
							+TempsAbsoluDatePartielle.ajouterZeroSiBesoin(c.get(Calendar.HOUR_OF_DAY))+":"
							+TempsAbsoluDatePartielle.ajouterZeroSiBesoin(c.get(Calendar.MINUTE))+":"
							+TempsAbsoluDatePartielle.ajouterZeroSiBesoin(c.get(Calendar.SECOND))
							+millis;
					Text t=(Text) textValue;
					t.setEditable(true);
					t.setEnabled(true);
					textValue.setEnabled(true);
					t.setText(datJour);
				}else if (init || this.isLastValueDiscrete) {
					this.comboOperation.removeAll();
					for (Operation o : EnumSet.range(Operation.NoOperation,
							Operation.ShiftLeft)) {
						this.comboOperation.add(o.value());
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
		boolean isDiscrete = false;
		String varName = this.comboVar.getText();
		if (varName != NO_VARIABLE && varName != ADV_SEARCH) {
			if (varName.equals(tempsAbs)) {
				//nothing to do
				return false;
			}

			// get the variable with this name
			DescripteurVariable descr = this.values.get(varName);
			if (descr != null
					&& ((descr.getTypeVariable() == TypeVariable.VAR_DISCRETE && (descr
							.getType() == Type.boolean1 || descr.getType() == Type.boolean8)) || descr
							.getTypeVariable() == TypeVariable.VAR_VIRTUAL | descr
							.getTypeVariable() == TypeVariable.VAR_COMPOSEE)) {

				//if (!this.isLastValueDiscrete) {
				this.textValue.dispose();
				this.textValue = new Combo(this.parent, SWT.NONE);
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
						List<LabelValeur> listeLabels = ((DescripteurVariableDiscrete) descr)
								.getLabels().get(Activator.getDefault().getCurrentLanguage());

						if (listeLabels != null) {
							combo.add(" "); //$NON-NLS-1$
							for (LabelValeur valeur : listeLabels) {
								if(valeur.getLabel().equals(Messages.getString("ASearchVariableDialog2.7")))
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
				isDiscrete = true;
			} else {
				//if (this.isLastValueDiscrete) {
				if(!isDiscrete){
					this.textValue.dispose();
					this.textValue = new Text(this.parent, SWT.BORDER);
					this.textValue.setBounds(new Rectangle(390, 45, 170, 20));
					this.parent.layout();
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
			ASearchVariableDialog2.this.comboVar.select(0);
		}

		/**
		 * if a value is selected
		 */
		public void widgetSelected(SelectionEvent selEvent) {

			String selEventData = ASearchVariableDialog2.this.comboVar.getText();
			// if label <Search> is selected open a dialog for advanced search
			if (selEventData.equals(ADV_SEARCH)) {
				lastSearchVarCombo = true;

				RechercheDialog dlg = new RechercheDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
				//				dlg.setSelectableValues(ASearchVariableDialog2.this.values
				//						.keySet().toArray(
				//								new String[ASearchVariableDialog2.this.values
				//										.size()]));

				dlg.setAppelant(this.getClass().getName());
				dlg.setTypeRecherche("Variable"); //$NON-NLS-1$

				String str = dlg.open();
				String[] items = ASearchVariableDialog2.this.comboVar.getItems();
				for (int i = 0; i < items.length; i++) {
					if (items[i].equals(str)) {
						ASearchVariableDialog2.this.comboVar.select(i);
						activerBoutonsRecherche();
					}
				}
				if (str != null)
					ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName() + "Variable", str); //$NON-NLS-1$

				ASearchVariableDialog2.this.isLastValueDiscrete = updateComboOperation(false)
						| updateValue();
				ASearchVariableDialog2.this.comboOperation.setEnabled(true);
				if (selEventData.equals(NO_VARIABLE) || str == null) {
					ASearchVariableDialog2.this.comboOperation.setEnabled(false);
					ASearchVariableDialog2.this.textValue.setEnabled(false);
				}

			} else if (selEventData.equals(NO_VARIABLE)) {
				desactiverBoutonsRecherche();
				lastSearchVarCombo = false;
				ASearchVariableDialog2.this.comboOperation.setEnabled(false);

				// do nothing
			} else {
				activerBoutonsRecherche();
				updateComboOperation(true);
				ASearchVariableDialog2.this.comboOperation.setEnabled(true);
				lastSearchVarCombo = false;

			}
			ASearchVariableDialog2.this.textValue.setEnabled(false);
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
			ASearchVariableDialog2.this.comboOperation.select(0);
		}

		/**
		 * if a value is selected
		 */
		public void widgetSelected(SelectionEvent selEvent) {
			String selEventData = ASearchVariableDialog2.this.comboOperation
					.getText();

			// if label <Search> is selected open a dialog for advanced search
			if (!selEventData.equals(" ")) { //$NON-NLS-1$
				ASearchVariableDialog2.this.textValue.setEnabled(true);
				// update the combo for operation and the control for value
				ASearchVariableDialog2.this.isLastValueDiscrete =
						// updateComboOperation(false)
						// |
						updateValue();

				if (selEventData.equals(" ")) //$NON-NLS-1$
					ASearchVariableDialog2.this.textValue.setEnabled(false);
			} else {
				ASearchVariableDialog2.this.textValue.setEnabled(false);
			}
		}
	}

	public abstract void desactiverBoutonsRecherche();

	public abstract void activerBoutonsRecherche();

}
