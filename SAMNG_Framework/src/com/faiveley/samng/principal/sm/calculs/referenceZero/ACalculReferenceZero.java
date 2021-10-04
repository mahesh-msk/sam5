package com.faiveley.samng.principal.sm.calculs.referenceZero;

import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;


/**
 * Classe abstraite de calcul de la référence zero
 * @author Olivier
 *
 */
public abstract class ACalculReferenceZero {
	protected static final byte[] VAR_SANS_VALEUR="###".getBytes();
	public ACalculReferenceZero() {
		
	}
	protected static int lastStartId = 0;

	protected static int lastEndId = 0;
	
	public abstract void calculerReferenceZero(int msgId, double PointRef) ;
	
	public static ACalculReferenceZero definirCalculByParseur(){
		ACalculReferenceZero calcRef=null;
		if (TypeParseur.getInstance().getParser()instanceof ParseurParcoursTomUk || TypeParseur.getInstance().getParser()instanceof ParseurParcoursTomDIS ||  TypeParseur.getInstance().getParser() instanceof ParseurParcoursTomHSBC) {
			calcRef=new CalculReferenceZeroTom4();
		}
		
		if (TypeParseur.getInstance().getParser()instanceof ParseurParcoursSamng) {
			calcRef=new CalculReferenceZeroTomNg();
		}
		
		if (TypeParseur.getInstance().getParser()instanceof ParseurParcoursAtess) {
			calcRef=new CalculReferenceZeroAtess();
		}
		
		if (TypeParseur.getInstance().getParser()instanceof ParseurParcoursJRU) {
			calcRef=new CalculReferenceZeroJRU();
		}
		
		return calcRef;
	}
		
}
