package com.faiveley.samng.vueexplorateur;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;

import com.faiveley.samng.principal.sm.missions.jaxb.TypeDocument;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRefMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRegroupementTemps;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeSegment;
import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFile;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFolder;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMensuel;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMission;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeRepository;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeSegment;

public class CommunicationFichiersVue {
	public static final  String SEPARATEUR = "/";
	
	private String workspace;

	public CommunicationFichiersVue(String workspace) {
		this.workspace = workspace;
	}
	
	public String getWorkspace() {
		return this.workspace;
	}
	
	public void setWorkspace(String workspace){
		this.workspace = workspace;
	}

	public TreeParent getListeFichiersMissions(String dossier, int level)
	{		
		File url = new File(dossier);
		TreeParent rep;
		
		// If the path is the head of the workspace...
		// rep becomes a repository (the head of the tree) otherwise it's just a folder
		if (dossier.equals(workspace))
			rep = new TreeRepository(dossier);
		else
			rep = new TreeFolder(dossier);		

		// Get, into a list, all files and directories, directly, under the path: "dossier" variable  
		File[] listeFiles = url.listFiles();
			
		if  (listeFiles != null)
		{
			// For each object of the list, file or directory found...
			for (File file : listeFiles) 
			{
				if (!file.isHidden())
				{
					// If it's a file, not a directory...
					if (file.isFile())
					{
						// If the file isn't a xml file (missions file) and it isn't hidden
						// and it's a journey file that SAM can analyze (it has xml file).
						if (!file.getName().toLowerCase().trim().endsWith(".xml") && !file.isHidden()
							&& BridageFormats.getInstance().isextensionValide(file.getName().toUpperCase()))
						{
							// Add the file found into the tree...
							TreeFile treeFile = new TreeFile(file.getAbsolutePath());
							rep.addChild(treeFile);
							
							// Did the file has been already explored: If it's the case it should have a 
							// xml file with the name
							File xml = new File(file.getAbsoluteFile()+".xml");
							
							if (xml.exists())
								traitementRegroupementsMensuels(xml, treeFile);
						}
					}
					else // It's a directory
					{
			//			System.out.println(file.getAbsolutePath());
						
						try 
						{
							TreeParent r;
							
							// Just analyze the next 1 level to show to user that there're files under
							if (level >= 1)
							{
								r = new TreeFolder(file.getAbsolutePath());
							}
							else
							{
								r = getListeFichiersMissions(file.getAbsolutePath(), level + 1);
							}
							
							// Add the nod to the tree
							rep.addChild(r);
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
						}
					}
				}
			}
		} 
		else
		{
			MessageBox msgBox = new MessageBox(Display.getCurrent().getActiveShell(), SWT.ERROR | SWT.OK);
			msgBox.setText(Messages.getString("WorkspaceLoad.1")); //$NON-NLS-1$
			
			msgBox.setMessage(Messages.getString("WorkspaceLoad.2")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
			msgBox.open();
		}
							
		// Return the head of the tree
		return rep;
	}	
	

	private void traitementRegroupementsMensuels(File xml, TreeFile treeFile) {
		String packageName = "com.faiveley.samng.principal.sm.missions.jaxb";
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(packageName);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			TypeDocument element = (TypeDocument) jaxbUnmarshaller.unmarshal(xml);

			if (element.getListeRegoupementTemps() != null){
				// Liste des regroupements mensuels
				List<TypeRegroupementTemps> listeRT= element.getListeRegoupementTemps().getRegroupementTemps();
				for (TypeRegroupementTemps type : listeRT) {
					String mois = String.format("%02d%s%02d", type.getMois().getMonth(),SEPARATEUR,type.getMois().getYear());
					TreeMensuel tm = new TreeMensuel(mois);
					tm.setRegroupementTemps(type);
					treeFile.addChild(tm);

					// Liste des missions journalieres
					List<TypeRefMission> listeMissions = type.getListeMission().getMission();
					for (TypeRefMission t : listeMissions) {
						TypeMission miss = (TypeMission)t.getReference();
						String jour = String.format("%02d%s%02d%s%4d", miss.getDateDebut().getDay(), SEPARATEUR, miss.getDateDebut().getMonth(), SEPARATEUR, miss.getDateDebut().getYear()); 
						TreeMission tmiss = new TreeMission(jour);
						tmiss.setMission(miss);
						tm.addChild(tmiss);

						// liste des segments de temps
						try{
							List<TypeSegment> listeSeg = miss.getListeSegment().getSegment();
							for (TypeSegment typeSegment : listeSeg) {
								XMLGregorianCalendar d = typeSegment.getDateDebut();
								String deb =  String.format("%02d%s%02d%s%4d %02d:%02d", d.getDay(), SEPARATEUR, d.getMonth(), SEPARATEUR, d.getYear(), d.getHour(), d.getMinute());
								XMLGregorianCalendar f = typeSegment.getDateFin();
								String fin =  String.format("%02d%s%02d%s%4d %02d:%02d", f.getDay(), SEPARATEUR,f.getMonth(), SEPARATEUR, f.getYear(), f.getHour(), f.getMinute());
								TreeSegment tSeg = new TreeSegment(deb +" - "+fin);
								tSeg.setSegment(typeSegment);
								tmiss.addChild(tSeg);
							}
						}catch(Exception e){
							System.out.println("Fichier JRU");
						}
					}					
				}
			}
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
}
