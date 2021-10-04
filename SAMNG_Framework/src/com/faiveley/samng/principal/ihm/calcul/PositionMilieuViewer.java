package com.faiveley.samng.principal.ihm.calcul;


public class PositionMilieuViewer {

	public static int getPosition(int rowIdx){
		int indicePourSelectMessageAuMilieu;
		if (rowIdx==-1) {
			indicePourSelectMessageAuMilieu=-1;
		}else{
//			int hauteur=Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().getShell().getBounds().height;
//			indicePourSelectMessageAuMilieu=rowIdx-	((int) ((hauteur-196)/28.7));
			indicePourSelectMessageAuMilieu=rowIdx-9;
			if (indicePourSelectMessageAuMilieu<0) {
				indicePourSelectMessageAuMilieu=0;
			}
		}
		return indicePourSelectMessageAuMilieu;
	}
}
