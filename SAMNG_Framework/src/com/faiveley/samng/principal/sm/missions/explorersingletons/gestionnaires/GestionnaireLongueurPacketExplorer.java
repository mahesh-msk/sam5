package com.faiveley.samng.principal.sm.missions.explorersingletons.gestionnaires;

import com.faiveley.samng.principal.sm.parseurs.parseursJRU.GestionnaireLongueurPacket;

public class GestionnaireLongueurPacketExplorer extends GestionnaireLongueurPacket {

	private static GestionnaireLongueurPacketExplorer instance = new GestionnaireLongueurPacketExplorer();
	
	public static GestionnaireLongueurPacket getInstance(){
		return instance;
	}
}
