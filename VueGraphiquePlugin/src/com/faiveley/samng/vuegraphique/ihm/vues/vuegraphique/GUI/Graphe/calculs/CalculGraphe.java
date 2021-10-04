package com.faiveley.samng.vuegraphique.ihm.vues.vuegraphique.GUI.Graphe.calculs;

import static com.faiveley.samng.vuegraphique.sm.gestionGraphes.FabriqueGraphe.MARGE_LATERALE;

import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.AxeX;
import com.faiveley.samng.vuegraphique.sm.gestionGraphes.axes.TypeAxe;

public class CalculGraphe {

	public static int calculerNbGrad(AxeX axe,int width,int nbmsg){
		int format=axe.getFormateValeur();
		int n=0;
		int nbPixByChar=6;
		int espace=10;
		TypeAxe typax=axe.m_TypeAxe;

		if(typax == TypeAxe.AXE_TEMPS || typax == TypeAxe.AXE_TEMPS_CORRIGE) {

			switch (format) { 
			case 0:
				n=(width-2*MARGE_LATERALE)/(4*nbPixByChar+3*espace);
				break;

			case 1:
				n=(width-2*MARGE_LATERALE)/(6*nbPixByChar+3*espace);
				break;

			case 2:
				n=(width-2*MARGE_LATERALE)/(5*nbPixByChar+3*espace);
				break;

			case 3:
				n=(width-2*MARGE_LATERALE)/(12*nbPixByChar+4*espace);
				break;
			}	
			n++;

		}else{
			n=(width-2*MARGE_LATERALE)/(6*nbPixByChar+3*espace);
		}
		if (n>nbmsg-2) {
			n=nbmsg-2;
		}
		if (n<2) {
			n=2;
		}
		
		return n;
	}
	
//	calcul the difference between 2 graduations
	public static int calculEcartGrad(int maxGrad,double multiple,double min,double max){
		int n=0;
		double tmp;
		double reste;
		tmp=(max-min)/(maxGrad-1);
		reste=tmp%multiple;

		if (maxGrad!=1) {
			if (reste==0) {
				n=(int)(tmp);
			}else{
				while(reste!=0 && maxGrad > 1){
					maxGrad--;
					tmp=(max-min)/(maxGrad-1);
					reste=tmp%multiple;				
				}
				n=(int)(tmp);
			}
		}
		return n >= (int) max ? 20 : n ;
	}
	
//	calcul the minimum space of graduations
	public static int nGradationsMax(int nbPixels){
		int n=0;
		int minCoefEspaceGrad=21;
		n=nbPixels/minCoefEspaceGrad;
		return n;
	}
	
//	calcul the minimum value to display
	public static double minRound(double min, double multiple){
		double grad=0;
		double tmp;

		if (min%multiple==0) {
			grad=min;
		}else{		
			tmp=min%multiple;				
			if (tmp>0) {
				grad=min-tmp+multiple;		
			} else {
				grad=min-tmp;		
			}	
		}

		return grad;
	}

	//calcul the maximum value to display
	public static double maxRound(double max, double multiple){
		double grad=0;
		double tmp;

		if (max%multiple==0) {
			grad=max;
		}else{		
			tmp=max%multiple;
			grad=max-tmp;
		}

		return grad;
	}

	//calcul the minimum difference between 2 graduations
	public static double findminEcartGrad(double num){
		double tmp=0;
		int i=0;
		while (num >= 10) {
			tmp= num/10;
			num=Math.floor(tmp);		
			i++;
		}
		if (num < 3) {
			i--;
		}
		return (Math.pow(10, i));
	}

	//calcul the abscisse of the axe depending on the number of the curve
	public static int choixAbscisseAxe(int courbeNr, int margeLaterale,int width) {
		int abscisse = 0;

		switch (courbeNr) {
		case 0:
			abscisse = width - margeLaterale;

			break;

		case 1:
			abscisse = margeLaterale;

			break;

		case 2:
			abscisse = width - (margeLaterale / 2);

			break;

		case 3:
			abscisse = margeLaterale / 2;

			break;
		}

		return abscisse;
	}
	
	public static int getLongueurRectByString(String lab){
		int space=1;
		int longu=lab.length();
		for (int i = 0; i < longu; i++) {
			if (lab.charAt(i)=='0'||lab.charAt(i)=='1'||lab.charAt(i)=='2'||lab.charAt(i)=='3'
				||lab.charAt(i)=='4'||lab.charAt(i)=='5'||lab.charAt(i)=='6'||lab.charAt(i)=='7'||
				lab.charAt(i)=='8'||lab.charAt(i)=='9') 
				space+=6;
			else{
				if (lab.charAt(i)=='/')
					space+=4;
				else{
					if (lab.charAt(i)=='.'||lab.charAt(i)==':')
						space+=4;
					else
						if (lab.charAt(i)==' ')
							space+=3;					
				}
			}
		}

		return space;
	}

}
