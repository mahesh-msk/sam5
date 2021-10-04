package com.faiveley.samng.principal.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ApplicationActionBarAdvisor;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IDuplicationFiltreListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.ihm.progbar.ValeurProgBar;
import com.faiveley.samng.principal.ihm.vues.VueData;
import com.faiveley.samng.principal.ihm.vues.VueProgressBar;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.AbstractProviderFiltre;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.VbvsProvider;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.multimedia.MultimediaFile;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.GestionnaireBaseFiltres;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activator.ActivatorDataExplorer;

public class ActivatorData implements Cloneable {
	/* Constante définie ici et non pas dans le plugin VueTabulairePlugin, car doit être accessible depuis la méthode notifyDuplicateFiltreListeners */
	public static final String TABULAR_VUE_FILTRE_ID = "SAMNG.Vue.Filtre.TabularVueFiltre";
	
	private static ActivatorData instance = new ActivatorData();
	
	protected ActivatorData() {
		this.vpExportExplorer=  new ValeurProgBar();
	}
	
	public static ActivatorData getInstance() {		
		if (ActivationExplorer.getInstance().isActif()) {
			return ActivatorDataExplorer.getInstance();
		}
		
		return instance;
	}
	
	@Override
	public ActivatorData clone() throws CloneNotSupportedException {
		return (ActivatorData) super.clone();
	}
	
	private String filename;
	private DecompressedFile decompressedFile;
	private boolean isCompressedFile;
	private List<IDataChangedListener> listeners = new ArrayList<IDataChangedListener>(0);
	private List<IRepereChangedListener> listenersRepere = new ArrayList<IRepereChangedListener>(0);
	private List<IDuplicationFiltreListener> listenersDuplication = new ArrayList<IDuplicationFiltreListener>(0);	
	private ValeurProgBar vp;
	private ValeurProgBar vpExportMult;
	private ValeurProgBar vpExportExplorer;
	private VueProgressBar progrBar;
	private VueData vueData;
	private GestionnaireBaseFiltres baseFiltersMng;

	private GestionnaireMarqueurs markersMng = new GestionnaireMarqueurs();

	private List<MultimediaFile> multimediaFiles = null;
	private boolean multimediaFileAlone = false;
	
	public GestionnaireBaseFiltres getBaseFiltersMng() {
		return baseFiltersMng;
	}

	public void setBaseFiltersMng(GestionnaireBaseFiltres baseFiltersMng) {
		this.baseFiltersMng = baseFiltersMng;
	}

	private VbvsProvider vbvsMng;

	private ApplicationActionBarAdvisor applicationActionBarAdvisor;

	private boolean correctionTempsApplied = false;

	private List<AbstractProviderFiltre> listeGestionnairesFiltres = new ArrayList<AbstractProviderFiltre>(0);

	private ArrayList<Action> ListMenuAction = new ArrayList<Action>(0);

	private Hashtable<String,Object> poolDonneesVues = new Hashtable<String, Object>();

	private Message selectedMsg = null;

	private ArrayList<String> listRunFileToMultipleExport = new ArrayList<String>(0);

	private int tailleMoyenneCaractere = 0;
	private int espaceMoyenCaractere=0;
	
	private int selectionVueTabulaire=-1;
	private int selectionVueListe = -1;
	
	public String getAbsoluteFileName() {
		return this.filename;
	}
	
	public void setAbsoluteFileName(String filename) {
		this.filename = filename;
	}

	public VueProgressBar getProgressBar() {
		return this.progrBar;
	}

	public GestionnaireBaseFiltres getGestionnaireBaseFiltres() {
		return this.baseFiltersMng;
	}

	public GestionnaireMarqueurs getGestionnaireMarqueurs() {
		return this.markersMng;
	}

	
	public VbvsProvider getProviderVBVs() {
		return this.vbvsMng;
	}
	
	public VbvsProvider getVbvsMng() {
		return vbvsMng;
	}

	public void setVbvsMng(VbvsProvider vbvsMng) {
		this.vbvsMng = vbvsMng;
	}
	
	public ArrayList<String> getListRunFileToMultipleExport() {
		return listRunFileToMultipleExport;
	}

	public void setListRunFileToMultipleExport(
			ArrayList<String> listRunFileToMultipleExport) {
		this.listRunFileToMultipleExport = listRunFileToMultipleExport;
	}

	public ArrayList<Action> getListMenuAction() {
		return ListMenuAction;
	}

	public Hashtable<String,Object> getPoolDonneesVues() {
		return poolDonneesVues;
	}

	public void setPoolDonneesVues(Hashtable<String,Object> poolDonneesVues) {
		this.poolDonneesVues = poolDonneesVues;
	}
	
	public ValeurProgBar getVpExportMult() {
		return vpExportMult;
	}

	public void setVpExportMult(ValeurProgBar vpExportMult) {
		this.vpExportMult = vpExportMult;
	}
	
	public int getSelectionVueTabulaire() {
		return selectionVueTabulaire;
	}

	public void setSelectionVueTabulaire(int selectionVueTabulaire) {
		this.selectionVueTabulaire = selectionVueTabulaire;
	}
	
	public int getSelectionVueListe() {
		return selectionVueListe;
	}

	public void setSelectionVueListe(int selectionVueListe) {
		this.selectionVueListe = selectionVueListe;
	}
	
	public List<AbstractProviderFiltre> getListeGestionnairesFiltres() {
		return listeGestionnairesFiltres;
	}

	public void setListeGestionnairesFiltres(List<AbstractProviderFiltre> listeGestionnairesFiltres) {
		this.listeGestionnairesFiltres = listeGestionnairesFiltres;
	}

	public Message getSelectedMsg() {
		return selectedMsg;
	}

	public void setSelectedMsg(Message selectedMsg) {
		this.selectedMsg = selectedMsg;
	}

	public ValeurProgBar getVp() {
		return vp;
	}

	public void setVp(ValeurProgBar vp) {
		this.vp = vp;
	}

	public int getEspaceMoyenCaractere() {
		return espaceMoyenCaractere;
	}

	public void setEspaceMoyenCaractere(int espaceMoyenCaractere) {
		this.espaceMoyenCaractere = espaceMoyenCaractere;
	}

	public int getTailleMoyenneCaractere() {
		return tailleMoyenneCaractere;
	}

	public void setTailleMoyenneCaractere(int tailleMoyenneCaractere) {
		this.tailleMoyenneCaractere = tailleMoyenneCaractere;
	}
	
	public boolean isCorrectionTempsApplied() {
		return correctionTempsApplied;
	}

	public void setCorrectionTempsApplied(boolean correctionTempsApplied) {
		this.correctionTempsApplied = correctionTempsApplied;

		ActivatorData.getInstance().getPoolDonneesVues().put("correctVueTabAffichee", new Boolean(true));
		ActivatorData.getInstance().getPoolDonneesVues().put("correctVueListeAffichee", new Boolean(true));
	}
	
	/**
	 * @return the parcours
	 */
	public VueData getVueData() {
		return this.vueData;
	}
	
	public boolean isFileEmpty() {
		return this.vueData.isEmpty() && this.multimediaFiles == null;
	}
	
	/**
	 * @param parcours, the parcours to set
	 */
	public void setParcoursData(AParcoursComposant parcours) {
		this.vueData.releaseParcoursData();
		this.vueData.loadParcoursData((ParcoursComposite) parcours);
		
		if (this.applicationActionBarAdvisor != null) {
			this.applicationActionBarAdvisor.setActionsEnabled(true);
			
			if (!ActivatorData.getInstance().hasMultimediaFiles()) {
				for (Action action : ActivatorData.getInstance().getListMenuAction()) {
					if (action instanceof VueAction && ((VueAction) action).getViewId().equals("SAMNG.Vue.Multimedia")) {
						action.setEnabled(false);
					}
				}
			}
			
			ActivatorData.getInstance().notifyDataListeners();
		}
	}

	/**
	 * Sets the application bar advisor
	 * 
	 * @param applicationActionBarAdvisor
	 */
	public void setBarAdvisor(ApplicationActionBarAdvisor applicationActionBarAdvisor) {
		this.applicationActionBarAdvisor = applicationActionBarAdvisor;
	}

	public ApplicationActionBarAdvisor getBarAdvisor() {
		return this.applicationActionBarAdvisor;
	}

	public GestionnaireMarqueurs getMarkersMng() {
		return markersMng;
	}

	public void setMarkersMng(GestionnaireMarqueurs markersMng) {
		this.markersMng = markersMng;
	}

	public void setVueData(VueData vueData) {
		this.vueData = vueData;
	}

	public VueProgressBar getProgrBar() {
		return progrBar;
	}

	public void setProgrBar(VueProgressBar progrBar) {
		this.progrBar = progrBar;
	}
	
	/**
	 * Adds a listener
	 * 
	 * @param listener
	 */
	public synchronized void addDataListener(IDataChangedListener listener) {
		if (listener != null) {
			int nbListeners = this.listeners.size();
			boolean trouve = false;
			int i = 0;
			int indiceListener = 0;
			
			while (!trouve && i < nbListeners) {
				if (this.listeners.get(i).getClass().equals(listener.getClass())){
					trouve = true;
					indiceListener = i;
				}
				
				i++;
			}
			
			if (!trouve) {
				this.listeners.add(listener);
			} else {
				this.listeners.remove(indiceListener);
				this.listeners.add(listener);
			}
		}
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 */
	public synchronized void removeDataListener(IDataChangedListener listener) {
		if (listener != null) {
			this.listeners.remove(listener);
		}
	}

	/**
	 * Notify the listeners
	 */
	public synchronized void notifyDataListeners() {
		Display display = Activator.getDefault().getWorkbench().getDisplay();

		if (display != null) {
			if (display.getThread() == Thread.currentThread()) {
				IDataChangedListener listener=null;
				int nbListeners = this.listeners.size();
				
				for (int i = 0; i < nbListeners; i++) {
					try {
						listener = this.listeners.get(i);
						listener.onDataChange();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			} else {
				display.asyncExec(new Runnable() {
					public void run() {
						try {
							for (IDataChangedListener listener : listeners) {
								listener.onDataChange();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}

	/**
	 * Return true if the listener was already added
	 * 
	 * @param listener
	 * @return
	 */
	public boolean isDataListening(IDataChangedListener listener) {
		return this.listeners != null ? this.listeners.contains(listener) : false;
	}


	public List<IRepereChangedListener> getListenersRepere() {
		return listenersRepere;
	}

	public void setListenersRepere(List<IRepereChangedListener> listenersRepere) {
		this.listenersRepere = listenersRepere;
	}
	
	/**
	 * Return true if the listener was already added
	 * 
	 * @param listener
	 * @return
	 */
	public boolean isRepereListening(IRepereChangedListener listener) {
		return this.listenersRepere != null ? this.listenersRepere.contains(listener) : false;
	}
	
	/**
	 * Removes a dupplication listener
	 * 
	 * @param listener
	 */
	public synchronized void removeDuplicationFiltreListener(IDuplicationFiltreListener listener) {
		if (listener != null) {
			this.listenersDuplication.remove(listener);
		}
	}
	
	/**
	 * Notify the listeners
	 */
	public synchronized void notifyRepereListeners(boolean added, TypeRepere... reper) {
		if (added) {
			for (IRepereChangedListener listener : this.listenersRepere) {
				listener.onRepereAdded(reper);
			}
		} else {
			for (IRepereChangedListener listener : this.listenersRepere) {
				listener.onRepereRemoved(reper);
			}
		}
		
		ActivatorData.getInstance().getProgressBar().stop();
	}
	
	/**
	 * Adds a dupplication listener
	 * 
	 * @param listener
	 */
	public synchronized void addDuplicationFiltreListener(IDuplicationFiltreListener listener) {
		if (listener != null && !this.listenersDuplication.contains(listener)) {
			this.listenersDuplication.add(listener);
		}
	}
	
	/**
	 * Adds a listener
	 * 
	 * @param listener
	 */
	public synchronized void addRepereListener(IRepereChangedListener listener) {
		if (listener != null) {
			int nbListeners = this.listenersRepere.size();
			boolean trouve = false;
			int i = 0;
			
			while(!trouve && i<nbListeners) {
				if(this.listenersRepere.get(i).getClass().equals(listener.getClass())) {
					trouve = true;
				}
				
				i++;
			}
			
			if (!trouve) {
				this.listenersRepere.add(listener);
			}

			if (!listenersRepere.contains(listener)) {
				this.listenersRepere.add(listener);
			}
		}
	}

	/**
	 * Removes a listener
	 * 
	 * @param listener
	 */
	public synchronized void removeRepereListener(IRepereChangedListener listener) {
		if (listener != null) {
			this.listenersRepere.remove(listener);
		}
	}

	/**
	 * Méthode qui averti les fenetre receptrice d'une duplication de filtre
	 * 
	 * @param filtre
	 */
	public synchronized void notifyDuplicateFiltreListeners(List<AFiltreComposant> listeFiltres) {
		/* Attention: correctif non robuste.
		 * Le cas où aucun listener n'a été enregistré (liste vide) correspond à une duplication d'un filtre (graphique vers tabulaire). */
		if (this.listenersDuplication.size() == 0) {
			/* La vue filtre tabulaire n'a jamais été ouverte pour le fichier courant.
			 * Son ouverture est nécessaire afin d'instancier l'objet vue qui faut office de listener. */
			
			IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
			try {
				window.getActivePage().showView(TABULAR_VUE_FILTRE_ID, null, IWorkbenchPage.VIEW_CREATE);
			} catch (PartInitException e) {
				e.printStackTrace();
			}
			
			/* À présent, un listener a été enregistré et la duplication est possible */
		}
		
		for (IDuplicationFiltreListener listener : this.listenersDuplication) {
			listener.onFiltreDuplique(listeFiltres);
		}
	}
	
	public List<IDuplicationFiltreListener> getListenersDuplication() {
		return listenersDuplication;
	}

	public void setListenersDuplication(List<IDuplicationFiltreListener> listenersDuplication) {
		this.listenersDuplication = listenersDuplication;
	}

	public ValeurProgBar getVpExportExplorer() {
		return vpExportExplorer;
	}

	public void setVpExportExplorer(ValeurProgBar vpExportExplorer) {
		this.vpExportExplorer = vpExportExplorer;
	}
	
	public List<MultimediaFile> getMultimediaFiles() {
		return this.multimediaFiles;
	}
	
	public void setMultimediaFileAlone(File file) {
		addMultimediaFile(file, null, null);
	}
	
	public void addMultimediaFile(File file, Message msg, Integer indexInParcoursList) {
		if (this.multimediaFiles == null) {
			this.multimediaFiles = new ArrayList<MultimediaFile>();
		}
		
		MultimediaFile multiMediaFile = new MultimediaFile(file, msg, indexInParcoursList);

		if (multiMediaFile != null) {
			this.multimediaFiles.add(multiMediaFile);
			this.multimediaFileAlone = msg == null;
		}
	}
	
	public void clearMultimediaFiles() {
		this.multimediaFiles = null;
		this.multimediaFileAlone = false;
	}
	
	public boolean hasMultimediaFiles() {
		if (this.multimediaFiles == null) {
			return false;
		}
		
		return this.multimediaFiles.size() > 0;
	}
	
	public boolean isMultimediaFileAlone() {
		return this.multimediaFileAlone;
	}

	public boolean isCompressedFile() {
		return this.isCompressedFile;
	}
	
	public void setCompressedFile(boolean isCompressedFile) {
		this.isCompressedFile = isCompressedFile;
	}

	public DecompressedFile getDecompressedFile() {
		return decompressedFile;
	}

	public void setDecompressedFile(DecompressedFile decompressedFile) {
		this.decompressedFile = decompressedFile;
	}

	public void clearCompressedFile() {
		this.isCompressedFile = false;
		this.decompressedFile = null;
	}
	
}