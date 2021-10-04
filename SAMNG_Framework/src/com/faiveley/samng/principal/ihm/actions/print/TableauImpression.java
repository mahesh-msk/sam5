package com.faiveley.samng.principal.ihm.actions.print;

import java.awt.print.Printable;
import java.text.MessageFormat;
import java.util.List;

import javax.swing.JTable;

import com.faiveley.samng.principal.ihm.vues.configuration.ConfigurationColonne;

public class TableauImpression extends JTable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4596208020674225193L;
	private List<String> columnNames;
	private List<ConfigurationColonne> configColonnes;

	public List<String> getColumnNames() {
		return columnNames;
	}


	public void setColumnNames(List<String> columnNames) {
		this.columnNames = columnNames;
	}


	public List<ConfigurationColonne> getConfigColonnes() {
		return configColonnes;
	}


	public void setConfigColonnes(List<ConfigurationColonne> configColonnes) {
		this.configColonnes = configColonnes;
	}


	public TableauImpression(Object[][] rowData, Object[] columnNamesTab, List<String> columnNames ,
			List<ConfigurationColonne> configColonnes) {
		super(rowData, columnNamesTab);
		this.columnNames = columnNames;
		this.configColonnes = configColonnes;

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		List<Integer> tailleColonnes=ImpressionVueTableau.getGoodTailleColonnes(this.getColumnNames(), this.getConfigColonnes());

		for (int i = 0; i < this.getColumnCount(); i++) {
			int largeur=tailleColonnes.get(i)+5;
			this.getColumnModel().getColumn(i).setPreferredWidth(largeur);
			this.getColumnModel().getColumn(i).setResizable(false);
			this.getColumnModel().getColumn(i).setMinWidth(largeur);
			this.getColumnModel().getColumn(i).setMaxWidth(largeur);
		}
	}

	@Override
	public Printable getPrintable(PrintMode printMode, MessageFormat headerFormat, MessageFormat footerFormat) {
		return new TablePrintable(this, printMode);
	}

}
