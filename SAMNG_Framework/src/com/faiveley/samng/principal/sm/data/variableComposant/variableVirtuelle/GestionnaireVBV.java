package com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.parseurs.ParseurVBV;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

/**
 * @author Olivier
 * @version 1.0
 * @created 07-janv.-2008 11:00:14
 */
public class GestionnaireVBV {
	private static int vbvCodeCounter = 65537; 
	private List<VariableVirtuelle> listeVBV = new ArrayList<VariableVirtuelle>(0);
	private ParseurVBV parser;

	public GestionnaireVBV(){

	}

	/**
	 * 
	 * @param var
	 */
	public boolean ajouterVBV(VariableVirtuelle var) {
		if(var != null && !this.listeVBV.contains(var)) {
			//generate a unique code that will be certainly unique
			//: maybe a unique class should be used for this inside the application
			//as other modules might need this
			var.getDescriptor().getM_AIdentificateurComposant().setCode(vbvCodeCounter++);
			this.listeVBV.add(var);
			return true;
		}
		return false;
	}

	/**
	 * Loads the VBVs from the XML file associated to the binary file name given as 
	 * parameter. If a previous file was loaded (different from the new one) the VBVs
	 * for this file are automatically saved in the corresponding XML file'
	 * 
	 * It also updates the descriptions of the variables that are used inside the loaded VBVs 
	 * as these have incomplete descriptions after a parse operation
	 * 
	 * @param fichier
	 * @return
	 */
	public boolean chargerVBV(String fichier) {
		this.listeVBV.clear();
		File file = new File(fichier);
		String fileName = file.getName();
		int dotPos;
		if((dotPos = fileName.indexOf('.')) != -1)
			fileName = fileName.substring(0, dotPos);
		
		String fullVbvsFileName = RepertoiresAdresses.getVBVs_XML();
		
		this.parser = new ParseurVBV();
		this.parser.parseRessource(fullVbvsFileName,false,0,-1);
		List<VariableVirtuelle> loadedVbvs = this.parser.chargerVBV();
		for(VariableVirtuelle vbv: loadedVbvs)
			ajouterVBV(vbv);	//this will generate also a code for that VBV
		
		return true;
	}

	public boolean enregistrerVBV() throws ParseurXMLException{
		boolean ret = false;
		if(parser != null)
			ret = this.parser.enregistrerVBV(this.listeVBV);
		return ret;
	}

	/**
	 * 
	 * @param var
	 */
	public List<VariableVirtuelle> getListeVBV(){
		return this.listeVBV;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setListeVBV(List<VariableVirtuelle> newVal) {
		this.listeVBV.clear();
		if(newVal != null) {
			for(VariableVirtuelle vbv: newVal) {
				this.listeVBV.add(vbv);
			}
		}
	}

	/**
	 * 
	 * @param nom
	 */
	public VariableVirtuelle supprimerVBV(String nom) {
		VariableVirtuelle vbv = getVBV(nom);
		if(vbv != null) {
			this.listeVBV.remove(vbv);
		}
		return vbv;
	}
	
	public VariableVirtuelle getVBV(String nom) {
		if(nom == null)
			return null;
		VariableVirtuelle retVar = null;
		for(VariableVirtuelle vbv: this.listeVBV) {
			if(nom.equals(vbv.getDescriptor().getM_AIdentificateurComposant().getNom())) {
				retVar = vbv;
				break;
			}
		}
		return retVar;
	}
	
	public String getVbvLabel(VariableVirtuelle vbv) {
		String ret = "";
		if(vbv != null)
			ret = vbv.getDescriptor().getNomUtilisateur().getNomUtilisateur(Langage.DEF);
		return ret;
	}
}