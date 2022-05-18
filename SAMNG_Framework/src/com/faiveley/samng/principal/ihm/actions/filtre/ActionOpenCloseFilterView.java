package com.faiveley.samng.principal.ihm.actions.filtre;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.ihm.ICommandIds;
import com.faiveley.samng.principal.ihm.actions.vue.VueAction;

/**
 *  An action to close the filter view using only the visible attribute (to avoid to recreate filter contents).
 * @author Olivier Prouvost
 *
 */
public class ActionOpenCloseFilterView extends VueAction {
	public static final int ACTION_OPEN = 1;
	public static final int ACTION_CLOSE = 2;
	
	/** An helper to display the filter window (callable from differents places) */
	private ShowFilterWindowHelper helper;

	private int flags = ACTION_OPEN | ACTION_CLOSE;

	public ActionOpenCloseFilterView(String label, String viewId) {
		this(label, viewId, ACTION_OPEN | ACTION_CLOSE);
		helper = new ShowFilterWindowHelper(viewId);
	}

	/**
	 * 
	 * @param window
	 *            1
	 * @param label
	 *            1
	 * @param viewId
	 *            1
	 */
	public ActionOpenCloseFilterView(String label, String viewId, int flags) {
		super(null, ICommandIds.CMD_OPEN, label, viewId, null,true);
		helper = new ShowFilterWindowHelper(viewId);

		if(((flags & ACTION_OPEN) != 0) || ((flags & ACTION_CLOSE) != 0))
			this.flags = flags;
			
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/menuH_filtre.gif"));
	}
	
	/** */
	public final void run() {
		if((flags & ACTION_CLOSE) == ACTION_CLOSE) {
			
			helper.hideFilterWindow();
		}
		else
		{
			helper.showFilterWindow();

		}
		
	}
}
