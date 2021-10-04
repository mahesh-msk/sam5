package com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.parseurs.adapteur.target.ParseurTarget;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class ParseurNG extends ParseurTarget{
	public ParseurNG() {
		// TODO Auto-generated constructor stub
	}
	
	public long gererCalculTempsParcoursChgtsHeure(long diffTempsChgtHeure,long tempAbsoluSegmentSuivant,long tempAbsoluChgtHeure,Message msgDebutSegment,SegmentTemps segTemps,
			Message msgFinSegment,long tempAbsoluSegmentPrecedent){

		if(msgFinSegment.getEvenement().isChangementHeure()){
			try {
				tempAbsoluChgtHeure = Long.valueOf((msgFinSegment.getVariable(TypeRepere.temps.getCode()).getCastedValeur())+"");
				SegmentTemps segSuivant= TableSegments.getInstance().getSegmentTemps(segTemps.getNumeroSegment()+1);
				msgDebutSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segSuivant.getStartMsgId());
				tempAbsoluSegmentSuivant = (Long)(msgDebutSegment.getVariable(TypeRepere.temps.getCode()).getCastedValeur());
				diffTempsChgtHeure +=tempAbsoluSegmentSuivant-tempAbsoluChgtHeure;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return diffTempsChgtHeure;
	}	
	
	@Override
	public boolean inhiberAxeDistanceVueGraphique() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean inhiberAxeTempsVueGraphique() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean inhiberBoiteDialoguePointReference() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void gererEvNonDate(Message msg){

	}
}
