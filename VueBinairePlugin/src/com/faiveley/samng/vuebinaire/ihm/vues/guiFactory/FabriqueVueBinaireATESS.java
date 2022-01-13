package com.faiveley.samng.vuebinaire.ihm.vues.guiFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.enregistrement.ErrorType;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.parseurs.AParseurParcours;
import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.vuebinaire.ihm.vues.BinaryRow;

public class FabriqueVueBinaireATESS extends AFabriqueVueBinaire {
	public ArrayList<BinaryRow> remplirTableau(IProgressMonitor monitor) {
		ArrayList<BinaryRow> rows = new ArrayList<BinaryRow>(0);
		BinaryRow row = null;
		int debutLectureEnregistrement = 0;
		int tailleEnregistrement = ConstantesParcoursBinaire.tailleBlocData;
		String[] labels = null;

		if (ConstantesParcoursBinaire.offsetFin==-1) {
			ConstantesParcoursBinaire.offsetFin=TypeParseur.getInstance().getParser().getMessage().length;
		}
		
		byte[] tableauOctets = Arrays.copyOfRange(TypeParseur.getInstance().getParser().getMessage(),
				ConstantesParcoursBinaire.offsetDebut,ConstantesParcoursBinaire.offsetFin);

		HashMap<Integer, Boolean> crcEnreg = ((AParseurParcours) TypeParseur.getInstance().getParser()).getCrcEnregistrements();
		String hexVal = null;

		int i = 0;
		int indiceLecture = debutLectureEnregistrement;

		while (indiceLecture < tableauOctets.length && !monitor.isCanceled()) {
			ActivatorData.getInstance().getVp().setValeurProgressBar(indiceLecture * 100 / (tableauOctets.length));
			row = new BinaryRow();
			labels = new String[tailleEnregistrement];
			row.setMsgId(indiceLecture - debutLectureEnregistrement+ConstantesParcoursBinaire.offsetDebut);
			for (int k = 0; k < tailleEnregistrement; k++) {
				try {
					if (monitor.isCanceled() || indiceLecture >= tableauOctets.length)
						break;
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			row.setBlockId(i + 1);
			row.setLabels(labels);
			try {
				if (!crcEnreg.get(Integer.valueOf(i + 1)))
					row.setError(ErrorType.CRC);
			} catch (Exception e) {
				// System.out.println("probleme CRC enreg");
			}
			rows.add(row);
			i++;
		}

		boolean isBlue = false;
		for (int j = 0; j < rows.size(); j++) {
			if (monitor.isCanceled())
				break;
			BinaryRow ligne = rows.get(j);
			ligne.setBlue(isBlue);
			if ((j < rows.size() - 1)
					&& (ligne.getMsgId() != rows.get(j + 1).getMsgId())) {
				isBlue = !isBlue;
			}
		}
		if (monitor.isCanceled())
			rows = new ArrayList<BinaryRow>(0);

		return rows;
	}
}