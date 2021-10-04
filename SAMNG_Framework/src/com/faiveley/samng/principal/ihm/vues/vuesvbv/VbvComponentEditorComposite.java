package com.faiveley.samng.principal.ihm.vues.vuesvbv;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.VbvsCombo;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VbvComponentEditorComposite extends Composite {
    private VbvsCombo operande1Combo;
    private Text operande1Text;
    private Label operande1Label;
    private boolean isSearcheable;
    private boolean isInitializingText;
    private static String CLEAR_STRING = "..."; //$NON-NLS-1$
    private static String SEARCH_STRING = Messages.getString("VbvComponentEditorComposite.1"); //$NON-NLS-1$
    private List<IVbvElementEditorListener> listeners = new ArrayList<IVbvElementEditorListener>(0);
    private Map<String, Object> mapValuesToObjects = new LinkedHashMap<String, Object>();
    private Map<String, Object> mapLabelsToObjects = new LinkedHashMap<String, Object>();
    private List<PropertyChangeListener> propertyChangeListeners = new ArrayList<PropertyChangeListener>(0);
    private String texteAide = "";
    protected ComboSelectionAdapter comboSelAdapter = new ComboSelectionAdapter();
	
	//private Color ERR_COLOR = getDisplay().getSystemColor(SWT.COLOR_RED);


	public VbvComponentEditorComposite(Composite parent, int style) {
		super(parent, style);
		createComponents();
		this.propertyChangeListeners.add((PropertyChangeListener) parent.getParent());
	}
	
	private void createComponents() {
    	operande1Label = new Label(this, SWT.NONE);
		GridData operande1LabelLData = new GridData();
		operande1LabelLData.heightHint = 13;
		operande1LabelLData.horizontalAlignment = GridData.CENTER;
		operande1Label.setLayoutData(operande1LabelLData);
		operande1Label.setText(Messages.getString("VbvComponentEditorComposite.2")); //$NON-NLS-1$
		//operande1Label.setToolTipText((Messages.getString("VbvComponentEditorComposite.2")));
		
    	operande1Text = new Text(this, SWT.NONE);
		GridData operande1TextLData = new GridData();
		operande1TextLData.heightHint = 20;
		operande1TextLData.grabExcessHorizontalSpace = true;
		operande1TextLData.horizontalAlignment = GridData.FILL;
		operande1Text.setLayoutData(operande1TextLData);
		setValueText(""); //$NON-NLS-1$
        this.operande1Text.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(!isInitializingText)	//workaround in order to avoid fireing events when setText is called
					fireContentEdited();
				setToolTipText(operande1Text.getText());
			}
        });
        this.operande1Text.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				
				for (PropertyChangeListener listener : propertyChangeListeners) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"TEXT_HELP_CHANGED", 
							null, texteAide);
					listener.propertyChange(evt);
				}	
			}

			public void focusLost(FocusEvent e) {
				
				for (PropertyChangeListener listener : propertyChangeListeners) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"TEXT_HELP_CHANGED", 
							null, null);
					listener.propertyChange(evt);
				}	
			}
			
		});
        
        
        
		operande1Combo = new VbvsCombo(this, SWT.READ_ONLY);
		GridData operande1ComboLData = new GridData();
		operande1ComboLData.heightHint = 20;
		operande1ComboLData.horizontalAlignment = GridData.FILL;
		operande1ComboLData.grabExcessHorizontalSpace = true;
		operande1Combo.setLayoutData(operande1ComboLData);
		operande1Combo.add(CLEAR_STRING);
		operande1Combo.select(0);
		operande1Combo.addSelectionListener(comboSelAdapter);
		this.operande1Combo.addFocusListener(new FocusListener(){

			public void focusGained(FocusEvent e) {
				
				for (PropertyChangeListener listener : propertyChangeListeners) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"TEXT_HELP_CHANGED", 
							null, texteAide);
					listener.propertyChange(evt);
				}	
			}

			public void focusLost(FocusEvent e) {
				
				for (PropertyChangeListener listener : propertyChangeListeners) {
					PropertyChangeEvent evt = new PropertyChangeEvent(this,
							"TEXT_HELP_CHANGED", 
							null, null);
					listener.propertyChange(evt);
				}	
			}
			
		});
		
	}
	
	public void setTopLabelText(String text) {
		operande1Label.setText(text);
	}
	
	public void setSearcheable(boolean searchable) {
		boolean oldValue = this.isSearcheable;
		if(searchable != oldValue) {
			this.isSearcheable = searchable;
			if(this.isSearcheable) 
				operande1Combo.add(SEARCH_STRING, 1);
			else
				operande1Combo.remove(1);
		}
	}
	
	public void setValueString(String text) {
		setValueText(text);
		selectValueInCombo();
	}

	public String getValueString() {
		return operande1Text.getText();
	}
	
	public Object getValueObject() {
		return this.mapLabelsToObjects.get(getValueString());
	}
	
	public Map<String, Object> getPossibleObjectsMap() {
		return this.mapValuesToObjects;		//return the map from unique name to objects
	}

	public void setPossibleComboValues(Map<String, Object> possibleValues) {
		operande1Combo.removeAll();
		this.mapValuesToObjects.clear();
		this.mapLabelsToObjects.clear();
		operande1Combo.add(CLEAR_STRING);
		if(this.isSearcheable)
			operande1Combo.add(SEARCH_STRING);
		Object obj; 
		String label;
		//we are keeping two maps with both names to objects 
		//and also labels to object 
		for(String val: possibleValues.keySet()) {

			obj = possibleValues.get(val);
			label = VbvsUtil.getLabel(obj);
			if(label == null) {
				operande1Combo.add(val);	//we might have true or false here that will generate a null label
				this.mapLabelsToObjects.put(val, obj);
				this.mapValuesToObjects.put(val, obj);
			} else {
				this.mapLabelsToObjects.put(label, obj);
				operande1Combo.add(label);
				this.mapValuesToObjects.put(val, obj);
			}
		}
		selectValueInCombo();	//try to select a value in combo
	}
	
	private void selectValueInCombo() {
		String text = operande1Text.getText();
		if(!this.mapLabelsToObjects.containsKey(text) || "".equals(text)) //$NON-NLS-1$
			operande1Combo.select(0);
		else {
			int i = 0;
			for(String str: this.mapLabelsToObjects.keySet()) {
				if(text.equals(str)) {
					//i is the index of value in the map not in the combo
					operande1Combo.select(i + (isSearcheable ? 2 : 1));
					break;
				}
				i++;
			}
		}
	}
	
    private class ComboSelectionAdapter extends SelectionAdapter {
        public void widgetSelected(SelectionEvent event) {
            VbvsCombo srcCombo = (VbvsCombo) event.getSource();
            String oldValue = (String) event.data;
            int selection = srcCombo.getSelectionIndex();

            if (selection == 0) {
            	operande1Text.setText(""); //$NON-NLS-1$
            	operande1Text.setToolTipText("");
            	
            } else if (isSearcheable && selection == 1) {
            	RechercheDialog dlg = new RechercheDialog(new Shell());
            	List<String> valuesPresent = getCurrentValues();
            	dlg.setSelectableValues(valuesPresent.toArray(new String[valuesPresent.size()]));
            	dlg.setAppelant(this.getClass().getName());
				
				dlg.setTypeRecherche("Variable");
            	String selValue = dlg.open();
            	if(selValue == null) {
            		setComboText(srcCombo, oldValue);
                    return;
            	}
            	if(selValue!=null)
					ActivatorData.getInstance().getPoolDonneesVues().put(this.getClass().getName()+"variable", selValue);
            	
            	setComboText(srcCombo, selValue);
            	operande1Text.setText(selValue);
            	operande1Text.setToolTipText((selValue));
            } else {
                String selValue = srcCombo.getText();
                setComboText(srcCombo, selValue);
                operande1Text.setText(selValue);
                operande1Text.setToolTipText((selValue));            
            }
        }
    }
    
    private List<String> getCurrentValues() {
    	
    	ArrayList<String> values = new ArrayList<String>();
    	TableItem[] comboValues = this.operande1Combo.getItems();
    	for(TableItem val: comboValues) {
    		values.add(val.getText());
    	}
    	
    	values.remove(CLEAR_STRING);
    	values.remove(SEARCH_STRING);
    	values.trimToSize();
    	return values;
   }
    
    private void setComboText(VbvsCombo combo, String text) {
    	//: color here the text if needed
    	combo.setText(text);
    }
    
    
    public void addVbvElementChangeListener(IVbvElementEditorListener listener) {
    	if(listener != null)
    		this.listeners.add(listener);
    }

    public void removeVbvElementChangeListener(IVbvElementEditorListener listener) {
    	if(listener != null)
    		this.listeners.remove(listener);
    }
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener){
    	if(listener != null)
    		this.propertyChangeListeners.remove(listener);
    }

    
    
    public void removePropertyChangeListener(PropertyChangeListener listener){
    	if(listener != null)
    		this.propertyChangeListeners.remove(listener);
    }
    
    private void fireContentEdited() {
    	for(IVbvElementEditorListener listener: this.listeners)
    		listener.vbvElementChanged(this);
    }
    
    /**
     * Workaround for Do not call directly operande1Text.setText as this fires a SWT.Modify event
     * which in turns notifies change listeners
     * @param text
     */
    private void setValueText(String text) {
    	try{
    	isInitializingText = true;
    	operande1Text.setText(text);
    	operande1Text.setToolTipText((text));
    	isInitializingText = false;
    	}
    	catch(Exception ex){
    		
    	}
    }
    
    public void addKeyListener(KeyListener listener) {
    	super.addKeyListener(listener);
    	operande1Text.addKeyListener(listener);
    	operande1Combo.addKeyTextListener(listener);
    }

	public String getTexteAide() {
		return texteAide;
	}

	public void setTexteAide(String texteAide) {
		this.texteAide = texteAide;
	}

	
}
