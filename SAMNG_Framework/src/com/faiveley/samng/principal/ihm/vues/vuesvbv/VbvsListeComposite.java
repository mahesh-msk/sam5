package com.faiveley.samng.principal.ihm.vues.vuesvbv;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.Operateur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VbvsListeComposite extends Composite implements PropertyChangeListener {

	private Label vbvsListLabel;
	
	private Table vbvsList;
	
	private Button deleteVbvsButton;
	private Button createNewVbvButton;
	private Button closeVbvsViewButton;
	private Composite vbvsButtonsOperationsComposite;
	
	private Action closeVbvsViewAction;

	private int prevSelectedIndex = -1;
	
	private VbvsProvider vbvsProvider;
	
	public VbvsListeComposite(Composite parent, int style) {
		super(parent, style);
		
		GridLayout mainCompositeLayout = new GridLayout();
		mainCompositeLayout.makeColumnsEqualWidth = true;
		setLayout(mainCompositeLayout);
		//construction de l'interface graphique de la vue des VBV
		createTopLabel();
		createVbvsList();
		createOperationsButtonsPanel();
	}
	
	/**
	 * 
	 * @param provider
	 */
	public void setVbvsProvider(VbvsProvider provider) {
		provider.addPropertyChangeListener(this);
		vbvsProvider = provider;
		provider.updateVbvsList(this);
	}

	
	/**
	 * Create the label of top
	 */
	private void createTopLabel() {
		// Create the top label for the left panel
		this.vbvsListLabel = new Label(this, SWT.SHADOW_IN | SWT.CENTER | SWT.WRAP | SWT.BORDER);
		GridData vbvsListLabelLData = new GridData();
		vbvsListLabelLData.heightHint = 22;
		vbvsListLabelLData.grabExcessHorizontalSpace = true;
		vbvsListLabelLData.horizontalAlignment = GridData.FILL;
		this.vbvsListLabel.setLayoutData(vbvsListLabelLData);
		this.vbvsListLabel.setText(Messages.getString("VbvsListeComposite.0")); //$NON-NLS-1$
		this.vbvsListLabel.setToolTipText((Messages.getString("VbvsListeComposite.0")));
	}
	
	/**
	 * Create the operations buttons: add, close, delete
	 *
	 */
	private void createOperationsButtonsPanel() {
		//create the panel for the two buttons
		this.vbvsButtonsOperationsComposite = new Composite(this, SWT.BORDER);
		FormLayout vbvsApplyCloseBtnsCompLayout = new FormLayout();
		GridData vbvsApplyCloseBtnsCompLData = new GridData();
		vbvsApplyCloseBtnsCompLData.grabExcessHorizontalSpace = true;
		vbvsApplyCloseBtnsCompLData.horizontalAlignment = GridData.FILL;
		vbvsApplyCloseBtnsCompLData.verticalAlignment = GridData.END;
		vbvsApplyCloseBtnsCompLData.heightHint = 35;
		this.vbvsButtonsOperationsComposite.setLayoutData(vbvsApplyCloseBtnsCompLData);
		this.vbvsButtonsOperationsComposite.setLayout(vbvsApplyCloseBtnsCompLayout);
		
		//Create the new  button
		this.createNewVbvButton = new Button(this.vbvsButtonsOperationsComposite, SWT.PUSH);
		FormData createNewVbvButtonLData = new FormData();
		createNewVbvButtonLData.height = 25;
		//createNewVbvButtonLData.width = 55;
		//position it near the center of the container panel
		createNewVbvButtonLData.right =  new FormAttachment(45, -4);
		createNewVbvButtonLData.bottom =  new FormAttachment(900, 1000, 0);
		this.createNewVbvButton.setLayoutData(createNewVbvButtonLData);
		this.createNewVbvButton.setText((Messages.getString("VbvsListeComposite.1"))); //$NON-NLS-1$
		this.createNewVbvButton.setToolTipText((Messages.getString("VbvsListeComposite.1"))); //$NON-NLS-1$
		this.createNewVbvButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				
				vbvsProvider.createNewVbv();
			}
		});

		//Create the close button
		this.deleteVbvsButton = new Button(this.vbvsButtonsOperationsComposite, SWT.PUSH);
		FormData deleteVbvsViewButtonLData = new FormData();
		deleteVbvsViewButtonLData.height = 25;
		//deleteVbvsViewButtonLData.width = 55;
		//align the close button to the right of the apply button
		deleteVbvsViewButtonLData.left =  new FormAttachment(this.createNewVbvButton, 4);
		deleteVbvsViewButtonLData.bottom =  new FormAttachment(900, 1000, 0);
		this.deleteVbvsButton.setLayoutData(deleteVbvsViewButtonLData);
		this.deleteVbvsButton.setText(Messages.getString("VbvsListeComposite.2")); //$NON-NLS-1$
		this.deleteVbvsButton.setToolTipText((Messages.getString("VbvsListeComposite.2")));
		this.deleteVbvsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				deleteSelectedVbvs();
			}
		});

		//Create the close button
		this.closeVbvsViewButton = new Button(this.vbvsButtonsOperationsComposite, SWT.PUSH);
		FormData closeVbvsViewButtonLData = new FormData();
		closeVbvsViewButtonLData.height = 25;
		//closeVbvsViewButtonLData.width = 55;
		//align the close button to the right of the apply button
		closeVbvsViewButtonLData.left =  new FormAttachment(this.deleteVbvsButton, 4);
		closeVbvsViewButtonLData.bottom =  new FormAttachment(900, 1000, 0);
		this.closeVbvsViewButton.setLayoutData(closeVbvsViewButtonLData);
		this.closeVbvsViewButton.setText(Messages.getString("VbvsListeComposite.3")); //$NON-NLS-1$
		this.closeVbvsViewButton.setToolTipText((Messages.getString("VbvsListeComposite.3")));
		this.closeVbvsViewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				super.widgetSelected(event);
				VbvsListeComposite.this.closeVbvsViewAction.run();
			}
		});
	}
	
	/**
	 * Création de la liste des VBV(panel de gauche)
	 *
	 */
	private void createVbvsList() {
		//Create the VBVs list of the left panel
		GridData vbvsListLData = new GridData();
		vbvsListLData.verticalAlignment = GridData.FILL;
		vbvsListLData.grabExcessVerticalSpace = true;
		vbvsListLData.grabExcessHorizontalSpace = true;
		vbvsListLData.horizontalAlignment = GridData.FILL;
		this.vbvsList = new Table(this, SWT.BORDER | SWT.MULTI);
		this.vbvsList.setLayoutData(vbvsListLData);
		this.vbvsList.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int selIdx = vbvsList.getSelectionIndex();
				if(selIdx < 0 || selIdx >= vbvsList.getItemCount())
					return;
				if (vbvsList.getSelectionCount()>1) {
					vbvsProvider.vbvMultiSelected(vbvsList);
				}else{

						TableItem tblItem = vbvsList.getItem(selIdx);
						
						vbvsProvider.vbvSelected(getVbvNameFromDisplayString(tblItem.getText()));
						
						prevSelectedIndex = selIdx;

				}
			}
		});
		
		this.vbvsList.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent keyevent) {
			}
			public void keyReleased(KeyEvent keyevent) {
				if(keyevent.keyCode == SWT.DEL) {
					deleteSelectedVbvs();
				}
			}
		});
		
//		 Disable native tooltip
		vbvsList.setToolTipText(""); //$NON-NLS-1$

	    // Implement a "fake" tooltip
	    final Listener labelListener = new Listener() {
	      public void handleEvent(Event event) {
	        Label label = (Label) event.widget;
	        Shell shell = label.getShell();
	        switch (event.type) {
	        case SWT.MouseDown:
	          Event e = new Event();
	          e.item = (TableItem) label.getData("_TABLEITEM"); 
	          
	          
	          //$NON-NLS-1$
	          // Assuming table is single select, set the selection as if
	          // the mouse down event went through to the table
	          vbvsList.setSelection(new TableItem[] { (TableItem) e.item });
	          
	          vbvsList.notifyListeners(SWT.Selection, e);
	        // fall through
	        case SWT.MouseExit:
	          shell.dispose();
	          break;
	        }
	      }
	    };

	    Listener tableListener = new Listener() {
	      Shell tip = null;

	      Label label = null;

	      public void handleEvent(Event event) {
	        switch (event.type) {
	        case SWT.Dispose:
	        case SWT.KeyDown:
	        case SWT.MouseMove: {
	          if (tip == null)
	            break;
	          tip.dispose();
	          tip = null;
	          label = null;
	          break;
	        }
	        case SWT.MouseHover: {
	          TableItem item = vbvsList.getItem(new Point(event.x, event.y));
	          if (item != null) {
	            if (tip != null && !tip.isDisposed())
	              tip.dispose();
	            tip = new Shell(vbvsList.getDisplay().getActiveShell(), SWT.ON_TOP | SWT.TOOL);
	            tip.setLayout(new FillLayout());
	            label = new Label(tip, SWT.NONE);
	            label.setForeground(getDisplay()
	                .getSystemColor(SWT.COLOR_INFO_FOREGROUND));
	            label.setBackground(getDisplay()
	                .getSystemColor(SWT.COLOR_INFO_BACKGROUND));
	            label.setData("_TABLEITEM", item); //$NON-NLS-1$
	            label.setText(item.getText());
	            label.addListener(SWT.MouseExit, labelListener);
	            label.addListener(SWT.MouseDown, labelListener);
	            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
	            Rectangle rect = item.getBounds(0);
	            //we possition the tooltip below the mouse pointer
	            //we add 15 to be displayed below the mouse pointer and not 
	            //to have the mouse pointer covering the tooltip
	            Point pt = vbvsList.toDisplay(event.x, rect.y + size.y + 12);
	            tip.setBounds(pt.x, pt.y, size.x, size.y);
	            tip.setVisible(true);
	          }
	        }
	        }
	      }
	    };
	    vbvsList.addListener(SWT.Dispose, tableListener);
	    vbvsList.addListener(SWT.KeyDown, tableListener);
	    vbvsList.addListener(SWT.MouseMove, tableListener);
	    vbvsList.addListener(SWT.MouseHover, tableListener);
	}
	
	public void setCloseButtonAction(Action action) {
		if(action != null)
			this.closeVbvsViewAction = action;
	}

	@Override
	public void dispose() {
		super.dispose();
		this.vbvsProvider.removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if ("VBV_SAVED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVbvSaved(evt);
			return;
		}
		if("VBV_EDIT_CANCEL".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVbvEditCancel(evt);
			return;	
		}
		if("VBV_CREATED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVbvCreated(evt);
			return;
		}
		if("VBV_DELETED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVbvDeleted(evt);
			return;
		}
		
		if("VBVS_UPDATE".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVbvsListUpdate(evt);
			return;
		}
		if("VBV_CONTENT_CHANGED".equals(evt.getPropertyName())) { //$NON-NLS-1$
			onVbvContentChanged(evt);
			return;
		}
	}
	
	/** 
	 * Handles the VBV_SAVED event. It enables also the buttons for create, 
	 * duplicate and delete and sends to the apply action a message to apply also 
	 * the saved vbv
	 * @param evt
	 */
	protected void onVbvSaved(PropertyChangeEvent evt) {
		VariableVirtuelle vbv = (VariableVirtuelle)evt.getNewValue();
		if(vbv == null)
			return;
		int selIdx = vbvsList.getSelectionIndex();
		vbvsList.setSelection(selIdx);
		if(selIdx == -1)
			return; //$NON-NLS-1$
		TableItem tblItem = vbvsList.getItem(selIdx);
		tblItem.setText(getDisplayVbvString(vbv));
		
		//vbvsList.setItem(selIdx, getDisplayVbvString(vbv));
		enableView(true);
	}
	
	protected void onVbvEditCancel(PropertyChangeEvent evt) {
		VariableVirtuelle vbv = (VariableVirtuelle)evt.getOldValue();
		if(vbv == null )
			return;
		int selIdx = vbvsList.getSelectionIndex();
		if(selIdx == -1)
			throw new RuntimeException("No selection in the VBVs list"); //$NON-NLS-1$
		Object newFiltre = evt.getNewValue();
		if(newFiltre == null) {	//we had a new VBV that was canceled
			vbvsList.remove(selIdx);
			if(vbvsList.getItemCount() > 0) {
				//select the new added VBV
				vbvsList.setSelection(0);
				//: It seems that List does not fires an selection event after the previous call
				//		maybe we can use the public available Widget.notifyListeners method for an explicit, 
				//		programmtically enforced event notification (in this case with
				//		type SWT.Selection).
				TableItem tblItem = vbvsList.getItem(0);
				vbvsProvider.vbvSelected(getVbvNameFromDisplayString(tblItem.getText()));
				prevSelectedIndex = 0;
			} else {
				prevSelectedIndex = -1;
			}
		} else {
			TableItem tblItem = vbvsList.getItem(selIdx);
			tblItem.setText(getDisplayVbvString(vbv));
			//vbvsList.setItem(selIdx, getDisplayVbvString(vbv));
			vbvsProvider.vbvSelected(getVbvNameFromDisplayString(tblItem.getText()));
			prevSelectedIndex = selIdx;
		}
		enableView(true);
	}
	
	protected void onVbvCreated(PropertyChangeEvent evt) {
		VariableVirtuelle vbv = (VariableVirtuelle)evt.getNewValue();
		if(vbv == null)
			return;
		//add the new VBV name to the VBVs list
		TableItem tblItem = new TableItem(vbvsList, SWT.NONE);
		tblItem.setText(getDisplayVbvString(vbv));
		//vbvsList.add(getDisplayVbvString(vbv));
		//select the new added VBV
		int selIdx = vbvsList.getItemCount() - 1;
		vbvsList.setSelection(selIdx);
		tblItem = vbvsList.getItem(selIdx);
		vbvsProvider.vbvSelected(getVbvNameFromDisplayString(tblItem.getText()));
		prevSelectedIndex = selIdx;
		enableView(false);
	}
	
	protected void onVbvDeleted(PropertyChangeEvent evt) {
		VariableVirtuelle vbv = (VariableVirtuelle)evt.getOldValue();
		if(vbv == null)
			return;

		
//		//removes the VBV name from the VBVs list
//		int selIdx = vbvsList.getSelectionIndex();
//		if(selIdx == -1)
//			throw new RuntimeException("No selection in the VBVs list"); //$NON-NLS-1$
//		vbvsList.remove(selIdx);
//		if(vbvsList.getItemCount() > 0) {
//			//select the new added VBV
//			vbvsList.setSelection(0);
//			//: It seems that List does not fires an selection event after the previous call
//			//		maybe we can use the public available Widget.notifyListeners method for an explicit, 
//			//		programmtically enforced event notification (in this case with
//			//		type SWT.Selection).
//			TableItem tblItem = vbvsList.getItem(0);
//			vbvsProvider.vbvSelected(getVbvNameFromDisplayString(tblItem.getText()));
//			prevSelectedIndex = 0;
//		} else {
//			prevSelectedIndex = -1;
//		}

		TableItem[] selIdx = vbvsList.getItems();
		for (int i = selIdx.length-1; i >=0 ; i--) {
			if ((getDisplayVbvString(vbv)).equals(selIdx[i].getText())) {
				vbvsList.remove(i);
			}		
		}
		if(vbvsList.getItemCount() > 0) {
			//select the new added filter
			vbvsList.setSelection(0);
			//: It seems that List does not fires an selection event after the previous call
			//		maybe we can use the public available Widget.notifyListeners method for an explicit, 
			//		programmtically enforced event notification (in this case with
			//		type SWT.Selection).
			vbvsProvider.vbvSelected(getVbvNameFromDisplayString(vbvsList.getItem(0).getText()));
			prevSelectedIndex = 0;
		} else {
			prevSelectedIndex = -1;
			createNewVbvButton.setEnabled(true);
		}
	}
	
	protected void onVbvsListUpdate(PropertyChangeEvent evt) {
		@SuppressWarnings("unchecked")
		java.util.List<VariableVirtuelle> currentVbvs = (java.util.List<VariableVirtuelle>)evt.getNewValue();
		//we should have a non-null value here
		int ind=vbvsList.getSelectionIndex();
		if(currentVbvs == null)
			throw new IllegalArgumentException("Invalid VBVs list received"); //$NON-NLS-1$
		vbvsList.removeAll();	//clear the current list of VBVs
		TableItem tblItem;
		ActivatorData.getInstance().getProviderVBVs().verifierValiditeVBVs(null);
		
		ArrayList<String> listvbv = new ArrayList<String>();
		for(VariableVirtuelle vbv0: currentVbvs) {
			if(vbv0 != null) {
				listvbv.add(getDisplayVbvString(vbv0));
			}
		}
		java.util.Collections.sort(listvbv);
		ArrayList<TableItem> listTableItem = new ArrayList<TableItem>();
		//SUPPR_ITERATOR
//		for (Iterator iter = listvbv.iterator(); iter.hasNext();) {
//			String element = (String) iter.next();
//			tblItem = new TableItem(vbvsList, SWT.NONE);
//			tblItem.setText(element);
//			listTableItem.add(tblItem);
//		}
		for (String str : listvbv) {
			tblItem = new TableItem(vbvsList, SWT.NONE);
			tblItem.setText(str);
			listTableItem.add(tblItem);
		}
		

//		tblItem.setText();
//				vérification de la validité des VBV
		for(VariableVirtuelle vbv: currentVbvs) {
			if(vbv != null) {
				//SUPPR_ITERATOR
//				for (Iterator iter = listTableItem.iterator(); iter.hasNext();) {
//					TableItem element = (TableItem) iter.next();
//					if (element.getText().equals(getDisplayVbvString(vbv))) {
//						tblItem = element;
////						vérification de la validité des VBV
//						
////						 Check to see if the deleted vbv is a VBV that is used in the VBVs
//						List<VariableVirtuelle> listVBVs = currentVbvs;
//						boolean isUsed;
//						String vbvName = vbv.getDescriptor().getM_AIdentificateurComposant().getNom();
//						String usingVarName;
//						for (VariableVirtuelle vbv2 : listVBVs) {
//							isUsed = false;
//							usingVarName = vbv2.getDescriptor().getM_AIdentificateurComposant()
//									.getNom();
//							if (vbv2.getEnfant(0) != null) {
//								if (vbvName.equals(vbv2.getEnfant(0).getDescriptor()
//										.getM_AIdentificateurComposant().getNom())) {
//									isUsed = true;
//								}
//							}
//							
//								if (vbv2.getVariableCount() == 2
//										&& vbvName.equals(vbv2.getEnfant(1).getDescriptor()
//												.getM_AIdentificateurComposant().getNom()))
//									isUsed = true;
//							
//							if (isUsed) {
//								tblItem.setForeground(new Color(Display.getCurrent(),
//										153, 153, 153));
//							}
//
//						}
//						
//						
//						
//						if(vbvsProvider.getVbvNonValides().contains(vbv)){
//							
//							
//							tblItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
//							
//						}
//						//vbvsList.add(getDisplayVbvString(vbv));
//						break;
//					}
//				}
				for (TableItem element: listTableItem) {
				//TableItem element = (TableItem) iter.next();
				if (element.getText().equals(getDisplayVbvString(vbv))) {
					tblItem = element;
//					vérification de la validité des VBV
					
//					 Check to see if the deleted vbv is a VBV that is used in the VBVs
					List<VariableVirtuelle> listVBVs = currentVbvs;
					boolean isUsed;
					String vbvName = vbv.getDescriptor().getM_AIdentificateurComposant().getNom();
					for (VariableVirtuelle vbv2 : listVBVs) {
						isUsed = false;
						if (vbv2.getEnfant(0) != null) {
							if (vbvName.equals(vbv2.getEnfant(0).getDescriptor()
									.getM_AIdentificateurComposant().getNom())) {
								isUsed = true;
							}
						}
						
							if (vbv2.getVariableCount() == 2 && vbv2.getEnfant(1)!=null
									&& vbvName.equals(vbv2.getEnfant(1).getDescriptor()
											.getM_AIdentificateurComposant().getNom()))
								isUsed = true;
						
						if (isUsed) {
							tblItem.setForeground(new Color(Display.getCurrent(),
									153, 153, 153));
						}

					}
					
					
					
					if(vbvsProvider.getVbvNonValides().contains(vbv)){
						
						
						tblItem.setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
						
					}
					//vbvsList.add(getDisplayVbvString(vbv));
					break;
				}
			}
			}
		}
		

		
		if(currentVbvs.size() > 0) {
			if (currentVbvs.size()>ind) {
				vbvsList.setSelection(ind);
				tblItem = vbvsList.getItem(0);
				vbvsProvider.vbvSelected(getVbvNameFromDisplayString(tblItem.getText()));
				prevSelectedIndex = ind;
				enableView(true);	//enable delete and duplicate buttons
			}
			
		} else {
			prevSelectedIndex = -1;
		}
	}
	
	protected void onVbvContentChanged(PropertyChangeEvent evt) {
		VariableVirtuelle filtre = (VariableVirtuelle)evt.getNewValue();
		if(filtre == null)
			return;
		Boolean changeState = (Boolean)evt.getOldValue();
		if(changeState == null)
			changeState = true;
		enableView(changeState);
	}

	private void enableView(boolean enabled) {
		vbvsList.setEnabled(enabled);
		createNewVbvButton.setEnabled(enabled);
		deleteVbvsButton.setEnabled(enabled);
	}
	
	/**
	 * Display the VBV in the list of left
	 * @param vbv
	 * @return
	 */
	private String getDisplayVbvString(VariableVirtuelle vbv) {
		Langage lang = Activator.getDefault().getCurrentLanguage();
		
		
		String displayStr = vbv.getDescriptor().getM_AIdentificateurComposant().getNom() + " = "; //$NON-NLS-1$
		
		AVariableComposant firstOperand = vbv.getVariableCount() == 0 ? null : vbv.getEnfant(0);
		
		
		
		//: nom utilisateur should be used here
		String strFirstOperand  = null;
		if(firstOperand!=null){
		if( firstOperand.getDescriptor().getNomUtilisateur()==null)
			strFirstOperand = firstOperand == null ? null : firstOperand.getDescriptor().getM_AIdentificateurComposant().getNom();
		else if(firstOperand.getDescriptor().getNomUtilisateur().getNomUtilisateur(lang)==null)
			strFirstOperand = firstOperand == null ? null : firstOperand.getDescriptor().getM_AIdentificateurComposant().getNom();
		else strFirstOperand = firstOperand == null ? null :firstOperand.getDescriptor().getNomUtilisateur().getNomUtilisateur(lang);
		}
		Operateur operator = vbv.getM_Operateur();
		String strOperator = operator == null ? null : operator.getStringValue();
		AVariableComposant secondOperand = vbv.getVariableCount() < 2 ? null : vbv.getEnfant(1);
		String strSecondOperand = null;
		if(secondOperand != null){
			
			if( secondOperand.getDescriptor().getNomUtilisateur()==null)
				strSecondOperand = secondOperand.getDescriptor().getM_AIdentificateurComposant().getNom();
			else if(secondOperand.getDescriptor().getNomUtilisateur().getNomUtilisateur(lang)==null)
				strSecondOperand = secondOperand.getDescriptor().getM_AIdentificateurComposant().getNom();
			else 
				strSecondOperand =secondOperand.getDescriptor().getNomUtilisateur().getNomUtilisateur(lang);
			
		}else {
			Object value = vbv.getValeurObjet();//tagValCor
			if(value != null && value instanceof String)
				strSecondOperand = (String)value;
		}
		if(strFirstOperand != null && strOperator != null && strSecondOperand != null) {
			displayStr += "( " + strFirstOperand + " " + strOperator + " " + strSecondOperand + " )"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}
		
		return displayStr;
	}
	
	private String getVbvNameFromDisplayString(String vbvDisplayString) {
		int sepIdx = vbvDisplayString.indexOf(" = "); //$NON-NLS-1$
		if(sepIdx > 0) 
			return vbvDisplayString.substring(0, sepIdx);
		return null;
	}
	
	private void deleteSelectedVbvs() {
//		int selIdx = vbvsList.getSelectionIndex();
//		if(selIdx < 0 || selIdx >= vbvsList.getItemCount())
//			return;
//		TableItem tblItem = vbvsList.getItem(selIdx);
//		vbvsProvider.deleteVbv(getVbvNameFromDisplayString(tblItem.getText()));
//		

		
		
    	TableItem[] selString = vbvsList.getSelection();
		for (int i = selString.length-1; i >= 0; i--) {
			vbvsProvider.deleteVbv(getVbvNameFromDisplayString(selString[i].getText()));
		}
		enableView(true);
	}
}
