package com.faiveley.samng.principal.ihm.progbar;


import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.AVueTableContentProvider;

public class BarreProgressionDialog implements IRunnableWithProgress,
		PropertyChangeListener {
	public IProgressMonitor monitor;
	public AVueTableContentProvider contentProvider;

	public BarreProgressionDialog(String name, AVueTableContentProvider contentprovider) {
		ActivatorData.getInstance().getVp().addPropertyChangeListener(this);
		this.contentProvider=contentprovider;
	}

	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		ActivatorData.getInstance().getVp().reset();
		this.monitor = monitor;
		monitor.beginTask(com.faiveley.samng.principal.ihm.progbar.Messages.getString("ProgressBar.0"), 100); //$NON-NLS-1$
		monitor.setCanceled(false);
		contentProvider.loadContent(this.monitor);
		ActivatorData.getInstance().getVp().setValeurProgressBar(100);
		this.monitor.done();
		ActivatorData.getInstance().getVp().removePropertyChangeListener(this);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		this.monitor.worked((Integer) (evt.getNewValue()));
	}
}
