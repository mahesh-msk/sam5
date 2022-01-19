package com.faiveley.samng.principal.sm.search;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.listeners.ISearchInFileExecutorListener;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.formats.DistinctionExtension;
import com.faiveley.samng.principal.sm.formats.FormatSAM;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseurXML.ParseurXML1;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class SearchInFoldersExecutor {

	private List<Result> results = null;
	protected File root = null; 
	protected boolean checkSubFolders = false;
	protected SearchData data = null;
	private FutureTask<List<Result>> task = null;

	private boolean isCanceled = false;
	private ISearchInFileExecutorListener listener = null;
	private String xmlFileName = null;

	public SearchInFoldersExecutor(ISearchInFileExecutorListener listener, String xmlFileName) {
		this.listener = listener;
		this.xmlFileName = xmlFileName;
	}

	public void run() {
		this.isCanceled = false;
		if (this.listener.onStartExecution()) {
			this.task = new FutureTask<List<Result>>(new MyCallable());
			ExecutorService es = Executors.newCachedThreadPool();
			// starts the task
			es.submit(this.task);
			es.shutdown();
		}
	}

	public void cancel() {
		System.err.println("cancel task : " + this.task);
		if(this.task != null) {
			this.isCanceled = true;
			this.task.cancel(true);
		}
	}

	public List<Result> getResults() {
		try {
			this.results = this.task.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return this.results;
	}

	public void setRootFileName(File fileName) {
		this.root = fileName;
	}

	public void setCheckSubfolders(boolean check) {
		this.checkSubFolders = check;
	}

	public void setDataToSearch(SearchData data) {
		this.data = data;
	}

	public class MyCallable implements Callable<List<Result>> {
		ArrayList<Result> res = null;

		public List<Result> call() throws java.io.IOException {
			this.res = new ArrayList<Result>(0);
			try {
				//do the search
				if (SearchInFoldersExecutor.this.root != null) {
					if (SearchInFoldersExecutor.this.root.isDirectory()) {
						searchDirectory(SearchInFoldersExecutor.this.root,SearchInFoldersExecutor.this.checkSubFolders,xmlFileName);
					} else {
						if (searchInFile(SearchInFoldersExecutor.this.root,xmlFileName)) {
							setResult(SearchInFoldersExecutor.this.root);
//							listener.onRefresh();
						}
					}
				}
				listener.onFinishExecution(true);
			} catch (OutOfMemoryError e) {
				System.err.println(
				"Not enough memory to run. Set a bigger memory heap by setting the -Xms and -Xmx arguments.");
				return null;
			} catch (Throwable t) {
				if (!(t instanceof com.faiveley.samng.principal.sm.erreurs.InconsistentFileException)) {
					t.printStackTrace();
				}
				listener.onFinishExecution(false);
				return null;
			}
			return this.res;
		}


		private boolean searchInFile(File f,String xmlFile) {
			
			DistinctionExtension.setEnableFormats();
			BridageFormats.getInstance().initialiseValides();
			BridageFormats.setGestionConflitExtension(true);
			BridageFormats.getInstance().setFormatFichierOuvert(null);
			
			boolean found = true;
			try {
				if (SearchInFoldersExecutor.this.isCanceled) {
					return false;
				}
				Activator.getDefault().release();
				Thread.sleep(20);
				ParseurParcoursBinaire parser = null;
				String nomFichier = f.getAbsolutePath().toUpperCase(); 

				FormatSAM format=BridageFormats.getFormat(nomFichier);

				//on récupère d'abord le nom du fichier xml du fichier de parcours
				String nomFichierXml = null;
				if (format==FormatSAM.TOMNG) {
					nomFichierXml = ParseurParcoursSamng.getInstance().getNomFichierXml(f.getAbsolutePath());
				}else if (format==FormatSAM.ATESS) {
					nomFichierXml = ParseurParcoursAtess.getInstance().getNomFichierXml(f.getAbsolutePath());
				}
				else if (format==FormatSAM.TOM4) {
					nomFichierXml = ParseurParcoursTomUk.getInstance().getNomFichierXml(f.getAbsolutePath());
				}
				else if(format==FormatSAM.JRU){
					String version = format.getFjru().getVersion();
					nomFichierXml = "JRU"+version+".xml";
				}else{
					System.out.println("Format non reconnu");
				}
				
				if (nomFichierXml!=null) {
					int cesure=nomFichierXml.lastIndexOf(".xml");
					if (cesure!=-1) {
						nomFichierXml=nomFichierXml.substring(0, cesure+4);
					}
				}
				
				if(nomFichierXml!=null){
					//on test si le fichier de parcours correspond au fichier xml choisi via la fenetre de dialogue
					if(nomFichierXml.toUpperCase().equals(xmlFileName.toUpperCase())){
						if (format==FormatSAM.TOMNG) {
							parser = ParseurParcoursSamng.getInstance();
							TypeParseur.getInstance().setParser(parser);
							parser.parseRessource(f.getAbsolutePath(),false,0,-1);
						}else if (format==FormatSAM.TOM4) {
							//on récupère le modèle d'enregistreur pour utiliser le bon parseur
							ParseurXML1 parseurXml = ParseurXML1.getInstance();
							parseurXml.parseRessource(RepertoiresAdresses.xml + File.separator + nomFichierXml,false,0,-1);
							String modele = parseurXml.chargerType();
							if(modele.equals("TOM_UK")){				
								parser = ParseurParcoursTomUk.getInstance();
								TypeParseur.getInstance().setParser(parser);
								parser.parseRessource(f.getAbsolutePath(),false,0,-1);
							}
							else if(modele.equals("TOM_DIS")){
								parser = ParseurParcoursTomDIS.getInstance();
								TypeParseur.getInstance().setParser(parser);
								parser.parseRessource(f.getAbsolutePath(),false,0,-1);
							}
							else if(modele.equals("TOM_HSBC")){
								parser = ParseurParcoursTomHSBC.getInstance();
								TypeParseur.getInstance().setParser(parser);
								parser.parseRessource(f.getAbsolutePath(),false,0,-1);
							}
						}else if (format==FormatSAM.ATESS) {
							parser = ParseurParcoursAtess.getInstance();
							TypeParseur.getInstance().setParser(parser);
							parser.parseRessource(f.getAbsolutePath(),false,0,-1);
						}else if(format==FormatSAM.JRU){
							parser = ParseurParcoursJRU.getInstance();
							TypeParseur.getInstance().setParser(parser);
							parser.parseRessource(f.getAbsolutePath(),false,0,-1);
						}
					}
				}else{
					return false;
				}
				if (!SearchInFoldersExecutor.this.isCanceled) {
					if(SearchInFoldersExecutor.this.data.getDescriptorVariable()!=null){
						//search for the selected variable
						found = new SearchInFileUtil().checkForVariable(SearchInFoldersExecutor.this.data);
					}
				}
			} catch (Exception e) {
				if (!(e instanceof com.faiveley.samng.principal.sm.erreurs.InconsistentFileException)) {
					e.printStackTrace();
				}
				found = false;
			}
			GestionnairePool.getInstance().clear();
			return found;
		}

		private void searchDirectory(File dir, boolean checkSubfolders,String xmlFile) {
			File[] files = null;

			if (SearchInFoldersExecutor.this.isCanceled) {
				return;
			}
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (checkSubfolders) {
				//check recursively in subfolders
				files = dir.listFiles();
				for (File f : files) {
					if (f.isDirectory()) {
						searchDirectory(f, checkSubfolders,xmlFileName);
					}
				}
			}

			//on current folder find the .tbf and .cbf files
			files = dir.listFiles(new FilenameFilter(){
				public boolean accept(File f, String fileName) {
					return  BridageFormats.getInstance().isextensionValide(fileName);
				}
			});

			//search each file
			for (File f : files) {
				if (SearchInFoldersExecutor.this.isCanceled) {
					return;
				}
				if (searchInFile(f,xmlFile)) {
					setResult(f);
				}
			}
		}

		private void setResult(File f) {
			Result r = new Result();
			r.setFileName(f.getName());
			r.setDirectory(f.getParent());
			r.setSize(f.length());
			r.setType(f.getName().substring(f.getName().lastIndexOf(".") + 1).toUpperCase());
			r.setModified(new Date(f.lastModified())); 
			this.res.add(r);
			listener.onRefresh(r);
		}
	} // MyCallable
}