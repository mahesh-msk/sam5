package com.faiveley.samng.principal.ihm.vues;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Abstract class for a label provider
 * Provides also abstract implementations for 
 * ITableLabelProvider, ITableColorProvider, ITableFontProvider
 * @author meggy
 *
 */
public class ATableLabelProvider extends LabelProvider 
	implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {

	private ImageRegistry imgRegistry;
	
	public ATableLabelProvider() {
		imgRegistry = new ImageRegistry();
		imgRegistry.put(ISharedImages.IMG_OBJ_ADD, PlatformUI.getWorkbench()
		.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_ADD));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		Row row = (Row) element;
		Image result = null;
		// La table est en deux parties : une partie fixe et une partie scrollable.
		// Pour chacune de ces parties, il y a une colonne vide qui est ajout�e au d�but.
		// Ce changement d'index permet de compenser les index de la partie scrollable pour se recaler sur les valeurs pr�sentes dans l'objet Row
		int realIndex = columnIndex - 2;
		if (realIndex >= 0 && realIndex < row.getVolatiles().length && row.isVolatile(realIndex)) {
			result = imgRegistry.get(ISharedImages.IMG_OBJ_ADD);	
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		Row row = ((Row)element);
		String str = "";
		//returns the current value on the specified column
		if(columnIndex >= 0 && row != null && row.getStrings() != null && columnIndex < row.getNbData()) {
			str =((Row)element).getValue(columnIndex);
		}
		return str;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getBackground(java.lang.Object, int)
	 */
	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableColorProvider#getForeground(java.lang.Object, int)
	 */
	public  Color getForeground(Object element, int columnIndex){
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableFontProvider#getFont(java.lang.Object, int)
	 */
	public Font getFont(Object element, int columnIndex) {
		return null;
	}
	
}
