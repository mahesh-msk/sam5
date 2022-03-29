package com.faiveley.samng.principal.ihm.actions.captures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.xml.sax.SAXException;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.print.Messages;
import com.faiveley.samng.principal.impression.GestionnaireImpression;
import com.faiveley.samng.principal.sm.VersionUtils;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

import net.sf.paperclips.GridPrint;
import net.sf.paperclips.ImagePrint;
import net.sf.paperclips.PageDecoration;
import net.sf.paperclips.PageNumber;
import net.sf.paperclips.PagePrint;
import net.sf.paperclips.Print;
import net.sf.paperclips.ScalePrint;
import net.sf.paperclips.TextPrint;

/**
 * Classe responsable de la capture d'une vue
 * 
 * @author Olivier
 * 
 */
public class ImprimerVueAction extends Action {
	protected final IWorkbenchWindow window;
	protected boolean vueVide = false;

	public ImprimerVueAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		setId(ICommandIds.CMD_PRINT_VIEW_MESSAGE);
		setActionDefinitionId(ICommandIds.CMD_PRINT_VIEW_MESSAGE);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_imprimer.png"));
	}

	/**
	 * M�thode d'ouverture de la fenetre de dialogue de capture de la vue sur
	 * laquelle on a le focus
	 */
	public final void run() {
		try {
			// r�cup�ration du widget de contenu de la vue
			Composite contenu = getActiveViewContent();
			if (contenu == null)
				return;
			GC gc = new GC(contenu);

			Display display = contenu.getShell().getDisplay();
			Point tableSize = contenu.getSize();
			Image image = new Image(display, tableSize.x, tableSize.y);
			gc.copyArea(image, 0, 0);
			gc.dispose();

			// creer repertoire si c pas fait encore
			File rep = new File(RepertoiresAdresses.temp);
			if (!rep.exists()) {
				rep.mkdir();
			}

			// chargment de l'image dans un loader
			// ImageLoader imageLoader = new ImageLoader();
			// imageLoader.data = new ImageData[] {image.getImageData()};
			// imageLoader.save(RepertoiresAdresses.temp_captTemp_JPG,SWT.IMAGE_JPEG);

			// PrinterJob pjob = PrinterJob.getPrinterJob();
			// PrintRequestAttributeSet pras = new
			// HashPrintRequestAttributeSet();
			// pras.add(OrientationRequested.LANDSCAPE);
			// Paper papier = new Paper();
			// papier.setSize(595, 842); // format A4
			// papier.setImageableArea(5, 5, papier.getWidth()-10,
			// papier.getHeight()-10);
			//
			// pras.add(new MediaPrintableArea(
			// 0f,
			// 0f,
			// (float)papier.getWidth() / 72f,
			// (float)papier.getHeight() / 72f,
			// MediaPrintableArea.INCH
			// ));
			// if (pjob.printDialog(pras)) {
			// PrintService ps = pjob.getPrintService();
			// DocPrintJob job = ps.createPrintJob();
			// FileInputStream fin = new
			// FileInputStream(RepertoiresAdresses.temp_captTemp_JPG);
			// Doc doc = new SimpleDoc(fin, DocFlavor.INPUT_STREAM.GIF, null);
			// job.print(doc, pras);
			// fin.close();
			// }

			// chargment de l'image dans un loader
//			ImageLoader imageLoader = new ImageLoader();
//			imageLoader.data = new ImageData[1];
//			imageLoader.data[0] = image.getImageData();
//			imageLoader.save(RepertoiresAdresses.temp_captTemp_JPG,
//					SWT.IMAGE_JPEG);
//
//			PrintDialog printDialog = new PrintDialog(this.window.getShell(),
//					SWT.NONE);
//			printDialog.setText("Print");
//
//			PrinterData printerData = new PrinterData();
//			printerData = printDialog.open();
//
//			if (printerData != null) {
//				printerData.orientation = PrinterData.LANDSCAPE;
//
//				Print pagePrint = createPrint();
//				// At this point you are ready to print the document
//				PrintJob job = new PrintJob("Impression", pagePrint);
//				job.setOrientation(PaperClips.ORIENTATION_LANDSCAPE);
//				job.setMargins(0); // 36 points = 36/72" = 1/2" = 12.7mm
//				PaperClips.print(job, printerData);
//			}
			
			
			GestionnaireImpression gestionnaireImpression = new GestionnaireImpression();
			gestionnaireImpression.setImage(image);
			gestionnaireImpression.setWindow(window);
			gestionnaireImpression.afficherImpression();
			
			

		} catch (Exception e) {
			MessageBox messageBox = new MessageBox(this.window.getShell(),
					SWT.ICON_ERROR);
			messageBox.setMessage(Messages.getString("ImprimerVue.2"));
			messageBox.open();
		}
	}

	private Composite getActiveViewContent() {
		// r�cup�ration de la vue sur laquelle on a le focus
		try {
			IWorkbenchPart part = this.window.getActivePage().getActivePart();
			if (part == null)
				return null;

			return part instanceof ICapturable ? ((ICapturable) part)
					.getContenu() : null;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public boolean isEnabled() {
		return !vueVide;
	}

	public boolean isVueVide() {
		return vueVide;
	}

	public void setVueVide(boolean vueVide) {
		this.vueVide = vueVide;
	}

	public static Print createPrint() {
		PageDecoration header = createHeader();
		Print body = createBody();
		PageDecoration footer = createFooter();

		PagePrint page = new PagePrint(header, body, footer);
		page.setHeaderGap(72 / 4);
		page.setFooterGap(72 / 8);

		return page;
	}

	public static PageDecoration createHeader() {
		PageDecoration header = new PageDecoration() {
			public Print createPrint(PageNumber pageNumber) {
				GridPrint gridPrint = new GridPrint("c:d:g(100)");
				FontData fontData = new FontData("Arial", 16, SWT.BOLD);
				TextPrint headerPrint = new TextPrint(getEnteteImpression(),
						fontData);
				gridPrint.add(new TextPrint());
				gridPrint.add(headerPrint);

				return gridPrint;
			}
		};
		return header;
	}

	public static PageDecoration createFooter() {
		PageDecoration footer = new PageDecoration() {
			public Print createPrint(PageNumber pageNumber) {
				GridPrint gridPrint = new GridPrint("c:d:g(100)");
				FontData fontData = new FontData("Arial", 16, SWT.NORMAL);
				String piedPage = getPiedPageImpression();
				TextPrint footerPrint = new TextPrint("  " + piedPage, fontData);
				gridPrint.add(footerPrint);
				gridPrint.add(new TextPrint());
				return gridPrint;
			}
		};
		return footer;
	}

	public static Print createBody() {
		GridPrint gridPrint = new GridPrint("l:d:g");

		Image image = null;
		try {
			image = new Image(Display.getCurrent(), new FileInputStream(
					RepertoiresAdresses.temp_captTemp_JPG));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ImageData imageData = image.getImageData();
		ImagePrint imagePrint = new ImagePrint(imageData);

		imagePrint.setDPI(72, 72);

		gridPrint.add(imagePrint);
		ScalePrint scalePrint = new ScalePrint(gridPrint);
		return scalePrint;
	}

	/**
	 * M�thode de cr�ation de la chaine affich�e dans l'entete d'une impression
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String getEnteteImpression() {
		InfosFichierSamNg infos = (InfosFichierSamNg) FabriqueParcours
				.getInstance().getParcours().getInfo();
		String nomFichier = infos.getNomFichierParcoursBinaire();
		nomFichier = nomFichier.replace("\\", "/");
		nomFichier = nomFichier.split("/")[nomFichier.split("/").length - 1];

		String nomFichierXmlAssocie = " " + infos.getNomFichierXml();

		// la chaine du nom du fichier xml contient des caract�res de bourrage :
		// unicode 0x0 qui ne permettent pas de concat�ner
		// cette chaine avec autre chose
		// solution : suppression des caract�res 0x0
		Pattern pattern = null;
		Matcher matcher = null;
		pattern = Pattern.compile("[\\000]*");
		matcher = pattern.matcher(nomFichierXmlAssocie);
		if (matcher.find()) {
			nomFichierXmlAssocie = matcher.replaceAll("");
		}

		String crcFichierXmlAssocie = "  "
				+ Messages.getString("ImpressionVueTableau.5") + " : "
				+ infos.getCRCFichierXML();

		String chaineEntete = "["
				+ Messages.getString("ImpressionVueTableau.3") + "] :" + " "
				+ nomFichier + " - " + nomFichierXmlAssocie + " - "
				+ crcFichierXmlAssocie;

		return chaineEntete;
	}

	/**
	 * M�thode de cr�ation de la chaine affich�e dans le pied de page d'une
	 * impression
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private static String getPiedPageImpression() {
		Date d = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/YYYY");
		String dateDuJour = dateFormat.format(d);

		String chainePiedDePage = "["
				+ Messages.getString("ImpressionVueTableau.6") + "]" + " : SAM"
				+ VersionUtils.getFormattedVersion() + " - ["
				+ Messages.getString("ImpressionVueTableau.11") + "]" + " : "
				+ dateDuJour;
		return chainePiedDePage;
	}

}
