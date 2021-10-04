package com.faiveley.samng.principal.sm.filtres;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.principal.sm.filtres.variables.ALigneVariableFiltreComposant;

/**
 * @author Administrateur
 * @version 1.0
 * @created 12-nov.-2007 15:58:31
 */
public class GraphiqueFiltreComposite extends AFiltreComposant {

	private boolean actif=true;
	private int numero;
	private TypeGraphique typeGraphique;
	private FiltreComposite parent;

	public List<AFiltreComposant> m_ALigneVariableFiltreComposant = new ArrayList<AFiltreComposant>(0);

	public GraphiqueFiltreComposite() {

	}
	public GraphiqueFiltreComposite(GraphiqueFiltreComposite source) {
		super(source);
		this.actif = source.actif;
		this.numero = source.numero;
		this.typeGraphique=source.typeGraphique;
		AFiltreComposant childClone;
		for(AFiltreComposant filtre: source.m_ALigneVariableFiltreComposant) {
			childClone = filtre.clone();
			this.m_ALigneVariableFiltreComposant.add(childClone);
		}
	}

	/**
	 * 
	 * @param var
	 */
	public void ajouter(ALigneVariableFiltreComposant var) {
		if(var != null)
			this.m_ALigneVariableFiltreComposant.add(var);
	}

	public AFiltreComposant getEnfant(int index) {
		if(index < 0 || index >= this.m_ALigneVariableFiltreComposant.size())
			return null;
		return m_ALigneVariableFiltreComposant.get(index);
	}

	public String getNom() {
		return nom;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setNom(String newVal){
		nom = newVal;
	}

	/**
	 * 
	 * @param var
	 */
	public void supprimer(ALigneVariableFiltreComposant var) {
		if(var != null)
			this.m_ALigneVariableFiltreComposant.remove(var);
	}

	@Override
	public void supprimer(int indice) {
		try {
			if(this.getEnfant(indice) != null)
				this.m_ALigneVariableFiltreComposant.remove(indice);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	/**
	 * Returns the number of possible variables lines for the graphic specified
	 * by its name
	 * 
	 * @param graphicName
	 * @return
	 */
	public int getPossibleVariableLines() {
		TypeGraphique type = this.typeGraphique;
		if (type==null) {
			return 0;
		}
		switch (type) {
		case analogique:
			return GraphicConstants.MAX_ANALOG_VARIABLES;
		case digital:
			return GraphicConstants.MAX_DISCRETE_VARIABLES;
		default:
			return 0;
		}
	}

	//	/**
	//	 * Returns the number of possible variables lines for the graphic specified
	//	 * by its name
	//	 * 
	//	 * @param graphicName
	//	 * @return
	//	 */
	//	public int getPossibleVariableLines(String name) {
	//		TypeGraphique type = getGraphicType(name);
	//		if (type==null)
	//			return 0;
	//		switch (type) {
	//		case analogique:
	//			return GraphicConstants.MAX_ANALOG_VARIABLES;
	//		case digital:
	//			return GraphicConstants.MAX_DISCRETE_VARIABLES;
	//		default:
	//			return 0;
	//		}
	//	}
	//
	//	public TypeGraphique getGraphicType(String graphicName) {
	//		// pour que la recherche fonctionne toujours
	//		if (graphicName.indexOf(com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages.getString("GraphiqueFiltresEditorTable.33")) > 0) //$NON-NLS-1$
	//			return TypeGraphique.analogique;
	//		else if (graphicName.indexOf(com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages.getString("GraphiqueFiltresEditorTable.34")) > 0) //$NON-NLS-1$
	//			return TypeGraphique.digital;
	//		else return null;
	//	}

	public boolean isFiltrable(){
		return filtrable;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setFiltrable(boolean newVal){
		filtrable = newVal;
	}

	public boolean isActif(){
		return actif;
	}

	/**
	 * 
	 * @param newVal
	 */
	public void setActif(boolean newVal){
		actif = newVal;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public int getNumero() {
		return this.numero;
	}

	@Override
	public void ajouter(AFiltreComposant filtreComp) {
		if(filtreComp != null && !this.m_ALigneVariableFiltreComposant.contains(filtreComp))
			this.m_ALigneVariableFiltreComposant.add(filtreComp);

	}

	public int getEnfantCount() {
		return this.m_ALigneVariableFiltreComposant.size();
	}

	@Override
	public AFiltreComposant getEnfant(AFiltreComposant comp) {
		return null;
	}

	@Override
	public void removeAll() {
		this.m_ALigneVariableFiltreComposant.clear();

	}

	@Override
	public void supprimer(AFiltreComposant filtreComp) {
		if(filtreComp != null)
			this.m_ALigneVariableFiltreComposant.remove(filtreComp);
	}

	@Override
	public GraphiqueFiltreComposite clone() {
		return new GraphiqueFiltreComposite(this);
	}
	public TypeGraphique getTypeGraphique() {
		return typeGraphique;
	}
	public void setTypeGraphique(TypeGraphique typeGraphique) {
		this.typeGraphique = typeGraphique;
	}
	public List<AFiltreComposant> getM_ALigneVariableFiltreComposant() {
		return m_ALigneVariableFiltreComposant;
	}
	public void setM_ALigneVariableFiltreComposant(
			List<AFiltreComposant> ligneVariableFiltreComposant) {
		m_ALigneVariableFiltreComposant = ligneVariableFiltreComposant;
	}


	/**
	 * Returns the list of graphical filters according to the table selected
	 * values
	 * 
	 * @return the list of graphical filters
	 */
	public static List<AFiltreComposant> getSelectedValues(GraphiqueFiltreComposite[] permanentGraphics,boolean includeNotUsed) {
		List<AFiltreComposant> selValues;
		try {
			selValues = new ArrayList<AFiltreComposant>();
			for (AFiltreComposant graphique : permanentGraphics) {
				if ((!includeNotUsed)&&graphique!=null&&(((GraphiqueFiltreComposite)graphique)).isActif())
					continue;
				selValues.add(graphique.clone());
			}
		} catch (RuntimeException e) {
			return new ArrayList<AFiltreComposant>(0);
		}

		return selValues;
	}

	//vérifie si il y a au moins une variable renseignee dans le filtre
	public boolean contientUneVariableRenseignee(){
		boolean uneVarRenseignees=false;
		int enfantsCount = this.getEnfantCount();
		for (int j = 0; j < enfantsCount; j++) {
			AFiltreComposant enfantCourant = this.getEnfant(j);
			if (enfantCourant.isVariableRenseigneeDansParcours()) {
				uneVarRenseignees=true;
				break;
			}
		}
		return uneVarRenseignees;
	}

	public FiltreComposite getParent() {
		return parent;
	}
	public void setParent(FiltreComposite parent) {
		this.parent = parent;
	}

}
