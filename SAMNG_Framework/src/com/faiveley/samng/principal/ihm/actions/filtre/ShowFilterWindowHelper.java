package com.faiveley.samng.principal.ihm.actions.filtre;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.workbench.IPresentationEngine;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.e4.ui.workbench.modeling.ISaveHandler;
import org.eclipse.e4.ui.workbench.modeling.IWindowCloseHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.PlatformUI;

/**
 * An helper class to show any filter view in a separate window (Issue 1104)
 * 
 * @author Olivier Prouvost
 *
 */
public class ShowFilterWindowHelper {

	private static final String WINDOW_ID_PREFIX = "filterwindow.";

	/** The filter view ID to be displayed in a separate window */
	private String filterViewID = null;

	/** There is one window per filter type. Compute its ID automatically */
	private String filterWindowID = null;

	public ShowFilterWindowHelper(String viewID) {
		filterViewID = viewID;
		filterWindowID = WINDOW_ID_PREFIX + viewID; // $NON-NLS-1$
	}

	/** Show the filter window : create it if not still exists */
	public void showFilterWindow() {

		IEclipseContext ctx = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		MApplication appli = ctx.get(MApplication.class);
		EModelService ms = ctx.get(EModelService.class);
		EPartService ps = ctx.get(EPartService.class);

		MWindow filtreWindow = prepareFilterListWindow(appli, ms);

		MPart p = filtreWindow.getChildren().isEmpty() ? null : (MPart) filtreWindow.getChildren().get(0);
		if (p == null) {
			// Create the part in the spyWindow... (not closable)
			p = ps.createPart(filterViewID);
			p.setCloseable(false);
			filtreWindow.getChildren().add(p);
			filtreWindow.setLabel(p.getLabel());
			filtreWindow.setSelectedElement(p);
			ps.activate(p, true);
		} else {
			filtreWindow.setVisible(true);
		}

		// Override close handler once it is visible and if it has a context
		if (filtreWindow.getContext() != null) {
			filtreWindow.getContext().set(IWindowCloseHandler.class, window1 -> {
				System.out.println("ENter in my close");
				window1.setVisible(false);
				return false;
			});
		}

	}

	/** Hide the filter window : keep it in the background and do not deactivate */
	public void hideFilterWindow() {

		IEclipseContext ctx = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		MApplication appli = ctx.get(MApplication.class);
		EModelService ms = ctx.get(EModelService.class);

		MWindow filtreWindow = getFilterWindow(appli, ms);

		if (filtreWindow != null) {
			filtreWindow.setVisible(false);
		}
	}

	/**
	 * Prepare the filter window : create or get it. There is 1 window per filter
	 * but each window has the same title
	 *
	 * @param appli
	 * @param modelService
	 * @return the filter window found in fragment.
	 */

	private MWindow prepareFilterListWindow(MApplication appli, EModelService modelService) {

		// If window already in application, it is already prepared -> Return it
		MWindow existingWindow = getFilterWindow(appli, modelService);
		if (existingWindow != null)
			return existingWindow;

		// No filter list window in main windows for the moment... create dynamically
		// this window.
		MTrimmedWindow window = modelService.createModelElement(MTrimmedWindow.class);
		window.setElementId(filterWindowID);
		// Make filter dialog shell modal (->> Must add property in get persisted state
		// !!)...
		window.getPersistedState().put(IPresentationEngine.STYLE_OVERRIDE_KEY,
				String.valueOf(SWT.APPLICATION_MODAL | SWT.SHELL_TRIM | SWT.TITLE));

		// Can not use move here because it is only for MWindowElement
		centerFilterWindow(appli, window);
		appli.getChildren().get(0).getWindows().add(window);

		return window;

	}

	private MWindow getFilterWindow(MApplication appli, EModelService modelService) {

		// If window already in application, it is already prepared -> Return it
		List<MWindow> existingWindows = modelService.findElements(appli, filterWindowID, MWindow.class, null);
		return (existingWindows.size() >= 1) ? existingWindows.get(0) : null;
	}

	/**
	 * Make the filter window centered on top of main window.
	 *
	 * @param appli current appli
	 * @param tw    main trim window
	 */
	private void centerFilterWindow(MApplication appli, MTrimmedWindow tw) {
		MWindow mainWindow = appli.getChildren().get(0);

		Monitor m = Display.getCurrent().getPrimaryMonitor();
		Rectangle carea = m.getClientArea();

		// Set the size of filter window to : 50 % for width, 70 % for height
		// Set the position in center at 0.15 of width et 0.2 of height.
		int filterW = (int) (mainWindow.getWidth() * 0.5f);
		int filterH = (int) (mainWindow.getHeight() * 0.7f);

		// Use a real X and Y coming from application or from monitor
		// X and Y could be not set.
		int realX = mainWindow.isSetX() ? mainWindow.getX() : carea.x;
		int realY = mainWindow.isSetY() ? mainWindow.getY() : carea.y;

		int filterX = (int) (realX + (mainWindow.getWidth() * 0.15d));
		int filterY = (int) (realY + (mainWindow.getHeight() * 0.2d));

		tw.setX(filterX);
		tw.setY(filterY);
		tw.setWidth(filterW);
		tw.setHeight(filterH);
	}

	/**
	 * When file is closed, must totally remove the existing filter windows to be
	 * sure that they will be created from scratch
	 */
	public static void removeAllFilterWindowsInApplication() {
		IEclipseContext ctx = PlatformUI.getWorkbench().getService(IEclipseContext.class);
		MApplication appli = ctx.get(MApplication.class);
		EModelService ms = ctx.get(EModelService.class);

		List<MWindow> windowToBeDeleted = new ArrayList<>();
		for (MWindow w : appli.getChildren().get(0).getWindows()) {
			if (w.getElementId().startsWith(WINDOW_ID_PREFIX))
				windowToBeDeleted.add(w);
		}

		// Remove this window from model.
		for (MWindow w : windowToBeDeleted) {
			appli.getChildren().get(0).getWindows().remove(w);
		}

	}

}
