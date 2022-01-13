package com.faiveley.samng.vueliste.ihm.vues.vueliste.configuration.action;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;
import com.faiveley.samng.principal.ihm.vues.configuration.ColonnesConfigDialog;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.ihm.vues.configuration.GestionnaireVueListeBase;
import com.faiveley.samng.vueliste.ihm.ActivatorVueListe;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.VueListe;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ConfigListVueAction extends VueAction {
	public ConfigListVueAction() {
		super(null, ICommandIds.CMD_OPEN_COFIG_LIST, Messages.getString("ConfigListVueAction.0"), null,  com.faiveley.samng.vueliste.ihm.ActivatorVueListe.getImageDescriptor("/icons/vueListe/vue_liste_gerer_colonnes.png"),true); //$NON-NLS-1$
	}
	
	@Override
	public void run() {
		GestionnaireVueListeBase cfgMng = ActivatorVueListe.getDefault().getConfigurationMng();
		ColonnesConfigDialog colCfgDlg = new ColonnesConfigDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
		colCfgDlg.setGestionnaireConfiguration(cfgMng);
		String title = Messages.getString("ConfigListVueAction.1"); //$NON-NLS-1$
		String appliedFilter = ActivatorVueListe.getDefault().getFiltresProvider().getAppliedFilter();
		if(appliedFilter != null && !"".equals(appliedFilter.trim())) { //$NON-NLS-1$
			title += " [" + appliedFilter + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		colCfgDlg.setText(title);
		int iRet = colCfgDlg.open(false);
		if(iRet == SWT.OK) {
			ConfigurationColonne[] colsCfg = colCfgDlg.getCurrentConfigurations();
			int changedColumns = cfgMng.updateColumnsConfigurations(colsCfg);
			cfgMng.setListAllColumns(colCfgDlg.isListAllColumns());
			if(changedColumns > 0) {
				VueListe vueListe = getVueList();
				if(vueListe == null) {	
					return;	//nothing more to do - the list view is not opened
				}
				//just a data change is enough
				//vueListe.getTop().layout();
				ActivatorData.getInstance().getPoolDonneesVues().put("changeColVueList", Boolean.valueOf(true));
				vueListe.reloadTable();
			}
			else{
				ActivatorData.getInstance().getPoolDonneesVues().put("changeColVueList", Boolean.valueOf(false));
				}
		}
	}
	
	private VueListe getVueList() {
		IWorkbenchWindow window = ActivatorVueListe.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueListe vueListe;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueListe.ID)) {
				vueListe = (VueListe)ivr[t].getPart(false);
				return vueListe;
			}
		}
		return null;
	}
}
