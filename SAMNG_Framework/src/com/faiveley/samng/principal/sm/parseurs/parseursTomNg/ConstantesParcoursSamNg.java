package com.faiveley.samng.principal.sm.parseurs.parseursTomNg;

import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;

/**
 * Constant codes loaded from binary file
 * 
 * @author The Administrator
 * @version 1.0
 * @created 02-oct.-2007 13:10:33
 */
public class ConstantesParcoursSamNg extends ConstantesParcoursBinaire {

	public static byte formatCodage;
	public static byte tailleNomFichierXML;
	public static int codeDebut;
	public static int codeFin;
	public static int codeDebutDefaut;
	public static int codeFinDefaut;
	public static int codeContinueFinBloc;
	public static int codeContinueDebutBloc;
	public static int codeContinueDebutDefaut;
	public static int codeContinueFinDefaut;

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();

		buf.append("ConstantesParcoursSamNg.formatCodage = ")
				.append(formatCodage).append("\ntailleEntete = ")
				.append(tailleEntete).append("\ntailleNomFichierXML = ")
				.append(tailleNomFichierXML).append("\nnomFichierXML = ")
				.append(nomFichierXML)
				.append("\nConstantesParcoursBinaire.tailleBlocData = ")
				.append(ConstantesParcoursBinaire.tailleBlocData)
				.append("\ntailleTableEvenement = ")
				.append(tailleTableEvenement).append("\nCD = 0x")
				.append(Integer.toHexString(codeDebut)).append("; CF = 0x")
				.append(Integer.toHexString(codeFin)).append("; CCF = 0x")
				.append(Integer.toHexString(codeContinueFinBloc))
				.append("; CCD = 0x")
				.append(Integer.toHexString(codeContinueDebutBloc))
				.append("; CDD = 0x")
				.append(Integer.toHexString(codeDebutDefaut))
				.append("; CFD = 0x")
				.append(Integer.toHexString(codeFinDefaut))
				.append("; CCFD = 0x")
				.append(Integer.toHexString(codeContinueFinDefaut))
				.append("; CCDD = 0x")
				.append(Integer.toHexString(codeContinueDebutDefaut));

		return buf.toString();
	}
}