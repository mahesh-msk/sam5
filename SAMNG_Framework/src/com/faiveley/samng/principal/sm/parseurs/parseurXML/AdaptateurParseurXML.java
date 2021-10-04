package com.faiveley.samng.principal.sm.parseurs.parseurXML;

public class AdaptateurParseurXML {

	/**
	 * M�thode pertmettant de valoriser ParseurXML1
	 * 
	 * @param fichierXmlAssocie
	 *            nom du fichier xmlAssoci�
	 */
	public static void definirParseurXML(String fichierXmlAssocie) {
		if (ParseurXML_NG_UK_ATESS.fichierXmlAssocieValide(fichierXmlAssocie)) {
			ParseurXML1.setParseurXML(new ParseurXML_NG_UK_ATESS());
		} else if (ParseurXMLJRU.fichierXmlAssocieValide(fichierXmlAssocie)) {
			ParseurXML1.setParseurXML(new ParseurXMLJRU());
		}
	}

	/**
	 * M�thode de v�rification du fichier xml associ�
	 * 
	 * @param fichierXmlAssocie
	 * @return
	 */
	public static boolean verifierValiditeFichierXmlAssocie(
			String fichierXmlAssocie) {
		return ParseurXML_NG_UK_ATESS
				.fichierXmlAssocieValide(fichierXmlAssocie)
				|| ParseurXMLJRU.fichierXmlAssocieValide(fichierXmlAssocie);
	}
}
