package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class VariablePixelInfo {
	private Integer abscisse;
	private List<CourbeMessageValue> variableValues = new ArrayList<CourbeMessageValue>();
	private List<CourbeMessageValue> variablePropagatedValues = new ArrayList<CourbeMessageValue>();
	private List<Integer> ordoneesList = new ArrayList<Integer>();
	private Integer minOrdonee;
	private Integer maxOrdonee;
	private Integer minPropagatedOrdonee;
	private Integer maxPropagatedOrdonee;
	private boolean isValid;
//	private int MessageId;
//	private int xPosToPixelInfo;
//	private int msgToPixelInfo;

	public VariablePixelInfo() {
		// TODO Auto-generated constructor stub
	}

	public VariablePixelInfo(Integer abscisse, boolean isValid) {
		this.abscisse = abscisse;
		this.isValid = isValid;
	}

	/**
	 * Sets the abscisse for this pixel
	 * @param abscisse
	 */
	public void setAbscisse(Integer abscisse) {
		this.abscisse = abscisse;
	}

	/**
	 * Returns the abscisse for this pixel
	 * @return
	 */
	public Integer getAbscisse() {
		return this.abscisse;
	}

	/**
	 * Returns the first ordonee for this pixel
	 * @return
	 */
	public Integer getFirstOrdonnee() {
	    if(ordoneesList.size() > 0){
		return ordoneesList.get(0);
	    }
	    return 0;
	}

	/**
	 * Returns the last ordonee for this pixel
	 * @return
	 */
	public Integer getLastOrdonnee() {
	    if(ordoneesList.size() > 0){
		return ordoneesList.get(ordoneesList.size()-1);
	    }
	    return 0;
	}

	/**
	 * Returns the first ordonee for this pixel not propagated
	 * @return
	 */
	public Integer getFirstOrdonneeNotPropagated() {
	    	Integer size=ordoneesList.size();
		for (int i = 0; i < size; i++) {
			if (!this.getValue(i).isPropagated()) {
				return ordoneesList.get(i);
			}
		}
		return null;
	}

	/**
	 * Returns the last ordonee for this pixel not propagated
	 * @return
	 */
	public Integer getLastOrdonneeNotPropagated() {
		int size=ordoneesList.size();
		if (size>0) {
			for (int i = size-1; i>=0; i--) {
				if (!this.getValue(i).isPropagated()) {
					return ordoneesList.get(i);
				}
			}
		}
		return null;
	}

	/**
	 * Returns the ordonees for this pixel not propagated
	 * @return
	 */
	public ArrayList<Integer> getOrdonneesNotPropagated() {
		ArrayList<Integer> ordonneeNotPropagated=new ArrayList<Integer>();
//		int size=ordoneesList.size();
//		boolean first=true;
//		for (int i = 0; i < size; i++) {
//			if (!this.getValue(i).isPropagated()
//					&&(!ordonneeNotPropagated.contains(ordoneesList.get(i)))) {
//				if (first) {
//					ordonneeNotPropagated.add(ordoneesList.get(i));
//					ordonneeNotPropagated.add(ordoneesList.get(i));
//					first=false;
//				}else{
//					if (ordoneesList.get(i)<ordonneeNotPropagated.get(0)) {
//						ordonneeNotPropagated.set(0, ordoneesList.get(i));
//					}
//					if (ordoneesList.get(i)>ordonneeNotPropagated.get(1)) {
//						ordonneeNotPropagated.set(1, ordoneesList.get(i));
//					}
//				}
//			}
//		}
		ordonneeNotPropagated.add(this.minPropagatedOrdonee);
		ordonneeNotPropagated.add(this.maxPropagatedOrdonee);
		if (ordonneeNotPropagated.get(0)==null 
				|| ordonneeNotPropagated.get(1)==null
				||ordonneeNotPropagated.get(0)==ordonneeNotPropagated.get(1)) {
			return null;
		}
		return ordonneeNotPropagated;
	}

	/**
	 * Adds a courbe message value to this pixel information
	 * The max and min are updated if necessarily
	 *  
	 * @param msgVal
	 */
	public void addCourbeMessageValue(CourbeMessageValue msgVal, Integer ordonee) {
		if(msgVal == null)
			return;
		
		if(variableValues.size() == 0) {
			minOrdonee = ordonee;
			maxOrdonee = ordonee;
		} else if(minOrdonee != null && maxOrdonee != null && ordonee != null) {
			if(ordonee < minOrdonee)
				minOrdonee = ordonee;
			else if(ordonee > maxOrdonee)
				maxOrdonee = ordonee;
		}
		if (!msgVal.isPropagated()) {
			if (variablePropagatedValues.size()==0) {
				minPropagatedOrdonee = ordonee;
				maxPropagatedOrdonee = ordonee;
			}else if(minPropagatedOrdonee != null && maxPropagatedOrdonee != null && ordonee != null){
				if(ordonee < minPropagatedOrdonee)
					minPropagatedOrdonee = ordonee;
				else if(ordonee > maxPropagatedOrdonee)
					maxPropagatedOrdonee = ordonee;
			}
			variablePropagatedValues.add(msgVal);
		}
		variableValues.add(msgVal);
		ajouterOrdonne(ordonee,-1);
	}

	public Integer setOrdonee(Integer ordo,int index) {
		if(index >= 0 && index < ordoneesList.size())
			ajouterOrdonne(ordo,index);
		return 0;
	}

	public void ajouterOrdonne(Integer ordo,int pos){
		//	if (!ordoneesList.contains(ordo)) {
		if (pos==-1) {
			ordoneesList.add(ordo);
		}else{
			ordoneesList.set(pos, ordo);
		}
		//	}
	}

	/**
	 * Return the ordonee for the given index from the ordonees list
	 * 
	 * @param index
	 * @return
	 */
	public Integer getOrdonee(int index) {
		if(index >= 0 && index < ordoneesList.size()){
			return ordoneesList.get(index);
		}
		return 0;
	}



	public CourbeMessageValue getFirstVariableValue() {
		if(variableValues.size() > 0)
			return variableValues.get(0);
		return null;
	}

	public CourbeMessageValue getValue(int index) {
		if(index >= 0 && index < variableValues.size())
			return variableValues.get(index);
		return null;
	}

	/**
	 * If a valid pixel information and we have variables values informations for this pixel
	 * returns the first message for this pixel otherwise returns -1
	 * @return
	 */
	public int getFirstMessageId() {
		if(isValid && variableValues.size() > 0)
			return variableValues.get(0).getMsgId();
		return -1;
	}

	/**
	 * If a valid pixel information and we have variables values informations for this pixel
	 * returns the last message for this pixel otherwise returns -1
	 * @return
	 */
	public int getLastMessageId() {
		if(isValid && variableValues.size() > 0)
			return variableValues.get(variableValues.size() - 1).getMsgId();
		return -1;
	}

	/**
	 * Specifies if this pixel was added for a real message or it was added as 
	 * virtual first or last pixel for the case of a zoom
	 * 
	 * @return
	 */
	public boolean isValid() {
		return isValid;
	}

	public void setValid(boolean valid) {
		this.isValid = valid;
	}

	/**
	 * Return the number of values for this pixel
	 * If the pixel information was added just as the first pixel or the last
	 * pixel due to a zoom but it does not really correspond to a message, 
	 * it will return 0
	 *   
	 * @return
	 */
	public int getValuesCount() {
		return isValid ? variableValues.size() : 0;
	}

	/**
	 * Returns the min ordonee value for this pixel
	 * 
	 * @return
	 */
	public Integer getMinOrdonee() {
		return this.minOrdonee;
	}

	/**
	 * Returns the max ordonee value for this pixel
	 * @return
	 */
	public Integer getMaxOrdonee() {
		return this.maxOrdonee;
	}



	/**
	 * Searches for a message id in this pixel information
	 * 
	 * @param messageId
	 * @return
	 */
	public int getIndexForMessageId(int messageId) {
		int i = 0;
		try {
			for(CourbeMessageValue msgVal: variableValues) {
				if(msgVal.getMsgId() == messageId)
					return i;
				i++;
			}
		} catch (Exception e) {
			return -1;
		}

		return -1;
	}

	public List<CourbeMessageValue> getVariableValues() {
		return variableValues;
	}

	/**
	 * Gets the pixel according for the given message id (if any)
	 * @param msgId
	 * @return
	 */
	public static VariablePixelInfo getXPositionForMessageId(int msgId,List<CourbePixelsInfo> courbesPixelsInfo) {
		for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {
			int i = 0;
			VariablePixelInfo prevPixelInfo = null;
			for(VariablePixelInfo pixelInfo: courbePixelsInfo.getXPosToPixelInfo()) {
				if (pixelInfo!=null) {
					if(msgId < pixelInfo.getFirstMessageId()) {
						//if this message is before the first courbe pixel, check the next courbe
						if(i == 0)
							break;
						return prevPixelInfo;
					} else {	//greater or equal
						//if is exaxtly the message associated to current pixel
						if(pixelInfo.getFirstMessageId() == msgId)
							return pixelInfo;
					}
					i++;
					prevPixelInfo = pixelInfo;	//check the next pixel of the curve
				}
			}
			//we might have the last pixel and the resolution is greater than 1
			if(prevPixelInfo != null && msgId <= GestionnaireAxes.getInstance().getCurrentAxeX().getIdMsgFin()) {
				return prevPixelInfo;
			}
		}
		return null;
	}

	public Integer getMaxPropagatedOrdonee() {
		return maxPropagatedOrdonee;
	}

	public void setMaxPropagatedOrdonee(Integer maxPropagatedOrdonee) {
		this.maxPropagatedOrdonee = maxPropagatedOrdonee;
	}

	public Integer getMinPropagatedOrdonee() {
		return minPropagatedOrdonee;
	}

	public void setMinPropagatedOrdonee(Integer minPropagatedOrdonee) {
		this.minPropagatedOrdonee = minPropagatedOrdonee;
	}

//	public int getMessageId() {
//	return MessageId;
//	}

//	public void setMessageId(int idMsg) {
//	this.MessageId = idMsg;
//	}
}