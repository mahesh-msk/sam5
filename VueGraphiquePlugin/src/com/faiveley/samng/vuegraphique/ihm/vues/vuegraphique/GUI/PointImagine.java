package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI;

public class PointImagine {
	
	private long abscissePixel;
	private Float value;
	private Long ordonnee;
	
	public PointImagine() {
	// TODO Auto-generated constructor stub
	}
	
	public PointImagine(long abscissePixel) {
	    this.abscissePixel=abscissePixel;
	}
	
	public PointImagine(long abscissePixel, Long ordonnee) {
	    this.abscissePixel = abscissePixel;
	    this.ordonnee = ordonnee;
	}
	
	public PointImagine(long abscissePixel, Long ordonnee, Float value) {
	    this.abscissePixel = abscissePixel;
	    this.ordonnee = ordonnee;
	    this.value = value;
	}
	
	public long getAbscissePixel() {
		return abscissePixel;
	}
	public void setAbscissePixel(long abscissePixel) {
		this.abscissePixel = abscissePixel;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float value) {
		this.value = value;
	}
	public Long getOrdonnee() {
		return ordonnee;
	}
	public void setOrdonnee(Long ordonnee) {
		this.ordonnee = ordonnee;
	}
	
}
