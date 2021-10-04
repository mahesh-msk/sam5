package com.faiveley.samng.principal.ihm.vues.vuemarqueurs.actions;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.flag.Flag;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.marqueurs.AMarqueur;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class SupprimerMarqueurAction extends AbstractMarqueurAction {
	public SupprimerMarqueurAction() {
		String texteAction = Messages.getString("SupprimerMarqueurAction.0"); //$NON-NLS-1$
		setText(texteAction);
		setToolTipText(texteAction);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/vueToolBars/vue_annotation_effacer.png"));
	}
	
	@Override
	public void run() {
		 java.util.List<AMarqueur> marker = getSelectedMarkers();
		 if(marker.size()>0){
		 for (int i = 0; i < marker.size(); i++) {
			
		
		if(marker.get(i) != null) {
			ActivatorData.getInstance().getGestionnaireMarqueurs().effacerMarqueur(marker.get(i));
			ActivatorData.getInstance().getVueData().removeMarkerId(marker.get(i).getIdMessage());


			// get parcours
			ParcoursComposite p = FabriqueParcours.getInstance().getParcours();
			if (p == null) {
				return;
			}
			// get enregisrtrement
			Enregistrement enrg = p.getData().getEnregistrement();
			if (enrg == null) {
				return;
			}

			// get the reference time and distance
			Message m = enrg.getGoodMessage(Integer.valueOf(marker.get(i).getIdMessage()));
			Flag flagmsg = m.getFlag();
			if (flagmsg != null) {
				String label = null;
				if (flagmsg.getLabel().contains("A")) {
					label = flagmsg.getLabel().replace("A", "");
				}
				if (label == null) {
					m.setFlag(null);
				} else {
					flagmsg.setLabel(label);
				}
			}
			
			
		}
		
		 }
		 ActivatorData.getInstance().getPoolDonneesVues().put(new String("modifMarqueurListe"), true);
			ActivatorData.getInstance().getPoolDonneesVues().put(new String("modifMarqueurTab"), true);
////////////////////////////////////	//
			ActivatorData.getInstance().notifyRepereListeners(
					true,
					new TypeRepere[] { TypeRepere.distanceRelatif,
							TypeRepere.tempsRelatif });
		 }
	}

	@Override
	public boolean isEnabled() {
//		return 	getVueMarqueurs() != null;
		return (!GestionLineCommandParameters.isAnnot_Lect_seule());
	}
}
