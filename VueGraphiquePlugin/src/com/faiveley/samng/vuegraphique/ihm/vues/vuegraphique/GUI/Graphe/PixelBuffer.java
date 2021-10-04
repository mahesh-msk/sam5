package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_BAS;
import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_HAUT;
import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.PointImagine;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.VirtualPoint;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Calcul;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeSegmentInfo;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;

public class PixelBuffer {

    	public static final long ORIGINE_X = MARGE_LATERALE;
    
	public PixelBuffer() {

	}

	public void creerPixelsBuffer(GrapheGUI mygraphe){
		CourbeMessageValue variableValueInfo;		
		Double variableValue;
		Long ordonnee;
		Long abscisse;
		Long ordonneeDernierPoint;
		Long abscisseDernierPoint;
		long pixelCourantX;
		long segmentStartPixelX;
		boolean pixeltotrace;
		double ecart;
		List<CourbeMessageValue>[] listValeurVariable;
		CourbeMessageValue[] listValeurVariableSimples;
		Message message;
		int idMessage;
		double xValue;
		//long segmentOrigineX = MARGE_LATERALE;
		long origineY = mygraphe.getHeight() - MARGE_BAS;


//		récupération uniquement des bons messages
		List<Message> messages = ActivatorData.getInstance().getVueData()
		.getDataTable().getEnregistrement().getMessages();
		//récupération de tous les messages
		//List<Message> messages = data.getEnregistrement(0).getMessages();

		int nbEvent = messages.size();
		AxeX currentAxe = GestionnaireAxes.getInstance().initialiserAxe(mygraphe.getWidth());
		FabriqueGraphe.initialiserGraphe(mygraphe.getWidth(), mygraphe.getHeight(), mygraphe.getGrapheCourante());
		int currentSegmentNr = 0;
		AxeSegmentInfo currentSegmentInfo;
		double xResolution = currentAxe.getResolution(); //x resolution
		double tolerance = xResolution - 1; //display tolerance
		double xOrigineValue = 0;
		try {
			xOrigineValue=currentAxe.getInfoSegments().get(0).getMinValue(); //first X value of the courbe          
		} catch (Exception e) {

		}
		double tamponCumulDistance=0;
		double cumulDistance=0;
		int prevMsgId;	
		int courbeNr = 0;


		double minVar;	//A
		double resoVerticale;	//A
		List<Courbe> curves = mygraphe.getGrapheCourante().getListeCourbe();//D
		int curvesCount = curves.size();//D
		int courbesVSpacing = (mygraphe.getHeight() - (MARGE_BAS + MARGE_HAUT)) / (2*curvesCount);//D

//		for (Courbe courbeCourante : grapheCourante.getListeCourbe()) {
		int nbCourbes=mygraphe.getGrapheCourante().getListeCourbe().size();
		try {
			for (int k=0; k < nbCourbes; k++) {

				int indice=0;
				if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
					indice=k;
				} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
					indice=nbCourbes-k-1;
				}

				Courbe courbeCourante=mygraphe.getGrapheCourante().getListeCourbe().get(indice);

				if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
					if (courbeCourante.getVariable().getDescriptor().getM_AIdentificateurComposant().getNom().equals("vitesse_corrigee")
							&& !TableSegments.getInstance().isAppliedDistanceCorrections()){

						continue;	
					}
				}

				Set<AVariableComposant> fpVariables = null;
				fpVariables = GestionnairePool.getInstance().getVariablesRenseignees();
				for (AVariableComposant var : fpVariables) {
					if (courbeCourante.getVariable().getDescriptor().getM_AIdentificateurComposant().getNom().toString()
							.equals(var.getDescriptor().getM_AIdentificateurComposant().getNom().toString())) {

						break;
					}
				}

				resoVerticale = courbeCourante.getResoVerticale();//A
				courbeNr++;
				CourbePixelsInfo courbePixelsInfo = new CourbePixelsInfo();
				mygraphe.getCourbesPixelsInfo().add(courbePixelsInfo);
				courbePixelsInfo.setCourbe(courbeCourante);
				
				LinkedList<VirtualPoint> listRPValues = courbePixelsInfo.getVirtualPointLinkedList();
				
				listValeurVariable = courbeCourante.getValeurs();
				listValeurVariableSimples = courbeCourante.getValeursSimples();
				variableValue = 0.0;
				variableValueInfo = null;
				ordonnee = null; //ordonnee to draw
				abscisse = null; //abscisse to draw	
				ordonneeDernierPoint = null; //ordonnee of last point drawn
				abscisseDernierPoint = null; //abscisse of last point drawn
				pixelCourantX = 0; //number of pixel beeing display
				long evaluPix=0;
				segmentStartPixelX = 0;


				minVar = courbeCourante.getMinValeur(); //minimum value of the courbe	//A
				pixeltotrace = false; //pixel to trace
				//ecart = xOrigineValue;
				prevMsgId = -1;
				boolean lastPixCurrentZoomFind=false;	//A

				double offset=0;
				
				for (int i = 0; i < nbEvent; i++) { //pour chaque EV

					message = messages.get(i);
					idMessage = message.getMessageId(); 		//Id of message		
					if (courbeNr==1) {
						if (i==0) {
							offset=message.getAbsoluteDistance();
						}
						if(TableRuptures.getInstance().getRuptureDistance(idMessage)!=null) {
							offset=message.getAbsoluteDistance();
							tamponCumulDistance=cumulDistance;
						}
						cumulDistance=tamponCumulDistance+message.getAbsoluteDistance()-offset;
						message.setCumulDistance(cumulDistance);
					}

					//In the case of a zoom ignore the messages before axe X start message
					if(idMessage < currentAxe.getIdMsgDebut()) {		//	1-> message avant cadre		
						if(listValeurVariable != null && listValeurVariable.length > 0) {
							for (CourbeMessageValue cmv : listValeurVariable[i]) {
								variableValueInfo = cmv;
								if(variableValueInfo != null) {
									if ( variableValueInfo.getValue() != null && !variableValueInfo.isPropagated()) {
										courbePixelsInfo.clearPreviousPix();
										double currentVz=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
										double allVz=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
										double maxz=mygraphe.getWidth()-2*MARGE_LATERALE;
										long newPixz=(long)(maxz*currentVz/allVz);
										
										variableValue = variableValueInfo.getValue().doubleValue();
										ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale);

										PointImagine previousPix=new PointImagine();
										previousPix.setAbscissePixel(newPixz);
										previousPix.setValue(variableValueInfo.getValue());
										previousPix.setOrdonnee(ordonnee);
										courbePixelsInfo.setPreviousPix(previousPix);
										// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
										listRPValues.add(new VirtualPoint(previousPix).setValorised(!variableValueInfo.isPropagated()));
									}
								}
							}
						}
						else if (listValeurVariableSimples != null) {
							variableValueInfo = listValeurVariableSimples[i];
							if(variableValueInfo != null) {
								if (variableValueInfo.getValue() != null && !variableValueInfo.isPropagated()) {
									courbePixelsInfo.clearPreviousPix();
									double currentVz=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
									double allVz=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
									double maxz=mygraphe.getWidth()-2*MARGE_LATERALE;
									long newPixz=(long)(maxz*currentVz/allVz);
									variableValue = variableValueInfo.getValue().doubleValue();
									PointImagine previousPix=new PointImagine();
									ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale);
									previousPix.setAbscissePixel(newPixz);
									previousPix.setValue(variableValueInfo.getValue());
									previousPix.setOrdonnee(ordonnee);
									courbePixelsInfo.setPreviousPix(previousPix);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(previousPix).setValorised(!variableValueInfo.isPropagated()));
								}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
								    	courbePixelsInfo.clearPreviousPix();
									double currentVz=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
									double allVz=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
									double maxz=mygraphe.getWidth()-2*MARGE_LATERALE;
									long newPixz=(long)(maxz*currentVz/allVz);
									PointImagine previousPix=new PointImagine();
									previousPix.setAbscissePixel(newPixz);
									previousPix.setValue(null);
									previousPix.setOrdonnee(null);
									courbePixelsInfo.setPreviousPix(previousPix);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(previousPix).setValorised(!variableValueInfo.isPropagated()));
								}
							}
						}
							
						prevMsgId = idMessage;
						continue;
					}

					//in the case of a zoom, check if the current message is beyond the last axe X message
					if(idMessage > currentAxe.getIdMsgFin()) {		//	2-> message après cadre		
						if(listValeurVariable != null) {
							for (CourbeMessageValue cmv : listValeurVariable[i]) {
								//get the value for this message
								variableValueInfo = cmv;	//get value
								if (!lastPixCurrentZoomFind) {
									lastPixCurrentZoomFind=true;
		
									if(variableValueInfo != null && variableValueInfo.getValue() != null) {
										variableValue = variableValueInfo.getValue().doubleValue();
										abscisse = new Long(mygraphe.getWidth() - MARGE_LATERALE); //calcul of abscisse
		
										if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
											ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale); //calcul of ordonnee
										} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
											if(variableValue == 0) {
												ordonnee = origineY;
											} else {
												ordonnee = origineY - courbesVSpacing; 
											}
										}
										//add the last point in case of a zoom
										if (ordonneeDernierPoint != null) {
											//	                        if (abscisse != abscisseDernierPoint) {
											//Add the current pixel to the courve pixels info
											courbePixelsInfo.addPixelInfo(abscisse.intValue(), 
													ordonnee.intValue(), variableValueInfo, false);
											// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
											listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee, variableValue).setValorised(!variableValueInfo.isPropagated()));
										}
									}else if(variableValueInfo != null && variableValueInfo.getValue() == null) {
									    // TODO faire qq chose ?
										abscisse = new Long(mygraphe.getWidth() - MARGE_LATERALE); //calcul of abscisse
										//add the last point in case of a zoom
										if (ordonneeDernierPoint != null) {
											//	                        if (abscisse != abscisseDernierPoint) {
											//Add the current pixel to the courve pixels info
											courbePixelsInfo.addPixelInfo(abscisse.intValue(), 
													null, variableValueInfo, false);
										}
										// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
										listRPValues.add(new VirtualPoint(abscisse.intValue(),null).setValorised(!variableValueInfo.isPropagated()));
									}
								}
		
								if(variableValueInfo != null) {
									if (variableValueInfo.getValue() != null && !variableValueInfo.isPropagated()) {
										courbePixelsInfo.clearNextPix();
										double currentVk=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
										double allVk=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
										double maxk=mygraphe.getWidth()-2*MARGE_LATERALE;
										long newPixk=(long)(maxk*currentVk/allVk);
										variableValue = variableValueInfo.getValue().doubleValue();
										ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale);
										PointImagine nextPix=new PointImagine();
										nextPix.setAbscissePixel(newPixk);
										nextPix.setValue(variableValueInfo.getValue());
										nextPix.setOrdonnee(ordonnee);
										courbePixelsInfo.setNextPix(nextPix);
										// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
										listRPValues.add(new VirtualPoint(nextPix).setValorised(!variableValueInfo.isPropagated()));
//										break;
									}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
									    	courbePixelsInfo.clearNextPix();
										double currentVk=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
										double allVk=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
										double maxk=mygraphe.getWidth()-2*MARGE_LATERALE;
										long newPixk=(long)(maxk*currentVk/allVk);
										PointImagine nextPix=new PointImagine();
										nextPix.setAbscissePixel(newPixk);
										nextPix.setValue(null);
										nextPix.setOrdonnee(null);
										courbePixelsInfo.setNextPix(nextPix);
										// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
										listRPValues.add(new VirtualPoint(nextPix).setValorised(!variableValueInfo.isPropagated()));
//										break;
									}else{
									    continue;
									}
								}
							}
						}
						else if (listValeurVariableSimples != null) {
							variableValueInfo = listValeurVariableSimples[i];	//get value
							if (!lastPixCurrentZoomFind) {
								lastPixCurrentZoomFind=true;

								if(variableValueInfo != null && variableValueInfo.getValue() != null) {
									variableValue = variableValueInfo.getValue().doubleValue();
									abscisse = new Long(mygraphe.getWidth() - MARGE_LATERALE); //calcul of abscisse

									if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
										ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale); //calcul of ordonnee
									} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
										if(variableValue == 0) {
											ordonnee = origineY;
										} else {
											ordonnee = origineY - courbesVSpacing; 
										}
									}
									//add the last point in case of a zoom
									if (ordonneeDernierPoint != null) {
										//	                        if (abscisse != abscisseDernierPoint) {
										//Add the current pixel to the courve pixels info
										courbePixelsInfo.addPixelInfo(abscisse.intValue(), 
												ordonnee.intValue(), variableValueInfo, false);
									}
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee,variableValue));
								}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
									abscisse = new Long(mygraphe.getWidth() - MARGE_LATERALE); //calcul of abscisse									
									//add the last point in case of a zoom
									if (ordonneeDernierPoint != null) {
										//Add the current pixel to the courve pixels info
										courbePixelsInfo.addPixelInfo(abscisse.intValue(), 
												null, variableValueInfo, false);
									}// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(abscisse.intValue(),null).setValorised(!variableValueInfo.isPropagated()));
								}
							}

							if(variableValueInfo != null) {
								if (variableValueInfo.getValue() != null && !variableValueInfo.isPropagated()) {
									courbePixelsInfo.clearNextPix();
									double currentVk=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
									double allVk=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
									double maxk=mygraphe.getWidth()-2*MARGE_LATERALE;
									long newPixk=(long)(maxk*currentVk/allVk);
									variableValue = variableValueInfo.getValue().doubleValue();
									ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale);
									PointImagine nextPix=new PointImagine();
									nextPix.setAbscissePixel(newPixk);
									nextPix.setValue(variableValueInfo.getValue());
									nextPix.setOrdonnee(ordonnee);
									courbePixelsInfo.setNextPix(nextPix);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(nextPix).setValorised(!variableValueInfo.isPropagated()));
//									break;
								}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
								    	courbePixelsInfo.clearNextPix();
									double currentVk=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
									double allVk=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
									double maxk=mygraphe.getWidth()-2*MARGE_LATERALE;
									long newPixk=(long)(maxk*currentVk/allVk);
									PointImagine nextPix=new PointImagine();
									nextPix.setAbscissePixel(newPixk);
									nextPix.setValue(null);
									nextPix.setOrdonnee(null);
									courbePixelsInfo.setNextPix(nextPix);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(nextPix).setValorised(!variableValueInfo.isPropagated()));
//									break;
								}else{
								    continue;
								}
							}
						}
					}

					
					// 3-> dans le cas d'un zoom
					
					//If we have a zoom then we should check the value from the previous message (if exists)
					//and set the origines (x and y) to point XOrigine respectively to that value
					if(prevMsgId != -1 && i > 0) {
						if(listValeurVariable != null) {
							for (CourbeMessageValue cmv : listValeurVariable[i]) {
								variableValueInfo = cmv;//.get(prevMsgId);	//get value
								if(variableValueInfo != null && variableValueInfo.getValue() != null) {
									variableValue = variableValueInfo.getValue().doubleValue();
		
									if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
										ordonneeDernierPoint  = origineY - (long)((variableValue - minVar) / resoVerticale);	//reset
									} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
										if(variableValue == 0) {
											ordonneeDernierPoint = origineY;
										} else {
											ordonneeDernierPoint = origineY - courbesVSpacing; 
										}
									}
									abscisseDernierPoint = ORIGINE_X;
									//variableValueInfo.setHorsCadre(true);
									//Add the current pixel to the courve pixels info
									courbePixelsInfo.addPixelInfo(abscisseDernierPoint.intValue(), 
											ordonneeDernierPoint.intValue(), variableValueInfo, false);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(abscisseDernierPoint.intValue(),ordonneeDernierPoint, variableValue).setValorised(!variableValueInfo.isPropagated()));
								}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
								    abscisseDernierPoint = ORIGINE_X;
								    courbePixelsInfo.addPixelInfo(abscisseDernierPoint.intValue(), 
										null, variableValueInfo, false);
								    ordonneeDernierPoint = null;
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
								    listRPValues.add(new VirtualPoint(abscisseDernierPoint.intValue(),ordonneeDernierPoint).setValorised(!variableValueInfo.isPropagated()));
								}
								
								prevMsgId = -1;
							}
						}
						else if (listValeurVariableSimples != null) {
							variableValueInfo = listValeurVariableSimples[i];//.get(prevMsgId);	//get value
							if(variableValueInfo != null && variableValueInfo.getValue() != null) {
								variableValue = variableValueInfo.getValue().doubleValue();

								if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
									ordonneeDernierPoint  = origineY - (long)((variableValue - minVar) / resoVerticale);	//reset
								} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
									if(variableValue == 0) {
										ordonneeDernierPoint = origineY;
									} else {
										ordonneeDernierPoint = origineY - courbesVSpacing; 
									}
								}
								abscisseDernierPoint = ORIGINE_X;
								//variableValueInfo.setHorsCadre(true);
								//Add the current pixel to the courve pixels info
								courbePixelsInfo.addPixelInfo(abscisseDernierPoint.intValue(), 
										ordonneeDernierPoint.intValue(), variableValueInfo, false);
								// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
								listRPValues.add(new VirtualPoint(abscisseDernierPoint.intValue(),ordonneeDernierPoint, variableValue));
							}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
							    abscisseDernierPoint = ORIGINE_X;
							    courbePixelsInfo.addPixelInfo(abscisseDernierPoint.intValue(), 
									null, variableValueInfo, false);
							    ordonneeDernierPoint= null;
								// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
							    listRPValues.add(new VirtualPoint(abscisseDernierPoint.intValue(),ordonneeDernierPoint).setValorised(!variableValueInfo.isPropagated()));
							}							    
							prevMsgId = -1;
						}
					}

					// 4-> cas normal
					
					currentSegmentInfo = currentAxe.getSegmentInfoByMessageId(idMessage);

					if (currentSegmentInfo!=null) {						
						//if a segment change then reset
						if(currentSegmentInfo.getSegmentNr() != currentSegmentNr) {
							currentSegmentNr = currentSegmentInfo.getSegmentNr();

							if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
								if(listValeurVariable != null) {
									for (CourbeMessageValue cmv : listValeurVariable[i]) {
										variableValueInfo = cmv;//.get(idMessage);	//get value
										//variableValueInfo.setHorsCadre(false);
										if(variableValueInfo != null) {
											ordonneeDernierPoint  = origineY - (long)((variableValueInfo.getValue() - minVar) / resoVerticale);	//reset
										}
										else {
											ordonneeDernierPoint = null;
										}
									}
								}
								else if (listValeurVariableSimples != null) {
									variableValueInfo = listValeurVariableSimples[i];//.get(idMessage);	//get value
									//variableValueInfo.setHorsCadre(false);
									if(variableValueInfo != null && variableValueInfo.getValue() != null) {
										ordonneeDernierPoint  = origineY - (long)((variableValueInfo.getValue() - minVar) / resoVerticale);	//reset
									}
									else {
										ordonneeDernierPoint = null;
									}
								}
							} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
								ordonneeDernierPoint = origineY;
							}									

							abscisseDernierPoint = (ORIGINE_X + pixelCourantX);
							xOrigineValue = currentSegmentInfo.getMinValue();
							segmentStartPixelX = pixelCourantX;
						}
					}

					ecart = xOrigineValue + (long)((xResolution * (pixelCourantX - segmentStartPixelX)));

					if (RedrawCourbesForAxeChange.getInstance().isRedraw())
						xValue=GestionnaireAxes.getInstance().changeTypeAxe(message);
					else
						xValue = GestionnaireAxes.getAxeXValue(message); 			//X value

					// 4.1 -> pas de changement de pixel
					// if time >= wanted time
					if (xValue >= ecart && !Calcul.ecartCorrect(xValue, ecart, tolerance)) {
						evaluPix = segmentStartPixelX + (long)((xValue - xOrigineValue) / xResolution);
						double currentV0=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
						double allV0=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();

//						double currentV=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
//						double allV=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), currentAxe.getIdMsgFin());

						double max0=mygraphe.getWidth()-2*MARGE_LATERALE;
						long newPix=(long)(max0*currentV0/allV0);


						if (newPix<max0 && newPix>=0)
							evaluPix=newPix;

						pixelCourantX = evaluPix>pixelCourantX ? evaluPix : pixelCourantX;
						ecart = xValue;
					}

					//we check only once for markers
					if(courbeNr == 1) { 
						mygraphe.computeInformationPixels(message, (int)pixelCourantX);
					}
					//.get(idMessage);	//get value
					//variableValueInfo.setHorsCadre(false);
					
					if(listValeurVariable != null) {
						for (CourbeMessageValue cmv : listValeurVariable[i]) {
							variableValueInfo = cmv;
		//					4.2 -> changement de pixel
							if(xValue >= ecart) {
								//we add the pixel info
								if (variableValueInfo != null && variableValueInfo.getValue() != null) {
									variableValue = variableValueInfo.getValue().doubleValue();
									pixeltotrace = true; //allowed the drawing of pixel 
								} else if(variableValueInfo != null && variableValueInfo.getValue() == null){
								    variableValue = null;
								    pixeltotrace = false;
								} 
								
								//	if the drawing of pixel is allowed
								if (pixeltotrace) {
									abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse
		
									if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
										ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale); //calcul of ordonnee
									} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
										if(variableValue == 0) {
											ordonnee = origineY;
										} else {
											ordonnee = origineY - courbesVSpacing; 
										}
									}
									
									courbePixelsInfo.addPixelInfo(abscisse.intValue(), ordonnee.intValue(),variableValueInfo, true);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee, variableValue).setValorised(!variableValueInfo.isPropagated()));
									
									abscisseDernierPoint = abscisse; //record the abscisse of drawn point
									ordonneeDernierPoint = ordonnee; //record the ordonnee of drawn point
									pixeltotrace = false;
								}
								double currentV=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
								double allV=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
								double max=mygraphe.getWidth()-2*MARGE_LATERALE;
								if (pixelCourantX/max<currentV/allV && pixelCourantX<max) {
									pixelCourantX++; //next pixel
								}
								
							} else {
								if (variableValueInfo != null && variableValueInfo.getValue() != null) {
									variableValue = variableValueInfo.getValue().doubleValue();
									abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse
		
									if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
										ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale); //calcul of ordonnee
									} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
										if(variableValue == 0) {
											ordonnee = origineY;
										} else {
											ordonnee = origineY - courbesVSpacing; 
										}
									}			
									courbePixelsInfo.addPixelInfo(abscisse.intValue(), ordonnee.intValue(), 
											variableValueInfo, true);
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee, variableValue).setValorised(!variableValueInfo.isPropagated()));
								}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
									abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse			
									courbePixelsInfo.addPixelInfo(abscisse.intValue(), null, 
											variableValueInfo, true);
									ordonnee = null;
									// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
									listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee).setValorised(!variableValueInfo.isPropagated()));
								}
							}
						}
					}
					else if (listValeurVariableSimples != null) {
						variableValueInfo = listValeurVariableSimples[i];//.get(idMessage);	//get value
						//variableValueInfo.setHorsCadre(false);
						
//						4.2 -> changement de pixel
						if(xValue >= ecart) {
							//we add the pixel info
							if (variableValueInfo != null && variableValueInfo.getValue() != null) {
								variableValue = variableValueInfo.getValue().doubleValue();
								pixeltotrace = true; //allowed the drawing of pixel 
							}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
							    variableValue = null;
							    pixeltotrace = true; 
							} 

							//	if the drawing of pixel is allowed
							if (pixeltotrace && variableValue != null) {
								abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse

								if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
									ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale); //calcul of ordonnee
								} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
									if(variableValue == 0) {
										ordonnee = origineY;
									} else {
										ordonnee = origineY - courbesVSpacing; 
									}
								}	
								
								courbePixelsInfo.addPixelInfo(abscisse.intValue(), ordonnee.intValue(),variableValueInfo, true);
								// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
								listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee, variableValue).setValorised(!variableValueInfo.isPropagated()));
								abscisseDernierPoint = abscisse; //record the abscisse of drawn point
								ordonneeDernierPoint = ordonnee; //record the ordonnee of drawn point
								pixeltotrace = false;
							}else if (pixeltotrace && variableValue == null) {
							    	abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse
							    	courbePixelsInfo.addPixelInfo(abscisse.intValue(), null,variableValueInfo, true);
								abscisseDernierPoint = abscisse; //record the abscisse of drawn point
								ordonneeDernierPoint = null; //record the ordonnee of drawn point
								pixeltotrace = false;
							}
							double currentV=GestionnaireAxes.getInstance().vValues.getCumul(currentAxe.getIdMsgDebut(), idMessage);
							double allV=GestionnaireAxes.getInstance().getCurrentAxeX().getCumul();	                    
							double max=mygraphe.getWidth()-2*MARGE_LATERALE;
							if (pixelCourantX/max<currentV/allV && pixelCourantX<max) {
								pixelCourantX++; //next pixel
							}

						} else {
							if (variableValueInfo != null && variableValueInfo.getValue() != null) {
								variableValue = variableValueInfo.getValue().doubleValue();
								abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse

								if(mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.ANALOGIC) {
									ordonnee = origineY - (long)((variableValue - minVar) / resoVerticale); //calcul of ordonnee
								} else if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {						 
									if(variableValue == 0) {
										ordonnee = origineY;
									} else {
										ordonnee = origineY - courbesVSpacing; 
									}
								}			
								courbePixelsInfo.addPixelInfo(abscisse.intValue(), ordonnee.intValue(), 
										variableValueInfo, true);
								// Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
								listRPValues.add(new VirtualPoint(abscisse.intValue(),ordonnee, variableValue).setValorised(!variableValueInfo.isPropagated()));
							}else if(variableValueInfo != null && variableValueInfo.getValue() == null){
							    abscisse = (ORIGINE_X + pixelCourantX); //calcul of abscisse	
							    courbePixelsInfo.addPixelInfo(abscisse.intValue(), null, variableValueInfo, true);
							    // Pour exploiter GrapheGUI.dessinerPointApointUsingLinkedList()
							    listRPValues.add(new VirtualPoint(abscisse.intValue(),null).setValorised(!variableValueInfo.isPropagated()));
							}
						}
					}
				}
				
				if (mygraphe.getGrapheCourante().getTypeGraphe() == TypeGraphe.DIGITAL) {
					origineY -= (2 * courbesVSpacing);	
				}			
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}