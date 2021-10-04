package com.faiveley.samng.principal.ihm.actions.captures;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.ICommandIds;


/**
 * Classe responsable de la capture d'une vue
 * 
 * @author Olivier
 * 
 */
public class CapturerVueAction extends Action {
	protected final IWorkbenchWindow window;
	protected boolean vueVide =false;

	public CapturerVueAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		//The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_CAPTURE_VIEW_MESSAGE);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_CAPTURE_VIEW_MESSAGE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_capturer.png"));
	}

	/**
	 * Méthode d'ouverture de la fenetre de dialogue de capture de la vue
	 * sur laquelle on a le focus
	 */
	public final void run() {
		//récupération du widget  de contenu de la vue
		Composite contenu = getActiveViewContent();
		if(contenu == null)
			return;
		
		//récupération du widget  de contenu de la vue
		GC gc = new GC(contenu);
		
		//création d'une image à partir du widget
		Display display = contenu.getShell().getDisplay();
		Point tableSize = contenu.getSize();
		Image image = new Image(display, tableSize.x, tableSize.y);
		gc.copyArea(image, 0, 0);
		gc.dispose();
	
		//chargment de l'image dans un loader
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] {image.getImageData()};

		// creates a file dialog to open the binary files
		FileDialog dialog = new FileDialog(this.window.getShell(), SWT.SAVE);
	
		//définition des extensions visibles
		dialog.setFilterExtensions(new String[] { "*.jpg" });

		dialog.setFilterNames(new String[] { "*.jpg"  });
		String cheminFichier = dialog.open();
		String nomFichier = dialog.getFileName();

		
		//enregistrement de l'image
		if (!nomFichier.trim().equals("")){
			imageLoader.save(cheminFichier,SWT.IMAGE_JPEG);}
	}
	
	private Composite getActiveViewContent() {
		//récupération de la vue sur laquelle on a le focus
		try {
			IWorkbenchPart part = this.window.getActivePage().getActivePart();
			if(part == null) 
				return null;
			
			return part instanceof ICapturable ? ((ICapturable)part).getContenu() : null;
		} catch (Exception e) {
			return null;
		}
	}
	
//	@Override
//	public boolean isEnabled() {
//		return !vueVide;
//		
//	}

	public boolean isVueVide() {
		return vueVide;
	}

	public void setVueVide(boolean vueVide) {
		this.vueVide = vueVide;
	}
	
}
