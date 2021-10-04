package com.faiveley.samng.vueliste.ihm;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.ListeFiltresProvider;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueDetaillee;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.GestionnaireVueListe;



/**
 * The activator class controls the plug-in life cycle
 */
public class ActivatorVueListe extends AbstractActivatorVue {

	// The plug-in ID
	public static final String PLUGIN_ID = "VueListePlugin"; //$NON-NLS-1$

	// The shared instance
	private static ActivatorVueListe plugin;
	
	private GestionnaireVueListe configurationMng = new GestionnaireVueListe();
	private GestionnaireVueDetaillee configurationVueDetaillee = new GestionnaireVueDetaillee();

	
	
	//private ShutdownListener shutdownListener = new ShutdownListener();
	private int vueFocus = FOCUS_VUE_LISTE;
	
	public static int FOCUS_VUE_LISTE = 1;
	public static int FOCUS_VUE_DETAILLEE = 2;




	public int getVueFocus() {
		return vueFocus;
	}



	public void setVueFocus(int vueFocus) {
		this.vueFocus = vueFocus;
	}



	/**
	 * The constructor
	 */
	public ActivatorVueListe() {
		plugin = this;
		//in this moment we might have a file already loaded.
		//So we make a forced update
		this.providerFiltres = new ListeFiltresProvider();
		this.providerFiltres.setAppliedFilterName(this.configurationMng.getFiltreApplique());
		this.configurationMng.onDataChange();	
		this.configurationVueDetaillee.onDataChange();
		
		
		ActivatorData.getInstance().getListeGestionnairesFiltres().add(providerFiltres);
		
		//add this view as listener on changing data and shotdown application
		ActivatorData.getInstance().addDataListener(this.configurationMng);
		ActivatorData.getInstance().addDataListener(this.configurationVueDetaillee);
		
//		VueListeContentProvider cp = new VueListeContentProvider();
//		cp.initializeColumns();
//		cp.loadContent();
	}



	/*
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		//create an action to open the Vue Liste
//		this.openVueListeAction = new VueAction(null, 
//				Messages.getString("ActivatorVueListe.1"), //$NON-NLS-1$
//				ICommandIds.CMD_OPEN_VUE_LIST,
//				VueListe.ID, 
//				com.faiveley.samng.principal.ihm.Activator
//				.getImageDescriptor("/icons/liste-ng.JPG"),true); //$NON-NLS-1$
//		
//		//add the action
//		this.openVueListeAction.setToolTipText("Vue Liste"); //$NON-NLS-1$
//		ActivatorData.getInstance().getBarAdvisor().createAction(this.openVueListeAction, VueType.VUE);
//
//		//Vue Filtre Liste
//		this.openVueFiltreListeAction = new ActionOpenCloseVue(Messages.getString("ActivatorVueListe.2"), //$NON-NLS-1$
//				VueListeFiltre.ID); //$NON-NLS-1$
//		this.openVueFiltreListeAction.setToolTipText("Vue Filtre Liste"); //$NON-NLS-1$
//		ActivatorData.getInstance().getBarAdvisor().createAction(this.openVueFiltreListeAction, VueType.FILTRE);
//
//		configListViewAction = new ConfigListVueAction();
//		ActivatorData.getInstance().getBarAdvisor().createAction(this.configListViewAction, VueType.VUE);
//
//		
//		//action responsable de l'export de la vue liste
//		exporterVueListeAction = new ExporterVueListeAction(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(),
//				Messages.getString("ActivatorVueListe.3")); //$NON-NLS-1$
//		ActivatorData.getInstance().getBarAdvisor().createAction(exporterVueListeAction, VueType.FILE);
//		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ActivatorVueListe getDefault() {
		return plugin;
	}

	
	/**
	 * Returns the configuration manager
	 * @return
	 */
	public GestionnaireVueListeBase getConfigurationMng() {
		return this.configurationMng;
	}

	/**
	 * Returns the configuration manager
	 * @return
	 */
	public GestionnaireVueDetaillee getConfigurationVueDetaillee() {
		return this.configurationVueDetaillee;
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
	
	/**
	 * Checks whether the view configuration needs to be saved or not and acts accordingly 
	 */
	public void saveConfigurationVue() {
		configurationMng.checkForSave(Messages.getString("ActivatorVueListe.4")); //$NON-NLS-1$
		configurationVueDetaillee.sauvegarderConfiguration();
	}
	
}
