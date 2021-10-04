package com.faiveley.samng.vuegraphique.sm.gestionGraphes;

public class Calcul {
	
	public static boolean ecartCorrect(double curVal, double ecart, double tolerance) {
        return (Math.abs(curVal - ecart) < tolerance);
    }
}
