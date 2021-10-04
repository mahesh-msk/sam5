package com.faiveley.samng.principal.sm.parseurs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.ListDialog;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.formats.FormatJRU;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class BridageFormats {
	private static final BridageFormats INSTANCE = new BridageFormats();
	protected List<FormatSAM> listFormats;
	protected List<String> extensionsValides;
	protected static boolean gestionConflitExtension = false;
	protected static boolean gestionConflitExtensionInitial = false;
	protected FormatSAM formatFichierOuvert = null;
	protected static ArrayList<String> list = new ArrayList<String>(0);
	protected static String fileName = "";
	protected static String fileName2 = "";
	protected static FormatSAM retourMeth1 = null;
	private Properties props = new Properties();
	
	public FormatSAM getFormatFichierOuvert(String fileName) {
		if (formatFichierOuvert != null && fileName.equals("")) {
			return formatFichierOuvert;
		} else {
			return getFormat(fileName);
		}
	}

	public void setFormatFichierOuvert(FormatSAM formatFichierOuvert) {
		this.formatFichierOuvert = formatFichierOuvert;
	}

	public static boolean isGestionConflitExtensionInitial() {
		return gestionConflitExtensionInitial;
	}

	public static void setGestionConflitExtensionInitial(boolean gestionConflitExtensionInitial) {
		BridageFormats.gestionConflitExtensionInitial = gestionConflitExtensionInitial;
	}

	protected BridageFormats() {
		loadProperties();
		listFormats = new ArrayList<FormatSAM>(4);

		FormatSAM.TOMNG.setCode(0x01);
		FormatSAM.TOMNG.setFormat("TOMNG");
		List<String> extensionsTOMNG = new ArrayList<String>(2);
		extensionsTOMNG.add(".tbf");
		extensionsTOMNG.add(".cbf");
		FormatSAM.TOMNG.setExtensions(extensionsTOMNG);

		FormatSAM.TOM4.setCode(0x02);
		FormatSAM.TOM4.setFormat("TOM4");
		List<String> extensionsTOM4 = new ArrayList<String>(5);
		extensionsTOM4.add(".lkb");
		extensionsTOM4.add(".ftb");
		extensionsTOM4.add(".ltb");
		extensionsTOM4.add(".fkb");
		extensionsTOM4.add(".lwb");
		FormatSAM.TOM4.setExtensions(extensionsTOM4);

		FormatSAM.ATESS.setCode(0x04);
		FormatSAM.ATESS.setFormat("ATESS");
		List<String> extensionsATESS = new ArrayList<String>(3);
		extensionsATESS.add(".lpb");
		extensionsATESS.add(".ftb");
		extensionsATESS.add(".fkb");
		extensionsATESS.add(".fpb");
		FormatSAM.ATESS.setExtensions(extensionsATESS);

		FormatSAM.JRU.setCode(0x04);
		FormatSAM.JRU.setFormat("JRU");
		List<String> extensionsJRU = new ArrayList<String>(2);
		extensionsJRU.add(".jru");
		extensionsJRU.add(".bru");
		FormatSAM.JRU.setExtensions(extensionsJRU);

		FormatSAM.MULTIMEDIA.setCode(0x05);
		FormatSAM.MULTIMEDIA.setFormat("MULTIMEDIA");
		List<String> extensionsMULTIMEDIA = new ArrayList<String>(1);
		
		FormatSAM.COMPRESSED.setCode(0x06);
		FormatSAM.COMPRESSED.setFormat("COMPRESSED");
		List<String> extensionsCOMPRESSED = new ArrayList<String>(1);
		extensionsCOMPRESSED.add(".gz");
		FormatSAM.COMPRESSED.setExtensions(extensionsCOMPRESSED);
		
		try {
			String[] multimedia_files = ((String) props.get("multimedia_files")).split(",");
			
			for (String m : multimedia_files) {
				extensionsMULTIMEDIA.add(String.format(".%s", m));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}		
		
		FormatSAM.MULTIMEDIA.setExtensions(extensionsMULTIMEDIA);
		
		listFormats.add(FormatSAM.MULTIMEDIA);
		listFormats.add(FormatSAM.TOMNG);
		listFormats.add(FormatSAM.TOM4);
		listFormats.add(FormatSAM.ATESS);
		listFormats.add(FormatSAM.JRU);
		listFormats.add(FormatSAM.COMPRESSED);
	}
	
	private void loadProperties() {
		try {
			FileInputStream inStream;
			String cheminFichiermissions_PROPERTIES = RepertoiresAdresses.missions_PROPERTIES;
			inStream = new FileInputStream(new File(cheminFichiermissions_PROPERTIES));
			props.load(inStream);		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static BridageFormats getInstance() {
		return INSTANCE;
	}

	public boolean maskResult(int code, int mask){
		return ((code & mask) > 0);
	}

	public List<String> CodeToFormats(int code) {
		List<String> list = new ArrayList<String>(listFormats.size());
		
		for (FormatSAM format : listFormats) {
			list.add(format.getFormat());
			format.setEnable(true);
		}
		
		return list;
	}

	public static boolean isGestionConflitExtension() {
		return gestionConflitExtension;
	}

	public static void setGestionConflitExtension(boolean gestionConflitExtension) {
		BridageFormats.gestionConflitExtension = gestionConflitExtension;
	}

	public void initialiseValides() {
		ArrayList<String> extensionsV = new ArrayList<String>();
		int nbFormats = this.listFormats.size();
		
		for (int i = 0; i < nbFormats; i++) {
			if (this.listFormats.get(i).isEnable()) {
				for (int j = 0; j < this.listFormats.get(i).getExtensions().size(); j++) {
					extensionsV.add(this.listFormats.get(i).getExtensions().get(j).toUpperCase());
				}
			}
		}
		
		extensionsV.trimToSize();
		this.extensionsValides = extensionsV;
	}

	public List<String> getExtensionsValides() {
		return extensionsValides;
	}

	public List<String> getExtensionsFromFormat(FormatSAM format) {
		int nbExtensions = format.getExtensions().size();
		List<String> extensions = new ArrayList<String>(nbExtensions);
		
		for (int i = 0; i < nbExtensions; i++) {
			extensions.add(format.getExtensions().get(i));
		}
		
		return extensions;
	}

	public boolean isextensionValideFromFormat(FormatSAM format, String nomFichier) {
		int nbExtensions=format.getExtensions().size();
		
		for (int i = 0; i < nbExtensions; i++) {
			if (nomFichier.toUpperCase().endsWith(format.getExtensions().get(i).toUpperCase())) {
				return true;
			}
		}
		
		return false;
	}

	public boolean isextensionValide(String nomFichier) {
		int nbExt = this.extensionsValides.size();
		
		for (int i = 0; i < nbExt; i++) {
			if (nomFichier.toUpperCase().endsWith(this.extensionsValides.get(i).toUpperCase())) {
				return true;
			}
		}
		
		return false;
	}

	public static FormatSAM gestionFichierVER(String fileName) {
		BridageFormats.fileName = fileName;
		int cesure = fileName.lastIndexOf("\\");
		String chemin = null;
		
		try {
			chemin = fileName.substring(0,cesure);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File repertoire=new File(chemin);
		String file = fileName.substring(0, fileName.length() - 3);

		if ( repertoire.isDirectory ( ) ) {
			File[] list = repertoire.listFiles();
			
			if (list != null) {
				for ( int i = 0; i < list.length; i++) {
					if(list[i].getAbsolutePath().toUpperCase().contains(file.toUpperCase()) 
						&& list[i].getAbsolutePath().toUpperCase().endsWith(".VER")) {
						FormatSAM format = readVER(list[i].getAbsolutePath());
						
						if (format != null) {
							return format;
						}
						break;
					}
				} 
			} else {
				System.err.println(repertoire + " : Erreur de lecture.");
				return null;
			}
		} 

		if (isGestionConflitExtension()) {
			Display.getDefault().syncExec(new Runnable() {
				public void run() {
					int cesure = BridageFormats.fileName.lastIndexOf("\\");
					String file = BridageFormats.fileName.substring(0,BridageFormats.fileName.length() - 3);

					MessageDialog mdi = new MessageDialog (
						Display.getCurrent().getActiveShell(),
						Messages.getString("FormatFileQuestion.0"),
						null,
						BridageFormats.fileName.substring(cesure + 1, BridageFormats.fileName.length()),
						MessageDialog.QUESTION,
						new String[] {"ATESS", "TOM"},
						0);
					
					int res = -1;
					res = mdi.open();

					if (res==0) {
						FormatSAM.TOM4.setEnable(false);
						createVER(true,file,false,"");
						BridageFormats.setGestionConflitExtension(false);
						BridageFormats.retourMeth1=FormatSAM.ATESS;
					} else {
						FormatSAM.ATESS.setEnable(false);
						createVER(false,file,false,"");
						BridageFormats.setGestionConflitExtension(false);
						BridageFormats.retourMeth1=FormatSAM.TOM4;
					}
				}
			});
			
			return BridageFormats.retourMeth1;
		} else {
			return null;
		}
	}

	public static FormatSAM gestionFichierjru(String fileName) {
		String fileName3 = new String(fileName);
		if (ActivatorData.getInstance().isCompressedFile()) {
			DecompressedFile df = ActivatorData.getInstance().getDecompressedFile();
			File f = new File(df.getCompressedFileName());
			fileName3 = new File(f.getParent(), df.getInnerFileName()).getAbsolutePath();
		}
		
		BridageFormats.retourMeth1 = FormatSAM.JRU;
		String extension=fileName.substring(fileName.length() - 4);
		
		if (extension.toUpperCase().equals(".BRU")) {
			BridageFormats.retourMeth1.setFjru(FormatJRU.bru);
			
			// Si le fichier binaire est un bru, on peut récupérer la version du fichier dans son entete
			try {
				String version = ParseurParcoursJRU.getInstance().getVersionDansEnteteBinaire(fileName);
				BridageFormats.retourMeth1.getFjru().setVersion(version);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			return BridageFormats.retourMeth1;
		} else if (extension.toUpperCase().equals(".JRU")) {
			BridageFormats.retourMeth1.setFjru(FormatJRU.jru);

			int cesure = fileName3.lastIndexOf("\\");
			String chemin = fileName3.substring(0, cesure);
			File repertoire = new File(chemin);
			fileName2 = fileName3.substring(0, fileName3.length() - 3);
			
			if (repertoire.isDirectory()) {
				File[] list = repertoire.listFiles();
				if (list != null) {
					for (int i = 0; i < list.length; i++) {
						if (list[i].getAbsolutePath().contains(fileName2 + "jru.ver")) {
							String version = list[i].getAbsolutePath();
							version=version.replace(fileName2 + "jru.ver" + ".", "");
							
							if (version != "") {
								BridageFormats.retourMeth1.getFjru().setVersion(version);
								fileName2 = fileName.substring(0, fileName.length() - 3);
								return BridageFormats.retourMeth1;
							}
							
							break;
						}
					} 
				} else {
					System.err.println(repertoire + " : Erreur de lecture.");
					BridageFormats.retourMeth1.getFjru().setVersion("");
					fileName2 = fileName.substring(0, fileName.length() - 3);					
					return BridageFormats.retourMeth1;
				}
			} 
			fileName2 = fileName.substring(0, fileName.length() - 3);		

			
			Display.getDefault().syncExec(new Runnable(){
				int res =- 1;
				public void run() {
					String chemin = fileName2;
					if(ActivatorData.getInstance().isCompressedFile()){
						DecompressedFile df = ActivatorData.getInstance().getDecompressedFile();
						File f = new File(df.getCompressedFileName());
						chemin = new File(f.getParent(), df.getInnerFileName()).getAbsolutePath();
						chemin = chemin.substring(0, chemin.lastIndexOf('.') + 1);
				}
					
					ListDialog ld=new ListDialog(Display.getCurrent().getActiveShell());
					ld.setAddCancelButton(false);
					ld.setBlockOnOpen(true);
					ld.setTitle(Messages.getString("FormatFileQuestion.1"));
					ld.setMessage(Messages.getString("FormatFileQuestion.2") + " : " + chemin);
					list = getVersionFromFile();
					String vers= "";
					
					if (list == null) {
						vers = "";
					} else {
						ld.setLabelProvider(new ILabelProvider(){
							public void dispose() {
							}
							public boolean isLabelProperty(Object element, String property) {
								return false;
							}
							public Image getImage(Object element) {
								return null;
							}
							public void removeListener(ILabelProviderListener listener) {
							}
							public void addListener(ILabelProviderListener listener) {
							}
							
							public String getText(Object element) {
								String i=(String)element;
								return i+"";
							}
						});
						
						ld.setContentProvider(new IStructuredContentProvider(){
							public void dispose() {
							}
							public Object[] getElements(Object inputElement) {
								return list.toArray();
							}
							public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
							}
						});
						
						ld.setInput(list.toArray());
						res = ld.open();
						
						try {
							vers = ld.getResult()[0] + "";
						} catch (NumberFormatException e) {
							vers = "";
						}
						
						if (res == 0 && vers != "") {
							createVER(true,fileName2,true,vers);
							BridageFormats.retourMeth1.getFjru().setVersion(vers);
						} else {
							BridageFormats.retourMeth1.getFjru().setVersion("");
						}
					}
				}
			});
			return BridageFormats.retourMeth1;
		} else {
			System.out.println("problème extension");
			BridageFormats.retourMeth1.getFjru().setVersion("");
			
			return BridageFormats.retourMeth1;
		}
	}

	public static ArrayList<String> getVersionFromFile() {
		FileReader f = null;
		String version = "";
		ArrayList<String> list = new ArrayList<String>();
		
		try {
			f = new FileReader(RepertoiresAdresses.JRU_liste_versions_JRU);
			BufferedReader bfrd = new BufferedReader(f);
			
			while (version != null){
				try {
					version=bfrd.readLine();
					
					if (!(version == null)) {
						list.add(version);
					}
				} catch (Exception ex) {
					return null;
				}
			}
		} catch (FileNotFoundException e) {
			return null;
		}
		
		list.trimToSize();
		
		return list;
	}

	public static FormatSAM readVER(String path) {
		FileReader f = null;
		String contenuVER;
		try {
			f = new FileReader(path);
			BufferedReader bfrd = new BufferedReader(f);
			
			try {
				contenuVER = bfrd.readLine();
				
				if (contenuVER.startsWith("UC")||contenuVER.startsWith("ATESS")) {
					FormatSAM.TOM4.setEnable(false);
					BridageFormats.setGestionConflitExtension(false);
					
					return FormatSAM.ATESS;
				} else {
					if (contenuVER.startsWith("TOM4")) {
						FormatSAM.ATESS.setEnable(false);
						BridageFormats.setGestionConflitExtension(false);
						return FormatSAM.TOM4;
						
					} else {
						System.out.println("VER invalide");
						
						return null;
					}
				}
			} catch (Exception ex) {
				return null;
			}	
		} catch (Exception ex) {
			return null;
		}
	}

	public static void createVER(boolean ATESS,String filename, boolean JRU, String version){
		String chemin = "";
		
		if(ActivatorData.getInstance().isCompressedFile()){
			DecompressedFile df = ActivatorData.getInstance().getDecompressedFile();
			File f = new File(df.getCompressedFileName());
			filename = new File(f.getParent(), df.getInnerFileName()).getAbsolutePath();
			filename = filename.substring(0, filename.lastIndexOf('.') + 1);
		}
		
		if (JRU) {
			chemin = filename + "jru.ver." + version;
		} else {
			chemin = filename + "VER";
		}

		FileWriter f = null;
		String texte = ATESS ? "ATESS" : "TOM4";
		
		try {
			f = new FileWriter(chemin);			
			
			if (!JRU) {
				BufferedWriter bf = new BufferedWriter(f);
				bf.write(texte);
				bf.flush();
				bf.close();
			}
			
			f.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isMultimedia(String fileName) {
		return BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.MULTIMEDIA, fileName);
	}
	
	public static boolean isCompressed(String fileName) {
		return BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.COMPRESSED, fileName);
	}

	public static FormatSAM getFormat(String fileName) {
		if (BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.TOMNG, fileName) && FormatSAM.TOMNG.isEnable()) {
			return FormatSAM.TOMNG;
		} else if (BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.JRU,fileName) && FormatSAM.JRU.isEnable()) {
			return gestionFichierjru(fileName);
		} else if (BridageFormats.isGestionConflitExtension() && BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.TOM4, fileName) && BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.ATESS, fileName)) {
			FormatSAM format=gestionFichierVER(fileName);
			return format;
		} else if (BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.TOM4,fileName) && FormatSAM.TOM4.isEnable()) {
			return FormatSAM.TOM4;
		} else if (BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.ATESS,fileName) && FormatSAM.ATESS.isEnable()) {
			return FormatSAM.ATESS;
		} else if (BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.MULTIMEDIA, fileName)) {
			return FormatSAM.MULTIMEDIA;
		} else if (BridageFormats.getInstance().isextensionValideFromFormat(FormatSAM.COMPRESSED, fileName)) {
			return FormatSAM.COMPRESSED;
		}

		return null;
	}

}