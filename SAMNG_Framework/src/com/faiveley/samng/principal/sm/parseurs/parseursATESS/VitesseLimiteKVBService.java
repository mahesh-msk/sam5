package com.faiveley.samng.principal.sm.parseurs.parseursATESS;

import java.io.File;
import java.math.BigInteger;

import com.faiveley.kvbdecoder.services.loader.KVBLoaderService;
import com.faiveley.kvbdecoder.services.xml.XMLService;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableAnalogique;

public class VitesseLimiteKVBService {

    private static VitesseLimiteKVBService instance = null;
    private static final Langage language = Activator.getDefault().getCurrentLanguage();

    public static final String VITESSE_LIMITE_KVB_NAME = "Vitesse_limite_KVB";

    private VariableAnalogique vitesseLimiteKVBVariable = null;
    private TableLangueNomUtilisateur tblLangUserName;

    private VitesseLimiteKVBService() {
	this.tblLangUserName = new TableLangueNomUtilisateur();
	this.tblLangUserName.setNomUtilisateur(language, KVBLoaderService.getServiceInstance().getLabel(VITESSE_LIMITE_KVB_NAME, language.toString()));
    }

    public static boolean isTableKVBXMLexist(){
	String filePath = XMLService.XML_FOLDER + KVBLoaderService.XML_FILE_TABLESKVB_NAME;
	File xmlFile = new File(filePath);
	return xmlFile.exists();
    }
    
    public static VitesseLimiteKVBService getInstance() {
	if (instance == null) {
	    instance = new VitesseLimiteKVBService();
	}
	return instance;
    }

    public VariableAnalogique getVariable() {
	if (this.vitesseLimiteKVBVariable == null) {
	    this.vitesseLimiteKVBVariable = this.buildVitesseLimiteKVBVariable();
	}
	return this.vitesseLimiteKVBVariable;
    }

    public VariableAnalogique getNewVariable(Long value) {
	VariableAnalogique newVitesseLimiteKVB = this.buildVitesseLimiteKVBVariable();
	if ((value != null) && (value >= 0)) {
	    newVitesseLimiteKVB.setValeur(bigIntToByteArray(value));
	} else {
	    newVitesseLimiteKVB.setValeur(null);
	}
	return newVitesseLimiteKVB;
    }

    public TableLangueNomUtilisateur getTableLangueNomUtilisateur() {
	return this.tblLangUserName;
    }

    private VariableAnalogique buildVitesseLimiteKVBVariable() {
	AIdentificateurComposant identificateurVitesseLimiteKVB = new IdentificateurVariable();
	identificateurVitesseLimiteKVB.setNom(VITESSE_LIMITE_KVB_NAME);
	identificateurVitesseLimiteKVB.setCode(Integer.MAX_VALUE);

	DescripteurVariableAnalogique descriptorVitesseLimiteKVB = new DescripteurVariableAnalogique();
	descriptorVitesseLimiteKVB.setM_AIdentificateurComposant(identificateurVitesseLimiteKVB);
	descriptorVitesseLimiteKVB.setTypeVariable(TypeVariable.VAR_ANALOGIC);
	descriptorVitesseLimiteKVB.setType(Type.uint16);
	descriptorVitesseLimiteKVB.setUnite("km/h");
	descriptorVitesseLimiteKVB.setCoefDirecteur(1.0);
	descriptorVitesseLimiteKVB.setOrdonneeOrigine(0.0);

	// Récupération des traductions
	descriptorVitesseLimiteKVB.setNomUtilisateur(tblLangUserName);

	VariableAnalogique vitesseLimiteKVB = new VariableAnalogique();
	vitesseLimiteKVB.setDescripteur(descriptorVitesseLimiteKVB);
	vitesseLimiteKVB.setTypeValeur(descriptorVitesseLimiteKVB.getType());
	vitesseLimiteKVB.setEscalier(true);
	return vitesseLimiteKVB;
    }

    private byte[] bigIntToByteArray(final long i) {
	BigInteger bigInt = BigInteger.valueOf(i);
	return bigInt.toByteArray();
    }
}
