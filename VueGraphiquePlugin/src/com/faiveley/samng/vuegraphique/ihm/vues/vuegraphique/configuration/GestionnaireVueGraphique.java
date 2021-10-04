package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration;
import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.vues.configuration.AGestionnaireConfigurationVue;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeMode;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;
import com.faiveley.samng.vuegraphique.sm.parseurs.ParseurConfigurationVueGraphique;

/**
 * @author olivier
 * @version 1.0
 * @created 04-févr.-2008 11:40:58
 */
public class GestionnaireVueGraphique extends AGestionnaireConfigurationVue implements IDataChangedListener {
	private static String FONT_NAME = "Tahoma"; //$NON-NLS-1$
	private static String FONT_ARIAL = "Arial"; //$NON-NLS-1$
	private static Font defaultTextFont;
	private static Font defaultItalicTextFont;
	private static Font defaultBoldTextFont;
	private static Font Arial_07;
	private static Font Arial_08;
	private static Font Arial_09;
	private static Font Arial_10;
	
	private static RGB rgbTextColor = new RGB(102, 102, 153);
	private static Color defaultTextColor;
	
	private ParseurConfigurationVueGraphique parserCfg;
	private boolean isChanged = false;
	
	/**
	 * défini si on affiche les annotations
	 * 
	 */
	private boolean annotations;
	/**
	 * défini l'axe affiché:temps ou distance
	 */
	private TypeAxe axe = TypeAxe.AXE_DISTANCE;
	/**
	 * défini si on effectue l'échantillonage
	 */
	private boolean echantillonnage;
	private boolean fond_blanc = false;
	/**
	 * défini si la légende est affichée
	 */
	private boolean legende = true;
	/**
	 * défini si on affiche en marches d'escalier
	 */
	private boolean marches_escalier = true;
	/**
	 * défini si on affiche les marqueurs
	 */
	private boolean marqueurs = true;
	/**
	 * défini le mode d'affichage parmi deux choix: ligne ou point
	 */
	private TypeMode mode = TypeMode.LINE;
	/**
	 * défini si on affiche la ligne de référence zéro des signaux digitaux
	 */
	private boolean ref_zero_digit = true;
	/**
	 * défini si on affiche les ruptures de distances
	 */
	private boolean ruptures_distance = true;
	/**
	 * défini si on affiche les ruptures de temps
	 */
	private boolean ruptures_temps = true;

	public GestionnaireVueGraphique() {
		resetToDefaultValues();
	}

	public TypeAxe getAxe(){
		return axe;
	}

	public TypeMode getMode(){
		return mode;
	}

	public boolean isAnnotations(){
		return annotations;
	}

	public boolean isEchantillonnage(){
		return echantillonnage;
	}

	public boolean isFond_blanc(){
		return fond_blanc;
	}

	public boolean isLegende(){
		return legende;
	}

	public boolean isMarches_escalier(){
		return marches_escalier;
	}

	public boolean isMarqueurs(){
		return marqueurs;
	}

	/**
	 * défini si on affiche la ligne de référence zéro des signaux digitaux
	 */
	public boolean isRef_zero_digit(){
		return ref_zero_digit;
	}

	/**
	 * défini si on affiche les ruptures de temps
	 */
	public boolean isRuptures_temps(){
		return this.ruptures_temps;
	}

	/**
	 * défini si on affiche les ruptures de distances
	 */
	public boolean isRuptures_distance() {
		return ruptures_distance;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setAnnotations(boolean newVal) {
		if(newVal != annotations) {
			annotations = newVal;
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setAxe(TypeAxe newVal) {
		if(newVal != GestionnaireAxes.getInstance().getCurrentAxeX().m_TypeAxe) {
			axe = newVal;
			GestionnaireAxes.getInstance().setCurrentAxeType(axe);
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setEchantillonnage(boolean newVal) {
		if(newVal != echantillonnage) {
			echantillonnage = newVal;
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFond_blanc(boolean newVal) {
		if(newVal != fond_blanc) {
			fond_blanc = newVal;
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setLegende(boolean newVal) {
		if(newVal != legende) {
			legende = newVal;
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setMarches_escalier(boolean newVal) {
		if(newVal != marches_escalier) {
			marches_escalier = newVal;
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setMarqueurs(boolean newVal) {
		if(newVal != marqueurs) {
			marqueurs = newVal;
			isChanged = true;
		}
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setMode(TypeMode newVal) {
		if(newVal != mode) {
			mode = newVal;
			isChanged = true;
		}
	}

	/**
	 * défini si on affiche la ligne de référence zéro des signaux digitaux
	 * 
	 * @param newVal
	 */
	public void setRef_zero_digit(boolean newVal) {
		if(newVal != ref_zero_digit) {
			ref_zero_digit = newVal;
			isChanged = true;
		}
	}

	/**
	 * défini si on affiche les ruptures de temps
	 * 
	 * @param newVal
	 */
	public void setRupture_temps(boolean newVal) {
		if(newVal != ruptures_temps) {
			ruptures_temps = newVal;
			isChanged = true;
		}
	}

	/**
	 * défini si on affiche les ruptures de distances
	 * 
	 * @param newVal
	 */
	public void setRuptures_distances(boolean newVal) {
		if(newVal != ruptures_distance) {
			ruptures_distance = newVal;
			isChanged = true;
		}
	}
	
	public static Font getArial10TextFont() {
		if(Arial_10 == null)
			Arial_10 = new Font(Display.getCurrent(), FONT_ARIAL, 10, SWT.NORMAL);
		return Arial_10;
    }
	
	public static Font getArial09TextFont() {
		if(Arial_09 == null)
			Arial_09 = new Font(Display.getCurrent(), FONT_ARIAL,  9, SWT.NORMAL);
		return Arial_09;
    }
	
	public static Font getArial08TextFont() {
		if(Arial_08 == null)
			Arial_08 = new Font(Display.getCurrent(), FONT_ARIAL,  8, SWT.NORMAL);
		return Arial_08;
    }
	
	public static Font getArial07TextFont() {
		if(Arial_07 == null)
			Arial_07 = new Font(Display.getCurrent(), FONT_ARIAL,  7, SWT.NORMAL);
		return Arial_07;
    }
	
    public static Font getDefaultTextFont() {
		if(defaultTextFont == null)
			defaultTextFont = new Font(Display.getCurrent(), FONT_NAME, 10, SWT.NORMAL);
		return defaultTextFont;
    }

    public static Font getDefaultItalicTextFont() {
		if(defaultItalicTextFont == null)
			defaultItalicTextFont = new Font(Display.getCurrent(), FONT_NAME, 9, SWT.ITALIC);
		return defaultItalicTextFont;
    }
    
    public static Font getDefaultBoldTextFont() {
		if(defaultBoldTextFont == null)
			defaultBoldTextFont = new Font(Display.getCurrent(), FONT_NAME, 9, SWT.BOLD);
		return defaultBoldTextFont;
    }

    public static Color getDefaultTextColor() {
		if(defaultTextColor == null)
			defaultTextColor= new Color(Display.getCurrent(), rgbTextColor.red, rgbTextColor.green, rgbTextColor.blue);
		return defaultTextColor;
    }
	
	@Override
	public void setFiltreApplique(String newVal) {
		if(newVal == this.filtreApplique)
			return;
		boolean setNewFilter = false;
		if(newVal != null && !newVal.equals(this.filtreApplique)) {
			setNewFilter = true;
		} else {
			if(!this.filtreApplique.equals(newVal))
				setNewFilter = true;
		}
		if(setNewFilter) {
			super.setFiltreApplique(newVal);
			this.isChanged = true;
		}
	}

	private void resetToDefaultValues() {
		isChanged = false;
		
		filtreApplique = null;

		annotations = false;
		axe = TypeAxe.AXE_DISTANCE;
		
		echantillonnage = false;
		fond_blanc = false;
		legende = true;
		marches_escalier = true;
		marqueurs = true;
		mode = TypeMode.LINE;
		ref_zero_digit = true;
		ruptures_distance = true;
		ruptures_temps = true;
	}
	
	public void onDataChange() {
		loadFromFile(RepertoiresAdresses.configurationvuesgraphiques_CFG, "Vue Graphique");
		GestionnaireFiltresGraphique filtersMng = (GestionnaireFiltresGraphique)ActivatorVueGraphique.getDefault().
													getFiltresProvider().getGestionnaireFiltres();
		//If the applied filter does not exists in the filters manager loaded filters
		//then do not apply the filter
		AFiltreComposant appliedFilter = filtersMng.getFiltre(this.filtreApplique);
		if(this.filtreApplique != null) {
			if(appliedFilter == null)
				this.filtreApplique = null;
		}
		if(appliedFilter == null) {
			ActivatorVueGraphique.getDefault().getFiltresProvider().setAppliedFilterName(null);
		} else {
			ActivatorVueGraphique.getDefault().getFiltresProvider().setAppliedFilterName(appliedFilter.getNom());
		}

		filtersMng.setFiltreCourant(appliedFilter);
	}
	
	private void loadFromFile(String vueSufix, String viewName) {
		ParseurXML1 p = GestionnairePool.getInstance().getXMLParser();
		String xmlFileName = p != null ? p.getXmlFileName() : null;
		if (xmlFileName != null) {
			File file = new File(xmlFileName);
			String fileName = file.getName();
			int dotPos;
			if((dotPos = fileName.indexOf('.')) != -1)
				fileName = fileName.substring(0, dotPos);
			String fullCfgFileName = RepertoiresAdresses.getConfigurationVues() + fileName + vueSufix;
			//do not make anything if is the same xml file name
			if(this.parserCfg != null && fullCfgFileName.equals(this.parserCfg.getLastParsedFileName()))
				return;
			checkForSave(viewName);
			this.parserCfg = new ParseurConfigurationVueGraphique();
			try {
				this.parserCfg.parseRessource(fullCfgFileName,false,0,-1);
				GestionnaireVueGraphique cfgView = (GestionnaireVueGraphique)this.parserCfg.chargerConfigurationVue();
				init(cfgView);
			} catch(Exception e) {
				init(null);
			} finally {
				isChanged = false;
			}
		}
	}
	
	public void checkForSave(String viewName) {
		if(this.parserCfg != null && this.isChanged) {
//			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_QUESTION | SWT.YES | SWT.NO);
//			msgBox.setText(viewName + Messages.getString("GestionnaireVueGraphique.4")); //$NON-NLS-1$
//			msgBox.setMessage(Messages.getString("GestionnaireVueGraphique.5") + viewName + "?"); //$NON-NLS-1$ //$NON-NLS-2$
//			int ret = msgBox.open();
//			if(ret == SWT.YES) {
				this.parserCfg.enregistrerConfigurationVue(this);
				this.isChanged = false;
//			}
		}
	}
	
	/**
	 * Initializes this gestionnaire from another gestionnaire
	 * 
	 * @param cfg
	 */
	private void init(GestionnaireVueGraphique cfg) {
		//First reset the configuration
		resetToDefaultValues();
		if(cfg != null) {
			filtreApplique = cfg.filtreApplique;

			annotations = cfg.annotations;
			axe = cfg.axe;
			echantillonnage = cfg.echantillonnage;
			fond_blanc = cfg.fond_blanc;
			legende = cfg.legende;
			marches_escalier = cfg.marches_escalier;
			marqueurs = cfg.marqueurs;
			mode = cfg.mode;
			ref_zero_digit = cfg.ref_zero_digit;
			ruptures_distance = cfg.ruptures_distance;
			ruptures_temps = cfg.ruptures_temps;
			nomsCourts = cfg.nomsCourts;
		}
	}
	
	@Override
	public void setUsesShortNames(boolean value) {
		if (this.nomsCourts != value) {
			this.nomsCourts = value;
			this.isChanged = true;
		}
	}

}