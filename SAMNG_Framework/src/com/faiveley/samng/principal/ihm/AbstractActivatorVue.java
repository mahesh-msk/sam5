package com.faiveley.samng.principal.ihm;

import org.eclipse.ui.plugin.AbstractUIPlugin;

import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;

public abstract class AbstractActivatorVue extends AbstractUIPlugin{
	
	protected AbstractProviderFiltre providerFiltres;
	

	/**
	 * Returns the filters provider
	 * @return
	 */
	public AbstractProviderFiltre getFiltresProvider() {
		return this.providerFiltres;
	}

}
