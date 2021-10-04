package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;

import com.faiveley.samng.principal.sm.filtres.GraphicConstants;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Legende.InfosBullesLegende;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Legende.LegendeGUI;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures.RupturesLegendeGUI;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;

public class LegendeComposite extends Composite {
	
	private int numGraphes;
	private LegendeGUI[] legendes;
	private RupturesLegendeGUI rupturesInfo;
	private List<PropertyChangeListener> listeners= new ArrayList<PropertyChangeListener>();
	private boolean usesShortNames;
	
	public LegendeComposite(Composite parent, int style, int numGraphes, boolean usesShortNames) {
		super(parent, SWT.NONE);
		this.numGraphes = numGraphes;
		this.legendes = new LegendeGUI[this.numGraphes];
		this.usesShortNames = usesShortNames;
		initialize();
	}

	private void initialize() {
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
        gridLayout.marginHeight = 0;
        gridLayout.horizontalSpacing = 0;
        gridLayout.marginWidth = 0;
        
        this.setLayout(gridLayout);		
        GridData gridData = new GridData(GridData.FILL_BOTH);
        
//        final Button maximize = new Button(this,SWT.NONE);
//        GridData gd0 = new GridData();
//		gd0.grabExcessVerticalSpace = false;
//		gd0.verticalAlignment = GridData.FILL;
//		gd0.grabExcessHorizontalSpace = false;
//		gd0.horizontalAlignment = GridData.CENTER;
        
//        maximize.setText("Maximisez");
//        maximize.addSelectionListener(new SelectionListener(){
//
//			public void widgetDefaultSelected(SelectionEvent e) {
//				//  Apéndice de método generado automáticamente
//				
//			}
//
//			public void widgetSelected(SelectionEvent e) {
//				PropertyChangeEvent evt = null;
//				if(maximize.getText().equals("Maximisez")){
//					evt = new PropertyChangeEvent(this,
//							"MAXIMISER_LEGENDE", //$NON-NLS-1$
//							null, null);
//					maximize.setText("Normal");
//				}
//				else if(maximize.getText().equals("Normal")){
//					evt = new PropertyChangeEvent(this,
//							"NORMAL_LEGENDE", //$NON-NLS-1$
//							null, null);
//					maximize.setText("Maximisez");
//				}
//				for (PropertyChangeListener listener : listeners) {
//					listener.propertyChange(evt);
//				}
//				
//			}
//        	
//        });
//        maximize.setLayoutData(gd0);
		InfosBullesLegende.getInstance().getListBullesDim().clear();
		InfosBullesLegende.getInstance().getListBullesName().clear();
		InfosBullesLegende.getInstance().getListBullesNumGraphe().clear();
        
		int index=0;
		
        for(int i = 0; i<GraphicConstants.MAX_GRAPHICS_COUNT; i++) {
        	GraphiqueFiltreComposite gfc = FabriqueGraphe.getGrapheFiltre2(i);
        	if (gfc != null) {
	        	boolean contientVarRenseignee = gfc.contientUneVariableRenseignee();
	        	if (gfc.isActif() && contientVarRenseignee) {
	        		legendes[index] = new LegendeGUI(this, SWT.NONE, i, this.usesShortNames);
	            	legendes[index].setLayoutData(gridData);
	        		index++;
				}
        	}
        }
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.verticalSpan = 1;
		gd2.heightHint = 75;
		rupturesInfo = new RupturesLegendeGUI(this, SWT.NONE);
		rupturesInfo.setLayoutData(gd2);
	}
	
	@Override
	public void dispose() {
		for(LegendeGUI legendeGui: legendes) {
			if(legendeGui != null)
				legendeGui.dispose();
		}
		rupturesInfo.dispose();
		super.dispose();
	}
	
	@Override
	public void setMenu(Menu menu) {
		for(int i = 0; i<numGraphes; i++) {
			legendes[i].setMenu(menu);
		}
		super.setMenu(menu);
	}

	public List<PropertyChangeListener> getListeners() {
		return listeners;
	}

	public void setListeners(List<PropertyChangeListener> listeners) {
		this.listeners = listeners;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener){
		this.listeners.add(listener);
	}
	
}
