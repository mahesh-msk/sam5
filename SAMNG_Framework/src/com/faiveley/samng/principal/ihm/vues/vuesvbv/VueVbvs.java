package com.faiveley.samng.principal.ihm.vues.vuesvbv;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.filtre.ActionOpenCloseVue;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.Operateur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VueVbvs extends ViewPart implements ISaveablePart2 {
	public static final String ID = "SAMNG.Vue.VBV.VueVirtualBooleanVariables"; //$NON-NLS-1$
	private SashForm sashForm = null;
	private String askForSaveMsg = Messages.getString("VueVbvs.1"); //$NON-NLS-1$
	private String title = 	Messages.getString("VbvsProvider.6"); //$NON-NLS-1$
	private VbvsListeComposite leftPanelComposite;
	private VbvEditorComposite rightPanelComposite;
	private Composite mainComposite;
	public VueVbvs() {
	}

	/**
	 * This method initializes sashForm	
	 *
	 */
	private void createSashForm(Composite parent) {
		sashForm = new SashForm(parent, SWT.HORIZONTAL | SWT.BORDER);
		sashForm.setBounds(new Rectangle(3, 4, 654, 294));
		leftPanelComposite = new VbvsListeComposite(sashForm, SWT.NONE);
		ActionOpenCloseVue closeAction = new ActionOpenCloseVue(Messages.getString("VueVbvs.2"), ID, ActionOpenCloseVue.ACTION_CLOSE); //$NON-NLS-1$
		leftPanelComposite.setCloseButtonAction(closeAction);

		rightPanelComposite = new VbvEditorComposite(sashForm, SWT.NONE);
		
		//Set the VBVs provider for the two views. We set first for the right view
		//as the left panel generates select messaged when he registers that we want 
		//to be captured by the right view.
		VbvsProvider provider = ActivatorData.getInstance().getProviderVBVs();
		rightPanelComposite.setVbvsProvider(provider);
		leftPanelComposite.setVbvsProvider(provider);
		
		if(provider.getGestionnaireVbvs().getListeVBV().size()==0){
			rightPanelComposite.setEnabled(false);
			
		}
		
		//vérification de la validité des VBV
		String s = ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBVs(null);
		if(s!=null){
		MessageBox msgBox = new MessageBox(Display.getCurrent()
				.getActiveShell(), SWT.OK);
		msgBox.setText(Messages.getString("VueVbvs.5"));  //$NON-NLS-1$
		msgBox.setMessage(s);  //$NON-NLS-1$
		msgBox.open();}
		
	}

	@Override
	public void createPartControl(Composite parent) {
		mainComposite = new Composite(parent, SWT.NONE);
		mainComposite.setLayout(new FillLayout());
		setPartName(title);
		createSashForm(mainComposite);
		
	}
	
	
	@Override
	public void setFocus() {
		//  Auto-generated method stub
		
	}

	/**
	 * Affichage due message de fermeture de la vue
	 */
	public int promptToSaveOnClose() {
		if(ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().getListeVBV().size()>0)
		{
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO | SWT.CANCEL);
			msgBox.setText(Messages.getString("VueVbvs.3")); //$NON-NLS-1$
			msgBox.setMessage(askForSaveMsg + /*curFilter.getNom() +*/ " ?"); //$NON-NLS-1$
			int res = msgBox.open();
			switch (res) {
			case SWT.YES:
				saveEditingVbv();
				return YES;
			case SWT.NO:
				return NO;
			case SWT.CANCEL:
				return CANCEL;
			default:
				return NO;
			}
		}else
			return YES;

	}
	
	public void saveEditingVbv() {
	
		String newVbvName=ActivatorData.getInstance().getProviderVBVs().lastCreatedVbv.getDescriptor().getM_AIdentificateurComposant().getNom();
		VariableVirtuelle vbvlc=ActivatorData.getInstance().getProviderVBVs().vbvsMng.getVBV(newVbvName);
		AVariableComposant firstOperand=(AVariableComposant)rightPanelComposite.operande1Composite.getValueObject();
		Object secondOperand = (AVariableComposant)rightPanelComposite.operande2Composite.getValueObject();
		if(secondOperand == null)
			secondOperand = rightPanelComposite.operande2Composite.getValueString();
    	Operateur operator = (Operateur)rightPanelComposite.operatorComposite.getValueObject();
		
		ActivatorData.getInstance().getProviderVBVs().saveVbv(vbvlc, newVbvName, firstOperand, operator, secondOperand);
		try {
			ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().enregistrerVBV();
		} catch (ParseurXMLException e) {
	
		}
    }
	
	public void doSave(IProgressMonitor monitor) {
		//rightPanelComposite.saveEditingFilter();
	}

	public void doSaveAs() {
	}

	public boolean isDirty() {
		boolean changement = rightPanelComposite.checkEditingVbvChange();
		return changement;
	}

	public boolean isSaveAsAllowed() {
		return false;
	}

	public boolean isSaveOnCloseNeeded() {
		return true;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		leftPanelComposite.dispose();
		rightPanelComposite.dispose();
	}
	
	
	
}
