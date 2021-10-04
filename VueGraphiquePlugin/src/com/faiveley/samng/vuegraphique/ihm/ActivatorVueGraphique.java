package com.faiveley.samng.vuegraphique.ihm;

import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.BundleContext;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.GraphiqueFiltresProvider;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.GestionnaireGraphesNotifications;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.configuration.GestionnaireVueGraphique;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;


/**
 * The activator class controls the plug-in life cycle
 */
public class ActivatorVueGraphique extends AbstractActivatorVue {

	// The plug-in ID
	public static final String PLUGIN_ID = "VueGraphiquePlugin"; //$NON-NLS-1$

	// The shared instance
	private static ActivatorVueGraphique plugin;
	
	private GestionnaireVueGraphique configurationMng = new GestionnaireVueGraphique();
	
	private boolean usesShortNames;

	/**
	 * The constructor
	 */
	public ActivatorVueGraphique() {
		plugin = this;
		
		//in this moment we might have a file already loaded.
		//So we make a forced update
		this.providerFiltres = new GraphiqueFiltresProvider();
		this.providerFiltres.setAppliedFilterName(this.configurationMng.getFiltreApplique());
		this.configurationMng.onDataChange();	
		
		ActivatorData.getInstance().getListeGestionnairesFiltres().add(providerFiltres);
		ActivatorData.getInstance().addDataListener(this.configurationMng);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void clear(){
		GestionnaireGraphesNotifications.getInstance().clear();
		GestionnaireZoom.getInstance().clear();
		GestionnaireAxes.getInstance().clear();
//CHECK01
	}
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		clear();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ActivatorVueGraphique getDefault() {
		return plugin;
	}
	
	public String getCurrentAppliedFilterName() {
		return this.providerFiltres.getAppliedFilter();
	}
	
	public GestionnaireVueGraphique getConfigurationMng() {
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
	
	public void saveConfigurationVue() {
		configurationMng.checkForSave("Vue Graphique");
	}
	
	public boolean isUsesShortNames() {
		return usesShortNames;
	}

	public void setUsesShortNames(boolean usesShortNames) {
		this.usesShortNames = usesShortNames;
	}
}
