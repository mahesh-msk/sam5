 package com.faiveley.samng.principal.ihm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarContributionItem;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.views.IViewDescriptor;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerEcranAction;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.actions.dialogs.RechercherDansFichierAction;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierFermerAction;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierOuvrirAction;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierQuitterAction;
import com.faiveley.samng.principal.ihm.actions.fichier.SauverRemarquesUtilisateurAction;
import com.faiveley.samng.principal.ihm.actions.fichier.VueDefautsAction;
import com.faiveley.samng.principal.ihm.actions.fichier.VueInfosParcoursAction;
import com.faiveley.samng.principal.ihm.actions.profil.ImporterProfilAction;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;
import com.faiveley.samng.principal.ihm.actions.vue.VueType;
import com.faiveley.samng.principal.ihm.preferences.PreferenceConstants;
import com.faiveley.samng.principal.ihm.vues.vuemarqueurs.VueMarqueurs;
import com.faiveley.samng.principal.ihm.vues.vuescorrections.VueCorrectionsDistance;
import com.faiveley.samng.principal.ihm.vues.vuescorrections.VueCorrectionsTemps;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.VueVbvs;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * An action bar advisor is responsible for creating, adding, and disposing of
 * the actions added to a workbench window. Each window will be populated with
 * new actions.
 */
public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
	// Actions - important to allocate these only in makeActions,
	// and then use them
	// in the fill methods. This ensures that the actions aren't recreated
	// when fillActionBars is called with FILL_PROXY.

	public static String MENU_PERSPECTIVE_ID = "perspective";

	private ArrayList<Action> actionExports = new ArrayList<Action>(0);
	
	/** */
	private IWorkbenchAction aboutAction;
	private Action helpAction;

	/** */
	private FichierOuvrirAction ouvrirAction;
	private FichierFermerAction fermerAction;
	private ImporterProfilAction importerProfil;
	private FichierQuitterAction quitterAction;
	
	private VueDefautsAction vueDefautsAction;
	private VueInfosParcoursAction vueInfosParcoursAction;
	private VueAction markersListAction;
	
	private SauverRemarquesUtilisateurAction saveRemarksAction;
	private CapturerVueAction capturerVueAction;
	private ImprimerVueAction imprimerVueAction;
	private ImprimerEcranAction imprimerEcranAction;
	
	/** */
	private Action ouvrirVueGraphiqueAction;
	private Action ouvrirVueTabulaireAction;
	private Action ouvrirVueListeAction;
	private Action ouvrirVueBinaireAction;
	private Action ouvrirVueMultimediaAction;
	private Action ouvrirVueExplorateurAction;
	public static boolean helpMe;
	
	/**
	 * @param configurer
	 *            1
	 */
	public ApplicationActionBarAdvisor(final IActionBarConfigurer configurer) {
		super(configurer);
		ActivatorData.getInstance().setBarAdvisor(this);
	}

	/**
	 * Menu Fichier.
	 * @param window
	 * @return menuMgr
	 */
	private MenuManager createFileMenu(final IWorkbenchWindow window) {
		MenuManager menuMgr = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.0"), VueType.FILE.value());
		createFileActions(window, menuMgr);
		
		return menuMgr;
	}
	
	private void createFileActions(final IWorkbenchWindow window, final IMenuManager managerFile) {
		managerFile.add(this.ouvrirAction);
		
		managerFile.add(this.fermerAction);
		this.fermerAction.setEnabled();
		
		managerFile.add(this.importerProfil);
		this.importerProfil.setEnabled(!ActivatorData.getInstance().isMultimediaFileAlone());
		
		managerFile.add(this.saveRemarksAction);
		this.saveRemarksAction.setEnabled(!GestionLineCommandParameters.isAnnot_Lect_seule());
		
		managerFile.add(new Separator());

		managerFile.add(new GroupMarker(IWorkbenchActionConstants.FILE_START));
		managerFile.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
		managerFile.add(new GroupMarker(IWorkbenchActionConstants.FILE_END));
		
		managerFile.add(new Separator());
	
		managerFile.add(this.imprimerEcranAction);
		this.imprimerEcranAction.setEnabled(this.imprimerEcranAction.isEnabled());
		
		managerFile.add(new Separator());
		
		IWorkbenchAction preference = ActionFactory.PREFERENCES.create(window);
		managerFile.add(preference);
		preference.setText(Messages.getString("ApplicationActionBarAdvisor.5"));
		
		managerFile.add(quitterAction);
	}

	/**
	 * Menu Edition.
	 * 
	 * @param window
	 * @return menuMgr
	 */
	private MenuManager createEditionMenu(final IWorkbenchWindow window) {
		MenuManager menuMgr = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.2"), VueType.EDITION.value());
		
		menuMgr.setRemoveAllWhenShown(true);
		createEditionActions(window, menuMgr);
		
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				createEditionActions(window, manager);
			}
		});
		
		return menuMgr;
	}
	
	private void createEditionActions(final IWorkbenchWindow window, IMenuManager menuMgr) {
		VueAction timeCorrectionsAction = new VueAction(window, ICommandIds.CMD_OPEN_VUE_TEMP_COR, Messages.getString("ApplicationActionBarAdvisor.21"), VueCorrectionsTemps.ID, null, true);
		VueAction distanceCorrectionsAction = new VueAction(window, ICommandIds.CMD_OPEN_VUE_DIST_COR, Messages.getString("ApplicationActionBarAdvisor.23"), VueCorrectionsDistance.ID, null, true);
		VueAction vbvsAction = new VueAction(window, ICommandIds.CMD_OPEN_VBV, Messages.getString("ApplicationActionBarAdvisor.20"), VueVbvs.ID, null, true);
		
		RechercherDansFichierAction searchInFile = new RechercherDansFichierAction(ICommandIds.CMD_OPEN_SEARCH_INFILE, Messages.getString("ApplicationActionBarAdvisor.8"), null);
		
		timeCorrectionsAction.setEnabled(TableSegments.getInstance().segmentsTempsValide());
		distanceCorrectionsAction.setEnabled(TableSegments.getInstance().segmentsDistanceValide());
		vbvsAction.setEnabled(!ActivatorData.getInstance().getVueData().isEmpty());
		searchInFile.setEnabled(!ActivatorData.getInstance().isMultimediaFileAlone());
		
		menuMgr.add(timeCorrectionsAction);
		menuMgr.add(distanceCorrectionsAction);
		menuMgr.add(new Separator());
		menuMgr.add(vbvsAction);
		menuMgr.add(searchInFile);
	}
	
	/**
	 * Menu Vue.
	 * 
	 * @param window
	 * @return menuMgr
	 */
	private MenuManager createViewMenu(final IWorkbenchWindow window) {
		MenuManager menuMgr = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.12"), VueType.VUE.value()); 
		
		createDataViewsActions(window, menuMgr);
		
		menuMgr.add(new Separator());
		
		menuMgr.add(this.markersListAction);
		this.markersListAction.setEnabled(this.vueInfosParcoursAction.isEnabled());
		
		menuMgr.add(this.vueInfosParcoursAction);
		this.vueInfosParcoursAction.setEnabled(this.vueInfosParcoursAction.isEnabled());

		menuMgr.add(this.vueDefautsAction);
		this.vueDefautsAction.setEnabled(this.vueDefautsAction.isEnabled());
		
		ActivatorData.getInstance().getListMenuAction().add(this.vueInfosParcoursAction);
		ActivatorData.getInstance().getListMenuAction().add(this.markersListAction);

		return menuMgr;
	}
	
	private void createDataViewsActions(final IWorkbenchWindow window, IMenuManager manager) {
        IViewDescriptor[] dataViewsDescriptors = ViewsDescriptorsProvider.getViewDescriptors(window, "SAMNG_Framework.DataViewsCategory"); //$NON-NLS-1$
        IViewDescriptor[] dataViewsDescriptorsSorted = new IViewDescriptor[6];
        String actionId;
        
        for (IViewDescriptor viewDescr: dataViewsDescriptors) {
            actionId = viewDescr.getId();
            int index = -1;
            
            if (actionId.equals("SAMNG.Vue.Binaire")) {
                index = 0;
            } else if (actionId.equals("SAMNG.Vue.Liste")) {
                index = 1;
            } else if (actionId.equals("SAMNG.Vue.Tabulaire")) {
                index = 2;
            } else if (actionId.equals("SAMNG.Vue.Graphique")) {
                index = 3;
            } else if (actionId.equals("SAMNG.Vue.Multimedia")) {
                index = 4;
            } else if (actionId.equals("SAMNG.Vue.VueExplorateur")) {
                index = 5;
            }
            
            if (index >= 0) {
                dataViewsDescriptorsSorted[index] = viewDescr;
            }
        }
        
        VueAction openDataVueAction;
        String actionLabel;
        
        for(IViewDescriptor viewDescr: dataViewsDescriptorsSorted) {
            actionId = viewDescr.getId();

			actionId = viewDescr.getId();
			actionLabel = viewDescr.getLabel();
			
			if (actionId.equals("SAMNG.Vue.VueExplorateur")) {
				actionLabel=Messages.getString("ApplicationActionBarAdvisor.57");
			}
			
			openDataVueAction = new VueAction(window, "", actionLabel, actionId, viewDescr.getImageDescriptor(),true);
			openDataVueAction.setToolTipText(actionLabel);
			
			if (actionId.equals("SAMNG.Vue.VueExplorateur")) {
				openDataVueAction.setEnabled(true);
			} else {
				openDataVueAction.setEnabled(false);
			}
			
			manager.add(openDataVueAction);
			openDataVueAction.setEnabled(openDataVueAction.isEnabled());
			ActivatorData.getInstance().getListMenuAction().add(openDataVueAction);
		}
	}

	public void manageMultiMediaActions() {		
		imprimerEcranAction.setEnabled(true);
	}
	
	public void setActionsEnabled(boolean enabled) {
		for (Action action : ActivatorData.getInstance().getListMenuAction()) {
			if (!(action instanceof VueAction) || !((VueAction)action).getViewId().equals("SAMNG.Vue.VueExplorateur")) {
				action.setEnabled(enabled) ;
			}
		}
	}

	/**
	 * Menu Aide.
	 * 
	 * @param window
	 * @return menuMgr
	 */
	private MenuManager createHelpMenu(final IWorkbenchWindow window) {
		MenuManager menuMgr = new MenuManager(Messages.getString("ApplicationActionBarAdvisor.29"),	IWorkbenchActionConstants.M_HELP);
		
		menuMgr.add(this.aboutAction);
		menuMgr.add(this.helpAction);
	
		return menuMgr;
	}

	public void addactionkey(Action act){
		register(act);
	}
	
	public void ouvrirDocPdf(File f, String s) {
		if (helpMe) {
			return;
		}
		
		if (f.getName().contains(s)) {
			try {
				java.awt.Desktop.getDesktop().open(f);
				helpMe = true;
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		} else {
			File[] liste_fils = f.listFiles();
	
			if (liste_fils != null) {
				for(File fils : liste_fils) {
					ouvrirDocPdf(fils, s);
				}
			}
		}
	}
	
	protected final void makeActions(final IWorkbenchWindow window) {
		this.aboutAction = ActionFactory.ABOUT.create(window);
		this.aboutAction.setText(Messages.getString("aproposName"));
		register(this.aboutAction);
		
		helpAction = new Action() {
			public void run() {
				helpMe = false;
				ouvrirDocPdf(new File(RepertoiresAdresses.doc),"ZA550403.800-"+Activator.getDefault().getPreferenceStore().getString(PreferenceConstants.LANG_CHOICE));
			}
		
		};
		
		helpAction.setId(ICommandIds.CMD_HELP);
		helpAction.setText(Messages.getString("Aide.0"));
		helpAction.setToolTipText(Messages.getString("Aide.0"));
		helpAction.setActionDefinitionId(ICommandIds.CMD_HELP);
		
		register(this.helpAction);

		/** Menu Fichier* */
		// Ouvrir
		this.ouvrirAction = new FichierOuvrirAction(window,Messages.getString("ApplicationActionBarAdvisor.31"));
		this.ouvrirAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.32"));
		register(this.ouvrirAction);
		
		// Fermer
		this.fermerAction = new FichierFermerAction(window, Messages.getString("ApplicationActionBarAdvisor.33"));
		this.fermerAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.34"));
		register(this.fermerAction);
		
		// Importer un profil
		this.importerProfil = new ImporterProfilAction(window, Messages.getString("ApplicationActionBarAdvisor.56"));
		this.importerProfil.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.56"));
		register(this.importerProfil);
		
		// Quitter
		this.quitterAction = new FichierQuitterAction(window, Messages.getString("ApplicationActionBarAdvisor.35"));
        this.quitterAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.35"));
        register(quitterAction);
		
		this.vueDefautsAction = new VueDefautsAction(window, Messages.getString("ApplicationActionBarAdvisor.36"));
		this.vueDefautsAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.37"));
		register(this.vueDefautsAction);
		
		this.vueInfosParcoursAction = new VueInfosParcoursAction(window, Messages.getString("ApplicationActionBarAdvisor.38"));
		this.vueInfosParcoursAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.39"));
		register(this.vueInfosParcoursAction);
		
		this.markersListAction = new VueAction(window, ICommandIds.CMD_OPEN_MARQUERS, Messages.getString("ApplicationActionBarAdvisor.18"), VueMarqueurs.ID, com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_annotations.png"), true);
		register(this.markersListAction);
		
		this.saveRemarksAction = new SauverRemarquesUtilisateurAction(Messages.getString("ApplicationActionBarAdvisor.40"));
		this.saveRemarksAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.41"));
	
		// Menu de capture d'une vue
		this.capturerVueAction = new CapturerVueAction(window,Messages.getString("ApplicationActionBarAdvisor.42"));
		this.capturerVueAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.43"));
		register(this.capturerVueAction);
		
		// Menu d'impression d'une vue
		this.imprimerVueAction = new ImprimerVueAction(window,Messages.getString("ApplicationActionBarAdvisor.44"));
		this.imprimerVueAction.setToolTipText(Messages.getString("ApplicationActionBarAdvisor.45"));
		register(this.imprimerVueAction);
		
		this.imprimerEcranAction = new ImprimerEcranAction(window,com.faiveley.samng.principal.ihm.actions.print.Messages.getString("ImpressionVueTableau.9"));
		this.imprimerEcranAction.setToolTipText(com.faiveley.samng.principal.ihm.actions.print.Messages.getString("ImpressionVueTableau.9"));
		register(this.imprimerEcranAction);
		
		ouvrirVueGraphiqueAction = new VueAction(window, ICommandIds.CMD_OPEN, Messages.getString("ApplicationActionBarAdvisor.46"), "SAMNG.Vue.Graphique", null, false);
		ouvrirVueGraphiqueAction.setImageDescriptor(null);
		ouvrirVueGraphiqueAction.setEnabled(false);
		ouvrirVueGraphiqueAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_graphique.png"));
		register(this.ouvrirVueGraphiqueAction);
		
		ouvrirVueTabulaireAction = new VueAction(window, ICommandIds.CMD_OPEN, Messages.getString("ApplicationActionBarAdvisor.49"), "SAMNG.Vue.Tabulaire", null, false);
		ouvrirVueTabulaireAction.setImageDescriptor(null);
		ouvrirVueTabulaireAction.setEnabled(false);
		ouvrirVueTabulaireAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_tabulaire.png"));
		register(this.ouvrirVueTabulaireAction);
		
		ouvrirVueListeAction = new VueAction(window, ICommandIds.CMD_OPEN, Messages.getString("ApplicationActionBarAdvisor.52"), "SAMNG.Vue.Liste", null,false);
		ouvrirVueListeAction.setImageDescriptor(null);
		ouvrirVueListeAction.setEnabled(false);
		ouvrirVueListeAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_liste.png"));
		register(this.ouvrirVueListeAction);
		
		ouvrirVueBinaireAction = new VueAction(window, ICommandIds.CMD_OPEN, Messages.getString("ApplicationActionBarAdvisor.55"), "SAMNG.Vue.Binaire", null,false);
		ouvrirVueBinaireAction.setEnabled(false);
		ouvrirVueBinaireAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_binaire.png"));
		register(this.ouvrirVueBinaireAction);
		
		ouvrirVueMultimediaAction = new VueAction(window, ICommandIds.CMD_OPEN, Messages.getString("ApplicationActionBarAdvisor.53"), "SAMNG.Vue.Multimedia", null, false);
		ouvrirVueMultimediaAction.setEnabled(false);
		ouvrirVueMultimediaAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_multimedia.png"));
		register(this.ouvrirVueMultimediaAction);
		
		ouvrirVueExplorateurAction = new VueAction(window, ICommandIds.CMD_OPEN, Messages.getString("ApplicationActionBarAdvisor.57"), "SAMNG.Vue.VueExplorateur", null,false);
		ouvrirVueExplorateurAction.setEnabled(true);
		ouvrirVueExplorateurAction.setImageDescriptor(com.faiveley.samng.principal.ihm.Activator.getImageDescriptor("/icons/menu/appli_vue_explorateur.png"));
		register(this.ouvrirVueExplorateurAction);
	}

	/**
	 * fillMenuBar.
	 * 
	 * @param menuBar
	 */
	protected final void fillMenuBar(final IMenuManager menuBar) {
		getActionBarConfigurer().getMenuManager().removeAll();
		IWorkbenchWindow window = getActionBarConfigurer().getWindowConfigurer().getWindow();

		menuBar.add(createFileMenu(window));
		menuBar.add(createEditionMenu(window));
		menuBar.add(createViewMenu(window));

		menuBar.add(createHelpMenu(window));
	}
	
	/**
	 * fillCoolBar.
	 * 
	 * @param coolBar
	 */
	protected final void fillCoolBar(final ICoolBarManager coolBar) {
		//getActionBarConfigurer().getCoolBarManager().removeAll();
		IToolBarManager toolbar1 = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		toolbar1.add(this.ouvrirAction);
		toolbar1.add(this.saveRemarksAction);
		toolbar1.add(this.imprimerEcranAction);
		IToolBarManager toolbar2 = new ToolBarManager(SWT.FLAT | SWT.RIGHT);
		
		toolbar2.add(this.ouvrirVueExplorateurAction);
        toolbar2.add(this.ouvrirVueBinaireAction);
        toolbar2.add(this.ouvrirVueListeAction);
        toolbar2.add(this.ouvrirVueTabulaireAction);
        toolbar2.add(this.ouvrirVueGraphiqueAction);
        toolbar2.add(this.ouvrirVueMultimediaAction);
        toolbar2.add(this.vueInfosParcoursAction);
		
		ActivatorData.getInstance().getListMenuAction().add(this.saveRemarksAction);
		ActivatorData.getInstance().getListMenuAction().add(this.imprimerEcranAction);
		
        ActivatorData.getInstance().getListMenuAction().add(this.ouvrirVueBinaireAction);
        ActivatorData.getInstance().getListMenuAction().add(this.ouvrirVueListeAction);
        ActivatorData.getInstance().getListMenuAction().add(this.ouvrirVueTabulaireAction);
        ActivatorData.getInstance().getListMenuAction().add(this.ouvrirVueGraphiqueAction);
        ActivatorData.getInstance().getListMenuAction().add(this.ouvrirVueMultimediaAction);
        ActivatorData.getInstance().getListMenuAction().add(this.ouvrirVueExplorateurAction);
        ActivatorData.getInstance().getListMenuAction().add(this.markersListAction);
        ActivatorData.getInstance().getListMenuAction().add(this.vueInfosParcoursAction);
        ActivatorData.getInstance().getListMenuAction().add(this.vueDefautsAction);
        ActivatorData.getInstance().getListMenuAction().add(this.fermerAction);

		
		coolBar.add(new ToolBarContributionItem(toolbar1, VueType.FILE.name()));
		coolBar.add(new ToolBarContributionItem(toolbar2, VueType.VUE.name()));
	}

	
	public void createAction(VueAction action, VueType type) {
		action.setWindow(getActionBarConfigurer().getWindowConfigurer().getWindow());
		
		action.setEnabled(ActivatorData.getInstance().getVueData() != null);
		register(action);
		
		ActivatorData.getInstance().getListMenuAction().add(action);
		
		// Add to coolbar
		ToolBarContributionItem coolBar = (ToolBarContributionItem)getActionBarConfigurer().getCoolBarManager().find(type.value());
		coolBar.getToolBarManager().add(action);
		coolBar.getToolBarManager().update(true);
		
		// Add to toolbar
		MenuManager toolBar = (MenuManager)getActionBarConfigurer().getMenuManager().find(type.value());
		toolBar.add(action);
		
		getActionBarConfigurer().getCoolBarManager().update(true);	
	}
	
	/**
	 * Ajoute une action d'export dans la liste d'export
	 * @param action
	 */
	public void ajouterExportsActions(Action action){
		if (!this.actionExports.contains(action)) {
			this.actionExports.add(action);
		}
	}
	
	/**
	 * Créer l'action dans le menu File
	 * @param action à ajouter
	 */
	public void creerExportsActions() {
		for (Action actionCourante : this.actionExports) {
			IMenuManager toolBar = (IMenuManager)getActionBarConfigurer().getMenuManager().find(VueType.FILE.value());
			IContributionItem[] item = toolBar.getItems();
			int i=0;
			boolean trouve = false;
			
			while(!trouve && i<item.length) {
				if (item[i].getId()!=null) {
					if (item[i].getId().equals(actionCourante.getId())) {
						trouve = true;
					}
				}
				
				i++;
			}
				
			if (!trouve) {
				createAction(actionCourante, VueType.FILE);
			}
		}
	}
	
	/**
	 * Méthode temporaire d'ajout du menu exporter Vue Liste
	 * @param action
	 */
	public void createAction(Action action, VueType type) {
		register(action);
		
		// Add to menu
		IMenuManager toolBar = (IMenuManager)getActionBarConfigurer().getMenuManager().find(type.value());
		
		toolBar.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, action);
		getActionBarConfigurer().getCoolBarManager().update(true);
		
	}
	
	public void enregistrerAction(IAction action){
		this.register(action);
	}	
}