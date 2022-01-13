package com.faiveley.samng.principal.ihm.vues.vuesvbv;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.Operateur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VbvEditorComposite extends Composite implements PropertyChangeListener, IVbvElementEditorListener{
	private Label vbvDefinitionLabel;
	private Composite vbvEditorComposite;
	private Composite vbvEditorButtonsComposite;
	public VbvComponentEditorComposite operatorComposite;
	public VbvComponentEditorComposite operande1Composite;
	private Label vbvNameEqLabel;
	private Text vbvNameText;
	private Label labelVbvName;
	public VbvComponentEditorComposite operande2Composite;
	private Composite vbvNameComposite;
	private Button cancelCreateVbvButton;
	private Button saveVbvButton;
	private StyledText helpStyledText;
	private VariableVirtuelle editingVbv;
	private VbvsProvider vbvsProvider; 
	private EditorKeyListener escKeyListener = new EditorKeyListener();

	public String openVueVbvHelp = Messages.getString("VbvEditorComposite.10"); //$NON-NLS-1$
	public String creerVbvHelp = Messages.getString("VbvEditorComposite.11"); //$NON-NLS-1$
	public String selectVbvHelp = Messages.getString("VbvEditorComposite.12"); //$NON-NLS-1$

	public VbvEditorComposite(Composite parent, int style) {
		super(parent, style);
		GridLayout composite1Layout = new GridLayout();
		composite1Layout.makeColumnsEqualWidth = true;
		setLayout(composite1Layout);
		this.setSize(677, 354);

		createTopLabel();
		createVbvEditorPanel();

		createHelpComponent();
		createVbvEditorButtonsPanel();
		this.addKeyListener(new KeyAdapter() {

			public void keyReleased(KeyEvent event) {
			}

			@Override	
			public void keyPressed(KeyEvent event) {

				if(event.keyCode == SWT.ESC) {
					onEscapePressed();
				}
			}
		});

//		this.addKeyListener(escKeyListener);

operande1Composite.addKeyListener(new KeyAdapter() {

	public void keyReleased(KeyEvent event) {
	}

	@Override	
	public void keyPressed(KeyEvent event) {

		if(event.keyCode == SWT.ESC) {
			onEscapePressed();
		}
	}
});

operatorComposite.setTexteAide(Messages.getString("VbvEditorComposite.11") + "\n" + Messages.getString("VbvEditorComposite.16"));
operande1Composite.setTexteAide(Messages.getString("VbvEditorComposite.11") + "\n" + Messages.getString("VbvEditorComposite.14"));
operande2Composite.setTexteAide(Messages.getString("VbvEditorComposite.11") + "\n" + Messages.getString("VbvEditorComposite.15"));


operatorComposite.addKeyListener(new KeyAdapter() {

	public void keyReleased(KeyEvent event) {
	}

	@Override	
	public void keyPressed(KeyEvent event) {

		if(event.keyCode == SWT.ESC) {
			onEscapePressed();
		}
	}
});
operande2Composite.addKeyListener(new KeyAdapter() {

	public void keyReleased(KeyEvent event) {
	}

	@Override	
	public void keyPressed(KeyEvent event) {

		if(event.keyCode == SWT.ESC) {
			onEscapePressed();
		}
	}
});


	}
	/**
	 * Méthode de changment du message d'aide
	 * @param msg
	 */
	public void setHelpMsgbasic(String msg){
		helpStyledText.setText(msg);
	}



	public void setHelpMsgbasic(){
		if(vbvsProvider.getGestionnaireVbvs().getListeVBV().size()<1){
			helpStyledText.setText(openVueVbvHelp);
		}else{
			helpStyledText.setText(selectVbvHelp);
		}
	}

	public void setVbvsProvider(VbvsProvider provider) {
		provider.addPropertyChangeListener(this);
		vbvsProvider = provider;
		vbvsProvider.updateVariablesList(this);	//initially update of variables list (if any)
		setHelpMsgbasic();
	}

	private void createTopLabel() {
		vbvDefinitionLabel = new Label(this,
				SWT.SHADOW_IN | SWT.CENTER | SWT.BORDER);

		GridData vbvDefinitionLabelLData = new GridData();
		vbvDefinitionLabelLData.grabExcessHorizontalSpace = true;
		vbvDefinitionLabelLData.horizontalAlignment = GridData.FILL;
		vbvDefinitionLabelLData.heightHint = 23;
		vbvDefinitionLabelLData.minimumWidth=350;
		vbvDefinitionLabel.setLayoutData(vbvDefinitionLabelLData);
		vbvDefinitionLabel.setText(Messages.getString("VbvEditorComposite.0")); //$NON-NLS-1$
		vbvDefinitionLabel.setToolTipText((Messages.getString("VbvEditorComposite.0"))); //$NON-NLS-1$
	}

	private void createVbvEditorPanel() {
		vbvEditorComposite = new Composite(this, SWT.BORDER);

		FormLayout vbvEditorCompositeLayout = new FormLayout();
		GridData vbvEditorCompositeLData = new GridData();
		vbvEditorCompositeLData.heightHint = 100;
		vbvEditorCompositeLData.verticalAlignment = GridData.BEGINNING;
		vbvEditorCompositeLData.horizontalAlignment = GridData.FILL;
		vbvEditorCompositeLData.grabExcessHorizontalSpace = true;
		vbvEditorComposite.setLayoutData(vbvEditorCompositeLData);
		vbvEditorComposite.setLayout(vbvEditorCompositeLayout);

		createVbvNameComposite();
		createVbvOperand1Panel();
		createVbvOperatorPanel();
		createVbvOperand2Panel();
	}

	private void createVbvNameComposite() {

		vbvNameComposite = new Composite(vbvEditorComposite, SWT.BORDER);
		GridLayout vbvNameCompositeLayout = new GridLayout(3, true);
		vbvNameCompositeLayout.makeColumnsEqualWidth = false;
		FormData vbvNameCompositeLData = new FormData();
		vbvNameCompositeLData.width = 186;
		vbvNameCompositeLData.height = 90;
		vbvNameCompositeLData.left =  new FormAttachment(5, 1000, 0);
		vbvNameCompositeLData.right =  new FormAttachment(291, 1000, 0);
		vbvNameCompositeLData.top =  new FormAttachment(15, 1000, 0);
		//vbvNameCompositeLData.bottom =  new FormAttachment(395, 1000, 0);
		vbvNameComposite.setLayoutData(vbvNameCompositeLData);
		vbvNameComposite.setLayout(vbvNameCompositeLayout);

		labelVbvName = new Label(vbvNameComposite, SWT.NONE);
		GridData labelVbvNameLData = new GridData();
		labelVbvNameLData.horizontalSpan = 3;
		labelVbvNameLData.grabExcessHorizontalSpace = true;
		labelVbvNameLData.horizontalAlignment = GridData.CENTER;
		labelVbvName.setLayoutData(labelVbvNameLData);
		labelVbvName.setText(Messages.getString("VbvEditorComposite.1")); //$NON-NLS-1$
		labelVbvName.setToolTipText((Messages.getString("VbvEditorComposite.1"))); //$NON-NLS-1$

		vbvNameText = new Text(vbvNameComposite, SWT.NONE);
		GridData vbvNameTextLData = new GridData();
		vbvNameTextLData.grabExcessHorizontalSpace = true;
		vbvNameTextLData.horizontalAlignment = GridData.FILL;
		vbvNameTextLData.horizontalSpan = 2;
		vbvNameTextLData.heightHint = 20;
		vbvNameText.setLayoutData(vbvNameTextLData);
		vbvNameText.setText(""); //$NON-NLS-1$
		vbvNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!vbvNameText.isFocusControl())
					return;
				checkEditingVbvChange();
			
			}
		});
		
		


		vbvNameText.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				setHelpMsgbasic(Messages.getString("VbvEditorComposite.11") + "\n" + Messages.getString("VbvEditorComposite.13"));

			}

			public void focusLost(FocusEvent e) {
				setHelpMsgbasic(Messages.getString("VbvEditorComposite.11"));

			}

		});


		vbvNameText.addKeyListener(this.escKeyListener);

		vbvNameEqLabel = new Label(vbvNameComposite, SWT.NONE);
		GridData vbvNameEqLabelLData = new GridData();
		vbvNameEqLabelLData.widthHint = 7;
		vbvNameEqLabelLData.heightHint = 20;
		vbvNameEqLabelLData.horizontalAlignment = GridData.END;
		vbvNameEqLabel.setLayoutData(vbvNameEqLabelLData);
		vbvNameEqLabel.setText("="); //$NON-NLS-1$
		vbvNameComposite.pack();
	}

	private void createVbvOperand1Panel() {
		operande1Composite = new VbvComponentEditorComposite(vbvEditorComposite, SWT.BORDER);
		operande1Composite.setTopLabelText(Messages.getString("VbvEditorComposite.4")); //$NON-NLS-1$
		operande1Composite.setSearcheable(true);
		operande1Composite.addVbvElementChangeListener(this);

		GridLayout operande1CompositeLayout = new GridLayout();
		operande1CompositeLayout.makeColumnsEqualWidth = true;
		FormData operande1CompositeLData = new FormData();
		operande1CompositeLData.width = 168;
		operande1CompositeLData.height = 90;
		operande1CompositeLData.left =  new FormAttachment(296, 1000, 0);
		operande1CompositeLData.right =  new FormAttachment(555, 1000, 0);
		operande1CompositeLData.top =  new FormAttachment(15, 1000, 0);
		operande1Composite.setLayoutData(operande1CompositeLData);
		operande1Composite.setLayout(operande1CompositeLayout);
	}

	private void createVbvOperatorPanel() {
		operatorComposite = new VbvComponentEditorComposite(vbvEditorComposite, SWT.BORDER);
		operatorComposite.setTopLabelText(Messages.getString("VbvEditorComposite.5")); //$NON-NLS-1$
		operatorComposite.addVbvElementChangeListener(this);

		GridLayout operatorCompositeLayout = new GridLayout();
		operatorCompositeLayout.makeColumnsEqualWidth = true;
		FormData operatorCompositeLData = new FormData();
		operatorCompositeLData.width = 95;
		operatorCompositeLData.height = 90;
		operatorCompositeLData.left =  new FormAttachment(560, 1000, 0);
		operatorCompositeLData.right =  new FormAttachment(709, 1000, 0);
		operatorCompositeLData.top =  new FormAttachment(15, 1000, 0);
		//operatorCompositeLData.bottom =  new FormAttachment(404, 1000, 0);
		operatorComposite.setLayoutData(operatorCompositeLData);
		operatorComposite.setLayout(operatorCompositeLayout);
	}

	public void createVbvOperand2Panel() {
		operande2Composite = new VbvComponentEditorComposite(vbvEditorComposite, SWT.BORDER);
		operande2Composite.setTopLabelText(Messages.getString("VbvEditorComposite.6")); //$NON-NLS-1$
		//operande2Composite.setToolTipText(Messages.getString("VbvEditorComposite.6"));
		operande2Composite.setSearcheable(true);
		operande2Composite.addVbvElementChangeListener(this);

		GridLayout operande2CompositeLayout = new GridLayout();
		operande2CompositeLayout.makeColumnsEqualWidth = true;
		FormData operande2CompositeLData = new FormData();
		operande2CompositeLData.width = 184;
		operande2CompositeLData.height = 90;
		operande2CompositeLData.left =  new FormAttachment(714, 1000, 0);
		operande2CompositeLData.right =  new FormAttachment(997, 1000, 0);
		operande2CompositeLData.top =  new FormAttachment(15, 1000, 0);
		//operande2CompositeLData.bottom =  new FormAttachment(409, 1000, 0);
		operande2Composite.setLayoutData(operande2CompositeLData);
		operande2Composite.setLayout(operande2CompositeLayout);
	}

	private void createVbvEditorButtonsPanel() {
		//create the panel for the two buttons
		vbvEditorButtonsComposite = new Composite(this, SWT.BORDER);
		FormLayout vbvEditorBtnsCompLayout = new FormLayout();
		GridData vbvEditorBtnsCompLData = new GridData();
		vbvEditorBtnsCompLData.grabExcessHorizontalSpace = true;
		vbvEditorBtnsCompLData.horizontalAlignment = GridData.FILL;
		vbvEditorBtnsCompLData.verticalAlignment = GridData.END;
		vbvEditorBtnsCompLData.heightHint = 35;
		vbvEditorButtonsComposite.setLayoutData(vbvEditorBtnsCompLData);
		vbvEditorButtonsComposite.setLayout(vbvEditorBtnsCompLayout);

		//Create the apply button
		saveVbvButton = new Button(vbvEditorButtonsComposite, SWT.PUSH);
		FormData createNewVbvButtonLData = new FormData();
		createNewVbvButtonLData.height = 25;
		//createNewVbvButtonLData.width = 70;
		//position it near the center of the container panel
		createNewVbvButtonLData.right =  new FormAttachment(48, -4);
		createNewVbvButtonLData.bottom =  new FormAttachment(900, 1000, 0);
		saveVbvButton.setLayoutData(createNewVbvButtonLData);
		saveVbvButton.setText(Messages.getString("VbvEditorComposite.7")); //$NON-NLS-1$
		saveVbvButton.setToolTipText((Messages.getString("VbvEditorComposite.7"))); //$NON-NLS-1$
		saveVbvButton.setEnabled(false);
		saveVbvButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				saveEditingVbv();
				setHelpMsgbasic();
			}
		});

		//Create the close button
		cancelCreateVbvButton = new Button(vbvEditorButtonsComposite, SWT.PUSH);
		FormData cancelCreateVbvButtonLData = new FormData();
		cancelCreateVbvButtonLData.height = 25;
		//cancelCreateVbvButtonLData.width = 70;
		//align the close button to the right of the apply button
		cancelCreateVbvButtonLData.left =  new FormAttachment(saveVbvButton, 4);
		cancelCreateVbvButtonLData.bottom =  new FormAttachment(900, 1000, 0);
		cancelCreateVbvButton.setLayoutData(cancelCreateVbvButtonLData);
		cancelCreateVbvButton.setText(Messages.getString("VbvEditorComposite.8")); //$NON-NLS-1$
		cancelCreateVbvButton.setToolTipText((Messages.getString("VbvEditorComposite.8"))); //$NON-NLS-1$
		saveVbvButton.setEnabled(false);
		cancelCreateVbvButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				vbvsProvider.vbvEditingCanceled(editingVbv);
				setHelpMsgbasic();
			}
		});
	}



	private void onEscapePressed() {
		vbvsProvider.vbvEditingCanceled(editingVbv);
		setHelpMsgbasic();
	}

	private void createHelpComponent() {
		helpStyledText = new StyledText(this, SWT.BORDER | SWT.V_SCROLL);

		GridData helpStyledTextLData = new GridData();
		helpStyledTextLData.grabExcessHorizontalSpace = true;
		helpStyledTextLData.horizontalAlignment = GridData.FILL;
		helpStyledTextLData.verticalAlignment = GridData.FILL;
		helpStyledTextLData.grabExcessVerticalSpace = true;
		helpStyledText.setLayoutData(helpStyledTextLData);
		helpStyledText.setToolTipText((Messages.getString("VbvEditorComposite.9"))); //$NON-NLS-1$
		helpStyledText.setEditable(false);
		helpStyledText.setBackground(new Color(null, 255, 255, 204));
		helpStyledText.setWordWrap(true);

	}

	public void propertyChange(PropertyChangeEvent evt) {
		String evPropName = evt.getPropertyName();
		boolean vbvSelected = "VBV_SELECTED".equals(evPropName); //$NON-NLS-1$
		boolean vbvCreated = false;
		if(!vbvSelected)
			vbvCreated = "VBV_CREATED".equals(evPropName); //$NON-NLS-1$
		if (vbvSelected || vbvCreated) { //$NON-NLS-1$
			if(!this.isEnabled())
				this.setEnabled(true);
			onVbvSelected((VariableVirtuelle) evt.getNewValue(), vbvCreated);
			if(vbvCreated)
				saveVbvButton.setEnabled(false);
			if("VBV_CREATED".equals(evt.getPropertyName())){ //$NON-NLS-1$
				helpStyledText.setText(creerVbvHelp);
			}
			return;
		}
		if("VBV_CONTENT_CHANGED".equals(evPropName)) { //$NON-NLS-1$
			Boolean restored = (Boolean)evt.getOldValue();
			if(restored == null)
				restored = true;
			enableCancelSaveButtons(!restored);
			return;
		}
		if("VBV_SAVED".equals(evPropName)) { //$NON-NLS-1$
			enableCancelSaveButtons(false);
			return;
		}

		if("VBV_EDIT_CANCEL".equals(evPropName)) { //$NON-NLS-1$
			enableCancelSaveButtons(false);
			if(evt.getNewValue()==null){
				this.setEnabled(false);
				vbvNameText.setText(""); //$NON-NLS-1$
				operande1Composite.setValueString(""); //$NON-NLS-1$
				operatorComposite.setValueString(""); //$NON-NLS-1$
				operande2Composite.setValueString(""); //$NON-NLS-1$
			}
			return;
		}

		if("VARIABLES_LIST_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVariableListUpdate(evt);
			return;
		}

		if("VBV_DELETED".equals(evt.getPropertyName())) { //$NON-NLS-1$

			setHelpMsgbasic();

			if(evt.getNewValue()==null){
				this.setEnabled(false);
				vbvNameText.setText(""); //$NON-NLS-1$
				operande1Composite.setValueString(""); //$NON-NLS-1$
				operatorComposite.setValueString(""); //$NON-NLS-1$
				operande2Composite.setValueString(""); //$NON-NLS-1$

			}
			this.editingVbv=null;
			return;
		}

		if("VBV_MULTI_SELECTED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			this.setEnabled(false);
			vbvNameText.setText(""); //$NON-NLS-1$
			operande1Composite.setValueString(""); //$NON-NLS-1$
			operatorComposite.setValueString(""); //$NON-NLS-1$
			operande2Composite.setValueString(""); //$NON-NLS-1$
			return;
		}

		if("TEXT_HELP_CHANGED".equals(evt.getPropertyName())){
			if(evt.getNewValue()==null)
				setHelpMsgbasic(Messages.getString("VbvEditorComposite.11"));
			else
				setHelpMsgbasic((String)evt.getNewValue());
			return;
		}


	}

	private void onVbvSelected(VariableVirtuelle vbv, boolean created) {

		if(vbvsProvider.getVbvNonValides().contains(vbv)){

			this.setEnabled(false);

		}else{
			this.setEnabled(true);
		}

		if(vbv == null) {
			vbvNameText.setText(""); //$NON-NLS-1$
			operande1Composite.setValueString(""); //$NON-NLS-1$
			operatorComposite.setValueString(""); //$NON-NLS-1$
			operande2Composite.setValueString(""); //$NON-NLS-1$
		} else {
			vbvNameText.setText(vbv.getDescriptor().getM_AIdentificateurComposant().getNom());
			vbvNameText.setToolTipText((vbv.getDescriptor().getM_AIdentificateurComposant().getNom()));
			if(created) {
				operande1Composite.setValueString(""); //$NON-NLS-1$
				operatorComposite.setValueString(""); //$NON-NLS-1$
				operande2Composite.setValueString(""); //$NON-NLS-1$
			} else {
				AVariableComposant firstOperand = vbv.getVariableCount() == 0 ? null : vbv.getEnfant(0);
				if(firstOperand != null) {
					//we are using the user name
					operande1Composite.setValueString(VbvsUtil.getLabel(firstOperand));
				}

				if(vbv.getM_Operateur() != null) {
					operatorComposite.setValueString(VbvsUtil.getLabel(vbv.getM_Operateur()));
				}

				if( vbv.getVariableCount()>1){
					AVariableComposant secondOperand;
					try {
						secondOperand = vbv.getEnfant(1);
					} catch (RuntimeException e) {
						secondOperand = vbv.getEnfant(0);
					}
					operande2Composite.setValueString(VbvsUtil.getLabel(secondOperand));
				}
				else{
					//If we have a VBV which uses a value as second operand
					if(vbv.getValeurObjet() != null) {//tagValCor
						operande2Composite.setValueString((String)vbv.getValeurObjet());//tagValCor
					}          
				}
			}
			//update also the operator and second operand possible values 
			//		according to the first operand
			updatePanelsComboPossibleValues();

			if(this.editingVbv != vbv)
				enableCancelSaveButtons(created);
		}
		this.editingVbv = vbv;
	}

	protected void enableComponents(boolean enabled) {
		vbvNameText.setEnabled(enabled);
		//management for these buttons should be done separatelly
		//		as they should be disabled on simple selection
		if(!enabled)
			enableCancelSaveButtons(enabled);
	}

	protected void enableCancelSaveButtons(boolean enabled) {

		cancelCreateVbvButton.setEnabled(enabled);
		saveVbvButton.setEnabled(enabled);
	}

	public void saveEditingVbv() {
		AVariableComposant firstOperand = (AVariableComposant)operande1Composite.getValueObject();
		Object secondOperand = (AVariableComposant)operande2Composite.getValueObject();
		if(secondOperand == null)
			secondOperand = operande2Composite.getValueString();
		Operateur operator = (Operateur)operatorComposite.getValueObject();
		ActivatorData.getInstance().getPoolDonneesVues().put("isVBVListChanged",Boolean.valueOf(true));
		vbvsProvider.saveVbv(editingVbv, vbvNameText.getText(), firstOperand, operator, secondOperand);

	}

	@Override
	public void dispose() {
		vbvsProvider.removePropertyChangeListener(this);
		super.dispose();
	}

	public void vbvElementChanged(VbvComponentEditorComposite source) {
		//if the change source is the Operande 1 composite then we have to update
		//according to the selected variable the according operators and VBV 
		//the variables for the second operand
		if(source == operande1Composite) {
			updatePanelsComboPossibleValues();
		}
		checkEditingVbvChange();
		activerDesactiverBoutonSave();
	}

	private void activerDesactiverBoutonSave() {
		boolean op1= true;
		boolean op2= true;
		boolean operator = true;
		if(operande1Composite.getValueObject()==null && operande1Composite.getValueString().equals("")){ //$NON-NLS-1$
			op1=false;
		}

		if(operande2Composite.getValueObject()==null && operande2Composite.getValueString().equals("")) //$NON-NLS-1$
		{
			op2=false;
		}
		if(operatorComposite.getValueObject()==null && operatorComposite.getValueString().equals("")) //$NON-NLS-1$
		{
			operator = false;
		}
		if(!op1 ||!op2||!operator)
			saveVbvButton.setEnabled(false);
		else saveVbvButton.setEnabled(true);



	}

	/**
	 * Updates the combo for the operator and second operand with the possible
	 * values according to the value present in the firs operand
	 *
	 */
	private void updatePanelsComboPossibleValues() {
		AVariableComposant firstOperand = (AVariableComposant)operande1Composite.getValueObject();
		Operateur[] operators = VbvsUtil.getPossibleOperatorsForOperand(firstOperand);
		Map<String, Object> mapOperators = new LinkedHashMap<String, Object>();
		for(Operateur op: operators) {
			mapOperators.put(op.getStringValue(), op);
		}
		operatorComposite.setPossibleComboValues(mapOperators);

		if(firstOperand != null) {
			//update the list of possible values for the second operand
			Map<String, AVariableComposant> vars = new LinkedHashMap<String, AVariableComposant>(
					VbvsUtil.getPossibleSecondOperandForOperand(firstOperand, 
							operande1Composite.getPossibleObjectsMap()));
			List<VariableVirtuelle> vbvs = vbvsProvider.getGestionnaireVbvs().getListeVBV();

			if(firstOperand.getDescriptor().getTypeVariable() != TypeVariable.VAR_ANALOGIC && (firstOperand.getDescriptor().getType()==Type.boolean8 || firstOperand.getDescriptor().getType()==Type.boolean1) ){
				//we are setting a map from unique name to the objects 
				//we are not setting the user name to objects map as this is done in component editor
				for(VariableVirtuelle vbv: vbvs) {
					vars.put(vbv.getDescriptor().getM_AIdentificateurComposant().getNom(), vbv);
				}


			}
			vars.remove(vbvNameText.getText());
			//AVariableComposant varComp = vars.remove(firstOperand.getDescriptor().getM_AIdentificateurComposant().getNom());
			operande2Composite.setPossibleComboValues((Map)vars);

		}
	}

	public boolean checkEditingVbvChange() {
		if(this.editingVbv == null)
			return false;
		boolean isEditingVbvNameChanged = !vbvNameText.getText().equals(
				editingVbv.getDescriptor().getM_AIdentificateurComposant().getNom());
		boolean isFirstOperandChanged = false;
		boolean isOperatorChanged = false;
		boolean isSecondOperandChanged = false;
		String curValue;
		if(!isEditingVbvNameChanged) {	//if the name did not changed
			//now check also the other parameters of the VBV
			//check the first operand change
			AVariableComposant firstOperator = editingVbv.getVariableCount() == 0 ? null : editingVbv.getEnfant(0);
			curValue = operande1Composite.getValueString();
			if(firstOperator != null) {
				if(!curValue.equals(VbvsUtil.getLabel(firstOperator)))
					isFirstOperandChanged = true;
			} else {	//we have a newly created VBV
				if(!"".equals(curValue)) //$NON-NLS-1$
					isFirstOperandChanged = true;
			}

			//check the operator change
			Operateur op = editingVbv.getM_Operateur();
			if(op != null) {
				if(op != operatorComposite.getValueObject())
					isOperatorChanged = true;
			} else {
				if(operatorComposite.getValueObject() != null)
					isOperatorChanged = true;	//we have a newly created VBV
			}

			//check the second operand change
			AVariableComposant secondOperator = editingVbv.getVariableCount() < 2 ? null : editingVbv.getEnfant(1);
			curValue = operande2Composite.getValueString();
			if(secondOperator != null) {
				if(!curValue.equals(VbvsUtil.getLabel(secondOperator)))
					isSecondOperandChanged = true;
			} else {
				//test if the VBV has a value set
				String valeurVbv =null;
				if(editingVbv.getValeurObjet()!=null)
					valeurVbv = (String)editingVbv.getValeurObjet();
				if(valeurVbv != null) {
					if(!curValue.equals(valeurVbv))
						isSecondOperandChanged = true;
				} else {	//we have a newly created VBV
					if(!"".equals(curValue)) //$NON-NLS-1$
						isSecondOperandChanged = true;
				}
			}
		}
		boolean isEditingVbvChanged = isEditingVbvNameChanged || isFirstOperandChanged || 
		isOperatorChanged || isSecondOperandChanged;

		return this.vbvsProvider.vbvContentChanged(this.editingVbv, !isEditingVbvChanged); 
	}

	/**
	 * Handles a notification that the list of variables changed.
	 * The list of the possible values from the combo boxes will be updated.
	 * @param evt Variables list update notification event
	 */
	protected void onVariableListUpdate(PropertyChangeEvent evt) {
		List<AVariableComposant> variablesList = (List<AVariableComposant>)evt.getNewValue();
		operande1Composite.setPossibleComboValues(getMapVarNames(variablesList));
	}

	private Map<String, Object> getMapVarNames(List<AVariableComposant> variablesList) {
		Map<String, Object> mapUserNamesVar = new LinkedHashMap<String, Object>();
		if(variablesList != null) {
			String varName;
			String varUserName;
			for(AVariableComposant var: variablesList) {

				varName = var.getDescriptor().getM_AIdentificateurComposant().getNom();
				//we are using here the unique var name and not the nom utisateur
				//as this is handled inside the operandes composites
				varUserName = varName;
				mapUserNamesVar.put(varUserName, var);
			}

		}
		
		return mapUserNamesVar;
	}

	private class EditorKeyListener implements KeyListener {
		public void keyPressed(KeyEvent keyevent) {
		}
		public void keyReleased(KeyEvent keyevent) {
			if(keyevent.keyCode == SWT.ESC) {
				if(cancelCreateVbvButton.isEnabled())
					vbvsProvider.vbvEditingCanceled(editingVbv);
			} else if (keyevent.keyCode == SWT.CR) {
				if(saveVbvButton.isEnabled())
					saveEditingVbv();
			}
		}
	}
}

