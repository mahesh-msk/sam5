package com.faiveley.samng.principal.ihm.actions.vue;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.vues.MessageSelection;
import com.faiveley.samng.principal.sm.calculs.referenceZero.ACalculReferenceZero;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;

public class SetReferenceAction extends Action {

	public static Double pointRef=0d;
	private Text distanceRelativeText;
	private boolean shellOpen=false;
	private Shell shell;

	/**
	 * Constructor
	 * 
	 * @param label
	 *            the label of the dialog
	 * @param descriptor
	 *            the image
	 */
	public SetReferenceAction(final String label,
			final ImageDescriptor descriptor) {

		setText(label);
		setToolTipText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_SET_REF);

		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_SET_REF);

		setImageDescriptor(descriptor);
	}

	

	/**
	 * Runs the action set for Set Repere Zero Calculates the values for
	 * relative time and relative distance and sets then in a AVariableComposant
	 */
	public void run() {

		if (isShellOpen()) {
			this.shell.setFocus();
			return;
		}

		ActivatorData.getInstance().getPoolDonneesVues().put("tabVueTabulaire",new String(""));
		ActivatorData.getInstance().getPoolDonneesVues().put("tabVueListe",new String(""));

		final IWorkbenchPart activePart = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart();
		if (activePart instanceof ISelectionProvider) {
			ISelectionProvider selProvider = (ISelectionProvider) activePart;
			ISelection sel = selProvider.getSelection();
			if (!(sel instanceof MessageSelection))
				return;
			MessageSelection msgSel = (MessageSelection) sel;
			if (msgSel == null || msgSel.isEmpty()) {
				showNoSelectionErrorMessage(activePart.getSite().getShell());
				return;
			}

			int msgId = msgSel.getMessageId();
			Message message=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(msgId);
			if (message.getError() != null 
					&& (!message.isBlockValid() || !message.isCRCValid() || !message.isLengthValid() || !message.isEvenementValid())) {
				showInvalidMessageErrorMessage(activePart.getSite().getShell());
				return;
			}
			ActivatorData.getInstance().setSelectedMsg(message);

			//si l'axe de distance existe, on peut fixer une valeur pour le point de R�f�rence
			if (!new ParseurAdapteur().inhiberBoiteDialoguePointReference()) {
				boiteDeDialogue(msgId, activePart);
			}else{
				lancerCalculs(msgId, activePart);
			}
		}
	}

	public void lancerCalculs(int msgId,IWorkbenchPart activePart){
		ACalculReferenceZero calcRef=ACalculReferenceZero.definirCalculByParseur();	
		try {
			calcRef.calculerReferenceZero(msgId,pointRef);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ActivatorData.getInstance().getPoolDonneesVues().put("ReferenceZero", "true");

		//modification pour correction issue GERER_VUE_GRAPHIQUE_SCEN3T12
		//la vue sur laquelle a �t� pos�e la r�f�rence z�ro est recharg�e en premier
		List<IRepereChangedListener>  listeRepereListeners = ActivatorData.getInstance().getListenersRepere();
		int i = 0;
		boolean partCouranteTrouve = false;
		while(i<listeRepereListeners.size()&&!partCouranteTrouve){
			if(listeRepereListeners.get(i).getClass()== (activePart.getClass())){
				listeRepereListeners.get(i).onRepereAdded(new TypeRepere[] { TypeRepere.distanceRelatif, TypeRepere.tempsRelatif });
				partCouranteTrouve = true;
			}
			i++;
		}
		i = 0;
		while(i<listeRepereListeners.size()){
			if(listeRepereListeners.get(i).getClass()!= (activePart.getClass()))
				listeRepereListeners.get(i).onRepereAdded(new TypeRepere[] { TypeRepere.distanceRelatif, TypeRepere.tempsRelatif });
			i++;
		}
		activePart.setFocus();
	}
	
	private void boiteDeDialogue(final int msgId,final IWorkbenchPart activePart){
		pointRef=null;

		Display display = Display.getCurrent();

		int sizeX=310;
		int sizeY=220;
		int bordX=15;
		int bordY=15;
		int Xcomposant=140;
		int XcomposantText=250;
		int Ycomposant=20;
		Rectangle rect=Display.getCurrent().getBounds();
		int posX=rect.width/2-sizeX/2;
		int posY=rect.height/2-sizeY/2;

		shell = new Shell (display);
		shell.setBounds(posX,posY,sizeX,sizeY);
		shell.setImage(com.faiveley.samng.principal.ihm.Activator.getDefault().getImage("/icons/toolBar/vues_commun_reference.png"));
		shell.setText(Messages.getString("ReferenceAction.5"));
		shell.addShellListener(new ShellListener() {
			@Override
			public void shellIconified(ShellEvent e) {
			}

			@Override
			public void shellDeiconified(ShellEvent e) {
			}

			@Override
			public void shellDeactivated(ShellEvent e) {
				setShellOpen(false);
			}

			@Override
			public void shellClosed(ShellEvent e) {
				setShellOpen(false);
			}

			@Override
			public void shellActivated(ShellEvent e) {
			}
		});

		Composite composite=new Composite(shell, SWT.NORMAL);
		composite.setVisible(true);
		composite.setBounds(bordX, bordY, sizeX-2*bordX, sizeY-4*bordY);
		int ecartYComposant=30;

		//composants		
		int posXcomp1=0;
		int posYcomp1=0;
		Label distanceRelativeLabel = new Label (composite, SWT.NORMAL);
		distanceRelativeLabel.setText(Messages.getString("ReferenceAction.6"));
		distanceRelativeLabel.setBounds(posXcomp1, posYcomp1, XcomposantText, Ycomposant);

		int posYcomp2=posYcomp1+ecartYComposant-8;
		distanceRelativeText = new Text (composite, SWT.BORDER);
		distanceRelativeText.setText("+0.000");
		distanceRelativeText.setBounds(posXcomp1, posYcomp2, Xcomposant, Ycomposant);
		distanceRelativeText.setEditable(true);

		Label uniteDistance=new Label(composite, 0);
		String uniteDistanceString = "";
		try {
			uniteDistanceString = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getUnite();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		uniteDistance.setText(uniteDistanceString);
		uniteDistance.setBounds(posXcomp1+Xcomposant+5, posYcomp2+3, sizeX-Xcomposant, Ycomposant);

		int posYcomp3=(int) (posYcomp2+ecartYComposant*1.5);
		Label tempsRelativeLabel = new Label (composite, SWT.NORMAL);
		tempsRelativeLabel.setText(Messages.getString("ReferenceAction.8"));
		tempsRelativeLabel.setBounds(posXcomp1, posYcomp3, XcomposantText, Ycomposant);

		int posYcomp4=posYcomp3+ecartYComposant-8;
		Text tempsRelativeText = new Text (composite, SWT.BORDER);
		tempsRelativeText.setText("+0h 0mn 0s 0ms");
		tempsRelativeText.setEditable(false);
		tempsRelativeText.setBounds(posXcomp1, posYcomp4, Xcomposant, Ycomposant);

		int posYcomp5=posYcomp3+ecartYComposant*2;
		Composite compB=new Composite(composite, SWT.NORMAL);
		compB.setBounds(0, posYcomp5, sizeX, Ycomposant*2);
		compB.setLayout(new RowLayout (SWT.HORIZONTAL));

		Button valider = new Button (compB, SWT.PUSH);
		int XBoutonValider=119;
		int YBoutonValider=25;
		valider.setText(Messages.getString("ReferenceAction.9"));
		valider.setBounds(0, 0, XBoutonValider, YBoutonValider);
		valider.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				String val=distanceRelativeText.getText();
				try {
					val=val.replace(",",".");
					Double d=Double.valueOf(val);
					SetReferenceAction.pointRef=d;
					lancerCalculs(msgId, activePart);
					shell.close();
					//					setShellOpen(false);
				} catch (Exception e) {
					MessageDialog.openError(shell, "", Messages.getString("ReferenceAction.7"));
				}
			}
		});

		Button buttonAnnuler = new Button (compB, SWT.NONE);
		buttonAnnuler.setText(Messages.getString("ReferenceAction.10"));
		buttonAnnuler.setBounds(XBoutonValider+10, 0, XBoutonValider, YBoutonValider);
		buttonAnnuler.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event event) {
				shell.close();
				//				setShellOpen(false);
			}
		});
		shell.open();
		setShellOpen(true);
		while (!shell.isDisposed ()) {
			if (!display.readAndDispatch ()) 
				display.sleep();
		}
	}
	
	private void showInvalidMessageErrorMessage(Shell shell) {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		msgBox.setText(Messages.getString("ReferenceAction.1"));
		msgBox.setMessage(Messages.getString("ReferenceAction.11"));
		msgBox.open();
	}

	private void showNoSelectionErrorMessage(Shell shell) {
		MessageBox msgBox = new MessageBox(shell, SWT.ICON_WARNING | SWT.OK);
		msgBox.setText(Messages.getString("ReferenceAction.1"));
		msgBox.setMessage(Messages.getString("ReferenceAction.2"));
		msgBox.open();
	}

	public static void creerReferenceZeroDefaut() {
		ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
		if (p == null) {
			return;
		}
		// get enregisrtrement
		Enregistrement enrg = p.getData().getEnregistrement();
		if (enrg == null) {
			return;
		}

		Message firstMsg = (enrg.getMessages() != null && !enrg.getMessages().isEmpty()) ? enrg.getMessages().get(0) : null;
		if (firstMsg != null) {
			ACalculReferenceZero calcRef=ACalculReferenceZero.definirCalculByParseur();	
			calcRef.calculerReferenceZero(firstMsg.getMessageId(),0);
		}
	}

	public boolean isShellOpen() {
		return shellOpen;
	}

	public void setShellOpen(boolean shellOpen) {
		this.shellOpen = shellOpen;
	}
}
