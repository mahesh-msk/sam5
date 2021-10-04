package com.faiveley.kvbdecoder.services.xml.sam;

public abstract class Hash extends CryptoUtils {
	protected int hashSize;
	protected byte[] hashBytes;
	
	public Hash(int hashSize) {
		this.hashSize = hashSize;
		hashBytes = new byte[hashSize];
	}
	
	public int hashSize() {
		return hashSize;
	}

	public abstract void reset();

	public abstract void add(byte b);

	public void add( byte[] data, int off, int len ) {
		for (int i = off; i < off + len; ++i) {
			add(data[i]);
		}
	}

	protected void prepare(){}

	public byte[] get() {
		prepare();
		byte[] hb = new byte[hashSize];
		System.arraycopy(hashBytes, 0, hb, 0, hashSize);
		return hb;
	}

	public void add(String str) {
		int len = str.length();
		char[] data = new char[len];
		str.getChars(0, len, data, 0);
		
		for (int i = 0; i < len; ++i) {
		    add( data[i] );
		}
	}

	public void addASCII( String str ) {
		int len = str.length();
		byte[] data = str.getBytes();
		add(data, 0, len);
	}

	public void add(byte[] data) {
		add( data, 0, data.length );
	}

	public void add( boolean b ) {
		if (b) {
		    add((byte) 1);
		} else {
		    add((byte) 0);
		}
	}

	public void add(char c) {
		add((byte) (c >>> 8));
		add((byte) c);
	}

	public void add(short s) {
		add((byte) (s >>> 8));
		add((byte) s);
	}

	public void add(int i) {
		add((byte) (i >>> 24));
		add((byte) (i >>> 16));
		add((byte) (i >>> 8));
		add((byte) i);
	}

	public void add(long l) {
		add((byte) (l >>> 56));
		add((byte) (l >>> 48));
		add((byte) (l >>> 40));
		add((byte) (l >>> 32));
		add((byte) (l >>> 24));
		add((byte) (l >>> 16));
		add((byte) (l >>> 8));
		add((byte) l);
	}

	public void add(float f) {
		add(Float.floatToIntBits(f));
	}

	public void add(double d) {
		add( Double.doubleToLongBits(d));
	}

	public void add(Object o) {
		add(o.toString());
	}

	public static byte[] hashStr(String str, Hash hash) {
		hash.add(str);
		return hash.get();
	}

	public boolean equals(Hash otherHash) {
		if (otherHash.hashSize != hashSize) {
			return false;
		}
		
		otherHash.prepare();
		prepare();
		
		for (int i = 0; i < hashSize; ++i) {
		    if (otherHash.hashBytes[i] != hashBytes[i]) {
		    	return false;
		    }
		}
		
		return true;
	}

	public int hashCode() {
		prepare();
		int code = 0, shift = 0;
		
		for (int i = 0; i < hashSize; ++i) {
		    code ^= hashBytes[i] << shift;
		    shift = (shift + 8) % 32;
		}
		
		return code;
	}

	public String toString() {
		prepare();
		return toStringBlock(hashBytes, 0, hashSize);
	}
 }