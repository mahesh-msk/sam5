package com.faiveley.samng.vuetabulaire.ihm.actions.progbar;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.vuetabulaire.ihm.actions.Messages;
import com.faiveley.samng.vuetabulaire.ihm.actions.MultipleExportsAction;

public class BarreProgressionExportMultiple extends Job implements
		PropertyChangeListener {
	MultipleExportsAction mea;

	public IProgressMonitor monitor;

	public BarreProgressionExportMultiple(MultipleExportsAction mea) {
		super(com.faiveley.samng.vuetabulaire.ihm.actions.Messages
				.getString("TabulaireExportExport.1")); //$NON-NLS-1$
		this.setUser(true);
		this.mea = mea;
		ActivatorData.getInstance().getVpExportMult()
				.addPropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		this.monitor.worked((Integer) (arg0.getNewValue()));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {		
		this.monitor = monitor;
		monitor.setCanceled(false);
		monitor.beginTask(
				com.faiveley.samng.vuetabulaire.ihm.actions.progbar.Messages
						.getString("ProgressBar.1"), 100); //$NON-NLS-1$
		List<String> cheminsFichier = mea.getCheminsFichier();

		int i = 0;
		ActivatorData.getInstance().getVpExportMult().reset() ;
		
		while (i < cheminsFichier.size() && !monitor.isCanceled()) {
			DistinctionExtension.setEnableFormats();
			BridageFormats.getInstance().initialiseValides();
			ActivatorData.getInstance().getListRunFileToMultipleExport()
					.add(cheminsFichier.get(i));
			BridageFormats.setGestionConflitExtension(true);
			BridageFormats.getInstance().setFormatFichierOuvert(null);
			try {
				mea.exporterFichier(cheminsFichier.get(i),
						mea.getSelFilterName(), monitor);
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				i++;
				int prog = i * 100 / cheminsFichier.size();
				ActivatorData.getInstance().getVpExportMult()
						.setValeurProgressBar(prog);
				System.out.println(prog);
			}

		}

		ActivatorData.getInstance().getVpExportMult().setValeurProgressBar(100);
		monitor.done();

		ActivatorData.getInstance().getVpExportMult()
				.removePropertyChangeListener(this);

		if (!monitor.isCanceled()) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					MessageBox msgBox2 = new MessageBox(mea.window.getShell(),
							SWT.ICON_INFORMATION);
					msgBox2.setText(Messages
							.getString("MultipleExportsAction.11")); //$NON-NLS-1$
					msgBox2.setMessage(Messages
							.getString("MultipleExportsAction.12")); //$NON-NLS-1$
					msgBox2.open();
				}
			});
		}
		
		Activator.getDefault().release();
		ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
		ActivatorData.getInstance().notifyDataListeners();						

		return Status.OK_STATUS;
	}
}