package com.faiveley.samng.principal.sm.parseurs;

import java.awt.BorderLayout;
import java.awt.print.PrinterException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VariableExplorationUtils;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.util.StringUtils;

/**
 * 
 * @author Olivier Classe d'impl�mentation du parseur de donn�e du parcours en
 *         fichier d'export
 */
public class ParseurExportVueTabulaire implements IParseurInterface {
			
	public OutputStreamWriter parseurFichier = null;
	public PrintWriter pw = null;

	public void exporterChaine(String chaine) {

		try {
			parseurFichier.write(chaine);
		} catch (IOException e) {
			// TODO Bloc catch g�n�r� automatiquement
			e.printStackTrace();
		}finally{
			try {
				parseurFichier.close();
			} catch (IOException e) {
				// TODO Bloc catch g�n�r� automatiquement
				e.printStackTrace();
			}
		}

	}

	/**
	 * M�thode d'export d'une liste d'objets Row dans un fichier
	 * 
	 * @param cheminFichier
	 * @param nomsColonnes
	 * @param lignes
	 */
	public void exporterLignes(String cheminFichier, List<String> nomsColonnes,
			List<Row> lignes, VueTabulaireContentProvider contentProvider) {
		String stringTemp = "";
		ConfigurationColonne[] configColonnes = contentProvider.gestionaireVue.getFilteredColumns(ActivatorData.getInstance().getVueData());
		
		try {
			String delimitation="\t";
			for (String nomColonne : nomsColonnes) {
				if (nomColonne != null) {
					if (nomColonne
							.equals(TypeRepere.distanceCorrigee.getName()))
						nomColonne = com.faiveley.samng.principal.ihm.vues.configuration.Messages
								.getString("GestionnaireVueListeBase.5");
					else if (nomColonne.equals(TypeRepere.vitesseCorrigee
							.getName())) {
						nomColonne = com.faiveley.samng.principal.sm.data.enregistrement.Messages
								.getString("NomRepere.4");
					}
					
					if (cheminFichier.trim().toLowerCase().endsWith(".tsv")) {
						delimitation="\t";
					}else{
						delimitation=";";
					}
					
					parseurFichier.write(nomColonne + delimitation, 0, nomColonne.length() + 1);
				}
			}
			parseurFichier.write("\n");
			for (Row r : lignes) {
				for (int j = 0; j < r.getNbData(); j++) {
					// Etape 1 : savoir sur quelle colonne on est pour tester si c'est une variable volatile ou pas
					String nomColonne = nomsColonnes.get(j);
					ConfigurationColonne configCol = getConfigurationColonneFromNom(configColonnes, nomColonne);
					boolean isVolatile = configCol != null && configCol.isVolatile();
				
					// Si c'est pas une variable volatile on fait comme avant
					if (isVolatile) {
						String[] values = VariableExplorationUtils.getValuesFromMessage((Message) r.getData(), nomColonne);
						// Sinon faut r�cup�rer toutes les valeurs pour la variable et cet �v�nement et les afficher avec un pipe
						if (values != null && values.length > 0) {
							stringTemp = StringUtils.join(values, "|");
						} else {
							stringTemp = getSimpleValueFromRow(r, nomColonne, j);
						}
					} else {
						stringTemp = getSimpleValueFromRow(r, nomColonne, j);
					}
					stringTemp = gestionCaractereFinDeChaine(stringTemp.trim());
					parseurFichier.write(stringTemp + delimitation, 0, stringTemp.length() + 1);
				}
				parseurFichier.write("\n");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (parseurFichier != null) {
				try {
					parseurFichier.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private String getSimpleValueFromRow(Row row, String nomColonne, int index){
		if (row.getValue(index) != null) {
			return row.getValue(index);
		} else {
			return "";
		}
	}
	
	private ConfigurationColonne getConfigurationColonneFromNom(ConfigurationColonne[] colonnes, String nom) {
		for (int i = 0; i < colonnes.length; i++) {
			if (nom.equals(colonnes[i].getNom())) {
				return colonnes[i];
			}
		}
		return null;
	}

	private String gestionCaractereFinDeChaine(String chaine) {
		String finChaine = "\0";
		if (chaine.contains(finChaine)) {
			return chaine.replace(finChaine, "");
		} else {
			return chaine;
		}
	}

	public void writeLignes(int index1, int index2, FileInputStream fis,
			List<Row> lignes, List<Integer> caracMaxByColumn) {
		String stringTemp = "";
		for (int i = 0; i < lignes.size(); i++) {
			Row r = lignes.get(i);
			for (int j = index1; j < index2; j++) {
				int curseur = 0;
				int caracMax = caracMaxByColumn.get(j);

				if (r.getValue(j) != null)
					stringTemp = r.getValue(j);
				else
					stringTemp = "";
				stringTemp = stringTemp + " ";
				// stringTemp =stringTemp.trim();
				int stringlenght = stringTemp.length();
				for (int j2 = 0; j2 < (caracMax - stringlenght - 1); j2++) {
					stringTemp = stringTemp + " ";
				}
				stringTemp = stringTemp + "|";
				String format = "%-" + caracMax + "s";
				try {
					pw.printf(format, stringTemp);
				} catch (Exception e) {
					e.printStackTrace();
				}
				curseur = curseur + caracMax;
			}
			pw.print("\n");
		}
	}

	public void writeLignes2(int index1, int index2, FileInputStream fis,
			String[][] lignes, List<Integer> caracMaxByColumn) {
		String stringTemp = "";

		for (int i = 0; i < lignes.length; i++) {
			for (int j = index1; j < index2; j++) {
				int caracMax = caracMaxByColumn.get(j);
			}
		}
	}

	public void ImprimerJTable(FileInputStream is, List<String> columnNames,
			String[][] selectionLignes) {
		try {
			JTable table;
			try {
				table = new JTable((Object[][]) selectionLignes,
						columnNames.toArray());

				PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
				attr.add(OrientationRequested.LANDSCAPE);
				attr.add(MediaSizeName.ISO_A4);

				JFrame frame = new JFrame("Table Printing");

				JScrollPane scrollPane = new JScrollPane(table);
				frame.add(scrollPane, BorderLayout.CENTER);
				frame.setVisible(true);
				frame.setSize(table.getColumnModel().getColumnCount() * 100,
						selectionLignes.length * 50);

				MessageFormat headerFormat = new MessageFormat("Page {0}");
				MessageFormat footerFormat = new MessageFormat("- {0} -");
				table.print(JTable.PrintMode.FIT_WIDTH, headerFormat,
						footerFormat, true, attr, false);
			} catch (PrinterException pe) {
				System.err.println("Error printing: " + pe.getMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(Display.getCurrent()
					.getActiveShell(), SWT.ICON_ERROR);
			messageBox.setMessage(Messages.getString("ImprimerVue.2"));
			messageBox.open();
		}
	}

	/**
	 * M�thode d'export d'une liste d'objets Row dans un fichier
	 * 
	 * @param cheminFichier
	 * @param nomsColonnes
	 * @param lignes
	 */
	public void exporterLignes3(FileInputStream fis, List<String> nomsColonnes,
			String[][] lignes) {
		int nbCaracteresMax = 130;
		int nbColonnes = nomsColonnes.size();
		List<Integer> caracMaxByColumn = new ArrayList<Integer>(nbColonnes);
		for (int i = 0; i < nbColonnes; i++) {
			caracMaxByColumn.add(0);
		}

		// adapter Nom colonnes
		int i = 0;
		for (String nomColonne : nomsColonnes) {
			if (nomColonne != null) {
				if (nomColonne.equals(TypeRepere.distanceCorrigee.getName()))
					nomColonne = com.faiveley.samng.principal.ihm.vues.configuration.Messages
							.getString("GestionnaireVueListeBase.5");
				else if (nomColonne
						.equals(TypeRepere.vitesseCorrigee.getName()))
					nomColonne = com.faiveley.samng.principal.sm.data.enregistrement.Messages
							.getString("NomRepere.4");
			}
			caracMaxByColumn.set(i, nomColonne.length() + 2);
			i++;
		}

		String stringTemp = "";
		List<Integer> colonnesStop = new ArrayList<Integer>();
		colonnesStop.add(0);
		int nbCaracByLigne = 0;

		// quels sont les cellules les plus longues ?
		nbCaracByLigne = 0;
		for (String[] r : lignes) {
			nbCaracByLigne = 0;
			for (int j = 0; j < r.length; j++) {
				stringTemp = r[j];
				if (stringTemp.length() > caracMaxByColumn.get(j)) {
					caracMaxByColumn.set(j, stringTemp.length() + 2);
				}
			}
		}

		for (int j = 0; j < caracMaxByColumn.size(); j++) {
			nbCaracByLigne = nbCaracByLigne + caracMaxByColumn.get(j) + 2;
			if (nbCaracByLigne > nbCaracteresMax) {
				// stocker les indices des colonnes o� on doit changer de page
				colonnesStop.add(j);
				nbCaracByLigne = caracMaxByColumn.get(j) + 2;
			}
		}

		colonnesStop.add(nbColonnes);
		int nbPages = colonnesStop.size() - 1;
		for (int i1 = 0; i1 < nbPages; i1++) {
			// �crire les lignes avec les bons indices pour ne pas d�passer la
			// page
			writeLignes2(colonnesStop.get(i1), colonnesStop.get(i1 + 1), fis,
					lignes, caracMaxByColumn);
			pw.print("\n");
			pw.print("\n");
		}

		if (pw != null) {
			pw.close();
		}
	}

	/**
	 * M�thode d'export d'une liste d'objets Row dans un fichier
	 * 
	 * @param cheminFichier
	 * @param nomsColonnes
	 * @param lignes
	 */
	public void exporterLignes2(FileInputStream fis, List<String> nomsColonnes,
			List<Row> lignes) {
		int nbColonnes = nomsColonnes.size();

		// adapter Nom colonnes
		for (String nomColonne : nomsColonnes) {
			if (nomColonne != null) {
				if (nomColonne.equals(TypeRepere.distanceCorrigee.getName()))
					nomColonne = com.faiveley.samng.principal.ihm.vues.configuration.Messages
							.getString("GestionnaireVueListeBase.5");
				else if (nomColonne
						.equals(TypeRepere.vitesseCorrigee.getName()))
					nomColonne = com.faiveley.samng.principal.sm.data.enregistrement.Messages
							.getString("NomRepere.4");
			}
		}

		// ajouter nomcolonnes dans list de Rows
		Row noms = new Row(nbColonnes);
		noms.setCellsData(nomsColonnes.toArray());
		for (int i = 0; i < nbColonnes; i++) {
			noms.setValue(i, nomsColonnes.get(i));
		}
		lignes.add(0, noms);

		String stringTemp = "";

		int nbCaracteresMax = 130;

		List<Integer> caracMaxByColumn = new ArrayList<Integer>(nbColonnes);
		for (int i = 0; i < nbColonnes; i++) {
			caracMaxByColumn.add(0);
		}
		List<Integer> colonnesStop = new ArrayList<Integer>();
		colonnesStop.add(0);
		int nbCaracByLigne = 0;

		// quels sont les cellules les plus longues ?
		nbCaracByLigne = 0;
		for (Row r : lignes) {
			nbCaracByLigne = 0;
			for (int j = 0; j < r.getNbData(); j++) {
				if (r.getValue(j) != null)
					stringTemp = r.getValue(j);
				else
					stringTemp = "";
				if (stringTemp.length() > caracMaxByColumn.get(j)) {
					caracMaxByColumn.set(j, stringTemp.length() + 2);
				}
			}
		}

		for (int j = 0; j < caracMaxByColumn.size(); j++) {
			nbCaracByLigne = nbCaracByLigne + caracMaxByColumn.get(j) + 2;
			if (nbCaracByLigne > nbCaracteresMax) {
				// stocker les indices des colonnes o� on doit changer de page
				colonnesStop.add(j);
				nbCaracByLigne = caracMaxByColumn.get(j) + 2;
			}
		}

		colonnesStop.add(nbColonnes);
		int nbPages = colonnesStop.size() - 1;
		for (int i = 0; i < nbPages; i++) {
			// �crire les lignes avec les bons indices pour ne pas d�passer la
			// page
			writeLignes(colonnesStop.get(i), colonnesStop.get(i + 1), fis,
					lignes, caracMaxByColumn);
			pw.print("\n");
			pw.print("\n");
		}

		if (pw != null) {
			pw.close();
		}
	}

	public void parseRessource(String chemin,boolean explorer,int deb,int fin) throws AExceptionSamNG {
		try {
			if (new File(chemin).exists())
				new File(chemin).delete();

			OutputStream fout = new FileOutputStream(chemin);
			OutputStream bout = new BufferedOutputStream(fout);

			this.parseurFichier = new OutputStreamWriter(bout, "UTF-8");
			this.pw = new PrintWriter(chemin, "UTF-8");
			// this.parseurFichier = new FileWriter(chemin, true);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void parseRessource(File f) throws AExceptionSamNG {
		try {
			OutputStream fout = new FileOutputStream(f);
			OutputStream bout = new BufferedOutputStream(fout);

			this.parseurFichier = new OutputStreamWriter(bout, "UTF-8");
			this.pw = new PrintWriter(f, "UTF-8");
			// this.parseurFichier = new FileWriter(chemin, true);
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * M�thode d'export d'un fichier binaire
	 * 
	 * @param cheminFichier
	 *            chemin du fichier d'export
	 * @param filtre
	 *            objet AFiltreComposant
	 */
	public void exporterFichier(String cheminFichier, AFiltreComposant filtre) {

	}

}
