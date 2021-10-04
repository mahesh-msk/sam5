package com.faiveley.samng.principal.sm.parseurs.adapteur.adapteur;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.parseurs.TypeParseur;
import com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs.ParseurATESS;
import com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs.ParseurDIS;
import com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs.ParseurHSBC;
import com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs.ParseurJRU;
import com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs.ParseurNG;
import com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs.ParseurUK;
import com.faiveley.samng.principal.sm.parseurs.adapteur.target.ParseurTarget;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ParseurParcoursAtess;
import com.faiveley.samng.principal.sm.parseurs.parseursJRU.ParseurParcoursJRU;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomDIS;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomHSBC;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ParseurParcoursTomUk;
import com.faiveley.samng.principal.sm.parseurs.parseursTomNg.ParseurParcoursSamng;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;

public class ParseurAdapteur extends ParseurTarget {
	
	private ParseurTarget target;
	
	public ParseurAdapteur() {	
		if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursAtess){
			target=new ParseurATESS();
		}else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursTomHSBC){ 
			target=new ParseurHSBC();
		}else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursTomDIS) {
			target=new ParseurDIS();
		}else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursSamng){
			target=new ParseurNG();
		}else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursTomUk) {
			target=new ParseurUK();
		}else if (TypeParseur.getInstance().getParser() instanceof ParseurParcoursJRU) {
			target=new ParseurJRU();
		}
	}
		
	@Override
	public void gererEvNonDate(Message msg) {
		target.gererEvNonDate(msg);
	}
	
	@Override
	public boolean inhiberAxeTempsVueGraphique() {
		return target.inhiberAxeTempsVueGraphique();
	}
	
	@Override
	public boolean inhiberBoiteDialoguePointReference() {
		return target.inhiberBoiteDialoguePointReference();
	}
	
	@Override
	public boolean inhiberAxeDistanceVueGraphique() {
		return target.inhiberAxeDistanceVueGraphique();
	}
	
	public long gererCalculTempsParcoursChgtsHeure(long diffTempsChgtHeure, long tempAbsoluSegmentSuivant,long tempAbsoluChgtHeure,Message msgDebutSegment,SegmentTemps segTemps,
			Message msgFinSegment,long tempAbsoluSegmentPrecedent){
		
		return (target.gererCalculTempsParcoursChgtsHeure(diffTempsChgtHeure,tempAbsoluSegmentSuivant,tempAbsoluChgtHeure,msgDebutSegment,segTemps,
				msgFinSegment,tempAbsoluSegmentPrecedent));
	}
}
