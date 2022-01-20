package com.faiveley.samng.vueliste.ihm.actions.table;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableTreeViewer;

import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableVueListe;

@SuppressWarnings("deprecation")
@Deprecated
public class CollapseAllAction extends CollapseAction {
	FixedColumnTableViewerDetail fct;
	
	public CollapseAllAction(final String actionId, final String label, final ImageDescriptor descriptor, FixedColumnTableVueListe fct) {
		super(actionId, label, descriptor, fct);
	}
		
	@Override
	protected void collapseTableTree(TableTreeViewer ttv) {
		ttv.collapseAll();
	}
}