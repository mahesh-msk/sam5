package com.faiveley.samng.principal.ihm.calcul;

public class TailleBouton {

	public static int CalculTailleBouton(int nbLetters){
		int coef=6;
		int width;
		if (nbLetters<4) {
			width=14*nbLetters;
		}else{
			width=coef*nbLetters+10;
		}
		return width;
	}
}
