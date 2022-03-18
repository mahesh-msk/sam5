package com.faiveley.samng.principal.ihm.actions.filtre;

import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MPartStack;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

/**
 * An helper class to show any filter view in a separate window (Issue 1104)
 *  
 * @author Olivier Prouvost 
 *
 */
public class ShowFilterWindowHelper {

	private static final String VUE_FILTRE_LIST_PARK_STACK_ID = "filter.partStack"; //$NON-NLS-1$
	
	/** The filter view ID to be displayed in a separate window */
	private String filterViewID = null; 
	
	/** There is one window per filter type. Compute its ID automatically */
	private String filterWindowID = null; 
	
	public ShowFilterWindowHelper(String viewID)
	{
		filterViewID = viewID;
		filterWindowID = "window.for." + viewID; //$NON-NLS-1$
	}
	
	/** Show the filter window : create it if not still exists */
	public void showFilterWindow() {
		
		IEclipseContext ctx = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		MApplication appli = ctx.get(MApplication.class);
		EModelService ms = ctx.get(EModelService.class);
		EPartService ps = ctx.get(EPartService.class);
		
		MWindow filtreWindow = prepareFiltreListeWindow(appli, ms);

		MPartStack partStack = (MPartStack) ms.find(VUE_FILTRE_LIST_PARK_STACK_ID, filtreWindow);

		MPart p = ps.findPart(filterViewID);
		if (p == null) {
			// Create the part in the spyWindow...
			p = ps.createPart(filterViewID);
			partStack.getChildren().add(p);
			partStack.setSelectedElement(p);
		}

		p.setVisible(true);
		ps.activate(p, true);

	}

	/**
	 * Prepare the filter window : create or get it. There is 1 window per filter but each window has the same title
	 *
	 * @param appli
	 * @param modelService
	 * @return the filter window found in fragment.
	 */

	private MWindow prepareFiltreListeWindow(MApplication appli, EModelService modelService) {

		// If window already in application, it is already prepared -> Return it
		List<MWindow> existingWindow = modelService.findElements(appli, filterWindowID, MWindow.class, null);
		if (existingWindow.size() >= 1)
			return existingWindow.get(0);

		// No filter list window in main windows for the moment... create dynamically this window. 
		MTrimmedWindow window = modelService.createModelElement(MTrimmedWindow.class);
		window.setElementId(filterWindowID);
		window.setLabel(Messages.ShowFilterWindowAction_0);
		
		MPartStack ps = modelService.createModelElement(MPartStack.class);
		ps.setElementId(VUE_FILTRE_LIST_PARK_STACK_ID);
		window.getChildren().add(ps);
	
//		MTrimBar trimBar = tw.getTrimBars().stream().filter(t -> t.getSide() == SideValue.TOP).findFirst().get();
//		MToolBar toolbar = (MToolBar) trimBar.getChildren().get(0);

		
		// Can not use move here because it is only for MWindowElement
		centerFilterWindow(appli, window);
		appli.getChildren().get(0).getWindows().add(window);

		return window;

	}

	/**
	 * Make the filter window centered on top of main window.
	 *
	 * @param appli
	 *            current appli
	 * @param tw
	 *            main trim window
	 */
	private void centerFilterWindow(MApplication appli, MTrimmedWindow tw) {
		MWindow mainWindow = appli.getChildren().get(0);
		
		// Set the size of filter window to : 50 % for width, 70 % for height
		// Set the position in center 
		int filterW = (int) (mainWindow.getWidth() * 0.5f);
		int filterH = (int) (mainWindow.getHeight() * 0.7f);
		
		int filterX = (int) (mainWindow.getX() + (mainWindow.getWidth() * 0.15d));
		int filterY = (int) (mainWindow.getY() + (mainWindow.getHeight() * 0.2d));
		

		tw.setX(filterX);
		tw.setY(filterY);
		tw.setWidth(filterW);
		tw.setHeight(filterH);
	}
	
	
	
	
}
