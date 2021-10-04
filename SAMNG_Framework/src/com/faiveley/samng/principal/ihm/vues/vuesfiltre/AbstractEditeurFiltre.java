package com.faiveley.samng.principal.ihm.vues.vuesfiltre;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.TypeFiltre;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public abstract class AbstractEditeurFiltre extends Composite
    implements PropertyChangeListener {
    protected Label filterDefinitionLabel;
    protected Composite filterNameEditorComposite;
    protected Button addVariable;
    protected Label filterNameLabel;
    protected Text filterNameText;
    protected Composite filterEditorButtonsComposite;
    protected Button cancelCreateFilterButton;
    protected Button saveFilterButton;
    protected StyledText helpStyledText;
    protected AFiltreComposant editingFilter;
    protected TypeFiltre acceptedFilterType;
    protected AbstractProviderFiltre filtersProvider; 

    protected boolean orderableButtons;
    protected Button moveFirstButton;
    protected Button moveLastButton;
    protected Button moveUpButton;
    protected Button moveDownButton;
    
    
    public String openVueFiltreHelp = ""; //$NON-NLS-1$
    public String selectFiltreHelp = ""; //$NON-NLS-1$
    public String creerFiltreHelp = ""; //$NON-NLS-1$
    public String dupliquerFiltreHelp = Messages.getString("AbstractEditeurFiltre.11"); //$NON-NLS-1$
    

    protected Action moveFirstButtonAction;
    protected Action moveLastButtonAction;
    protected Action moveUpButtonAction;
    protected Action moveDownButtonAction;

    
    public AbstractEditeurFiltre(Composite parent, int style,
        TypeFiltre filterType, boolean showOrderButtons) {
        super(parent, style);
        this.orderableButtons = showOrderButtons;

        GridLayout composite1Layout = new GridLayout();
        composite1Layout.makeColumnsEqualWidth = true;
        setLayout(composite1Layout);

        createTopLabel();
        createFilterNameEditorPanel();

        createFilterEventsEditorPanel(); //: 

        createFilterEditorButtonsPanel();
        createHelpComponent();
        acceptedFilterType = filterType;
        
        
    }
    
	public void setHelpMsgbasic(){
		if(filtersProvider.getGestionnaireFiltres().getListeFiltres().getEnfantCount()<1){
			helpStyledText.setText(openVueFiltreHelp);
		}else{
			helpStyledText.setText(selectFiltreHelp);
		}
	}
	/**
	Méthode permettant de définir un message dans la zone d'aide de l'éditeur
	@param msg: message de la zone d'aide
	*/
	public void setHelpMsgbasic(String msg){
			helpStyledText.setText(msg);
	}
    
	public void setFiltersProvider(AbstractProviderFiltre provider) {
        //: remove the listener on dispose
		provider.addPropertyChangeListener(this);
		filtersProvider = provider;
		setHelpMsgbasic();
    }

    public void setAcceptedFilterType(TypeFiltre filterType) {
        acceptedFilterType = filterType;
    }

    private void createTopLabel() {
        filterDefinitionLabel = new Label(this,
                SWT.SHADOW_IN | SWT.CENTER | SWT.BORDER);

        GridData filterDefinitionLabelLData = new GridData();
        filterDefinitionLabelLData.grabExcessHorizontalSpace = true;
        filterDefinitionLabelLData.horizontalAlignment = GridData.FILL;
        filterDefinitionLabelLData.heightHint = 23;
        filterDefinitionLabel.setLayoutData(filterDefinitionLabelLData);
        filterDefinitionLabel.setText(Messages.getString("AbstractEditeurFiltre.0")); //$NON-NLS-1$
        filterDefinitionLabel.setToolTipText((Messages.getString("AbstractEditeurFiltre.0"))); //$NON-NLS-1$
    }

    private void createFilterNameEditorPanel() {
        filterNameEditorComposite = new Composite(this, SWT.BORDER);

        FormLayout filterNameEditorCompositeLayout = new FormLayout();
        GridData filterNameEditorCompositeLData = new GridData();
        filterNameEditorCompositeLData.heightHint = 30;
        filterNameEditorCompositeLData.verticalAlignment = GridData.BEGINNING;
        filterNameEditorCompositeLData.horizontalAlignment = GridData.FILL;
        filterNameEditorCompositeLData.grabExcessHorizontalSpace = true;
        filterNameEditorComposite.setLayoutData(filterNameEditorCompositeLData);
        filterNameEditorComposite.setLayout(filterNameEditorCompositeLayout);

        filterNameLabel = new Label(filterNameEditorComposite, SWT.CENTER);

        FormData filterNameLabelLData = new FormData();
        filterNameLabelLData.width = 64;
        filterNameLabelLData.height = 24;
        filterNameLabelLData.left = new FormAttachment(11, 1000, 0);
        filterNameLabelLData.right = new FormAttachment(217, 1000, 0);
        filterNameLabelLData.top = new FormAttachment(116, 1000, 0);
        filterNameLabelLData.bottom = new FormAttachment(916, 1000, 0);
        filterNameLabel.setLayoutData(filterNameLabelLData);
        filterNameLabel.setText(Messages.getString("AbstractEditeurFiltre.1")); //$NON-NLS-1$
        filterNameLabel.setToolTipText((Messages.getString("AbstractEditeurFiltre.2"))); //$NON-NLS-1$

        filterNameText = new Text(filterNameEditorComposite,
                SWT.SINGLE | SWT.LEFT);

        FormData filterNameTextLData = new FormData();
        filterNameTextLData.width = 233;
        filterNameTextLData.height = 23;
        filterNameTextLData.left = new FormAttachment(226, 1000, 0);
        filterNameTextLData.right = new FormAttachment(995, 1000, 0);
        filterNameTextLData.top = new FormAttachment(83, 1000, 0);
        filterNameTextLData.bottom = new FormAttachment(850, 1000, 0);
        filterNameText.setLayoutData(filterNameTextLData);
        filterNameText.setText(""); //$NON-NLS-1$
        filterNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (!filterNameText.isFocusControl())
				      return;
				checkEditingFilterChange();
			}
        });
    }

    private void createFilterEditorButtonsPanel() {
        int insetX = 4;

        filterEditorButtonsComposite = new Composite(this, SWT.BORDER);

        FormLayout filterEditorButtonsCompositeLayout = new FormLayout();
        filterEditorButtonsComposite.setLayout(filterEditorButtonsCompositeLayout);
        
        GridData filterEditorButtonsCompositeLData = new GridData();
        filterEditorButtonsCompositeLData.horizontalAlignment = GridData.FILL;
        filterEditorButtonsCompositeLData.heightHint = 29;
        filterEditorButtonsCompositeLData.minimumWidth=100;
        filterEditorButtonsCompositeLData.grabExcessHorizontalSpace = true;
        filterEditorButtonsComposite.setLayoutData(filterEditorButtonsCompositeLData);
        filterEditorButtonsComposite.setBounds(6, 70, 295, 33);

        createOrderButtons();
        
        // Create the cancel button and position it to the right margin of the panel
        
        //Create the save buuton and position it to the left of the cancel button
        saveFilterButton = new Button(filterEditorButtonsComposite, SWT.PUSH);

        FormData saveFilterButtonLData = new FormData();
        saveFilterButtonLData.left = new FormAttachment(this.moveLastButton, insetX);
        saveFilterButtonLData.bottom = new FormAttachment(1000, 1000, -2);
        saveFilterButtonLData.height = 25;
        //saveFilterButtonLData.width = TailleBouton.CalculTailleBouton(Messages.getString("AbstractEditeurFiltre.4").length()); //80
        saveFilterButton.setLayoutData(saveFilterButtonLData);
        saveFilterButton.setText(Messages.getString("AbstractEditeurFiltre.4")); //$NON-NLS-1$
        saveFilterButton.setToolTipText((Messages.getString("AbstractEditeurFiltre.4"))); //$NON-NLS-1$
        saveFilterButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                	saveEditingFilter();
                	setHelpMsgbasic();
                }
            });
        
        cancelCreateFilterButton = new Button(filterEditorButtonsComposite,
                SWT.PUSH);
        cancelCreateFilterButton.setText(Messages.getString("AbstractEditeurFiltre.3")); //$NON-NLS-1$
        cancelCreateFilterButton.setToolTipText((Messages.getString("AbstractEditeurFiltre.3"))); //$NON-NLS-1$
        cancelCreateFilterButton.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    super.widgetSelected(e);
                    filtersProvider.filterEditingCanceled(AbstractEditeurFiltre.this.editingFilter);
                    setHelpMsgbasic();
                }
            });
        
        FormData cancelCreateFilterButtonLData = new FormData();
        cancelCreateFilterButtonLData.left = new FormAttachment(this.saveFilterButton,insetX);
        cancelCreateFilterButtonLData.bottom = new FormAttachment(1000, 1000, -2);
        cancelCreateFilterButtonLData.height = 25;
        //cancelCreateFilterButtonLData.width = TailleBouton.CalculTailleBouton(Messages.getString("AbstractEditeurFiltre.3").length()); //50
        cancelCreateFilterButton.setLayoutData(cancelCreateFilterButtonLData);
        
//        addVariable = new Button(filterEditorButtonsComposite,
//                SWT.PUSH);
//        addVariable.setText("Ajouter Variables"); //$NON-NLS-1$
//        addVariable.setToolTipText((Messages.getString("Ajouter Variables"))); //$NON-NLS-1$
//        addVariable.addSelectionListener(new SelectionAdapter() {
//                public void widgetSelected(SelectionEvent e) {
//                    super.widgetSelected(e);
//                    ajouterVariabledansFiltre();
//                    setHelpMsgbasic();
//                }
//            });
//
//        FormData addVCreateFilterButtonLData = new FormData();
//        addVCreateFilterButtonLData.left = new FormAttachment(this.cancelCreateFilterButton,insetX);
//        addVCreateFilterButtonLData.bottom = new FormAttachment(1000, 1000, -2);
//        addVCreateFilterButtonLData.height = 25;
//        //cancelCreateFilterButtonLData.width = TailleBouton.CalculTailleBouton(Messages.getString("AbstractEditeurFiltre.3").length()); //50
//        addVariable.setLayoutData(addVCreateFilterButtonLData);

    }
    
    protected abstract void ajouterVariabledansFiltre();

    private void createHelpComponent() {
        helpStyledText = new StyledText(this, SWT.BORDER | SWT.V_SCROLL);

        GridData helpStyledTextLData = new GridData();
        helpStyledTextLData.grabExcessHorizontalSpace = true;
        helpStyledTextLData.horizontalAlignment = GridData.FILL;
        helpStyledTextLData.verticalAlignment = GridData.END;
        helpStyledTextLData.heightHint = 85;
        helpStyledText.setLayoutData(helpStyledTextLData);
        helpStyledText.setText(Messages.getString("AbstractEditeurFiltre.5")); //$NON-NLS-1$
        helpStyledText.setToolTipText((Messages.getString("AbstractEditeurFiltre.5"))); //$NON-NLS-1$
        helpStyledText.setBackground(new Color(null, 255, 255, 204));
        helpStyledText.setWordWrap(true);
        
        
       
    }

    protected abstract void createFilterEventsEditorPanel();
    protected abstract boolean prepareFilterSaving();
    
    public boolean checkEditingFilterChange() {
    	if(editingFilter == null)
    		return false;
    	return !filterNameText.getText().equals(editingFilter.getNom());
    	
    }

    public void propertyChange(PropertyChangeEvent evt) {
    	String evPropName = evt.getPropertyName();
    	    	        
    	if ("FILTER_SELECTED".equals(evPropName)) { //$NON-NLS-1$
        	enableComponents(true);
            AFiltreComposant filtre = (AFiltreComposant) evt.getNewValue();
            if(filtre == null) {
            	filterNameText.setText(""); //$NON-NLS-1$
            }
            //: set the type of the filter 
            filterNameText.setText(filtre.getNom());
            filterNameText.setToolTipText((filtre.getNom()));
            if(this.editingFilter != filtre)
               	enableCancelSaveButtons(false);
            this.editingFilter = filtre;
			if("FILTER_CREATED".equals(evt.getPropertyName())){ //$NON-NLS-1$
				if (evt.getOldValue() != null){
					helpStyledText.setText(dupliquerFiltreHelp);
				}else{
					helpStyledText.setText(creerFiltreHelp);
				}
				
			}
    		return;
    		
        }else if ("FILTER_CREATED".equals(evPropName)) { //$NON-NLS-1$
        	enableComponents(true);
            AFiltreComposant filtre = (AFiltreComposant) evt.getNewValue();
            if(filtre == null) {
            	filterNameText.setText(""); //$NON-NLS-1$
            }
            //: set the type of the filter 
            filterNameText.setText(filtre.getNom());
            filterNameText.setToolTipText((filtre.getNom()));
            if(this.editingFilter != filtre)
               	enableCancelSaveButtons(false);
            this.editingFilter = filtre;
			if("FILTER_CREATED".equals(evt.getPropertyName())){ //$NON-NLS-1$
				if (evt.getOldValue() != null){
					helpStyledText.setText(dupliquerFiltreHelp);
				}else{
					helpStyledText.setText(creerFiltreHelp);
				}
				
			}
    		return;
        }
		if("FILTER_CONTENT_CHANGED".equals(evPropName)) { //$NON-NLS-1$
			Boolean restored = (Boolean)evt.getOldValue();
			if(restored == null)
				restored = true;
			enableCancelSaveButtons(!restored);
			return;
		}
		
		if ("FILTER_DUPLICATED".equals(evt.getPropertyName()) ) { //$NON-NLS-1$
			enableComponents(true);
			
		}
		if("FILTER_SAVED".equals(evPropName)) { //$NON-NLS-1$
			enableCancelSaveButtons(false);
			return;
		}
		if ("FILTER_DELETED".equals(evPropName)) { //$NON-NLS-1$
			enableCancelSaveButtons(false);
			enableComponents(false);
				if (evt.getOldValue() != null){
					helpStyledText.setText(openVueFiltreHelp);
				}
			return;
		}
		if ("FILTER_MULTI_SELECTED".equals(evPropName)) { //$NON-NLS-1$
			enableCancelSaveButtons(false);
			enableComponents(false);
			return;
		}
		
    }

    protected void enableComponents(boolean enabled) {
        filterNameText.setEnabled(enabled);
        //: management for these buttons should be done separatelly
        //		as they should be disabled on simple selection
        if(!enabled)
        	enableCancelSaveButtons(enabled);
    }
    
    public abstract boolean filtreEnregistrable();
    
    protected void enableCancelSaveButtons(boolean enabled) {
        cancelCreateFilterButton.setEnabled(enabled);
        saveFilterButton.setEnabled(enabled && filtreEnregistrable());
    }
    public abstract boolean filtrevalide();
    
    public void saveEditingFilter() {
    	if (prepareFilterSaving()) {
    		if (filtrevalide()) {
    			filtersProvider.saveFilter(editingFilter, filterNameText.getText());
    			//notify childs in hierarchy to put anything they have 
			}else {
				MessageBox msgBox3 = new MessageBox(Display.getCurrent()
						.getActiveShell(), SWT.ICON_WARNING | SWT.YES);
				msgBox3.setText(Messages.getString("AbstractEditeurFiltre.14"));  //$NON-NLS-1$
				msgBox3.setMessage(Messages.getString("AbstractEditeurFiltre.15"));  //$NON-NLS-1$
				msgBox3.open();
			}
    		
    	}else {
    		MessageBox msgBox2 = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_WARNING | SWT.YES);
			msgBox2.setText(Messages.getString("AbstractEditeurFiltre.13"));  //$NON-NLS-1$
			msgBox2.setMessage(Messages.getString("AbstractEditeurFiltre.12"));  //$NON-NLS-1$
			msgBox2.open();
		}
    }
    
    @Override
	public void dispose() {
		super.dispose();
		this.filtersProvider.removePropertyChangeListener(this);
	}
    
    
    private void createOrderButtons() {
        if(orderableButtons) {
			//Create the apply button
			moveFirstButton = new Button(this.filterEditorButtonsComposite, SWT.PUSH);
			FormData moveFirstButtonLData = new FormData();
			moveFirstButtonLData.height = 25;
			moveFirstButtonLData.width = 50;
			//position it near the center of the container panel
			moveFirstButtonLData.right =  new FormAttachment(162, 1000, 0);
			moveFirstButtonLData.bottom =  new FormAttachment(1000, 1000, -2);
			moveFirstButton.setLayoutData(moveFirstButtonLData);
			moveFirstButton.setToolTipText(Messages.getString("AbstractEditeurFiltre.6")); //$NON-NLS-1$
			moveFirstButton.setImage(new Image(getDisplay(), 
					getClass().getResourceAsStream("/icons/btnMoveTop.gif"))); //$NON-NLS-1$
			moveFirstButton.setEnabled(false);
			moveFirstButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if(moveFirstButtonAction != null)
						moveFirstButtonAction.run();
				}
			});
			
			
	
			//Create the close button
			moveUpButton = new Button(this.filterEditorButtonsComposite, SWT.PUSH);
			FormData moveUpButtonLData = new FormData();
			moveUpButtonLData.height = 25;
			moveUpButtonLData.width = 50;
			//align the close button to the right of the apply button
			moveUpButtonLData.left =  new FormAttachment(this.moveFirstButton, 4);
			moveUpButtonLData.bottom =  new FormAttachment(1000, 1000, -2);
			moveUpButton.setLayoutData(moveUpButtonLData);
			moveUpButton.setImage(new Image(getDisplay(), 
					getClass().getResourceAsStream("/icons/btnMoveUp.gif"))); //$NON-NLS-1$
			moveUpButton.setToolTipText(Messages.getString("AbstractEditeurFiltre.7")); //$NON-NLS-1$
			moveUpButton.setEnabled(false);
			moveUpButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if(moveUpButtonAction != null)
						moveUpButtonAction.run();
				}
			});
			

			//Create the close button
			moveDownButton = new Button(this.filterEditorButtonsComposite, SWT.PUSH);
			FormData moveDownButtonLData = new FormData();
			moveDownButtonLData.height = 25;
			moveDownButtonLData.width = 50;
			//align the close button to the right of the apply button
			moveDownButtonLData.left =  new FormAttachment(this.moveUpButton, 4);
			moveDownButtonLData.bottom =  new FormAttachment(1000, 1000, -2);
			moveDownButton.setLayoutData(moveDownButtonLData);
			moveDownButton.setImage(new Image(getDisplay(), 
					getClass().getResourceAsStream("/icons/btnMoveDown.gif"))); //$NON-NLS-1$
			moveDownButton.setToolTipText(Messages.getString("AbstractEditeurFiltre.8")); //$NON-NLS-1$
			moveDownButton.setEnabled(false);
			moveDownButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if(moveDownButtonAction != null)
						moveDownButtonAction.run();
				}
			});
			

			//Create the close button
			moveLastButton = new Button(this.filterEditorButtonsComposite, SWT.PUSH);
			FormData moveLastButtonLData = new FormData();
			moveLastButtonLData.height = 25;
			moveLastButtonLData.width = 50;
			//align the close button to the right of the apply button
			moveLastButtonLData.left =  new FormAttachment(this.moveDownButton, 4);
			moveLastButtonLData.bottom =  new FormAttachment(1000, 1000, -2);
			moveLastButton.setLayoutData(moveLastButtonLData);
			moveLastButton.setImage(new Image(getDisplay(), 
					getClass().getResourceAsStream("/icons/btnMoveBottom.gif"))); //$NON-NLS-1$
			moveLastButton.setToolTipText(Messages.getString("AbstractEditeurFiltre.9")); //$NON-NLS-1$
			moveLastButton.setEnabled(false);
			moveLastButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent event) {
					if(moveLastButtonAction != null)
						moveLastButtonAction.run();
				}
			});

        }
    }
    
    /**
     * Updates the order buttons enable/disable states
     * @param flags 
     */
    protected void updateOrderButtonsStatus(int flags) {
    	moveFirstButton.setEnabled((flags & MoveOperationsFlags.MOVE_TOP) != 0);
    	moveUpButton.setEnabled((flags & MoveOperationsFlags.MOVE_UP) != 0);
    	moveDownButton.setEnabled((flags & MoveOperationsFlags.MOVE_DOWN) != 0);
    	moveLastButton.setEnabled((flags & MoveOperationsFlags.MOVE_BOTTOM) != 0);
    }
    
    protected void setMoveFirstButtonAction(Action action) {
    	this.moveFirstButtonAction = action;
    }
    
    protected void setMoveUpButtonAction(Action action) {
    	this.moveUpButtonAction = action;
    }

    protected void setMoveDownButtonAction(Action action) {
    	this.moveDownButtonAction = action;
    }
    
    protected void setMoveLastButtonAction(Action action) {
    	this.moveLastButtonAction = action;
    }
}
