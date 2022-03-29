package com.faiveley.samng.principal.ihm.vues.search;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.calcul.TailleBouton;
import com.faiveley.samng.principal.ihm.listeners.ISearchVariableListener;
import com.faiveley.samng.principal.ihm.listeners.ISearchVariableVirtuele;
import com.faiveley.samng.principal.ihm.vues.AbstractSelectionProviderVue;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableValeurLabel;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.LabelValeur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.GestionnaireVBV;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;

public class SearchVariable extends ASearchVariableDialog {

	private Button btnPrecedent;

	private Button btnSuivant;

	private Button btnCancel;

	/**
	 * Constructor
	 * 
	 * @param parent
	 */
	public SearchVariable(Shell parent) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);

	}

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param style
	 */
	public SearchVariable(Shell parent, int style) {
		super(parent, style);
	}

	/**
	 * Create the controls for the current dialog
	 */
	@Override
	protected void createControls(final Shell parentShell) {

		super.createControls(parentShell);
//		this.parent.setBounds(0, 0, 600, 400);
		// button "Previous"
		btnPrecedent = new Button(this.parent, SWT.NONE);
		int widthprec = TailleBouton.CalculTailleBouton(Messages.getString(
		"SearchVariable.0").length());
		btnPrecedent.setBounds(new Rectangle(300, 100, widthprec, 23)); // (50,
		// 100,
		// 80,
		// 23)
		btnPrecedent.setText(Messages.getString("SearchVariable.0")); //$NON-NLS-1$
		btnPrecedent.setToolTipText((Messages.getString("SearchVariable.0")));
		btnPrecedent.setEnabled(false);
		ButtonSelection sel = new ButtonSelection();
		sel.setNext(false);
		btnPrecedent.addSelectionListener(sel);

		// button "Next"
		btnSuivant = new Button(this.parent, SWT.NONE);
		int widthsuiv = TailleBouton.CalculTailleBouton(Messages.getString(
		"SearchVariable.1").length());
		btnSuivant.setBounds(new Rectangle(400, 100, widthsuiv, 23));
		btnSuivant.setText(Messages.getString("SearchVariable.1")); //$NON-NLS-1$
		btnSuivant.setToolTipText((Messages.getString("SearchVariable.1")));
		btnSuivant.setEnabled(false);
		sel = new ButtonSelection();
		sel.setNext(true);
		btnSuivant.addSelectionListener(sel);

		// button "Cancel"
		btnCancel = new Button(this.parent, SWT.NONE);
		int widthannul = TailleBouton.CalculTailleBouton(Messages.getString(
		"SearchVariable.2").length());
		btnCancel.setBounds(new Rectangle(500, 100, widthannul, 23));
		btnCancel.setText(Messages.getString("SearchVariable.2")); //$NON-NLS-1$
		btnCancel.setToolTipText((Messages.getString("SearchVariable.2")));
		btnCancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AbstractSelectionProviderVue.varSelectionnee.reset();
				SearchVariable.this.parent.close();
			}
		});	

		if (ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant()
				+ this.getTypeRecherche() + "select") != null){

			int indexToSelect=(Integer)ActivatorData.getInstance().getPoolDonneesVues().get(this.getAppelant()
					+ this.getTypeRecherche() + "select");

			this.itemsTable.setSelection(indexToSelect);
//			this.itemsTable.setTopIndex(PositionMilieuViewer.getPosition(indexToSelect));
//			this.itemsTable.forceFocus();
			selectValue();
		}

		this.selectText.forceFocus();
		this.selectText.setFocus();
	}

	/**
	 * Gets the size of the current dialog
	 */
	protected Point getSize() {
		return new Point(600, 200);
	}

	/**
	 * Fills the combo of the variables. Adds also the virtual variables if
	 * there is any view that implements ISearchVariableVirtuele
	 */
	protected void fillCombo() {
		super.fillCombo();
		if (this.values == null) {
			this.values = new LinkedHashMap<String, DescripteurVariable>();
		}

		IViewReference[] vr = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();
		for (IViewReference v : vr) {
			IViewPart view = v.getView(false);
			// search for the interface "SearchVariable"
			if (view instanceof ISearchVariableVirtuele) {
				GestionnaireVBV vbvGest = ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs();
				List<VariableVirtuelle> vbvs = vbvGest.getListeVBV();
				for (VariableVirtuelle vbv : vbvs) {
					String nom = vbv.getDescriptor().getM_AIdentificateurComposant().getNom();
					//issue 739
					if (this.getVariablesDuFiltre().contains(nom)) {
						this.comboVar.add(nom);
						this.values.put(nom, vbv.getDescriptor());
					}
				}
			}
		}
	}

	/**
	 * Button selection listener used by "Next" and "Previous" buttons
	 * 
	 * @author meggy
	 * 
	 */
	class ButtonSelection extends SelectionAdapter {
		private boolean isNext = false;

		/**
		 * Sets the directions of search
		 * 
		 * @param next
		 */
		public void setNext(boolean next) {
			this.isNext = next;
		}

		/**
		 * If the button is selected
		 */
		public void widgetSelected(SelectionEvent e) {
			DescripteurVariable descrVar = null;
			Operation op = null;
			String value = null;
			String stringValue = null;

			// get variable
			String varName = SearchVariable.this.selectedValue;
			if (!tableitemLongNameTable.isEmpty()) {
				varName = tableitemLongNameTable.get(SearchVariable.this.selectedItem);
			}
			if ((!varName.equals(NO_VARIABLE)) && (!varName.equals(ADV_SEARCH))) {
				descrVar = SearchVariable.this.values.get(varName);
			}

			// get operation
			for (Operation o : EnumSet.range(Operation.NoOperation,Operation.Change)) {
				if (o.value().equals(SearchVariable.this.comboOperation.getText())) {
					op = o;
					break;
				}
			}

			// get value
			if (SearchVariable.this.textValue instanceof Text) {
				value = ((Text) SearchVariable.this.textValue).getText();
				stringValue = value;
				if (op==Operation.NoOperation){
					op=null;
				}
				if (op!=null){
					if(value.trim().equals("")){
						MessageBox msgBox = new	MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING | SWT.OK);
						msgBox.setText(""); //$NON-NLS-1$
						msgBox.setMessage(Messages.getString("SearchVariable.5"));
						msgBox.open();
						return;
					}else{
						if(descrVar!=null){
							if (descrVar.getType() != com.faiveley.samng.principal.sm.data.descripteur.Type.string) {
								if (descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.int16||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.int24||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.int32||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.int64||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.int8||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.intXbits||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.real32||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.uint16||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.uint24||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.uint32||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.uint64||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.uint8||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.uintXbits||
										descrVar.getType() == com.faiveley.samng.principal.sm.data.descripteur.Type.real64							
								) {
									try {
										if (op==Operation.ShiftLeft) {
											//issue 731
											String value2 = value.replace(",", ".");
											int splito = value2.indexOf("...");
											Double.parseDouble(value2.substring(0, splito));
											Double.parseDouble(value2.substring(splito + 3, value2.length()));
										}else{
											Double.parseDouble(value);
										}
									} catch (Exception ex) {
										MessageBox msgBox = new	MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING | SWT.OK);
										msgBox.setText(""); //$NON-NLS-1$
										msgBox.setMessage(Messages.getString("SearchVariable.5"));
										//$NON-NLS-1$
										msgBox.open();
										return;
									}
								}
							}
						}
					}
				}
			} else {
				value = ((Combo) SearchVariable.this.textValue).getText();
				stringValue = value;
				if (op==Operation.NoOperation)
					op=null;
				
				boolean varTypNID_XX = false ;
				AVariableComposant var = GestionnairePool.getInstance().getVariable(descrVar.getM_AIdentificateurComposant().getCode());
				
				try {
					if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
						if (((DescripteurVariableDiscrete)descrVar).getLabels().
								get(Activator.getDefault().getCurrentLanguage()).
								get(0).getLabel().equals("$retirer$")) {
							
							varTypNID_XX = true ;
						}
					}
				} catch (Exception e1) {
					// La variable ne contient pas de table de labels
				}
				
				// Pour les variables de type NID_XX, il est possible de saisir "" comme valeur 
				// recherch�e. Cela correspond � chercher la valeur 0xFFFFFF.
				if (!varTypNID_XX) {
				
					if (op!=null){
						if(value.trim().equals("")){
							MessageBox msgBox = new	MessageBox(Display.getCurrent().getActiveShell(),SWT.ICON_WARNING | SWT.OK);
							msgBox.setText(""); 
							msgBox.setMessage(Messages.getString("SearchVariable.5"));
							msgBox.open();
							return;
						}
					}
				}
								
				if(var!=null){
					if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_DISCRETE) {
						TableValeurLabel valeurLabel = ((DescripteurVariableDiscrete) descrVar).getLabels();
						if (valeurLabel != null) {
							List<LabelValeur> listeLabelvaleur = valeurLabel.get(Activator.getDefault().getCurrentLanguage());
							if (listeLabelvaleur != null) {									
								int size = listeLabelvaleur.size();
								boolean trouve = false;
								int i = 0;
								String valeur = null;
								String label = null;
								
								// Pour rep�rer une variable de type NID_XX, il faut regarder si la variable est de 
								// type discrete, et qu'elle possede une table de label contenant le label "$retirer$".
								// La valeur associ�e a ce label doit �tre retir�e de la valeur lors de son affichage.
								// A contrario, cette valeur doit �tre ajout�e pour les traitements.
								if (!value.equals(ASearchVariableDialog.TOUSLESCHANGEMENTS)) {
									// Si la table de label est une table de suppression de caract�re...
									// Variable de type NID_XXX. Codage BCD (/4).
									if (listeLabelvaleur.get(0).getLabel().equals("$retirer$")) {
										int bcdSize = (var.getDescriptor().getTailleBits() / 4) ;
										
										/*String value2 = null ;
										
										if (value.contains("...")) {
											value2 = value.substring(value.indexOf("...") + "...".length()) ;
											value = value.substring(0, value.indexOf("...")) ;
										}*/
										
										// Ajout des caract�res retir�s � l'affichage (ex : 'f')
										for (i = value.length() ; i < bcdSize ; i++) {
											value = value + Integer.toString(Integer.parseInt((String) listeLabelvaleur.get(0).getValeurs()), 16) ;										
										}
										
										// Conversion de la cha�ne en valeur
										value = Long.toString(Long.parseLong(value, 16)) ;
										
										/*if (value2 != null) {
											// Ajout des caract�res retir�s � l'affichage (ex : 'f')
											for (i = value2.length() ; i < bcdSize ; i++) {
												value2 = value2 + Integer.toString(Integer.parseInt((String) listeLabelvaleur.get(0).getValeurs()), 16) ;										
											}
											
											value2 = Long.toString(Long.parseLong(value2, 16)) ;
											
											value = value + "..." + value2 ;
										}*/
																				
									} else {																
										while (!trouve && i < size) {
											label = listeLabelvaleur.get(i).getLabel();
											if (label.equals(value)) {
												trouve = true;
												valeur = (String) listeLabelvaleur.get(i).getValeurs();
											}
											i++;
										}
									}
									
									if (valeur != null) {
										value = valeur;
										if (valeur.equals(Messages.getString("ASearchVariableDialog.7")))
											value = new String(valeur).toLowerCase();
									}
								}
							}
						}
					}

					if (var.getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPOSEE) {
						TableValeurLabel valeurLabel = (TableValeurLabel) var.getDescriptor().getTableComposant(0);
						if (valeurLabel != null) {
							List<LabelValeur> listeLabelvaleur = valeurLabel.get(Activator.getDefault().getCurrentLanguage());
							if (listeLabelvaleur != null) {
								int size = listeLabelvaleur.size();
								boolean trouve = false;
								int i = 0;
								String valeur = null;
								String label = null;
								while (!trouve && i < size) {
									label = listeLabelvaleur.get(i).getLabel();
									// si on ajoute les valeurs apr�s le label
									// String valeurs = (String)listeLabelvaleur.get(i).getValeurs();
									// if(label.equals(value.substring(0,indexParentheseOuvrante)) && valeurs.equals(value.substring(indexParentheseOuvrante+1, indexOfParentheseFermante))){

									// si on ne regarde que le label
									if (label.equals(value)) {
										trouve = true;
										valeur = (String) listeLabelvaleur.get(i)
										.getLabel();
									}
									i++;
								}
								if (valeur != null)
									value = valeur;
							}
						}
					}
				}
				if (value.trim().equals("")) { //$NON-NLS-1$
					value = null;
				}
			}

			// get the opened views that implements ISearchVariableListener
			// and notify them
			// if (value != null) {
			IViewReference[] vr = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getViewReferences();

			if (op==Operation.NoOperation)
				op=null;
			if (op!=null)
				if ((!value.contains("...") || value.startsWith(".") || value.endsWith("."))
						&& (op.compareTo(Operation.ShiftLeft) == 0)) {

					MessageBox msgBox = new MessageBox(Display.getCurrent()
							.getActiveShell(), SWT.ICON_ERROR | SWT.OK);
					msgBox.setText(""); //$NON-NLS-1$
					msgBox.setMessage(com.faiveley.samng.principal.ihm.vues.search.Messages.getString("SearchVariable.11"));
					msgBox.open();
					return;
				}
			
			
			//test sens recherche
			//si le sens change, on ne garde pas la variable sauvegard�e
			if ((this.isNext && AbstractSelectionProviderVue.sensRecherche==1)
			||(!this.isNext && AbstractSelectionProviderVue.sensRecherche==-1)){
				AbstractSelectionProviderVue.varSelectionnee.reset();
			}
			
			//ajouter test de validit� si l'utilisateur fait une recherche sur la date
			//la date saisie doit etre au format jj/mm/aaaa

			for (IViewReference v : vr) {
				IViewPart view = v.getView(false);
				// search for the interface "SearchVariable"
				if (view instanceof ISearchVariableListener) {
					try {
						((ISearchVariableListener) view).onSearchVariable(descrVar, stringValue, value, op, this.isNext);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public void desactiverBoutonsRecherche() {
		this.btnPrecedent.setEnabled(false);
		this.btnSuivant.setEnabled(false);
	}

	@Override
	public void activerBoutonsRecherche() {
		this.btnPrecedent.setEnabled(true);
		this.btnSuivant.setEnabled(true);
	}

} // @jve:decl-index=0:visual-constraint="10,10,540,208"
