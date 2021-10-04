package com.faiveley.samng.vueliste.ihm.vues.vueliste.kvb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.faiveley.kvbdecoder.model.kvb.event.NumericalEvent;
import com.faiveley.kvbdecoder.model.kvb.ip.InformationPoint;

public class TableTreeInformationPointDetailContentProvider implements ITreeContentProvider {
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof InformationPoint) {
			InformationPoint ip = (InformationPoint) inputElement;
			
			List<Object> childs = new ArrayList<Object>(ip.getMarkers());
			
			if (ip.getParent().getParent() instanceof NumericalEvent) {
				int xsm = ip.getXsm();
				int xcs = ip.getXcs();
	
				if (xsm != InformationPoint.XSM_XCS_DEFAULT_VALUE) {
					if (xsm == xcs) {
						childs.add(0, new XsmXcsInteger(Direction.XSM_XCS, xsm));
					} else {
						childs.add(0, new XsmXcsInteger(Direction.XSM, xsm));
					}
				} else if (xcs !=  InformationPoint.XSM_XCS_DEFAULT_VALUE) {
					childs.add(0, new XsmXcsInteger(Direction.XCS, xcs));
				}
			}
			
			return childs.toArray();
		}
		
		return null;
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}
	
	public enum Direction {
		XSM,
		XCS,
		XSM_XCS;
	}
	
	/**
	 * Classe identique à la classe Integer.
	 * Elle permet d'avoir des instances différenciables d'instances de Integer simple (ou d'instances d'autres classes construites de la même manière que XsmXcsInteger).
	 * Cela est utile dans le cas d'un contexte où les objets sont seulement connus comme étant Object. Exemple: ITableLabelProvider. 
	 */
	public class XsmXcsInteger {
		private Direction direction;
		private int xsmxcs;
		
		public XsmXcsInteger(Direction direction, int xsmxcs) {
			this.direction = direction;
			this.xsmxcs = xsmxcs;
		}
		
		public int getXsmXcs() {
			return this.xsmxcs;
		}
		
		public Direction getDirection() {
			return this.direction;
		}
	}
}
