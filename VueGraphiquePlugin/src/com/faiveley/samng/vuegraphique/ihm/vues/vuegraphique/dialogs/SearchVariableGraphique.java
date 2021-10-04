package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.ihm.vues.search.SearchVariable;

public class SearchVariableGraphique extends SearchVariable{

	public SearchVariableGraphique(Shell parent, boolean usesShortNames) {
		super(parent);
		this.usesShortNames = usesShortNames;
		// 
	} 
	
	public SearchVariableGraphique(Shell parent,int style) {
		super(parent,style);
		// 
	}

}
