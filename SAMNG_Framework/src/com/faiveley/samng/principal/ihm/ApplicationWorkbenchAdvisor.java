package com.faiveley.samng.principal.ihm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

import com.faiveley.samng.principal.ihm.actions.fichier.FichierFermerAction;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveGestionDesMissions;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * This workbench advisor creates the window advisor, and specifies the
 * perspective id for the initial window.
 */
public class ApplicationWorkbenchAdvisor extends WorkbenchAdvisor {
	
	private static final String PERSPECTIVE_ID = PerspectiveGestionDesMissions.getID();

	/**
	 * @return new ApplicationWorkbenchWindowAdvisor(configurer)
	 * @param configurer
	 *            1
	 */
	public final WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(
			final IWorkbenchWindowConfigurer configurer) {
		return new ApplicationWorkbenchWindowAdvisor(configurer);
	}

	/**
	 * @return PERSPECTIVE_ID
	 */
	public final String getInitialWindowPerspectiveId() {
			return PERSPECTIVE_ID;
	}

	public void initialize(IWorkbenchConfigurer configurer) {
		// Utiliser la valeur 'true' pour que l'état du workbench soit sauvegardé.
		configurer.setSaveAndRestore(false);
		// Attention, si sauvegarde de l'état, la méthode createInitialLayout de la
		// perspective sera appelée uniquement au premier lancement de l'application.
		// Il est alors préférable de prévoir une entrée dans un menu permettant
		// à l'utilisateur de réinitialiser la vue avec le code suivant :
		//      PlatformUI.getWorkbench().getActiveWorkbenchWindow().
		//                                    getActivePage().resetPerspective();
	}

	@Override
	public boolean preShutdown() {
		try{
			//save repertoire travail DR28_CL36 
			Properties p = new Properties();
			OutputStream stream;
			try {
				if (!RepertoiresAdresses.RepertoireTravail.equals("")) {
					stream = new FileOutputStream(RepertoiresAdresses.temp_directory);
					String path=RepertoiresAdresses.RepertoireTravail;
					if (!path.endsWith(File.separator)) {
						path=path + File.separator;
					}
					p.setProperty("dir",path);
					p.store(stream, null);
					stream.flush();
					stream.close();
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			FichierFermerAction ffa = null ;
			ffa = new FichierFermerAction(
					Activator.getDefault().getWorkbench().getActiveWorkbenchWindow(), 
					"");
			ffa.run();
		}
		catch(Exception ex){
			System.exit(0);
		}
		
		return super.preShutdown();
	}
}