package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI;

public class VirtualPoint extends PointImagine{
	private boolean isRupture = false;
	private boolean isValorised=false;
    
	public VirtualPoint() {
	    // TODO Auto-generated constructor stub
	}
	
	public VirtualPoint(PointImagine pointImagine) {
	    super(pointImagine.getAbscissePixel(), pointImagine.getOrdonnee(), pointImagine.getValue());
	}
	
	public VirtualPoint(long abscissePixel) {
	    super(abscissePixel);
	}
	
	public VirtualPoint(long abscissePixel, Long ordonnee) {
	    super(abscissePixel, ordonnee);
	}
	
	public VirtualPoint(long abscissePixel, Long ordonnee, Float value) {
	    super(abscissePixel, ordonnee, value);
	}
	
	public VirtualPoint(long abscissePixel, Long ordonnee, Double value) {
	    super(abscissePixel, ordonnee, value == null ? null : value.floatValue());
	}
	
	public long getAbscissePixel() {
	    return super.getAbscissePixel();
	}
	public void setAbscissePixel(long abscissePixel) {
	    super.setAbscissePixel(abscissePixel);
	}
	public Float getValue() {
	    return super.getValue();
	}
	public void setValue(Float value) {
	    super.setValue(value);
	}
	public Long getOrdonnee() {
	    return super.getOrdonnee();
	}
	public void setOrdonnee(Long ordonnee) {
	    super.setOrdonnee(ordonnee);
	}

	public boolean isRupture() {
	    return isRupture;
	}

	public VirtualPoint setRupture(boolean isRupture) {
	    this.isRupture = isRupture;
	    return this;
	}

	public boolean isValorised() {
	    return isValorised;
	}

	public VirtualPoint setValorised(boolean isValorised) {
	    this.isValorised = isValorised;
	    return this;
	}

	@Override
	public String toString() {
	    return "VirtualPoint [isRup=" + isRupture + ",\t isValo=" + isValorised + ", \t absPx=" + getAbscissePixel() + ", \t val=" + getValue() + ", \t ord=" + getOrdonnee() + "]";
	}
	
	
}
