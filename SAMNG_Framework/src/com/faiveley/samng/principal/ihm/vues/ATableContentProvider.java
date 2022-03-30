package com.faiveley.samng.principal.ihm.vues;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * Abstract class for a content provider used by views taht needs a table
 * 
 * @author meggy
 * 
 */
public abstract class ATableContentProvider implements
		IStructuredContentProvider {

	// the elements of the table
	protected Object[] elements = null;

	public abstract void loadContent(IProgressMonitor monitor);

	@Override
	public Object[] getElements(Object inputElement) {
		return elements == null ? new Object[0] : elements;
	}

	@Override
	public void dispose() {
		this.elements = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}