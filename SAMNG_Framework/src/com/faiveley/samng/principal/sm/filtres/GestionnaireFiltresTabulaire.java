package com.faiveley.samng.principal.sm.filtres;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.graphics.RGB;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.listeners.IDuplicationFiltreListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.GestionnaireCouleurs;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Reperes;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.filtres.gestionnaires.AGestionnaireFiltres;
import com.faiveley.samng.principal.sm.filtres.variables.CouleurLigneVariable;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.ParseurFiltreTabulaire;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;


/**
 * @author Cosmin Udroiu
 * @version 1.0
 * @created 23-nov.-2007 15:24:36
 */
public class GestionnaireFiltresTabulaire extends AGestionnaireFiltres implements IDuplicationFiltreListener {
	
	public ParseurFiltreTabulaire filtreTabulaireParseur = new ParseurFiltreTabulaire();

	public GestionnaireFiltresTabulaire() {

		ActivatorData.getInstance().addDuplicationFiltreListener(this);
		this.filtreTabulaireParseur.parseRessource(RepertoiresAdresses.getFiltres_TabulairesXML(),false,0,-1);
		this.listeFiltres = this.filtreTabulaireParseur.chargerFiltres();

		if (FabriqueParcours.getInstance().getParcours()!=null) {
			try {
				this.filtreDefault=(FiltreComposite)initialiserFiltreDefaut();
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Adds a filter to the list and also records it in the xml file
	 * @param filtre
	 */
	public void ajouterFiltre(AFiltreComposant filtre){
		super.ajouterFiltre(filtre);
		this.filtreTabulaireParseur.enregistrerFiltre(filtre);
	}

	public int getNbVarsFiltreDefaut(){
		FileReader f;
		String nombre;
		boolean codeTrouve=true;
		try {
			f = new FileReader(RepertoiresAdresses.filtresdefautsproperties);
			BufferedReader bfrd=new BufferedReader(f);
			try {
				nombre=bfrd.readLine();
				if (!(nombre==null)) {
					try {
						int codeInt=Integer.valueOf(nombre);
						if (codeInt>-1) {
							return codeInt;
						}
					} catch (Exception e) {
						codeTrouve=false;
					}
				}
			} catch (Exception ex) {
				codeTrouve=false;
//				MessageBox msgBox=new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
//				msgBox.setMessage(Messages.getString("PreferencePage.17"));
//				msgBox.open();
			}

		} catch (FileNotFoundException e) {
			codeTrouve=false;
//			MessageBox msgBox0=new MessageBox(Display.getCurrent().getActiveShell(), SWT.ICON_ERROR | SWT.OK);
//			msgBox0.setMessage(Messages.getString("PreferencePage.18"));
//			msgBox0.open();
		}
		return 0;
	}

	@Override
	public AFiltreComposant initialiserFiltreDefaut() {

		int nbVarsMax = getNbVarsFiltreDefaut();			//First analogic variables
		int digitalVarsCnt = 0;		//first digital variables
		boolean varVitessePresente=false;
		int codeVit=0;
		int codeVitCor=0;
		FiltreComposite filter;
		CouleurLigneVariable newColor;
		AFiltreComposant newLineVar;
		Set<AVariableComposant> fpVariables = null;
		fpVariables=GestionnairePool.getInstance().getVariablesRenseignees();
		Map<Integer, AVariableComposant> allVariables = GestionnairePool.getInstance().getAllVariables();
		AVariableComposant[] Vars = new AVariableComposant[allVariables.size()];
		AVariableComposant vitesse=null;
		AVariableComposant vitesseCorrigee=null;

		if (fpVariables!=null) {
			for (AVariableComposant var : fpVariables) {
				DescripteurVariable descrVar =var.getDescriptor();

				if (descrVar.getM_AIdentificateurComposant().getCode()== TypeRepere.vitesse.getCode()) {
					varVitessePresente=true;
					codeVit=var.getDescriptor().getM_AIdentificateurComposant().getCode();
					vitesse=allVariables.get(codeVit);	
				}
			}
			//we subtract one as the first graphic has only 3 variables
			//AVariableComposant[] anaVars = new AVariableComposant[GraphicConstants.MAX_GRAPHICS_COUNT * GraphicConstants.MAX_ANALOG_GRAPHIC_VARIABLES - 1];

			DescripteurVariable descrVar;
			Reperes reperes = GestionnairePool.getInstance().getReperes();
			Type type;
			boolean varUsed=false;

//			for(AVariableComposant var: allVars.values()) {
			Iterator<AVariableComposant> k=fpVariables.iterator();
			if (nbVarsMax>0) {
				while (k.hasNext()) {
					AVariableComposant var=(AVariableComposant)k.next();
					try {
						descrVar = var.getDescriptor();
						type = var.getTypeValeur();
						String varName=descrVar.getM_AIdentificateurComposant().getNom();

						if(descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE && (type == Type.boolean1 ||
								type == Type.boolean8) && 
								reperes.getRepere(descrVar.getM_AIdentificateurComposant().getNom()) == null) {
							if(digitalVarsCnt < Vars.length) {
								for (AVariableComposant vars : fpVariables) {
									if (vars.getDescriptor().getM_AIdentificateurComposant().getNom().equals(varName)) {
										varUsed=false;
										for (int j = 0; j < digitalVarsCnt; j++) {
											if (Vars[j].getDescriptor().getM_AIdentificateurComposant().getNom()
													.equals(varName)) {
												varUsed=true;
											}										
										}
										if (!varUsed)
											Vars[digitalVarsCnt++] = var;
										break;
									}
								}				
							}
						} 

						if(descrVar.getTypeVariable() == TypeVariable.VAR_COMPLEXE && 
								reperes.getRepere(descrVar.getM_AIdentificateurComposant().getNom()) == null) {
							VariableComposite vcomp=(VariableComposite)var;
							int nbChilds=vcomp.getVariableCount();
							for (int i = 0; i < nbChilds; i++) {

								if (vcomp.getEnfant(i).getDescriptor().getType() != Type.reserved) {
									descrVar = vcomp.getEnfant(i).getDescriptor();
									type = vcomp.getEnfant(i).getTypeValeur();
									varName=descrVar.getM_AIdentificateurComposant().getNom();
									AVariableComposant sousvar=vcomp.getEnfant(i);

									if(descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE && (type == Type.boolean1 ||
											type == Type.boolean8)) {

										if(digitalVarsCnt < Vars.length) {
											for (AVariableComposant vars : fpVariables) {										
												varUsed=false;
												for (int j = 0; j < digitalVarsCnt; j++) {
													if (Vars[j].getDescriptor().getM_AIdentificateurComposant().getNom()
															.equals(varName)) {
														varUsed=true;
													}										
												}
												if (!varUsed)
													Vars[digitalVarsCnt++] = sousvar;
												break;											
											}				
										}
									}
								}
							}
						} 
					} catch (Exception e) {
						int t=0;
					}
					//check if the arrays are completelly filled
					if(nbVarsMax == digitalVarsCnt)
						break;
				}
			}
		}

		filtreDefault = new FiltreComposite();		
		filtreDefault.setNom("defaut");
		filtreDefault.setFiltrable(true);
		filtreDefault.setFiltreType(TypeFiltre.tabulaire);
		filtreDefault.setSelectionnable(true);

		AVariableComposant var;

		boolean variablesAnalogicFinished = false;
		RGB[] colors = GestionnaireCouleurs.getVariablesColors();

		filter = new FiltreComposite();
		filter.setFiltrable(true);
		filter.setFiltreType(TypeFiltre.tabulaire);
		filter.setSelectionnable(true);

		if (varVitessePresente) {
			var =vitesse;
			newLineVar = new LigneVariableFiltreComposite();
			newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
			FiltreComposite operateur=new FiltreComposite();operateur.setNom(null);
			FiltreComposite valeur=new FiltreComposite();valeur.setNom(null);
			newLineVar.ajouter(operateur);
			newLineVar.ajouter(valeur);
			filter.ajouter(newLineVar);
		}

		//if the variables analogic finished and we have no childs (we might get here due to a break and we are
		//on the last graphic but analogic graphic created no childs)
		int nbVariable =0;
		if(digitalVarsCnt<=nbVarsMax)
			nbVariable = digitalVarsCnt;
		else
			nbVariable = nbVarsMax;

		for(int j = 0; j <nbVariable; j++) {
			//set the variable filter
			try {
				var =Vars[j];
				newLineVar = new LigneVariableFiltreComposite();
				newLineVar.setNom(var.getDescriptor().getM_AIdentificateurComposant().getNom());
				filter.ajouter(newLineVar);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

//		Collections.sort(filter.getM_ALigneVariableFiltreComposant());
		if(filter.getEnfantCount() > 0)
			filtreDefault.ajouter(filter);

//		int a=0;
//		int b=1;
//		GraphiqueFiltreComposite df=(GraphiqueFiltreComposite)(filtreDefault.getEnfant(a));
//		LigneVariableFiltreComposite lv= (LigneVariableFiltreComposite)df.getM_ALigneVariableFiltreComposant().get(b);
//		CouleurLigneVariable clv=(CouleurLigneVariable)lv.getEnfant(0);
//		clv.getValeurHexa();
		return filtreDefault; 
	}

	/**
	 * Removes a filter by index
	 * @param indice
	 */
	public AFiltreComposant supprimerFiltre(int indice) {
		AFiltreComposant filtre = getFiltre(indice);
		super.supprimerFiltre(indice);
		this.filtreTabulaireParseur.effacerFiltre(filtre);
		return filtre;
	}

	/**
	 * Removes a filter by name
	 */
	public AFiltreComposant supprimerFiltre(String nom){
		AFiltreComposant filtre = super.supprimerFiltre(nom);
		this.filtreTabulaireParseur.effacerFiltre(filtre);
		OrdonnerFiltre.getInstance().getListeFiltreTabulaire().remove(filtre) ;
		return filtre;
	}

	/**
	 * Méthode executé lorsqu'un filtre graphique est dupliqué en filtre tabulaire
	 * @param filtre
	 */
	public void onFiltreDuplique(List<AFiltreComposant> listeFiltres) {
		//on doit modifier le nom du filtre si celui-ci a le meme indice qu'un filtre existant
		//exemple: 
		//si on duplique un filtre graphique nommé sirio_kayseri_samng_New Filter(1) en filtre tabulaire
		//si il existe déjà un filtre tabulaire nommé sirio_kayseri_samng_New Filter(1) de sirio_kayseri_samng_New Filter(1)
		//alors le nouveau filtre  devra s'appeler sirio_kayseri_samng_New Filter(2) de sirio_kayseri_samng_New Filter(1)
		for (AFiltreComposant composant : listeFiltres) {
			String nomComposant = composant.getNom();
			if(composant.getFiltreType()==TypeFiltre.tabulaire){
				for (int i =0; i< this.listeFiltres.getEnfantCount();i++) {
					AFiltreComposant composant2 = this.listeFiltres.getEnfant(i);
					if(composant2.getNom().equals(composant.getNom())){
						int indiceDebutParenthese =  composant.getNom().indexOf("(");
						int indiceFinParenthese =  composant.getNom().indexOf(")");
						String debutNomComposant = composant.getNom().substring(0, indiceFinParenthese+1);
						String ch = debutNomComposant.replace(Integer.parseInt(debutNomComposant.substring(indiceDebutParenthese+1, indiceFinParenthese))+"",  (Integer.parseInt(debutNomComposant.substring(indiceDebutParenthese+1, indiceFinParenthese))+1)+"");
						nomComposant = ch + nomComposant.substring(indiceFinParenthese+1,nomComposant.length());
						composant.setNom(nomComposant);
					}			
				}
				ajouterFiltre(composant);
			}
		}
	}

	public FiltreComposite getFiltreDefault() {
		return filtreDefault;
	}

	public void setFiltreDefault(FiltreComposite filtreDefault) {
		this.filtreDefault = filtreDefault;
	}
}