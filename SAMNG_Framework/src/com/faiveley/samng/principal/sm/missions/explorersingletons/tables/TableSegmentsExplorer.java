package com.faiveley.samng.principal.sm.missions.explorersingletons.tables;

import com.faiveley.samng.principal.sm.segments.TableSegments;

public class TableSegmentsExplorer extends TableSegments {
	
	private static TableSegmentsExplorer instance = new TableSegmentsExplorer();
	
	public static TableSegments getInstance(){
		return instance;
	}
}
