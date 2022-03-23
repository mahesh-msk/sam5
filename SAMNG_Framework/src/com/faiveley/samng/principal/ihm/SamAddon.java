 
package com.faiveley.samng.principal.ihm;

import javax.annotation.PostConstruct;

import org.eclipse.e4.ui.model.application.MApplication;

public class SamAddon {

	
	@PostConstruct
	public void applicationStarted(MApplication a)
	{
	   // Remove the minmax Addon (see https://www.eclipse.org/eclipse/news/4.15/platform_isv.php#disable-swt-addons)
		a.getTags().add("DisableMinMaxAddon");  // private constant defined in MinMaxAddon class !
	}

}
