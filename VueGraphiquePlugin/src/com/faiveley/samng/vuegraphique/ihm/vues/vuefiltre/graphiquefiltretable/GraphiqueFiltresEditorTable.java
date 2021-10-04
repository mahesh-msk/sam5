package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.graphiquefiltretable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Widget;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.Activator;
import com.faiveley.samng.principal.ihm.vues.search.dialogs.RechercheDialog;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.GestionnaireCouleurs;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.IMoveOperationsListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ComboPopupDissapearListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ComboPopupShowListener;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ExtendedCombo;
import com.faiveley.samng.principal.ihm.vues.vuesfiltre.controls.ImageCombo;
import com.faiveley.samng.principal.ihm.vues.vuesvbv.VbvsProvider;
import com.faiveley.samng.principal.sm.data.descripteur.DescripteurVariable;
import com.faiveley.samng.principal.sm.data.descripteur.GestionnaireDescripteurs;
import com.faiveley.samng.principal.sm.data.descripteur.Type;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.descripteur.TypeVariable;
import com.faiveley.samng.principal.sm.data.enregistrement.GestionnairePool;
import com.faiveley.samng.principal.sm.data.enregistrement.Util;
import com.faiveley.samng.principal.sm.data.variableComposant.variableVirtuelle.VariableVirtuelle;
import com.faiveley.samng.principal.sm.filtres.AFiltreComposant;
import com.faiveley.samng.principal.sm.filtres.GraphicConstants;
import com.faiveley.samng.principal.sm.filtres.GraphiqueFiltreComposite;
import com.faiveley.samng.principal.sm.filtres.TypeGraphique;
import com.faiveley.samng.principal.sm.filtres.variables.CouleurLigneVariable;
import com.faiveley.samng.principal.sm.filtres.variables.LigneVariableFiltreComposite;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.VitesseLimiteKVBService;
import com.faiveley.samng.principal.sm.segments.TableSegments;
import com.faiveley.samng.vuegraphique.ihm.ActivatorVueGraphique;
import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class GraphiqueFiltresEditorTable extends Composite {

	public static String searchDlgInputLabelText = Messages
			.getString("GraphiqueFiltresEditorTable.0"); //$NON-NLS-1$

	private static final String removeRowUid = Messages
			.getString("GraphiqueFiltresEditorTable.1"); //$NON-NLS-1$

	private static final String searchStringUid = Messages
			.getString("GraphiqueFiltresEditorTable.2"); //$NON-NLS-1$

	public LigneVariableFiltreComposite removeRow = new LigneVariableFiltreComposite();

	public LigneVariableFiltreComposite searchString = new LigneVariableFiltreComposite();

	public Table internalTable;

	protected TableColumn indexesColumn;

	protected TableColumn variablesColumn;

	protected TableColumn colorColumn;

	// Listeners
	public InputSelectionAdapter notificationsAdapter = new InputSelectionAdapter();

	public VariableNamesComboSelectionAdapter varNamesComboAdapter = new VariableNamesComboSelectionAdapter();

	public VariablesComboPopupListener comboVarNamesPopupListener = new VariablesComboPopupListener();

	public GraphicsComboSelectionAdapter graphicsComboAdapter = new GraphicsComboSelectionAdapter();

	public ColorsComboPopupListener comboColorsPopupListener = new ColorsComboPopupListener();

	public ColorsComboSelectionAdapter comboColorsSelAdapter = new ColorsComboSelectionAdapter();

	public Map<String, String> mapNamesToDisplayNames = new HashMap<String, String>();

	public Map<String, DescripteurVariable> initialDigitalOptionValues = new LinkedHashMap<String, DescripteurVariable>();

	public Map<String, DescripteurVariable> initialAnalogOptionValues = new LinkedHashMap<String, DescripteurVariable>();

	public List<TableItemInfo> itemsInfo = new ArrayList<TableItemInfo>();

	public List<AFiltreComposant> initialGraphicsList = new ArrayList<AFiltreComposant>();

	public GraphiqueFiltreComposite[] permanentGraphics = new GraphiqueFiltreComposite[GraphicConstants.MAX_GRAPHICS_COUNT];

	/**
	 * Set containing values that are not present in XML file but they appear as
	 * selected in the loaded filter
	 */
	protected Set<String> setInvalidValues = new HashSet<String>();

	/**
	 * Set containing the values that need to be colores. It contains both
	 * setInvalidValues and also the names that exist in XML file but are not
	 * used in the binary file
	 */
	protected Set<String> setColoredValues = new HashSet<String>();

	protected boolean isLastComboSearch;

	protected int indicesearch;

	protected boolean isChangedStateFromInitial;

	public boolean linesInterchanged;

	public boolean typeGraphiqueChanged;

	protected transient PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	protected List<IMoveOperationsListener> moveOperationsListener = new ArrayList<IMoveOperationsListener>();

	public LineSelectionListener popupDissapearListener = new LineSelectionListener();

	private String searchFilter;

	private Color ERR_COLOR = getDisplay().getSystemColor(SWT.COLOR_RED);

	public GraphiqueFiltresEditorTable(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout());

		this.removeRow.setNom(removeRowUid);
		for (int i = 0; i < GraphicConstants.MAX_GRAPHICS_COUNT; i++)
			permanentGraphics[i] = new GraphiqueFiltreComposite();

		this.initialDigitalOptionValues.put(removeRowUid, null);
		this.initialDigitalOptionValues.put(searchStringUid, null);
		indicesearch = 1;
		this.initialAnalogOptionValues.put(removeRowUid, null);
		this.initialAnalogOptionValues.put(searchStringUid, null);

		this.internalTable = new Table(this, SWT.BORDER | SWT.FULL_SELECTION
				| SWT.VIRTUAL);
		this.internalTable.setHeaderVisible(true);
		initTableColumns();
		// addRowItem(this.removeRow);

		addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent e) {
				Rectangle area = getClientArea();
				Point preferredSize = internalTable.computeSize(SWT.DEFAULT,
						SWT.DEFAULT);
				int width = area.width - (2 * internalTable.getBorderWidth());

				if (preferredSize.y > (area.height + internalTable
						.getHeaderHeight())) {
					// Subtract the scrollbar width from the total column width
					// if a vertical scrollbar will be required
					Point vBarSize = internalTable.getVerticalBar().getSize();
					width -= vBarSize.x;
				}

				Point oldSize = internalTable.getSize();

				int fixedColumnsSize = indexesColumn.getWidth()
						+ colorColumn.getWidth();
				if (oldSize.x > area.width) {
					// table is getting smaller so make the columns
					// smaller first and then resize the table to
					// match the client area width
					// indexesColumn.setWidth(indexesColumn.getWidth());
					variablesColumn.setWidth(width - fixedColumnsSize - 10);
					internalTable.setSize(area.width, area.height);
				} else {
					// table is getting bigger so make the table
					// bigger first and then make the columns wider
					// to match the client area width
					internalTable.setSize(area.width, area.height);
					variablesColumn.setWidth(width - fixedColumnsSize - 10);
				}
			}
		});
		this.internalTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectionIndex(internalTable.getSelectionIndex());
			}
		});

	}

	/**
	 * Returns the list of graphical filters according to the table selected
	 * values
	 * 
	 * @return the list of graphical filters
	 */
	public List<AFiltreComposant> getSelectedValues() {
		List<AFiltreComposant> selValues = new ArrayList<AFiltreComposant>();
		for (AFiltreComposant graphique : this.permanentGraphics) {
			selValues.add(graphique.clone());
		}
		return selValues;
	}

	public void affecterValeursComboBoxGrapheVide(int numero,
			ExtendedCombo combo) {
		String notUsedLabel = LabelForGraphic.getNotUsedLabelForGraphic(numero);
		String analogicLabel = LabelForGraphic.getAnalogLabelForGraphic(numero);
		String digitalLabel = LabelForGraphic.getDigitalLabelForGraphic(numero);
		combo.removeAll();
		combo.add(notUsedLabel);
		combo.add(analogicLabel);
		combo.add(digitalLabel);
		combo.setText(notUsedLabel);
		combo.setToolTipText(notUsedLabel);
	}

	public void affecterValeursComboBox(GraphiqueFiltreComposite graphic,
			ExtendedCombo combo) {
		String notUsedLabel = LabelForGraphic.getNotUsedLabelForGraphic(graphic
				.getNumero());
		String analogicLabel = LabelForGraphic.getAnalogLabelForGraphic(graphic
				.getNumero());
		String digitalLabel = LabelForGraphic.getDigitalLabelForGraphic(graphic
				.getNumero());
		TypeGraphique typeG = graphic.getTypeGraphique();
		String textToDisplay = "";
		combo.removeAll();
		combo.add(notUsedLabel);

		if (typeG == null) {
			textToDisplay = notUsedLabel;
			combo.add(analogicLabel);
			combo.add(digitalLabel);
		} else if (!graphic.isActif()) {
			textToDisplay = notUsedLabel;
			if (graphic.getEnfantCount() == 0) {
				combo.add(analogicLabel);
				combo.add(digitalLabel);
			} else {
				switch (typeG) {
				case analogique:
					combo.add(analogicLabel);
					break;
				case digital:
					combo.add(digitalLabel);
					break;
				default:
					break;
				}
			}
		} else {
			switch (typeG) {
			case analogique:
				textToDisplay = analogicLabel;
				combo.add(analogicLabel);
				break;
			case digital:
				textToDisplay = digitalLabel;
				combo.add(digitalLabel);
				break;
			default:
				break;
			}
		}
		combo.setText(textToDisplay);
		combo.setToolTipText(textToDisplay);
	}

	/**
	 * Sets the text to be displayed for remove row option in the variable combo
	 * 
	 * @param str
	 */
	public void setRemoveRowText(String str) {
		this.removeRow.setNom(str);
	}

	/**
	 * Sets the text for the search option in the variables combo
	 * 
	 * @param str
	 */
	public void setSearchRowText(String str) {
		this.searchString.setNom(str);
	}

	/**
	 * Sets the text to be displayed in the header of the table for the given
	 * column index
	 * 
	 * @param colIdx
	 * @param text
	 */
	public void setColumnText(int colIdx, String text) {
		this.internalTable.getColumn(colIdx).setText(text);
	}

	/**
	 * Returns the internal table
	 * 
	 * @return
	 */
	public Table getTable() {
		return this.internalTable;
	}

	/**
	 * Resets the values from the table and add the rows only for the "not used"
	 * graphics
	 * 
	 */
	private void resetValues(GraphiqueFiltreComposite[] graphs) {
		this.initialGraphicsList.clear();
		this.internalTable.removeAll();
		for (TableItemInfo info : this.itemsInfo) {
			if (info != null) {
				if (info.item != null)
					info.item.dispose();
				if (info.editorVarName != null)
					info.editorVarName.dispose();
				if (info.comboVarName != null)
					info.comboVarName.dispose();
				if (info.editorColor != null)
					info.editorColor.dispose();
				if (info.comboColor != null)
					info.comboColor.dispose();
			}
		}
		this.itemsInfo.clear();
		LinkedHashMap<Integer, GraphiqueFiltreComposite> mapNumeroGrapheFiltreComposite = null;
		if (graphs.length < GraphicConstants.MAX_GRAPHICS_COUNT) {
			mapNumeroGrapheFiltreComposite = new LinkedHashMap<Integer, GraphiqueFiltreComposite>();

			mapNumeroGrapheFiltreComposite.put(0, null);
			mapNumeroGrapheFiltreComposite.put(1, null);
			mapNumeroGrapheFiltreComposite.put(2, null);
			mapNumeroGrapheFiltreComposite.put(3, null);

			for (GraphiqueFiltreComposite composite : graphs) {
				mapNumeroGrapheFiltreComposite.put(composite.getNumero(),
						composite);
			}

		}
		if (mapNumeroGrapheFiltreComposite == null) {
			for (int i = 0; i < GraphicConstants.MAX_GRAPHICS_COUNT; i++) {

				this.permanentGraphics[i].removeAll();
				this.permanentGraphics[i].setNom(LabelForGraphic
						.getNotUsedLabelForGraphic(i));
				this.permanentGraphics[i].setNumero(i);
				try {
					this.permanentGraphics[i].setActif(graphs[i].isActif());
					this.permanentGraphics[i].setParent(graphs[i].getParent());
					this.permanentGraphics[i].setTypeGraphique(graphs[i]
							.getTypeGraphique());

					TypeVariable typeVariablePremiereLigneGraphique = GestionnairePool.getInstance()
							.getVariable(
									graphs[i]
											.getM_ALigneVariableFiltreComposant()
											.get(0).getNom()).getDescriptor()
							.getTypeVariable();

					if (typeVariablePremiereLigneGraphique == TypeVariable.VAR_ANALOGIC) {
						this.permanentGraphics[i]
								.setTypeGraphique(TypeGraphique.analogique);
					} else if (typeVariablePremiereLigneGraphique == TypeVariable.VAR_VIRTUAL
							|| typeVariablePremiereLigneGraphique == TypeVariable.VAR_DISCRETE) {
						this.permanentGraphics[i]
								.setTypeGraphique(TypeGraphique.digital);
					}
				} catch (Exception e) {

				}
				addGraphicRowItem(this.permanentGraphics[i]);
			}
		} else {
			for (int i = 0; i < GraphicConstants.MAX_GRAPHICS_COUNT; i++) {
				if (mapNumeroGrapheFiltreComposite.get(i) == null) {
					this.permanentGraphics[i].removeAll();
					this.permanentGraphics[i].setNom(LabelForGraphic
							.getNotUsedLabelForGraphic(i));
					this.permanentGraphics[i].setNumero(i);
					this.permanentGraphics[i].setActif(false);
				} else {
					this.permanentGraphics[i].setNom(LabelForGraphic
							.getNotUsedLabelForGraphic(i));
					this.permanentGraphics[i].setNumero(i);
					GraphiqueFiltreComposite graphe = mapNumeroGrapheFiltreComposite
							.get(i);
					this.permanentGraphics[i].setActif(graphe.isActif());
					this.permanentGraphics[i].setParent(graphe.getParent());
					this.permanentGraphics[i].setTypeGraphique(graphe
							.getTypeGraphique());
					this.permanentGraphics[i]
							.setM_ALigneVariableFiltreComposant(graphe
									.getM_ALigneVariableFiltreComposant());
					if (graphe.getEnfantCount() > 0) {
						if (GestionnairePool.getInstance().getVariable(graphe
								.getM_ALigneVariableFiltreComposant().get(0)
								.getNom()) != null) {
							TypeVariable typeVariablePremiereLigneGraphique = GestionnairePool.getInstance()
									.getVariable(
											graphe.getM_ALigneVariableFiltreComposant()
													.get(0).getNom())
									.getDescriptor().getTypeVariable();
							if (typeVariablePremiereLigneGraphique == TypeVariable.VAR_ANALOGIC) {
								this.permanentGraphics[i]
										.setTypeGraphique(TypeGraphique.analogique);
							} else if (typeVariablePremiereLigneGraphique == TypeVariable.VAR_VIRTUAL
									|| typeVariablePremiereLigneGraphique == TypeVariable.VAR_DISCRETE) {
								this.permanentGraphics[i]
										.setTypeGraphique(TypeGraphique.digital);
							}
						}
					} else {
						this.permanentGraphics[i].setTypeGraphique(null);
					}
				}
				addGraphicRowItem(this.permanentGraphics[i]);
			}
		}
	}

	/**
	 * Initializes the values to be displayed in the combo boxes
	 * 
	 * @param cmvvalues
	 */
	public void initValues(GraphiqueFiltreComposite[] graphics) {
		resetValues(graphics);

		String graphicName;
		int graphicNumber;
		GraphiqueFiltreComposite value = null;
		List<Integer> listeNumeroGraphes = new ArrayList<Integer>();
		List<Integer> listeNumeroGraphesVides = new ArrayList<Integer>();
		GraphiqueFiltreComposite[] tableauTmpGraphe = new GraphiqueFiltreComposite[4];
		for (int i = 0; i < tableauTmpGraphe.length; i++) {
			try {
				tableauTmpGraphe[i] = graphics[i];
				listeNumeroGraphes.add(tableauTmpGraphe[i].getNumero());
			} catch (Exception e) {
				tableauTmpGraphe[i] = null;
			}
		}
		// on stocke les numéro des graphes dont le type est null
		for (int o = 0; o < GraphicConstants.MAX_GRAPHICS_COUNT; o++) {
			if (!listeNumeroGraphes.contains(o))
				listeNumeroGraphesVides.add(o);
		}
		int numeroManquant = 0;
		// on initialise la liste déroulante des graphes non vides
		for (int k = 0; k < GraphicConstants.MAX_GRAPHICS_COUNT; k++) {
			if (tableauTmpGraphe[k] != null) {
				value = tableauTmpGraphe[k];
				graphicNumber = value.getNumero();
				createRowsForGraphic(value);
				if (!value.isActif()) {
					int nbLinesBefore = 0;
					boolean graphFound = false;
					int j = 0;
					while (!graphFound && j < 30) {
						if (itemsInfo.get(j).item.getData() instanceof GraphiqueFiltreComposite) {
							if (((GraphiqueFiltreComposite) itemsInfo.get(j).item
									.getData()).getNom().equalsIgnoreCase(
									value.getNom())) {
								graphFound = true;
							} else
								j++;
						} else
							j++;
					}
					nbLinesBefore = +j;

					if (graphFound) {
						int nbValuesToDisable = Math.min(
								value.getEnfantCount() + 1,
								value.getPossibleVariableLines());
						for (int i = nbLinesBefore + nbValuesToDisable; i > nbLinesBefore; i--) {
							try {
								enableLine(itemsInfo.get(i), false);
							} catch (RuntimeException e) {
								e.printStackTrace();
							}
						}
					}
				}
				TableItemInfo itemInfo = getGraphicItemInfo(graphicNumber);
				affecterValeursComboBox(value, itemInfo.comboVarName);
				this.initialGraphicsList.add(value);
			}
		}
		// on initialise la liste déroulante des graphes vides
		value = null;
		for (int numero : listeNumeroGraphesVides) {
			if (getGraphicItemInfo(numero) != null) {
				affecterValeursComboBoxGrapheVide(numero,
						getGraphicItemInfo(numero).comboVarName);
			}
			this.initialGraphicsList.add(value);
		}
		this.isChangedStateFromInitial = false;
		this.linesInterchanged = false;
		updateMissingValues();
	}

	/**
	 * Creates a rows for a graphical filter. The corresponding not used graphic
	 * (according to the graphic number) is searched and the variables filters
	 * for that graphic are added as rows
	 * 
	 * @param value
	 *            the graphic composite filter
	 */
	private void createRowsForGraphic(GraphiqueFiltreComposite value) {
		int graphicLinesNr;
		String graphicName = value.getNom();
		int graphicNumber = value.getNumero();
		TableItemInfo itemInfo = getGraphicItemInfo(graphicNumber);

		try {
			((GraphiqueFiltreComposite) itemInfo.item.getData())
					.setNom(graphicName);
		} catch (RuntimeException e) {
			e.printStackTrace();
		}

		if (value.getTypeGraphique() != null)
			createEmptyLinesForGraphic(graphicNumber, value);

		graphicLinesNr = value.getEnfantCount();
		// now add the variables
		int graphicStartIdx = getGraphicItemInfoIndex(graphicNumber) + 1;
		for (int i = 0; i < graphicLinesNr; i++) {
			addVariableRowItem(value.getEnfant(i), graphicStartIdx + i, false);
		}
		// Add also a selection row
		int graphicPossibleLines = value.getPossibleVariableLines();// graphicName);
		if (graphicLinesNr < graphicPossibleLines) {
			addVariableRowItem(removeRow, graphicStartIdx + graphicLinesNr,
					false);
		}
	}

	/**
	 * Initializes the values to be available as options in the combo boxes
	 * 
	 * @param values
	 */
	public void setInitialOptionValues(Map<String, DescripteurVariable> values,
			Set<String> nonPresentValues) {
		if (this.initialDigitalOptionValues.size() != 2) {
			this.initialDigitalOptionValues.clear();
			this.initialDigitalOptionValues.put(removeRowUid, null);
			this.initialDigitalOptionValues.put(searchStringUid, null);
			indicesearch = 1;
			this.initialAnalogOptionValues.clear();
			this.initialAnalogOptionValues.put(removeRowUid, null);
			this.initialAnalogOptionValues.put(searchStringUid, null);
			mapNamesToDisplayNames.clear();
		}
		if ((values == null) || (values.size() == 0)) {
			return;
		}
		DescripteurVariable descrVar;
		Type valueType;
		for (String key : values.keySet()) {
			descrVar = values.get(key);
			if (descrVar != null) {
				valueType = descrVar.getType();
				if (descrVar.getTypeVariable() == TypeVariable.VAR_ANALOGIC) {
					this.initialAnalogOptionValues.put(key, descrVar);
				} else if ((descrVar.getTypeVariable() == TypeVariable.VAR_DISCRETE || descrVar
						.getTypeVariable() == TypeVariable.VAR_VIRTUAL)
						&& (valueType == Type.boolean1 || valueType == Type.boolean8)) {
					this.initialDigitalOptionValues.put(key, descrVar);
				}
				mapNamesToDisplayNames.put(descrVar
						.getM_AIdentificateurComposant().getNom(), key);
			} else {
				this.initialDigitalOptionValues.put(key, null);
				this.initialAnalogOptionValues.put(key, null);
				mapNamesToDisplayNames.put(key, key);
				this.setInvalidValues.add(key);
			}
		}

		this.setColoredValues.clear();
		if (nonPresentValues != null) {
			this.setColoredValues.addAll(nonPresentValues);
		}

		updateMissingValues();
	}

	/**
	 * Updates the row for the corresponding graphic with the name of the given
	 * graphic.
	 * 
	 * @param graphic
	 *            the graphic whose table item should be updated
	 */
	private void addGraphicRowItem(GraphiqueFiltreComposite graphic) {
		TableItemInfo itemInfo = new TableItemInfo(graphic);
		affecterValeursComboBox(graphic, itemInfo.comboVarName);
		setComboText(itemInfo.comboVarName, graphic.getNom());
	}

	/**
	 * Adds a row for a variable in the table at the given position (absolute
	 * position)
	 * 
	 * @param variable
	 *            the variable to be added
	 * @param tablePos
	 *            the position in the table where to add the variable
	 * @param forceAdd
	 *            if is true a new table item info is created and updated from
	 *            variable otherwise if the item already exists, it is only
	 *            updated from variable
	 * @return
	 */
	private TableItemInfo addVariableRowItem(AFiltreComposant variable,
			int tablePos, boolean forceAdd) {
		TableItemInfo itemInfo = null;
		if (variable == null || forceAdd) {
			itemInfo = new TableItemInfo(variable, tablePos); // just create
			// an empty row
			// maybe we should have a lignevarcomp like the one for remove row
			if (variable != null) { // we have a force add
				itemInfo = this.itemsInfo.get(tablePos);
				// : get parent permanent graphic and add or remove the
				// child at the given position
				updateVariableTableLine(variable, itemInfo);
				this.itemsInfo.get(tablePos - 1).comboVarName.getArrow()
						.setText("-");
			}
		} else {
			try {
				itemInfo = this.itemsInfo.get(tablePos);
			} catch (RuntimeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// : get parent permanent graphic and add or remove the child at
			// the given position
			updateVariableTableLine(variable, itemInfo);
			if (this.removeRow == variable) {
				itemInfo.comboVarName.setText(""); //$NON-NLS-1$
			} else {
				int currentLinePos = this.itemsInfo.indexOf(itemInfo);
				int parentPos = getGraphicParentForVariablePos(currentLinePos);
				TableItemInfo parentGraphicItemInfo = this.itemsInfo
						.get(parentPos);
				AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
						.getData();
				graphic.ajouter(variable);
			}
			this.itemsInfo.get(tablePos - 1).comboVarName.getArrow().setText(
					"-");
		}

		return itemInfo;
	}

	private List<String> getPossibleValues(ExtendedCombo combo,
			String currentValue, List<String> initialOptionsList,
			boolean removeSearchAndRemove) {
		TableItemInfo itemInfo = (TableItemInfo) combo.getData();
		AFiltreComposant parentGraphic = getParentGraphicForLine(itemInfo);
		TypeGraphique graphicType = ((GraphiqueFiltreComposite) parentGraphic)
				.getTypeGraphique();
		Map<String, DescripteurVariable> mapInitialOptions;
		if (graphicType == TypeGraphique.analogique)
			mapInitialOptions = initialAnalogOptionValues;
		else
			// if (graphicType == TypeGraphique.digital)
			mapInitialOptions = initialDigitalOptionValues;

		List<String> valuesPresent = getCurrentVariablesNames();
		initialOptionsList = new ArrayList<String>(mapInitialOptions.keySet());
		List<String> possibleValues = listDifference(initialOptionsList,
				valuesPresent, currentValue);
		if (removeSearchAndRemove) {
			possibleValues.remove(removeRowUid);
			possibleValues.remove(searchStringUid);
		}
		return possibleValues;
	}

	/**
	 * Updates the possible values for a combo box by removing from the possible
	 * selection values the items that already exist in the table
	 * 
	 * @param combo
	 * @param currentValue
	 */
	private void updateVarComboPossibleValues(ExtendedCombo combo,
			String currentValue) {
		TableItemInfo itemInfo = (TableItemInfo) combo.getData();
		AFiltreComposant parentGraphic = getParentGraphicForLine(itemInfo);

		List<String> initialOptionsList = new ArrayList<String>();
		List<String> possibleValues = getPossibleValues(combo, currentValue,
				initialOptionsList, false);
		combo.removeAll();
		for (String val : possibleValues) {
			combo.add(
					val,
					this.setColoredValues.contains(val) ? ColorsConstants
							.getERR_COLOR() : null);
		}

		// combo.setItems(possibleValues.toArray(new
		// String[possibleValues.size()]));
		setComboText(combo, currentValue);
		if (("".equals(currentValue) || " ".equals(currentValue))
				&& (this.internalTable.getItemCount() > 1)
				&& possibleValues.size() < initialOptionsList.size()) {
			if (isLastComboSearch) {
				combo.setTopIndex(indicesearch + 1);
			} else {
				if (((GraphiqueFiltreComposite) parentGraphic).isActif()) {
					String prevValText = itemsInfo.get(itemsInfo
							.indexOf(itemInfo) - 1).comboVarName.getText();
					int idx;
					int prevValInitialPos = initialOptionsList
							.indexOf(prevValText);
					for (int i = prevValInitialPos; i < initialOptionsList
							.size(); i++) {
						try {
							idx = possibleValues.indexOf(initialOptionsList
									.get(i));
						} catch (Exception e) {
							idx = -1;
						}

						if (idx > 0) {
							combo.setTopIndex(idx);
							break;
						}
					}
				}
			}
		}
	}

	/**
	 * Return a unique name identifier for a variable from the user name
	 * 
	 * @param displayName
	 *            the user name of the variable
	 * @param analogicFilter
	 *            specifies if is an analogic or discrete filter variable
	 * @return the unique name if found or the user name if unique name cannot
	 *         be extracted
	 */
	private String getNameFromDisplayName(String displayName,
			boolean analogicFilter) {
		Map<String, DescripteurVariable> map = analogicFilter ? this.initialAnalogOptionValues
				: this.initialDigitalOptionValues;
		DescripteurVariable descr = map.get(displayName);
		String retStr = displayName;
		if (descr != null)
			retStr = descr.getM_AIdentificateurComposant().getNom();
		return retStr;
	}

	/**
	 * Returns the user name for a variable from its unique name. If the user
	 * name is not found the given unique name is returned
	 * 
	 * @param name
	 * @return
	 */
	private String getDisplayStringFromName(String name) {
		String displayName = this.mapNamesToDisplayNames.get(name);
		if (displayName == null)
			displayName = name;
		return displayName;
	}

	/**
	 * Returns the list of colors that are available for a given graphic. If the
	 * graphic has already variables colors these will be removed from the list
	 * of global possible colors
	 * 
	 * @param graphic
	 *            the graphic for which the colors are extracted
	 * @param ignoredValue
	 *            a string representing the color that is ignored also (not
	 *            added in the returned list)
	 * @return
	 */
	private List<RGB> getAvailableGraphicColors(AFiltreComposant graphic,
			String ignoredValue) {
		int graphicLines = graphic.getEnfantCount();
		AFiltreComposant graphicLine;
		AFiltreComposant lineColor;
		RGB colorRgb;
		List<RGB> availColors = new ArrayList<RGB>(
				Arrays.asList(GestionnaireCouleurs.getVariablesColors()));

		for (int i = 0; i < graphicLines; i++) {
			graphicLine = graphic.getEnfant(i);
			if (graphicLine.getEnfantCount() == 0)
				continue;
			lineColor = graphicLine.getEnfant(0);
			if (lineColor.getNom().equals(ignoredValue))
				continue;
			colorRgb = GestionnaireCouleurs.getRgbForHexValue(lineColor
					.getNom());
			availColors.remove(colorRgb);
		}
		return availColors;
	}

	/**
	 * Updates the possible values for a combo box by removing from the possible
	 * selection values the items that already exist in the table
	 * 
	 * @param combo
	 * @param currentValue
	 */
	private void updateColorComboPossibleValues(ImageCombo combo) {
		TableItemInfo itemInfo = (TableItemInfo) combo.getData();
		AFiltreComposant lineFilter = (AFiltreComposant) itemInfo.item
				.getData();
		AFiltreComposant color = lineFilter.getEnfant(0);
		int currentLinePos = this.itemsInfo.indexOf(itemInfo);
		int parentPos = getGraphicParentForVariablePos(currentLinePos);
		TableItemInfo parentGraphicItemInfo = this.itemsInfo.get(parentPos);
		AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
				.getData();

		List<RGB> availableColors = getAvailableGraphicColors(graphic,
				color.getNom());

		Rectangle comboBounds = combo.getBounds();
		int height = comboBounds.height - 2;
		int width = comboBounds.width - 5; // 5 is assumed to be combo arrow
		int lineHeight = 4;
		Image currentImage = combo.getImage();
		combo.removeAll();
		for (int i = 0; i < availableColors.size(); i++)
			combo.add("", GestionnaireCouleurs.createImage(getDisplay(),
					availableColors.get(i), height, width, lineHeight));
		combo.setText("", currentImage); //$NON-NLS-1$
	}

	/**
	 * Returns the list of alredy added variables names (user names) for all
	 * graphics
	 * 
	 * @return the list of extracted variable names
	 */
	private List<String> getCurrentVariablesNames() {
		List<String> valuesPresent = new ArrayList<String>();
		for (TableItemInfo info : this.itemsInfo) {
			if (info != null && info.comboVarName != null) {
				// : check here also the types of the variables
				valuesPresent.add(info.comboVarName.getText());
			}
		}
		return valuesPresent;
	}

	/**
	 * Returns the difference between two lists of strings
	 * 
	 * @param list1
	 * @param list2
	 * @param ignoredValue
	 * @return
	 */
	private List<String> listDifference(Collection<String> list1,
			List<String> list2, String ignoredValue) {
		List<String> retList = new ArrayList<String>();
		for (String curElement : list1) {
			if (curElement != null) {
				if (curElement.equals(ignoredValue)
						|| !(list2.contains(curElement))) {
					retList.add(curElement);
				}
			}
		}
		return (retList);
	}

	/**
	 * Enables or disables a line specified by the given table item info
	 * 
	 * @param info
	 * @param enabled
	 */
	private void enableLine(TableItemInfo info, boolean enabled) {
		if (info != null) {
			if (info.comboVarName != null)
				info.comboVarName.setEnabled(enabled);
			if (info.comboColor != null)
				info.comboColor.setEnabled(enabled);
		}
	}

	/**
	 * Removes a line variable corresponding to the given table item info
	 * 
	 * @param info
	 *            the table item info
	 * @param permanent
	 *            specifies if the row should be removed permanently from the
	 *            table or just emptied (for example in a graph if is removed
	 *            only one line)
	 */
	private void removeVariableLineAt(TableItemInfo info, boolean permanent) {
		int index = this.itemsInfo.indexOf(info);
		int parentGraphicPos = -1;
		AFiltreComposant lineVar = null;
		if (!permanent) {
			parentGraphicPos = getGraphicParentForVariablePos(index);
			lineVar = (AFiltreComposant) info.item.getData();
		}
		this.internalTable.remove(index);
		info.item.dispose();
		if (info != null) {
			if (info.editorVarName != null)
				info.editorVarName.dispose();
			if (info.comboVarName != null)
				info.comboVarName.dispose();
			if (info.editorColor != null)
				info.editorColor.dispose();
			if (info.comboColor != null)
				info.comboColor.dispose();

			this.itemsInfo.remove(info);
		}
		if (!permanent) {
			TableItemInfo parentGraphicItemInfo = this.itemsInfo
					.get(parentGraphicPos);
			AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
					.getData();
			graphic.supprimer(lineVar);
			int varLines = ((GraphiqueFiltreComposite) graphic)
					.getPossibleVariableLines();
			if (graphic.getEnfantCount() == varLines - 1) {
				info = addVariableRowItem(this.removeRow, parentGraphicPos
						+ varLines, true);
			} else {
				info = addVariableRowItem(null, parentGraphicPos + varLines,
						false);
			}
			updateVariablesIndexes(parentGraphicPos + 1, varLines);
		}
		onFilterChanged();
		// Notify the table editors that the table changed (a removal occured)
		notifyRefreshTableEditors();
		setSelectionIndex(index);
	}

	/**
	 * Adds a property-change listener.
	 * 
	 * @param l
	 *            the listener
	 */
	public final void addPropertyChangeListener(final PropertyChangeListener l) {
		if (l == null) {
			throw new IllegalArgumentException();
		}
		this.listeners.addPropertyChangeListener(l);
	}

	public void addMoveOperationListener(IMoveOperationsListener listener) {
		if (listener != null)
			moveOperationsListener.add(listener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener l) {
		this.listeners.removePropertyChangeListener(l);
	}

	public void removeMoveOperationListener(IMoveOperationsListener listener) {
		if (listener != null)
			moveOperationsListener.remove(listener);
	}

	/**
	 * Notifies move operations listeners that the flags changed
	 * 
	 * @param flags
	 */
	private void fireMoveOperations(int flags) {
		for (IMoveOperationsListener listener : moveOperationsListener) {
			listener.moveFlagsChanged(flags);
		}
	}

	/**
	 * Notificates all listeners to a model-change
	 * 
	 * @param prop
	 *            the property-id
	 * @param old
	 *            the old-value
	 * @param newValue
	 *            the new value
	 */
	protected final void firePropertyChange(final String prop,
			final Object old, final Object newValue) {
		try {
			if (this.listeners.hasListeners(prop)) {
				this.listeners.firePropertyChange(prop, old, newValue);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Computes the change state from the orginial one
	 * 
	 * @return true if the filter changed
	 */
	private boolean isDifferentFromInitialValues() {
		List<AFiltreComposant> selValues = getSelectedValues();
		if (this.initialGraphicsList.size() != selValues.size()) {
			this.isChangedStateFromInitial = true;
		} else {
			selValues.removeAll(this.initialGraphicsList);
			this.isChangedStateFromInitial = (selValues.size() > 0);
		}
		return this.isChangedStateFromInitial || linesInterchanged;
	}

	/**
	 * If the filter changed from the original state it will return true
	 * otherwise will return false
	 * 
	 * @return true if the filter changed
	 */
	public boolean isChangedStateFromInitial() {
		return this.isChangedStateFromInitial || linesInterchanged;
	}

	public void resetChangedStateFromInitial() {
		this.initialGraphicsList.clear();
		this.initialGraphicsList.addAll(getSelectedValues());
		this.isChangedStateFromInitial = false;
		this.linesInterchanged = false;
	}

	/**
	 * Updates the possible selection values with the missing values that are
	 * going to be displayed as we might have not them in the possible values
	 * list. These are going to be displayed with red color
	 * 
	 */
	public void updateMissingValues() {
		this.setColoredValues.removeAll(this.setInvalidValues);
		this.setInvalidValues.clear();
		String curVal;

		for (int i = 0; i < this.itemsInfo.size(); i++) {
			if (this.itemsInfo.get(i).comboVarName == null)
				continue;
			try {
				if (this.itemsInfo.get(i).getRowType() == ROW_TYPE.ROW_GRAPHIQUE)
					continue;
			} catch (Exception ex) {
				continue;
			}
			curVal = this.itemsInfo.get(i).comboVarName.getText();
			if (!"".equals(curVal)) { //$NON-NLS-1$

				if (!this.initialDigitalOptionValues.containsKey(curVal)
						&& !this.initialAnalogOptionValues.containsKey(curVal)) {
					DescripteurVariable desc = this.initialDigitalOptionValues
							.get(curVal);
					if (desc == null) {
						if (ActivatorData.getInstance().getPoolDonneesVues()
								.get("vbvNewName") != null) {
							// internalTable.remove(i);
							if (!((String) ActivatorData.getInstance().getPoolDonneesVues().get("vbvNewName"))
									.equals("")) {
								this.itemsInfo.get(i).comboVarName
										.setText("(V) "
												+ (String) ActivatorData.getInstance().getPoolDonneesVues()
														.get("vbvNewName"));
								setComboText(
										this.itemsInfo.get(i).comboVarName,
										this.itemsInfo.get(i).comboVarName
												.getText());
							} else {
								removeVariableLineAt(this.itemsInfo.get(i),
										false);
								this.initialDigitalOptionValues.remove(curVal);
								this.initialAnalogOptionValues.remove(curVal);
								mapNamesToDisplayNames.remove(curVal);
							}
						}
						this.setInvalidValues.add(curVal);
					} else {
						this.initialDigitalOptionValues.put(curVal, null);
						this.initialAnalogOptionValues.put(curVal, null);
						this.setInvalidValues.add(curVal);
						mapNamesToDisplayNames.put(curVal, curVal);

						this.itemsInfo.get(i).comboVarName.setText(curVal,
								ColorsConstants.getERR_COLOR());
					}
				}
			}
		}
		this.setColoredValues.addAll(this.setInvalidValues);
		// Refresh the colors
		for (int i = 0; i < this.itemsInfo.size(); i++) {
			if (this.itemsInfo.get(i).comboVarName != null) {
				try {
					if (this.itemsInfo.get(i).getRowType() == ROW_TYPE.ROW_GRAPHIQUE)
						continue;
				} catch (Exception ex) {
					continue;
				}
				setComboText(this.itemsInfo.get(i).comboVarName,
						this.itemsInfo.get(i).comboVarName.getText());
			}
		}
	}

	/**
	 * Sets the text for a variable name. If an invalid value, the text will be
	 * colored with red
	 * 
	 * @param combo
	 * @param text
	 */
	private void setComboText(ExtendedCombo combo, String text) {
		boolean variableValide = false;
		boolean usesShortNames = ActivatorVueGraphique.getDefault().isUsesShortNames();
		if (!"".equals(text)) {

			String varDistanceCorrige = null;
			String varVitesseCorrigee = null;
			String vitesseLimiteKVBNomUtilisateur = VitesseLimiteKVBService.getInstance().getTableLangueNomUtilisateur()
					.getNomUtilisateur(Activator.getDefault().getCurrentLanguage());

			if (GestionnaireDescripteurs
					.getDescripteurVariable(TypeRepere.vitesseCorrigee
							.getCode()) != null) {
				varVitesseCorrigee = GestionnaireDescripteurs
						.getDescripteurVariable(
								TypeRepere.vitesseCorrigee.getCode())
						.getNomUtilisateur()
						.getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
			} else if (GestionnaireDescripteurs
					.getDescripteurVariable(TypeRepere.distanceCorrigee
							.getCode()) != null) {
				varVitesseCorrigee = GestionnaireDescripteurs
						.getDescripteurVariable(
								TypeRepere.distanceCorrigee.getCode())
						.getNomUtilisateur()
						.getNomUtilisateur(
								Activator.getDefault().getCurrentLanguage());
			}
			if (text.equals(varVitesseCorrigee)) {
				if (TableSegments.getInstance().isAppliedDistanceCorrections())
					variableValide = true;

			} else if (text.equals(varDistanceCorrige)) {
				if (TableSegments.getInstance().isAppliedDistanceCorrections())
					variableValide = true;

			} else if (text.equals(vitesseLimiteKVBNomUtilisateur)) {
				variableValide = VitesseLimiteKVBService.isTableKVBXMLexist();
			}

			else {
				if (text.startsWith("(V) ")) {
					VbvsProvider providerVbv = ActivatorData.getInstance().getProviderVBVs();
					String nomVbvSansPrefixe = text.substring(4, text.length());
					VariableVirtuelle vbv = providerVbv.getGestionnaireVbvs()
							.getVBV(nomVbvSansPrefixe);
					if (providerVbv.verifierValiditeVBV(vbv) == null) {
						variableValide = true;
					}
				} else {
					// 1er test : si on trouve la variable, on regarde si elle
					// est dans le fichier de parcours
					variableValide = Util.getInstance().isVariableInXml(text, true);

					// 2ème vérification : la variable est elle dans le parcours
					if (variableValide) {
						variableValide = Util
								.getInstance().isVariableDansParcours(text, true);
					}
				}
			}
		}

		String displayText = (usesShortNames 
				&& text.lastIndexOf('.') != -1 
				&& text.lastIndexOf('.') < text.length() - 1) 
				? text.substring(text.lastIndexOf('.') + 1) : text;
		if (!"".equals(text) && (!variableValide)) {
			combo.setText(displayText, ERR_COLOR);
		} else {
			combo.setText(displayText);
		}
		combo.setToolTipText(text);
	}

	/**
	 * Initializes the columns of the table
	 * 
	 */
	private void initTableColumns() {
		// Create the indexes column
		this.indexesColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.indexesColumn.setWidth(25);
		this.indexesColumn.setResizable(false);
		this.indexesColumn.setText(" "); //$NON-NLS-1$
		// Create the variables column
		this.variablesColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.variablesColumn.setText(" "); //$NON-NLS-1$
		this.variablesColumn.setResizable(false);
		this.variablesColumn.setAlignment(SWT.CENTER);
		// Create the operator column
		this.colorColumn = new TableColumn(this.internalTable, SWT.NONE);
		this.colorColumn.setText(" "); //$NON-NLS-1$
		this.colorColumn.setWidth(80);
		this.colorColumn.setResizable(false);
		this.colorColumn.setAlignment(SWT.CENTER);
	}

	/**
	 * Updates the operator and value column
	 * 
	 * @param value
	 * @param itemInfo
	 */
	private void updateVariableTableLine(AFiltreComposant value,
			TableItemInfo itemInfo) {
		// update the columns texts
		if (itemInfo.comboVarName == null) {
			itemInfo.createVariableNameComponent(value);
		}
		if (value.getNom().equals(TypeRepere.vitesseCorrigee.getName()))
			setComboText(
					itemInfo.comboVarName,
					GestionnaireDescripteurs
							.getDescripteurVariableAnalogique(
									TypeRepere.vitesseCorrigee.getCode())
							.getNomUtilisateur()
							.getNomUtilisateur(
									Activator.getDefault().getCurrentLanguage()));
		else if (value.getNom().equals(TypeRepere.distanceCorrigee.getName()))
			setComboText(
					itemInfo.comboVarName,
					GestionnaireDescripteurs
							.getDescripteurVariableAnalogique(
									TypeRepere.distanceCorrigee.getCode())
							.getNomUtilisateur()
							.getNomUtilisateur(
									Activator.getDefault().getCurrentLanguage()));
		else
			setComboText(itemInfo.comboVarName,
					getDisplayStringFromName(value.getNom()));
		itemInfo.item.setData(value);
		// if we have a remove row then we will put no combo for color
		if (value != removeRow) {
			if (itemInfo.comboColor == null) {
				itemInfo.createVariableColorComponent();
			}
			AFiltreComposant filtreColor = value.getEnfant(0);
			RGB rgbVal = GestionnaireCouleurs.getRgbForHexValue(filtreColor
					.getNom());
			Rectangle comboBounds = itemInfo.comboColor.getBounds();
			int height = comboBounds.height - 2;
			int width = comboBounds.width - 35; // 5 is assumed to be combo
			// arrow
			int lineHeight = 4;
			itemInfo.comboColor.setText("", GestionnaireCouleurs.createImage(
					getDisplay(), rgbVal, height, width, lineHeight));
			// itemInfo.comboColor.setText(
			// "", com.faiveley.samng.principal.ihm.Activator
			// .getImageDescriptor("/icons/toolBar/vues_commun_imprimer.png").createImage());
		}
	}

	/**
	 * When a change notification occured, the current editing items are checked
	 * to see if they changed from the original ones and according to the
	 * current state a change or restored notification is sent
	 * 
	 */
	private void onFilterChanged() {
		boolean listChanged = isDifferentFromInitialValues();
		this.isChangedStateFromInitial = listChanged;
		if (listChanged)
			firePropertyChange("CMB_SEL_CHANGED", null,
					GraphiqueFiltresEditorTable.this);
		else
			firePropertyChange("CMB_SEL_RESTORED", null,
					GraphiqueFiltresEditorTable.this);
	}

	/**
	 * Return the row index for the graphic line specified by the given graphic
	 * number
	 * 
	 * @param graphicNumero
	 * @return
	 */
	private int getGraphicItemInfoIndex(int graphicNumero) {
		TableItemInfo itemInfo;
		int itemInfoSize = this.itemsInfo.size();
		for (int i = 0; i < itemInfoSize; i++) {
			itemInfo = this.itemsInfo.get(i);
			if (itemInfo.getRowType() == ROW_TYPE.ROW_GRAPHIQUE) {
				if (((GraphiqueFiltreComposite) itemInfo.item.getData())
						.getNumero() == graphicNumero)
					return i;
			}
		}
		return -1;
	}

	/**
	 * Return the item info for the graphic line specified by the given graphic
	 * number
	 * 
	 * @param graphicNumero
	 * @return
	 */
	private TableItemInfo getGraphicItemInfo(int graphicNumero) {
		int idx = getGraphicItemInfoIndex(graphicNumero);
		if (idx != -1)
			return this.itemsInfo.get(idx);
		return null;
	}

	/**
	 * Creates a number of empty lines below the graphic line, according to the
	 * type of the graphic (for analogic are added 4 line and for discrete 10
	 * lines)
	 * 
	 * @param graphicNumero
	 */
	private void createEmptyLinesForGraphic(int graphicNumero,
			GraphiqueFiltreComposite gfc) {
		if (graphicNumero < 0
				|| graphicNumero >= GraphicConstants.MAX_GRAPHICS_COUNT)
			return;
		int itemsCount = gfc.getPossibleVariableLines();
		int firstVarPos = getGraphicItemInfoIndex(graphicNumero) + 1;
		addVariableRowItem(removeRow, firstVarPos, true);
		for (int i = 1; i < itemsCount; i++) {
			addVariableRowItem(null, firstVarPos + i, false);
		}
		notifyRefreshTableEditors();
		updateVariablesIndexes(firstVarPos, itemsCount);
	}

	/**
	 * Updates the indexes from the first column for itemsCount rows starting at
	 * firstVarPos
	 * 
	 * @param firstVarPos
	 * @param itemsCount
	 */
	private void updateVariablesIndexes(int firstVarPos, int itemsCount) {
		for (int i = 0; i < itemsCount; i++) {
			// update also the first column of the table
			itemsInfo.get(firstVarPos + i).item.setText(0,
					Integer.toString(i + 1));
		}
	}

	/**
	 * Return the number of the graphic according to the child variable line
	 * index (absolute index)
	 * 
	 * @param varLineIdx
	 * @return
	 */
	private int getGraphicParentForVariablePos(int varLineIdx) {
		TableItemInfo parentGraphicItem;
		for (int i = varLineIdx - 1; i >= 0; i--) {
			parentGraphicItem = this.itemsInfo.get(i);
			if (parentGraphicItem.getRowType() == ROW_TYPE.ROW_GRAPHIQUE)
				return i;
		}
		return -1;
	}

	/**
	 * Filters the possible values for a combo corresponding to a graphic
	 * 
	 * @param combo
	 * @param restore
	 */
	private void filterGraphicComboNames(ExtendedCombo combo, boolean restore,
			int choix) {
		try {
			TableItemInfo itemInfo = (TableItemInfo) combo.getData();
			AFiltreComposant filter = (AFiltreComposant) itemInfo.item
					.getData();
			if (((GraphiqueFiltreComposite) filter).getEnfantCount() > 0) {
				TypeVariable typeVariablePremiereLigneGraphique = GestionnairePool.getInstance()
						.getVariable(
								((GraphiqueFiltreComposite) filter)
										.getM_ALigneVariableFiltreComposant()
										.get(0).getNom()).getDescriptor()
						.getTypeVariable();
				if (typeVariablePremiereLigneGraphique == TypeVariable.VAR_ANALOGIC) {
					((GraphiqueFiltreComposite) filter)
							.setTypeGraphique(TypeGraphique.analogique);
				} else if (typeVariablePremiereLigneGraphique == TypeVariable.VAR_VIRTUAL
						|| typeVariablePremiereLigneGraphique == TypeVariable.VAR_DISCRETE) {
					((GraphiqueFiltreComposite) filter)
							.setTypeGraphique(TypeGraphique.digital);
				}
			}
			TypeGraphique graphicType = ((GraphiqueFiltreComposite) filter)
					.getTypeGraphique();
			String notUsedLabel = LabelForGraphic
					.getNotUsedLabelForGraphic(((GraphiqueFiltreComposite) filter)
							.getNumero());
			String analogicLabel = LabelForGraphic
					.getAnalogLabelForGraphic(((GraphiqueFiltreComposite) filter)
							.getNumero());
			String digitalLabel = LabelForGraphic
					.getDigitalLabelForGraphic(((GraphiqueFiltreComposite) filter)
							.getNumero());

			if (restore) {
				combo.removeAll();
				combo.setText(notUsedLabel);
				combo.add(notUsedLabel);
				combo.add(analogicLabel);
				combo.add(digitalLabel);
			} else {
				switch (choix) {
				case 0:
					combo.removeAll();
					combo.add(notUsedLabel);
					combo.setText(notUsedLabel);
					if (graphicType == TypeGraphique.analogique) {
						combo.add(analogicLabel);
					} else {
						combo.add(digitalLabel);
					}
					break;
				case 1:
					combo.removeAll();
					combo.setText(analogicLabel);
					combo.add(notUsedLabel);
					break;
				case 2:
					combo.removeAll();
					combo.setText(digitalLabel);
					combo.add(notUsedLabel);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Refreshes the editors for the table items when something changed (an
	 * insertion or removal of a line.
	 * 
	 */
	private void notifyRefreshTableEditors() {
		// The items editors must be notified that something changed
		int size = this.itemsInfo.size();
		TableItemInfo itemInfo;
		for (int i = 0; i < size; i++) {
			itemInfo = this.itemsInfo.get(i);
			if (itemInfo.editorVarName != null)
				itemInfo.editorVarName.setItem(itemInfo.item);
			if (itemInfo.editorColor != null)
				itemInfo.editorColor.setItem(itemInfo.item);
		}
	}

	/**
	 * Returns the graphic filter for a variable line specified by its name
	 * 
	 * @param itemInfo
	 * @return
	 */
	private AFiltreComposant getParentGraphicForLine(TableItemInfo itemInfo) {
		int currentLinePos = itemsInfo.indexOf(itemInfo);
		int parentPos = getGraphicParentForVariablePos(currentLinePos);
		TableItemInfo parentGraphicItemInfo = itemsInfo
				.get(getGraphicParentForVariablePos(itemsInfo.indexOf(itemInfo)));
		return (AFiltreComposant) parentGraphicItemInfo.item.getData();
	}

	/**
	 * Updates a line variable from a selection of a variable name in the
	 * variables combo
	 * 
	 * @param srcCombo
	 * @param oldValue
	 * @param newValue
	 */
	private void onNewVariableSelected(ExtendedCombo srcCombo, String oldValue,
			String newValue) {
		if (newValue == null) {
			srcCombo.setText(oldValue);
			srcCombo.setToolTipText(oldValue);
			return;
		}
		TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();

		setComboText(srcCombo, newValue);
		if (!newValue.equals(oldValue)) {
			int currentLinePos = itemsInfo.indexOf(itemInfo);
			int parentPos = getGraphicParentForVariablePos(currentLinePos);
			TableItemInfo parentGraphicItemInfo = itemsInfo.get(parentPos);
			AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
					.getData();
			TypeGraphique graphicType = ((GraphiqueFiltreComposite) graphic)
					.getTypeGraphique();
			String varName = getNameFromDisplayName(newValue,
					graphicType == TypeGraphique.analogique);
			int possValsCount = ((GraphiqueFiltreComposite) graphic)
					.getPossibleVariableLines();// (GraphiqueFiltreComposite)graphic);
			if ("".equals(oldValue.trim())) { // we had the last empty row
				// //$NON-NLS-1$
				AFiltreComposant newLineVar = new LigneVariableFiltreComposite();
				newLineVar.setNom(varName);
				// compute the new color
				AFiltreComposant newColor = new CouleurLigneVariable();
				List<RGB> availableColors = getAvailableGraphicColors(graphic,
						null);
				String color = Integer.toHexString(GestionnaireCouleurs
						.getIntValue(availableColors.get(0)));
				newColor.setNom(color);
				((CouleurLigneVariable) newColor).setValeurHexa(color);
				newLineVar.ajouter(newColor);
				graphic.ajouter(newLineVar);
				itemInfo.item.setData(newLineVar); // update the table item
				// data
				updateVariableTableLine(newLineVar, itemInfo);
				if (possValsCount > graphic.getEnfantCount())
					addVariableRowItem(removeRow, currentLinePos + 1, false); // :
			} else { // we have just a change of the name of the variable
				((AFiltreComposant) itemInfo.item.getData()).setNom(varName);
				// graphic.getEnfant(0).setNom(varName);
			}
			this.itemsInfo.get(currentLinePos).comboVarName.getArrow().setText(
					"-");
		}
		onFilterChanged();
	}

	/**
	 * Class for encapsulating an row in the table. It contains the editors and
	 * the item that is added to the row
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	public class TableItemInfo {
		public TableItem item;

		public TableEditor editorVarName;

		public ExtendedCombo comboVarName;

		public TableEditor editorColor;

		public ImageCombo comboColor;

		public TableItemInfo(AFiltreComposant rowData) {
			this(rowData, internalTable.getItemCount());
		}

		public TableItemInfo(AFiltreComposant rowData, int tablePos) {
			this.item = new TableItem(internalTable, SWT.NONE, tablePos);
			this.item.setData(rowData);
			ROW_TYPE dataType = getRowType();
			if (dataType != ROW_TYPE.ROW_VARIABLE) {
				// We have added a Graphic row
				this.editorVarName = new TableEditor(internalTable);
				this.editorVarName.grabHorizontal = true;
				this.comboVarName = new ExtendedCombo(internalTable, SWT.NONE,
						false);
				initEditorComponent(this.comboVarName);
				this.comboVarName.setTextBackground(ColorsConstants
						.getGraphicBackgroundColor());
				this.item.setBackground(ColorsConstants
						.getGraphicBackgroundColor());
				this.editorVarName.setEditor(this.comboVarName, this.item, 1);
				this.comboVarName.addSelectionListener(graphicsComboAdapter);
				this.comboVarName.addSelectionListener(notificationsAdapter);
				this.comboVarName
						.addPopupDissapearListener(popupDissapearListener);
			}
			GraphiqueFiltresEditorTable.this.itemsInfo.add(tablePos, this);
		}

		public void createVariableNameComponent(AFiltreComposant rowData) {
			this.item.setData(rowData);
			final TableItemInfo tablItemInf = this;
			this.editorVarName = new TableEditor(internalTable);
			this.editorVarName.grabHorizontal = true;
			this.comboVarName = new ExtendedCombo(internalTable, SWT.NONE, true);
			initEditorComponent(this.comboVarName);
			this.editorVarName.setEditor(this.comboVarName, this.item, 1);
			this.comboVarName.addSelectionListener(varNamesComboAdapter);
			this.comboVarName.addSelectionListener(notificationsAdapter);
			this.comboVarName.addPopupListener(comboVarNamesPopupListener);
			this.comboVarName.addPopupDissapearListener(popupDissapearListener);
			this.comboVarName.addKeyListener(new KeyListener() {

				public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.DEL) {
						comboVarName.select(0);
						removeVariableLineAt(tablItemInf, false);
					}
				}

				public void keyReleased(KeyEvent e) {
				}
			});
		}

		public void createVariableColorComponent() {
			this.editorColor = new TableEditor(internalTable);
			this.editorColor.grabHorizontal = true;
			this.comboColor = new ImageCombo(internalTable, SWT.NONE);
			initEditorComponent(this.comboColor);
			this.editorColor.setEditor(this.comboColor, this.item, 2);
			this.comboColor.addSelectionListener(comboColorsSelAdapter);
			this.comboColor.addSelectionListener(notificationsAdapter);
			this.comboColor.addPopupListener(comboColorsPopupListener);
			this.comboColor.addPopupDissapearListener(popupDissapearListener);
		}

		public void initEditorComponent(Scrollable widget) {
			widget.setData(this);
			GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
			gridData.grabExcessHorizontalSpace = true;
			widget.setLayoutData(gridData);
			widget.setBackground(internalTable.getBackground());
			widget.setFont(internalTable.getFont());
			widget.setForeground(internalTable.getForeground());
			if (widget instanceof ExtendedCombo)
				((ExtendedCombo) widget).setEditable(false);
		}

		public ROW_TYPE getRowType() {
			Object data = this.item.getData();
			if ((data != null) && (data instanceof GraphiqueFiltreComposite))
				return ROW_TYPE.ROW_GRAPHIQUE;
			return ROW_TYPE.ROW_VARIABLE;
		}
	}

	/**
	 * Listener to changes that notifies in turn the change listeners that the
	 * filter changed.
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class InputSelectionAdapter extends SelectionAdapter {
		@Override
		public void widgetSelected(SelectionEvent e) {
			super.widgetSelected(e);
			onFilterChanged();
		}
	}

	/**
	 * Listener for ExtendedCombo of the graphics column that handles the change
	 * of the name of a graphic
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class GraphicsComboSelectionAdapter extends SelectionAdapter {

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			super.widgetDefaultSelected(e);
		}

		private void notUsedToDigitAna_emptyGraph(
				GraphiqueFiltreComposite graphic, ExtendedCombo srcCombo,
				String selValue, int choix) {
			// a brand new graphic
			// First update the graphic name and then the combo possible
			// values
			graphic.setNom(selValue);
			((GraphiqueFiltreComposite) graphic).setActif(true);
			filterGraphicComboNames(srcCombo, false, choix);
			createEmptyLinesForGraphic(
					((GraphiqueFiltreComposite) graphic).getNumero(),
					(GraphiqueFiltreComposite) graphic);
		}

		private void notUsedToDigitAna(GraphiqueFiltreComposite graphic,
				ExtendedCombo srcCombo, int lineIdx, String selValue, int choix) {
			// name but with line
			// childrens
			// just disable the table items for this
			graphic.setNom(selValue);
			((GraphiqueFiltreComposite) graphic).setActif(true);
			filterGraphicComboNames(srcCombo, false, choix);
			try {
				for (int i = lineIdx + 1; i < lineIdx
						+ graphic.getPossibleVariableLines() + 1; i++)
					enableLine(itemsInfo.get(i), true);
			} catch (Exception e) {

			}
		}

		private void digitAnaToNotUsed_emptyGraph(
				GraphiqueFiltreComposite graphic, ExtendedCombo srcCombo,
				int lineIdx, String selValue) {
			// First update the combo and then update the graphic name
			filterGraphicComboNames(srcCombo, true, 0);
			graphic.setNom(selValue);
			((GraphiqueFiltreComposite) graphic).setActif(false);
			// update here also the combo possible values (add both
			// discrete and analogic)
			for (int i = lineIdx + graphic.getPossibleVariableLines(); i > lineIdx; i--)
				try {
					removeVariableLineAt(itemsInfo.get(i), true);
				} catch (Exception e) {

				}
		}

		private void digitAnaToNotUsed(GraphiqueFiltreComposite graphic,
				ExtendedCombo srcCombo, int lineIdx, String selValue) {
			graphic.setNom(selValue);
			((GraphiqueFiltreComposite) graphic).setActif(false);
			filterGraphicComboNames(srcCombo, false, 0);
			for (int i = lineIdx + 1; i < lineIdx
					+ graphic.getPossibleVariableLines() + 1; i++)
				try {
					enableLine(itemsInfo.get(i), false);
				} catch (Exception e) {

				}
		}

		public int interpreterChoixGraphe(ExtendedCombo combo,
				GraphiqueFiltreComposite graphic) {
			int selection = combo.getSelectionIndex();
			int nbChoix = combo.getItemCount();
			if (selection == 0) {
				return 0; // non utilisé
			} else {
				if (nbChoix == 3) {
					if (selection == 1) {
						graphic.setTypeGraphique(TypeGraphique.analogique);
						return 1; // analogique
					} else {
						graphic.setTypeGraphique(TypeGraphique.digital);
						return 2; // digital
					}
				} else { // nbchoix==2
					if (graphic.getTypeGraphique() == TypeGraphique.analogique) {
						return 1;
					} else if (graphic.getTypeGraphique() == TypeGraphique.digital) {
						return 2;
					}
				}
			}
			return 0;
		}

		public void widgetSelected(SelectionEvent event) {
			String oldValue = (String) event.data;

			ExtendedCombo srcCombo = (ExtendedCombo) event.getSource();
			String selValue = srcCombo.getText();
			if (selValue.equals(oldValue))
				return;
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();

			int lineIdx = itemsInfo.indexOf(itemInfo);
			GraphiqueFiltreComposite graphic = (GraphiqueFiltreComposite) itemInfo.item
					.getData();

			int interpret = interpreterChoixGraphe(srcCombo, graphic);

			switch (interpret) {
			case 0:
				if (graphic.getEnfantCount() > 0) {
					digitAnaToNotUsed(graphic, srcCombo, lineIdx, selValue);
				} else {
					digitAnaToNotUsed_emptyGraph(graphic, srcCombo, lineIdx,
							selValue);
				}
				break;
			case 1:
				if (graphic.getEnfantCount() > 0) {
					notUsedToDigitAna(graphic, srcCombo, lineIdx, selValue,
							interpret);
				} else {
					notUsedToDigitAna_emptyGraph(graphic, srcCombo, selValue,
							interpret);
				}
				break;
			case 2:
				if (graphic.getEnfantCount() > 0) {
					notUsedToDigitAna(graphic, srcCombo, lineIdx, selValue,
							interpret);
				} else {
					notUsedToDigitAna_emptyGraph(graphic, srcCombo, selValue,
							interpret);
				}
				break;
			default:
				break;
			}
		}
	};

	/**
	 * Listener for ExtendedCombo of the variables column that handles the
	 * change of the name of a variable for an item
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class VariableNamesComboSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			ExtendedCombo srcCombo = (ExtendedCombo) event.getSource();
			String oldValue = (String) event.data;
			int selection = srcCombo.getSelectionIndex();
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();

			if (selection == 0) {
				// if the user selected the remove variable value
				if ("".equals(oldValue.trim())) { //$NON-NLS-1$
					srcCombo.setText(""); //$NON-NLS-1$
					return;
				} else {
					removeVariableLineAt(itemInfo, false);
				}
			} else if (selection == 1) {
				try {
					RechercheDialog searchDlg = new RechercheDialog(
							getDisplay().getActiveShell(), true);
					searchDlg.setInputLabelText(searchDlgInputLabelText);
					searchDlg.setFilterText(searchFilter);
					searchDlg.setAppelant(this.getClass().getName());

					searchDlg.setTypeRecherche(Messages
							.getString("GraphiqueFiltresEditorTable.36"));

					AFiltreComposant parentGraphic = getParentGraphicForLine(itemInfo);
					TypeGraphique graphicType = ((GraphiqueFiltreComposite) parentGraphic)
							.getTypeGraphique();
					Map<String, DescripteurVariable> mapInitialOptions;
					if (graphicType == TypeGraphique.analogique)
						mapInitialOptions = initialAnalogOptionValues;
					else
						mapInitialOptions = initialDigitalOptionValues;

					List<String> valuesPresent = getCurrentVariablesNames();
					// The oldValue should be in this case searchString
					List<String> possibleValues = listDifference(
							mapInitialOptions.keySet(), valuesPresent, oldValue);
					possibleValues.remove(removeRowUid); // Remove the
					// removeRowString value
					searchDlg.setSelectableValues(possibleValues
							.toArray(new String[mapInitialOptions.size()]));
					String selValue = searchDlg.open();

					if (selValue.equals("")) {
						return;
					} else {

						isLastComboSearch = true;
						searchFilter = searchDlg.getFilterText(); // save the
						// filter
						// for further
						// searches

						int nbSelected = searchDlg.getSelectedValue().length;
						if (nbSelected > 1) {
							String selected[] = searchDlg.getSelectedValue();
							for (int i = 0; i < nbSelected; i++) {
								onNewVariableSelected(srcCombo, oldValue,
										selected[i]);
							}
						} else {
							onNewVariableSelected(srcCombo, oldValue, selValue);
						}
					}
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			} else {
				isLastComboSearch = false;
				onNewVariableSelected(srcCombo, oldValue, srcCombo.getText());
			}
		}
	}

	/**
	 * Listener for ExtendedCombo of the colors column that handles the change
	 * of the color of a variable for an item
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class ColorsComboSelectionAdapter extends SelectionAdapter {
		public void widgetSelected(SelectionEvent event) {
			ImageCombo srcCombo = (ImageCombo) event.getSource();
			Image oldValue = (Image) event.data;
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();
			Image selValue = srcCombo.getImage();
			if (selValue == oldValue)
				return;
			AFiltreComposant lineFilter = (AFiltreComposant) itemInfo.item
					.getData();
			int lineColor = GestionnaireCouleurs.getIntValue(selValue,
					srcCombo.getBounds().height);
			String strLineColor = Integer.toHexString(lineColor);
			CouleurLigneVariable colorFilter = (CouleurLigneVariable) lineFilter
					.getEnfant(0);
			colorFilter.setNom(strLineColor);
			colorFilter.setValeurHexa(strLineColor);
		}
	};

	/**
	 * Listener for ExtendedCombo components of the variables column that the
	 * combo is about to be displayed. This is used to dynamically populate the
	 * possible values (as the already added variables should not appear in the
	 * combo)
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class VariablesComboPopupListener implements ComboPopupShowListener {
		public void onComboPopupShowing(Widget widget) {
			if (!(widget instanceof ExtendedCombo)) {
				return;
			}

			ExtendedCombo srcCombo = (ExtendedCombo) widget;

			// set selection for this line
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();
			int currentLinePos = itemsInfo.indexOf(itemInfo);
			setSelectionIndex(currentLinePos);
			// TypeGraphique typeG=getTypeGraphiqueFromItem(itemsInfo,
			// currentLinePos);
			updateVarComboPossibleValues(srcCombo, srcCombo.getText());
			List<String> lStrings = new ArrayList<String>();

			if (itemInfo.comboVarName.getArrow().getText().equals("-")) {
				if (itemInfo.comboVarName.getArrow().isFocusControl()) {
					removeVariableLineAt(itemInfo, false);
				}
			} else {
				boolean usesShortNames = ActivatorVueGraphique.getDefault().isUsesShortNames();
				RechercheDialog dlg = new RechercheDialog(Activator
						.getDefault().getWorkbench().getActiveWorkbenchWindow()
						.getShell(), true, usesShortNames);
				dlg.setSelectableValues(getPossibleValues(srcCombo,
						srcCombo.getText(), lStrings, true));
				dlg.setAppelant(this.getClass().getName());
				dlg.setTypeRecherche("Variable"); //$NON-NLS-1$

				String selValue = dlg.open();
				if (selValue == null) {
					return;
				} else {
					isLastComboSearch = true;
					searchFilter = dlg.getFilterText(); // save the filter
					// for further
					// searches

					int nbSelected = 0;
					try {
						nbSelected = dlg.getSelectedValue().length;
					} catch (Exception e) {

					}
					if (nbSelected > 1) {
						int parentPos = getGraphicParentForVariablePos(currentLinePos);
						TableItemInfo parentGraphicItemInfo = itemsInfo
								.get(parentPos);
						AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
								.getData();
						int possValsCount = ((GraphiqueFiltreComposite) graphic)
								.getPossibleVariableLines();
						int valsSelected = ((GraphiqueFiltreComposite) graphic)
								.getEnfantCount();

						if (nbSelected > possValsCount - valsSelected) {
							MessageDialog
									.openWarning(
											Display.getCurrent()
													.getActiveShell(),
											com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages
													.getString("GraphiqueFiltresEditorTable.37"),
											com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages
													.getString("GraphiqueFiltresEditorTable.38"));
						} else {
							String selected[] = dlg.getSelectedValue();
							for (int i = 0; i < nbSelected; i++) {
								onNewVariableSelected(
										itemsInfo.get(currentLinePos + i).comboVarName,
										"", selected[i]);
							}
						}
					} else {
						onNewVariableSelected(srcCombo, srcCombo.getText(),
								selValue);
					}
				}
			}
		}

		public TypeGraphique getTypeGraphiqueFromItem(
				List<TableItemInfo> itemsInfo, int position) {
			boolean typeFind = false;
			int i = position;
			while (!typeFind && i >= 0) {
				if (itemsInfo.get(i).item.getData() instanceof GraphiqueFiltreComposite) {
					GraphiqueFiltreComposite graphic = (GraphiqueFiltreComposite) itemsInfo
							.get(i).item.getData();
					return graphic.getTypeGraphique();
				}
			}
			return null;
		}
	}

	/**
	 * Listener for ExtendedCombo components of the colors column that the combo
	 * is about to be displayed. This is used to dynamically populate the
	 * possible values (as the already added variables colors should not appear
	 * in the combo)
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class ColorsComboPopupListener implements ComboPopupShowListener {
		public void onComboPopupShowing(Widget widget) {
			if (!(widget instanceof ImageCombo)) {
				return;
			}

			// set selection for this line
			ImageCombo srcCombo = (ImageCombo) widget;
			TableItemInfo itemInfo = (TableItemInfo) srcCombo.getData();
			int currentLinePos = itemsInfo.indexOf(itemInfo);
			setSelectionIndex(currentLinePos);
			updateColorComboPossibleValues(srcCombo);
		}
	}

	/**
	 * Listener for a line selection
	 * 
	 * @author Cosmin Udroiu
	 * 
	 */
	private class LineSelectionListener implements ComboPopupDissapearListener {
		public void onComboPopupDissapear(Widget widget) {
			TableItemInfo itemInfo;
			if (widget instanceof ExtendedCombo) {
				itemInfo = (TableItemInfo) ((ExtendedCombo) widget).getData();
			} else {
				itemInfo = (TableItemInfo) ((ImageCombo) widget).getData();
			}
			int currentLinePos = itemsInfo.indexOf(itemInfo);
			setSelectionIndex(currentLinePos);
		}
	}

	/**
	 * Sets the selection in the table to the given index
	 * 
	 * @param index
	 */
	public void setSelectionIndex(int index) {
		internalTable.setSelection(index);
		int flags = computeMoveFlags(index);
		fireMoveOperations(flags);
	}

	/**
	 * Handler for the notification that a move operation was performed (due to
	 * pressing a button, for example). The flag will contain only one of the
	 * possible flags and not a mask
	 * 
	 * @param flag
	 *            the move operation flag (ex. MOVE_TOP, MOVE_UP etc.)
	 */
	public void moveSelection(int flag) {
		int index = internalTable.getSelectionIndex();
		if (index == -1)
			return;
		TableItemInfo itemInfo = itemsInfo.get(index);
		if (itemInfo.getRowType() == ROW_TYPE.ROW_GRAPHIQUE) {
			// switch between two graphics
			GraphiqueFiltreComposite graphicVar = (GraphiqueFiltreComposite) itemInfo.item
					.getData();
			int num = 0;
			switch (flag) {
			case MoveOperationsFlags.MOVE_TOP:
				num = graphicVar.getNumero();
				for (int i = 0; i < num; i++) {
					interchangeGraphics(graphicVar.getNumero() - 1,
							graphicVar.getNumero());
				}
				break;
			case MoveOperationsFlags.MOVE_UP:
				interchangeGraphics(graphicVar.getNumero() - 1,
						graphicVar.getNumero());
				break;
			case MoveOperationsFlags.MOVE_DOWN:
				interchangeGraphics(graphicVar.getNumero() + 1,
						graphicVar.getNumero());
				break;
			case MoveOperationsFlags.MOVE_BOTTOM:
				num = permanentGraphics.length - 1 - graphicVar.getNumero();
				for (int i = 0; i < num; i++) {
					interchangeGraphics(graphicVar.getNumero() + 1,
							graphicVar.getNumero());
				}
				break;
			}
		} else {
			// switch between two variables
			int parentGraphicPos = getGraphicParentForVariablePos(index);
			AFiltreComposant lineVar = (AFiltreComposant) itemInfo.item
					.getData();
			TableItemInfo parentGraphicItemInfo = this.itemsInfo
					.get(parentGraphicPos);
			AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
					.getData();

			int i = getVariableIndexInGraphic(graphic, lineVar);
			if (i != -1) {
				int num = 0;
				switch (flag) {
				case MoveOperationsFlags.MOVE_TOP:
					num = this.itemsInfo.indexOf(itemInfo) - parentGraphicPos
							- 1;
					for (int j = 0; j < num; j++) {
						interchangeVariableLines(index - j - 1, index - j);
					}
					break;
				case MoveOperationsFlags.MOVE_UP:
					interchangeVariableLines(index - 1, index);
					break;
				case MoveOperationsFlags.MOVE_DOWN:
					interchangeVariableLines(index + 1, index);
					break;
				case MoveOperationsFlags.MOVE_BOTTOM:
					num = graphic.getEnfantCount() + parentGraphicPos
							- (this.itemsInfo.indexOf(itemInfo));
					for (int j = 0; j < num; j++) {
						interchangeVariableLines(index + 1 + j, index + j);
					}
					break;
				}
			}
		}
	}

	/**
	 * Computes for the given index in the table the move operations that can be
	 * performed
	 * 
	 * @param index
	 *            index of the item in the table
	 * @return the move operations flags (a mask of all possible operations)
	 */
	private int computeMoveFlags(int index) {
		int moveFlags = MoveOperationsFlags.NO_MOVE;
		TableItemInfo itemInfo = itemsInfo.get(index);
		if (index == 0) {
			// we have a graphic selected
			moveFlags |= MoveOperationsFlags.MOVE_BOTTOM
					| MoveOperationsFlags.MOVE_DOWN;
		} else {
			if (itemInfo.getRowType() == ROW_TYPE.ROW_GRAPHIQUE) {
				// we have a graphic row
				if (itemInfo.item.getData() == permanentGraphics[permanentGraphics.length - 1]) {
					// we are on last graphic
					moveFlags |= MoveOperationsFlags.MOVE_UP
							| MoveOperationsFlags.MOVE_TOP;
				} else {
					moveFlags |= MoveOperationsFlags.MOVE_UP
							| MoveOperationsFlags.MOVE_TOP
							| MoveOperationsFlags.MOVE_DOWN
							| MoveOperationsFlags.MOVE_BOTTOM;
				}
			} else {
				// we have a line row
				int parentGraphicPos = getGraphicParentForVariablePos(index);
				AFiltreComposant lineVar = (AFiltreComposant) itemInfo.item
						.getData();
				TableItemInfo parentGraphicItemInfo = this.itemsInfo
						.get(parentGraphicPos);
				AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
						.getData();

				int i = getVariableIndexInGraphic(graphic, lineVar);
				if (i != -1) {
					// we have the first variable in graphic
					if (i == 0) {
						if (graphic.getEnfantCount() > 1) {
							moveFlags |= MoveOperationsFlags.MOVE_BOTTOM
									| MoveOperationsFlags.MOVE_DOWN;
						}
					} else if (i == graphic.getEnfantCount() - 1) {
						// we have the last variable in graphic
						moveFlags |= MoveOperationsFlags.MOVE_UP
								| MoveOperationsFlags.MOVE_TOP;
					} else {
						moveFlags |= MoveOperationsFlags.MOVE_UP
								| MoveOperationsFlags.MOVE_TOP
								| MoveOperationsFlags.MOVE_DOWN
								| MoveOperationsFlags.MOVE_BOTTOM;
					}
				}
			}
		}
		return moveFlags;
	}

	/**
	 * Interchange two variables lines (from a graphic) from the table
	 * 
	 * @param firstLine
	 *            the first line index
	 * @param secondLine
	 *            the second line index
	 */
	private void interchangeVariableLines(int firstLine, int secondLine) {
		int parentGraphicPos = getGraphicParentForVariablePos(firstLine);
		TableItemInfo parentGraphicItemInfo = this.itemsInfo
				.get(parentGraphicPos);
		AFiltreComposant graphic = (AFiltreComposant) parentGraphicItemInfo.item
				.getData();
		List<AFiltreComposant> childs = new ArrayList<AFiltreComposant>();
		for (int i = 0; i < graphic.getEnfantCount(); i++) {
			childs.add(graphic.getEnfant(i));
		}
		TableItemInfo firstItemInfo = itemsInfo.get(firstLine);
		TableItemInfo secondItemInfo = itemsInfo.get(secondLine);
		AFiltreComposant firstFilter = (AFiltreComposant) firstItemInfo.item
				.getData();
		AFiltreComposant secondFilter = (AFiltreComposant) secondItemInfo.item
				.getData();
		int firstIndex = getVariableIndexInGraphic(graphic, firstFilter);
		int secondIndex = getVariableIndexInGraphic(graphic, secondFilter);

		String firstItemText = firstItemInfo.comboVarName.getText();
		Image firstItemImage = firstItemInfo.comboColor.getImage();

		childs.set(firstIndex, secondFilter);
		childs.set(secondIndex, firstFilter);

		graphic.removeAll();
		for (int i = 0; i < childs.size(); i++) {
			graphic.ajouter(childs.get(i));
		}
		firstItemInfo.item.setData(secondFilter);
		firstItemInfo.comboVarName.setText(secondItemInfo.comboVarName
				.getText());
		firstItemInfo.comboColor.setText(
				"", secondItemInfo.comboColor.getImage()); //$NON-NLS-1$

		secondItemInfo.item.setData(firstFilter);
		secondItemInfo.comboVarName.setText(firstItemText);
		secondItemInfo.comboColor.setText("", firstItemImage); //$NON-NLS-1$

		setSelectionIndex(firstLine); // preserve selection

		linesInterchanged = true;
		onFilterChanged();
	}

	/**
	 * Interchange two graphics from the table
	 * 
	 * @param firstLine
	 *            the first line index
	 * @param secondLine
	 *            the second line index
	 */
	private void interchangeGraphics(int firstGraphicNr, int secondGraphicNr) {
		GraphiqueFiltreComposite firstGraphic = permanentGraphics[firstGraphicNr];
		GraphiqueFiltreComposite secondGraphic = permanentGraphics[secondGraphicNr];
		permanentGraphics[firstGraphicNr] = secondGraphic;
		permanentGraphics[firstGraphicNr].setNumero(firstGraphicNr);
		permanentGraphics[secondGraphicNr] = firstGraphic;
		permanentGraphics[secondGraphicNr].setNumero(secondGraphicNr);

		this.internalTable.removeAll();
		for (TableItemInfo info : this.itemsInfo) {
			if (info != null) {
				if (info.item != null)
					info.item.dispose();
				if (info.editorVarName != null)
					info.editorVarName.dispose();
				if (info.comboVarName != null)
					info.comboVarName.dispose();
				if (info.editorColor != null)
					info.editorColor.dispose();
				if (info.comboColor != null)
					info.comboColor.dispose();
			}
		}
		this.itemsInfo.clear();

		for (GraphiqueFiltreComposite value : permanentGraphics) {
			if (value.getEnfantCount() == 0
					&& value.getNom()
							.endsWith(
									com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.Messages
											.getString("GraphiqueFiltresEditorTable.3"))) {
				value.setActif(false);
				value.setTypeGraphique(null);
			}
			TypeGraphique graphicType = ((GraphiqueFiltreComposite) value)
					.getTypeGraphique();
			String newName = null;

			if (value.isActif()) {
				if (graphicType != null) {
					switch (graphicType) {
					case analogique:
						newName = LabelForGraphic
								.getAnalogLabelForGraphic(value.getNumero());
						break;
					case digital:
						newName = LabelForGraphic
								.getDigitalLabelForGraphic(value.getNumero());
						break;
					}
				} else
					newName = LabelForGraphic.getNotUsedLabelForGraphic(value
							.getNumero());

			} else {
				newName = LabelForGraphic.getNotUsedLabelForGraphic(value
						.getNumero());
			}
			value.setNom(newName);
			addGraphicRowItem(value);
			createRowsForGraphic(value);
		}

		setSelectionIndex(getGraphicItemInfoIndex(firstGraphicNr)); // preserve
		// selection
		linesInterchanged = true;
		onFilterChanged();
	}

	/**
	 * Returns the index of a line variable from a graphic The returned value is
	 * in fact the of the child filter inside the parent graphic filter
	 * 
	 * @param graphic
	 * @param lineVar
	 * @return
	 */
	private int getVariableIndexInGraphic(AFiltreComposant graphic,
			AFiltreComposant lineVar) {
		int i;
		// get the index of the variable in graphic
		for (i = 0; i < graphic.getEnfantCount(); i++) {
			if (lineVar == graphic.getEnfant(i)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Méthode permettatn de raffraichir le tableau de variables
	 * 
	 */
	public void raffraichirListeVariables() {
		updateMissingValues();
	}
}