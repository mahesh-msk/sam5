package com.faiveley.samng.vueexplorateur.ihm.vue;

import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.ActivatorVueExplorateur;
import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierFermerAction;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.AjouterEspaceTravailAction;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.ChangerEspaceTravailAction;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.ExplorerActionProgressionBar;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.OuvrirAction;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.ProprietesAction;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.RafraichirExplorateurAction;
import com.faiveley.samng.vueexplorateur.ihm.vue.actions.SupprimerEspaceTravailAction;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFile;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFolder;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMensuel;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMission;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeObject;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeRepository;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeSegment;
import com.faiveley.samng.vueexplorateur.ihm.vue.viewprovider.ViewCellLabelProvider;
import com.faiveley.samng.vueexplorateur.ihm.vue.viewprovider.ViewContentProvider;

public class VueExplorateurFichiersDeParcours extends ViewPart implements IDataChangedListener {

	private static TreeViewer viewer;
	private Action ouvrir;
	private Action explorer;
	private Action ajouterEspaceTravail;
	private Action changerEspaceTravail;
	private Action supprimerEspaceTravail;
	private Action rafraichirExplorateur;
	private Action proprietes;
	public static final String ID="SAMNG.Vue.VueExplorateur";
	private Object currentSelectedObject;

	public VueExplorateurFichiersDeParcours() {
		this.setPartName(Messages.getString("SampleView_63"));
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewCellLabelProvider());
		//		viewer.setSorter(new ViewerSorter());
		viewer.setInput(getViewSite());
		ColumnViewerToolTipSupport.enableFor(viewer);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection() instanceof IStructuredSelection) {
					IStructuredSelection structureSelection = (IStructuredSelection) event
							.getSelection();
					currentSelectedObject = structureSelection.getFirstElement();
					activeActions(structureSelection);
				}
			}
		});
		
		// Allow to refresh the treeviewer by pressing F5
		viewer.getTree().addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				// Nothing to do on this event
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.F5) {
					refreshViewExplorateur(true);
				}
			}
		});
		
		viewer.addDoubleClickListener(new IDoubleClickListener()
		  {
			public void doubleClick(DoubleClickEvent event) 
			{			
				final IStructuredSelection selection = (IStructuredSelection)event.getSelection() ;
				
			    if (selection == null || selection.isEmpty())
			    	return ;

			    final Object sel = selection.getFirstElement() ;
			    
			    if ((sel instanceof TreeFolder) || (sel instanceof TreeRepository))
			    {
			    	final ITreeContentProvider provider = (ITreeContentProvider)viewer.getContentProvider() ;

			    	if (!provider.hasChildren(sel))
			    		return ;

			    	// Tree expand ?
			    	if (viewer.getExpandedState(sel))
			    		viewer.collapseToLevel(sel, AbstractTreeViewer.ALL_LEVELS) ;
			    	else
			    		viewer.expandToLevel(sel, 1);
			    }
			    else
			    	OuvrirAction.lancerActionOuvrir(viewer);
			}			
		  }
		 );

		// Create the help context id for the viewer's control
		makeActions();
		initSelection();
		hookContextMenu();
		contributeToActionBars();
		//refreshViewExplorateur(true);
		refreshViewExplorateurExpand();
		
		ActivatorData.getInstance().addDataListener(this);
	}

	public static void refreshViewExplorateur(boolean expand){		
		try {
			viewer.refresh();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (expand) {
			refreshViewExplorateurExpand();
		}
	}

	private static void refreshViewExplorateurExpand(){				
		try {
			ViewContentProvider vcp=(ViewContentProvider) viewer.getContentProvider();
			TreeParent tp=vcp.getInvisibleRoot();
			expandFileAndFolder(viewer, tp);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void expandFileAndFolder(TreeViewer viewer, TreeParent tp)
	{
		if (tp!=null) 
		{
			TreeObject[] to=tp.getChildren();
			
			if (to!=null && to.length>0) 
			{
				int size=to.length;
				
				for (int i = 0; i < size; i++) 
				{
					if (to[i] instanceof TreeFolder || to[i] instanceof TreeRepository) 
					{
						viewer.expandToLevel(to[i], 1);					
					}
				}
			}
		}		
	}

	private void initSelection() {
		ouvrir.setEnabled(false);
		explorer.setEnabled(false);
		ajouterEspaceTravail.setEnabled(true);
		changerEspaceTravail.setEnabled(false);
		supprimerEspaceTravail.setEnabled(false);
		proprietes.setEnabled(false);
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				VueExplorateurFichiersDeParcours.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillContextMenu(IMenuManager manager) {
		ISelection selection = viewer.getSelection();
		IStructuredSelection structureSelection = ((IStructuredSelection) selection);

		activeActions(structureSelection);
		manager.add(ouvrir);
		manager.add(explorer);
		manager.add(new Separator());
		manager.add(ajouterEspaceTravail);
		manager.add(changerEspaceTravail);
		manager.add(supprimerEspaceTravail);
		manager.add(rafraichirExplorateur);
		manager.add(new Separator());
		manager.add(proprietes);
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(ouvrir);
		manager.add(explorer);
		manager.add(new Separator());
		manager.add(ajouterEspaceTravail);
		manager.add(changerEspaceTravail);
		manager.add(supprimerEspaceTravail);
		manager.add(rafraichirExplorateur);
		manager.add(new Separator());
		manager.add(proprietes);
	}

	private void activeActions(IStructuredSelection structureSelection) {
		changerEspaceTravail.setEnabled(false);
		supprimerEspaceTravail.setEnabled(false);
		
		List<?> list=structureSelection.toList();
		
//		Class<? extends Object> mainSelectionClass = TreeRepository.class ;
			
		if (currentSelectedObject instanceof TreeRepository) {
			changerEspaceTravail.setEnabled(true);
			supprimerEspaceTravail.setEnabled(true);
		}
		if (currentSelectedObject instanceof TreeFile) {
			ouvrir.setEnabled(true);
			
			TreeFile treeFile = (TreeFile) currentSelectedObject;
			boolean isMultimediaFile = BridageFormats.isMultimedia(treeFile.getAbsoluteName());
			explorer.setEnabled(!isMultimediaFile);
			
			proprietes.setEnabled(true);
		}
		if (currentSelectedObject instanceof TreeMensuel) {
			ouvrir.setEnabled(true);
			explorer.setEnabled(false);
			proprietes.setEnabled(false);
		}
		if (currentSelectedObject instanceof TreeMission) {
			ouvrir.setEnabled(true);
			explorer.setEnabled(false);
			proprietes.setEnabled(false);
		}
		if (currentSelectedObject instanceof TreeSegment) {
			ouvrir.setEnabled(true);
			explorer.setEnabled(false);
			proprietes.setEnabled(false);
		}
		
		for (int i=0; i<structureSelection.size(); i++) {
			if (list.get(i) instanceof TreeRepository) { 				
					ouvrir.setEnabled(false);
					explorer.setEnabled(false);
					proprietes.setEnabled(true);
					break ;
			}
			
			if (list.get(i) instanceof TreeFolder) {
				ouvrir.setEnabled(false);
				explorer.setEnabled(true);
				proprietes.setEnabled(true);
				break ;
			}
		}

		if(structureSelection.size()>1){
			proprietes.setEnabled(false);
			explorer.setEnabled(false);
		}
		
		ViewContentProvider contentProvider = (ViewContentProvider)viewer.getContentProvider();
		if (contentProvider.getInvisibleRoot().getChildren().length == 1) {
			supprimerEspaceTravail.setEnabled(false);
		}
	}

	private void makeActions() {
		this.setPartName(Messages.getString("SampleView_63"));
		ouvrir = new Action() {
			public void run() {				
				OuvrirAction.lancerActionOuvrir(viewer);
			}
		};
		ouvrir.setText(Messages.getString("SampleView_52"));
		ouvrir.setToolTipText(Messages.getString("SampleView_52"));
		ouvrir.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));

		explorer = new Action() {
			public void run() {				
				lancerActionExplorer();				
			}
		};
		explorer.setText(Messages.getString("SampleView_55"));
		explorer.setToolTipText(Messages.getString("SampleView_55"));
		explorer.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT));

		ajouterEspaceTravail = new Action() {
			public void run() {
				AjouterEspaceTravailAction.ajouterEspace();
			}
		};
		ajouterEspaceTravail.setText(Messages.getString("SampleView_57"));
		ajouterEspaceTravail.setToolTipText(Messages.getString("SampleView_57"));
		ajouterEspaceTravail.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
				
		changerEspaceTravail = new Action() {
			public void run() {
				ChangerEspaceTravailAction.changerEspace((TreeParent)currentSelectedObject);
			}
		};
		changerEspaceTravail.setText(Messages.getString("SampleView_58"));
		changerEspaceTravail.setToolTipText(Messages.getString("SampleView_58"));
		changerEspaceTravail.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_HOME_NAV));
		
		supprimerEspaceTravail = new Action() {
			public void run() {
				SupprimerEspaceTravailAction.supprimerEspaceTravail((TreeParent)currentSelectedObject);
			}
		};
		supprimerEspaceTravail.setText(Messages.getString("SampleView_59"));
		supprimerEspaceTravail.setToolTipText(Messages.getString("SampleView_59"));
		supprimerEspaceTravail.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DELETE));

		rafraichirExplorateur = new Action() {
			public void run() {
				RafraichirExplorateurAction.rafraichirExplorateur();
			}
		};
		rafraichirExplorateur.setText(Messages.getString("SampleView_60"));		
		rafraichirExplorateur.setImageDescriptor(ImageDescriptor.createFromURL(FileLocator.find(ActivatorVueExplorateur.getDefault().getBundle(), new Path("/icons/refresh.png"), null)));
		rafraichirExplorateur.setToolTipText(Messages.getString("SampleView_60"));
		
		proprietes = new Action() {
			public void run() {
				TreeSelection selection=((TreeSelection) viewer.getSelection());
				ProprietesAction.afficherProprietes(selection,viewer);
			}
		};
		proprietes.setText(Messages.getString("SampleView_61"));
		proprietes.setToolTipText(Messages.getString("SampleView_61"));
		proprietes.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(ISharedImages.IMG_LCL_LINKTO_HELP));
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}

	private void lancerActionExplorer() {
		boolean search=false;

		if(ActivatorData.getInstance().getVueData().getDataTable()!=null){
			MessageBox msgBox;
			try{
				msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_QUESTION | SWT.OK | SWT.CANCEL );
				msgBox.setText(com.faiveley.samng.vueexplorateur.ihm.vue.Messages.getString("SampleView_55")); //$NON-NLS-1$
				msgBox.setMessage(com.faiveley.samng.vueexplorateur.ihm.vue.Messages.getString("SampleView_64")); //$NON-NLS-1$

				if (msgBox.open() == SWT.OK) {
					search=true;
					
					FichierFermerAction ffa = null ;
					ffa = new FichierFermerAction(
							Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(), 
							"");
					ffa.run();	
				}	
			}catch (Exception e) {
				search=true;
			}
		}else{
			search = true;
		}

		if (search){			

			Activator.getDefault().hideAllViewsExceptViews(VueExplorateurFichiersDeParcours.ID);			
			
			try {
				ActivatorData.getInstance().notifyDataListeners();					
			} catch (Exception e) {
				e.printStackTrace();
			}				

			ActivatorData.getInstance().getBarAdvisor().setActionsEnabled(false);
			
			for (Action action : ActivatorData.getInstance().getListMenuAction()) {
				if (action instanceof VueAction && ((VueAction)action).getViewId().equals(VueExplorateurFichiersDeParcours.ID)) {
					action.setEnabled(true);
				}					
			}
			
//			PlatformUI.getWorkbench().showPerspective(PerspectiveGestionDesMissions.VueExplorateur_ID, 
//					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow());
			
			ExplorerActionProgressionBar eap=new ExplorerActionProgressionBar(viewer.getSelection());
			eap.schedule();			
		}
	}

	@Override
	public void onDataChange() {
		// En cas d'ouverture non partielle, le fichier est explorer et donc le tree doit être mis à
		// jour pour récupérer ses nouveaux enfants : regroupement mensuel, segment de temps, ... 
		if (!ActivationExplorer.getInstance().isOuvertureFichierPartielle())
			refreshViewExplorateur(true);		
	}
}