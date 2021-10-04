package com.faiveley.samng.principal.sm.controles.physique;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ControleMemoire {

	/**
	 * Quantité mémoire vive libre de la machine
	 * 
	 * @return nombre de bytes
	 */
	public static native long getMemoireViveLibre();

	/**
	 * Quantité mémoire vive totale de la machine
	 * 
	 * @return nombre de bytes
	 */
	public static native long getMemoireViveTotale();
	static {
		System.loadLibrary("ControleMemoire");
	}

	/**
	 * Méthode permetttant de calculer la valeur du paramètre JVM Xmx en partant
	 * d'une valeur initiale(dans notre cas les 2/3 de la mémoire physique
	 * totale)
	 * 
	 * @param valeur
	 *            de départ
	 */
	public static long calculerValeurXmxMaximum(long valeurDepart)
			throws IOException {

		boolean valeurTrouve = false;
		long xmxDepart = valeurDepart;

		while (!valeurTrouve && xmxDepart>0) {
			int exitVal = 1;
			Runtime r = Runtime.getRuntime();
			Process p = r.exec("jre/bin/java -Xmx"+xmxDepart+"m -classpath ./jre/lib/rt.jar sun.security.tools.KeyTool");
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			String line;
			String msg = "";
			while ((line = in.readLine()) != null) {
				System.out.println(line);
				msg = line; 
			}
			try {
				exitVal = p.waitFor();
				p.destroy();
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			if((exitVal==1||exitVal==0) && (
					(!msg.contains("Could not create the Java virtual machine."))
					&&(!msg.contains("Program will exit."))
					)
			){
				valeurTrouve = true;
			}else{
				xmxDepart = xmxDepart - 50;
			}

		}
		if(xmxDepart<=0){
			xmxDepart = 600;
		}else{
			xmxDepart=xmxDepart-50;
			if (xmxDepart>1000) {
				xmxDepart=1000;
			}
		}

		return xmxDepart;
	}
}
