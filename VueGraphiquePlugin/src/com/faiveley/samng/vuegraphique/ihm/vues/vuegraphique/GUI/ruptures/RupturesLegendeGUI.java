package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.ihm.vues.vuesfiltre.GestionnaireCouleurs;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Messages;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GestionnaireGraphesNotifications;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.IGrapheCursorListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeXUtil;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class RupturesLegendeGUI extends Canvas implements IGrapheCursorListener {
	private static final int INFO_V_SPACING = 15;
	private static final int INFO_LINE_WIDTH = 20;

	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;
	private Composite drawingAreaComposite;
	private Composite topComposite;


	private String[] variablesValues;

	private Color[] infoColors;

	public RupturesLegendeGUI(Composite parent, int style) {
		super(parent, style);
		initialize();

		drawingAreaComposite.addListener(SWT.Paint,new Listener(){ 
			public void handleEvent(Event event) { 
				//fill with the white background
				if(ActivatorVueGraphique.getDefault().getConfigurationMng().isFond_blanc()) {
					int width = Math.max(getBounds().width, computeMaxTextWidth(event));
					int height = Math.max(getBounds().height, INFO_V_SPACING * 6);
					event.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
					event.gc.fillRectangle(0, 0, width, height);
				}

				if((AxeXUtil.getSegments(TypeAxe.AXE_TEMPS) != null && AxeXUtil.getSegments(TypeAxe.AXE_TEMPS).size() > 1) || 
						(AxeXUtil.getSegments(TypeAxe.AXE_DISTANCE) != null && AxeXUtil.getSegments(TypeAxe.AXE_DISTANCE).size() > 1)) {
					drawInfo(event);
				}
			}
		});

		vScrollBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Point location = drawingAreaComposite.getLocation();
				location.y = -vScrollBar.getSelection();
				drawingAreaComposite.setLocation(location);
			}
		});

		hScrollBar.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				Point location = drawingAreaComposite.getLocation();
				location.x = -hScrollBar.getSelection();
				drawingAreaComposite.setLocation(location);
			}
		});

		drawingAreaComposite.addListener(SWT.Resize, new Listener() {
			public void handleEvent(Event e) {
				Point size = drawingAreaComposite.getSize();
				Rectangle rect = getClientArea();
				hScrollBar.setMaximum(size.x);
				vScrollBar.setMaximum(size.y);
				hScrollBar.setThumb(Math.min(size.x, rect.width));
				vScrollBar.setThumb(Math.min(size.y, rect.height));
				int hPage = size.x - rect.width;
				int vPage = size.y - rect.height;
				int hSelection = hScrollBar.getSelection();
				int vSelection = vScrollBar.getSelection();
				Point location = drawingAreaComposite.getLocation();
				if (hSelection >= hPage) {
					if (hPage <= 0)
						hSelection = 0;
					location.x = -hSelection;
				}
				if (vSelection >= vPage) {
					if (vPage <= 0)
						vSelection = 0;
					location.y = -vSelection;
				}
				drawingAreaComposite.setLocation(location);
			}
		});



		drawingAreaComposite.addMouseTrackListener(new MouseTrackAdapter() {
			Shell tip = null;
			Label label = null;
			Rectangle rec;
			String libelle="";
			int nbLegende=3;
			int dimss=2;
			int x=70;
			int y=12;
			public void mouseHover(MouseEvent e) {

				if (RupturesLegendePosition.getInstance().isMutex()) {

					int tab[]=RupturesLegendePosition.getInstance().getTabArray()[RupturesLegendePosition.getInstance().getNumLeg()];
					this.rec=new Rectangle(tab[0],tab[1],tab[0]+x,tab[1]+y);					
					
					if (e.x<INFO_LINE_WIDTH || e.x>drawingAreaComposite.getSize().x-15) {
						RupturesLegendePosition.getInstance().setMutex(false);
						this.libelle="";
						this.label.setVisible(false);
		            	this.tip.setVisible(false);
					}					
					
					if ((	(e.x>rec.x) || (e.x<rec.x+rec.width) || (e.y>rec.y) || (e.y<rec.y+rec.height) )  ) {
						RupturesLegendePosition.getInstance().setMutex(false);
						this.libelle="";
						this.label.setVisible(false);
		            	this.tip.setVisible(false);
					}		          
				}
				
				if (!RupturesLegendePosition.getInstance().isMutex()) {

					int[][]liste=new int[nbLegende][dimss];
					liste=RupturesLegendePosition.getInstance().getTabArray();

					for (int i = 0; i < nbLegende; i++) {

						int [] dims=new int[4];
						dims[0]=liste[i][0];
						dims[1]=liste[i][1];
						dims[2]=x;
						dims[3]=y;

						if (e.x>dims[0] && e.x<dims[0]+dims[2] && e.y>dims[1] && e.y<dims[1]+dims[3]) {

							String text=RupturesLegendePosition.getInstance().getLibelle()[i];
							this.rec=new Rectangle(dims[0],dims[1],dims[2],dims[3]);
							RupturesLegendePosition.getInstance().setMutex(true);
							RupturesLegendePosition.getInstance().setNumLeg(i);
							this.libelle=text;
							x=text.length()*6;
							Display display = RupturesLegendeGUI.this.drawingAreaComposite.getDisplay(); 

							int x1=e.display.getClientArea().x+10;
							int y1=e.display.getClientArea().y+18;
							int x2=text.length()*6;
							int y2=dims[3]+5;

							this.tip = new Shell(display	//getActiveShell()
									, SWT.ON_TOP | SWT.NO_FOCUS
									| SWT.TOOL);
							this.tip.setBackground(display
									.getSystemColor(SWT.COLOR_INFO_BACKGROUND));
							FillLayout layout = new FillLayout();
							layout.marginWidth = 2;
							this.tip.setLayout(layout);														
							this.label = new Label(this.tip, SWT.NONE);
							this.label.setForeground(display
									.getSystemColor(SWT.COLOR_INFO_FOREGROUND));
							this.label.setBackground(display
									.getSystemColor(SWT.COLOR_INFO_BACKGROUND));

							this.label.setText(text);
							this.tip.setBounds(x1,y1,x2, y2);
							this.tip.setVisible(true);	

							break;					
						}	
					}
				}
			}
			@Override
			public void mouseExit(MouseEvent e) {
				if (RupturesLegendePosition.getInstance().isMutex()){	
					//if (e.x<INFO_LINE_WIDTH || e.x>drawingAreaComposite.getSize().x-15 || e.y<2 || e.y>drawingAreaComposite.getSize().y-2) {
					RupturesLegendePosition.getInstance().setMutex(false);
					this.libelle="";
					this.label.setVisible(false);
					this.tip.setVisible(false);
					//}
					super.mouseExit(e);
				}
			}
		});

		GestionnaireGraphesNotifications.addGrapheCursorListener(this);		
	}

	private void initialize() {
		GridLayout thisLayout = new GridLayout(1, false);
		thisLayout.makeColumnsEqualWidth = true;
		thisLayout.horizontalSpacing = 0;
		thisLayout.marginWidth = 0;
		setLayout(thisLayout);

		//We create first a top composite that has a H_Scroll
		//we do not put the scroll in the this class as we need to draw 
		//cadre outside the scoll and not inside
		GridData topCompositeLData = new GridData();
		topCompositeLData.verticalAlignment = GridData.FILL;
		topCompositeLData.horizontalAlignment = GridData.FILL;
		topCompositeLData.grabExcessHorizontalSpace = true;
		topCompositeLData.grabExcessVerticalSpace = true;
		topComposite = new Composite(this,  SWT.H_SCROLL | SWT.V_SCROLL);
		GridLayout topCompositeLayout = new GridLayout();
		topCompositeLayout.makeColumnsEqualWidth = true;
		topCompositeLayout.verticalSpacing = 0;
		topCompositeLayout.marginHeight = 0;
		topCompositeLayout.horizontalSpacing = 0;
		topCompositeLayout.marginWidth = 0;
		topComposite.setLayout(topCompositeLayout);
		topComposite.setLayoutData(topCompositeLData);

		drawingAreaComposite = new Composite(topComposite, SWT.NONE);
		GridLayout drawingAreaCompositeLayout = new GridLayout();
		drawingAreaCompositeLayout.makeColumnsEqualWidth = true;
		drawingAreaCompositeLayout.verticalSpacing = 0;
		drawingAreaCompositeLayout.marginHeight = 0;
		drawingAreaCompositeLayout.horizontalSpacing = 0;
		drawingAreaCompositeLayout.marginWidth = 0;

		drawingAreaComposite.setLayout(drawingAreaCompositeLayout);
		GridData drawingAreaCompositeLData = new GridData();
		drawingAreaCompositeLData.horizontalAlignment = GridData.FILL;
		drawingAreaCompositeLData.verticalAlignment = GridData.FILL;
		drawingAreaCompositeLData.grabExcessVerticalSpace = true;
		drawingAreaCompositeLData.grabExcessHorizontalSpace = true;
		drawingAreaComposite.setLayoutData(drawingAreaCompositeLData);

		hScrollBar = topComposite.getHorizontalBar();
		hScrollBar.setVisible(false);

		vScrollBar = topComposite.getVerticalBar();
		vScrollBar.setVisible(false);

		infoColors = new Color[3];
		infoColors[0] = getDisplay().getSystemColor(SWT.COLOR_BLACK);
		infoColors[1] = new Color(getDisplay(), GestionnaireCouleurs.getTimeBreaksColor());
		infoColors[2] = new Color(getDisplay(), GestionnaireCouleurs.getDistanceBreaksColor());
	}

	private void drawInfo(Event event) {
		int yOffset = 0;
		String displayText;
		int width = getBounds().width;
		int maxWidth = 0;
		int lineWidth;
		int dim=2;

		variablesValues=new String[]{Messages.getString("RupturesLegendeGUI.0"),  //$NON-NLS-1$
				Messages.getString("RupturesLegendeGUI.1"),  //$NON-NLS-1$
				Messages.getString("RupturesLegendeGUI.2")}; //$NON-NLS-1$

		int tabDim[][]=new int[variablesValues.length][2];
		String libelle[]=new String[variablesValues.length];

		event.gc.setLineWidth(2);
		event.gc.setFont(GestionnaireVueGraphique.getArial09TextFont());


		for(int i = 0; i<variablesValues.length; i++) {
			if ((i==0||i==1) && TableRuptures.getInstance().getListeRupturesTemps().size()>0 ||
					(i==2 && TableRuptures.getInstance().getListeRupturesDistance().size()>0)) {


				event.gc.setForeground(infoColors[i]);
				yOffset = i*INFO_V_SPACING;
				if(i == 0) {
					event.gc.drawLine(0, yOffset + INFO_V_SPACING/2, INFO_LINE_WIDTH /3, yOffset + INFO_V_SPACING/2);
					event.gc.drawLine(2*INFO_LINE_WIDTH/3, yOffset + INFO_V_SPACING/2, INFO_LINE_WIDTH, yOffset + INFO_V_SPACING/2);
				} else {
					event.gc.drawLine(0, yOffset + INFO_V_SPACING/2, INFO_LINE_WIDTH, yOffset + INFO_V_SPACING/2);
				}

				event.gc.setForeground(GestionnaireVueGraphique.getDefaultTextColor());
				displayText = variablesValues[i];
				event.gc.drawString(displayText, INFO_LINE_WIDTH + 5, yOffset);


				tabDim[i][0]=INFO_LINE_WIDTH;
				tabDim[i][1]=yOffset;
				libelle[i]=displayText;

				lineWidth = INFO_LINE_WIDTH + 5 + event.gc.stringExtent(displayText).x;
				if(lineWidth > maxWidth)
					maxWidth = lineWidth;
			}
		}

		RupturesLegendePosition.getInstance().setTabArray(tabDim);
		RupturesLegendePosition.getInstance().setLibelle(libelle);

		yOffset = INFO_V_SPACING * (variablesValues.length + 3);
		if(yOffset > getBounds().height) {
			vScrollBar.setVisible(true);
			drawingAreaComposite.setSize(drawingAreaComposite.getBounds().width, yOffset);				
		} else {
			vScrollBar.setVisible(false);
		}
		maxWidth += 25;	//to cover also the V scroll width
		if(maxWidth > width) {
			hScrollBar.setVisible(true);
			drawingAreaComposite.setSize(maxWidth, drawingAreaComposite.getBounds().height);
		} else {
			hScrollBar.setVisible(false);
		}
	}

	private int computeMaxTextWidth(Event event) {
		int retWidth = getBounds().width;
		int maxWidth = 0;
		int lineWidth;
		String displayText;
		if (variablesValues!=null) {
			for(int i = 0; i<variablesValues.length; i++) {
				displayText = variablesValues[i];
				lineWidth = INFO_LINE_WIDTH + 5 + event.gc.stringExtent(displayText).x;
				if(lineWidth > maxWidth)
					maxWidth = lineWidth;
			}
		}

		retWidth = Math.max(retWidth, maxWidth);
		return retWidth;
	}

	public void cursorPositionChanged(CursorPositionEvent event) {
		redraw();
		drawingAreaComposite.redraw();
	}

	@Override
	public void dispose() {
		for(int i = 1; i<infoColors.length; i++)
			infoColors[i].dispose();
		GestionnaireGraphesNotifications.removeGrapheCursorListener(this);
		super.dispose();
	}

} 
