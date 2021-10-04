package com.faiveley.samng.principal.sm.parseurs.parseurXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import com.faiveley.samng.principal.logging.SamngLogger;
import com.faiveley.samng.principal.sm.controles.CRC16Xml;
import com.faiveley.samng.principal.sm.controles.IStrategieControle;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableDiscrete;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComplexe;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete;
import com.faiveley.samng.principal.sm.data.variableComposant.JRU.VariableDynamique;
import com.faiveley.samng.principal.sm.parseurs.Messages;

public class ParseurUtils {

	/**
	 * Ajoute la variable d'entête à une variable dynamique
	 * @param codeVarEntete
	 * @param varDyn
	 */
	public static void ajouterVariableTeteVariableDynamique(int codeVarEntete, VariableDynamique varDyn) {
		DescripteurVariable descVar = GestionnaireDescripteurs
				.getDescripteurVariable(codeVarEntete);
		if (descVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
			VariableAnalogique varAna = new VariableAnalogique();
			DescripteurVariableAnalogique d = GestionnaireDescripteurs
					.getDescripteurVariableAnalogique(codeVarEntete);
			varAna.setDescripteur(d);
			varAna.setTypeValeur(d.getType());
			varDyn.setVariableEntete(varAna);

		} else if (descVar.getTypeVariable() == TypeVariable.VAR_DISCRETE) {
			VariableDiscrete varDiscrete = new VariableDiscrete();
			DescripteurVariableDiscrete d = GestionnaireDescripteurs
					.getDescripteurVariableDiscrete(codeVarEntete);
			varDiscrete.setDescripteur(d);
			varDiscrete.setTypeValeur(d.getType());
			varDyn.setVariableEntete(varDiscrete);
		}
	}
	
	/**
	 * Retourne la taille d'une variable en bits
	 * @param v
	 * @return
	 */
	public static int getVariableBitSize(AVariableComposant v) {
		return v.getDescriptor().getTailleBits();
	}

	/**
	 * Met a jour les sous variables d'uen variable composite
	 * @param varId
	 * @param v
	 */
	public static void updateSubVarVarComposite(int varId, VariableComposite v) {
		// get each subvariable
		AVariableComposant subvar = null;
		DescripteurVariable d = GestionnaireDescripteurs
				.containsDescriptorVariable(varId);
		switch (d.getTypeVariable()) {
		case VAR_ANALOGIC:
			subvar = new VariableAnalogique();
			break;
		case VAR_DISCRETE:
			subvar = new VariableDiscrete();
			break;
		case VAR_COMPLEXE:
			subvar = new VariableComplexe();
			break;
		case VAR_COMPOSEE:
			subvar = new VariableComposite();
			break;
		}
		if (subvar != null) {
			// add the subvariable
			subvar.setDescripteur(d);
			v.ajouter(subvar);
		} else {
			throw new RuntimeException("this must not be null");
		}
	}

	/**
	 * Mise à jour des noms utiliusateur en ajoutant un prefix
	 * 
	 * @param d
	 * @param prefix
	 */
	public static void updateLanguage(DescripteurVariable d, String prefix) {
		for (Langage lng : d.getNomUtilisateur().getLanguages()) {
			d.getNomUtilisateur().setNomUtilisateur(lng,
					prefix + d.getNomUtilisateur().getNomUtilisateur(lng));

		}
	}

	/**
	 * Méthode qui charge le langage en fonction de la valeur renseignée dans le
	 * champs nom-utilisateur
	 * 
	 * @param valeurStatiqueLangage
	 * @return
	 */
	public static Langage chargerLangage(int valeurStatiqueLangage) {
		Langage lang = null;
		switch (valeurStatiqueLangage) {

		case 1:
			lang = Langage.JA_JP;
			break;
		case 2:
			lang = Langage.ES_PE;
			break;
		case 3:
			lang = Langage.EN;
			break;
		case 4:
			lang = Langage.JA_JP_JP;
			break;
		case 5:
			lang = Langage.ES_PA;
			break;
		case 6:
			lang = Langage.SR_BA;
			break;
		case 7:
			lang = Langage.MK;
			break;
		case 8:
			lang = Langage.ES_GT;
			break;
		case 9:
			lang = Langage.AR_AE;
			break;
		case 10:
			lang = Langage.NO_NO;
			break;
		case 11:
			lang = Langage.SQ_AL;
			break;
		case 12:
			lang = Langage.BG;
			break;
		case 13:
			lang = Langage.AR_IQ;
			break;
		case 14:
			lang = Langage.AR_YE;
			break;
		case 15:
			lang = Langage.HU;
			break;
		case 16:
			lang = Langage.PT_PT;
			break;
		case 17:
			lang = Langage.EL_CY;
			break;
		case 18:
			lang = Langage.AR_QA;
			break;
		case 19:
			lang = Langage.MK_MK;
			break;
		case 20:
			lang = Langage.SV;
			break;
		case 21:
			lang = Langage.DE_CH;
			break;
		case 22:
			lang = Langage.EN_US;
			break;
		case 23:
			lang = Langage.FI_FI;
			break;
		case 24:
			lang = Langage.IS;
			break;
		case 25:
			lang = Langage.CS;
			break;
		case 26:
			lang = Langage.EN_MT;
			break;
		case 27:
			lang = Langage.SL_SI;
			break;
		case 28:
			lang = Langage.SK_SK;
			break;
		case 29:
			lang = Langage.IT;
			break;
		case 30:
			lang = Langage.TR_TR;
			break;
		case 31:
			lang = Langage.ZH;
			break;
		case 32:
			lang = Langage.TH;
			break;
		case 33:
			lang = Langage.AR_SA;
			break;
		case 34:
			lang = Langage.NO;
			break;
		case 35:
			lang = Langage.EN_GB;
			break;
		case 36:
			lang = Langage.SR_CS;
			break;
		case 37:
			lang = Langage.LT;
			break;
		case 38:
			lang = Langage.RO;
			break;
		case 39:
			lang = Langage.EN_NZ;
			break;
		case 40:
			lang = Langage.NO_NO_NY;
			break;
		case 41:
			lang = Langage.LT_LT;
			break;
		case 42:
			lang = Langage.ES_NI;
			break;
		case 43:
			lang = Langage.NL;
			break;
		case 44:
			lang = Langage.GA_IE;
			break;
		case 45:
			lang = Langage.FR_BE;
			break;
		case 46:
			lang = Langage.ES_ES;
			break;
		case 47:
			lang = Langage.AR_LB;
			break;
		case 48:
			lang = Langage.KO;
			break;
		case 49:
			lang = Langage.FR_CA;
			break;
		case 50:
			lang = Langage.ET_EE;
			break;
		case 51:
			lang = Langage.AR_KW;
			break;
		case 52:
			lang = Langage.SR_RS;
			break;
		case 53:
			lang = Langage.ES_US;
			break;
		case 54:
			lang = Langage.ES_MX;
			break;
		case 55:
			lang = Langage.AR_SD;
			break;
		case 56:
			lang = Langage.IN_ID;
			break;
		case 57:
			lang = Langage.RU;
			break;
		case 58:
			lang = Langage.LV;
			break;
		case 59:
			lang = Langage.ES_UY;
			break;
		case 60:
			lang = Langage.LV_LV;
			break;
		case 61:
			lang = Langage.IW;
			break;
		case 62:
			lang = Langage.PT_BR;
			break;
		case 63:
			lang = Langage.AR_SY;
			break;
		case 64:
			lang = Langage.HR;
			break;
		case 65:
			lang = Langage.ET;
			break;
		case 66:
			lang = Langage.ES_DO;
			break;
		case 67:
			lang = Langage.FR_CH;
			break;
		case 68:
			lang = Langage.HI_IN;
			break;
		case 69:
			lang = Langage.ES_VE;
			break;
		case 70:
			lang = Langage.AR_BH;
			break;
		case 71:
			lang = Langage.EN_PH;
			break;
		case 72:
			lang = Langage.AR_TN;
			break;
		case 73:
			lang = Langage.FI;
			break;
		case 74:
			lang = Langage.DE_AT;
			break;
		case 75:
			lang = Langage.ES;
			break;
		case 76:
			lang = Langage.NL_NL;
			break;
		case 77:
			lang = Langage.ES_EC;
			break;
		case 78:
			lang = Langage.ZH_TW;
			break;
		case 79:
			lang = Langage.AR_JO;
			break;
		case 80:
			lang = Langage.BE;
			break;
		case 81:
			lang = Langage.IS_IS;
			break;
		case 82:
			lang = Langage.ES_CO;
			break;
		case 83:
			lang = Langage.ES_CR;
			break;
		case 84:
			lang = Langage.ES_CL;
			break;
		case 85:
			lang = Langage.AR_EG;
			break;
		case 86:
			lang = Langage.EN_ZA;
			break;
		case 87:
			lang = Langage.TH_TH;
			break;
		case 88:
			lang = Langage.EL_GR;
			break;
		case 89:
			lang = Langage.IT_IT;
			break;
		case 90:
			lang = Langage.CA;
			break;
		case 91:
			lang = Langage.HU_HU;
			break;
		case 92:
			lang = Langage.FR;
			break;
		case 93:
			lang = Langage.EN_IE;
			break;
		case 94:
			lang = Langage.UK_UA;
			break;
		case 95:
			lang = Langage.PL_PL;
			break;
		case 96:
			lang = Langage.FR_LU;
			break;
		case 97:
			lang = Langage.NL_BE;
			break;
		case 98:
			lang = Langage.EN_IN;
			break;
		case 99:
			lang = Langage.CA_ES;
			break;
		case 100:
			lang = Langage.AR_MA;
			break;
		case 101:
			lang = Langage.ES_BO;
			break;
		case 102:
			lang = Langage.EN_AU;
			break;
		case 103:
			lang = Langage.SR;
			break;
		case 104:
			lang = Langage.ZH_SG;
			break;
		case 105:
			lang = Langage.PT;
			break;
		case 106:
			lang = Langage.UK;
			break;
		case 107:
			lang = Langage.ES_SV;
			break;
		case 108:
			lang = Langage.RU_RU;
			break;
		case 109:
			lang = Langage.KO_KR;
			break;
		case 110:
			lang = Langage.VI;
			break;
		case 111:
			lang = Langage.AR_DZ;
			break;
		case 112:
			lang = Langage.VI_VN;
			break;
		case 113:
			lang = Langage.SR_ME;
			break;
		case 114:
			lang = Langage.SQ;
			break;
		case 115:
			lang = Langage.AR_LY;
			break;
		case 116:
			lang = Langage.AR;
			break;
		case 117:
			lang = Langage.ZH_CN;
			break;
		case 118:
			lang = Langage.BE_BY;
			break;
		case 119:
			lang = Langage.ZH_HK;
			break;
		case 120:
			lang = Langage.JA;
			break;
		case 121:
			lang = Langage.IW_IL;
			break;
		case 122:
			lang = Langage.BG_BG;
			break;
		case 123:
			lang = Langage.IN;
			break;
		case 124:
			lang = Langage.MT_MT;
			break;
		case 125:
			lang = Langage.ES_PY;
			break;
		case 126:
			lang = Langage.SL;
			break;
		case 127:
			lang = Langage.FR_FR;
			break;
		case 128:
			lang = Langage.CS_CZ;
			break;
		case 129:
			lang = Langage.IT_CH;
			break;
		case 130:
			lang = Langage.RO_RO;
			break;
		case 131:
			lang = Langage.ES_PR;
			break;
		case 132:
			lang = Langage.EN_CA;
			break;
		case 133:
			lang = Langage.DE_DE;
			break;
		case 134:
			lang = Langage.GA;
			break;
		case 135:
			lang = Langage.DE_LU;
			break;
		case 136:
			lang = Langage.DE;
			break;
		case 137:
			lang = Langage.ES_AR;
			break;
		case 138:
			lang = Langage.SK;
			break;
		case 139:
			lang = Langage.MS_MY;
			break;
		case 140:
			lang = Langage.HR_HR;
			break;
		case 141:
			lang = Langage.EN_SG;
			break;
		case 142:
			lang = Langage.DA;
			break;
		case 143:
			lang = Langage.MT;
			break;
		case 144:
			lang = Langage.PL;
			break;
		case 145:
			lang = Langage.AR_OM;
			break;
		case 146:
			lang = Langage.TR;
			break;
		case 147:
			lang = Langage.TH_TH_TH;
			break;
		case 148:
			lang = Langage.EL;
			break;
		case 149:
			lang = Langage.MS;
			break;
		case 150:
			lang = Langage.SV_SE;
			break;
		case 151:
			lang = Langage.DA_DK;
			break;
		case 152:
			lang = Langage.ES_HN;
			break;
		case 153:
			lang = Langage.DEF;
			break;
		default:
			lang = Langage.DEF;
			break;
		}

		return lang;
	}

	public static void verifCRC(String fileName, int CRC) {
		byte data[][] = initDatasDepuisFichier(fileName);
		byte debData[] = data[0];
		byte finData[] = data[1];
		byte totalAux[] = new byte[debData.length + finData.length];
		System.arraycopy(debData, 0, totalAux, 0, debData.length);
		System.arraycopy(finData, 0, totalAux, debData.length, finData.length);
		IStrategieControle control = new CRC16Xml();
		
		if (!control.controlerCRC(CRC, totalAux)) {
			String errStr = Messages.getString("errors.blocking.badXmlCrc");
			String[] fileNameSlashBlocks = fileName.split("/");
			String[] fileNameLastSlashBlockBackSlashBlocks = fileNameSlashBlocks[fileNameSlashBlocks.length - 1].split("\\\\");
			SamngLogger.getLogger().warn(String.format(errStr, fileNameLastSlashBlockBackSlashBlocks[fileNameLastSlashBlockBackSlashBlocks.length - 1]));
		}
	}

	private static byte[][] initDatasDepuisFichier(String fileName) {
		Integer intDebFin = null;
		Integer intFinDeb = null;
		File _monFile = new File(fileName);
		byte result[][] = new byte[2][];
		byte debData[] = new byte[0];
		byte finData[] = new byte[0];

		try {
			FileInputStream fis = new FileInputStream(_monFile);
			byte totalData[] = new byte[fis.available()];
			fis.read(totalData, 0, totalData.length);
			fis.close();
			int tailleaux = "\"".getBytes().length;
			byte aux[];
			for (int i = totalData.length - 1 - ("\"".getBytes().length - 1); i >= 0; i--) {
				aux = new byte[tailleaux];
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				if (!Arrays.equals(aux, "\"".getBytes()))
					continue;
				finData = new byte[totalData.length - i];
				System.arraycopy(totalData, i, finData, 0, finData.length);
				intDebFin = new Integer(i);
				break;
			}

			aux = null;
			tailleaux = "<signature CRC=\"".getBytes().length;
			for (int i = intDebFin.intValue(); i >= 0; i--) {
				aux = new byte[tailleaux];
				System.arraycopy(totalData, i, aux, 0, tailleaux);
				if (!Arrays.equals(aux, "<signature CRC=\"".getBytes()))
					continue;
				debData = new byte[i + tailleaux];
				System.arraycopy(totalData, 0, debData, 0, debData.length);
				intFinDeb = new Integer(i + tailleaux);
				break;
			}

			if (intFinDeb != null) {
				byte crcData[] = new byte[intDebFin.intValue()
						- intFinDeb.intValue()];
				System.arraycopy(totalData, intFinDeb.intValue(), crcData, 0,
						crcData.length);
				result[0] = debData;
				result[1] = finData;
				return result;
			}
		} catch (FileNotFoundException ef) {
			System.out.println("fichier introuvable");
		} catch (IOException e) {
			System.out.println(e + "erreur lors de la lecture du fichier");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}

		result[0] = debData;
		result[1] = finData;
		return result;
	}

}
