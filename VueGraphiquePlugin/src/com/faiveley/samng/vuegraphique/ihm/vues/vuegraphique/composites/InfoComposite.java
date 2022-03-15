package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosParcours;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.Messages;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.PositionReferenceZero;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GestionnaireGraphesNotifications;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.IGrapheCursorListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.IGrapheInfosListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.Curseur;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.ValuedCursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul.InfosBullesMarqueurs;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul.InfosBullesRefZero;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures.RuptureLegendeMessage;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.IZoomChangeListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomX;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;

public class InfoComposite extends Composite implements IGrapheCursorListener, IGrapheInfosListener,IZoomChangeListener {
	private int xCursorPosition;
	private long time;
	//	private long deltaTime;
	//	private double distance;
	private String absoluteTimeText;
	private String deltaTimeText;
	private String distanceText;
	public static final String DELTA = "\u2206";
	//	private Integer ref0XPos;
	private List<Integer> listMarkerPixels = new ArrayList<Integer>();
	boolean applyfilter=false;

	public InfoComposite(Composite parent, int style) {
		super(parent, style);
		initialize();
		GestionnaireGraphesNotifications.addGrapheCursorListener(this);
		GestionnaireGraphesNotifications.addGrapheInfosListener(this);
		GestionnaireZoom.getInstance().addListener(this);		
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setLayout(null);
		this.addListener(SWT.Paint,new Listener(){ 
			public void handleEvent(Event event) { 
				try {
					draw(event);
					drawMarkers(event.gc);
					drawRef0(event.gc);
					drawMessageRupture(event.gc);
				} catch (Exception e) {
//										e.printStackTrace();
				}
			}
		});
	}

	public void draw(Event event){
		Point textExtent;
		int maxXTextExtent;
		Color color = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		event.gc.setForeground(color);
		int height = getBounds().height;
		int width = getBounds().width;

		//		récupération uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();

		if (Curseur.getInstance().isSynchroniseCurseur()) {
			CursorPositionEvent ev = new CursorPositionEvent(messages.get(0),ActivatorData.getInstance().getSelectedMsg(),
					Curseur.getInstance().getpositionCurseur());
			//			ev.currentMessage=ActivatorData.getInstance().getMsgSelectionner();
			//			ev.xPos=Curseur.getInstance().getpositionCurseur();
			//			ev.firstMessage=messages.get(0);
			cursorPositionChanged(ev);
			Curseur.getInstance().setSynchroniseCurseur(false);
		}

		if(ActivatorVueGraphique.getDefault().getConfigurationMng().isFond_blanc()) {
			event.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
			event.gc.fillRectangle(0, 0, width, height);
		}

		event.gc.setForeground( event.display.getSystemColor(SWT.COLOR_BLACK));
		event.gc.setLineWidth(1);
		event.gc.drawLine(MARGE_LATERALE, 0, MARGE_LATERALE, height);

		if (Curseur.getInstance().getpositionCurseur()>-1 && Curseur.getInstance().getpositionCurseur()<width-MARGE_LATERALE)
			this.xCursorPosition=Curseur.getInstance().getpositionCurseur();

		if (Curseur.getInstance().getCurseurVisible()){
			if(this.xCursorPosition != 0) {
				//event.gc.setFont(textFont);
				if (absoluteTimeText==null) {
					cursorPositionChanged(Curseur.getInstance().getEv());
				}
				event.gc.drawLine(xCursorPosition, 0, xCursorPosition, height);
				event.gc.setFont(GestionnaireVueGraphique.getArial09TextFont());
				maxXTextExtent = event.gc.stringExtent(absoluteTimeText).x;
				textExtent = event.gc.stringExtent(deltaTimeText);
				if(textExtent.x > maxXTextExtent)
					maxXTextExtent = textExtent.x;
				textExtent = event.gc.stringExtent(distanceText);
				if(textExtent.x > maxXTextExtent)
					maxXTextExtent = textExtent.x;
				int textXDrawPosition = xCursorPosition + 5;
				if((xCursorPosition + maxXTextExtent) > width - 20)
					textXDrawPosition = xCursorPosition - (maxXTextExtent + 5);


				//////////////////////////////////////////////////
				//Ajout Infos Curseur Evolution 3.25 
				InfosParcours infosParcours = GestionnairePool.getInstance().getInfosParcours();
				List<Message> allMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
				ArrayList<String> infosParcoursChaine = new ArrayList<String>();
				int codeVar = 0;
				VariableComposite var = null;
				Message msgCourant=ActivatorData.getInstance().getSelectedMsg();
				if (infosParcours != null) {
					for (String info : infosParcours.getListeInformations()) {
						String variableInfosParcours="";
						DescripteurVariable descrVar=GestionnairePool.getInstance().getVariable(info).getDescriptor();
						codeVar = descrVar.getM_AIdentificateurComposant().getCode();
						if (descrVar != null) {
							for (Message msg : allMessages) {
								if (msg.getVariable(descrVar) != null) {
									//var simple
									variableInfosParcours=msg.getVariable(descrVar).getDescriptor().getNomUtilisateur().
											getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

									variableInfosParcours = variableInfosParcours+" : "+msg.getVariable(descrVar)+"";
								}else if (msg.getVariable(codeVar) != null) {
									//var complexe issue 810
									if (descrVar.getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
										var = (VariableComposite) msg.getVariable(codeVar);
										variableInfosParcours = var.getValeurBruteForVariableComposee();
									}
								}

								//si on est rendu au msg courant on s'arrête
								if (msg.getMessageId()>=msgCourant.getMessageId()) {
									break;
								}
							}
						}
						GestionnairePool.getInstance().getAllVariables();
						if (variableInfosParcours.equals("")) {
							variableInfosParcours=GestionnairePool.getInstance().getVariable(info).getDescriptor().getNomUtilisateur().
									getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
						}

						infosParcoursChaine.add(variableInfosParcours);	
					}
				}
				///////////////////////////////////////////////////////////////////
				//				Font fnt1;
				//				FontMetrics fm1;
				//				Applet app=new Applet();
				//				fnt1 = new Font("Arial", Font.PLAIN, 9);
				//				fm1 = app.getFontMetrics(fnt1);

				int t1=event.gc.stringExtent(absoluteTimeText).x;
				int t2=event.gc.stringExtent(deltaTimeText).x;
				int t3=event.gc.stringExtent(distanceText).x;
				int t4=infosParcoursChaine.size()<1 ? 0 : event.gc.stringExtent(infosParcoursChaine.get(0)).x;
				int t5=infosParcoursChaine.size()<2 ? 0 : event.gc.stringExtent(infosParcoursChaine.get(1)).x;
				int t6=infosParcoursChaine.size()<3 ? 0 : event.gc.stringExtent(infosParcoursChaine.get(2)).x;

				int maxlongueurCol1=Math.max(t1, t2);
				maxlongueurCol1=Math.max(maxlongueurCol1, t3);
				int decalage=10;

				int maxlongueurCol2=Math.max(t4, t5);
				maxlongueurCol2=Math.max(maxlongueurCol2, t6);

				int maxLongueurTotale=maxlongueurCol1+maxlongueurCol2+decalage;

				if (maxLongueurTotale+textXDrawPosition > width-MARGE_LATERALE-20) {//si on déborde dans la marge
					textXDrawPosition=width-MARGE_LATERALE-maxLongueurTotale-20;
					if (textXDrawPosition<MARGE_LATERALE) {
						textXDrawPosition=MARGE_LATERALE;
					}
				}

				//dessin
				String strTime = absoluteTimeText;
				
				if(msgCourant.isMessageIncertitude()){
					if (strTime.contains("01/01/1970 00:00:00")) {
						strTime = strTime.replaceAll("01/01/1970 00:00:00", "##/##/#### ##:##:##");
					} else {
						strTime = strTime.replaceAll("[0-9]{2}/[0-9]{2}/[0-9]{4} [0-9]{2}", "##/##/#### ##");
					}
				}
				
				event.gc.drawString(strTime, textXDrawPosition, 0);
				event.gc.drawString(deltaTimeText, textXDrawPosition, 15);
				event.gc.drawString(distanceText, textXDrawPosition, 30);
				for (int i = 0; i < infosParcoursChaine.size(); i++) {
					event.gc.drawString(infosParcoursChaine.get(i), textXDrawPosition+maxlongueurCol1 +decalage, i*15);
					if (i==2) {
						break;//pas plus de 3 infos
					}
				}				
			}else{
				applyfilter=true;
				try{
				}catch (Exception e) {
					// : handle exception
				}
			}
		}
	}

	public void cursorPositionChanged(CursorPositionEvent event) {
		if(event instanceof ValuedCursorPositionEvent || event==null)
			return;

		String strTime = null;
		String strCorrectedTime = null;
		String strRelTime = null;
		String uniteDist=null;
		String strCorrectedDistance = null;

		AVariableComposant tempCorigee = event.getCurrentMessage().getVariable(TypeRepere.tempsCorrigee.getCode());
		AVariableComposant tempRel = event.getCurrentMessage().getVariable(TypeRepere.tempsRelatif.getCode());
		AVariableComposant distRel = event.getCurrentMessage().getVariable(TypeRepere.distanceRelatif.getCode());
		AVariableComposant distanceCorigee = event.getCurrentMessage().getVariable(TypeRepere.distanceCorrigee.getCode());

		if (applyfilter) {
			this.xCursorPosition=Curseur.getInstance().getpositionCurseur();
			applyfilter=false;
		}else{
			this.xCursorPosition = event.getxPos();
		}
		Curseur.getInstance().setpositionCurseur(xCursorPosition);

		this.time = event.getCurrentMessage().getAbsoluteTime();
		//		this.distance = event.currentMessage.getAbsoluteDistance();
		//		this.deltaTime = this.time - event.firstMessage.getAbsoluteTime();

		if(TableSegments.getInstance().isAppliedTempCorrections()) {
			if(tempCorigee!=null)
				strCorrectedTime =  new String((byte[])tempCorigee.getValeur());//tagValCor
			if(strCorrectedTime==null)
				strCorrectedTime =  ConversionTemps.getFormattedDate(time, true);;
		} 
		
		strTime = ConversionTemps.getFormattedDate(time, true);
		
		absoluteTimeText = "T = " + strTime;
		if(strCorrectedTime != null) {
			absoluteTimeText += " / CorT = " + strCorrectedTime;
		}

		if (tempRel != null) {
			strRelTime =  new String((byte[])tempRel.getValeur());//tagValCor
		} 
		else {
			strRelTime = "###";
		}
		deltaTimeText = DELTA + "T = " + strRelTime;

		try {
			uniteDist = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getUnite();		
		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 
		if (uniteDist==null)			
			uniteDist="";

		String signe="";
		String strRelDistance;
		if (distRel != null) {
			//We know that the value of the corrected time is set as string 
			strRelDistance = new String((byte[])distRel.getValeur());//tagValCor
		} else {
			strRelDistance = "###";
		}

		try {
			if (Double.parseDouble(strRelDistance)>0) {
				signe="+";
			}
		} catch (Exception e) {
			//			System.out.println(e.getMessage());
		}

		strRelDistance=strRelDistance.replace("+","");
		distanceText = ("D = " +signe+ strRelDistance +" "+uniteDist);

		SegmentDistance seg=TableSegments.getInstance().getContainingDistanceSegment(event.getCurrentMessage().getMessageId());
		if (seg.getInitialDiameter()!=seg.getDiameterCorrige()&&TableSegments.getInstance().isAppliedDistanceCorrections()) {	
			strCorrectedDistance=calculDistanceRelativeCorrigee(event.getCurrentMessage());
		} 

		if(strCorrectedDistance != null) {
			distanceText += " / CorD =" + strCorrectedDistance+" "+uniteDist;
		}

		redraw();
	}

	public String calculDistanceRelativeCorrigee(Message msg){
		String distStr="###";
		try {
			if(msg.getVariable(TypeRepere.distanceRelatif.getCode())!=null){
				String distanceRelativeString = new String((byte[])msg.getVariable(TypeRepere.distanceRelatif.getCode()).getValeur());//tagValCor
				String signe =null;
				float valeur =0;
				if(distanceRelativeString!=null){
					signe = distanceRelativeString.substring(0, 1);
					//valeur = Float.valueOf(distanceRelativeString.substring(1, distanceRelativeString.length()));
					valeur = Float.valueOf(distanceRelativeString);

					SegmentDistance segment = TableSegments.getInstance().getContainingDistanceSegment(msg.getMessageId());
					double valModif = segment.getDiameterCorrige();
					double factor = valModif / segment.getInitialDiameter();

					double distCorr = Math.abs(Float.valueOf(distanceRelativeString) * factor);
					DecimalFormat fmt = new DecimalFormat("0.000");
					String distanceRelativeStringCorrigee = signe  +(fmt.format((double) distCorr)).replace(",",
							".");

					distStr = distanceRelativeStringCorrigee;
					//distCorrection.setValeur(distanceRelativeStringCorrigee.getBytes());
					//distCorrection.setTypeValeur(Type.string);
				}
			}
		} catch (Exception e) {
			// : handle exception
		}
		return distStr;
	}

	@Override
	public void dispose() {
		GestionnaireGraphesNotifications.removeGrapheCursorListener(this);
		GestionnaireGraphesNotifications.removeGrapheInfosListener(this);
		super.dispose();
	}

	private void drawMarkers(GC gc) {
		if(ActivatorVueGraphique.getDefault().getConfigurationMng().isMarqueurs()) {
			gc.setLineWidth(1);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
			int nbAnnotations=InfosBullesMarqueurs.getInstance().getListBullesDim().size();
			for (int i = 0; i < nbAnnotations; i++) {
				int pixel=Integer.parseInt(InfosBullesMarqueurs.getInstance().getListBullesDim().get(i).toString());
				gc.drawLine(pixel + MARGE_LATERALE, 0, pixel + MARGE_LATERALE, getBounds().height);
			}
		}
	}

	private void drawMessageRupture(GC gc){
		if(RuptureLegendeMessage.getInstance().isDisplayTimeRuptureMsg()||RuptureLegendeMessage.getInstance().isDisplayDistanceRuptureMsg()){			
			String text=Messages.getString("InfoComposite.1");
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_RED));
			gc.setFont(GestionnaireVueGraphique.getArial09TextFont());
			int longueurString=getTotalLongueurString(text,gc);
			if (longueurString<MARGE_LATERALE) {
				gc.drawText(text, 0, 0);
			}else{
				drawTextWithCuts(text,longueurString,gc);
			}  		
		}
	}

	private void drawTextWithCuts(String text,int longueurString,GC gc){
		int ecartWord=15;
		int hauteur=15;
		int margeSecuriteLong=10;
		int nbLinesMax=getBounds().height/hauteur;

		int longueurTotale=nbLinesMax*MARGE_LATERALE;
		if (longueurString>longueurTotale+margeSecuriteLong){
			return;
		}else{
			String tabText[]=new String[0];
			tabText=text.split(" ");
			int nbWords=tabText.length;
			int nbXPos=0;
			int indiceLine=0;
			for (int i = 0; i < nbWords; i++) {
				if ((getTotalLongueurString(tabText[i],gc)+nbXPos)+margeSecuriteLong<MARGE_LATERALE){
					gc.drawText(tabText[i], nbXPos, indiceLine);
					nbXPos=nbXPos+getTotalLongueurString(tabText[i],gc)+ecartWord;
				}else{
					if ((getTotalLongueurString(tabText[i],gc))<MARGE_LATERALE){
						indiceLine=indiceLine+hauteur;
						nbXPos=0;
						gc.drawText(tabText[i], nbXPos, indiceLine);
						nbXPos=getTotalLongueurString(tabText[i],gc)+ecartWord;
					}else{					
						int nbCuts=getTotalLongueurString(tabText[i],gc)/MARGE_LATERALE +1;
						int nbLettersByCut=MARGE_LATERALE/8;
						for (int j = 0; j < nbCuts; j++) {
							String partString=tabText[i].substring(j*nbLettersByCut, (j+1)*nbLettersByCut);
							gc.drawText(partString, 0, indiceLine);
							indiceLine=indiceLine+hauteur;
						}
					}
				}
			}
		}
	}

	private int getTotalLongueurString(String text,GC gc){
		int nbLetters=text.length();
		int longueur=0;
		for (int i = 0; i < nbLetters; i++) {
			longueur+=gc.getCharWidth(text.charAt(i));
		}
		return longueur;
	}

	private void drawRef0(GC gc) {
		if(PositionReferenceZero.getInstance().getPosition()!=-1) {
			gc.setLineWidth(2);
			Color couleurOr=new Color(getDisplay(), 253, 240, 0);
			gc.setForeground(couleurOr);
			//			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			int position=PositionReferenceZero.getInstance().getPosition()+MARGE_LATERALE;
			gc.drawLine(position, 0, position, getBounds().height);
			gc.drawString("Ref", position + 5, getBounds().height - 15);
			InfosBullesRefZero.getInstance().setAbscissePointRef(position);
		}
	}

	public static int[] getIDsMessageStartStopCurrentZoom(){
		Message msgStart;
		Message msgEnd;
		ZoomComposite currentZoom = (ZoomComposite)GestionnaireZoom.getZoomCourant();

		AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
		Enregistrement enr = data.getEnregistrement();
		List<Message> messages = new ArrayList<Message>();

		messages.addAll((Collection<Message>)enr.getMessages());

		if(currentZoom == null || ((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0) == null) {
			msgStart = messages.get(0);
			msgEnd = messages.get(messages.size() - 1);
		} else {
			ZoomX currentZoomX = (ZoomX)((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0);
			msgStart = enr.getGoodMessage(currentZoomX.getFirstVisibleMsgId());
			msgEnd = enr.getGoodMessage(currentZoomX.getLastVisibleMsgId());
		}

		int msgFin=msgEnd.getMessageId();
		int msgDebut=msgStart.getMessageId();

		int tab[]=new int[2];
		tab[0]=msgDebut;
		tab[1]=msgFin;
		return tab;
	}

	public static boolean visibleEventRecord(){		
		int tab[]=new int[2];
		tab=getIDsMessageStartStopCurrentZoom();

		int msgDebut=tab[0];
		int msgFin=tab[1];

		try {
			CursorPositionEvent ev=Curseur.getInstance().getEv();
			if (ev.getCurrentMessage().getMessageId()>=msgDebut && ev.getCurrentMessage().getMessageId()<=msgFin)
				return true;
			else
				return false;
		} catch (Exception e) {
			return false;
		}
	}

	public void onInfosChanged(Integer ref0XPos, List<Integer> markersXPos) {
		if (Curseur.getInstance().getEv()!=null) {
			this.xCursorPosition=Curseur.getInstance().getEv().getxPos();
		}else{
			this.xCursorPosition=-1;
			Curseur.getInstance().setpositionCurseur(-1);
		}

		//		this.ref0XPos = ref0XPos;
		this.listMarkerPixels.clear();
		this.listMarkerPixels.addAll(markersXPos);
		redraw();
	}

	public void zoomChanged() {
		if (InfoComposite.visibleEventRecord()) 
			Curseur.getInstance().setCurseurVisible(true);
		else
			Curseur.getInstance().setCurseurVisible(false);       
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
