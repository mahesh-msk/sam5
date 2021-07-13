package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Legende;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
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

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GestionnaireGraphesNotifications;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.IGrapheCursorListener;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.ValuedCursorPositionEvent;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Graphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeGraphe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class LegendeGUI extends Canvas implements IGrapheCursorListener{
	private static final int INFO_V_SPACING = 15;
	private static final int INFO_LINE_WIDTH = 20;
	
	private static int MAX_VARS_COUNT = 10;
	private int numGraphe;
	private Color[] varColors;
	private AVariableComposant[] variables;
	private String[] variablesValues;
	private boolean[] variablesPropagations;
	private ScrollBar hScrollBar;
	private ScrollBar vScrollBar;
	private Composite drawingAreaComposite;
	private Composite topComposite;
	private Graphe graphe;
	
	private boolean usesShortNames;
	
	public LegendeGUI(Composite parent, int style, int numGraphe, boolean usesShortNames) {
		super(parent, style);
		
		this.usesShortNames = usesShortNames;
		this.numGraphe = numGraphe;
		
		initialize();
		
		drawingAreaComposite.addListener(SWT.Paint,new Listener(){ 
        	public void handleEvent(Event event) { 
        		//fill with the white background
        		if(ActivatorVueGraphique.getDefault().getConfigurationMng().isFond_blanc()) {
        			int width = Math.max(getBounds().width, computeMaxTextWidth(event));
        			int height = Math.max(getBounds().height, INFO_V_SPACING * (variables.length + 3));
        			
        			
        			
        			event.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));
        			event.gc.fillRectangle(0, 0, width, height);
        		}

        		drawInfo(event);
        	}
        });

		addListener(SWT.Paint,new Listener(){ 
        	public void handleEvent(Event event) { 
        		drawCadre(event);
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
			int num=LegendeGUI.this.numGraphe;
		
			public void mouseHover(MouseEvent e) {
            
				if (getMutexTooltiptexLeg()) {
					
					ArrayList liste=new ArrayList();
					liste=InfosBullesLegende.getInstance().getListBullesDim();
					int [] dims=new int[4];
					dims=(int[])liste.get(InfosBullesLegende.getInstance().getNumeroVariable());
					this.rec=new Rectangle(dims[0],dims[1],dims[2],dims[3]);					
					
					if (e.x<INFO_LINE_WIDTH || e.x>drawingAreaComposite.getSize().x-15) {
						setMutexTooltiptexLeg(false);
						this.libelle="";
						this.label.setVisible(false);
		            	this.tip.setVisible(false);
					}					
					
					if ((	(e.x>rec.x) || (e.x<rec.x+rec.width) || (e.y>rec.y) || (e.y<rec.y+rec.height) )  ) {
						setMutexTooltiptexLeg(false);
						this.libelle="";
						this.label.setVisible(false);
		            	this.tip.setVisible(false);
					}
		          
				}
				if (!getMutexTooltiptexLeg()) {
								
					int nbVar=InfosBullesLegende.getInstance().getListBullesDim().size();
					ArrayList liste=new ArrayList();
					liste=InfosBullesLegende.getInstance().getListBullesDim();
					for (int i = 0; i < nbVar; i++) {
						if (Integer.parseInt(InfosBullesLegende.getInstance().getListBullesNumGraphe().get(i).toString())==this.num){
							int [] dims=new int[4];
							dims=(int[])liste.get(i);
							if (e.x>dims[0] && e.x<dims[0]+dims[2] && e.y>dims[1] && e.y<dims[1]+dims[3]) {
								
								nbVarbeforethisVar(num);
								
								String value=getValueText(i-nbVarbeforethisVar(num));
								String lib=InfosBullesLegende.getInstance().getListBullesName().get(i).toString();
								String text=value+" "+lib;
								
								this.rec=new Rectangle(dims[0],dims[1],dims[2],dims[3]);
								
								setMutexTooltiptexLeg(true);
								InfosBullesLegende.getInstance().setNumeroVariable(i);
								this.libelle=text;
								Display display = LegendeGUI.this.drawingAreaComposite.getDisplay(); 
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
			}
			@Override
			public void mouseExit(MouseEvent e) {
				if (getMutexTooltiptexLeg()){	
					//if (e.x<INFO_LINE_WIDTH || e.x>drawingAreaComposite.getSize().x-15 || e.y<2 || e.y>drawingAreaComposite.getSize().y-2) {
						setMutexTooltiptexLeg(false);
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

	private int nbVarbeforethisVar(int pos){
		int nbVarPrec=0;
		try {
			Graphe[] gr=new Graphe[0];
			gr=FabriqueGraphe.getGraphes();
			for (int j = 0; j < pos; j++) {
				int nbVARGraphe=0;
				nbVARGraphe=gr[j].getListeCourbe().size();
				
				nbVarPrec=nbVarPrec+nbVARGraphe;
			}
		} catch (Exception e) {
	
		}
		return nbVarPrec;
	}
	
	private boolean getMutexTooltiptexLeg(){
    	
    	try{
    		ActivatorData.getInstance().getPoolDonneesVues().get("");
    		if (ActivatorData.getInstance().getPoolDonneesVues().get("mutex_tooltip_annotation_legende").toString().equals("false"))
				return false;
			else
				return true;
    	}catch (Exception e) {
			return false;
		}
    
    }
    
    private void setMutexTooltiptexLeg(boolean bool){
    	if (bool) 
    		ActivatorData.getInstance().getPoolDonneesVues().put("mutex_tooltip_annotation_legende","true");
    	else
    	ActivatorData.getInstance().getPoolDonneesVues().put("mutex_tooltip_annotation_legende","false");
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

		graphe = FabriqueGraphe.getGraphe(numGraphe);
		if(graphe != null) {
			if(varColors != null) {
				for(int i = 0; i<varColors.length; i++)
					varColors[i].dispose();
			}
			List<Courbe> courbes = graphe.getListeCourbe();
			int courbesNr = courbes.size() < MAX_VARS_COUNT ? courbes.size() : MAX_VARS_COUNT;
			Courbe courbe;
			RGB rgb;
			variables = new AVariableComposant[courbesNr];
			varColors = new Color[courbesNr];
			for(int i = 0; i<courbesNr; i++) {
				courbe = courbes.get(i);
				variables[i] = courbe.getVariable();
				rgb = courbe.getCouleur();				
				varColors[i] = new Color(getDisplay(), rgb.red, rgb.green, rgb.blue); 
			}
			for (int i = 0; i < variables.length; i++) {
				InfosBullesLegende.getInstance().getListBullesNumGraphe().add(this.numGraphe);
			}
		}
	}
	
    private void drawCadre(Event event) {
        event.gc.setLineWidth(2);
        event.gc.setForeground(event.display.getSystemColor(SWT.COLOR_DARK_GRAY));
        event.gc.drawLine(0, getBounds().height - 1, getBounds().width, getBounds().height - 1);
    }       
    	       
    private void drawInfo(Event event) {
		if(variables != null) {
			int yOffset = 0;
			String displayText;
			String valueText;
			Point valueTextExtent;
			int width = getBounds().width;
			int maxWidth = 0;
			int lineWidth;
			event.gc.setLineWidth(2);
			boolean exist=false;
			
			for(int i = 0; i<variables.length; i++) {
				if(variables[i].getDescriptor().getM_AIdentificateurComposant().getNom().equals("vitesse_corrigee")
					&& !TableSegments.getInstance().isAppliedDistanceCorrections()	){
					continue;
				}
				
				event.gc.setForeground(varColors[i]);
				yOffset = i*INFO_V_SPACING;
				event.gc.drawLine(0, yOffset + INFO_V_SPACING/2, INFO_LINE_WIDTH, yOffset + INFO_V_SPACING/2);
				event.gc.setForeground(GestionnaireVueGraphique.getDefaultTextColor());
				if(variablesPropagations != null && variablesPropagations[i]) {
					event.gc.setFont(GestionnaireVueGraphique.getDefaultItalicTextFont());
				}else{					
					event.gc.setFont(GestionnaireVueGraphique.getDefaultTextFont());
				}
				
				
				int[]tabval=new int[4];
				tabval[0]=INFO_LINE_WIDTH;
				tabval[1]=yOffset+3;
				tabval[2]=width-20;
				tabval[3]=INFO_V_SPACING-4;
				
				
				valueText = getValueText(i);
				//event.gc.drawRectangle(INFO_LINE_WIDTH, yOffset+3, width-20, INFO_V_SPACING-4);
				event.gc.drawString(valueText, INFO_LINE_WIDTH + 5, yOffset);
				
				
				//event.gc.setFont(GestionnaireVueGraphique.getArial09TextFont());
				valueTextExtent = event.gc.stringExtent(valueText);
				
				displayText = getDescriptionForVariable(i, this.usesShortNames);
				
				//setToolTipText(displayText);
				event.gc.drawString(displayText, INFO_LINE_WIDTH + 5 + valueTextExtent.x, yOffset);
				ArrayList<String> tabname=new ArrayList<String>();
				tabname=InfosBullesLegende.getInstance().getListBullesName();
				exist=false;
				for (int j = 0; j < tabname.size(); j++) {
					if (tabname.get(j).toString().equals(displayText))
						exist=true;
				}
				if (!exist) {
					String infoBulleDisplayText = getDescriptionForVariable(i, false);
					InfosBullesLegende.getInstance().getListBullesName().add(infoBulleDisplayText);
					InfosBullesLegende.getInstance().getListBullesDim().add(tabval);
				}
				
				lineWidth = INFO_LINE_WIDTH + 5 + valueTextExtent.x + event.gc.stringExtent(displayText).x;
				if(lineWidth > maxWidth)
					maxWidth = lineWidth;
			}
			yOffset = INFO_V_SPACING * (variables.length + 3);
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
    }
    
    private int computeMaxTextWidth(Event event) {
    	int retWidth = getBounds().width;
		if(variables != null) {
			int maxWidth = 0;
			int lineWidth;
			Point valueTextExtent;
			String displayText;
			String valueText;
			for(int i = 0; i<variables.length; i++) {
				valueText = getValueText(i);
				valueTextExtent = event.gc.stringExtent(valueText);
				displayText = getDescriptionForVariable(i, this.usesShortNames);
				lineWidth = INFO_LINE_WIDTH + 5 + valueTextExtent.x + event.gc.stringExtent(displayText).x;
				if(lineWidth > maxWidth)
					maxWidth = lineWidth;
			}
			retWidth = maxWidth;
		}
		return retWidth;
    }
    
    private String getValueText(int varIdx) {
		String descr = "";
		if(variablesValues != null && varIdx >= 0 && varIdx < variablesValues.length) {
		    if(variablesValues[varIdx] != null){
			descr = "[" + variablesValues[varIdx] + "] ";			
		    }
		}
		return descr;
    }
    
	private String getDescriptionForVariable(int varIdx, boolean usesShortNames) {
		String descr = "";
		AVariableComposant variable = variables[varIdx];
		//: NomUtilisateur should be used here
		DescripteurVariable descrVar = variable.getDescriptor();
		Langage curLang = Activator.getDefault().getCurrentLanguage();
		String name = descrVar.getNomUtilisateur().getNomUtilisateur(curLang);	//descrVar.getM_AIdentificateurComposant().getNom();
		if(name == null)
			name = descrVar.getM_AIdentificateurComposant().getNom();
		
		// If we use short names then we only take what is after the separator
		if (usesShortNames) {
			name = Util.getInstance().getNomCourtFromNomUtilisateur(name);
		}
		
		descr += name;
		if(descrVar instanceof DescripteurVariableAnalogique) {
			descr += " (" + ((DescripteurVariableAnalogique)descrVar).getUnite() + ")";
		}
		return descr;
	}
	
	
	
	public void cursorPositionChanged(CursorPositionEvent event) {
		Integer intVal;
		if(event instanceof ValuedCursorPositionEvent && event.getSourceGrapheNr() == numGraphe) {
			ValuedCursorPositionEvent evt = (ValuedCursorPositionEvent)event;
			variablesValues = new String[variables.length];
			variablesPropagations = new boolean[variables.length];
			int indiceInverse=0;
			for(int i = 0; i<variablesValues.length; i++) {
				if (graphe.getTypeGraphe()== TypeGraphe.DIGITAL){
					indiceInverse=variablesValues.length-1-i;
				}else{
					indiceInverse=i;
				}
				
				try {
					if(evt.getCmvvalues()[indiceInverse] == null || evt.getCmvvalues()[indiceInverse].getValue() == null)
						variablesValues[i] = "###";
					else {
						//display as integer if we have nothing after separator
						intVal = evt.getCmvvalues()[indiceInverse].getValue().intValue();
						
						if(evt.getCmvvalues()[indiceInverse].getValue() - intVal == 0.0)
							variablesValues[i] = String.valueOf(intVal);
						else
							variablesValues[i] = String.valueOf((Float)evt.getCmvvalues()[indiceInverse].getValue());
						
						variablesPropagations[i] = evt.getCmvvalues()[indiceInverse].isPropagated();
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.print("ArrayIndexOutOfBoundsException");
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
			InfosBullesLegende.getInstance().getListBullesDim().clear();
			//InfosBullesLegende.getInstance().getListBullesNumGraphe().clear();
			InfosBullesLegende.getInstance().getListBullesName().clear();
			redraw();
			drawingAreaComposite.redraw();
		}
		
	}
	
	@Override
	public void dispose() {
		GestionnaireGraphesNotifications.removeGrapheCursorListener(this);
		super.dispose();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
