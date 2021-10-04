package com.faiveley.samng.principal.ihm.actions.print;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.TableItem;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.AVueTableContentProvider;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;


public abstract class ImpressionVueTableau {

	public int MAX_LIGNES_ENTETE=1;
	public static Font fontheader = new Font("Arial", Font.PLAIN, 10);

	public void lancerlimpression(){
		try {
			exporterSelection();
		} catch (AExceptionSamNG e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Méthode d'export de la selection de la vue tabulaire: fait appel à la
	 * classe ParseurExport du framework
	 * 
	 * @param cheminFichier
	 * @return
	 * @throws AExceptionSamNG
	 */
	private void exporterSelection() throws AExceptionSamNG {
		String[][] leslignes = null;
		TableItem[] items = getSelectionTableItem();
		int nbLignesSel = items.length;
		String stringTemp = "";

		AVueTableContentProvider contentProvider = getContentProvider();
		ConfigurationColonne[] largeurColonnes=contentProvider.gestionaireVue.getConfigurationColonnes();
		List<ConfigurationColonne> configColonnes=new ArrayList<ConfigurationColonne>(0);
		for (int i = 0; i < largeurColonnes.length; i++) {
			if (largeurColonnes[i].isAffiche()) {
				configColonnes.add(largeurColonnes[i]);
			}
		}

		if (nbLignesSel > 0) {
			DialogPrint dp=new DialogPrint();
			dp.afficher(nbLignesSel > 1);
			String res=dp.res;
			if(res.equals("NO")){
				Object[] rows = contentProvider.getElements(null);
				int nblignes=rows.length;
				leslignes=new String[nblignes][];
				stringTemp = "";
				int indice=0;
				for (Object r : rows) {
					Row rangee=(Row)r;
					int nbData=rangee.getNbData();
					leslignes[indice]=new String[nbData];
					for (int j = 0; j < nbData; j++) {
						if (j==0) {
							rangee.getCellData(j);
						}
						if (rangee.getValue(j) != null)
							stringTemp = rangee.getValue(j);
						else
							stringTemp = "";

						stringTemp =stringTemp.trim();
						//						parseurFichier.write(stringTemp + "\t", 0, stringTemp.length() + 1);
						leslignes[indice][j]=gestionCaractereFinDeChaine(stringTemp);
					}
					//					parseurFichier.write("\n");
					indice++;
				}
			}else if(res.equals("YES")){
				stringTemp = "";
				int indice=0;
				leslignes=new String[nbLignesSel][];
				for (int i = 0; i < nbLignesSel; i++) {
					Row rangee=((Row)items[i].getData());
					int nbData=rangee.getNbData();
					leslignes[indice]=new String[nbData];
					for (int j = 0; j < nbData; j++) {
						if (rangee.getValue(j) != null)
							stringTemp = rangee.getValue(j);
						else
							stringTemp = "";

						stringTemp =stringTemp.trim();
						//						parseurFichier.write(stringTemp + "\t", 0, stringTemp.length() + 1);
						leslignes[indice][j]=gestionCaractereFinDeChaine(stringTemp);	//issue 726
					}
					//					parseurFichier.write("\n");
					indice++;
				}
			}else{
				return;
			}
			ImprimerJTable(replaceLabelsColumns(getColumnLabels()),leslignes,configColonnes);
		}
	}

	//issue 726
	public String gestionCaractereFinDeChaine(String chaine){
		String finChaine="\0";
		if (chaine.contains(finChaine)) {
			String[] split=chaine.split(finChaine);
			return split[0];
		}else{
			return chaine;
		}
	}
	
	public abstract List<String> getColumnLabels();

	public List<String> replaceLabelsColumns(List<String> nomsColonnes){
		for (String nomColonne : nomsColonnes) {
			if(nomColonne!=null){
				if(nomColonne.equals(TypeRepere.distanceCorrigee.getName()))
					nomColonne = com.faiveley.samng.principal.ihm.vues.configuration.Messages.getString("GestionnaireVueListeBase.5");
				else if(nomColonne.equals(TypeRepere.vitesseCorrigee.getName()))
					nomColonne = com.faiveley.samng.principal.sm.data.enregistrement.Messages.getString("NomRepere.4");
			}
		}
		return nomsColonnes;
	}

	/**
	 * Méthode de recherche de l'instance de la vue tabulaire
	 * 
	 * @return instance de VueTabulaire
	 */
	protected abstract TableItem[] getSelectionTableItem();
	protected abstract AVueTableContentProvider getContentProvider();

	public void ImprimerJTable(List<String> columnNames, String [][] selectionLignes, List<ConfigurationColonne> configColonnes){
		try {
			try {
				InfosFichierSamNg infos=(InfosFichierSamNg) FabriqueParcours.getInstance().getParcours()
						.getInfo();
				String fileOpen= infos.getNomFichierParcoursBinaire();
				fileOpen=fileOpen.replace("\\", "/");
				fileOpen=fileOpen.split("/")[fileOpen.split("/").length-1];
				fileOpen=Messages.getString("ImpressionVueTableau.3")+" :"+" "+fileOpen;

				String XMLfile=" "+infos.getNomFichierXml();
				// String crc="  "+Messages.getString("ImpressionVueTableau.5")+":"+infos.getCRCFichierXML();
				
				//correction anomalie sur le pied de page
				// la chaine du nom du fichier xml contient des caractères de bourrage : unicode 0x0 qui ne permettent pas de concaténer 
				// cette chaine avec autre chose
				//solution : suppression des caractères 0x0
				Pattern pattern = null;
				Matcher matcher = null;
				pattern = Pattern.compile("[\\000]*");
				matcher = pattern.matcher(XMLfile);
				if (matcher.find()) {
					XMLfile = matcher.replaceAll("");
				}

				JFrame frame = new JFrame();
				//Font fonttable = new Font("Arial", Font.PLAIN, 10);

				List<Integer> tailleColonnes=getGoodTailleColonnes(columnNames, configColonnes);
				List<String> columnsNames2=getNomscolonnesWithRC(columnNames,tailleColonnes,frame.getFontMetrics(fontheader),frame.getGraphics());

				
				TableauImpression tableauImpression = new TableauImpression((Object[][]) selectionLignes,columnsNames2.toArray(), columnNames, configColonnes);
				
//				table=new JTable((Object[][]) selectionLignes,columnsNames2.toArray()){
//					
//					@Override
//			        public Printable getPrintable(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {
//			           return new TablePrintable(this, PrintMode.NORMAL, headerFormat, footerFormat);
//			        }
//				};
				
				
//				table.setFont(fonttable);
//				table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//
//				table.getTableHeader().setPreferredSize((new Dimension(table.getColumnModel().getTotalColumnWidth(),MAX_LIGNES_ENTETE*16)));
//
//				for (int i = 0; i < table.getColumnCount(); i++) {
//					int largeur=tailleColonnes.get(i)+5;
//					table.getColumnModel().getColumn(i).setMinWidth(largeur);
//					table.getColumnModel().getColumn(i).setMaxWidth(largeur);
//				}

//				PrintRequestAttributeSet attr = new HashPrintRequestAttributeSet();
//				attr.add(OrientationRequested.LANDSCAPE);
				//				attr.add(MediaSizeName.ISO_A4);

				JScrollPane panel = new JScrollPane(tableauImpression);
				frame.add(panel, BorderLayout.CENTER);    
				frame.setVisible(true);
				frame.setVisible(false);
				frame.setSize(tableauImpression.getSize());
				frame.setLocation(10000,10000);

				PrintPreview p = new PrintPreview(tableauImpression);
				p.openPreview();
				
				tableauImpression=null;
				frame.dispose();
				frame=null;
			} catch (Exception pe) {
				pe.printStackTrace();
				System.err.println("Error printing: " + pe.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR);
			messageBox.setMessage(com.faiveley.samng.principal.ihm.actions.captures.Messages.getString("ImprimerVue.2"));
			messageBox.open();
		}
	}

	
	private List<String> getNomscolonnesWithRC(List<String> columnNames, List<Integer> tailleColonnes, FontMetrics fm, Graphics g) {
		int nbColonnes=tailleColonnes.size();
		List<String> colummNamesToReturn=new ArrayList<String>(nbColonnes);
		for (int i = 0; i < nbColonnes; i++) {
			String nomCol=columnNames.get(i);
			int largeurColonne=tailleColonnes.get(i);
			int largeurTexte=(int) ((fm.stringWidth(nomCol)));
			int nbLignes=1;
			if (largeurColonne!=0) {
				nbLignes=(int) ((largeurTexte/(largeurColonne*0.9))+1);
			}
			if (nbLignes>MAX_LIGNES_ENTETE) {
				MAX_LIGNES_ENTETE=nbLignes;
			}
			colummNamesToReturn.add(nomCol);
		}
		return colummNamesToReturn;
	}

	public static List<Integer> getGoodTailleColonnes(List<String> nomsColonnes, List<ConfigurationColonne> configColonnes) {
		int nbColonnes=nomsColonnes.size();
		List<Integer> taillecolonnes=new ArrayList<Integer>(nbColonnes);
		for (int i = 0; i < nbColonnes; i++) {
//							System.out.println(nomsColonnes.get(i));
			boolean find=false;
			int largeur=0;
			for (int j = 0; j < configColonnes.size(); j++) {
				ConfigurationColonne config=configColonnes.get(j);
				if (nomsColonnes.get(i).equals(config.getNom()) || nomsColonnes.get(i).contains(config.getNom())) {
					if (config.getLargeur()==-1) {
						//mode AUTOMATIQUE
						largeur=config.getLargeurCalculee();
					}else{
						largeur=config.getLargeur();
					}
					taillecolonnes.add(largeur);
					find=true;
					break;
				}else if (GestionnairePool.getInstance().getVariable(config.getNom())!=null) {
					if(nomsColonnes.get(i).equals(GestionnairePool.getInstance().getVariable(config.getNom()).getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()))
							||nomsColonnes.get(i).contains(GestionnairePool.getInstance().getVariable(config.getNom()).getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage()))
							){
						if (config.getLargeur()==-1) {
							//mode AUTOMATIQUE
							largeur=config.getLargeurCalculee();
						}else{
							largeur=config.getLargeur();
						}
						taillecolonnes.add(largeur);
						find=true;
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
}
