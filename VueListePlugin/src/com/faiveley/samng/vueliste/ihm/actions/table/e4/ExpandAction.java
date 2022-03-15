package com.faiveley.samng.vueliste.ihm.actions.table.e4;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.TreeViewer;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.e4.TreeKVBDetailViewer;

public class ExpandAction extends Action {
	FixedColumnTableViewerDetail fct;

	public ExpandAction(final String actionId, final String label, final ImageDescriptor descriptor,
			FixedColumnTableVueListe fct) {
		setText(label);
		setToolTipText(label);

		// The id is used to refer to the action in a menu or toolbar
		setId(actionId);

		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EXP);

		setImageDescriptor(descriptor);
		this.fct = (FixedColumnTableViewerDetail) fct;
	}

	public void setFixedColumnTableViewerDetail(FixedColumnTableViewerDetail fct) {
		this.fct = fct;
	}

	@Override
	public void run() {
		if (this.fct.getTreeKVBDetailViewer() == null || this.fct.getSelectedTab() == 0) {
			expandTableTree(this.fct.getTreeDetailViewer());
		} else {
			expandTableTree(this.fct.getTreeKVBDetailViewer());
		}
	}


	protected void expandTableTree(TreeViewer ttv) {

		if (!ttv.getStructuredSelection().isEmpty()) {
			Iterator<?> it = ttv.getStructuredSelection().iterator();
			do {
				ttv.expandToLevel(it.next(), AbstractTreeViewer.ALL_LEVELS);
			} while (it.hasNext());
		}


		// Re-color lines
		if (ttv instanceof TreeKVBDetailViewer) {
			((TreeKVBDetailViewer) ttv).colorLines();

		}
	}
}
