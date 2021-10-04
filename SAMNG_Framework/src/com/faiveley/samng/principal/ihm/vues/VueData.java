package com.faiveley.samng.principal.ihm.vues;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.widgets.Display;

import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

public class VueData {
	private AParcoursComposant dataTable;
	private AParcoursComposant reperes;
	private List<Integer> markerMsgIds = new ArrayList<Integer>(0);
	
	private static String FONT_NAME = "Tahoma";
    
    protected boolean hasResources;
    
    private Font normalFont;
    private Font boldFont;
    private Font italicFont;
    private Color color;
    private Color repZeroColor;
    private Color markersColor;
    private Color incertitudeColor;
    
    private Message lastRepere0;
    
    protected List<Resource> resources;
    
	
	private List<IMarqueursListener> markersListeners = new ArrayList<IMarqueursListener>(0);

    public VueData() {
    	
    }
    
    protected void createResources() {
    	if (this.resources == null) {
    		this.resources = new ArrayList<Resource>(0);
    	}
    	
		this.normalFont = createFont(SWT.NORMAL);
        this.boldFont = createFont(SWT.BOLD);
        this.italicFont = createFont(SWT.ITALIC);
        this.color = createColor(new RGB(192,192,192));
        this.repZeroColor = createColor(new RGB(255, 255, 0));
        this.markersColor = createColor(new RGB(0, 255, 0));
        this.incertitudeColor = createColor(new RGB(255, 140, 0));
        this.hasResources = true;
	}
    
    /**
	 * Gets the data from the parcours object created by the parser
	 */
	public void loadParcoursData(ParcoursComposite parcours) {
		try {
			this.dataTable = parcours.getData();
			this.reperes = parcours.getReperes();
		} catch (Exception e) {
			System.out.println("error loading Data");
		}
	}
	
	/**
	 * Release the current parcours data
	 */
	public void releaseParcoursData() {
		//if there is any data to release, then do it here
		this.dataTable = null;
		this.reperes = null;
	}
	
	/**.
	 * Creates the font
	 */
	private Font createFont(int style) {
		// Create a font that will display the range
		// of characters. "Terminal" works well in
		// Windows
		Font f = new Font(Display.getCurrent(), FONT_NAME, 8, style);
		this.resources.add(f);
		return f;
	}
	
	/**
	 * Creates the color
	 * @param rgb
	 * @return
	 */
	public Color createColor(RGB rgb) {
		// Create a color that will display the range
		// of characters. "Terminal" works well in
		// Windows
		Color c = new Color(Display.getCurrent(), rgb);
		this.resources.add(c);
		return c;
	}
	
	

	/**
	 * @return the color
	 */
	public Color getColor() {
		return this.color;
	}
	
	public Color getMarkersColor() {
		return this.markersColor;
	}

	public Color getRepZeroColor() {
		return this.repZeroColor;
	}
	
	/**
	 * @return the boldFont
	 */
	public Font getBoldFont() {
		return this.boldFont;
	}

	/**
	 * @return the italicFont
	 */
	public Font getItalicFont() {
		return this.italicFont;
	}

	/**
	 * @return the normalFont
	 */
	public Font getNormalFont() {
		return this.normalFont;
	}
	
	
	
	/**
	 * @return the dataTable
	 */
	public AParcoursComposant getDataTable() {
		if (!this.hasResources) {
			createResources();
		}
		return this.dataTable;
	}
	
	/**
	 * @return the reperes
	 */
	public AParcoursComposant getReperes() {
		if (!this.hasResources) {
			createResources();
		}
		return this.reperes;
	}
	

	public void releaseData() {
		if (this.resources != null) {
			for (Resource f : this.resources) {
				if (!f.isDisposed()) {
					f.dispose();
				}
			}
		}
	}
	
	/**
	 * Markers support
	 */
	public void setMarkerIds(int[] msgIdsArr) {
		int[] oldMarkers = new int[this.markerMsgIds.size()];
		
		//Save first the list of old markers
		for(int i = 0; i<this.markerMsgIds.size(); i++) {
			oldMarkers[i] = this.markerMsgIds.get(i);
		}
		//Update the internal markers list
		this.markerMsgIds.clear();
		if(msgIdsArr != null) {
			////////////////////////////
			ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
			if (p == null) {
				return;
			}
			// get enregisrtrement
			Enregistrement enrg = p.getData().getEnregistrement();
			if (enrg == null) {
				return;
			}
			for(int markerId: msgIdsArr) {
				this.markerMsgIds.add(markerId);
				Message m = enrg.getGoodMessage(markerId);
				if(m!=null){
				Flag flagmsg = m.getFlag();
				Flag fl;
				if (flagmsg != null) {
					if (!(flagmsg.getLabel().contains("A"))) {
						fl = new Flag(markerId, flagmsg.getLabel() + "A", m.getEvenement()
								.getM_ADescripteurComposant()
								.getM_AIdentificateurComposant().getNom());
						m.setFlag(fl);
					}
				} else {
					fl = new Flag(markerId, "A", m.getEvenement()
							.getM_ADescripteurComposant()
							.getM_AIdentificateurComposant().getNom());
					m.setFlag(fl);
				}

				}
				
				////////////////////////
				
				
				
				
			}
		}
		//Notify listeners
		for(IMarqueursListener listener: this.markersListeners) {
			try{
			listener.marquersListeChangement(msgIdsArr, oldMarkers);
			}
			catch(Exception ex){
				
			}
		}
	}
	
	public boolean isMarkerId(int msgId) {
		return this.markerMsgIds.contains(msgId);
	}
	
	public int[] getMarkerMsgIds() {
		int[] msgIdsArr = new int[this.markerMsgIds.size()];
		
		//Save first the list of old markers
		for(int i = 0; i<markerMsgIds.size(); i++) {
			msgIdsArr[i] = this.markerMsgIds.get(i);
		}
		return msgIdsArr;
	}
	
	public void addMarkerId(int msgId) {
		this.markerMsgIds.add(msgId);
		for(IMarqueursListener listener: this.markersListeners) {
			listener.marqueurAjoutee(msgId);
		}
	}
	
	public void removeMarkerId(int msgId) {
		this.markerMsgIds.remove((Object)msgId);
		for(IMarqueursListener listener: this.markersListeners) {
			listener.marqueurEffacee(msgId);
		}
	}
	
	/**
	 * Adds a listener
	 * @param listener
	 */
	public synchronized void addMarkersListener(IMarqueursListener listener) {
		if(listener != null)
			this.markersListeners.add(listener);
	}
	
	/**
	 * Removes a listener
	 * @param listener
	 */
	public synchronized void removeMarkersListener(IMarqueursListener listener) {
		if(listener != null)
			this.markersListeners.remove(listener);	
	}
	
	public boolean isEmpty() {
		return this.dataTable == null;
	}

	/**
	 * @return the lastRepere0
	 */
	public Message getLastRepere0() {
		return this.lastRepere0;
	}

	/**
	 * @param lastRepere0 the lastRepere0 to set
	 */
	public void setLastRepere0(Message lastRepere0) {
		this.lastRepere0 = lastRepere0;
	}

	public Color getIncertitudeColor() {
		return incertitudeColor;
	}

	public void setIncertitudeColor(Color incertitudeColor) {
		this.incertitudeColor = incertitudeColor;
	}
	
	
}
