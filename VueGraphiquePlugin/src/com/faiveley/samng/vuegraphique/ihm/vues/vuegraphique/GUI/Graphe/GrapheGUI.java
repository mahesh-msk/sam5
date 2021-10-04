package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_BAS;
import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_HAUT;
import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.vue.SetReferenceAction;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.GestionnaireCouleurs;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.MessageAndIndice;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.principal.sm.segments.ruptures.TypeRupture;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.VueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.PointImagine;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.PositionReferenceZero;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.VirtualPoint;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.calculs.CalculGraphe;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.Curseur;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.ValuedCursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul.InfosBullesMarqueurs;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul.InfosBullesRefZero;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures.EmplacementsRuptures;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures.RuptureLegendeMessage;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.AZoomComposant;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.AnnulerZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.IZoomChangeListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.TypeZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomY;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.composites.InfoComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Graphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeMode;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeXUtil;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;


public class GrapheGUI extends Canvas implements IGrapheCursorListener, IZoomChangeListener{
    
    	private static final int LONGUEUR_GRAD = 3;
    	private static final int ESPACE_GRAD_LABEL = 3;
    	private static final String RUPTURE_ACQUISITION = "RUPTURE_ACQUISITION";
    
	public enum ZOOM_STATE {ZOOM_X, ZOOM_Y}
	private int height;
	private int width;
	private Point zoomPoint;
	private Rectangle zoomRectangle;
	private static int POINT_SIZE = 6;	//the size of the point in POINT_MODE
	private static int ZOOM_MINIMUM_SIZE = 3;
	private boolean dernierGraphe=false;
	private boolean dernierGrapheNonVide;
	private int[][] spacesRupture;
	private Long timeClic;
	public boolean vitCorDansFiltre;
	private Graphe grapheCourante;
	private int xCursorPosition;
	private Integer cursorPositionMsgId;
	private List<CourbePixelsInfo> courbesPixelsInfo = new ArrayList<CourbePixelsInfo>();
	private Image image;		//image buffer for the graphic

	private ZOOM_STATE zoomState;

	private GraphicMouseListener mouseListener = new GraphicMouseListener();
	private GraphicMouseMoveListener mouseMoveListener = new GraphicMouseMoveListener();

	private List<Integer> listMarkerPixels = new ArrayList<Integer>();

	public int nGraphs;
	public int positionG;
	private Integer ref0Pixel;
	boolean verrouMsgBox=true;

	public GrapheGUI(Composite parent, int style, Graphe graphe, boolean lastGraphe, int nGraphes,int positionGraphe) {
		super(parent, style);
		try {
			this.grapheCourante = graphe;
			this.dernierGraphe=lastGraphe;
			this.nGraphs=nGraphes;
			this.positionG=positionGraphe;
			initialiser();
			GestionnaireGraphesNotifications.addGrapheCursorListener(this);  
			GestionnaireZoom.getInstance().addListener(this);  
		} catch (Exception e) {

		}
	}

	private void initialiser() {
		this.setLayout(null);
		this.addListener(SWT.Paint,
				new Listener() {
			//: here save some information about current window dimmensions
			//in order to ignore other events than the ones generated from
			//a resize operation
			public void handleEvent(Event event) {
				try {
					int prevHeight = height;
					int prevWidth = width;
					boolean flagForActivVue=false;
					height = getBounds().height; //get height of the graph
					width = getBounds().width; //get width of the graph
					dernierGrapheNonVide=dernierGraphe;
					GrapheGUI.this.vitCorDansFiltre=false;

					if(prevHeight != height || prevWidth != width || image == null || GrapheGUI.this.vitCorDansFiltre||RedrawCourbesForAxeChange.getInstance().isRedraw()) {
						try {
							//							if (!VueWaitBar.getInstance().isWorking() && GrapheGUI.this.positionG==0) {
							//							VueWaitBar.getInstance().setRect(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell().getBounds());
							//							VueWaitBar.getInstance().start();
							//							}
							//							TAGQQQ 
							//							GrapheGUI.this.grapheCourante.getListeCourbe().get(0).getMaxValeur();
							flagForActivVue=true;
							GrapheGUI.this.verrouMsgBox=true;

							if(prevWidth != width && GrapheGUI.this.positionG==0){
								Curseur.getInstance().setAddCursorAfterRedraw(true);
								if (GestionnaireZoom.getZoomCourant()==null) {
									Curseur.getInstance().setCurseurVisible(true);
								}
							}

							if(image != null) {
								image.dispose();
							}
							image = new Image(getDisplay(), getBounds());
							GC gc = new GC(image);
							if(ActivatorVueGraphique.getDefault().getConfigurationMng().isFond_blanc())
								gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
							else 
								gc.setBackground(event.gc.getBackground());
							gc.fillRectangle(0, 0, width, height);
							drawCadre(gc);
							dessinerRepere(gc);

							if (GrapheGUI.this.positionG==0) {
								PositionReferenceZero.getInstance().setPosition(-1);
							}
							remplirPixelBuffer();

							GestionnaireGraphesNotifications.notifyInfosChanged(ref0Pixel, listMarkerPixels);

							//tracerCourbesLigne(event); //draw courbe
//							long ttStart = System.currentTimeMillis();
							drawPixelsInfos(gc);
							//event.gc.copyArea(image, 0, 0);
//							gc.dispose();
//							long ttEnd = System.currentTimeMillis();
//							System.err.println("Drawing took " + (ttEnd - ttStart));
						} catch (Exception e) {
							e.printStackTrace();
						}
					} 

					event.gc.drawImage(image, 0, 0);
					drawMarkers(event.gc);        		
					drawRef0(event.gc);

					if (Curseur.getInstance().isAddCursorAfterRedraw()&&Curseur.getInstance().getEv()!=null) {
						try {
							if (Curseur.getInstance()!=null && Curseur.getInstance().getpositionCurseur()>=0&&GrapheGUI.this.positionG>1) {
								GrapheGUI.this.xCursorPosition=Curseur.getInstance().getpositionCurseur();
							}else{
								int nbc=courbesPixelsInfo.size();
								VariablePixelInfo vpi=null;
								int i;
								for (i = 0; i < nbc; i++) {
									if (courbesPixelsInfo.get(i).haveOneValue()) {
										vpi=courbesPixelsInfo.get(i).
												getPixelInfoByMessageId(Curseur.getInstance().getEv().getCurrentMessage().getMessageId());                       	
										if (vpi!=null) {
											break;
										}
									} 
								}
								GrapheGUI.this.xCursorPosition=vpi.getAbscisse();
								Curseur.getInstance().setpositionCurseur(vpi.getAbscisse());
								if (Curseur.getInstance().getEv()!=null) {
									Curseur.getInstance().getEv().setxPos(vpi.getAbscisse());
								}
							}
						} catch (Exception e) {
							if (!(GrapheGUI.this.xCursorPosition>0)) {
								GrapheGUI.this.xCursorPosition=-1;
							}else{
								GrapheGUI.this.xCursorPosition=Curseur.getInstance().getpositionCurseur();
							}
						}
					}

					try {
						drawCursor(event.gc);  
					} catch (Exception e) {

					}

					spacesRupture=null;
					drawTimeBreaks(event.gc);//timebreak before distance break
					drawDistanceBreaks(event.gc);
					drawDigitalZeroRefLines(event.gc);

					try {													
						if (dernierGraphe) {               		    		
							Curseur.getInstance().setAddCursorAfterRedraw(false);
							RedrawCourbesForAxeChange.getInstance().setRedraw(false);  

						}               			
					} catch (Exception e) {

					}

					if (flagForActivVue&&dernierGraphe) {
						activeVueGraphique();
						flagForActivVue=false;

					}

				} catch (Exception e) {

				}
			}
		});

		/////////////
		GrapheGUI.this.addMouseTrackListener(new MouseTrackAdapter() {   
			Shell tip = null;
			Label label = null;
			Rectangle rec;			

			public void mouseExit(MouseEvent e) {
				if(getMutexTooltiptex()){
					//					int pix=Integer.parseInt(InfosBullesMarqueurs.getInstance().getListBullesDim().get(InfosBullesMarqueurs.getInstance().getNumeroMarker()).toString());
					this.label.setVisible(false);
					this.tip.setVisible(false);
					setMutexTooltiptex(false);
				}
			}

			public void mouseHover(MouseEvent e) {
				//recuperer le nombre de marqueurs
				int nbmarkers=InfosBullesMarqueurs.getInstance().getListBullesDim().size();

				if(!getMutexTooltiptex()){

					//PointRef
					int abscissePointRef=InfosBullesRefZero.getInstance().getAbscissePointRef();
					if (e.x>abscissePointRef-5 && e.x<abscissePointRef+5) {
						String libelle="Point de R�f�rence="+SetReferenceAction.pointRef;	
						int larg=libelle.length()*6;
						int longu=16;
						setMutexTooltiptex(true);
						Display display = GrapheGUI.this.getDisplay();
						this.tip = new Shell(display, SWT.OFF | SWT.NO_FOCUS | SWT.TOOL);
						this.tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
						FillLayout layout = new FillLayout();
						layout.marginWidth = 2;
						this.tip.setLayout(layout);
						this.label = new Label(this.tip, SWT.NONE);
						this.label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
						this.label.setText(libelle);
						this.tip.setBounds(e.display.getClientArea().x + 15,e.display.getClientArea().y,larg,longu);
						this.tip.setVisible(true);
						InfosBullesRefZero.getInstance().setAffiche(true);
					}else{

						//annotations
						for (int i = 0; i < nbmarkers; i++) {
							int pix=Integer.parseInt(InfosBullesMarqueurs.getInstance().getListBullesDim().get(i).toString());

							if (e.x>MARGE_LATERALE+pix-5 && e.x < MARGE_LATERALE+pix+5) {
								String libelle=InfosBullesMarqueurs.getInstance().getListBullesName().get(i).toString();	
								int larg=libelle.length()*8;
								int longu=16;
								setMutexTooltiptex(true);
								Display display = GrapheGUI.this.getDisplay();
								this.tip = new Shell(display, SWT.OFF | SWT.NO_FOCUS | SWT.TOOL);
								this.tip.setBackground(display.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
								FillLayout layout = new FillLayout();
								layout.marginWidth = 2;
								this.tip.setLayout(layout);														
								this.label = new Label(this.tip, SWT.NONE);
								this.label.setBackground(display.getSystemColor(SWT.COLOR_WHITE));
								this.label.setText(libelle);
								this.tip.setBounds(e.display.getClientArea().x + 15,e.display.getClientArea().y,larg,longu);
								this.tip.setVisible(true);	
								InfosBullesMarqueurs.getInstance().setNumeroMarker(i);
								break;
							}
						}
					}
				}else{  
					try{
						if (InfosBullesRefZero.getInstance().isAffiche()) {
							if (InfosBullesRefZero.getInstance().isAffiche() && 
									e.x<InfosBullesRefZero.getInstance().getAbscissePointRef()-5 ||
									e.x>InfosBullesRefZero.getInstance().getAbscissePointRef()+5) {
								this.label.setVisible(false);
								this.tip.setVisible(false);
								setMutexTooltiptex(false);
							}
						}else{
							int pix=Integer.parseInt(InfosBullesMarqueurs.getInstance().getListBullesDim().get(InfosBullesMarqueurs.getInstance().getNumeroMarker()).toString());
							if (e.x<pix-5 || e.x>pix+5) {
								this.label.setVisible(false);
								this.tip.setVisible(false);
								setMutexTooltiptex(false);
							}
						}
					}catch (Exception eyt) {
						eyt.printStackTrace();
					}   								
				}						
			}		
		});
		/////////////////

		this.addMouseMoveListener(mouseMoveListener);
		this.addMouseListener(mouseListener);

		this.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				System.err.println("Resise ");
			}
		});

		addKeyListener(new KeyAdapter() {

			boolean isLeftpressed=false;
			boolean isCtrlpressed=false;
			boolean isRightpressed=false;

			public void keyReleased(KeyEvent event) {

				switch(event.keyCode) {
				case SWT.ARROW_LEFT : {
					isLeftpressed=false;           		
					break;
				}
				case SWT.ARROW_RIGHT : {
					isRightpressed=false;           		
					break;
				}
				case SWT.CTRL : {
					isCtrlpressed=false;	            	
					break;
				}
				case SWT.HOME : {
					fireCursorPositionChanged(MARGE_LATERALE,false);
					break;
				}
				case SWT.END : {
					fireCursorPositionChanged(width-MARGE_LATERALE,false);
					break;
				}
				}
			}

			@Override	
			public void keyPressed(KeyEvent event) {

				if (event.keyCode == SWT.CTRL) {
					isCtrlpressed=true;

				}else if(event.keyCode == SWT.ARROW_LEFT) {
					isLeftpressed=true;
					if (isCtrlpressed) {
						onArrowKeyPressed(true,false,true);
					}else {
						onArrowKeyPressed(true,false,false);
					}

				} else if (event.keyCode == SWT.ARROW_RIGHT) {
					isRightpressed=true;
					if (isCtrlpressed) {
						onArrowKeyPressed(false, false,true);
					}else{
						onArrowKeyPressed(false, false,false);
					}
				} else if(event.keyCode == SWT.ESC) {
					onEscapePressed();
				} else if (event.keyCode == SWT.CR) {
					VariablePixelInfo closestMsgPixelInfo=
							courbesPixelsInfo.get(0).getPixelInfoByMessageId(Curseur.getInstance().getEv().getCurrentMessage().getMessageId());
					fireCursorPositionChanged(closestMsgPixelInfo, 
							Curseur.getInstance().getMsgId(), 
							true);
				}
			}
		});



	}


	public void remplirPixelBuffer(){
		courbesPixelsInfo.clear();
		listMarkerPixels.clear();
		if (GrapheGUI.this.positionG==0) {
			InfosBullesMarqueurs.getInstance().getListBullesDim().clear();
			InfosBullesMarqueurs.getInstance().getListBullesName().clear();
			EmplacementsRuptures.getInstance().getListDistanceBreaksPixels().clear();
			EmplacementsRuptures.getInstance().getListTimeBreaksPixels().clear();
		}

		ref0Pixel = null;

		PixelBuffer pbuff=new PixelBuffer();
		pbuff.creerPixelsBuffer(GrapheGUI.this);
	}

	public void activeVueGraphique(){
		try {
			//			GrapheGUI.this.forceFocus();

			if (Curseur.getInstance().getEv()!=null) {
				if (Curseur.getInstance().getEv().getxPos()>=MARGE_LATERALE && Curseur.getInstance().getEv().getxPos()<=width-MARGE_LATERALE) {
					fireCursorPositionChanged(Curseur.getInstance().getEv().getxPos(), false);
				}
			}
			GrapheGUI.this.forceFocus();	
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	private boolean getMutexTooltiptex(){

		try{
			ActivatorData.getInstance().getPoolDonneesVues().get("");
			if (ActivatorData.getInstance().getPoolDonneesVues().get("mutex_tooltip_annotation").toString().equals("false"))
				return false;
			else
				return true;
		}catch (Exception e) {
			return false;
		}

	}

	private void setMutexTooltiptex(boolean bool){
		if (bool) 
			ActivatorData.getInstance().getPoolDonneesVues().put("mutex_tooltip_annotation","true");
		else
			ActivatorData.getInstance().getPoolDonneesVues().put("mutex_tooltip_annotation","false");
	}

	private void dessinerRepere(GC gc) {

		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
		gc.setLineWidth(1);
		// vertical right repere
		gc.drawLine(width - MARGE_LATERALE, MARGE_HAUT, width - MARGE_LATERALE, height - MARGE_BAS);
		// vertical left repere
		gc.drawLine(MARGE_LATERALE, 0, MARGE_LATERALE, height);
		// horizontal repere 
		gc.drawLine(MARGE_LATERALE, height - MARGE_BAS, width - MARGE_LATERALE, height - MARGE_BAS);
	}

	private void dessinerGradationsAbscisse(Graphe g, GC gc, int nbGraduations) {
		boolean axeTime=false;
		if (GestionnaireAxes.getInstance().getCurrentAxeType()==TypeAxe.AXE_TEMPS ||
				GestionnaireAxes.getInstance().getCurrentAxeType() == TypeAxe.AXE_TEMPS_CORRIGE) {
			axeTime = true;
		}else{
			axeTime = false;
		}

		AxeX axeX = GestionnaireAxes.getInstance().getCurrentAxeX();
		//		AxeSegmentInfo axeSegmentInfo;		
		int longueurGrad=3;				//long of the trace graduation
		int abscisseGrad=0;				//abscisse of the graduation
		int ecartGrad=0;				//space between 2 graduations
		int espaceGrad_Label=6;			//space between graduation and label
		int centrage=25;				//value to center the date near the graduation
		String label="";				//string to display
		double val,valmin=0,valmax=0;

		gc.setForeground(GestionnaireVueGraphique.getDefaultTextColor());
		gc.setFont(GestionnaireVueGraphique.getArial09TextFont());
		gc.setLineWidth(1);

		gc.fillRectangle( 0, height-MARGE_BAS+1, width - MARGE_LATERALE, MARGE_BAS-longueurGrad );

		ListMessages listeMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
		int id1=axeX.getIdMsgDebut();
		int id2=axeX.getIdMsgFin();

		MessageAndIndice msgAndI1=listeMessages.getMessageAndIndiceByIdExact(id1);
		MessageAndIndice msgAndI2=listeMessages.getMessageAndIndiceByIdExact(id2);

		Message msg1=msgAndI1.getMsg();
		Message msg2=msgAndI2.getMsg();

		int indice1=msgAndI1.getID();
		int indice2=msgAndI2.getID();

		int nbGrad=(nbGraduations==-1) ? CalculGraphe.calculerNbGrad(axeX,width,indice2-indice1) : nbGraduations;	//number of graduations
		if (this.positionG!=0 && LabelsAxeAbscisse.getInstance()!=null && LabelsAxeAbscisse.getInstance().labels!=null && LabelsAxeAbscisse.getInstance().labels.length>0) {
			nbGrad=LabelsAxeAbscisse.getInstance().labels.length;
		}
		if (this.positionG==0) {
			LabelsAxeAbscisse.getInstance().labels = null;
			LabelsAxeAbscisse.getInstance().labels = new Graduation[nbGrad];
		}

		if (nbGrad-1!=0) {		
			try {
				ecartGrad = (width-2*FabriqueGraphe.MARGE_LATERALE)/(nbGrad-1);	//calcul for space between 2 graduations
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		//		convert the time in date 
		//		axeSegmentInfo = axeX.getSegmentInfoByXPosition(abscisseGrad);
		int pota=3;
		double cumul=axeX.getCumul();
		double coefgrad=0;
		double n=cumul;
		for (int q = -9; q < 20; q++) {
			if ((pota*Math.pow(10,(double)q)<n)&&(pota*Math.pow(10,(double)q+1)>n)) {
				coefgrad=Math.pow(10,(double)(q-1));
				break;
			}
		}

		for (int i = 0; i < nbGrad; i++) {
			if (this.positionG==0) {
				LabelsAxeAbscisse.getInstance().labels[i]=new Graduation();
				LabelsAxeAbscisse.getInstance().labels[i].setX1(abscisseGrad+MARGE_LATERALE);
				LabelsAxeAbscisse.getInstance().labels[i].setY1(height-MARGE_BAS-longueurGrad);
				LabelsAxeAbscisse.getInstance().labels[i].setX2(abscisseGrad+MARGE_LATERALE);
				LabelsAxeAbscisse.getInstance().labels[i].setY2(height-MARGE_BAS+longueurGrad);
			}

			if (this.positionG==0) {
				double minDouble=GestionnaireAxes.getAxeXValue(ActivatorData.getInstance().getVueData()
						.getDataTable().getEnregistrement().getGoodMessage(axeX.getIdMsgDebut()));
				//for the first gradation we take the value from the first axe message ID 
				//which might not be the start of a repere
				if(i == 0 ){
					val=abscisseGrad * axeX.getResolution();
					valmin=val;
					if (axeTime) {
						label = AxeXUtil.getConvertedValueForXAxis(true, coefgrad, minDouble,
								msg1.getAbsoluteTime());						
					}else{
						label = AxeXUtil.getConvertedValueForXAxis(true, coefgrad, minDouble,
								msg1.getCumulDistance());
					}
				} else {
					//axe temps
					if (axeTime) {
						val=abscisseGrad+MARGE_LATERALE; 
						Object value=null;

						//						VariablePixelInfo vpi=courbesPixelsInfo.get(0).getClosestMessageId(64, TYPE_CLOSEST_MESSAGE.NEXT);
						VariablePixelInfo vpi=courbesPixelsInfo.get(0).getPixelInfoByAbscisse(Math.round((float)val));
						//						VariablePixelInfo vpi=courbesPixelsInfo.get(0).getClosestMessageId(64, TYPE_CLOSEST_MESSAGE.NEXT);
						//						VariablePixelInfo vpi=courbesPixelsInfo.get(0).getClosestMessageId(64, TYPE_CLOSEST_MESSAGE.NEXT);
						VariablePixelInfo vpi2=courbesPixelsInfo.get(0).getPixelInfoByAbscisse2(Math.round((float)val));
						if (vpi==null && vpi2!=null) {
							vpi=vpi2;
						}
						int msgID=vpi.getFirstMessageId();
						Message msg=ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(msgID);
						if (msg!=null) {
							if (axeTime) {
								value=msg.getAbsoluteTime();
							}else{
								//								value=msg.getCumulDistance();
							}											
							label=AxeXUtil.getConvertedValueForXAxis(false, coefgrad, minDouble,value);
						}
					}else{
						//axe distance
						double d1=msg1.getCumulDistance();
						double d2=msg2.getCumulDistance();
						double d=d1+(d2-d1)/(nbGrad-1)*i;
						label=AxeXUtil.getConvertedValueForXAxis(false, coefgrad, minDouble,d);
					}
				}
				LabelsAxeAbscisse.getInstance().labels[i].setLabel(label);
				centrage = gc.stringExtent(label).x / 2;
				LabelsAxeAbscisse.getInstance().labels[i].setLabelX(abscisseGrad+MARGE_LATERALE-centrage);
				LabelsAxeAbscisse.getInstance().labels[i].setLabelY(height-MARGE_BAS+longueurGrad+espaceGrad_Label);
				abscisseGrad=abscisseGrad+ecartGrad;	//new abscisse	
			}
		}

		if (this.positionG==0 && (!tousLesLabelsDifferentsetCoherents(LabelsAxeAbscisse.getInstance().labels,axeTime)) && nbGrad>1) {
			dessinerGradationsAbscisse(g, gc, nbGrad-1);
		}else{
			tracerLabels(gc,LabelsAxeAbscisse.getInstance().labels);
		}
	}


	private void tracerLabels(GC gc, Graduation [] grads){
        	if (grads == null) {
        	    return;
        	}
		for (Graduation graduation : grads) {
			gc.drawLine(graduation.getX1(),graduation.getY1(),graduation.getX2(),graduation.getY2());
			gc.drawString(graduation.getLabel(), graduation.getLabelX(), graduation.getLabelY());
		}
	}

	private boolean tousLesLabelsDifferentsetCoherents(Graduation labels[], boolean axeTime){
		boolean ok=true;
		int size=labels.length;
		Double lastLabel=null,newLabel=null;
		for (int i = 0; i < size-1; i++) {
			for (int j = i+1; j < size; j++) {
				if (i!=j && labels[i].getLabel().equals(labels[j].getLabel())) {
					return false;
				}
			}
		}
		if (axeTime) {
			return true;
		}
		try {
			for (Graduation graduation : labels) {
				newLabel=Double.valueOf(graduation.getLabel());
				if (lastLabel!=null) {
					if (newLabel<=lastLabel) {
						return false;
					}
				}
				lastLabel=newLabel;
			}
		} catch (NumberFormatException e) {
			// TODO Bloc catch auto-g�n�r�
			e.printStackTrace();
		}
		return ok;
	}

	private void dessinerAxeOrdonnee(Courbe c, GC gc, Graphe g){
		if(g.getTypeGraphe() == TypeGraphe.ANALOGIC) {
			Double min = c.getMinValeur();	//minimum value of the curve
			Double max = c.getMaxValeur();	//maximum value of the curve
			int abscisseAxe=CalculGraphe.choixAbscisseAxe(c.getNum(), MARGE_LATERALE,width);	//calcul the abscisse of the axe

			//choice a color
			Color color = new Color(gc.getDevice(),c.getCouleur().red,c.getCouleur().green,c.getCouleur().blue);
			gc.setForeground(color);
			gc.setLineWidth(1);

			//draw the axe
			gc.drawLine(abscisseAxe, MARGE_HAUT-5, abscisseAxe,height-MARGE_BAS);	

			//draw the labels
			dessinerGradationsOrdonneesV2(min, max, height-MARGE_BAS, 
				abscisseAxe, c.getResoVerticale(), gc, c, g);
			color.dispose();
		} else {
			gc.setForeground(GestionnaireVueGraphique.getDefaultTextColor());
			dessinerGradationsOrdonneesV2(0D, 0D, 0, 0, 0D, gc, c, g);
		}
	}

    private void dessinerGradationsOrdonneesV2(Double min, Double max, int hauteur, int abscisse, Double resolutionVerticale, GC gc, Courbe c, Graphe g) {
	    // Double min : Valeur minimale de la courbe
	    // Double max : Valeur maximale de la courbe
	    // int hauteur : Hauteur en pixel de la zone de dessin de la courbe 
	    // int abscisse : Position en pixel de l'axe de l'ordonn� dans la zone de dessin de la courbe 
	    // Double resolutionVerticale : R�solution verticale d la courbe
	    // GC gc : composant graphique pour le dessin de la courbe
	    // Courbe c : Courbe � dessiner

		//set the font 
		gc.setFont(GestionnaireVueGraphique.getArial09TextFont());
	FontMetrics fm = gc.getFontMetrics();

	/* Get the height of characters */
	int textHeightPixel = fm.getHeight();

	if (grapheCourante.getTypeGraphe() == TypeGraphe.ANALOGIC) {	    
	    int indicePow = 0;
	    double stepValue = 10;
	    double pixelStep = 1;
	    int maxNbStep = 40;
	    double[] allowedMultiple = {1D, 2D, 5D};
	    
	    double maxClostest= ((int) Math.ceil(max)/10)*10;
	    double minClostest= ((int) Math.floor(min)/10)*10;
	    
	    // D�termination du nombre de step
	    int nbStep = (int) ((maxClostest-minClostest)/stepValue);
	    
	    // Cas des valeurs strictement inf�rieures � 10
	    if(nbStep == 0){		
		while((int) ((maxClostest-minClostest)/Math.pow(10, indicePow)) < 10){
		    maxClostest= (Math.ceil(max)/Math.pow(10, indicePow))*Math.pow(10, indicePow);
		    minClostest= (Math.floor(min)/Math.pow(10, indicePow))*Math.pow(10, indicePow);
    		indicePow--;
    	    }
	    }
	    // Cas des valeurs strictement sup�rieures � 100
	    else{		
		while((int) ((maxClostest-minClostest)/Math.pow(10, indicePow)) > 100){
    		indicePow++;    		
    	    }
	    }
    	    
    	    for (int i = 0; i < allowedMultiple.length; i++) {
    		stepValue = allowedMultiple[i]*Math.pow(10, indicePow);
		nbStep = (int) ((maxClostest-minClostest)/stepValue); 
    		if(nbStep <= maxNbStep && nbStep >= maxNbStep/4){
    		   break; 
    		}
    	    }
    	    
    	    nbStep++;
    	    // D�termination du nombre de pixels entre deux graduations
    	    pixelStep = stepValue / resolutionVerticale;

    	    //Logique pour que les graduations restent lisibles    	    
    	    int minHeightPixel = nbStep*textHeightPixel ;
    	    int coefCorrectGradua = 1;
    	    double seuil = ((double) minHeightPixel/ (double) height);
    	    
    	    if(seuil > 1.25){
    		coefCorrectGradua = (int) ( Math.round(seuil * 3) + 1);
    	    }else if(seuil > 0.75){
    		coefCorrectGradua = (int) ( Math.round(seuil * 2) + 1);    		
    	    }
    	    
    	    if(seuil > 0.75){
    		pixelStep = pixelStep * coefCorrectGradua;
    		nbStep = nbStep / coefCorrectGradua;
    	    }

			String strValue;
	    int xPosRef = (LONGUEUR_GRAD + ESPACE_GRAD_LABEL );
			int yPos;
	    // Position du label par rapport � son axe des ordonn�es
	    int coteLabel=(((c.getNum() + 1) %2)-1) * 7 + 1;

	    // Format d'afficahge de la valeur
	    String labelFormat = "###";
	    if(indicePow < 0 && indicePow >= -2){
		labelFormat = "0.###";
	    }else if(indicePow < -2){
		labelFormat = "#E0";
	    }
	    
	    // Offset quand on effectue un zoom vertical
	    double offsetStepPixel = Math.abs(pixelStep / stepValue *(min % stepValue));
	    double offsetStepValue = ((int)(min/stepValue)) * stepValue;
	    
	    // Pour chaque graduation de l'�chelle
	    for(int i=0; i <= nbStep ;i++ ){		
		// D�termination de la valeur � afficher
		strValue =  new DecimalFormat(labelFormat).format(stepValue*(double) i*coefCorrectGradua + offsetStepValue);
		// D�termination de l'emplacement en Y de la valeur � afficher
		yPos = (int) (hauteur - i*pixelStep - offsetStepPixel);			
		// Cas particulier pour l'origine : sa valeur est le minimum et sa position est au 0 du graphique
		if( i == 0 && offsetStepPixel < 0){
		    continue;
		}		
		int xPos = abscisse + (coteLabel > 0 ?  xPosRef : - (xPosRef + gc.stringExtent(strValue).x +2*LONGUEUR_GRAD));	
		// Cas o� on d�passe la zone graphique
		if(yPos < MARGE_HAUT/2){
					break;
		}
		// Dessiner la graduation
		gc.drawLine(abscisse-LONGUEUR_GRAD, yPos, abscisse+LONGUEUR_GRAD, yPos);
		// Dessiner la valeur correspondante
		gc.drawString(strValue, xPos+2*LONGUEUR_GRAD, yPos-8);
			}
		} else {
			List<Courbe> curves = grapheCourante.getListeCourbe();
			int curvesCount = curves.size();
			int courbesVSpacing = (height - (MARGE_BAS + MARGE_HAUT)) / (2*curvesCount);
			int yTextExtent = gc.stringExtent("0").y;
			int i = 0;
			int yDrawPos = height - MARGE_BAS;
			int xDrawPos = width - MARGE_LATERALE;
			int textDrawPos;
			boolean drawStringEnabled = true; 
			int prevDrawPos = -1;
			while(true) {
				if(prevDrawPos != -1 && (prevDrawPos - yDrawPos) < yTextExtent) {
					yDrawPos -= 2*courbesVSpacing;
					i++;
				} else {
		    // gc.drawLine(xDrawPos, yDrawPos, xDrawPos+longueurGrad,
		    // yDrawPos);
					if(drawStringEnabled) {
						textDrawPos = yDrawPos - yTextExtent/2;
			gc.drawString("0", xDrawPos + LONGUEUR_GRAD + 2, textDrawPos);
			gc.drawLine(xDrawPos, yDrawPos, xDrawPos + LONGUEUR_GRAD, yDrawPos);
					}
					prevDrawPos = yDrawPos;
					yDrawPos -= courbesVSpacing;
					drawStringEnabled = !drawStringEnabled;
				}
				i++;
				if(i >= 2*curvesCount)
					break;
			}
		}

		
	    
	}

	
	/**
	 * Determines if for the given current pixel exists marker,
	 * zero reference or ruptures (time or distance).
	 * If one exist the information pixel lists are updated.  
	 *
	 */
	public void computeInformationPixels(Message message, int pixelCourantX) {

		int idMessage = message.getMessageId();
		String[] tabInfoPixDist=new String[4];
		String[] tabInfoPixTemps=new String[4];

		String IDmsgDist;
		String pixCourantDist;
		String libelleDist;

		String IDmsgTemps;
		String pixCourantTemps;
		String libelleTemps;
		HashMap<Integer, TypeRupture> timeBreaks = TableRuptures.getInstance().getListeRupturesTemps();
		HashMap<Integer, TypeRupture> distanceBreaks = TableRuptures.getInstance().getListeRupturesDistance();
		GestionnaireMarqueurs markersMng = ActivatorData.getInstance().getGestionnaireMarqueurs();
		//check for time break, distance break and marker
		if(GrapheGUI.this.positionG==0){	

			if(timeBreaks != null && timeBreaks.get(idMessage) != null) {
				//we have a time break
				IDmsgTemps=String.valueOf(idMessage);
				pixCourantTemps=String.valueOf(pixelCourantX);
				libelleTemps=ConversionTemps.getFormattedDate(message.getAbsoluteTime(), true);
				tabInfoPixTemps[0]=IDmsgTemps;
				tabInfoPixTemps[1]=pixCourantTemps;
				tabInfoPixTemps[2]=libelleTemps;
				if (message != null && message.getEvenement() != null && message.getEvenement().isRuptureAcquisition()) {
					tabInfoPixDist[3] = RUPTURE_ACQUISITION;
				}
				EmplacementsRuptures.getInstance().getListTimeBreaksPixels().add(tabInfoPixTemps);
			}
			if(distanceBreaks != null && distanceBreaks.get(idMessage) != null) {
				//we have a distance break
				IDmsgDist=String.valueOf(idMessage);
				pixCourantDist=String.valueOf(pixelCourantX);
				libelleDist=String.valueOf(message.getAbsoluteDistance());
				tabInfoPixDist[0]=IDmsgDist;
				tabInfoPixDist[1]=pixCourantDist;
				tabInfoPixDist[2]=libelleDist;
				if (message != null && message.getEvenement() != null && message.getEvenement().isRuptureAcquisition()) {
					tabInfoPixDist[3] = RUPTURE_ACQUISITION;
				}
				EmplacementsRuptures.getInstance().getListDistanceBreaksPixels().add(tabInfoPixDist);
			}

			if(markersMng.getMarqueurParId((int)idMessage) != null) {
				//we have a marker			
				listMarkerPixels.add(pixelCourantX);
				InfosBullesMarqueurs.getInstance().getListBullesName().add(markersMng.getMarqueurParId((int)idMessage).getNom());
				InfosBullesMarqueurs.getInstance().getListBullesDim().add(pixelCourantX);
			}
			if(message.isRepereZero()){
				ref0Pixel = (int)pixelCourantX;
				PositionReferenceZero.getInstance().setPosition((int)pixelCourantX);
			}
		}
	}

	
	
	/**
	 * Method to draw pixels according to the point mode (set or not set)
	 * 
	 * @param gc where to draw
	 */
	private void drawPixelsInfos(GC gc) {
		if(ActivatorVueGraphique.getDefault().getConfigurationMng().getMode() == TypeMode.POINT) {
			drawPointPixelsInfos(gc);
		} else {
			drawLinePixelsInfos(gc);
		}
	}

	/**
	 * Method to draw pixels in point mode. It is called if POINT_MODE is set
	 * 
	 * @param gc where to draw
	 */
	private void drawPointPixelsInfos(GC gc) {
		Courbe courbeCourante;
		Color courbeCouleur;
		RGB rgbCourbeCouleur;
		Integer curPixelAbscisse;
		Integer curPixelOrdonee;
		int upperThreshold = MARGE_HAUT;	//ordonees should not be less than this (for zooming)
		int lowerThreshold = height - MARGE_BAS;	//ordonees should not be less than this (for zooming)

        	int lowerThresholdAbs = (int) PixelBuffer.ORIGINE_X;
        	int upperThresholdAbs = width - MARGE_LATERALE;

		//If in PointMode the points are drawn by using backgrownd color
		//We save the current GC background in order to restore it when finished
		Color prevGcBackColor = gc.getBackground();

		for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {
			courbeCourante = courbePixelsInfo.getCourbe();
			gc.setBackground(prevGcBackColor);



			rgbCourbeCouleur = courbeCourante.getCouleur();
			courbeCouleur = new Color(gc.getDevice(), rgbCourbeCouleur.red, 
					rgbCourbeCouleur.green, rgbCourbeCouleur.blue);

			//set the colors according to the mode
			gc.setBackground(courbeCouleur);
			for(VariablePixelInfo curPixelInfo: courbePixelsInfo.getXPosToPixelInfo()) {
				curPixelAbscisse = curPixelInfo.getAbscisse();
				for(int i = 0; i<curPixelInfo.getValuesCount(); i++) {
					curPixelOrdonee = curPixelInfo.getOrdonee(i);
					//we draw visible points only
					if(curPixelOrdonee != null && curPixelOrdonee >= upperThreshold && curPixelOrdonee <= lowerThreshold) {
						//Draw the point only if is not a propagated value
						if(curPixelAbscisse!=null && !curPixelInfo.getValue(i).isPropagated()){
							if(curPixelAbscisse >= lowerThresholdAbs && curPixelAbscisse < upperThresholdAbs){
							gc.fillOval(curPixelAbscisse - POINT_SIZE/2, curPixelOrdonee - POINT_SIZE/2,
									POINT_SIZE, POINT_SIZE);
					}
				}
			}
				}
			}
			courbeCouleur.dispose();
			gc.setBackground(prevGcBackColor);
		}
		gc.setBackground(prevGcBackColor);		
		for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {
			courbeCourante = courbePixelsInfo.getCourbe();
			dessinerAxeOrdonnee(courbeCourante, gc, grapheCourante);
		}
		
		gc.setBackground(prevGcBackColor);
		dessinerGradationsAbscisse(grapheCourante, gc, -1);
		//Restore GC back color if in point mode
		gc.setBackground(prevGcBackColor);
	}

	private void dessinerMarcheEscalier(GC gc, CourbePixelsInfo courbePixelsInfo) {
		Courbe courbeCourante;
		Color courbeCouleur;
		RGB rgbCourbeCouleur;
		VariablePixelInfo prevPixelInfo;
		Integer prevPixelAbscisse;
		Integer prevPixelOrdonee;
		Integer curPixelAbscisse;
		Integer curPixelOrdonee;
		int upperThresholdOrd = MARGE_HAUT;	//ordonees should not be less than this (for zooming)
		int lowerThresholdOrd = height - MARGE_BAS;	//ordonees should not be less than this (for zooming)
		int upperThresholdAbs = width-MARGE_LATERALE;

		// for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {

			courbeCourante = courbePixelsInfo.getCourbe();
			if (courbeCourante.getVariable().getDescriptor().getM_AIdentificateurComposant().getNom().equals(TypeRepere.vitesseCorrigee.getName()) && GrapheGUI.this.vitCorDansFiltre) {
				return;
			}

			rgbCourbeCouleur = courbeCourante.getCouleur();
			courbeCouleur = new Color(gc.getDevice(), rgbCourbeCouleur.red, 
					rgbCourbeCouleur.green, rgbCourbeCouleur.blue);

			//set the colors according to the mode
			gc.setForeground(courbeCouleur);
			gc.setLineWidth(1);
			prevPixelInfo = null;

			// R�cup�ration de la liste des ruptures d'acquisition
			List<Integer> rupturesAcquisition = new ArrayList<Integer>();
			for (String[] item : EmplacementsRuptures.getInstance().getListDistanceBreaksPixels()) {
        		if ("RUPTURE_ACQUISITION".equals(item[3])) {
        			rupturesAcquisition.add(Integer.valueOf(item[1]) + MARGE_LATERALE);
        		}
        	}
			Collections.sort(rupturesAcquisition, new Comparator<Integer>() {
			    @Override
			    public int compare(Integer rupture1Abs, Integer rupture2Abs) {
			        return rupture1Abs.compareTo(rupture2Abs);
			    }
			});     
			
			Object tab[]=new Object[0];
			tab=courbePixelsInfo.getXPosToPixelInfo().toArray();

			for (int i = 0; i < tab.length; i++) {
				VariablePixelInfo curPixelInfo=(VariablePixelInfo)tab[i];
				int valuesCount=curPixelInfo.getValuesCount();

				for (int j = 0; j < valuesCount; j++) {

					curPixelAbscisse = curPixelInfo.getAbscisse();

					if(prevPixelInfo != null) {
						//    				//do not draw a line between segments
						//  				if(!currentAxe.isSegmentDifferent(prevPixelInfo.getFirstMessageId(), 
						//  				curPixelInfo.getFirstMessageId())) {	

						prevPixelAbscisse = prevPixelInfo.getAbscisse();
						curPixelAbscisse = curPixelInfo.getAbscisse();

						if (prevPixelAbscisse < upperThresholdAbs) {

							//draw the line only if not the same pixel 
							//(usually, we should not have this situation)
							if(curPixelAbscisse != prevPixelAbscisse) {

								//if(curPixelAbscisse != prevPixelAbscisse)// && ActivatorVueGraphique.getDefault().getConfigurationMng().isMarches_escalier())
								//draw line for this pixel
								drawLineForPixel(gc, curPixelInfo);    								 								

								//now draw line between previous and current pixel
								prevPixelOrdonee = prevPixelInfo.getLastOrdonnee();
								curPixelOrdonee = curPixelInfo.getFirstOrdonnee();

								//We ignore pixels that are both outside the drawing area 
								if((prevPixelOrdonee!= null && curPixelOrdonee!= null) && (!(prevPixelOrdonee < upperThresholdOrd && curPixelOrdonee < upperThresholdOrd) &&
										!(prevPixelOrdonee > lowerThresholdOrd && curPixelOrdonee > lowerThresholdOrd))) {    			

									//if we have a stepped configuration or if the graphic
									//display discrete variables, we create a stepped graphic

									//for zooming we want to have the lines inside the drawing frame
									if(prevPixelOrdonee < upperThresholdOrd) {
										prevPixelOrdonee = upperThresholdOrd;
										prevPixelAbscisse = curPixelAbscisse;
									} else if(prevPixelOrdonee > lowerThresholdOrd) {	            			
										prevPixelOrdonee = lowerThresholdOrd;
										prevPixelAbscisse = curPixelAbscisse;
									}

									//if the current pixel is outside min/max domain
									if(curPixelOrdonee < upperThresholdOrd) {
										curPixelOrdonee = upperThresholdOrd;
										curPixelAbscisse = prevPixelAbscisse;

									} else if(curPixelOrdonee > lowerThresholdOrd) {
										curPixelOrdonee = lowerThresholdOrd;
										curPixelAbscisse = prevPixelAbscisse;
									}

									
									if(curPixelAbscisse> upperThresholdAbs){
									    curPixelAbscisse = upperThresholdAbs;
									}
									
									Integer rupture = existsRuptureBetween(rupturesAcquisition, prevPixelAbscisse, curPixelAbscisse);
									if (rupture == -1) {
									//we draw a line from x1.y1 to x2.y1
									gc.drawLine(prevPixelAbscisse, prevPixelOrdonee,
											curPixelAbscisse, prevPixelOrdonee);

									//we draw a line from x2.y1 to x2.y2
									gc.drawLine(curPixelAbscisse, prevPixelOrdonee,
											curPixelAbscisse, curPixelOrdonee);									
									
									} else {
										// we draw from the prev point to the break
										gc.drawLine(prevPixelAbscisse, prevPixelOrdonee,
												rupture, prevPixelOrdonee);
									}
								}
							}
						}

					}
					prevPixelInfo = curPixelInfo;
				}
			}
			courbeCouleur.dispose();
		// }
	}

	private Integer existsRuptureBetween(List<Integer> rupturesAbscisses, Integer abscisse1, Integer abscisse2) {
		Integer rupture = -1;
		Iterator<Integer> it = rupturesAbscisses.iterator();
		while (it.hasNext()) {
			Integer currAbscisse = it.next();
			if (currAbscisse > abscisse1 && currAbscisse < abscisse2) {
				rupture = currAbscisse;
				break;
			}
			if (currAbscisse > abscisse2) {
				break;
			}
		}
		return rupture;
	}

	private void dessinerPointApointUsingLinkedList(GC gc, CourbePixelsInfo courbePixelsInfo) {     	
        	int lowerThresholdAbs = (int) PixelBuffer.ORIGINE_X;
        	int upperThresholdAbs = width - MARGE_LATERALE;
        
        	Courbe courbeCourante = courbePixelsInfo.getCourbe();
        	RGB rgbCourbeCouleur = courbeCourante.getCouleur();
		Color courbeCouleur = new Color(gc.getDevice(), rgbCourbeCouleur.red, 
				rgbCourbeCouleur.green, rgbCourbeCouleur.blue);

		//set the colors according to the mode
		gc.setForeground(courbeCouleur);
		gc.setLineWidth(1);
		
        	if (courbeCourante.getVariable().getDescriptor().getM_AIdentificateurComposant().getNom().equals(TypeRepere.vitesseCorrigee.getName()) && GrapheGUI.this.vitCorDansFiltre) {
        	    return;
        	}
        	
        	// r�cup�ration des changements de valeurs et des point valoris�s
        	LinkedList<VirtualPoint> vpEdge = edgeDetection(courbePixelsInfo, gc);

        	// Ajout des ruptures de distance (provoque des pertes de trac� et non homog�ne avec la l�gende...)
        	List<Integer> rupturesAcquisition = new ArrayList<Integer>();
        	for (String[] item : EmplacementsRuptures.getInstance().getListDistanceBreaksPixels()) {
        		if (RUPTURE_ACQUISITION.equals(item[3])) {
        			rupturesAcquisition.add(Integer.valueOf(item[1]) + MARGE_LATERALE);
        		}
        	}
			Collections.sort(rupturesAcquisition, new Comparator<Integer>() {
			    @Override
			    public int compare(Integer rupture1Abs, Integer rupture2Abs) {
			        return rupture1Abs.compareTo(rupture2Abs);
			    }
			}); 
        	
        	LinkedList<VirtualPoint> vpEdgeValuedOnly = new LinkedList<VirtualPoint>();
        	LinkedList<VirtualPoint> vpEdgeNullOnly = new LinkedList<VirtualPoint>();
        	
        	Iterator<VirtualPoint> it = vpEdge.iterator();
        	while (it.hasNext()) {
        	    VirtualPoint vp = it.next();        	    
        	    if(vp.getOrdonnee() != null){
        		vpEdgeValuedOnly.add(vp);
        	    }else if(vp.getOrdonnee() == null && !vp.isRupture()){
        		vpEdgeNullOnly.add( vp);        		
	}
        	}

        	// Tracer la courbe en ignorant les ruptures 
    	        VirtualPoint leftP = null;
    	        NavigableMap<Long, VirtualPoint> mapVpTrace = new TreeMap<Long, VirtualPoint>();
    	        for (Iterator<VirtualPoint> i = vpEdgeValuedOnly.iterator(); i.hasNext();) {
        	    VirtualPoint vp = i.next();
        	    long abscisseKey = vp.getAbscissePixel();
        	    
        	    mapVpTrace.put(abscisseKey, vp);
        	    
        	    if (leftP == null) {
        	    	leftP = vp;
        	    	continue;
        	    }     
        	    // Point les points � tracer dans la zone d'affichage
        	    if (leftP.getAbscissePixel() >= lowerThresholdAbs && leftP.getAbscissePixel() <= upperThresholdAbs 
        		    && vp.getAbscissePixel() >= lowerThresholdAbs && vp.getAbscissePixel() <= upperThresholdAbs) {
        	    	checkRupturesAcquisitionAndTrace(gc, rupturesAcquisition, (int) leftP.getAbscissePixel(), leftP.getOrdonnee().intValue(), (int) vp.getAbscissePixel(), vp.getOrdonnee().intValue());
        	    }
        	    // Point le point � tracer dans la zone d'affichage et le point � gauche de la zone
        	    else if(leftP.getAbscissePixel() < lowerThresholdAbs && vp.getAbscissePixel() >= lowerThresholdAbs && vp.getAbscissePixel() <= upperThresholdAbs){
        	    	double ordonne = computeOrdonneeFromAbscisse(leftP.getAbscissePixel(), leftP.getOrdonnee(), vp.getAbscissePixel(), vp.getOrdonnee(), lowerThresholdAbs);
        	    	checkRupturesAcquisitionAndTrace(gc, rupturesAcquisition, lowerThresholdAbs, (int) ordonne, (int) vp.getAbscissePixel(), vp.getOrdonnee().intValue());
        	    }
        	    // Point le point � tracer dans la zone d'affichage et le point � droite de la zone
        	    else if(leftP.getAbscissePixel() >= lowerThresholdAbs && leftP.getAbscissePixel() <= upperThresholdAbs && vp.getAbscissePixel() > upperThresholdAbs){
        	    	double ordonne = computeOrdonneeFromAbscisse(leftP.getAbscissePixel(), leftP.getOrdonnee(), vp.getAbscissePixel(), vp.getOrdonnee(), upperThresholdAbs);
        			checkRupturesAcquisitionAndTrace(gc, rupturesAcquisition, (int) leftP.getAbscissePixel(), leftP.getOrdonnee().intValue(), upperThresholdAbs, (int) ordonne);
        	    }
        	    // Point le point � tracer le point � gauche de la zone d'affichage et le point � droite de la zone (aucun point dans la zone)
        	    else if(leftP.getAbscissePixel() < lowerThresholdAbs && vp.getAbscissePixel() > upperThresholdAbs){
    	    		double lowerOrdonne = computeOrdonneeFromAbscisse(leftP.getAbscissePixel(), leftP.getOrdonnee(), vp.getAbscissePixel(), vp.getOrdonnee(), lowerThresholdAbs);
    	    		double upperOrdonne = computeOrdonneeFromAbscisse(leftP.getAbscissePixel(), leftP.getOrdonnee(), vp.getAbscissePixel(), vp.getOrdonnee(), upperThresholdAbs);
    	    		checkRupturesAcquisitionAndTrace(gc, rupturesAcquisition, lowerThresholdAbs, (int) lowerOrdonne, upperThresholdAbs, (int) upperOrdonne);
        	    }
        	    leftP = vp;        	    
        	}
    	            	        
    	        // Retirer les trac�s sans valorisation
    	        for (Iterator<VirtualPoint> i = vpEdgeNullOnly.iterator(); i.hasNext();) {
        	    VirtualPoint vp = i.next();
        	    long abscisseKey = vp.getAbscissePixel();
        	    if(!mapVpTrace.containsKey(abscisseKey)){     		
        		Long floorKey = mapVpTrace.floorKey(abscisseKey);
        		Long ceilingKey = mapVpTrace.ceilingKey(abscisseKey);
        		if(floorKey == null || ceilingKey == null){
        		    continue;
        		}
        		VirtualPoint beforeVp = mapVpTrace.get(floorKey);
        		VirtualPoint afterVp = mapVpTrace.get(ceilingKey);
        		double ordonne = computeOrdonneeFromAbscisse(beforeVp.getAbscissePixel(), beforeVp.getOrdonnee(), afterVp.getAbscissePixel(), afterVp.getOrdonnee(), abscisseKey+1);
        		double ordoneeFin = computeOrdonneeFromAbscisse(beforeVp.getAbscissePixel(), beforeVp.getOrdonnee(), afterVp.getAbscissePixel(), afterVp.getOrdonnee(), afterVp.getAbscissePixel()-1);
        		gc.drawLine((int) abscisseKey+1, (int) ordonne,(int) afterVp.getAbscissePixel()-1, (int) ordoneeFin);
        	    }
    	        }
    	        
    	        // Lib�ration des objets Java allou�s
    	        vpEdge = null;
    	        // vpEdgeRupture = null;
    	        vpEdgeNullOnly = null;
    	        vpEdgeValuedOnly = null;
    	        mapVpTrace = null;
    	        courbePixelsInfo.setVirtualPointLinkedList(null);

		gc.setLineWidth(1);
		courbeCouleur.dispose();
	}
	
	private void checkRupturesAcquisitionAndTrace(GC gc, List<Integer> rupturesAcquisition, int absP1, int ordP1, int absP2, int ordP2) {
    	Integer rupture = existsRuptureBetween(rupturesAcquisition, absP1, absP2); 	
    	if (rupture == -1) {
    		gc.drawLine(absP1, ordP1, absP2, ordP2);
    	} else {
    		Double ordonneeRupture = computeOrdonneeFromAbscisse(absP1, ordP1, absP2, ordP2, rupture);
    		gc.drawLine(absP1, ordP1, rupture.intValue(), ordonneeRupture.intValue());
    	}
	}

	private LinkedList<VirtualPoint> edgeDetection(CourbePixelsInfo courbePixelsInfo, GC gc){
        	boolean edge = false;
        	VirtualPoint leftPoint = null;
        	VirtualPoint rightPoint = null;
        
        	LinkedList<VirtualPoint> listRPValues = courbePixelsInfo.getVirtualPointLinkedList();
        	LinkedList<VirtualPoint> listRPValuesEdgeOnly = new LinkedList<VirtualPoint>();
        	long abscisseKey = -1;
        	for (Iterator<VirtualPoint> i = listRPValues.iterator(); i.hasNext();) {
        	    VirtualPoint vp = i.next();
        	    
        	    if(leftPoint != null && abscisseKey > vp.getAbscissePixel()){
        		//Cas ou le point n'est pas valide
        		continue;
        	    }
        	    else if(leftPoint != null && abscisseKey <= vp.getAbscissePixel()){
        		abscisseKey = vp.getAbscissePixel();
        	    }
        	    
        	    if (leftPoint == null) {
        		leftPoint = vp;
        		abscisseKey = vp.getAbscissePixel();
        		continue;
        	    } else {
        		rightPoint = vp;
        	    }   	    
        	    
        	    if ((leftPoint.getOrdonnee() == null && rightPoint.getOrdonnee() != null) ||
        		    (leftPoint.getOrdonnee() != null && !leftPoint.getOrdonnee().equals(rightPoint.getOrdonnee()))) {
        		edge = true;
        		leftPoint = vp;
        	    } else {
        		edge = false;
        	    }
        
        	    if (edge || vp.isValorised()) {
        		listRPValuesEdgeOnly.add(vp);        		
        		edge = false;
        	    }

        	}
        	return listRPValuesEdgeOnly;
	}
	
	/**
	 * @deprecated
	 * @param gc
	 * @param courbePixelsInfo
	 */
	private void dessinerPointApoint(GC gc, CourbePixelsInfo courbePixelsInfo) {
		Courbe courbeCourante;
		Color courbeCouleur;
		RGB rgbCourbeCouleur;
		VariablePixelInfo prevPixelInfo;
		Integer prevPixelAbscisse, prevPixelOrdonee;
		Integer curPixelAbscisse, curPixelOrdonee;
		int upperThresholdOrd = MARGE_HAUT;	//ordonees should not be less than this (for zooming)
		int lowerThresholdOrd = height - MARGE_BAS;	//ordonees should not be less than this (for zooming)
		int upperThresholdAbs = width-MARGE_LATERALE;

		// for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {

			courbeCourante = courbePixelsInfo.getCourbe();
			if (courbeCourante.getVariable().getDescriptor().getM_AIdentificateurComposant().getNom().equals(TypeRepere.vitesseCorrigee.getName()) && GrapheGUI.this.vitCorDansFiltre) {
				return;
			}

			rgbCourbeCouleur = courbeCourante.getCouleur();
			courbeCouleur = new Color(gc.getDevice(), rgbCourbeCouleur.red, rgbCourbeCouleur.green, rgbCourbeCouleur.blue);

			//set the colors according to the mode
			gc.setForeground(courbeCouleur);
			gc.setLineWidth(1);
			prevPixelInfo = null;

			Object tab[]=new Object[0];
			tab=courbePixelsInfo.getXPosToPixelInfo().toArray();

			PointImagine afterOutsideLeftPoint = null;
			PointImagine beforeOutsideRightPoint = new PointImagine();


			ArrayList<String[]> listDistanceBreaksPixels = EmplacementsRuptures.getInstance().getListDistanceBreaksPixels();
			
			for (int i = 0; i < tab.length; i++) {
				VariablePixelInfo curPixelInfo=(VariablePixelInfo)tab[i];

					curPixelAbscisse = curPixelInfo.getAbscisse();
					curPixelOrdonee = curPixelInfo.getFirstOrdonneeNotPropagated();

					if(prevPixelInfo != null) {

						prevPixelAbscisse = prevPixelInfo.getAbscisse();
						curPixelAbscisse = curPixelInfo.getAbscisse();

						if (prevPixelAbscisse!= null && prevPixelAbscisse < upperThresholdAbs) {

							//draw the line only if not the same pixel 
							//(usually, we should not have this situation)
							if(curPixelAbscisse != prevPixelAbscisse) {

								//draw line for this pixel
								drawLineForPixel2(gc, curPixelInfo);    								 								

								//now draw line between previous and current pixel
								Integer prevpxif= prevPixelInfo.getLastOrdonneeNotPropagated();
								Integer curpxif=curPixelInfo.getFirstOrdonneeNotPropagated();
								
								if (prevpxif!=null && curpxif!=null) {
									curPixelOrdonee=curpxif;
									prevPixelOrdonee=prevpxif;
									
									//We ignore pixels that are both outside the drawing area 
									if((!(prevPixelOrdonee < upperThresholdOrd && curPixelOrdonee < upperThresholdOrd) &&
											!(prevPixelOrdonee > lowerThresholdOrd && curPixelOrdonee > lowerThresholdOrd))) {    			

										//if we have a stepped configuration or if the graphic
										//display discrete variables, we create a stepped graphic

										//for zooming we want to have the lines inside the drawing frame
										if(prevPixelOrdonee < upperThresholdOrd) {
											prevPixelOrdonee = upperThresholdOrd;
											prevPixelAbscisse = curPixelAbscisse;
										} else if(prevPixelOrdonee > lowerThresholdOrd) {	            			
											prevPixelOrdonee = lowerThresholdOrd;
											prevPixelAbscisse = curPixelAbscisse;
										}

										//if the current pixel is outside min/max domain
										if(curPixelOrdonee < upperThresholdOrd) {
											curPixelOrdonee = upperThresholdOrd;
											curPixelAbscisse = prevPixelAbscisse;

										} else if(curPixelOrdonee > lowerThresholdOrd) {
											curPixelOrdonee = lowerThresholdOrd;
											curPixelAbscisse = prevPixelAbscisse;
										}

										if(curPixelAbscisse> upperThresholdAbs){
										    // Fonction affine pour relier les deux points 
										    // et d�terminer la position du point au bord 
										    // de la zone d'affichage
										    int correctedCurPixelAbscisse = upperThresholdAbs-1+LONGUEUR_GRAD/2;
										    int correctedCurPixelOrdonee = (int) computeOrdonneeFromAbscisse(prevPixelAbscisse, prevPixelOrdonee, curPixelAbscisse, curPixelOrdonee, correctedCurPixelAbscisse);
										    //we draw a line from x1.y1 to x2.y2
										    gc.drawLine(prevPixelAbscisse, prevPixelOrdonee,correctedCurPixelAbscisse, correctedCurPixelOrdonee);

										}else{
										//we draw a line from x1.y1 to x2.y2
										gc.drawLine(prevPixelAbscisse, prevPixelOrdonee,curPixelAbscisse, curPixelOrdonee);		
										    beforeOutsideRightPoint.setAbscissePixel(curPixelAbscisse);
										    beforeOutsideRightPoint.setOrdonnee((long) curPixelOrdonee);
									}

								}
							}
						}
					}
					}
					if (curPixelInfo.getLastOrdonneeNotPropagated()!=null) {
						prevPixelInfo = curPixelInfo;
					}
					
					if(curPixelOrdonee != null && afterOutsideLeftPoint == null ){
						afterOutsideLeftPoint = new PointImagine();
						afterOutsideLeftPoint.setAbscissePixel(curPixelAbscisse);
						afterOutsideLeftPoint.setOrdonnee((long) curPixelOrdonee);
						beforeOutsideRightPoint.setAbscissePixel(curPixelAbscisse);
						beforeOutsideRightPoint.setOrdonnee((long) curPixelOrdonee);
					}
					
			}
									
			// Traitement du cas du point � gauche hors cadre
			if(afterOutsideLeftPoint != null && courbePixelsInfo.getPreviousPix() != null && courbePixelsInfo.getPreviousPix().getOrdonnee() != null &&  afterOutsideLeftPoint.getOrdonnee() != null){
				double newVirtualPixOrdo = computeOrdonneeFromAbscisse((int) courbePixelsInfo.getPreviousPix().getAbscissePixel(),courbePixelsInfo.getPreviousPix().getOrdonnee().intValue(),(int) afterOutsideLeftPoint.getAbscissePixel(), afterOutsideLeftPoint.getOrdonnee().intValue(), (int) PixelBuffer.ORIGINE_X);
				newVirtualPixOrdo = fixThresholdOrdLimites(newVirtualPixOrdo , courbePixelsInfo.getPreviousPix().getOrdonnee(),afterOutsideLeftPoint.getOrdonnee(), lowerThresholdOrd, upperThresholdOrd);			
				gc.drawLine((int) PixelBuffer.ORIGINE_X, (int) newVirtualPixOrdo, (int) afterOutsideLeftPoint.getAbscissePixel(), afterOutsideLeftPoint.getOrdonnee().intValue());
			}
			
			// Traitement du cas du point � droite hors cadre
			if(beforeOutsideRightPoint.getOrdonnee() != null && courbePixelsInfo.getNextPix() != null && courbePixelsInfo.getNextPix().getOrdonnee() != null){
				double newVirtualPixOrdo = computeOrdonneeFromAbscisse((int) beforeOutsideRightPoint.getAbscissePixel(), beforeOutsideRightPoint.getOrdonnee().intValue(), (int) courbePixelsInfo.getNextPix().getAbscissePixel(),courbePixelsInfo.getNextPix().getOrdonnee().intValue(), upperThresholdAbs);
				newVirtualPixOrdo = fixThresholdOrdLimites(newVirtualPixOrdo , courbePixelsInfo.getNextPix().getOrdonnee(),beforeOutsideRightPoint.getOrdonnee(), lowerThresholdOrd, upperThresholdOrd);			
				gc.drawLine(upperThresholdAbs, (int) newVirtualPixOrdo,(int) beforeOutsideRightPoint.getAbscissePixel(), beforeOutsideRightPoint.getOrdonnee().intValue());
			}
			// Cas o� un zoom sur uen zone sans point � afficher
			if(afterOutsideLeftPoint == null && beforeOutsideRightPoint.getOrdonnee() == null && courbePixelsInfo.getPreviousPix() != null && courbePixelsInfo.getNextPix() != null && courbePixelsInfo.getNextPix().getOrdonnee() != null && courbePixelsInfo.getPreviousPix().getOrdonnee() != null){
			    
			    double virtualPrevPixOrdo = courbePixelsInfo.getPreviousPix().getOrdonnee();
			    double virtualNextPixOrdo = courbePixelsInfo.getNextPix().getOrdonnee();			    
			    double vitrtualPrevPixAbs = courbePixelsInfo.getPreviousPix().getAbscissePixel();
			    double vitrtualNextPixAbs = courbePixelsInfo.getNextPix().getAbscissePixel();
			    
			    if(vitrtualPrevPixAbs < PixelBuffer.ORIGINE_X){
				virtualPrevPixOrdo = computeOrdonneeFromAbscisse(vitrtualPrevPixAbs, virtualPrevPixOrdo, vitrtualNextPixAbs, virtualNextPixOrdo, PixelBuffer.ORIGINE_X);
				vitrtualPrevPixAbs = PixelBuffer.ORIGINE_X;
			    }
			    
			    if(vitrtualNextPixAbs > upperThresholdAbs){
				virtualNextPixOrdo = computeOrdonneeFromAbscisse(vitrtualPrevPixAbs, virtualPrevPixOrdo, vitrtualNextPixAbs, virtualNextPixOrdo, upperThresholdAbs);				
				vitrtualNextPixAbs = upperThresholdAbs;
			    }
			    
			    virtualPrevPixOrdo = fixThresholdOrdLimites(virtualPrevPixOrdo , virtualPrevPixOrdo,virtualNextPixOrdo, lowerThresholdOrd, upperThresholdOrd);			
			    virtualNextPixOrdo = fixThresholdOrdLimites(virtualNextPixOrdo , virtualPrevPixOrdo,virtualNextPixOrdo, lowerThresholdOrd, upperThresholdOrd);			    
				    
			    gc.drawLine( (int) vitrtualPrevPixAbs, (int) virtualPrevPixOrdo,(int) vitrtualNextPixAbs, (int) virtualNextPixOrdo);
			}
			
			for(String[] item : listDistanceBreaksPixels){
			    PointImagine ruptureStartPointImg = new PointImagine();
			    PointImagine ruptureEndPointImg = new PointImagine();
			    
			    
			    ruptureStartPointImg.setAbscissePixel(Integer.valueOf(item[1]));
			    
			    // Si un point outside left jusqu'� rupture
			    if(beforeOutsideRightPoint != null && beforeOutsideRightPoint.getOrdonnee() != null && courbePixelsInfo.getNextPix() != null && courbePixelsInfo.getNextPix().getOrdonnee() != null && ruptureStartPointImg.getAbscissePixel() > beforeOutsideRightPoint.getAbscissePixel()){
				ruptureStartPointImg.setOrdonnee(beforeOutsideRightPoint.getOrdonnee());
				ruptureEndPointImg.setAbscissePixel(Math.max(beforeOutsideRightPoint.getAbscissePixel(), PixelBuffer.ORIGINE_X));
			    }else if(beforeOutsideRightPoint != null && beforeOutsideRightPoint.getOrdonnee() != null && courbePixelsInfo.getNextPix() != null && courbePixelsInfo.getNextPix().getOrdonnee() == null && ruptureStartPointImg.getAbscissePixel()+MARGE_LATERALE > beforeOutsideRightPoint.getAbscissePixel()){
				ruptureStartPointImg.setOrdonnee(beforeOutsideRightPoint.getOrdonnee());
				ruptureEndPointImg.setAbscissePixel(Math.max(beforeOutsideRightPoint.getAbscissePixel(), PixelBuffer.ORIGINE_X));
			    }
			    
			    // Si un point outside left jusqu'� rupture
			    if(courbePixelsInfo != null && courbePixelsInfo.getPreviousPix() != null && courbePixelsInfo.getPreviousPix().getOrdonnee() != null && afterOutsideLeftPoint == null ){
				ruptureStartPointImg.setOrdonnee(courbePixelsInfo.getPreviousPix().getOrdonnee());
				ruptureEndPointImg.setAbscissePixel(Math.max(PixelBuffer.ORIGINE_X, courbePixelsInfo.getPreviousPix().getAbscissePixel()));
			    }
			    
			    ruptureStartPointImg.setAbscissePixel(ruptureStartPointImg.getAbscissePixel()+ MARGE_LATERALE);		    
			    ruptureEndPointImg.setOrdonnee(ruptureStartPointImg.getOrdonnee());
			    
			    if(ruptureEndPointImg.getOrdonnee() != null && ruptureStartPointImg.getOrdonnee()!= null){
				gc.drawLine((int) ruptureStartPointImg.getAbscissePixel(), ruptureStartPointImg.getOrdonnee().intValue(),(int) ruptureEndPointImg.getAbscissePixel(), (int) ruptureEndPointImg.getOrdonnee().intValue());
			}
			    
			}
			
			courbeCouleur.dispose();
		}

	// Fonction de calcul de l'ordonn�e � partir de deux points pour un abscisse d�termin� en Integer
	private double computeOrdonneeFromAbscisse(int x1Pos, int y1Pos, int x2Pos, int y2Pos, int xPos){	
	    return computeOrdonneeFromAbscisse((double) x1Pos, (double) y1Pos, (double) x2Pos, (double) y2Pos, (double) xPos);
	}

	// Fonction de calcul de l'ordonn�e � partir de deux points pour un abscisse d�termin� en Double
	private double computeOrdonneeFromAbscisse(double x1Pos, double y1Pos, double x2Pos, double y2Pos, double xPos){
	    double a = (y2Pos- y1Pos)/(x2Pos - x1Pos);    
	    double b = y1Pos - (x1Pos * a);
	    return a*xPos+b;
	}
	
	// Fonction de bornage de l'ordonn� calcul� pour �viter des glitches 
	private double fixThresholdOrdLimites(double yPosToValidate , double y1Pos, double y2Pos, double frameLowerThresoldPos, double frameUpperThresoldPos){

        	if (yPosToValidate < Math.min(y1Pos, y2Pos)) {
        	    yPosToValidate = Math.min(y1Pos, y2Pos);
        	}
        	if (yPosToValidate > Math.max(y1Pos, y2Pos)) {
        	    yPosToValidate = Math.max(y1Pos, y2Pos);
        	}
        
        	if (yPosToValidate < frameUpperThresoldPos) {
        	    yPosToValidate = Math.min(y1Pos, y2Pos);
        	}
        	if (yPosToValidate > frameLowerThresoldPos) {
        	    yPosToValidate = Math.max(y1Pos, y2Pos);
        	}
    	    
	    return yPosToValidate;
	}

	/**
	 * Method to draw pixels in line mode. It is called if POINT_MODE is not set
	 * 
	 * @param gc where to draw
	 */
	private void drawLinePixelsInfos(GC gc) {
		for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {
			Courbe courbe = courbePixelsInfo.getCourbe();
			AVariableComposant courbeVar = courbe.getVariable();
		if (ActivatorVueGraphique.getDefault().getConfigurationMng().isMarches_escalier()||
					grapheCourante.getTypeGraphe() == TypeGraphe.DIGITAL 
					|| courbeVar.isEscalier()) {
				dessinerMarcheEscalier(gc, courbePixelsInfo);
		}
			else{
			    dessinerPointApointUsingLinkedList(gc, courbePixelsInfo);			    
			}
		}

		for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {
		    dessinerAxeOrdonnee(courbePixelsInfo.getCourbe(), gc, grapheCourante);
		}
		
		dessinerGradationsAbscisse(grapheCourante, gc, -1);
	}

	/**
	 * For a pixel that has multiple values we draw a line from maximum 
	 * to minimum
	 *
	 */
	private void drawLineForPixel2(GC gc, VariablePixelInfo pixelInfo) {
		Integer minOrdonee=null;
		Integer maxOrdonee=null;

		minOrdonee = pixelInfo.getMinPropagatedOrdonee();
		maxOrdonee = pixelInfo.getMaxPropagatedOrdonee();

		if(minOrdonee == maxOrdonee)
			return;
		int upperThreshold = MARGE_HAUT;	//ordonees should not be less than this (for zooming)
		int lowerThreshold = height - MARGE_BAS;	//ordonees should not be greater than this (for zooming)
		if((minOrdonee < upperThreshold && maxOrdonee < upperThreshold) ||
				(minOrdonee > lowerThreshold && maxOrdonee > lowerThreshold)) {
			//if we have the point outside the drawing area, we draw nothing for this point
			return;
		}
		//as we are drawing relative to Top we have min and max inverted
		//: see that
		if(minOrdonee < upperThreshold)
			minOrdonee = upperThreshold;
		if(maxOrdonee > lowerThreshold)
			maxOrdonee = lowerThreshold;
		int abscisse = pixelInfo.getAbscisse();

		//relie last point drawn with new point
		gc.drawLine(abscisse, minOrdonee, abscisse, maxOrdonee);
	}

	/**
	 * For a pixel that has multiple values we draw a line from maximum 
	 * to minimum
	 *
	 */
	private void drawLineForPixel(GC gc, VariablePixelInfo pixelInfo) {
		Integer minOrdonee= null;
		Integer maxOrdonee= null;

		minOrdonee = pixelInfo.getMinOrdonee();
		maxOrdonee = pixelInfo.getMaxOrdonee();

		if(minOrdonee == maxOrdonee)
			return;
		int upperThreshold = MARGE_HAUT;	//ordonees should not be less than this (for zooming)
		int lowerThreshold = height - MARGE_BAS;	//ordonees should not be greater than this (for zooming)
		if((minOrdonee < upperThreshold && maxOrdonee < upperThreshold) ||
				(minOrdonee > lowerThreshold && maxOrdonee > lowerThreshold)) {
			//if we have the point outside the drawing area, we draw nothing for this point
			return;
		}
		//as we are drawing relative to Top we have min and max inverted
		//: see that
		if(minOrdonee < upperThreshold)
			minOrdonee = upperThreshold;
		if(maxOrdonee > lowerThreshold)
			maxOrdonee = lowerThreshold;
		int abscisse = pixelInfo.getAbscisse();

		//relie last point drawn with new point
		gc.drawLine(abscisse, minOrdonee, abscisse, maxOrdonee);
	}

	/**
	 * Draws the lines for zero ref in digital graphic
	 * @param gc
	 */
	private void drawDigitalZeroRefLines(GC gc) {
		if(grapheCourante.getTypeGraphe() == TypeGraphe.DIGITAL &&
				ActivatorVueGraphique.getDefault().getConfigurationMng().isRef_zero_digit()) {
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
			gc.setLineStyle(SWT.LINE_DASH);
			List<Courbe> curves = grapheCourante.getListeCourbe();
			int curvesCount = curves.size();
			int courbesVSpacing = (height - (MARGE_BAS + MARGE_HAUT)) / (2*curvesCount);
			int yDrawPos = height - MARGE_BAS;
			int xDrawPos = width - MARGE_LATERALE;
			for(int i = 0; i<curvesCount; i++) {
				gc.drawLine(MARGE_LATERALE, yDrawPos, xDrawPos, yDrawPos);
				yDrawPos -= 2*courbesVSpacing;
			}
			gc.setLineStyle(SWT.LINE_SOLID);	        
		}
	}

	private void drawCadre(GC gc) {
		gc.setLineWidth(2);
		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY));
		gc.drawLine(0, height - 1, width, height - 1);
	}

	private void drawCursor(GC gc) {

		if (Curseur.getInstance().getCurseurVisible()) {
			if(this.xCursorPosition >= MARGE_LATERALE) {
				gc.setLineWidth(1);
				gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
				//				xCursorPosition=Curseur.getInstance().positionCurseur;
				Curseur.getInstance().setpositionCurseur(xCursorPosition);
				gc.drawLine(xCursorPosition, 0, xCursorPosition, height);

			}else{ 		
				try {
					Curseur.getInstance().getEv();
					this.xCursorPosition=Curseur.getInstance().getpositionCurseur();
					gc.setLineWidth(1);
					gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
					xCursorPosition=Curseur.getInstance().getEv().getxPos();
					gc.drawLine(xCursorPosition, 0, xCursorPosition, height);

				} catch (Exception e) {
					this.xCursorPosition=1;
				} 
			}
		}

		if (this.zoomRectangle != null) {
			//Set the Xor Mode for the rectangle
			gc.setXORMode(true);
			//gc.drawRectangle(this.zoomRectangle);
			gc.fillRectangle(this.zoomRectangle);
			gc.setXORMode(false);
		}
	}



	private void drawMarkers(GC gc) {
		if(ActivatorVueGraphique.getDefault().getConfigurationMng().isMarqueurs()) {
			gc.setLineWidth(1);
			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
			int nbAnnotations=InfosBullesMarqueurs.getInstance().getListBullesDim().size();
			for (int i = 0; i < nbAnnotations; i++) {
				int pixel=Integer.parseInt(InfosBullesMarqueurs.getInstance().getListBullesDim().get(i).toString());
				gc.drawLine(pixel + MARGE_LATERALE, 0, pixel + MARGE_LATERALE, height);
			}
		}
	}

	private void drawTimeBreaks(GC gc) {
		RuptureLegendeMessage.getInstance().setDisplayTimeRuptureMsg(false);
		TableRuptures.getInstance().getListeRupturesTemps();
		int lastpixel=0;	//l'abscisse de fin de la derniere rupture affichee en ce moment 
		int hauteur=12;		//hauteur de l'�tiquette
		int longu=114;		//longueur de l'�tiquette
		String libelle;
		int nbpix=EmplacementsRuptures.getInstance().getListTimeBreaksPixels().size();	//nombre de ruptures de temps
		spacesRupture=new int [nbpix][2]; //tableau contenant les positions des �tiquettes
		int incre=0;	//indice incremental du tableau contenant les positions des �tiquettes

		if(ActivatorVueGraphique.getDefault().getConfigurationMng().isRuptures_temps()) {	//si les ruptures de temps doivent etre affichees
			gc.setLineWidth(1);
			Color colorTimeBreaks = new Color(getDisplay(), 
					GestionnaireCouleurs.getTimeBreaksColor()); //on utilise la couleur des ruptures de temps
			gc.setForeground(colorTimeBreaks); 		   		

			for (int i = 0; i < nbpix; i++) {	//pour chaque rupture de temps
				String listTimeBreaksPixels[]=new String[3];
				listTimeBreaksPixels=EmplacementsRuptures.getInstance().getListTimeBreaksPixels().get(i);
				int pixel=Integer.valueOf(listTimeBreaksPixels[1]);
				if (pixel+MARGE_LATERALE>lastpixel) {		//si l'�tiquette associ�e � la rupture peut etre affichee

					gc.drawLine(pixel + MARGE_LATERALE , 0, pixel + MARGE_LATERALE, height); //trace une ligne sur toute la verticalit� de la vue des graphes

					if(dernierGrapheNonVide){	//si dernier graphe
						libelle=String.valueOf(listTimeBreaksPixels[2]);//on recupere le libelle de l'�tiquette
						longu=CalculGraphe.getLongueurRectByString(libelle);

						gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
						gc.drawString(libelle,pixel + MARGE_LATERALE+1, 0);//on affiche l'�tiquette
						gc.drawRectangle(pixel + MARGE_LATERALE, 0, longu, hauteur);
						lastpixel=pixel+MARGE_LATERALE+longu;//on enregistre jusqu'ou va l'�tiquette
						spacesRupture[incre][0]=pixel+MARGE_LATERALE;//on stocke les positions de l'�tiquette
						spacesRupture[incre][1]=pixel+MARGE_LATERALE+longu;
						incre+=1;
					}
				}else{
					gc.drawLine(pixel + MARGE_LATERALE , hauteur, pixel + MARGE_LATERALE, height);
				}
			}
			colorTimeBreaks.dispose();			
		}else{
			if (nbpix>0 && dernierGrapheNonVide && TableRuptures.getInstance().getListeRupturesTemps().size()>0) {
				//				MessageBox msgBox = new MessageBox(this.getShell(),SWT.ICON_INFORMATION);
				//				//msgBox.setText(Messages.getString("RupturesDistanceGUI.0")); //$NON-NLS-1$
				//				msgBox.setMessage(Messages.getString("RupturesTempsGUI.0")); //$NON-NLS-1$
				//				msgBox.open();
				RuptureLegendeMessage.getInstance().setDisplayTimeRuptureMsg(true);
			}
		}
	}

	private void drawDistanceBreaks(GC gc) {
		int lastpixel=0;	//l'abscisse de fin de la derniere rupture affichee en ce moment
		int hauteur=12;		//hauteur de l'�tiquette
		int longu=114;		//longueur de l'�tiquette
		String libelle="";		//libelle de l'�tiquette
		int nbpix=EmplacementsRuptures.getInstance().getListDistanceBreaksPixels().size();	//nombre de ruptures de distance
		boolean ruptureToDisplay=true;
		int decalage2rupturesOnSamePixel=0;

		RuptureLegendeMessage.getInstance().setDisplayDistanceRuptureMsg(false);

		if(ActivatorVueGraphique.getDefault().getConfigurationMng().isRuptures_distance()) {	//si les ruptures de distance doivent etre affichees
			gc.setLineWidth(1);
			Color colorDistanceBreaks = new Color(getDisplay(), //on utilise la couleur des ruptures de distance
					GestionnaireCouleurs.getDistanceBreaksColor()); 
			gc.setForeground(colorDistanceBreaks);
			for (int j = 0; j < nbpix; j++) {	//pour chaque rupture de distance
				String listDistanceBreaksPixels[]=new String[3];
				listDistanceBreaksPixels=EmplacementsRuptures.getInstance().getListDistanceBreaksPixels().get(j);
				int pixel=Integer.valueOf(listDistanceBreaksPixels[1]);
				if (dernierGrapheNonVide){							//si dernier graphe

					ruptureToDisplay=true;
					decalage2rupturesOnSamePixel=0;
					for (int i = 0; i < spacesRupture.length; i++) {	//pour chaque rupture de temps
						//si le pixel o� doit etre affich�e la rupture est compris entre le d�but et la fin d'une �tiquette de rupture de temps
						if (spacesRupture[i][0]<pixel+MARGE_LATERALE && spacesRupture[i][1]>pixel+MARGE_LATERALE) {						
							ruptureToDisplay=false; //on affiche pas cette rupture
						}
						if (spacesRupture[i][0]==pixel+MARGE_LATERALE) {
							decalage2rupturesOnSamePixel=hauteur+1;
						}
					}	
				}
				//si l'�tiquette � afficher n'empiete pas sur l'�tiquette pr�c�dente
				if (pixel+MARGE_LATERALE>lastpixel && ruptureToDisplay) {
					//on trace une ligne verticale

					gc.setForeground(colorDistanceBreaks);
					gc.drawLine(pixel + MARGE_LATERALE, 0, pixel + MARGE_LATERALE, height); 	

					if(dernierGrapheNonVide){
						libelle=String.valueOf(listDistanceBreaksPixels[2]);;
						longu=CalculGraphe.getLongueurRectByString(libelle);

						gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
						gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_BLACK));
						gc.drawString(libelle,pixel + MARGE_LATERALE+1, 0+decalage2rupturesOnSamePixel);	//on affiche l'�tiquette
						gc.drawRectangle(pixel + MARGE_LATERALE, 0+decalage2rupturesOnSamePixel, longu, hauteur);
						lastpixel=pixel+MARGE_LATERALE+longu;
					}

				}else{	//on trace une ligne qui n'empiete pas sur l'�tiquette pr�c�dente
					gc.setForeground(colorDistanceBreaks);
					gc.drawLine(pixel + MARGE_LATERALE , hauteur, pixel + MARGE_LATERALE, height);
				}   			
			}
			colorDistanceBreaks.dispose();
		}else{
			if (nbpix>0 && dernierGrapheNonVide && TableRuptures.getInstance().getListeRupturesDistance().size()>0) {
				//				MessageBox msgBox = new MessageBox(this.getShell(),SWT.ICON_INFORMATION);
				//				msgBox.setText(Messages.getString("RupturesDistanceGUI.0")); //$NON-NLS-1$
				//				msgBox.setMessage(Messages.getString("RupturesDistanceGUI.0")); //$NON-NLS-1$
				//				msgBox.open();   			
				RuptureLegendeMessage.getInstance().setDisplayDistanceRuptureMsg(true);
			}
		}
	}

	/**
	 * Draws ref0 line (if the ref0 pixel is in the visivle area)
	 * 
	 * @param gc
	 */
	private void drawRef0(GC gc) {

		if(PositionReferenceZero.getInstance().getPosition()!=-1) {
			gc.setLineWidth(2);
			Color couleurOr=new Color(getDisplay(), 253, 240, 0);
			gc.setForeground(couleurOr);
			//			gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			int position=PositionReferenceZero.getInstance().getPosition()+MARGE_LATERALE;
			gc.drawLine(position, 0, position, height);
		}


		//		if (dernierGraphe) {
		//		gc.setLineWidth(1);
		//		gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_YELLOW));
		//		gc.drawLine(ref0Pixel + MARGE_LATERALE, 0, ref0Pixel + MARGE_LATERALE, height);
		//		}
	}

	private void fireCursorPositionChanged(int xPos, boolean isDoubleClick) {
		if(courbesPixelsInfo == null || courbesPixelsInfo.size() == 0)
			return;
		if(xPos < MARGE_LATERALE)
			xPos = MARGE_LATERALE;
		else if(xPos > width - MARGE_LATERALE)
			xPos = width - MARGE_LATERALE;
		int nbCurves=courbesPixelsInfo.size();
		CourbePixelsInfo courbePixelsInfo=null;
		VariablePixelInfo closestMsgPixelInfo=null,tempPixelInfo=null;

		int currentPos=xPos;
		int ecart=-1;

		for (int i = 0; i < nbCurves; i++) {
			courbePixelsInfo = courbesPixelsInfo.get(i);
			tempPixelInfo = courbePixelsInfo.getClosestMessageId(xPos, TYPE_CLOSEST_MESSAGE.BOTH);
			if(tempPixelInfo != null){
				if (ecart!=-1) {
					if (Math.abs(tempPixelInfo.getAbscisse()-currentPos)<ecart) {
						ecart=Math.abs(tempPixelInfo.getAbscisse()-currentPos);
						closestMsgPixelInfo=tempPixelInfo;
					}
				}else{
					ecart=Math.abs(tempPixelInfo.getAbscisse()-currentPos);
					closestMsgPixelInfo=tempPixelInfo;
				}
			}
		}

		int msgId = 0;
		try {
			if (closestMsgPixelInfo!=null) {
				msgId = closestMsgPixelInfo.getFirstMessageId();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if (Curseur.getInstance().getEv()!=null) {//correction impact correction issue 800
			int currentID=Curseur.getInstance().getEv().getCurrentMessage().getMessageId();
			for (CourbeMessageValue cmv : closestMsgPixelInfo.getVariableValues()) {
				if (cmv.getMsgId()==currentID){
					msgId=currentID;
					break;
				}
			}
		}

		try {
			fireCursorPositionChanged(closestMsgPixelInfo,msgId,isDoubleClick);
		} catch (RuntimeException e) {
			System.out.println("impossible de positionner le curseur");
		}
	}

	/**
	 * Handles a key pressed notification (left arrow, right arrow, home, end)
	 * 
	 * @param arrowLeft specifies the direction (left or right)
	 * @param isToExtremity specifies if was a home/end or just a simple arrow move
	 */
	private void onArrowKeyPressed(boolean arrowLeft, boolean isToExtremity, boolean turbo) {

		MouvementCurseur mvC=new MouvementCurseur(this);
		VariablePixelInfo closestMsgPixelInfo=mvC.RechercherNouveauPixel(arrowLeft, isToExtremity, turbo);
	
		if(closestMsgPixelInfo != null)
			fireCursorPositionChanged(closestMsgPixelInfo, Curseur.getInstance().getMsgId(), false);
		
		if (Activator.getDefault().getPreferenceStore()
				.getBoolean(PreferenceConstants.KEY_SYNC_CHOICE)) {
			closestMsgPixelInfo=
					courbesPixelsInfo.get(0).getPixelInfoByMessageId(Curseur.getInstance().getEv().getCurrentMessage().getMessageId());
			fireCursorPositionChanged(closestMsgPixelInfo, 
					Curseur.getInstance().getMsgId(), 
					true);
		}
	}

	/**
	 * Fires a cursor position change. The doubleClick flag is propagated in the
	 * fired event
	 * 
	 * @param closestMsgPixelInfo
	 * @param isDoubleClick
	 */
	public void fireCursorPositionChanged(VariablePixelInfo closestMsgPixelInfo, int msgId, boolean isDoubleClick) {
		//if we have at least one message in this graphic
		if(closestMsgPixelInfo != null) {
			AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
			//we always have the message ID set here
			setCursorPositionMsgId(msgId)/*closestMsgPixelInfo.getFirstMessageId()*/;
			Message msg = data.getEnregistrement().getGoodMessage(getCursorPositionMsgId());
			if(msg == null)
				return;
			//SUPPR_HASHMAP_VALEURMESSAGE
			//			r�cup�ration uniquement des bons messages

			ListMessages messages = ActivatorData.getInstance().getVueData()
					.getDataTable().getEnregistrement().getMessages();

			Curseur.getInstance().setpositionCurseur(closestMsgPixelInfo.getAbscisse());
			CursorPositionEvent cursorEvent = new CursorPositionEvent(messages.get(0),msg,closestMsgPixelInfo.getAbscisse(),
					grapheCourante.getNumero(),isDoubleClick);
			//			cursorEvent.firstMessage = messages.get(0);
			//			cursorEvent.currentMessage = msg;
			//			cursorEvent.xPos = closestMsgPixelInfo.getAbscisse();//xPos;
			//			cursorEvent.sourceGrapheNr = grapheCourante.getNumero();
			//			cursorEvent.isDoubleClick = isDoubleClick;
			GestionnaireGraphesNotifications.notifyCursorPositionChanged(cursorEvent);
			Curseur.getInstance().setMsgId(msgId);
		}
	}

	/**
	 * Notifies this graph that the cursor position has changed
	 * 
	 * @param event the cursor position changed event
	 * 
	 */
	public void cursorPositionChanged(CursorPositionEvent event) {
		if(event instanceof ValuedCursorPositionEvent)
			return;
		if(event.getxPos() < 0) {
			VariablePixelInfo info = VariablePixelInfo.getXPositionForMessageId(event.getCurrentMessage().getMessageId(),courbesPixelsInfo);
			event.setxPos(info == null ? -1 : info.getAbscisse());
		} 
		this.xCursorPosition = event.getxPos();	//we are interested in this class only on this member
		setCursorPositionMsgId(event.getCurrentMessage().getMessageId());




		Curseur.getInstance().setEv(event);
		if (InfoComposite.visibleEventRecord())
			Curseur.getInstance().setCurseurVisible(true);
		else
			Curseur.getInstance().setCurseurVisible(false);


		redraw();

		//create a new message with the values (this should be captured by the Legend GUI)
		ValuedCursorPositionEvent newCursorEvent = new ValuedCursorPositionEvent(event.getFirstMessage(),event.getCurrentMessage(),
				event.getxPos(),grapheCourante.getNumero(),event.isDoubleClick(),new CourbeMessageValue[courbesPixelsInfo.size()]
				);
		//		newCursorEvent.firstMessage = event.getFirstMessage();
		//		newCursorEvent.currentMessage = event.getCurrentMessage();
		//		newCursorEvent.xPos = event.getxPos();
		//		newCursorEvent.sourceGrapheNr = grapheCourante.getNumero();
		//		newCursorEvent.cmvvalues = new CourbeMessageValue[courbesPixelsInfo.size()];
		//		newCursorEvent.isDoubleClick = event.isDoubleClick();

		VariablePixelInfo pixelInfo;
		int i = 0;
		int msgIdx;
		int msgId = newCursorEvent.getCurrentMessage().getMessageId();
		//: these information should be taken from courves
		for(CourbePixelsInfo courbePixelsInfo: courbesPixelsInfo) {
			//we should have this
			int id = newCursorEvent.getCurrentMessage().getMessageId();
			pixelInfo = courbePixelsInfo.getPixelInfoByMessageId(id);
			if(pixelInfo != null) {
				msgIdx = pixelInfo.getIndexForMessageId(msgId);
				if(msgIdx != -1)
					newCursorEvent.getCmvvalues()[i] = pixelInfo.getValue(msgIdx);
				else 
					newCursorEvent.getCmvvalues()[i] = pixelInfo.getFirstVariableValue();
			}
			i++;
		}

		GestionnaireGraphesNotifications.notifyCursorPositionChanged(newCursorEvent);
	}

	/**
	 * Performs a zoom for the given rectangle and for the given zoom type
	 * 
	 * @param selection
	 * @param zoomState
	 */
	public void zoom(Rectangle selection, ZOOM_STATE zoomState) {
		if(courbesPixelsInfo.size() > 0) {
			int x1 = selection.x;
			int x2 = selection.x + selection.width;
			int y1 = selection.y;
			int y2 = selection.y + selection.height;
			
			if(zoomState == ZOOM_STATE.ZOOM_X) {
				AxeX currentAxeX = GestionnaireAxes.getInstance().getCurrentAxeX();
				//Zoom X

				CourbePixelsInfo courbePixelsInfo=null;
				VariablePixelInfo startVarPixelInfo=null;
				for (CourbePixelsInfo courbePixelsInfoz : courbesPixelsInfo) {
					startVarPixelInfo = courbePixelsInfoz.getClosestMessageId(x1, TYPE_CLOSEST_MESSAGE.NEXT);
					if (startVarPixelInfo!=null) {
						courbePixelsInfo=courbePixelsInfoz;
						break;
					}
				}

				if(startVarPixelInfo != null) {
					//check if the pixel is in the rectangle. We know for sure that it is 
					//greater than x1 but we have to check if it is less than X2
					if(startVarPixelInfo.getAbscisse() <= x2) {
						//the end var should be in this case at least startVarPixelInfo
						VariablePixelInfo endVarPixelInfo = courbePixelsInfo.getClosestMessageId(
								x2, TYPE_CLOSEST_MESSAGE.PREV);
						int startMsgId = startVarPixelInfo.getFirstMessageId();
						int endMsgId = endVarPixelInfo.getLastMessageId();

						AParcoursComposant data = ActivatorData.getInstance().getVueData().getDataTable();
						Message msgStart = data.getEnregistrement().getGoodMessage(startMsgId);
						Message msgEnd = data.getEnregistrement().getGoodMessage(endMsgId);

						int indice1=data.getEnregistrement().getMessages().getIndiceMessageById(startMsgId);
						int indice2=data.getEnregistrement().getMessages().getIndiceMessageById(endMsgId);

						if (indice2-indice1<5) {
							redraw();
							return;
						}

						double valueOffsetToStartMsg = (startVarPixelInfo.getAbscisse() - x1) * currentAxeX.getResolution();
						double computedTimeStart = GestionnaireAxes.getAxeXValue(msgStart) - valueOffsetToStartMsg;
						if(computedTimeStart < 0)
							computedTimeStart = GestionnaireAxes.getAxeXValue(msgStart);
						double valueOffsetFromEndMsg = (x2 - endVarPixelInfo.getAbscisse()) * currentAxeX.getResolution();
						double computedTimeEnd = valueOffsetFromEndMsg - GestionnaireAxes.getAxeXValue(msgEnd);
						if(computedTimeEnd < 0)
							computedTimeEnd = GestionnaireAxes.getAxeXValue(msgEnd);
						GestionnaireZoom.creerZoomX(currentAxeX, startMsgId, 
								endMsgId, computedTimeStart, computedTimeEnd);
					}
				} else {
					//we have no message in this view. Nothing 
				}
			} else {
				//change the coordonates relative to the bottom axis
				try {
					GestionnaireZoom.creerAllVarsZoomY(grapheCourante.getListeCourbe(), 
							grapheCourante.getNumero(), height - MARGE_BAS - y2, height - MARGE_BAS - y1);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.err.println("Zooming ....");
		}
		redraw();
	}

	/**
	 * Forces redraw (the courbes points are recalculated)
	 *
	 */
	public void forceRedraw() {
		image = null;
		redraw();
	}

	@Override
	public void dispose() {
		GestionnaireZoom.getInstance().removeListener(this);
		GestionnaireGraphesNotifications.removeGrapheCursorListener(this);
		FabriqueGraphe.removeGraphe(grapheCourante.getNumero());
		super.dispose();
	}

	/**
	 * Handles a zoom changed notification
	 */
	public void zoomChanged() {
		ZoomComposite currentZoom = (ZoomComposite)GestionnaireZoom.getZoomCourant();
		// R�cup�ration du zoom courant � partir du gestionnaire de Zoom
		if(currentZoom != null) {
		    // Pour chaque composant de zoom
		    for(AZoomComposant aZoom : currentZoom.getEnfants()){
			// Si c'est une composition de zoom
			if(aZoom != null && aZoom instanceof ZoomComposite) {
			    ZoomComposite zoom = (ZoomComposite)aZoom;	
			    // pour chaque zoom de la composition de zoom
			    for(AZoomComposant curZoom  : zoom.getEnfants()){
				// Si c'est un zoom en Y non null
				if(curZoom != null && TypeZoom.ZOOM_Y.equals(curZoom.getTypeZoom())){				    
				    ZoomY curZoomY = (ZoomY) curZoom;
				    // Pour chaque graphique : retrouver le zoom � appliquer sur la courbe depuis la fabrique de graphique
				    for(Graphe curGraphe : FabriqueGraphe.getGraphes()){
					// Si le graphique et la courbe est pr�sente dans le graphique
					if(zoom.getIndiceGraphe() == curGraphe.getNumero() && curGraphe.getNumero() == grapheCourante.getNumero() && curGraphe.getListeCourbe().contains(curZoomY.getCourbe())){
					    	// Identification de la courbe associ� au Zoom en Y
						for(int i = 0; i < curGraphe.getListeCourbe().size(); i++){
						    Courbe courbe = curGraphe.getListeCourbe().get(i);
						    // Courbe trouv�e 
						    if(courbe.equals(curZoomY.getCourbe()) && i < grapheCourante.getListeCourbe().size()){	
							// A partir de l'indice de la courbe trouv�e, on modifie la courbe 
							// du graphe courant de cette instance de l'objet GrapheUI
							courbe = grapheCourante.getListeCourbe().get(i);
							// Applicaiton des valeurs limites du zoom
							courbe.setMinValeur(curZoomY.getMinValue());
							courbe.setMaxValeur(curZoomY.getMaxValue());
						    }
						}
						}
					}
				}
			}
			}
		    }
		    
		} else {
			//If main zoom was set, reset the min and max curves values
			//to the domain values
			List<Courbe> courbes = GrapheGUI.this.grapheCourante.getListeCourbe();
			for(Courbe courbe: courbes) {
				courbe.setMinValeur(courbe.getMinDomainValeur());
				courbe.setMaxValeur(courbe.getMaxDomainValeur());
			}
		}
		//force initializing this here as we have the H scroll as
		//listener to events ... it needs the updated X axe if a H 
		//scroll occured
		GestionnaireAxes.getInstance().initialiserAxe(width);

		if (InfoComposite.visibleEventRecord()) 
			Curseur.getInstance().setCurseurVisible(true);
		else
			Curseur.getInstance().setCurseurVisible(false);

		forceRedraw();



		try {
			Curseur.getInstance().setAddCursorAfterRedraw(true);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	/**
	 * Handles a escape key pressed event.
	 * If the user is currently performing a zoom, the zoom rectangle
	 * is erased and the arrow mouse cursor is presented
	 *
	 */
	private void onEscapePressed() {
		if(zoomPoint != null || zoomRectangle != null) {
			AnnulerZoom.getInstance().setZoomAnnule(true);
			zoomPoint = null;
			zoomRectangle = null;
			redraw();
		}
	}

	private class GraphicMouseListener implements MouseListener {
		public void mouseDoubleClick(MouseEvent e) {
			if(e.button == 1) {
				if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
						.equals(PreferenceConstants.MOUSE_SYNC_DOUBLE_CLICK)) {
					fireCursorPositionChanged(e.x, true);
					zoomPoint = null;
					zoomRectangle = null;
				}
			}
		}

		public void mouseDown(MouseEvent e) {
			if 	(e.button == 1 ) {
				GrapheGUI.this.timeClic=System.currentTimeMillis();

				int x = e.x;
				int y = e.y;

				if(x < MARGE_LATERALE)
					return;

				if (x > width-MARGE_LATERALE)
					return;

				if(grapheCourante.getTypeGraphe() == TypeGraphe.DIGITAL) {
					y = 0;
				} else {
					if(y > height - MARGE_BAS)
						y = height - MARGE_BAS;
					else if (y < MARGE_HAUT)
						y = MARGE_HAUT;
				}
				zoomPoint = new Point(x, y);
				zoomState = ZOOM_STATE.ZOOM_X; 
			}else if (e.button==3) {
				ActivatorVueGraphique.getDefault().getConfigurationMng();
				
			}
		}

		public void mouseUp(MouseEvent e) {
			if(e.button == 3) {	//right button double click

			} else if(e.button == 1 ) {

				if(zoomRectangle != null) {
					if((zoomState == ZOOM_STATE.ZOOM_X && zoomRectangle.width > ZOOM_MINIMUM_SIZE) || 
							(zoomState == ZOOM_STATE.ZOOM_Y && zoomRectangle.height > ZOOM_MINIMUM_SIZE))
						zoom(zoomRectangle, zoomState);
					else {
						//just redraw to remove the rectangle
						redraw();
					}
				} else {
					if (!AnnulerZoom.getInstance().isZoomAnnule()) {
						fireCursorPositionChanged(e.x, false);
						if (Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.MOUSE_SYNC_CHOICE)
								.equals(PreferenceConstants.MOUSE_SYNC_SINGLE_CLICK)) {
							fireCursorPositionChanged(e.x, true);
						} 
					} else {
						AnnulerZoom.getInstance().setZoomAnnule(false);
					}
				}		
			}
			zoomPoint = null;
			zoomRectangle = null;
		}
	}

	private class GraphicMouseMoveListener implements MouseMoveListener {
		public void mouseMove(MouseEvent e) {
			if (GrapheGUI.this!=null && GrapheGUI.this.timeClic!=null &&
					System.currentTimeMillis()>GrapheGUI.this.timeClic+300) {

				if(zoomPoint != null) {
					int zoomPointX = zoomPoint.x;
					int zoomPointY = zoomPoint.y;
					int x = e.x;
					int y = e.y;

					if(x < MARGE_LATERALE)
						x = MARGE_LATERALE;
					else if (x > width - MARGE_LATERALE)
						x = width - MARGE_LATERALE;
					int rectWidth = Math.abs(zoomPointX - x);
					if(zoomPointX > x) {
						zoomPointX = x;
					}
					int rectHeight;
					if(grapheCourante.getTypeGraphe() == TypeGraphe.DIGITAL) {
						zoomState = ZOOM_STATE.ZOOM_X;
						rectHeight = GrapheGUI.this.height;
					} else {
						if(y > height - MARGE_BAS)
							y = height - MARGE_BAS;
						else if (y < MARGE_HAUT)
							y = MARGE_HAUT;

						rectHeight = Math.abs(zoomPointY - y);
						if(zoomPointY > y) {	//the rectangle should be drawn up
							zoomPointY = y;
						}
						if(rectWidth > rectHeight) {	
							zoomState = ZOOM_STATE.ZOOM_X;
							rectHeight = GrapheGUI.this.height;
							zoomPointY = 0;
						} else {
							zoomState = ZOOM_STATE.ZOOM_Y;
							rectWidth = GrapheGUI.this.width;
							zoomPointX = 0;
						}
					}
					zoomRectangle = new Rectangle(zoomPointX, zoomPointY, rectWidth, rectHeight);
					redraw();
				}
			}
		}
	}

	public void run() {

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
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public Graphe getGrapheCourante() {
		return grapheCourante;
	}

	public void setGrapheCourante(Graphe grapheCourante) {
		this.grapheCourante = grapheCourante;
	}
}