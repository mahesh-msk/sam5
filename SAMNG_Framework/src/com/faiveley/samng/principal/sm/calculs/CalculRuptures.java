///**
// * 
// */
//package com.faiveley.samng.principal.sm.calculs;
//
//import java.math.BigInteger;
//
//import com.faiveley.samng.principal.sm.data.variableComposant.AVariableComposant;
//
///**
// * @author meggy
// *
// */
//public class CalculRuptures implements ICalculRuptures {
//
//	/* (non-Javadoc)
//	 * @see com.faiveley.samng.principal.sm.calculs.ICalculRuptures#calculRuptureDistance(com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete, com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete)
//	 */
//	public boolean calculRuptureDistance(AVariableComposant varPrev, AVariableComposant varCrt) {
//		
//		byte[] valPrev = (byte[])varPrev.getValeur();
//		byte[] valCrt = (byte[])varCrt.getValeur();
//		
//		return new BigInteger(1, valPrev).longValue() > new BigInteger(1, valCrt).longValue() ;
//		 
//	}
//
//	/* (non-Javadoc)
//	 * @see com.faiveley.samng.principal.sm.calculs.ICalculRuptures#calculRuptureTemps(com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete, com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete)
//	 */
//	public boolean calculRuptureTemps(AVariableComposant varPrev, AVariableComposant varCrt) {
//		byte[] valPrev = (byte[])varPrev.getValeur();
//		byte[] valCrt = (byte[])varCrt.getValeur();
//		
//		return new BigInteger(1, valPrev).longValue() > new BigInteger(1, valCrt).longValue();
//	}
//
//	public boolean calculRuptureDistance(long valPrev, long valCrt) {
//		
//		return valPrev > valCrt;
//		 
//	}
//
//	/* (non-Javadoc)
//	 * @see com.faiveley.samng.principal.sm.calculs.ICalculRuptures#calculRuptureTemps(com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete, com.faiveley.samng.principal.sm.data.variableComposant.VariableDiscrete)
//	 */
//	public boolean calculRuptureTemps(long valPrev, long valCrt) {
//		
//		return valPrev > valCrt;
//	}
//
//}
