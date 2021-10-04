package com.faiveley.samng.vuemultimedia.ihm;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.vuemultimedia.ihm.vues.vuemultimedia.configuration.GestionnaireVueMultimedia;

/**
 * The activator class controls the plug-in life cycle
 */
public class ActivatorVueMultimedia extends AbstractActivatorVue {
	// The plug-in ID
	public static final String PLUGIN_ID = "VueMultimediaPlugin";

	// The shared instance
	private static ActivatorVueMultimedia plugin;
	
	private GestionnaireVueMultimedia configurationManager = new GestionnaireVueMultimedia();
	
	/**
	 * The constructor
	 */
	public ActivatorVueMultimedia() {		
		plugin = this;
	}
	
	/*
	 * 
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@SuppressWarnings("deprecation")
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
        Bundle bundle = Platform.getBundle(PLUGIN_ID);
        
        if (bundle == null) {
            throw new RuntimeException("Could not resolve plugin: " + PLUGIN_ID + "\r\n" + "Probably the plugin has not been correctly installed.\r\n");
        }
        
        URL vlcPluginURL = null;
        
        try {
            vlcPluginURL = Platform.resolve(bundle.getEntry("/"));
        } catch (IOException e) {
            throw new RuntimeException("Could not get installation directory of the plugin: " + PLUGIN_ID);
        }
        
        String vlcPluginLocation = vlcPluginURL.getPath().trim();

        vlcPluginLocation = vlcPluginLocation.substring(0, vlcPluginLocation.length() - 1);
        
        String[] commands = System.getProperty("eclipse.commands").split("\n");
        String launcherCommandValue = null;
        String osCommandValue = null;
        String archCommandValue = null;
        
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals("-launcher")) {
                launcherCommandValue = commands[i + 1];
            } else if (commands[i].equals("-os")) {
                osCommandValue = commands[i + 1];
            } else if (commands[i].equals("-arch")) {
                archCommandValue = commands[i + 1];
            }
        }
        
        // Note: On reconstruit ici le chemin vers le bon plugin (c'est à dire vers le fragment correspondant à l'architecture de la machine).
        // Ce chemin dépend du type d'environnement (DEV ou PROD) mais aussi de l'architecture de l'application
        // Les plugins et fragments ont été dénommés de manière à déterminer facilement ces chemins.
        // Le plugin "VueMultimediaPlugin" et le fragment "VueMultimediaPlugin.win32.x86" ne doit en aucun cas être renommé (ou alors uniquement le bloc "VueMultimediaPlugin" pour le plugin et son fragment).
        // Sans quoi les chemins déterminés seront faux, la localisation des .dll de VLC ne pourra se faire et la librairie VLCj ne pourra fonctionner.
                
        if (launcherCommandValue.toLowerCase().contains("eclipse.exe")) { // If DEV environment
        	// since path returned by URL::getPath starts with a forward slash, that
            // is not suitable to run commandlines on Windows-OS, but for Unix-based
            // OSes it is needed. So strip one character for windows. There seems
            // to be no other clean way of doing this.
            if (Platform.getOS().compareTo(Platform.OS_WIN32) == 0) {
                vlcPluginLocation = vlcPluginLocation.substring(1);
            }
            
            vlcPluginLocation = String.format("%s.%s.%s", vlcPluginLocation, osCommandValue, archCommandValue);
        } else {
            vlcPluginLocation = vlcPluginLocation.replace("file:/", "");
            vlcPluginLocation = vlcPluginLocation.replace("!/", "");
        
            if (vlcPluginLocation != null && vlcPluginLocation.length() > 0 ) {
                int endIndex = vlcPluginLocation.lastIndexOf("/");
                
                if (endIndex != -1) {
                    vlcPluginLocation = vlcPluginLocation.substring(0, endIndex);
                }
            }
            
            vlcPluginLocation = String.format("%s/%s.%s.%s_%s", vlcPluginLocation, PLUGIN_ID, osCommandValue, archCommandValue, bundle.getVersion());
        }
                
        System.setProperty("jna.library.path", vlcPluginLocation);
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
	public static ActivatorVueMultimedia getDefault() {
		return plugin;
	}
		
	public GestionnaireVueMultimedia getConfigurationManager() {
		return this.configurationManager;
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
}
