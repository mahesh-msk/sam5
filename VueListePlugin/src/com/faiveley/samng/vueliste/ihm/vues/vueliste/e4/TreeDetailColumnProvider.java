package com.faiveley.samng.vueliste.ihm.vues.vueliste.e4;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.Paquets;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.ChaineDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.StructureDynamique;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.TableauDynamique;

/** The column label provider (inspired by the legacy TableTreeDetailLabelProvider)
 * */
public class TreeDetailColumnProvider extends ColumnLabelProvider{
	private int columnIndex;

	public TreeDetailColumnProvider(int index)
	{
		columnIndex = index;
	}
	
	@Override
	public String getText(Object element) {
		return getColumnText(element, columnIndex);
	}
	
	
	/**
	 * Gets the text for the specified column
	 * 
	 * @param arg0 the player
	 * @param arg1 the column
	 * @return String
	 */
	public String getColumnText(Object arg0, int arg1) {
		AVariableComposant var = (AVariableComposant) arg0;

		String text = "";

		Langage langage = Activator.getDefault().getCurrentLanguage();
		try {
			switch (arg1) {
				// Nom de la variable
				case 0:
					text = getVariableName(langage, var);
					break;
				// Valeur brute de la variable
				case 1:
					text = getVariableRawValue(var);
					break;
				// Valeur decod�e de la variable
				case 2:
					text = getDecodedValue(var);
					break;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return text;
	}
	
	
	public String getVariableName(Langage langage, AVariableComposant var) {
		String nomUtilisateur = "";
		
		if (var.getDescriptor().getM_AIdentificateurComposant().getNom() != null) {
			nomUtilisateur = var.getDescriptor().getNomUtilisateur().getNomUtilisateur(langage);
		}
		
		if (var.getParent() != null && var.getParent() instanceof VariableComplexe) {
			String nomUtilisateurParent = "";
			nomUtilisateurParent = var.getParent().getDescriptor().getNomUtilisateur().getNomUtilisateur(langage);
			nomUtilisateurParent = nomUtilisateurParent.replace("(" + var.getParent().getDescriptor().getM_AIdentificateurComposant().getNom() + ") ", "");
			nomUtilisateur = nomUtilisateur.replace(nomUtilisateurParent + ".", "");
		} else {
			String nomUtilisateurVariableEntete = "";
			if (var instanceof StructureDynamique) {
				nomUtilisateurVariableEntete = ((StructureDynamique) var).getVariableEntete().getDescriptor().getNomUtilisateur().getNomUtilisateur(langage);
				nomUtilisateur = nomUtilisateurVariableEntete;
			} else if (var instanceof TableauDynamique) {
				nomUtilisateurVariableEntete = ((TableauDynamique) var).getVariableEntete().getDescriptor().getNomUtilisateur().getNomUtilisateur(langage);
				nomUtilisateur = nomUtilisateurVariableEntete;
			} else if (var instanceof Paquets) {
				nomUtilisateurVariableEntete = ((Paquets) var).getVariableEntete().getDescriptor().getNomUtilisateur().getNomUtilisateur(langage);
				nomUtilisateur = nomUtilisateurVariableEntete;
			} else if (var instanceof ChaineDynamique) {
				ChaineDynamique chaineDynamique = (ChaineDynamique) var;
				
				if (((VariableComposite) chaineDynamique.getListeTablesSousVariable().get(0)).getEnfant(0) != null) {					
					nomUtilisateurVariableEntete = ((VariableComposite) chaineDynamique.getListeTablesSousVariable().get(0)).getEnfant(0).getDescriptor().getNomUtilisateur().getNomUtilisateur(langage);
					nomUtilisateur = nomUtilisateurVariableEntete;
				}
				else { // if a ChaineDynamic has a length equal 0, no character has to be displayed
					nomUtilisateur = "";
				}
					
			}
		}
		
		return nomUtilisateur;
	}
	
	public String getVariableRawValue(AVariableComposant var) {
		String rawValue = "";
		
		if (var instanceof StructureDynamique) {
			rawValue = "0x"	+ AVariableComposant.getHexString((byte[]) ((StructureDynamique) var).getVariableEntete().getValeur());
		} else if (var instanceof TableauDynamique) {
			rawValue = "0x" + AVariableComposant.getHexString((byte[]) ((TableauDynamique) var).getVariableEntete().getValeur());
		} else if (var instanceof Paquets) {
			rawValue = "0x" + AVariableComposant.getHexString((byte[]) ((Paquets) var).getVariableEntete().getValeur());
		} else if (var instanceof ChaineDynamique) {
			try {
				ChaineDynamique chaineDynamique = (ChaineDynamique) var;
				
				// if a ChaineDynamic has a length equal 0, no character has to be displayed
				if (((VariableComposite)chaineDynamique.getM_AVariableComposant().get(0)).getEnfant(0) != null) {
					rawValue = chaineDynamique.getValeurHexa();
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else if (!(var instanceof VariableComplexe)) {
			if (var.getValeur() != null) {
				try {
					rawValue = "0x"	+ AVariableComposant.getHexString((byte[]) var.getValeur());
				} catch (NumberFormatException e) {
					rawValue = "###";
				}
			} else {
				rawValue = "###";
			}
		}
		
		return rawValue;
	}
	
	public String getDecodedValue(AVariableComposant var) {
		String decodedValue = "";
		
		if (var instanceof StructureDynamique) {
			decodedValue = ((StructureDynamique) var).getVariableEntete().toString();
		} else if (var instanceof TableauDynamique) {
			decodedValue = ((TableauDynamique) var).getVariableEntete().toString();
		} else if (var instanceof Paquets) {
			decodedValue = ((Paquets) var).getVariableEntete().toString();
		} else if (var instanceof ChaineDynamique) {
			decodedValue = var.toString();
		} else if (!(var instanceof VariableComplexe)) {
			String unite = "";
			if (var instanceof VariableAnalogique) unite = ((DescripteurVariableAnalogique) var.getDescriptor()).getUnite();
			decodedValue = var.toString() + " " + unite;
		}
		
		return decodedValue;
	}
	

	/**
	 * Returns whether the specified property, if changed, would affect the
	 * label
	 * 
	 * @param arg0 the player
	 * @param arg1 the property
	 * @return boolean
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	
}