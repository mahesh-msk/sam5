package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe;

import java.util.ArrayList;
import java.util.List;

import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur.CursorPositionEvent;


/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class GestionnaireGraphesNotifications {
	private static GestionnaireGraphesNotifications instance;
	private List<IGrapheCursorListener> cursorListeners = new ArrayList<IGrapheCursorListener>();
	private List<IGrapheInfosListener> infosListeners = new ArrayList<IGrapheInfosListener>();
	private GestionnaireGraphesNotifications() {
	}
	
	public static GestionnaireGraphesNotifications getInstance() {
		if(instance == null)
			instance = new GestionnaireGraphesNotifications();
		return instance;
	}
	
	public void clear(){
	//TAGQQQ 
		cursorListeners.clear();
		infosListeners.clear();
//CHECK01
	}
	
	public static void addGrapheCursorListener(IGrapheCursorListener listener) {
		if(listener != null)
			getInstance().cursorListeners.add(listener);
	}
	
	public static void removeGrapheCursorListener(IGrapheCursorListener listener) {
		if(listener != null)
			getInstance().cursorListeners.remove(listener);
	}

	public static void addGrapheInfosListener(IGrapheInfosListener listener) {
		if(listener != null)
			getInstance().infosListeners.add(listener);
	}
	
	public static void removeGrapheInfosListener(IGrapheInfosListener listener) {
		if(listener != null)
			getInstance().infosListeners.remove(listener);
	}

	public static void notifyCursorPositionChanged(CursorPositionEvent event) {
		try {
			List<IGrapheCursorListener> listeners = getInstance().cursorListeners;
			for(IGrapheCursorListener l: listeners) {
				l.cursorPositionChanged(event);
			}
		} catch (Exception e) {

		}	
	}
	
	public static void notifyInfosChanged(Integer ref0XPos, List<Integer> markersXPos) {
		List<IGrapheInfosListener> listeners = getInstance().infosListeners;
		for(IGrapheInfosListener l: listeners) {
			l.onInfosChanged(ref0XPos, markersXPos);
		}
	}
}
