package com.faiveley.samng.principal.sm.controles.physique;

public class TestNative {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("MemoireViveTotale : " + ControleMemoire.getMemoireViveTotale() / 1024);
		System.out.println("getMemoireViveLibre : " + ControleMemoire.getMemoireViveLibre() / 1024);

	}

}
