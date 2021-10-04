package com.faiveley.samng.vuebinaire.ihm;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.vuebinaire.ihm.vues.BinaryRow;
import com.faiveley.samng.vuebinaire.ihm.vues.guiFactory.AFabriqueVueBinaire;

public class BarreProgressionDialog implements IRunnableWithProgress,PropertyChangeListener{
	public IProgressMonitor monitor;
	private List<BinaryRow> lignesTableaux;
	private AFabriqueVueBinaire fabriqueVue;
	
	
public BarreProgressionDialog(String name,AFabriqueVueBinaire fabriqueVue) {
		
	
		this.lignesTableaux= new ArrayList<BinaryRow>(0);;
		this.fabriqueVue = fabriqueVue;
		ActivatorData.getInstance().getVp().addPropertyChangeListener(this);
		
	}


	public synchronized void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		ActivatorData.getInstance().getVp().reset();
		this.monitor=monitor;
		monitor.beginTask(com.faiveley.samng.principal.ihm.progbar.Messages.getString("ProgressBar.0"), 100);  //$NON-NLS-1$
		monitor.setCanceled(false);
		
		
		if(ActivatorData.getInstance().getVueData().getDataTable()!=null)
		this.lignesTableaux=this.fabriqueVue.remplirTableau(monitor);
		ActivatorData.getInstance().getVp().setValeurProgressBar(100);
		this.monitor.done(); 
		ActivatorData.getInstance().getVp().removePropertyChangeListener(this);
		
	}


	public void propertyChange(PropertyChangeEvent evt) {

		this.monitor.worked((Integer)(evt.getNewValue()));
		
	}


	public List<BinaryRow> getLignesTableaux() {
		return lignesTableaux;
	}


	public void setLignesTableaux(List<BinaryRow> lignesTableaux) {
		this.lignesTableaux = lignesTableaux;
	}

}
