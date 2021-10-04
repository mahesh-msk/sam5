package com.faiveley.samng.vueexplorateur.ihm.vue.viewprovider;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.faiveley.samng.vueexplorateur.CommunicationFichiersVue;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeFolder;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeObject;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeParent;
import com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects.TreeRepository;
import com.faiveley.samng.vueexplorateur.util.WorkspaceService;

public 	class ViewContentProvider implements IStructuredContentProvider, ITreeContentProvider {

	private TreeParent invisibleRoot;

	public Object[] getElements(Object parent) {
		initialize();
		return getChildren(invisibleRoot);
	}
	public Object getParent(Object child) {
		if (child instanceof TreeObject) {
			return ((TreeObject)child).getParent();
		}
		return null;
	}
	
	public Object [] getChildren(Object parent) 
	{	
		if (parent instanceof TreeParent) 
		{			
			// If the parent is the repository (root folder) all the child folder content are already 
			// analyzed because of getListeFichiersMissions() call 
			if (!(parent instanceof TreeRepository))
			{
				TreeObject[] folderContent = ((TreeParent)parent).getChildren();			
			
				// For each element of the folder... 
				for (TreeObject element : folderContent)
				{					
					// 	If it's a folder that it seems not analyzed...
					if ((element instanceof TreeFolder) && !(((TreeFolder)element).hasChildren()))
					{			
						File url = new File(((TreeFolder)element).getAbsoluteName());
						
						if (!url.isHidden()){						
							// 	Check if this folder has been analyzed						
							CommunicationFichiersVue main = new CommunicationFichiersVue(url.getAbsolutePath());
							((TreeFolder)element).addChildren(main.getListeFichiersMissions(((TreeFolder)element).getAbsoluteName(), 1).getChildren());
						}
					}
				}
			}
								
			return ((TreeParent)parent).getChildren();
		}
		
		return new Object[0];
	}
	
	public boolean hasChildren(Object parent) {
		if (parent instanceof TreeParent)
			return ((TreeParent)parent).hasChildren();
		return false;
	}

	private void initialize() {
		
		String[] workspaceDirectories = WorkspaceService.instance.getWorkspaceDirectories();
		
		CommunicationFichiersVue[] comFichiersVues = new CommunicationFichiersVue[workspaceDirectories.length];
		TreeParent[] workspaceRoots = new TreeParent[workspaceDirectories.length];
		
		for(int i = 0; i < comFichiersVues.length; i++) {
			comFichiersVues[i] = new CommunicationFichiersVue(workspaceDirectories[i]);
			workspaceRoots[i] = comFichiersVues[i].getListeFichiersMissions(workspaceDirectories[i], 0);
		}
		
		invisibleRoot = new TreeParent("");
		
		for(TreeParent root : workspaceRoots) {
			invisibleRoot.addChild(root);
		}
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	public TreeParent getInvisibleRoot() {
		return invisibleRoot;
	}
	public void setInvisibleRoot(TreeParent invisibleRoot) {
		this.invisibleRoot = invisibleRoot;
	}
}
