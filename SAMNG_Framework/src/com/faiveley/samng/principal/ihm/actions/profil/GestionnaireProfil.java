package com.faiveley.samng.principal.ihm.actions.profil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires.GestionnaireProfilExplorer;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.util.file.GestionFichiers;


public class GestionnaireProfil {

	public static final String nomProfilVide=RepertoiresAdresses.nomprofil_vide;

	// the single instance of this singleton
	private static GestionnaireProfil instance = new GestionnaireProfil();

	protected GestionnaireProfil() {

	}

	public void gererOuvertureFichierXML(String fileName) {
		File xml=new File(fileName);
		String nomXML=xml.getName().replace(".xml", "").replace(".XML", "");
		RepertoiresAdresses.setNom_profil_actuel(nomXML);
		if (!profilPresent(nomXML)) {
			creerNouveauProfil(nomXML);
		}
	}

	private void creerNouveauProfil(String nomXML) {
		File nouveauProfil=new File(RepertoiresAdresses.profil+nomXML);
		GestionFichiers.copierRepertoire(new File(RepertoiresAdresses.profil_vide),nouveauProfil);
	}

	public List<String> getListProfil(){
		ArrayList<String> listeProfils=new ArrayList<String>();
		String cheminProfil=RepertoiresAdresses.profil;
		File profil=new File(cheminProfil);
		File[] sousRep=profil.listFiles();
		for (File file : sousRep) {
			String nomprofil=file.getName();
			if (!nomprofil.equals(nomProfilVide)) {
				listeProfils.add(nomprofil);
			}
		}
		listeProfils.trimToSize();
		return listeProfils;
	}

	public boolean profilPresent(String nomProfil){
		List<String> listeProfils=getListProfil();
		for (String string : listeProfils) {
			if (string.equals(nomProfil)) {
				return true;
			}
		}
		return false;
	}

	public void supprimerProfil(String nomProfil){
		try {
			File profil=new File(RepertoiresAdresses.profil+"//"+nomProfil);
			if (profil.exists()) {
				supprimerRepertoireEntier(profil);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void supprimerRepertoireEntier(File f){
		File[] sousDossiers=f.listFiles();
		if (sousDossiers.length!=0) {
			for (int i = 0; i < sousDossiers.length; i++) {
				File f1=sousDossiers[i];
				if (f1.isFile()) {
					f1.delete();
				}else{
					supprimerRepertoireEntier(f1);
				}
			}
			f.delete();
		}else{
			f.delete();
		}
	}

	public void resetProfil(){
		RepertoiresAdresses.setNom_profil_actuel("");
	}

	/**
	 * Returns the single instance of this class. Sngleton class.
	 * 
	 * @return the instance
	 */
	public static GestionnaireProfil getInstance() {
		if (ActivationExplorer.getInstance().isActif()) {
			return GestionnaireProfilExplorer.getInstance();
		}
		return instance;
	}

	/** Suppression de l'instance */
	public void clear() {
		try {
			this.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//CHECK01
	}
}
