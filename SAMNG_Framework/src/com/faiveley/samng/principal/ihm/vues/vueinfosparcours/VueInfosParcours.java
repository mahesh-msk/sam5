package com.faiveley.samng.principal.ihm.vues.vueinfosparcours;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.ViewPart;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.actions.captures.CapturerVueAction;
import com.faiveley.samng.principal.ihm.actions.captures.ICapturable;
import com.faiveley.samng.principal.ihm.actions.captures.ImprimerVueAction;
import com.faiveley.samng.principal.ihm.listeners.IDataChangedListener;
import com.faiveley.samng.principal.ihm.listeners.IRepereChangedListener;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTom4;
import com.faiveley.samng.principal.sm.calculs.conversionTemps.ConversionTempsTomNg;
import com.faiveley.samng.principal.sm.calculs.referenceZero.CalculReferenceZeroAtess;
import com.faiveley.samng.principal.sm.controles.util.XMLName;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.Enregistrement;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosFichierSamNg;
import com.faiveley.samng.principal.sm.data.tableAssociationComposant.InfosParcours;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.data.variableComposant.VariableComposite;
import com.faiveley.samng.principal.sm.erreurs.ParseurXMLException;
import com.faiveley.samng.principal.sm.fabriques.FabriqueParcours;
import com.faiveley.samng.principal.sm.marqueurs.GestionnaireMarqueurs;
import com.faiveley.samng.principal.sm.parseurs.ParseurParcoursBinaire;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur.ParseurAdapteur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.repertoires.RepertoiresAdresses;
import com.faiveley.samng.principal.sm.segments.SegmentDistance;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueInfosParcours extends ViewPart implements ICapturable,IRepereChangedListener,IDataChangedListener {
	public static final String ID = "SAMNG_Framework.VueInfosParcours"; //$NON-NLS-1$
	boolean unSegmentTempsEstNonValide;
	private Composite mainComposite;
	private Text infoText;

	private Color backgroundColor;

	private Label lblAnnotation;

	private Text annotationText;

	/**
	 * Déclaration des actions
	 */
	private CapturerVueAction capturerVueAction;

	private ImprimerVueAction imprimerVueAction;

	// ToolBar
	private IActionBars bars;

	private Button bSave = null;

	private static String jour = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteJour.0");
	private static String heure = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteHeure.0");
	private static String minute = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMinute.0");
	private static String seconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteSeconde.0");
	private static String milliseconde = com.faiveley.samng.principal.sm.calculs.Messages.getString("uniteMilliSeconde.0");

	@Override
	public void createPartControl(Composite parent) {
		
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		setPartName(Messages.getString("VueInfosParcours.0"));
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		
		ScrolledComposite sc = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);

		this.mainComposite = new Composite(sc, SWT.NONE);
		mainComposite.setLayout(layout);

		this.infoText = new Text(this.mainComposite, 
								 SWT.MULTI | SWT.WRAP);
		this.infoText.setText(""); //$NON-NLS-1$
		this.infoText.setEditable(false);
		this.backgroundColor = new Color(Display.getCurrent(), new RGB(255, 255, 255));
		this.infoText.setBackground(this.backgroundColor);
		
		GridData gridDataInfo = new GridData();
		gridDataInfo.horizontalAlignment = SWT.FILL;
		gridDataInfo.verticalAlignment = SWT.FILL;
		gridDataInfo.grabExcessHorizontalSpace = true;
		gridDataInfo.grabExcessVerticalSpace = true;
		gridDataInfo.minimumHeight = 30;
	    this.infoText.setLayoutData(gridDataInfo);
		

		this.lblAnnotation = new Label(this.mainComposite, SWT.NONE);
		this.lblAnnotation.setText("\n\r" + Messages.getString("VueInfosParcours.15")); //$NON-NLS-1$
		this.lblAnnotation.setToolTipText((Messages.getString("VueInfosParcours.15")));
		GridData gridDataLbl = new GridData();
		gridDataLbl.grabExcessVerticalSpace = false;
		gridDataLbl.horizontalAlignment = SWT.FILL;
	    this.infoText.setLayoutData(gridDataLbl);

		this.annotationText = new Text(this.mainComposite, SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL);
		this.annotationText.setEditable(true);
		this.annotationText.setBackground(this.backgroundColor);
		GridData gridDatAno = new GridData();
		gridDatAno.horizontalAlignment = SWT.FILL;
		gridDatAno.verticalAlignment = SWT.FILL;
		gridDatAno.grabExcessHorizontalSpace = true;
		gridDatAno.grabExcessVerticalSpace = true;
		gridDatAno.minimumHeight = 200;
	    this.annotationText.setLayoutData(gridDatAno);

		bSave = new Button(getContenu(), SWT.NONE);
		bSave.setEnabled(false);
		bSave.setText(Messages.getString("VueInfosParcours.16")); //$NON-NLS-1$
		bSave.setToolTipText((Messages.getString("VueInfosParcours.16")));
		
		GridData gridDataBsave = new GridData();
		gridDataBsave.grabExcessVerticalSpace = false;
		bSave.setLayoutData(gridDataBsave);

		bSave
		.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
			public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
				InfosFichierSamNg infos;
				try{
					infos = (InfosFichierSamNg)FabriqueParcours.getInstance().getParcours().getInfo();
				}catch(Exception ex){
					infos = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier());
				}	
				GestionnaireMarqueurs gestMark = new GestionnaireMarqueurs();
				gestMark.chargerMarqueurs(infos.getNomFichierParcoursBinaire());
				gestMark.enregistrerAnnotationParcours(VueInfosParcours.this.annotationText.getText());
				MessageBox messageBox = new MessageBox(VueInfosParcours.this.getViewSite().getShell(),SWT.ICON_INFORMATION);
				messageBox.setMessage(Messages.getString("VueInfosParcours.18")); //$NON-NLS-1$
				messageBox.open();
				VueInfosParcours.this.bSave.setEnabled(false);
			}
		});

		if(ActivatorData.getInstance().getVueData().getDataTable()!=null){
			definirTexteInfosParcours();
			definirAnnotationParcours();
		}

		this.annotationText.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
			public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
				VueInfosParcours.this.bSave.setEnabled(true);
			}
		});
		
		makeActions();
		this.bars = getViewSite().getActionBars();
		ajoutActionToolBar(imprimerVueAction);
		ajoutActionToolBar(capturerVueAction);
		ActivatorData.getInstance().addRepereListener(this);
		ActivatorData.getInstance().addDataListener(this);
		
		
		sc.setContent(mainComposite);
		sc.setExpandHorizontal(true);
		sc.setExpandVertical(true);
		sc.setMinSize(mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
	}

	/** Ajout des actions */
	public void makeActions() {
		// super.makeActions();
		// récupération de la fenetre active
		IWorkbenchWindow window = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
		// ajout des actions
		this.capturerVueAction = new CapturerVueAction(window, Messages.getString("VueInfosParcours.2")); //$NON-NLS-1$
		this.capturerVueAction.setEnabled(capturerVueAction.isEnabled());
		this.imprimerVueAction = new ImprimerVueAction(window, Messages.getString("VueInfosParcours.3")); //$NON-NLS-1$
		this.imprimerVueAction.setEnabled(imprimerVueAction.isEnabled());
	}

	/**
	 * Méthode d'ajout d'une action dans le menu
	 * 
	 * @param action
	 */
	public void ajoutActionToolBar(Action action) {
		this.bars.getToolBarManager().add(action);
	}

	/**
	 * Méthode d'ajout d'une action dans la toolbar
	 * 
	 * @param action
	 */
	public void ajoutActionToolMenuBar(Action action) {
		this.bars.getMenuManager().add(action);
	}

	/** */
	public void ajoutSeparateurToolBar() {
		this.bars.getMenuManager().add(new Separator());
	}

	@Override
	public void setFocus() {
	}

	private long getDuree1(long duree,Message msgFinSegment,Message msgDebutSegment,
			String initialTime,String currentTime){

		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		// creates the rows
		long diffTempsChgtHeure=0;
		for (SegmentTemps segTemps : listeSegs) {
			if (segTemps.isValide()) {
				msgFinSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segTemps.getEndMsgId());
				msgDebutSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segTemps.getStartMsgId());
				long tempAbsoluChgtHeure = 0;
				long tempAbsoluSegmentSuivant = 0;
				long tempAbsoluSegmentPrecedent = 0;

				ParseurAdapteur padapt=new ParseurAdapteur();
				diffTempsChgtHeure=padapt.gererCalculTempsParcoursChgtsHeure(diffTempsChgtHeure, tempAbsoluSegmentSuivant, tempAbsoluChgtHeure, msgDebutSegment, segTemps, msgFinSegment, tempAbsoluSegmentPrecedent);
				initialTime = segTemps.getTempInitial();
				currentTime = segTemps.getTempFinal();
				try {
					duree += ConversionTemps.calculatePeriodAsLong(initialTime,currentTime);// +decalageChangmtHeure;
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				unSegmentTempsEstNonValide = true;
			}	
		}

		return (duree+diffTempsChgtHeure);
	}




	private long getDureeParcours(){
		String initialTime="";
		String currentTime="";
		Long duree = (long) 0;
		unSegmentTempsEstNonValide = false;
		Message msgFinSegment = null;
		Message msgDebutSegment = null;

		duree=getDuree1(duree,msgFinSegment, msgDebutSegment, initialTime, currentTime);
		return duree;
	}

	/**
	 * Définit le texte concernant les informations parcours
	 * 
	 */
	private void definirTexteInfosParcours() {

		String uniteDistance = "";
		try {
			uniteDistance = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getUnite();
		} catch (Exception ex) {

		}

		InfosFichierSamNg infos = (InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier();
		try {
			if (infos == null){
				throw new ParseurXMLException(Messages.getString("errors.blocking.errorLoadingXMLFile"), true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		File f = new File(RepertoiresAdresses.xml + "/" + XMLName.updateCurrentXmlName());
		
		String infosFichierXml = Messages.getString("VueInfosParcours.4") + " ";
		
		try {
			infosFichierXml +=  f.getCanonicalPath();
		} catch (IOException e1) {
			// TODO Bloc catch généré automatiquement
			e1.printStackTrace();
			infosFichierXml += XMLName.updateCurrentXmlName();
		} //$NON-NLS-1$ //$NON-NLS-2$
		
		infosFichierXml += "\n" + Messages.getString("VueInfosParcours.valeurCRC") + infos.getCRCFichierXML() + "\n";;
		
		InfosParcours infosParcours = GestionnairePool.getInstance().getInfosParcours();
		List<Message> allMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();


		double distancetotale = 0;


		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();

		double memoireDistance=-1;
		Enregistrement enreg= ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement();

		//Ancien calcul distance cumulee
		for (SegmentTemps segTemps : listeSegs) {
			int startMsgId = segTemps.getStartMsgId();
			int endMsgId = segTemps.getEndMsgId();

			double diff = enreg.getGoodMessage(endMsgId)
					.getAbsoluteDistance()- enreg.getGoodMessage(startMsgId).getAbsoluteDistance();
			if (memoireDistance>=0) {
				distancetotale =distancetotale + enreg.
						getGoodMessage(startMsgId).getAbsoluteDistance()-memoireDistance;
				memoireDistance=-1;
			}
			if (enreg.getGoodMessage(endMsgId).getEvenement().isChangementHeure()) {
				memoireDistance=enreg.
						getGoodMessage(endMsgId).getAbsoluteDistance();
			}

			distancetotale =distancetotale + diff;
			//			System.out.println("distance parcours ( " +ConversionTemps.getFormattedDate(enreg.
			//					getGoodMessage(startMsgId).getAbsoluteTime(),true) + " - " +ConversionTemps.getFormattedDate(enreg.
			//							getGoodMessage(endMsgId).getAbsoluteTime(),true)   +") : " +diff);
		}


		//ancien calcul duree cumulee
		long duree=getDureeParcours();

		String dureeRec = "###";
		if (!unSegmentTempsEstNonValide) {
			dureeRec = ConversionTemps.getRelativeTimeAsString(duree,jour,heure,minute,seconde,milliseconde);
		}

		String distancetotaleSTR = null;
		if (distancetotale != -1)
			distancetotaleSTR = String.valueOf(AVariableComposant.arrondir(distancetotale, 3));




		//nouveau calcul duree cumulee et distance cumulee utilisant le meme calcul que la référence
		//		double distanceCumuleeTotale= 0;
		//		long tempsCumuleTotal= 0;
		//		if(GestionnairePool.getMapTempsCumuleEtDistanceCumule()==null){
		//			try{
		//				HashMap<String,Object> hashMap = calculerTempsCumuleEtDistanceCumuleeCommeRefZero();
		//				
		//			tempsCumuleTotal = (Long)hashMap.get("tempsCumuleTotal");
		//			distanceCumuleeTotale = (Double)hashMap.get("distanceCumuleeTotale");
		//			
		//			GestionnairePool.setMapTempsCumuleEtDistanceCumule(hashMap);
		//			}
		//			catch(Exception ex){
		//			
		//			}
		//		}else{
		//			try{
		//			tempsCumuleTotal = (Long)GestionnairePool.getMapTempsCumuleEtDistanceCumule().get("tempsCumuleTotal");
		//			distanceCumuleeTotale = (Double)GestionnairePool.getMapTempsCumuleEtDistanceCumule().get("distanceCumuleeTotale");
		//			}
		//			catch(Exception ex){
		//				
		//			}
		//		}
		//		
		//		String dureeTotalEnregistrement = ConversionTemps.getRelativeTimeAsString(tempsCumuleTotal,jour,heure,minute,seconde,milliseconde);
		//		String distanceTotaleEnregistrement = String.valueOf(AVariableComposant.arrondir(distanceCumuleeTotale, 3));

		//		if(!(TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess) && !(TypeParseur.getInstance().getParser() instanceof ParseurParcoursSamng)){
		//		dureeRec = dureeTotalEnregistrement;
		//		distancetotaleSTR = distanceTotaleEnregistrement;
		//		}

		String debutTime = ConversionTemps.getFormattedDate(allMessages.get(0).getAbsoluteTime(),true);
		String finalTime = ConversionTemps.getFormattedDate(allMessages.get(allMessages.size() - 1).getAbsoluteTime(),true);

		String infosFichierBinaire = Messages.getString("VueInfosParcours.6")
				+ " " + infos.getNomFichierParcoursBinaire() + "\n"
				+ Messages.getString("VueInfosParcours.10") + " "
				+ distancetotaleSTR + " " + uniteDistance + "\n"
				+ getDistanceTotaleCorrigee()
				+ Messages.getString("VueInfosParcours.12") + " " + debutTime
				+ "\n";

		try {
			if (ActivatorData.getInstance().getPoolDonneesVues().get(
					"axeTpsCorrige")!=null && (Boolean) ActivatorData.getInstance().getPoolDonneesVues().get(
							"axeTpsCorrige")) {

				String tempsCorrigDeb = "";
				if( allMessages.get(0).getVariable(TypeRepere.tempsCorrigee.getCode())!=null){
					tempsCorrigDeb = new String((byte[])allMessages.get(0).getVariable(TypeRepere.tempsCorrigee.getCode()).getValeur());
				}
				infosFichierBinaire = infosFichierBinaire + Messages.getString("VueInfosParcours.21") + " "	+ tempsCorrigDeb + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		infosFichierBinaire = infosFichierBinaire+ Messages.getString("VueInfosParcours.14") + " " + finalTime+ "\n";

		try {
			if (ActivatorData.getInstance().getPoolDonneesVues().get("axeTpsCorrige")!=null && (Boolean) ActivatorData.getInstance().getPoolDonneesVues().get(
					"axeTpsCorrige")) {

				String tempsCorrigFin = "";
				//				SUPPR_SETTEMPSCORRIGE
				if(allMessages.get(allMessages.size() - 1).getVariable(TypeRepere.tempsCorrigee.getCode())!=null){
					if(allMessages.get(allMessages.size() - 1).getVariable(TypeRepere.tempsCorrigee.getCode()).getValeur()!=null){
						tempsCorrigFin = new String ((byte[])allMessages.get(allMessages.size() - 1).getVariable(TypeRepere.tempsCorrigee.getCode()).getValeur());
					}
				}
				infosFichierBinaire = infosFichierBinaire+ Messages.getString("VueInfosParcours.22") + " "+ tempsCorrigFin + "\n";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		infosFichierBinaire = infosFichierBinaire+ Messages.getString("VueInfosParcours.19") + " "+ dureeRec.toString() + "\n";

		String infosParcoursChaine = Messages.getString("VueInfosParcours.1")+ "\n";
		int codeVar = 0;
		VariableComposite var = null;
		if (infosParcours != null) {
			for (String info : infosParcours.getListeInformations()) {				
				if (GestionnairePool.getInstance().getVariable(info) != null) {
					AVariableComposant varComp=GestionnairePool.getInstance().getVariable(info);
					codeVar = varComp.getDescriptor().getM_AIdentificateurComposant().getCode();
					String nom=Util.getInstance().getNomUtilisateurSiChaineVide(varComp);
					infosParcoursChaine += nom  + "\n"; 
					for (Message msg : allMessages) {
						if (msg.getVariable(codeVar) != null) {
							if (msg.getVariable(codeVar).getDescriptor().getTypeVariable() == TypeVariable.VAR_COMPLEXE) {
								var = (VariableComposite) msg.getVariable(codeVar);
								for (int i = 0; i < var.getVariableCount(); i++) {
									String value = var.getEnfant(i).toString();
									infosParcoursChaine += var.getEnfant(i).getDescriptor().getNomUtilisateur().getNomUtilisateur(
											Activator.getDefault().getCurrentLanguage())+ ": " + value + "\n";
								}
								infosParcoursChaine += "\n";
							} else
								infosParcoursChaine += msg.getVariable(codeVar)+ "\n";
						}
					}
					infosParcoursChaine += "\n";
				}
			}
		} else {
			infosParcoursChaine += Messages.getString("VueInfosParcours.5");
		}
		try{
			this.infoText.setText(infosFichierXml + "\n\n" + infosFichierBinaire + "\n\n" + infosParcoursChaine);
		}
		catch(Exception ex){

		}
	}

	/**
	 * 
	 */
	public void definirAnnotationParcours() {
		InfosFichierSamNg infos;
		try{
			infos = (InfosFichierSamNg)FabriqueParcours.getInstance().getParcours().getInfo();
		}catch(Exception ex){
			infos = ((InfosFichierSamNg) GestionnairePool.getInstance().getXMLParser().getInfosFichier());
		}	
		GestionnaireMarqueurs gestMark = new GestionnaireMarqueurs();
		gestMark.chargerMarqueurs(infos.getNomFichierParcoursBinaire());
		if (gestMark.chargerAnnotationParcours() != null)
			this.annotationText.setText(gestMark.chargerAnnotationParcours());
	}

	@Override
	public void dispose() {
		if (ActivatorData.getInstance().isMultimediaFileAlone()) {			
			return;
		}
		
		this.backgroundColor.dispose();
		super.dispose();
	}

	public Composite getContenu() {
		return this.mainComposite;
	}

	public void onRepereAdded(TypeRepere... reper) {
		definirTexteInfosParcours();
	}

	public void onRepereRemoved(TypeRepere... reper) {
		definirTexteInfosParcours();
	}

	private String getDistanceTotaleCorrigee(){

		String distanceTotaleCorrigeeSTR ="";
		if((ActivatorData.getInstance().getPoolDonneesVues().get(
				"axeDistanceCorrige")!=null && (Boolean) ActivatorData.getInstance().getPoolDonneesVues().get("axeDistanceCorrige"))){
			String uniteDistance ="";
			try {
				uniteDistance = GestionnaireDescripteurs.getDescripteurVariableAnalogique(TypeRepere.distance.getCode()).getUnite();
			} catch (Exception ex) {

			}
			int codeDistanceCorrigee = TypeRepere.distanceCorrigee.getCode();
			HashMap<Integer, SegmentDistance> listeSegmentsDistance = TableSegments.getInstance().getSegmentsDistance();
			Message msgDebutSegment = null;
			Message msgFinSegment = null;
			double distanceCorrige=0;
			double distance1 = 0;
			double distance2 =0;
			for (SegmentDistance segment : listeSegmentsDistance.values()) {
				if(segment.isValide()){
					msgDebutSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segment.getStartMsgId());
					msgFinSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segment.getEndMsgId());
					if(segment.getInitialDiameter()!=segment.getDiameterCorrige()){
						distance1 = Double.parseDouble(msgDebutSegment.getVariable(codeDistanceCorrigee).toString());
						distance2 = Double.parseDouble(msgFinSegment.getVariable(codeDistanceCorrigee).toString());

					}else{
						distance1 = msgDebutSegment.getAbsoluteDistance();
						distance2 = msgFinSegment.getAbsoluteDistance();
					}
					distanceCorrige += (distance2-distance1);
				}
			}
			distanceTotaleCorrigeeSTR = Messages.getString("VueInfosParcours.17")+ distanceCorrige+  " " + uniteDistance + "\n";
		}
		return distanceTotaleCorrigeeSTR;
	}

	public void onDataChange() {
		if(((InfosFichierSamNg) FabriqueParcours.getInstance().getParcours().getInfo())!=null){
			definirTexteInfosParcours();
			try{
				definirAnnotationParcours();
			}
			catch(Exception ex){

			}
		}

	}


	private static HashMap<String, Object> calculerTempsEtDistanceCumulee(int msgStartId, int msgEndId) throws Exception {

		int codeTpsAvtChgt = TypeRepere.tempsAvantChangement.getCode();
		int codeTpsRel = TypeRepere.tempsRelatif.getCode();
		int codeDistRel = TypeRepere.distanceRelatif.getCode();
		int codeTemps = TypeRepere.temps.getCode();
		int codeDateAvtChgt = TypeRepere.dateAvantChangement.getCode();
		int codeDate = TypeRepere.date.getCode();

		ParseurParcoursBinaire parseur = TypeParseur.getInstance().getParser();
		double distanceCumulee=0;
		long tempsCumule =0;
		int lastStartId = 0;
		int lastEndId = 0;

		ListMessages listeMessages = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getMessages();
		// get the reference time and distance
		Message m = listeMessages.getMessageById(msgStartId);
		if(parseur instanceof ParseurParcoursTomUk||parseur instanceof ParseurParcoursTomDIS || parseur instanceof ParseurParcoursTomHSBC){

			double refDist = 0;
			int startId = 0;
			int endId = 0;

			AVariableComposant relVar = null;

			startId=msgStartId;
			endId=msgEndId;

			long millisAvecChgt = 0;
			Hashtable<Integer, Long> decalageHashMap = new Hashtable<Integer, Long>();
			// parcours des messages pour récupérer les décalages relatifs aux
			// changements d'heures
			long decalageTps = 0;
			// this will return a copy of the variable temp and we change the
			// descriptor for it
			relVar = GestionnairePool.getInstance().getVariable(TypeRepere.tempsRelatif.getCode());

			for (Message msg :listeMessages) {

				if (relVar != null) {

					if (msg.getEvenement().isChangementHeure()) {
						ConversionTemps tc = new ConversionTempsTom4(
								"01/01/1990 00:00:00.000");

						double newDateValue = ConversionTemps
								.getDateFromTypeRepereDate(msg, TypeRepere.date);
						long newTempsValue = ConversionTemps
								.getTempsFromTypeRepereDate(msg,
										TypeRepere.date);

						tc.addDate(newDateValue);
						tc.addTime(newTempsValue);
						long millisSansChgt = tc.getCurrentDateAsMillis();

						long temps1 = ConversionTemps
								.getTempsFromTypeRepereDate(msg,
										TypeRepere.dateAvantChangement);
						double date1 = ConversionTemps
								.getDateFromTypeRepereDate(msg,
										TypeRepere.dateAvantChangement);

						tc.addDate(date1);
						tc.addTime(temps1);
						millisAvecChgt = tc.getCurrentDateAsMillis();

						// calcul du décalage lors d'un changement d'heure

						decalageTps = millisSansChgt - millisAvecChgt;

						decalageHashMap.put(msg.getMessageId(), decalageTps);
					}

				}

			}

			long decalageTmp = 0;
			for (Integer idMessage : decalageHashMap.keySet()) {

				if (m.getMessageId() > idMessage) {
					decalageTmp += decalageHashMap.get(idMessage);
				}

				else {
					decalageTmp -= decalageHashMap.get(idMessage);
				}
			}

			Message lastMsg = null;
			long tmpRelatif = 0;

			long deltaTempsRef0 = 0;
			long deltaTempsTempo = 0;
			long tampondeltaTempsTempo = 0;

			long date = -1;
			long dateLastMsg = -1;
			Hashtable<Integer, Long> deltaTempsMsgs = new Hashtable<Integer, Long>();

			// variables temporaires
			long tamponDate = -1;
			long tamponTemps = 0;
			long tempsLastMsg = 0;
			long temps = 0;
			long compteurTemps = 0;
			long compteurTempsLastMsg = 0;
			long tamponCptTemps = 0;
			long tamponCptTempsLastMsg = 0;
			long firstCptTemps = 0;
			long cumulTempsMax = 0;
			long lastHeureValue = 0;

			for (Message msg2 :listeMessages) {


				if (msg2.getMessageId() < startId) {
					if (lastStartId < startId) {


						continue;
					}
				}

				if (msg2.getMessageId() > endId) {
					if (lastEndId > endId) {

						continue;
					}
					break;

				}

				relVar = GestionnairePool.getInstance().getVariable(TypeRepere.tempsRelatif.getCode());

				if (relVar != null) {
					try {
						if (!msg2.getEvenement().isChangementHeure()) {
							if (lastMsg == null) {
								deltaTempsTempo = 0;

							} else {


								if (lastMsg.getVariable(TypeRepere.date
										.getCode()) != null) {

									if(lastMsg.getEvenement().isRuptureAcquisition()){
										dateLastMsg = ConversionTemps
												.getDateFromTypeRepereDate(lastMsg,
														TypeRepere.date);

										tempsLastMsg = ConversionTemps
												.getTempsFromTypeRepereDate(
														lastMsg, TypeRepere.date);

									}
									else {
										if (lastMsg.getVariable(TypeRepere.temps
												.getCode()) != null) {

											compteurTempsLastMsg = (Long) lastMsg
													.getVariable(
															TypeRepere.temps
															.getCode())
															.getCastedValeur()
															* ConstantesParcoursTom4.pasCptTps
															* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
											tamponCptTempsLastMsg = compteurTempsLastMsg;
										}else{

											if(lastMsg.getVariable(TypeRepere.tempsAvantChangement
													.getCode()) != null){

												tamponCptTempsLastMsg = 0;
											}else
												compteurTempsLastMsg = tamponCptTempsLastMsg;
										}
										dateLastMsg = tamponDate;
									}

									if (lastMsg.getVariable(TypeRepere.temps
											.getCode()) != null) {

										compteurTempsLastMsg = (Long) lastMsg
												.getVariable(
														TypeRepere.temps
														.getCode())
														.getCastedValeur()
														* ConstantesParcoursTom4.pasCptTps
														* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
										tamponCptTempsLastMsg = compteurTempsLastMsg;
									} else {

										compteurTempsLastMsg = tamponCptTempsLastMsg;
									}

								} else {

									if(lastMsg.getEvenement().isRazCompteurTemps()){

										compteurTempsLastMsg = 0;
										tamponCptTempsLastMsg = compteurTempsLastMsg;
									}


									if (lastMsg.getVariable(TypeRepere.temps
											.getCode()) != null) {

										compteurTempsLastMsg = (Long) lastMsg
												.getVariable(
														TypeRepere.temps
														.getCode())
														.getCastedValeur()
														* ConstantesParcoursTom4.pasCptTps
														* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
										tamponCptTempsLastMsg = compteurTempsLastMsg;
									} else {
										if(lastMsg.getVariable(TypeRepere.tempsAvantChangement
												.getCode()) != null){

											tamponCptTempsLastMsg = 0;
										}else{

											compteurTempsLastMsg = tamponCptTempsLastMsg;
										}

									}

									dateLastMsg = tamponDate;
								}





								if(msg2.getEvenement().isRazCompteurTemps()){

									compteurTemps = 0;
									tamponCptTemps = compteurTemps;
								}

								else{


									if (msg2.getVariable(TypeRepere.date.getCode()) != null) {


										// nombre de jours depuis date pivot
										if(msg2.getEvenement().isRuptureAcquisition()){
											date = ConversionTemps
													.getDateFromTypeRepereDate(msg2,
															TypeRepere.date);
											//										 nombre de milliseconde depuis le début de
											// la journée de la variable date
											temps = ConversionTemps
													.getTempsFromTypeRepereDate(msg2,
															TypeRepere.date);
											lastHeureValue = temps;
										}
										else{
											if(msg2.getVariable(TypeRepere.temps
													.getCode()) != null){
												compteurTemps = (Long) msg2.getVariable(
														TypeRepere.temps.getCode())
														.getCastedValeur()
														* ConstantesParcoursTom4.pasCptTps
														* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

												tamponCptTemps = compteurTemps;
											}
											else{
												compteurTemps = tamponCptTemps;
											}
											date = tamponDate;
										}

										// récupération de la valeur du compteur de
										// temps
										if (msg2.getVariable(TypeRepere.temps
												.getCode()) != null) {

											compteurTemps = (Long) msg2.getVariable(
													TypeRepere.temps.getCode())
													.getCastedValeur()
													* ConstantesParcoursTom4.pasCptTps
													* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);

											tamponCptTemps = compteurTemps;
										} else {
											compteurTemps = tamponCptTemps;
										}

									} else {

										if (msg2.getVariable(TypeRepere.temps
												.getCode()) != null) {
											compteurTemps = (Long) msg2.getVariable(
													TypeRepere.temps.getCode())
													.getCastedValeur()
													* ConstantesParcoursTom4.pasCptTps
													* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
											tamponCptTemps = compteurTemps;
										} else {
											compteurTemps = tamponCptTemps;
										}

										date = tamponDate;
									}
								}


								// si l'on change de date, on doit incrémenter
								// le compteur/décrémenter le compteur
								// d'autant de jour
								// il faut que la date soit valorisée
								if (date != -1 && dateLastMsg != -1
										&& (date != dateLastMsg))
									temps += (date - dateLastMsg)
									* (24 * 3600 * 1000);

								if(temps==0 && tempsLastMsg>0)
									temps = tempsLastMsg;

								if (msg2.getVariable(TypeRepere.date.getCode()) != null) {

									deltaTempsTempo = deltaTempsTempo + temps
											- tempsLastMsg + compteurTemps
											- compteurTempsLastMsg
											- firstCptTemps;
								} 
								else if(msg2.getEvenement().isRazCompteurTemps()){

									//									cumulTempsMax += ConstantesParcoursTom4.pasCptTps
									//									* (long) (ConstantesParcoursTom4.resolutionTemps * 1000)
									//									* ConstantesParcoursTom4.maxCptTps;
									deltaTempsTempo = deltaTempsTempo + temps
											- tempsLastMsg + ConstantesParcoursTom4.pasCptTps
											* (long) (ConstantesParcoursTom4.resolutionTemps * 1000)
											* ConstantesParcoursTom4.maxCptTps
											- compteurTempsLastMsg;

								}

								else {


									// on calcul le delta temps
									// deltaTempsCumul + temps message courant -
									// temps message précédent
									deltaTempsTempo = deltaTempsTempo + temps
											- tempsLastMsg + compteurTemps
											- compteurTempsLastMsg;
								}



								// on met en mémoire les valeurs des variables
								// temps
								tamponDate = date;
								tamponTemps = temps;
							}

						} else {



							if (lastMsg.getVariable(TypeRepere.temps
									.getCode()) != null) {

								compteurTempsLastMsg = (Long) lastMsg
										.getVariable(
												TypeRepere.temps
												.getCode())
												.getCastedValeur()
												* ConstantesParcoursTom4.pasCptTps
												* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
								tamponCptTempsLastMsg = compteurTempsLastMsg;



							}else if(lastMsg.getVariable(TypeRepere.tempsAvantChangement
									.getCode()) != null){
								compteurTempsLastMsg = 0;

							}
							else

								compteurTempsLastMsg = tamponCptTempsLastMsg;



							//pour les fichiers hsbc, la variable temps n'est pas valorisée mais  la variable tempsAvantChangement est valorisée et on doit l'utiliser en guise de compteur temps
							if (msg2.getVariable(TypeRepere.tempsAvantChangement
									.getCode()) != null) {
								compteurTemps = new Long(msg2.getVariable(
										TypeRepere.tempsAvantChangement.getCode()).toString())
								* (long)ConstantesParcoursTom4.pasCptTps
								* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
								tamponCptTemps = compteurTemps;
							} else {
								//pour les fichiers uk, il n'y a pas de tempsAvantChangement, mais la variable temps est elle valorisé
								if (msg2.getVariable(TypeRepere.temps
										.getCode()) != null) {
									compteurTemps = (Long) msg2.getVariable(
											TypeRepere.temps.getCode())
											.getCastedValeur()
											* ConstantesParcoursTom4.pasCptTps
											* (long) (ConstantesParcoursTom4.resolutionTemps * 1000);
									tamponCptTemps = compteurTemps;
								} else {
									compteurTemps = tamponCptTemps;
								}
							}

							date = tamponDate;




							deltaTempsTempo = deltaTempsTempo+ compteurTemps - compteurTempsLastMsg;

						}

					} catch (Exception e) {
						deltaTempsTempo = tampondeltaTempsTempo;
					}


					deltaTempsMsgs.put(new Integer(msg2.getMessageId()),
							new Long(deltaTempsTempo));
					tampondeltaTempsTempo = deltaTempsTempo;
				}

				if (msg2.getMessageId() == m.getMessageId()) {
					deltaTempsRef0 = deltaTempsTempo;
				}
				lastMsg = msg2;
			}


			for (Message msg1 : listeMessages) {

				// the messages are ordonated and i should move forward to get
				// into
				// the segment
				if (msg1.getMessageId() < startId) {

					continue;
				}
				// the message is after the segment. not interesting
				if (msg1.getMessageId() > endId) {
					if (lastEndId > endId) {

						continue;
					}
					break;

				}

				// this will return a copy of the variable temp and we change
				// the
				// descriptor for it
				relVar = GestionnairePool.getInstance().getVariable(TypeRepere.tempsRelatif.getCode());
				if (relVar != null) {
					// pour le message ou l'on
					if (msg1 == m) {
						tmpRelatif = 0;
					} else {

						if (!msg1.getEvenement().isChangementHeure()) {
							if (lastMsg == null) {
								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg1.getMessageId())))
										- deltaTempsRef0;

							} else {
								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg1.getMessageId())))
										- deltaTempsRef0;
							}

						} else {

							long tempsAvtChgt = ConversionTemps
									.getTempsFromTypeRepereDate(msg1,
											TypeRepere.dateAvantChangement);
							long dateAvtChgt = ConversionTemps
									.getDateFromTypeRepereDate(msg1,
											TypeRepere.dateAvantChangement);

							ConversionTemps tc = new ConversionTempsTom4(
									"01/01/1990 00:00:00.000");

							tc.addDate(dateAvtChgt);
							tc.addTime(tempsAvtChgt);

							if (lastMsg == null) {

								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg1.getMessageId())))
										- deltaTempsRef0;
							} else {

								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(msg1.getMessageId())))
										- deltaTempsRef0;
							}

						}

					}
					tempsCumule = tmpRelatif;


				} else {
					throw new RuntimeException(Messages
							.getString("ReferenceAction.3"));
				}

				relVar = GestionnairePool.getInstance()
						.getVariable(TypeRepere.distanceRelatif.getCode());
				if (relVar != null) {
					double dif = msg1.getAbsoluteDistance() - refDist;
					BigDecimal b = new BigDecimal(dif);
					b.setScale(6, RoundingMode.DOWN);

					DecimalFormat fmt = new DecimalFormat("0.000");
					String s = fmt.format(b.floatValue());
					distanceCumulee = b.floatValue();
				} else {
					throw new RuntimeException(Messages
							.getString("ReferenceAction.4"));
				}
				lastMsg = msg1;
			}

			lastStartId = startId;
			lastEndId = endId;



		}
		else if(parseur instanceof ParseurParcoursSamng){
			double refDist = 0;
			int startId = msgStartId;
			int endId = msgEndId;

			long millisAvecChgt = 0;
			Hashtable<Integer, Long> decalageHashMap = new Hashtable<Integer, Long>();
			// parcours des messages pour récupérer les décalages relatifs aux
			// changements d'heures
			long decalageTps = 0;
			// this will return a copy of the variable temp and we change the
			// descriptor for it
			AVariableComposant relVar = GestionnairePool.getInstance()
					.getVariable(codeTpsRel);


			for (Message msg : listeMessages) {

				if (relVar != null) {

					if (msg.getEvenement().isChangementHeure()) {
						ConversionTemps tc = new ConversionTempsTomNg(
								"01/01/2000 00:00:00.000");
						long temps = (Long) (msg.getVariable(codeTemps).getCastedValeur());

						long date = (Long) (msg.getVariable(codeDate).getCastedValeur());

						tc.addDate(date);
						tc.addTime(temps);
						long millisSansChgt = tc.getCurrentDateAsMillis();

						long temps1 = (Long) (msg
								.getVariable(codeTpsAvtChgt).getCastedValeur());

						long date1 = (Long) (msg
								.getVariable(codeDateAvtChgt).getCastedValeur());
						tc.addDate(date1);
						tc.addTime(temps1);
						millisAvecChgt = tc.getCurrentDateAsMillis();

						// calcul du décalage lors d'un changement d'heure

						decalageTps = millisSansChgt - millisAvecChgt;

						decalageHashMap.put(msg.getMessageId(), decalageTps);
					}

				}

			}

			long decalageTmp = 0;
			for (Integer idMessage : decalageHashMap.keySet()) {

				if (m.getMessageId() > idMessage) {
					decalageTmp += decalageHashMap.get(idMessage);
				}

				else {
					decalageTmp -= decalageHashMap.get(idMessage);
				}
			}

			Message lastMsg = null;
			long tmpRelatif = 0;
			long deltaTemps = 0;
			long deltaTempsRef0 = 0;
			long deltaTempsTempo = 0;
			long date = -1;
			long dateLastMsg = -1;
			Hashtable<Integer, Long> deltaTempsMsgs = new Hashtable<Integer, Long>();

			for (Message msg : listeMessages) {

				// the messages are ordonated and i should move forward to get into
				// the segment
				if (msg.getMessageId() < startId) {
					if (lastStartId < startId) {

					}

					continue;
				}
				// the message is after the segment. not interesting
				if (msg.getMessageId() > endId) {
					if (lastEndId > endId) {

						continue;
					}
					break;

				}

				// this will return a copy of the variable temp and we change the
				// descriptor for it
				relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);

				if (relVar != null) {

					if (!msg.getEvenement().isChangementHeure()) {

						if (lastMsg == null) {
							deltaTempsTempo = 0;

						} else {
							// récupération des date et temps du message courant
							long temps = (Long) (msg.getVariable(codeTemps).getCastedValeur());

							if (msg.getVariable(codeDate) != null) {
								date = (Long) (msg.getVariable(codeDate).getCastedValeur());
							}

							// récupération des date et temps du message précédent
							long tempsLastMsg = (Long) (lastMsg
									.getVariable(codeTemps)
									.getCastedValeur());

							if (lastMsg.getVariable(codeDate) != null) {
								dateLastMsg = (Long) (lastMsg
										.getVariable(codeDate)
										.getCastedValeur());
							}

							// si l'on change de date, on doit incrémenter le
							// compteur/décrémenter le compteur
							// d'autant de jour
							// il faut que la date soit valorisée
							if (date != -1 && dateLastMsg != -1
									&& (date == dateLastMsg + 1))
								temps += (date - dateLastMsg) * (24 * 3600 * 1000);

							// on calcul le delta temps
							// deltaTempsCumul + temps message courant - temps
							// message précédent
							deltaTempsTempo = deltaTempsTempo + temps
									- tempsLastMsg;

						}

					} else {

						long tempsAvtChgt = (Long) (msg
								.getVariable(codeTpsAvtChgt).getCastedValeur());

						long dateAvtChgt = (Long) (msg
								.getVariable(codeDateAvtChgt).getCastedValeur());

						ConversionTemps tc = new ConversionTempsTomNg(
								"01/01/2000 00:00:00.000");

						tc.addDate(dateAvtChgt);
						tc.addTime(tempsAvtChgt);

						if (lastMsg == null) {
							deltaTempsTempo = 0;

						} else {

							long tempsLastMsg = (Long) (lastMsg
									.getVariable(codeTemps)
									.getCastedValeur());

							if (lastMsg.getVariable(codeDate) != null) {
								dateLastMsg = (Long) (lastMsg
										.getVariable(codeDate)
										.getCastedValeur());
							}

							if (dateLastMsg != -1 && (dateLastMsg != dateAvtChgt))
								tempsAvtChgt += (dateAvtChgt - dateLastMsg)
								* (24 * 3600 * 1000);

							// deltaTempsCumul + temps message courant - temps
							// message précédent
							deltaTempsTempo = deltaTempsTempo + tempsAvtChgt
									- tempsLastMsg;

						}

					}
					deltaTempsMsgs.put(new Integer(msg.getMessageId()), new Long(
							deltaTempsTempo));
				}

				if (msg.getMessageId() == m.getMessageId()) {

					deltaTempsRef0 = deltaTempsTempo;

				}
				lastMsg = msg;
			}

			long lastTempRelatif = 0;
			int idMsg =0;
			long t =0;
			for (Message msg : listeMessages) {
				idMsg = msg.getMessageId();

				// the messages are ordonated and i should move forward to get into
				// the segment
				if (idMsg < startId) {
					if (lastStartId < startId) {}

					continue;
				}
				// the message is after the segment. not interesting
				if (idMsg > endId) {
					if (lastEndId > endId) {


						continue;
					}
					break;

				}

				// this will return a copy of the variable temp and we change the
				// descriptor for it
				relVar = GestionnairePool.getInstance().getVariable(codeTpsRel);
				if (relVar != null) {
					// pour le message ou l'on
					if (msg == m) {
						tmpRelatif = 0;


					} else {

						if (!msg.getEvenement().isChangementHeure()) {

							if (lastMsg == null) {

								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(idMsg)))
										- deltaTempsRef0;

							} else {

								tmpRelatif = ((Long) deltaTempsMsgs
										.get(new Integer(idMsg)))
										- deltaTempsRef0;

							}

						} else {

							long tempsAvtChgt = (Long) (msg
									.getVariable(codeTpsAvtChgt).getCastedValeur());



							long dateAvtChgt = (Long) (msg
									.getVariable(codeDateAvtChgt).getCastedValeur());

							ConversionTemps tc = new ConversionTempsTomNg(
									"01/01/2000 00:00:00.000");
							tc.addDate(dateAvtChgt);
							tc.addTime(tempsAvtChgt);

							if (lastMsg == null) {
								deltaTemps = 0;
								tmpRelatif = ((Long) deltaTempsMsgs.get(new Integer(idMsg)))
										- deltaTempsRef0;
							} else {
								tmpRelatif = ((Long) deltaTempsMsgs.get(new Integer(idMsg)))
										- deltaTempsRef0;
							}
						}
					}

					tempsCumule = tmpRelatif;

				} else {
					throw new RuntimeException(Messages.getString("ReferenceAction.3"));
				}

				relVar = GestionnairePool.getInstance().getVariable(codeDistRel);
				if (relVar != null) {
					double dif = msg.getAbsoluteDistance() - refDist;
					dif = AVariableComposant.arrondir(dif, 3);
					distanceCumulee = dif;
				} else {
					throw new RuntimeException(Messages.getString("ReferenceAction.4"));
				}
				lastMsg = msg;
			}
			lastStartId = startId;
			lastEndId = endId;

		}else if(parseur instanceof ParseurParcoursAtess){

			CalculReferenceZeroAtess calcRef=new CalculReferenceZeroAtess();
			HashMap<String,Object> mapTempsDistanceCumules = calcRef.calculerTempsEtDistanceCumulee(msgStartId, msgEndId,0);

			tempsCumule = (Long)mapTempsDistanceCumules.get("tempsCumule");
			distanceCumulee = (Double)mapTempsDistanceCumules.get("distanceCumule");
		}

		HashMap<String,Object> mapTempsDistance= new HashMap<String, Object>();
		mapTempsDistance.put("tempsCumule",tempsCumule );
		mapTempsDistance.put("distanceCumule",distanceCumulee );

		return mapTempsDistance;
	}

	/**
	 * Méthode de calcul du cumul des temps et distance
	 * @return
	 * @throws Exception 
	 */
	public HashMap<String,Object> calculerTempsCumuleEtDistanceCumuleeCommeRefZero() throws Exception{

		double distanceCumulee= 0;
		long tempsCumule= 0;
		List<SegmentTemps> listeSegs = TableSegments.getInstance().classerSegmentsTemps();
		// creates the rows
		long diffTempsChgtHeure=0;
		for (SegmentTemps segTemps : listeSegs) {
			int startMsgId = segTemps.getStartMsgId();
			int endMsgId = segTemps.getEndMsgId();
			Message msgFinSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(startMsgId);
			Message msgDebutSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(endMsgId);
			HashMap<String,Object> mapTempsDistanceCumules = calculerTempsEtDistanceCumulee(startMsgId,endMsgId);

			tempsCumule+=(Long)mapTempsDistanceCumules.get("tempsCumule");
			distanceCumulee+=(Double)mapTempsDistanceCumules.get("distanceCumule");
			long tempAbsoluChgtHeure = 0;
			long tempAbsoluSegmentSuivant = 0;
			long tempAbsoluSegmentPrecedent = 0;

			ParseurAdapteur padapt=new ParseurAdapteur();
			diffTempsChgtHeure=padapt.gererCalculTempsParcoursChgtsHeure(diffTempsChgtHeure, tempAbsoluSegmentSuivant, tempAbsoluChgtHeure, msgDebutSegment, segTemps, msgFinSegment, tempAbsoluSegmentPrecedent);
		}

		HashMap<String,Object> hashMap = new HashMap<String, Object>();
		hashMap.put("tempsCumuleTotal",tempsCumule );
		hashMap.put("distanceCumuleeTotale",distanceCumulee );
		return hashMap;
	}
}