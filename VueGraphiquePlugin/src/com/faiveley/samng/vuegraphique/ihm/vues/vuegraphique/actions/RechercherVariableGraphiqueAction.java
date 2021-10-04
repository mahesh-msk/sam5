package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.actions;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.vues.search.ASearchDialog;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.FiltreComposite;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.dialogs.SearchVariableGraphique;
import com.faiveley.samng.vuegraphique.sm.filtres.GestionnaireFiltresGraphique;

public class RechercherVariableGraphiqueAction extends Action{
	private boolean usesShortNames;
	
	/**
	 * Constructor
	 * 
	 * @param actionId	the id of the action
	 * @param label		the label of the dialog
	 * @param descriptor	the image 
	 */
	public RechercherVariableGraphiqueAction( 
			final String actionId,
			final String label, 
			final ImageDescriptor descriptor,
			final boolean usesShortNames) {
		
		setText(label);
		setToolTipText(label);
		// The id is used to refer to the action in a menu or toolbar
		setId(actionId);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_OPEN);
		
		setImageDescriptor(descriptor);
		
		this.usesShortNames = usesShortNames;
	}
	
	/**
	 * Runs the action set for Search By Variable 
	 * */
	public void run() {
		
		List<String> list=new ArrayList<String>();
		GestionnaireFiltresGraphique filtersMng = (GestionnaireFiltresGraphique)ActivatorVueGraphique.getDefault().getFiltresProvider().getGestionnaireFiltres();
		String currentAppliedFilterName = ActivatorVueGraphique.getDefault().getFiltresProvider().getAppliedFilter();

		if (currentAppliedFilterName==null || currentAppliedFilterName.equals("null")) {//si pas de filtre on utilise filtre defaut
			FiltreComposite filtreDef= filtersMng.getFiltreDefault();
			for (int i = 0; i < filtreDef.getEnfantCount(); i++) {
				for (int j = 0; j < filtreDef.getEnfant(i).getEnfantCount(); j++) {
					list.add(filtreDef.getEnfant(i).getEnfant(j).getNom());
				}
			}
		}else{//sinon on utilise le filter appliqué		
			AFiltreComposant gest=filtersMng.listeFiltres;
			for (int i = 0; i < gest.getEnfantCount(); i++) {
				AFiltreComposant filtre=gest.getEnfant(i);
				if (filtre.getNom().equals(currentAppliedFilterName)) {
					for (int j = 0; j < filtre.getEnfantCount(); j++) {
						AFiltreComposant comp=filtre.getEnfant(j);
						for (int j2 = 0; j2 < comp.getEnfantCount(); j2++) {
							list.add(comp.getEnfant(j2).getNom());
						}
					}
				}
			}
		}
		
		ASearchDialog dlg = new SearchVariableGraphique(Activator.getDefault().
				getWorkbench().getActiveWorkbenchWindow().getShell(), this.usesShortNames);
		
		dlg.setVariablesDuFiltre(list);
		dlg.open();
	}
	
	public void usesShortNames(boolean usesShortNames) {
		this.usesShortNames = usesShortNames;
	}
}
