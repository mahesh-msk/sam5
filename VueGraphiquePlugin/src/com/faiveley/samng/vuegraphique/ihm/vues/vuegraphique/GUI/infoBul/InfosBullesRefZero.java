package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.infoBul;


public class InfosBullesRefZero {

	private String PointRef;
	private int abscissePointRef;
	private boolean affiche=false;

	private static final InfosBullesRefZero INSTANCE = new InfosBullesRefZero();

    private InfosBullesRefZero() {
   
    }
    
    public static InfosBullesRefZero getInstance() {
        return INSTANCE;
    }
    
    public String getPointRef() {
		return PointRef;
	}

	public void setPointRef(String pointRef) {
		PointRef = pointRef;
	}

	public int getAbscissePointRef() {
		return abscissePointRef;
	}

	public void setAbscissePointRef(int abscissePointRef) {
		this.abscissePointRef = abscissePointRef;
	}
	
	public boolean isAffiche() {
		return affiche;
	}

	public void setAffiche(boolean affiche) {
		this.affiche = affiche;
	}
}

