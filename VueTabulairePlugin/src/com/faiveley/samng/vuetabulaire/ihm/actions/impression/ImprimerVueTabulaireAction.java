package com.faiveley.samng.vuetabulaire.ihm.actions.impression;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.ihm.ICommandIds;

public class ImprimerVueTabulaireAction extends Action {

		protected final IWorkbenchWindow window;
		protected boolean vueVide = false;



		public ImprimerVueTabulaireAction(final IWorkbenchWindow window, final String text) {
			super(text);
			this.window = window;
			// The id is used to refer to the action in a menu or toolbar
			setId(ICommandIds.CMD_PRINT_VIEW_MESSAGE);
			// Associate the action with a pre-defined command, to allow key
			// bindings.
			setActionDefinitionId(ICommandIds.CMD_PRINT_VIEW_MESSAGE);
			setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
					.getImageDescriptor("/icons/toolBar/vues_commun_imprimer.png"));

		}

		/**
		 * Méthode d'ouverture de la fenetre de dialogue de capture de la vue sur
		 * laquelle on a le focus
		 */
		public final void run() {
			ImpressionVueTabulaire ivt=new ImpressionVueTabulaire();
			ivt.lancerlimpression();
		}
}
