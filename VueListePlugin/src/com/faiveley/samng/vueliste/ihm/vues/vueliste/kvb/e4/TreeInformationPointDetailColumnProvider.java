package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.e4;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.Messages;
import com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb.TableTreeInformationPointDetailContentProvider.XsmXcsInteger;

public class TreeInformationPointDetailColumnProvider extends ColumnLabelProvider{
	private int columnIndex;

	
	protected static final String LABEL_INFORMATION_POINT_XSM = Messages.getString("FixedColumnTableViewerDetailLabelInformationPointXsm");
	protected static final String LABEL_INFORMATION_POINT_XCS = Messages.getString("FixedColumnTableViewerDetailLabelInformationPointXcs");
	protected static final String LABEL_INFORMATION_POINT_XSMXCS = Messages.getString("FixedColumnTableViewerDetailLabelInformationPointXsmXcs");


	public TreeInformationPointDetailColumnProvider(int index)
	{
		columnIndex = index;
	}
	
	@Override
	public String getText(Object element) {
		return getColumnText(element, columnIndex);
	}
	

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}


	public String getColumnText(Object element, int columnIndex) {		
		String columnText = "";
		
		if (element instanceof XsmXcsInteger) {
			XsmXcsInteger xsmXcs = (XsmXcsInteger) element;
			String label = null;
			
			switch (xsmXcs.getDirection()) {
				case XSM:
					label = LABEL_INFORMATION_POINT_XSM;
					break;
				case XCS:
					label = LABEL_INFORMATION_POINT_XCS;
					break;
				case XSM_XCS:
					label = LABEL_INFORMATION_POINT_XSMXCS;
					break;
				default:
					break;
			}
					
			switch (columnIndex) {
				case 0:
					columnText = String.format(label, xsmXcs.getXsmXcs());
				case 1:
				case 2:
				case 3:
					break;
				default:
					break;
			}
		}
		
		if (element instanceof Marker) {
			Marker m = (Marker) element;
									
			switch (columnIndex) {
				case 0:
					break;
				case 1:
					columnText = getXYZ(m.getX());
					break;
				case 2:
					columnText = getXYZ(m.getY());
					break;
				case 3:
					columnText = getXYZ(m.getZ());
					break;
				default:
					break;
			}
		}
				
		return columnText;
	}
		
	private String getXYZ(int xyz) {		
		if (xyz == Marker.X_Y_Z_DEFAULT_VALUE) {
			return "";
		} else if (xyz == Marker.MARKER_M_VALUE) {
			return Marker.MARKER_M;
		} else {
			return String.valueOf(xyz);
		}
	}
}
