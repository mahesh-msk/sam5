package com.faiveley.samng.vueliste.ihm.actions.table.e4;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.e4.FixedColumnTableVueListe;

public class CollapseAction extends Action {
	FixedColumnTableViewerDetail fct;
	
	public CollapseAction(final String actionId, final String label, final ImageDescriptor descriptor, FixedColumnTableVueListe fct) {
		setText(label);
		setToolTipText(label);
		
		// The id is used to refer to the action in a menu or toolbar
		setId(actionId);
		
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_COLL);
		
		setImageDescriptor(descriptor);
		this.fct = (FixedColumnTableViewerDetail) fct;
	}
	
	public void setFixedColumnTableViewerDetail (FixedColumnTableViewerDetail fct) {
		this.fct = fct;
	}
	
	@Override
	public void run() {
		if (this.fct.getTreeKVBDetailViewer() == null || this.fct.getSelectedTab() == 0) {
			collapseTableTree(this.fct.getTreeDetailViewer());
		} else {
			collapseTableTree(this.fct.getTreeKVBDetailViewer());
		}
	}
	
	@Override
	public void finalize() throws Throwable {
		this.fct = null;
		super.finalize();
	}
	
	protected void collapseTableTree(TreeViewer ttv) {
		
		// E34 Reimplementer collapseTableTree dans e4.CollapseAction
		
		/*
		int nbSelected = ttv.getTableTree().getSelection().length;
		
		for (int i = 0; i < nbSelected; i++) {
			TableTreeItem item = ttv.getTableTree().getSelection()[i];
			
			if (item.getItems().length == 0) {
				item = item.getParentItem();
			}
			
			ttv.collapseToLevel(item.getData(), -1);
		}
		*/
	}
}