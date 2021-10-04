package com.faiveley.samng.principal.sm.data.descripteur;

import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:10:48
 */
public class DescripteurVariable extends ADescripteurComposant {

	protected Poids poidsPremierBit;
	
	protected Poids poidsPremierOctet;
	
	protected Type type;
	
	protected TypeVariable typeVariable;

	protected TableLangueNomUtilisateur nomUtilisateur;
	
	public static final int NO_TAILLE_OCTETS = -1; 
	
	private AVariableComposant variableEchelle;
	
	private boolean volatil=false;
		
	public DescripteurVariable(){

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


	/**
	 * @return the poidsPremierBit
	 */
	public Poids getPoidsPremierBit() {
		return this.poidsPremierBit;
	}

	/**
	 * @param poidsPremierBit the poidsPremierBit to set
	 */
	public void setPoidsPremierBit(Poids poidsPremierBit) {
		this.poidsPremierBit = poidsPremierBit;
	}

	/**
	 * @return the poidsPremierOctet
	 */
	public Poids getPoidsPremierOctet() {
		return this.poidsPremierOctet;
	}

	/**
	 * @param poidsPremierOctet the poidsPremierOctet to set
	 */
	public void setPoidsPremierOctet(Poids poidsPremierOctet) {
		this.poidsPremierOctet = poidsPremierOctet;
	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return this.type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(Type type) {
		this.type = type;
	}

	/**
	 * @return Returns the type.
	 */
	public TypeVariable getTypeVariable() {
		return this.typeVariable;
	}

	/**
	 * @param type The type to set.
	 */
	public void setTypeVariable(TypeVariable type) {
		this.typeVariable = type;
	}


	public AVariableComposant getVariableEchelle() {
		return variableEchelle;
	}


	public void setVariableEchelle(AVariableComposant variableEchelle) {
		this.variableEchelle = variableEchelle;
	}

	public void setVolatil(boolean volatil) {
		this.volatil = volatil;
	}


	public boolean isVolatil() {
		return volatil;
	}
	
}