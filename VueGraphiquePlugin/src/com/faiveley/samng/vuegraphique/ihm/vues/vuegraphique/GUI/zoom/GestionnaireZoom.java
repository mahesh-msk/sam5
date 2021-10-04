package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Graphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class GestionnaireZoom implements IDataChangedListener{
	private List<AZoomComposant> listeZooms = new ArrayList<AZoomComposant>();
	private AZoomComposant zoomCourant;
	private int currentZoomIndex;
	private static GestionnaireZoom instance;
	private List<IZoomChangeListener> listeners = new ArrayList<IZoomChangeListener>();
	public static int MAX_ZOOMS = 20;
	private AZoomComposant premierZoom;
	private double lastIInitialMinDistanceDouble;
	private double lastIInitialMaxDistanceDouble;
	private static List<AZoomComposant> currentZoomsState;

	private GestionnaireZoom() {
		ActivatorData.getInstance().addDataListener(this);
		//The main zoom is considered null.
		//We add it by default
		listeZooms.add(null);
	}

	public void clear(){
		//TAGQQQ 
		listeZooms.clear();
		zoomCourant=null;
		listeners.clear();
		premierZoom=null;
		if (currentZoomsState!=null) {
			currentZoomsState.clear();
		}
		//CHECK01
	}

	public static GestionnaireZoom getInstance() {
		if(instance == null)
			instance = new GestionnaireZoom();
		return instance;
	}

	public static void reset() {
		instance = new GestionnaireZoom();
	}

	/**
	 * Sets the current zoom. 
	 * Note: this will not add the zoom to the zooms list and this zoom is lost
	 * when a next/prev zoom is made. This is usefull for scrolling
	 * @param zoom
	 */
	public static void setZoomCourant(AZoomComposant zoom) {
		getInstance().zoomCourant = zoom;
		getInstance().fireZoomChanged();	//notify listeners that the current zoom changed
	}

	public static AZoomComposant getZoomCourant() {
		return getInstance().zoomCourant;
	}

	/**
	 * Creates a X zoom object based on the received parameters and also sets it as the current zoom.
	 * 
	 * @param axe the current AxeX object
	 * @param startMsgId the first message in the zoom. It should 
	 * @param endMsgId the last message in the zoom
	 * @param computedStartValue a value on X axis where the zoom starts. It can be computed
	 * 					based on resolution and zoom selection rectangle or can be inserted by the user.
	 * 					No message should exist between [(message.getXValue() - computedStartValue), 
	 * 					message.getXValue()] interval 
	 * @param computedEndValue
	 * @return
	 */
	public static AZoomComposant creerZoomX(AxeX axe, int startMsgId, int endMsgId, 
			double computedStartValue, double computedEndValue) {
		if (startMsgId==endMsgId)
			return null;
		List<AZoomComposant> zooms = createCurrentZoomStates();
		ZoomComposite mainZoomComposite = new ZoomComposite(); 

		mainZoomComposite.setTypeZoom(TypeZoom.ZOOM_X);
		ZoomX zoom = new ZoomX();
		zoom.setFirstVisibleMsgId(startMsgId);
		zoom.setLastVisibleMsgId(endMsgId);
		zoom.setFirstXValue(computedStartValue);
		zoom.setLastXValue(computedEndValue);
		zoom.setTypeAxe(axe.m_TypeAxe);

		zooms.set(0, zoom);

		mainZoomComposite.ajouterZoomComposants(zooms);
		ajouterZoom(mainZoomComposite);
		return zoom;
	}

	public static AZoomComposant creerZoomXWithAxeChange(){
		List<AZoomComposant> zooms = createCurrentZoomStates();
		ZoomX lastZoom = new ZoomX();
		lastZoom=(ZoomX)zooms.get(zooms.size()-1);

		ZoomComposite mainZoomComposite = new ZoomComposite(); 

		mainZoomComposite.setTypeZoom(TypeZoom.ZOOM_X);
		ZoomX zoom = new ZoomX();
		zoom.setFirstVisibleMsgId(lastZoom.getFirstVisibleMsgId());
		zoom.setLastVisibleMsgId(lastZoom.getLastVisibleMsgId());
		zoom.setFirstXValue(lastZoom.getFirstXValue());
		zoom.setLastXValue(lastZoom.getLastXValue());

		zooms.set(0, zoom);

		mainZoomComposite.ajouterZoomComposants(zooms);
		ajouterZoom(mainZoomComposite);

		return zoom;
	}

	public static AZoomComposant creerAllVarsZoomY(List<Courbe> listeCourbes, 
			int indiceGraphe, int y1Pos, int y2Pos) {
		Graphe graph = FabriqueGraphe.getGraphe(indiceGraphe);
		ZoomComposite zoomComposite = null;
		List<AZoomComposant> zooms = createCurrentZoomStates();
		ZoomComposite mainZoomComposite = new ZoomComposite();

		mainZoomComposite.setTypeZoom(TypeZoom.ZOOM_Y);
		mainZoomComposite.setIndiceGraphe(indiceGraphe);
		ZoomY zoomY;
		double minVal;
		double maxVal;
		double courveMinVal;
		double courbeResoVerticale;
		if(graph != null) {
			//we are performing a Y zoom only in analogic graphs
			if(graph.getTypeGraphe() == TypeGraphe.ANALOGIC) {
				//check if we really have this courve in the graph
				List<Courbe> graphCourbes = graph.getListeCourbe(); 
				zoomComposite = new ZoomComposite();
				zoomComposite.setIndiceGraphe(indiceGraphe);
				for(Courbe currentCourbe: graphCourbes) {
					courveMinVal = currentCourbe.getMinValeur();
					courbeResoVerticale = currentCourbe.getResoVerticale();
					minVal = courveMinVal + y1Pos * courbeResoVerticale;
					maxVal = courveMinVal + y2Pos * courbeResoVerticale;
					zoomY = new ZoomY(currentCourbe, minVal, maxVal);
					zoomY.setTypeZoom(TypeZoom.ZOOM_Y);
					zoomY.setCourbe(currentCourbe);
					zoomComposite.ajouterZoomComposant(zoomY);
				}
				int zoomIndex = getZoomIndexForGraph(zooms, indiceGraphe);
				zooms.set(zoomIndex, zoomComposite);


				mainZoomComposite.ajouterZoomComposants(zooms);

				//				if(mainZoomComposite.getEnfant(0)==null){
				//					mainZoomComposite.ajouterZoomComposant(0, getInstance().premierZoom);
				//					mainZoomComposite.supprimerZoomComposant(1);
				//				}
				ajouterZoom(mainZoomComposite);
			}
		}
		return zoomComposite;
	}

	public static AZoomComposant creerSingleVarZoomY(Courbe courbe, 
			int indiceGraphe, double minValue, double maxValue) {
		Graphe graph = FabriqueGraphe.getGraphe(indiceGraphe);
		ZoomComposite zoomComposite = null;
		List<AZoomComposant> zooms = createCurrentZoomStates();
		ZoomComposite mainZoomComposite = new ZoomComposite();
		mainZoomComposite.setTypeZoom(TypeZoom.ZOOM_Y);
		mainZoomComposite.setIndiceGraphe(indiceGraphe);
		ZoomY zoomY;
		if(graph != null) {
			//we are performing a Y zoom only in analogic graphs
			if(graph.getTypeGraphe() == TypeGraphe.ANALOGIC) {
				//check if we really have this courve in the graph
				List<Courbe> graphCourbes = graph.getListeCourbe(); 
				if(graphCourbes.contains(courbe)) {
					zoomComposite = new ZoomComposite();
					zoomComposite.setIndiceGraphe(indiceGraphe);
					for(Courbe currentCourbe: graphCourbes) {
						if(currentCourbe == courbe) {
							zoomY = new ZoomY(courbe, minValue, maxValue);
						} else {
							zoomY = new ZoomY(currentCourbe, currentCourbe.getMinValeur(), currentCourbe.getMaxValeur());
						}
						zoomY.setTypeZoom(TypeZoom.ZOOM_Y);
						zoomY.setCourbe(currentCourbe);
						zoomComposite.ajouterZoomComposant(zoomY);
					}
					int zoomIndex = getZoomIndexForGraph(zooms, indiceGraphe);
					zooms.set(zoomIndex, zoomComposite);
					mainZoomComposite.ajouterZoomComposants(zooms);
					ajouterZoom(mainZoomComposite);
				}
			}
		}
		return zoomComposite;
	}

	/**
	 * Creates a zoom object from the current graph state
	 * @param graph
	 * @return
	 */
	private static ZoomComposite createYZoomForGraph(Graphe graph) {
		ZoomY zoomY;
		ZoomComposite zoomComposite = new ZoomComposite();
		zoomComposite.setIndiceGraphe(graph.getNumero());

		List<Courbe> graphCourbes = graph.getListeCourbe();
		for(Courbe currentCourbe: graphCourbes) {
			zoomY = new ZoomY(currentCourbe, currentCourbe.getMinValeur(), currentCourbe.getMaxValeur());
			zoomComposite.ajouterZoomComposant(zoomY);
		}
		return zoomComposite;
	}

	/**
	 * Adds the given zoom to the zooms list and sets it as current zoom
	 * 
	 * @param zoom the zoom to be added and that will become current zoom
	 */
	public static void ajouterZoom(AZoomComposant zoom) {
		GestionnaireZoom curInstance = getInstance();
		//if we have the situation when creating a new zoom but he made "previous"
		//keep only the zooms until the current one
		if(curInstance.currentZoomIndex < curInstance.listeZooms.size() - 1) {
			curInstance.listeZooms = curInstance.listeZooms.subList(0, curInstance.currentZoomIndex+1);
		}
		//we are on the last position in the list, so we should add at the end
		curInstance.listeZooms.add(zoom);
		//check for maximum zooms
		if(curInstance.listeZooms.size() > MAX_ZOOMS)
			curInstance.listeZooms.remove(0);

		curInstance.zoomCourant = zoom;	//set the current zoom. It can be null for main zoom
		curInstance.currentZoomIndex = curInstance.listeZooms.size() - 1;
		if(zoom != null)
			currentZoomsState = ((ZoomComposite)zoom).getEnfants();
		else 
			currentZoomsState = null;

		curInstance.fireZoomChanged();
	}

	public static AZoomComposant setNextZoom() {
		GestionnaireZoom curInstance = getInstance();
		if(hasNextZoom()) {
			getInstance().currentZoomIndex++;
			curInstance.zoomCourant = getInstance().listeZooms.get(getInstance().currentZoomIndex);
			curInstance.fireZoomChanged();
		}
		return curInstance.zoomCourant;
	}

	public static AZoomComposant setPrevZoom() {
		GestionnaireZoom curInstance = getInstance();
		if(hasPrevZoom()) {
			getInstance().currentZoomIndex--;
			for(int i = 0 ; i <= getInstance().currentZoomIndex; i++){
				curInstance.zoomCourant = getInstance().listeZooms.get(i);
				curInstance.fireZoomChanged();
			}			
		}
		return curInstance.zoomCourant;
	}

	public static boolean hasNextZoom() {
		GestionnaireZoom curInstance = getInstance();
		int zoomsCount = curInstance.listeZooms.size();
		//check to see if we have any zooms and if current zoom is not the last
		return (zoomsCount > 0) && (curInstance.currentZoomIndex < zoomsCount-1);
	}

	public static boolean hasPrevZoom() {
		return getInstance().currentZoomIndex > 0;
	}

	public void addListener(IZoomChangeListener listener) {
		if(listener != null)
			this.listeners.add(listener);
	}

	public void removeListener(IZoomChangeListener listener) {
		if(listener != null)
			this.listeners.remove(listener);
	}

	public void fireZoomChanged() {
		for(IZoomChangeListener listener: this.listeners) {
			listener.zoomChanged();
		}
		if(getZoomCourant() != null)
			currentZoomsState=((ZoomComposite)getZoomCourant()).getEnfants();
	}

	public static int getZoomIndexForGraph(List<AZoomComposant> zooms, int graphNumero) {
		ZoomComposite zoomComposite;
		int i = 0;
		for(AZoomComposant curZoom: zooms) {
			//for graphs zooms we will allways have non null values
			//for X Zoom we may have null values
			if(curZoom != null && !(curZoom instanceof ZoomX)) { 
				zoomComposite = (ZoomComposite)curZoom;
				if(zoomComposite.getIndiceGraphe() == graphNumero) {
					return i;
				}
			}
			i++;
		}
		
		return -1;
	}

	private static List<AZoomComposant> createCurrentZoomStates() {
		List<AZoomComposant> zoomStates = new ArrayList<AZoomComposant>();
		//on first position we will have always ZoomX

		if(currentZoomsState != null){
			zoomStates.addAll(currentZoomsState);
//			if(GestionnaireZoom.getInstance().listeZooms != null){
//				zoomStates.addAll(GestionnaireZoom.getInstance().listeZooms);
		}else {
			zoomStates.add(null);
			Graphe[] graphes = FabriqueGraphe.getGraphes();
			for(Graphe graph: graphes) {
				if(graph.getTypeGraphe() == TypeGraphe.ANALOGIC)
					zoomStates.add(createYZoomForGraph(graph));
			}
		}

		return zoomStates;
	}
	/**
	 * Méthode de création du prremier zoom qui correspond au zoom principal
	 * @param axe
	 * @param startMsgId
	 * @param endMsgId
	 * @param computedStartValue
	 * @param computedEndValue
	 */
	public void creerPremierZoom(AxeX axe, int startMsgId, int endMsgId, 
			double computedStartValue, double computedEndValue){

		List<AZoomComposant> zooms = createCurrentZoomStates();
		ZoomComposite mainZoomComposite = new ZoomComposite(); 

		mainZoomComposite.setTypeZoom(TypeZoom.ZOOM_X);
		ZoomX zoom = new ZoomX();
		zoom.setFirstVisibleMsgId(startMsgId);
		zoom.setLastVisibleMsgId(endMsgId);
		zoom.setFirstXValue(computedStartValue);
		zoom.setLastXValue(computedEndValue);
		this.premierZoom = zoom;

	}

	public void onDataChange() {
		reset();

	}

	public double getLastIInitialMaxDistanceDouble() {
		return lastIInitialMaxDistanceDouble;
	}

	public void setLastIInitialMaxDistanceDouble(
			double lastIInitialMaxDistanceDouble) {
		this.lastIInitialMaxDistanceDouble = lastIInitialMaxDistanceDouble;
	}

	public double getLastIInitialMinDistanceDouble() {
		return lastIInitialMinDistanceDouble;
	}

	public void setLastIInitialMinDistanceDouble(
			double lastIInitialMinDistanceDouble) {
		this.lastIInitialMinDistanceDouble = lastIInitialMinDistanceDouble;
	}
}
