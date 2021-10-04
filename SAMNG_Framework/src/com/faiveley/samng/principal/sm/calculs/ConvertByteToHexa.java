package com.faiveley.samng.principal.sm.calculs;


public class ConvertByteToHexa {
	
	public ConvertByteToHexa() {
		// TODO Auto-generated constructor stub
	}
	
	public static String Convert(String str){
	    int i =Integer.parseInt(str);
	    if (i<0) {
			i=256+i;
		}
	    return Integer.toHexString(i);
	}
	
	
}
