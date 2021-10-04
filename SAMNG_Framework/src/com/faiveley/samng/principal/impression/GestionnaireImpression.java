package com.faiveley.samng.principal.impression;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTable;
import javax.xml.parsers.ParserConfigurationException;

import net.sf.paperclips.GridPrint;
import net.sf.paperclips.ImagePrint;
import net.sf.paperclips.Margins;
import net.sf.paperclips.PageDecoration;
import net.sf.paperclips.PageNumber;
import net.sf.paperclips.PageNumberFormat;
import net.sf.paperclips.PageNumberPrint;
import net.sf.paperclips.PagePrint;
import net.sf.paperclips.PaperClips;
import net.sf.paperclips.Print;
import net.sf.paperclips.PrintJob;
import net.sf.paperclips.ScalePrint;
import net.sf.paperclips.TextPrint;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.ui.IWorkbenchWindow;
import org.xml.sax.SAXException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.print.Messages;
import com.faiveley.samng.principal.sm.VersionUtils;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

/**
 * Classe de gestion des impression de Sam Peut etre utilisée pour imprimer les
 * vues, l'ecran etc...
 * 
 * @author Olivier
 * 
 */
public class GestionnaireImpression {

	private Image image;
	private IWorkbenchWindow window;
	private JTable jtable;

	public IWorkbenchWindow getWindow() {
		return window;
	}

	public void setWindow(IWorkbenchWindow window) {
		this.window = window;
	}

	/**
	 * Méthode d'affichage de l'impression
	 * 
	 * @param window
	 */
	public void afficherImpression() {

		PrintDialog printDialog = new PrintDialog(window.getShell(), SWT.NONE);
		printDialog.setText("Print");

		PrinterData printerData = new PrinterData();
		printerData = printDialog.open();

		if (printerData != null) {
			printerData.orientation = PrinterData.LANDSCAPE;

			Print pagePrint = creerStructureImpression();
			// At this point you are ready to print the document
			PrintJob job = new PrintJob("Impression", pagePrint);
			job.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);

			Margins myMargin = new Margins();
			myMargin.top = 70;
			myMargin.bottom = 71;
			myMargin.left = 73;
			myMargin.right = 25; 
			job.setMargins(myMargin);
			try {
				PaperClips.print(job, printerData);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Méthode de création de la structure à imprimer : entete, corps, pied de
	 * page
	 * 
	 * @return
	 */
	public Print creerStructureImpression() {

		PageDecoration header = creerEnteteImpression();
		Print body = this.creerCorpImpression();
		PageDecoration footer = creerPiedImpression();

		PagePrint page = new PagePrint(header, body, footer);

		page.setHeaderGap(72);
		page.setFooterGap(72);

		return page;
	}

	/**
	 * Méthode de création de la structure d'entete
	 * 
	 * @return
	 */
	public static PageDecoration creerEnteteImpression() {
		PageDecoration header = new PageDecoration() {
			public Print createPrint(PageNumber pageNumber) {

				GridPrint gridPrint = new GridPrint("l:p");
				FontData fontData = new FontData("Arial", 10, SWT.NORMAL);
				TextPrint headerPrint = new TextPrint(getTexteEntete(),
						fontData);
				gridPrint.add(headerPrint);
				return gridPrint;
			}
		};
		return header;
	}

	/**
	 * Méthode de création de la structure du pied de page
	 * 
	 * @return
	 */
	public static PageDecoration creerPiedImpression() {
		PageDecoration footer = new PageDecoration() {
			public Print createPrint(PageNumber pageNumber) {
				GridPrint gridPrint = new GridPrint("l:232mm, R:p");
				FontData fontData = new FontData("Arial", 10, SWT.NORMAL);
				String piedPage = getTextePiedPage();

				// ajout du texte du pied
				TextPrint textePrint = new TextPrint("  " + piedPage, fontData);
				gridPrint.add(textePrint);

				FontData fontDataPageNumber = new FontData("Arial", 10,
						SWT.NORMAL);
				PageNumberPrint pageNumberPrint = new PageNumberPrint(
						pageNumber, fontDataPageNumber);
				PageNumberFormat pageNumberFormat = new PageNumberFormat() {
					@Override
					public String format(PageNumber pageNumber) {
						String libellePage = Messages
								.getString("ImpressionVueTableau.12");
						libellePage = "[" + libellePage + "]" + " : "
								+ pageNumber.getPageCount();
						return libellePage;
					}
				};
				pageNumberPrint.setPageNumberFormat(pageNumberFormat);
				gridPrint.add(pageNumberPrint);
				return gridPrint;
			}
		};
		return footer;
	}

	/**
	 * 
	 * @return
	 */
	public Print creerCorpImpression() {
		GridPrint gridPrint = new GridPrint("l:d:g");
		ScalePrint scalePrint = null;

		if (this.image != null) {
			ImageData imageData = this.image.getImageData();
			ImagePrint imagePrint = new ImagePrint(imageData);
			imagePrint.setDPI(72, 72);
			gridPrint.add(imagePrint);
			scalePrint = new ScalePrint(gridPrint);
		} else if (this.jtable != null) {

		}
		return scalePrint;
	}

	/**
	 * Méthode de création de la chaine affichée dans l'entete d'une impression
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String getTexteEntete() {		
		if (!ActivatorData.getInstance().isMultimediaFileAlone()) {
			InfosFichierSamNg infos = (InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo();
			String nomFichier = infos.getNomFichierParcoursBinaire();
			nomFichier = nomFichier.replace("\\", "/");
			nomFichier = nomFichier.split("/")[nomFichier.split("/").length - 1];

			if (nomFichier.length() > 33) {
				nomFichier = nomFichier.substring(0, 30) + "...";
			}

			String nomFichierXmlAssocie = " " + infos.getNomFichierXml();
			String crcFichierXmlAssocie = "  "	+ Messages.getString("ImpressionVueTableau.5") + " : " + infos.getCRCFichierXML();
			
			// la chaine du nom du fichier xml contient des caractères de bourrage :
			// unicode 0x0 qui ne permettent pas de concaténer
			// cette chaine avec autre chose
			// solution : suppression des caractères 0x0
			Pattern pattern = null;
			Matcher matcher = null;
			pattern = Pattern.compile("[\\000]*");
			matcher = pattern.matcher(nomFichierXmlAssocie);
			
			if (matcher.find()) {
				nomFichierXmlAssocie = matcher.replaceAll("");
			}
			
			return "[" + Messages.getString("ImpressionVueTableau.3") + "] :" + " " + nomFichier + " - " + nomFichierXmlAssocie + " - " + crcFichierXmlAssocie;
		} else if (ActivatorData.getInstance().getMultimediaFiles().size() == 1) {			
			return "[" + Messages.getString("ImpressionVueTableau.4") + "] :" + " " + ActivatorData.getInstance().getMultimediaFiles().get(0).getFile().getName();
		} else {
			return null;
		}
	}

	/**
	 * Méthode de création de la chaine affichée dans le pied de page d'une
	 * impression
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String getTextePiedPage() {
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dateDuJour = dateFormat.format(d);

		String chainePiedDePage = "["
				+ Messages.getString("ImpressionVueTableau.6") + "]" + " : SAM"
				+ VersionUtils.getFormattedVersion() + " - ["
				+ Messages.getString("ImpressionVueTableau.11") + "]" + " : "
				+ dateDuJour;
		return chainePiedDePage;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public JTable getJtable() {
		return jtable;
	}

	public void setJtable(JTable jtable) {
		this.jtable = jtable;
	}
}
