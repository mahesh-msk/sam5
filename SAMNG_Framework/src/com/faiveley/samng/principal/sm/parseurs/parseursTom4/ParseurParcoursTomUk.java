package com.faiveley.samng.principal.sm.parseurs.parseursTom4;

import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.nomFichierXML;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleEntete;
import static com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire.tailleTableEvenement;
import static com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4.maxCptDistance;
import static com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4.maxCptTps;
import static com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4.pasCptDistance;
import static com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4.pasCptTps;
import static com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4.resolutionTemps;

import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTom4;
import com.faiveley.samng.principal.sm.data.descripteur.ADescripteurComposant;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurCodeBloc;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurComposite;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurTableAssociation;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.ADonneeBlocComposant;
import com.faiveley.samng.principal.sm.data.descripteur.donneeBlocComposant.DonneeBloc;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.identificateurComposant.AIdentificateurComposant;
import com.faiveley.samng.principal.sm.data.identificateurComposant.IdentificateurVariable;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.AParcoursComposant;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.Entete;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.erreurs.AExceptionSamNG;
import com.faiveley.samng.principal.sm.erreurs.BadFileLengthException;
import com.faiveley.samng.principal.sm.erreurs.BadTableEventStructureException;
import com.faiveley.samng.principal.sm.erreurs.ParseurException;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.AFabriqueParcoursAbstraite;
import com.faiveley.samng.principal.sm.missions.explorersingletons.activationexplorer.ActivationExplorer;
import com.faiveley.samng.principal.sm.missions.explorersingletons.parseurs.ParseurParcoursTomUkExplorer;
import com.faiveley.samng.principal.sm.parseurs.ConstantesParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.Messages;
import com.faiveley.samng.principal.sm.parseurs.ParseurFlags;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;

public class ParseurParcoursTomUk extends AParseurParcoursTom4{


	/**
	 * Private constructor. Just in the class is created the only instance of
	 * the parser
	 * 
	 */
	protected ParseurParcoursTomUk() {

	}

	/**
	 * Returns the instance of the parser
	 * 
	 * @return ParseurParcoursSamng instance
	 */
	public static ParseurParcoursTomUk getInstance() {
		try{
			if(instance==null)
				instance = new ParseurParcoursTomUk();
			
			
			if (ActivationExplorer.getInstance().isActif()) {
				return ParseurParcoursTomUkExplorer.getInstance();
			}
			
			return (ParseurParcoursTomUk)instance;
		}
		catch(ClassCastException ex){
			if (ActivationExplorer.getInstance().isActif()) {
				return new ParseurParcoursTomUkExplorer();
			}
			return new ParseurParcoursTomUk();
		}
	}

	
	/** Suppression de l'instance */
	public void clear(){
		super.clear();
		instance = null;
	}

	/**
	 * Load the header from the binary file
	 * 
	 * @return the Entete as a ParcoursComposant
	 * @throws AExceptionSamNG
	 */
	protected AParcoursComposant chargerEntete() throws AExceptionSamNG {

		Entete entete = new Entete();



		// set the bytes for tailleEntate message[1, 2]
		tailleEntete = 132;

		ConstantesParcoursTom4.tailleConfiguration = 11452;
		String nomFichier = nomFichierBinaire.toUpperCase();

		//		on défini la taille des enregistrements(blocs de données) et le début de la table evt var en fonction de l'extension
		//cas des fichiers fin de parcours
		if( nomFichier.endsWith(".ftb".toUpperCase())|| nomFichier.endsWith(".fkb".toUpperCase())){
			ConstantesParcoursTom4.octetDebutTableEvtVar = 132;
			ConstantesParcoursBinaire.tailleBlocData = new BigInteger(Arrays.copyOfRange(this.message, 130, 132)).shortValue();
		}

		if(nomFichier.endsWith(".ltb".toUpperCase())|| nomFichier.endsWith(".lkb".toUpperCase())|| nomFichier.endsWith(".lwb".toUpperCase())){
			ConstantesParcoursTom4.octetDebutTableEvtVar = 5778;
			ConstantesParcoursBinaire.tailleBlocData = new BigInteger(Arrays.copyOfRange(this.message, 5774, 5776)).shortValue();
		}



		resolutionTemps = 1;

		pasCptTps = this.message[92];
		maxCptTps =  new BigInteger(Arrays.copyOfRange(this.message, 94, 96)).shortValue();

		pasCptDistance = new BigInteger(Arrays.copyOfRange(this.message, 102, 104)).shortValue();
		maxCptDistance = new BigInteger(Arrays.copyOfRange(this.message, 104, 106)).shortValue();





		// CIU - Protection on header length is greater than the read bytes
		if (this.message.length < tailleEntete)
			throw new BadFileLengthException(Messages
					.getString("errors.blocking.invalidHeaderLength2")); //$NON-NLS-1$
		entete.setTailleEntete(tailleEntete);

		// set the file name that is written on x bytes starting to byte 4
		try {
			nomFichierXML = getNomFichierXml(nomFichierBinaire);
		} catch (Exception e) {

			throw new ParseurException();
		}


		entete.setNomFichierXML(nomFichierXML);

		return entete;
	}


	/**
	 * Loads the descriptor pf the table association
	 * 
	 * @param start
	 *            the start position for the association table between events
	 *            and variables
	 * @return the descriptor
	 */
	private ADescripteurComposant chargerDescripteursTableAssociation()
			throws AExceptionSamNG {
		DescripteurTableAssociation descr = new DescripteurTableAssociation();
		AIdentificateurComposant identifier = new IdentificateurVariable();

		// set the table evenement
		// set the code for the identifier
		// la table evt/var a une taille fixe de 5640
		tailleTableEvenement = 5640;
		if (tailleTableEvenement <= 0)
			throw new BadFileLengthException(Messages
					.getString("errors.blocking.invalidEvTableLen1")); //$NON-NLS-1$

		if ((tailleTableEvenement % 2) != 0)
			throw new BadTableEventStructureException(Messages
					.getString("errors.blocking.invalidEvTableLen2")); //$NON-NLS-1$

		identifier.setCode(tailleTableEvenement);
		// set the name for the identifier
		identifier.setNom("tailleTableEvenement"); //$NON-NLS-1$

		// set the identifier
		descr.setM_AIdentificateurComposant(identifier);
		descr.setTailleBits(32);

		return descr;
	}

	/**
	 * Load the descriptor for the header
	 * 
	 * @return the descriptor
	 */
	private ADescripteurComposant chargerDescripteursEntete()
			throws AExceptionSamNG {
		DescripteurCodeBloc descr = new DescripteurCodeBloc();

		// the first position of a descriptor

		// set ConstantesParcoursBinaire.tailleBlocData in Entete
		descr.ajouter(chargerDescripteursTableAssociation());

		// set the codes
		descr.ajouter(chargerDescripteursCodeBloc());

		return descr;
	}

	protected void traitementFichier(AFabriqueParcoursAbstraite factory,
			String fileName, boolean explorer) throws AExceptionSamNG {
		// // resets the parser
		resetParser();

		this.tc=new ConversionTempsTom4("01/01/1990 00:00:00.000");
		//
		// check if the file is ok
		if (fileName == null || fileName.length() == 0
				|| !new File(fileName).exists()) {
			throw new IllegalArgumentException("No resource to parse"); //$NON-NLS-1$
		}
		//  check the extension file
		if (!(fileName.endsWith(".lkb")|| fileName.endsWith(".ftb")|| fileName.endsWith(".fkb") || fileName.endsWith(".ltb")|| fileName.endsWith(".lkb")|| fileName.endsWith(".lwb"))) { //$NON-NLS-1$ //$NON-NLS-2$
			throw new IllegalArgumentException(Messages
					.getString("errors.blocking.invalidFileExtension")); //$NON-NLS-1$
		}
		File binaryFile = new File(fileName);

		this.message = loadBinaryFile(binaryFile,0,-1);
		if (this.message == null) {
			throw new IllegalArgumentException("No resource to parse");
		}
		if (this.message.length < 4)
			throw new BadFileLengthException(Messages.getString("errors.blocking.invalidHeaderLength1"));

		// create the Parcour
		factory.creerParcours();

		// create base Entete
		AParcoursComposant baseEntete = chargerEntete();

		// create the Entete descriptor
		ADescripteurComposant descriptor = chargerDescripteursEntete();

		// create the Entete
		factory.creerEntete(descriptor, baseEntete);

		ParseurFlags.getInstance().parseRessource(RepertoiresAdresses.FLAGS_FILE_DIR + "flags.xml",false,0,-1);
		this.loadedFlags = ParseurFlags.getInstance().chargerFlags();

		// ************ PARSE XML file ********/
		GestionnairePool.getInstance().chargerFichierXml(RepertoiresAdresses.xml+ File.separator + ((Entete)baseEntete).getNomFichierXML(),fileName);

		//chargement de la table evt/var
		ADescripteurComposant descripteursEvt = chargerTableEvtVariable();

		// create the table
		factory.creerTableEvtVar(descripteursEvt);

		//		affichage ordonné des variables
		DescripteurComposite descrComposite = (DescripteurComposite)descripteursEvt;
		HashMap<Integer, DescripteurComposite> hashMapCodeDescComp = new HashMap<Integer, DescripteurComposite>();
		for (int o =0; o<descrComposite.getLength();o++) {
			hashMapCodeDescComp.put((Integer)descrComposite.getEnfant(o).getEnfant(0).getM_AIdentificateurComposant().getCode(), (DescripteurComposite)descrComposite.getEnfant(o));
		}
		GestionnaireDescripteurs.setMapEvenementVariables(hashMapCodeDescComp);

		// ********* LOAD Info ******************
		InfosFichierSamNg info = (InfosFichierSamNg) GestionnairePool.getInstance()
				.getXMLParser().getInfosFichier();
		if (info == null)
			throw new ParseurXMLException(Messages.getString("errors.blocking.errorLoadingXMLFile"), true);
		info.setNomFichierParcoursBinaire(binaryFile.getAbsolutePath());
		info.setNomFichierXml(((Entete)baseEntete).getNomFichierXML());
		factory.creerInfoFichier(info);
	}

	@Override
	protected ADonneeBlocComposant chargerCRCConfiguration() {
		ADonneeBlocComposant crc = new DonneeBloc();
		int value = new BigInteger(Arrays.copyOfRange(this.message, 11450, 11452)).intValue();
		crc.setValeur(Integer.valueOf(value));
		return crc;
	}
}
