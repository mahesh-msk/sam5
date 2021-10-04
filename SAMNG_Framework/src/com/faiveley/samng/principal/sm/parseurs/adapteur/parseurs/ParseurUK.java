package com.faiveley.samng.principal.sm.parseurs.adapteur.parseurs;

import com.faiveley.samng.principal.data.ActivatorData;
import com.faiveley.samng.principal.sm.data.descripteur.TypeRepere;
import com.faiveley.samng.principal.sm.data.enregistrement.Message;
import com.faiveley.samng.principal.sm.parseurs.adapteur.target.ParseurTarget;
import com.faiveley.samng.principal.sm.parseurs.parseursTom4.ConstantesParcoursTom4;
import com.faiveley.samng.principal.sm.segments.SegmentTemps;
import com.faiveley.samng.principal.sm.segments.TableSegments;

public class ParseurUK extends ParseurTarget {

	public ParseurUK() {
		// TODO Auto-generated constructor stub
	}
	
	public long gererCalculTempsParcoursChgtsHeure(long diffTempsChgtHeure,long tempAbsoluSegmentSuivant,long tempAbsoluChgtHeure,Message msgDebutSegment,SegmentTemps segTemps,
			Message msgFinSegment,long tempAbsoluSegmentPrecedent){
		if(msgDebutSegment.getEvenement().isChangementHeure()){
			try {	
				tempAbsoluChgtHeure = Long.valueOf((msgDebutSegment.getVariable(TypeRepere.temps.getCode()).getCastedValeur())+"");
				SegmentTemps segPrecedent= TableSegments.getInstance().getSegmentTemps(segTemps.getNumeroSegment()-1);
				msgFinSegment = ActivatorData.getInstance().getVueData().getDataTable().getEnregistrement().getGoodMessage(segPrecedent.getEndMsgId());
				if (msgFinSegment.getVariable(TypeRepere.temps.getCode())!=null) {
					tempAbsoluSegmentPrecedent = (Long)(msgFinSegment.getVariable(TypeRepere.temps.getCode()).getCastedValeur());
				}else{
					if (msgFinSegment.getEvenement().isRazCompteurTemps()) {
						tempAbsoluSegmentPrecedent = 0;
					}else{
						tempAbsoluSegmentPrecedent=0;
					}
				}
				diffTempsChgtHeure +=(tempAbsoluChgtHeure  - tempAbsoluSegmentPrecedent)
				*ConstantesParcoursTom4.resolutionTemps*1000*ConstantesParcoursTom4.pasCptTps ;
			}catch (Exception e) {
				// TODO: handle exception
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
	
	@Override
	public void gererEvNonDate(Message msg) {
		// TODO Auto-generated method stub
		
	}
}
