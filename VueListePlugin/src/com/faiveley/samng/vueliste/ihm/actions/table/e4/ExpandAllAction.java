package com.faiveley.samng.vueliste.ihm.actions.table.e4;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.e4.TreeKVBDetailViewer;

public class ExpandAllAction extends ExpandAction {
	FixedColumnTableViewerDetail fct;
	
	public ExpandAllAction(final String actionId, final String label, final ImageDescriptor descriptor, FixedColumnTableVueListe fct) {
		super(actionId, label, descriptor, fct);
	}

	@Override
	protected void expandTableTree(TreeViewer ttv) {
		ttv.expandAll();
		
		// Re-color lines
		if (ttv instanceof TreeKVBDetailViewer) {
			((TreeKVBDetailViewer) ttv).colorLines();
		}
	}
}
