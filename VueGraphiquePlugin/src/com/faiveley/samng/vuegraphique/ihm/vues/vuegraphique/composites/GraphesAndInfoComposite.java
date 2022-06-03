package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites;

import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ScrollBar;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.Curseur;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.AZoomComposant;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.IZoomChangeListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;


public class GraphesAndInfoComposite extends Composite implements IZoomChangeListener {
	
	private static int SCROLL_UNIT_INCREMENT_RATIO = 10;
	private GraphesComposite graphe = null;
	private InfoComposite info = null;
	private int numGraphes;
	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;
	private int prevHScrollSelection;
	private Composite graphesCompositeTop;
	private ZoomComposite createdZoom;
	private List<PropertyChangeListener> listeners= new ArrayList<PropertyChangeListener>();

	public GraphesAndInfoComposite(Composite parent, int style, int numGraphes) {
		super(parent, SWT.H_SCROLL);
		this.numGraphes = numGraphes;
		initialize();
		GestionnaireZoom.getInstance().addListener(this);
		
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.verticalSpacing = 0;
		this.setLayout(gridLayout);		
        this.setSize(new Point(394, 212));

    	GridData graphesCompositeTopLData = new GridData();
    	graphesCompositeTopLData.verticalAlignment = GridData.FILL;
    	graphesCompositeTopLData.horizontalAlignment = GridData.FILL;
    	graphesCompositeTopLData.grabExcessHorizontalSpace = true;
    	graphesCompositeTopLData.grabExcessVerticalSpace = true;
    	graphesCompositeTop = new Composite(this, SWT.V_SCROLL);
    	GridLayout graphesCompositeTopLayout = new GridLayout();
    	graphesCompositeTopLayout.makeColumnsEqualWidth = true;
    	graphesCompositeTopLayout.verticalSpacing = 0;
    	graphesCompositeTopLayout.marginHeight = 0;
    	graphesCompositeTopLayout.horizontalSpacing = 0;
    	graphesCompositeTopLayout.marginWidth = 0;

    	graphesCompositeTop.setLayout(graphesCompositeTopLayout);
    	graphesCompositeTop.setLayoutData(graphesCompositeTopLData);

    	
    	if(ActivatorData.getInstance().getSelectedMsg() != null){
			Curseur.getInstance().setSynchroniseCurseur(true);
		}
    	
    	graphe = new GraphesComposite(graphesCompositeTop, SWT.NONE, numGraphes);
		GridData gd1 = new GridData();
		gd1.grabExcessVerticalSpace = true;
		gd1.verticalAlignment = GridData.FILL;
		gd1.grabExcessHorizontalSpace = true;
		gd1.horizontalAlignment = GridData.FILL;
		gd1.minimumWidth=5*55;
		graphe.setLayoutData(gd1);
	
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL);
		gd2.verticalSpan = 1;
		gd2.heightHint = 56;
        info = new InfoComposite(this, SWT.NONE);
        info.setLayoutData(gd2);
        
        hScrollBar = getHorizontalBar();
        hScrollBar.setThumb(getBounds().width);
		hScrollBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				int currentXLocation = hScrollBar.getSelection();
				boolean scrollLeft;
				if(prevHScrollSelection < currentXLocation)
					scrollLeft = false;
				else if (prevHScrollSelection > currentXLocation)
					scrollLeft = true;
				else 
					return;

				AxeX currentXAxe = GestionnaireAxes.getInstance().getCurrentAxeX();
				ZoomComposite currentZoom = (ZoomComposite)GestionnaireZoom.getZoomCourant();
				if(currentZoom != null && currentZoom.getEnfant(0) != null) {
					double prevTime = prevHScrollSelection * currentXAxe.getResolution();
					prevHScrollSelection = currentXLocation;

					int width = getBounds().width;
					double unzoomedCumul = currentXAxe.getAllCumul();
					double currentCumul = currentXAxe.getCumul();
					//compute the width of the screen for the whole cumul (unzoomed)
					int allWidth = (int)((unzoomedCumul * (width - 2*FabriqueGraphe.MARGE_LATERALE)) / currentCumul);
					double computedTime = currentXLocation * currentXAxe.getResolution();
					
					System.err.println("Selection: width=" + width + " allWidth=" + allWidth + " currentBarPos=" + currentXLocation + 
							" computedTime=" + computedTime);
					
					double offset = Math.abs(prevTime - computedTime);
					
					//get the current zoom and change the parameters according to the new
					//scroll possition
					//first clone the current zoom
					createdZoom = (ZoomComposite)currentZoom.clone();
					ZoomX newZoomX = GestionnaireAxes.offsetAxeX(offset, scrollLeft);
					ZoomX zoomX = (ZoomX)createdZoom.getEnfant(0);
					zoomX.setFirstVisibleMsgId(newZoomX.getFirstVisibleMsgId());
					zoomX.setLastVisibleMsgId(newZoomX.getLastVisibleMsgId());
					zoomX.setFirstXValue(newZoomX.getFirstXValue());
					zoomX.setLastXValue(newZoomX.getLastXValue());
					GestionnaireZoom.setZoomCourant(createdZoom);	//set the new zoom
				}
			}
		});

        vScrollBar = graphesCompositeTop.getVerticalBar();
        vScrollBar.setThumb(getBounds().height);
		vScrollBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Point location = graphe.getLocation();
				location.y = -vScrollBar.getSelection();
				graphe.setLocation(location);
			}
		});
		
		
		
		graphe.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				int inc;
				Point size = graphe.getSize();	
				int sizeY = size.y;
				if(sizeY < 200) { 
					inc=(200-size.y)+1;
					graphe.setSize(graphe.getSize().x, 200);
					sizeY += inc;	//30;
				}
				Rectangle rect = getClientArea();
				vScrollBar.setMaximum(sizeY);
				vScrollBar.setThumb(Math.min(size.y, rect.height));
//				int vPage = size.y - rect.height;
//				int vSelection = vScrollBar.getSelection();
//				Point location = graphe.getLocation();
//				if (vSelection >= vPage) {
//					if (vPage <= 0)
//						vSelection = 0;
//					location.y = -vSelection;
//				}
//				graphe.setLocation(location);
			}
		});
		

		
	}
	
	@Override
	public void setMenu(Menu menu) {
		if (!isDisposed())
		{
			graphe.setMenu(menu);
			info.setMenu(menu);
			super.setMenu(menu);
		}
	}
	
	@Override
	public void dispose() {
		GestionnaireZoom.getInstance().removeListener(this);
		graphe.dispose();
		info.dispose();
		super.dispose();
	}

	public void zoomChanged() {
		int width = getBounds().width;
		AZoomComposant currentZoom = GestionnaireZoom.getZoomCourant();
		if(currentZoom == null || ((ZoomComposite)currentZoom).getEnfant(0) == null) {
			hScrollBar.setThumb(getBounds().width);
			hScrollBar.setSelection(0);
			hScrollBar.setMaximum(width);
			prevHScrollSelection = 0;
			createdZoom = null;
		} else {
			AxeX currentXAxe = GestionnaireAxes.getInstance().getCurrentAxeX();
			//check if is not the zoom we created by a scroll
			if(GestionnaireZoom.getZoomCourant() != createdZoom) {
				double unzoomedCumul = currentXAxe.getAllCumul();
				double currentCumul = currentXAxe.getCumul();
				//compute the width of the screen for the whole cumul (unzoomed)
				int allWidth = (int)((unzoomedCumul * (width - 2*FabriqueGraphe.MARGE_LATERALE)) / currentCumul);
				hScrollBar.setMaximum((int)allWidth);
				hScrollBar.setThumb(Math.min(width, allWidth));
		        //Position the scroll according to the offset from begining
		        double cumulFromBegining = GestionnaireAxes.computeCumulToMessage(currentXAxe.getIdMsgDebut());
		        int widthToBegin = (int)(cumulFromBegining / currentXAxe.getResolution());
		        hScrollBar.setSelection(widthToBegin);
		        prevHScrollSelection = widthToBegin;	//save the current selection
		        
		        int increment = (int)width/SCROLL_UNIT_INCREMENT_RATIO;
		        if(increment < 1)
		        	increment = 1;
		        hScrollBar.setIncrement(increment);
		        hScrollBar.setPageIncrement(increment);
		        
		        System.err.println("zoomChanged: width=" + width + " allWidth=" + allWidth + " currentBarPos=" + widthToBegin);
			}
		}
	}
	
	public void redrawGraphes(boolean redrawCourbes) {
		this.graphe.redrawGraphes(redrawCourbes);
		this.info.redraw();
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
