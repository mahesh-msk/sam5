package com.faiveley.samng.principal.sm.missions.explorersingletons.print;

import com.faiveley.samng.principal.ihm.vues.search.TableResultats;

public class TableResultatsExplorer extends
		com.faiveley.samng.principal.ihm.vues.search.TableResultats {

	private static final TableResultatsExplorer INSTANCE = new TableResultatsExplorer();
	
	public static TableResultats getInstance() {
		return INSTANCE;
	}
}
