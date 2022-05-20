package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique;

// This class is never used... -> Should be removed...
public class TailleVue {
	int x=-1;
	int y=-1;
	private static final TailleVue INSTANCE = new TailleVue();

    private TailleVue() {
    	this.x=-1;
    	this.y=-1;
    }
    
    public static TailleVue getInstance() {
        return INSTANCE;
    }

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
