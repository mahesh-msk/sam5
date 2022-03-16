package com.faiveley.samng.principal.ihm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.profil.GestionnaireProfil;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.progbar.ValeurProgBar;
import com.faiveley.samng.principal.ihm.vues.InstancePresentationVueDetaillee;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.VueProgressBar;
import com.faiveley.samng.principal.ihm.vues.VueWaitBar;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.configuration.GestionnaireVueMarqueurs;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.VbvsProvider;
import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.corrections.GestionnaireCorrection;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.enregistrement.tom4.GestionnaireSynchronisationGroupes;
import com.faiveley.samng.principal.sm.data.flag.GestionnaireFlags;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireBaseFiltres;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activator.ActivatorDataExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.data.FabriqueParcoursExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.data.UtilExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireCorrectionExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireDescripteursExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireFlagsExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireLongueurMessageExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireLongueurPacketExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnairePoolExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireProfilExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireSynchronisationGroupesExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireVueMarqueursExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML.ParseurXML1Explorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML.ParseurXML_JRUExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML.ParseurXML_NG_UK_ATESS_Explorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurXML.TypeParseurExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurCorrectionsExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurCouleursExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurFlagsExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurJRUTableAssociationEvVarsExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursAtessExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursJRUExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursSamngExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomDISExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomHSBCExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomUkExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurTableAssociationEvVarsATESSExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.progbar.VueProgressBarExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.progbar.VueWaitBarExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.tables.TableSegmentsExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.tables.TablesRupturesExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.vueliste.InstancePresentationVueDetailleeExplorer;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ParseurCorrections;
import com.faiveley.samng.principal.sm.parseurs.ParseurCouleurs;
import com.faiveley.samng.principal.sm.parseurs.ParseurFlags;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXMLJRU;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML_NG_UK_ATESS;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurTableAssociationEvVars;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.GestionnaireLongueurMessage;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.GestionnaireLongueurPacket;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurJRUTableAssociationEvVars;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;

/** The activator class controls the plug-in life cycle. */
public class Activator extends AbstractUIPlugin {
	/** The plug-in ID. */
	public static final String PLUGIN_ID = "SAMNG";
	
	/** The shared instance. */
	private static Activator plugin;

	private Langage currentLanguage;

	/** Perspectives list */
	private IPerspectiveDescriptor[] perspectives;

	/** The constructor. */
	public Activator() {
		if (plugin != null) {
			return;
		}

		plugin = this;
		
		initRepertoireINSTALL_SAM_PARAM();

		ActivatorData.getInstance().setVp(new ValeurProgBar());
		ActivatorData.getInstance().setVpExportMult(new ValeurProgBar());

		SamngLogger.emptyLogFile();
		ActivatorData.getInstance().setVueData(new VueData());
		ActivatorData.getInstance().setProgrBar(VueProgressBar.getInstance());
		
		// Create the base filters mng and vbvs manager here as they add theirself as listeners
		ActivatorData.getInstance().setBaseFiltersMng(new GestionnaireBaseFiltres());
		ActivatorData.getInstance().setVbvsMng(new VbvsProvider());

		DistinctionExtension.setEnableFormats();
		BridageFormats.getInstance().initialiseValides();
	
	}

	public final void start(final BundleContext context) throws Exception {
		super.start(context);
		selectionLangue();
	}
	
	private void initRepertoireINSTALL_SAM_PARAM() {
		RepertoiresAdresses.initPaths(getPathINSTALL_SAM_PARAM());
	}
	
	public static String getPathINSTALL_SAM_PARAM() {
		String res = "";
		
		// Le répertoire de travail correspond au dossier parent du workspace
		// SAM (donné par -data dans SAM5.ini ou dans les arguments de lancement
		// dans éclipse)
		List<String> args = Arrays.asList(Platform.getCommandLineArgs());
		
		int workspaceIndex = args.indexOf(GestionLineCommandParameters.workspace);

		if (workspaceIndex != -1 && workspaceIndex < args.size()) {
			res = args.get(workspaceIndex + 1);
		} else {
			MessageBox msgBox = new MessageBox(new Shell(), SWT.ICON_ERROR);
			msgBox.setText("Workspace error");
			msgBox.setMessage("Mandatory option '-workspace PATH' is missing in SAM5.ini");
			msgBox.open();
			System.exit(1);
		}
		
		if (!res.endsWith(File.separator)) {
			res += File.separator;
		}
		return res;
	}
	
	public void releaseMultimedia() {
		ActivatorData.getInstance().clearMultimediaFiles();
	}

	public void release() {
		try {
			if (ActivatorData.getInstance().getPoolDonneesVues().get("fichierVide") != null) {
				if (ActivatorData.getInstance().getPoolDonneesVues().get("fichierVide").equals("true")) {
					ActivatorData.getInstance().getPoolDonneesVues().put("fichierVide", new String("false"));
				}
			}
			
			GestionnaireFlags.getInstance().clear();
			GestionnaireDescripteurs.emptyPool();
			GestionnairePool.getInstance().emptyPool();

			if (ActivatorData.getInstance().getVueData() != null) {
				ActivatorData.getInstance().getVueData().releaseParcoursData();
			}
			
			GestionnaireProfil.getInstance().clear();
			GestionnaireVueMarqueurs.getInstance().clear();
			GestionnaireCorrection.getInstance().clear();
			GestionnaireDescripteurs.getInstance().clear();
			GestionnairePool.getInstance().clear();
			GestionnaireSynchronisationGroupes.getInstance().clear();			
			
			ParseurParcoursAtess.getInstance().clear();
			ParseurTableAssociationEvVars.getInstance().clear();
			GestionnaireLongueurMessage.getInstance().clear();
			GestionnaireLongueurPacket.getInstance().clear();
			ParseurJRUTableAssociationEvVars.getInstance().clear();
			ParseurParcoursJRU.getInstance().clear();
			ParseurParcoursSamng.getInstance().clear();
			ParseurParcoursTomUk.getInstance().clear();
			ParseurParcoursTomDIS.getInstance().clear();
			ParseurParcoursTomHSBC.getInstance().clear();
			ParseurXMLJRU.getInstance().clear();
			
			try {
				ParseurXML1 parseurXML_NG_UK_ATESS = ParseurXML_NG_UK_ATESS.getInstance();
				
				if (parseurXML_NG_UK_ATESS != null) {
					parseurXML_NG_UK_ATESS.clear();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			ParseurCorrections.getInstance().clear();
			ParseurCouleurs.getInstance().clear();
			ParseurFlags.getInstance().clear();
			
			ParseurXML1 parseurXML1 = ParseurXML1.getInstance();
			
			if (parseurXML1 != null) {
				parseurXML1.clear();
			}
			
			TypeParseur.getInstance().clear();
			ActivatorData.getInstance().clearMultimediaFiles();
			ActivatorData.getInstance().getListenersRepere().clear();
			ActivatorData.getInstance().getListenersDuplication().clear();
			ActivatorData.getInstance().setMarkersMng(new GestionnaireMarqueurs());
			ActivatorData.getInstance().setVbvsMng(new VbvsProvider());
			ActivatorData.getInstance().setCorrectionTempsApplied(false);
			ActivatorData.getInstance().getListeGestionnairesFiltres().clear();
			ActivatorData.getInstance().getPoolDonneesVues().clear();
			InstancePresentationVueDetaillee.getInstance().clear();
			VueProgressBar.getInstance().clear();
			VueWaitBar.getInstance().clear();
			ActivatorData.getInstance().setSelectedMsg(null);
			Util.getInstance().clear();					
			
			//Explorer
			FabriqueParcours.getInstance().clear();
			TableRuptures.getInstance().clear();
			TableSegments.getInstance().clear();			
			
			release2();
			
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	public void release2() {
		try {			
			
			GestionnaireFlagsExplorer.getInstance().clear();
			GestionnaireDescripteursExplorer.emptyPool();
			GestionnairePoolExplorer.getInstance().emptyPool();

			if (ActivatorDataExplorer.getInstance().getVueData() != null) {
				ActivatorDataExplorer.getInstance().getVueData().releaseParcoursData();
			}
			
			GestionnaireProfilExplorer.getInstance().clear();
			GestionnaireVueMarqueursExplorer.getInstance().clear();
			GestionnaireCorrectionExplorer.getInstance().clear();
			GestionnaireDescripteursExplorer.getInstance().clear();
			GestionnairePoolExplorer.getInstance().clear();
			GestionnaireSynchronisationGroupesExplorer.getInstance().clear();			
			
			ParseurParcoursAtessExplorer.getInstance().clear();
			ParseurTableAssociationEvVarsATESSExplorer.getInstance().clear();
			GestionnaireLongueurMessageExplorer.getInstance().clear();
			GestionnaireLongueurPacketExplorer.getInstance().clear();
			ParseurJRUTableAssociationEvVarsExplorer.getInstance().clear();
			ParseurParcoursJRUExplorer.getInstance().clear();
			ParseurParcoursSamngExplorer.getInstance().clear();
			ParseurParcoursTomUkExplorer.getInstance().clear();
			ParseurParcoursTomDISExplorer.getInstance().clear();
			ParseurParcoursTomHSBCExplorer.getInstance().clear();
			ParseurXML_JRUExplorer.getInstance().clear();
			ParseurXML_NG_UK_ATESS_Explorer.getInstance().clear();
			ParseurCorrectionsExplorer.getInstance().clear();
			ParseurCouleursExplorer.getInstance().clear();
			ParseurFlagsExplorer.getInstance().clear();
			
			ParseurXML1 parseurXML1 = ParseurXML1Explorer.getInstance();
			
			if (parseurXML1 != null) {
				parseurXML1.clear();
			}
			
			TypeParseurExplorer.getInstance().clear();
						
			ActivatorDataExplorer.getInstance().getListenersRepere().clear();
			ActivatorDataExplorer.getInstance().getListenersDuplication().clear();
			ActivatorDataExplorer.getInstance().setMarkersMng(new GestionnaireMarqueurs());
			ActivatorDataExplorer.getInstance().setVbvsMng(new VbvsProvider());
			ActivatorDataExplorer.getInstance().setCorrectionTempsApplied(false);
			ActivatorDataExplorer.getInstance().getListeGestionnairesFiltres().clear();
			ActivatorDataExplorer.getInstance().getPoolDonneesVues().clear();
			InstancePresentationVueDetailleeExplorer.getInstance().clear();
			VueProgressBarExplorer.getInstance().clear();
			VueWaitBarExplorer.getInstance().clear();
			ActivatorDataExplorer.getInstance().setSelectedMsg(null);
			UtilExplorer.getInstance().clear();			
			
			FabriqueParcoursExplorer.getInstance().clear();
			TablesRupturesExplorer.getInstance().clear();
			TableSegmentsExplorer.getInstance().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public final void stop(final BundleContext context) throws Exception {
		ActivatorData.getInstance().getProviderVBVs().getGestionnaireVbvs().enregistrerVBV();		
		
		try {
			ActivatorData.getInstance().getVueData().releaseData();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * getDefault.
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in relative path
	 * 
	 * @param path, the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * @return the perspectives
	 */
	public final IPerspectiveDescriptor[] getPerpectives() {
		return this.perspectives;
	}

	public void selectionLangue(){
		// Récupération du pays et de la langue via la Locale
		String langue = Locale.getDefault().getLanguage();
		String pays = Locale.getDefault().getCountry();
		String suffixeMessages = "";
		
		// Si le pays est celui de la langue, on ne garde que la langue (exemples: fr_FR: FR, en_EN : FR)
		if (langue.toUpperCase().equals(pays.toUpperCase())) {
			suffixeMessages = new String(langue).toUpperCase();
		} else { 
			suffixeMessages = new String(langue + "_" + pays).toUpperCase();
		}

		// Test de présence et d'activation de la locale choisie dans le fichier ./ressources/xml/languages.properties
		Properties p = new Properties();
		
		InputStream stream;
		try {
			stream = new FileInputStream(RepertoiresAdresses.languages_PROPERTIES);
			p.load(stream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean langueActive = true;
		
		// Locale absente 
		if (p.get(suffixeMessages) == null) {
			langueActive = false;
		}
		else if (((String) p.get(suffixeMessages)).trim().equals("0")) { // Locale présente mais inactive 
			langueActive = false;
		}
		
		Langage l = null;
		// Si la langue ne peut etre chargée, 
		if (!langueActive) {
			l = Langage.valueOf("DEF");
			this.getPreferenceStore().setValue(PreferenceConstants.LANG_CHOICE, "DEF");
		} else {
			l = Langage.valueOf(suffixeMessages);
			this.getPreferenceStore().setValue(PreferenceConstants.LANG_CHOICE, suffixeMessages);
		}
		
		this.currentLanguage = l;
	}
	
	public void hideAllViews(){
		hideAllViewsExceptViews("SAMNG.Vue.VueExplorateur");
	}
	
	public void hideAllViewsExceptViews(String ... ids){
		IWorkbenchPage iwp=Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewReference[] views = iwp.getViewReferences();

		for (IViewReference view : views) {
			boolean vueANePasFermer = false;
			
			for (String id : ids) {
				if (view.getId().equals(id)) {
					vueANePasFermer = true;
				}
			}
			
			if (!vueANePasFermer) {
				iwp.hideView(view);
			}			
		}
	}
	
	/**
	 * @return the currentLanguage
	 */
	public Langage getCurrentLanguage() {
		return this.currentLanguage;
	}
}
