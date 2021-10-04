package com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.segments.ASegment;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.AZoomComposant;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;

/**
 * @author Olivier
 * @version 1.0
 * @created 14-janv.-2008 12:36:58
 */
public class GestionnaireAxes extends AGestionnaireAxes {
	private Map<TypeAxe, AxeX> listeAxes = new HashMap<TypeAxe, AxeX>();
	private AxeX currentAxe;
	public AxeXMessagesVirtualValues vValues = new AxeXMessagesVirtualValues();
	private static GestionnaireAxes instance;


	private GestionnaireAxes() {
		currentAxe = getAxeByType(TypeAxe.AXE_DISTANCE);	
	}

	public void clear(){
//TAGDDDD
		listeAxes.clear();
		currentAxe=null;
		vValues=null;
//CHECK01
	}

	public static GestionnaireAxes getInstance() {
		if(instance == null)
			instance = new GestionnaireAxes();
		return instance;
	}

	/**
	 * récupère un axe à partir de son type: AXE_DISTANCE, AXE_DISTANCE_CORRIGEE,
	 * AXE_TEMPS, AXE_TEMPS_CORRIGE
	 * 
	 * @param typeAxe
	 */
	public AxeX getAxeByType(TypeAxe typeAxe) {
		AxeX axe = listeAxes.get(typeAxe);
		if(axe == null) {
			axe = new AxeX();
			axe.m_TypeAxe = typeAxe;
			listeAxes.put(typeAxe, axe);
		}
		return listeAxes.get(typeAxe);
	}

	/**
	 * initialise l'axe des distance à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - de la distance cumulée de tous les objets SegmentDistance
	 * 
	 * @param largeurPixels
	 */
	public void initialiserAxeDistance(int largeurPixels){

	}

	/**
	 * initialise l'axe des distances corrigées à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - de la distance cumulée de tous les objets SegmentDistance
	 * 
	 * @param largeurPixels
	 */
	public void initialiserAxeDistanceCorrigee(int largeurPixels){

	}

	/**
	 * initialise l'axe de temps à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - du temps cumulé de tous les objets SegmentTemps
	 * 
	 * @param largeurPixels
	 */
	public void initialiserAxeTemps(int largeurPixels){

	}

	/**
	 * initialise l'axe de temps à partir:
	 * - de la largeur en pixels de la partie réservée aux graphes
	 * - du temps cumulé de tous les objets SegmentTemps
	 * 
	 * @param largeurPixels
	 */
	public void initialiserAxeTempsCorrige(int largeurPixels){

	}

	public double changeTypeAxe(Message mess){
		TypeAxe typeAxe = GestionnaireAxes.getInstance().getCurrentAxeType();
		TypeAxe newtypeAxe=null;
		if (typeAxe==TypeAxe.AXE_DISTANCE)
			if (TableSegments.getInstance().isAppliedTempCorrections()) {
				newtypeAxe=TypeAxe.AXE_TEMPS_CORRIGE;
			}else
				newtypeAxe=TypeAxe.AXE_TEMPS;
		else
			if (typeAxe==TypeAxe.AXE_TEMPS)
				if(TableSegments.getInstance().isAppliedDistanceCorrections())
					newtypeAxe=TypeAxe.AXE_DISTANCE_CORRIGEE;
				else
					newtypeAxe=TypeAxe.AXE_DISTANCE;

		return AxeXUtil.getAxeXValue(mess, newtypeAxe);
	}

	public AxeX initialiserAxe(int largeurPixels) {
		AxeSegmentInfo segmentInfo;
		Message msgStart; 
		Message msgEnd;
		ZoomX currentZoomX = null;
		this.currentAxe.reset();

		AZoomComposant currentZoom = GestionnaireZoom.getZoomCourant();
		if(currentZoom != null)
			currentZoomX = (ZoomX)((ZoomComposite)currentZoom).getEnfant(0);

		vValues.initialize(currentAxe.m_TypeAxe);

		double globalCumul = vValues.getGlobalCumul();//computeGlobalCumul();
		this.currentAxe.setAllCumul(globalCumul);
		double cumul = computeCumul(currentZoom);
		this.currentAxe.setCumul(cumul);
		double resoHorizontale = cumul / (largeurPixels - (2*FabriqueGraphe.MARGE_LATERALE));
		currentAxe.setResolution(resoHorizontale);
		currentAxe.setFormateValeur(AxeXUtil.computeFormatValeur(cumul, currentAxe.m_TypeAxe));

//		récupération uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();

		//récupération de tous les messages
//		List<Message> messages = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
		double offsetBeforeFirstValue = 0;
		if(currentZoom == null || currentZoomX == null) {
			msgStart = messages.get(0);
			msgEnd = messages.get(messages.size() - 1);
		} else {
			msgStart = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(currentZoomX.getFirstVisibleMsgId());
			msgEnd = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(currentZoomX.getLastVisibleMsgId());

			if(currentAxe.m_TypeAxe == TypeAxe.AXE_TEMPS && offsetBeforeFirstValue < 100)
				offsetBeforeFirstValue = 0;
			if(msgStart == messages.get(0))
				offsetBeforeFirstValue = 0;
		}
		int msgStartId = msgStart.getMessageId();
		int msgEndId = msgEnd.getMessageId();
		currentAxe.setIdMsgDebut(msgStartId);
		currentAxe.setIdMsgFin(msgEndId);

		HashMap<Integer, ASegment> segments = AxeXUtil.getSegments(currentAxe.m_TypeAxe);
		if(segments == null || segments.size() == 0) {
			segmentInfo = new AxeSegmentInfo();
			segmentInfo.setSegmentNr(0);
			this.currentAxe.setIdMsgDebut(msgStartId);
			this.currentAxe.setIdMsgFin(msgEndId);
			segmentInfo.setSegmentStartMsgId(msgStartId);
			segmentInfo.setSegmentEndMsgId(msgEndId);
			segmentInfo.setMinX(0/*FabriqueGraphe.MARGE_LATERALE*/);
			segmentInfo.setMaxX(largeurPixels - FabriqueGraphe.MARGE_LATERALE);
			double startValue = getAxeXValue(msgStart);
			double endValue = getAxeXValue(msgEnd);
			segmentInfo.setMinValue(startValue);
			segmentInfo.setMaxValue(endValue);

			this.currentAxe.addSegmentInfo(segmentInfo);
		}else {
			ASegment segment = null;
			int minX = 0;
			double startValue;
			double endValue;
			double xIncrement;
			Message segStartMsg;
			Message segEndMsg;
			int startSegId;
			int endSegId;
			boolean firstSegment = true;
			//counter for the axeX segments
			//if ruptures inside segments occur this value will be different than
			//the segments count, otherwise it should be the same
			int segmentsCount = -1;		
			for(int i = 0; i<segments.size(); i++) {
				segment = AxeXUtil.getSegment(segments, i, currentAxe.m_TypeAxe);
				startSegId = segment.getStartMsgId();
				endSegId = segment.getEndMsgId();
				//check if the [startMsgId, endMsgId] interval overlapps over this segment
				if(isSegmentOverlapping(startSegId, endSegId, msgStartId, msgEndId)) {
					//check now if there are breaks inside between msg start and msg end
					List<Integer> segmentIntervals = AxeXUtil.getRuptures(currentAxe.m_TypeAxe, 
							startSegId, endSegId);
					//check for subsegments inside a segment caused by breaks (ruptures)
					for(int j = 0; j<segmentIntervals.size(); j++) {
						//check for end interval
						if(j == segmentIntervals.size() - 1)
							break;
						//get the current interval inside a segment
						startSegId = segmentIntervals.get(j);
						endSegId = segmentIntervals.get(j+1);
						if(isSegmentOverlapping(startSegId, endSegId, msgStartId, msgEndId)) {
							segStartMsg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(startSegId);
							segEndMsg = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(endSegId);

							segmentsCount++;
							segmentInfo = new AxeSegmentInfo();
							segmentInfo.setSegmentNr(segmentsCount);
							segmentInfo.setSegmentStartMsgId(startSegId);
							segmentInfo.setSegmentEndMsgId(endSegId);
							segmentInfo.setMinX(minX);

							//if a zoom, for the first overlapping segment 
							//we consider start value and not start of segment
							startValue = getAxeXValue(segStartMsg);
							endValue = getAxeXValue(segEndMsg);
							if(msgStartId > startSegId) {
								//take the segment interval starting from msgStartId
								startValue = getAxeXValue(msgStart);
							} 
							if(msgEndId < endSegId) {
								//take the segment interval starting from begining to msgEndId
								endValue = getAxeXValue(msgEnd);
							} 

							if(firstSegment) {
								startValue -= offsetBeforeFirstValue;
								firstSegment = false;
							}
							xIncrement = (endValue - startValue) / resoHorizontale; //number of pixels
							minX += xIncrement;	//minX will become maxX	
							segmentInfo.setMaxX(minX);
							segmentInfo.setMinValue(startValue);
							segmentInfo.setMaxValue(endValue);

							this.currentAxe.addSegmentInfo(segmentInfo);
						}
						j++;	//move to the next segment
					}
				}
			}
		}
		return this.currentAxe;
	}

	public AxeX getCurrentAxeX() {
		return this.currentAxe;
	}

	public TypeAxe getCurrentAxeType() {
		return this.currentAxe.m_TypeAxe;
	}

	public void setCurrentAxeType(TypeAxe typeAxe) {
		this.currentAxe = getAxeByType(typeAxe);
	}

	public static String getConvertedValue(double currentValue, double origine,boolean firstgrad,double coefgrad) {
		TypeAxe typeAxe = getInstance().getCurrentAxeType();
		return AxeXUtil.getConvertedValue(currentValue, origine, typeAxe, 
				getInstance().getCurrentAxeX().getFormateValeur(),firstgrad,coefgrad);
	}

	public static double getAxeXValue(Message message) {
		return AxeXUtil.getAxeXValue(message, getInstance().getCurrentAxeType());
	}

	private double computeCumul(AZoomComposant currentZoom) {
		double cumul = 0;
		Message msgStart;
		Message msgEnd;

		Enregistrement enr = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement();
		ListMessages messages = enr.getMessages();

		if(currentZoom == null || ((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0) == null) {
			msgStart = messages.get(0);
			msgEnd = messages.get(messages.size() - 1);
		} else {
			ZoomX currentZoomX = (ZoomX)((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0);
			msgStart = enr.getGoodMessage(currentZoomX.getFirstVisibleMsgId());
			msgEnd = enr.getGoodMessage(currentZoomX.getLastVisibleMsgId());
		}
		cumul += getInstance().vValues.getCumul(msgStart.getMessageId(), msgEnd.getMessageId());
		System.err.println("AxeX cumul = " + cumul);
		return cumul;
	}


	
	public static double computeCumulToMessage(int msgId) {
		return getInstance().vValues.getCumulToMessage(msgId);
	}

	private boolean isSegmentOverlapping(int segStartId, int segEndId, int msgStartId, int msgEndId) {
		if(msgStartId <= segStartId && msgEndId >= segStartId) {
			return true;
		}
		if(msgStartId >= segStartId && msgStartId <= segEndId)
			return true;
		return false;
	}

	/**
	 * Translates the X axe to the left or to the right with an offset (time or distance offset).
	 * 
	 * @param offset the offset
	 * @param left direction
	 * @return a zoom X according to the translation
	 */
	public static ZoomX offsetAxeX(double offset, boolean left) {
		AParcoursComposant data = ActivatorData.getInstance().getVueData()
		.getDataTable();
		Enregistrement enr = data.getEnregistrement();
//		récupération uniquement des bons messages
		ListMessages messages = enr.getMessages();

		//récupération de tous les messages
//		List<Message> messages = ActivatorData.getInstance().getVueData()
//		.getDataTable().getEnregistrement(0).getMessages();
		List<AxeXMessageVirtualValue> values = getInstance().vValues.virtualValues;

		ZoomX currentZoomX = (ZoomX)((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0);
		Message startMsg = enr.getGoodMessage(currentZoomX.getFirstVisibleMsgId());
		Message endMsg = enr.getGoodMessage(currentZoomX.getLastVisibleMsgId());
		if((startMsg == messages.get(0) && left) || 
				(endMsg == messages.get(messages.size()-1) && !left)) {
			//we are already at the begining of file and a left offset is needed
			//or we are already at the end of file and a right offset is needed
			return currentZoomX;		//do nothing, just return current zoom
		}

		//compute the current start value and end values of the current zoom
		double startOffset = getAxeXValue(startMsg) - currentZoomX.getFirstXValue();
		double endOffset = currentZoomX.getLastXValue() - getAxeXValue(endMsg);
		if(startOffset < 0)
			startOffset = 0;
		if(endOffset < 0)
			endOffset = 0;

		//translate the start and end values to virtual X values
		AxeXMessageVirtualValue startValue = getInstance().vValues.getValueByMsgId(startMsg.getMessageId());
		double virtualStartValue = startValue.getVirtualValue() - startOffset;
		AxeXMessageVirtualValue endValue = getInstance().vValues.getValueByMsgId(endMsg.getMessageId());
		double virtualEndValue = endValue.getVirtualValue() + endOffset;

		//compute the expected start and end values
		double expectedOffsetedStart = left ? virtualStartValue - offset : virtualStartValue + offset;
		double expectedOffsetedEnd = left ? virtualEndValue - offset : virtualEndValue + offset;
		//check if start is in values interval
		if(expectedOffsetedStart < values.get(0).getVirtualValue()) {
			expectedOffsetedStart = values.get(0).getVirtualValue();
		}
		if(expectedOffsetedEnd > values.get(values.size()-1).getVirtualValue()) {
			expectedOffsetedEnd = values.get(values.size()-1).getVirtualValue();
		}
		//something is wrong here ... we should not have this situation ... we cannot continue further
		if(expectedOffsetedStart >= expectedOffsetedEnd)
			return currentZoomX;

		boolean startFound = false;
		AxeXMessageVirtualValue virtualValue;
		int newFirstMsgId = messages.get(0).getMessageId();
		int newLastMsgId = newFirstMsgId;
		double newValueBeforeFirstMsg = expectedOffsetedStart;
		double newValueAfterLastMsg = expectedOffsetedEnd;
		double virtualXValue;
		for(int i = 0; i<values.size(); i++) {
			virtualValue = values.get(i);
			virtualXValue = virtualValue.getVirtualValue();
			if(!startFound && virtualXValue >= expectedOffsetedStart) {
				newFirstMsgId = virtualValue.getMsgId();
				//translate the virtual value to the real X value
				newValueBeforeFirstMsg = getAxeXValue(enr.getGoodMessage(newFirstMsgId)) - 
				(virtualXValue - expectedOffsetedStart);
				startFound = true;
			}
			if(virtualXValue >= expectedOffsetedEnd) {
				//get the msg last message id
				newLastMsgId = values.get(i-1).getMsgId();
				newValueAfterLastMsg = getAxeXValue(enr.getGoodMessage(newLastMsgId)) + 
				(expectedOffsetedEnd - values.get(i-1).getVirtualValue());
				break;
			}
		}
		//create a new zoom according to the offset
		ZoomX zoomX = new ZoomX();
		zoomX.setFirstVisibleMsgId(newFirstMsgId);
		zoomX.setFirstXValue(newValueBeforeFirstMsg);
		zoomX.setLastVisibleMsgId(newLastMsgId);
		zoomX.setLastXValue(newValueAfterLastMsg);
		return zoomX;
	}
}