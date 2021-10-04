package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.ruptures;


public class RupturesLegendePosition {
	private static final RupturesLegendePosition INSTANCE = new RupturesLegendePosition();
	String[]libelle;
	int[] ruptureTempsProche;
	int[] ruptureTemps;
	int[] ruptureDistance;
	int[][]tabArray;
    private int dim=2;
    boolean mutex=false;
	int numLeg=-1;
    
    public int getNumLeg() {
		return numLeg;
	}

	public void setNumLeg(int numLeg) {
		this.numLeg = numLeg;
	}

	public boolean isMutex() {
		return mutex;
	}

	public void setMutex(boolean mutex) {
		this.mutex = mutex;
		if (mutex==false) {
			numLeg=-1;
		}
	}

	private RupturesLegendePosition() {
    	libelle=new String[dim];
    	ruptureTempsProche=new int[dim];
    	ruptureTemps=new int[dim];
    	ruptureDistance=new int[dim];
    	tabArray=new int[3][dim];
    	tabArray[0]=ruptureTempsProche;
    	tabArray[1]=ruptureTemps;
    	tabArray[2]=ruptureDistance;
    }

	public static RupturesLegendePosition getInstance() {
        return INSTANCE;
    }

	public int[][] getTabArray() {
		return tabArray;
	}

	public void setTabArray(int[][] tabArray) {
		this.tabArray = tabArray;
	}

	public String[] getLibelle() {
		return libelle;
	}

	public void setLibelle(String[] libelle) {
		this.libelle = libelle;
	}

	
}
