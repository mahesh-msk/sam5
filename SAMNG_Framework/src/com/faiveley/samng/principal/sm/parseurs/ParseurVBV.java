package com.faiveley.samng.principal.sm.parseurs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import noNamespace.ComposantVBVDocument.ComposantVBV;
import noNamespace.ListeVBVDocument;
import noNamespace.ListeVBVDocument.ListeVBV;
import noNamespace.VBVDocument.VBV;

import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.TableLangueNomUtilisateur;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.Langage;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.Operateur;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;

/**
 * @author Olivier
 * @version 1.0
 * @created 07-janv.-2008 11:35:58
 */
public class ParseurVBV implements IParseurInterface {
    private String vbvsFileName;
    private ListeVBVDocument vbvsDoc;
    private static String emptyXMLFileContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><liste-VBV xsi:noNamespaceSchemaLocation=\"variables_booleennes.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"></liste-VBV>";

	public ParseurVBV(){

	}

	public void finalize() throws Throwable {

	}

	private AVariableComposant createVariableComposant(String nom) {
		if(nom == null)
			return null;
		if(GestionnairePool.getInstance().getVariable(nom)!=null)
			return GestionnairePool.getInstance().getVariable(nom);
		
		//Creates an incomplete variable composite with only the name
		DescripteurVariable descrVar;
        IdentificateurVariable identif;
		AVariableComposant varComposant = new VariableComposite();
		
        descrVar = new DescripteurVariable();
		identif = new IdentificateurVariable();
		identif.setCode(-1);
		identif.setNom(nom);
		descrVar.setM_AIdentificateurComposant(identif);
		descrVar.setTailleBits(8);
		descrVar.setTypeVariable(TypeVariable.VAR_DISCRETE);
		descrVar.setType(Type.boolean8);

		varComposant.setDescripteur(descrVar);
		varComposant.setValeur("false".getBytes());//tagValCor

		return varComposant;
	}
	
	public List<VariableVirtuelle> chargerVBV(){
        String vbvId;
        String vbvName;
        String vbvValue;
        String compVbvId1;
        String compVbvName1;
        String compVbvName2;

        Operateur vbvOperator;
        VariableVirtuelle varVirtuelle;
        DescripteurVariable descrVar;
        TableLangueNomUtilisateur tblLangUserName;
        IdentificateurVariable identif;
        List<ComposantVBV> listCompVbv;
        ComposantVBV xmlComposantVbv;
        
        AVariableComposant operand1;
        AVariableComposant operand2;

        List<VariableVirtuelle> retVbvsList = new ArrayList<VariableVirtuelle>(0);

        if (this.vbvsDoc == null) {
            return retVbvsList;
        }

        ListeVBV xmlListVbvs = vbvsDoc.getListeVBV();

        if (xmlListVbvs == null) {
            return retVbvsList;
        }

        List<VBV> xmlListVbvsList = xmlListVbvs.getVBVList();

        for (VBV xmlVBV : xmlListVbvsList) {
            operand1 = null;
            operand2 = null;
            vbvValue = null;
            
            try {
                vbvId = xmlVBV.getId().getStringValue();
            } catch (Exception e) {
                System.err.println("Cannot read VBV id: " + e.getMessage());

                continue;
            }
            
            try {
            	vbvOperator = getOperatorFromString(xmlVBV.getOperateur().getStringValue());
            } catch (Exception e) {
                System.err.println("Read VBV operator exception: " +
                    e.getMessage());
                continue;
            }
            
            try {
            	vbvName = xmlVBV.getNom().getStringValue();
                if(vbvName == null || "".equals(vbvName.trim()))
                	vbvName = "Generated VBV name for id " + " " + vbvId;
            } catch (Exception e) {
                System.err.println("Cannot read VBV name: " +
                    e.getMessage());
                vbvName = "Generated VBV name for id " + vbvId;
            }
            
            listCompVbv = xmlVBV.getComposantVBVList();
            if(listCompVbv == null || listCompVbv.size() == 0) {
            	System.err.println("Invalid list of VBV composants");
            	continue;
            }
            xmlComposantVbv = listCompVbv.get(0);
        	compVbvId1 = xmlComposantVbv.getId().getStringValue();
        	compVbvName1 = xmlComposantVbv.getNom().getStringValue();
        	if(listCompVbv.size() == 2) {
        		xmlComposantVbv = listCompVbv.get(1);
            	compVbvName2 = xmlComposantVbv.getNom().getStringValue();
            	if("0".equals(compVbvId1)) {
            		operand1 = createVariableComposant(compVbvName1);
            		operand2 = createVariableComposant(compVbvName2);
            	} else {
            		operand1 = createVariableComposant(compVbvName2);
            		operand2 = createVariableComposant(compVbvName1);
            	}
        	} else {
        		operand1 = createVariableComposant(compVbvName1);
        	}
            
            if(operand1 == null && operand2 == null) {
            	System.err.println("Invalid operands configuration found for VBV " + vbvName);
            	continue;
            }

            if(operand2 == null) {
	            try {
	            	vbvValue = xmlVBV.getValeur().getStringValue();
	            } catch (Exception e) {
	            	vbvValue = null;
	            }
            }
            //We need to have at least one of these 2 valid
            if(vbvValue == null && operand2 == null) {
            	System.err.println("Invalid operand or value configuration found for VBV " + vbvName);
            	continue;
            }

            varVirtuelle = new VariableVirtuelle();
            descrVar = new DescripteurVariable();
			identif = new IdentificateurVariable();
			identif.setCode(Integer.parseInt(vbvId) + 65536);
			identif.setNom(vbvName);
			descrVar.setM_AIdentificateurComposant(identif);
			descrVar.setTailleBits(8);
			descrVar.setTypeVariable(TypeVariable.VAR_VIRTUAL);
			descrVar.setType(Type.boolean8);
			tblLangUserName = new TableLangueNomUtilisateur();
			tblLangUserName.setNomUtilisateur(Langage.DEF, "(V) " + vbvName);
			descrVar.setNomUtilisateur(tblLangUserName);

            varVirtuelle.setDescripteur(descrVar);
            varVirtuelle.setM_Operateur(vbvOperator);
            varVirtuelle.setTypeValeur(Type.string);
            varVirtuelle.setValeurObjet(vbvValue);//tagValCor
            
            varVirtuelle.ajouter(operand1);
            if(operand2 != null)
            	varVirtuelle.ajouter(operand2);
            
            retVbvsList.add(varVirtuelle);
        }

        return retVbvsList;
	}

	public boolean enregistrerVBV(List<VariableVirtuelle> listeVariables) throws ParseurXMLException{
        if (this.vbvsDoc == null || listeVariables == null) {
            return false;
        }

        //We should remove the file if no VBVs are for save
        if(listeVariables.size() == 0) {
        	try {
	        	File file = new File(this.vbvsFileName);
	        	if (file.exists())
	        		file.delete();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }

        this.vbvsDoc.getListeVBV().getVBVList().clear();
        
        VBV xmlVBV;
        ComposantVBV xmlComposantVbv;
        XmlAnySimpleType xmlSimpleType;
        Object value;
        int id = 0;
        for (VariableVirtuelle vbv : listeVariables) {
        	//add a new VBV
        	xmlVBV = this.vbvsDoc.getListeVBV().addNewVBV();
        	
        	//first add the ID
        	xmlSimpleType = xmlVBV.addNewId();
        	xmlSimpleType.setStringValue(String.valueOf(id++));
        	
        	//add the name
        	xmlSimpleType = xmlVBV.addNewNom();
        	xmlSimpleType.setStringValue(vbv.getDescriptor().getM_AIdentificateurComposant().getNom());

        	//add the operator
        	xmlSimpleType = xmlVBV.addNewOperateur();
        	xmlSimpleType.setStringValue(vbv.getM_Operateur().getStringValue());
        	
        	//add the first operand
        	xmlComposantVbv = xmlVBV.addNewComposantVBV();
        	xmlSimpleType = xmlComposantVbv.addNewNom();
        	xmlSimpleType.setStringValue(vbv.getEnfant(0).getDescriptor().getM_AIdentificateurComposant().getNom());
        	xmlSimpleType = xmlComposantVbv.addNewId();
        	xmlSimpleType.setStringValue("0");	//set also its id
        
        	//add the second operator or the value
        	if(vbv.getM_AVariableComposant()!=null && vbv.getM_AVariableComposant().size()>1 && vbv.getM_AVariableComposant().get(1)!=null)	{ //we have also the second operator
            	xmlComposantVbv = xmlVBV.addNewComposantVBV();
            	xmlSimpleType = xmlComposantVbv.addNewNom();
            	xmlSimpleType.setStringValue(vbv.getEnfant(1).getDescriptor().getM_AIdentificateurComposant().getNom());
            	xmlSimpleType = xmlComposantVbv.addNewId();
            	xmlSimpleType.setStringValue("1");	//set also its id
        	} else {		//we have a value
        		value = vbv.getValeurObjet();//tagValCor	//normally it should be not null and a String
        		if(value == null || !(value instanceof String))
        			value = "0";
        		xmlSimpleType = xmlVBV.addNewValeur();
        		xmlSimpleType.setStringValue((String)value);
        	}
        }

        return saveDocument();
	}

	/**
	 * 
	 * @param chemin
	 */
	public void parseRessource(String chemin,boolean explorer,int deb,int fin){
        try {
            File file = new File(chemin);

            if (!file.exists() || file.length() == 0) {
                FileOutputStream fOut = new FileOutputStream(file);
                new PrintStream(fOut).print(emptyXMLFileContent);
            }

            this.vbvsDoc = ListeVBVDocument.Factory.parse(file);
            this.vbvsFileName = chemin;
        } catch (XmlException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            //  Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	public String getLastParsedFileName() {
		return this.vbvsFileName;
	}
	
    /**
     * Saves the current document in the specified XML file name
     * @return true if the save was successfull
     */
    private boolean saveDocument() {
        try {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setSavePrettyPrint();
            xmlOptions.setSavePrettyPrintIndent(4);
            this.vbvsDoc.save(new File(this.vbvsFileName), xmlOptions);
            //: I think this is not ok ... Maybe another method is to have the listeFiltres updated
            this.vbvsDoc = ListeVBVDocument.Factory.parse(new File(
                        this.vbvsFileName));
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
        return true;
    }
    
    private Operateur getOperatorFromString(String opStr) throws InvalidOperatorException {
    	if(opStr == null)
    		throw new InvalidOperatorException("Operator is null");
    	opStr = opStr.trim();
    	if(opStr.length() == 0)
    		throw new InvalidOperatorException("Operator value is empty");
    	if(opStr.equals("AND"))
    		return Operateur.AND;
    	else if(opStr.equals("OR"))
    		return Operateur.OR;
    	else {
	    	switch(opStr.charAt(0)) {
	    		case '=':
	    			return Operateur.EQUALS;
	    		case '<':
	    			return Operateur.LESS_THAN;
	    		case '>':
	    			return Operateur.GREATER_THAN;
	    		case '\u2260':
	    			return Operateur.DIFFERENT;
	    		case '\u2265':
	    			return Operateur.GREATER_THAN_OR_EQUALS;
	    		case '\u2264':
	    			return Operateur.LESS_THAN_OR_EQUALS;
	    		default:
	    			throw new InvalidOperatorException("Inavalid operator value " + opStr);
	    	}
    	}
    }
    
    
    private class InvalidOperatorException extends Exception {
		private static final long serialVersionUID = -2225958842854083472L;

		public InvalidOperatorException(String msg) {
    		super(msg);
    	}
    }

}