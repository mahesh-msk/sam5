package com.faiveley.samng.principal.sm.data.enregistrement;
import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:58
 */
public class Evenement {

	protected ADescripteurComposant m_ADescripteurComposant;

	protected TableLangueNomUtilisateur nomUtilisateur;
	
	protected boolean changementHeure;
	
	protected boolean ruptureAcquisition;
	private boolean razCompteurTemps;
	private boolean razCompteurDistance;
	private boolean referenceSynchro;
	private boolean aSychroniser;
	
	public boolean isASychroniser() {
		return aSychroniser;
	}
	public void setASychroniser(boolean sychroniser) {
		aSychroniser = sychroniser;
	}
	public boolean isReferenceSynchro() {
		return referenceSynchro;
	}
	public void setReferenceSynchro(boolean referenceSynchro) {
		this.referenceSynchro = referenceSynchro;
	}
	public boolean isRazCompteurDistance() {
		return razCompteurDistance;
	}
	public void setRazCompteurDistance(boolean razCompteurDistance) {
		this.razCompteurDistance = razCompteurDistance;
	}
	public boolean isRazCompteurTemps() {
		return razCompteurTemps;
	}
	public void setRazCompteurTemps(boolean razCompteurTemps) {
		this.razCompteurTemps = razCompteurTemps;
	}
	
	public boolean isChangementHeure() {
		return changementHeure;
	}

	public void setChangementHeure(boolean changementHeure) {
		this.changementHeure = changementHeure;
	}

	public Evenement(){

	}

	/**
	 * @return
	 */
	public ADescripteurComposant getM_ADescripteurComposant() {
		return this.m_ADescripteurComposant;
	}

	/**
	 * @param descripteurComposant
	 */
	public void setM_ADescripteurComposant(
			ADescripteurComposant descripteurComposant) {
		this.m_ADescripteurComposant = descripteurComposant;
	}


	
	/**
	 * @return the nomUtilisateur
	 */
	public TableLangueNomUtilisateur getNomUtilisateur() {
		return this.nomUtilisateur;
	}

	/**
	 * @param nomUtilisateur the nomUtilisateur to set
	 */
	public void setNomUtilisateur(TableLangueNomUtilisateur nomUtilisateur) {
		this.nomUtilisateur = nomUtilisateur;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		if (this.m_ADescripteurComposant != null) {
			buf.append(this.m_ADescripteurComposant.getM_AIdentificateurComposant());
		}
		return super.toString();
	}

	public boolean isRuptureAcquisition() {
		return ruptureAcquisition;
	}

	public void setRuptureAcquisition(boolean ruptureAcquisition) {
		this.ruptureAcquisition = ruptureAcquisition;
	}
	
	public boolean isKVBEvent() {
		int code = getM_ADescripteurComposant().getM_AIdentificateurComposant().getCode();
		return code == 117 || code == 118 || code == 120;
	}
}