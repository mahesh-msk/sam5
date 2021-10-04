package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

import java.util.List;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.Curseur;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;

public class MouvementCurseur {

	private GrapheGUI graphe;
	private List<CourbePixelsInfo> courbesPixelsInfo;
	private Integer cursorPositionMsgId;

	public MouvementCurseur(GrapheGUI ggui) {
		this.graphe=ggui;
		this.courbesPixelsInfo=ggui.getCourbesPixelsInfo();
		this.cursorPositionMsgId=ggui.getCursorPositionMsgId();
	}

	public CourbePixelsInfo TrouverUneBonneCourbe(){
		CourbePixelsInfo courbePixelsInfo=null;
		int nbCourbes=courbesPixelsInfo.size();
		for (int i = 0; i < nbCourbes; i++) {
			if (courbesPixelsInfo.get(i).haveOneValue()) {
				courbePixelsInfo = courbesPixelsInfo.get(i);
				break;
			}
		}
		return courbePixelsInfo;
	}

	private boolean traiterPremierPixelDroite(Object[] pixelInfos,CourbeMessageValue msgVal,VariablePixelInfo closestMsgPixelInfo,int msgIdx,
			int vitesse, CourbePixelsInfo courbePixelsInfo,boolean changePixel){
		msgVal = closestMsgPixelInfo.getValue(msgIdx + 1*vitesse);
		if(msgVal != null) {
			if (cursorPositionMsgId!=msgVal.getMsgId()) {
				setCursorPositionMsgId(msgVal.getMsgId());
				changePixel = false;	//move to next message	
				return changePixel;
			}
		}

		boolean trouv=false;
		int abscisseCourante=closestMsgPixelInfo.getAbscisse();
		int nbVarInPix=closestMsgPixelInfo.getVariableValues().size();
		int k=msgIdx+1;
		while (k<nbVarInPix-1-msgIdx && !trouv) {
			msgVal = closestMsgPixelInfo.getValue(k);
			if (msgVal!=null) {
				if (cursorPositionMsgId != msgVal.getMsgId()) {
					setCursorPositionMsgId(msgVal.getMsgId());
					trouv=true;									
				}
			}
			k++;
		}
		if (!trouv) {
			VariablePixelInfo vpi=(VariablePixelInfo)pixelInfos[pixelInfos.length-1];
			while (!trouv && abscisseCourante<vpi.getAbscisse()) {
				int nbPix=pixelInfos.length;
				VariablePixelInfo varPix=null;
				int i=0;
				while (i<nbPix && !trouv) {					
					varPix=(VariablePixelInfo)pixelInfos[i];
					if (varPix.getAbscisse()>abscisseCourante) {
						int nbMSG=varPix.getVariableValues().size();
						int j=0;
						while (j<nbMSG && !trouv) {	
							msgVal = varPix.getValue(j);
							if (msgVal!=null) {

								if (cursorPositionMsgId != msgVal.getMsgId()) {
									setCursorPositionMsgId(msgVal.getMsgId());
									changePixel=true;
									trouv=true;									
								}
							}
							j++;
						}
					}
					i++;
				}
			}
		}

		return changePixel;
	}

	private boolean traiterDernierPointGauche(CourbeMessageValue msgVal,VariablePixelInfo closestMsgPixelInfo,int vitesse,int msgIdx,
			boolean changePixel){
		msgVal = closestMsgPixelInfo.getValue(msgIdx - 1*vitesse);
		if(msgVal != null) {
			setCursorPositionMsgId(msgVal.getMsgId());
			changePixel = false;	//move to previous message
		}
		return changePixel;
	}

	private VariablePixelInfo msgIDisValid(Message messageCourant, 
			VariablePixelInfo closestMsgPixelInfo,List <CourbePixelsInfo> courbesPixelsInfo,
			boolean turbo,boolean arrowLeft,CourbeMessageValue msgVal){

		CourbePixelsInfo courbePixelsInfo=null;
		messageCourant = Curseur.getInstance().getEv().getCurrentMessage();
		Object[] pixelInfos=null;

		int ecart=-1;
		for (int i = 0; i < courbesPixelsInfo.size(); i++) {
			VariablePixelInfo vpi=courbesPixelsInfo.get(i).getPixelInfoByAbscisse(Curseur.getInstance().getpositionCurseur());
			if (vpi!=null) {
				int abscisse=vpi.getAbscisse();
				if (ecart==-1 || ecart>Math.abs(abscisse-Curseur.getInstance().getpositionCurseur())) {
					ecart=Math.abs(abscisse-Curseur.getInstance().getpositionCurseur());
					courbePixelsInfo=courbesPixelsInfo.get(i);
				}
			}	
		}
		pixelInfos=courbePixelsInfo.getXPosToPixelInfo().toArray();

		int msgIdx=-1;
		messageCourant = Curseur.getInstance().getEv().getCurrentMessage();
		closestMsgPixelInfo=courbePixelsInfo.getPixelInfoByMessageId(messageCourant.getMessageId());
		if (messageCourant!=null && closestMsgPixelInfo!=null) {
			msgIdx = closestMsgPixelInfo.getIndexForMessageId(getCursorPositionMsgId());
		} 

		//normally we should find the message in this pixel
		//we check if is not the last or the first message in pixel
		boolean changePixel = true;

		int vitesse=1;
		if (turbo) {
			vitesse=100;
		}
		if(msgIdx != -1) {	//we found the message in this pixel
			if(msgIdx == 0) {	//if is on first position
				if(!arrowLeft) {	//but we have a move to right
					changePixel=traiterPremierPixelDroite(pixelInfos,msgVal,closestMsgPixelInfo,msgIdx,vitesse, courbePixelsInfo,changePixel);					
				}else{
					if(courbePixelsInfo.getXPosToPixelInfo().get(0).getVariableValues().get(0).getMsgId()==messageCourant.getMessageId()){
						setCursorPositionMsgId(closestMsgPixelInfo.getValue(0).getMsgId());
						return closestMsgPixelInfo;
					}
				}
			} else if(msgIdx == closestMsgPixelInfo.getValuesCount()-1) {
				//we are on the last message in the pixel
				if(arrowLeft) {	
					changePixel=traiterDernierPointGauche(msgVal, closestMsgPixelInfo, vitesse, msgIdx, changePixel);
				}
			} else {
				//we have a message inside the current pixel
				//get the previous or next message id from current pixel
				setCursorPositionMsgId(closestMsgPixelInfo.getValue(arrowLeft ? msgIdx - 1 : msgIdx + 1).getMsgId());
				changePixel = false;
			}
		}
		//if we have to change the current pixel
		if(changePixel) {
			//search for the current pixel info
			closestMsgPixelInfo=changePix(pixelInfos,closestMsgPixelInfo,arrowLeft);
		}
		return closestMsgPixelInfo;
	}

	private VariablePixelInfo changePix(Object[] pixelInfos,VariablePixelInfo closestMsgPixelInfo,boolean arrowLeft){
		int i = 0;
		for(i = 0; i<pixelInfos.length; i++) {
			if(pixelInfos[i] == closestMsgPixelInfo)
				break;
		}
		//if is the last or the first message in the current pixel
		//check if we are not on the first or last pixel
		if((i < 1 && arrowLeft && Curseur.getInstance().getpositionCurseur()<=MARGE_LATERALE) 
				//		|| (i >= pixelInfos.length-2 && 
				|| (i == pixelInfos.length-2 &&
						!arrowLeft && Curseur.getInstance().getpositionCurseur()>=this.graphe.getWidth()-MARGE_LATERALE-1
				)){

			ZoomComposite currentZoom = (ZoomComposite)GestionnaireZoom.getZoomCourant();
			double offset=Math.abs(this.graphe.getWidth()-2*MARGE_LATERALE)/2*GestionnaireAxes.getInstance().getCurrentAxeX().getResolution();

			ZoomComposite createdZoom = (ZoomComposite)currentZoom.clone();
			ZoomX newZoomX = GestionnaireAxes.offsetAxeX(offset, arrowLeft);
			ZoomX zoomX = (ZoomX)createdZoom.getEnfant(0);
			if (newZoomX.getFirstVisibleMsgId()==newZoomX.getLastVisibleMsgId()) {
				return null;
			}

			int nbPix=pixelInfos.length;
			int j=0;
			int newZoomXFirstVisibleMsgId=-1;
			try {
				while(newZoomXFirstVisibleMsgId==-1 && j<nbPix){
					VariablePixelInfo var=(VariablePixelInfo)pixelInfos[j];
					if (var.getFirstMessageId()>=newZoomX.getFirstVisibleMsgId()) {
						if (var.getFirstMessageId()==newZoomX.getFirstVisibleMsgId()) {
							newZoomXFirstVisibleMsgId=var.getFirstMessageId();
						}else{
							newZoomXFirstVisibleMsgId=((VariablePixelInfo)(pixelInfos[j-1])).getFirstMessageId();
						}
					}
					j++;
				}
			} catch (Exception e) {

			}

			int newZoomXLastVisibleMsgId=-1;
			try {
				int k=j;
				while(newZoomXLastVisibleMsgId==-1 && k<nbPix){
					VariablePixelInfo var=(VariablePixelInfo)pixelInfos[k];
					if (var.getFirstMessageId()>=newZoomX.getLastVisibleMsgId()) {
						if (var.getFirstMessageId()==newZoomX.getLastVisibleMsgId()) {
							newZoomXLastVisibleMsgId=var.getLastMessageId();
						}else{
							newZoomXLastVisibleMsgId=((VariablePixelInfo)pixelInfos[k-1]).getLastMessageId();
						}
					}
					k++;
				}
			} catch (Exception e) {

			}
			zoomX.setFirstVisibleMsgId(newZoomXFirstVisibleMsgId==-1?newZoomX.getFirstVisibleMsgId():newZoomXFirstVisibleMsgId);
			zoomX.setLastVisibleMsgId(newZoomXLastVisibleMsgId==-1?newZoomX.getLastVisibleMsgId():newZoomXLastVisibleMsgId);
			zoomX.setFirstXValue(newZoomX.getFirstXValue());
			zoomX.setLastXValue(newZoomX.getLastXValue());
			GestionnaireZoom.setZoomCourant(createdZoom);

		}else{
			try {
				closestMsgPixelInfo = (VariablePixelInfo)pixelInfos[arrowLeft ? i - 1 : i + 1];
			} catch (Exception e) {

			}
			//get the last message id of the previous pixel or the 
			//first message of the next pixel
			int newID=arrowLeft ? closestMsgPixelInfo.getLastMessageId() : closestMsgPixelInfo.getFirstMessageId();
			setCursorPositionMsgId(newID);
			Curseur.getInstance().setMsgId(newID);
		}	
		return closestMsgPixelInfo;
	}

	public VariablePixelInfo RechercherNouveauPixel(boolean arrowLeft, boolean isToExtremity, boolean turbo){


		if(courbesPixelsInfo == null || courbesPixelsInfo.size() == 0 || !Curseur.getInstance().getCurseurVisible())
			return null;
		CourbePixelsInfo courbePixelsInfo=TrouverUneBonneCourbe();

		//: we should handle here the case when we must change the X axe
		//		if we are zoomed and we are on the left or right most msg if 
		Object[] pixelInfos = courbePixelsInfo.getXPosToPixelInfo().toArray();
		VariablePixelInfo closestMsgPixelInfo=null;
		Message messageCourant=null;

		CourbeMessageValue msgVal=null;
		if(cursorPositionMsgId == null || isToExtremity) {
			closestMsgPixelInfo = (VariablePixelInfo)pixelInfos[!arrowLeft ? 0 : pixelInfos.length-1];
			setCursorPositionMsgId(closestMsgPixelInfo.getFirstMessageId());
			return closestMsgPixelInfo;
		} else {
			return msgIDisValid(messageCourant,closestMsgPixelInfo,courbesPixelsInfo,turbo,arrowLeft,msgVal);
		}			
	}

	public List<CourbePixelsInfo> getCourbesPixelsInfo() {
		return courbesPixelsInfo;
	}

	public void setCourbesPixelsInfo(List<CourbePixelsInfo> courbesPixelsInfo) {
		this.courbesPixelsInfo = courbesPixelsInfo;
	}

	public Integer getCursorPositionMsgId() {
		return cursorPositionMsgId;
	}

	public void setCursorPositionMsgId(Integer cursorPositionMsgId) {
		this.cursorPositionMsgId = cursorPositionMsgId;
		Curseur.getInstance().setMsgId(cursorPositionMsgId);
	}

	public GrapheGUI getGraphe() {
		return graphe;
	}

	public void setGraphe(GrapheGUI graphe) {
		this.graphe = graphe;
	}
}
