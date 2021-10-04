package com.faiveley.samng.principal.ihm.vues.vuetoolbar;

import org.eclipse.jface.action.Action;

public interface IVueToolbar {
	public void ajoutActionToolBar(Action action) ;
	
	public void ajoutActionToolMenuBar(Action action) ;

	public void ajoutSeparateurToolBar();
	
	public void makeActions() ;

}
