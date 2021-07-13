package com.faiveley.samng.principal.sm.parseurs.adapteur.target;

import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;

public abstract class ParseurTarget {

	public ParseurTarget() {
		// TODO Auto-generated constructor stub
	}
	
	public abstract long gererCalculTempsParcoursChgtsHeure(long diffTempsChgtHeure, long tempAbsoluSegmentSuivant,long tempAbsoluChgtHeure,Message msgDebutSegment,SegmentTemps segTemps,
			Message msgFinSegment,long tempAbsoluSegmentPrecedent);
	
	public abstract boolean inhiberAxeTempsVueGraphique();
	
	public abstract boolean inhiberAxeDistanceVueGraphique();
	
	public abstract void gererEvNonDate(Message msg);
	
	public abstract boolean inhiberBoiteDialoguePointReference();
}
