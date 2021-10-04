package com.faiveley.samng.principal.sm.data.descripteur;

/**
 * @author Graton Olivier
 * @version 1.0
 * @created 02-oct.-2007 13:11:37
 */
public enum Type {
	/*unsignedint,
	signedint,
	date,
	booleen,
	reserved,
	string,
	bitsfield;*/
	
	uint8,
	int8, 
	uint16, 
	int16, 
	uint24, 
	int24, 
	uint32, 
	int32,
    uint64,
    int64, 
    real32, 
    real64,
    boolean8,
    boolean1, 
    string,
    unixTimestamp,
    BCD4,
    BCD8,
    array,
    uintXbits,
    intXbits,
    dateHeureBCD,
    reserved;  
}