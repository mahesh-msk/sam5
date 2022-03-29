package com.faiveley.samng.vueexplorateur.ihm.vue.actions;

//import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.ihm.actions.fichier.FichierOuvrirAction;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeRefMission;
import com.faiveley.samng.principal.sm.missions.jaxb.TypeSegment;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFile;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMensuel;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeMission;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeObject;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeSegment;

public class OuvrirAction {

	public static void lancerActionOuvrir(TreeViewer viewer) {		
		TreeSelection selection=((TreeSelection) viewer.getSelection());		
		if (selection.size()>1) {
			ouvrirSelectionMultiple(selection);
		}else if (selection.size()==1) {
			ouvrirSelectionSimple(selection);
		}
	}

	private static void ouvrirSelectionSimple(TreeSelection selection) {
		Object ob=selection.getFirstElement();
		FichierOuvrirAction foa = null;
		String absolutename=""; //$NON-NLS-1$
		boolean lancerExplorer=false;
		int deb=0;
		int fin=-1;
		String initDateS = "";
		
		if (ob instanceof TreeFile) {
			TreeFile file = (TreeFile) selection.getFirstElement();
			absolutename = file.getAbsoluteName();
			
			// Lorsque l'utilisateur demande une ouverture, si le fichier XML n'existe pas,
			// il faut explorer le fichier pour le cr�er
			// Did the file has been already explored: If it's the case it should have a 
			// xml file with the name 					
			//File xml = new File(absolutename+".xml");
			//if (!xml.exists())			
			//	lancerExplorer=true;		
			// => L'ouverture ne doit plus explorer
		}else if (ob instanceof TreeMensuel) {
			TreeMensuel treeMensuel=((TreeMensuel) selection.getFirstElement());
			absolutename=treeMensuel.getParent().getAbsoluteName();
			List<TypeRefMission> listMissions=treeMensuel.getRegroupementTemps().getListeMission().getMission();
			TypeMission tm1=(TypeMission) listMissions.get(0).getReference();
			TypeMission tm2=(TypeMission) listMissions.get(listMissions.size()-1).getReference();
			deb=(int) tm1.getOffsetDebut();
			fin=(int) tm2.getOffsetFin();
			
			XMLGregorianCalendar initDateGC = tm1.getDateDebut();
			initDateS = String.format("%02d/%02d/%4d 00:00:00.000", initDateGC.getDay(), initDateGC.getMonth(), initDateGC.getYear());
		}else if (ob instanceof TreeMission) {
			TreeMission treeMission=((TreeMission) selection.getFirstElement());
			absolutename=treeMission.getParent().getParent().getAbsoluteName();			
			TypeMission tm=treeMission.getMission();
			deb=(int) tm.getOffsetDebut();
			fin=(int) tm.getOffsetFin();
			
			XMLGregorianCalendar initDateGC = tm.getDateDebut();
			initDateS = String.format("%02d/%02d/%4d 00:00:00.000", initDateGC.getDay(), initDateGC.getMonth(), initDateGC.getYear());
		}else if (ob instanceof TreeSegment) {
			TreeSegment treeSegment=((TreeSegment) selection.getFirstElement());
			absolutename=treeSegment.getParent().getParent().getParent().getAbsoluteName();			
			deb=(int) treeSegment.getSegment().getOffsetDebut();
			fin=(int) treeSegment.getSegment().getOffsetFin();
			
			XMLGregorianCalendar initDateGC = treeSegment.getSegment().getDateDebut();
			initDateS = String.format("%02d/%02d/%4d 00:00:00.000", initDateGC.getDay(), initDateGC.getMonth(), initDateGC.getYear());
		}else{
			System.out.println(Messages.getString("OuvrirAction_2")); //$NON-NLS-1$
			return;
		}

		foa=new FichierOuvrirAction(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				absolutename, deb, fin, lancerExplorer, initDateS);
		foa.run();		
	}

	private static void ouvrirSelectionMultiple(TreeSelection selection) {
		if (!checkSelectionCoherente(selection)) {
			return;
		}

		if (!checkSelectionConsecutive(selection)){
			MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.getString("OuvrirAction_2"), 
					Messages.getString("OuvrirAction_3"));
			return;
		}

		ouvrirSelectionMultipleConsecutive(selection);
	}

	private static void ouvrirSelectionMultipleConsecutive(TreeSelection selection) {
		Object ob1=selection.getFirstElement();
		Object ob2=selection.toList().get(selection.size()-1);
		FichierOuvrirAction foa = null;
		String absolutename=""; //$NON-NLS-1$
		boolean lancerExplorer=false;
		int deb=0;
		int fin=-1;
		String initDateS = "";
		
		if (ob1 instanceof TreeMensuel) {
			absolutename=((TreeMensuel) ob1).getParent().getAbsoluteName();			
			List<TypeRefMission> listMissions1=((TreeMensuel) ob1).getRegroupementTemps().getListeMission().getMission();			
			deb = (int)((TypeMission) listMissions1.get(0).getReference()).getOffsetDebut();
			
			XMLGregorianCalendar initDateGC = ((TypeMission) listMissions1.get(0).getReference()).getDateDebut();
			initDateS = String.format("%02d/%02d/%4d 00:00:00.000", initDateGC.getDay(), initDateGC.getMonth(), initDateGC.getYear());
		}else if (ob1 instanceof TreeMission) {
			deb = (int)((TreeMission) ob1).getMission().getOffsetDebut();			
			absolutename=((TreeMission) ob1).getParent().getParent().getAbsoluteName();
			
			XMLGregorianCalendar initDateGC = ((TreeMission) ob1).getMission().getDateDebut();
			initDateS = String.format("%02d/%02d/%4d 00:00:00.000", initDateGC.getDay(), initDateGC.getMonth(), initDateGC.getYear());
		}else if (ob1 instanceof TreeSegment) {
			absolutename=((TreeSegment) ob1).getParent().getParent().getParent().getAbsoluteName();
			deb=(int) ((TreeSegment) ob1).getSegment().getOffsetDebut();
			
			XMLGregorianCalendar initDateGC = ((TreeSegment) ob1).getSegment().getDateDebut();
			initDateS = String.format("%02d/%02d/%4d 00:00:00.000", initDateGC.getDay(), initDateGC.getMonth(), initDateGC.getYear());
		}else if (ob1 instanceof TreeFile) {
			absolutename=((TreeFile) ob1).getAbsoluteName();			
			deb = (int)((TypeMission)((TreeMensuel)((TreeFile) ob1).getChildren()[0]).
					getRegroupementTemps().getListeMission().getMission().get(0).getReference()).
					getOffsetDebut() ;
		}/*else{
			System.out.println(Messages.OuvrirAction_2); //$NON-NLS-1$
			return;
		}*/
		
		if (ob2 instanceof TreeSegment) {
			fin = (int)((TreeSegment) ob2).getSegment().getOffsetFin();
		} else if (ob2 instanceof TreeMission) {
			fin = (int)((TreeMission) ob2).getMission().getOffsetFin() ;
		} else if (ob2 instanceof TreeMensuel) {
			List<TypeRefMission> listMissions2=((TreeMensuel) ob2).getRegroupementTemps().getListeMission().getMission();
			fin = (int)((TypeMission) listMissions2.get(listMissions2.size()-1).getReference()).getOffsetFin() ;
		}		

		foa=new FichierOuvrirAction(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow(), 
				absolutename, deb, fin,lancerExplorer, initDateS);
		foa.run();		
	}

	private static boolean checkSelectionConsecutive(TreeSelection selection) {
		
		List<?> list=selection.toList();
		boolean selectionOK=false;
		for (int i=1; i<selection.size(); i++) {
					
			/*if (list.get(i-1) instanceof TreeSegment) {
				if (list.get(i) instanceof TreeSegment) {
					selectionOK=checkSegmentsConsecutifs((TreeSegment)list.get(i-1),(TreeSegment)list.get(i));	
				}else if (list.get(i) instanceof TreeMission) {
					
				}				
			}*/
				
			
			if (list.get(i-1) instanceof TreeMensuel) {
				selectionOK=checkRegroupementsMensuelsConsecutifs((TreeMensuel)list.get(i-1),(TreeObject)list.get(i));
			}else if (list.get(i-1) instanceof TreeMission) {
				selectionOK=checkMissionsConsecutives((TreeMission)list.get(i-1),(TreeObject)list.get(i));
			//}else if (selection.getFirstElement() instanceof TreeSegment) {
			}else if (list.get(i-1) instanceof TreeSegment) {
				//selectionOK=checkSegmentsConsecutifs((TreeSegment)list.get(i-1),(TreeSegment)list.get(i));
				selectionOK=checkSegmentsConsecutifs((TreeSegment)list.get(i-1),(TreeObject)list.get(i));
			}else if (list.get(i-1) instanceof TreeFile) {
				if ((list.get(i) instanceof TreeMensuel)
					// ici le TreeMensuel appartient obligatoirement au TreeFile
					//&& ((TreeMensuel)list.get(i)).getParent().equals(list.get(i-1)) 
					// Es ce le 1er TreeMensuel du TreeFile ?
					&& (((TreeMensuel)list.get(i)).getRegroupementTemps().getNumero().intValue() == 0)) { 
						selectionOK = true ;
				} else if ((list.get(i) instanceof TreeMission) // ici le TreeMission appartient forc�ment au meme TreeFile					
							// La TreeMission appartient au 1er TreeMensuel ?
							&& (((TreeMensuel)((TreeMission)list.get(i)).getParent()).getRegroupementTemps().getNumero().intValue() == 0)
							// La TreeMission est elle la lere du TreeMensuel ? 
							&& (((TreeMensuel)((TreeMission)list.get(i)).getParent()).getRegroupementTemps().getListeMission().getMission().get(0).getReference().equals(((TreeMission)list.get(i)).getMission()))) {
					selectionOK = true ;					
				} else if ((list.get(i) instanceof TreeSegment) // ici le TreeSegment appartient forc�ment au meme TreeFile					
						// Le TreeSegment appartient au 1er TreeMensuel ?
						&& (((TreeMensuel)((TreeSegment)list.get(i)).getParent().getParent()).getRegroupementTemps().getNumero().intValue() == 0)
						// Le TreeSegment appartient � la 1ere TreeMission du 1er TreeMensuel ?
						&& (((TreeMensuel)((TreeSegment)list.get(i)).getParent().getParent()).getRegroupementTemps().getListeMission().getMission().get(0).getReference().equals(((TreeMission)((TreeSegment)list.get(i)).getParent()).getMission()))
						// Le TreeSegment est le 1er de la 1ere TreeMission du 1er TreeMensuel ?
						&& (((TreeMission)((TreeSegment)list.get(i)).getParent()).getMission().getListeSegment().getSegment().get(0).equals(((TreeSegment)list.get(i)).getSegment()))) {
					selectionOK = true ;					
				}
			}
			if (!selectionOK) {
				return false;
			}
		}
		return true;
	}

	private static boolean checkSegmentsConsecutifs(TreeSegment seg1, TreeObject obj2) {
		
		TreeMission tm1 = (TreeMission) seg1.getParent();
		List<TypeSegment> listsegs1=tm1.getMission().getListeSegment().getSegment();
			
		if (obj2 instanceof TreeSegment) {			
			TreeMission tm2 = (TreeMission) obj2.getParent();
		
			int indice1 = seg1.getSegment().getNumero().intValue();
			int indice2 = ((TreeSegment)obj2).getSegment().getNumero().intValue();
			
			// Si les segments sont dans la m�me mission...
			if (tm1.equals(tm2)) {
				// et qu'ils se  suivent...
				if (indice2 == indice1 + 1) {
					return true;
				}
			}else{ //si les segments ne sont pas dans la m�me mission
											
				List<TypeSegment> listsegs2=tm2.getMission().getListeSegment().getSegment();

				//si les 2 missions sont cons�cutives
				if (checkMissionsConsecutives(tm1,tm2)
					//si le segment 1 est le dernier de la mission
					&& (listsegs1.get(listsegs1.size()-1).equals(seg1.getSegment()))
					//si le segment 2 est le premier de la mission 
					&& (listsegs2.get(0).equals(((TreeSegment)obj2).getSegment()))) {
						return true;				
				}
			}
		} else if(obj2 instanceof TreeMission) {
			//si les 2 missions sont cons�cutives
			if (checkMissionsConsecutives(tm1,obj2)
				//si le segment 1 est le dernier de la mission
				&& (listsegs1.get(listsegs1.size()-1).equals(seg1.getSegment()))) {
					return true;
			}			
		} else if(obj2 instanceof TreeMensuel) {
			TreeMensuel tme1=(TreeMensuel) tm1.getParent();
			List<TypeRefMission> listmiss1=tme1.getRegroupementTemps().getListeMission().getMission();
			
			//si les 2 regroupements sont cons�cutifs
			if (checkRegroupementsMensuelsConsecutifs(tme1,obj2)
				//si la mission 1 est la derniere du regroupement
				&& (listmiss1.get(listmiss1.size()-1).getReference().equals(tm1.getMission()))
				//si le segment 1 est le dernier de la mission 
				&& (listsegs1.get(listsegs1.size()-1).equals(seg1.getSegment()))) {
					return true;
			}			
		}
		
		return false;
	}

	private static boolean checkMissionsConsecutives(TreeMission tm1,TreeObject obj2) {
		
		TreeMensuel tme1=(TreeMensuel) tm1.getParent();					

		if (obj2 instanceof TreeMission) {
			TreeMensuel tme2=(TreeMensuel) obj2.getParent();
			
			int indice1=tm1.getMission().getNumero().intValue();
			int indice2=((TreeMission)obj2).getMission().getNumero().intValue();
			
			if (tme1.equals(tme2)) {//si les missions sont dans le m�me regroupement mensuel 			
				if (indice2==indice1+1) {
					return true;
				}
			}else{ 												
				//si les missions ne sont pas dans le m�me regroupement mensuel

				List<TypeRefMission> listmiss1=tme1.getRegroupementTemps().getListeMission().getMission();
				List<TypeRefMission> listmiss2=tme2.getRegroupementTemps().getListeMission().getMission();

				//si les 2 regroupements sont cons�cutifs
				if (checkRegroupementsMensuelsConsecutifs(tme1,tme2)
					//si la mission 1 est la derniere du regroupement
					&& (listmiss1.get(listmiss1.size()-1).getReference().equals(tm1.getMission()))
					//si la mission 2 est le premier du regroupement 
					&& (listmiss2.get(0).getReference().equals(((TreeMission)obj2).getMission()))) {
						return true;				
				}
			}
		} else if(obj2 instanceof TreeSegment) {
			List<TypeSegment> listsegs=tm1.getMission().getListeSegment().getSegment();
			
			//si le segment est le premier de la m�me mission
			if (obj2.getParent().equals(tm1)
				//si le segment est le premier de la mission 
				&& (listsegs.get(0).equals(((TreeSegment)obj2).getSegment()))) {
					return true;
			}			
		} else if(obj2 instanceof TreeMensuel) {
			List<TypeRefMission> listmiss1=tme1.getRegroupementTemps().getListeMission().getMission();
			
			//si les 2 regroupements sont cons�cutifs
			if (checkRegroupementsMensuelsConsecutifs(tme1,obj2)
				//si la mission 1 est la derniere du regroupement
				&& (listmiss1.get(listmiss1.size()-1).getReference().equals(tm1.getMission()))) {
					return true;
			}			
		}
		
		return false ;
	}

	private static boolean checkRegroupementsMensuelsConsecutifs(TreeMensuel tm1, TreeObject obj2) {
		
		if (obj2 instanceof TreeMensuel) {			
			int indice1=tm1.getRegroupementTemps().getNumero().intValue();
			int indice2=((TreeMensuel)obj2).getRegroupementTemps().getNumero().intValue();

			//si les regroupements sont cons�cutifs
			if (indice2==indice1+1) {
				return true;
			}
		} else if(obj2 instanceof TreeMission) {
			List<TypeRefMission> listmiss=tm1.getRegroupementTemps().getListeMission().getMission();
			
			//si la mission est la premiere du meme regroupement
			if (obj2.getParent().equals(tm1)
				//si la mission 2 est le premier du regroupement 
				&& (listmiss.get(0).getReference().equals(((TreeMission)obj2).getMission()))) {
					return true;
			}			
		}
		
		return false;
	}

	private static boolean checkSelectionCoherente(TreeSelection selection) {
		int nbElement=selection.size();
		//Class<? extends Object> firstElement=selection.getFirstElement().getClass();
		// int treefileSelected = 0 ;
		//List<TreeFile> filesSelected = new ArrayList<TreeFile>() ;
		Set<TreeFile> filesSelected = new HashSet<TreeFile>() ;
		
		for (int i = 0; i < nbElement; i++) {
			/*if (selection.toList().get(i).getClass().equals(TreeFile.class)) {
				filesSelected.add((TreeFile)selection.toList().get(i)) ;
				treefileSelected++ ;
				// Si plus d'1 fichier de parcours est s�lectionn�...
				if (treefileSelected > 1) {
					MessageDialog.openError(Display.getDefault().getActiveShell(), 
							Messages.OuvrirAction_2, Messages.OuvrirAction_7);
					
					return false;
				}				
			}else {	*/						
				TreeObject treeObject = ((TreeObject)selection.toList().get(i)) ;
				
				while(!treeObject.getClass().equals(TreeFile.class)) {
					treeObject = treeObject.getParent() ;
				}
				
				filesSelected.add((TreeFile)treeObject) ;
			//}
			
		/*	if (!selection.toList().get(i).getClass().equals(firstElement)) {
				MessageDialog.openError(Display.getDefault().getActiveShell(), Messages.OuvrirAction_2,  //$NON-NLS-1$
						Messages.OuvrirAction_9);
				return false;
			}*/
		}
		
		if (filesSelected.size() > 1) {
			MessageDialog.openError(Display.getDefault().getActiveShell(), 
					Messages.getString("OuvrirAction_2"), Messages.getString("OuvrirAction_7"));
			
			return false ;
		}			
				
		
		return true;
	}
}
