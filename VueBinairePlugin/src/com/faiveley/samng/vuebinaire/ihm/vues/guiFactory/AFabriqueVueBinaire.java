package com.faiveley.samng.vuebinaire.ihm.vues.guiFactory;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;

import com.faiveley.samng.vuebinaire.ihm.vues.BinaryRow;

public abstract class AFabriqueVueBinaire {
	public abstract ArrayList<BinaryRow> remplirTableau(IProgressMonitor monitor);
}
