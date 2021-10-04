package com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.parseurs.adapteur.target.ParseurTarget;
import com.faiveley.samng.principal.sm.parseurs.parseursATESS.ConstantesParcoursATESS;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class ParseurJRU extends ParseurTarget {

	public long gererCalculTempsParcoursChgtsHeure(long diffTempsChgtHeure, long tempAbsoluSegmentSuivant,long tempAbsoluChgtHeure,Message msgDebutSegment,SegmentTemps segTemps,
			Message msgFinSegment,long tempAbsoluSegmentPrecedent){

		if(msgDebutSegment.getEvenement().isChangementHeure()){
			try {
				tempAbsoluChgtHeure = Long.valueOf((msgDebutSegment.getVariable(TypeRepere.tempsAvantChangement.getCode()).getCastedValeur())+"");
				SegmentTemps segPrecedent= TableSegments.getInstance().getSegmentTemps(segTemps.getNumeroSegment()-1);
				msgFinSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segPrecedent.getEndMsgId());
				tempAbsoluSegmentPrecedent = (Long)(msgFinSegment.getVariable(TypeRepere.temps.getCode()).getCastedValeur());
				diffTempsChgtHeure +=(tempAbsoluChgtHeure  - tempAbsoluSegmentPrecedent)* 
				(long)ConstantesParcoursATESS.pasCptTps * (long) (ConstantesParcoursATESS.resolutionTemps * 1000) ;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return diffTempsChgtHeure;
	}
	
	@Override
	public boolean inhiberAxeDistanceVueGraphique() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public boolean inhiberAxeTempsVueGraphique() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean inhiberBoiteDialoguePointReference() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void gererEvNonDate(Message msg){
		if (msg.getVariable(TypeRepere.temps.getCode())==null &&
				!msg.getEvenement().isRazCompteurTemps()
		) {
			msg.setEvNonDate(true);
		}else{
			msg.setEvNonDate(false);
		}
	}
}
