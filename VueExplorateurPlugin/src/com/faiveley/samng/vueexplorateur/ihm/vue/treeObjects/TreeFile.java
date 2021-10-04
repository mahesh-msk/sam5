package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import java.io.File;


public class TreeFile extends TreeParent{
	public TreeFile(String absoluteName) {
		super(absoluteName);
		this.name = new File(absoluteName).getName();	
	}
}