package com.faiveley.samng.vueliste.ihm.actions.table.e4;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableVueListe;


public class CollapseAllAction extends CollapseAction {
	FixedColumnTableViewerDetail fct;
	
	public CollapseAllAction(final String actionId, final String label, final ImageDescriptor descriptor, FixedColumnTableVueListe fct) {
		super(actionId, label, descriptor, fct);
	}
		
	@Override
	protected void collapseTableTree(TreeViewer ttv) {
		ttv.collapseAll();
	}
}