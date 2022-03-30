package com.faiveley.samng.principal.ihm;

import java.util.TimeZone;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

import com.faiveley.samng.principal.sm.linecommands.GestionLineCommandParameters;

/** This class controls all aspects of the application's execution. */
public class Application implements IApplication {

	
	public Object startOfficiel(IApplicationContext context) throws Exception {
		Display display = PlatformUI.createDisplay();
		try {
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
		
	}

	
	
	
	public Object start(IApplicationContext context) throws Exception {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		GestionLineCommandParameters.gestionLineCommandParameters();

		Display display = PlatformUI.createDisplay();
		try {
			new Thread("GC-Thread") {
				@Override
				public void run() {
					while (true) {
						//System.out.println("Memory before gc(kb) : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000));

						System.gc();
						//System.out.println("Memory after gc(kb): " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1000));

						try {
							sleep(60 * 1000);
							//sleep(10 * 1000);
						} catch (InterruptedException e) {
						}
					}
				}
			}.start();
			
			int returnCode = PlatformUI.createAndRunWorkbench(display, new ApplicationWorkbenchAdvisor());
			if (returnCode == PlatformUI.RETURN_RESTART)
				return IApplication.EXIT_RESTART;
			else
				return IApplication.EXIT_OK;
		} finally {
			display.dispose();
		}
	}

	@Override
	public void stop() {
		if (!PlatformUI.isWorkbenchRunning())
			return;
		final IWorkbench workbench = PlatformUI.getWorkbench();
		final Display display = workbench.getDisplay();
		display.syncExec(() -> {
			if (!display.isDisposed())
				workbench.close();
		});
	}
}