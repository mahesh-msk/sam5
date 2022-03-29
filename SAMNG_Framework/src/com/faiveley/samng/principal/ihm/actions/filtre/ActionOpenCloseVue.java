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
 * 
 * @author Cosmin Udroiu
 *
 */
public class ActionOpenCloseVue extends VueAction {
	public static final int ACTION_OPEN = 1;
	public static final int ACTION_CLOSE = 2;
	
	private int flags = ACTION_OPEN | ACTION_CLOSE;

	public ActionOpenCloseVue(String label, String viewId) {
		this(label, viewId, ACTION_OPEN | ACTION_CLOSE);
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
	public ActionOpenCloseVue(String label, String viewId, int flags) {
		super(null, ICommandIds.CMD_OPEN, label, viewId, null,true);
		if(((flags & ACTION_OPEN) != 0) || ((flags & ACTION_CLOSE) != 0))
			this.flags = flags;
			
		setImageDescriptor(com.faiveley.samng.principal.ihm.Activator
				.getImageDescriptor("/icons/menuH_filtre.gif"));
	}
	
	/** */
	public final void run() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow();
		try {
			IViewReference[] ivr = window.getActivePage()
									.getViewReferences();
			IViewReference viewRef = null;
			for (int i = 0; i < ivr.length; i++) {
				if (ivr[i].getId().equals(getViewId())) {
					viewRef = ivr[i];
					break;
				}
			}

			if (viewRef != null) {
				if((flags & ACTION_CLOSE) == ACTION_CLOSE) {
					IWorkbenchPart viewPart = viewRef.getPart(false); 
					if((viewPart != null) && (viewPart instanceof ISaveablePart2)) {
						if(!((ISaveablePart2)viewPart).isSaveOnCloseNeeded())
							return;
					}
//					window.getActivePage().hideView(viewRef);
					for (int i = 0; i < window.getActivePage().getViewReferences().length; i++) {
						if (window.getActivePage().getViewReferences()[i].equals(viewRef)) {
							if (window.getActivePage().getViewReferences()[i].isFastView()) {
							IWorkbenchPage page =
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
								IWorkbenchPartReference myView = page.findViewReference(window.getActivePage().getViewReferences()[i].getId());
								page.setPartState(myView, IWorkbenchPage.STATE_MINIMIZED);
							}else{
								window.getActivePage().hideView(viewRef);
							}
						}
					}

					
				}
			} else {
				if((flags & ACTION_OPEN) == ACTION_OPEN)
					window.getActivePage().showView(getViewId());
			}
			
		} catch (PartInitException e) {
			MessageDialog.openError(window.getShell(), "Error",
					"Error opening view:" + e.getMessage());
		}
	}
}
