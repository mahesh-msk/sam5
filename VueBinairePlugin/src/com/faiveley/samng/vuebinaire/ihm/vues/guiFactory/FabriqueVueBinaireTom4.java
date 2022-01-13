package com.faiveley.samng.vuebinaire.ihm.vues.guiFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.AParseurParcoursTom4;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4;
import com.faiveley.samng.vuebinaire.ihm.vues.BinaryRow;

/**
 * 
 * @author Olivier
 * 
 *         Classe de gestion du remplissage de la vue binaire pour les fichiers
 *         Tom4
 */
public class FabriqueVueBinaireTom4 extends AFabriqueVueBinaire {

	public ArrayList<BinaryRow> remplirTableau(IProgressMonitor monitor) {
		ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>(0);
		BinaryRow row = null;		
		int tailleEnregistrement = ConstantesParcoursTom4.tailleBlocData;
		String[] labels = null;

		if (ConstantesParcoursBinaire.offsetFin==-1) {
			ConstantesParcoursBinaire.offsetFin=TypeParseur.getInstance().getParser().getMessage().length;
		}

		int offsetDebut=ConstantesParcoursTom4.tailleConfiguration;
		if (ConstantesParcoursBinaire.offsetDebut!=0) {
			offsetDebut=ConstantesParcoursBinaire.offsetDebut;
		}

		byte[] tableauOctets = Arrays.copyOfRange(TypeParseur.getInstance().getParser().getMessage(),
				offsetDebut,ConstantesParcoursBinaire.offsetFin);

		HashMap<Integer, Boolean> crcEnreg = ((AParseurParcoursTom4) TypeParseur.getInstance().getParser()).getCrcEnregistrements();
		String hexVal = null;

		int i = 0;
		int indiceLecture = 0;
		int offsetID=ConstantesParcoursBinaire.offsetDebut-ConstantesParcoursTom4.tailleConfiguration;
		if (ConstantesParcoursBinaire.offsetDebut==0) {
			offsetID=0;
		}

		while (indiceLecture < (tableauOctets.length) && !monitor.isCanceled()) {
			ActivatorData.getInstance().getVp().setValeurProgressBar(indiceLecture * 100 / (tableauOctets.length));
			row = new BinaryRow();
			labels = new String[tailleEnregistrement];
			row.setMsgId(indiceLecture + offsetID);
			for (int k = 0; k < tailleEnregistrement; k++) {
				if (monitor.isCanceled() || indiceLecture >= tableauOctets.length)
					break;
				try {
					hexVal = Integer.toHexString(tableauOctets[indiceLecture] & 0xff);
					hexVal = hexVal.length() == 2 ? hexVal : "0" + hexVal;
					// labels[k] = hexVal;

					if (GestionnairePool.getInstance().getMapByteHexa().get(tableauOctets[indiceLecture]) == null) {
						GestionnairePool.getInstance().getMapByteHexa().put(tableauOctets[indiceLecture], hexVal);
						labels[k] = hexVal;
					} else {
						labels[k] = GestionnairePool.getInstance().getMapByteHexa().get(tableauOctets[indiceLecture]);
					}
					indiceLecture++;
				} catch (Exception e) {
					indiceLecture++;
					e.printStackTrace();
				}
			}

			row.setBlockId(i + 1);
			row.setLabels(labels);			

			if ((crcEnreg.get(Integer.valueOf(i + 1))!=null)) {
				if (!crcEnreg.get(Integer.valueOf(i + 1))){
					row.setError(ErrorType.CRC);
				}
				rows.add(row);
				i++;
			}
		}
		boolean isBlue = false;

		for (int j = 0; j < rows.size(); j++) {
			if (monitor.isCanceled())
				break;
			BinaryRow ligne = rows.get(j);
			ligne.setBlue(isBlue);
			if ((j < rows.size() - 1) && (ligne.getMsgId() != rows.get(j + 1).getMsgId())) {
				isBlue = !isBlue;
			}
		}

		if (monitor.isCanceled())
			rows = new ArrayList<BinaryRow>(0);
		return rows;
	}
}