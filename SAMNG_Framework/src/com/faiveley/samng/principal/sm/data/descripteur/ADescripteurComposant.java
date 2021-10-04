package com.faiveley.samng.principal.sm.data.descripteur;
import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.ATableAssociationComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:03
 */
public abstract class ADescripteurComposant {

	protected int tailleBits = -1;
	
	protected AIdentificateurComposant m_AIdentificateurComposant;

	protected List<ATableAssociationComposant> tableComposant;
	
	private static final int NB_BITS_OCTET = 8;
	
	private boolean renseigne = false; 
	
	private boolean hasEntreeLogique = false;
	private boolean valeurEntreeLogique = false;
	
	private OffsetComposant offsetComposant= null;


	public ADescripteurComposant(){
		
	}

	/**
	 * 
	 * @param composant
	 */
	public void ajouter(ADescripteurComposant composant){
		
	}

	/**
	 * 
	 * @param indice
	 */
	public ADescripteurComposant getEnfant(int indice){
		return null;
	}

	/**
	 * taille en octet du descripteur
	 * ATTENTION, la taille retournée peut valloir -1
	 * si la taille du composant n'est pas un multiple de 8
	 */
	public int getTailleOctets(){
		return this.tailleBits % NB_BITS_OCTET == 0 ? this.tailleBits / NB_BITS_OCTET : DescripteurVariable.NO_TAILLE_OCTETS;
	}

	/**
	 * taille en octets du descripteur
	 * Renseigne la taille en bits équivalente
	 * 
	 * @param taille
	 */
	public void setTailleOctets(int taille){
		// On affecte la variable tailleBits (1 octet = 8 bits)
		this.tailleBits = taille * NB_BITS_OCTET;
	}
	
	public int getTailleBits() {
		return this.tailleBits;
	}

	/**
	 * taille en bits du descripteur
	 * Renseigne la taille en octet équivalente
	 * ATTENTION, si la taille en bits équivalente n'est pas un multiple de 8
	 * la taille en octet n'est pas renseignée et vaut -1
	 * 
	 * @param taille
	 */	
	public void setTailleBits(int taille) {		
		this.tailleBits = taille;
	}

	/**
	 * 
	 * @param composant
	 */
	public void supprimer(ADescripteurComposant composant){

	}

	
	/**
	 * @return
	 */
	public AIdentificateurComposant getM_AIdentificateurComposant() {
		return this.m_AIdentificateurComposant;
	}
	

	/**
	 * @param identificateurComposant
	 */
	public void setM_AIdentificateurComposant(
			AIdentificateurComposant identificateurComposant) {
		this.m_AIdentificateurComposant = identificateurComposant;
	}

	public boolean add(ATableAssociationComposant arg0) {
		if (this.tableComposant == null) {
			this.tableComposant = new ArrayList<ATableAssociationComposant>(1);
		}
		return this.tableComposant.add(arg0);
	}

	public ATableAssociationComposant getTableComposant(int arg0) {
		ATableAssociationComposant tc = null;
		if (this.tableComposant != null) {
			tc = this.tableComposant.get(arg0);
		}
		return tc;
	}
	
	public int getTableComposantCount() {
		return this.tableComposant != null ? this.tableComposant.size() : 0;
	}

	public boolean remove(Object arg0) {
		boolean ret = false;
		if (this.tableComposant != null) {
			ret = this.tableComposant.remove(arg0);
		}
		return ret;
		
	}

	public boolean isRenseigne() {
		return this.renseigne;
	}
	
	public void setRenseigne(boolean isFilled) {
		this.renseigne = isFilled;
	}

	public boolean isHasEntreeLogique() {
		return hasEntreeLogique;
	}

	public void setHasEntreeLogique(boolean hasEntreeLogique) {
		this.hasEntreeLogique = hasEntreeLogique;
	}

	public boolean isValeurEntreeLogique() {
		return valeurEntreeLogique;
	}

	public void setValeurEntreeLogique(boolean valeurEntreeLogique) {
		this.valeurEntreeLogique = valeurEntreeLogique;
	}

	public List<ATableAssociationComposant> getTableComposant() {
		return tableComposant;
	}

	public void setTableComposant(List<ATableAssociationComposant> tableComposant) {
		this.tableComposant = tableComposant;
	}
	public OffsetComposant getOffsetComposant() {
		return offsetComposant;
	}

	public void setOffsetComposant(OffsetComposant offsetComposant) {
		this.offsetComposant = offsetComposant;
	}
}
