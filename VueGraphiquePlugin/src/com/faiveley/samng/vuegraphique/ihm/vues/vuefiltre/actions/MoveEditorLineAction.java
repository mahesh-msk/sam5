package com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.actions;

import static com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags.MOVE_BOTTOM;
import static com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags.MOVE_DOWN;
import static com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags.MOVE_TOP;
import static com.faiveley.samng.principal.ihm.vues.vuesfiltre.MoveOperationsFlags.MOVE_UP;

import org.eclipse.jface.action.Action;

import com.faiveley.samng.vuegraphique.ihm.vues.vuefiltre.graphiquefiltretable.GraphiqueFiltresEditorTable;

/**
 * 
 * @author Cosmin Udroiu
 *
 */
public class MoveEditorLineAction extends Action {
	private int currentFlag;
	private GraphiqueFiltresEditorTable table;
	
	public MoveEditorLineAction(GraphiqueFiltresEditorTable table, int flag) {
		this.currentFlag = flag;
		this.table = table;
	}
	
	@Override
	public void run() {
		if(currentFlag == MOVE_TOP || currentFlag == MOVE_UP || 
				currentFlag == MOVE_DOWN || currentFlag == MOVE_BOTTOM) {
			table.moveSelection(currentFlag);
		}
	}
}
