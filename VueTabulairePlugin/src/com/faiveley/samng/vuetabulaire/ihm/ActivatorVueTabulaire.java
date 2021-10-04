package com.faiveley.samng.vuetabulaire.ihm;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.progbar.ValeurProgBar;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.TabulaireFiltresProvider;
import com.faiveley.samng.vuetabulaire.ihm.actions.ExportFichierAction;
import com.faiveley.samng.vuetabulaire.ihm.actions.MultipleExportsAction;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.configuration.GestionnaireVueTabulaire;
import com.faiveley.samng.principal.sm.filtres.GestionnaireFiltresTabulaire;

/**
 * The activator class controls the plug-in life cycle
 */
public class ActivatorVueTabulaire extends AbstractActivatorVue {

	private ValeurProgBar vpExportMult;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "VueTabulairePlugin"; //$NON-NLS-1$

	// The shared instance
	private static ActivatorVueTabulaire plugin;
	
	
	private ExportFichierAction exporterFichierAction;
	private MultipleExportsAction multipleExportAction;
	private GestionnaireVueTabulaire configurationMng;

	private Object[] lignesVue =null;
	
	private boolean usesShortNames;
	
	
	/**
	 * The constructor
	 */
	public ActivatorVueTabulaire() {
		plugin = this;
		
		providerFiltres = new TabulaireFiltresProvider();
		
		configurationMng = new GestionnaireVueTabulaire();
	
		this.vpExportMult=new ValeurProgBar();
		
		//in this moment we might have a file already loaded.
		//So we make a forced update
		this.configurationMng.onDataChange();	
		this.providerFiltres.setAppliedFilterName(this.configurationMng.getFiltreApplique());
		
		ActivatorData.getInstance().getListeGestionnairesFiltres().add(providerFiltres);
		ActivatorData.getInstance().addDataListener(this.configurationMng);		
		
//		if(ActivatorData.getInstance().getVueData().getDataTable()!=null){
//		try {
//			VueTabulaireContentProvider cp = new VueTabulaireContentProvider();
//			cp.initializeColumns();
//			try {
//				getWorkbench().addWorkbenchListener(this.shutdownListener);
//			} catch (Exception e) {
//			
//			}
//			
//			cp.loadContent();
//			
//		} catch (Exception e) {
//		
//		}	
//		}
		
	}

	public void setElements(Object [] tableauValeur){

		this.lignesVue = tableauValeur;
	}
	
	
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		try {
			super.start(context);
		} catch (Exception e) {
			System.out.println("handle exception");
		}
		
		try {
			Activator.getDefault().getWorkbench();
		} catch (Exception e) {
			System.out.println("workbench not yet");
			return;
		}
		
		exporterFichierAction = new ExportFichierAction(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(),
				Messages.getString("ActivatorVueTabulaire.5")); //$NON-NLS-1$
		exporterFichierAction.setEnabled(false);

		multipleExportAction = new MultipleExportsAction(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(),
				Messages.getString("ActivatorVueTabulaire.6")); //$NON-NLS-1$
		multipleExportAction.setEnabled(true);
		ActivatorData.getInstance().getBarAdvisor().ajouterExportsActions(exporterFichierAction);
		ActivatorData.getInstance().getBarAdvisor().ajouterExportsActions(multipleExportAction);
		
		ActivatorData.getInstance().getListMenuAction().add(exporterFichierAction);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ActivatorData.getInstance().removeDuplicationFiltreListener((GestionnaireFiltresTabulaire) providerFiltres.getGestionnaireFiltres());
		
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ActivatorVueTabulaire getDefault() {
		return plugin;
	}
	
	public GestionnaireVueListeBase getConfigurationMng() {
		return this.configurationMng;
	}
	
	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	
	public void simpleMethode()
	{
		
	}

	public Object[] getLignesVue() {
		return this.lignesVue;
	}

	public void setLignesVue(Object[] lignesVue) {
		this.lignesVue = lignesVue;
	}

	public ExportFichierAction getExporterFichierAction() {
		return exporterFichierAction;
	}

	public void setExporterFichierAction(ExportFichierAction exporterFichierAction) {
		this.exporterFichierAction = exporterFichierAction;
	}

	public MultipleExportsAction getMultipleExportAction() {
		return multipleExportAction;
	}

	public void setMultipleExportAction(MultipleExportsAction multipleExportAction) {
		this.multipleExportAction = multipleExportAction;
	}
	
	public ValeurProgBar getVpExportMult() {
		return vpExportMult;
	}

	public void setVpExportMult(ValeurProgBar vpExportMult) {
		this.vpExportMult = vpExportMult;
	}
	
	public void saveConfigurationVue() {
		configurationMng.checkForSave("Vue Tabulaire");
	}

	public boolean isUsesShortNames() {
		return usesShortNames;
	}

	public void setUsesShortNames(boolean usesShortNames) {
		this.usesShortNames = usesShortNames;
	}

}
