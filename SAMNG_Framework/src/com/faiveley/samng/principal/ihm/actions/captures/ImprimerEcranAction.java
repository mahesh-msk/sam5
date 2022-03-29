package com.faiveley.samng.principal.ihm.actions.captures;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

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
import org.eclipse.ui.IWorkbenchWindow;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.ICommandIds;
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

public class ImprimerEcranAction extends Action {
	protected final IWorkbenchWindow window;
	protected boolean vueVide = false;

	public ImprimerEcranAction(final IWorkbenchWindow window, final String text) {
		super(text);
		this.window = window;
		// The id is used to refer to the action in a menu or toolbar
		setId(ICommandIds.CMD_PRINT_SCREEN);
		// Associate the action with a pre-defined command, to allow key
		// bindings.
		setActionDefinitionId(ICommandIds.CMD_PRINT_SCREEN);
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/toolBar/vues_commun_imprimer.png"));
		setAccelerator(SWT.CTRL + 'P');
	}

	/**
	 * M�thode d'ouverture de la fenetre de dialogue de capture de la vue sur
	 * laquelle on a le focus
	 */
	public final void run() {
		try {
			// r�cup�ration du widget de contenu de la vue
			Composite contenu = getVueEcran();
			if (contenu == null)
				return;

			GC gc = new GC(contenu);
			Point tableSize = new Point(contenu.getSize().x - 10,
					contenu.getSize().y - 120);
			Image image = new Image(contenu.getDisplay(), tableSize.x,
					tableSize.y);

			gc.copyArea(image, 0, 50);
			gc.dispose();

			// creer repertoire si c pas fait encore
			File rep = new File(RepertoiresAdresses.temp);
			if (!rep.exists()) {
				rep.mkdir();
			}

			// suppression image existante
			File f = new File(RepertoiresAdresses.temp_captTemp_JPG);
			if (f.exists())
				f.delete();

			GestionnaireImpression gestionnaireImpression = new GestionnaireImpression();
			gestionnaireImpression.setImage(image);
			gestionnaireImpression.setWindow(window);
			gestionnaireImpression.afficherImpression();
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(this.window.getShell(),
					SWT.ICON_ERROR);
			messageBox.setMessage(Messages.getString("ImprimerVue.2"));
			messageBox.open();
		}
	}

	private Composite getVueEcran() {
		// r�cup�ration de la vue sur laquelle on a le focus
		try {
			return this.window.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
		} catch (Exception e) {
			return null;
		}
	}

	public static String getHeader() {
		String fileOpen;
		try {
			InfosFichierSamNg infos = (InfosFichierSamNg) FabriqueParcours
					.getInstance().getParcours().getInfo();
			fileOpen = infos.getNomFichierParcoursBinaire();
			fileOpen = fileOpen.replace("\\", "/");
			fileOpen = fileOpen.split("/")[fileOpen.split("/").length - 1];
			fileOpen = com.faiveley.samng.principal.ihm.actions.print.Messages
					.getString("ImpressionVueTableau.3")
					+ " :"
					+ " "
					+ fileOpen;
		} catch (Exception pe) {
			return "";
		}
		// return headerFormat.toString();
		return fileOpen;
	}

	public static String getFooter() {
		InfosFichierSamNg infos = (InfosFichierSamNg) FabriqueParcours
				.getInstance().getParcours().getInfo();
		String XMLfile = " " + infos.getNomFichierXml();
		String crc = "  "
				+ com.faiveley.samng.principal.ihm.actions.print.Messages
						.getString("ImpressionVueTableau.5") + ":"
				+ infos.getCRCFichierXML();

		String versionSAM = " "
				+ com.faiveley.samng.principal.ihm.actions.print.Messages
						.getString("ImpressionVueTableau.6")
				+ VersionUtils.getFormattedVersion();

		String texteFooter = versionSAM + "  -  " + XMLfile + "  -  " + crc;
		return texteFooter;
	}

	@Override
	public boolean isEnabled() {
		if (ActivatorData.getInstance().isFileEmpty())
			return false;
		return true;
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
				TextPrint headerPrint = new TextPrint(getHeader(), fontData);
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
				TextPrint footerPrint = new TextPrint("  " + getFooter(),
						fontData);
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

}