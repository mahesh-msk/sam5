package com.faiveley.samng.vueliste.ihm.vues.vueliste;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.ihm.vues.AVueTableLabelProvider;
import com.faiveley.samng.principal.ihm.vues.Row;
import com.faiveley.samng.principal.ihm.vues.VueTableColumnsIndices;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;

/**
 * 
 * @author Cosmin Udroiu
 * 
 */
public class VueListeLabelProvider extends AVueTableLabelProvider {
	private int posColEvent = -1;

	public VueListeLabelProvider() {
	}

	@Override
	public Color getForeground(Object element, int columnIndex) {
		Row row = (Row) element;
		Message msg = (Message) row.getData();

		if (msg.isErrorMessage()) {
			return new Color(Display.getCurrent(), 255, 0, 0);
		} else {
			return new Color(Display.getCurrent(), 0, 0, 0);
		}
	}

	@Override
	public Color getBackground(Object element, int columnIndex) {
		if (!isDisplayableColumnIndex(columnIndex))
			return null;
		columnIndex = getCorrectedColumnIndex(columnIndex);

		Color color = null;

		Message msg = (Message) ((Row) element).getData();
		Message lastRepere = this.vueData.getLastRepere0();
		// If a ref0 is set, overwrite all other colors
		if ((columnIndex <= this.lastFixedColumn)
				&& (columnIndex != this.posColEvent)) {
			color = this.vueData.getColor();
		}

		if (!msg.isRepereZero()
				&& ActivatorData.getInstance().getGestionnaireMarqueurs()
						.getMarqueurParId(msg.getMessageId()) == null
				&& (columnIndex == this.posColCorDist || columnIndex == this.posColCorTemp)) {
			color = this.vueData.createColor(new RGB(255, 183, 161));
		}

		if (msg != null && lastRepere == msg && msg.isRepereZero()) {
			color = this.vueData.getRepZeroColor();
		} else if (ActivatorData.getInstance().getGestionnaireMarqueurs()
				.getMarqueurParId(msg.getMessageId()) != null) {
			color = this.vueData.getMarkersColor();
		}

		return color;
	}

	@Override
	public Font getFont(Object element, int columnIndex) {
		if (!isDisplayableColumnIndex(columnIndex))
			return null;
		columnIndex = getCorrectedColumnIndex(columnIndex);

		Row row = (Row) element;
		Message msg = (Message) row.getData();

		Font f = null;
		if (msg.isEvNonDate() && (columnIndex == 1 || columnIndex == 2)) {
			f = this.vueData.getItalicFont();
		} else {
			if (this.posColEvent >= 0 && columnIndex == this.posColEvent) {
				f = this.vueData.getBoldFont();
			} else {
				f = this.vueData.getNormalFont();
			}
		}

		return f;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (!isDisplayableColumnIndex(columnIndex))
			return "";
		columnIndex = getCorrectedColumnIndex(columnIndex);
		Row row = ((Row) element);
		String str = "";

		// returns the current value on the specified column
		if (columnIndex >= 0 && row != null && columnIndex < row.getNbData()) {
			str = row.getValue(columnIndex);
		}

		return str;
	}

	@Override
	public void setColumnIndices(VueTableColumnsIndices columnsIndices) {
		this.lastFixedColumn = columnsIndices.getLastFixedColumn();
		this.posColCorDist = columnsIndices.getPosColCorDist();
		this.posColCorTemp = columnsIndices.getPosColCorTime();
		this.posColEvent = columnsIndices.getPosColEvent();
	}

	@Override
	protected int getCorrectedColumnIndex(int columnIndex) {
		if (columnIndex <= this.lastFixedColumn + 1) {
			columnIndex--;
		} else {
			columnIndex -= 1;
		}
		return columnIndex;
	}

	@Override
	protected boolean isDisplayableColumnIndex(int columnIndex) {
		if (columnIndex == 0)
			return false;
		return true;
	}
}
