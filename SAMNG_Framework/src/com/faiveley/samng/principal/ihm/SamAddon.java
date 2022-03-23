 
package com.faiveley.samng.principal.ihm;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.ui.model.application.MApplication;

public class SamAddon {

	@Inject SamAddon()
	{
		System.out.println("Sam Framework addon constructor");

		
	}
	@PostConstruct
	public void applicationStarted(MApplication a)
	{
		System.out.println("Sam Framework addon");
			// Remove the minmax Addon (see https://www.eclipse.org/eclipse/news/4.15/platform_isv.php#disable-swt-addons)
		a.getTags().add("DisableMinMaxAddon");  // private constant defined in MinMaxAddon class !
	}

}
