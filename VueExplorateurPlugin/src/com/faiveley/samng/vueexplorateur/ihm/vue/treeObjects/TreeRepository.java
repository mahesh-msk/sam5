package com.faiveley.samng.vueexplorateur.ihm.vue.treeObjects;

import java.io.File;


public class TreeRepository extends TreeParent{
	public TreeRepository(String absoluteName) {
		super(absoluteName);
		this.name = new File(absoluteName).getName();
	}
}
