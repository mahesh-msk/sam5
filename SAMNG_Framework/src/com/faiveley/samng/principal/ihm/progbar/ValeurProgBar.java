package com.faiveley.samng.principal.ihm.progbar;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import com.faiveley.samng.principal.data.ActivatorData;

public class ValeurProgBar {
	private int valeurProgressBar=-1;
	private int indiceFile=0;
	private int nbFiles=0;
	private PropertyChangeSupport listenerProgBar = new PropertyChangeSupport(this);

	public int getValeurProgressBar() {
		return valeurProgressBar;
	}

	public void setValeurProgressBar(int valeurProgressBar) {
		if (valeurProgressBar>=0 && valeurProgressBar<=100 && 
				valeurProgressBar!=this.valeurProgressBar &&
				valeurProgressBar>this.valeurProgressBar) {			
			listenerProgBar.firePropertyChange ("valeurProgBar", null, valeurProgressBar-this.valeurProgressBar);
			this.valeurProgressBar = valeurProgressBar;
		}
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		listenerProgBar.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		listenerProgBar.removePropertyChangeListener(l);
	}

	public void reset(){
		this.valeurProgressBar=0;
	}

	public int getIndiceFile() {
		return indiceFile;
	}

	public void setIndiceFile(int indiceFile) {
		this.indiceFile = indiceFile;
	}

	public void incrementIndiceFile() {
		this.indiceFile++;
	}

	public int getNbFiles() {
		return nbFiles;
	}

	public void setNbFiles(int nbFiles) {
		this.nbFiles = nbFiles;
	}

	public void incrementNbFiles() {
		this.nbFiles++;
	}

	public void setValeurProgressBar(int pos, int tailleTableauMessage) {
		try {
			if (tailleTableauMessage!=0 
					&& ActivatorData.getInstance().getVpExportExplorer().getNbFiles()!=0) {

				this.setValeurProgressBar(
						(int)((long)pos*100/tailleTableauMessage)
						/ActivatorData.getInstance().getVpExportExplorer().getNbFiles()
						+ 
						ActivatorData.getInstance().getVpExportExplorer().getIndiceFile()*100
						/ActivatorData.getInstance().getVpExportExplorer().getNbFiles()
						);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
}
