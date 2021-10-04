package com.faiveley.samng.principal.ihm;

import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.IViewCategory;
import org.eclipse.ui.views.IViewDescriptor;
import org.eclipse.ui.views.IViewRegistry;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ViewsDescriptorsProvider {
	
	private ViewsDescriptorsProvider() {
	}
	
	public static IViewDescriptor[] getViewDescriptors(IWorkbenchWindow window, String categoryId) {
		IViewDescriptor [] views = null;
		IViewRegistry viewRegistry = window.getWorkbench().getViewRegistry();
		IViewCategory [] categories = viewRegistry.getCategories();
        for (int i = 0; i < categories.length; i++) {
        	views = categories[i].getViews();
        	if(views == null || views.length == 0)
        		continue;
        	if(categoryId.equals(categories[i].getId()))
        		return views;
        }
		
		return null;
	}
}
