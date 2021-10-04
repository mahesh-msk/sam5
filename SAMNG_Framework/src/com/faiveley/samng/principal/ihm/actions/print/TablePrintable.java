package com.faiveley.samng.principal.ihm.actions.print;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JEditorPane;
import javax.swing.JTable;
import javax.swing.JTable.PrintMode;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.sm.VersionUtils;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;

public class TablePrintable implements Printable {

	private JTable table;
	private JTableHeader header;
	private TableColumnModel colModel;
	private int totalColWidth;
	private PrintMode printMode = PrintMode.NORMAL;
	private MessageFormat headerFormat;
	private MessageFormat footerFormat;
	private int last = -1;
	private int row = 0;
	private int col = 0;
	private Rectangle clip = new Rectangle(0, 0, 0, 0);
	private Rectangle hclip = new Rectangle(0, 0, 0, 0);
	private Rectangle tempRect = new Rectangle(0, 0, 0, 0);
	private static final int H_F_SPACE = 8;

	private Font headerFont = new Font("Arial", Font.PLAIN, 10);
	private Font footerFont = new Font("Arial", Font.PLAIN, 10);
	private Font tableFont = new Font("Arial", Font.PLAIN, 10);
	private int MAX_LIGNES_ENTETE = 1;

	public TablePrintable(JTable table, PrintMode printMode) {
		this.table = table;
		this.printMode = printMode;
		init();
	}

	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		return print2(graphics, pageFormat, pageIndex);
	}

	private void init() {
		this.table.setFont(tableFont);
		this.table.getTableHeader().setPreferredSize(
				(new Dimension(this.table.getColumnModel()
						.getTotalColumnWidth(), MAX_LIGNES_ENTETE * 16)));

		header = this.table.getTableHeader();
		colModel = this.table.getColumnModel();
		totalColWidth = this.table.getColumnModel().getTotalColumnWidth();

		hclip.height = header.getHeight();
		hclip.width = totalColWidth;

		this.headerFormat = new MessageFormat(this.getTexteEntete());

		this.footerFormat = new MessageFormat(this.getTextePiedPage());

		this.table.getTableHeader().setDefaultRenderer(
				new ExampleHeaderRenderer());
		this.table.setMaximumSize(new Dimension(hclip.width, hclip.height));
	}

	public synchronized int print2(Graphics graphics, PageFormat pageFormat,
			int pageIndex) throws PrinterException {
		int imgWidth = 100;
		int imgHeight = 100;
		if (pageFormat != null) {
			// for easy access to these values
			imgWidth = (int) pageFormat.getImageableWidth();
			imgHeight = (int) pageFormat.getImageableHeight();
		}

		if (imgWidth <= 0) {
			throw new PrinterException("Width of printable area is too small.");
		}

		// to pass the page number when formatting the header and footer
		// text
		Object[] pageNumber = new Object[] { Integer.valueOf(pageIndex + 1) };

		// fetch the formatted header text, if any
		String headerText = null;
		if (headerFormat != null) {
			headerText = headerFormat.format(pageNumber);
		}

		// fetch the formatted footer text, if any
		String footerText = null;
		if (footerFormat != null) {
			footerText = footerFormat.format(pageNumber);
		}

		String pageNumberText = null;
		pageNumberText = "[Page] : " + (pageIndex + 1);
		// to store the bounds of the header and footer text
		Rectangle2D hRect = null;
		Rectangle2D fRect = null;

		// the amount of vertical space needed for the header and footer
		// text
		int headerTextSpace = 0;
		int footerTextSpace = 0;

		// the amount of vertical space available for printing the table
		int availableSpace = imgHeight;

		// if there's header text, find out how much space is needed for it
		// and subtract that from the available space
		if (headerText != null) {
			graphics.setFont(headerFont);
			int nbLines = headerText.split("\n").length;
			hRect = graphics.getFontMetrics().getStringBounds(headerText,
					graphics);
			hRect = new Rectangle2D.Double(hRect.getX(),
					Math.abs(hRect.getY()), hRect.getWidth(), hRect.getHeight()
							* nbLines);
			headerTextSpace = (int) Math.ceil(hRect.getHeight() * nbLines);
			availableSpace -= headerTextSpace + H_F_SPACE;
		}

		// if there's footer text, find out how much space is needed for it
		// and subtract that from the available space
		if (footerText != null) {
			graphics.setFont(footerFont);
			fRect = graphics.getFontMetrics().getStringBounds(footerText,
					graphics);
			footerTextSpace = (int) Math.ceil(fRect.getHeight());
			availableSpace -= footerTextSpace + H_F_SPACE;
		}

		if (availableSpace <= 0) {
			throw new PrinterException("Height of printable area is too small.");
		}

		// depending on the print mode, we may need a scale factor to
		// fit the table's entire width on the page
		double sf = 1.0D;
		if (printMode == JTable.PrintMode.FIT_WIDTH && totalColWidth > imgWidth) {
			// if not, we would have thrown an acception previously
			assert imgWidth > 0;
			// it must be, according to the if-condition, since imgWidth > 0
			assert totalColWidth > 1;
			sf = (double) imgWidth / (double) totalColWidth;
		}

		// dictated by the previous two assertions
		assert sf > 0;
		// This is in a loop for two reasons:
		// First, it allows us to catch up in case we're called starting
		// with a non-zero pageIndex. Second, we know that we can be called
		// for the same page multiple times. The condition of this while
		// loop acts as a check, ensuring that we don't attempt to do the
		// calculations again when we are called subsequent times for the
		// same page.
		while (last < pageIndex) {
			// if we are finished all columns in all rows
			if (row >= table.getRowCount() && col == 0) {
				return NO_SUCH_PAGE;
			}

			// rather than multiplying every row and column by the scale
			// factor
			// in findNextClip, just pass a width and height that have
			// already
			// been divided by it
			int scaledWidth = (int) (imgWidth / sf);
			int scaledHeight = (int) ((availableSpace - hclip.height) / sf);

			// calculate the area of the table to be printed for this page
			findNextClip(scaledWidth, scaledHeight);
			last++;
		}

		// create a copy of the graphics so we don't affect the one given to
		// us
		Graphics2D g2d = (Graphics2D) graphics.create();

		// translate into the co-ordinate system of the pageFormat
		g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

		// to save and store the transform
		AffineTransform oldTrans;

		// if there's footer text, print it at the bottom of the imageable
		// area
		if (footerText != null) {
			oldTrans = g2d.getTransform();

			g2d.translate(0, imgHeight - footerTextSpace);

			String[] lines = footerText.split("\n");
			printText(g2d, lines, fRect, footerFont, imgWidth);

			Rectangle2D rectPageNumber = graphics.getFontMetrics()
					.getStringBounds(pageNumberText, graphics);

			g2d.translate(imgWidth - rectPageNumber.getWidth() - 0,
					fRect.getY() + fRect.getHeight() - 5);

			String[] linesPageNumber = new String[] { pageNumberText };
			printText(g2d, linesPageNumber, rectPageNumber, footerFont,
					imgWidth);
			g2d.setTransform(oldTrans);
		}

		// if there's header text, print it at the top of the imageable area
		// and then translate downwards
		if (headerText != null) {
			String[] lines = headerText.split("\n");
			printText(g2d, lines, hRect, headerFont, imgWidth);

			g2d.translate(0, headerTextSpace + H_F_SPACE);
		}

		// constrain the table output to the available space
		tempRect.x = 0;
		tempRect.y = 0;
		tempRect.width = imgWidth;
		tempRect.height = availableSpace;
		g2d.clip(tempRect);

		// if we have a scale factor, scale the graphics object to fit
		// the entire width
		if (sf != 1.0D) {
			g2d.scale(sf, sf);

			// otherwise, ensure that the current portion of the table is
			// centered horizontally
		} else {
			// int diff = (imgWidth - clip.width) / 2;
			// g2d.translate(diff, 0);
		}

		// store the old transform and clip for later restoration
		oldTrans = g2d.getTransform();
		Shape oldClip = g2d.getClip();

		// if there's a table header, print the current section and
		// then translate downwards
		if (header != null) {
			hclip.x = clip.x;
			hclip.width = clip.width;

			g2d.translate(-hclip.x, 0);
			g2d.clip(hclip);
			header.print(g2d);

			// restore the original transform and clip
			g2d.setTransform(oldTrans);
			g2d.setClip(oldClip);

			// translate downwards
			g2d.translate(0, hclip.height);
		}

		// print the current section of the table
		g2d.translate(-clip.x, -clip.y);
		g2d.clip(clip);
		table.print(g2d);

		// restore the original transform and clip
		g2d.setTransform(oldTrans);
		g2d.setClip(oldClip);

		// draw a box around the table
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, clip.width, hclip.height + clip.height);

		// dispose the graphics copy
		g2d.dispose();

		return PAGE_EXISTS;
	}

	private void printText(Graphics2D g2d, String[] lines, Rectangle2D rect,
			Font font, int imgWidth) {
		g2d.setColor(Color.BLACK);
		g2d.setFont(font);

		for (int i = 0; i < lines.length; i++) {
			int tx;
			/*
			 * // if the text is small enough to fit, center it if
			 * (rect.getWidth() < imgWidth) { tx = (int) (imgWidth / 2 -
			 * g2d.getFontMetrics().getStringBounds(lines[i], g2d).getWidth() /
			 * 2);
			 * 
			 * // otherwise, if the table is LTR, ensure the left side of // the
			 * text shows; the right can be clipped } else
			 */
			if (table.getComponentOrientation().isLeftToRight()) {
				tx = 0;

				// otherwise, ensure the right side of the text shows
			} else {
				tx = -(int) (Math.ceil(rect.getWidth()) - imgWidth);
			}

			int ty = (int) Math.ceil(Math.abs(rect.getY() + i
					* rect.getHeight() / lines.length));
			g2d.drawString(lines[i], tx, ty);
		}
	}

	private void findNextClip(int pw, int ph) {

		final boolean ltr = table.getComponentOrientation().isLeftToRight();
		// if we're ready to start a new set of rows
		if (col == 0) {
			if (ltr) {
				// adjust clip to the left of the first column
				clip.x = 0;
			} else {
				// adjust clip to the right of the first column
				clip.x = totalColWidth;
			}

			// adjust clip to the top of the next set of rows
			clip.y += clip.height;

			// adjust clip width and height to be zero
			clip.width = 0;
			clip.height = 0;

			// fit as many rows as possible, and at least one
			int rowCount = table.getRowCount();
			int rowHeight = table.getRowHeight(row);
			do {
				clip.height += rowHeight;

				if (++row >= rowCount) {
					break;
				}

				rowHeight = table.getRowHeight(row);
			} while (clip.height + rowHeight <= ph);
		}

		// we can short-circuit for JTable.PrintMode.FIT_WIDTH since
		// we'll always fit all columns on the page
		if (printMode == JTable.PrintMode.FIT_WIDTH) {
			clip.x = 0;
			clip.width = totalColWidth;
			return;
		}

		if (ltr) {
			// adjust clip to the left of the next set of columns
			clip.x += clip.width;
		}

		// adjust clip width to be zero
		clip.width = 0;

		// fit as many columns as possible, and at least one
		int colCount = table.getColumnCount();
		int colWidth = colModel.getColumn(col).getWidth();
		do {
			clip.width += colWidth;
			if (!ltr) {
				clip.x -= colWidth;
			}

			if (++col >= colCount) {
				// reset col to 0 to indicate we're finished all columns
				col = 0;
				break;
			}

			colWidth = colModel.getColumn(col).getWidth();
		} while (clip.width + colWidth <= pw);
	}

	/**
	 * Méthode de création de la chaine affichée dans l'entete d'une impression
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private String getTexteEntete() {
		InfosFichierSamNg infos = (InfosFichierSamNg) FabriqueParcours
				.getInstance().getParcours().getInfo();
		String nomFichier = infos.getNomFichierParcoursBinaire();
		nomFichier = nomFichier.replace("\\", "/");

		nomFichier = nomFichier.split("/")[nomFichier.split("/").length - 1];

		if (nomFichier.length() > 33)
			nomFichier = nomFichier.substring(0, 30) + "...";

		String nomFichierXmlAssocie = " " + infos.getNomFichierXml();

		// la chaine du nom du fichier xml contient des caractères de
		// bourrage :
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
	 * Méthode de création de la chaine affichée dans le pied de page d'une
	 * impression
	 * 
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private String getTextePiedPage() {

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

	public List<Integer> getGoodTailleColonnes(List<String> nomsColonnes,
			List<ConfigurationColonne> configColonnes) {
		int nbColonnes = nomsColonnes.size();
		List<Integer> taillecolonnes = new ArrayList<Integer>(nbColonnes);

		for (int i = 0; i < nbColonnes; i++) {
			// System.out.println(nomsColonnes.get(i));
			boolean find = false;
			int largeur = 0;
			for (int j = 0; j < configColonnes.size(); j++) {
				ConfigurationColonne config = configColonnes.get(j);

				if (nomsColonnes.get(i).equals(config.getNom())
						|| nomsColonnes.get(i).endsWith(config.getNom())) {
					if (config.getLargeur() == -1) {
						// mode AUTOMATIQUE
						largeur = config.getLargeurCalculee();
					} else {
						largeur = config.getLargeur();
					}
					taillecolonnes.add(largeur);
					find = true;
					break;
				} else if (GestionnairePool.getInstance().getVariable(
						config.getNom()) != null) {
					if (nomsColonnes.get(i).equals(
							GestionnairePool
									.getInstance()
									.getVariable(config.getNom())
									.getDescriptor()
									.getNomUtilisateur()
									.getNomUtilisateur(
											Activator.getDefault()
													.getCurrentLanguage()))
							|| nomsColonnes
									.get(i)
									.contains(
											GestionnairePool
													.getInstance()
													.getVariable(
															config.getNom())
													.getDescriptor()
													.getNomUtilisateur()
													.getNomUtilisateur(
															Activator
																	.getDefault()
																	.getCurrentLanguage()))) {
						if (config.getLargeur() == -1) {
							// mode AUTOMATIQUE
							largeur = config.getLargeurCalculee();
						} else {
							largeur = config.getLargeur();
						}
						taillecolonnes.add(largeur);
						find = true;
						break;
					}
				}
			}
			if (!find) {
				System.out.println("Probleme taille colonne");
				taillecolonnes.add(0);
			}
		}
		return taillecolonnes;
	}

	public class ExampleHeaderRenderer extends DefaultTableCellRenderer {
		private static final long serialVersionUID = -1613958042459334439L;

		public ExampleHeaderRenderer() {
			setOpaque(true);
		}

		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean selected, boolean focused, int row,
				int column) {
			JEditorPane lbl = new JEditorPane();
			lbl.setOpaque(true);
			lbl.setFont(headerFont);
			lbl.setForeground(Color.BLACK);
			lbl.setText((String) value);
			lbl.setAlignmentX(LEFT_ALIGNMENT);
			table.setGridColor(Color.BLACK);
			return lbl;
		}
	}
}