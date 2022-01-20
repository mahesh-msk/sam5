package com.faiveley.samng.vueliste.ihm.actions.table;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.TableTreeDetailViewer;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeKVBDetailViewer;

@SuppressWarnings("deprecation")
@Deprecated
public class ExpandAction extends Action {
	FixedColumnTableViewerDetail fct;
	
	public ExpandAction(final String actionId, final String label, final ImageDescriptor descriptor, FixedColumnTableVueListe fct) {
		setText(label);
		setToolTipText(label);
		
		// The id is used to refer to the action in a menu or toolbar
		setId(actionId);
		
		// Associate the action with a pre-defined command, to allow key bindings.
		setActionDefinitionId(ICommandIds.CMD_EXP);
		
		setImageDescriptor(descriptor);
		this.fct = (FixedColumnTableViewerDetail)fct;
	}
	
	public void setFixedColumnTableViewerDetail (FixedColumnTableViewerDetail fct) {
		this.fct = fct;
	}
	
	@Override
	public void run() {
		if (this.fct.getTableTreeKVBDetailViewer() == null || this.fct.getSelectedTab() == 0) {
			expandTableTree(this.fct.getTableTreeDetailViewer());
		} else {
			expandTableTree(this.fct.getTableTreeKVBDetailViewer());
		}
	}
	
	@Override
	public void finalize() throws Throwable {
		this.fct = null;
		super.finalize();
	}
	
	protected void expandTableTree(TableTreeDetailViewer ttv) {
		int nbSelected = ttv.getTableTree().getSelection().length;
		
		for (int i = 0; i < nbSelected; i++) {
			ttv.expandToLevel(ttv.getTableTree().getSelection()[i].getData(), AbstractTreeViewer.ALL_LEVELS);
		}
		
		// Re-color lines
		if (ttv instanceof TableTreeKVBDetailViewer) {
			((TableTreeKVBDetailViewer) ttv).colorLines();
		}
	}
}
