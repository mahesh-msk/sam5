package com.faiveley.samng.principal.sm.linecommands;

import com.faiveley.samng.principal.ihm.AbstractActivatorVue;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;

public class DummyActivator<T extends AbstractProviderFiltre> extends AbstractActivatorVue {
	
	public DummyActivator(T filterProvider) {
		providerFiltres = filterProvider;
	}

}
