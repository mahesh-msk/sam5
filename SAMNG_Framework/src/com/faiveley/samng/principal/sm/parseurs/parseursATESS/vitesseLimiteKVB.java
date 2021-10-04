package com.faiveley.samng.principal.sm.parseurs.parseursATESS;

import java.util.List;

import com.faiveley.kvbdecoder.model.kvb.marker.Marker;
import com.faiveley.kvbdecoder.model.kvb.marker.Marker.MarkerValue;

public class vitesseLimiteKVB {
	
	private long valeur = -1;
	private static long valeurMax = -1;
	private vitesseLimiteKVB valeurPrec = null;
	private String type = null;
	private boolean tivd = false;
	private boolean withValeurPrec = true;
	protected List<MarkerValue> mV; // Suite de valeurs (numériques ou textuelles) sous forme de chaînes

	
	public vitesseLimiteKVB()
    {}
	
	/** Holder */
    private static class vitesseLimiteKVBHolder
    {       
        /** Instance unique non préinitialisée */
        private final static vitesseLimiteKVB instance = new vitesseLimiteKVB();
    }
    
    /** Point d'accès pour l'instance unique du singleton */
    public static vitesseLimiteKVB getInstance()
    {
        return vitesseLimiteKVBHolder.instance;
    }
	
    public vitesseLimiteKVB(long valeur, String type, boolean tivd, boolean withValeurPrec, List<MarkerValue> m) {
    	
    	// Seules les valeurs permanentes sont à mémoriser
    	// car ce sont elles qui sont potentiellement réappliquées après l'application d'une valeur temporaire
    	if (withValeurPrec && (this.type != "LTV")) {
			if (valeurPrec == null) {
				valeurPrec = new vitesseLimiteKVB(vitesseLimiteKVB.getInstance(), false);
			} else {
				valeurPrec.set(vitesseLimiteKVB.getInstance());
			}
		}
    	    	
    	this.setValeur(valeur);
		this.setType(type);
		this.setTivd(tivd);
		this.withValeurPrec = withValeurPrec;
		
		mV = m;
	}	
    
    public vitesseLimiteKVB(long valeur, String type, boolean tivd) {
		this(valeur, type, tivd, true, null);
	}
	
	public vitesseLimiteKVB(MarkerValue valeur, Marker marker, boolean tivd) {
		this(valeur.getNumericalValue().longValue(), vitesseLimiteKVB.findLimitationType(marker), tivd, true, marker.getValues());
	}
	
	public vitesseLimiteKVB(vitesseLimiteKVB vLK) {
		this(vLK.getValeur(), vLK.getType(), vLK.isTivd());
	}

	private vitesseLimiteKVB(vitesseLimiteKVB vLK, boolean withValeurPrec) {
		this(vLK.getValeur(), vLK.getType(), vLK.isTivd(), withValeurPrec, null);
	}

	public vitesseLimiteKVB(long valeur, Marker marker, boolean tivd) {
		this(valeur, vitesseLimiteKVB.findLimitationType(marker), tivd);
	}
	
	public List<MarkerValue> getValues() {
		return mV;
	}
	
	public void setValues(List<MarkerValue> m) {
		mV = m;
	}

	public long getValeur() {
		return valeur;
	}

	private void setValeur(long valeur) {
		this.valeur = valeur;
    	
    	if (this.valeur > vitesseLimiteKVB.valeurMax) {
    		this.valeur = vitesseLimiteKVB.valeurMax; 
    	}
	}

	public long getValeurMax() {
		return vitesseLimiteKVB.valeurMax;
	}

	public void setValeurMax(long valeurMax) {
		vitesseLimiteKVB.valeurMax = valeurMax;
	}
	
	public boolean isRightNowApplication(long valeur) {		
		return ((this.valeur == -1) || (valeur < this.valeur));
	}
	
	public boolean isRightNowApplication(MarkerValue valeur) {	
		return this.isRightNowApplication(valeur.getNumericalValue().longValue());
	}
	
	public void clear(){
		this.setValeur(-1);
		this.setValeurMax(-1);
		this.setType((String)null);
		this.setTivd(false);
		
		if (this.valeurPrec != null) {
			this.valeurPrec.clear();
		}
	}

	public String getType() {
		return type;
	}
	
	public boolean isLPV() { 		
		return this.type.contentEquals("LPV");
	}
	
	public boolean isLTV() { 		
		return this.type.contentEquals("LTV");
	}
	
	public boolean isGSFC() { 		
		return this.type.contentEquals("GSFC");
	}

	private void setType(String type) {
		this.type = type;
	}

	public boolean isTivd() {
		return tivd;
	}

	public void setTivd(boolean tivd) {
		this.tivd = tivd;
	}
	
	public void set(MarkerValue valeur, String type, boolean tivd){
		this.set(valeur.getNumericalValue().longValue(), type, tivd);
	}
	
	public void set(MarkerValue valeur, Marker marker, boolean tivd){
		this.set(valeur, findLimitationType(marker), tivd);
	}
	
	public void set(vitesseLimiteKVB vLK){
		// vLK peut etre null dans le cas ou la vitesse précédente doit être appliquée, mais aucune n'est présente
		// ex : insertion K7 entre 2 balises KVB vitesse temporaire de vitesse
		if (vLK != null) {			
			this.set(vLK.getValeur(), vLK.getType(), vLK.isTivd());
		}	
	}
	
	public void set(long valeur, Marker marker, boolean tivd) {
		this.set(valeur, findLimitationType(marker), tivd);		
	}
	
	public void set(long valeur, String type, boolean tivd) {
		
		// Seules les valeurs permanentes sont à mémoriser
		// car ce sont elles qui sont potentiellement réappliquées après l'application d'une valeur temporaire
		if (withValeurPrec && (this.type != "LTV")) {
			if (valeurPrec == null) {
				valeurPrec = new vitesseLimiteKVB(vitesseLimiteKVB.getInstance(), false);
			} else {
				valeurPrec.set(vitesseLimiteKVB.getInstance());
			}
		}
			
		this.setValeur(valeur);
		this.setType(type);
		this.setTivd(tivd);				
	}

	private static String findLimitationType(Marker marker) {
		String _type = "LPV";
		
		// Il faut trouver le type de la limitation
		for (MarkerValue value : marker.getValues()) {
			if (value.getValue().contains("LTV")) {
				_type = "LTV";
			}
		}
		
		return _type;
	}

	public vitesseLimiteKVB getValeurPrec() {
		return valeurPrec;
	}
}
