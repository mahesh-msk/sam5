package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import java.util.List;


/**
 * Interface for listers interested in computed X position of
 * ref0 and markers x positions 
 * 
 * @author Cosmin Udroiu
 *
 */
public interface IGrapheInfosListener {
	public void onInfosChanged(Integer ref0XPos, List<Integer> markersXPos);
}
