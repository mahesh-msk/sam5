package com.faiveley.samng.vueliste.ihm.actions.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TableTreeViewer;
import org.eclipse.swt.custom.TableTreeItem;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableVueListe;

@SuppressWarnings("deprecation")
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
		if (this.fct.getTableTreeKVBDetailViewer() == null || this.fct.getSelectedTab() == 0) {
			collapseTableTree(this.fct.getTableTreeDetailViewer());
		} else {
			collapseTableTree(this.fct.getTableTreeKVBDetailViewer());
		}
	}
	
	@Override
	public void finalize() throws Throwable {
		this.fct = null;
		super.finalize();
	}
	
	protected void collapseTableTree(TableTreeViewer ttv) {
		int nbSelected = ttv.getTableTree().getSelection().length;
		
		for (int i = 0; i < nbSelected; i++) {
			TableTreeItem item = ttv.getTableTree().getSelection()[i];
			
			if (item.getItems().length == 0) {
				item = item.getParentItem();
			}
			
			ttv.collapseToLevel(item.getData(), -1);
		}
	}
}