package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import java.io.File;


public class TreeFolder extends TreeParent{
	public TreeFolder(String absoluteName) {
		super(absoluteName);
		this.name = new File(absoluteName).getName();
	}
}