package com.faiveley.samng.principal.sm.conversionCodage;

public class ConversionBase {
	public static int HexaBCDToDecimal(int in){
		if (in<0) {
			in=256+in;
		}
		int out;
		int nbDec=in / 16;
		int res=in % 16;
		out=nbDec*10+res;
		return out;
	}
}
