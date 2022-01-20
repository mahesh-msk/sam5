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

	@Override
	public void finalize() throws Throwable {
		this.fct = null;
		super.finalize();
	}

	protected void expandTableTree(TreeViewer ttv) {

		// E34 Verifier le portage de expand table tree
		if (!ttv.getStructuredSelection().isEmpty()) {
			Iterator<?> it = ttv.getStructuredSelection().iterator();
			do {
				ttv.expandToLevel(it.next(), AbstractTreeViewer.ALL_LEVELS);
			} while (it.hasNext());
		}

		/*
		 * int nbSelected = ttv.getTree().getSelection().length;
		 * 
		 * 
		 * for (int i = 0; i < nbSelected; i++) {
		 * ttv.expandToLevel(ttv.getTableTree().getSelection()[i].getData(),
		 * AbstractTreeViewer.ALL_LEVELS); }
		 * 
		 * 
		 */

		// Re-color lines
		if (ttv instanceof TreeKVBDetailViewer) {
			((TreeKVBDetailViewer) ttv).colorLines();

		}
	}
}
