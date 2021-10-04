package com.faiveley.samng.principal.sm.missions.explorersingletons.tables;

import com.faiveley.samng.principal.sm.segments.ruptures.TableRuptures;

public class TablesRupturesExplorer extends TableRuptures {
	
	private static TablesRupturesExplorer instance = new TablesRupturesExplorer();
	
	public static TableRuptures getInstance() {
		return instance;
	}
}
