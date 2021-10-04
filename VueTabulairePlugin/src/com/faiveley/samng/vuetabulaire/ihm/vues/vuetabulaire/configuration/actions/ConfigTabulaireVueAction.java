package com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.configuration.actions;

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
import com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire;
import com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire.VueTabulaire;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ConfigTabulaireVueAction extends VueAction {
	private boolean usesShortNames;
	
	public ConfigTabulaireVueAction() {
		super(null, ICommandIds.CMD_OPEN_COFIG_TABULAIRE, Messages.getString("ConfigTabulaireVueAction.0"), null,com.faiveley.samng.vuetabulaire.ihm.ActivatorVueTabulaire //$NON-NLS-1$
				.getImageDescriptor("/icons/vueTabulaire/vue_tabulaire_gerer_colonnes.png"),true); //$NON-NLS-1$
		this.usesShortNames = false;
	}
	
	@Override
	public void run() {
		GestionnaireVueListeBase cfgMng = ActivatorVueTabulaire.getDefault().getConfigurationMng();
		ColonnesConfigDialog colCfgDlg = new ColonnesConfigDialog(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell());
		colCfgDlg.setGestionnaireConfiguration(cfgMng);
		String title = Messages.getString("ConfigTabulaireVueAction.2"); //$NON-NLS-1$
		String appliedFilter = ActivatorVueTabulaire.getDefault().getFiltresProvider().getAppliedFilter();
		if(appliedFilter != null && !"".equals(appliedFilter.trim())) { //$NON-NLS-1$
			title += " [" + appliedFilter + "]"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		colCfgDlg.setText(title);
		int iRet = colCfgDlg.open(usesShortNames);
		if(iRet == SWT.OK) {
			ConfigurationColonne[] colsCfg = colCfgDlg.getCurrentConfigurations();
			int changedColumns = cfgMng.updateColumnsConfigurations(colsCfg);
			cfgMng.setListAllColumns(colCfgDlg.isListAllColumns());
			if(changedColumns > 0) {
				VueTabulaire vueTabulaire = getVueTabulaire();
				if(vueTabulaire == null) {	
					return;				//nothing more to do - the tabulaire view is not opened
				}
				ActivatorData.getInstance().getPoolDonneesVues().put("changeColVueTab", new Boolean(true));
				//just a data change is enough
				vueTabulaire.reloadTable();
			}else{
			ActivatorData.getInstance().getPoolDonneesVues().put("changeColVueTab", new Boolean(false));
			}
		}
	}
	
	private VueTabulaire getVueTabulaire() {
		IWorkbenchWindow window = ActivatorVueTabulaire.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IViewReference[] ivr = window.getActivePage().getViewReferences();
		VueTabulaire vueTabulaire;
		for (int t = 0; t < ivr.length; t++) {
			if (ivr[t].getId().equals(VueTabulaire.ID)) {
				vueTabulaire = (VueTabulaire)ivr[t].getPart(false);
				return vueTabulaire;
			}
		}
		return null;
	}

	public void usesShortNames(boolean value) {
		this.usesShortNames = value;		
	}

}
