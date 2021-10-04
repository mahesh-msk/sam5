package com.faiveley.samng.principal.sm.missions.explorersingletons.data;

import com.faiveley.samng.principal.sm.data.enregistrement.Util;

public class UtilExplorer extends Util {

	private static UtilExplorer instance = new UtilExplorer();
	
	public static Util getInstance(){
		return instance;
	}
}
