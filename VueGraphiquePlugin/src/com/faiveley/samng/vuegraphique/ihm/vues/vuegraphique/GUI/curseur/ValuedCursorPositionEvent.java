package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.curseur;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.CourbeMessageValue;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class ValuedCursorPositionEvent extends CursorPositionEvent {
	private CourbeMessageValue[] cmvvalues;
	
	public ValuedCursorPositionEvent(Message message, Message currentMessage, int abscisse,
			int numero, boolean isDoubleClick2, CourbeMessageValue[] cmvvalues) {
		super(message, currentMessage, abscisse, numero, isDoubleClick2);
		this.cmvvalues=cmvvalues;
	}

	public CourbeMessageValue[] getCmvvalues() {
		return cmvvalues;
	}

	public void setCmvvalues(CourbeMessageValue[] cmvvalues) {
		this.cmvvalues = cmvvalues;
	}
}
