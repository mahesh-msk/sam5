package com.faiveley.samng.vuetabulaire.ihm.vues.vuetabulaire;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;

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
public class VueTabulaireLabelProvider extends AVueTableLabelProvider {
	private int posColCorSpeed = -1;

	
	public VueTabulaireLabelProvider() {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	public Color getBackground(Object element, int columnIndex) {
		//first check if the column index is not the index of a dummy added column
		if(!isDisplayableColumnIndex(columnIndex))
			return null;
		//as we are adding empty columns to solve first column text dissapearence,
		//we must get the corrected column index
		columnIndex = getCorrectedColumnIndex(columnIndex);

		Color color = null;
		
		//If a ref0 is set, overwrite all other colors
		Message msg = (Message)((Row)element).getData();
		
		Message lastRepere = this.vueData.getLastRepere0();

		 if (columnIndex <= this.lastFixedColumn) {
				color = this.vueData.getColor();	//return gray color for the fixed columns
			}
		
		//get the color for the correction columns
		if (!msg.isRepereZero() && ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msg.getMessageId())==null &&  (columnIndex == this.posColCorDist ||
				columnIndex == this.posColCorTemp ||
				columnIndex == this.posColCorSpeed)) {
			color = this.vueData.createColor(new RGB(255, 183, 161));
		}
		if(msg != null && lastRepere == msg && msg.isRepereZero()) {
			color = this.vueData.getRepZeroColor();	//return color for ref0
		} else if(ActivatorData.getInstance().getGestionnaireMarqueurs().getMarqueurParId(msg.getMessageId())!=null) {
			color = this.vueData.getMarkersColor();
		}

//		} else if (columnIndex <= this.lastFixedColumn) {
//			color = this.vueData.getColor();	//return gray color for the fixed columns
//		}
		
		return color;			
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
	 */
	public Font getFont(Object element, int columnIndex) {
		//first check if the column index is not the index of a dummy added column
		if(!isDisplayableColumnIndex(columnIndex))
			return null;
		//as we are adding empty columns to solve first column text disappearance,
		//we must get the corrected column index
		columnIndex = getCorrectedColumnIndex(columnIndex);

		Font f;
		Row row=(Row)element;
		Message msg=(Message)row.getData();
		if (msg.isEvNonDate() && (columnIndex==1 || columnIndex==2)) {
			f = this.vueData.getItalicFont();
		}else{
			if(columnIndex < 0 || columnIndex >= ((Row)element).getNbData())
				f =  this.vueData.getNormalFont();
			else {
				if(((Row)element).isCellInvalid(columnIndex) || columnIndex <= this.lastFixedColumn) {	//check for a propagated value
					f = this.vueData.getNormalFont();
				} else {
					f = this.vueData.getBoldFont();
				}
			}
		}
		return f;
	}
	
	public String getColumnText(Object element, int columnIndex) {
		//first check if the column index is not the index of a dummy added column
		if(!isDisplayableColumnIndex(columnIndex))
			return "";
		//as we are adding empty columns to solve first column text disappearance,
		//we must get the corrected column index
		columnIndex = getCorrectedColumnIndex(columnIndex);
		Row row = ((Row)element);
		String str = "";
		//returns the current value on the specified column
		if(columnIndex >= 0 && row != null && columnIndex < row.getNbData()) {
			str =((Row)element).getValue(columnIndex);
		}
		return str;
	}

	@Override
	public void setColumnIndices(VueTableColumnsIndices columnsIndices) {
		this.lastFixedColumn = columnsIndices.getLastFixedColumn();
		this.posColCorDist = columnsIndices.getPosColCorDist();
		this.posColCorSpeed = columnsIndices.getPosColCorSpeed();
		this.posColCorTemp = columnsIndices.getPosColCorTime();
	}		
}
