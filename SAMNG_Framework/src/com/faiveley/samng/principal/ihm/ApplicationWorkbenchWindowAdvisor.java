package com.faiveley.samng.principal.ihm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.actions.fichier.FichierOuvrirAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveAccueil;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveGALT;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveGL;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveGT;
import com.faiveley.samng.principal.ihm.perspectives.PerspectiveGTL;
import com.faiveley.samng.principal.sm.controles.physique.ControleMemoire;
import com.faiveley.samng.principal.sm.data.compression.DecompressedFile;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor implements IDataChangedListener {
	private static final String PERSPECTIVE_ID = PerspectiveAccueil.getID();
	private static final String P13 = "P13";
	private static final String P14 = "P14";
	private static final String P23 = "P23";
	private static final String P123 = "P123";
	private static final String P316 = "P316";
	private static final String P34 = "P34";
	private static final String Reception = "Reception";

	public ApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer) {
		super(configurer);
		ActivatorData.getInstance().addDataListener(this);
	}

	public final ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer) {
		return new ApplicationActionBarAdvisor(configurer);
	}
	
	public static void afficherPerspectiveApresOuvertureFichier(){
		String idPersp = GestionLineCommandParameters.getNomPerspect();
		String ID = PerspectiveAccueil.getID();

		if (idPersp != null && (!idPersp.equals(""))) {
			if (idPersp.equals("P13")) {
				ID = PerspectiveGL.getID();
			} else if (idPersp.equals("P23")) {
				ID = PerspectiveGT.getID();
			} else if (idPersp.equals("P123")) {
				ID = PerspectiveGTL.getID();
			} else if (idPersp.equals("P316")) {
				ID = PerspectiveGALT.getID();
			} else if (idPersp.equals("P14")) {
				ID = PerspectiveGT.getID();
			} else if (idPersp.equals("P34")) {
				ID = PerspectiveGTL.getID();
			} else if (idPersp.equals("Reception")) {
				ID = PERSPECTIVE_ID;
			} else {
				String s1 = com.faiveley.samng.principal.sm.linecommands.Messages.GestionLineCommandParameters_45;
				String s2 = com.faiveley.samng.principal.sm.linecommands.Messages.GestionLineCommandParameters_46 + ": ";
				String s3 = P13;
				String s4 = P14;
				String s5 = P23;
				String s6 = P123;
				String s7 = P316;
				String s8 = P34;
				String s9 = Reception;

				GestionLineCommandParameters.echo(s1, s2, s3, s4, s5, s6, s7, s8,  s9);				
				System.exit(0);
				ID = PERSPECTIVE_ID;
			}
		}
			
		try {
			PlatformUI.getWorkbench().showPerspective(ID, PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IPerspectiveDescriptor[] perspectives = page.getOpenPerspectives();
		
		// La perspective Accueil doit contenir au minimum la vue Liste dans tous les cas de figure et notamment au premier lancement de lapplication suite � une installation Issue#878
		for (IPerspectiveDescriptor perspective : perspectives) {
			if (perspective.getId().equals("SAMNG.perspectiveAccueil")) {
				IViewReference[] views = page.getViewReferences();
				boolean printListView = true;
				
				for (IViewReference view : views) {
					if (!view.isFastView()) {
						printListView = false;
					}
					
					if (view.getId().equals("SAMNG.Vue.Multimedia") && ActivatorData.getInstance().getMultimediaFiles() == null) {
						page.hideView(view);
					}
				}
				
				if (printListView) {					
					try {
						page.showView("SAMNG.Vue.Liste.e4");
					} catch (PartInitException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	@Override
	public void postWindowOpen() {
		if (GestionLineCommandParameters.getNomfichier() != null && (!GestionLineCommandParameters.getNomfichier().equals(""))) {
			//il y a un fichier � ouvrir
			FichierOuvrirAction foa = new FichierOuvrirAction(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), GestionLineCommandParameters.getNomfichier(), 0, -1, false, "");
			foa.run();
		}
	}
	
	public final void preWindowOpen() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		Display display = Display.getDefault();
		Rectangle r = display.getPrimaryMonitor().getClientArea();
		final int largeur = r.width;
		final int hauteur = r.height;
		configurer.setInitialSize(new Point(largeur, hauteur));
		configurer.setShowCoolBar(true);

		configurer.setTitle("SAM5");
		configurer.setShowStatusLine(false);
		configurer.setShowPerspectiveBar(true);
		// configurer.setShowFastViewBars(true);
		configurer.setShowProgressIndicator(true);

		FileReader fileR = null;
		
		try {
			String SAM5File = new URL(Platform.getInstallLocation().getURL() + "SAM5.ini").getPath();
			File file = new File(SAM5File);
			fileR = new FileReader(file);
			int c;
			String g = "";;
			
			while ((c = fileR.read()) != -1) {
				g = g + (char) c;
			}
			
			String u = g.replaceAll("\r\n", "");
			fileR.close();

			String codeLangueParDefaut = "EN";
			String defaultMouseSyncViews = "syncDoubleClick";
			Boolean defaultKeySyncViews = false;
			
			try {
				FileInputStream inStream;
				String cheminFichierdefautLangageProperties = RepertoiresAdresses.defaultLanguage_PROPERTIES;
				inStream = new FileInputStream(new File(cheminFichierdefautLangageProperties));
				Properties props = new Properties();
				props.load(inStream);

				codeLangueParDefaut = (String) props.get("default_language");
				
				String pathDefaultMouseSyncViews = RepertoiresAdresses.viewsSynchronizationProperties;
				inStream = new FileInputStream(new File(pathDefaultMouseSyncViews));
				props.load(inStream);
				
				defaultMouseSyncViews = (String) props.get("default_mouse_sync");
				defaultKeySyncViews = Boolean.parseBoolean((String) props.get("default_key_sync"));
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (u.contains("-Xmx64m")) {
				String travail = "-workspace\r\n" + Activator.getPathINSTALL_SAM_PARAM() + "\r\n";
				String workspaceLocation = "-data\r\n" + Activator.getPathINSTALL_SAM_PARAM() + "workspace\r\n";
				String chaineLanguageDefaut = "-Duser.language="+codeLangueParDefaut.toUpperCase() + "\r\n-Duser.country=" + codeLangueParDefaut.toLowerCase() + "\r\n";
				String chaineParametreMemoireDefaut = "-vmargs\r\n-Xms64m\r\n-Xmx";
				String strDefaultMouseSyncViews = "-Duser.mouseSync=" + defaultMouseSyncViews + "\r\n";
				String strDefaultKeySyncViews = "-Duser.keySync=" + defaultKeySyncViews.toString() + "\r\n";
				
				FileWriter writ = new FileWriter(file);

				// Tentative de calcul du max de m�moire disponible dans la jvm
				long xmxValide = 64;
				
				try {
					long memoirePhysiqueTotale = ControleMemoire.getMemoireViveTotale() / 1024 / 1024;
					xmxValide = 2 * memoirePhysiqueTotale / 3;
					xmxValide = ControleMemoire.calculerValeurXmxMaximum(xmxValide);
				} catch(Exception ex) {
					xmxValide = 600;
				}

				String chaineEcriture = travail + workspaceLocation + chaineParametreMemoireDefaut + xmxValide + "m\r\n" + chaineLanguageDefaut
						+ strDefaultMouseSyncViews + strDefaultKeySyncViews;
				writ.write(chaineEcriture);
				writ.close();

				Runtime.getRuntime().exec("SAM5");
				System.exit(0);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fileR != null) {
				try {
					fileR.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public final void postWindowCreate() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();

		if (Boolean.TRUE.equals(configurer.getData("restored"))) {
			return;
		}

		Properties p = new Properties();
		InputStream stream;
		
		try {
			stream = new FileInputStream(RepertoiresAdresses.temp_directory);
			p.load(stream);
			RepertoiresAdresses.RepertoireTravail = p.getProperty("dir");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			Thread t1 = new Thread() {
				public void run() {
					try {
						Bundle bundle = Platform.getBundle("VueTabulairePlugin");
						
						if (bundle.getState() != Bundle.ACTIVE) {
							bundle.start();
						} else {
							bundle.stop();
							bundle.start();
						}
					} catch (BundleException e) {
						e.printStackTrace();
					}
				}
			};

			t1.start();
			
			while (t1.isAlive()) {
			}
			
			ActivatorData.getInstance().getBarAdvisor().creerExportsActions();
		} catch (OutOfMemoryError e) {
		} catch (Throwable t) {
			t.printStackTrace();
		}
		
		Activator.getDefault().hideAllViews();
		ActivatorData.getInstance().getBarAdvisor().creerExportsActions();

		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow()==null){
			try {
				PlatformUI.getWorkbench().openWorkbenchWindow(null);
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		}
		
	    IWorkbenchWindow window = configurer.getWindow();
	    window.getShell().setMaximized(true);
	}

	/**
	 * M�thode permetttant de calculer la valeur du param�tre JVM Xmx en partant d'une valeur
	 */
	public static int calculerValeurXmxMaximum(int valeurDepart) throws IOException{
		int exitVal = -1;
		int xmxDepard = 601;
		
		while (exitVal != 1) {
			xmxDepard = xmxDepard + 25;
			Runtime r = Runtime.getRuntime();
			Process p = r.exec("java -Xmx" + xmxDepard + "m -classpath bin  tin.Bob");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream())); 
			String line;
			
			while ((line = in.readLine()) != null) {
				System.out.println(line);
			}
			
			try {
				exitVal = p.waitFor();
				System.out.println(exitVal);
				p.destroy();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		if (exitVal == -1) {
			xmxDepard = 64 ;
		} else {
			xmxDepard = xmxDepard - 20 ;
		}
		
		return xmxDepard;
	}

	public void onDataChange() {
		IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
		
		try {
			InfosFichierSamNg info = null;
			
			if(FabriqueParcours.getInstance().getParcours() != null) {
				try {
					info = (InfosFichierSamNg)FabriqueParcours.getInstance().getParcours().getInfo();
				} catch(Exception ex) {
					info = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier());
				}
				
				if (info.getNomFichierParcoursBinaire() == null) {
					configurer.setTitle("SAM5 ");
				} else {
					File f = new File(info.getNomFichierParcoursBinaire());

					if (ActivatorData.getInstance().getVueData().isEmpty())	{
						configurer.setTitle("SAM5 ");
					} else {
						ListMessages messages = FabriqueParcours.getInstance().getParcours().getData().getEnregistrement().getMessages();
						long deb = messages.get(0).getAbsoluteTime();
						int nbrMsg = messages.size();
						long fin = messages.get(nbrMsg - 1).getAbsoluteTime();

						GregorianCalendar gc = new GregorianCalendar();
						gc.setTimeInMillis(deb);
						XMLGregorianCalendar xcDeb = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);
						gc.setTimeInMillis(fin);
						XMLGregorianCalendar xcFin = DatatypeFactory.newInstance().newXMLGregorianCalendar(gc);

						String debstr = String.format("%02d/%02d/%4d %02d:%02d", xcDeb.getDay(), xcDeb.getMonth(), xcDeb.getYear(), xcDeb.getHour(), xcDeb.getMinute());
						String finstr = String.format("%02d/%02d/%4d %02d:%02d", xcFin.getDay(), xcFin.getMonth(), xcFin.getYear(), xcFin.getHour(), xcFin.getMinute());

						String titleFileName = f.getName();

						if (ActivatorData.getInstance().isCompressedFile()) {
							DecompressedFile df = ActivatorData.getInstance()
									.getDecompressedFile();
							titleFileName = df.getInnerFileName();
						}

						configurer.setTitle("SAM5 - " + titleFileName + " [" + debstr + " ... " + finstr + "]");
					}
				}
			} else {
				configurer.setTitle("SAM5 ");
			}
		} catch(Exception ex) {
			configurer.setTitle("SAM5 ");
		}
	}
}
