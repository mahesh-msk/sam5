package com.faiveley.samng.vuetabulaire.ihm.dialogs;

import org.eclipse.swt.widgets.Shell;

import com.faiveley.samng.principal.ihm.vues.search.SearchVariable;

public class SearchVariableTabulaire extends SearchVariable{

	public SearchVariableTabulaire(Shell parent, boolean usesShortNames) {
		super(parent);
		this.usesShortNames = usesShortNames;
		// 
	} 
	
	public SearchVariableTabulaire(Shell parent,int style) {
		super(parent,style);
		// 
	}

}
