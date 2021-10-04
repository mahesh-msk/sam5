package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.dialogs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.sm.calculs.ConversionTemps;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariableAnalogique;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.ListMessages;
import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.GestionnaireZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.TypeZoom;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomComposite;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.zoom.ZoomX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Courbe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.Graphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.TypeGraphe;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeSegmentInfo;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.GestionnaireAxes;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class ManualZoomDialog extends Dialog {
	public static SimpleDateFormat FORMATER = new SimpleDateFormat(
			"dd/MM/yyyy HH:mm:ss.SSS");

	private String strDateFormatSample = Messages
			.getString("ManualZoomDialog.0"); //$NON-NLS-1$

	private String strDateParseError2 = Messages
			.getString("ManualZoomDialog.2") + //$NON-NLS-1$
			this.strDateFormatSample + "\""; //$NON-NLS-1$

	private String strNumberParseError = Messages
			.getString("ManualZoomDialog.4"); //$NON-NLS-1$

	private TabFolder tabFolder;

	private Composite zoomXPage;

	private Composite zoomYPage;

	private Composite zoomXButtonsComposite;

	private Label labelMinDate;

	private Text textMinDate;

	private Label labelMinDateFormat;

	private Label labelMaxDate;

	private Text textMaxDate;

	private Label labelMaxDateFormat;

	private Label labelMinValue;

	private Text textMinValue;

	private Label labelMaxValue;

	private Text textMaxValue;

	private String initialMinDateStr;

	private String initialMaxDateStr;

	private String initialMinValueStr;

	private String initialMaxValueStr;

	private double initialMinDistanceDouble;

	private double initialMaxDistanceDouble;
	
	private double initialMinDistanceParcoursDouble;
	private double initialMaxDistanceParcoursDouble;

	private long initialMinDateLong;

	private long initialMaxDateLong;

	private Label labelVariable;

	private Label labelMaxValueUm;

	private Label labelMinValueUm;

	private Combo comboVariable;

	private Composite zoomYButtonsComposite;

	private Button zoomXApplyButton;

	private Button zoomXCloseButton;

	private Button zoomXCancelButton;

	private Button zoomYApplyButton;

	private Button zoomYCloseButton;

	private Button zoomYCancelButton;

	protected ComboSelectionAdapter comboSelAdapter = new ComboSelectionAdapter();

	private TypeAxe typeAxe;

	/**
	 * ManualZoomDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 */
	public ManualZoomDialog(Shell parent) {
		// Pass the default styles here
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	/**
	 * ManualZoomDialog constructor
	 * 
	 * @param parent
	 *            the parent
	 * @param style
	 *            the style
	 */
	public ManualZoomDialog(Shell parent, int style) {
		// Let users override the default styles
		super(parent, style);
		setText(Messages.getString("ManualZoomDialog.6")); //$NON-NLS-1$
	}

	/**
	 * Opens the dialog and returns the input
	 * 
	 * @return String
	 */
	public void open() {
		// Create the dialog window
		Shell shell = new Shell(getParent(), getStyle());
		shell.setText(getText());
		createContents(shell);
		shell.pack();

		// set the size of the dialog
		shell.setSize(425, 170);
		// position the dialog in the center of the parent shell
		Rectangle parentBounds = getParent().getBounds();
		Rectangle childBounds = shell.getBounds();
		int x = parentBounds.x + (parentBounds.width - childBounds.width) / 2;
		int y = parentBounds.y + (parentBounds.height - childBounds.height) / 2;
		shell.setLocation(x, y);

		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
	}

	/**
	 * Creates the dialog's contents
	 * 
	 * @param shell
	 *            the dialog window
	 */
	private void createContents(final Shell shell) {
		shell.setLayout(new FillLayout());

		tabFolder = new TabFolder(shell, SWT.NONE);
		// is mandatory to create the panels here due to selection listener
		// that is called when panel zoom X is first added which in turns checks
		// for
		// zoom Y panel changes (which is not yet created)
		zoomXPage = createTabPage(tabFolder, Messages
				.getString("ManualZoomDialog.7")); //$NON-NLS-1$
		initZoomXPage(shell);
		zoomYPage = createTabPage(tabFolder, Messages
				.getString("ManualZoomDialog.8")); //$NON-NLS-1$
		initZoomYPage(shell);

		// Protocol selection event
		tabFolder.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (((TabItem) e.item).getControl() == zoomYPage) { // we
					// switched
					// from
					// zoomXPage
					// to
					// zoomYPage
					if (!checkZoomXChanges(false)) {
						tabFolder.setSelection(0); // restore selection on Y
						// Zoom Page
						e.doit = false; // This should work but it doesn't
						// (setSelection is the only that solves the problem)
						return;
					}
					initZoomYValues(); // init the values
				} else { // we switched from zoomYPage to zoomXPage
					if (!checkZoomYChanges(false)) {
						tabFolder.setSelection(1); // restore selection on Y
						// Zoom Page
						e.doit = false;
						return;
					}
					initZoomXValues(); // init the values
				}
			}
		});

		try {
			ActivatorData.getInstance().getPoolDonneesVues().get("ongletZoom")
					.equals("X");
		} catch (Exception e) {
			ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom", "X");
		}

		if (ActivatorData.getInstance().getPoolDonneesVues().get("ongletZoom")
				.toString().equals("Y")) {
			tabFolder.setSelection(1);
		} else {
			tabFolder.setSelection(0);
		}

		// Fill tabbed folder completely with content
		tabFolder.setLayout(new FillLayout());
	}

	private Composite createTabPage(TabFolder folder, String label) {
		// Create and label a new tab
		TabItem tab = new TabItem(folder, SWT.NONE);
		tab.setText(label);
		// Create a new page as a Composite instance
		Composite page = new Composite(folder, SWT.NONE);
		// ... and assign to tab
		tab.setControl(page);
		return page;
	}

	private void initZoomXPage(final Shell shell) {
		labelMinDate = new Label(zoomXPage, SWT.NONE);
		labelMinDate.setText(Messages.getString("ManualZoomDialog.9")); //$NON-NLS-1$
		labelMinDate.setToolTipText(Messages.getString("ManualZoomDialog.9"));
		textMinDate = new Text(zoomXPage, SWT.BORDER);
		textMinDate.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// restore color to black in the case we had a parse error
				textMinDate.setForeground(getParent().getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
				boolean isSame = textMinDate.getText()
						.equals(initialMinDateStr);
				if (!isSame) {
					try {
						double curValue = getZoomXValueFromString(textMinDate
								.getText());
						double initialValue = getZoomXValueFromString(initialMinDateStr);
						if (curValue == initialValue)
							isSame = true;
					} catch (Exception ex) {
					}
				}
				zoomXApplyButton.setEnabled(!isSame);
			}
		});

		labelMinDateFormat = new Label(zoomXPage, SWT.NONE);
		// labelMinDateFormat.setText("DD/MM/YYYY 00h00mn00s0"); //no need for
		// this ... management is made in another place

		labelMaxDate = new Label(zoomXPage, SWT.NONE);
		labelMaxDate.setText(Messages.getString("ManualZoomDialog.10")); //$NON-NLS-1$
		labelMaxDate.setToolTipText(Messages.getString("ManualZoomDialog.10"));
		textMaxDate = new Text(zoomXPage, SWT.BORDER);
		textMaxDate.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// restore color to black in the case we had a parse error
				textMaxDate.setForeground(getParent().getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
				boolean isSame = textMaxDate.getText()
						.equals(initialMaxDateStr);
				if (!isSame) {
					try {
						double curValue = getZoomXValueFromString(textMaxDate
								.getText());
						double initialValue = getZoomXValueFromString(initialMaxDateStr);
						if (curValue == initialValue)
							isSame = true;
					} catch (Exception ex) {
					}
				}
				zoomXApplyButton.setEnabled(!isSame);
			}
		});
		textMaxDate.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				AxeX currentXAxe = GestionnaireAxes.getInstance()
						.getCurrentAxeX();

				if (currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS
						|| currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {

					long minActuel = ConversionTemps.getMillis(textMinDate
							.getText(), true);

					long initialMin = ManualZoomDialog.this.initialMinDateLong;
					if (minActuel < initialMin) {
						textMinDate.setText(initialMinDateStr);
						textMinDate.setToolTipText(initialMinDateStr);

					}
				} else {

					double minActuel;
					try {
						minActuel = getZoomXValueFromString(textMinDate
								.getText());
						
						//correction issue 750 : Saisie Zoom Mini Zoom Maxi
						// on doit regarder la distance minimum de tout le parcours pour valider la distance minimum saisie
						
						//double initialMin = ManualZoomDialog.this.initialMinDistanceDouble;
						double initialMin = ManualZoomDialog.this.initialMinDistanceParcoursDouble;
						
						if (minActuel < initialMin) {
							textMinDate.setText(String
									.valueOf(initialMinDistanceParcoursDouble));
							textMinDate.setToolTipText(String
									.valueOf(initialMinDistanceParcoursDouble));
						}
					} catch (NumberFormatException e1) {
						MessageBox msgBox = new MessageBox(
								ManualZoomDialog.this.getParent(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setMessage(strNumberParseError);

						msgBox.open();
						textMinDate.setText(String
								.valueOf(initialMinDistanceDouble));
						textMinDate.setToolTipText(String
								.valueOf(initialMinDistanceDouble));
					} catch (ParseException e1) {

					}

				}

			}

			public void focusLost(FocusEvent e) {

				AxeX currentXAxe = GestionnaireAxes.getInstance()
						.getCurrentAxeX();

				if (currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS
						|| currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
					try {
						ConversionTemps.getMillis(textMaxDate.getText(), true);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} else {

					try {
						getZoomXValueFromString(textMaxDate.getText());

					} catch (NumberFormatException e1) {
						MessageBox msgBox = new MessageBox(
								ManualZoomDialog.this.getParent(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setMessage(strNumberParseError);

						msgBox.open();
						textMaxDate.setText(String
								.valueOf(initialMaxDistanceDouble));
						textMaxDate.setToolTipText(String
								.valueOf(initialMaxDistanceDouble));
					} catch (ParseException e1) {

					}

				}

			}

		});
		textMinDate.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				AxeX currentXAxe = GestionnaireAxes.getInstance()
						.getCurrentAxeX();

				if (currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS
						|| currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {

					long maxActuel = ConversionTemps.getMillis(textMaxDate
							.getText(), true);

					long initialMax = ManualZoomDialog.this.initialMaxDateLong;
					if (maxActuel > initialMax) {
						textMaxDate.setText(initialMaxDateStr);
						textMaxDate.setToolTipText(initialMaxDateStr);

					}
				} else {

					double maxActuel;
					try {
						maxActuel = getZoomXValueFromString(textMaxDate
								.getText());
						
						//correction issue 750 : Saisie Zoom Mini Zoom Maxi
						// on doit regarder la distance maximum de tout le parcours pour valider la distance minimum saisie
						//double initialMax = ManualZoomDialog.this.initialMaxDistanceDouble;
						
						double initialMax = ManualZoomDialog.this.initialMaxDistanceParcoursDouble;
						
						if (maxActuel > initialMax) {
							textMaxDate.setText(String
									.valueOf(initialMaxDistanceParcoursDouble));
							textMaxDate.setToolTipText(String
									.valueOf(initialMaxDistanceParcoursDouble));
						}
					} catch (NumberFormatException e1) {
						MessageBox msgBox = new MessageBox(
								ManualZoomDialog.this.getParent(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setMessage(strNumberParseError);

						msgBox.open();
						textMaxDate.setText(String
								.valueOf(initialMaxDistanceDouble));
						textMaxDate.setToolTipText(String
								.valueOf(initialMaxDistanceDouble));
					} catch (ParseException e1) {

					}

				}

			}

			public void focusLost(FocusEvent e) {

				AxeX currentXAxe = GestionnaireAxes.getInstance()
						.getCurrentAxeX();

				if (currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS
						|| currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
					try {
						ConversionTemps.getMillis(textMinDate.getText(), true);
					} catch (Exception ex) {
						ex.printStackTrace();
					}

				} else {

					try {
						getZoomXValueFromString(textMinDate.getText());

					} catch (NumberFormatException e1) {
						MessageBox msgBox = new MessageBox(
								ManualZoomDialog.this.getParent(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setMessage(strNumberParseError);

						msgBox.open();
						textMinDate.setText(String
								.valueOf(initialMinDistanceDouble));
						textMinDate.setToolTipText(String
								.valueOf(initialMinDistanceDouble));
					} catch (ParseException e1) {

					}

				}

			}

		});
		labelMaxDateFormat = new Label(zoomXPage, SWT.NONE);
		// labelMaxDateFormat.setText("DD/MM/YYYY 00h00mn00s0"); //no need for
		// this ... management is made in another place

		zoomXButtonsComposite = new Composite(zoomXPage, SWT.NONE);
		zoomXButtonsComposite.setLayout(new RowLayout());

		zoomXApplyButton = new Button(zoomXButtonsComposite, SWT.PUSH);
		this.zoomXApplyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (!checkZoomXChanges(true)) {
					return;
				} else {
					AxeX currentXAxe = GestionnaireAxes.getInstance()
							.getCurrentAxeX();
					if (currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS
							|| currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {

						try {
							ManualZoomDialog.this.initialMinDateLong = (long) getZoomXValueFromString(textMinDate
									.getText());
							ManualZoomDialog.this.initialMaxDateLong = (long) getZoomXValueFromString(textMaxDate
									.getText());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					} else {
						try {
							ManualZoomDialog.this.initialMinDistanceDouble = getZoomXValueFromString(textMinDate
									.getText());
							ManualZoomDialog.this.initialMaxDistanceDouble = getZoomXValueFromString(textMaxDate
									.getText());
						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				// here disable only the apply button
				zoomXApplyButton.setEnabled(false);
			}
		});
		zoomXApplyButton.setText(Messages.getString("ManualZoomDialog.11")); //$NON-NLS-1$
		zoomXApplyButton.setToolTipText(Messages
				.getString("ManualZoomDialog.11"));

		zoomXCancelButton = new Button(zoomXButtonsComposite, SWT.PUSH);
		this.zoomXCancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom",
						"X");
				shell.close();
			}
		});
		zoomXCancelButton.setText(Messages.getString("ManualZoomDialog.12")); //$NON-NLS-1$
		zoomXCancelButton.setToolTipText(Messages
				.getString("ManualZoomDialog.12"));

		zoomXCloseButton = new Button(zoomXButtonsComposite, SWT.PUSH);
		zoomXCloseButton.setText(Messages.getString("ManualZoomDialog.13")); //$NON-NLS-1$
		zoomXCloseButton.setToolTipText(Messages
				.getString("ManualZoomDialog.13"));
		this.zoomXCloseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {

				boolean isTimeAxis = false;
				TypeAxe typeAxe = GestionnaireAxes.getInstance()
						.getCurrentAxeType();
				if (typeAxe == TypeAxe.AXE_TEMPS
						|| typeAxe == TypeAxe.AXE_TEMPS_CORRIGE)
					isTimeAxis = true;

				// test de la validité de la saisie de la valeur minimum
				try {
					getZoomXValueFromString(textMinDate.getText());

				} catch (ParseException e) {

					String strErr = textMinDate.getText() + strDateParseError2;
					if (!isTimeAxis) {
						strErr = strNumberParseError;
					}
					showFieldValidationErrorMsgBox(textMinDate, strErr);

					// Auto-generated catch block
					e.printStackTrace();
				}

				// test de la validité de la saisie de la valeur maximum
				try {
					getZoomXValueFromString(textMaxDate.getText());
				} catch (ParseException e) {

					String strErr = textMaxDate.getText() + strDateParseError2;
					if (!isTimeAxis) {
						strErr = strNumberParseError;
					}
					showFieldValidationErrorMsgBox(textMaxDate, strErr);

					// Auto-generated catch block
					e.printStackTrace();
				}

				// test de vérification des changements de valeurs
				if (areValeursChangees(TypeZoom.ZOOM_X)) {

					// boolean
					// res=showFieldConfirmMsgBox(Messages.getString("ManualZoomDialog.43"),
					// Messages.getString("ManualZoomDialog.42"));
					// if(res)
					// applyZoomX(dateMin, dateMax);
					checkZoomXChanges(false);

				}

				ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom",
						"X");
				shell.close();
			}
		});
		zoomXCloseButton.setSelection(true);

		/* Use a GridLayout to position the controls */
		// Monitor monitor = zoomXPage.getMonitor();
		// int width = monitor.getClientArea().width / 7;
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = layout.marginHeight = 9;
		zoomXPage.setLayout(layout);

		GridData labelMinDateData = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		labelMinDate.setLayoutData(labelMinDateData);

		GridData textMinDateData = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		// textMinDateData.widthHint = width;
		textMinDate.setLayoutData(textMinDateData);

		GridData labelMinDateFormatData = new GridData(SWT.FILL, SWT.CENTER,
				false, false);
		labelMinDateFormat.setLayoutData(labelMinDateFormatData);

		GridData labelMaxDateData = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		labelMaxDate.setLayoutData(labelMaxDateData);

		GridData textMaxDateData = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		// textMaxDateData.widthHint = width;
		textMaxDate.setLayoutData(textMaxDateData);

		GridData labelMaxDateFormatData = new GridData(SWT.FILL, SWT.CENTER,
				false, false);
		labelMaxDateFormat.setLayoutData(labelMaxDateFormatData);

		GridData groupData = new GridData(SWT.CENTER, SWT.TOP, true, false, 4,
				1);
		zoomXButtonsComposite.setLayoutData(groupData);

		initZoomXValues();
	}

	private void initZoomXValues() {

		int distCode = TypeRepere.distance.getCode();
		String uniteDistance = "";
		try {
			DescripteurVariableAnalogique dist = GestionnaireDescripteurs
					.getDescripteurVariableAnalogique(distCode);
			uniteDistance = ((DescripteurVariableAnalogique) dist).getUnite();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		// private String uniteDistance=
		if (uniteDistance == null)
			uniteDistance = "";

		AxeX currentXAxe = GestionnaireAxes.getInstance().getCurrentAxeX();
		List<AxeSegmentInfo> listInfoSegments = currentXAxe.getInfoSegments();
		AxeSegmentInfo firstSegmentInfo = listInfoSegments.get(0);
		AxeSegmentInfo lastSegmentInfo = listInfoSegments.get(listInfoSegments
				.size() - 1);
//		 récupération uniquement des bons messages
		ListMessages messages = ActivatorData.getInstance().getVueData()
				.getDataTable().getEnregistrement().getMessages();
		if (currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS
				|| currentXAxe.m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
			labelMinDateFormat.setText(strDateFormatSample);
			labelMinDateFormat.setToolTipText(strDateFormatSample);
			labelMaxDateFormat.setText(strDateFormatSample);
			labelMaxDateFormat.setToolTipText(strDateFormatSample);
			long minDate = (long) firstSegmentInfo.getMinValue();
			long maxDate = (long) lastSegmentInfo.getMaxValue();
			initialMinDateLong = minDate;
			initialMaxDateLong = maxDate;
//			récupération des valeurs min et max du dernier zoom
			if (GestionnaireZoom.getZoomCourant() != null
					&& ((ZoomComposite) GestionnaireZoom.getZoomCourant())
							.getEnfant(0) != null) {
				ZoomX currentZoomX = (ZoomX) ((ZoomComposite) GestionnaireZoom
						.getZoomCourant()).getEnfant(0);
				//if(currentZoomX.getTypeAxe()==currentXAxe.m_TypeAxe){
					initialMinDateLong = (long)messages.getMessageByIdExact(currentZoomX.getFirstVisibleMsgId()).getAbsoluteTime();
					initialMaxDateLong = (long)messages.getMessageByIdExact(currentZoomX.getLastVisibleMsgId()).getAbsoluteTime();
				//}
			}
			initialMinDateStr = ConversionTemps.getFormattedDate(initialMinDateLong, true);// sdf.format(new
			// Date(minDate));
			initialMaxDateStr = ConversionTemps.getFormattedDate(initialMaxDateLong, true);// sdf.format(new
			// Date(maxDate));
			textMinDate.setText(initialMinDateStr);
			textMinDate.setToolTipText(initialMinDateStr);
			textMaxDate.setText(initialMaxDateStr);
			textMaxDate.setToolTipText(initialMaxDateStr);

		} else {
			

			

			// récupération de tous les messages
			// List<Message> messages =
			// this.data.getDataTable().getEnregistrement(0)
			// .getMessages();
			
			initialMinDistanceDouble = AVariableComposant.arrondir(messages.get(0).getCumulDistance(),3);
			initialMaxDistanceDouble = AVariableComposant.arrondir(messages.get(messages.size() - 1)
					.getCumulDistance(),3);
			
			initialMinDistanceParcoursDouble = AVariableComposant.arrondir(messages.get(0).getCumulDistance(),3);
			initialMaxDistanceParcoursDouble = AVariableComposant.arrondir(messages.get(messages.size() - 1)
					.getCumulDistance(),3);
			
			//récupération des valeurs min et max du dernier zoom
			if (GestionnaireZoom.getZoomCourant() != null
					&& ((ZoomComposite) GestionnaireZoom.getZoomCourant())
							.getEnfant(0) != null) {
				ZoomX currentZoomX = (ZoomX) ((ZoomComposite) GestionnaireZoom
						.getZoomCourant()).getEnfant(0);
//				if(currentZoomX.getTypeAxe()==currentXAxe.m_TypeAxe){
//				
//				initialMinDistanceDouble = AVariableComposant.arrondir(currentZoomX.getFirstXValue(),3);
//				initialMaxDistanceDouble = AVariableComposant.arrondir(currentZoomX.getLastXValue(),3);
//				
//				}else{
					initialMinDistanceDouble = AVariableComposant.arrondir(messages.getMessageByIdExact(currentZoomX.getFirstVisibleMsgId()).getCumulDistance(),3);
					initialMaxDistanceDouble = AVariableComposant.arrondir(messages.getMessageByIdExact(currentZoomX.getLastVisibleMsgId()).getCumulDistance(),3);
				//}
			}
			labelMinDateFormat.setText(uniteDistance); //$NON-NLS-1$
			labelMinDateFormat.setToolTipText(uniteDistance);
			labelMaxDateFormat.setText(uniteDistance); //$NON-NLS-1$
			labelMaxDateFormat.setToolTipText(uniteDistance);
			textMinDate.setText(String.valueOf(initialMinDistanceDouble));
			textMaxDate.setText(String.valueOf(initialMaxDistanceDouble));
		}
		// disable also the apply button here as it can be enabled by the modify
		// listener
		zoomXApplyButton.setEnabled(false);
	}

	private void initZoomYPage(final Shell shell) {
		labelMinValue = new Label(zoomYPage, SWT.NONE);
		labelMinValue.setText(Messages.getString("ManualZoomDialog.16")); //$NON-NLS-1$
		labelMinValue.setToolTipText(Messages.getString("ManualZoomDialog.16"));
		textMinValue = new Text(zoomYPage, SWT.BORDER);
		textMinValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				textMinValue.setForeground(getParent().getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
				boolean isSame = textMinValue.getText().equals(
						initialMinValueStr);
				if (!isSame) {
					try {
						double curValue = Double.parseDouble(textMinValue
								.getText());
						double initialValue = Double
								.parseDouble(initialMinValueStr);
						if (curValue == initialValue)
							isSame = true;
					} catch (Exception ex) {
					}
				}
				zoomYApplyButton.setEnabled(!isSame);
			}
		});

		textMinValue.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {

				double maxActuel;
				try {
					maxActuel = getZoomXValueFromString(textMaxValue.getText());
					double initialMax = Double
							.parseDouble(ManualZoomDialog.this.initialMaxValueStr);
					if (maxActuel > initialMax) {
						textMaxValue
								.setText(ManualZoomDialog.this.initialMaxValueStr);
						textMaxValue
								.setToolTipText(ManualZoomDialog.this.initialMaxValueStr);
					}
				} catch (NumberFormatException e1) {
					MessageBox msgBox = new MessageBox(ManualZoomDialog.this
							.getParent(), SWT.ICON_ERROR | SWT.OK);
					msgBox.setMessage(strNumberParseError);

					msgBox.open();
					textMaxValue
							.setText(ManualZoomDialog.this.initialMaxValueStr);
					textMaxValue
							.setToolTipText(ManualZoomDialog.this.initialMaxValueStr);
				} catch (ParseException e1) {

				}

			}

			public void focusLost(FocusEvent e) {

				try {
					getZoomXValueFromString(textMinValue.getText());

				} catch (NumberFormatException e1) {
					MessageBox msgBox = new MessageBox(ManualZoomDialog.this
							.getParent(), SWT.ICON_ERROR | SWT.OK);
					msgBox.setMessage(strNumberParseError);

					msgBox.open();
					textMinValue.setText(initialMinValueStr);
					textMinValue.setToolTipText(initialMinValueStr);
				} catch (ParseException e1) {

				}

			}
		});

		labelMinValueUm = new Label(zoomYPage, SWT.NONE);
		labelMinValueUm.setText(""); //$NON-NLS-1$

		labelMaxValue = new Label(zoomYPage, SWT.NONE);
		labelMaxValue.setText(Messages.getString("ManualZoomDialog.18")); //$NON-NLS-1$
		labelMaxValue.setToolTipText(Messages.getString("ManualZoomDialog.18"));

		textMaxValue = new Text(zoomYPage, SWT.BORDER);
		textMaxValue.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				textMaxValue.setForeground(getParent().getDisplay()
						.getSystemColor(SWT.COLOR_BLACK));
				boolean isSame = textMaxValue.getText().equals(
						initialMaxValueStr);
				if (!isSame) {
					try {
						double curValue = Double.parseDouble(textMaxValue
								.getText());
						double initialValue = Double
								.parseDouble(initialMaxValueStr);
						if (curValue == initialValue)
							isSame = true;
					} catch (Exception ex) {
					}
				}
				zoomYApplyButton.setEnabled(!isSame);
			}
		});

		textMaxValue.addFocusListener(new FocusListener() {

			public void focusGained(FocusEvent e) {
				double minActuel;
				try {
					minActuel = getZoomXValueFromString(textMinValue.getText());
					double initialMin = Double
							.parseDouble(ManualZoomDialog.this.initialMinValueStr);
					if (minActuel < initialMin) {
						textMinValue
								.setText(String.valueOf(initialMinValueStr));
						textMinValue.setToolTipText(String
								.valueOf(initialMinValueStr));
					}
				} catch (NumberFormatException e1) {
					MessageBox msgBox = new MessageBox(ManualZoomDialog.this
							.getParent(), SWT.ICON_ERROR | SWT.OK);
					msgBox.setMessage(strNumberParseError);

					msgBox.open();
					textMinValue.setText(initialMinValueStr);
					textMinValue.setToolTipText(initialMinValueStr);
				} catch (ParseException e1) {

				}

			}

			public void focusLost(FocusEvent e) {

				try {
					getZoomXValueFromString(textMaxValue.getText());

				} catch (NumberFormatException e1) {
					MessageBox msgBox = new MessageBox(ManualZoomDialog.this
							.getParent(), SWT.ICON_ERROR | SWT.OK);
					msgBox.setMessage(strNumberParseError);

					msgBox.open();
					textMaxValue.setText(initialMaxValueStr);
					textMaxValue.setToolTipText(initialMaxValueStr);
				} catch (ParseException e1) {

				}
			}
		});

		labelMaxValueUm = new Label(zoomYPage, SWT.NONE);
		labelMaxValueUm.setText(""); //$NON-NLS-1$

		labelVariable = new Label(zoomYPage, SWT.NONE);
		labelVariable.setText(Messages.getString("ManualZoomDialog.20")); //$NON-NLS-1$
		labelVariable.setToolTipText(Messages.getString("ManualZoomDialog.20"));

		comboVariable = new Combo(zoomYPage, SWT.READ_ONLY);
		comboVariable.addSelectionListener(comboSelAdapter);

		zoomYButtonsComposite = new Composite(zoomYPage, SWT.NONE);
		zoomYButtonsComposite.setLayout(new RowLayout());

		zoomYApplyButton = new Button(zoomYButtonsComposite, SWT.PUSH);
		this.zoomYApplyButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (!checkZoomYChanges(true))
					return;
				// Here just disable the apply button
				zoomYApplyButton.setEnabled(false);
			}
		});
		zoomYApplyButton.setText(Messages.getString("ManualZoomDialog.21")); //$NON-NLS-1$
		zoomYApplyButton.setToolTipText(Messages
				.getString("ManualZoomDialog.21"));

		zoomYCancelButton = new Button(zoomYButtonsComposite, SWT.PUSH);
		this.zoomYCancelButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom",
						"Y");
				shell.close();
			}
		});
		zoomYCancelButton.setText(Messages.getString("ManualZoomDialog.22")); //$NON-NLS-1$
		zoomYCancelButton.setToolTipText(Messages
				.getString("ManualZoomDialog.22"));
		zoomYCloseButton = new Button(zoomYButtonsComposite, SWT.PUSH);
		zoomYCloseButton.setText(Messages.getString("ManualZoomDialog.23"));
		zoomYCloseButton.setToolTipText(Messages
				.getString("ManualZoomDialog.23"));
		//$NON-NLS-1$
		this.zoomYCloseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (!checkZoomYChanges(false)) {
					return;
				}
				ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom",
						"Y");
				shell.close();
			}
		});
		zoomYCloseButton.setSelection(true);

		/* Use a GridLayout to position the controls */
		// Monitor monitor = zoomXPage.getMonitor();
		// int width = monitor.getClientArea().width / 7;
		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = layout.marginHeight = 9;
		zoomYPage.setLayout(layout);

		GridData labelMinDateData = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		labelMinValue.setLayoutData(labelMinDateData);

		GridData textMinDateData = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		// textMinDateData.widthHint = width;
		textMinValue.setLayoutData(textMinDateData);

		GridData labelMinDateFormatData = new GridData(SWT.FILL, SWT.CENTER,
				false, false);
		labelMinDateFormatData.grabExcessHorizontalSpace = true;
		labelMinValueUm.setLayoutData(labelMinDateFormatData);

		GridData labelMaxDateData = new GridData(SWT.FILL, SWT.CENTER, false,
				false);
		labelMaxValue.setLayoutData(labelMaxDateData);

		GridData textMaxDateData = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		// textMaxDateData.widthHint = width;
		textMaxValue.setLayoutData(textMaxDateData);

		GridData labelMaxDateFormatData = new GridData(SWT.FILL, SWT.CENTER,
				false, false);
		labelMaxValueUm.setLayoutData(labelMaxDateFormatData);

		GridData variableData = new GridData(SWT.FILL, SWT.CENTER, false, false);
		labelVariable.setLayoutData(variableData);

		GridData comboVariableData = new GridData(SWT.FILL, SWT.CENTER, true,
				false, 2, 1);
		// textMaxDateData.widthHint = width;
		comboVariable.setLayoutData(comboVariableData);

		GridData groupData = new GridData(SWT.CENTER, SWT.TOP, true, false, 4,
				1);
		zoomYButtonsComposite.setLayoutData(groupData);

		initZoomYValues();
	}

	private void initZoomYValues() {
		comboVariable.removeAll();
		comboVariable.setData(null);
		Graphe[] graphes = FabriqueGraphe.getGraphes();
		List<Courbe> allCourbes = new ArrayList<Courbe>();
		List<String> IDvars = new ArrayList<String>();
		for (Graphe graph : graphes) {
			// For ZoomY we are using only analogic graphs
			if (graph.getTypeGraphe() == TypeGraphe.ANALOGIC) {
				List<Courbe> courbes = graph.getListeCourbe();
				for (Courbe courbe : courbes) {
					String nameVar=courbe.getVariable().getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					if (!IDvars.contains(nameVar)) {
						allCourbes.add(courbe);
						IDvars.add(nameVar);
					}
				}
			}
		}
		for (Courbe courbe : allCourbes) {
//			Langage curLang = Activator.getDefault().getCurrentLanguage();
			String name = courbe.getVariable().getDescriptor().getNomUtilisateur().getNomUtilisateur(Activator.getDefault().getCurrentLanguage());
					//=courbe.getVariable().getDescriptor().getNomUtilisateur().getNomUtilisateur(curLang); // =descrVar.getM_AIdentificateurComposant().getNom();
			if (name!=null) {
				comboVariable.add(name);
			}
		}

		comboVariable.setData(allCourbes);

		try {
			ActivatorData.getInstance().getPoolDonneesVues().get("variableZoom")
					.equals("X");
		} catch (Exception e) {
			ActivatorData.getInstance().getPoolDonneesVues().put("variableZoom", "");
		}

		comboVariable.select(0);

		try {
			String var = ActivatorData.getInstance().getPoolDonneesVues().get(
					"variableZoom").toString();
			for (int i = 0; i < comboVariable.getItemCount(); i++) {
				if (comboVariable.getItem(i).toString().equals(var)) {
					comboVariable.select(i);
					break;
				}
			}
		} catch (Exception e) {

		}

		updateZoomYValuesFromSelection();
	}

	public boolean verifierSaisieZoomX(String valeur) {

		SimpleDateFormat format = new SimpleDateFormat(
				"dd/MM/yyyy HH:mm:ss.SSS");

		Date d = null;
		boolean correct = true;

		try {
			// String chaine = String.valueOf(valeur);
			String chaine = valeur;
			if (chaine.length() != 21)
				correct = false;

			else {

				if (Integer.parseInt(chaine.substring(20, 21)) > 9)
					correct = false;

				if (Integer.parseInt(chaine.substring(17, 19)) > 60)
					correct = false;

				if (Integer.parseInt(chaine.substring(14, 16)) > 60)
					correct = false;

				if (Integer.parseInt(chaine.substring(11, 13)) > 24)
					correct = false;

				if (Integer.parseInt(chaine.substring(0, 2)) > 31)
					correct = false;

				if (Integer.parseInt(chaine.substring(0, 2)) > 31)
					correct = false;

				if (Integer.parseInt(chaine.substring(3, 5)) > 12)
					correct = false;
			}

		} catch (NumberFormatException ex) {
			correct = false;
		}

		return correct;

	}

	private boolean checkZoomXChanges(boolean forceApply) {

		double startXValue = 0;
		double endXValue = 0;
		boolean isTimeAxis = false;
		boolean correctValue = true;
		int miniID = -1, maxiID = -1;
		String firstValue = "", lastValue = "";

		typeAxe = GestionnaireAxes.getInstance().getCurrentAxeType();

		if (typeAxe == TypeAxe.AXE_TEMPS
				|| typeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
			isTimeAxis = true;
		}

		try {
			startXValue = getZoomXValueFromString(textMinDate.getText());
			endXValue = getZoomXValueFromString(textMaxDate.getText());
		} catch (Exception e) {
			String strErr = strNumberParseError;
			showFieldValidationErrorMsgBox(textMinDate, strErr);
			correctValue = false;
		}

		if (correctValue) {
			if (zoomXApplyButton.isEnabled()) {
				if (!forceApply) {

					AxeX currentXAxe = GestionnaireAxes.getInstance()
							.getCurrentAxeX();
					List<AxeSegmentInfo> listInfoSegments = currentXAxe
							.getInfoSegments();
					AxeSegmentInfo firstSegmentInfo = listInfoSegments.get(0);
					AxeSegmentInfo lastSegmentInfo = listInfoSegments
							.get(listInfoSegments.size() - 1);
					double dateMinZoom = firstSegmentInfo.getMinValue();
					double dateMaxZoom = lastSegmentInfo.getMaxValue();

					MessageBox msgBox = new MessageBox(this.getParent(),
							SWT.ICON_QUESTION | SWT.YES | SWT.NO);
					msgBox.setText(Messages.getString("ManualZoomDialog.24")); //$NON-NLS-1$
					msgBox
							.setMessage(Messages
									.getString("ManualZoomDialog.25") + " " + "[" + textMinDate.getText() + ";" //$NON-NLS-1$ //$NON-NLS-2$
									+ textMaxDate.getText() + "]");
					// + Messages.getString("ManualZoomDialog.27") //$NON-NLS-1$
					// + (isTimeAxis ? Messages
					// .getString("ManualZoomDialog.28") :
					// Messages.getString("ManualZoomDialog.29")) +
					// Messages.getString("ManualZoomDialog.30")); //$NON-NLS-1$
					// //$NON-NLS-2$ //$NON-NLS-3$
					int ret = msgBox.open();
					if (ret == SWT.NO) {
						return true;
					}
				}
				// validate that start value is strictly greater than the start
				// value
				int minNumSeg = -1;
				int maxNumSeg = -1;

				if (isTimeAxis) {

					try {
						ManualZoomDialog.this.initialMinDateLong = (long) getZoomXValueFromString(textMinDate
								.getText());
						ManualZoomDialog.this.initialMaxDateLong = (long) getZoomXValueFromString(textMaxDate
								.getText());
					} catch (NumberFormatException e) {

					} catch (ParseException e) {

					}

					int nbSeg = TableSegments.getInstance().getSegmentsTemps()
							.size();

					for (int i = 0; i < nbSeg; i++) {
						String miniDate = TableSegments.getInstance()
								.getSegmentTemps(i).getTempInitial();
						String maxiDate = TableSegments.getInstance()
								.getSegmentTemps(i).getTempFinal();

						// si la valeur min est contenue dans un segment
						if (ConversionTemps.calculatePeriodAsLong(miniDate,
								textMinDate.getText().toString()) >= 0
								&& ConversionTemps.calculatePeriodAsLong(
										textMinDate.getText().toString(),
										maxiDate) >= 0) {
							minNumSeg = i;
							break;
						} else if (ConversionTemps.calculatePeriodAsLong(
								miniDate, textMinDate.getText().toString()) == -1) {
							minNumSeg = -1;
							break;
						}

					}

					if (minNumSeg == -1) {
						showFieldValidationErrorMsgBox(textMinDate, Messages
								.getString("ManualZoomDialog.32")); //$NON-NLS-1$
						textMaxDate.setForeground(getParent().getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						textMinDate.setForeground(getParent().getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						textMinDate.setText(initialMinDateStr);
						return false;
					} else {

						for (int i = 0; i < nbSeg; i++) {
							String miniDate = TableSegments.getInstance()
									.getSegmentTemps(i).getTempInitial();
							String maxiDate = TableSegments.getInstance()
									.getSegmentTemps(i).getTempFinal();

							// si la valeur max est contenue dans un segment
							if (ConversionTemps.calculatePeriodAsLong(miniDate,
									textMaxDate.getText().toString()) >= 0
									&& ConversionTemps.calculatePeriodAsLong(
											textMaxDate.getText().toString(),
											maxiDate) >= 0) {
								maxNumSeg = i;
								break;
							}
						}
						if (maxNumSeg == -1) {
							showFieldValidationErrorMsgBox(textMinDate,
									Messages.getString("ManualZoomDialog.33")); //$NON-NLS-1$
							textMaxDate.setForeground(getParent().getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
							textMinDate.setForeground(getParent().getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
							textMaxDate.setText(initialMaxDateStr);
							return false;
						} else {
							// si val min > val max
							if (minNumSeg > maxNumSeg) {
								showFieldValidationErrorMsgBox(
										textMaxDate,
										Messages
												.getString("ManualZoomDialog.31")); //$NON-NLS-1$
								textMaxDate.setForeground(getParent()
										.getDisplay().getSystemColor(
												SWT.COLOR_BLACK));
								textMinDate.setForeground(getParent()
										.getDisplay().getSystemColor(
												SWT.COLOR_BLACK));
								textMinDate.setText(initialMinDateStr);
								textMaxDate.setText(initialMaxDateStr);
								return false;
							}
						}
					}

					// si val min > val max
					if (minNumSeg == maxNumSeg
							&& ConversionTemps.calculatePeriodAsLong(
									textMinDate.getText().toString(),
									textMaxDate.getText().toString()) <= 0) {
						showFieldValidationErrorMsgBox(textMaxDate, Messages
								.getString("ManualZoomDialog.31")); //$NON-NLS-1$
						textMaxDate.setForeground(getParent().getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						textMinDate.setForeground(getParent().getDisplay()
								.getSystemColor(SWT.COLOR_BLACK));
						textMinDate.setText(initialMinDateStr);
						textMaxDate.setText(initialMaxDateStr);
						return false;
					}

					// récupération uniquement des bons messages
					ListMessages messages = ActivatorData.getInstance().getVueData()
							.getDataTable().getEnregistrement().getMessages();

					// récupération de tous les messages
					// List<Message> messages =
					// this.data.getDataTable().getEnregistrement(0)
					// .getMessages();

					TableSegments tabTemps = TableSegments.getInstance();
					int nbmsgs = messages.size();

					boolean fini = false;

					for (int j = 0; j < nbmsgs; j++) {
						if (messages.get(j).getAbsoluteTime() >= startXValue) {
							if (j > 0) {
								miniID = messages.get(j - 1).getMessageId();
								firstValue = String.valueOf(messages.get(j - 1)
										.getAbsoluteTime());
							} else {
								miniID = messages.get(0).getMessageId();
								firstValue = String.valueOf(messages.get(0)
										.getAbsoluteTime());
							}
							fini = true;
							minNumSeg = TableSegments.getSegmentTempsById(
									miniID, tabTemps);
							break;
						}
					}

					if (!fini) {
						miniID = messages.get(0).getMessageId();
						minNumSeg = TableSegments.getSegmentTempsById(miniID,
								tabTemps);
						firstValue = String.valueOf(messages.get(0)
								.getAbsoluteTime());
					}

					fini = false;
					for (int j = nbmsgs - 1; j > -1; j--) {
						if (messages.get(j).getAbsoluteTime() <= endXValue) {
							if (j < nbmsgs - 1) {
								maxiID = messages.get(j + 1).getMessageId();
								lastValue = String.valueOf(messages.get(j + 1)
										.getAbsoluteTime());
							} else {
								maxiID = messages.get(nbmsgs - 1)
										.getMessageId();
								lastValue = String.valueOf(messages.get(
										nbmsgs - 1).getAbsoluteTime());
							}
							fini = true;
							maxNumSeg = TableSegments.getSegmentTempsById(
									maxiID, tabTemps);
							break;
						}
					}

					if (!fini) {
						maxiID = messages.get(nbmsgs - 1).getMessageId();
						maxNumSeg = TableSegments.getSegmentTempsById(maxiID,
								tabTemps);
						lastValue = String.valueOf(messages.get(nbmsgs - 1)
								.getAbsoluteTime());
					}
					// /

				} else {

					try {
						ManualZoomDialog.this.initialMinDistanceDouble = getZoomXValueFromString(textMinDate
								.getText());
						ManualZoomDialog.this.initialMaxDistanceDouble = getZoomXValueFromString(textMaxDate
								.getText());

					} catch (NumberFormatException e) {

					} catch (ParseException e) {

					}

					if (startXValue < 0 || endXValue < 0) {
						MessageBox msgBox = new MessageBox(this.getParent(),
								SWT.ICON_ERROR | SWT.OK);
						msgBox.setMessage(Messages
								.getString("ManualZoomDialog.41"));

						msgBox.open();
						return false;
					}

					minNumSeg = -1;
					maxNumSeg = -1;

					// récupération uniquement des bons messages
					ListMessages messages = ActivatorData.getInstance().getVueData()
							.getDataTable().getEnregistrement().getMessages();

					// récupération de tous les messages
					// List<Message> messages =
					// this.data.getDataTable().getEnregistrement(0)
					// .getMessages();

					TableSegments tabDistance = TableSegments.getInstance();
					int nbmsgs = messages.size();
					boolean fini = false;

					for (int j = 0; j < nbmsgs; j++) {
						if (messages.get(j).getCumulDistance() >= startXValue) {
							if (j > 0) {
								miniID = messages.get(j - 1).getMessageId();
								firstValue = String.valueOf(messages.get(j - 1)
										.getCumulDistance());
							} else {
								miniID = messages.get(0).getMessageId();
								firstValue = String.valueOf(messages.get(0)
										.getCumulDistance());
							}
							fini = true;
							minNumSeg = TableSegments.getSegmentDistanceById(
									miniID, tabDistance);
							break;
						}
					}

					if (!fini) {
						miniID = messages.get(0).getMessageId();
						minNumSeg = TableSegments.getSegmentDistanceById(
								miniID, tabDistance);
						firstValue = String.valueOf(messages.get(0)
								.getCumulDistance());
					}

					fini = false;
					for (int j = nbmsgs - 1; j > -1; j--) {
						if (messages.get(j).getCumulDistance() <= endXValue) {
							if (j < nbmsgs - 1) {
								maxiID = messages.get(j + 1).getMessageId();
								lastValue = String.valueOf(messages.get(j + 1)
										.getCumulDistance());
							} else {
								maxiID = messages.get(nbmsgs - 1)
										.getMessageId();
								lastValue = String.valueOf(messages.get(
										nbmsgs - 1).getCumulDistance());
							}
							fini = true;
							maxNumSeg = TableSegments.getSegmentDistanceById(
									maxiID, tabDistance);
							break;
						}
					}

					if (!fini) {
						maxiID = messages.get(nbmsgs - 1).getMessageId();
						maxNumSeg = TableSegments.getSegmentDistanceById(
								maxiID, tabDistance);
						lastValue = String.valueOf(messages.get(nbmsgs - 1)
								.getCumulDistance());
					} else {
						// si val min > val max
						if (minNumSeg > maxNumSeg) {
							showFieldValidationErrorMsgBox(textMaxDate,
									Messages.getString("ManualZoomDialog.31")); //$NON-NLS-1$
							textMaxDate.setForeground(getParent().getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
							textMinDate.setForeground(getParent().getDisplay()
									.getSystemColor(SWT.COLOR_BLACK));
							textMinDate.setText(initialMinDateStr);
							textMaxDate.setText(initialMaxDateStr);
							return false;
						}
					}

					if (startXValue >= endXValue && minNumSeg == maxNumSeg) {
						showFieldValidationErrorMsgBox(textMaxDate, Messages
								.getString("ManualZoomDialog.31")); //$NON-NLS-1$
						textMaxDate.setForeground(getParent().getDisplay()
								.getSystemColor(SWT.COLOR_BLUE));

						return false;
					}
				}
				applyZoomX(startXValue, endXValue, miniID, maxiID, firstValue,
						lastValue);
			}

		}
		return true;
	}

	private boolean tempsSup(String temps1, String temps2) {

		boolean sup = true;

		if (Integer.parseInt(temps1.substring(6, 8)) == Integer.parseInt(temps2
				.substring(6, 8))) {
			if (Integer.parseInt(temps1.substring(3, 5)) == Integer
					.parseInt(temps2.substring(3, 5))) {
				if (Integer.parseInt(temps1.substring(0, 2)) == Integer
						.parseInt(temps2.substring(0, 2))) {
					if (Integer.parseInt(temps1.substring(11, 13)) == Integer
							.parseInt(temps2.substring(11, 13))) {
						if (Integer.parseInt(temps1.substring(14, 16)) == Integer
								.parseInt(temps2.substring(14, 16))) {
							if (Integer.parseInt(temps1.substring(17, 19)) == Integer
									.parseInt(temps2.substring(17, 19))) {
								if (Integer.parseInt(temps1.substring(20, 21)) == Integer
										.parseInt(temps2.substring(20, 21))) {
									sup = false;
								} else if (Integer.parseInt(temps1.substring(
										20, 21)) > Integer.parseInt(temps2
										.substring(20, 21))) {
									sup = false;
								}
							} else if (Integer.parseInt(temps1
									.substring(17, 19)) > Integer
									.parseInt(temps2.substring(17, 19))) {
								sup = false;
							}
						} else if (Integer.parseInt(temps1.substring(14, 16)) > Integer
								.parseInt(temps2.substring(14, 16))) {
							sup = false;
						}
					} else if (Integer.parseInt(temps1.substring(11, 13)) > Integer
							.parseInt(temps2.substring(11, 13))) {
						sup = false;
					}
				} else if (Integer.parseInt(temps1.substring(0, 2)) > Integer
						.parseInt(temps2.substring(0, 2))) {
					sup = false;
				}
			} else if (Integer.parseInt(temps1.substring(3, 5)) > Integer
					.parseInt(temps2.substring(3, 5))) {
				sup = false;
			}
		} else if (Integer.parseInt(temps1.substring(6, 8)) > Integer
				.parseInt(temps2.substring(6, 8))) {
			sup = false;
		}
		return sup;
	}

	private boolean checkZoomYChanges(boolean forceApply) {
		if (zoomYApplyButton.isEnabled()) {
			double startYValue = 0;
			double endYValue = 0;
			if (!forceApply) {
				MessageBox msgBox = new MessageBox(this.getParent(),
						SWT.ICON_QUESTION | SWT.YES | SWT.NO);
				msgBox.setText(Messages.getString("ManualZoomDialog.34")); //$NON-NLS-1$
				msgBox
						.setMessage(Messages.getString("ManualZoomDialog.35") + " " + textMinValue.getText() + ";" + //$NON-NLS-1$ //$NON-NLS-2$
								textMaxValue.getText()); //$NON-NLS-1$
				int ret = msgBox.open();
				if (ret == SWT.NO) {
					return true;
				}
			}

			try {
				startYValue = Double.parseDouble(textMinValue.getText());
			} catch (NumberFormatException nfe) {
				showFieldValidationErrorMsgBox(textMinValue,
						strNumberParseError);
				return false;
			}
			try {
				endYValue = Double.parseDouble(textMaxValue.getText());
			} catch (NumberFormatException nfe) {
				showFieldValidationErrorMsgBox(textMaxValue,
						strNumberParseError);
				return false;
			}
			// validate that start value is strictly greater than the start
			// value
			if (endYValue <= startYValue) {
				showFieldValidationErrorMsgBox(textMaxValue, Messages
						.getString("ManualZoomDialog.38")); //$NON-NLS-1$
				textMaxValue.setForeground(getParent().getDisplay()
						.getSystemColor(SWT.COLOR_RED));
				return false;
			}
			this.initialMinValueStr = textMinValue.getText();
			this.initialMaxValueStr = textMaxValue.getText();
			applyZoomY(startYValue, endYValue);
		}

		return true;
	}

	private double getZoomXValueFromString(String strValue)
			throws ParseException, NumberFormatException {
		TypeAxe currentXAxeType = GestionnaireAxes.getInstance()
				.getCurrentAxeType();
		if (currentXAxeType == TypeAxe.AXE_TEMPS
				|| currentXAxeType == TypeAxe.AXE_TEMPS_CORRIGE) {
			long t = 0;
			t = ConversionTemps.getMillis(strValue, true);// sdf.parse(strValue).getTime();
			return t;
		} else {
			return Double.parseDouble(strValue);
		}
	}

	private void updateZoomYValuesFromSelection() {
		int selIdx = comboVariable.getSelectionIndex();
		List<Courbe> courbes = (List<Courbe>) comboVariable.getData();
		if (selIdx < 0 || selIdx >= courbes.size())
			return;
		Courbe courbe = courbes.get(selIdx);
		String um = "";
		if (courbe.getVariable().getDescriptor() instanceof DescripteurVariableAnalogique)
			um = ((DescripteurVariableAnalogique) courbe.getVariable()
					.getDescriptor()).getUnite();

		labelMinValueUm.setText(um);
		labelMaxValueUm.setText(um);
		double minValeur = courbe.getMinValeur();
		double maxValeur = courbe.getMaxValeur();
		try{
		minValeur = AVariableComposant.arrondir(minValeur, 3);
		maxValeur = AVariableComposant.arrondir(maxValeur, 3);
		}
		catch(Exception ex){
			
		}
		this.initialMinValueStr = String.valueOf(minValeur);
		this.initialMaxValueStr = String.valueOf(maxValeur);
		textMinValue.setText(initialMinValueStr);
		textMaxValue.setText(initialMaxValueStr);
	}

	private void showFieldValidationErrorMsgBox(Text textField, String errMsg) {
		MessageBox msgBox = new MessageBox(this.getParent(), SWT.ICON_ERROR
				| SWT.OK);
		msgBox.setText(Messages.getString("ManualZoomDialog.40")); //$NON-NLS-1$
		msgBox.setMessage(errMsg);
		textField.setForeground(getParent().getDisplay().getSystemColor(
				SWT.COLOR_BLACK));
		msgBox.open();
	}

	private boolean showFieldConfirmMsgBox(String textField, String Msg) {
		MessageBox msgBox = new MessageBox(this.getParent(), SWT.ICON_QUESTION
				| SWT.YES | SWT.NO);
		msgBox.setText(textField); //$NON-NLS-1$
		msgBox.setMessage(Msg);
		int ret = msgBox.open();
		return ret == SWT.YES;
	}

	private void applyZoomX(double startXValue, double endXValue, int startID,
			int stopID, String firstValue, String lastValue) {
		// int startMsgId = MessagesUtil.getFirstMessageAfter(startXValue);
		int startMsgId = startID;
		// int endMsgId = MessagesUtil.getLastMessageBefore(endXValue);
		int endMsgId = stopID;
		if (startMsgId != -1 && endMsgId != -1) {

			if ((ZoomComposite) GestionnaireZoom.getZoomCourant() != null
					&& ((ZoomComposite) GestionnaireZoom.getZoomCourant())
							.getEnfant(0) != null) {
				ZoomX currentZoomX = (ZoomX) ((ZoomComposite) GestionnaireZoom
						.getZoomCourant()).getEnfant(0);
				if (currentZoomX.getFirstVisibleMsgId() != startID
						|| currentZoomX.getLastVisibleMsgId() != stopID) {
					GestionnaireZoom.creerZoomX(GestionnaireAxes.getInstance()
							.getCurrentAxeX(), startMsgId, endMsgId,
							startXValue, endXValue);
				}
			} else {
				GestionnaireZoom.creerZoomX(GestionnaireAxes.getInstance()
						.getCurrentAxeX(), startMsgId, endMsgId, startXValue,
						endXValue);
			}

			ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom", "X");
			if (GestionnaireAxes.getInstance().getCurrentAxeX().m_TypeAxe == TypeAxe.AXE_TEMPS
					|| GestionnaireAxes.getInstance().getCurrentAxeX().m_TypeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
				textMinDate.setText(ConversionTemps.getFormattedDate(new Long(
						firstValue), true));
				textMaxDate.setText(ConversionTemps.getFormattedDate(new Long(
						lastValue), true));
			} else {
				textMinDate.setText(firstValue);
				textMaxDate.setText(lastValue);
			}
		}
	}

	private void applyZoomY(double minValue, double maxValue) {
		int selIdx = comboVariable.getSelectionIndex();
		// extract current curve
		List<Courbe> courbes = (List<Courbe>) comboVariable.getData();
		if (selIdx < 0 || selIdx >= courbes.size())
			return;
		Courbe courbe = courbes.get(selIdx);
		if (courbe == null)
			return; // : show an error message box here

		// get the graph containing courve
		Graphe[] graphes = FabriqueGraphe.getGraphes();
		Graphe currentGraph = null;
		for (Graphe graph : graphes) {
			if (graph.getListeCourbe().contains(courbe)) {
				currentGraph = graph;
				break;
			}
		}
		if (currentGraph == null)
			return; // : show an error message box here
		GestionnaireZoom.creerSingleVarZoomY(courbe, currentGraph.getNumero(),
				minValue, maxValue);
		ActivatorData.getInstance().getPoolDonneesVues().put("ongletZoom", "Y");
		ActivatorData.getInstance().getPoolDonneesVues().put("variableZoom",
				comboVariable.getItem(comboVariable.getSelectionIndex()));
	}

	private class ComboSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			// : check the current values if changed
			if (!checkZoomYChanges(false)) {
				event.doit = false;
				return;
			}
			updateZoomYValuesFromSelection();
		}
	}

	/**
	 * Méthode de vérification du changement du contenu des 2 champs textes
	 * depuis l'ouverture de la fenetre du zoom
	 * 
	 * @param le
	 *            type du zoom: X ou Y
	 * @return tru si valeurs changees, false sinon
	 */
	private boolean areValeursChangees(TypeZoom typeZoom) {

		boolean changement = false;
		if (typeZoom == TypeZoom.ZOOM_X) {

			// double dateMin=0;
			// double dateMax=0;
			// if ((ZoomComposite)GestionnaireZoom.getZoomCourant()==null) {
			// return false;
			// }
			// ZoomX currentZoomX =
			// (ZoomX)((ZoomComposite)GestionnaireZoom.getZoomCourant()).getEnfant(0);
			// double dateMinZoom=currentZoomX.getFirstXValue();
			// double dateMaxZoom=currentZoomX.getLastXValue();
			// double dateMinZoom=0;
			// double dateMaxZoom=0;
			TypeAxe typeAxe = GestionnaireAxes.getInstance()
					.getCurrentAxeType();
			// boolean isTimeAxis = false;

			// AxeX currentXAxe =
			// GestionnaireAxes.getInstance().getCurrentAxeX();
			// List<AxeSegmentInfo> listInfoSegments =
			// currentXAxe.getInfoSegments();
			// AxeSegmentInfo firstSegmentInfo = listInfoSegments.get(0);
			// AxeSegmentInfo lastSegmentInfo =
			// listInfoSegments.get(listInfoSegments
			// .size() - 1);

			if (typeAxe == TypeAxe.AXE_TEMPS
					|| typeAxe == TypeAxe.AXE_TEMPS_CORRIGE) {
				if (!textMinDate.getText().equals(this.initialMinDateStr)
						|| !textMaxDate.getText()
								.equals(this.initialMaxDateStr))
					changement = true;
				// isTimeAxis = true;
				// dateMinZoom = firstSegmentInfo.getMinValue();
				// dateMaxZoom = lastSegmentInfo.getMaxValue();

			} else {
				try {
					if (getZoomXValueFromString(textMinDate.getText()) != this.initialMinDistanceDouble
							|| getZoomXValueFromString(textMaxDate.getText()) != this.initialMaxDistanceDouble)
						changement = true;
				} catch (NumberFormatException e) {

				} catch (ParseException e) {

				}
			}
			// try {
			// dateMin = getZoomXValueFromString(textMinDate.getText());
			//			
			// } catch (ParseException e) {
			//			
			// String strErr = textMinDate.getText()
			// + strDateParseError2;
			// if (!isTimeAxis) {
			// strErr = strNumberParseError;
			// }
			// showFieldValidationErrorMsgBox(textMinDate, strErr);
			//			
			// // Auto-generated catch block
			// e.printStackTrace();
			// }
			//		
			//		
			//		
			// try {
			// dateMax = getZoomXValueFromString(textMaxDate.getText());
			// } catch (ParseException e) {
			//			
			// String strErr = textMaxDate.getText()
			// + strDateParseError2;
			// if (!isTimeAxis) {
			// strErr = strNumberParseError;
			// }
			// showFieldValidationErrorMsgBox(textMaxDate, strErr);
			//			
			// // Auto-generated catch block
			// e.printStackTrace();
			// }

			// if(!((dateMin == dateMinZoom) && (dateMax == dateMaxZoom)))
			// return true;
			// else return false;
			return changement;
		}

		else
			return true;
	}

	public long getInitialMaxDateLong() {
		return initialMaxDateLong;
	}

	public void setInitialMaxDateLong(long initialMaxDateLong) {
		this.initialMaxDateLong = initialMaxDateLong;
	}

	public double getInitialMaxDistanceDouble() {
		return initialMaxDistanceDouble;
	}

	public void setInitialMaxDistanceDouble(double initialMaxDistanceDouble) {
		this.initialMaxDistanceDouble = initialMaxDistanceDouble;
	}

	public long getInitialMinDateLong() {
		return initialMinDateLong;
	}

	public void setInitialMinDateLong(long initialMinDateLong) {
		this.initialMinDateLong = initialMinDateLong;
	}

	public double getInitialMinDistanceDouble() {
		return initialMinDistanceDouble;
	}

	public void setInitialMinDistanceDouble(double initialMinDistanceDouble) {
		this.initialMinDistanceDouble = initialMinDistanceDouble;
	}

}
