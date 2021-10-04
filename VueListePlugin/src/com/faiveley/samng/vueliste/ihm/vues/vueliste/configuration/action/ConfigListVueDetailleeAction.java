package com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.action;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.FixedColumnTableViewerDetail;

/**
 * @author Cosmin Udroiu
 *
 */
public class ConfigListVueDetailleeAction extends VueAction {
	private FixedColumnTableViewerDetail tableauVueDetaillee = null;
		
	public ConfigListVueDetailleeAction(FixedColumnTableViewerDetail tableauVueDetaillee, boolean isKVBTab) {
		super(null, ICommandIds.CMD_OPEN_COFIG_LIST, Messages.getString("ConfigListVueAction.0"), null, com.faiveley.samng.vueliste.ihm.ActivatorVueListe.getImageDescriptor("/icons/vueListe/vue_liste_gerer_colonnes.png"), true);
		this.tableauVueDetaillee = tableauVueDetaillee;
	}
	
	@Override
	public void run() {
		ColonnesConfigVueDetailleeDialog colCfgDlg = new ColonnesConfigVueDetailleeDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(), this.tableauVueDetaillee);
		
		colCfgDlg.setGestionnaireConfiguration(ActivatorVueListe.getDefault().getConfigurationVueDetaillee());
		
		String title = Messages.getString("ConfigListVueAction.2");
		String appliedFilter = ActivatorVueListe.getDefault().getFiltresProvider().getAppliedFilter();
		
		if (appliedFilter != null && !"".equals(appliedFilter.trim())) {
			title += " [" + appliedFilter + "]";
		}
		
		colCfgDlg.setText(title);
		colCfgDlg.open();		
	}
}
