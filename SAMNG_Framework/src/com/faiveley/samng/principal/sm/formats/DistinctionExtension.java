package com.faiveley.samng.principal.sm.formats;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.xmlbeans.XmlException;

import com.faiveley.samng.principal.sm.parseurs.BridageFormats;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.util.StringUtils;

import noNamespace.EnregistreurDefDocument;

public class DistinctionExtension {

	static boolean ATESS=false;
	static boolean TOM4=false;
	static boolean ATESSinit=false;
	static boolean TOM4init=false;

	public static void setEnableFormats(){
//		BridageFormats.getInstance().getFormatsBrides();	
		BridageFormats.getInstance().CodeToFormats(0);
		
		if (FormatSAM.TOM4.isEnable()&&FormatSAM.ATESS.isEnable()) {
			scanRessources();

			if (!ATESS || !TOM4) {
				if (ATESS) {
					FormatSAM.TOM4.setEnable(false);
				}else{
					if (TOM4) {
						FormatSAM.ATESS.setEnable(false);
					}else{
						FormatSAM.TOM4.setEnable(false);
						FormatSAM.ATESS.setEnable(false);
					}
				}	
			}else{
				BridageFormats.setGestionConflitExtension(true);
				BridageFormats.setGestionConflitExtensionInitial(true);
			}
			setATESSinit(isATESS());
			setTOM4init(isTOM4());
		}
	}

	public static void scanRessources(){
		String form;
		File repertoire=new File(RepertoiresAdresses.xml);
		if ( repertoire.isDirectory ( ) ) {
			File[] list = repertoire.listFiles();
			if (list != null){
				for ( int i = 0; i < list.length; i++) {
					// Appel r�cursif sur les sous-r�pertoires
					form=getTypeFormat(list[i]);
					if (form.equals("ATESS")) {
						ATESS=true;
					}else{
						if (form.equals("TOM_DIS")||form.equals("TOM_HSBC")||form.equals("TOM_UK")) {
							TOM4=true;
						}
					}
					if (ATESS && TOM4) {
						break;
					}
				} 
			} else {
				System.err.println(repertoire + " : Erreur de lecture.");
			}
		} 
	} 

	public static String getTypeFormat(File f){
		if (f.getAbsolutePath().endsWith(".xml")){
			EnregistreurDefDocument enregistreurDefDocument=null;
			try {
				enregistreurDefDocument=EnregistreurDefDocument.Factory.parse(new File(f.getAbsolutePath()));
				String type=enregistreurDefDocument.getEnregistreurDef().getEnregistreur().getType();
				return type;
			} 
			catch(ClassCastException ex){
//				xmlbeans.jru.xmlAssocie.EnregistreurDefDocument enregistreurDefDocument2;
//				try {
//					enregistreurDefDocument2 =xmlbeans.jru.xmlAssocie.EnregistreurDefDocument.Factory.parse(new File(f.getAbsolutePath()));
//					String type=enregistreurDefDocument2.getEnregistreurDef().getEnregistreur().getType();
//					return type;
//				} catch (XmlException e) {
//
//
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//
//				}


			}
			catch (IOException e) {

			} catch (XmlException e) {

			}
		}
		return "";
	}

	public static List<String> supprimerExtensionsDoublons(List <String> listA,List <String> listB){
		ArrayList <String> listSansDoublons=new ArrayList<String>();
		listSansDoublons.addAll(listB);
		int lengthA=listA.size();
		int lengthB=listB.size();
		boolean findDoublon=false;
		for (int i = 0; i < lengthA; i++) {
			findDoublon=false;
			for (int j = 0; j < lengthB; j++) {
				if (listA.get(i).equals(listB.get(j))){
					findDoublon=true;
				}
			}
			if (!findDoublon) {
				listSansDoublons.add(listA.get(i));
			}
		}
		listSansDoublons.trimToSize();
		return listSansDoublons;
	}

	public static String toExtensionFormalisees(List <String> list) {
		return String.format("*%s", StringUtils.join(list, ";*"));
		
//		String listFormalisee="";
//		if (list.size()!=0) {
//			for (int i = 0; i < list.size(); i++) {
//				listFormalisee=listFormalisee+"*"+list.get(i)+";";
//			}
//		}
//		return listFormalisee;
	}

	public static boolean isExtensionOF(List <String> listExtensions,String extensionFichier){
		for (int i = 0; i < listExtensions.size(); i++) {
			if (extensionFichier.toUpperCase().equals(listExtensions.get(i).toUpperCase())) {
				return true;
			}
		}		
		return false;
	}

	public static boolean isATESS() {
		return ATESS;
	}

	public static void setATESS(boolean atess) {
		ATESS = atess;
	}

	public static boolean isATESSinit() {
		return ATESSinit;
	}

	public static void setATESSinit(boolean sinit) {
		ATESSinit = sinit;
	}

	public static boolean isTOM4() {
		return TOM4;
	}

	public static void setTOM4(boolean tom4) {
		TOM4 = tom4;
	}

	public static boolean isTOM4init() {
		return TOM4init;
	}

	public static void setTOM4init(boolean tom4init) {
		TOM4init = tom4init;
	}
}
