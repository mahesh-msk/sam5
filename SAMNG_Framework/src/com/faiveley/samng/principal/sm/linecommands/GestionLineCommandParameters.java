package com.faiveley.samng.principal.sm.linecommands;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Platform;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueListeContentProvider;
import com.faiveley.samng.principal.ihm.vues.VueTabulaireContentProvider;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.ListeFiltresProvider;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.TabulaireFiltresProvider;
import com.faiveley.samng.principal.sm.VersionUtils;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ParcoursComposite;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.parseurs.ChargeurParcours;
import com.faiveley.samng.principal.sm.parseurs.ParseurExport;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.util.file.GestionFichiers;

public class GestionLineCommandParameters {

	public static final String workspace = "-workspace";

	private static Runtime runtime = Runtime.getRuntime();

	private static String filename = "-f"; //$NON-NLS-1$
	private static String offsetMessage = "-o"; //$NON-NLS-1$
	private static String annot_lecture_seule = "-r"; //$NON-NLS-1$
	private static String perspective = "-p"; //$NON-NLS-1$
	private static String infos = "-h"; //$NON-NLS-1$
	private static String product = "-product"; //$NON-NLS-1$
	private static String export = "-x"; //$NON-NLS-1$
	private static String export_list = "-xlist"; //$NON-NLS-1$
	private static String filtre = "-filt"; //$NON-NLS-1$
	private static String rapport = "-rp"; //$NON-NLS-1$

	private static String filename_long = "--filename"; //$NON-NLS-1$
	private static String offsetMessage_long = "--offset"; //$NON-NLS-1$
	private static String annot_lecture_seule_long = "--readonly"; //$NON-NLS-1$
	private static String perspective_long = "--perspective"; //$NON-NLS-1$
	private static String infos_long = "--help"; //$NON-NLS-1$
	private static String export_long = "--export"; //$NON-NLS-1$
	private static String export_list_long = "--exportlist"; //$NON-NLS-1$
	private static String filtre_long = "--filter"; //$NON-NLS-1$
	private static String rapport_long = "--report"; //$NON-NLS-1$

	private static String nomfichier = "";
	private static int indiceMsg = -1;
	private static String nomPerspect = "";
	private static boolean Annot_Lect_seule = false;

	private static boolean isExport = false;
	private static boolean isExportList = false;
	private static boolean isExportReport = false;
	public static String filterName = "";
	public static String exportType = "";

	private static boolean help = false;

	private static Process p = null;

	public static boolean auMoinsUnArgument = false;
	public static boolean ligneCommande = false;

	// L'offset de la ligne de commande doit être actif qu'à la première
	// ouverture du fichier,
	// ensuite, même si on ouvre le même fichier l'offset doit être inactif et
	// donc toutes les vues
	// doivent pointer sur leur première ligne.
	private static boolean oneTime = true;// le positionnement offset ne se fait
											// qu'une fois

	public static boolean isOneTime() {
		return oneTime;
	}

	public static void setOneTime(boolean state) {
		oneTime = state;
	}

	public GestionLineCommandParameters() {

	}

	public static void echo(String... s) {
		File f = new File(RepertoiresAdresses.temp + "\\infos.bat");
		File dir = new File(RepertoiresAdresses.temp);
		try {
			if (!f.exists()) {
				f.createNewFile();
			}

			PrintWriter pw = new PrintWriter(f);
			pw.println("@ECHO off");
			for (String str : s) {
				str = str.replace("|", "^|");
				str = str.replace("<", "^<");
				str = str.replace(">", "^>");
				pw.println("echo " + str);
			}
			pw.println("pause");
			pw.close();

			String[] s2 = { "cmd", "/c", "start", "infos.bat" };
			p = runtime.exec(s2, null, dir);
			p.waitFor();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void reset() {
		nomfichier = ""; //$NON-NLS-1$		
		indiceMsg = 0;
		nomPerspect = ""; //$NON-NLS-1$
		Annot_Lect_seule = false;// "D:\work\3-Faiveley\SAM NG\fichiers de
									// parcours\parcours_ng\FP_TEST_VALID_SAM_B\Fp_VALID_SAM_5_B.tbf"
		// --filename "D:\work\3-Faiveley\SAM NG\fichiers de
		// parcours\parcours_ng\FP_TEST_VALID_SAM_B\Fp_VALID_SAM_5_B.tbf"
		// --offset 2871 --perspective P316 --readonly
		auMoinsUnArgument = false;
	}

	public static void gestionLineCommandParameters() {
		boolean erreur = false;
		String msgErreur = ""; //$NON-NLS-1$
		String param_incorrect = "";
		String[] args = Platform.getCommandLineArgs();
		String dossierLancementCommande = System.getProperty("user.dir");
		int nbArgs = args.length;

		// ignore the workspace option
		if (nbArgs == 2 && args[0].equals(workspace)) {
			return;
		}
		
		for (int i = 0; i < nbArgs; i++) {
			ligneCommande = true;
			if (args[i].equals(product) || args[i].equals(workspace)) {
				i++;
			} else {
				if (args[i].equals(filename) || args[i].equals(filename_long)) {
					auMoinsUnArgument = true;
					// si l'argument nom de fichier n'est pas présent
					if (i >= args.length - 1) {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_14;
						break;
					} else {
						nomfichier = args[i + 1];
						File file = new File(nomfichier);
						if (!file.isAbsolute()) {
							nomfichier = dossierLancementCommande + "\\"
									+ nomfichier;
							System.out.println("chemin "
									+ dossierLancementCommande + "\\"
									+ nomfichier);
							file = new File(nomfichier);
							if (!file.exists()) {
								GestionLineCommandParameters
										.echo(Messages.GestionLineCommandParameters_48);
								System.exit(0);
							}
						} else if (!file.exists()) {
							GestionLineCommandParameters
									.echo(Messages.GestionLineCommandParameters_48);
							System.exit(0);
						}
						i++;
					}
				} else if (args[i].equals(offsetMessage)
						|| args[i].equals(offsetMessage_long)) {
					auMoinsUnArgument = true;
					// si l'argument numéro de message n'est pas présent
					if (i >= args.length - 1) {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_15;
						break;
					} else {
						try {
							indiceMsg = Integer.valueOf(args[i + 1]);

							if (indiceMsg < 0) {
								String msgErreurs = Messages.GestionLineCommandParameters_42;
								GestionLineCommandParameters.echo(msgErreurs);
								// MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
								// com.faiveley.samng.principal.sm.linecommands.Messages.GestionLineCommandParameters_47,
								// msgErreurs);
								// System.out.println(msgErreurs);
								System.exit(0);
							}

						} catch (NumberFormatException e) {
							erreur = true;
							msgErreur = Messages.GestionLineCommandParameters_16;
							break;
						}
						i++;
					}
				} else if (args[i].equals(annot_lecture_seule)
						|| args[i].equals(annot_lecture_seule_long)) {
					auMoinsUnArgument = true;
					setAnnot_Lect_seule(true);
				} else if (args[i].equals(perspective)
						|| args[i].equals(perspective_long)) {
					auMoinsUnArgument = true;
					// si l'argument nom de perspective n'est pas présent
					if (i >= args.length - 1) {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_17;
						break;
					} else {
						nomPerspect = args[i + 1];
						i++;
					}
				} else if (args[i].equals(export)
						|| args[i].equals(export_long)) {
					auMoinsUnArgument = true;
					if (i >= args.length - 1
							|| (!"csv".equals(args[i + 1]) && !"tsv"
									.equals(args[i + 1]))) {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_19;
						break;
					} else {
						exportType = args[i + 1];
						i++;
						isExport = true;
					}
				} else if (args[i].equals(export_list)
						|| args[i].equals(export_list_long)) {
					auMoinsUnArgument = true;
					if (i >= args.length - 1
							|| (!"csv".equals(args[i + 1]) && !"tsv"
									.equals(args[i + 1]))) {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_19;
						break;
					} else {
						exportType = args[i + 1];
						i++;
						isExportList = true;
					}
				} else if (args[i].equals(rapport)
						|| args[i].equals(rapport_long)) {
					auMoinsUnArgument = true;
					isExportReport = true;
				} else if (args[i].equals(filtre)
						|| args[i].equals(filtre_long)) {
					auMoinsUnArgument = true;
					// si l'argument n'est pas présent
					if (i >= args.length - 1) {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_18;
						break;
					} else {
						filterName = args[i + 1];
						i++;
					}
				} else if (args[i].equals(infos) || args[i].equals(infos_long)) {
					auMoinsUnArgument = true;
					setHelp(true);
					String s1 = "SAM VERSION : "
							+ VersionUtils.getFormattedVersion();
					String s2 = Messages.GestionLineCommandParameters_30;
					String s3 = Messages.GestionLineCommandParameters_31;
					String s4 = Messages.GestionLineCommandParameters_32;
					String s5 = Messages.GestionLineCommandParameters_33;
					String s6 = Messages.GestionLineCommandParameters_34;
					String s7 = Messages.GestionLineCommandParameters_35;
					String s8 = Messages.GestionLineCommandParameters_50;
					String s9 = Messages.GestionLineCommandParameters_51;
					String s10 = Messages.GestionLineCommandParameters_52;
					String s11 = Messages.GestionLineCommandParameters_53;
					System.out.println(s1);
					System.out.println(s2);
					System.out.println(s3);
					System.out.println(s4);
					System.out.println(s5);
					System.out.println(s6);
					System.out.println(s7);
					System.out.println(s8);
					System.out.println(s9);
					System.out.println(s10);
					System.out.println(s11);
					GestionLineCommandParameters.echo(s1, s2, s3, s4, s5, s6,
							s7, s8, s9, s10, s11);
				} else {
					if (args[i].startsWith("-")) { //$NON-NLS-1$
						param_incorrect = Messages.GestionLineCommandParameters_37
								+ args[i]
								+ " "
								+ Messages.GestionLineCommandParameters_40;
						// si l'argument qui suit un mauvais argument avec un
						// tiret ne comporte pas de tiret
						if ((args.length >= i + 2)
								&& (!args[i + 1].startsWith("-"))) {
							if (args.length > i + 2) {
								// il reste des arguments à traiter
								param_incorrect = Messages.GestionLineCommandParameters_37
										+ args[i]
										+ " "
										+ args[i + 1]
										+ " "
										+ Messages.GestionLineCommandParameters_40;
								i = i + 1;
							} else {
								// on est arrivé au bout des arguments
								break;
							}
						}
					} else {
						erreur = true;
						msgErreur = Messages.GestionLineCommandParameters_39
								+ args[i] + " "
								+ Messages.GestionLineCommandParameters_40;
						break;
					}
				}
			}
		}

		gestionFichierAbsent();
		onlyHelp();
		if (isExport || isExportList || isExportReport) {
			File file = new File(nomfichier);
			if (file.isDirectory()) {
				File[] subFiles = file.listFiles();
				for (File subFile : subFiles) {
					if (isValidFile(subFile)) {
						runExport(subFile.getAbsolutePath());
					}
				}
			} else if (file.exists()) {
				runExport(nomfichier);
			}
			System.exit(0);
		}

		if (erreur) {
			System.out.println(msgErreur);
			GestionLineCommandParameters.echo(msgErreur);
			// MessageDialog.openError(Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell(),
			// com.faiveley.samng.principal.sm.linecommands.Messages.GestionLineCommandParameters_47,
			// msgErreur);
			System.exit(0);
		} else {
			if (!param_incorrect.equals("")) {
				GestionLineCommandParameters.echo(param_incorrect);
			}
		}
	}

	private static boolean isValidFile(File subFile) {
		if (subFile != null && !subFile.isDirectory()) {
			String fileName = subFile.getName();
			String extension = fileName.lastIndexOf('.') == -1 ? "" : fileName
					.substring(fileName.lastIndexOf('.') + 1);
			return !("".equals(extension) || "tsv".equals(extension) || "csv"
					.equals(extension));
		} else {
			return false;
		}
	}

	public static void positionnerMessage() {
		String msgErreur = "";

		if (indiceMsg != -1) {
			Message message = ActivatorData.getInstance().getVueData()
					.getDataTable().getEnregistrement()
					.getGoodMessage(indiceMsg);
			if (message == null) {
				msgErreur = Messages.GestionLineCommandParameters_42 + " "
						+ Messages.GestionLineCommandParameters_49;

				System.out.println(msgErreur);
				echo(msgErreur);
				System.exit(0);
			}
		}
		indiceMsg = -1;
	}

	public static void gestionFichierAbsent() {
		// si pas de fichier mentionné et pas de demande d'infos mais un
		// paramètre ok (o, p ou r) => erreur
		if (nomfichier.equals("")
				&& (!isHelp())
				&& ((!nomPerspect.equals("")) || (Annot_Lect_seule)
						|| (indiceMsg != -1) || isExport || isExportList || isExportReport)) {
			System.out.println(Messages.GestionLineCommandParameters_44);
			echo(Messages.GestionLineCommandParameters_44);
			System.exit(0);
		}
	}

	public static void onlyHelp() {
		// si que param help on ferme SAM
		if ((nomPerspect.equals("")) && (!Annot_Lect_seule)
				&& (indiceMsg == -1) && nomfichier.equals("") && help
				&& !isExport && !isExportList && !isExportReport) {
			System.exit(0);
		}
	}

	public static void runExport(String file) {
		try {
			if (isExport && isExportList) {
				echo(Messages.GestionLineCommandParameters_54);
			}
			// Seulement si au moins un des export est sélectionné
			if (isExport) {
				runExportVueTabulaire(file);

			}
			if (isExportList) {
				// lancement de l'export liste
				runExportVueListe(file);
			}
			if (isExportReport) {
				runExportReport(file);
			}
		} catch (AExceptionSamNG e) {
			e.printStackTrace();
			echo(e.getMessage());
		}
	}

	/**
	 * @param file
	 * @throws AExceptionSamNG
	 * @throws ParseurXMLException
	 */
	public static void runExportReport(String file) throws AExceptionSamNG,
			ParseurXMLException {
		loadFile(file);
		// Récupération du rapport d'erreur qui est le fichier de log
		File report = new File(RepertoiresAdresses.logs_parser_log_TXT);
		GestionFichiers.copyFile2(report,
				new File(file.substring(0, file.lastIndexOf('.')) + ".txt"));
	}

	/**
	 * @param file
	 * @throws AExceptionSamNG
	 * @throws ParseurXMLException
	 */
	public static void runExportVueTabulaire(String file)
			throws AExceptionSamNG, ParseurXMLException {
		loadFile(file);

		DummyActivator<TabulaireFiltresProvider> activator = new DummyActivator<TabulaireFiltresProvider>(
				new TabulaireFiltresProvider());
		DummyGestionnaireVueTabulaire gestionnaire = new DummyGestionnaireVueTabulaire();
		gestionnaire.setActivator(activator);

		VueTabulaireContentProvider contentProvider = new VueTabulaireContentProvider(
				gestionnaire, activator);
		// set the filter name to the content provider and gestionnaire
		if (!"".equals(filterName)) {
			gestionnaire.setFiltreApplique(filterName);
			contentProvider.setFilter(filterName);
		}

		contentProvider.initializeColumns();
		// VueProgressBar.getInstance().start();
		contentProvider.setExport(true);
		contentProvider.loadContent(null);
		contentProvider.setExport(false);
		Object[] rows = contentProvider.getElements(null);
		List<String> columnNames = contentProvider.getColumnLabels();

		exportFile(file, rows, columnNames);
	}

	/**
	 * @param file
	 * @throws AExceptionSamNG
	 * @throws ParseurXMLException
	 */
	public static void runExportVueListe(String file) throws AExceptionSamNG,
			ParseurXMLException {
		loadFile(file);

		DummyActivator<ListeFiltresProvider> activator = new DummyActivator<ListeFiltresProvider>(
				new ListeFiltresProvider());
		DummyGestionnaireVueListe gestionnaire = new DummyGestionnaireVueListe();
		gestionnaire.setActivator(activator);

		VueListeContentProvider contentProvider = new VueListeContentProvider(
				gestionnaire, activator);
		// set the filter name to the content provider and gestionnaire
		if (!"".equals(filterName)) {
			gestionnaire.setFiltreApplique(filterName);
			contentProvider.setFilter(filterName);
		}

		contentProvider.initializeColumns();
		// VueProgressBar.getInstance().start();
		contentProvider.setExport(true);
		contentProvider.loadContent(null);
		contentProvider.setExport(false);
		Object[] rows = contentProvider.getElements(null);
		List<String> columnNames = contentProvider.getColumnNames();

		exportFile(file, rows, columnNames);
	}

	/**
	 * @param file
	 * @throws AExceptionSamNG
	 * @throws ParseurXMLException
	 */
	public static void loadFile(String file) throws AExceptionSamNG,
			ParseurXMLException {
		ParcoursComposite parcours = FabriqueParcours.getInstance().getParcours();
		if (parcours != null && parcours.getData().getEnregistrement() != null) {
			Enregistrement enr = parcours.getData().getEnregistrement();
			enr.setMessages(new ListMessages());
			enr.setBadMessages(new ListMessages());
			enr.setLongueurEnregistrement(0);
		}
		// load the file
		ChargeurParcours.loadBinaryFile(file, 0, -1, false);
		ChargeurParcours.initializePools(file, false);
	}

	/**
	 * @param file
	 * @param rows
	 * @param columnNames
	 * @throws AExceptionSamNG
	 */
	public static void exportFile(String file, Object[] rows,
			List<String> columnNames) throws AExceptionSamNG {
		List<Row> selectionLignes = new ArrayList<Row>(rows.length);
		for (int j = 0; j < rows.length; j++) {
			Row r = (Row) rows[j];
			String[] tabStrings = r.getStrings();
			for (int i = 0; i < tabStrings.length; i++) {
				if (tabStrings[i] != null) {
					if (tabStrings[i].contains("\0")) {
						r.setValue(
								i,
								tabStrings[i].substring(0,
										tabStrings[i].indexOf("\0")));
					}
				}
			}
			selectionLignes.add((Row) rows[j]);
		}

		String outputFileName = file + "." + exportType;
		new File(outputFileName).delete();

		ParseurExport parseur = new ParseurExport();
		parseur.parseRessource(outputFileName, false, 0, -1);

		parseur.exporterLignes(outputFileName, columnNames, selectionLignes);
	}

	public static String getNomfichier() {
		return nomfichier;
	}

	public static void setNomfichier(String nomfichier) {
		GestionLineCommandParameters.nomfichier = nomfichier;
	}

	public static int getIndiceMsg() {
		return indiceMsg;
	}

	public static void setIndiceMsg(int indiceMsg) {
		GestionLineCommandParameters.indiceMsg = indiceMsg;
	}

	public static String getNomPerspect() {
		return nomPerspect;
	}

	public static void setNomPerspect(String nomPerspect) {
		GestionLineCommandParameters.nomPerspect = nomPerspect;
	}

	public static boolean isAnnot_Lect_seule() {
		return Annot_Lect_seule;
	}

	public static void setAnnot_Lect_seule(boolean annot_Lect_seule) {
		GestionLineCommandParameters.Annot_Lect_seule = annot_Lect_seule;
	}

	public static boolean isHelp() {
		return help;
	}

	public static void setHelp(boolean help) {
		GestionLineCommandParameters.help = help;
	}

}
