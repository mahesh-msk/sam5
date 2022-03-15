package com.faiveley.samng.vueliste.ihm.actions.table;

import org.eclipse.jface.resource.ImageDescriptor;

import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.TableTreeDetailViewer;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailViewer;

@Deprecated
public class ExpandAllAction extends ExpandAction {
	FixedColumnTableViewerDetail fct;
	
	public ExpandAllAction(final String actionId, final String label, final ImageDescriptor descriptor, FixedColumnTableVueListe fct) {
		super(actionId, label, descriptor, fct);
	}

	@Override
	protected void expandTableTree(TableTreeDetailViewer ttv) {
		ttv.expandAll();
		
		// Re-color lines
		if (ttv instanceof TableTreeKVBDetailViewer) {
			((TableTreeKVBDetailViewer) ttv).colorLines();
		}
	}
}
