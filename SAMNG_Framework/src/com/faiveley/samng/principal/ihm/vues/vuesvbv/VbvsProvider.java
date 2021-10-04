package com.faiveley.samng.principal.ihm.vues.vuesvbv;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.VueWaitBar;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.GestionnaireVBV;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.Operateur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VbvsProvider implements IDataChangedListener,
IRepereChangedListener {
	private static byte[] BYTE_FALSE = { 0 };

	private static byte[] BYTE_TRUE = { 1 };

	protected int newVbvCounter = 1;

	protected VariableVirtuelle lastCreatedVbv = null;

	protected GestionnaireVBV vbvsMng;


	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	private List<IVbvChangeListener> vbvChangeListeners = new ArrayList<IVbvChangeListener>(0);

	private List<VariableVirtuelle> vbvNonValides = new ArrayList<VariableVirtuelle>(0);

	public VbvsProvider() {
		ActivatorData.getInstance().addDataListener(this);
		ActivatorData.getInstance().addRepereListener(this);

		this.vbvsMng = new GestionnaireVBV();
	}

	public GestionnaireVBV getGestionnaireVbvs() {
		return this.vbvsMng;
	}

	/**
	 * Adds a property-change listener.
	 * 
	 * @param l
	 *            the listener
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		this.listeners.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.removePropertyChangeListener(l);
	}

	public void addVbvListener(IVbvChangeListener listener) {
		if (listener != null)
			vbvChangeListeners.add(listener);
	}

	public void removeVbvListener(IVbvChangeListener listener) {
		if (listener != null)
			vbvChangeListeners.remove(listener);
	}

	private void fireVbvSaved(String newVbvName, String oldVbvName) {
		for (IVbvChangeListener l : vbvChangeListeners) {
			l.onVbvAdded(newVbvName, oldVbvName);
		}
	}

	private void fireVbvRemoved(String vbvName) {
		for (IVbvChangeListener l : vbvChangeListeners) {
			l.onVbvRemoved(vbvName);
		}
	}

	/**
	 * Notificates all listeners to a model-change
	 * 
	 * @param prop
	 *            the property-id
	 * @param old
	 *            the old-value
	 * @param newValue
	 *            the new value
	 */
	protected final void firePropertyChange(final String prop,
			final Object old, final Object newValue) {
		try {
			if (this.listeners.hasListeners(prop)) {
				this.listeners.firePropertyChange(prop, old, newValue);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Création d'une nouvelle VBV
	 * 
	 */
	public void createNewVbv() {
		VariableVirtuelle vbv = new VariableVirtuelle();
		DescripteurVariable descrVar = new DescripteurVariable();
		IdentificateurVariable identif = new IdentificateurVariable();
		// identif.setCode(code);
		String name = creerUniqueVbvNom(
				"", "newVariable", newVbvCounter, false); //$NON-NLS-1$ //$NON-NLS-2$
		identif.setNom(name);
		descrVar.setM_AIdentificateurComposant(identif);
		descrVar.setTailleBits(8);
		descrVar.setTypeVariable(TypeVariable.VAR_VIRTUAL);
		descrVar.setType(Type.boolean8);

		// add the language table with only a default language
		TableLangueNomUtilisateur tblLangUserName;
		tblLangUserName = new TableLangueNomUtilisateur();
		tblLangUserName.setNomUtilisateur(Langage.DEF, "(V)" + name); //$NON-NLS-1$
		descrVar.setNomUtilisateur(tblLangUserName);

		vbv.setDescripteur(descrVar);

		this.vbvsMng.ajouterVBV(vbv);
		this.lastCreatedVbv = vbv;
		firePropertyChange("VBV_CREATED", null, vbv); //$NON-NLS-1$
	}

	/**
	 * Mise à jour de la liste des variables virtuelles dans la vue
	 * 
	 * @param le
	 *            listenner
	 */
	public void updateVbvsList(PropertyChangeListener l) {
		// We should notify only this listener to update its VBVs list
		// (if it is interested in this)
		List<VariableVirtuelle> vbvsList = vbvsMng.getListeVBV();
		if (vbvsList != null) {
			if (l != null) {
				try {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"VBVS_UPDATE", //$NON-NLS-1$
							null, vbvsList);
					l.propertyChange(evt);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				firePropertyChange("VBVS_UPDATE", null, vbvsList); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Méthode de récupération de la liste des variables pouvant être
	 * selectionnées dans la vue des VBVS
	 * 
	 * @return
	 */
	private List<AVariableComposant> creerListeVariablesSelectionnables() {
		ArrayList<AVariableComposant> variables = new ArrayList<AVariableComposant>();
		if(GestionnairePool.getInstance().getVariablesRenseignees()!=null){
			variables.addAll(GestionnairePool.getInstance().getVariablesRenseignees());
			List<AVariableComposant> variablesToRemove = new ArrayList<AVariableComposant>();
			List<AVariableComposant> variablesToAdd = new ArrayList<AVariableComposant>();
			for (AVariableComposant composant : variables) {
				if(composant.getDescriptor().getTypeVariable()==TypeVariable.VAR_COMPLEXE){
					VariableComposite comp = (VariableComposite)composant;
					int nbVar = comp.getVariableCount();
					for(int i=0; i<nbVar;i++){
						variablesToAdd.add(comp.getEnfant(i));
					}
					variablesToRemove.add(composant);

				}
			}


			//List<AVariableComposant> variables = Util.getAllVariables(true);
			if (ActivatorData.getInstance().getPoolDonneesVues().get(
			"axeDistanceCorrige") != null) { //$NON-NLS-1$
				if ((Boolean) ActivatorData.getInstance().getPoolDonneesVues().get(
				"axeDistanceCorrige")) { //$NON-NLS-1$

					variables.add(GestionnairePool.getInstance()
							.getVariable(TypeRepere.vitesseCorrigee.getCode()));
					variables.add(GestionnairePool.getInstance()
							.getVariable(TypeRepere.distanceCorrigee.getCode()));

				}

			}
			List<VariableVirtuelle> vbvs = this.getGestionnaireVbvs().getListeVBV();
			variables.addAll(vbvs);

			//vérification de la validité des VBV
			ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBVs(null);

			variables.addAll(variablesToAdd);
			variables.removeAll(variablesToRemove);
		}
		variables.trimToSize();
		return variables;

	}

	/**
	 * Mise à jour de la liste des variables utilisables pour créer des VBV
	 * 
	 * @param le
	 *            listener
	 */
	public void updateVariablesList(PropertyChangeListener l) {

		List<AVariableComposant> variables = creerListeVariablesSelectionnables();
		// List<AVariableComposant> variables = Util.getAllVariables(true);
		// List<VariableVirtuelle> vbvs =
		// this.getGestionnaireVbvs().getListeVBV();
		// variables.addAll(vbvs);

		if (ActivatorData.getInstance().getPoolDonneesVues().get(
		"axeDistanceCorrige") != null) { //$NON-NLS-1$
			if ((Boolean) ActivatorData.getInstance().getPoolDonneesVues().get(
			"axeDistanceCorrige")) { //$NON-NLS-1$

				variables.add(GestionnairePool.getInstance()
						.getVariable(TypeRepere.vitesseCorrigee.getCode()));
				variables.add(GestionnairePool.getInstance()
						.getVariable(TypeRepere.distanceCorrigee.getCode()));

			}

		}

		if (variables != null) {
			if (l != null) {
				try {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"VARIABLES_LIST_UPDATE", //$NON-NLS-1$
							null, variables);
					l.propertyChange(evt);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				firePropertyChange("VARIABLES_LIST_UPDATE", null, variables); //$NON-NLS-1$
			}
		}
	}



	/**
	 * Suppression d'une VBV
	 * 
	 * @param vbvName
	 */
	public void deleteVbv(String vbvName) {

		// Check to see if the deleted vbv is a VBV that is used in the VBVs
		List<VariableVirtuelle> listVBVs = this.vbvsMng.getListeVBV();
		boolean isUsed;
		String usingVarName;
		for (VariableVirtuelle vbv : listVBVs) {
			isUsed = false;
			usingVarName = vbv.getDescriptor().getM_AIdentificateurComposant().getNom();
			if (vbv.getEnfant(0) != null) {
				if (vbvName.equals(vbv.getEnfant(0).getDescriptor().getM_AIdentificateurComposant().getNom())) {
					isUsed = true;
				}
			}
			if (vbv.getVariableCount() == 2
					&&vbv.getEnfant(1)!=null
					&& vbvName.equals(vbv.getEnfant(1).getDescriptor().getM_AIdentificateurComposant().getNom()))
				isUsed = true;

			if (isUsed) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
				msgBox.setMessage(vbvName + Messages.getString("VbvsProvider.4") +" "+ usingVarName); //$NON-NLS-1$
				msgBox.open();
				return;
			}
		}

		List<AFiltreComposant> listeVarFiltres = new ArrayList<AFiltreComposant>();
		LigneVariableFiltreComposite ligneVarFiltre=null;
		// check if the deleted variable is used in a filter
		List<AbstractProviderFiltre> listeGestionnaires = ActivatorData.getInstance().getListeGestionnairesFiltres();	

		for (AbstractProviderFiltre gestionnaire : listeGestionnaires) {

			AFiltreComposant listeFiltres = gestionnaire.getGestionnaireFiltres().getListeFiltres();
			for (int i = 0; i < listeFiltres.getEnfantCount(); i++) {
				AFiltreComposant filtreTmp = listeFiltres.getEnfant(i);
				if (filtreTmp.getFiltreType() == TypeFiltre.tabulaire) {
					for (int j = 0; j < filtreTmp.getEnfantCount(); j++) {
						if (filtreTmp.getEnfant(j) instanceof LigneVariableFiltreComposite) {
							ligneVarFiltre = (LigneVariableFiltreComposite) filtreTmp.getEnfant(j);
							
							if (ligneVarFiltre.getNom().equals(vbvName)) {
								if (!listeVarFiltres.contains(filtreTmp)) {
									listeVarFiltres.add(filtreTmp);
//									filtreTmp.supprimer(ligneVarFiltre);
								}
							}
						}
					}
				}
				if (filtreTmp.getFiltreType() == TypeFiltre.graphique) {
					GraphiqueFiltreComposite graphComposite;
					for (int j = 0; j < filtreTmp.getEnfantCount(); j++) {
						graphComposite = (GraphiqueFiltreComposite) filtreTmp.getEnfant(j);
						for (int m = 0; m < graphComposite.getEnfantCount(); m++) {
							ligneVarFiltre = (LigneVariableFiltreComposite) graphComposite.getEnfant(m);
							if (ligneVarFiltre.getNom().equals(vbvName)) {
								if (!listeVarFiltres.contains(filtreTmp)) {
									listeVarFiltres.add(filtreTmp);
//									filtreTmp.supprimer(ligneVarFiltre);
								}
							}
						}
					}
				}
			}
		}

		if (listeVarFiltres.size() > 0) {
			String filtresChaine = ""; //$NON-NLS-1$
			List<String> listeFiltreNom = new ArrayList<String>();
			for (AFiltreComposant filtre : listeVarFiltres) {
				
				if(!listeFiltreNom.contains(filtre.getNom())){
				filtresChaine += filtre.getNom() + "\n "; //$NON-NLS-1$
				listeFiltreNom.add(filtre.getNom()) ;
				}
				
			}
			// ask user if he want to delete the variable even if it is in
			// filters
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VbvsProvider.19") + "\n " + filtresChaine); //$NON-NLS-1$ //$NON-NLS-2$

			if (msgBox.open() == SWT.NO)
				return;
			else {
				
				for (AbstractProviderFiltre gestionnaire : listeGestionnaires) {

					AFiltreComposant listeFiltres = gestionnaire.getGestionnaireFiltres().getListeFiltres();
					for (int i = 0; i < listeFiltres.getEnfantCount(); i++) {
						AFiltreComposant filtreTmp = listeFiltres.getEnfant(i);
						if (filtreTmp.getFiltreType() == TypeFiltre.tabulaire) {
							for (int j = 0; j < filtreTmp.getEnfantCount(); j++) {
								if (filtreTmp.getEnfant(j) instanceof LigneVariableFiltreComposite) {
									ligneVarFiltre = (LigneVariableFiltreComposite) filtreTmp.getEnfant(j);
									if (ligneVarFiltre.getNom().equals(vbvName)) {
										if (!listeVarFiltres.contains(filtreTmp)) {
//											listeVarFiltres.add(filtreTmp);
											filtreTmp.supprimer(ligneVarFiltre);
										}
									}
								}
							}
						}
						if (filtreTmp.getFiltreType() == TypeFiltre.graphique) {
							GraphiqueFiltreComposite graphComposite;
							for (int j = 0; j < filtreTmp.getEnfantCount(); j++) {
								graphComposite = (GraphiqueFiltreComposite) filtreTmp.getEnfant(j);
								for (int m = 0; m < graphComposite.getEnfantCount(); m++) {
									ligneVarFiltre = (LigneVariableFiltreComposite) graphComposite.getEnfant(m);
									if (ligneVarFiltre.getNom().equals(vbvName)) {
										if (!listeVarFiltres.contains(filtreTmp)) {
//											listeVarFiltres.add(filtreTmp);
											filtreTmp.supprimer(ligneVarFiltre);
										}
									}
								}
							}
						}
					}
				}
				
				VariableVirtuelle removedVbv = vbvsMng.supprimerVBV(vbvName);
				if (removedVbv != null) {
					VueWaitBar.getInstance().setRect(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getBounds());
					VueWaitBar.getInstance().start();
					try{
						firePropertyChange("VBV_DELETED", removedVbv, null); //$NON-NLS-1$
						removeVbvFromMessage(removedVbv); // remove the VBV from
						// messages
						// where it occurs
						fireVbvRemoved(vbvName); // fire gestionnaires that vbv
					}
					catch(Exception ex){
						ex.printStackTrace();
					}
					finally{
						VueWaitBar.getInstance().stop();
					}
					// was
					// removed
					ActivatorData.getInstance().notifyDataListeners();
				}
				onDataChange();
			}
		}
		else {
			// Ask the user if he really wants to delete the selected VBV
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION	| SWT.YES | SWT.NO);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VbvsProvider.0") + vbvName + "?"); //$NON-NLS-1$ //$NON-NLS-2$
			if (msgBox.open() == SWT.NO)
				return;

			VariableVirtuelle removedVbv = vbvsMng.supprimerVBV(vbvName);
			if (removedVbv != null) {
				VueWaitBar.getInstance().setRect(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getBounds());
				VueWaitBar.getInstance().start();
				try{
					firePropertyChange("VBV_DELETED", removedVbv, null); //$NON-NLS-1$
					removeVbvFromMessage(removedVbv); // remove the VBV from
					// messages
					// where it occurs
					fireVbvRemoved(vbvName); // fire gestionnaires that vbv was
				}
				catch(Exception ex){
					ex.printStackTrace();
				}
				finally{
					VueWaitBar.getInstance().stop();
				}
				// removed
				ActivatorData.getInstance().notifyDataListeners();
			}
			onDataChange();
		}
	}



	/**
	 * Receives an existing VBV in the VBVs List to save it
	 * 
	 * @param vbv
	 * @param newVbvName
	 */
	public void saveVbv(VariableVirtuelle vbv, String newVbvName,
			AVariableComposant firstOperand, Operateur operator,
			Object secondOperand) {

		if (vbv != null && firstOperand != null) {
			String oldVbvName = vbv.getDescriptor()
			.getM_AIdentificateurComposant().getNom();
			// perform validations
			if (!isValidVbvName(vbv, newVbvName,firstOperand,secondOperand))
				return;

			// vérification de la validité des opérandes
			// controle des cycles
			VariableVirtuelle v1;
			if (firstOperand instanceof VariableVirtuelle) {
				v1 = (VariableVirtuelle) firstOperand;

				if (v1.getEnfant(0) instanceof VariableVirtuelle)
					if (v1.getEnfant(0).getDescriptor()
							.getM_AIdentificateurComposant().getNom().equals(
									newVbvName)) {
						MessageBox msgBox = new MessageBox(new Shell(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
						msgBox.setMessage(Messages.getString("VbvsProvider.12") //$NON-NLS-1$
								+ v1.getDescriptor()
								.getM_AIdentificateurComposant()
								.getNom());
						msgBox.open();
						return;
					}
				if (v1.getVariableCount() > 1) {
					if (v1.getEnfant(1) instanceof VariableVirtuelle)
						if (v1.getEnfant(1).getDescriptor()
								.getM_AIdentificateurComposant().getNom()
								.equals(newVbvName)) {
							MessageBox msgBox = new MessageBox(new Shell(),
									SWT.ICON_ERROR | SWT.OK);
							msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
							msgBox.setMessage(Messages
									.getString("VbvsProvider.13") //$NON-NLS-1$
									+ v1.getDescriptor()
									.getM_AIdentificateurComposant()
									.getNom());
							msgBox.open();
							return;
						}
				}
			}

			if (secondOperand instanceof VariableVirtuelle) {
				v1 = (VariableVirtuelle) secondOperand;

				if (v1.getEnfant(0) instanceof VariableVirtuelle){
					if (v1.getEnfant(0).getDescriptor()
							.getM_AIdentificateurComposant().getNom().equals(
									newVbvName)) {
						MessageBox msgBox = new MessageBox(new Shell(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
						msgBox.setMessage(Messages.getString("VbvsProvider.14") //$NON-NLS-1$
								+ v1.getDescriptor()
								.getM_AIdentificateurComposant()
								.getNom());
						msgBox.open();
						return;
					}
				}
				if(v1.getVariableCount()>1){
					if (v1.getEnfant(1) instanceof VariableVirtuelle)
						if (v1.getEnfant(1).getDescriptor()
								.getM_AIdentificateurComposant().getNom().equals(
										newVbvName)) {
							MessageBox msgBox = new MessageBox(new Shell(),
									SWT.ICON_ERROR | SWT.OK);
							msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
							msgBox.setMessage(Messages.getString("VbvsProvider.15") //$NON-NLS-1$
									+ v1.getDescriptor()
									.getM_AIdentificateurComposant()
									.getNom());
							msgBox.open();
							return;
						}
				}
			}

			boolean isSecondOpValue = secondOperand instanceof String;
			boolean secondOpValid = true;
			if (isSecondOpValue) {
				if (secondOperand == null)
					secondOpValid = false;
				else {
					if ("".equals(((String) secondOperand).trim())) //$NON-NLS-1$
						secondOpValid = false; // we do not accept empty string
					else {
						secondOpValid = VbvsUtil.isValidValue(firstOperand,
								(String) secondOperand);
					}
				}
			}


			if (firstOperand == null || operator == null
					|| !VbvsUtil.isValidOperator(firstOperand, operator)
					|| !secondOpValid) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
						| SWT.OK);
				msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("VbvsProvider.7")); //$NON-NLS-1$
				msgBox.open();
				return;
			}

			//if (!newVbvName.equals(vbv.getDescriptor()
			//		.getM_AIdentificateurComposant().getNom())) {
			if (!newVbvName.equals(vbv.getDescriptor()
					.getM_AIdentificateurComposant().getNom()))
				ActivatorData.getInstance().getPoolDonneesVues().put(
						"vbvNewName", newVbvName); //$NON-NLS-1$

			List<AFiltreComposant> listeVarFiltres = new ArrayList<AFiltreComposant>();

			LigneVariableFiltreComposite ligneVarFiltre=null;
			// check if the deleted variable is used in a filter
			List<AbstractProviderFiltre> listeGestionnaires = ActivatorData.getInstance().getListeGestionnairesFiltres();

			String vbvName = vbv.getDescriptor()
			.getM_AIdentificateurComposant().getNom();

			for (AbstractProviderFiltre gestionnaire : listeGestionnaires) {

				AFiltreComposant listeFiltres = gestionnaire
				.getGestionnaireFiltres().getListeFiltres();
				for (int i = 0; i < listeFiltres.getEnfantCount(); i++) {
					AFiltreComposant filtreTmp = listeFiltres.getEnfant(i);

					if (filtreTmp.getFiltreType() == TypeFiltre.tabulaire) {

						for (int j = 0; j < filtreTmp.getEnfantCount(); j++) {

							if (filtreTmp.getEnfant(j) instanceof LigneVariableFiltreComposite) {
								ligneVarFiltre = (LigneVariableFiltreComposite) filtreTmp
								.getEnfant(j);
								if (ligneVarFiltre.getNom().equals(vbvName)) {
									if (!listeVarFiltres
											.contains(filtreTmp)) {
										listeVarFiltres.add(filtreTmp);
										ligneVarFiltre.setNom(newVbvName);
									}
								}
							}
						}
					}
					if (filtreTmp.getFiltreType() == TypeFiltre.graphique) {
						GraphiqueFiltreComposite graphComposite;
						for (int j = 0; j < filtreTmp.getEnfantCount(); j++) {
							graphComposite = (GraphiqueFiltreComposite) filtreTmp
							.getEnfant(j);

							for (int m = 0; m < graphComposite
							.getEnfantCount(); m++) {
								ligneVarFiltre = (LigneVariableFiltreComposite) graphComposite
								.getEnfant(m);
								if (ligneVarFiltre.getNom().equals(vbvName)) {
									if (!listeVarFiltres
											.contains(filtreTmp)) {
										listeVarFiltres.add(filtreTmp);
										ligneVarFiltre.setNom(newVbvName);
									}
								}
							}
						}
					}
				}
			}
			//}

			if (newVbvName.equals(vbv.getDescriptor()
					.getM_AIdentificateurComposant().getNom())&&  listeVarFiltres.size()>0){

				String filtresChaine = ""; //$NON-NLS-1$
				for (AFiltreComposant filtre : listeVarFiltres) {
					filtresChaine += filtre.getNom() + "\n  "; //$NON-NLS-1$
				}

				MessageBox msgBox2 = new MessageBox(new Shell(), SWT.ICON_QUESTION
						| SWT.YES | SWT.NO);
				msgBox2.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
				msgBox2
				.setMessage(Messages.getString("VbvsProvider.30") + "\n " + filtresChaine); //$NON-NLS-1$ //$NON-NLS-2$

				if (msgBox2.open() == SWT.NO){
					return;
				}

			}

//			if everything is ok, update VBV and fire the save event
			vbv.clear();
			vbv.getDescriptor().getM_AIdentificateurComposant().setNom(newVbvName);
			vbv.ajouter(firstOperand);
			vbv.setM_Operateur(operator);
			//CHANGE_VALEUR
			if (isSecondOpValue)
				vbv.setValeurObjet(secondOperand);//tagValCor
			else
				vbv.ajouter((AVariableComposant) secondOperand);
			int niveauVBV = 0;
			try {
				niveauVBV = calculerNiveauVBV(vbv);
			} catch (StackOverflowError ex) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR | SWT.OK);
				msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
				msgBox.setMessage(Messages.getString("VbvsProvider.16")); //$NON-NLS-1$
				msgBox.open();
				return;
			}

			if (niveauVBV > 20) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
						| SWT.OK);
				msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
				msgBox
				.setMessage(
						Messages.getString("VbvsProvider.17") + " : " //$NON-NLS-1$
						+vbv.getDescriptor().getM_AIdentificateurComposant().getNom() +" : "
						+niveauVBV); //$NON-NLS-1$ //$NON-NLS-2$
				msgBox.open();
				return;
			}

			// Set the default language language
			TableLangueNomUtilisateur tblLangUserName;
			tblLangUserName = new TableLangueNomUtilisateur();
			tblLangUserName.setNomUtilisateur(Langage.DEF, "(V) " + newVbvName); //$NON-NLS-1$
			vbv.getDescriptor().setNomUtilisateur(tblLangUserName);

			firePropertyChange("VBV_SAVED", oldVbvName, vbv); //$NON-NLS-1$
			List<VariableVirtuelle> listVbvsDependant=fireDependentVbv(vbv);

			List<VariableVirtuelle> vbvsList = vbvsMng.getListeVBV();
			firePropertyChange("VBVS_UPDATE", null, vbvsList); //$NON-NLS-1$VBVS_UPDATE

			// Update also the messages
			addVbvToMessages(vbv);
			for (int i = 0; i < listVbvsDependant.size(); i++) {
				addVbvToMessages(listVbvsDependant.get(i));
			}
			// first notify that the VBV was saved ... this is generally used
			// for any
			// gestionnaire liste to know that the VBVs changed
			fireVbvSaved(newVbvName, oldVbvName);
			ActivatorData.getInstance().notifyDataListeners();
			ActivatorData.getInstance().getPoolDonneesVues().put("vbvNewName", ""); //$NON-NLS-1$ //$NON-NLS-2$
			lastCreatedVbv = null;
		}
		onDataChange();
	}

	public List<VariableVirtuelle> fireDependentVbv(VariableVirtuelle vbvCurrent){
		ArrayList<VariableVirtuelle> listVbvsDependant=new ArrayList<VariableVirtuelle>();
		List<VariableVirtuelle> listVbvs=this.vbvsMng.getListeVBV();
		int nbVbvs=listVbvs.size();
		for (int i = 0; i < nbVbvs; i++) {
			if (listVbvs.get(i)!=vbvCurrent) {
				if (VariableVirtuelle.contenirVariable(listVbvs.get(i), vbvCurrent)) {
					firePropertyChange("VBV_SAVED", listVbvs.get(i).getDescriptor().getM_AIdentificateurComposant().getNom(),
							listVbvs.get(i)); //$NON-NLS-1$
					listVbvsDependant.add(listVbvs.get(i));
				}
			}
		}
		listVbvsDependant.trimToSize();
		return listVbvsDependant;
	}

	/**
	 * Fires a notification that the current VBV editing was canceled. If the
	 * current editing VBV was just created (due to a New action) this VBV will
	 * be deleted also from the list of VBVs
	 * 
	 * @param vbv
	 */
	public void vbvEditingCanceled(VariableVirtuelle vbv) {
		if (lastCreatedVbv != null) {
			vbvsMng.supprimerVBV(vbv.getDescriptor()
					.getM_AIdentificateurComposant().getNom());
			firePropertyChange("VBV_EDIT_CANCEL", vbv, null); //$NON-NLS-1$
		} else {
			// Normally we should put the same VBV here but unfortunatelly the
			// firePropertyChange is checking if is the same object as old and
			// new
			firePropertyChange("VBV_EDIT_CANCEL", vbv, new Object()); //$NON-NLS-1$
		}

		lastCreatedVbv = null;
	}

	/**
	 * Sends a notification that the VBV selection changed. The selected VBV is
	 * cloned such that the editing is made on a copy of the selected VBV
	 * 
	 * @param vbvName
	 */
	public void vbvSelected(String vbvName) {
		VariableVirtuelle filtre = vbvsMng.getVBV(vbvName);
		if (filtre != null) {
			firePropertyChange("VBV_SELECTED", null, filtre); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the computed change state of the filter. If is a new created
	 * filter then it will return always true (as it is always changed until the
	 * save)
	 * 
	 * @param vbv
	 *            the verified VBV
	 * @param restored
	 *            the restored state computed by the caller (usually a view)
	 * @return the new change state
	 */
	public boolean vbvContentChanged(VariableVirtuelle vbv, boolean restored) {
		// send notification only if we are not editing a new created VBV.
		// If we are editing a new created VBV this is always changed
		if (lastCreatedVbv == null) {
			//lastCreatedVbv = vbv;
			firePropertyChange("VBV_CONTENT_CHANGED", restored, vbv); //$NON-NLS-1$
			return !restored;
		}
		if(restored)
			return !restored;
		return true; // if we are working on a new created VBV it is always
		// changed
	}

	protected String creerUniqueVbvNom(String namePrefix, String nameSuffix,
			int baseIdx, boolean idxAfterPrefix) {
		String name;
		// avoid adding VBVs with the same name
		while (true) {
			// try finding a name that do not exists yet
			name = idxAfterPrefix ? namePrefix
					+ " (" + (baseIdx++) + ")" + nameSuffix : //$NON-NLS-1$ //$NON-NLS-2$
						namePrefix + nameSuffix + (baseIdx++);
					VariableVirtuelle existingVBV = vbvsMng.getVBV(name);
					if (existingVBV == null) {
						return name;
					}
		}
	}

	public void onDataChange() {
		try {
			PropertyChangeListener[] listenersArr = this.listeners
			.getPropertyChangeListeners();
			for (PropertyChangeListener listener : listenersArr) {
				updateVariablesList(listener);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Updates the descriptions of the variables that are used inside the loaded
	 * VBVs as these have incomplete descriptions after a parse operation
	 * 
	 * @param chemin
	 * @return
	 */
	public boolean chargerVBV(String chemin) {
		boolean ret = this.vbvsMng.chargerVBV(chemin);

		// NOTE: this method should be called after the binary file was parsed

		// We have to update the descriptions of the variables that are
		// used inside the loaded VBVs as these have incomplete descriptions
		if (ret == true) { // if a successful load
			updateVbsDescriptions();
			updateMessagesFromVBVs();
		}

		// Notify listeners to update their VBV list
		try {
			PropertyChangeListener[] listenersArr = this.listeners
			.getPropertyChangeListeners();
			for (PropertyChangeListener listener : listenersArr) {
				updateVbvsList(listener);
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return ret;
	}

	/**
	 * Updates the descriptions of the variables that are used inside the loaded
	 * VBVs as these have incomplete descriptions after a parse operation
	 * 
	 */
	public void updateVbsDescriptions() {
		List<AVariableComposant> variables = Util.getInstance().getAllVariablesIncludeSubvars();
		List<VariableVirtuelle> listVbvs = this.vbvsMng.getListeVBV();
		// add to the variables list also the VBVs to be updated
		// THIS IS VERRY IMPORTANT
		variables.addAll(listVbvs);

		Map<String, AVariableComposant> mapVarNames = new HashMap<String, AVariableComposant>();
		for (AVariableComposant var : variables) {
			mapVarNames.put(var.getDescriptor().getM_AIdentificateurComposant()
					.getNom(), var);
		}

		String operandName;
		AVariableComposant existingVar;
		AVariableComposant curFirstChild;
		AVariableComposant curSecondChild;
		Operateur curOperator;

		for (VariableVirtuelle vbv : listVbvs) {
			curOperator = vbv.getM_Operateur(); // save the operator as a clear
			// is done later
			// update the first operand if possible
			curFirstChild = vbv.getEnfant(0);
			operandName = curFirstChild.getDescriptor()
			.getM_AIdentificateurComposant().getNom();
			existingVar = mapVarNames.get(operandName);
			if (existingVar != null) {
				curFirstChild = existingVar;
			}

			curSecondChild = null;
			// Update the second operande if possible
			if (vbv.getVariableCount() == 2) {
				curSecondChild = vbv.getEnfant(1);
				operandName = curSecondChild.getDescriptor()
				.getM_AIdentificateurComposant().getNom();
				existingVar = mapVarNames.get(operandName);
				if (existingVar != null) {
					curSecondChild = existingVar;
				}
			}
			vbv.clear();
			vbv.setM_Operateur(curOperator);
			vbv.ajouter(curFirstChild);
			if (curSecondChild != null) // this is happening only when the VBV
				// had the second operator
				vbv.ajouter(curSecondChild);
		}
	}

	/**
	 * Checks if the new vbv name is allowed to be set to a VBV. The name is
	 * checked in the VBVs names list but also in the real variables names
	 * 
	 * @param vbv
	 * @param newVbvName
	 * @return
	 */
	private boolean isValidVbvName(VariableVirtuelle vbv, String newVbvName, AVariableComposant firstOperand, Object secondOperand) {
		String oldVbvName = vbv.getDescriptor().getM_AIdentificateurComposant()
		.getNom();
		if (newVbvName == null || "".equals(newVbvName)) { //$NON-NLS-1$
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages
					.getString("VbvsProvider.1")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}
		if (!newVbvName.equals(oldVbvName)) {
			VariableVirtuelle existingVBV = vbvsMng.getVBV(newVbvName);
			if (existingVBV != null) {
				MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
						| SWT.OK);
				msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
				msgBox.setMessage(newVbvName + Messages
						.getString("VbvsProvider.2")); //$NON-NLS-1$
				msgBox.open();
				return false;
			}
		}
		AVariableComposant var = GestionnairePool.getInstance().getVariable(newVbvName);
		Collection<AVariableComposant> tete=GestionnairePool.getInstance().getAllVariables().values();
		boolean isRealName = false;
		//SUPPR_ITERATOR
//		for (Iterator iter = tete.iterator(); iter.hasNext();) {
//		AVariableComposant variable = (AVariableComposant) iter.next();
//		if (variable.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()).equals(newVbvName)) {
//		isRealName = true;
//		}

//		}

		for (AVariableComposant variable: tete) {
			//AVariableComposant variable = (AVariableComposant) iter.next();
			if (variable.getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()).equals(newVbvName)) {
				isRealName = true;
			}

		}
		if (var != null && var != vbv || isRealName) {
			// if we already have this name for a real variable
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(newVbvName + Messages
					.getString("VbvsProvider.3")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}

		if (newVbvName.matches("-?\\d+(\\.|,\\d+)?")) {
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VbvsProvider.26")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}
		
		if(newVbvName.toLowerCase().trim().equals("true") || newVbvName.trim().toLowerCase().equals("false")||newVbvName.trim().startsWith(com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.14"))){ //$NON-NLS-1$ //$NON-NLS-2$
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VbvsProvider.27")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}

		if(firstOperand.getDescriptor().getM_AIdentificateurComposant().getNom().equals(newVbvName)){
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VbvsProvider.28")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}

		if(secondOperand instanceof VariableVirtuelle && ((VariableVirtuelle)secondOperand).getDescriptor().getM_AIdentificateurComposant().getNom().equals(newVbvName)){
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR
					| SWT.OK);
			msgBox.setText(Messages.getString("VbvsProvider.6")); //$NON-NLS-1$
			msgBox.setMessage(Messages.getString("VbvsProvider.29")); //$NON-NLS-1$
			msgBox.open();
			return false;
		}




		return true;
	}

	/**
	 * After a VBV modification (save, or delete) the fichier de parcours data
	 * is updated with the new VBV variables
	 * 
	 */
	private void updateMessagesFromVBVs() {
		GestionnaireVBV vbvMng = ActivatorData.getInstance().getProviderVBVs()
		.getGestionnaireVbvs();
		List<VariableVirtuelle> vbvsList = vbvMng.getListeVBV();

		for (VariableVirtuelle vbv : vbvsList) {
			String chaineVerificationVBV = verifierValiditeVBV(vbv);
			if(chaineVerificationVBV==null)
			addVbvToMessages(vbv);
		}
	}

	/**
	 * Charge les VBV et les rajoute dans les messages
	 *
	 */
	public void chargerVBVDansMessages(){
		List<VariableVirtuelle> listeVarVirt = vbvsMng.getListeVBV();
		for (VariableVirtuelle virtuelle : listeVarVirt) {
			String chaineVerificationVBV = verifierValiditeVBV(virtuelle);
			if(chaineVerificationVBV==null)
			ActivatorData.getInstance().getProviderVBVs().addVbvToMessages(virtuelle);
		}
	}



	/**
	 * Adds a VBV to the messages where the VBV can be added
	 * 
	 */
	private void addVbvToMessages(VariableVirtuelle vbv) {
		VueData vueData = ActivatorData.getInstance().getVueData();
		List<Message> listeMessages= vueData.getDataTable().getEnregistrement().getMessages();
		
		if (listeMessages == null || listeMessages.size()==0)
			return;

		Map<DescripteurVariable, Integer> idMsgOpMap = new HashMap<DescripteurVariable, Integer>();

		for (Message msg : listeMessages) {
			addVbvToMessage(msg, vbv, idMsgOpMap);
		}

	}

	/**
	 * Adds a VBV to a message. If the VBV contains other VBV as first or second
	 * operand, these are added recursively to the message before current one is
	 * added
	 * 
	 * @param message
	 *            the message where VBV should be added
	 * @param vbv
	 *            the vbv to be added
	 * @param idMsgOpMap 
	 */
	private void addVbvToMessage(Message message, VariableVirtuelle vbv, Map<DescripteurVariable, Integer> idMsgOpMap) {
		// on conserve le message original sur lequel on effectue la comparaison
		AVariableComposant firstOperand; // the first operand found in VBV
		AVariableComposant secondOperand; // the second operand found in VBV
		String vbvSecondValue; // the VBV value
		Operateur operator; // the VBV operator
		boolean isSecondOperand; // specifies if value or second operand
		// should be considered
		AVariableComposant msgFirstOperand; // the variable for the first
		// operand found in message (if any)
		AVariableComposant msgSecondOperand; // the variable for the first
		// operand found in message (if
		// any)
		boolean vbvValue = false;

		secondOperand = null;
		isSecondOperand = false;
		msgSecondOperand = null;



		// extract the first operand, the operator and the second operand
		firstOperand = vbv.getEnfant(0);
		// check if the first operand is in turn a virtual variable
		if (firstOperand.getDescriptor().getTypeVariable() == TypeVariable.VAR_VIRTUAL) {
			// if so, add recursively all the VBVs in the message
			addVbvToMessage(message, (VariableVirtuelle) firstOperand, idMsgOpMap);
		}

		vbvSecondValue = (String) vbv.getValeurObjet();//tagValCor
		operator = vbv.getM_Operateur();
		if ( vbv.getVariableCount()>1 && vbv.getEnfant(1)!=null ||vbvSecondValue == null) {
			secondOperand = vbv.getEnfant(1);
			// check if the second operand is in turn a virtual variable
			if (secondOperand.getDescriptor().getTypeVariable() == TypeVariable.VAR_VIRTUAL) {
				// if so, add recursively all the VBVs in the message
				addVbvToMessage(message, (VariableVirtuelle) secondOperand, idMsgOpMap);
			}
			isSecondOperand = true;
		}

		// get the variable for the first operand
		msgFirstOperand = message.getVariable(firstOperand.getDescriptor());

		if (msgFirstOperand != null) {
			idMsgOpMap.put(firstOperand.getDescriptor(), message.getMessageId());
		}
		else {
			if (idMsgOpMap.containsKey(firstOperand.getDescriptor())) {
				msgFirstOperand = ActivatorData.getInstance().getVueData()
				.getDataTable().getEnregistrement().getGoodMessage(
						idMsgOpMap.get(firstOperand.getDescriptor())).getVariable(
								firstOperand.getDescriptor());
			}

			if (msgFirstOperand == null)
				return;
		}

		if (isSecondOperand) {
			// if the second operand is also a variable, get the variable for
			// the second operand
			msgSecondOperand = message.getVariable(secondOperand
					.getDescriptor());

			if (msgSecondOperand != null) {
				idMsgOpMap.put(secondOperand.getDescriptor(), message.getMessageId());
			}
			else {
				if (idMsgOpMap.containsKey(secondOperand.getDescriptor())) {
					msgSecondOperand = ActivatorData.getInstance().getVueData()
					.getDataTable().getEnregistrement()
					.getGoodMessage(idMsgOpMap.get(secondOperand.getDescriptor())).getVariable(
							secondOperand.getDescriptor());
				}
			}
			if (msgSecondOperand == null)
				return;

		}

		switch (operator) {
		case EQUALS:
			if (isSecondOperand) {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(msgSecondOperand
						.toString()) == 0;
			} else {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(vbvSecondValue) == 0;
			}
			break;
		case DIFFERENT:
			if (isSecondOperand) {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(msgSecondOperand
						.toString()) != 0;
			} else {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(vbvSecondValue) != 0;
			}
			break;
		case LESS_THAN:
			if (isSecondOperand) {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(msgSecondOperand
						.toString()) < 0;
			} else {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(vbvSecondValue) < 0;
			}
			break;
		case GREATER_THAN:
			if (isSecondOperand) {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(msgSecondOperand
						.toString()) > 0;
			} else {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(vbvSecondValue) > 0;
			}
			break;
		case LESS_THAN_OR_EQUALS:
			if (isSecondOperand) {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(msgSecondOperand
						.toString()) <= 0;
			} else {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(vbvSecondValue) <= 0;
			}

			break;
		case GREATER_THAN_OR_EQUALS:
			if (isSecondOperand) {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(msgSecondOperand
						.toString()) >= 0;
			} else {
				vbvValue = msgFirstOperand
				.compareValueWithStringValue(vbvSecondValue) >= 0;
			}
			break;
		case AND:
			Type typeFirst = msgFirstOperand.getDescriptor().getType();
			if (typeFirst == Type.boolean1 || typeFirst == Type.boolean8) {
				int msgFirstOperandValue = (Integer) msgFirstOperand
				.getCastedValeur();
				if (isSecondOperand) {
					Type typeSecond = msgSecondOperand.getDescriptor()
					.getType();
					if (typeSecond == Type.boolean1
							|| typeSecond == Type.boolean8) {
						if (AVariableComposant.BOOLEAN_FALSE_VALUE == msgFirstOperandValue)
							vbvValue = false; // AND is a short circuit
							// operation if first is 0
						else if (AVariableComposant.BOOLEAN_FALSE_VALUE == (Integer) msgSecondOperand
								.getCastedValeur()) {
							vbvValue = false; // AND is a short circuit
							// operation if first is 0
						} else {
							vbvValue = true; // both of them are true
						}
					}
				} else {
					int vbvSecondIntValue = vbvSecondValue
					.equals(AVariableComposant.BOOL_STR_TRUE_VALUE) ? 1
							: 0;
					// we have true if both values are true
					vbvValue = AVariableComposant.BOOLEAN_TRUE_VALUE == msgFirstOperandValue
					&& AVariableComposant.BOOLEAN_TRUE_VALUE == vbvSecondIntValue;
				}
			}
			break;
		case OR:
			typeFirst = msgFirstOperand.getDescriptor().getType();
			if (typeFirst == Type.boolean1 || typeFirst == Type.boolean8) {
				int msgFirstOperandValue = (Integer) msgFirstOperand
				.getCastedValeur();
				if (isSecondOperand) {
					Type typeSecond = msgSecondOperand.getDescriptor()
					.getType();
					if (typeSecond == Type.boolean1
							|| typeSecond == Type.boolean8) {
						if (AVariableComposant.BOOLEAN_TRUE_VALUE == msgFirstOperandValue)
							vbvValue = true; // OR is a short circuit
						// operation if first is 1
						else if (AVariableComposant.BOOLEAN_TRUE_VALUE == (Integer) msgSecondOperand
								.getCastedValeur()) {
							vbvValue = true; // OR is a short circuit
							// operation if second is 1
						} else {
							vbvValue = false; // both of them are true
						}
					}
				} else {
					// we have true if one of the values is true
					int vbvSecondIntValue = vbvSecondValue
					.equals(AVariableComposant.BOOL_STR_TRUE_VALUE) ? 1
							: 0;
					vbvValue = AVariableComposant.BOOLEAN_TRUE_VALUE == msgFirstOperandValue
					|| AVariableComposant.BOOLEAN_TRUE_VALUE == vbvSecondIntValue;
				}
			}
			break;
		}
		byte[] booleanStrVal = vbvValue == false ? BYTE_FALSE : BYTE_TRUE;

		AVariableComposant newVbv = new VariableDiscrete();
		newVbv.setDescripteur(vbv.getDescriptor());
		newVbv.setValeur(booleanStrVal);
		newVbv.setTypeValeur(Type.boolean8);
		message.modifierVariable(newVbv);
	}

	private void removeVbvFromMessage(VariableVirtuelle vbv) {
		VueData vueData = ActivatorData.getInstance().getVueData();
		List<Message> listeMessages= vueData.getDataTable().getEnregistrement().getMessages();

		if (listeMessages == null ||listeMessages.size()==0)
			return;


		List<VariableDiscrete> msgVbvs;
		for (Message msg : listeMessages) {
			msgVbvs = msg.getVariablesVirtuelle();
			if (msgVbvs != null) {
				msg.supprimerVariable(vbv);
			}
		}
	}

	public void vbvMultiSelected(Table vbvsList) {
		if (vbvsList != null) {
			firePropertyChange("VBV_MULTI_SELECTED", null, null); //$NON-NLS-1$
			// firePropertyChange("VBV_MULTI_SELECTED",null,null); //$NON-NLS-1$
		}

	}

	public void onRepereAdded(TypeRepere... reper) {

		List<AVariableComposant> variables = creerListeVariablesSelectionnables();
//		for (TypeRepere repere : reper) {
//		if (repere.equals(TypeRepere.vitesseCorrigee)) {

//		variables.add(GestionnairePool
//		.getVariable(TypeRepere.vitesseCorrigee.getCode()));

//		}
//		if (repere.equals(TypeRepere.distanceCorrigee)) {

//		variables.add(GestionnairePool
//		.getVariable(TypeRepere.distanceCorrigee.getCode()));

//		}
//		}
		firePropertyChange("VARIABLES_LIST_UPDATE", null, variables); //$NON-NLS-1$

		IViewReference refView[] = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < refView.length; i++) {
			if (refView[i].getId().equals("SAMNG.Vue.VBV.VueVirtualBooleanVariables")) { //$NON-NLS-1$
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(refView[i]);
				try {
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(refView[i].getId());
				} catch (PartInitException e1) {
					// TODO Bloc catch auto-généré
					e1.printStackTrace();
				}
			}
		}

	}

	public void onRepereRemoved(TypeRepere... reper) {

		List<AVariableComposant> variables = creerListeVariablesSelectionnables();

		firePropertyChange("VARIABLES_LIST_UPDATE", null, variables); //$NON-NLS-1$

		IViewReference refView[] = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (int i = 0; i < refView.length; i++) {
			if (refView[i].getId().equals("SAMNG.Vue.VBV.VueVirtualBooleanVariables")) { //$NON-NLS-1$
				Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().hideView(refView[i]);
				try {
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(refView[i].getId());
				} catch (PartInitException e1) {

					e1.printStackTrace();
				}
			}
		}
	}

	/**
	 * Vérifie la validité d'une variable virtuelle
	 * @param var
	 * @return null si valide 
	 */
	public String verifierValiditeVBV(VariableVirtuelle var){
		if (var==null) {
			return "";
		}
		Langage langue = Activator.getDefault().getCurrentLanguage();
		
		if (var.getVariableCount()==0) {
			return null;
		}
		AVariableComposant op1 = var.getEnfant(0);
		boolean valide =true;
		String chaine=""; //$NON-NLS-1$
		boolean trouve = false;
		if(op1 instanceof AVariableComposant){
			if(op1.getDescriptor().getM_AIdentificateurComposant().getCode()==-1){
				valide = false;
				chaine = Messages.getString("VbvsProvider.20") + op1.getDescriptor().getM_AIdentificateurComposant().getNom() +"\n "; //$NON-NLS-1$ //$NON-NLS-2$
			}
			else if(op1 instanceof VariableComposite){
				String chtmp = null;
				if(!(op1 instanceof VariableComplexe) ){
					chtmp= verifierValiditeVBV((VariableVirtuelle)op1);
				}
				if(chtmp!=null)
					chaine+=chtmp;
			} 
			else {
				if(op1.getDescriptor().getM_AIdentificateurComposant().getCode() == TypeRepere.distanceCorrigee.getCode() || op1.getDescriptor().getM_AIdentificateurComposant().getCode() == TypeRepere.vitesseCorrigee.getCode()){
					if (ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige") != null && (Boolean)(ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige")).toString().equals("true")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						trouve=true;
					}
					if(!trouve){
						valide = false;
						chaine = Messages.getString("VbvsProvider.21") + op1.getDescriptor().getNomUtilisateur().getNomUtilisateur(langue) +"\n "; //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
			}
		}

		if(var.getVariableCount()>1){
			AVariableComposant op2 = var.getEnfant(1);
			if(op2 instanceof AVariableComposant){
				trouve = false;
				if(op2.getDescriptor().getM_AIdentificateurComposant().getCode()==-1){
					valide = false;
					chaine = Messages.getString("VbvsProvider.22") + op2.getDescriptor().getM_AIdentificateurComposant().getNom() +"\n "; //$NON-NLS-1$ //$NON-NLS-2$
				}
				else if(op2 instanceof VariableComposite){
					String chtmp= verifierValiditeVBV((VariableVirtuelle)op2);
					if(chtmp!=null)
						chaine+=chtmp;
				}
			}
		}
		if(!valide){
			chaine = Messages.getString("VbvsProvider.24") + var.getDescriptor().getM_AIdentificateurComposant().getNom() + ": " +chaine; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if(chaine.equals("")) //$NON-NLS-1$
			return null;

		else return chaine;
	}



	/**
	 * Affiche un message récapitulant les VBV en erreur: 
	 * opérandes non valorisés dans le parcours
	 * @return null si pas d'erreur, une chaine sinon
	 * 
	 */
	public String verifierValiditeVBVs(List<String> nomsVBVtoCheck){

		String chaine=""; //$NON-NLS-1$
		List<VariableVirtuelle> listeVBV = 
			vbvsMng.getListeVBV();
		this.vbvNonValides.clear();
		for (VariableVirtuelle virtuelle : listeVBV) {

			String chtmp= verifierValiditeVBV(virtuelle);
			if(chtmp!=null){
				chaine+=chtmp;
				if(nomsVBVtoCheck!=null ){
					if(nomsVBVtoCheck.contains(virtuelle.getDescriptor().getM_AIdentificateurComposant().getNom())){
						this.vbvNonValides.add(virtuelle);	
					}

				}else{
					this.vbvNonValides.add(virtuelle);
				}
			}		

		}

		if(chaine.equals("")) //$NON-NLS-1$
			return null;

		else return Messages.getString("VbvsProvider.25")+" \n " +chaine; //$NON-NLS-1$
	}

	public List<VariableVirtuelle> getVbvNonValides() {
		return vbvNonValides;
	}

	public void setVbvNonValides(List<VariableVirtuelle> vbvNonValides) {
		this.vbvNonValides = vbvNonValides;
	}

	public void clear() {
		vbvsMng=new GestionnaireVBV();
		vbvChangeListeners.clear();
		vbvNonValides.clear();
	}
	
	/**
	 * Calcul le niveau d'une VBV
	 * 
	 * @param vbv
	 * @return un entier
	 */
	private static int calculerNiveauVBV(VariableVirtuelle vbv) {
		int niveauOp1 = 0;
		int niveauOp2 = 0;

		if (vbv.getVariableCount() > 0) {
			if (vbv.getEnfant(0) instanceof VariableVirtuelle) {
				niveauOp1++;
				niveauOp1 += calculerNiveauVBV((VariableVirtuelle) vbv
						.getEnfant(0));
			}
		}
		if (vbv.getVariableCount() > 1) {
			if (vbv.getEnfant(1) instanceof VariableVirtuelle) {
				niveauOp2++;
				niveauOp2 += calculerNiveauVBV((VariableVirtuelle) vbv
						.getEnfant(1));
			}
		}

		if (niveauOp1 == 0 && niveauOp2 == 0) {
			niveauOp1++;
			niveauOp2++;
		}
		
		return Math.max(niveauOp1, niveauOp2);
	}
}
