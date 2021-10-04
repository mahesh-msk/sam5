package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.PointImagine;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.VirtualPoint;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class CourbePixelsInfo {
	private Courbe courbe;
	private ArrayList<VariablePixelInfo> xPosToPixelInfo;
	private ArrayList<VariablePixelInfo> msgToPixelInfo;
	private PointImagine previousPix,nextPix;
	private int lastabscisse=-1,lastID=-1;

	private LinkedList<VirtualPoint> virtualPointLinkedList = new LinkedList<VirtualPoint>();
	
	public CourbePixelsInfo() {
		xPosToPixelInfo = new ArrayList<VariablePixelInfo>();
		msgToPixelInfo = new ArrayList<VariablePixelInfo>();
	}

	public void addPixelInfo(Integer abscisse, Integer ordonee, CourbeMessageValue msgVal, boolean isValid) {
		VariablePixelInfo curVarPixelInfo = new VariablePixelInfo();

		if ((curVarPixelInfo = getPixelInfoByAbscisse(abscisse)) == null) {
			curVarPixelInfo = new VariablePixelInfo(abscisse, isValid);
	
			if (lastabscisse == curVarPixelInfo.getAbscisse()) {
				xPosToPixelInfo.set(xPosToPixelInfo.size() - 1, curVarPixelInfo);
			} else {
				xPosToPixelInfo.add(curVarPixelInfo);
				lastabscisse = curVarPixelInfo.getAbscisse();
			}
		} else {
			//if we have at least one value in this pixel set to true, we set it forced to true 
			if(curVarPixelInfo.isValid() != isValid)
				curVarPixelInfo.setValid(true);
		}

		curVarPixelInfo.addCourbeMessageValue(msgVal, ordonee);

		if (lastID == msgVal.getMsgId()) {
			msgToPixelInfo.set(msgToPixelInfo.size() - 1, curVarPixelInfo);
		} else {
			msgToPixelInfo.add(curVarPixelInfo);
			lastID = msgVal.getMsgId();
		}
	}

	public boolean msgIDinCMV(ArrayList<VariablePixelInfo> msgToPixelInfo,int flotantIndice,int ID){
		VariablePixelInfo vpi=msgToPixelInfo.get(flotantIndice);
		List<CourbeMessageValue> listCMV=vpi.getVariableValues();
		for (CourbeMessageValue value : listCMV) {
			if (value.getMsgId()==ID) {
				return true;
			}
		}
		return false;
	}

	public VariablePixelInfo getPixelInfoByMessageId(int ID) {

		int size=msgToPixelInfo.size();
		if (size==0) return null;
		int firstID=msgToPixelInfo.get(0).getVariableValues().get(0).getMsgId();
		int lastID=msgToPixelInfo.get(size-1).getVariableValues().
		get(msgToPixelInfo.get(size-1).getVariableValues().size()-1).getMsgId();

		long estim=0;
		if (lastID==firstID) {
			estim=0;
		}else
			estim=((long)(ID-firstID)*size)/(lastID-firstID)-1;

		if(estim<0)
			estim = 0;
		if (estim>size-1)
			estim = size-1;

		boolean messageFound=false;
		int flotantIndice=(int)estim;
		int iterator=1;
		while (!messageFound && flotantIndice>=-size && flotantIndice<=2*size) {
			if (flotantIndice>=0 && flotantIndice<size && msgIDinCMV(msgToPixelInfo,flotantIndice,ID)) {
				messageFound=true;
			}else{
				flotantIndice=flotantIndice+iterator;
				iterator=(iterator>0 ? iterator+1 : iterator-1 ) *(-1);
			}}
		if (messageFound) {
			return msgToPixelInfo.get(flotantIndice);
		}else{
			return null;
		}
	}

	public int getIndiceFromPixelInfoByAbscisse(int abscisse){
		int size=xPosToPixelInfo.size();
		if (size==0) return -1;
		int firstAbscisse=xPosToPixelInfo.get(0).getAbscisse();
		int lastAbscisse=xPosToPixelInfo.get(size-1).getAbscisse();
		long estim=0;
		if (lastAbscisse==firstAbscisse) {
			estim=0;
		}else{
			estim=((long)(abscisse-firstAbscisse)*size)/(lastAbscisse-firstAbscisse)-1;
		}
		if(estim<0)
			estim = 0;
		if (estim>size-1)
			estim = size-1;
		boolean messageFound=false;

		int flotantIndice=(int)estim;
		int iterator=1;

		while (!messageFound && flotantIndice>=-15 && flotantIndice<=size+15) {
			if (flotantIndice>=0 && flotantIndice<size && xPosToPixelInfo.get((int)flotantIndice).getAbscisse()==abscisse) {
				messageFound=true;
			}else{
				flotantIndice=flotantIndice+iterator;
				iterator=(iterator>0 ? iterator+1 : iterator-1 ) *(-1);
			}
		}

		if (messageFound) {
			return flotantIndice;
		}else{
			return -1;
		}
	}

	public VariablePixelInfo getPixelInfoByAbscisse(int abscisse) {
		int size=xPosToPixelInfo.size();
		if (size==0) 
			return null;
		
		int firstAbscisse=xPosToPixelInfo.get(0).getAbscisse();
		int lastAbscisse=xPosToPixelInfo.get(size-1).getAbscisse();
		long estim=0;
	
		if (lastAbscisse==firstAbscisse) {			
			estim=0;
		}else{			
			estim=((long)(abscisse-firstAbscisse)*size)/(lastAbscisse-firstAbscisse)-1;
		}
		
		if(estim<0)	
			estim = 0;
		
		if (estim>size-1) 
			estim = size-1;
		
		boolean messageFound=false;
		int flotantIndice=(int)estim;
		int iterator=1;
		while (!messageFound && flotantIndice>=-15 && flotantIndice<=size+15) {
			if (flotantIndice>=0 && flotantIndice<size && xPosToPixelInfo.get((int)flotantIndice).getAbscisse()==abscisse) {
				messageFound=true;
			}else{
				flotantIndice=flotantIndice+iterator;
				iterator=(iterator>0 ? iterator+1 : iterator-1 ) *(-1);
			}
		}
		
		if (messageFound) {		
			return xPosToPixelInfo.get(flotantIndice);
		}else{	
			return null;	
		}
	}

	public VariablePixelInfo getPixelInfoByAbscisse2(int abscisse) {
		int size=xPosToPixelInfo.size();if (size==0) return null;
		int firstAbscisse=xPosToPixelInfo.get(0).getAbscisse();
		int lastAbscisse=xPosToPixelInfo.get(size-1).getAbscisse();
		long estim=0;
		if (lastAbscisse==firstAbscisse) {			estim=0;
		}else{			estim=((long)(abscisse-firstAbscisse)*size)/(lastAbscisse-firstAbscisse)-1;	}
		if(estim<0)	estim = 0;
		if (estim>size-1)	estim = size-1;
		boolean messageFound=false;
		boolean depassementValeur=false;
		int flotantIndice=(int)estim;
		int iterator=1;
		boolean estimationInferieure=xPosToPixelInfo.get((int)flotantIndice).getAbscisse()<abscisse;

		while (!messageFound && !depassementValeur && flotantIndice>=-15 && flotantIndice<=size+15) {
			if(flotantIndice<0)
				flotantIndice=0;
			else if(flotantIndice>=size)
				flotantIndice= size-1;
			if (xPosToPixelInfo.get((int)flotantIndice).getAbscisse()==abscisse) {
				messageFound=true;
			}else{
				if (xPosToPixelInfo.get((int)flotantIndice).getAbscisse()>abscisse && estimationInferieure) {
					messageFound=true;
					depassementValeur=true;
				}else if (xPosToPixelInfo.get((int)flotantIndice).getAbscisse()<abscisse && !estimationInferieure) {
					messageFound=true;
					depassementValeur=true;
				}else{
					flotantIndice=flotantIndice+iterator;
					iterator=(iterator>0 ? iterator+1 : iterator-1 ) *(-1);
				}
			}
		}

		if (messageFound) {
			return xPosToPixelInfo.get(flotantIndice);
		}else{
			return null;
		}
	}

	public Courbe getCourbe() {
		return this.courbe;
	}

	public void setCourbe(Courbe courbe) {
		this.courbe = courbe;
	}

//	public Collection<VariablePixelInfo> getPixelsInfos() {
//	return xPosToPixelInfo.values();
//	}

//	public VariablePixelInfo[] getPixelsInfosAsArray() {
//	return xPosToPixelInfo.values().toArray(new VariablePixelInfo[xPosToPixelInfo.values().size()]);
//	}

//	public Set<Integer> getPixels() {
//	return xPosToPixelInfo.keySet();
//	}

//	public Set<Integer> getMessagesIds() {
//	return this.msgToPixelInfo.keySet();
//	}

	public boolean haveOneValue(){
		int nbPixVal;
		if (this.courbe.getValeurs() != null) {
			nbPixVal = this.courbe.getValeurs().length;
			
			for (int i = 0; i < nbPixVal; i++) {
				if (this.courbe.getValeurs()[i]!=null) {
					return true;
				}
			}
		} else {
			nbPixVal = this.courbe.getValeursSimples().length;
			
			for (int i = 0; i < nbPixVal; i++) {
				if (this.courbe.getValeursSimples()[i]!=null) {
					return true;
				}
			}
		}
	
		return false;
	}
	public int getClosestValue(int indice,boolean left){
		int nbPixVal;
		boolean trouve=false;
		int i=indice;
		int increment;
		if (left)
			increment=-1;
		else
			increment=1;
		
		if (this.courbe.getValeurs() != null) {
			nbPixVal=this.courbe.getValeurs().length;
			
			while (!trouve && i>=0 && i<nbPixVal) {
				if (this.courbe.getValeurs()[i]!=null) {
					return i;
				}
				i=i+increment;
			}
		} else {
			nbPixVal=this.courbe.getValeursSimples().length;
			
			while (!trouve && i>=0 && i<nbPixVal) {
				if (this.courbe.getValeursSimples()[i]!=null) {
					return i;
				}
				i=i+increment;
			}
		}
				
		return -1;
	}

	public VariablePixelInfo getClosestMessageId(int xPos, 
			TYPE_CLOSEST_MESSAGE typeRequest) {
		VariablePixelInfo pixelInfo = null;
		VariablePixelInfo prevPixelInfo = null;
		VariablePixelInfo nextPixelInfo = null;
		VariablePixelInfo curPixelInfo;

		for (int pos = xPos; pos < xPosToPixelInfo.size()+xPos; pos++) {
			curPixelInfo = this.getPixelInfoByAbscisse2(pos);
			//check if message possition is greater than xPos and if we have a message there
			if(curPixelInfo!=null && pos >= xPos && curPixelInfo.getFirstMessageId() != -1) {	
				nextPixelInfo = curPixelInfo;
				prevPixelInfo=curPixelInfo;
				break;
			}
			prevPixelInfo = curPixelInfo;
		}
		if(typeRequest == TYPE_CLOSEST_MESSAGE.BOTH) {
			//we always take the preceding message (as old SAM did)
			//we might have the nextPixelInfo null or not
			//we might also have the first message forced added due to a zoom  
			if(prevPixelInfo != null && prevPixelInfo.getFirstMessageId() != -1 && Math.abs(prevPixelInfo.getAbscisse()-xPos) < Math.abs(nextPixelInfo.getAbscisse()-xPos)) 	 
				pixelInfo = prevPixelInfo;
			else 				//we have no message before this ... we must take the next pixel info
				pixelInfo = nextPixelInfo;	//it can be null or not
		} else if(typeRequest == TYPE_CLOSEST_MESSAGE.PREV)
			pixelInfo = prevPixelInfo;
		else if(typeRequest == TYPE_CLOSEST_MESSAGE.NEXT)
			pixelInfo = nextPixelInfo;	//it can be null or not

		return pixelInfo;
	}

	public static CourbePixelsInfo TrouverUneBonneCourbe(List<CourbePixelsInfo> courbesPixelsInfo){
		CourbePixelsInfo courbePixelsInfo=null;
		int sizeOfPixelsInfo=0;
		int nbCourbes=courbesPixelsInfo.size();
		for (int i = 0; i < nbCourbes; i++) {
			if (courbesPixelsInfo.get(i).haveOneValue() && courbesPixelsInfo.get(i).msgToPixelInfo.size()>sizeOfPixelsInfo) {
				courbePixelsInfo = courbesPixelsInfo.get(i);
				sizeOfPixelsInfo=courbePixelsInfo.msgToPixelInfo.size();
			}
		}
		return courbePixelsInfo;
	}

	public PointImagine getNextPix() {
		return nextPix;
	}

	public void setNextPix(PointImagine nextPix) {
		this.nextPix = nextPix;
	}

	public PointImagine getPreviousPix() {
		return previousPix;
	}

	public void setPreviousPix(PointImagine previousPix) {
		this.previousPix = previousPix;
	}

	public void clearPreviousPix(){
		this.previousPix=null;
	}

	public void clearNextPix(){
		this.nextPix=null;
	}

	public ArrayList<VariablePixelInfo> getXPosToPixelInfo() {
		return xPosToPixelInfo;
	}

	public void setXPosToPixelInfo(ArrayList<VariablePixelInfo> posToPixelInfo) {
		xPosToPixelInfo = posToPixelInfo;
	}

	/**
	 * @return
	 */
	public LinkedList<VirtualPoint> getVirtualPointLinkedList() {
	    return virtualPointLinkedList;
	}
	
	/**
	 * @return
	 */
	public void setVirtualPointLinkedList(LinkedList<VirtualPoint> virtualPointLinkedList) {
	    this.virtualPointLinkedList = virtualPointLinkedList;
	}
	

}
